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
package com.questhelper.helpers.quests.dragonslayerii;

import com.google.inject.Inject;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.questhelpers.QuestUtil;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.steps.DetailedOwnerStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import net.runelite.api.Client;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;

import java.util.*;

public class CryptPuzzle extends DetailedOwnerStep
{
	@Inject
	protected EventBus eventBus;

	@Inject
	protected Client client;

	private final int TRISTAN = 1;
	private final int CAMORRA = 2;
	private final int AIVAS = 3;
	private final int ROBERT = 4;

	Integer northBust, eastBust, southBust, westBust;

	private final HashMap<String, Integer> locations = new HashMap<>();
	private final HashMap<String, Integer> weaponsSouth = new HashMap<>();
	private final HashMap<String, Integer> weaponsWest = new HashMap<>();

	private final HashMap<Integer, QuestStep> getBustSteps = new HashMap<>();
	private final HashMap<Integer, ItemRequirement> items = new HashMap<>();
	private final HashMap<Integer, ItemRequirement> bustsConditions = new HashMap<>();


	private boolean solutionFound;

	ItemRequirement aivasBust, camorraBust, robertBust, tristanBust;

	Requirement hasAivasBust, hasRobertBust, hasCamorraBust, hasTristanBust;

	DetailedQuestStep takeCamorraBust, takeAivasBust, takeRobertBust, takeTristanBust, placeBustNorth, placeBustSouth, placeBustEast, placeBustWest, inspectTomb;

	Zone firstFloor, basement, secondFloor;

	// Find match, set bust to take to correct step, set bust to use to correct ItemRequirement
	public CryptPuzzle(QuestHelper questHelper)
	{
		super(questHelper, "Solve the bust puzzle.");

		locations.put("Zartharim sat at the north of the table", AIVAS);
		locations.put("Saranthium sat at the north of the table", CAMORRA);
		locations.put("Arkney sat at the north of the table", ROBERT);
		locations.put("Karville sat at the north of the table", TRISTAN);

		weaponsSouth.put("opposite the one with the crossbow", AIVAS);
		weaponsSouth.put("opposite the one with the axe", CAMORRA);
		weaponsSouth.put("opposite the one with the bow", ROBERT);
		weaponsSouth.put("opposite the one with the sword", TRISTAN);

		weaponsWest.put("The one with a crossbow asked the", AIVAS);
		weaponsWest.put("The one with an axe asked the", CAMORRA);
		weaponsWest.put("The one with a bow asked the", ROBERT);
		weaponsWest.put("The one with a sword asked the", TRISTAN);

		getBustSteps.put(AIVAS, takeAivasBust);
		getBustSteps.put(CAMORRA, takeCamorraBust);
		getBustSteps.put(ROBERT, takeRobertBust);
		getBustSteps.put(TRISTAN, takeTristanBust);

		bustsConditions.put(AIVAS, aivasBust);
		bustsConditions.put(CAMORRA, camorraBust);
		bustsConditions.put(ROBERT, robertBust);
		bustsConditions.put(TRISTAN, tristanBust);

		items.put(AIVAS, aivasBust);
		items.put(CAMORRA, camorraBust);
		items.put(ROBERT, robertBust);
		items.put(TRISTAN, tristanBust);
	}

	@Override
	public void startUp()
	{
		updateSteps();
	}

	@Override
	public void shutDown()
	{
		shutDownStep();
		currentStep = null;
	}

	@Subscribe
	public void onGameTick(GameTick ignoredEvent)
	{
		updateSteps();
	}

	protected void updateSteps()
	{
		if (!solutionFound)
		{
			startUpStep(inspectTomb);
			return;
		}
		int currentNorthBust = client.getVarbitValue(6152);
		int currentEastBust = client.getVarbitValue(6154);
		int currentSouthBust = client.getVarbitValue(6153);
		int currentWestBust = client.getVarbitValue(6155);

		if (currentNorthBust != northBust)
		{
			if (!bustsConditions.get(northBust).check(client))
			{
				startUpStep(getBustSteps.get(northBust));
			}
			else
			{
				startUpStep(placeBustNorth);
			}
		}
		else if (currentEastBust != eastBust)
		{
			if (!bustsConditions.get(eastBust).check(client))
			{
				startUpStep(getBustSteps.get(eastBust));
			}
			else
			{
				startUpStep(placeBustEast);
			}
		}
		else if (currentSouthBust != southBust)
		{
			if (!bustsConditions.get(southBust).check(client))
			{
				startUpStep(getBustSteps.get(southBust));
			}
			else
			{
				startUpStep(placeBustSouth);
			}
		}
		else if (currentWestBust != westBust)
		{
			if (!bustsConditions.get(westBust).check(client))
			{
				startUpStep(getBustSteps.get(westBust));
			}
			else
			{
				startUpStep(placeBustWest);
			}
		}
		else
		{
			startUpStep(inspectTomb);
		}
	}

	private void setupItemRequirements()
	{
		aivasBust = new ItemRequirement("Aivas bust", ItemID.DS2_BUST_AIVAS);
		aivasBust.setHighlightInInventory(true);
		robertBust = new ItemRequirement("Robert bust", ItemID.DS2_BUST_ROBERT);
		robertBust.setHighlightInInventory(true);
		tristanBust = new ItemRequirement("Trisan bust", ItemID.DS2_BUST_TRISTAN);
		tristanBust.setHighlightInInventory(true);
		camorraBust = new ItemRequirement("Camorra bust", ItemID.DS2_BUST_CAMORRA);
		camorraBust.setHighlightInInventory(true);
	}

	private void setupConditions()
	{
		hasAivasBust = aivasBust;
		hasRobertBust = robertBust;
		hasCamorraBust = camorraBust;
		hasTristanBust = tristanBust;
	}

	@Override
	protected void setupSteps()
	{
		inspectTomb = new ObjectStep(getQuestHelper(), ObjectID.DS2_TOMBSTONE_CAMORRA, new WorldPoint(1504, 9939, 1), "Inspect the tomb in the south room.");


		takeTristanBust = new ObjectStep(getQuestHelper(), ObjectID.DS2_TOMB_BUST_TRISTAN, new WorldPoint(1507, 9941, 1), "Take Tristan's Bust.");
		takeAivasBust = new ObjectStep(getQuestHelper(), ObjectID.DS2_TOMB_BUST_AIVAS, new WorldPoint(1500, 9941, 1), "Take Aivas' Bust.");
		takeRobertBust = new ObjectStep(getQuestHelper(), ObjectID.DS2_TOMB_BUST_ROBERT, new WorldPoint(1500, 9936, 1), "Take Robert's Bust.");
		takeCamorraBust = new ObjectStep(getQuestHelper(), ObjectID.DS2_TOMB_BUST_CAMORRA, new WorldPoint(1507, 9936, 1), "Take Camorra's Bust.");
		placeBustNorth = new ObjectStep(getQuestHelper(), ObjectID.DS2_TOMB_PLINTH_NORTH, new WorldPoint(1504, 9941, 1), "Place the bust on the north plinth.");
		placeBustEast = new ObjectStep(getQuestHelper(), ObjectID.DS2_TOMB_PLINTH_EAST, new WorldPoint(1506, 9939, 1), "Place the bust on the east plinth.");
		placeBustSouth = new ObjectStep(getQuestHelper(), ObjectID.DS2_TOMB_PLINTH_SOUTH, new WorldPoint(1504, 9936, 1), "Place the bust on the south plinth.");
		placeBustWest = new ObjectStep(getQuestHelper(), ObjectID.DS2_TOMB_PLINTH_WEST, new WorldPoint(1501, 9939, 1), "Place the bust on the west plinth.");

		setupItemRequirements();
		setupConditions();
	}

	@Override
	public Collection<QuestStep> getSteps()
	{
		return Arrays.asList(inspectTomb, takeAivasBust, takeCamorraBust, takeRobertBust, takeTristanBust, placeBustEast, placeBustWest, placeBustNorth, placeBustSouth);
	}

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded widgetLoaded)
	{
		if (!solutionFound && widgetLoaded.getGroupId() == 748)
		{
			List<Integer> potentialBusts = QuestUtil.toArrayList(AIVAS, CAMORRA, ROBERT, TRISTAN);
			Widget parentWidget = client.getWidget(InterfaceID.Stoneplaque.CONTENT);
			if (parentWidget == null || parentWidget.getStaticChildren() == null) return;

			StringBuilder storyString = new StringBuilder();

			for (Widget child : parentWidget.getStaticChildren())
			{
				storyString.append(child.getText()).append(" ");
			}

			String fullStory = storyString.toString();

			northBust = locations.entrySet().stream()
				.filter(e -> fullStory.contains(e.getKey()))
				.map(Map.Entry::getValue).findFirst()
				.orElse(null);

			southBust = weaponsSouth.entrySet().stream()
				.filter(e -> fullStory.contains(e.getKey()))
				.map(Map.Entry::getValue).findFirst()
				.orElse(null);

			westBust = weaponsWest.entrySet().stream()
				.filter(e -> fullStory.contains(e.getKey()))
				.map(Map.Entry::getValue).findFirst()
				.orElse(null);

			if (northBust == null || southBust == null || westBust == null)
			{
				return;
			}

			potentialBusts.remove(northBust);
			potentialBusts.remove(southBust);
			potentialBusts.remove(westBust);

			eastBust = potentialBusts.iterator().next();

			placeBustNorth.addItemRequirements(Collections.singletonList(items.get(northBust)));
			placeBustNorth.addIcon(items.get(northBust).getId());
			placeBustEast.addItemRequirements(Collections.singletonList(items.get(eastBust)));
			placeBustEast.addIcon(items.get(eastBust).getId());
			placeBustSouth.addItemRequirements(Collections.singletonList(items.get(southBust)));
			placeBustSouth.addIcon(items.get(southBust).getId());
			placeBustWest.addItemRequirements(Collections.singletonList(items.get(westBust)));
			placeBustWest.addIcon(items.get(westBust).getId());
			solutionFound = true;
		}
	}
}
