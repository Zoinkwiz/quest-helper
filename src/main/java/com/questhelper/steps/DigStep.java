/*
 * Copyright (c) 2019, Trevor <https://github.com/Trevor159>
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
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.util.InventorySlots;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.function.Predicate;
import net.runelite.api.Item;
import net.runelite.api.ItemID;
import net.runelite.api.Player;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.overlay.OverlayUtil;

public class DigStep extends DetailedQuestStep
{
	private final ItemRequirement SPADE = new ItemRequirement("Spade", ItemID.SPADE);
	private Predicate<Item> expectedItemPredicate = i -> i.getId() == -1;
	private boolean hasExpectedItem = false;
	public DigStep(QuestHelper questHelper, WorldPoint worldPoint, String text, Requirement... requirements)
	{
		super(questHelper, worldPoint, text, requirements);
		this.getRequirements().add(SPADE);
	}

	public void setExpectedItem(int itemID)
	{
		setExpectedItem(i -> i.getId() == itemID);
	}

	public void setExpectedItem(Predicate<Item> predicate)
	{
		this.expectedItemPredicate = predicate == null ? i -> true : predicate;
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		super.onGameTick(event);
		hasExpectedItem = InventorySlots.INVENTORY_SLOTS.contains(client, expectedItemPredicate);
		if (!hasExpectedItem)
		{
			Player player = client.getLocalPlayer();
			if (player == null) {
				return;
			}
			WorldPoint targetLocation = worldPoint;
			boolean shouldHighlightSpade = targetLocation.isInScene(client);
			SPADE.setHighlightInInventory(shouldHighlightSpade);
		}
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
}
