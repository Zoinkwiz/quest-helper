/*
 * Copyright (c) 2021, Zoinkwiz <https://github.com/Zoinkwiz/>
 * Copyright (c) 2020, Patyfatycake <https://github.com/Patyfatycake/>
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
 * ON ANY THEORY OF LIABI`LITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.questhelper.helpers.quests.recruitmentdrive;

import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.steps.ObjectStep;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;

public class LadyTableStep extends ObjectStep
{
	private final Statue[] statues = new Statue[]{
		new Statue("Unknown", new WorldPoint(0, 0, 0)),
		new Statue("Bronze Halberd", new WorldPoint(2452, 4976, 0)),
		new Statue("Silver Halberd", new WorldPoint(2452, 4979, 0)),
		new Statue("Gold Halberd", new WorldPoint(2452, 4982, 0)),

		new Statue("Bronze 2H", new WorldPoint(2450, 4976, 0)),
		new Statue("Silver 2H", new WorldPoint(2450, 4979, 0)),
		new Statue("Gold 2H", new WorldPoint(2450, 4982, 0)),

		new Statue("Gold Mace", new WorldPoint(2456, 4982, 0)),
		new Statue("Silver Mace", new WorldPoint(2456, 4979, 0)),
		new Statue("Bronze mace", new WorldPoint(2456, 4976, 0)),

		new Statue("Bronze axe", new WorldPoint(2454, 4976, 0)),
		new Statue("Silver axe", new WorldPoint(2454, 4979, 0)),
		new Statue("Gold axe", new WorldPoint(2454, 4972, 0))
	};

	public LadyTableStep(QuestHelper questHelper, Requirement... requirements)
	{
		super(questHelper, ObjectID.RD_1G, new WorldPoint(0, 0, 0), "Click the missing statue.", requirements);

		addAlternateObjects(ObjectID.RD_1S, ObjectID.RD_1B,
			ObjectID.RD_2G, ObjectID.RD_2S, ObjectID.RD_2B, ObjectID.RD_3G,
			ObjectID.RD_3S, ObjectID.RD_3B, ObjectID.RD_4G, ObjectID.RD_4B);
	}

	@Override
	public void startUp()
	{
		super.startUp();

		var answerIndex = client.getVarbitValue(VarbitID.RD_TEMPLOCK_2);
		if (answerIndex < statues.length)
		{
			var answerStatue = statues[answerIndex];
			setText("Click the " + answerStatue.text + " once it appears.");
			setWorldPoint(answerStatue.point);
		}
	}

	@Override
	public void onVarbitChanged(VarbitChanged varbitChanged)
	{
		super.onVarbitChanged(varbitChanged);

		if (varbitChanged.getVarbitId() == VarbitID.RD_TEMPLOCK_2)
		{
			var answerIndex = varbitChanged.getValue();
			var answerStatue = statues[answerIndex];
			setText("Click the " + answerStatue.text + " once it appears.");
			setWorldPoint(answerStatue.point);
		}
	}

	static class Statue
	{
		private final String text;
		private final WorldPoint point;

		public Statue(String text, WorldPoint point)
		{
			this.text = text;
			this.point = point;
		}
	}
}
