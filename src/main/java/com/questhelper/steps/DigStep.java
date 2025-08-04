/*
 * Copyright (c) 2019, Trevor <https://github.com/Trevor159>
 * Copyright (c) 2025, pajlada <https://github.com/pajlada>
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
package com.questhelper.steps;

import com.questhelper.QuestHelperPlugin;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import java.awt.*;
import java.awt.image.BufferedImage;
import lombok.Setter;
import net.runelite.api.Player;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.overlay.OverlayUtil;

public class DigStep extends DetailedQuestStep
{
	private final ItemRequirement spade;

	@Setter
	private WhenToHighlight whenToHighlight = WhenToHighlight.InScene;

	/// Private ctor requiring a spade requirement, to be used by public ctors & builders
	private DigStep(QuestHelper questHelper, WorldPoint worldPoint, String text, ItemRequirement spade, Requirement... requirements)
	{
		super(questHelper, worldPoint, text, requirements);
		this.spade = spade;
		this.getRequirements().add(this.spade);
	}

	public DigStep(QuestHelper questHelper, WorldPoint worldPoint, String text, Requirement... requirements)
	{
		this(questHelper, worldPoint, text, new ItemRequirement("Spade", ItemID.SPADE), requirements);
	}

	/// Creates a DigStep with a custom spade requirement, allowing you to pass through custom tooltips / tips to the player
	public static DigStep withCustomSpadeRequirement(QuestHelper questHelper, WorldPoint worldPoint, String text, ItemRequirement spade, Requirement... requirements)
	{
		return new DigStep(questHelper, worldPoint, text, spade, requirements);
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		super.onGameTick(event);

		Player player = client.getLocalPlayer();
		if (player == null)
		{
			return;
		}
		WorldPoint targetLocation = worldPoint;
		boolean shouldHighlightSpade = false;
		switch (this.whenToHighlight)
		{
			case InScene:
				shouldHighlightSpade = targetLocation.isInScene(client);
				break;

			case OnTile:
				shouldHighlightSpade = targetLocation.distanceTo(player.getWorldLocation()) == 0;
				break;
		}
		spade.setHighlightInInventory(shouldHighlightSpade);
	}

	@Override
	public void makeWorldOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin)
	{
		super.makeWorldOverlayHint(graphics, plugin);

		if (inCutscene)
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

	private BufferedImage getSpadeImage()
	{
		return itemManager.getImage(ItemID.SPADE);
	}

	public enum WhenToHighlight
	{
		/// Highlight the spade whenever the target tile is in the same scene as the player
		InScene,
		/// Highlight the spade whenever the player is standing on the target tile
		OnTile,
	}
}
