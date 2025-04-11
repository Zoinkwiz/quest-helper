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
package com.questhelper.runeliteobjects;

import com.questhelper.QuestHelperConfig;
import com.questhelper.questinfo.PlayerQuests;
import com.questhelper.requirements.runelite.PlayerQuestStateRequirement;
import com.questhelper.requirements.util.Operation;
import com.questhelper.runeliteobjects.extendedruneliteobjects.ReplacedNpc;
import com.questhelper.runeliteobjects.extendedruneliteobjects.RuneliteObjectManager;
import com.questhelper.runeliteobjects.extendedruneliteobjects.WidgetReplacement;
import com.questhelper.steps.widget.WidgetDetails;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.NpcID;
import net.runelite.client.config.ConfigManager;

public class GlobalFakeObjects
{
	@Setter
	private static boolean initialized;

	public static void createNpcs(Client client, RuneliteObjectManager runeliteObjectManager, ConfigManager configManager, QuestHelperConfig questHelperConfig)
	{
		if (initialized || !questHelperConfig.showRuneliteObjects()) return;
		initialized = true;
		createHopleez(runeliteObjectManager, client, configManager);
		Cheerer.createCheerers(runeliteObjectManager, client, configManager);
	}

	public static void disableNpcs(RuneliteObjectManager runeliteObjectManager)
	{
		if (!initialized) return;
		runeliteObjectManager.removeGroupAndSubgroups("global");
		initialized = false;
	}

	private static void createHopleez(RuneliteObjectManager runeliteObjectManager, Client client, ConfigManager configManager)
	{
		ReplacedNpc replacedHopleez = runeliteObjectManager.createReplacedNpc(client.getNpcDefinition(NpcID.ZEAH_DEFENCE_PURE).getModels(), new WorldPoint(3235, 3215, 0), NpcID.HATIUS_LUMBRIDGE_DIARY);
		replacedHopleez.setName("Hopleez");
		replacedHopleez.setFace(7481);
		replacedHopleez.setExamine("He was here first.");
		replacedHopleez.addExamineAction(runeliteObjectManager);
		replacedHopleez.setDisplayRequirement(new PlayerQuestStateRequirement(configManager, PlayerQuests.COOKS_HELPER, 4, Operation.GREATER_EQUAL));
		replacedHopleez.addWidgetReplacement(new WidgetReplacement(new WidgetDetails(InterfaceID.ChatLeft.TEXT), "Hatius Cosaintus", "Hopleez"));
		replacedHopleez.addWidgetReplacement(new WidgetReplacement(new WidgetDetails(InterfaceID.Objectbox.TEXT), "Hatius", "Hopleez"));
	}
}
