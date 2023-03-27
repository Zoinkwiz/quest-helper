package com.questhelper.steps.playermadesteps;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Actor;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.NPC;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.SpriteID;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.game.chatbox.ChatboxPanelManager;
import net.runelite.client.ui.overlay.OverlayUtil;

// This will hold all RuneliteObjects
// Can group up to helpers to have specific helpers remove all things themselves
// Can also when closing quest helper remove all
@Slf4j
@Singleton
public class RuneliteObjectManager
{
	protected final Client client;
	protected final EventBus eventBus;
	protected final ChatboxPanelManager chatboxPanelManager;
	protected final ClientThread clientThread;
	protected final ChatMessageManager chatMessageManager;
	protected final SpriteManager spriteManager;

	protected final Map<String, RuneliteNpc> npcs = new HashMap<>();

	// Red Click
	Point clickPos;
	int clickAnimationFrame = 0;
	int bufferAnimation = 0;
	int[] redClick = new int[]{SpriteID.RED_CLICK_ANIMATION_1, SpriteID.RED_CLICK_ANIMATION_2, SpriteID.RED_CLICK_ANIMATION_3, SpriteID.RED_CLICK_ANIMATION_4};
	final int ANIMATION_PERIOD = 5;

	// Hiding NPCs
	// TODO: Need to be per object
	boolean playerBeenOnTile = false;
	boolean npcBeenOnTile = false;

	RuneliteNpc lastInteractedWithNpc;

	@Inject
	public RuneliteObjectManager(Client client, EventBus eventBus, ChatboxPanelManager chatboxPanelManager, ClientThread clientThread, ChatMessageManager chatMessageManager, SpriteManager spriteManager)
	{
		this.client = client;
		this.eventBus = eventBus;
		this.chatboxPanelManager = chatboxPanelManager;
		this.clientThread = clientThread;
		this.chatMessageManager = chatMessageManager;
		this.spriteManager = spriteManager;
	}

	public void startUp()
	{

	}

	public void shutDown()
	{
		// Need to close dialogs
		if (chatboxPanelManager.getCurrentInput() instanceof ChatBox) chatboxPanelManager.close();

		clientThread.invokeLater(this::removeRuneliteNpcs);
	}

	private void removeRuneliteNpcs()
	{
		npcs.forEach((type, npc) -> {
			npc.remove();
		});
		npcs.clear();
	}

	public RuneliteNpc createRuneliteNpc(String group, int[] model, WorldPoint wp, int animation)
	{
		RuneliteNpc runeliteNpc = new RuneliteNpc(client, clientThread, wp, model, animation);
		// Should this be here or a separate 'activate' step?
		runeliteNpc.getRuneliteObject().setActive(true);
		npcs.put(group, runeliteNpc);
		return runeliteNpc;
	}

	public void removeRuneliteNpc(RuneliteNpc npc)
	{
		// TODO: This should be conditional to remove from the Manager depending on a config of some kind
		if (npc != null)
		{
			npc.remove();
		}
	}

	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded event)
	{
		npcs.forEach((source, npc) -> {
			setupMenuOptions(npc, event);
		});
	}

	private void setupMenuOptions(RuneliteNpc npc, MenuEntryAdded event)
	{
		LocalPoint lp = LocalPoint.fromWorld(client, npc.getWorldPoint());

		int widgetIndex = event.getActionParam0();
		int widgetID = event.getActionParam1();
		MenuEntry[] menuEntries = client.getMenuEntries();

		if (npc.getRuneliteObject() != null && npc.getRuneliteObject().getModel() != null)
		{
			if (!hasClickedNpc(npc)) return;
			if (event.getOption().equals("Walk here"))
			{
				addExamine(npc, menuEntries, widgetIndex, widgetID);
				addTalkOption(npc, widgetIndex, widgetID);
			}

			if (lp != null && OBJECT_MENU_TYPES.contains(event.getMenuEntry().getType()))
			{
				updatePriorities(event, event.getActionParam0(), event.getActionParam1(), menuEntries, lp);
			}
			Actor actor = event.getMenuEntry().getActor();
			if (actor != null)
			{
				LocalPoint actorLp = actor.getLocalLocation();
				updatePriorities(event, actorLp.getSceneX(), actorLp.getSceneY(), menuEntries, lp);
			}
		}
	}

	public void setActive(RuneliteNpc npc)
	{
		LocalPoint lp = LocalPoint.fromWorld(client, npc.getWorldPoint());
		if (lp == null) return;

		npc.getRuneliteObject().setLocation(lp, client.getPlane());
		npc.getRuneliteObject().setActive(true);
	}

	public boolean isActive(RuneliteNpc runeliteNpc)
	{
		return runeliteNpc.getRuneliteObject().isActive();
	}

	public void remove(RuneliteNpc runeliteNpc)
	{
		runeliteNpc.getRuneliteObject().setActive(false);
	}

	public void remove(String runeliteNpcID)
	{
		RuneliteNpc npc = npcs.get(runeliteNpcID);
		npc.remove();
		npcs.remove(runeliteNpcID);
	}

	private static final List<MenuAction> OBJECT_MENU_TYPES = ImmutableList.of(
		MenuAction.GAME_OBJECT_FIRST_OPTION,
		MenuAction.GAME_OBJECT_SECOND_OPTION,
		MenuAction.GAME_OBJECT_THIRD_OPTION,
		MenuAction.GAME_OBJECT_FOURTH_OPTION,
		MenuAction.GAME_OBJECT_FIFTH_OPTION,
		MenuAction.WIDGET_TARGET_ON_GAME_OBJECT,

		MenuAction.GROUND_ITEM_FIRST_OPTION,
		MenuAction.GROUND_ITEM_SECOND_OPTION,
		MenuAction.GROUND_ITEM_THIRD_OPTION,
		MenuAction.GROUND_ITEM_FOURTH_OPTION,
		MenuAction.GROUND_ITEM_FIFTH_OPTION);

	private void updatePriorities(MenuEntryAdded event, int currentItemX, int currentItemY, MenuEntry[] menuEntries, LocalPoint runeliteObjectLp)
	{
		int cameraX = client.getCameraX();
		int cameraY = client.getCameraY();
		int playerX = client.getLocalPlayer().getLocalLocation().getX();
		int playerY = client.getLocalPlayer().getLocalLocation().getY();

		if ((cameraX - playerX + 15 > 0 && currentItemX < runeliteObjectLp.getSceneX()) // Facing west
			|| (cameraX - playerX + 15 < 0 && currentItemX > runeliteObjectLp.getSceneX()) // Facing east
			|| (cameraY - playerY - 15 > 0 && currentItemY < runeliteObjectLp.getSceneY()) // Facing south
			|| (cameraY - playerY - 15 < 0 && currentItemY > runeliteObjectLp.getSceneY())) // Facing north
		{
			event.getMenuEntry().setDeprioritized(true);
			for (MenuEntry menuEntry : menuEntries)
			{
				if (menuEntry.getType() == MenuAction.RUNELITE_HIGH_PRIORITY)
				{
					menuEntry.setDeprioritized(false);
				}
			}
		}
	}

	public void addExamine(RuneliteNpc npc, MenuEntry[] menuEntries, int widgetIndex, int widgetID)
	{
		if (!hasClickedNpc(npc)) return;

		// This is important for getting talk to be left-clickable, but don't fully understand why
		menuEntries = Arrays.copyOf(menuEntries, menuEntries.length + 1);

		client.createMenuEntry(menuEntries.length - 1)
			.setOption("Examine")
			.setTarget("<col=ffff00>" + npc.getName() + "</col>")
			.onClick(menuEntry -> {
				clickPos = client.getMouseCanvasPosition();
				clickAnimationFrame = 0;
				bufferAnimation = 0;

				String chatMessage = new ChatMessageBuilder()
					.append(ChatColorType.NORMAL)
					.append("The Cook's cousin, Vinny.")
					.build();

				chatMessageManager.queue(QueuedMessage.builder()
					.type(ChatMessageType.NPC_EXAMINE)
					.runeLiteFormattedMessage(chatMessage)
					.timestamp((int) (System.currentTimeMillis() / 1000))
					.build());
			})
			.setType(MenuAction.RUNELITE)
			.setParam0(widgetIndex)
			.setParam1(widgetID);
	}

	public void addTalkOption(RuneliteNpc runeliteNpc, int widgetIndex, int widgetID)
	{
		if (!hasClickedNpc(runeliteNpc)) return;

		client.createMenuEntry(-2)
			.setOption("Talk")
			.setTarget("<col=ffff00>" + runeliteNpc.getName() + "</col>")
			.setType(MenuAction.RUNELITE_HIGH_PRIORITY)
			.onClick((menuEntry -> {
				clickPos = client.getMouseCanvasPosition();

				WorldPoint wp = WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation());
				if (wp.distanceTo(runeliteNpc.getWorldPoint()) > 1)
				{
					String chatMessage = new ChatMessageBuilder()
						.append(ChatColorType.NORMAL)
						.append("You'll need to move closer to them to talk!")
						.build();

					chatMessageManager.queue(QueuedMessage.builder()
						.type(ChatMessageType.NPC_EXAMINE)
						.runeLiteFormattedMessage(chatMessage)
						.timestamp((int) (System.currentTimeMillis() / 1000))
						.build());
					return;
				}
				lastInteractedWithNpc = runeliteNpc;
				runeliteNpc.setupChatBox(chatboxPanelManager);
			}))
			.setParam0(widgetIndex)
			.setParam1(widgetID);
	}

	private boolean hasClickedNpc(RuneliteNpc runeliteNpc)
	{
		LocalPoint lp = LocalPoint.fromWorld(client, runeliteNpc.getWorldPoint());

		if (lp == null) return false;
		Shape clickbox = Perspective.getClickbox(client, runeliteNpc.getRuneliteObject().getModel(), runeliteNpc.getRuneliteObject().getOrientation(), lp.getX(), lp.getY(),
			Perspective.getTileHeight(client, lp, runeliteNpc.getWorldPoint().getPlane()));

		if (clickbox == null) return false;

		Point p = client.getMouseCanvasPosition();

		return clickbox.contains(p.getX(), p.getY());
	}

	public void makeWorldOverlayHint(Graphics2D graphics)
	{
		renderRedClick(graphics);

		WorldPoint playerPosition = WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation());
		if (lastInteractedWithNpc != null && playerPosition.distanceTo(lastInteractedWithNpc.getWorldPoint()) > 1 && chatboxPanelManager.getCurrentInput() instanceof ChatBox) chatboxPanelManager.close();
		npcs.forEach((group, npc) -> {
			hideIfNpcOnTile(npc);
			hideIfPlayerOnTile(npc, playerPosition);
		});
	}

	private void renderRedClick(Graphics2D graphics)
	{
		if (clickPos != null && clickAnimationFrame < 4)
		{
			BufferedImage img = spriteManager.getSprite(redClick[clickAnimationFrame], 0);
			if (img == null) return;
			Point point = new Point(clickPos.getX() - (img.getWidth() / 2), clickPos.getY() - (img.getHeight() / 2));
			OverlayUtil.renderImageLocation(graphics, point, img);
			bufferAnimation = Math.floorMod(bufferAnimation + 1, ANIMATION_PERIOD);
			if (bufferAnimation == 0)
			{
				clickAnimationFrame++;
			}
		}
		else
		{
			clickPos = null;
			clickAnimationFrame = 0;
		}
	}

	private void hideIfNpcOnTile(RuneliteNpc runeliteNpc)
	{
		boolean npcCurrentlyOnTile = false;
		for (NPC npc : client.getNpcs())
		{
			WorldPoint wpNpc = WorldPoint.fromLocalInstance(client, npc.getLocalLocation());
			if (wpNpc != null && wpNpc.distanceTo2D(runeliteNpc.getWorldPoint()) == 0)
			{
				npcCurrentlyOnTile = true;
				break;
			}
		}
		if (npcCurrentlyOnTile && !npcBeenOnTile)
		{
			npcBeenOnTile = true;
			remove(runeliteNpc);
		}
		else if (!npcCurrentlyOnTile && npcBeenOnTile)
		{
			npcBeenOnTile = false;

			if (playerBeenOnTile) return;

			LocalPoint lp = LocalPoint.fromWorld(client, runeliteNpc.getWorldPoint());
			if (lp == null) return;
			setActive(runeliteNpc);
		}
	}

	private void hideIfPlayerOnTile(RuneliteNpc runeliteNpc, WorldPoint playerPosition)
	{
		if (playerPosition.distanceTo(runeliteNpc.getWorldPoint()) == 0 && !playerBeenOnTile)
		{
			playerBeenOnTile = true;
			remove(runeliteNpc);
//			runeliteNpc.remove();
		}
		else if (playerBeenOnTile && !runeliteNpc.getRuneliteObject().isActive() && playerPosition.distanceTo(runeliteNpc.getWorldPoint()) != 0)
		{
			playerBeenOnTile = false;

			if (npcBeenOnTile) return;

			LocalPoint lp = LocalPoint.fromWorld(client, runeliteNpc.getWorldPoint());
			if (lp == null) return;
			setActive(runeliteNpc);
		}
	}

	@Subscribe
	public void onGameStateChanged(final GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGGED_IN)
		{
			npcs.forEach((group, npc) -> {
				LocalPoint lp = LocalPoint.fromWorld(client, npc.getWorldPoint());
				if (lp == null) return;
				setActive(npc);
			});
		}
		else if (event.getGameState() == GameState.LOADING)
		{
			npcs.forEach((group, npc) -> {
				remove(npc);
			});
		}
	}
}
