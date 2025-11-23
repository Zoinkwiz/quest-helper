/*
 * Copyright (c) 2025, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.helpers.activities.charting.steps;

import com.questhelper.helpers.activities.charting.ChartingTaskDefinition;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.util.LogicType;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;

public class ChartingDivingStep extends ChartingTaskNpcStep
{
	public ChartingDivingStep(QuestHelper questHelper, ChartingTaskDefinition definition, boolean showAnswer, Requirement... requirements)
	{
		super(questHelper, NpcID.SAILING_CHARTING_MERMAID_GUIDE_1, definition, showAnswer, requirements);
		addAlternateNpcs(NpcID.SAILING_CHARTING_MERMAID_GUIDE_2, NpcID.SAILING_CHARTING_MERMAID_GUIDE_3,
			NpcID.SAILING_CHARTING_MERMAID_GUIDE_4, NpcID.SAILING_CHARTING_MERMAID_GUIDE_5);
		if (showAnswer) setGeInterfaceIcon(definition.getItemIds());

		addDialogStep("Can I dive to the sea floor with you?");

		var medallionOfTheDeep = new ItemRequirement("Medallion of the deep", ItemID.MEDALLION_OF_THE_DEEP).equipped();
		var divingHelmet = new ItemRequirement("Fishbowl helmet", ItemID.HUNDRED_PIRATE_DIVING_HELMET).equipped();
		var divingApparatus = new ItemRequirement("Diving apparatus", ItemID.HUNDRED_PIRATE_DIVING_BACKPACK).equipped();
		var divingGear = new ItemRequirements(LogicType.AND, divingHelmet, divingApparatus);
		var canBreathUnderwater = new ItemRequirements(LogicType.OR, "Diving helmet + apparatus OR medallion of the deep", medallionOfTheDeep, divingGear).equipped();
		addRequirement(canBreathUnderwater);
	}
}
