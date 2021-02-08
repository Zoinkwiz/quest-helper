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
package com.questhelper.quests.sinsofthefather;

import com.questhelper.Zone;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.steps.DetailedOwnerStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.runelite.api.ItemID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.Subscribe;

public class ValveStep extends DetailedOwnerStep
{

	DetailedQuestStep readNote, setNorthValve, setSouthValve, setNorthValveNoHighlight, setSouthValveNoHighlight, cutTree;

	private int valveTotalValue;
	private int northTurns;
	private int southTurns;

	private final int GALLONS_NORTH = 7;
	private final int GALLONS_SOUTH = 4;

	private Zone northValveArea;
	private Zone southValveArea;

	Requirement atNorthValve, atSouthValve;

	ItemRequirement scentedTop, scentedLegs, scentedShoes, oldNote;

	private boolean foundSum = false;
	private boolean solving = false;
	private boolean solved = false;
	private boolean northDone = false;
	private boolean southDone = false;


	public ValveStep(QuestHelper questHelper)
	{
		super(questHelper, "Turn the valves to solve the water puzzle.");
	}

	@Override
	protected void updateSteps()
	{
		if (!foundSum)
		{
			startUpStep(readNote);
		}
		else
		{
			if (!southDone)
			{
				if (atSouthValve.check(client))
				{
					startUpStep(setSouthValve);
				}
				else
				{
					startUpStep(setSouthValveNoHighlight);
				}
			}
			else if (!northDone)
			{
				if (atNorthValve.check(client))
				{
					startUpStep(setNorthValve);
				}
				else
				{
					startUpStep(setNorthValveNoHighlight);
				}
			}
			else
			{
				startUpStep(cutTree);
			}
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		Widget widgetNote = client.getWidget(625, 7);
		if (!foundSum && widgetNote != null)
		{
			Matcher foundValveValue = Pattern.compile("[0-9]+").matcher(widgetNote.getText());
			final boolean foundAnswer = foundValveValue.find();
			if (foundAnswer && !solving)
			{
				foundSum = true;
				solving = true;
				valveTotalValue = Integer.parseInt(foundValveValue.group(0));
				getValveValues();
				updateSteps();
			}
		}
		else if (foundSum)
		{
			Widget widgetNumberOptions = client.getWidget(187, 3);
			Widget widgetValveChoice = client.getWidget(229, 1);

			if (atSouthValve.check(client))
			{
				if (widgetNumberOptions != null)
				{
					southDone = checkValve(widgetNumberOptions, southTurns);
				}
				else if (widgetValveChoice != null)
				{
					if (widgetValveChoice.getText().contains(String.valueOf(southTurns)))
					{
						southDone = true;
					}
					else if (widgetValveChoice.getText().contains("You set the valve"))
					{
						southDone = false;
					}
				}
			}
			else if (atNorthValve.check(client))
			{
				if (widgetNumberOptions != null)
				{
					northDone = checkValve(widgetNumberOptions, northTurns);
				}
				else if (widgetValveChoice != null)
				{
					if (widgetValveChoice.getText().contains(String.valueOf(northTurns)))
					{
						northDone = true;
					}
					else if (widgetValveChoice.getText().contains("You set the valve"))
					{
						northDone = false;
					}
				}
			}
			updateSteps();
		}
	}

	private boolean checkValve(Widget choices, int turns)
	{
		boolean isSelected = false;
		for (Widget option : choices.getChildren())
		{
			if (option.getText().equals(turns + " (current)"))
			{
				isSelected = true;
			}
		}
		return isSelected;
	}

	private void getValveValues()
	{
		int maxNorthTurns = valveTotalValue / GALLONS_NORTH;
		iterateValueCombos(maxNorthTurns);
	}

	private void iterateValueCombos(int currentNorthTurns)
	{
		if (currentNorthTurns == 0)
		{
			return;
		}

		int northSum = currentNorthTurns * GALLONS_NORTH;
		int remainderNorth = (valveTotalValue - northSum);

		if (currentNorthTurns <= 5 && (remainderNorth % GALLONS_SOUTH) == 0)
		{
			solved = true;
			northTurns = currentNorthTurns;
			southTurns = (remainderNorth / GALLONS_SOUTH);
			setNorthValve.setText("Set the north valve to " + northTurns + ".");
			setNorthValveNoHighlight.setText("Set the north valve to " + northTurns + ".");
			setNorthValve.addWidgetChoice(String.valueOf(northTurns), 187, 3);
			setNorthValve.addWidgetChoice(northTurns + " (current)", 187, 3);

			setSouthValve.setText("Set the south valve to " + southTurns + ".");
			setSouthValveNoHighlight.setText("Set the south valve to " + southTurns + ".");
			setSouthValve.addWidgetChoice(String.valueOf(southTurns), 187, 3);
			setSouthValve.addWidgetChoice(southTurns + " (current)", 187, 3);
		}
		else
		{
			int newNorthTurns = currentNorthTurns - 1;
			iterateValueCombos(newNorthTurns);
		}
	}

	protected  void setupRequirements()
	{
		scentedTop = new ItemRequirement("Vyre noble top", ItemID.VYRE_NOBLE_TOP, 1, true);
		scentedTop.setTooltip("You can get a replacement from a chest in Old Man Ral's basement.");
		scentedLegs = new ItemRequirement("Vyre noble legs", ItemID.VYRE_NOBLE_LEGS, 1, true);
		scentedLegs.setTooltip("You can get a replacement from a chest in Old Man Ral's basement.");
		scentedShoes = new ItemRequirement("Vyre noble shoes", ItemID.VYRE_NOBLE_SHOES, 1, true);
		scentedShoes.setTooltip("You can get a replacement from a chest in Old Man Ral's basement.");
		oldNote = new ItemRequirement("Old note", ItemID.OLD_NOTE);
		oldNote.setHighlightInInventory(true);
	}

	protected void setupZones()
	{
		northValveArea = new Zone(new WorldPoint(3619, 3362, 0), new WorldPoint(3624, 3365, 0));
		southValveArea = new Zone(new WorldPoint(3618, 3358, 0), new WorldPoint(3624, 3361, 0));
	}

	protected void setupConditions()
	{
		atNorthValve = new ZoneRequirement(northValveArea);
		atSouthValve = new ZoneRequirement(southValveArea);
	}

	@Override
	protected void setupSteps()
	{
		setupRequirements();
		setupZones();
		setupConditions();

		readNote = new DetailedQuestStep(getQuestHelper(), "Read the Old Note to find out the amount of water the Blisterwood Tree needs.", oldNote, scentedTop, scentedLegs, scentedShoes);

		setNorthValve = new ObjectStep(getQuestHelper(), ObjectID.VALVE_37997, new WorldPoint(3621, 3364, 0), "Turn the northern valve to the highlighted value.");
		setSouthValve = new ObjectStep(getQuestHelper(), ObjectID.VALVE_37998, new WorldPoint(3621, 3359, 0), "Turn the southern valve to the highlighted value.");
		setNorthValveNoHighlight = new ObjectStep(getQuestHelper(), ObjectID.VALVE_37997, new WorldPoint(3621, 3364, 0), "Turn the northern valve to the highlighted value.");
		setSouthValveNoHighlight = new ObjectStep(getQuestHelper(), ObjectID.VALVE_37998, new WorldPoint(3621, 3359, 0), "Turn the southern valve to the highlighted value.");

		cutTree =  new ObjectStep(getQuestHelper(), ObjectID.BLISTERWOOD_TREE, new WorldPoint(3635, 3362, 0),
			"Gather 8 logs from the Blisterwood tree.", scentedTop, scentedLegs, scentedShoes);
	}

	@Override
	public Collection<QuestStep> getSteps()
	{
		return Arrays.asList(readNote, setNorthValve, setSouthValve, setNorthValveNoHighlight, setSouthValveNoHighlight, cutTree);
	}
}
