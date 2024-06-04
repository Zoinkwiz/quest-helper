/*
 * Copyright (c) 2024, pajlada <https://github.com/pajlada>
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
package com.questhelper.playerquests.bikeshedder;

import com.google.common.collect.ImmutableMap;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.SpellbookRequirement;
import com.questhelper.requirements.util.Spellbook;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.QuestStep;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.coords.WorldPoint;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BikeShedder extends BasicQuestHelper
{
	private DetailedQuestStep moveToLumbridge;
	private DetailedQuestStep confuseHans;
	private DetailedQuestStep equipLightbearer;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		var lumbridge = new Zone(new WorldPoint(3217, 3210, 0), new WorldPoint(3226, 3228, 0));
		var outsideLumbridge = new ZoneRequirement(false, lumbridge);

		var steps = new ConditionalStep(this, confuseHans);
		steps.addStep(outsideLumbridge, moveToLumbridge);
		steps.addStep(new ZoneRequirement(new WorldPoint(3222, 3218, 0)), equipLightbearer);
		return new ImmutableMap.Builder<Integer, QuestStep>()
			.put(-1, steps)
			.build();
	}

	@Override
	public void setupRequirements()
	{
		moveToLumbridge = new DetailedQuestStep(this, new WorldPoint(3221, 3218, 0), "Move to outside Lumbridge Castle");

		var normalSpellbook = new SpellbookRequirement(Spellbook.NORMAL);

		confuseHans = new NpcStep(this, NpcID.HANS, new WorldPoint(3221, 3218, 0), "Cast Confuse on Hans", normalSpellbook);
		confuseHans.addSpellHighlight("Confuse");

		var lightbearer = new ItemRequirement("Lightbearer", ItemID.LIGHTBEARER).highlighted();
		equipLightbearer = new DetailedQuestStep(this, "Equip a Lightbearer", lightbearer.equipped());
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var panels = new ArrayList<PanelDetails>();

		panels.add(new PanelDetails("Move to Lumbridge", List.of(moveToLumbridge)));
		panels.add(new PanelDetails("Normal Spellbook", List.of(confuseHans)));
		panels.add(new PanelDetails("Equip Lightbearer", List.of(equipLightbearer)));

		return panels;
	}
}
