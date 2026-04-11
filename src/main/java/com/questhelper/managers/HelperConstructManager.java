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
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
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
import static com.questhelper.managers.HelperConstructModels.DraftStepAttachedRequirement;
import static com.questhelper.managers.HelperConstructModels.DraftVarbitRequirement;
import static com.questhelper.managers.HelperConstructModels.StepAttachmentKind;
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
		var itemID = sourceEntry.getItemId();

		WorldPoint clickedWorldPoint = resolveClickedWorldPoint(sourceEntry, event);

		if (isNpcAction(sourceType))
		{
			addAction(menuEntries, MENU_PREFIX + " Add NPC Step", target, () -> addStep(StepKind.NPC, sourceEntry.getNpc().getId(), option, target, clickedWorldPoint));
		}
		else if (isObjectAction(sourceType))
		{
			addAction(menuEntries, MENU_PREFIX + " Add Object Step", target, () -> addStep(StepKind.OBJECT, rawId, option, target, clickedWorldPoint));
		}
		else if (isItemAction(sourceType))
		{
			addAction(menuEntries, MENU_PREFIX + " Add Item Requirement", target, () -> addRequirement(rawId, target));
			addAction(menuEntries, MENU_PREFIX + " Add Generic Step (item)", target, () -> addGenericStepFromItem(rawId, target));
		}
		else if (isInventoryItemAction(sourceType))
		{
			addAction(menuEntries, MENU_PREFIX + " Add Item Requirement", target, () -> addRequirement(itemID, target));
			addAction(menuEntries, MENU_PREFIX + " Add Generic Step (item)", target, () -> addGenericStepFromItem(itemID, target));
		}
		else if (isWalkHereMenu(sourceType, option) && clickedWorldPoint != null)
		{
			addAction(menuEntries, MENU_PREFIX + " Add Generic Step (here)", target, () -> addGenericStepAtWorldPoint(clickedWorldPoint));
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
		HelperConstructModels.IdType idType = kind == StepKind.NPC
			? HelperConstructModels.IdType.NPC
			: HelperConstructModels.IdType.OBJECT;
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

		if (isObjectAction(sourceEntry.getType()))
		{
			var fromObject = resolveObjectWorldPoint(sourceEntry);
			if (fromObject != null)
			{
				return fromObject;
			}
		}
		if (sourceEntry.getTarget().contains("Gauntlet"))
		{
			System.out.println("Wow");
			System.out.println(sourceEntry.getType());
			System.out.println(sourceEntry.getTarget());
			System.out.println(sourceEntry.getActor());
			System.out.println(sourceEntry.getParam0());
			System.out.println(sourceEntry.getParam1());
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
		requirement.setResolvedSymbol(symbolResolver.resolve(HelperConstructModels.IdType.ITEM, idToUse).getSymbol());
		requirement.setDisplayName(normalizeText(target).isBlank() ? "Captured Item" : normalizeText(target));
		currentDraft.getRequirements().add(requirement);
		saveDraftToConfig();
		sendGameMessage("Quest Helper Construct: added item requirement (" + idToUse + " -> " + requirement.getResolvedSymbol() + ").");
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
		step.setResolvedSymbol("");
		step.setOption("Use");
		step.setTargetText(target);
		String itemName = normalizeText(target).isBlank() ? requirement.getDisplayName() : normalizeText(target);
		step.setInstructionText("Use " + itemName + ".");
		step.setPanelName("Captured Steps");
		step.setSuggestedVarName(HelperScaffoldGenerator.toVarName("use " + itemName, "step"));
		step.getAttachedRequirements().add(DraftStepAttachedRequirement.item(normalizedItemId));
		currentDraft.getStepDefinitions().add(step);
		saveDraftToConfig();
		sendGameMessage("Quest Helper Construct: added generic step with item " + normalizedItemId + " (" + requirement.getResolvedSymbol() + "). Add it to quest order if needed.");
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
		step.setResolvedSymbol("");
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
		reconcileVarbitRequirementsWithOrder();
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
		reconcileVarbitRequirementsWithOrder();
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
				out.add(String.valueOf(step.getRawId()));
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
			HelperConstructModels.IdType idType = sk == StepKind.NPC ? HelperConstructModels.IdType.NPC : HelperConstructModels.IdType.OBJECT;
			step.setResolvedSymbol(symbolResolver.resolve(idType, 0).getSymbol());
			step.setSuggestedVarName(HelperScaffoldGenerator.toVarName(sk == StepKind.NPC ? "npc step" : "object step", "step"));
		}
		else
		{
			step.setRawId(0);
			step.setResolvedSymbol("");
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
		List<Integer> ids = getRequirementRawIds();
		List<String> labels = getRequirementSummaries();
		for (int i = 0; i < ids.size(); i++)
		{
			if (ids.get(i) == rawId)
			{
				return i < labels.size() ? labels.get(i) : String.valueOf(rawId);
			}
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
		int parsed;
		try
		{
			parsed = Integer.parseInt(rawIdText == null ? "" : rawIdText.trim());
		}
		catch (NumberFormatException ex)
		{
			return false;
		}
		HelperConstructModels.IdType idType = kind == StepKind.NPC ? HelperConstructModels.IdType.NPC : HelperConstructModels.IdType.OBJECT;
		step.setRawId(parsed);
		step.setResolvedSymbol(symbolResolver.resolve(idType, parsed).getSymbol());
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
					line.getLineId(),
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
				line.getLineId(),
				def.getStepId(),
				def.getSuggestedVarName(),
				formatStepSummary(def, i + 1),
				false,
				"",
				false,
				line.getLinkedRequirementRawId(),
				instr == null ? "" : instr));
		}
		reconcileVarbitRequirementsWithOrder();
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
		line.setLineId(UUID.randomUUID().toString());
		line.setSectionDivider(false);
		line.setRefStepId(stepId);
		line.setLinkedRequirementRawId(null);
		currentDraft.getOrder().add(line);
		ensureVarbitRecordForOrderLine(line);
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
		ensureOrderLineHasId(line);
		line.setLinkedRequirementRawId(linkedRequirementRawId);
		if (orderRowUsesDefaultOrVarbitRouting(linkedRequirementRawId))
		{
			ensureVarbitRecordForOrderLine(line);
		}
		else
		{
			removeVarbitRecordForLineId(line.getLineId());
		}
		saveDraftToConfig();
		return true;
	}

	private static boolean orderRowUsesDefaultOrVarbitRouting(Integer linkedRequirementRawId)
	{
		return linkedRequirementRawId == null || linkedRequirementRawId == ORDER_ROUTING_VARBIT_SENTINEL;
	}

	private void ensureOrderLineHasId(DraftOrderLine line)
	{
		if (line.getLineId() == null || line.getLineId().isBlank())
		{
			line.setLineId(UUID.randomUUID().toString());
		}
	}

	private void ensureVarbitRecordForOrderLine(DraftOrderLine line)
	{
		if (line.isSectionDivider() || !orderRowUsesDefaultOrVarbitRouting(line.getLinkedRequirementRawId()))
		{
			return;
		}
		ensureOrderLineHasId(line);
		String lid = line.getLineId();
		for (DraftVarbitRequirement v : currentDraft.getVarbitRequirements())
		{
			if (lid.equals(v.getLineId()))
			{
				return;
			}
		}
		currentDraft.getVarbitRequirements().add(new DraftVarbitRequirement(lid, 0, 1, "EQUAL", null));
	}

	private void removeVarbitRecordForLineId(String lineId)
	{
		if (lineId == null || lineId.isBlank())
		{
			return;
		}
		currentDraft.getVarbitRequirements().removeIf(v -> lineId.equals(v.getLineId()));
	}

	private void reconcileVarbitRequirementsWithOrder()
	{
		LinkedHashSet<String> wanted = new LinkedHashSet<>();
		for (DraftOrderLine line : currentDraft.getOrder())
		{
			if (line.isSectionDivider())
			{
				continue;
			}
			ensureOrderLineHasId(line);
			if (orderRowUsesDefaultOrVarbitRouting(line.getLinkedRequirementRawId()))
			{
				wanted.add(line.getLineId());
			}
		}
		currentDraft.getVarbitRequirements().removeIf(v -> v.getLineId() == null || !wanted.contains(v.getLineId()));
		for (String lineId : wanted)
		{
			DraftOrderLine line = findOrderLineByLineId(lineId);
			if (line != null)
			{
				ensureVarbitRecordForOrderLine(line);
			}
		}
	}

	private DraftOrderLine findOrderLineByLineId(String lineId)
	{
		for (DraftOrderLine line : currentDraft.getOrder())
		{
			if (lineId.equals(line.getLineId()))
			{
				return line;
			}
		}
		return null;
	}

	public List<VarbitSlotRow> getVarbitSlotsInQuestOrderForEditor()
	{
		ensureDraftLoaded();
		reconcileVarbitRequirementsWithOrder();
		List<VarbitSlotRow> out = new ArrayList<>();
		List<DraftOrderLine> order = currentDraft.getOrder();
		for (int ord = 0; ord < order.size(); ord++)
		{
			DraftOrderLine line = order.get(ord);
			if (line.isSectionDivider())
			{
				continue;
			}
			if (!orderRowUsesDefaultOrVarbitRouting(line.getLinkedRequirementRawId()))
			{
				continue;
			}
			ensureOrderLineHasId(line);
			DraftVarbitRequirement cfg = findVarbitRequirementByLineId(line.getLineId());
			if (cfg == null)
			{
				continue;
			}
			DraftStep def = findDefinitionByStepId(line.getRefStepId());
			String summary = def == null
				? "Order row " + (ord + 1) + " (missing definition)"
				: "Row " + (ord + 1) + ": " + formatStepSummary(def, ord + 1);
			out.add(new VarbitSlotRow(
				line.getLineId(),
				summary,
				cfg.getVarbitId(),
				cfg.getRequiredValue(),
				cfg.getOperation() == null || cfg.getOperation().isBlank() ? "EQUAL" : cfg.getOperation(),
				cfg.getDisplayText() == null ? "" : cfg.getDisplayText()));
		}
		return Collections.unmodifiableList(out);
	}

	private DraftVarbitRequirement findVarbitRequirementByLineId(String lineId)
	{
		for (DraftVarbitRequirement v : currentDraft.getVarbitRequirements())
		{
			if (lineId.equals(v.getLineId()))
			{
				return v;
			}
		}
		return null;
	}

	public boolean updateVarbitSlotForOrderLine(String orderLineId, int varbitId, int requiredValue, String operationName, String displayText)
	{
		ensureDraftLoaded();
		if (orderLineId == null || orderLineId.isBlank())
		{
			return false;
		}
		DraftVarbitRequirement cfg = findVarbitRequirementByLineId(orderLineId);
		if (cfg == null)
		{
			return false;
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
		cfg.setRequiredValue(requiredValue);
		cfg.setOperation(op.name());
		cfg.setDisplayText(displayText == null ? "" : displayText);
		saveDraftToConfig();
		return true;
	}

	public static final class VarbitSlotRow
	{
		private final String orderLineId;
		private final String orderSlotSummary;
		private final int varbitId;
		private final int requiredValue;
		private final String operation;
		private final String displayText;

		public VarbitSlotRow(String orderLineId, String orderSlotSummary, int varbitId, int requiredValue, String operation, String displayText)
		{
			this.orderLineId = orderLineId;
			this.orderSlotSummary = orderSlotSummary;
			this.varbitId = varbitId;
			this.requiredValue = requiredValue;
			this.operation = operation;
			this.displayText = displayText;
		}

		public String getOrderLineId()
		{
			return orderLineId;
		}

		public String getOrderSlotSummary()
		{
			return orderSlotSummary;
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
		reconcileVarbitRequirementsWithOrder();
		List<StepAttachmentPickOption> out = new ArrayList<>();
		List<Integer> ids = getRequirementRawIds();
		List<String> labels = getRequirementSummaries();
		for (int i = 0; i < ids.size(); i++)
		{
			String lab = i < labels.size() ? labels.get(i) : String.valueOf(ids.get(i));
			String shortLab = lab.replaceFirst("^\\d+\\.\\s*", "").trim();
			out.add(new StepAttachmentPickOption(shortLab, StepAttachmentEdit.item(ids.get(i))));
		}
		for (VarbitSlotRow row : getVarbitSlotsInQuestOrderForEditor())
		{
			String disp = row.getDisplayText();
			String base = row.getOrderSlotSummary() + " — " + row.getVarbitId() + " " + row.getOperation() + " " + row.getRequiredValue();
			String label = disp != null && !disp.isBlank() ? disp + " — " + base : base;
			out.add(new StepAttachmentPickOption(label, StepAttachmentEdit.varbit(
				row.getVarbitId(),
				row.getRequiredValue(),
				row.getOperation(),
				disp)));
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
		DraftOrderLine removed = currentDraft.getOrder().get(index);
		removeVarbitRecordForLineId(removed.getLineId());
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
			removeVarbitRecordForLineId(line.getLineId());
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
			ConstructDraftPersistence.DraftState state = gson.fromJson(json, ConstructDraftPersistence.DraftState.class);
			if (state == null)
			{
				return;
			}
			currentDraft = ConstructDraftPersistence.draftHelperFromState(state);
			reconcileVarbitRequirementsWithOrder();
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
		reconcileVarbitRequirementsWithOrder();
		return prettyDraftGson.toJson(ConstructDraftPersistence.toDraftState(currentDraft));
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
			ConstructDraftPersistence.DraftState state = gson.fromJson(json.trim(), ConstructDraftPersistence.DraftState.class);
			if (state == null)
			{
				return ImportDraftResult.failure("Could not parse draft");
			}
			currentDraft = ConstructDraftPersistence.draftHelperFromState(state);
			reconcileVarbitRequirementsWithOrder();
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

	private void saveDraftToConfig()
	{
		if (configManager == null)
		{
			return;
		}
		configManager.setConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, CONSTRUCT_DRAFT_CONFIG_KEY, gson.toJson(ConstructDraftPersistence.toDraftState(currentDraft)));
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
				displayName = step.getResolvedSymbol() != null ? step.getResolvedSymbol() : String.valueOf(step.getRawId());
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
			Integer itemHighlightRawId = itemHighlightRawIdForPreview(draftStep, orderLine.getLinkedRequirementRawId());
			ConstructStepKindHandlers.ConstructStepKindHandler handler = ConstructStepKindHandlers.forStepKind(draftStep.getKind());
			QuestStep step = handler != null
				? handler.buildPreviewQuestStep(new ConstructStepKindHandlers.ConstructPreviewStepParams(
				this, draftStep, instruction, extrasArr, requirementById, itemHighlightRawId))
				: new DetailedQuestStep(this, instruction);

			ManualRequirement manualOverride = defaultIncompleteRequirement();
			manualStepRequirements.add(manualOverride);
			return new PreviewStepEntry(step, or(originalCompletionRequirement, manualOverride));
		}

		private Requirement previewCompletionRequirement(DraftStep def, Map<Integer, ItemRequirement> requirementById, DraftOrderLine orderLine)
		{
			Integer override = orderLine.getLinkedRequirementRawId();
			if (override != null && override == ORDER_ROUTING_VARBIT_SENTINEL)
			{
				return varbitRequirementForOrderLine(orderLine);
			}
			Integer rid = override;
			if (rid != null)
			{
				ItemRequirement ir = requirementById.get(rid);
				if (ir != null)
				{
					return ir;
				}
				return defaultIncompleteRequirement();
			}
			return varbitRequirementForOrderLine(orderLine);
		}

		private Requirement varbitRequirementForOrderLine(DraftOrderLine orderLine)
		{
			if (orderLine.getLineId() == null || orderLine.getLineId().isBlank())
			{
				return defaultIncompleteRequirement();
			}
			for (DraftVarbitRequirement cfg : draft.getVarbitRequirements())
			{
				if (!orderLine.getLineId().equals(cfg.getLineId()))
				{
					continue;
				}
				return VarbitSpec.fromDraftVarbit(cfg).toVarbitRequirement();
			}
			return defaultIncompleteRequirement();
		}

		private Integer itemHighlightRawIdForPreview(DraftStep def, Integer orderLinkedOverride)
		{
			if (orderLinkedOverride != null && orderLinkedOverride != ORDER_ROUTING_VARBIT_SENTINEL)
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

		private PreviewStepEntry(QuestStep step, Requirement completionRequirement)
		{
			this.step = step;
			this.completionRequirement = completionRequirement;
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

		private StepAttachmentEdit(
			String kind,
			Integer itemRawId,
			Integer varbitId,
			Integer varbitRequiredValue,
			String varbitOperation,
			String varbitDisplayText,
			boolean itemHighlighted)
		{
			this.kind = kind;
			this.itemRawId = itemRawId;
			this.varbitId = varbitId;
			this.varbitRequiredValue = varbitRequiredValue;
			this.varbitOperation = varbitOperation;
			this.varbitDisplayText = varbitDisplayText;
			this.itemHighlighted = itemHighlighted;
		}

		public static StepAttachmentEdit copyOf(StepAttachmentEdit o)
		{
			if (o == null)
			{
				return null;
			}
			if (StepAttachmentKind.VARBIT.name().equalsIgnoreCase(o.getKind()))
			{
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
			return new StepAttachmentEdit(StepAttachmentKind.ITEM.name(), rawId, null, null, null, null, highlighted);
		}

		public static StepAttachmentEdit varbit(int varbitId, int requiredValue, String operation, String displayText)
		{
			String op = operation == null || operation.isBlank() ? "EQUAL" : operation.trim();
			return new StepAttachmentEdit(StepAttachmentKind.VARBIT.name(), null, varbitId, requiredValue, op, displayText, false);
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
		private final String orderLineId;
		private final String stepId;
		private final String varName;
		private final String summary;
		private final boolean sectionDivider;
		private final String sectionCondition;
		private final boolean skipWhenConditionMet;
		private final Integer orderLinkedRequirementRawId;
		private final String instructionText;

		public CombinedStepRow(int index, String orderLineId, String stepId, String varName, String summary, boolean sectionDivider, String sectionCondition, boolean skipWhenConditionMet, Integer orderLinkedRequirementRawId, String instructionText)
		{
			this.index = index;
			this.orderLineId = orderLineId == null ? "" : orderLineId;
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

		/** Stable id of the quest order row (not the step definition id). */
		public String getOrderLineId()
		{
			return orderLineId;
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
		 * Per order row: {@code null} = varbit routing (item steps use the step's item raw id for highlight); {@link HelperConstructModels#ORDER_ROUTING_VARBIT_SENTINEL} = varbit only; else captured requirement raw id for routing/highlight.
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
}
