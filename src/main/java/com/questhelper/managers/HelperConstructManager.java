package com.questhelper.managers;

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
import net.runelite.client.util.Text;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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

	@Inject
	private Client client;

	@Inject
	private QuestHelperConfig config;

	@Inject
	private HelperScaffoldGenerator scaffoldGenerator;

	@Inject
	@Named("developerMode")
	private boolean developerMode;

	@Getter
	private DraftHelper currentDraft = new DraftHelper();

	public void setupConstructMenuOptions(MenuEntryAdded event)
	{
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

		if (!menuEntryExists(menuEntries, MENU_PREFIX + " Start New Draft"))
		{
			addAction(menuEntries, MENU_PREFIX + " Start New Draft", "Quest Helper Dev", () -> startNewDraft(target));

		}
		if (isNpcAction(sourceType))
		{
			addAction(menuEntries, MENU_PREFIX + " Add NPC Step", target, () -> addStep(StepKind.NPC, rawId, option, target, clickedWorldPoint));
		}
		if (isObjectAction(sourceType))
		{
			addAction(menuEntries, MENU_PREFIX + " Add Object Step", target, () -> addStep(StepKind.OBJECT, rawId, option, target, clickedWorldPoint));
		}
		if (isItemAction(sourceType))
		{
			addAction(menuEntries, MENU_PREFIX + " Add Item Requirement", target, () -> addRequirement(rawId, target));
		}
		if (!menuEntryExists(menuEntries, MENU_PREFIX + " Build Scaffold To Clipboard"))
		{
			addAction(menuEntries, MENU_PREFIX + " Build Scaffold To Clipboard", "Quest Helper Dev", this::buildToClipboard);
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
		currentDraft = helper;
		sendGameMessage("Quest Helper Construct: started new draft '" + currentDraft.getClassName() + "'.");
	}

	private void addStep(StepKind kind, int rawId, String option, String target, WorldPoint clickedWorldPoint)
	{
		DraftStep step = new DraftStep();
		step.setKind(kind);
		step.setRawId(rawId);
		step.setOption(option);
		step.setTargetText(target);
		step.setInstructionText(instructionText(option, target));
		step.setPanelName("Captured Steps");
		step.setSuggestedVarName(HelperScaffoldGenerator.toVarName(option + " " + target, "step"));
		step.setWorldPoint(clickedWorldPoint);
		currentDraft.getSteps().add(step);
		sendGameMessage("Quest Helper Construct: added " + kind.name().toLowerCase(Locale.ROOT) + " step (" + rawId + ") at " + formatWorldPoint(clickedWorldPoint) + ".");
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
		DraftRequirement requirement = new DraftRequirement();
		requirement.setRawId(rawId);
		requirement.setDisplayName(normalizeText(target).isBlank() ? "Captured Item" : normalizeText(target));
		currentDraft.getRequirements().add(requirement);
		sendGameMessage("Quest Helper Construct: added item requirement (" + rawId + ").");
	}

	private void buildToClipboard()
	{
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

	private boolean isNpcAction(MenuAction menuAction)
	{
		return menuAction == MenuAction.EXAMINE_NPC;
	}

	private boolean isObjectAction(MenuAction menuAction)
	{
		return menuAction == MenuAction.EXAMINE_OBJECT;
	}

	private boolean isItemAction(MenuAction menuAction)
	{
		return menuAction == MenuAction.EXAMINE_ITEM_GROUND;
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
		return out.isEmpty() ? "GeneratedQuest" : out.toString();
	}

	private void sendGameMessage(String message)
	{
		client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", message, null);
	}

	private String formatWorldPoint(WorldPoint point)
	{
		if (point == null)
		{
			return "(unknown)";
		}
		return "(" + point.getX() + ", " + point.getY() + ", " + point.getPlane() + ")";
	}
}
