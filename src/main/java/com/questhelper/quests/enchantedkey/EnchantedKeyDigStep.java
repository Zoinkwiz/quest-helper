/*
 * Copyright (c) 2020, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.quests.enchantedkey;

import com.google.inject.Inject;
import com.questhelper.QuestHelperPlugin;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
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
import net.runelite.api.Player;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;

public class EnchantedKeyDigStep extends DetailedQuestStep
{
	@Inject
	ItemManager itemManager;

	@Nullable
	private EnchantedKeySolver enchantedKeySolver;

	int currentVar = 0;

	public EnchantedKeyDigStep(QuestHelper questHelper, ItemRequirement... requirements)
	{
		super(questHelper, "Use the Enchanted Key to locate treasure.", requirements);
	}

	@Override
	public void makeOverlayHint(PanelComponent panelComponent, QuestHelperPlugin plugin, List<String> additionalText, Requirement... additionalRequirements)
	{
		super.makeOverlayHint(panelComponent, plugin, additionalText);
		if (enchantedKeySolver == null)
		{
			return;
		}

		final Collection<EnchantedKeyDigLocation> digLocations = enchantedKeySolver.getPossibleLocations();

		if (digLocations.size() > 1)
		{
			panelComponent.getChildren().add(LineComponent.builder()
				.left("Possible locations:")
				.build());
		}
		else if (digLocations.size() < 1)
		{
			panelComponent.getChildren().add(LineComponent.builder()
				.left("Unable to establish dig location")
				.build());
		}
		{
			panelComponent.getChildren().add(LineComponent.builder()
				.left("Dig location:")
				.build());
		}

		for (EnchantedKeyDigLocation enchantedKeyDigLocation : digLocations)
		{
			panelComponent.getChildren().add(LineComponent.builder()
				.left("- " + enchantedKeyDigLocation.getArea())
				.leftColor(Color.LIGHT_GRAY)
				.build());
		}
	}

	@Subscribe
	@Override
	public void onVarbitChanged(VarbitChanged varbitChanged)
	{
		super.onVarbitChanged(varbitChanged);
		if (questHelper.getVar() != currentVar)
		{
			currentVar = questHelper.getVar();
			resetState();
		}
	}

	public void resetState()
	{
		setWorldPoint(null);
		int locationStates = client.getVarbitValue(1391);
		Set<EnchantedKeyDigLocation> locations = Arrays.stream(EnchantedKeyDigLocation.values()).filter(p -> ((locationStates >> p.getBit()) & 1) == 0)
			.collect(Collectors.toSet());
		if (enchantedKeySolver != null)
		{
			enchantedKeySolver.resetSolver(locations);
		}
		if (enchantedKeySolver.getPossibleLocations().size() == 1)
		{
			this.setWorldPoint(enchantedKeySolver.getPossibleLocations().iterator().next().getWorldPoint());
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

		OverlayUtil.renderTileOverlay(client, graphics, localLocation, getSpadeImage(), questHelper.getConfig().targetOverlayColor());
	}

	@Subscribe
	public void onChatMessage(ChatMessage chatMessage)
	{
		if (chatMessage.getType() == ChatMessageType.GAMEMESSAGE)
		{
			update(chatMessage.getMessage());
		}
	}

	public boolean update(final String message)
	{
		if (enchantedKeySolver == null)
		{
			return false;
		}

		final EnchantedKeyTemperature temperature = EnchantedKeyTemperature.getFromTemperatureSet(message);

		if (temperature == null)
		{
			return false;
		}

		Player player = client.getLocalPlayer();
		if (player == null)
		{
			return false;
		}

		final WorldPoint localWorld = player.getWorldLocation();

		if (localWorld == null)
		{
			return false;
		}

		final EnchantedKeyTemperatureChange temperatureChange = EnchantedKeyTemperatureChange.of(message);

		enchantedKeySolver.signal(localWorld, temperature, temperatureChange);

		if (enchantedKeySolver.getPossibleLocations().size() == 1)
		{
			this.setWorldPoint(enchantedKeySolver.getPossibleLocations().iterator().next().getWorldPoint());
		}
		else
		{
			this.setWorldPoint(null);
		}

		return true;
	}

	@Override
	public void startUp()
	{
		super.startUp();
		currentVar = questHelper.getVar();
		Set<EnchantedKeyDigLocation> locations = Arrays.stream(EnchantedKeyDigLocation.values()).filter(p -> ((currentVar >> p.getBit()) & 1) == 0)
			.collect(Collectors.toSet());
		enchantedKeySolver = new EnchantedKeySolver(locations);
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

	private BufferedImage getSpadeImage()
	{
		return itemManager.getImage(ItemID.SPADE);
	}
}
