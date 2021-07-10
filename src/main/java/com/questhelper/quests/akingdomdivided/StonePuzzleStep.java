/*
 * Copyright (c) 2020, Zoinkwiz
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
import com.questhelper.steps.DetailedOwnerStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.Subscribe;

public class StonePuzzleStep extends DetailedOwnerStep
{
	Requirement inPanelZone;
	private boolean rStoneDone = false;
	private boolean oStoneDone = false;
	private boolean sStoneDone = false;
	private boolean eStoneDone = false;
	private boolean codeFound = false;

	DetailedQuestStep checkRStone, checkOStone, checkSStone, checkEStone, chopVines, squeezeThroughVines, checkPanel;

	Zone panelArea1, panelArea2;

	private final HashMap<String, String> answers = new HashMap<>();

	public StonePuzzleStep(QuestHelper questHelper)
	{
		super(questHelper, "Solve the wall panel puzzle.");
	}

	@Override
	protected void updateSteps()
	{
		if (!codeFound)
		{
			if (!eStoneDone)
			{
				startUpStep(checkEStone);
			}
			else if (!rStoneDone)
			{
				startUpStep(checkRStone);
			}
			else if (!sStoneDone)
			{
				startUpStep(checkSStone);
			}
			else if (!oStoneDone)
			{
				startUpStep(checkOStone);
			}
			else
			{
				checkPanel.addText("Enter code: " +
					answers.get("R") +
					answers.get("O") +
					answers.get("S") +
					answers.get("E")
				);

				codeFound = true;
			}

		}
		else
		{
			if (inPanelZone.check(client))
			{

				startUpStep(checkPanel);
			}
			else
			{
				startUpStep(squeezeThroughVines);
			}
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		updateSteps();
	}

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded widgetLoaded)
	{
		if (widgetLoaded.getGroupId() != 229)
		{
			return;
		}

		clientThread.invokeLater(() -> {
			Widget widgetStone = client.getWidget(229, 1);

			if (widgetStone != null && !widgetStone.isHidden() && !codeFound)
			{
				Matcher foundStoneValue = Pattern.compile("(?:^|)'([^']*?)'(?:\\s|$)").matcher(widgetStone.getText());
				final boolean foundAnswer = foundStoneValue.find();

				if (foundAnswer)
				{
					final String value = foundStoneValue.group(0);
					final String letter = value.substring(1, 2);
					final String number = value.substring(value.length() - 2, value.length() - 1);

					switch (letter)
					{
						case "R":
							answers.put("R", number);
							rStoneDone = true;
							break;
						case "O":
							answers.put("O", number);
							oStoneDone = true;
							break;
						case "S":
							answers.put("S", number);
							sStoneDone = true;
							break;
						case "E":
							answers.put("E", number);
							eStoneDone = true;
							break;
					}
				}
			}
		});
	}

	protected void setupZones()
	{
		panelArea1 = new Zone(new WorldPoint(1670, 3577, 0), new WorldPoint(1671, 3576, 0));
		panelArea2 = new Zone(new WorldPoint(1669, 3581, 0), new WorldPoint(1672, 3578, 0));
	}

	public void setupConditions()
	{
		inPanelZone = new ZoneRequirement(panelArea1, panelArea2);
	}

	@Override
	protected void setupSteps()
	{
		setupZones();
		setupConditions();


		checkRStone = new ObjectStep(getQuestHelper(), ObjectID.STONE_PILE_41827, new WorldPoint(1678, 3567, 0), "Check the south east stone pile.");
		checkOStone = new ObjectStep(getQuestHelper(), ObjectID.STONE_PILE_41827, new WorldPoint(1670, 3575, 0), "Check the north west stone pile.");
		checkSStone = new ObjectStep(getQuestHelper(), ObjectID.STONE_PILE_41827, new WorldPoint(1672, 3571, 0), "Check the south west stone pile.");
		checkEStone = new ObjectStep(getQuestHelper(), ObjectID.STONE_PILE_41827, new WorldPoint(1680, 3576, 0), "Check the north east stone pile.");

		chopVines = new ObjectStep(getQuestHelper(), ObjectID.VINES_41815, new WorldPoint(1671, 3577, 0), "Chop the vines south of Martin Holt.");
		squeezeThroughVines = new ObjectStep(getQuestHelper(), ObjectID.VINES_41816, new WorldPoint(1671, 3577, 0), "Squeeze through the vines.");
		checkPanel = new ObjectStep(getQuestHelper(), ObjectID.PANEL_41822, new WorldPoint(1672, 3579, 0), "Check the panel on the wall.");
	}

	@Override
	public Collection<QuestStep> getSteps()
	{
		return Arrays.asList(checkEStone, checkRStone, checkSStone, checkOStone, squeezeThroughVines, checkPanel);
	}
}
