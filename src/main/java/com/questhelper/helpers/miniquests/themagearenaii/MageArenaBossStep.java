/*
 * Copyright (c) 2018, Eadgars Ruse <https://github.com/Eadgars-Ruse>
 * Copyright (c) 2019, Jordan Atwood <nightfirecat@protonmail.com>
 * Copyright (c) 2021, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.helpers.miniquests.themagearenaii;

import com.google.inject.Inject;
import com.questhelper.QuestHelperPlugin;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.tools.DefinedPoint;
import lombok.NonNull;
import net.runelite.api.ChatMessageType;
import net.runelite.api.KeyCode;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.api.widgets.JavaScriptCallback;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class MageArenaBossStep extends DetailedQuestStep
{
	@Inject
	ItemManager itemManager;

	static String originalTextStart = "Use the Enchanted Symbol to locate the ";
	static String originalTextEnd = " boss. Only bring food and the symbol for this bit." +
		"Make sure to only click to locate the current boss, or the locating " +
		"functionality won't work.";

	String goFightTextStart = "Gear up with your staff, food, potions and gear you're willing to " +
		"risk. Go to the location and use the device to spawn the boss. Protect from Magic and " +
		"defeat it. ";

	final String bossName;
	final String abilityDetail;

	final ItemRequirement staff;

	ItemRequirement[] baseRequirements;

	private God godToFind;

	private God lastGodClicked;

	private final Map<God, MageArenaSolver> mageArenaSolvers = new HashMap<>();

	boolean foundLocation = false;

	int currentVar = 0;

	boolean allowChangingBoss;

	// We need this as a player can press multiple keys in the options dialog, but only the first press counts
	boolean keyPressedOnce;

	public MageArenaBossStep(QuestHelper questHelper, ItemRequirement staff, String bossName,
							 String abilityDetail, ItemRequirement... requirements)
	{
		super(questHelper, originalTextStart + bossName + originalTextEnd, requirements);
		this.bossName = bossName;
		this.abilityDetail = abilityDetail;
		this.staff = staff;
		this.baseRequirements = requirements;
		this.godToFind = God.getByName(bossName);
	}

	public MageArenaBossStep(QuestHelper questHelper, ItemRequirement staff,
							 String abilityDetail, ItemRequirement... requirements)
	{
		super(questHelper, originalTextStart + "desired" + originalTextEnd, requirements);
		this.bossName = "desired";
		this.abilityDetail = abilityDetail;
		this.staff = staff;
		this.baseRequirements = requirements;
		this.allowChangingBoss = true;
		this.godToFind = God.SARADOMIN;
	}

	@Override
	public void makeOverlayHint(PanelComponent panelComponent, QuestHelperPlugin plugin, @NonNull List<String> additionalText, @NonNull List<Requirement> additionalRequirements)
	{
		super.makeOverlayHint(panelComponent, plugin, additionalText, additionalRequirements);
		if (mageArenaSolvers == null)
		{
			return;
		}

		final Collection<MageArenaSpawnLocation> digLocations = mageArenaSolvers.get(godToFind).getPossibleLocations();
		List<String> locations = digLocations.stream()
			.map(MageArenaSpawnLocation::getArea)
			.distinct()
			.collect(Collectors.toList());

		if (digLocations.size() > 1)
		{
			panelComponent.getChildren().add(LineComponent.builder()
				.left("Possible locations:")
				.build());
		}
		else if (digLocations.isEmpty())
		{
			if (!foundLocation)
			{
				addRequirement(staff);
				setText(goFightTextStart + abilityDetail);
			}
			foundLocation = true;
			panelComponent.getChildren().add(LineComponent.builder()
				.left("Unable to establish spawn location. Let the Quest Helper team know the location in Discord so " +
					"we can add it in")
				.build());
		}
		else
		{
			if (!foundLocation)
			{
				addRequirement(staff);
				setText(goFightTextStart + abilityDetail);
			}
			foundLocation = true;
			panelComponent.getChildren().add(LineComponent.builder()
				.left("Spawn location:")
				.build());
		}

		locations.forEach((location -> panelComponent.getChildren().add(LineComponent.builder()
			.left("- " + location)
			.leftColor(Color.LIGHT_GRAY)
			.build())));
	}

	@Override
	public void onVarbitChanged(VarbitChanged varbitChanged)
	{
		super.onVarbitChanged(varbitChanged);
		int newState = client.getVarbitValue(VarbitID.MA2_TIMER_REMAINING);

		// If the position of the bosses changes, reset
		if (newState > currentVar)
		{
			foundLocation = false;
			setText("The bosses have changed locations. " + originalTextStart + bossName + originalTextEnd);
			setRequirements(Arrays.asList(baseRequirements));
			resetState();
		}
		currentVar = newState;
	}

	public void resetState()
	{
		setWorldPoint(DefinedPoint.of(null));
		Set<MageArenaSpawnLocation> locations =
			Arrays.stream(MageArenaSpawnLocation.values())
				.collect(Collectors.toSet());

		if (mageArenaSolvers == null) return;

		mageArenaSolvers.forEach(((god, mageArenaSolver) ->
			mageArenaSolver.resetSolver(locations)
		));

		if (mageArenaSolvers.get(godToFind).getPossibleLocations().size() == 1)
		{
			this.setWorldPoint(mageArenaSolvers.get(godToFind).getPossibleLocations().iterator().next().getWorldPoint());
		}
	}

	@Override
	public void setWorldPoint(DefinedPoint definedPoint)
	{
		super.setWorldPoint(definedPoint);

		if (definedPoint == null || definedPoint.getWorldPoint() == null)
		{
			setHighlightZone(java.util.Collections.emptyList());
			return;
		}

		final WorldPoint worldPoint = definedPoint.getWorldPoint();
		final int maxDistance = MageArenaTemperature.SHAKING.getMaxDistance();

		final WorldPoint minPoint = new WorldPoint(
			worldPoint.getX() - maxDistance,
			worldPoint.getY() - maxDistance,
			worldPoint.getPlane()
		);

		final WorldPoint maxPoint = new WorldPoint(
			worldPoint.getX() + maxDistance,
			worldPoint.getY() + maxDistance,
			worldPoint.getPlane()
		);

		setHighlightZone(new Zone(minPoint, maxPoint));
	}

	@Override
	public void makeWorldOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin)
	{
		super.makeWorldOverlayHint(graphics, plugin);

		if (definedPoint == null)
		{
			return;
		}

		LocalPoint localLocation = LocalPoint.fromWorld(client, definedPoint.getWorldPoint());

		if (localLocation == null)
		{
			return;
		}

		OverlayUtil.renderTileOverlay(client, graphics, localLocation, getSymbolLocation(), questHelper.getConfig().targetOverlayColor());
	}

	@Override
	public void onWidgetLoaded(WidgetLoaded widgetLoaded)
	{
		super.onWidgetLoaded(widgetLoaded);
		if (widgetLoaded.getGroupId() != InterfaceID.CHATMENU) return;

		clientThread.invokeAtTickEnd(this::addListeners);
	}

	public void addListeners()
	{
		final var SARADOMIN_POS = 1;
		final var GUTHIX_POS = 2;
		final var ZAMORAK_POS = 3;
		var chatMenu = client.getWidget(InterfaceID.Chatmenu.OPTIONS);
		if (chatMenu == null || chatMenu.isHidden()) return;
		if (chatMenu.getChildren() == null || chatMenu.getChildren().length < 4) return;

		var saradominButton = chatMenu.getChildren()[SARADOMIN_POS];
		var guthixButton = chatMenu.getChildren()[GUTHIX_POS];
		var zamorakButton = chatMenu.getChildren()[ZAMORAK_POS];
		if (saradominButton == null || guthixButton == null || zamorakButton == null) return;
		saradominButton.setOnClickListener((JavaScriptCallback) ev -> { handleBossButtonClick(God.SARADOMIN); });
		guthixButton.setOnClickListener((JavaScriptCallback) ev -> {handleBossButtonClick(God.GUTHIX);});
		zamorakButton.setOnClickListener((JavaScriptCallback) ev -> { handleBossButtonClick(God.ZAMORAK); });

		keyPressedOnce = false;

		chatMenu.setHasListener(true);
		chatMenu.setOnKeyListener((JavaScriptCallback) ev -> {
			if (keyPressedOnce) return;
			keyPressedOnce = true;
			if (ev.getTypedKeyCode() == KeyCode.KC_1) handleBossButtonClick(God.SARADOMIN);
			if (ev.getTypedKeyCode() == KeyCode.KC_2) handleBossButtonClick(God.GUTHIX);
			if (ev.getTypedKeyCode() == KeyCode.KC_3) handleBossButtonClick(God.ZAMORAK);
		});
		chatMenu.revalidate();
	}

	private void handleBossButtonClick(God godClicked)
	{
		if (allowChangingBoss)
		{
			mageArenaSolvers.get(godClicked).setLastWorldPoint(mageArenaSolvers.get(godToFind).getLastWorldPoint());
			godToFind = godClicked;
		}
		else
		{
			lastGodClicked = godClicked;
		}
	}

	@Override
	public void onGameTick(GameTick gameTick)
	{
		super.onGameTick(gameTick);
	}

	@Override
	public void onChatMessage(ChatMessage chatMessage)
	{
		super.onChatMessage(chatMessage);

		if (chatMessage.getType() == ChatMessageType.GAMEMESSAGE)
		{
			update(chatMessage.getMessage());
		}
	}

	public void update(final String message)
	{
		final MageArenaTemperatureChange temperatureChange = MageArenaTemperatureChange.of(message);

		if (mageArenaSolvers == null || mageArenaSolvers.get(godToFind) == null)
		{
			return;
		}

		// If looking only for a specific boss, don't bother checking others
		if (!allowChangingBoss && lastGodClicked != godToFind)
		{
			return;
		}

		final MageArenaTemperature temperature = MageArenaTemperature.getFromTemperatureSet(message);

		if (temperature == null)
		{
			return;
		}

		if (client.getLocalPlayer() == null)
		{
			return;
		}
		final WorldPoint localWorld = client.getLocalPlayer().getWorldLocation();

		if (localWorld == null)
		{
			return;
		}


		System.out.println("Finding temp for god " + godToFind + ", with previous wp of " + mageArenaSolvers.get(godToFind).getLastWorldPoint());
		System.out.println(mageArenaSolvers.get(godToFind).getPossibleLocations().size());

		final MageArenaSolver currentMageArenaSolver = mageArenaSolvers.get(godToFind);
		currentMageArenaSolver.signal(localWorld, temperature, temperatureChange);

		if (currentMageArenaSolver.getPossibleLocations().size() == 1)
		{
			this.setWorldPoint(currentMageArenaSolver.getPossibleLocations().iterator().next().getWorldPoint());
		}
		else
		{
			this.setWorldPoint(DefinedPoint.of(null));
		}

	}

	@Override
	public void startUp()
	{
		super.startUp();
		currentVar = client.getVarbitValue(VarbitID.MA2_TIMER_REMAINING);

		mageArenaSolvers.put(God.SARADOMIN, new MageArenaSolver(Arrays.stream(MageArenaSpawnLocation.values()).collect(Collectors.toSet())));
		mageArenaSolvers.put(God.GUTHIX, new MageArenaSolver(Arrays.stream(MageArenaSpawnLocation.values()).collect(Collectors.toSet())));
		mageArenaSolvers.put(God.ZAMORAK, new MageArenaSolver(Arrays.stream(MageArenaSpawnLocation.values()).collect(Collectors.toSet())));

		var locations = Arrays.stream(MageArenaSpawnLocation.values())
			.collect(Collectors.toSet());

		if (locations.size() == 1)
		{
			this.setWorldPoint(locations.iterator().next().getWorldPoint());
		}

		addListeners();
	}

	@Override
	public void shutDown()
	{
		super.shutDown();
		this.setWorldPoint(DefinedPoint.of(null));
	}

	private BufferedImage getSymbolLocation()
	{
		return itemManager.getImage(ItemID.MA2_SYMBOL);
	}

	enum God
	{
		SARADOMIN("Saradomin"),
		GUTHIX("Guthix"),
		ZAMORAK("Zamorak");

		final String name;

		God(String name)
		{
			this.name = name;
		}

		public static God getByName(String name)
		{
			for (God god : God.values())
			{
				if (god.name.equals(name)) return god;
			}
			// Failure catch
			return SARADOMIN;
		}
	}
}
