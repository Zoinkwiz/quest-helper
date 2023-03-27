/*
 * Copyright (c) 2023, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.steps.playermadesteps;

import com.google.common.collect.ImmutableList;
import com.questhelper.QuestHelperPlugin;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.overlay.DirectionArrow;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import lombok.Setter;
import net.runelite.api.Actor;
import net.runelite.api.ChatMessageType;
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
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.chatbox.ChatboxPanelManager;
import net.runelite.client.ui.overlay.OverlayUtil;

// TODO: Separate out NPC logic from Step logic
public class RuneliteNpcStep extends DetailedQuestStep
{
	@Inject
	private ChatboxPanelManager chatboxPanelManager;

	@Inject
	ChatMessageManager chatMessageManager;

	private RuneliteNpc runeliteNpc;

	@Setter
	private int animation = 808;

	private final int[] model;

	@Setter
	private int face = -1;

	@Setter
	private int faceAnimation;

	@Setter
	private RuneliteNpcDialogStep dialogTree;

	Point clickPos;
	int clickAnimationFrame = 0;
	int bufferAnimation = 0;
	int[] redClick = new int[]{SpriteID.RED_CLICK_ANIMATION_1, SpriteID.RED_CLICK_ANIMATION_2, SpriteID.RED_CLICK_ANIMATION_3, SpriteID.RED_CLICK_ANIMATION_4};

	final int ANIMATION_PERIOD = 5;

	private String npcName;

	public RuneliteNpcStep(QuestHelper questHelper, int[] model, WorldPoint wp, String text, Requirement... requirements)
	{
		super(questHelper, wp, text, requirements);
		this.model = model;
	}

	public void setName(String name)
	{
		this.npcName = name;
	}

	public void setOptions(String[] options)
	{

	}

	// This should exist in an Npc-specific function, not here
	public void createDialogStepForNpc(String text, int faceAnimation)
	{
		if (face == -1)
		{
			throw new IllegalStateException("Face value must be positive");
		}
		RuneliteNpcDialogStep nextDialog = new RuneliteNpcDialogStep(npcName, text, face, faceAnimation);
	}

	public RuneliteNpcDialogStep createDialogStepForNpc(String text)
	{
		if (face == -1)
		{
			throw new IllegalStateException("Face value must be positive");
		}
		if (npcName == null)
		{
			throw new IllegalStateException("Name must be assigned");
		}
		return new RuneliteNpcDialogStep(npcName, text, face);
	}

	@Override
	public void startUp()
	{
		super.startUp();

		// Create object
		createRuneliteNpc();
	}

	@Override
	public void shutDown()
	{
		super.shutDown();
		clientThread.invokeLater(this::removeRuneliteNpc);
	}

	private void createRuneliteNpc()
	{
		runeliteNpc = new RuneliteNpc(client, clientThread, worldPoint, model, animation);
	}

	private void removeRuneliteNpc()
	{
		if (runeliteNpc != null)
		{
			runeliteNpc.remove();
		}
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

	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded event)
	{
		int widgetIndex = event.getActionParam0();
		int widgetID = event.getActionParam1();
		MenuEntry[] menuEntries = client.getMenuEntries();

		LocalPoint lp = LocalPoint.fromWorld(client, worldPoint);

		if (runeliteNpc != null && runeliteNpc.getRuneLiteObject() != null && runeliteNpc.getRuneLiteObject().getModel() != null)
		{
			if (!hasClickedNpc()) return;
			if (event.getOption().equals("Walk here"))
			{
				addExamine(menuEntries, widgetIndex, widgetID);
				addTalkOption(widgetIndex, widgetID);
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

	public void addExamine(MenuEntry[] menuEntries, int widgetIndex, int widgetID)
	{
		if (!hasClickedNpc()) return;

		// This is important for getting talk to be left-clickable, but don't fully understand why
		menuEntries = Arrays.copyOf(menuEntries, menuEntries.length + 1);

		client.createMenuEntry(menuEntries.length - 1)
			.setOption("Examine")
			.setTarget("<col=ffff00>" + npcName + "</col>")
			.onClick(menuEntry -> {
				clickPos = client.getMouseCanvasPosition();
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

	public void addTalkOption(int widgetIndex, int widgetID)
	{
		if (!hasClickedNpc()) return;

//		menuEntries = Arrays.copyOf(menuEntries, menuEntries.length + 1);

		client.createMenuEntry(-2)
			.setOption("Talk")
			.setTarget("<col=ffff00>" + npcName + "</col>")
			.setType(MenuAction.RUNELITE_HIGH_PRIORITY)
			.onClick((menuEntry -> {
				clickPos = client.getMouseCanvasPosition();

				WorldPoint wp = WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation());
				if (wp.distanceTo(worldPoint) > 1)
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

				setupChatBox();
			}))
			.setParam0(widgetIndex)
			.setParam1(widgetID);
		return;
	}

	private boolean hasClickedNpc()
	{
		LocalPoint lp = LocalPoint.fromWorld(client, worldPoint);

		if (lp == null) return false;
		Shape clickbox = Perspective.getClickbox(client, runeliteNpc.getRuneLiteObject().getModel(), runeliteNpc.getRuneLiteObject().getOrientation(), lp.getX(), lp.getY(),
			Perspective.getTileHeight(client, lp, worldPoint.getPlane()));

		if (clickbox == null) return false;

		Point p = client.getMouseCanvasPosition();

		return clickbox.contains(p.getX(), p.getY());
	}

	@Subscribe
	public void onGameStateChanged(final GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGGED_IN)
		{
			LocalPoint lp = LocalPoint.fromWorld(client, worldPoint);
			if (lp == null) return;
			runeliteNpc.setActive(lp);
		}
		else if (event.getGameState() == GameState.LOADING)
		{
			runeliteNpc.remove();
		}
	}

	@Override
	public void renderArrow(Graphics2D graphics)
	{
		if (questHelper.getConfig().showMiniMapArrow())
		{
			if (!runeliteNpc.isActive())
			{
				super.renderArrow(graphics);
			}
			else if (!hideWorldArrow)
			{
				Point p = Perspective.localToCanvas(client, runeliteNpc.getRuneLiteObject().getLocation(), client.getPlane(),
					runeliteNpc.getRuneLiteObject().getModelHeight());
				if (p != null)
				{
					DirectionArrow.drawWorldArrow(graphics, getQuestHelper().getConfig().targetOverlayColor(), p.getX(), p.getY() - ARROW_SHIFT_Y);
				}
			}
		}
	}

	@Override
	public void makeWorldOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin)
	{
		super.makeWorldOverlayHint(graphics, plugin);

		Color configColor = getQuestHelper().getConfig().targetOverlayColor();
		highlightNpc(configColor, graphics);
		renderRedClick(graphics);

		WorldPoint playerPosition = WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation());
		hideIfNpcOnTile();
		hideIfPlayerOnTile(playerPosition);
	}

	private void highlightNpc(Color color, Graphics2D graphics)
	{
		switch (questHelper.getConfig().highlightStyleNpcs())
		{
			case CONVEX_HULL:
			case OUTLINE:
				if (!runeliteNpc.isActive()) break;
				modelOutlineRenderer.drawOutline(
					runeliteNpc.getRuneLiteObject(),
					questHelper.getConfig().outlineThickness(),
					color,
					questHelper.getConfig().outlineFeathering()
				);
				break;
			case TILE:
				Polygon poly = Perspective.getCanvasTilePoly(client, runeliteNpc.getRuneLiteObject().getLocation());
				if (poly != null)
				{
					OverlayUtil.renderPolygon(graphics, poly, color);
				}
				break;
			default:
		}
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

	ChatBox currentChatBox;

	private void setupChatBox()
	{
		currentChatBox = new ChatBox(client, chatboxPanelManager, clientThread)
			.dialog(dialogTree)
			.build();
	}

	boolean playerBeenOnTile = false;
	boolean npcBeenOnTile = false;
	@Subscribe
	public void onGameTick(GameTick event)
	{
		super.onGameTick(event);
		WorldPoint playerPosition = WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation());
		if (playerPosition.distanceTo(worldPoint) > 1 && chatboxPanelManager.getCurrentInput() instanceof ChatBox) chatboxPanelManager.close();

		// South -> North
		//6589
		//5713
		//-822

		// West -> East
		// 5457
		//6845
		//-822

		// North -> South
		//6579
		//7984
		//-822

		// East -> West
		// 7728
		// 6866
		//-824

		// Is facing west->east if 5460 X
		// Is east->west if 7728 X
		// MID is 6592 for X

		// If (cameraX > 6592 (facing west) AND optionPosX < objPos) reduce priority
		// If (cameraX < 6592 (facing east) AND optionPosX > objPos) reduce priority
		// If (cameraY > 6961 (facing south) AND optionPosX < objPos) reduce priority
		// If (cameraY < 6961 (facing north) AND optionPosX > objPos) reduce priority


		// 6961 MID for Y
	}

	private void hideIfNpcOnTile()
	{
		boolean npcCurrentlyOnTile = false;
		for (NPC npc : client.getNpcs())
		{
			WorldPoint wpNpc = WorldPoint.fromLocalInstance(client, npc.getLocalLocation());
			if (wpNpc.distanceTo(worldPoint) == 0)
			{
				npcCurrentlyOnTile = true;
				break;
			}
		}
		if (npcCurrentlyOnTile && !npcBeenOnTile)
		{
			npcBeenOnTile = true;
			removeRuneliteNpc();
		}
		else if (!npcCurrentlyOnTile && npcBeenOnTile)
		{
			npcBeenOnTile = false;

			if (playerBeenOnTile) return;

			LocalPoint lp = LocalPoint.fromWorld(client, worldPoint);
			if (lp == null) return;
			runeliteNpc.setActive(lp);
		}
	}

	private void hideIfPlayerOnTile(WorldPoint playerPosition)
	{
		if (playerPosition.distanceTo(worldPoint) == 0 && !playerBeenOnTile)
		{
			playerBeenOnTile = true;
			removeRuneliteNpc();
			runeliteNpc.remove();
		}
		else if (playerBeenOnTile && !runeliteNpc.isActive() && playerPosition.distanceTo(worldPoint) != 0)
		{
			playerBeenOnTile = false;

			if (npcBeenOnTile) return;

			LocalPoint lp = LocalPoint.fromWorld(client, worldPoint);
			if (lp == null) return;
			runeliteNpc.setActive(lp);
		}
	}
}
