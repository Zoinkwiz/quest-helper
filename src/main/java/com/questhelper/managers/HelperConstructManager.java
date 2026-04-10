package com.questhelper.managers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.questhelper.QuestHelperConfig;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.tools.QuestPerspective;
import com.questhelper.tools.ConstructWorldMapPoint;
import com.questhelper.util.worldmap.WorldPointMapper;
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
import net.runelite.api.QuestState;
import net.runelite.client.RuneLite;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.ui.overlay.worldmap.WorldMapPointManager;
import net.runelite.client.util.Text;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;

import static com.questhelper.managers.HelperConstructModels.DraftHelper;
import static com.questhelper.managers.HelperConstructModels.DraftRequirement;
import static com.questhelper.managers.HelperConstructModels.DraftStep;
import static com.questhelper.managers.HelperConstructModels.StepKind;
import static com.questhelper.requirements.util.LogicHelper.nor;

@Singleton
public class HelperConstructManager
{
	private static final String MENU_PREFIX = "Construct:";
	private static final Pattern TAG_PATTERN = Pattern.compile("<[^>]+>");
	private static final String CONSTRUCT_DRAFT_CONFIG_KEY = "constructDraftState";
	private static final String DEFAULT_SECTION_NAME = "New Section";
	private static final int MAP_MIN_X = 960;
	private static final int MAP_MIN_Y = 2048;
	private static final int MAP_MAX_X = 4031;
	private static final int MAP_MAX_Y = 4223;

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
	private WorldMapPointManager worldMapPointManager;

	@Inject
	private GamevalSymbolResolver symbolResolver;

	@Inject
	private QuestManager questManager;

	@Inject
	@Named("developerMode")
	private boolean developerMode;

	@Getter
	private DraftHelper currentDraft = new DraftHelper();
	private boolean loadedFromConfig;
	private final Gson gson = new Gson();
	private boolean worldMapRoutePreviewEnabled;

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
			addAction(menuEntries, MENU_PREFIX + " Add Item Step", target, () -> addItemStep(rawId, target));
		}
		if (isInventoryItemAction(sourceType))
		{
			addAction(menuEntries, MENU_PREFIX + " Add Item Requirement", target, () -> addRequirement(itemID, target));
			addAction(menuEntries, MENU_PREFIX + " Add Item Step", target, () -> addItemStep(itemID, target));
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
		if (worldMapRoutePreviewEnabled)
		{
			rebuildWorldMapRoutePoints();
		}
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
		if (worldMapRoutePreviewEnabled)
		{
			rebuildWorldMapRoutePoints();
		}
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
		var idToUse = normalizeItemId(rawId);
		DraftRequirement requirement = new DraftRequirement();
		requirement.setRawId(idToUse);
		requirement.setResolvedSymbol(symbolResolver.resolve(HelperConstructModels.IdType.ITEM, idToUse).getSymbol());
		requirement.setDisplayName(normalizeText(target).isBlank() ? "Captured Item" : normalizeText(target));
		currentDraft.getRequirements().add(requirement);
		saveDraftToConfig();
		sendGameMessage("Quest Helper Construct: added item requirement (" + idToUse + " -> " + requirement.getResolvedSymbol() + ").");
	}

	private void addItemStep(int rawId, String target)
	{
		ensureDraftLoaded();
		int normalizedItemId = normalizeItemId(rawId);
		DraftRequirement requirement = findOrCreateRequirement(normalizedItemId, target);

		if (isDuplicateStep(StepKind.ITEM, normalizedItemId, target, null))
		{
			sendGameMessage("Quest Helper Construct: skipped duplicate item step (" + normalizedItemId + ").");
			return;
		}

		DraftStep step = new DraftStep();
		step.setStepId(UUID.randomUUID().toString());
		step.setKind(StepKind.ITEM);
		step.setRawId(normalizedItemId);
		step.setLinkedRequirementRawId(requirement.getRawId());
		step.setResolvedSymbol(requirement.getResolvedSymbol());
		step.setOption("Use");
		step.setTargetText(target);
		String itemName = normalizeText(target).isBlank() ? requirement.getDisplayName() : normalizeText(target);
		step.setInstructionText("Use " + itemName + ".");
		step.setPanelName("Captured Steps");
		step.setSuggestedVarName(HelperScaffoldGenerator.toVarName("use " + itemName, "itemStep"));
		currentDraft.getSteps().add(step);
		saveDraftToConfig();
		sendGameMessage("Quest Helper Construct: added item step (" + normalizedItemId + " -> " + requirement.getResolvedSymbol() + ").");
	}

	private DraftRequirement findOrCreateRequirement(int normalizedItemId, String target)
	{
		for (DraftRequirement requirement : currentDraft.getRequirements())
		{
			if (requirement.getRawId() == normalizedItemId)
			{
				return requirement;
			}
		}

		DraftRequirement requirement = new DraftRequirement();
		requirement.setRawId(normalizedItemId);
		requirement.setResolvedSymbol(symbolResolver.resolve(HelperConstructModels.IdType.ITEM, normalizedItemId).getSymbol());
		requirement.setDisplayName(normalizeText(target).isBlank() ? "Captured Item" : normalizeText(target));
		currentDraft.getRequirements().add(requirement);
		return requirement;
	}

	private int normalizeItemId(int rawId)
	{
		if (client == null)
		{
			return rawId;
		}
		var itemDefinition = client.getItemDefinition(rawId);
		if (itemDefinition != null && itemDefinition.getNote() >= 0)
		{
			return itemDefinition.getLinkedNoteId();
		}
		return rawId;
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

	public void buildRouteMapImageFromUi()
	{
		ensureDraftLoaded();
		List<WorldPoint> points = new ArrayList<>();
		for (DraftStep step : currentDraft.getSteps())
		{
			if (step.getWorldPoint() != null)
			{
				points.add(step.getWorldPoint());
			}
		}

		if (points.size() < 2)
		{
			sendGameMessage("Quest Helper Construct: need at least 2 steps with world points to generate route image.");
			return;
		}

		try
		{
			RouteImageResult result = writeRouteImage(points);
			sendGameMessage("Quest Helper Construct: route map image saved to " + result.file.getAbsolutePath()
				+ " (drawn " + result.drawnCount + ", skipped " + result.skippedCount + ").");
		}
		catch (IOException ex)
		{
			sendGameMessage("Quest Helper Construct: failed to save route map image (" + ex.getMessage() + ").");
		}
	}

	public void previewInSidebarFromUi()
	{
		ensureDraftLoaded();
		List<String> validationErrors = validateDraft();
		if (!validationErrors.isEmpty())
		{
			sendGameMessage("Quest Helper Construct: preview failed - " + String.join(" | ", validationErrors));
			return;
		}

		PreviewQuestHelper previewHelper = new PreviewQuestHelper(currentDraft);
		wireQuestHelperForRuntimeUse(previewHelper);
		questManager.startUpQuest(previewHelper, true);
		sendGameMessage("Quest Helper Construct: preview loaded in Quest Helper sidebar.");
	}

	private void wireQuestHelperForRuntimeUse(QuestHelper questHelper)
	{
		Module questModule = (Binder binder) ->
		{
			binder.bind(QuestHelper.class).toInstance(questHelper);
			binder.install(questHelper);
		};
		Injector questInjector = RuneLite.getInjector().createChildInjector(questModule);
		RuneLite.getInjector().injectMembers(questHelper);
		questHelper.setInjector(questInjector);
		questHelper.setConfig(config);
		questHelper.setQuestHelperPlugin(questManager.questHelperPlugin);
	}

	public void toggleWorldMapRoutePreviewFromUi()
	{
		ensureDraftLoaded();
		worldMapRoutePreviewEnabled = !worldMapRoutePreviewEnabled;
		if (worldMapRoutePreviewEnabled)
		{
			rebuildWorldMapRoutePoints();
			sendGameMessage("Quest Helper Construct: in-game world map route preview enabled.");
		}
		else
		{
			clearWorldMapRoutePoints();
			sendGameMessage("Quest Helper Construct: in-game world map route preview disabled.");
		}
	}

	public boolean isWorldMapRoutePreviewEnabled()
	{
		return worldMapRoutePreviewEnabled;
	}

	public void disableWorldMapRoutePreview()
	{
		worldMapRoutePreviewEnabled = false;
		clearWorldMapRoutePoints();
	}

	public List<WorldPoint> getWorldMapRouteLinePoints()
	{
		ensureDraftLoaded();
		if (!worldMapRoutePreviewEnabled)
		{
			return Collections.emptyList();
		}
		List<WorldPoint> points = new ArrayList<>();
		for (DraftStep step : currentDraft.getSteps())
		{
			WorldPoint wp = step.getWorldPoint();
			if (wp != null && isWithinMapBounds(wp))
			{
				points.add(WorldPointMapper.getMapWorldPointFromRealWorldPoint(wp).getWorldPoint());
			}
		}
		return points;
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
				formatStepSummary(step, i + 1),
				step.isSectionDivider(),
				step.getSectionCondition(),
				step.isSkipWhenConditionMet()));
		}
		return Collections.unmodifiableList(rows);
	}

	public void addSectionDivider()
	{
		ensureDraftLoaded();
		DraftStep divider = new DraftStep();
		divider.setStepId(UUID.randomUUID().toString());
		divider.setKind(StepKind.TEXT);
		divider.setSectionDivider(true);
		divider.setSuggestedVarName(DEFAULT_SECTION_NAME);
		divider.setInstructionText(DEFAULT_SECTION_NAME);
		divider.setSectionCondition("");
		divider.setSkipWhenConditionMet(false);
		currentDraft.getSteps().add(divider);
		saveDraftToConfig();
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
		if (worldMapRoutePreviewEnabled)
		{
			rebuildWorldMapRoutePoints();
		}
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
		if (worldMapRoutePreviewEnabled)
		{
			rebuildWorldMapRoutePoints();
		}
		return true;
	}

	public boolean updateSectionCondition(int index, String condition)
	{
		ensureDraftLoaded();
		if (index < 0 || index >= currentDraft.getSteps().size())
		{
			return false;
		}
		DraftStep step = currentDraft.getSteps().get(index);
		if (!step.isSectionDivider())
		{
			return false;
		}
		step.setSectionCondition(condition == null ? "" : condition);
		saveDraftToConfig();
		return true;
	}

	public boolean toggleSectionSkipMode(int index)
	{
		ensureDraftLoaded();
		if (index < 0 || index >= currentDraft.getSteps().size())
		{
			return false;
		}
		DraftStep step = currentDraft.getSteps().get(index);
		if (!step.isSectionDivider())
		{
			return false;
		}
		step.setSkipWhenConditionMet(!step.isSkipWhenConditionMet());
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
		if (worldMapRoutePreviewEnabled)
		{
			rebuildWorldMapRoutePoints();
		}
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
				if (worldMapRoutePreviewEnabled)
				{
					rebuildWorldMapRoutePoints();
				}
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
				step.setSectionDivider(stepState.sectionDivider);
				step.setRawId(stepState.rawId);
				step.setLinkedRequirementRawId(stepState.linkedRequirementRawId);
				step.setResolvedSymbol(stepState.resolvedSymbol);
				step.setOption(stepState.option);
				step.setTargetText(stepState.targetText);
				step.setSuggestedVarName(stepState.suggestedVarName);
				step.setInstructionText(stepState.instructionText);
				step.setPanelName(stepState.panelName);
				step.setSectionCondition(stepState.sectionCondition);
				step.setSkipWhenConditionMet(stepState.skipWhenConditionMet);
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
			stepState.sectionDivider = step.isSectionDivider();
			stepState.rawId = step.getRawId();
			stepState.linkedRequirementRawId = step.getLinkedRequirementRawId();
			stepState.resolvedSymbol = step.getResolvedSymbol();
			stepState.option = step.getOption();
			stepState.targetText = step.getTargetText();
			stepState.suggestedVarName = step.getSuggestedVarName();
			stepState.instructionText = step.getInstructionText();
			stepState.panelName = step.getPanelName();
			stepState.sectionCondition = step.getSectionCondition();
			stepState.skipWhenConditionMet = step.isSkipWhenConditionMet();
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
		if (step.isSectionDivider())
		{
			String mode = step.isSkipWhenConditionMet() ? "skip when true" : "show when true";
			String condition = step.getSectionCondition() == null || step.getSectionCondition().isBlank() ? "no condition" : step.getSectionCondition();
			return String.format("SECTION %d. %s [%s: %s]",
				displayIndex,
				step.getSuggestedVarName() == null || step.getSuggestedVarName().isBlank() ? DEFAULT_SECTION_NAME : step.getSuggestedVarName(),
				mode,
				condition);
		}
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

	private void rebuildWorldMapRoutePoints()
	{
		if (worldMapPointManager == null)
		{
			return;
		}
		clearWorldMapRoutePoints();
		List<DraftStep> steps = currentDraft.getSteps();
		for (int i = 0; i < steps.size(); i++)
		{
			DraftStep step = steps.get(i);
			WorldPoint wp = step.getWorldPoint();
			if (wp == null || !isWithinMapBounds(wp))
			{
				continue;
			}
			String label = (i + 1) + ". " + displayStepName(step);
			ConstructWorldMapPoint point = new ConstructWorldMapPoint(wp, routePointIcon(i, steps.size()), label);
			worldMapPointManager.add(point);
		}
	}

	private void clearWorldMapRoutePoints()
	{
		if (worldMapPointManager != null)
		{
			worldMapPointManager.removeIf(ConstructWorldMapPoint.class::isInstance);
		}
	}

	private String displayStepName(DraftStep step)
	{
		String varName = step.getSuggestedVarName();
		if (varName != null && !varName.isBlank())
		{
			return varName;
		}
		String target = normalizeText(step.getTargetText());
		if (!target.isBlank())
		{
			return target;
		}
		return step.getResolvedSymbol() != null ? step.getResolvedSymbol() : "step";
	}

	private BufferedImage routePointIcon(int index, int total)
	{
		BufferedImage image = new BufferedImage(14, 14, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		try
		{
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			Color color = new Color(23, 162, 184);
			if (index == 0)
			{
				color = new Color(40, 167, 69);
			}
			else if (index == total - 1)
			{
				color = new Color(220, 53, 69);
			}
			g.setColor(color);
			g.fillOval(1, 1, 12, 12);
			g.setColor(Color.WHITE);
			g.drawOval(1, 1, 12, 12);
		}
		finally
		{
			g.dispose();
		}
		return image;
	}

	private boolean isWithinMapBounds(WorldPoint worldPoint)
	{
		return worldPoint.getX() >= MAP_MIN_X && worldPoint.getX() <= MAP_MAX_X
			&& worldPoint.getY() >= MAP_MIN_Y && worldPoint.getY() <= MAP_MAX_Y;
	}

	private RouteImageResult writeRouteImage(List<WorldPoint> points) throws IOException
	{
		BufferedImage mapImage = loadWorldMapImage();
		int width = mapImage.getWidth();
		int height = mapImage.getHeight();
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		try
		{
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.drawImage(mapImage, 0, 0, null);

			List<Point> plotted = new ArrayList<>();
			int skipped = 0;
			for (int i = 0; i < points.size(); i++)
			{
				WorldPoint wp = points.get(i);
				if (wp.getX() < MAP_MIN_X || wp.getX() > MAP_MAX_X || wp.getY() < MAP_MIN_Y || wp.getY() > MAP_MAX_Y)
				{
					skipped++;
					continue;
				}
				double nx = (double) (wp.getX() - MAP_MIN_X) / (MAP_MAX_X - MAP_MIN_X);
				double ny = (double) (wp.getY() - MAP_MIN_Y) / (MAP_MAX_Y - MAP_MIN_Y);
				int px = (int) Math.round(nx * (width - 1));
				int py = height - 1 - (int) Math.round(ny * (height - 1));
				plotted.add(new Point(px, py));
			}

			if (plotted.size() < 2)
			{
				throw new IOException("Not enough in-bounds route points to draw.");
			}

			g.setStroke(new BasicStroke(3f));
			g.setColor(new Color(255, 193, 7));
			for (int i = 0; i < plotted.size() - 1; i++)
			{
				Point a = plotted.get(i);
				Point b = plotted.get(i + 1);
				g.drawLine(a.x, a.y, b.x, b.y);
			}

			for (int i = 0; i < plotted.size(); i++)
			{
				Point p = plotted.get(i);
				boolean isStart = i == 0;
				boolean isEnd = i == plotted.size() - 1;
				if (isStart)
				{
					g.setColor(new Color(40, 167, 69));
				}
				else if (isEnd)
				{
					g.setColor(new Color(220, 53, 69));
				}
				else
				{
					g.setColor(new Color(23, 162, 184));
				}
				g.fillOval(p.x - 6, p.y - 6, 12, 12);
				g.setColor(Color.WHITE);
				g.drawString(String.valueOf(i + 1), p.x + 8, p.y - 8);
			}

			g.setColor(Color.WHITE);
			g.drawString("Quest Helper Construct Route", 12, 20);
			g.drawString("Bounds: (" + MAP_MIN_X + ", " + MAP_MIN_Y + ") to (" + MAP_MAX_X + ", " + MAP_MAX_Y + ")", 12, 38);
			g.drawString("Drawn: " + plotted.size() + "  Skipped: " + skipped, 12, 56);

			File outputDir = new File(System.getProperty("java.io.tmpdir"), "quest-helper-construct");
			if (!outputDir.exists() && !outputDir.mkdirs())
			{
				throw new IOException("Could not create output directory");
			}
			File outFile = new File(outputDir, "route-map-" + System.currentTimeMillis() + ".png");
			ImageIO.write(image, "png", outFile);
			return new RouteImageResult(outFile, plotted.size(), skipped);
		}
		finally
		{
			g.dispose();
		}
	}

	private BufferedImage loadWorldMapImage() throws IOException
	{
		var stream = HelperConstructManager.class.getResourceAsStream("/world-map.png");
		if (stream == null)
		{
			throw new IOException("world-map.png not found in resources");
		}
		try (stream)
		{
			BufferedImage image = ImageIO.read(stream);
			if (image == null)
			{
				throw new IOException("world-map.png could not be decoded");
			}
			return image;
		}
	}

	private static class RouteImageResult
	{
		final File file;
		final int drawnCount;
		final int skippedCount;

		private RouteImageResult(File file, int drawnCount, int skippedCount)
		{
			this.file = file;
			this.drawnCount = drawnCount;
			this.skippedCount = skippedCount;
		}
	}

	private static class PreviewQuestHelper extends ComplexStateQuestHelper
	{
		private final DraftHelper draft;
		private final List<ItemRequirement> previewRequirements = new ArrayList<>();
		private final List<PanelDetails> previewPanels = new ArrayList<>();

		private PreviewQuestHelper(DraftHelper draft)
		{
			this.draft = draft;
		}

		@Override
		protected void setupRequirements()
		{
			previewRequirements.clear();
			for (DraftRequirement requirement : draft.getRequirements())
			{
				String display = requirement.getDisplayName() == null || requirement.getDisplayName().isBlank()
					? "Required item"
					: requirement.getDisplayName();
				previewRequirements.add(new ItemRequirement(display, requirement.getRawId()));
			}
		}

		@Override
		public QuestStep loadStep()
		{
			initializeRequirements();
			previewPanels.clear();
			Map<Integer, ItemRequirement> requirementById = new HashMap<>();
			for (ItemRequirement req : previewRequirements)
			{
				requirementById.put(req.getId(), req);
			}

			List<ConditionalStep> sectionTasks = new ArrayList<>();
			List<List<Requirement>> sectionCompletionRequirements = new ArrayList<>();
			List<QuestStep> currentPanelSteps = new ArrayList<>();
			String currentPanelName = "Captured Steps";

			for (DraftStep draftStep : draft.getSteps())
			{
				if (draftStep.isSectionDivider())
				{
					ConditionalStep sectionTask = createSectionTask(currentPanelName, currentPanelSteps);
					if (sectionTask != null)
					{
						sectionCompletionRequirements.add(extractSectionCompletionRequirements(currentPanelSteps));
						addPanelWithLockingStep(currentPanelName, currentPanelSteps, sectionTask);
						sectionTasks.add(sectionTask);
					}
					String sectionName = draftStep.getSuggestedVarName();
					currentPanelName = (sectionName == null || sectionName.isBlank()) ? "Section" : sectionName;
					continue;
				}

				QuestStep step = toPreviewStep(draftStep, requirementById);
				currentPanelSteps.add(step);
			}

			if (!currentPanelSteps.isEmpty())
			{
				ConditionalStep sectionTask = createSectionTask(currentPanelName, currentPanelSteps);
				if (sectionTask != null)
				{
					sectionCompletionRequirements.add(extractSectionCompletionRequirements(currentPanelSteps));
					addPanelWithLockingStep(currentPanelName, currentPanelSteps, sectionTask);
					sectionTasks.add(sectionTask);
				}
			}
			if (sectionTasks.isEmpty())
			{
				QuestStep empty = new DetailedQuestStep(this, "No previewable steps captured yet.");
				previewPanels.add(new PanelDetails("Captured Steps", List.of(empty)));
				return empty;
			}

			if (sectionTasks.size() == 1)
			{
				return sectionTasks.get(0);
			}

			int lastSectionIndex = sectionTasks.size() - 1;
			ConditionalStep allSections = new ConditionalStep(this, sectionTasks.get(lastSectionIndex), "All Sections");
			allSections.setShouldPassthroughText(true);

			List<Requirement> priorSectionRequirements = new ArrayList<>();
			for (int i = 0; i < lastSectionIndex; i++)
			{
				allSections.addStep(nor(priorSectionRequirements.toArray(new Requirement[0])), sectionTasks.get(i));
				priorSectionRequirements.addAll(sectionCompletionRequirements.get(i));
			}

			return allSections;
		}

		private ConditionalStep createSectionTask(String panelName, List<QuestStep> panelSteps)
		{
			if (panelSteps.isEmpty())
			{
				return null;
			}

			QuestStep fallbackStep = panelSteps.get(0);
			ConditionalStep sectionTask = new ConditionalStep(this, fallbackStep, panelName);
			sectionTask.setShouldPassthroughText(true);
			return sectionTask;
		}

		private void addPanelWithLockingStep(String panelName, List<QuestStep> panelSteps, ConditionalStep sectionTask)
		{
			if (sectionTask == null || panelSteps.isEmpty())
			{
				return;
			}

			List<QuestStep> stepsForPanel = new ArrayList<>(panelSteps);
			PanelDetails panel = new PanelDetails(panelName, stepsForPanel);
			panel.setLockingStep(sectionTask);
			previewPanels.add(panel);
			panelSteps.clear();
		}

		private List<Requirement> extractSectionCompletionRequirements(List<QuestStep> panelSteps)
		{
			LinkedHashSet<Requirement> requirements = new LinkedHashSet<>();
			for (QuestStep step : panelSteps)
			{
				if (step instanceof DetailedQuestStep)
				{
					requirements.addAll(((DetailedQuestStep) step).getRequirements());
				}
				if (step instanceof ConditionalStep)
				{
					for (Requirement condition : ((ConditionalStep) step).getConditions())
					{
						if (condition != null)
						{
							requirements.add(condition);
						}
					}
				}
			}
			return new ArrayList<>(requirements);
		}

		private QuestStep toPreviewStep(DraftStep draftStep, Map<Integer, ItemRequirement> requirementById)
		{
			String instruction = (draftStep.getInstructionText() == null || draftStep.getInstructionText().isBlank())
				? "Complete step."
				: draftStep.getInstructionText();
			if (draftStep.getKind() == StepKind.NPC)
			{
				if (draftStep.getWorldPoint() != null)
				{
					return new NpcStep(this, draftStep.getRawId(), draftStep.getWorldPoint(), instruction);
				}
				return new NpcStep(this, draftStep.getRawId(), instruction);
			}
			if (draftStep.getKind() == StepKind.OBJECT)
			{
				if (draftStep.getWorldPoint() != null)
				{
					return new ObjectStep(this, draftStep.getRawId(), draftStep.getWorldPoint(), instruction);
				}
				return new ObjectStep(this, draftStep.getRawId(), instruction);
			}
			if (draftStep.getKind() == StepKind.ITEM)
			{
				ItemRequirement itemRequirement = null;
				Integer linkedRequirementRawId = draftStep.getLinkedRequirementRawId();
				if (linkedRequirementRawId != null)
				{
					itemRequirement = requirementById.get(linkedRequirementRawId);
				}
				if (itemRequirement == null)
				{
					itemRequirement = requirementById.get(draftStep.getRawId());
				}
				if (itemRequirement != null)
				{
					return new ItemStep(this, instruction, itemRequirement.highlighted());
				}
				return new ItemStep(this, instruction);
			}
			return new DetailedQuestStep(this, instruction);
		}

		@Override
		public List<ItemRequirement> getItemRequirements()
		{
			return previewRequirements;
		}

		@Override
		public List<PanelDetails> getPanels()
		{
			return previewPanels;
		}

		@Override
		public int getVar()
		{
			return 0;
		}

		@Override
		public QuestState getState(Client client)
		{
			return QuestState.IN_PROGRESS;
		}

		@Override
		public boolean hasQuestStateBecomeFinished()
		{
			return false;
		}

		@Override
		public String toString()
		{
			return draft.getQuestName() == null || draft.getQuestName().isBlank()
				? "Construct Preview"
				: draft.getQuestName() + " (Preview)";
		}
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
		boolean sectionDivider;
		int rawId;
		Integer linkedRequirementRawId;
		String resolvedSymbol;
		String option;
		String targetText;
		String suggestedVarName;
		String instructionText;
		String panelName;
		String sectionCondition;
		boolean skipWhenConditionMet;
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
		private final boolean sectionDivider;
		private final String sectionCondition;
		private final boolean skipWhenConditionMet;

		public CombinedStepRow(int index, String stepId, String varName, String summary, boolean sectionDivider, String sectionCondition, boolean skipWhenConditionMet)
		{
			this.index = index;
			this.stepId = stepId;
			this.varName = varName;
			this.summary = summary;
			this.sectionDivider = sectionDivider;
			this.sectionCondition = sectionCondition;
			this.skipWhenConditionMet = skipWhenConditionMet;
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

		public boolean isSectionDivider()
		{
			return sectionDivider;
		}

		public String getSectionCondition()
		{
			return sectionCondition;
		}

		public boolean isSkipWhenConditionMet()
		{
			return skipWhenConditionMet;
		}
	}

	private static class DraftRequirementState
	{
		int rawId;
		String resolvedSymbol;
		String displayName;
	}
}
