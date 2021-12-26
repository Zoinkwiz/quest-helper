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
package com.questhelper.quests.songoftheelves;

import com.google.inject.Inject;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.steps.DetailedOwnerStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import net.runelite.api.Client;
import net.runelite.api.GraphicID;
import net.runelite.api.GraphicsObject;
import net.runelite.api.ItemID;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.GraphicsObjectCreated;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import org.apache.commons.lang3.StringUtils;

public class BaxtorianPuzzle extends DetailedOwnerStep
{
	@Inject
	protected EventBus eventBus;

	@Inject
	protected Client client;

	private HashMap<String, ItemRequirement> items;
	private ArrayList<BaxtorianPillar> pillars;
	private ArrayList<String> unknownItems;

	ItemRequirement natureRune, flowersOrIrit, blackKnifeOrDagger, wineOfZamorakOrZamorakBrew, cabbage, adamantChainbody, blackKnife, blackDagger, wineOfZamorak, zamorakBrew, flowers, iritLeaf;

	boolean foundFinalItem;

	public BaxtorianPuzzle(QuestHelper questHelper)
	{
		super(questHelper, "Solve the pillar puzzle.");
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
	public void onGameTick(GameTick event)
	{
		updateSteps();
	}

	protected void updateSteps()
	{
		for (BaxtorianPillar pillar : pillars)
		{
			if (pillar.getAnswerText() == null)
			{
				startUpStep(pillar.getUseStep());
				return;
			}
			else if (pillar.getSolution() == null)
			{
				startUpStep(pillar.getInspectStep());
				return;
			}
			else if (pillar.getSolution() != pillar.getPlacedItem())
			{
				startUpStep(pillar.getUseStep());
				return;
			}
		}
	}

	private void checkHint(String hintText)
	{
		if (hintText == null)
		{
			return;
		}

		String id;

		for (BaxtorianPillar pillar : pillars)
		{
			if (pillar.getAnswerText() != null && hintText.contains(pillar.getAnswerText()))
			{
				id = hintText.replace(pillar.getAnswerText(), "");
				pillar.setSolution(items.get(id));

				unknownItems.remove(id);
				if (!foundFinalItem && unknownItems.size() == 1)
				{
					foundFinalItem = true;
					pillars.get(pillars.size() - 1).setSolution(items.get(unknownItems.get(0)));
				}
				return;
			}
		}
	}

	@Subscribe
	public void onGraphicsObjectCreated(GraphicsObjectCreated event)
	{
		final GraphicsObject go = event.getGraphicsObject();

		if (go.getId() == GraphicID.GREY_BUBBLE_TELEPORT)
		{
			clientThread.invokeLater(() ->
			{
				Widget itemPlacedWidget = client.getWidget(WidgetInfo.DIALOG_SPRITE_TEXT);

				if (itemPlacedWidget == null)
				{
					return;
				}

				LocalPoint smokePoint = go.getLocation();
				WorldPoint worldSmokePoint = WorldPoint.fromLocal(client, smokePoint);

				for (BaxtorianPillar pillar : pillars)
				{
					if (worldSmokePoint.equals(pillar.getWp()))
					{
						checkItemPlaced(itemPlacedWidget.getText(), pillar);
						return;
					}
				}
			});
		}
	}

	private void checkItemPlaced(String itemPlacedText, BaxtorianPillar pillar)
	{
		if (itemPlacedText == null)
		{
			return;
		}

		String textStart = "You place the ";
		String textEnd = " on the pillar.";

		String itemPlaced = StringUtils.substringBetween(itemPlacedText, textStart, textEnd);

		for (ItemRequirement item : items.values())
		{
			if (checkIfItemMatches(itemPlaced, item))
			{
				pillar.setPlacedItem(item);
			}
		}
	}

	private boolean checkIfItemMatches(String itemPlaced, ItemRequirement solutionItem)
	{
		if (solutionItem instanceof ItemRequirements)
		{
			ItemRequirements solutionItems = (ItemRequirements) solutionItem;
			for (ItemRequirement itemRequirement : solutionItems.getItemRequirements())
			{
				if (checkIfItemMatches(itemPlaced, itemRequirement))
				{
					return true;
				}
			}
		}
		else
		{
			return itemPlaced.contains(solutionItem.getName());
		}

		return false;
	}

	private void setupItemRequirements()
	{
		natureRune = new ItemRequirement("Nature rune", ItemID.NATURE_RUNE);
		natureRune.setHighlightInInventory(true);

		flowers = new ItemRequirements(LogicType.OR, "Flowers",
			new ItemRequirement("Assorted flowers", ItemID.ASSORTED_FLOWERS),
			new ItemRequirement("Black flowers", ItemID.BLACK_FLOWERS),
			new ItemRequirement("Blue flowers", ItemID.BLUE_FLOWERS),
			new ItemRequirement("Exotic flowers", ItemID.EXOTIC_FLOWER),
			new ItemRequirement("Marigolds", ItemID.MARIGOLDS),
			new ItemRequirement("Mixed flowers", ItemID.MIXED_FLOWERS),
			new ItemRequirement("Purple flowers", ItemID.PURPLE_FLOWERS),
			new ItemRequirement("Orange flowers", ItemID.ORANGE_FLOWERS),
			new ItemRequirement("Red flowers", ItemID.RED_FLOWERS),
			new ItemRequirement("White flowers", ItemID.WHITE_FLOWERS),
			new ItemRequirement("Yellow flowers", ItemID.YELLOW_FLOWERS)
		);

		ItemRequirement grimyIritLeaf = new ItemRequirement("Grimy irit leaf", ItemID.GRIMY_IRIT_LEAF);
		ItemRequirement cleanIritLeaf = new ItemRequirement("Irit leaf", ItemID.IRIT_LEAF);

		iritLeaf = new ItemRequirements(LogicType.OR, "Irit leaf", cleanIritLeaf, grimyIritLeaf);
		iritLeaf.setDisplayMatchedItemName(true);
		iritLeaf.setTooltip("Grimy Irit Leaf is also valid.");

		flowersOrIrit = new ItemRequirements(LogicType.OR, "Irit leaf or a flower", iritLeaf, flowers);
		flowersOrIrit.setHighlightInInventory(true);

		adamantChainbody = new ItemRequirement("Adamant chainbody", ItemID.ADAMANT_CHAINBODY);
		adamantChainbody.setHighlightInInventory(true);

		wineOfZamorak = new ItemRequirement("Wine of zamorak", ItemID.WINE_OF_ZAMORAK);

		ItemRequirement zamorakBrew1 = new ItemRequirement("Zamorak brew(1)", ItemID.ZAMORAK_BREW1);
		ItemRequirement zamorakBrew2 = new ItemRequirement("Zamorak brew(2)", ItemID.ZAMORAK_BREW2);
		ItemRequirement zamorakBrew3 = new ItemRequirement("Zamorak brew(3)", ItemID.ZAMORAK_BREW3);
		ItemRequirement zamorakBrew4 = new ItemRequirement("Zamorak brew(4)", ItemID.ZAMORAK_BREW4);

		zamorakBrew = new ItemRequirements("Zamorak brew", zamorakBrew1, zamorakBrew2, zamorakBrew3, zamorakBrew4);
		zamorakBrew.setDisplayMatchedItemName(true);

		wineOfZamorakOrZamorakBrew = new ItemRequirements(LogicType.OR, "Wine of zamorak or Zamorak brew", wineOfZamorak, zamorakBrew);
		wineOfZamorakOrZamorakBrew.setHighlightInInventory(true);

		cabbage = new ItemRequirement("Cabbage", ItemID.CABBAGE);
		cabbage.setHighlightInInventory(true);

		ItemRequirement blackKnifeClean = new ItemRequirement("Black knife", ItemID.BLACK_KNIFE);
		ItemRequirement blackKnifeP = new ItemRequirement("Black knife(p)", ItemID.BLACK_KNIFEP);
		ItemRequirement blackKnifePplus = new ItemRequirement("Black knife(p+)", ItemID.BLACK_KNIFEP_5658);
		ItemRequirement blackKnifePplusPlus = new ItemRequirement("Black knife(p++)", ItemID.BLACK_KNIFEP_5665);

		blackKnife = new ItemRequirements(LogicType.OR, "Black knife", blackKnifeClean, blackKnifeP, blackKnifePplus,
			blackKnifePplusPlus);
		blackKnife.setHighlightInInventory(true);

		ItemRequirement blackDaggerClean = new ItemRequirement("Black dagger", ItemID.BLACK_DAGGER);
		ItemRequirement blackDaggerP = new ItemRequirement("Black dagger(p)", ItemID.BLACK_DAGGERP);
		ItemRequirement blackDaggerPplus = new ItemRequirement("Black dagger(p+)", ItemID.BLACK_DAGGERP_5682);
		ItemRequirement blackDaggerPplusPlus = new ItemRequirement("Black dagger(p++)", ItemID.BLACK_DAGGERP_5700);
		blackDagger = new ItemRequirements(LogicType.OR, "Black dagger", blackDaggerClean, blackDaggerP, blackDaggerPplus,
		blackDaggerPplusPlus);

		blackKnifeOrDagger = new ItemRequirements(LogicType.OR, "Black knife or black dagger", blackKnife, blackDagger);
		blackKnifeOrDagger.setHighlightInInventory(true);
	}


	@Override
	protected void setupSteps()
	{
		items = new HashMap<>();
		pillars = new ArrayList<>();
		unknownItems = new ArrayList<>();
		unknownItems.addAll(Arrays.asList("1st.", "2nd.", "3rd.", "4th.", "5th.", "6th."));

		String SOUTHWEST_ID = "south west";
		pillars.add(new BaxtorianPillar(getQuestHelper(), new WorldPoint(2600, 9909, 0), new WorldPoint(2600, 9909, 0), "I am the ", SOUTHWEST_ID));
		String WEST_ID = "west";
		pillars.add(new BaxtorianPillar(getQuestHelper(), new WorldPoint(2600, 9913, 0), new WorldPoint(2600, 9911, 0),  "I am next to the ", WEST_ID));
		String NORTHWEST_ID = "north west";
		pillars.add(new BaxtorianPillar(getQuestHelper(), new WorldPoint(2607, 9913, 0), new WorldPoint(2600, 9913, 0), "I am opposite the ", NORTHWEST_ID));
		String EAST_ID = "east";
		pillars.add(new BaxtorianPillar(getQuestHelper(), new WorldPoint(2607, 9911, 0), new WorldPoint(2607, 9911, 0), "I am not next to the ", EAST_ID));
		String NORTHEAST_ID = "north east";
		pillars.add(new BaxtorianPillar(getQuestHelper(), new WorldPoint(2607, 9909, 0), new WorldPoint(2607, 9913, 0), "I am not the ", NORTHEAST_ID));
		String SOUTHEAST_ID = "south east";
		pillars.add(new BaxtorianPillar(getQuestHelper(), new WorldPoint(2607, 9909, 0), new WorldPoint(2607, 9909, 0), null, SOUTHEAST_ID));

		setupItemRequirements();

		items.put("1st.", natureRune);
		items.put("2nd.", flowersOrIrit);
		items.put("3rd.", blackKnifeOrDagger);
		items.put("4th.", wineOfZamorakOrZamorakBrew);
		items.put("5th.", adamantChainbody);
		items.put("6th.", cabbage);
	}

	@Override
	public Collection<QuestStep> getSteps()
	{
		List<QuestStep> steps = new ArrayList<>();
		for (BaxtorianPillar pillar : pillars)
		{
			steps.add(pillar.getInspectStep());
			steps.add(pillar.getUseStep());
		}
		return steps;
	}

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded widgetLoaded)
	{
		if (widgetLoaded.getGroupId() == 229)
		{
			Widget hintWidget = client.getWidget(229, 1);

			if (hintWidget != null)
			{
				clientThread.invokeLater(() -> checkHint(hintWidget.getText()));
			}
		}
	}
}
