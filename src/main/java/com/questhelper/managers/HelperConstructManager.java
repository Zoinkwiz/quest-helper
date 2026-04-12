package com.questhelper.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.questhelper.managers.taskstroute.TasksTrackerRouteExporter;
import com.questhelper.managers.HelperConstructModels.DraftOrderStepRequirement;
import com.questhelper.managers.construct.DraftRoutingIds;
import com.questhelper.managers.construct.ConstructMenuCapture;
import com.questhelper.managers.construct.MakerDraftFileStore;
import com.questhelper.managers.construct.MakerDraftJsonLoader;
import com.questhelper.managers.construct.MakerPreviewRuntime;
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
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.tools.QuestPerspective;
import com.questhelper.tools.ConstructWorldMapPoint;
import com.questhelper.util.worldmap.WorldPointMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.Menu;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.NPC;
import net.runelite.api.Tile;
import net.runelite.api.WorldView;
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
import javax.inject.Singleton;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;

import static com.questhelper.managers.HelperConstructModels.DraftHelper;
import static com.questhelper.managers.HelperConstructModels.DraftOrderLine;
import static com.questhelper.managers.HelperConstructModels.DraftRequirement;
import static com.questhelper.managers.HelperConstructModels.DraftStep;
import static com.questhelper.managers.HelperConstructModels.DraftStepAttachedRequirement;
import static com.questhelper.managers.HelperConstructModels.StepAttachmentKind;
import static com.questhelper.managers.HelperConstructModels.StepKind;
import static com.questhelper.requirements.util.LogicHelper.and;
import static com.questhelper.requirements.util.LogicHelper.not;
import static com.questhelper.requirements.util.LogicHelper.or;
import static com.questhelper.requirements.util.LogicHelper.nor;

@Singleton
@Slf4j
public class HelperConstructManager
{
	private static final Pattern TAG_PATTERN = Pattern.compile("<[^>]+>");
	/** One quest-order "linked item" option: label and the raw item id used for routing / highlight. */
	public static final class RequirementRoutingChoice
	{
		private final String label;
		private final int rawId;

		public RequirementRoutingChoice(String label, int rawId)
		{
			this.label = label;
			this.rawId = rawId;
		}

		public String getLabel()
		{
			return label;
		}

		public int getRawId()
		{
			return rawId;
		}
	}

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
		if (!config.constructModeEnabled())
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
		var itemID = sourceEntry.getItemId();

		WorldPoint clickedWorldPoint = resolveClickedWorldPoint(sourceEntry, event);

		if (isNpcAction(sourceType))
		{
			addAction(menuEntries, ConstructMenuCapture.MENU_OPTION_PREFIX + " Add NPC Step", target, () -> addStep(StepKind.NPC, sourceEntry.getNpc().getId(), option, target, clickedWorldPoint));
		}
		else if (isObjectAction(sourceType))
		{
			addAction(menuEntries, ConstructMenuCapture.MENU_OPTION_PREFIX + " Add Object Step", target, () -> addStep(StepKind.OBJECT, rawId, option, target, clickedWorldPoint));
		}
		else if (isItemAction(sourceType))
		{
			addAction(menuEntries, ConstructMenuCapture.MENU_OPTION_PREFIX + " Add Item Requirement", target, () -> addRequirement(rawId, target));
			addAction(menuEntries, ConstructMenuCapture.MENU_OPTION_PREFIX + " Add Generic Step (item)", target, () -> addGenericStepFromItem(rawId, target));
		}
		else if (isInventoryItemAction(sourceType))
		{
			addAction(menuEntries, ConstructMenuCapture.MENU_OPTION_PREFIX + " Add Item Requirement", target, () -> addRequirement(itemID, target));
			addAction(menuEntries, ConstructMenuCapture.MENU_OPTION_PREFIX + " Add Generic Step (item)", target, () -> addGenericStepFromItem(itemID, target));
		}
		else if (isWalkHereMenu(sourceType, option) && clickedWorldPoint != null)
		{
			addAction(menuEntries, ConstructMenuCapture.MENU_OPTION_PREFIX + " Add Generic Step (here)", target, () -> addGenericStepAtWorldPoint(clickedWorldPoint));
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
		saveDraftToConfig();
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
		step.setOption(option);
		step.setTargetText(target);
		step.setInstructionText(instructionText(option, target));
		step.setPanelName("Captured Steps");
		step.setSuggestedVarName(HelperScaffoldGenerator.toVarName(option + " " + target, "step"));
		step.setWorldPoint(clickedWorldPoint);
		currentDraft.getStepDefinitions().add(step);
		saveDraftToConfig();
		sendGameMessage("Quest Helper Construct: added " + kind.name().toLowerCase(Locale.ROOT) + " step (" + rawId + ") at " + formatWorldPoint(clickedWorldPoint) + ". Use Add Step in Order View to place it in the quest order.");
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

		if (isObjectAction(sourceEntry.getType()))
		{
			var fromObject = resolveObjectWorldPoint(sourceEntry);
			if (fromObject != null)
			{
				return fromObject;
			}
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

	private WorldPoint resolveObjectWorldPoint(MenuEntry sourceEntry)
	{
		if (client.getTopLevelWorldView() == null)
		{
			return null;
		}

		var baseX = client.getTopLevelWorldView().getBaseX();
		var baseY = client.getTopLevelWorldView().getBaseY();

		var sceneX = sourceEntry.getParam0();
		var sceneY = sourceEntry.getParam1();

		var worldView = client.getTopLevelWorldView();

		return normalizeWorldPointWithWorldView(worldView, new WorldPoint(baseX + sceneX, baseY + sceneY, worldView.getPlane()));
	}

	private WorldPoint resolveSceneTileWorldPoint(MenuEntryAdded event)
	{
		if (client.getTopLevelWorldView() == null)
		{
			return null;
		}

		var tile = client.getTopLevelWorldView().getSelectedSceneTile();
		if (tile == null) return null;
		if (tile.getLocalLocation() == null) return null;

		return normalizeLocalPoint(tile.getLocalLocation());

	}

	private WorldPoint normalizeLocalPoint(LocalPoint localPoint)
	{
		if (localPoint == null)
		{
			return null;
		}
		return QuestPerspective.getWorldPointConsideringWorldView(client, localPoint);
	}

	private WorldPoint normalizeWorldPointWithWorldView(WorldView worldView, WorldPoint worldPoint)
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
		requirement.setDisplayName(normalizeText(target).isBlank() ? "Captured Item" : normalizeText(target));
		currentDraft.getRequirements().add(requirement);
		saveDraftToConfig();
		sendGameMessage("Quest Helper Construct: added item requirement (" + idToUse + ").");
	}

	private void addGenericStepFromItem(int rawId, String target)
	{
		ensureDraftLoaded();
		int normalizedItemId = normalizeItemId(rawId);
		DraftRequirement requirement = findOrCreateRequirement(normalizedItemId, target);
		if (isDuplicateGenericItemStep(normalizedItemId, target))
		{
			sendGameMessage("Quest Helper Construct: skipped duplicate generic step for item (" + normalizedItemId + ").");
			return;
		}

		DraftStep step = new DraftStep();
		step.setStepId(UUID.randomUUID().toString());
		step.setKind(StepKind.TEXT);
		step.setRawId(0);
		step.setOption("Use");
		step.setTargetText(target);
		String itemName = normalizeText(target).isBlank() ? requirement.getDisplayName() : normalizeText(target);
		step.setInstructionText("Use " + itemName + ".");
		step.setPanelName("Captured Steps");
		step.setSuggestedVarName(HelperScaffoldGenerator.toVarName("use " + itemName, "step"));
		step.getAttachedRequirements().add(DraftStepAttachedRequirement.item(normalizedItemId));
		currentDraft.getStepDefinitions().add(step);
		saveDraftToConfig();
		sendGameMessage("Quest Helper Construct: added generic step with item " + normalizedItemId + ". Add it to quest order if needed.");
	}

	private void addGenericStepAtWorldPoint(WorldPoint worldPoint)
	{
		ensureDraftLoaded();
		if (worldPoint == null)
		{
			return;
		}
		if (isDuplicateGenericWalkStep(worldPoint))
		{
			sendGameMessage("Quest Helper Construct: skipped duplicate generic step at " + formatWorldPoint(worldPoint) + ".");
			return;
		}
		DraftStep step = new DraftStep();
		step.setStepId(UUID.randomUUID().toString());
		step.setKind(StepKind.TEXT);
		step.setRawId(0);
		step.setWorldPoint(worldPoint);
		step.setOption("Walk here");
		step.setTargetText("");
		step.setInstructionText("Go to " + formatWorldPoint(worldPoint) + ".");
		step.setPanelName("Captured Steps");
		step.setSuggestedVarName(HelperScaffoldGenerator.toVarName("go " + worldPoint.getX() + " " + worldPoint.getY(), "step"));
		currentDraft.getStepDefinitions().add(step);
		saveDraftToConfig();
		sendGameMessage("Quest Helper Construct: added generic step at " + formatWorldPoint(worldPoint) + ".");
	}

	private boolean isWalkHereMenu(MenuAction sourceType, String option)
	{
		if (option == null)
		{
			return false;
		}
		if (!"Walk here".equalsIgnoreCase(option.trim()))
		{
			return false;
		}
		return !isNpcAction(sourceType) && !isObjectAction(sourceType);
	}

	private boolean isDuplicateGenericItemStep(int normalizedItemId, String target)
	{
		String t = normalizeText(target);
		for (DraftStep existing : currentDraft.getStepDefinitions())
		{
			if (existing.getKind() != StepKind.TEXT)
			{
				continue;
			}
			if (existing.getWorldPoint() != null)
			{
				continue;
			}
			if (!safeEquals(normalizeText(existing.getTargetText()), t))
			{
				continue;
			}
			for (DraftStepAttachedRequirement a : existing.getAttachedRequirements())
			{
				if (StepAttachmentKind.ITEM.name().equalsIgnoreCase(a.getKind())
					&& a.getItemRawId() != null && a.getItemRawId() == normalizedItemId)
				{
					return true;
				}
			}
		}
		return false;
	}

	private boolean isDuplicateGenericWalkStep(WorldPoint worldPoint)
	{
		for (DraftStep existing : currentDraft.getStepDefinitions())
		{
			if (existing.getKind() != StepKind.TEXT || existing.getWorldPoint() == null)
			{
				continue;
			}
			if (worldPoint.equals(existing.getWorldPoint()))
			{
				return true;
			}
		}
		return false;
	}

	private DraftRequirement findOrCreateRequirement(int normalizedItemId, String target)
	{
		for (DraftRequirement requirement : currentDraft.getRequirements())
		{
			if (requirement.getRawId() == normalizedItemId)
			{
				return requirement;
			}
			if (requirement.getAlternateRawIds().contains(normalizedItemId))
			{
				return requirement;
			}
		}

		DraftRequirement requirement = new DraftRequirement();
		requirement.setRawId(normalizedItemId);
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
		reconcileOrderSlotRoutingAttachments();
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
		reconcileOrderSlotRoutingAttachments();
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

	public List<String> getStepSummaries(ConstructStepKind kind)
	{
		return getStepSummariesByKind(kind.stepKind());
	}

	/** Variable names for step definitions in the maker tables (same field as order column Name/Var). */
	public List<String> getStepVarNames(ConstructStepKind kind)
	{
		return getStepVarNamesByKind(kind.stepKind());
	}

	private List<String> getStepVarNamesByKind(StepKind kind)
	{
		ensureDraftLoaded();
		List<String> out = new ArrayList<>();
		for (DraftStep step : currentDraft.getStepDefinitions())
		{
			if (step.getKind() != kind)
			{
				continue;
			}
			String v = step.getSuggestedVarName();
			out.add(v == null || v.isBlank() ? "?" : v);
		}
		return Collections.unmodifiableList(out);
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

	public List<String> getStepInstructionTexts(ConstructStepKind kind)
	{
		return getStepInstructionTextsByKind(kind.stepKind());
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

	public boolean updateStepInstructionAt(ConstructStepKind kind, int index, String instructionText)
	{
		return updateStepInstructionByKindAt(kind.stepKind(), index, instructionText);
	}

	public List<String> getStepRawIdTexts(ConstructStepKind kind)
	{
		return getStepRawIdTextsByKind(kind.stepKind());
	}

	private List<String> getStepRawIdTextsByKind(StepKind kind)
	{
		ensureDraftLoaded();
		List<String> out = new ArrayList<>();
		for (DraftStep step : currentDraft.getStepDefinitions())
		{
			if (step.getKind() != kind)
			{
				continue;
			}
			if (kind == StepKind.NPC || kind == StepKind.OBJECT)
			{
				out.add(DraftRoutingIds.formatCsvIds(DraftRoutingIds.mergedStepOrRequirementIds(step.getRawId(), step.getAlternateRawIds())));
			}
			else
			{
				out.add("");
			}
		}
		return Collections.unmodifiableList(out);
	}

	public boolean updateStepRawIdAt(ConstructStepKind kind, int index, String rawIdText)
	{
		return updateStepRawIdAt(kind.stepKind(), index, rawIdText);
	}

	/**
	 * Changes a step definition's kind (e.g. generic TEXT to NPC/Object) while keeping {@link DraftStep#getStepId()}
	 * so quest order and varbit rows stay linked.
	 */
	public boolean convertStepDefinitionKind(ConstructStepKind fromKind, int rowIndex, ConstructStepKind toKind)
	{
		ensureDraftLoaded();
		if (fromKind == null || toKind == null || fromKind == toKind)
		{
			return fromKind == toKind;
		}
		StepKind fromSk = fromKind.stepKind();
		StepKind toSk = toKind.stepKind();
		DraftStep step = stepDefinitionAtKindIndexOrNull(fromSk, rowIndex);
		if (step == null)
		{
			return false;
		}
		step.setKind(toSk);
		if (toSk == StepKind.TEXT)
		{
			step.setRawId(0);
			step.getAlternateRawIds().clear();
			step.setOption("");
			step.setTargetText("");
		}
		else if (toSk == StepKind.NPC || toSk == StepKind.OBJECT)
		{
			step.setRawId(0);
			step.getAlternateRawIds().clear();
			if (step.getOption() == null)
			{
				step.setOption("");
			}
			if (step.getTargetText() == null)
			{
				step.setTargetText("");
			}
		}
		reconcileOrderSlotRoutingAttachments();
		saveDraftToConfig();
		rebuildWorldMapRouteIfEnabled();
		return true;
	}

	public void addEmptyStepFromUi(ConstructStepKind kind)
	{
		ensureDraftLoaded();
		StepKind sk = kind.stepKind();
		DraftStep step = new DraftStep();
		step.setStepId(UUID.randomUUID().toString());
		step.setKind(sk);
		step.setInstructionText("");
		step.setTargetText("");
		step.setPanelName("Captured Steps");
		step.setWorldPoint(null);
		step.setOption("");
		step.setSectionDivider(false);
		if (sk == StepKind.NPC || sk == StepKind.OBJECT)
		{
			step.setRawId(0);
			step.setSuggestedVarName(HelperScaffoldGenerator.toVarName(sk == StepKind.NPC ? "npc step" : "object step", "step"));
		}
		else
		{
			step.setRawId(0);
			step.setSuggestedVarName(HelperScaffoldGenerator.toVarName("generic step", "step"));
		}
		currentDraft.getStepDefinitions().add(step);
		saveDraftToConfig();
		rebuildWorldMapRouteIfEnabled();
	}

	public List<String> getStepWorldPointTexts(ConstructStepKind kind)
	{
		return getWorldPointTextsByKind(kind.stepKind());
	}

	public List<String> getStepRequiredItemsTexts(ConstructStepKind kind)
	{
		return getRequiredItemsTextsByKind(kind.stepKind());
	}

	public boolean updateStepWorldPointAt(ConstructStepKind kind, int index, String text)
	{
		return updateStepWorldPointAt(kind.stepKind(), index, text);
	}

	public boolean updateStepRequiredItemsAt(ConstructStepKind kind, int index, String text)
	{
		return updateStepRequiredItemsAt(kind.stepKind(), index, text);
	}

	public List<String> getStepRequirementsDisplays(ConstructStepKind kind)
	{
		return getRequirementsDisplaysByKind(kind.stepKind());
	}

	public List<Integer> getStepRequiredItemIdsCopyAt(ConstructStepKind kind, int index)
	{
		return getRequiredItemIdsCopyAt(kind.stepKind(), index);
	}

	public List<StepAttachmentEdit> getStepAttachmentsAt(ConstructStepKind kind, int index)
	{
		return getStepAttachmentsAt(kind.stepKind(), index);
	}

	public boolean applyStepAttachmentsAt(ConstructStepKind kind, int index, List<StepAttachmentEdit> attachments)
	{
		return applyStepAttachmentsAt(kind.stepKind(), index, attachments);
	}

	public String summarizeStepAttachmentEdit(StepAttachmentEdit edit)
	{
		if (edit == null)
		{
			return "";
		}
		if (StepAttachmentKind.VARBIT.name().equalsIgnoreCase(edit.getKind()))
		{
			String disp = edit.getVarbitDisplayText();
			if (disp != null && !disp.isBlank())
			{
				return disp;
			}
			int vid = edit.getVarbitId() == null ? 0 : edit.getVarbitId();
			int val = edit.getVarbitRequiredValue() == null ? 0 : edit.getVarbitRequiredValue();
			String op = edit.getVarbitOperation() == null ? "EQUAL" : edit.getVarbitOperation();
			return vid + " " + op + " " + val;
		}
		if (StepAttachmentKind.ITEM.name().equalsIgnoreCase(edit.getKind()))
		{
			if (edit.getItemRawId() == null)
			{
				return "(unset)";
			}
			return requirementDisplayLabelForRawId(edit.getItemRawId());
		}
		String fk = edit.getKind();
		return fk == null || fk.isBlank() ? "Attachment" : fk;
	}

	private List<String> getRequirementsDisplaysByKind(StepKind kind)
	{
		ensureDraftLoaded();
		List<String> out = new ArrayList<>();
		for (DraftStep step : currentDraft.getStepDefinitions())
		{
			if (step.getKind() == kind)
			{
				out.add(formatStepRequirementsDisplay(step));
			}
		}
		return Collections.unmodifiableList(out);
	}

	private String formatStepRequirementsDisplay(DraftStep step)
	{
		List<DraftStepAttachedRequirement> list = step.getAttachedRequirements();
		if (list == null || list.isEmpty())
		{
			return "Click to set…";
		}
		List<String> parts = new ArrayList<>();
		for (DraftStepAttachedRequirement a : list)
		{
			parts.add(summarizeStepAttachmentEdit(stepAttachmentEditFromDraft(a)));
		}
		if (parts.size() > 2)
		{
			return parts.get(0) + " · " + parts.get(1) + " (+" + (parts.size() - 2) + ")";
		}
		return String.join(" · ", parts);
	}

	private String labelForCapturedRequirementRawId(int rawId)
	{
		List<String> labels = getRequirementSummaries();
		int i = indexOfRequirementContainingItemId(rawId);
		if (i >= 0)
		{
			return i < labels.size() ? labels.get(i) : String.valueOf(rawId);
		}
		return String.valueOf(rawId);
	}

	/** Display text for a captured requirement id (no {@code N.} list prefix, no type prefix). */
	private String requirementDisplayLabelForRawId(int rawId)
	{
		String numbered = labelForCapturedRequirementRawId(rawId);
		return numbered.replaceFirst("^\\d+\\.\\s*", "").trim();
	}

	private List<StepAttachmentEdit> getStepAttachmentsAt(StepKind kind, int filteredIndex)
	{
		DraftStep step = stepDefinitionAtKindIndexOrNull(kind, filteredIndex);
		if (step == null)
		{
			return Collections.emptyList();
		}
		List<StepAttachmentEdit> out = new ArrayList<>();
		for (DraftStepAttachedRequirement a : step.getAttachedRequirements())
		{
			out.add(stepAttachmentEditFromDraft(a));
		}
		return Collections.unmodifiableList(out);
	}

	private boolean applyStepAttachmentsAt(StepKind kind, int filteredIndex, List<StepAttachmentEdit> attachments)
	{
		DraftStep step = stepDefinitionAtKindIndexOrNull(kind, filteredIndex);
		if (step == null)
		{
			return false;
		}
		if (attachments == null)
		{
			return false;
		}
		List<DraftStepAttachedRequirement> next = new ArrayList<>();
		for (StepAttachmentEdit edit : attachments)
		{
			if (StepAttachmentKind.VARBIT.name().equalsIgnoreCase(edit.getKind())
				&& edit.getOrderSlotId() != null && !edit.getOrderSlotId().isBlank())
			{
				DraftOrderLine line = findOrderLineByOrderSlotId(edit.getOrderSlotId());
				if (line == null || line.isSectionDivider() || !step.getStepId().equals(line.getRefStepId()))
				{
					return false;
				}
				DraftStepAttachedRequirement d = draftAttachedRequirementFromEdit(edit);
				if (d == null)
				{
					return false;
				}
				DraftStepAttachedRequirement.setOrderLineRoutingVarbit(line, d);
				continue;
			}
			DraftStepAttachedRequirement d = draftAttachedRequirementFromEdit(edit);
			if (d == null)
			{
				return false;
			}
			next.add(d);
		}
		step.getAttachedRequirements().clear();
		step.getAttachedRequirements().addAll(next);
		saveDraftToConfig();
		return true;
	}

	private static StepAttachmentEdit stepAttachmentEditFromDraft(DraftStepAttachedRequirement a)
	{
		if (a == null)
		{
			return null;
		}
		String k = a.getKind() == null ? StepAttachmentKind.ITEM.name() : a.getKind();
		if (StepAttachmentKind.VARBIT.name().equalsIgnoreCase(k))
		{
			int vid = a.getVarbitId() == null ? 0 : a.getVarbitId();
			int val = a.getVarbitRequiredValue() == null ? 1 : a.getVarbitRequiredValue();
			if (a.getOrderSlotId() != null && !a.getOrderSlotId().isBlank())
			{
				return StepAttachmentEdit.varbitForOrderSlot(a.getOrderSlotId(), vid, val, a.getVarbitOperation(), a.getVarbitDisplayText());
			}
			return StepAttachmentEdit.varbit(vid, val, a.getVarbitOperation(), a.getVarbitDisplayText());
		}
		int itemId = a.getItemRawId() == null ? 0 : a.getItemRawId();
		return StepAttachmentEdit.item(itemId, a.isAttachmentHighlighted());
	}

	private static DraftStepAttachedRequirement draftAttachedRequirementFromEdit(StepAttachmentEdit edit)
	{
		if (edit == null)
		{
			return null;
		}
		if (StepAttachmentKind.VARBIT.name().equalsIgnoreCase(edit.getKind()))
		{
			VarbitSpec spec = VarbitSpec.tryFromStepAttachmentEdit(edit);
			if (spec == null)
			{
				return null;
			}
			if (edit.getOrderSlotId() != null && !edit.getOrderSlotId().isBlank())
			{
				return DraftStepAttachedRequirement.routingVarbitForOrderSlot(
					edit.getOrderSlotId(),
					spec.getVarbitId(),
					spec.getRequiredValue(),
					spec.getOperation().name(),
					spec.getDisplayText());
			}
			return DraftStepAttachedRequirement.varbit(
				spec.getVarbitId(),
				spec.getRequiredValue(),
				spec.getOperation().name(),
				spec.getDisplayText());
		}
		if (edit.getItemRawId() == null)
		{
			return null;
		}
		return DraftStepAttachedRequirement.item(edit.getItemRawId(), edit.isItemHighlighted());
	}

	private DraftStep stepDefinitionAtKindIndexOrNull(StepKind kind, int filteredIndex)
	{
		ensureDraftLoaded();
		if (filteredIndex < 0)
		{
			return null;
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
				return step;
			}
			i++;
		}
		return null;
	}

	private List<Integer> getRequiredItemIdsCopyAt(StepKind kind, int filteredIndex)
	{
		DraftStep step = stepDefinitionAtKindIndexOrNull(kind, filteredIndex);
		if (step == null)
		{
			return Collections.emptyList();
		}
		List<DraftStepAttachedRequirement> list = step.getAttachedRequirements();
		if (list == null || list.isEmpty())
		{
			return Collections.emptyList();
		}
		List<Integer> out = new ArrayList<>();
		for (DraftStepAttachedRequirement a : list)
		{
			String k = a.getKind() == null ? StepAttachmentKind.ITEM.name() : a.getKind();
			if (!StepAttachmentKind.ITEM.name().equalsIgnoreCase(k))
			{
				continue;
			}
			Integer id = a.getItemRawId();
			if (id != null)
			{
				out.add(id);
			}
		}
		return Collections.unmodifiableList(out);
	}

	private List<String> getWorldPointTextsByKind(StepKind kind)
	{
		ensureDraftLoaded();
		List<String> out = new ArrayList<>();
		for (DraftStep step : currentDraft.getStepDefinitions())
		{
			if (step.getKind() == kind)
			{
				out.add(formatWorldPointForField(step.getWorldPoint()));
			}
		}
		return Collections.unmodifiableList(out);
	}

	private List<String> getRequiredItemsTextsByKind(StepKind kind)
	{
		ensureDraftLoaded();
		List<String> out = new ArrayList<>();
		for (DraftStep step : currentDraft.getStepDefinitions())
		{
			if (step.getKind() == kind)
			{
				out.add(formatStepRequirementsDisplay(step));
			}
		}
		return Collections.unmodifiableList(out);
	}

	private boolean updateStepRawIdAt(StepKind kind, int filteredIndex, String rawIdText)
	{
		if (kind != StepKind.NPC && kind != StepKind.OBJECT)
		{
			return false;
		}
		DraftStep step = stepDefinitionAtKindIndexOrNull(kind, filteredIndex);
		if (step == null)
		{
			return false;
		}
		List<Integer> parsed = DraftRoutingIds.parseCsvIntsStrict(rawIdText == null ? "" : rawIdText);
		if (parsed == null || parsed.isEmpty())
		{
			return false;
		}
		List<Integer> ids = DraftRoutingIds.dedupeIntsPreserveOrder(parsed);
		step.setRawId(ids.get(0));
		step.getAlternateRawIds().clear();
		for (int i = 1; i < ids.size(); i++)
		{
			step.getAlternateRawIds().add(ids.get(i));
		}
		saveDraftToConfig();
		rebuildWorldMapRouteIfEnabled();
		return true;
	}

	private boolean updateStepWorldPointAt(StepKind kind, int filteredIndex, String text)
	{
		DraftStep step = stepDefinitionAtKindIndexOrNull(kind, filteredIndex);
		if (step == null)
		{
			return false;
		}
		String raw = text == null ? "" : text.trim();
		if (raw.isEmpty())
		{
			step.setWorldPoint(null);
			saveDraftToConfig();
			rebuildWorldMapRouteIfEnabled();
			return true;
		}
		WorldPoint wp = parseWorldPointField(text);
		if (wp == null)
		{
			return false;
		}
		step.setWorldPoint(wp);
		saveDraftToConfig();
		rebuildWorldMapRouteIfEnabled();
		return true;
	}

	private void rebuildWorldMapRouteIfEnabled()
	{
		if (worldMapRoutePreviewEnabled)
		{
			rebuildWorldMapRoutePoints();
		}
	}

	private boolean updateStepRequiredItemsAt(StepKind kind, int filteredIndex, String text)
	{
		DraftStep step = stepDefinitionAtKindIndexOrNull(kind, filteredIndex);
		if (step == null)
		{
			return false;
		}
		step.getAttachedRequirements().clear();
		for (Integer id : parseRequiredItemsField(text == null ? "" : text))
		{
			if (id != null)
			{
				step.getAttachedRequirements().add(DraftStepAttachedRequirement.item(id));
			}
		}
		saveDraftToConfig();
		return true;
	}

	private static String formatWorldPointForField(WorldPoint wp)
	{
		if (wp == null)
		{
			return "";
		}
		return wp.getX() + ", " + wp.getY() + ", " + wp.getPlane();
	}

	private static WorldPoint parseWorldPointField(String raw)
	{
		if (raw == null)
		{
			return null;
		}
		String t = raw.trim();
		if (t.isEmpty())
		{
			return null;
		}
		String[] parts = t.split(",");
		if (parts.length != 3)
		{
			return null;
		}
		try
		{
			int x = Integer.parseInt(parts[0].trim());
			int y = Integer.parseInt(parts[1].trim());
			int p = Integer.parseInt(parts[2].trim());
			return new WorldPoint(x, y, p);
		}
		catch (NumberFormatException e)
		{
			return null;
		}
	}

	private static List<Integer> parseRequiredItemsField(String raw)
	{
		List<Integer> out = new ArrayList<>();
		if (raw == null)
		{
			return out;
		}
		String t = raw.trim();
		if (t.isEmpty())
		{
			return out;
		}
		for (String part : t.split(","))
		{
			String p = part.trim();
			if (p.isEmpty())
			{
				continue;
			}
			try
			{
				out.add(Integer.parseInt(p));
			}
			catch (NumberFormatException ignored)
			{
			}
		}
		return out;
	}

	/**
	 * Quest-order combo: one entry per routable item id (primary and alternates each get a row when alternates exist).
	 */
	public List<RequirementRoutingChoice> getRequirementRoutingChoices()
	{
		ensureDraftLoaded();
		List<RequirementRoutingChoice> out = new ArrayList<>();
		List<DraftRequirement> reqs = currentDraft.getRequirements();
		List<String> labels = getRequirementSummaries();
		for (int i = 0; i < reqs.size(); i++)
		{
			DraftRequirement r = reqs.get(i);
			String lab = i < labels.size() ? labels.get(i) : String.valueOf(r.getRawId());
			List<Integer> itemIds = DraftRoutingIds.mergedStepOrRequirementIds(r.getRawId(), r.getAlternateRawIds());
			if (itemIds.size() <= 1)
			{
				out.add(new RequirementRoutingChoice(lab, r.getRawId()));
			}
			else
			{
				for (Integer id : itemIds)
				{
					out.add(new RequirementRoutingChoice(lab + " [item " + id + "]", id));
				}
			}
		}
		return Collections.unmodifiableList(out);
	}

	public String labelForOrderLinkedRequirementRawId(Integer rawId)
	{
		if (rawId == null)
		{
			return "";
		}
		for (RequirementRoutingChoice c : getRequirementRoutingChoices())
		{
			if (Objects.equals(c.getRawId(), rawId))
			{
				return c.getLabel();
			}
		}
		return "Item id " + rawId;
	}

	private int indexOfRequirementContainingItemId(int itemId)
	{
		List<DraftRequirement> reqs = currentDraft.getRequirements();
		for (int i = 0; i < reqs.size(); i++)
		{
			DraftRequirement r = reqs.get(i);
			if (r.getRawId() == itemId)
			{
				return i;
			}
			if (r.getAlternateRawIds().contains(itemId))
			{
				return i;
			}
		}
		return -1;
	}

	public List<CombinedStepRow> getCombinedStepRows()
	{
		ensureDraftLoaded();
		List<DraftOrderLine> order = currentDraft.getOrder();
		List<CombinedStepRow> rows = new ArrayList<>(order.size());
		for (int i = 0; i < order.size(); i++)
		{
			DraftOrderLine line = order.get(i);
			if (line.getOrderSlotId() == null || line.getOrderSlotId().isBlank())
			{
				line.setOrderSlotId(UUID.randomUUID().toString());
				saveDraftToConfig();
			}
			if (line.isSectionDivider())
			{
				rows.add(new CombinedStepRow(
					i,
					line.getOrderSlotId(),
					line.getOrderSlotId(),
					line.getSuggestedVarName(),
					formatSectionLineSummary(line, i + 1),
					true,
					line.getSectionCondition(),
					line.isSkipWhenConditionMet(),
					null,
					"",
					false,
					false));
				continue;
			}
			DraftStep def = findDefinitionByStepId(line.getRefStepId());
			if (def == null)
			{
				rows.add(new CombinedStepRow(
					i,
					line.getOrderSlotId(),
					line.getRefStepId() == null ? "" : line.getRefStepId(),
					"?",
					"(missing step definition)",
					false,
					"",
					false,
					line.getLinkedRequirementRawId(),
					"",
					isNonDefaultOrderStepRequirementTree(line, def),
					line.getStepRequirement() != null));
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
				line.getOrderSlotId(),
				def.getStepId(),
				def.getSuggestedVarName(),
				formatStepSummary(def, i + 1),
				false,
				"",
				false,
				line.getLinkedRequirementRawId(),
				instr == null ? "" : instr,
				isNonDefaultOrderStepRequirementTree(line, def),
				line.getStepRequirement() != null));
		}
		reconcileOrderSlotRoutingAttachments();
		return Collections.unmodifiableList(rows);
	}

	private static boolean isNonDefaultOrderStepRequirementTree(DraftOrderLine line, DraftStep def)
	{
		if (line.getStepRequirement() == null)
		{
			return false;
		}
		DraftOrderStepRequirement syn = OrderStepRequirementSupport.synthesizeLegacyTreeForOrderRow(line, def);
		return syn == null || !OrderStepRequirementSupport.orderStepTreesEqual(line.getStepRequirement(), syn);
	}

	public void addSectionDivider()
	{
		addSectionDivider(-1);
	}

	/**
	 * @param insertAt index in {@link DraftHelper#getOrder()} to insert at; {@code -1} or out of range appends at the end.
	 *                 Use {@code selectedIndex + 1} to place a new row just below the selected order row.
	 */
	public void addSectionDivider(int insertAt)
	{
		ensureDraftLoaded();
		DraftOrderLine line = new DraftOrderLine();
		line.setOrderSlotId(UUID.randomUUID().toString());
		line.setSectionDivider(true);
		line.setSuggestedVarName(DEFAULT_SECTION_NAME);
		line.setSectionCondition("");
		line.setSkipWhenConditionMet(false);
		List<DraftOrderLine> order = currentDraft.getOrder();
		int idx = insertAt < 0 ? order.size() : Math.min(Math.max(0, insertAt), order.size());
		order.add(idx, line);
		saveDraftToConfig();
	}

	public void addOrderRef(String stepId)
	{
		addOrderRef(stepId, -1);
	}

	/**
	 * @param insertAt index in {@link DraftHelper#getOrder()} to insert at; {@code -1} or out of range appends at the end.
	 *                 Use {@code selectedIndex + 1} to place a new row just below the selected order row.
	 */
	public void addOrderRef(String stepId, int insertAt)
	{
		ensureDraftLoaded();
		if (stepId == null || stepId.isBlank() || findDefinitionByStepId(stepId) == null)
		{
			return;
		}
		DraftOrderLine line = new DraftOrderLine();
		line.setOrderSlotId(UUID.randomUUID().toString());
		line.setSectionDivider(false);
		line.setRefStepId(stepId);
		line.setLinkedRequirementRawId(null);
		List<DraftOrderLine> order = currentDraft.getOrder();
		int idx = insertAt < 0 ? order.size() : Math.min(Math.max(0, insertAt), order.size());
		order.add(idx, line);
		saveDraftToConfig();
		if (worldMapRoutePreviewEnabled)
		{
			rebuildWorldMapRoutePoints();
		}
	}

	/**
	 * Appends a new empty generic step, a quest-order row, and a placeholder routing VARBIT on that order row
	 * (0 / 0) so the Varbit reqs tab can edit it immediately. Ordinary {@link #addOrderRef(String)} does not create that.
	 */
	public void addEmptyVarbitSlotFromUi()
	{
		ensureDraftLoaded();
		addEmptyStepFromUi(ConstructStepKind.TEXT);
		List<DraftStep> defs = currentDraft.getStepDefinitions();
		DraftStep placeholder = defs.get(defs.size() - 1);
		placeholder.setSuggestedVarName(HelperScaffoldGenerator.toVarName("varbit", "step"));
		addOrderRef(placeholder.getStepId());
		List<DraftOrderLine> order = currentDraft.getOrder();
		if (order.isEmpty())
		{
			return;
		}
		DraftOrderLine added = order.get(order.size() - 1);
		if (added.isSectionDivider())
		{
			return;
		}
		ensureOrderSlotId(added);
		if (DraftStepAttachedRequirement.findOrderRoutingVarbit(added) == null)
		{
			DraftStepAttachedRequirement.setOrderLineRoutingVarbit(added, DraftStepAttachedRequirement.varbit(0, 0, "EQUAL", ""));
		}
		saveDraftToConfig();
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
		ensureOrderSlotId(line);
		line.setLinkedRequirementRawId(linkedRequirementRawId);
		line.setStepRequirement(null);
		if (linkedRequirementRawId != null && linkedRequirementRawId > 0)
		{
			removeRoutingVarbitForOrderSlot(line.getOrderSlotId());
		}
		saveDraftToConfig();
		return true;
	}

	/**
	 * @return {@code null} on success, otherwise a short error message for the UI.
	 */
	public String applyOrderStepRequirementJson(int orderIndex, String json)
	{
		ensureDraftLoaded();
		if (orderIndex < 0 || orderIndex >= currentDraft.getOrder().size())
		{
			return "Invalid order row.";
		}
		DraftOrderLine line = currentDraft.getOrder().get(orderIndex);
		if (line.isSectionDivider())
		{
			return "Not an order step row.";
		}
		String trimmed = json == null ? "" : json.trim();
		if (trimmed.isEmpty() || "{}".equals(trimmed))
		{
			line.setStepRequirement(null);
			saveDraftToConfig();
			return null;
		}
		try
		{
			DraftOrderStepRequirement parsed = gson.fromJson(trimmed, DraftOrderStepRequirement.class);
			String err = OrderStepRequirementSupport.prepareOrderStepTreeForPersistence(line, parsed);
			if (err != null)
			{
				return err;
			}
			line.setStepRequirement(parsed);
			OrderStepRequirementSupport.mirrorLinkedRawIdFromSimpleTree(line);
			saveDraftToConfig();
			return null;
		}
		catch (Exception ex)
		{
			log.warn("Invalid order step requirement JSON", ex);
			return "Invalid JSON: " + ex.getMessage();
		}
	}

	public String getOrderStepRequirementJsonForEditor(int orderIndex)
	{
		ensureDraftLoaded();
		if (orderIndex < 0 || orderIndex >= currentDraft.getOrder().size())
		{
			return "";
		}
		DraftOrderLine line = currentDraft.getOrder().get(orderIndex);
		if (line.isSectionDivider())
		{
			return "";
		}
		DraftOrderStepRequirement t = line.getStepRequirement();
		if (t == null)
		{
			return "";
		}
		return prettyDraftGson.toJson(t);
	}

	/** Deep copy for the tree editor; {@code null} when the order row has no step requirement yet. */
	public DraftOrderStepRequirement cloneOrderStepRequirementForEditor(int orderIndex)
	{
		ensureDraftLoaded();
		if (orderIndex < 0 || orderIndex >= currentDraft.getOrder().size())
		{
			return null;
		}
		DraftOrderLine line = currentDraft.getOrder().get(orderIndex);
		if (line.isSectionDivider())
		{
			return null;
		}
		DraftOrderStepRequirement t = line.getStepRequirement();
		if (t == null)
		{
			return null;
		}
		return gson.fromJson(gson.toJson(t), DraftOrderStepRequirement.class);
	}

	/**
	 * Persists the branch tree from the GUI editor. Pass {@code null} to clear.
	 * @return {@code null} on success, otherwise a short error message for the UI.
	 */
	public String applyOrderStepRequirementTree(int orderIndex, DraftOrderStepRequirement tree)
	{
		ensureDraftLoaded();
		if (orderIndex < 0 || orderIndex >= currentDraft.getOrder().size())
		{
			return "Invalid order row.";
		}
		DraftOrderLine line = currentDraft.getOrder().get(orderIndex);
		if (line.isSectionDivider())
		{
			return "Not an order step row.";
		}
		if (tree == null)
		{
			line.setStepRequirement(null);
			saveDraftToConfig();
			return null;
		}
		String err = OrderStepRequirementSupport.prepareOrderStepTreeForPersistence(line, tree);
		if (err != null)
		{
			return err;
		}
		line.setStepRequirement(tree);
		OrderStepRequirementSupport.mirrorLinkedRawIdFromSimpleTree(line);
		saveDraftToConfig();
		return null;
	}

	private void ensureOrderSlotId(DraftOrderLine line)
	{
		if (line.getOrderSlotId() == null || line.getOrderSlotId().isBlank())
		{
			line.setOrderSlotId(UUID.randomUUID().toString());
		}
	}

	private void removeRoutingVarbitForOrderSlot(String orderSlotId)
	{
		if (orderSlotId == null || orderSlotId.isBlank())
		{
			return;
		}
		DraftOrderLine line = findOrderLineByOrderSlotId(orderSlotId);
		if (line != null)
		{
			DraftStepAttachedRequirement.clearVarbitAttachmentsOnOrderLine(line);
		}
		for (DraftStep step : currentDraft.getStepDefinitions())
		{
			DraftStepAttachedRequirement.removeRoutingVarbitForSlot(step, orderSlotId);
		}
	}

	private void reconcileOrderSlotRoutingAttachments()
	{
		LinkedHashSet<String> wantedSlots = new LinkedHashSet<>();
		for (DraftOrderLine line : currentDraft.getOrder())
		{
			if (line.isSectionDivider())
			{
				continue;
			}
			ensureOrderSlotId(line);
			if (orderLineUsesVarbitRouting(line))
			{
				wantedSlots.add(line.getOrderSlotId());
			}
		}
		for (DraftOrderLine line : currentDraft.getOrder())
		{
			if (line.isSectionDivider())
			{
				continue;
			}
			if (!orderLineUsesVarbitRouting(line))
			{
				DraftStepAttachedRequirement.clearVarbitAttachmentsOnOrderLine(line);
			}
		}
		for (DraftStep step : currentDraft.getStepDefinitions())
		{
			if (step.getAttachedRequirements() == null)
			{
				continue;
			}
			step.getAttachedRequirements().removeIf(a ->
				StepAttachmentKind.VARBIT.name().equalsIgnoreCase(a.getKind())
					&& a.getOrderSlotId() != null && !a.getOrderSlotId().isBlank()
					&& !wantedSlots.contains(a.getOrderSlotId()));
		}
	}

	/** True when the Varbit tab should keep routing data: conditions reference the slot or the row already has a tab attachment. */
	private static boolean orderLineUsesVarbitRouting(DraftOrderLine line)
	{
		if (line == null || line.isSectionDivider())
		{
			return false;
		}
		return OrderStepRequirementSupport.treeContainsOrderVarbitLeaf(line.getStepRequirement())
			|| DraftStepAttachedRequirement.findOrderRoutingVarbit(line) != null;
	}

	private DraftOrderLine findOrderLineByOrderSlotId(String orderSlotId)
	{
		for (DraftOrderLine line : currentDraft.getOrder())
		{
			if (orderSlotId.equals(line.getOrderSlotId()))
			{
				return line;
			}
		}
		return null;
	}

	public List<VarbitSlotRow> getVarbitSlotsInQuestOrderForEditor()
	{
		ensureDraftLoaded();
		reconcileOrderSlotRoutingAttachments();
		List<VarbitSlotRow> out = new ArrayList<>();
		List<DraftOrderLine> order = currentDraft.getOrder();
		for (int ord = 0; ord < order.size(); ord++)
		{
			DraftOrderLine line = order.get(ord);
			if (line.isSectionDivider())
			{
				continue;
			}
			if (!orderLineUsesVarbitRouting(line))
			{
				continue;
			}
			ensureOrderSlotId(line);
			DraftStep def = findDefinitionByStepId(line.getRefStepId());
			DraftStepAttachedRequirement cfg = DraftStepAttachedRequirement.findOrderRoutingVarbit(line);
			String varName = def == null
				? "?"
				: (def.getSuggestedVarName() == null || def.getSuggestedVarName().isBlank() ? "?" : def.getSuggestedVarName());
			VarbitSpec spec = VarbitSpec.fromStepAttachment(cfg);
			out.add(new VarbitSlotRow(
				line.getOrderSlotId(),
				varName,
				spec.getVarbitId(),
				spec.getRequiredValue(),
				spec.getOperation().name(),
				spec.getDisplayText() == null ? "" : spec.getDisplayText()));
		}
		return Collections.unmodifiableList(out);
	}

	/**
	 * Updates varbit routing stored on a quest-order row (Varbit reqs tab / {@code ORDER_VARBIT}).
	 *
	 * @param requireResolvedStepDefinition when true, fails if the row has no step definition (Varbit table editing).
	 */
	private boolean applyVarbitRoutingToOrderLine(
		DraftOrderLine line,
		int varbitId,
		int requiredValue,
		String operationName,
		String displayText,
		boolean requireResolvedStepDefinition)
	{
		if (line == null || line.isSectionDivider())
		{
			return false;
		}
		if (requireResolvedStepDefinition && findDefinitionByStepId(line.getRefStepId()) == null)
		{
			return false;
		}
		ensureOrderSlotId(line);
		DraftStepAttachedRequirement cfg = DraftStepAttachedRequirement.findOrderRoutingVarbit(line);
		if (cfg == null)
		{
			cfg = DraftStepAttachedRequirement.varbit(0, 1, "EQUAL", null);
			DraftStepAttachedRequirement.setOrderLineRoutingVarbit(line, cfg);
		}
		Operation op;
		try
		{
			op = Operation.valueOf(operationName == null || operationName.isBlank() ? "EQUAL" : operationName.trim());
		}
		catch (IllegalArgumentException ex)
		{
			return false;
		}
		cfg.setVarbitId(varbitId);
		cfg.setVarbitRequiredValue(requiredValue);
		cfg.setVarbitOperation(op.name());
		cfg.setVarbitDisplayText(displayText == null ? "" : displayText);
		return true;
	}

	public boolean updateVarbitSlotForOrderSlot(String orderSlotId, int varbitId, int requiredValue, String operationName, String displayText)
	{
		ensureDraftLoaded();
		if (orderSlotId == null || orderSlotId.isBlank())
		{
			return false;
		}
		DraftOrderLine line = findOrderLineByOrderSlotId(orderSlotId);
		if (line == null || line.isSectionDivider())
		{
			return false;
		}
		if (!applyVarbitRoutingToOrderLine(line, varbitId, requiredValue, operationName, displayText, true))
		{
			return false;
		}
		saveDraftToConfig();
		return true;
	}

	/**
	 * Applies varbit routing to the quest-order row at {@code orderIndex}. Used from the conditions editor so routing
	 * can be updated without requiring a resolved step definition. When {@code persist} is false, only the in-memory
	 * draft changes (caller saves afterward, e.g. with {@link #applyOrderStepRequirementTree}).
	 */
	public boolean applyVarbitRoutingToOrderLineByIndex(
		int orderIndex,
		int varbitId,
		int requiredValue,
		String operationName,
		String displayText,
		boolean persist)
	{
		ensureDraftLoaded();
		if (orderIndex < 0 || orderIndex >= currentDraft.getOrder().size())
		{
			return false;
		}
		DraftOrderLine line = currentDraft.getOrder().get(orderIndex);
		if (!applyVarbitRoutingToOrderLine(line, varbitId, requiredValue, operationName, displayText, false))
		{
			return false;
		}
		if (persist)
		{
			saveDraftToConfig();
		}
		return true;
	}

	public static final class VarbitSlotRow
	{
		private final String orderSlotId;
		private final String varName;
		private final int varbitId;
		private final int requiredValue;
		private final String operation;
		private final String displayText;

		public VarbitSlotRow(String orderSlotId, String varName, int varbitId, int requiredValue, String operation, String displayText)
		{
			this.orderSlotId = orderSlotId;
			this.varName = varName == null ? "" : varName;
			this.varbitId = varbitId;
			this.requiredValue = requiredValue;
			this.operation = operation;
			this.displayText = displayText;
		}

		public String getOrderSlotId()
		{
			return orderSlotId;
		}

		public String getVarName()
		{
			return varName;
		}

		public int getVarbitId()
		{
			return varbitId;
		}

		public int getRequiredValue()
		{
			return requiredValue;
		}

		public String getOperation()
		{
			return operation;
		}

		public String getDisplayText()
		{
			return displayText;
		}
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

	/**
	 * Labels and {@link StepAttachmentEdit} payloads for the single "Add…" control in the step attachments dialog:
	 * captured item requirements plus varbit routing rows from quest order.
	 */
	public List<StepAttachmentPickOption> getStepAttachmentPickOptions()
	{
		ensureDraftLoaded();
		reconcileOrderSlotRoutingAttachments();
		List<StepAttachmentPickOption> out = new ArrayList<>();
		List<DraftRequirement> reqs = currentDraft.getRequirements();
		List<String> labels = getRequirementSummaries();
		for (int i = 0; i < reqs.size(); i++)
		{
			DraftRequirement r = reqs.get(i);
			String lab = i < labels.size() ? labels.get(i) : String.valueOf(r.getRawId());
			String shortLab = lab.replaceFirst("^\\d+\\.\\s*", "").trim();
			List<Integer> itemIds = DraftRoutingIds.mergedStepOrRequirementIds(r.getRawId(), r.getAlternateRawIds());
			if (itemIds.size() <= 1)
			{
				out.add(new StepAttachmentPickOption(shortLab, StepAttachmentEdit.item(r.getRawId())));
			}
			else
			{
				for (Integer id : itemIds)
				{
					out.add(new StepAttachmentPickOption(shortLab + " [item " + id + "]", StepAttachmentEdit.item(id)));
				}
			}
		}
		for (VarbitSlotRow row : getVarbitSlotsInQuestOrderForEditor())
		{
			String disp = row.getDisplayText();
			String base = row.getVarName() + " — " + row.getVarbitId() + " " + row.getOperation() + " " + row.getRequiredValue();
			String label = disp != null && !disp.isBlank() ? disp + " — " + base : base;
			out.add(new StepAttachmentPickOption(label, StepAttachmentEdit.varbitForOrderSlot(
				row.getOrderSlotId(),
				row.getVarbitId(),
				row.getRequiredValue(),
				row.getOperation(),
				disp)));
		}
		return Collections.unmodifiableList(out);
	}

	public boolean updateStepVarNameAt(ConstructStepKind kind, int filteredIndex, String varName)
	{
		return updateStepVarNameByKindAt(kind.stepKind(), filteredIndex, varName);
	}

	private boolean updateStepVarNameByKindAt(StepKind kind, int filteredIndex, String varName)
	{
		DraftStep step = stepDefinitionAtKindIndexOrNull(kind, filteredIndex);
		if (step == null)
		{
			return false;
		}
		step.setSuggestedVarName(varName == null ? "" : varName.trim());
		saveDraftToConfig();
		rebuildWorldMapRouteIfEnabled();
		return true;
	}

	/** Updates {@link DraftStep#getSuggestedVarName()} for the step referenced by this quest-order line (varbit / order UI). */
	public boolean updateStepVarNameForOrderSlotId(String orderSlotId, String varName)
	{
		ensureDraftLoaded();
		if (orderSlotId == null || orderSlotId.isBlank())
		{
			return false;
		}
		DraftOrderLine line = findOrderLineByOrderSlotId(orderSlotId);
		if (line == null || line.isSectionDivider())
		{
			return false;
		}
		DraftStep def = findDefinitionByStepId(line.getRefStepId());
		if (def == null)
		{
			return false;
		}
		def.setSuggestedVarName(varName == null ? "" : varName.trim());
		saveDraftToConfig();
		rebuildWorldMapRouteIfEnabled();
		return true;
	}

	public List<String> getRequirementDisplayNames()
	{
		ensureDraftLoaded();
		List<String> out = new ArrayList<>();
		for (DraftRequirement r : currentDraft.getRequirements())
		{
			String d = normalizeText(r.getDisplayName());
			out.add(d.isBlank() ? "" : d);
		}
		return Collections.unmodifiableList(out);
	}

	public List<String> getRequirementRawIdStrings()
	{
		ensureDraftLoaded();
		List<String> out = new ArrayList<>();
		for (DraftRequirement r : currentDraft.getRequirements())
		{
			out.add(DraftRoutingIds.formatCsvIds(DraftRoutingIds.mergedStepOrRequirementIds(r.getRawId(), r.getAlternateRawIds())));
		}
		return Collections.unmodifiableList(out);
	}

	public boolean updateRequirementDisplayNameAt(int index, String displayName)
	{
		ensureDraftLoaded();
		if (index < 0 || index >= currentDraft.getRequirements().size())
		{
			return false;
		}
		currentDraft.getRequirements().get(index).setDisplayName(displayName == null ? "" : displayName.trim());
		saveDraftToConfig();
		return true;
	}

	public boolean updateRequirementRawIdAt(int index, String rawIdText)
	{
		ensureDraftLoaded();
		if (index < 0 || index >= currentDraft.getRequirements().size())
		{
			return false;
		}
		List<Integer> parsed = DraftRoutingIds.parseCsvIntsStrict(rawIdText == null ? "" : rawIdText);
		if (parsed == null || parsed.isEmpty())
		{
			return false;
		}
		List<Integer> normalized = new ArrayList<>(parsed.size());
		for (Integer v : parsed)
		{
			normalized.add(normalizeItemId(v));
		}
		normalized = DraftRoutingIds.dedupeIntsPreserveOrder(normalized);
		DraftRequirement r = currentDraft.getRequirements().get(index);
		r.setRawId(normalized.get(0));
		r.getAlternateRawIds().clear();
		for (int i = 1; i < normalized.size(); i++)
		{
			r.getAlternateRawIds().add(normalized.get(i));
		}
		saveDraftToConfig();
		return true;
	}

	public void addEmptyItemRequirementFromUi()
	{
		ensureDraftLoaded();
		DraftRequirement r = new DraftRequirement();
		r.setRawId(0);
		r.setDisplayName("Item");
		currentDraft.getRequirements().add(r);
		saveDraftToConfig();
	}

	/** Removes the quest-order row with this {@link DraftOrderLine#getOrderSlotId()} (and its routing varbit attachment if any). */
	public boolean removeOrderLineByOrderSlotId(String orderSlotId)
	{
		ensureDraftLoaded();
		if (orderSlotId == null || orderSlotId.isBlank())
		{
			return false;
		}
		List<DraftOrderLine> order = currentDraft.getOrder();
		for (int i = 0; i < order.size(); i++)
		{
			DraftOrderLine line = order.get(i);
			if (line.isSectionDivider())
			{
				continue;
			}
			if (orderSlotId.equals(line.getOrderSlotId()))
			{
				return removeStepAt(i);
			}
		}
		return false;
	}

	/**
	 * Clears varbit routing for this quest-order slot, removes {@code ORDER_VARBIT} nodes from that row's conditions
	 * tree, and drops step-definition routing rows for the slot. Does not remove the order row or any step definition.
	 */
	public boolean clearVarbitRoutingForOrderSlotId(String orderSlotId)
	{
		ensureDraftLoaded();
		if (orderSlotId == null || orderSlotId.isBlank())
		{
			return false;
		}
		DraftOrderLine line = findOrderLineByOrderSlotId(orderSlotId);
		if (line == null || line.isSectionDivider())
		{
			return false;
		}
		removeRoutingVarbitForOrderSlot(orderSlotId);
		DraftOrderStepRequirement tree = line.getStepRequirement();
		if (tree != null)
		{
			DraftOrderStepRequirement pruned = OrderStepRequirementSupport.stripOrderVarbitLeaves(tree);
			line.setStepRequirement(pruned);
			OrderStepRequirementSupport.mirrorLinkedRawIdFromSimpleTree(line);
		}
		saveDraftToConfig();
		if (worldMapRoutePreviewEnabled)
		{
			rebuildWorldMapRoutePoints();
		}
		return true;
	}

	/** Short label for {@code ORDER_VARBIT} leaves in the order-conditions editor (from this row's Varbit routing). */
	public String formatOrderVarbitLeafSummaryForEditor(int orderIndex)
	{
		ensureDraftLoaded();
		if (orderIndex < 0 || orderIndex >= currentDraft.getOrder().size())
		{
			return "Varbit";
		}
		DraftOrderLine line = currentDraft.getOrder().get(orderIndex);
		if (line.isSectionDivider())
		{
			return "Varbit";
		}
		DraftStepAttachedRequirement cfg = DraftStepAttachedRequirement.findOrderRoutingVarbit(line);
		VarbitSpec spec = VarbitSpec.fromStepAttachment(cfg);
		String disp = spec.getDisplayText();
		if (disp != null && !disp.isBlank())
		{
			return disp + " — vb " + spec.getVarbitId() + " " + spec.getOperation() + " " + spec.getRequiredValue();
		}
		return "Varbit " + spec.getVarbitId() + " " + spec.getOperation() + " " + spec.getRequiredValue();
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
				displayName = DraftRoutingIds.formatCsvIds(DraftRoutingIds.mergedStepOrRequirementIds(requirement.getRawId(), requirement.getAlternateRawIds()));
				if (displayName.isBlank())
				{
					displayName = String.valueOf(requirement.getRawId());
				}
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
		DraftOrderLine removed = currentDraft.getOrder().get(index);
		removeRoutingVarbitForOrderSlot(removed.getOrderSlotId());
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

	public boolean removeStepAt(ConstructStepKind kind, int index)
	{
		return removeStepByKindAt(kind.stepKind(), index);
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

	private void removeOrderRefsToStepId(String stepId)
	{
		if (stepId == null || stepId.isBlank())
		{
			return;
		}
		currentDraft.getOrder().removeIf(line ->
		{
			if (line.isSectionDivider() || !stepId.equals(line.getRefStepId()))
			{
				return false;
			}
			removeRoutingVarbitForOrderSlot(line.getOrderSlotId());
			return true;
		});
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
		File draftFile = MakerDraftFileStore.draftFileOrNull();
		String json = null;
		if (draftFile != null && draftFile.isFile())
		{
			try
			{
				json = Files.readString(draftFile.toPath(), StandardCharsets.UTF_8);
			}
			catch (IOException e)
			{
				log.warn("Could not read Quest Helper Maker draft from {}", draftFile.getAbsolutePath(), e);
			}
		}
		if (json == null || json.isBlank())
		{
			return;
		}
		MakerDraftJsonLoader.LoadOutcome outcome = MakerDraftJsonLoader.loadDraftFromJson(json, gson);
		if (!outcome.isSuccess())
		{
			log.warn("Quest Helper Maker draft file is not valid extended route JSON: {}", outcome.getErrorMessage());
			currentDraft = new DraftHelper();
			return;
		}
		currentDraft = outcome.getDraft();
		reconcileOrderSlotRoutingAttachments();
	}

	/**
	 * Pretty-printed extended Tasks Tracker route JSON (sections/items for the plugin plus {@code questHelperMaker}
	 * with the full maker snapshot). Same shape as the auto-saved draft file.
	 */
	public String exportDraftJson()
	{
		ensureDraftLoaded();
		reconcileOrderSlotRoutingAttachments();
		return prettyDraftGson.toJson(TasksTrackerRouteExporter.export(currentDraft));
	}

	/**
	 * Replace the current draft from JSON (e.g. pasted or read from a file). Persists to the maker draft file on success.
	 */
	public ImportDraftResult importDraftFromJson(String json)
	{
		ensureDraftLoaded();
		MakerDraftJsonLoader.LoadOutcome outcome = MakerDraftJsonLoader.loadDraftFromJson(json, gson);
		if (!outcome.isSuccess())
		{
			return ImportDraftResult.failure(outcome.getErrorMessage());
		}
		currentDraft = outcome.getDraft();
		reconcileOrderSlotRoutingAttachments();
		saveDraftToConfig();
		if (worldMapRoutePreviewEnabled)
		{
			rebuildWorldMapRoutePoints();
		}
		return ImportDraftResult.ok();
	}

	/**
	 * Same document as {@link #exportDraftJson()}: extended Tasks Tracker route JSON with {@code questHelperMaker}.
	 */
	public String exportTasksTrackerRouteJson()
	{
		return exportDraftJson();
	}

	/**
	 * Replace the maker draft from extended Tasks Tracker route JSON: optional {@code questHelperMaker}
	 * for a full snapshot, otherwise {@code taskId} / {@code note} / {@code location} / {@code interact} only.
	 */
	public ImportDraftResult importTasksTrackerRouteFromJson(String routeJson)
	{
		return importDraftFromJson(routeJson);
	}

	private void saveDraftToConfig()
	{
		File file = MakerDraftFileStore.draftFileOrNull();
		if (file == null)
		{
			log.warn("Cannot save Quest Helper Maker draft: RuneLite data directory is not set.");
			return;
		}
		try
		{
			reconcileOrderSlotRoutingAttachments();
			MakerDraftFileStore.writeUtf8(file, gson.toJson(TasksTrackerRouteExporter.export(currentDraft)));
		}
		catch (IOException e)
		{
			log.warn("Could not save Quest Helper Maker draft to {}", file.getAbsolutePath(), e);
		}
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
		return (cleanOption + " " + cleanTarget + ".").trim();
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
		String displayName;
		if (step.getKind() == StepKind.TEXT)
		{
			String ins = normalizeText(step.getInstructionText());
			if (!ins.isBlank())
			{
				displayName = ins.length() > 48 ? ins.substring(0, 45) + "…" : ins;
			}
			else
			{
				displayName = "Generic step";
			}
		}
		else
		{
			displayName = normalizeText(step.getTargetText());
			if (displayName.isBlank())
			{
				displayName = DraftRoutingIds.formatCsvIds(DraftRoutingIds.mergedStepOrRequirementIds(step.getRawId(), step.getAlternateRawIds()));
				if (displayName.isBlank())
				{
					displayName = String.valueOf(step.getRawId());
				}
			}
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
		MakerPreviewRuntime.clearConstructWorldMapPoints(worldMapPointManager);
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
		MakerPreviewRuntime.clearConstructWorldMapPoints(worldMapPointManager);
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
		String idLabel = DraftRoutingIds.formatCsvIds(DraftRoutingIds.mergedStepOrRequirementIds(step.getRawId(), step.getAlternateRawIds()));
		return idLabel.isBlank() ? "step" : idLabel;
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
			String display = draft.getQuestName();
			if (display == null || display.isBlank())
			{
				display = draft.getClassName();
			}
			if (display == null || display.isBlank())
			{
				display = "Shared helper";
			}
			setPlayerFacingQuestName(display.trim());
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
				List<Integer> ids = DraftRoutingIds.mergedStepOrRequirementIds(requirement.getRawId(), requirement.getAlternateRawIds());
				if (ids.size() <= 1)
				{
					previewRequirements.add(new ItemRequirement(display, requirement.getRawId()));
				}
				else
				{
					previewRequirements.add(new ItemRequirement(display, ids));
				}
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
				for (int id : req.getAllIds())
				{
					requirementById.put(id, req);
				}
			}

			List<ConditionalStep> sectionTasks = new ArrayList<>();
			List<List<Requirement>> sectionCompletionRequirements = new ArrayList<>();
			List<PreviewStepEntry> currentSectionEntries = new ArrayList<>();
			String currentPanelName = "Captured Steps";

			for (DraftOrderLine orderLine : draft.getOrder())
			{
				if (orderLine.isSectionDivider())
				{
					ConditionalStep sectionTask = createSectionTask(currentSectionEntries);
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

				PreviewStepEntry stepEntry = toPreviewStep(def, requirementById, orderLine);
				currentSectionEntries.add(stepEntry);
			}

			if (!currentSectionEntries.isEmpty())
			{
				ConditionalStep sectionTask = createSectionTask(currentSectionEntries);
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
			ConditionalStep allSections = new ConditionalStep(this, sectionTasks.get(lastSectionIndex));
			allSections.setShouldPassthroughText(true);

			List<Requirement> priorSectionRequirements = new ArrayList<>();
			for (int i = 0; i < lastSectionIndex; i++)
			{
				allSections.addStep(nor(priorSectionRequirements.toArray(new Requirement[0])), sectionTasks.get(i));
				priorSectionRequirements.addAll(sectionCompletionRequirements.get(i));
			}

			return allSections;
		}

		private ConditionalStep createSectionTask(List<PreviewStepEntry> sectionEntries)
		{
			if (sectionEntries.isEmpty())
			{
				return null;
			}

			QuestStep fallbackStep = sectionEntries.get(sectionEntries.size() - 1).step;
			ConditionalStep sectionTask = new ConditionalStep(this, fallbackStep);
			sectionTask.setShouldPassthroughText(true);
			for (PreviewStepEntry entry : sectionEntries)
			{
				sectionTask.addStep(entry.branchRequirement, entry.step);
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

		private List<Requirement> extraAttachedRequirementsForStep(DraftStep draftStep, Map<Integer, ItemRequirement> requirementById)
		{
			List<Requirement> reqs = new ArrayList<>();
			if (draftStep.getAttachedRequirements() == null)
			{
				return reqs;
			}
			for (DraftStepAttachedRequirement a : draftStep.getAttachedRequirements())
			{
				String k = a.getKind() == null ? StepAttachmentKind.ITEM.name() : a.getKind();
				if (StepAttachmentKind.VARBIT.name().equalsIgnoreCase(k))
				{
					if (a.getOrderSlotId() != null && !a.getOrderSlotId().isBlank())
					{
						continue;
					}
					reqs.add(VarbitSpec.fromStepAttachment(a).toVarbitRequirement());
					continue;
				}
				Integer id = a.getItemRawId();
				if (id == null)
				{
					continue;
				}
				ItemRequirement ir = requirementById.get(id);
				ItemRequirement base = ir != null ? ir : new ItemRequirement("Item " + id, id);
				reqs.add(a.isAttachmentHighlighted() ? base.highlighted() : base);
			}
			return reqs;
		}

		private PreviewStepEntry toPreviewStep(DraftStep draftStep, Map<Integer, ItemRequirement> requirementById, DraftOrderLine orderLine)
		{
			String instruction = (draftStep.getInstructionText() == null || draftStep.getInstructionText().isBlank())
				? "Complete step."
				: draftStep.getInstructionText();
			Requirement originalCompletionRequirement = previewCompletionRequirement(draftStep, requirementById, orderLine);
			List<Requirement> extras = extraAttachedRequirementsForStep(draftStep, requirementById);
			Requirement[] extrasArr = extras.toArray(new Requirement[0]);
			Integer itemHighlightRawId = itemHighlightRawIdForPreview(draftStep, orderLine);
			ConstructStepKindHandlers.ConstructStepKindHandler handler = ConstructStepKindHandlers.forStepKind(draftStep.getKind());
			QuestStep step = handler != null
				? handler.buildPreviewQuestStep(new ConstructStepKindHandlers.ConstructPreviewStepParams(
				this, draftStep, instruction, extrasArr, requirementById, itemHighlightRawId))
				: new DetailedQuestStep(this, instruction);

			ManualRequirement manualOverride = defaultIncompleteRequirement();
			manualStepRequirements.add(manualOverride);
			Requirement completion;
			Requirement branchRequirement;
			DraftOrderStepRequirement tree = orderLine.getStepRequirement();
			if (tree != null)
			{
				Requirement selector = OrderStepRequirementSupport.buildRuntimeSelector(tree, orderLine, draftStep, requirementById);
				if (selector == null)
				{
					completion = manualOverride;
					branchRequirement = not(manualOverride);
				}
				else
				{
					completion = or(not(selector), manualOverride);
					branchRequirement = and(selector, not(manualOverride));
				}
			}
			else
			{
				completion = originalCompletionRequirement == null
					? manualOverride
					: or(originalCompletionRequirement, manualOverride);
				branchRequirement = not(completion);
			}
			return new PreviewStepEntry(step, completion, branchRequirement);
		}

		/**
		 * @return {@code null} when varbit/item routing is not configured for this order row — preview then uses only
		 * the per-step {@link ManualRequirement} (arrow controls).
		 */
		private Requirement previewCompletionRequirement(DraftStep def, Map<Integer, ItemRequirement> requirementById, DraftOrderLine orderLine)
		{
			Integer rid = orderLine.getLinkedRequirementRawId();
			if (rid != null && rid > 0)
			{
				ItemRequirement ir = requirementById.get(rid);
				if (ir != null)
				{
					return ir;
				}
				return defaultIncompleteRequirement();
			}
			return null;
		}

		private Integer itemHighlightRawIdForPreview(DraftStep def, DraftOrderLine orderLine)
		{
			Integer fromTree = OrderStepRequirementSupport.findFirstItemRawIdInTree(orderLine.getStepRequirement());
			if (fromTree != null)
			{
				return fromTree;
			}
			Integer orderLinkedOverride = orderLine.getLinkedRequirementRawId();
			if (orderLinkedOverride != null && orderLinkedOverride > 0)
			{
				return orderLinkedOverride;
			}
			if (def.getKind() == StepKind.ITEM)
			{
				return def.getRawId();
			}
			return null;
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
			return getDisplayedQuestName() + " (Preview)";
		}
	}

	private static class PreviewStepEntry
	{
		private final QuestStep step;
		private final Requirement completionRequirement;
		private final Requirement branchRequirement;

		private PreviewStepEntry(QuestStep step, Requirement completionRequirement, Requirement branchRequirement)
		{
			this.step = step;
			this.completionRequirement = completionRequirement;
			this.branchRequirement = branchRequirement;
		}
	}

	/** One row in the unified step-attachment picker (items + varbit routing slots). */
	public static final class StepAttachmentPickOption
	{
		private final String label;
		private final StepAttachmentEdit edit;

		public StepAttachmentPickOption(String label, StepAttachmentEdit edit)
		{
			this.label = label;
			this.edit = edit;
		}

		public String getLabel()
		{
			return label;
		}

		public StepAttachmentEdit getEdit()
		{
			return edit;
		}
	}

	/** UI / API DTO for extra requirements on a step (items, varbits, extensible kinds). */
	public static final class StepAttachmentEdit
	{
		private final String kind;
		private final Integer itemRawId;
		private final Integer varbitId;
		private final Integer varbitRequiredValue;
		private final String varbitOperation;
		private final String varbitDisplayText;
		private boolean itemHighlighted;
		private final String orderSlotId;

		private StepAttachmentEdit(
			String kind,
			Integer itemRawId,
			Integer varbitId,
			Integer varbitRequiredValue,
			String varbitOperation,
			String varbitDisplayText,
			boolean itemHighlighted,
			String orderSlotId)
		{
			this.kind = kind;
			this.itemRawId = itemRawId;
			this.varbitId = varbitId;
			this.varbitRequiredValue = varbitRequiredValue;
			this.varbitOperation = varbitOperation;
			this.varbitDisplayText = varbitDisplayText;
			this.itemHighlighted = itemHighlighted;
			this.orderSlotId = orderSlotId;
		}

		public static StepAttachmentEdit copyOf(StepAttachmentEdit o)
		{
			if (o == null)
			{
				return null;
			}
			if (StepAttachmentKind.VARBIT.name().equalsIgnoreCase(o.getKind()))
			{
				if (o.getOrderSlotId() != null && !o.getOrderSlotId().isBlank())
				{
					return varbitForOrderSlot(o.getOrderSlotId(), o.getVarbitId(), o.getVarbitRequiredValue(), o.getVarbitOperation(), o.getVarbitDisplayText());
				}
				return varbit(o.getVarbitId(), o.getVarbitRequiredValue(), o.getVarbitOperation(), o.getVarbitDisplayText());
			}
			if (o.getItemRawId() == null)
			{
				return null;
			}
			return item(o.getItemRawId(), o.isItemHighlighted());
		}

		public static StepAttachmentEdit item(int rawId)
		{
			return item(rawId, false);
		}

		public static StepAttachmentEdit item(int rawId, boolean highlighted)
		{
			return new StepAttachmentEdit(StepAttachmentKind.ITEM.name(), rawId, null, null, null, null, highlighted, null);
		}

		public static StepAttachmentEdit varbit(int varbitId, int requiredValue, String operation, String displayText)
		{
			String op = operation == null || operation.isBlank() ? "EQUAL" : operation.trim();
			return new StepAttachmentEdit(StepAttachmentKind.VARBIT.name(), null, varbitId, requiredValue, op, displayText, false, null);
		}

		public static StepAttachmentEdit varbitForOrderSlot(String orderSlotId, int varbitId, int requiredValue, String operation, String displayText)
		{
			String op = operation == null || operation.isBlank() ? "EQUAL" : operation.trim();
			return new StepAttachmentEdit(StepAttachmentKind.VARBIT.name(), null, varbitId, requiredValue, op, displayText, false, orderSlotId);
		}

		public String getKind()
		{
			return kind;
		}

		public Integer getItemRawId()
		{
			return itemRawId;
		}

		public Integer getVarbitId()
		{
			return varbitId;
		}

		public Integer getVarbitRequiredValue()
		{
			return varbitRequiredValue;
		}

		public String getVarbitOperation()
		{
			return varbitOperation;
		}

		public String getVarbitDisplayText()
		{
			return varbitDisplayText;
		}

		/** When set for a VARBIT edit, binds to {@link DraftOrderLine#getOrderSlotId()}. */
		public String getOrderSlotId()
		{
			return orderSlotId;
		}

		public boolean isItemHighlighted()
		{
			return itemHighlighted;
		}

		public void setItemHighlighted(boolean itemHighlighted)
		{
			this.itemHighlighted = itemHighlighted;
		}
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
		private final String orderSlotId;
		private final String stepId;
		private final String varName;
		private final String summary;
		private final boolean sectionDivider;
		private final String sectionCondition;
		private final boolean skipWhenConditionMet;
		private final Integer orderLinkedRequirementRawId;
		private final String instructionText;
		private final boolean customOrderStepRequirement;
		private final boolean hasOrderStepRequirementTree;

		public CombinedStepRow(int index, String orderSlotId, String stepId, String varName, String summary, boolean sectionDivider, String sectionCondition, boolean skipWhenConditionMet, Integer orderLinkedRequirementRawId, String instructionText, boolean customOrderStepRequirement, boolean hasOrderStepRequirementTree)
		{
			this.index = index;
			this.orderSlotId = orderSlotId == null ? "" : orderSlotId;
			this.stepId = stepId;
			this.varName = varName;
			this.summary = summary;
			this.sectionDivider = sectionDivider;
			this.sectionCondition = sectionCondition;
			this.skipWhenConditionMet = skipWhenConditionMet;
			this.orderLinkedRequirementRawId = orderLinkedRequirementRawId;
			this.instructionText = instructionText == null ? "" : instructionText;
			this.customOrderStepRequirement = customOrderStepRequirement;
			this.hasOrderStepRequirementTree = hasOrderStepRequirementTree;
		}

		public int getIndex()
		{
			return index;
		}

		/** Stable id of the quest-order slot (not the step definition id); matches {@link DraftOrderLine#getOrderSlotId()}. */
		public String getOrderSlotId()
		{
			return orderSlotId;
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
		 * Optional captured item raw id for highlight when no {@link DraftOrderLine#getStepRequirement()} tree is set.
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

		/**
		 * When {@code true}, the persisted {@link DraftOrderLine#getStepRequirement()} tree is not the same as the
		 * legacy scalar migration would synthesize (user-edited JSON / non-trivial logic).
		 */
		public boolean isCustomOrderStepRequirement()
		{
			return customOrderStepRequirement;
		}

		public boolean hasOrderStepRequirementTree()
		{
			return hasOrderStepRequirementTree;
		}
	}
}
