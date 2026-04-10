package com.questhelper.managers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.questhelper.QuestHelperConfig;
import com.questhelper.steps.tools.QuestPerspective;
import lombok.Getter;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.Menu;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.NPC;
import net.runelite.api.Tile;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.util.Text;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

import static com.questhelper.managers.HelperConstructModels.DraftHelper;
import static com.questhelper.managers.HelperConstructModels.DraftRequirement;
import static com.questhelper.managers.HelperConstructModels.DraftStep;
import static com.questhelper.managers.HelperConstructModels.StepKind;

@Singleton
public class HelperConstructManager
{
	private static final String MENU_PREFIX = "Construct:";
	private static final Pattern TAG_PATTERN = Pattern.compile("<[^>]+>");
	private static final String CONSTRUCT_DRAFT_CONFIG_KEY = "constructDraftState";

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private QuestHelperConfig config;

	@Inject
	private ConfigManager configManager;

	@Inject
	private HelperScaffoldGenerator scaffoldGenerator;

	@Inject
	private GamevalSymbolResolver symbolResolver;

	@Inject
	@Named("developerMode")
	private boolean developerMode;

	@Getter
	private DraftHelper currentDraft = new DraftHelper();
	private boolean loadedFromConfig;
	private final Gson gson = new Gson();

	public String getCurrentDraftClassName()
	{
		ensureDraftLoaded();
		return currentDraft.getClassName();
	}

	public void setupConstructMenuOptions(MenuEntryAdded event)
	{
		ensureDraftLoaded();
		if (!developerMode || !config.constructModeEnabled())
		{
			return;
		}

		MenuEntry sourceEntry = event.getMenuEntry();
		if (sourceEntry == null || sourceEntry.getType() == MenuAction.RUNELITE)
		{
			return;
		}

		Menu menu = client.getMenu();
		MenuEntry[] menuEntries = menu.getMenuEntries();

		MenuAction sourceType = sourceEntry.getType();
		int rawId = sourceEntry.getIdentifier();
		String option = sourceEntry.getOption();
		String target = Text.removeTags(sourceEntry.getTarget());
		WorldPoint clickedWorldPoint = resolveClickedWorldPoint(sourceEntry, event);
		var itemID = sourceEntry.getItemId();
		if (isNpcAction(sourceType))
		{
			addAction(menuEntries, MENU_PREFIX + " Add NPC Step", target, () -> addStep(StepKind.NPC, sourceEntry.getNpc().getId(), option, target, clickedWorldPoint));
		}
		if (isObjectAction(sourceType))
		{
			addAction(menuEntries, MENU_PREFIX + " Add Object Step", target, () -> addStep(StepKind.OBJECT, rawId, option, target, clickedWorldPoint));
		}
		if (isItemAction(sourceType))
		{
			addAction(menuEntries, MENU_PREFIX + " Add Item Requirement", target, () -> addRequirement(rawId, target));
		}
		if (isInventoryItemAction(sourceType))
		{
			addAction(menuEntries, MENU_PREFIX + " Add Item Requirement", target, () -> addRequirement(itemID, target));
		}
	}

	private void addAction(MenuEntry[] menuEntries, String option, String target, Runnable callback)
	{
		client.getMenu().createMenuEntry(menuEntries.length - 1)
			.setOption(option)
			.setTarget("<col=ff9040>" + target + "</col>")
			.setType(MenuAction.RUNELITE)
			.onClick((menuEntry) -> callback.run());
	}

	private boolean menuEntryExists(MenuEntry[] entries, String option)
	{
		for (MenuEntry entry : entries)
		{
			if (entry != null && option.equals(entry.getOption()))
			{
				return true;
			}
		}
		return false;
	}

	private void startNewDraft(String targetText)
	{
		DraftHelper helper = new DraftHelper();
		String base = normalizeText(targetText);
		if (!base.isBlank())
		{
			String classStem = toPascal(base);
			helper.setClassName(classStem + "Helper");
			helper.setQuestName(classStem.replace("Helper", ""));
		}
		helper.setHelperType("ComplexStateQuestHelper");
		currentDraft = helper;
		sendGameMessage("Quest Helper Construct: started new draft '" + currentDraft.getClassName() + "'.");
	}

	public void startNewDraftFromUi()
	{
		ensureDraftLoaded();
		startNewDraft("Generated Quest");
		saveDraftToConfig();
	}

	public void resetDraftAndClearSavedStateFromUi()
	{
		ensureDraftLoaded();
		startNewDraft("Generated Quest");
		if (configManager != null)
		{
			configManager.unsetConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, CONSTRUCT_DRAFT_CONFIG_KEY);
		}
	}

	private void addStep(StepKind kind, int rawId, String option, String target, WorldPoint clickedWorldPoint)
	{
		ensureDraftLoaded();
		if (isDuplicateStep(kind, rawId, target, clickedWorldPoint))
		{
			sendGameMessage("Quest Helper Construct: skipped duplicate " + kind.name().toLowerCase(Locale.ROOT) + " step (" + rawId + ").");
			return;
		}

		DraftStep step = new DraftStep();
		step.setStepId(UUID.randomUUID().toString());
		step.setKind(kind);
		step.setRawId(rawId);
		var idType = kind == StepKind.NPC
			? HelperConstructModels.IdType.NPC
			: kind == StepKind.OBJECT
			? HelperConstructModels.IdType.OBJECT
			: HelperConstructModels.IdType.ITEM;
		var resolved = symbolResolver.resolve(idType, rawId);
		step.setResolvedSymbol(resolved.getSymbol());
		step.setOption(option);
		step.setTargetText(target);
		step.setInstructionText(instructionText(option, target));
		step.setPanelName("Captured Steps");
		step.setSuggestedVarName(HelperScaffoldGenerator.toVarName(option + " " + target, "step"));
		step.setWorldPoint(clickedWorldPoint);
		currentDraft.getSteps().add(step);
		saveDraftToConfig();
		sendGameMessage("Quest Helper Construct: added " + kind.name().toLowerCase(Locale.ROOT) + " step (" + rawId + " -> " + resolved.getSymbol() + ") at " + formatWorldPoint(clickedWorldPoint) + ".");
	}

	private boolean isDuplicateStep(StepKind kind, int rawId, String targetText, WorldPoint worldPoint)
	{
		for (DraftStep existingStep : currentDraft.getSteps())
		{
			if (existingStep.getKind() != kind)
			{
				continue;
			}
			if (existingStep.getRawId() != rawId)
			{
				continue;
			}
			if (!safeEquals(normalizeText(existingStep.getTargetText()), normalizeText(targetText)))
			{
				continue;
			}
			if (worldPoint == null && existingStep.getWorldPoint() == null)
			{
				return true;
			}
			if (worldPoint != null && worldPoint.equals(existingStep.getWorldPoint()))
			{
				return true;
			}
		}
		return false;
	}

	private WorldPoint resolveClickedWorldPoint(MenuEntry sourceEntry, MenuEntryAdded event)
	{
		WorldPoint fromNpc = resolveNpcWorldPoint(sourceEntry);
		if (fromNpc != null)
		{
			return fromNpc;
		}

		WorldPoint fromSceneTile = resolveSceneTileWorldPoint(event);
		if (fromSceneTile != null)
		{
			return fromSceneTile;
		}

		if (client.getLocalPlayer() != null)
		{
			return normalizeLocalPoint(client.getLocalPlayer().getLocalLocation());
		}

		return null;
	}

	private WorldPoint resolveNpcWorldPoint(MenuEntry sourceEntry)
	{
		NPC npc = sourceEntry.getNpc();
		if (npc == null)
		{
			return null;
		}
		if (npc.getLocalLocation() != null)
		{
			return normalizeLocalPoint(npc.getLocalLocation());
		}
		return normalizeWorldPointWithWorldView(npc.getWorldView(), npc.getWorldLocation());
	}

	private WorldPoint resolveSceneTileWorldPoint(MenuEntryAdded event)
	{
		if (client.getTopLevelWorldView() == null)
		{
			return null;
		}

		int sceneX = event.getActionParam0();
		int sceneY = event.getActionParam1();
		var worldView = client.getTopLevelWorldView();
		Tile[][][] tiles = worldView.getScene().getTiles();
		int plane = worldView.getPlane();

		if (tiles == null || plane < 0 || plane >= tiles.length)
		{
			return null;
		}
		Tile[][] planeTiles = tiles[plane];
		if (planeTiles == null
			|| sceneX < 0 || sceneY < 0
			|| sceneX >= planeTiles.length
			|| sceneY >= planeTiles[sceneX].length)
		{
			return null;
		}

		Tile tile = planeTiles[sceneX][sceneY];
		if (tile == null)
		{
			return null;
		}
		if (tile.getLocalLocation() != null)
		{
			return normalizeLocalPoint(tile.getLocalLocation());
		}
		return normalizeWorldPointWithWorldView(worldView, tile.getWorldLocation());
	}

	private WorldPoint normalizeLocalPoint(LocalPoint localPoint)
	{
		if (localPoint == null)
		{
			return null;
		}
		return QuestPerspective.getWorldPointConsideringWorldView(client, localPoint);
	}

	private WorldPoint normalizeWorldPointWithWorldView(net.runelite.api.WorldView worldView, WorldPoint worldPoint)
	{
		if (worldPoint == null)
		{
			return null;
		}
		return QuestPerspective.getWorldPointConsideringWorldView(client, worldView, worldPoint);
	}

	private void addRequirement(int rawId, String target)
	{
		ensureDraftLoaded();
		var idToUse = rawId;
		var itemDefinition = client.getItemDefinition(rawId);
		if (itemDefinition.getNote() >= 0)
		{
			idToUse = itemDefinition.getLinkedNoteId();
		}
		DraftRequirement requirement = new DraftRequirement();
		requirement.setRawId(idToUse);
		requirement.setResolvedSymbol(symbolResolver.resolve(HelperConstructModels.IdType.ITEM, idToUse).getSymbol());
		requirement.setDisplayName(normalizeText(target).isBlank() ? "Captured Item" : normalizeText(target));
		currentDraft.getRequirements().add(requirement);
		saveDraftToConfig();
		sendGameMessage("Quest Helper Construct: added item requirement (" + idToUse + " -> " + requirement.getResolvedSymbol() + ").");
	}

	private void buildToClipboard()
	{
		ensureDraftLoaded();
		List<String> validationErrors = validateDraft();
		if (!validationErrors.isEmpty())
		{
			sendGameMessage("Quest Helper Construct: build failed - " + String.join(" | ", validationErrors));
			return;
		}

		var generated = scaffoldGenerator.generate(currentDraft);
		var fullOutput = new StringBuilder(generated.getSource());
		if (!generated.getWarnings().isEmpty())
		{
			fullOutput.append("\n/*\n");
			fullOutput.append(" Build warnings:\n");
			for (String warning : generated.getWarnings())
			{
				fullOutput.append(" - ").append(warning).append("\n");
			}
			fullOutput.append("*/\n");
		}

		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(fullOutput.toString()), null);
		sendGameMessage("Quest Helper Construct: scaffold copied to clipboard.");
	}

	public void buildToClipboardFromUi()
	{
		buildToClipboard();
	}

	private List<String> validateDraft()
	{
		List<String> errors = new ArrayList<>();
		if (currentDraft.getClassName() == null || currentDraft.getClassName().isBlank())
		{
			errors.add("missing className");
		}
		if (currentDraft.getPackagePath() == null || currentDraft.getPackagePath().isBlank())
		{
			errors.add("missing packagePath");
		}
		if (currentDraft.getSteps().isEmpty())
		{
			errors.add("no steps captured");
		}
		return errors;
	}

	public List<String> getStepSummaries()
	{
		ensureDraftLoaded();
		var steps = currentDraft.getSteps();
		List<String> summaries = new ArrayList<>(steps.size());
		for (int i = 0; i < steps.size(); i++)
		{
			summaries.add(formatStepSummary(steps.get(i), i + 1));
		}
		return Collections.unmodifiableList(summaries);
	}

	public List<String> getNpcStepSummaries()
	{
		return getStepSummariesByKind(StepKind.NPC);
	}

	public List<String> getObjectStepSummaries()
	{
		return getStepSummariesByKind(StepKind.OBJECT);
	}

	private List<String> getStepSummariesByKind(StepKind kind)
	{
		ensureDraftLoaded();
		List<String> summaries = new ArrayList<>();
		int index = 1;
		for (DraftStep step : currentDraft.getSteps())
		{
			if (step.getKind() == kind)
			{
				summaries.add(formatStepSummary(step, index++));
			}
		}
		return Collections.unmodifiableList(summaries);
	}

	public List<CombinedStepRow> getCombinedStepRows()
	{
		ensureDraftLoaded();
		List<CombinedStepRow> rows = new ArrayList<>(currentDraft.getSteps().size());
		for (int i = 0; i < currentDraft.getSteps().size(); i++)
		{
			DraftStep step = currentDraft.getSteps().get(i);
			if (step.getStepId() == null || step.getStepId().isBlank())
			{
				step.setStepId(UUID.randomUUID().toString());
				saveDraftToConfig();
			}
			rows.add(new CombinedStepRow(
				i,
				step.getStepId(),
				step.getSuggestedVarName(),
				formatStepSummary(step, i + 1)));
		}
		return Collections.unmodifiableList(rows);
	}

	public boolean moveStep(int fromIndex, int toIndex)
	{
		ensureDraftLoaded();
		List<DraftStep> steps = currentDraft.getSteps();
		if (fromIndex < 0 || toIndex < 0 || fromIndex >= steps.size() || toIndex >= steps.size() || fromIndex == toIndex)
		{
			return false;
		}

		DraftStep moved = steps.remove(fromIndex);
		steps.add(toIndex, moved);
		saveDraftToConfig();
		return true;
	}

	public boolean updateStepVarName(int index, String updatedVarName)
	{
		ensureDraftLoaded();
		if (index < 0 || index >= currentDraft.getSteps().size())
		{
			return false;
		}
		currentDraft.getSteps().get(index).setSuggestedVarName(updatedVarName == null ? "" : updatedVarName);
		saveDraftToConfig();
		return true;
	}

	public List<String> getRequirementSummaries()
	{
		ensureDraftLoaded();
		var requirements = currentDraft.getRequirements();
		List<String> summaries = new ArrayList<>(requirements.size());
		for (int i = 0; i < requirements.size(); i++)
		{
			var requirement = requirements.get(i);
			String displayName = normalizeText(requirement.getDisplayName());
			if (displayName.isBlank())
			{
				displayName = requirement.getResolvedSymbol() != null ? requirement.getResolvedSymbol() : String.valueOf(requirement.getRawId());
			}
			String summary = String.format("%d. %s",
				i + 1,
				displayName);
			summaries.add(summary);
		}
		return Collections.unmodifiableList(summaries);
	}

	private boolean safeEquals(String left, String right)
	{
		return left == null ? right == null : left.equals(right);
	}

	public boolean removeStepAt(int index)
	{
		ensureDraftLoaded();
		if (index < 0 || index >= currentDraft.getSteps().size())
		{
			return false;
		}
		currentDraft.getSteps().remove(index);
		saveDraftToConfig();
		return true;
	}

	public boolean removeRequirementAt(int index)
	{
		ensureDraftLoaded();
		if (index < 0 || index >= currentDraft.getRequirements().size())
		{
			return false;
		}
		currentDraft.getRequirements().remove(index);
		saveDraftToConfig();
		return true;
	}

	public boolean removeNpcStepAt(int index)
	{
		return removeStepByKindAt(StepKind.NPC, index);
	}

	public boolean removeObjectStepAt(int index)
	{
		return removeStepByKindAt(StepKind.OBJECT, index);
	}

	private boolean removeStepByKindAt(StepKind kind, int index)
	{
		ensureDraftLoaded();
		if (index < 0)
		{
			return false;
		}

		int filteredIndex = 0;
		for (int i = 0; i < currentDraft.getSteps().size(); i++)
		{
			if (currentDraft.getSteps().get(i).getKind() != kind)
			{
				continue;
			}
			if (filteredIndex == index)
			{
				currentDraft.getSteps().remove(i);
				saveDraftToConfig();
				return true;
			}
			filteredIndex++;
		}
		return false;
	}

	private void ensureDraftLoaded()
	{
		if (loadedFromConfig)
		{
			return;
		}
		loadedFromConfig = true;
		loadDraftFromConfig();
	}

	private void loadDraftFromConfig()
	{
		if (configManager == null)
		{
			return;
		}
		String json = configManager.getConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, CONSTRUCT_DRAFT_CONFIG_KEY);
		if (json == null || json.isBlank())
		{
			return;
		}

		try
		{
			DraftState state = gson.fromJson(json, DraftState.class);
			if (state == null)
			{
				return;
			}

			DraftHelper loaded = new DraftHelper();
			if (state.questName != null) loaded.setQuestName(state.questName);
			if (state.className != null) loaded.setClassName(state.className);
			if (state.packagePath != null) loaded.setPackagePath(state.packagePath);
			if (state.helperType != null) loaded.setHelperType(state.helperType);

			for (DraftStepState stepState : state.steps)
			{
				DraftStep step = new DraftStep();
				step.setStepId(stepState.stepId == null || stepState.stepId.isBlank() ? UUID.randomUUID().toString() : stepState.stepId);
				step.setKind(stepState.kind);
				step.setRawId(stepState.rawId);
				step.setResolvedSymbol(stepState.resolvedSymbol);
				step.setOption(stepState.option);
				step.setTargetText(stepState.targetText);
				step.setSuggestedVarName(stepState.suggestedVarName);
				step.setInstructionText(stepState.instructionText);
				step.setPanelName(stepState.panelName);
				if (stepState.worldX != null && stepState.worldY != null && stepState.worldPlane != null)
				{
					step.setWorldPoint(new WorldPoint(stepState.worldX, stepState.worldY, stepState.worldPlane));
				}
				loaded.getSteps().add(step);
			}

			for (DraftRequirementState reqState : state.requirements)
			{
				DraftRequirement req = new DraftRequirement();
				req.setRawId(reqState.rawId);
				req.setResolvedSymbol(reqState.resolvedSymbol);
				req.setDisplayName(reqState.displayName);
				loaded.getRequirements().add(req);
			}

			currentDraft = loaded;
		}
		catch (JsonSyntaxException ignored)
		{
			currentDraft = new DraftHelper();
		}
	}

	private void saveDraftToConfig()
	{
		if (configManager == null)
		{
			return;
		}
		DraftState state = new DraftState();
		state.questName = currentDraft.getQuestName();
		state.className = currentDraft.getClassName();
		state.packagePath = currentDraft.getPackagePath();
		state.helperType = currentDraft.getHelperType();

		for (DraftStep step : currentDraft.getSteps())
		{
			DraftStepState stepState = new DraftStepState();
			stepState.stepId = step.getStepId();
			stepState.kind = step.getKind();
			stepState.rawId = step.getRawId();
			stepState.resolvedSymbol = step.getResolvedSymbol();
			stepState.option = step.getOption();
			stepState.targetText = step.getTargetText();
			stepState.suggestedVarName = step.getSuggestedVarName();
			stepState.instructionText = step.getInstructionText();
			stepState.panelName = step.getPanelName();
			if (step.getWorldPoint() != null)
			{
				stepState.worldX = step.getWorldPoint().getX();
				stepState.worldY = step.getWorldPoint().getY();
				stepState.worldPlane = step.getWorldPoint().getPlane();
			}
			state.steps.add(stepState);
		}

		for (DraftRequirement req : currentDraft.getRequirements())
		{
			DraftRequirementState reqState = new DraftRequirementState();
			reqState.rawId = req.getRawId();
			reqState.resolvedSymbol = req.getResolvedSymbol();
			reqState.displayName = req.getDisplayName();
			state.requirements.add(reqState);
		}

		configManager.setConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, CONSTRUCT_DRAFT_CONFIG_KEY, gson.toJson(state));
	}

	private boolean isNpcAction(MenuAction menuAction)
	{
		return menuAction == MenuAction.NPC_FIRST_OPTION;
	}

	private boolean isObjectAction(MenuAction menuAction)
	{
		return menuAction == MenuAction.GAME_OBJECT_FIRST_OPTION;
	}

	private boolean isItemAction(MenuAction menuAction)
	{
		return menuAction == MenuAction.EXAMINE_ITEM_GROUND;
	}

	private boolean isInventoryItemAction(MenuAction menuAction)
	{
		return menuAction == MenuAction.WIDGET_TARGET;
	}

	private String instructionText(String option, String target)
	{
		String cleanOption = normalizeText(option);
		String cleanTarget = normalizeText(target);
		if (cleanOption.isBlank() && cleanTarget.isBlank())
		{
			return "TODO: capture instruction text";
		}
		return (cleanOption + " " + cleanTarget).trim();
	}

	private String normalizeText(String text)
	{
		if (text == null)
		{
			return "";
		}
		return TAG_PATTERN.matcher(text).replaceAll("").trim();
	}

	private String toPascal(String text)
	{
		String cleaned = text.replaceAll("[^a-zA-Z0-9 ]", " ").trim();
		if (cleaned.isBlank())
		{
			return "GeneratedQuest";
		}
		StringBuilder out = new StringBuilder();
		for (String part : cleaned.split("\\s+"))
		{
			if (!part.isBlank())
			{
				out.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1).toLowerCase(Locale.ROOT));
			}
		}
		return out.length() == 0 ? "GeneratedQuest" : out.toString();
	}

	private void sendGameMessage(String message)
	{
		if (clientThread == null || client == null)
		{
			return;
		}
		clientThread.invokeLater(() -> client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", message, null));
	}

	private String formatWorldPoint(WorldPoint point)
	{
		if (point == null)
		{
			return "(unknown)";
		}
		return "(" + point.getX() + ", " + point.getY() + ", " + point.getPlane() + ")";
	}

	private String formatStepSummary(DraftStep step, int displayIndex)
	{
		String displayName = normalizeText(step.getTargetText());
		if (displayName.isBlank())
		{
			displayName = step.getResolvedSymbol() != null ? step.getResolvedSymbol() : String.valueOf(step.getRawId());
		}
		return String.format("%d. %s (%s)",
			displayIndex,
			displayName,
			formatWorldPoint(step.getWorldPoint()));
	}

	private static class DraftState
	{
		String questName;
		String className;
		String packagePath;
		String helperType;
		List<DraftStepState> steps = new ArrayList<>();
		List<DraftRequirementState> requirements = new ArrayList<>();
	}

	private static class DraftStepState
	{
		String stepId;
		StepKind kind;
		int rawId;
		String resolvedSymbol;
		String option;
		String targetText;
		String suggestedVarName;
		String instructionText;
		String panelName;
		Integer worldX;
		Integer worldY;
		Integer worldPlane;
	}

	public static class CombinedStepRow
	{
		private final int index;
		private final String stepId;
		private final String varName;
		private final String summary;

		public CombinedStepRow(int index, String stepId, String varName, String summary)
		{
			this.index = index;
			this.stepId = stepId;
			this.varName = varName;
			this.summary = summary;
		}

		public int getIndex()
		{
			return index;
		}

		public String getStepId()
		{
			return stepId;
		}

		public String getVarName()
		{
			return varName;
		}

		public String getSummary()
		{
			return summary;
		}
	}

	private static class DraftRequirementState
	{
		int rawId;
		String resolvedSymbol;
		String displayName;
	}
}
