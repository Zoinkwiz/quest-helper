/*
 * Copyright (c) 2026, Zoinkwiz <https://github.com/Zoinkwiz>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.questhelper.maker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.questhelper.managers.QuestManager;
import com.questhelper.maker.taskstroute.TasksTrackerRouteDto;
import com.questhelper.maker.taskstroute.TasksTrackerRouteDto.RouteCustomItemDto;
import com.questhelper.maker.taskstroute.TasksTrackerRouteDto.RouteItemDto;
import com.questhelper.maker.taskstroute.TasksTrackerRouteDto.RouteSectionDto;
import com.questhelper.maker.taskstroute.TasksTrackerRouteExporter;
import com.questhelper.maker.taskstroute.TasksTrackerRouteImporter;
import com.questhelper.maker.HelperConstructModels.DraftOrderStepRequirement;
import com.questhelper.maker.HelperConstructModels.OrderConditionMode;
import com.questhelper.maker.construct.DraftRoutingIds;
import com.questhelper.maker.construct.ConstructMenuCapture;
import com.questhelper.maker.construct.MakerDraftFileStore;
import com.questhelper.maker.construct.MakerImportFormatAdapter;
import com.questhelper.maker.construct.MakerImportFormatAdapter.AdaptedImport;
import com.questhelper.maker.construct.MakerImportFormatAdapter.ImportSourceFormat;
import com.questhelper.maker.construct.MakerDraftJsonLoader;
import com.questhelper.maker.construct.MakerPreviewRuntime;
import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.questhelper.QuestHelperConfig;
import com.questhelper.panel.ManualStepSkipStore;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.ManualRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.util.Operation;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.tools.QuestPerspective;
import com.questhelper.tools.ConstructWorldMapPoint;
import com.questhelper.util.worldmap.WorldPointMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.Menu;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.NPC;
import net.runelite.api.WorldView;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.client.RuneLite;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.ui.overlay.worldmap.WorldMapPointManager;
import net.runelite.client.util.Text;
import org.jetbrains.annotations.NotNull;

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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.UUID;
import java.util.regex.Pattern;

import static com.questhelper.maker.HelperConstructModels.DraftHelper;
import static com.questhelper.maker.HelperConstructModels.DraftOrderLine;
import static com.questhelper.maker.HelperConstructModels.DraftRequirement;
import static com.questhelper.maker.HelperConstructModels.DraftSkillRequirement;
import static com.questhelper.maker.HelperConstructModels.DraftStep;
import static com.questhelper.maker.HelperConstructModels.DraftStepAttachedRequirement;
import static com.questhelper.maker.HelperConstructModels.StepAttachmentKind;
import static com.questhelper.maker.HelperConstructModels.StepKind;
import static com.questhelper.requirements.util.LogicHelper.and;
import static com.questhelper.requirements.util.LogicHelper.not;
import static com.questhelper.requirements.util.LogicHelper.or;

@Singleton
@Slf4j
public class HelperConstructManager
{
	private static final Pattern TAG_PATTERN = Pattern.compile("<[^>]+>");
	/** One quest-order "linked item" option: label and the raw item id used for routing / highlight. */
	@Getter
	public static final class RequirementRoutingChoice
	{
		private final String label;
		private final int rawId;

		public RequirementRoutingChoice(String label, int rawId)
		{
			this.label = label;
			this.rawId = rawId;
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
	private QuestManager questManager;

	@Inject
	private Gson gson;

	@Getter
	private DraftHelper currentDraft = new DraftHelper();
	private boolean loadedFromConfig;
	private final Gson prettyDraftGson = new GsonBuilder().setPrettyPrinting().create();
	@Getter
	private boolean worldMapRoutePreviewEnabled;
	@Getter
	private int worldMapRouteRevealPercent = 100;
	private boolean makerUiOpen;
	private String selectedConstructMenuStepId;
	private WorldPoint pendingZoneFirstCorner;
	private WorldPoint selectedZoneOverlayCorner1;
	private WorldPoint selectedZoneOverlayCorner2;
	private boolean zoneSelectionOverlayEnabled;
	private final List<Runnable> draftChangeListeners = new CopyOnWriteArrayList<>();

	public void setMakerUiOpen(boolean makerUiOpen)
	{
		this.makerUiOpen = makerUiOpen;
	}

	public void setConstructMenuSelectedStep(ConstructStepKind kind, int filteredIndex)
	{
		ensureDraftLoaded();
		if (kind == null || filteredIndex < 0)
		{
			selectedConstructMenuStepId = null;
			return;
		}
		DraftStep step = stepDefinitionAtKindIndexOrNull(kind.stepKind(), filteredIndex);
		selectedConstructMenuStepId = step == null ? null : step.getStepId();
	}

	public void clearConstructMenuSelectedStep()
	{
		selectedConstructMenuStepId = null;
	}

	public void addDraftChangeListener(Runnable listener)
	{
		if (listener != null)
		{
			draftChangeListeners.add(listener);
		}
	}

	public void removeDraftChangeListener(Runnable listener)
	{
		draftChangeListeners.remove(listener);
	}

	private void notifyDraftChanged()
	{
		for (Runnable listener : draftChangeListeners)
		{
			try
			{
				listener.run();
			}
			catch (RuntimeException ex)
			{
				log.warn("Quest Helper Maker draft change listener failed.", ex);
			}
		}
	}

	/**
	 * Result of {@link #importDraftFromJson(String)}.
	 */
	public enum ImportMode
	{
		FULL_FRESH,
		ORDER_ONLY,
		DATA_ONLY
	}

	public enum ExportFormat
	{
		QH_FORMAT,
		LEAGUE_ROUTE
	}

	@Getter
	public static final class ImportDraftResult
	{
		private final boolean success;
		private final String errorMessage;
		private final String infoMessage;

		private ImportDraftResult(boolean success, String errorMessage, String infoMessage)
		{
			this.success = success;
			this.errorMessage = errorMessage;
			this.infoMessage = infoMessage;
		}

		public static ImportDraftResult ok()
		{
			return new ImportDraftResult(true, null, null);
		}

		public static ImportDraftResult ok(String infoMessage)
		{
			return new ImportDraftResult(true, null, infoMessage);
		}

		public static ImportDraftResult failure(String message)
		{
			return new ImportDraftResult(false, message == null ? "Unknown error" : message, null);
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
		if (!config.constructModeEnabled() || !makerUiOpen)
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
			NPC npc = sourceEntry.getNpc();
			if (npc != null)
			{
				int npcId = npc.getId();
				addAction(menuEntries, ConstructMenuCapture.MENU_OPTION_PREFIX + " Add NPC Step", target, () ->
				{
					addStep(StepKind.NPC, npcId, option, target, clickedWorldPoint);
				});
				DraftStep selected = selectedConstructMenuStepOrNull();
				if (selected != null && selected.getKind() == StepKind.NPC)
				{
					addAction(menuEntries, ConstructMenuCapture.MENU_OPTION_PREFIX + " Update step ID", target, () -> updateSelectedStepIdFromMenu(npcId));
				}
			}
		}
		else if (isObjectAction(sourceType))
		{
			addAction(menuEntries, ConstructMenuCapture.MENU_OPTION_PREFIX + " Add Object Step", target, () ->
			{
				addStep(StepKind.OBJECT, rawId, option, target, clickedWorldPoint);
			});
			DraftStep selected = selectedConstructMenuStepOrNull();
			if (selected != null && selected.getKind() == StepKind.OBJECT)
			{
				addAction(menuEntries, ConstructMenuCapture.MENU_OPTION_PREFIX + " Update step ID", target, () -> updateSelectedStepIdFromMenu(rawId));
			}
		}
		else if (isItemAction(sourceType))
		{
			addAction(menuEntries, ConstructMenuCapture.MENU_OPTION_PREFIX + " Add Item Requirement", target, () -> addRequirement(rawId, target));
			addAction(menuEntries, ConstructMenuCapture.MENU_OPTION_PREFIX + " Add Generic Step (item)", target, () -> addGenericStepFromItem(rawId, target));
			return;
		}
		else if (isInventoryItemAction(sourceType))
		{
			addAction(menuEntries, ConstructMenuCapture.MENU_OPTION_PREFIX + " Add Item Requirement", target, () -> addRequirement(itemID, target));
			addAction(menuEntries, ConstructMenuCapture.MENU_OPTION_PREFIX + " Add Generic Step (item)", target, () -> addGenericStepFromItem(itemID, target));
			return;
		}
		else if (isWalkHereMenu(sourceType, option) && clickedWorldPoint != null)
		{
			final WorldPoint tilePoint = clickedWorldPoint;
			String tileTarget = "Tile";
			addAction(menuEntries, ConstructMenuCapture.MENU_OPTION_PREFIX + " Add Generic Step (here)", tileTarget, () ->
			{
				addGenericStepAtWorldPoint(tilePoint);
			});
			if (pendingZoneFirstCorner == null)
			{
				addAction(menuEntries, ConstructMenuCapture.MENU_OPTION_PREFIX + " Create new Zone", tileTarget, () ->
				{
					startZoneCreationAt(tilePoint);
				});
			}
			else
			{
				addAction(menuEntries, ConstructMenuCapture.MENU_OPTION_PREFIX + " Add second zone corner", tileTarget, () ->
				{
					finishZoneCreationAt(tilePoint);
				});
				addAction(menuEntries, ConstructMenuCapture.MENU_OPTION_PREFIX + " Stop making zone", tileTarget, this::stopZoneCreationFromUi);
			}
		}
		else
		{
			return;
		}

		DraftStep selected = selectedConstructMenuStepOrNull();
		if (selected != null && clickedWorldPoint != null)
		{
			final WorldPoint point = clickedWorldPoint;
			String wpTarget = isWalkHereMenu(sourceType, option)
				? "Tile"
				: target;
			addAction(menuEntries, ConstructMenuCapture.MENU_OPTION_PREFIX + " Update step WP", wpTarget, () -> updateSelectedStepWorldPointFromMenu(point));
		}
	}

	private DraftStep selectedConstructMenuStepOrNull()
	{
		if (selectedConstructMenuStepId == null || selectedConstructMenuStepId.isBlank())
		{
			return null;
		}
		DraftStep def = findDefinitionByStepId(selectedConstructMenuStepId);
		if (def == null)
		{
			selectedConstructMenuStepId = null;
		}
		return def;
	}

	private void updateSelectedStepWorldPointFromMenu(WorldPoint worldPoint)
	{
		ensureDraftLoaded();
		DraftStep selected = selectedConstructMenuStepOrNull();
		if (selected == null || worldPoint == null)
		{
			return;
		}
		selected.setWorldPoint(worldPoint);
		saveDraftToConfig();
		rebuildWorldMapRouteIfEnabled();
		sendGameMessage("Quest Helper Construct: updated selected step world point to " + formatWorldPoint(worldPoint) + ".");
	}

	private void updateSelectedStepIdFromMenu(int rawId)
	{
		ensureDraftLoaded();
		DraftStep selected = selectedConstructMenuStepOrNull();
		if (selected == null || rawId <= 0)
		{
			return;
		}
		if (selected.getKind() != StepKind.NPC && selected.getKind() != StepKind.OBJECT)
		{
			return;
		}
		selected.setRawId(rawId);
		selected.getAlternateRawIds().clear();
		saveDraftToConfig();
		sendGameMessage("Quest Helper Construct: updated selected step id to " + rawId + ".");
	}

	private void startZoneCreationAt(WorldPoint tilePoint)
	{
		ensureDraftLoaded();
		if (tilePoint == null)
		{
			return;
		}
		pendingZoneFirstCorner = tilePoint;
		sendGameMessage("Quest Helper Construct: zone corner 1 saved at " + formatWorldPoint(tilePoint) + ". Right-click the next tile and choose 'Add second zone corner'.");
	}

	private void finishZoneCreationAt(WorldPoint tilePoint)
	{
		ensureDraftLoaded();
		if (pendingZoneFirstCorner == null || tilePoint == null)
		{
			return;
		}
		WorldPoint c1 = pendingZoneFirstCorner;
		pendingZoneFirstCorner = null;
		if (!addEmptyZoneSlotFromUi())
		{
			sendGameMessage("Quest Helper Construct: could not create new zone row.");
			return;
		}
		List<ZoneSlotRow> zones = getZoneSlotsInQuestOrderForEditor();
		if (zones.isEmpty())
		{
			sendGameMessage("Quest Helper Construct: zone row created but corners could not be applied.");
			return;
		}
		ZoneSlotRow created = zones.get(zones.size() - 1);
		boolean ok = updateZoneSlotForOrderSlot(
			created.getOrderSlotId(),
			formatWorldPointForField(c1),
			formatWorldPointForField(tilePoint),
			"");
		if (ok)
		{
			clearSelectedZoneOverlay();
			sendGameMessage("Quest Helper Construct: created zone from " + formatWorldPoint(c1) + " to " + formatWorldPoint(tilePoint) + ".");
		}
		else
		{
			sendGameMessage("Quest Helper Construct: could not apply zone corners.");
		}
	}

	public void stopZoneCreationFromUi()
	{
		if (pendingZoneFirstCorner == null)
		{
			return;
		}
		pendingZoneFirstCorner = null;
		sendGameMessage("Quest Helper Construct: stopped making zone.");
	}

	private void addAction(MenuEntry[] menuEntries, String option, String target, Runnable callback)
	{
		String safeTarget = target == null ? "" : target.trim();
		String decoratedTarget = safeTarget.isEmpty() ? "" : "<col=ff9040>" + safeTarget + "</col>";
		client.getMenu().createMenuEntry(menuEntries.length - 1)
			.setOption(option)
			.setTarget(decoratedTarget)
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
		pendingZoneFirstCorner = null;
		clearSelectedZoneOverlay();
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
		try
		{
			var itemDefinition = client.getItemDefinition(rawId);
			if (itemDefinition.getNote() >= 0)
			{
				return itemDefinition.getLinkedNoteId();
			}
		}
		catch (AssertionError ex)
		{
			// Some edit paths run on Swing thread (not client thread). In that case keep the raw id.
			log.debug("Skipping item id normalization off client thread for rawId={}", rawId);
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

	public void disableWorldMapRoutePreview()
	{
		worldMapRoutePreviewEnabled = false;
		clearWorldMapRoutePoints();
	}

	public boolean hasZoneCreationOrSelectionOverlay()
	{
		return pendingZoneFirstCorner != null
			|| (zoneSelectionOverlayEnabled && selectedZoneOverlayCorner1 != null && selectedZoneOverlayCorner2 != null);
	}

	public void setZoneSelectionOverlayEnabled(boolean enabled)
	{
		this.zoneSelectionOverlayEnabled = enabled;
	}

	public WorldPoint getPendingZoneFirstCorner()
	{
		return pendingZoneFirstCorner;
	}

	public WorldPoint getSelectedZoneOverlayCorner1()
	{
		return selectedZoneOverlayCorner1;
	}

	public WorldPoint getSelectedZoneOverlayCorner2()
	{
		return selectedZoneOverlayCorner2;
	}

	public void clearSelectedZoneOverlay()
	{
		selectedZoneOverlayCorner1 = null;
		selectedZoneOverlayCorner2 = null;
	}

	public void setSelectedZoneOverlayByOrderSlotId(String orderSlotId)
	{
		ensureDraftLoaded();
		if (orderSlotId == null || orderSlotId.isBlank())
		{
			clearSelectedZoneOverlay();
			return;
		}
		DraftOrderLine line = findOrderLineByOrderSlotId(orderSlotId);
		if (line == null || line.isSectionDivider())
		{
			clearSelectedZoneOverlay();
			return;
		}
		selectedZoneOverlayCorner1 = line.getZoneRoutingCorner1();
		selectedZoneOverlayCorner2 = line.getZoneRoutingCorner2();
	}

	public List<WorldPoint> getWorldMapRouteLinePoints()
	{
		ensureDraftLoaded();
		if (!worldMapRoutePreviewEnabled)
		{
			return Collections.emptyList();
		}
		List<WorldPoint> points = new ArrayList<>();
		List<DraftStep> visibleSteps = getVisibleOrderedRouteSteps();
		for (DraftStep step : visibleSteps)
		{
			WorldPoint wp = step.getWorldPoint();
			if (wp != null && isWithinMapBounds(wp))
			{
				points.add(WorldPointMapper.getMapWorldPointFromRealWorldPoint(wp).getWorldPoint());
			}
		}
		return points;
	}

	public void setWorldMapRouteRevealPercent(int percent)
	{
		ensureDraftLoaded();
		int clamped = clampWorldMapRouteRevealPercent(percent);
		if (worldMapRouteRevealPercent == clamped)
		{
			return;
		}
		worldMapRouteRevealPercent = clamped;
		rebuildWorldMapRouteIfEnabled();
	}

	public int getOrderedRouteStepCountForPreview()
	{
		ensureDraftLoaded();
		return expandOrderToDefinitionsInOrder().size();
	}

	public int getVisibleRouteStepCountForPreview()
	{
		ensureDraftLoaded();
		return visibleRouteStepCount(expandOrderToDefinitionsInOrder().size());
	}

	public void stepWorldMapRouteRevealBy(int stepDelta)
	{
		if (stepDelta == 0)
		{
			return;
		}
		ensureDraftLoaded();
		int totalSteps = expandOrderToDefinitionsInOrder().size();
		if (totalSteps <= 0)
		{
			return;
		}
		int currentVisible = visibleRouteStepCount(totalSteps);
		int targetVisible = Math.max(1, Math.min(totalSteps, currentVisible + stepDelta));
		setWorldMapRouteRevealPercent(percentForVisibleStepCount(targetVisible, totalSteps));
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

	public List<Boolean> getStepLeagueFlags(ConstructStepKind kind)
	{
		ensureDraftLoaded();
		List<Boolean> out = new ArrayList<>();
		StepKind targetKind = kind.stepKind();
		for (DraftStep step : currentDraft.getStepDefinitions())
		{
			if (step.getKind() != targetKind)
			{
				continue;
			}
			Integer structId = step.getStructId();
			out.add(structId != null && structId != 0);
		}
		return Collections.unmodifiableList(out);
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

	public Integer getStepStructIdAt(ConstructStepKind kind, int index)
	{
		ensureDraftLoaded();
		DraftStep step = stepDefinitionAtKindIndexOrNull(kind.stepKind(), index);
		if (step == null)
		{
			return null;
		}
		Integer structId = step.getStructId();
		return structId != null && structId != 0 ? structId : null;
	}

	public boolean updateStepStructIdAt(ConstructStepKind kind, int index, Integer structId)
	{
		ensureDraftLoaded();
		DraftStep step = stepDefinitionAtKindIndexOrNull(kind.stepKind(), index);
		if (step == null)
		{
			return false;
		}
		if (structId == null || structId == 0)
		{
			step.setStructId(null);
		}
		else
		{
			step.setStructId(structId);
		}
		saveDraftToConfig();
		rebuildWorldMapRouteIfEnabled();
		return true;
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
		ConstructStepKindHandlers.ConstructStepKindHandler handler = ConstructStepKindHandlers.forStepKind(sk);
		if (handler == null)
		{
			handler = ConstructStepKindHandlers.forStepKind(StepKind.TEXT);
		}
		DraftStep step = handler.createBlankStepForMakerUi();
		step.setStepId(UUID.randomUUID().toString());
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
		if (StepAttachmentKind.SKILL.name().equalsIgnoreCase(edit.getKind()))
		{
			Skill skill = parseSkillName(edit.getSkillName());
			int level = edit.getSkillRequiredLevel() == null ? 1 : Math.max(1, edit.getSkillRequiredLevel());
			String op = normalizeOperationName(edit.getSkillOperation()).name();
			String base = level + " " + skill.getName() + " (" + op + ")";
			if (edit.isSkillCanBeBoosted())
			{
				base += " [boost]";
			}
			String displayText = edit.getSkillDisplayText();
			return displayText == null || displayText.isBlank() ? base : displayText + " — " + base;
		}
		if (StepAttachmentKind.ITEM.name().equalsIgnoreCase(edit.getKind()))
		{
			if (edit.getItemRawId() == null)
			{
				return "(unset)";
			}
			String base = requirementDisplayLabelForRawId(edit.getItemRawId());
			int q = edit.getItemQuantity();
			return q > 1 ? base + " ×" + q : base;
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

	private static Skill parseSkillName(String name)
	{
		if (name == null || name.isBlank())
		{
			return Skill.ATTACK;
		}
		try
		{
			return Skill.valueOf(name.trim().toUpperCase(Locale.ROOT));
		}
		catch (IllegalArgumentException ex)
		{
			return Skill.ATTACK;
		}
	}

	private static Operation normalizeOperationName(String opName)
	{
		if (opName == null || opName.isBlank())
		{
			return Operation.GREATER_EQUAL;
		}
		try
		{
			return Operation.valueOf(opName.trim().toUpperCase(Locale.ROOT));
		}
		catch (IllegalArgumentException ex)
		{
			return Operation.GREATER_EQUAL;
		}
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
		if (StepAttachmentKind.SKILL.name().equalsIgnoreCase(k))
		{
			Skill skill = parseSkillName(a.getSkillName());
			int level = a.getSkillRequiredLevel() == null ? 1 : Math.max(1, a.getSkillRequiredLevel());
			return StepAttachmentEdit.skill(skill.name(), level, a.getSkillOperation(), a.getSkillDisplayText(), a.isSkillCanBeBoosted());
		}
		int itemId = a.getItemRawId() == null ? 0 : a.getItemRawId();
		int q = Math.max(a.getItemQuantity(), 1);
		return StepAttachmentEdit.item(itemId, a.isAttachmentHighlighted(), q);
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
		if (StepAttachmentKind.SKILL.name().equalsIgnoreCase(edit.getKind()))
		{
			Skill skill = parseSkillName(edit.getSkillName());
			int level = edit.getSkillRequiredLevel() == null ? 1 : Math.max(1, edit.getSkillRequiredLevel());
			Operation op = normalizeOperationName(edit.getSkillOperation());
			return DraftStepAttachedRequirement.skill(
				skill.name(),
				level,
				op.name(),
				edit.getSkillDisplayText(),
				edit.isSkillCanBeBoosted());
		}
		if (edit.getItemRawId() == null)
		{
			return null;
		}
		int q = edit.getItemQuantity() < 1 ? 1 : edit.getItemQuantity();
		return DraftStepAttachedRequirement.item(edit.getItemRawId(), edit.isItemHighlighted(), q);
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
					OrderConditionMode.SHOW_WHEN_TRUE,
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
					false,
					effectiveOrderConditionMode(line),
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
				def.getStructId() != null && def.getStructId() != 0,
				effectiveOrderConditionMode(line),
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
		int idx = insertAt < 0 ? order.size() : Math.min(insertAt, order.size());
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
		line.setStepRequirementMode(OrderConditionMode.CONTINUE_WHEN_TRUE);
		List<DraftOrderLine> order = currentDraft.getOrder();
		int idx = insertAt < 0 ? order.size() : Math.min(insertAt, order.size());
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

	/**
	 * Appends a new empty generic step, a quest-order row, and wires default ORDER_ZONE routing on that order row
	 * so the Zone reqs tab can edit it immediately.
	 * @return {@code true} when the placeholder row was created and zone routing attached.
	 */
	public boolean addEmptyZoneSlotFromUi()
	{
		ensureDraftLoaded();
		addEmptyStepFromUi(ConstructStepKind.TEXT);
		List<DraftStep> defs = currentDraft.getStepDefinitions();
		DraftStep placeholder = defs.get(defs.size() - 1);
		placeholder.setSuggestedVarName(HelperScaffoldGenerator.toVarName("zone", "step"));
		addOrderRef(placeholder.getStepId());
		List<DraftOrderLine> order = currentDraft.getOrder();
		if (order.isEmpty())
		{
			return false;
		}
		int addedIndex = order.size() - 1;
		DraftOrderLine added = order.get(addedIndex);
		if (added.isSectionDivider())
		{
			return false;
		}
		return addZoneSlotToOrderRowFromUi(addedIndex);
	}

	/**
	 * Adds default zone corners and an {@code ORDER_ZONE} leaf to an existing quest-order step row — no new step
	 * definition and no new order line. Use when the maker should only wire zone routing onto the row the author
	 * already selected in Quest order.
	 *
	 * @return {@code false} when the index is invalid, the row is a section, the row already participates in zone
	 *         routing for the editor, or the updated conditions tree fails validation.
	 */
	public boolean addZoneSlotToOrderRowFromUi(int orderIndex)
	{
		ensureDraftLoaded();
		List<DraftOrderLine> order = currentDraft.getOrder();
		if (orderIndex < 0 || orderIndex >= order.size())
		{
			return false;
		}
		DraftOrderLine line = order.get(orderIndex);
		if (line.isSectionDivider())
		{
			return false;
		}
		if (OrderStepRequirementSupport.orderLineUsesZoneRoutingForEditor(line))
		{
			return false;
		}
		DraftOrderStepRequirement priorRoot = line.getStepRequirement();
		DraftOrderStepRequirement priorClone = priorRoot == null
			? null
			: gson.fromJson(gson.toJson(priorRoot), DraftOrderStepRequirement.class);
		WorldPoint prevC1 = line.getZoneRoutingCorner1();
		WorldPoint prevC2 = line.getZoneRoutingCorner2();
		String prevDisp = line.getZoneRoutingDisplayText();

		ensureOrderSlotId(line);
		line.setZoneRoutingCorner1(new WorldPoint(0, 0, 0));
		line.setZoneRoutingCorner2(new WorldPoint(1, 1, 0));
		line.setZoneRoutingDisplayText(null);

		DraftOrderStepRequirement zoneLeaf = DraftOrderStepRequirement.zone(
			line.getZoneRoutingCorner1(),
			line.getZoneRoutingCorner2(),
			line.getZoneRoutingDisplayText());
		DraftOrderStepRequirement newRoot = priorClone == null
			? zoneLeaf
			: DraftOrderStepRequirement.group("AND", priorClone, zoneLeaf);
		line.setStepRequirement(newRoot);
		String err = OrderStepRequirementSupport.prepareOrderStepTreeForPersistence(line, line.getStepRequirement());
		if (err != null)
		{
			line.setStepRequirement(priorRoot);
			line.setZoneRoutingCorner1(prevC1);
			line.setZoneRoutingCorner2(prevC2);
			line.setZoneRoutingDisplayText(prevDisp);
			log.warn("Could not add zone slot to order row {}: {}", orderIndex, err);
			return false;
		}
		OrderStepRequirementSupport.mirrorLinkedRawIdFromSimpleTree(line);
		if (line.getStepRequirement() != null)
		{
			line.setLinkedRequirementRawId(null);
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
			if (line.getStepRequirement() != null)
			{
				line.setLinkedRequirementRawId(null);
			}
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

	public OrderConditionMode getOrderStepRequirementMode(int orderIndex)
	{
		ensureDraftLoaded();
		if (orderIndex < 0 || orderIndex >= currentDraft.getOrder().size())
		{
			return OrderConditionMode.SHOW_WHEN_TRUE;
		}
		DraftOrderLine line = currentDraft.getOrder().get(orderIndex);
		return effectiveOrderConditionMode(line);
	}

	public String applyOrderStepRequirementMode(int orderIndex, OrderConditionMode mode)
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
		line.setStepRequirementMode(mode == null ? OrderConditionMode.CONTINUE_WHEN_TRUE : mode);
		saveDraftToConfig();
		return null;
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
			if (line.getStepRequirementMode() == null)
			{
				line.setStepRequirementMode(OrderConditionMode.CONTINUE_WHEN_TRUE);
			}
			saveDraftToConfig();
			return null;
		}
		String err = OrderStepRequirementSupport.prepareOrderStepTreeForPersistence(line, tree);
		if (err != null)
		{
			return err;
		}
		line.setStepRequirement(tree);
		if (line.getStepRequirementMode() == null)
		{
			line.setStepRequirementMode(OrderConditionMode.CONTINUE_WHEN_TRUE);
		}
		// Keep tree-based order conditions fully order-scoped; do not mirror into legacy linked item routing.
		line.setLinkedRequirementRawId(null);
		saveDraftToConfig();
		return null;
	}

	private static OrderConditionMode effectiveOrderConditionMode(DraftOrderLine line)
	{
		if (line == null || line.getStepRequirementMode() == null)
		{
			return OrderConditionMode.SHOW_WHEN_TRUE;
		}
		return line.getStepRequirementMode();
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

	private void reconcileOrderSlotZoneRouting()
	{
		for (DraftOrderLine line : currentDraft.getOrder())
		{
			if (line == null || line.isSectionDivider())
			{
				continue;
			}
			if (!OrderStepRequirementSupport.orderLineUsesZoneRoutingForEditor(line))
			{
				line.setZoneRoutingCorner1(null);
				line.setZoneRoutingCorner2(null);
				line.setZoneRoutingDisplayText(null);
			}
		}
	}

	/** True when this row has any varbit condition leaf or already has legacy routing data attached. */
	private static boolean orderLineUsesVarbitRouting(DraftOrderLine line)
	{
		if (line == null || line.isSectionDivider())
		{
			return false;
		}
		return OrderStepRequirementSupport.treeContainsAnyVarbitLeaf(line.getStepRequirement())
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
		reconcileOrderSlotZoneRouting();
		List<VarbitSlotRow> out = new ArrayList<>();
		List<DraftOrderLine> order = currentDraft.getOrder();
		for (DraftOrderLine line : order)
		{
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

	@Getter
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

	}

	public List<ZoneSlotRow> getZoneSlotsInQuestOrderForEditor()
	{
		ensureDraftLoaded();
		reconcileOrderSlotRoutingAttachments();
		reconcileOrderSlotZoneRouting();
		List<ZoneSlotRow> out = new ArrayList<>();
		List<DraftOrderLine> order = currentDraft.getOrder();
		for (DraftOrderLine line : order)
		{
			if (line.isSectionDivider())
			{
				continue;
			}
			if (!OrderStepRequirementSupport.orderLineUsesZoneRoutingForEditor(line))
			{
				continue;
			}
			ensureOrderSlotId(line);
			DraftStep def = findDefinitionByStepId(line.getRefStepId());
			String varName = def == null
				? "?"
				: (def.getSuggestedVarName() == null || def.getSuggestedVarName().isBlank() ? "?" : def.getSuggestedVarName());
			WorldPoint c1 = line.getZoneRoutingCorner1();
			WorldPoint c2 = line.getZoneRoutingCorner2();
			if (c1 == null || c2 == null)
			{
				continue;
			}
			out.add(new ZoneSlotRow(
				line.getOrderSlotId(),
				varName,
				formatWorldPointForField(c1),
				formatWorldPointForField(c2),
				line.getZoneRoutingDisplayText() == null ? "" : line.getZoneRoutingDisplayText()));
		}
		return Collections.unmodifiableList(out);
	}

	private boolean applyZoneRoutingToOrderLine(
		DraftOrderLine line,
		WorldPoint corner1,
		WorldPoint corner2,
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
		if (corner1 == null || corner2 == null)
		{
			return false;
		}
		ensureOrderSlotId(line);
		line.setZoneRoutingCorner1(corner1);
		line.setZoneRoutingCorner2(corner2);
		line.setZoneRoutingDisplayText(displayText == null || displayText.isBlank() ? null : displayText.trim());
		return true;
	}

	public boolean updateZoneSlotForOrderSlot(String orderSlotId, String corner1Text, String corner2Text, String displayText)
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
		WorldPoint c1 = parseWorldPointField(corner1Text);
		WorldPoint c2 = parseWorldPointField(corner2Text);
		if (c1 == null || c2 == null)
		{
			return false;
		}
		if (!applyZoneRoutingToOrderLine(line, c1, c2, displayText, true))
		{
			return false;
		}
		saveDraftToConfig();
		return true;
	}

	/**
	 * Applies zone routing to the quest-order row at {@code orderIndex}. Used from the conditions editor; when
	 * {@code persist} is false the caller saves afterward (e.g. with {@link #applyOrderStepRequirementTree}).
	 */
	public boolean applyZoneRoutingToOrderLineByIndex(
		int orderIndex,
		WorldPoint corner1,
		WorldPoint corner2,
		String displayText,
		boolean persist)
	{
		ensureDraftLoaded();
		if (orderIndex < 0 || orderIndex >= currentDraft.getOrder().size())
		{
			return false;
		}
		DraftOrderLine line = currentDraft.getOrder().get(orderIndex);
		if (!applyZoneRoutingToOrderLine(line, corner1, corner2, displayText, false))
		{
			return false;
		}
		if (persist)
		{
			saveDraftToConfig();
		}
		return true;
	}

	@Getter
	public static final class ZoneSlotRow
	{
		private final String orderSlotId;
		private final String varName;
		private final String corner1Text;
		private final String corner2Text;
		private final String displayText;

		public ZoneSlotRow(String orderSlotId, String varName, String corner1Text, String corner2Text, String displayText)
		{
			this.orderSlotId = orderSlotId;
			this.varName = varName == null ? "" : varName;
			this.corner1Text = corner1Text == null ? "" : corner1Text;
			this.corner2Text = corner2Text == null ? "" : corner2Text;
			this.displayText = displayText == null ? "" : displayText;
		}

	}

	@Getter
	public static final class SkillReqRow
	{
		private final int index;
		private final String skillName;
		private final int requiredLevel;
		private final boolean canBeBoosted;
		private final String displayText;
		private final String operation;

		public SkillReqRow(int index, String skillName, int requiredLevel, boolean canBeBoosted, String displayText, String operation)
		{
			this.index = index;
			this.skillName = skillName == null ? Skill.ATTACK.name() : skillName;
			this.requiredLevel = Math.max(1, requiredLevel);
			this.canBeBoosted = canBeBoosted;
			this.displayText = displayText == null ? "" : displayText;
			this.operation = operation == null || operation.isBlank() ? Operation.GREATER_EQUAL.name() : operation;
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
			String label = formatStepDefinitionPickLabel(def, i);
			String filterText = formatStepDefinitionPickFilterText(def);
			out.add(new StepDefinitionPickOption(def.getStepId(), label, filterText));
			i++;
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
		for (SkillReqRow row : getSkillRequirementsForEditor())
		{
			String base = row.getRequiredLevel() + " " + parseSkillName(row.getSkillName()).getName()
				+ " (" + row.getOperation() + ")";
			if (row.isCanBeBoosted())
			{
				base += " [boost]";
			}
			String display = row.getDisplayText();
			String label = !display.isBlank() ? display + " — " + base : base;
			out.add(new StepAttachmentPickOption(label, StepAttachmentEdit.skill(
				row.getSkillName(),
				row.getRequiredLevel(),
				row.getOperation(),
				row.getDisplayText(),
				row.isCanBeBoosted())));
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
		currentDraft.getRequirements().add(RequirementDraftFactory.newPlaceholderItemRequirement());
		saveDraftToConfig();
	}

	public List<SkillReqRow> getSkillRequirementsForEditor()
	{
		ensureDraftLoaded();
		List<SkillReqRow> out = new ArrayList<>();
		List<DraftSkillRequirement> skills = currentDraft.getSkillRequirements();
		for (int i = 0; i < skills.size(); i++)
		{
			DraftSkillRequirement s = skills.get(i);
			Skill skill = parseSkillName(s.getSkillName());
			int requiredLevel = Math.max(1, s.getRequiredLevel());
			String displayText = s.getDisplayText() == null ? "" : s.getDisplayText();
			Operation operation = normalizeOperationName(s.getOperation());
			out.add(new SkillReqRow(i, skill.name(), requiredLevel, s.isCanBeBoosted(), displayText, operation.name()));
		}
		return Collections.unmodifiableList(out);
	}

	public void addEmptySkillRequirementFromUi()
	{
		ensureDraftLoaded();
		currentDraft.getSkillRequirements().add(RequirementDraftFactory.newPlaceholderSkillRequirement());
		saveDraftToConfig();
	}

	public boolean removeSkillRequirementAt(int index)
	{
		ensureDraftLoaded();
		if (index < 0 || index >= currentDraft.getSkillRequirements().size())
		{
			return false;
		}
		currentDraft.getSkillRequirements().remove(index);
		saveDraftToConfig();
		return true;
	}

	public boolean updateSkillRequirementAt(int index, String skillName, int requiredLevel, boolean canBeBoosted, String displayText, String operation)
	{
		ensureDraftLoaded();
		if (index < 0 || index >= currentDraft.getSkillRequirements().size())
		{
			return false;
		}
		Skill skill = parseSkillName(skillName);
		Operation op = normalizeOperationName(operation);
		DraftSkillRequirement row = currentDraft.getSkillRequirements().get(index);
		row.setSkillName(skill.name());
		row.setRequiredLevel(Math.max(1, requiredLevel));
		row.setCanBeBoosted(canBeBoosted);
		row.setDisplayText(displayText == null ? "" : displayText.trim());
		row.setOperation(op.name());
		saveDraftToConfig();
		return true;
	}

	public void ensureSkillRequirementForReuse(String skillName, int requiredLevel, boolean canBeBoosted, String displayText, String operation)
	{
		ensureDraftLoaded();
		Skill skill = parseSkillName(skillName);
		int level = Math.max(1, requiredLevel);
		String normalizedDisplay = displayText == null ? "" : displayText.trim();
		Operation op = normalizeOperationName(operation);
		for (DraftSkillRequirement existing : currentDraft.getSkillRequirements())
		{
			Skill exSkill = parseSkillName(existing.getSkillName());
			int exLevel = Math.max(1, existing.getRequiredLevel());
			String exDisplay = existing.getDisplayText() == null ? "" : existing.getDisplayText().trim();
			Operation exOp = normalizeOperationName(existing.getOperation());
			if (exSkill == skill
				&& exLevel == level
				&& existing.isCanBeBoosted() == canBeBoosted
				&& exOp == op
				&& Objects.equals(exDisplay, normalizedDisplay))
			{
				return;
			}
		}
		DraftSkillRequirement add = new DraftSkillRequirement();
		add.setSkillName(skill.name());
		add.setRequiredLevel(level);
		add.setCanBeBoosted(canBeBoosted);
		add.setDisplayText(normalizedDisplay);
		add.setOperation(op.name());
		currentDraft.getSkillRequirements().add(add);
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
			line.setLinkedRequirementRawId(null);
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

	/**
	 * Clears zone routing for this quest-order slot, removes {@code ORDER_ZONE} nodes from that row's conditions tree.
	 * Does not remove the order row or any step definition.
	 */
	public boolean clearZoneRoutingForOrderSlotId(String orderSlotId)
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
		line.setZoneRoutingCorner1(null);
		line.setZoneRoutingCorner2(null);
		line.setZoneRoutingDisplayText(null);
		DraftOrderStepRequirement tree = line.getStepRequirement();
		if (tree != null)
		{
			DraftOrderStepRequirement pruned = OrderStepRequirementSupport.stripOrderZoneLeaves(tree);
			line.setStepRequirement(pruned);
			line.setLinkedRequirementRawId(null);
		}
		saveDraftToConfig();
		if (worldMapRoutePreviewEnabled)
		{
			rebuildWorldMapRoutePoints();
		}
		return true;
	}

	/** Short label for {@code ORDER_ZONE} leaves in the order-conditions editor (from this row's Zone reqs tab). */
	public String formatOrderZoneLeafSummaryForEditor(int orderIndex)
	{
		ensureDraftLoaded();
		if (orderIndex < 0 || orderIndex >= currentDraft.getOrder().size())
		{
			return "Zone";
		}
		DraftOrderLine line = currentDraft.getOrder().get(orderIndex);
		if (line.isSectionDivider())
		{
			return "Zone";
		}
		WorldPoint c1 = line.getZoneRoutingCorner1();
		WorldPoint c2 = line.getZoneRoutingCorner2();
		if (c1 == null || c2 == null)
		{
			return "Zone (set corners on Zone reqs tab)";
		}
		String disp = line.getZoneRoutingDisplayText();
		String pts = "(" + c1.getX() + "," + c1.getY() + "," + c1.getPlane() + ")–("
			+ c2.getX() + "," + c2.getY() + "," + c2.getPlane() + ")";
		if (disp != null && !disp.isBlank())
		{
			return disp + " — " + pts;
		}
		return "Zone " + pts;
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

	public void updateOrderReferencedStepInstructionText(int orderIndex, String instructionText)
	{
		ensureDraftLoaded();
		if (orderIndex < 0 || orderIndex >= currentDraft.getOrder().size())
		{
			return;
		}
		DraftOrderLine line = currentDraft.getOrder().get(orderIndex);
		if (line.isSectionDivider())
		{
			return;
		}
		DraftStep def = findDefinitionByStepId(line.getRefStepId());
		if (def == null)
		{
			return;
		}
		def.setInstructionText(instructionText == null ? "" : instructionText);
		saveDraftToConfig();
	}

	public Integer getOrderReferencedStepStructId(int orderIndex)
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
		DraftStep def = findDefinitionByStepId(line.getRefStepId());
		if (def == null)
		{
			return null;
		}
		Integer structId = def.getStructId();
		return structId != null && structId != 0 ? structId : null;
	}

	public boolean updateOrderReferencedStepStructId(int orderIndex, Integer structId)
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
		if (structId == null || structId == 0)
		{
			def.setStructId(null);
		}
		else
		{
			def.setStructId(structId);
		}
		saveDraftToConfig();
		rebuildWorldMapRouteIfEnabled();
		return true;
	}

	public void updateStepVarName(int index, String updatedVarName)
	{
		ensureDraftLoaded();
		if (index < 0 || index >= currentDraft.getOrder().size())
		{
			return;
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
				return;
			}
			def.setSuggestedVarName(v);
		}
		saveDraftToConfig();
		if (worldMapRoutePreviewEnabled)
		{
			rebuildWorldMapRoutePoints();
		}
	}

	public void updateSectionCondition(int index, String condition)
	{
		ensureDraftLoaded();
		if (index < 0 || index >= currentDraft.getOrder().size())
		{
			return;
		}
		DraftOrderLine line = currentDraft.getOrder().get(index);
		if (!line.isSectionDivider())
		{
			return;
		}
		line.setSectionCondition(condition == null ? "" : condition);
		saveDraftToConfig();
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

	private boolean safeEquals(String left, String right)
	{
		return Objects.equals(left, right);
	}

	private void removeOrderLineAtIndexNoSave(int index)
	{
		if (index < 0 || index >= currentDraft.getOrder().size())
		{
			return;
		}
		DraftOrderLine removed = currentDraft.getOrder().get(index);
		removeRoutingVarbitForOrderSlot(removed.getOrderSlotId());
		currentDraft.getOrder().remove(index);
	}

	/**
	 * Removes several quest-order rows in one save. Pass model indices (positions in the draft order list), not view
	 * indices; duplicates are ignored. Indices may be in any order.
	 */
	public void removeOrderLinesAtModelIndices(List<Integer> modelIndices)
	{
		ensureDraftLoaded();
		if (modelIndices == null || modelIndices.isEmpty())
		{
			return;
		}
		List<Integer> uniqueSortedDesc = new ArrayList<>(new LinkedHashSet<>(modelIndices));
		uniqueSortedDesc.sort(Collections.reverseOrder());
		boolean any = false;
		for (int idx : uniqueSortedDesc)
		{
			if (idx >= 0 && idx < currentDraft.getOrder().size())
			{
				removeOrderLineAtIndexNoSave(idx);
				any = true;
			}
		}
		if (any)
		{
			saveDraftToConfig();
			if (worldMapRoutePreviewEnabled)
			{
				rebuildWorldMapRoutePoints();
			}
		}
	}

	public void removeRequirementAt(int index)
	{
		ensureDraftLoaded();
		if (index < 0 || index >= currentDraft.getRequirements().size())
		{
			return;
		}
		currentDraft.getRequirements().remove(index);
		saveDraftToConfig();
	}

	public void removeStepAt(ConstructStepKind kind, int index)
	{
		removeStepByKindAt(kind.stepKind(), index);
	}

	private void removeStepByKindAt(StepKind kind, int index)
	{
		ensureDraftLoaded();
		if (index < 0)
		{
			return;
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
				return;
			}
			filteredIndex++;
		}
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
	 * Pretty-printed QH canonical JSON (questHelperMaker snapshot plus minimal metadata).
	 */
	public String exportDraftJson()
	{
		return exportDraftJson(ExportFormat.QH_FORMAT);
	}

	public String exportDraftJson(ExportFormat format)
	{
		ensureDraftLoaded();
		reconcileOrderSlotRoutingAttachments();
		if (format == ExportFormat.LEAGUE_ROUTE)
		{
			return prettyDraftGson.toJson(TasksTrackerRouteExporter.export(currentDraft, false));
		}
		return prettyDraftGson.toJson(buildQhCanonicalDocument());
	}

	/**
	 * Replace the current draft from JSON (e.g. pasted or read from a file). Persists to the maker draft file on success.
	 */
	public ImportDraftResult importDraftFromJson(String json)
	{
		return importFromJson(json, ImportMode.FULL_FRESH);
	}

	public ImportDraftResult importItemRequirementsFromJson(String json)
	{
		ensureDraftLoaded();
		if (json == null || json.isBlank())
		{
			return ImportDraftResult.failure("JSON is empty");
		}
		JsonObject root;
		try
		{
			root = gson.fromJson(json.trim(), JsonObject.class);
		}
		catch (RuntimeException ex)
		{
			return ImportDraftResult.failure(ex.getMessage());
		}
		JsonElement maker = root == null ? null : root.get("questHelperMaker");
		if (maker == null || !maker.isJsonObject())
		{
			return ImportDraftResult.failure("Item data import requires QH canonical JSON with questHelperMaker.");
		}

		MakerDraftJsonLoader.LoadOutcome outcome = MakerDraftJsonLoader.loadDraftFromJson(json, gson);
		if (!outcome.isSuccess())
		{
			return ImportDraftResult.failure(outcome.getErrorMessage());
		}
		DraftHelper incoming = outcome.getDraft();
		if (incoming == null)
		{
			return ImportDraftResult.failure("Could not parse incoming draft.");
		}

		int added = 0;
		int reused = 0;
		for (DraftRequirement req : incoming.getRequirements())
		{
			if (req == null)
			{
				continue;
			}
			List<Integer> ids = DraftRoutingIds.dedupeIntsPreserveOrder(DraftRoutingIds.mergedStepOrRequirementIds(req.getRawId(), req.getAlternateRawIds()));
			if (ids.isEmpty())
			{
				continue;
			}
			String key = requirementKey(ids);
			DraftRequirement existing = null;
			for (DraftRequirement cur : currentDraft.getRequirements())
			{
				List<Integer> curIds = DraftRoutingIds.dedupeIntsPreserveOrder(DraftRoutingIds.mergedStepOrRequirementIds(cur.getRawId(), cur.getAlternateRawIds()));
				if (requirementKey(curIds).equals(key))
				{
					existing = cur;
					break;
				}
			}
			if (existing != null)
			{
				reused++;
				if ((existing.getDisplayName() == null || existing.getDisplayName().isBlank())
					&& req.getDisplayName() != null && !req.getDisplayName().isBlank())
				{
					existing.setDisplayName(req.getDisplayName().trim());
				}
				continue;
			}
			DraftRequirement add = new DraftRequirement();
			add.setRawId(ids.get(0));
			add.setDisplayName(req.getDisplayName() == null ? "" : req.getDisplayName().trim());
			add.getAlternateRawIds().clear();
			for (int i = 1; i < ids.size(); i++)
			{
				add.getAlternateRawIds().add(ids.get(i));
			}
			currentDraft.getRequirements().add(add);
			added++;
		}

		saveDraftToConfig();
		return ImportDraftResult.ok(String.format("Item data import complete. Added: %d, reused: %d.", added, reused));
	}

	private static String requirementKey(List<Integer> ids)
	{
		if (ids == null || ids.isEmpty())
		{
			return "";
		}
		int[] arr = new int[ids.size()];
		for (int i = 0; i < ids.size(); i++)
		{
			arr[i] = ids.get(i);
		}
		Arrays.sort(arr);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < arr.length; i++)
		{
			if (i > 0)
			{
				sb.append(',');
			}
			sb.append(arr[i]);
		}
		return sb.toString();
	}

	/**
	 * Same document as {@link #exportDraftJson()}: extended Tasks Tracker route JSON with {@code questHelperMaker}.
	 */
	public String exportTasksTrackerRouteJson()
	{
		return exportDraftJson(ExportFormat.LEAGUE_ROUTE);
	}

	/**
	 * Replace the maker draft from extended Tasks Tracker route JSON: optional {@code questHelperMaker}
	 * for a full snapshot, otherwise {@code taskId} / {@code note} / {@code location} / {@code interact} only.
	 */
	public ImportDraftResult importTasksTrackerRouteFromJson(String routeJson)
	{
		return importFromJson(routeJson, ImportMode.FULL_FRESH);
	}

	public ImportDraftResult importFromJson(String json, ImportMode mode)
	{
		ensureDraftLoaded();
		if (mode == ImportMode.FULL_FRESH)
		{
			MakerDraftJsonLoader.LoadOutcome snapshotOutcome = MakerDraftJsonLoader.loadDraftFromJson(json, gson);
			if (snapshotOutcome.isSuccess())
			{
				currentDraft = snapshotOutcome.getDraft();
				reconcileOrderSlotRoutingAttachments();
				saveDraftToConfig();
				rebuildWorldMapRouteIfEnabled();
				return ImportDraftResult.ok("Imported qh format (full fresh).");
			}
		}
		AdaptedImport adapted = MakerImportFormatAdapter.adapt(json, gson);
		if (!adapted.isSuccess())
		{
			return ImportDraftResult.failure(adapted.getErrorMessage());
		}
		TasksTrackerRouteDto route = adapted.getRoute();
		Map<Integer, JsonObject> hubByStructId = adapted.getHubByStructId();

		switch (mode)
		{
			case FULL_FRESH:
				return importFullFresh(json, adapted, route, hubByStructId);
			case ORDER_ONLY:
				return importOrderOnly(route, hubByStructId, adapted.getSourceFormat());
			case DATA_ONLY:
				return importDataOnly(route, hubByStructId, adapted.getSourceFormat());
			default:
				return ImportDraftResult.failure("Unsupported import mode");
		}
	}

	/**
	 * Merge imported route order into the current draft:
	 * reuses existing steps by task/custom ids, creates missing ones, and replaces current quest order.
	 */
	public ImportDraftResult mergeOrderFromJson(String json)
	{
		return importFromJson(json, ImportMode.ORDER_ONLY);
	}

	private ImportDraftResult importFullFresh(
		String rawJson,
		AdaptedImport adapted,
		TasksTrackerRouteDto route,
		Map<Integer, JsonObject> hubByStructId)
	{
		DraftHelper nextDraft;
		if (adapted.getSourceFormat() == ImportSourceFormat.QH_EXTENDED_ROUTE
			&& ConstructDraftPersistence.isSupportedMakerSnapshot(route.getQuestHelperMaker()))
		{
			MakerDraftJsonLoader.LoadOutcome outcome = MakerDraftJsonLoader.loadDraftFromJson(rawJson, gson);
			if (!outcome.isSuccess())
			{
				return ImportDraftResult.failure(outcome.getErrorMessage());
			}
			nextDraft = outcome.getDraft();
		}
		else
		{
			nextDraft = TasksTrackerRouteImporter.importRoute(route, hubByStructId);
		}
		currentDraft = nextDraft;
		reconcileOrderSlotRoutingAttachments();
		saveDraftToConfig();
		rebuildWorldMapRouteIfEnabled();
		return ImportDraftResult.ok("Imported " + adapted.getSourceFormat().name().toLowerCase(Locale.ROOT).replace('_', ' ') + " (full fresh).");
	}

	private ImportDraftResult importOrderOnly(
		TasksTrackerRouteDto route,
		Map<Integer, JsonObject> hubByStructId,
		ImportSourceFormat sourceFormat)
	{
		MergeStats stats = mergeRouteOrderIntoCurrentDraft(route, hubByStructId);
		reconcileOrderSlotRoutingAttachments();
		saveDraftToConfig();
		rebuildWorldMapRouteIfEnabled();
		return ImportDraftResult.ok(buildMergeMessage("Order import", sourceFormat, stats));
	}

	private ImportDraftResult importDataOnly(
		TasksTrackerRouteDto route,
		Map<Integer, JsonObject> hubByStructId,
		ImportSourceFormat sourceFormat)
	{
		MergeStats stats = mergeRouteDataIntoCurrentDraft(route, hubByStructId);
		reconcileOrderSlotRoutingAttachments();
		saveDraftToConfig();
		rebuildWorldMapRouteIfEnabled();
		return ImportDraftResult.ok(buildMergeMessage("Data import", sourceFormat, stats));
	}

	private MergeStats mergeRouteOrderIntoCurrentDraft(TasksTrackerRouteDto route, Map<Integer, JsonObject> hubByStructId)
	{
		MergeStats stats = new MergeStats();
		List<DraftOrderLine> mergedOrder = new ArrayList<>();
		List<RouteSectionDto> sections = route == null || route.getSections() == null
			? List.of()
			: route.getSections();
		for (int si = 0; si < sections.size(); si++)
		{
			RouteSectionDto sec = sections.get(si);
			List<RouteItemDto> items = sec != null && sec.getItems() != null ? sec.getItems() : List.of();
			if (si > 0 || items.isEmpty())
			{
				mergedOrder.add(TasksTrackerRouteImporter.newSectionDivider(sec == null ? null : sec.getName()));
			}
			for (RouteItemDto item : items)
			{
				if (item == null)
				{
					continue;
				}
				DraftStep step = findOrCreateMergeStep(item, hubByStructId, stats);
				if (step == null || step.getStepId() == null || step.getStepId().isBlank())
				{
					continue;
				}
				String routingText = item.getTaskId() != null
					? truncateForRouting(step.getInstructionText())
					: truncateForRouting(customItemLabel(item.getCustomItem()));
				mergedOrder.add(TasksTrackerRouteImporter.newOrderRefLine(step.getStepId(), routingText));
				stats.orderLines++;
			}
		}
		currentDraft.getOrder().clear();
		currentDraft.getOrder().addAll(mergedOrder);
		return stats;
	}

	private MergeStats mergeRouteDataIntoCurrentDraft(TasksTrackerRouteDto route, Map<Integer, JsonObject> hubByStructId)
	{
		MergeStats stats = new MergeStats();
		List<RouteSectionDto> sections = route == null || route.getSections() == null ? List.of() : route.getSections();
		for (RouteSectionDto section : sections)
		{
			List<RouteItemDto> items = section != null && section.getItems() != null ? section.getItems() : List.of();
			for (RouteItemDto item : items)
			{
				findOrCreateMergeStep(item, hubByStructId, stats);
			}
		}
		return stats;
	}

	private DraftStep findOrCreateMergeStep(RouteItemDto item, Map<Integer, JsonObject> hubByStructId, MergeStats stats)
	{
		Integer taskId = item.getTaskId();
		if (taskId != null)
		{
			DraftStep existing = findStepByTaskId(taskId);
			if (existing != null)
			{
				stats.reused++;
				return existing;
			}
			DraftStep created = TasksTrackerRouteImporter.newLeagueTaskStep(item, hubByStructId);
			currentDraft.getStepDefinitions().add(created);
			stats.added++;
			return created;
		}
		RouteCustomItemDto customItem = item.getCustomItem();
		if (customItem == null)
		{
			return null;
		}
		String customId = customItem.getId() == null ? "" : customItem.getId().trim();
		if (customId.isBlank())
		{
			return null;
		}
		DraftStep existing = findDefinitionByStepId(customId);
		if (existing != null)
		{
			stats.reused++;
			return existing;
		}
		DraftStep created = TasksTrackerRouteImporter.newCustomItemStep(item, customId);
		currentDraft.getStepDefinitions().add(created);
		stats.added++;
		return created;
	}

	private String buildMergeMessage(String label, ImportSourceFormat sourceFormat, MergeStats stats)
	{
		String source = sourceFormat == null ? "unknown source" : sourceFormat.name().toLowerCase(Locale.ROOT).replace('_', ' ');
		return String.format("%s complete from %s. Added: %d, reused: %d, order lines: %d.",
			label,
			source,
			stats.added,
			stats.reused,
			stats.orderLines);
	}

	private static final class MergeStats
	{
		private int added;
		private int reused;
		private int orderLines;
	}

	private DraftStep findStepByTaskId(int taskId)
	{
		for (DraftStep step : currentDraft.getStepDefinitions())
		{
			if (step != null && step.getStructId() != null && step.getStructId() == taskId)
			{
				return step;
			}
		}
		return findDefinitionByStepId(String.valueOf(taskId));
	}

	private static String customItemLabel(RouteCustomItemDto customItem)
	{
		if (customItem == null || customItem.getLabel() == null)
		{
			return "";
		}
		return customItem.getLabel();
	}

	private static String truncateForRouting(String text)
	{
		if (text == null)
		{
			return "";
		}
		return text.length() <= 120 ? text : text.substring(0, 120);
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
			MakerDraftFileStore.writeUtf8(file, gson.toJson(buildQhCanonicalDocument()));
			notifyDraftChanged();
		}
		catch (IOException e)
		{
			log.warn("Could not save Quest Helper Maker draft to {}", file.getAbsolutePath(), e);
		}
	}

	private JsonObject buildQhCanonicalDocument()
	{
		JsonObject root = new JsonObject();
		root.addProperty("name", normalizeText(currentDraft.getQuestName()).isBlank() ? "Generated Quest" : currentDraft.getQuestName());
		root.addProperty("taskType", "LEAGUE_5");
		root.addProperty("author", "Quest Helper Maker");
		root.add("questHelperMaker", gson.toJsonTree(ConstructDraftPersistence.toDraftState(currentDraft)));
		return root;
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

	private static final int STEP_PICK_LABEL_MAX_LEN = 96;

	private static String truncatePickListLine(String s)
	{
		if (s == null)
		{
			return "";
		}
		String t = s.trim();
		if (t.length() <= STEP_PICK_LABEL_MAX_LEN)
		{
			return t;
		}
		return t.substring(0, STEP_PICK_LABEL_MAX_LEN - 1) + "…";
	}

	private void appendPickFilterChunk(StringBuilder sb, String chunk)
	{
		String t = normalizeText(chunk);
		if (t.isEmpty())
		{
			return;
		}
		if (sb.length() > 0)
		{
			sb.append(' ');
		}
		sb.append(t);
	}

	/** One-line label for the Add Step pick list: instruction and human-readable fields only (no ids, coords, or kind). */
	private String formatStepDefinitionPickLabel(DraftStep step, int displayIndex)
	{
		if (step.isSectionDivider())
		{
			String name = step.getSuggestedVarName() == null || step.getSuggestedVarName().isBlank()
				? DEFAULT_SECTION_NAME
				: normalizeText(step.getSuggestedVarName());
			String condition = step.getSectionCondition() == null ? "" : normalizeText(step.getSectionCondition());
			String mode = step.isSkipWhenConditionMet() ? "skip when met" : "show when met";
			if (condition.isEmpty())
			{
				return truncatePickListLine("Section: " + name + " (" + mode + ")");
			}
			return truncatePickListLine("Section: " + name + " (" + mode + ") — " + condition);
		}
		String ins = normalizeText(step.getInstructionText());
		if (!ins.isBlank())
		{
			return truncatePickListLine(ins.replace('\n', ' ').replace('\r', ' '));
		}
		if (step.getKind() == StepKind.TEXT)
		{
			return "Generic step";
		}
		String opt = normalizeText(step.getOption());
		String target = normalizeText(step.getTargetText());
		StringBuilder b = new StringBuilder();
		if (!opt.isBlank())
		{
			b.append(opt);
		}
		if (!target.isBlank())
		{
			if (b.length() > 0)
			{
				b.append(' ');
			}
			b.append(target);
		}
		String ot = b.toString().trim();
		if (!ot.isBlank())
		{
			return truncatePickListLine(ot);
		}
		String var = normalizeText(step.getSuggestedVarName());
		if (!var.isBlank())
		{
			return truncatePickListLine(var);
		}
		return "Step " + displayIndex;
	}

	/** Text used to filter the Add Step list (includes ids for numeric search, but not shown in the list). */
	private String formatStepDefinitionPickFilterText(DraftStep step)
	{
		StringBuilder sb = new StringBuilder();
		appendPickFilterChunk(sb, step.getInstructionText());
		appendPickFilterChunk(sb, step.getSuggestedVarName());
		appendPickFilterChunk(sb, step.getTargetText());
		appendPickFilterChunk(sb, step.getOption());
		appendPickFilterChunk(sb, step.getSectionCondition());
		if (step.getKind() != null)
		{
			appendPickFilterChunk(sb, step.getKind().name());
		}
		if (step.getKind() == StepKind.NPC || step.getKind() == StepKind.OBJECT)
		{
			String ids = DraftRoutingIds.formatCsvIds(DraftRoutingIds.mergedStepOrRequirementIds(step.getRawId(), step.getAlternateRawIds()));
			appendPickFilterChunk(sb, ids);
		}
		return sb.toString().trim();
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
		List<DraftStep> steps = getVisibleOrderedRouteSteps();
		for (int i = 0; i < steps.size(); i++)
		{
			DraftStep step = steps.get(i);
			WorldPoint wp = step.getWorldPoint();
			if (wp == null || !isWithinMapBounds(wp))
			{
				continue;
			}
			String label = "(" + (i + 1) + ") " + displayStepName(step);
			ConstructWorldMapPoint point = new ConstructWorldMapPoint(wp, routePointIcon(i, steps.size()), label);
			worldMapPointManager.add(point);
		}
	}

	private List<DraftStep> getVisibleOrderedRouteSteps()
	{
		List<DraftStep> steps = expandOrderToDefinitionsInOrder();
		int visibleCount = visibleRouteStepCount(steps.size());
		if (visibleCount >= steps.size())
		{
			return steps;
		}
		return new ArrayList<>(steps.subList(0, visibleCount));
	}

	private int visibleRouteStepCount(int totalSteps)
	{
		if (totalSteps <= 0)
		{
			return 0;
		}
		int clampedPercent = clampWorldMapRouteRevealPercent(worldMapRouteRevealPercent);
		if (clampedPercent >= 100)
		{
			return totalSteps;
		}
		if (clampedPercent <= 0)
		{
			return 1;
		}
		int roundedUp = (clampedPercent * totalSteps + 99) / 100;
		return Math.max(1, Math.min(totalSteps, roundedUp));
	}

	private static int clampWorldMapRouteRevealPercent(int percent)
	{
		return Math.max(0, Math.min(100, percent));
	}

	private static int percentForVisibleStepCount(int visibleSteps, int totalSteps)
	{
		if (totalSteps <= 0)
		{
			return 0;
		}
		int clampedVisible = Math.max(1, Math.min(totalSteps, visibleSteps));
		if (clampedVisible >= totalSteps)
		{
			return 100;
		}
		if (clampedVisible <= 1)
		{
			return 0;
		}
		return Math.max(0, Math.min(99, ((clampedVisible - 1) * 100) / totalSteps));
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

	private static int itemAttachmentQuantityOrOne(DraftStepAttachedRequirement a)
	{
		if (a == null)
		{
			return 1;
		}
		String k = a.getKind() == null ? StepAttachmentKind.ITEM.name() : a.getKind();
		if (!StepAttachmentKind.ITEM.name().equalsIgnoreCase(k))
		{
			return 1;
		}
		int q = a.getItemQuantity();
		return Math.max(q, 1);
	}

	private class PreviewQuestHelper extends ComplexStateQuestHelper
	{
		private final DraftHelper draft;
		private final List<ItemRequirement> previewRequirements = new ArrayList<>();
		private final List<PanelDetails> previewPanels = new ArrayList<>();
		private final List<ManualRequirement> manualStepRequirements = new ArrayList<>();
		private final List<String> manualStepSkipPersistenceKeys = new ArrayList<>();
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
				List<Integer> ids = new ArrayList<>();
				for (Integer id : DraftRoutingIds.mergedStepOrRequirementIds(requirement.getRawId(), requirement.getAlternateRawIds()))
				{
					if (id != null && id > 0)
					{
						ids.add(id);
					}
				}
				if (ids.isEmpty())
				{
					continue;
				}
				if (ids.size() <= 1)
				{
					previewRequirements.add(new ItemRequirement(display, ids.get(0)));
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
			manualStepSkipPersistenceKeys.clear();
			Map<Integer, ItemRequirement> requirementById = new HashMap<>();
			for (ItemRequirement req : previewRequirements)
			{
				for (int id : req.getAllIds())
				{
					requirementById.put(id, req);
				}
			}

			List<ConditionalStep> sectionTasks = new ArrayList<>();
			List<Requirement> sectionCompletionRequirements = new ArrayList<>();
			List<PreviewStepEntry> currentSectionEntries = new ArrayList<>();
			String currentPanelName = "Captured Steps";

			for (DraftOrderLine orderLine : draft.getOrder())
			{
				if (orderLine.isSectionDivider())
				{
					ConditionalStep sectionTask = createSectionTask(currentSectionEntries);
					if (sectionTask != null)
					{
						sectionCompletionRequirements.add(buildSectionCompletionRequirement(currentSectionEntries, sectionTask));
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
					sectionCompletionRequirements.add(buildSectionCompletionRequirement(currentSectionEntries, sectionTask));
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

			List<Requirement> priorSectionCompletionRequirements = new ArrayList<>();
			for (int i = 0; i < lastSectionIndex; i++)
			{
				Requirement currentSectionIncomplete = not(sectionCompletionRequirements.get(i));
				Requirement sectionGate = priorSectionCompletionRequirements.isEmpty()
					? currentSectionIncomplete
					: and(and(priorSectionCompletionRequirements.toArray(new Requirement[0])), currentSectionIncomplete);
				allSections.addStep(sectionGate, sectionTasks.get(i));
				priorSectionCompletionRequirements.add(sectionCompletionRequirements.get(i));
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

		private Requirement buildSectionCompletionRequirement(List<PreviewStepEntry> sectionEntries, QuestStep sectionTask)
		{
			LinkedHashSet<Requirement> requirements = new LinkedHashSet<>();
			for (PreviewStepEntry entry : sectionEntries)
			{
				requirements.add(entry.completionRequirement);
			}
			Requirement stepCompletion = and(requirements.toArray(new Requirement[0]));
			if (sectionTask == null)
			{
				return stepCompletion;
			}
			return or(stepCompletion, new PreviewSectionLockedRequirement(sectionTask));
		}

		private class PreviewSectionLockedRequirement implements Requirement
		{
			private final QuestStep sectionTask;

			private PreviewSectionLockedRequirement(QuestStep sectionTask)
			{
				this.sectionTask = sectionTask;
			}

			@Override
			public boolean check(Client client)
			{
				return sectionTask.isLocked();
			}

			@Override
			public @NotNull String getDisplayText()
			{
				return "Section marked complete";
			}
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
				if (StepAttachmentKind.SKILL.name().equalsIgnoreCase(k))
				{
					Skill skill = parseSkillName(a.getSkillName());
					int level = a.getSkillRequiredLevel() == null ? 1 : Math.max(1, a.getSkillRequiredLevel());
					Operation op = normalizeOperationName(a.getSkillOperation());
					String display = a.getSkillDisplayText();
					SkillRequirement sr;
					if (op == Operation.GREATER_EQUAL)
					{
						sr = (display != null && !display.isBlank())
							? new SkillRequirement(skill, level, a.isSkillCanBeBoosted(), display)
							: new SkillRequirement(skill, level, a.isSkillCanBeBoosted());
					}
					else
					{
						sr = new SkillRequirement(skill, level, op);
					}
					reqs.add(sr);
					continue;
				}
				Integer id = a.getItemRawId();
				if (id == null || id <= 0)
				{
					continue;
				}
				ItemRequirement ir = requirementById.get(id);
				ItemRequirement base = ir != null ? ir : new ItemRequirement("Item " + id, id);
				int q = itemAttachmentQuantityOrOne(a);
				ItemRequirement withQty = q > 1 ? base.quantity(q) : base;
				reqs.add(a.isAttachmentHighlighted() ? withQty.highlighted() : withQty);
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
			ensureOrderSlotId(orderLine);
			String persistKey = orderLine.getOrderSlotId();
			if (persistKey == null || persistKey.isBlank())
			{
				persistKey = draftStep.getStepId() != null && !draftStep.getStepId().isBlank()
					? draftStep.getStepId()
					: UUID.randomUUID().toString();
			}
			manualStepSkipPersistenceKeys.add(persistKey);
			step.setSidebarManualSkipRequirement(manualOverride);
			step.setSidebarManualSkipPersistenceKey(persistKey);
			Requirement completion;
			Requirement branchRequirement;
			DraftOrderStepRequirement tree = orderLine.getStepRequirement();
			OrderConditionMode conditionMode = OrderStepRequirementSupport.effectiveConditionMode(orderLine);
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
					if (conditionMode == OrderConditionMode.CONTINUE_WHEN_TRUE)
					{
						completion = or(selector, manualOverride);
						branchRequirement = and(not(selector), not(manualOverride));
					}
					else
					{
						completion = or(not(selector), manualOverride);
						branchRequirement = and(selector, not(manualOverride));
					}
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
			// When a conditions tree exists, it is the sole routing source for this order row.
			if (orderLine.getStepRequirement() != null)
			{
				return null;
			}
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
			if (fromTree != null && fromTree > 0)
			{
				return fromTree;
			}
			if (orderLine.getStepRequirement() != null)
			{
				return null;
			}
			Integer orderLinkedOverride = orderLine.getLinkedRequirementRawId();
			if (orderLinkedOverride != null && orderLinkedOverride > 0)
			{
				return orderLinkedOverride;
			}
			if (def.getKind() == StepKind.ITEM)
			{
				return def.getRawId() > 0 ? def.getRawId() : null;
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
			Map<String, Boolean> persisted = ManualStepSkipStore.load(configManager, gson, getDisplayedQuestName());
			for (int i = 0; i < manualStepRequirements.size(); i++)
			{
				String pk = i < manualStepSkipPersistenceKeys.size() ? manualStepSkipPersistenceKeys.get(i) : "";
				boolean fromPersist = pk != null && !pk.isBlank() && Boolean.TRUE.equals(persisted.get(pk));
				manualStepRequirements.get(i).setShouldPass(i < previewProgressIndex || fromPersist);
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

		private void stepLeft()
		{
			if (!canStepLeft())
			{
				return;
			}
			previewProgressIndex--;
			syncManualProgress();
		}

		private void stepRight()
		{
			if (!canStepRight())
			{
				return;
			}
			previewProgressIndex++;
			syncManualProgress();
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

		@Override
		protected void onManualSidebarSkipsPersistedChanged()
		{
			syncManualProgress();
			QuestStep cur = getCurrentStep();
			if (cur instanceof ConditionalStep)
			{
				((ConditionalStep) cur).refreshAfterRequirementChangeDeep();
			}
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
	@Getter
	public static final class StepAttachmentPickOption
	{
		private final String label;
		private final StepAttachmentEdit edit;

		public StepAttachmentPickOption(String label, StepAttachmentEdit edit)
		{
			this.label = label;
			this.edit = edit;
		}
	}

	/** UI / API DTO for extra requirements on a step (items, varbits, extensible kinds). */
	@Getter
	public static final class StepAttachmentEdit
	{
		private final String kind;
		private final Integer itemRawId;
		private final Integer varbitId;
		private final Integer varbitRequiredValue;
		private final String varbitOperation;
		private final String varbitDisplayText;
		private final String skillName;
		private final Integer skillRequiredLevel;
		private final String skillOperation;
		private final String skillDisplayText;
		private final boolean skillCanBeBoosted;
		@Setter
		private boolean itemHighlighted;
		@Setter
		private int itemQuantity;
		private final String orderSlotId;

		private StepAttachmentEdit(
			String kind,
			Integer itemRawId,
			Integer varbitId,
			Integer varbitRequiredValue,
			String varbitOperation,
			String varbitDisplayText,
			String skillName,
			Integer skillRequiredLevel,
			String skillOperation,
			String skillDisplayText,
			boolean skillCanBeBoosted,
			boolean itemHighlighted,
			int itemQuantity,
			String orderSlotId)
		{
			this.kind = kind;
			this.itemRawId = itemRawId;
			this.varbitId = varbitId;
			this.varbitRequiredValue = varbitRequiredValue;
			this.varbitOperation = varbitOperation;
			this.varbitDisplayText = varbitDisplayText;
			this.skillName = skillName;
			this.skillRequiredLevel = skillRequiredLevel;
			this.skillOperation = skillOperation;
			this.skillDisplayText = skillDisplayText;
			this.skillCanBeBoosted = skillCanBeBoosted;
			this.itemHighlighted = itemHighlighted;
			this.itemQuantity = Math.max(itemQuantity, 1);
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
			if (StepAttachmentKind.SKILL.name().equalsIgnoreCase(o.getKind()))
			{
				return skill(o.getSkillName(), o.getSkillRequiredLevel(), o.getSkillOperation(), o.getSkillDisplayText(), o.isSkillCanBeBoosted());
			}
			if (o.getItemRawId() == null)
			{
				return null;
			}
			return item(o.getItemRawId(), o.isItemHighlighted(), o.getItemQuantity());
		}

		public static StepAttachmentEdit item(int rawId)
		{
			return item(rawId, false);
		}

		public static StepAttachmentEdit item(int rawId, boolean highlighted)
		{
			return item(rawId, highlighted, 1);
		}

		public static StepAttachmentEdit item(int rawId, boolean highlighted, int quantity)
		{
			int q = Math.max(quantity, 1);
			return new StepAttachmentEdit(StepAttachmentKind.ITEM.name(), rawId, null, null, null, null, null, null, null, null, false, highlighted, q, null);
		}

		public static StepAttachmentEdit varbit(int varbitId, int requiredValue, String operation, String displayText)
		{
			String op = operation == null || operation.isBlank() ? "EQUAL" : operation.trim();
			return new StepAttachmentEdit(StepAttachmentKind.VARBIT.name(), null, varbitId, requiredValue, op, displayText, null, null, null, null, false, false, 1, null);
		}

		public static StepAttachmentEdit varbitForOrderSlot(String orderSlotId, int varbitId, int requiredValue, String operation, String displayText)
		{
			String op = operation == null || operation.isBlank() ? "EQUAL" : operation.trim();
			return new StepAttachmentEdit(StepAttachmentKind.VARBIT.name(), null, varbitId, requiredValue, op, displayText, null, null, null, null, false, false, 1, orderSlotId);
		}

		public static StepAttachmentEdit skill(String skillName, Integer requiredLevel, String operation, String displayText, boolean canBeBoosted)
		{
			Skill skill = parseSkillName(skillName);
			int level = requiredLevel == null ? 1 : Math.max(1, requiredLevel);
			String op = normalizeOperationName(operation).name();
			return new StepAttachmentEdit(StepAttachmentKind.SKILL.name(), null, null, null, null, null,
				skill.name(), level, op, displayText, canBeBoosted, false, 1, null);
		}
	}

	@Getter
	public static final class StepDefinitionPickOption
	{
		private final String stepId;
		private final String label;
		/** Extra tokens for search (not shown in the list); may include numeric ids. */
		private final String filterText;

		public StepDefinitionPickOption(String stepId, String label, String filterText)
		{
			this.stepId = stepId;
			this.label = label == null ? "" : label;
			this.filterText = filterText == null || filterText.isBlank() ? this.label : filterText;
		}

		@Override
		public String toString()
		{
			return label;
		}
	}

	@Getter
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
		/**
		 * -- GETTER --
		 * Player-facing step text used in generated helpers (empty for section rows).
		 */
		private final String instructionText;
		private final boolean leagueStep;
		private final OrderConditionMode orderConditionMode;
		private final boolean customOrderStepRequirement;
		private final boolean hasOrderStepRequirementTree;

		public CombinedStepRow(int index, String orderSlotId, String stepId, String varName, String summary, boolean sectionDivider, String sectionCondition, boolean skipWhenConditionMet, Integer orderLinkedRequirementRawId, String instructionText, boolean leagueStep, OrderConditionMode orderConditionMode, boolean customOrderStepRequirement, boolean hasOrderStepRequirementTree)
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
			this.leagueStep = leagueStep;
			this.orderConditionMode = orderConditionMode == null ? OrderConditionMode.SHOW_WHEN_TRUE : orderConditionMode;
			this.customOrderStepRequirement = customOrderStepRequirement;
			this.hasOrderStepRequirementTree = hasOrderStepRequirementTree;
		}

		public boolean hasOrderStepRequirementTree()
		{
			return hasOrderStepRequirementTree;
		}
	}
}
