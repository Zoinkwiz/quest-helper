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
package com.questhelper.quests.themagearenaii;

import com.google.inject.Inject;
import com.questhelper.QuestHelperPlugin;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.steps.DetailedQuestStep;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.runelite.api.ChatMessageType;
import net.runelite.api.ItemID;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;

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

	@Nullable
	private MageArenaSolver mageArenaSolver;

	boolean foundLocation = false;

	int currentVar = 0;

	final int BOSS_MOVING_TIMER_VARBIT = 6062;

	public MageArenaBossStep(QuestHelper questHelper, ItemRequirement staff, String bossName,
							 String abilityDetail, ItemRequirement... requirements)
	{
		super(questHelper, originalTextStart + bossName + originalTextEnd, requirements);
		this.bossName = bossName;
		this.abilityDetail = abilityDetail;
		this.staff = staff;
		this.baseRequirements = requirements;
	}

	@Override
	public void makeOverlayHint(PanelComponent panelComponent, QuestHelperPlugin plugin, List<String> additionalText, Requirement... additionalRequirements)
	{
		super.makeOverlayHint(panelComponent, plugin, additionalText);
		if (mageArenaSolver == null)
		{
			return;
		}

		final Collection<MageArenaSpawnLocation> digLocations = mageArenaSolver.getPossibleLocations();
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
		else if (digLocations.size() < 1)
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

	@Subscribe
	@Override
	public void onVarbitChanged(VarbitChanged varbitChanged)
	{
		super.onVarbitChanged(varbitChanged);
		int newState = client.getVarbitValue(BOSS_MOVING_TIMER_VARBIT);

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
		setWorldPoint(null);
		Set<MageArenaSpawnLocation> locations =
			Arrays.stream(MageArenaSpawnLocation.values())
			.collect(Collectors.toSet());

		if (mageArenaSolver != null)
		{
			mageArenaSolver.resetSolver(locations);
		}
		if (mageArenaSolver.getPossibleLocations().size() == 1)
		{
			this.setWorldPoint(mageArenaSolver.getPossibleLocations().iterator().next().getWorldPoint());
		}
	}

	@Override
	public void makeWorldOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin)
	{
		super.makeWorldOverlayHint(graphics, plugin);

		if (worldPoint == null)
		{
			return;
		}

		LocalPoint localLocation = LocalPoint.fromWorld(client, worldPoint);

		if (localLocation == null)
		{
			return;
		}

		OverlayUtil.renderTileOverlay(client, graphics, localLocation, getSymbolLocation(), questHelper.getConfig().targetOverlayColor());
	}

	@Subscribe
	public void onChatMessage(ChatMessage chatMessage)
	{
		if (chatMessage.getType() == ChatMessageType.GAMEMESSAGE)
		{
			update(chatMessage.getMessage());
		}
	}

	public void update(final String message)
	{
		if (mageArenaSolver == null)
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

		final MageArenaTemperatureChange temperatureChange = MageArenaTemperatureChange.of(message);

		mageArenaSolver.signal(localWorld, temperature, temperatureChange);

		if (mageArenaSolver.getPossibleLocations().size() == 1)
		{
			this.setWorldPoint(mageArenaSolver.getPossibleLocations().iterator().next().getWorldPoint());
		}
		else
		{
			this.setWorldPoint(null);
		}

	}

	@Override
	public void startUp()
	{
		super.startUp();
		currentVar = client.getVarbitValue(BOSS_MOVING_TIMER_VARBIT);
		Set<MageArenaSpawnLocation> locations =
			Arrays.stream(MageArenaSpawnLocation.values())
			.collect(Collectors.toSet());
		mageArenaSolver = new MageArenaSolver(locations);
		if (locations.size() == 1)
		{
			this.setWorldPoint(locations.iterator().next().getWorldPoint());
		}
	}

	@Override
	public void shutDown()
	{
		super.shutDown();
		this.setWorldPoint(null);
	}

	private BufferedImage getSymbolLocation()
	{
		return itemManager.getImage(ItemID.ENCHANTED_SYMBOL);
	}
}
