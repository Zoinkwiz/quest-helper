package com.questhelper.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.questhelper.QuestHelperConfig;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.ManualRequirement;
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
import static com.questhelper.managers.HelperConstructModels.DraftOrderLine;
import static com.questhelper.managers.HelperConstructModels.DraftRequirement;
import static com.questhelper.managers.HelperConstructModels.DraftStep;
import static com.questhelper.managers.HelperConstructModels.ORDER_ROUTING_VARBIT_SENTINEL;
import static com.questhelper.managers.HelperConstructModels.StepKind;
import static com.questhelper.requirements.util.LogicHelper.not;
import static com.questhelper.requirements.util.LogicHelper.or;
import static com.questhelper.requirements.util.LogicHelper.nor;

@Singleton
public class HelperConstructManager
{
	private static final String MENU_PREFIX = "Construct:";
	private static final Pattern TAG_PATTERN = Pattern.compile("<[^>]+>");
	/** Use in UI when forcing varbit-based routing for an order row (matches persisted sentinel). */
	public static final int ORDER_REQUIREMENT_VARBIT_ONLY = ORDER_ROUTING_VARBIT_SENTINEL;

	private static final String CONSTRUCT_DRAFT_CONFIG_KEY = "constructDraftState";
	/** Written on export/save; older JSON without this field is still loaded (Gson default 0). */
	private static final int DRAFT_STATE_FORMAT_VERSION = 1;
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
	private final Gson prettyDraftGson = new GsonBuilder().setPrettyPrinting().create();
	private boolean worldMapRoutePreviewEnabled;

	/**
	 * Result of {@link #importDraftFromJson(String)}.
	 */
	public static final class ImportDraftResult
	{
		private final boolean success;
		private final String errorMessage;

		private ImportDraftResult(boolean success, String errorMessage)
		{
			this.success = success;
			this.errorMessage = errorMessage;
		}

		public static ImportDraftResult ok()
		{
			return new ImportDraftResult(true, null);
		}

		public static ImportDraftResult failure(String message)
		{
			return new ImportDraftResult(false, message == null ? "Unknown error" : message);
		}

		public boolean isSuccess()
		{
			return success;
		}

		public String getErrorMessage()
		{
			return errorMessage;
		}
	}

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
		currentDraft.getStepDefinitions().add(step);
		saveDraftToConfig();
		sendGameMessage("Quest Helper Construct: added " + kind.name().toLowerCase(Locale.ROOT) + " step (" + rawId + " -> " + resolved.getSymbol() + ") at " + formatWorldPoint(clickedWorldPoint) + ". Use Add Step in Order View to place it in the quest order.");
	}

	private boolean isDuplicateStep(StepKind kind, int rawId, String targetText, WorldPoint worldPoint)
	{
		for (DraftStep existingStep : currentDraft.getStepDefinitions())
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
		currentDraft.getStepDefinitions().add(step);
		saveDraftToConfig();
		sendGameMessage("Quest Helper Construct: added item step (" + normalizedItemId + " -> " + requirement.getResolvedSymbol() + "). Use Add Step in Order View to place it in the quest order.");
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
		for (DraftStep step : expandOrderToDefinitionsInOrder())
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

	public boolean isSelectedConstructPreview()
	{
		return questManager.getSelectedQuest() instanceof PreviewQuestHelper;
	}

	public boolean canStepConstructPreviewLeft()
	{
		var selected = questManager.getSelectedQuest();
		return selected instanceof PreviewQuestHelper && ((PreviewQuestHelper) selected).canStepLeft();
	}

	public boolean canStepConstructPreviewRight()
	{
		var selected = questManager.getSelectedQuest();
		return selected instanceof PreviewQuestHelper && ((PreviewQuestHelper) selected).canStepRight();
	}

	public void stepConstructPreviewLeftFromUi()
	{
		var selected = questManager.getSelectedQuest();
		if (!(selected instanceof PreviewQuestHelper))
		{
			return;
		}
		PreviewQuestHelper preview = (PreviewQuestHelper) selected;
		preview.stepLeft();
	}

	public void stepConstructPreviewRightFromUi()
	{
		var selected = questManager.getSelectedQuest();
		if (!(selected instanceof PreviewQuestHelper))
		{
			return;
		}
		PreviewQuestHelper preview = (PreviewQuestHelper) selected;
		preview.stepRight();
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
		for (DraftStep step : expandOrderToDefinitionsInOrder())
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
		if (!hasAnyOrderRef())
		{
			errors.add("no steps in quest order (use Add Step)");
		}
		return errors;
	}

	private boolean hasAnyOrderRef()
	{
		for (DraftOrderLine line : currentDraft.getOrder())
		{
			if (!line.isSectionDivider())
			{
				return true;
			}
		}
		return false;
	}

	public List<String> getStepSummaries()
	{
		ensureDraftLoaded();
		var defs = currentDraft.getStepDefinitions();
		List<String> summaries = new ArrayList<>(defs.size());
		for (int i = 0; i < defs.size(); i++)
		{
			summaries.add(formatStepSummary(defs.get(i), i + 1));
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
		for (DraftStep step : currentDraft.getStepDefinitions())
		{
			if (step.getKind() == kind)
			{
				summaries.add(formatStepSummary(step, index++));
			}
		}
		return Collections.unmodifiableList(summaries);
	}

	private List<String> getStepInstructionTextsByKind(StepKind kind)
	{
		ensureDraftLoaded();
		List<String> texts = new ArrayList<>();
		for (DraftStep step : currentDraft.getStepDefinitions())
		{
			if (step.getKind() == kind)
			{
				String t = step.getInstructionText();
				texts.add(t == null ? "" : t);
			}
		}
		return Collections.unmodifiableList(texts);
	}

	public List<String> getNpcStepInstructionTexts()
	{
		return getStepInstructionTextsByKind(StepKind.NPC);
	}

	public List<String> getObjectStepInstructionTexts()
	{
		return getStepInstructionTextsByKind(StepKind.OBJECT);
	}

	private boolean updateStepInstructionByKindAt(StepKind kind, int filteredIndex, String instructionText)
	{
		ensureDraftLoaded();
		if (filteredIndex < 0)
		{
			return false;
		}
		int i = 0;
		for (DraftStep step : currentDraft.getStepDefinitions())
		{
			if (step.getKind() != kind)
			{
				continue;
			}
			if (i == filteredIndex)
			{
				step.setInstructionText(instructionText == null ? "" : instructionText);
				saveDraftToConfig();
				return true;
			}
			i++;
		}
		return false;
	}

	public boolean updateNpcStepInstructionAt(int index, String instructionText)
	{
		return updateStepInstructionByKindAt(StepKind.NPC, index, instructionText);
	}

	public boolean updateObjectStepInstructionAt(int index, String instructionText)
	{
		return updateStepInstructionByKindAt(StepKind.OBJECT, index, instructionText);
	}

	public List<CombinedStepRow> getCombinedStepRows()
	{
		ensureDraftLoaded();
		List<DraftOrderLine> order = currentDraft.getOrder();
		List<CombinedStepRow> rows = new ArrayList<>(order.size());
		for (int i = 0; i < order.size(); i++)
		{
			DraftOrderLine line = order.get(i);
			if (line.getLineId() == null || line.getLineId().isBlank())
			{
				line.setLineId(UUID.randomUUID().toString());
				saveDraftToConfig();
			}
			if (line.isSectionDivider())
			{
				rows.add(new CombinedStepRow(
					i,
					line.getLineId(),
					line.getSuggestedVarName(),
					formatSectionLineSummary(line, i + 1),
					true,
					line.getSectionCondition(),
					line.isSkipWhenConditionMet(),
					null,
					""));
				continue;
			}
			DraftStep def = findDefinitionByStepId(line.getRefStepId());
			if (def == null)
			{
				rows.add(new CombinedStepRow(
					i,
					line.getRefStepId() == null ? "" : line.getRefStepId(),
					"?",
					"(missing step definition)",
					false,
					"",
					false,
					line.getLinkedRequirementRawId(),
					""));
				continue;
			}
			if (def.getStepId() == null || def.getStepId().isBlank())
			{
				def.setStepId(UUID.randomUUID().toString());
				saveDraftToConfig();
			}
			String instr = def.getInstructionText();
			rows.add(new CombinedStepRow(
				i,
				def.getStepId(),
				def.getSuggestedVarName(),
				formatStepSummary(def, i + 1),
				false,
				"",
				false,
				line.getLinkedRequirementRawId(),
				instr == null ? "" : instr));
		}
		return Collections.unmodifiableList(rows);
	}

	public void addSectionDivider()
	{
		ensureDraftLoaded();
		DraftOrderLine line = new DraftOrderLine();
		line.setLineId(UUID.randomUUID().toString());
		line.setSectionDivider(true);
		line.setSuggestedVarName(DEFAULT_SECTION_NAME);
		line.setSectionCondition("");
		line.setSkipWhenConditionMet(false);
		currentDraft.getOrder().add(line);
		saveDraftToConfig();
	}

	public void addOrderRef(String stepId)
	{
		ensureDraftLoaded();
		if (stepId == null || stepId.isBlank() || findDefinitionByStepId(stepId) == null)
		{
			return;
		}
		DraftOrderLine line = new DraftOrderLine();
		line.setSectionDivider(false);
		line.setRefStepId(stepId);
		line.setLinkedRequirementRawId(null);
		currentDraft.getOrder().add(line);
		saveDraftToConfig();
		if (worldMapRoutePreviewEnabled)
		{
			rebuildWorldMapRoutePoints();
		}
	}

	public boolean updateOrderLinkedRequirement(int orderIndex, Integer linkedRequirementRawId)
	{
		ensureDraftLoaded();
		if (orderIndex < 0 || orderIndex >= currentDraft.getOrder().size())
		{
			return false;
		}
		DraftOrderLine line = currentDraft.getOrder().get(orderIndex);
		if (line.isSectionDivider())
		{
			return false;
		}
		line.setLinkedRequirementRawId(linkedRequirementRawId);
		saveDraftToConfig();
		return true;
	}

	public List<StepDefinitionPickOption> getStepDefinitionPickOptions()
	{
		ensureDraftLoaded();
		List<StepDefinitionPickOption> out = new ArrayList<>();
		int i = 1;
		for (DraftStep def : currentDraft.getStepDefinitions())
		{
			if (def.getStepId() == null || def.getStepId().isBlank())
			{
				def.setStepId(UUID.randomUUID().toString());
			}
			String label = formatStepSummary(def, i++) + " [" + def.getKind() + "]";
			out.add(new StepDefinitionPickOption(def.getStepId(), label));
		}
		if (!out.isEmpty())
		{
			saveDraftToConfig();
		}
		return Collections.unmodifiableList(out);
	}

	public boolean moveStep(int fromIndex, int toIndex)
	{
		ensureDraftLoaded();
		List<DraftOrderLine> order = currentDraft.getOrder();
		if (fromIndex < 0 || toIndex < 0 || fromIndex >= order.size() || toIndex >= order.size() || fromIndex == toIndex)
		{
			return false;
		}

		DraftOrderLine moved = order.remove(fromIndex);
		order.add(toIndex, moved);
		saveDraftToConfig();
		if (worldMapRoutePreviewEnabled)
		{
			rebuildWorldMapRoutePoints();
		}
		return true;
	}

	public boolean updateOrderReferencedStepInstructionText(int orderIndex, String instructionText)
	{
		ensureDraftLoaded();
		if (orderIndex < 0 || orderIndex >= currentDraft.getOrder().size())
		{
			return false;
		}
		DraftOrderLine line = currentDraft.getOrder().get(orderIndex);
		if (line.isSectionDivider())
		{
			return false;
		}
		DraftStep def = findDefinitionByStepId(line.getRefStepId());
		if (def == null)
		{
			return false;
		}
		def.setInstructionText(instructionText == null ? "" : instructionText);
		saveDraftToConfig();
		return true;
	}

	public boolean updateStepVarName(int index, String updatedVarName)
	{
		ensureDraftLoaded();
		if (index < 0 || index >= currentDraft.getOrder().size())
		{
			return false;
		}
		DraftOrderLine line = currentDraft.getOrder().get(index);
		String v = updatedVarName == null ? "" : String.valueOf(updatedVarName);
		if (line.isSectionDivider())
		{
			line.setSuggestedVarName(v);
		}
		else
		{
			DraftStep def = findDefinitionByStepId(line.getRefStepId());
			if (def == null)
			{
				return false;
			}
			def.setSuggestedVarName(v);
		}
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
		if (index < 0 || index >= currentDraft.getOrder().size())
		{
			return false;
		}
		DraftOrderLine line = currentDraft.getOrder().get(index);
		if (!line.isSectionDivider())
		{
			return false;
		}
		line.setSectionCondition(condition == null ? "" : condition);
		saveDraftToConfig();
		return true;
	}

	public boolean toggleSectionSkipMode(int index)
	{
		ensureDraftLoaded();
		if (index < 0 || index >= currentDraft.getOrder().size())
		{
			return false;
		}
		DraftOrderLine line = currentDraft.getOrder().get(index);
		if (!line.isSectionDivider())
		{
			return false;
		}
		line.setSkipWhenConditionMet(!line.isSkipWhenConditionMet());
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

	public List<Integer> getRequirementRawIds()
	{
		ensureDraftLoaded();
		List<Integer> ids = new ArrayList<>();
		for (DraftRequirement r : currentDraft.getRequirements())
		{
			ids.add(r.getRawId());
		}
		return Collections.unmodifiableList(ids);
	}

	private boolean safeEquals(String left, String right)
	{
		return left == null ? right == null : left.equals(right);
	}

	public boolean removeStepAt(int index)
	{
		ensureDraftLoaded();
		if (index < 0 || index >= currentDraft.getOrder().size())
		{
			return false;
		}
		currentDraft.getOrder().remove(index);
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
		for (int i = 0; i < currentDraft.getStepDefinitions().size(); i++)
		{
			if (currentDraft.getStepDefinitions().get(i).getKind() != kind)
			{
				continue;
			}
			if (filteredIndex == index)
			{
				DraftStep removed = currentDraft.getStepDefinitions().remove(i);
				removeOrderRefsToStepId(removed.getStepId());
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

	public boolean removeItemStepAt(int index)
	{
		return removeStepByKindAt(StepKind.ITEM, index);
	}

	private void removeOrderRefsToStepId(String stepId)
	{
		if (stepId == null || stepId.isBlank())
		{
			return;
		}
		currentDraft.getOrder().removeIf(line -> !line.isSectionDivider() && stepId.equals(line.getRefStepId()));
	}

	private DraftStep findDefinitionByStepId(String stepId)
	{
		if (stepId == null || stepId.isBlank())
		{
			return null;
		}
		for (DraftStep def : currentDraft.getStepDefinitions())
		{
			if (stepId.equals(def.getStepId()))
			{
				return def;
			}
		}
		return null;
	}

	/**
	 * Ordered definitions for route map / preview: one entry per non-section order line with a resolved definition.
	 */
	public List<DraftStep> expandOrderToDefinitionsInOrder()
	{
		ensureDraftLoaded();
		List<DraftStep> out = new ArrayList<>();
		for (DraftOrderLine line : currentDraft.getOrder())
		{
			if (line.isSectionDivider())
			{
				continue;
			}
			DraftStep def = findDefinitionByStepId(line.getRefStepId());
			if (def != null)
			{
				out.add(def);
			}
		}
		return out;
	}

	private String formatSectionLineSummary(DraftOrderLine line, int displayIndex)
	{
		String mode = line.isSkipWhenConditionMet() ? "skip when true" : "show when true";
		String condition = line.getSectionCondition() == null || line.getSectionCondition().isBlank() ? "no condition" : line.getSectionCondition();
		return String.format("SECTION %d. %s [%s: %s]",
			displayIndex,
			line.getSuggestedVarName() == null || line.getSuggestedVarName().isBlank() ? DEFAULT_SECTION_NAME : line.getSuggestedVarName(),
			mode,
			condition);
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
			currentDraft = draftHelperFromState(state);
		}
		catch (JsonSyntaxException ignored)
		{
			currentDraft = new DraftHelper();
		}
	}

	/**
	 * Pretty-printed JSON of the current draft (same shape as config / import). For sharing or saving to a file.
	 */
	public String exportDraftJson()
	{
		ensureDraftLoaded();
		return prettyDraftGson.toJson(toDraftState(currentDraft));
	}

	/**
	 * Replace the current draft from JSON (e.g. pasted or read from a file). Persists to config on success.
	 */
	public ImportDraftResult importDraftFromJson(String json)
	{
		ensureDraftLoaded();
		if (json == null || json.isBlank())
		{
			return ImportDraftResult.failure("JSON is empty");
		}
		try
		{
			DraftState state = gson.fromJson(json.trim(), DraftState.class);
			if (state == null)
			{
				return ImportDraftResult.failure("Could not parse draft");
			}
			currentDraft = draftHelperFromState(state);
			saveDraftToConfig();
			if (worldMapRoutePreviewEnabled)
			{
				rebuildWorldMapRoutePoints();
			}
			return ImportDraftResult.ok();
		}
		catch (JsonSyntaxException e)
		{
			return ImportDraftResult.failure(e.getMessage());
		}
	}

	private static DraftHelper draftHelperFromState(DraftState state)
	{
		DraftHelper loaded = new DraftHelper();
		if (state.questName != null)
		{
			loaded.setQuestName(state.questName);
		}
		if (state.className != null)
		{
			loaded.setClassName(state.className);
		}
		if (state.packagePath != null)
		{
			loaded.setPackagePath(state.packagePath);
		}
		if (state.helperType != null)
		{
			loaded.setHelperType(state.helperType);
		}

		if (state.order != null && !state.order.isEmpty())
		{
			if (state.definitions != null)
			{
				for (DraftStepState stepState : state.definitions)
				{
					loaded.getStepDefinitions().add(draftStepFromState(stepState, false));
				}
			}
			for (DraftOrderLineState lineState : state.order)
			{
				loaded.getOrder().add(draftOrderLineFromState(lineState));
			}
		}
		else if (state.definitions != null && !state.definitions.isEmpty())
		{
			for (DraftStepState stepState : state.definitions)
			{
				loaded.getStepDefinitions().add(draftStepFromState(stepState, false));
			}
		}
		else if (state.steps != null && !state.steps.isEmpty())
		{
			migrateLegacyStepsList(state.steps, loaded);
		}

		if (state.requirements != null)
		{
			for (DraftRequirementState reqState : state.requirements)
			{
				DraftRequirement req = new DraftRequirement();
				req.setRawId(reqState.rawId);
				req.setResolvedSymbol(reqState.resolvedSymbol);
				req.setDisplayName(reqState.displayName);
				loaded.getRequirements().add(req);
			}
		}

		return loaded;
	}

	private static DraftState toDraftState(DraftHelper draft)
	{
		DraftState state = new DraftState();
		state.formatVersion = DRAFT_STATE_FORMAT_VERSION;
		state.questName = draft.getQuestName();
		state.className = draft.getClassName();
		state.packagePath = draft.getPackagePath();
		state.helperType = draft.getHelperType();

		for (DraftStep step : draft.getStepDefinitions())
		{
			DraftStepState stepState = new DraftStepState();
			stepState.stepId = step.getStepId();
			stepState.kind = step.getKind();
			stepState.sectionDivider = false;
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
			if (step.getRequiredItems() != null && !step.getRequiredItems().isEmpty())
			{
				stepState.requiredItems = new ArrayList<>(step.getRequiredItems());
			}
			if (step.getWorldPoint() != null)
			{
				stepState.worldX = step.getWorldPoint().getX();
				stepState.worldY = step.getWorldPoint().getY();
				stepState.worldPlane = step.getWorldPoint().getPlane();
			}
			state.definitions.add(stepState);
		}

		for (DraftOrderLine line : draft.getOrder())
		{
			DraftOrderLineState lineState = new DraftOrderLineState();
			lineState.lineId = line.getLineId();
			lineState.sectionDivider = line.isSectionDivider();
			lineState.suggestedVarName = line.getSuggestedVarName();
			lineState.sectionCondition = line.getSectionCondition();
			lineState.skipWhenConditionMet = line.isSkipWhenConditionMet();
			lineState.refStepId = line.getRefStepId();
			lineState.linkedRequirementRawId = line.getLinkedRequirementRawId();
			state.order.add(lineState);
		}

		for (DraftRequirement req : draft.getRequirements())
		{
			DraftRequirementState reqState = new DraftRequirementState();
			reqState.rawId = req.getRawId();
			reqState.resolvedSymbol = req.getResolvedSymbol();
			reqState.displayName = req.getDisplayName();
			state.requirements.add(reqState);
		}

		return state;
	}

	private static DraftStep draftStepFromState(DraftStepState stepState, boolean keepSectionDivider)
	{
		DraftStep step = new DraftStep();
		step.setStepId(stepState.stepId == null || stepState.stepId.isBlank() ? UUID.randomUUID().toString() : stepState.stepId);
		step.setKind(stepState.kind);
		step.setSectionDivider(keepSectionDivider && stepState.sectionDivider);
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
		if (stepState.requiredItems != null && !stepState.requiredItems.isEmpty())
		{
			step.getRequiredItems().clear();
			step.getRequiredItems().addAll(stepState.requiredItems);
		}
		return step;
	}

	private static DraftOrderLine draftOrderLineFromState(DraftOrderLineState s)
	{
		DraftOrderLine line = new DraftOrderLine();
		line.setLineId(s.lineId == null || s.lineId.isBlank() ? UUID.randomUUID().toString() : s.lineId);
		line.setSectionDivider(s.sectionDivider);
		line.setSuggestedVarName(s.suggestedVarName);
		line.setSectionCondition(s.sectionCondition);
		line.setSkipWhenConditionMet(s.skipWhenConditionMet);
		line.setRefStepId(s.refStepId);
		line.setLinkedRequirementRawId(s.linkedRequirementRawId);
		return line;
	}

	private static void migrateLegacyStepsList(List<DraftStepState> legacySteps, DraftHelper loaded)
	{
		for (DraftStepState stepState : legacySteps)
		{
			DraftStep step = draftStepFromState(stepState, true);
			if (step.isSectionDivider())
			{
				DraftOrderLine line = new DraftOrderLine();
				line.setLineId(step.getStepId());
				line.setSectionDivider(true);
				line.setSuggestedVarName(step.getSuggestedVarName());
				line.setSectionCondition(step.getSectionCondition());
				line.setSkipWhenConditionMet(step.isSkipWhenConditionMet());
				loaded.getOrder().add(line);
				continue;
			}
			step.setSectionDivider(false);
			loaded.getStepDefinitions().add(step);
			DraftOrderLine refLine = new DraftOrderLine();
			refLine.setSectionDivider(false);
			refLine.setRefStepId(step.getStepId());
			refLine.setLinkedRequirementRawId(null);
			loaded.getOrder().add(refLine);
		}
	}

	private void saveDraftToConfig()
	{
		if (configManager == null)
		{
			return;
		}
		configManager.setConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, CONSTRUCT_DRAFT_CONFIG_KEY, gson.toJson(toDraftState(currentDraft)));
	}

	private boolean isNpcAction(MenuAction menuAction)
	{
		return menuAction == MenuAction.NPC_FIRST_OPTION || menuAction == MenuAction.NPC_SECOND_OPTION;
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
		List<DraftStep> steps = expandOrderToDefinitionsInOrder();
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

	private class PreviewQuestHelper extends ComplexStateQuestHelper
	{
		private final DraftHelper draft;
		private final List<ItemRequirement> previewRequirements = new ArrayList<>();
		private final List<PanelDetails> previewPanels = new ArrayList<>();
		private final List<ManualRequirement> manualStepRequirements = new ArrayList<>();
		private int previewProgressIndex;

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
			manualStepRequirements.clear();
			Map<Integer, ItemRequirement> requirementById = new HashMap<>();
			for (ItemRequirement req : previewRequirements)
			{
				requirementById.put(req.getId(), req);
			}

			List<ConditionalStep> sectionTasks = new ArrayList<>();
			List<List<Requirement>> sectionCompletionRequirements = new ArrayList<>();
			List<PreviewStepEntry> currentSectionEntries = new ArrayList<>();
			String currentPanelName = "Captured Steps";

			for (DraftOrderLine orderLine : draft.getOrder())
			{
				if (orderLine.isSectionDivider())
				{
					ConditionalStep sectionTask = createSectionTask(currentPanelName, currentSectionEntries);
					if (sectionTask != null)
					{
						sectionCompletionRequirements.add(extractSectionCompletionRequirements(currentSectionEntries));
						addPanelWithLockingStep(currentPanelName, currentSectionEntries, sectionTask);
						sectionTasks.add(sectionTask);
					}
					String sectionName = orderLine.getSuggestedVarName();
					currentPanelName = (sectionName == null || sectionName.isBlank()) ? "Section" : sectionName;
					continue;
				}

				DraftStep def = findDefinitionByStepId(orderLine.getRefStepId());
				if (def == null)
				{
					continue;
				}

				PreviewStepEntry stepEntry = toPreviewStep(def, requirementById, orderLine.getLinkedRequirementRawId());
				if (stepEntry != null)
				{
					currentSectionEntries.add(stepEntry);
				}
			}

			if (!currentSectionEntries.isEmpty())
			{
				ConditionalStep sectionTask = createSectionTask(currentPanelName, currentSectionEntries);
				if (sectionTask != null)
				{
					sectionCompletionRequirements.add(extractSectionCompletionRequirements(currentSectionEntries));
					addPanelWithLockingStep(currentPanelName, currentSectionEntries, sectionTask);
					sectionTasks.add(sectionTask);
				}
			}
			if (sectionTasks.isEmpty())
			{
				QuestStep empty = new DetailedQuestStep(this, "No previewable steps captured yet.");
				previewPanels.add(new PanelDetails("Captured Steps", List.of(empty)));
				return empty;
			}

			if (previewProgressIndex < 0)
			{
				previewProgressIndex = 0;
			}
			if (previewProgressIndex > manualStepRequirements.size())
			{
				previewProgressIndex = manualStepRequirements.size();
			}
			syncManualProgress();

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

		private ConditionalStep createSectionTask(String panelName, List<PreviewStepEntry> sectionEntries)
		{
			if (sectionEntries.isEmpty())
			{
				return null;
			}

			QuestStep fallbackStep = sectionEntries.get(sectionEntries.size() - 1).step;
			ConditionalStep sectionTask = new ConditionalStep(this, fallbackStep, panelName);
			sectionTask.setShouldPassthroughText(true);
			for (PreviewStepEntry entry : sectionEntries)
			{
				sectionTask.addStep(not(entry.completionRequirement), entry.step);
			}
			return sectionTask;
		}

		private void addPanelWithLockingStep(String panelName, List<PreviewStepEntry> sectionEntries, ConditionalStep sectionTask)
		{
			if (sectionTask == null || sectionEntries.isEmpty())
			{
				return;
			}

			List<QuestStep> stepsForPanel = new ArrayList<>();
			for (PreviewStepEntry entry : sectionEntries)
			{
				stepsForPanel.add(entry.step);
			}
			PanelDetails panel = new PanelDetails(panelName, stepsForPanel);
			panel.setLockingStep(sectionTask);
			previewPanels.add(panel);
			sectionEntries.clear();
		}

		private List<Requirement> extractSectionCompletionRequirements(List<PreviewStepEntry> sectionEntries)
		{
			LinkedHashSet<Requirement> requirements = new LinkedHashSet<>();
			for (PreviewStepEntry entry : sectionEntries)
			{
				requirements.add(entry.completionRequirement);
			}
			return new ArrayList<>(requirements);
		}

		private PreviewStepEntry toPreviewStep(DraftStep draftStep, Map<Integer, ItemRequirement> requirementById, Integer orderLinkedOverride)
		{
			String instruction = (draftStep.getInstructionText() == null || draftStep.getInstructionText().isBlank())
				? "Complete step."
				: draftStep.getInstructionText();
			QuestStep step;
			Requirement originalCompletionRequirement = previewCompletionRequirement(draftStep, requirementById, orderLinkedOverride);
			if (draftStep.getKind() == StepKind.NPC)
			{
				if (draftStep.getWorldPoint() != null)
				{
					step = new NpcStep(this, draftStep.getRawId(), draftStep.getWorldPoint(), instruction);
				}
				else
				{
					step = new NpcStep(this, draftStep.getRawId(), instruction);
				}
			}
			else if (draftStep.getKind() == StepKind.OBJECT)
			{
				if (draftStep.getWorldPoint() != null)
				{
					step = new ObjectStep(this, draftStep.getRawId(), draftStep.getWorldPoint(), instruction);
				}
				else
				{
					step = new ObjectStep(this, draftStep.getRawId(), instruction);
				}
			}
			else if (draftStep.getKind() == StepKind.ITEM)
			{
				Integer highlightRawId = itemHighlightRawIdForPreview(draftStep, orderLinkedOverride);
				ItemRequirement itemRequirement = null;
				if (highlightRawId != null)
				{
					itemRequirement = requirementById.get(highlightRawId);
				}
				if (itemRequirement == null)
				{
					itemRequirement = requirementById.get(draftStep.getRawId());
				}
				if (itemRequirement != null)
				{
					step = new ItemStep(this, instruction, itemRequirement.highlighted());
				}
				else
				{
					step = new ItemStep(this, instruction);
				}
			}
			else
			{
				step = new DetailedQuestStep(this, instruction);
			}

			ManualRequirement manualOverride = defaultIncompleteRequirement();
			manualStepRequirements.add(manualOverride);
			return new PreviewStepEntry(step, or(originalCompletionRequirement, manualOverride));
		}

		private Requirement previewCompletionRequirement(DraftStep def, Map<Integer, ItemRequirement> requirementById, Integer orderLinkedOverride)
		{
			if (orderLinkedOverride != null && orderLinkedOverride == ORDER_ROUTING_VARBIT_SENTINEL)
			{
				return defaultIncompleteRequirement();
			}
			Integer rid = orderLinkedOverride != null ? orderLinkedOverride : def.getLinkedRequirementRawId();
			if (rid != null)
			{
				ItemRequirement ir = requirementById.get(rid);
				if (ir != null)
				{
					return ir;
				}
			}
			return defaultIncompleteRequirement();
		}

		private Integer itemHighlightRawIdForPreview(DraftStep def, Integer orderLinkedOverride)
		{
			if (orderLinkedOverride != null && orderLinkedOverride != ORDER_ROUTING_VARBIT_SENTINEL)
			{
				return orderLinkedOverride;
			}
			return def.getLinkedRequirementRawId();
		}

		private ManualRequirement defaultIncompleteRequirement()
		{
			ManualRequirement requirement = new ManualRequirement();
			requirement.setShouldPass(false);
			return requirement;
		}

		private void syncManualProgress()
		{
			for (int i = 0; i < manualStepRequirements.size(); i++)
			{
				manualStepRequirements.get(i).setShouldPass(i < previewProgressIndex);
			}
		}

		private boolean canStepLeft()
		{
			return previewProgressIndex > 0 && !manualStepRequirements.isEmpty();
		}

		private boolean canStepRight()
		{
			return previewProgressIndex < manualStepRequirements.size();
		}

		private boolean stepLeft()
		{
			if (!canStepLeft())
			{
				return false;
			}
			previewProgressIndex--;
			syncManualProgress();
			return true;
		}

		private boolean stepRight()
		{
			if (!canStepRight())
			{
				return false;
			}
			previewProgressIndex++;
			syncManualProgress();
			return true;
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

	private static class PreviewStepEntry
	{
		private final QuestStep step;
		private final Requirement completionRequirement;

		private PreviewStepEntry(QuestStep step, Requirement completionRequirement)
		{
			this.step = step;
			this.completionRequirement = completionRequirement;
		}
	}

	private static class DraftState
	{
		int formatVersion;
		String questName;
		String className;
		String packagePath;
		String helperType;
		/** @deprecated Legacy single-list format; migrated on load. */
		List<DraftStepState> steps = new ArrayList<>();
		List<DraftStepState> definitions = new ArrayList<>();
		List<DraftOrderLineState> order = new ArrayList<>();
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
		List<Integer> requiredItems;
	}

	public static final class StepDefinitionPickOption
	{
		private final String stepId;
		private final String label;

		public StepDefinitionPickOption(String stepId, String label)
		{
			this.stepId = stepId;
			this.label = label;
		}

		public String getStepId()
		{
			return stepId;
		}

		public String getLabel()
		{
			return label;
		}

		@Override
		public String toString()
		{
			return label;
		}
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
		private final Integer orderLinkedRequirementRawId;
		private final String instructionText;

		public CombinedStepRow(int index, String stepId, String varName, String summary, boolean sectionDivider, String sectionCondition, boolean skipWhenConditionMet, Integer orderLinkedRequirementRawId, String instructionText)
		{
			this.index = index;
			this.stepId = stepId;
			this.varName = varName;
			this.summary = summary;
			this.sectionDivider = sectionDivider;
			this.sectionCondition = sectionCondition;
			this.skipWhenConditionMet = skipWhenConditionMet;
			this.orderLinkedRequirementRawId = orderLinkedRequirementRawId;
			this.instructionText = instructionText == null ? "" : instructionText;
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

		/**
		 * Order-line override for routing / item highlight: {@code null} inherit, {@link HelperConstructModels#ORDER_ROUTING_VARBIT_SENTINEL} varbit, else requirement raw id.
		 */
		public Integer getOrderLinkedRequirementRawId()
		{
			return orderLinkedRequirementRawId;
		}

		/** Player-facing step text used in generated helpers (empty for section rows). */
		public String getInstructionText()
		{
			return instructionText;
		}
	}

	private static class DraftOrderLineState
	{
		String lineId;
		boolean sectionDivider;
		String suggestedVarName;
		String sectionCondition;
		boolean skipWhenConditionMet;
		String refStepId;
		Integer linkedRequirementRawId;
	}

	private static class DraftRequirementState
	{
		int rawId;
		String resolvedSymbol;
		String displayName;
	}
}
