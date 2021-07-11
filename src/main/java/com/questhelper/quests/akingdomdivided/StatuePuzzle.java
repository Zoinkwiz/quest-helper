/*
 * Copyright (c) 2021, Zoinkwiz
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
package com.questhelper.quests.akingdomdivided;

import com.questhelper.Zone;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.steps.DetailedOwnerStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.Subscribe;

public class StatuePuzzle extends DetailedOwnerStep
{
	DetailedQuestStep checkPanel, climbUpPillarLeglessFaun, climbDownLeglessFaun, moveShayzienStatue, moveHosidiusStatue,
		moveLovakengjStatue, movePiscStatue, moveArceuusStatue, invalidState;

	Zone leglessFaunF1;

	Requirement inLeglessFaunF1, statuesAllValid;

	Requirement[] statueStates;

	Boolean readOnce = false;
	ArrayList<KourendCities> cityOrder;
	HashMap<KourendCities, DetailedQuestStep> statueMap;


	public enum KourendCities
	{
		HOSIDIUS,
		ARCEUUS,
		SHAYZIEN,
		LOVAKENGJ,
		PISCARILIUS;

		public int getPos()
		{
			KourendCities[] cities = KourendCities.values();
			for (int i = 0; i < cities.length; i++)
			{
				if (cities[i] == this)
				{
					return i + 1;
				}
			}

			return 0;
		}
	}

	public StatuePuzzle(QuestHelper questHelper)
	{
		super(questHelper, "Solve the statue puzzle.");
	}

	@Override
	protected void updateSteps()
	{
		if (!readOnce)
		{
			if (inLeglessFaunF1.check(client))
			{
				startUpStep(checkPanel);
			}
			else
			{
				startUpStep(climbUpPillarLeglessFaun);
			}
		}
		else
		{
			if (statuesAllValid != null && !statuesAllValid.check(client))
			{
				startUpStep(invalidState);
			}
			else if (inLeglessFaunF1.check(client))
			{
				startUpStep(climbDownLeglessFaun);
			}
			else
			{
				// If conditional exists and
				if (statueStates[0].check(client))
				{
					startUpStep(statueMap.get(cityOrder.get(0)));
				}
				else if (statueStates[1].check(client))
				{
					startUpStep(statueMap.get(cityOrder.get(1)));
				}
				else if (statueStates[2].check(client))
				{
					startUpStep(statueMap.get(cityOrder.get(2)));
				}
				else if (statueStates[3].check(client))
				{
					startUpStep(statueMap.get(cityOrder.get(3)));
				}
				else
				{
					startUpStep(statueMap.get(cityOrder.get(4)));
				}
			}
		}
	}


	@Subscribe
	public void onGameTick(GameTick event)
	{
		Widget widgetPanel = client.getWidget(229, 1);

		if (widgetPanel != null && !widgetPanel.isHidden() && !readOnce)
		{
			final String[] panelWords = widgetPanel
				.getText()
				.replaceAll("<br>", " ")
				.replaceAll(",", "")
				.split(" ");

			for (String word : panelWords)
			{
				if (word.equalsIgnoreCase(KourendCities.SHAYZIEN.toString()) ||
					word.equalsIgnoreCase(KourendCities.PISCARILIUS.toString()) ||
					word.equalsIgnoreCase(KourendCities.HOSIDIUS.toString()) ||
					word.equalsIgnoreCase(KourendCities.LOVAKENGJ.toString()) ||
					word.equalsIgnoreCase(KourendCities.ARCEUUS.toString())
				)
				{
					cityOrder.add(KourendCities.valueOf(word.toUpperCase()));
				}
			}

			if (cityOrder.size() == 5)
			{
				if (statueStates == null)
				{
					return;
				}
				readOnce = true;

				List<Requirement> validState = new ArrayList<>();
				for (int i = 0; i < cityOrder.size() - 1; i++)
				{
					VarbitRequirement correctValue = new VarbitRequirement(12306 + i, cityOrder.get(i).getPos());
					validState.add(new Conditions(LogicType.OR, statueStates[i], correctValue));
				}

				statuesAllValid = new Conditions(validState);
			}
		}

		updateSteps();
	}

	protected void setupZones()
	{
		leglessFaunF1 = new Zone(new WorldPoint(1766, 3686, 1), new WorldPoint(1773, 3679, 1));
	}

	public void setupConditions()
	{
		inLeglessFaunF1 = new ZoneRequirement(leglessFaunF1);
		statueStates = new Requirement[]{
			new VarbitRequirement(12306, 0),
			new VarbitRequirement(12307, 0),
			new VarbitRequirement(12308, 0),
			new VarbitRequirement(12309, 0)
		};
	}

	@Override
	protected void setupSteps()
	{
		setupZones();
		setupConditions();

		cityOrder = new ArrayList<>();
		statueMap = new HashMap<>();

		checkPanel = new ObjectStep(getQuestHelper(), ObjectID.PANEL_41833, new WorldPoint(1768, 3686, 1), "Check the panel on the wall.");
		climbUpPillarLeglessFaun = new ObjectStep(getQuestHelper(), ObjectID.PILLAR_41836, new WorldPoint(1772, 3680, 0), "Climb up the pillar west of Martin Holt.");
		climbDownLeglessFaun = new ObjectStep(getQuestHelper(), ObjectID.WALL_41839, new WorldPoint(1772, 3679, 1), "Climb down the wall.");

		moveArceuusStatue = new ObjectStep(getQuestHelper(), ObjectID.ARCEUUS_STATUE_41842, new WorldPoint(1777, 3686, 0), "Inspect the Arceuus statue.");
		moveArceuusStatue.addDialogStep("Press it in.");

		movePiscStatue = new ObjectStep(getQuestHelper(), ObjectID.PISCARILIUS_STATUE_41848, new WorldPoint(1780, 3687, 0), "Inspect the Piscarilius statue.");
		movePiscStatue.addDialogStep("Press it in.");

		moveHosidiusStatue = new ObjectStep(getQuestHelper(), ObjectID.HOSIDIUS_STATUE_41844, new WorldPoint(1780, 3677, 0), "Inspect the Hosidius statue.");
		moveHosidiusStatue.addDialogStep("Press it in.");

		moveLovakengjStatue = new ObjectStep(getQuestHelper(), ObjectID.LOVAKENGJ_STATUE_41846, new WorldPoint(1776, 3682, 0), "Inspect the Lovakengj statue.");
		moveLovakengjStatue.addDialogStep("Press it in.");

		moveShayzienStatue = new ObjectStep(getQuestHelper(), ObjectID.SHAYZIEN_STATUE_41850, new WorldPoint(1777, 3678, 0), "Inspect the Shayzien statue.");
		moveShayzienStatue.addDialogStep("Press it in.");

		invalidState = new DetailedQuestStep(getQuestHelper(), "You've inspected the statues in the wrong order." +
			" Inspect the remaining statues to reset the puzzle.");

		statueMap.put(KourendCities.ARCEUUS, moveArceuusStatue);
		statueMap.put(KourendCities.HOSIDIUS, moveHosidiusStatue);
		statueMap.put(KourendCities.PISCARILIUS, movePiscStatue);
		statueMap.put(KourendCities.LOVAKENGJ, moveLovakengjStatue);
		statueMap.put(KourendCities.SHAYZIEN, moveShayzienStatue);
	}

	@Override
	public Collection<QuestStep> getSteps()
	{
		return Arrays.asList(checkPanel, climbUpPillarLeglessFaun, climbDownLeglessFaun, moveArceuusStatue,
			moveHosidiusStatue, moveLovakengjStatue, moveShayzienStatue, movePiscStatue, invalidState);
	}
}
