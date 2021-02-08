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
package com.questhelper.quests.ghostsahoy;

import com.questhelper.Zone;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.steps.DetailedOwnerStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.eventbus.Subscribe;

public class DyeShipSteps extends DetailedOwnerStep
{
	boolean coloursKnown = false;

	HashMap<String, FlagColour> shapeColours = new HashMap<>();
	HashMap<String, FlagColour> currentColours = new HashMap<>();

	DetailedQuestStep searchMast, dyeTop, dyeSkull, dyeBottom, goDownToMan, talkToMan, goUpToMan, goUpToDeckForMast, goUpToMast;

	Requirement onTopOfShip, onDeck;

	Zone topOfShip, deck;

	ItemRequirement modelShip;

	public DyeShipSteps(QuestHelper questHelper)
	{
		super(questHelper);

		topOfShip = new Zone(new WorldPoint(3616, 3541, 2), new WorldPoint(3622, 3545, 2));
		deck = new Zone(new WorldPoint(3600, 3541, 1), new WorldPoint(3623, 3545, 1));
		onTopOfShip = new ZoneRequirement(topOfShip);
		onDeck = new ZoneRequirement(deck);

		shapeColours.put("skull", FlagColour.WHITE);
		shapeColours.put("top", FlagColour.WHITE);
		shapeColours.put("bottom", FlagColour.WHITE);

		currentColours.put("skull", FlagColour.WHITE);
		currentColours.put("top", FlagColour.WHITE);
		currentColours.put("bottom", FlagColour.WHITE);
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		updateSteps();
	}

	private void updateCurrentColours()
	{
		Widget dyed = client.getWidget(WidgetInfo.DIALOG_SPRITE_TEXT);
		if (dyed == null)
		{
			return;
		}

		String text = dyed.getText();
		if (text.isEmpty())
		{
			return;
		}
		String[] splitOnNewLines = text.split("<br>");
		if (splitOnNewLines.length > 1)
		{
			for (String splitOnNewLine : splitOnNewLines)
			{
				updateCurrentColoursFromString(splitOnNewLine);
			}
		}

		String[] splitText = text.split("dye the ");
		if (splitText.length < 2)
		{
			return;
		}

		updateCurrentColoursFromString(splitText[1]);
	}

	private void updateCurrentColoursFromString(String text)
	{
		String[] shapeAndColour = text.split(" (emblem|of the flag) ");
		if (shapeAndColour.length < 2)
		{
			return;
		}
		String shape = shapeAndColour[0];
		String colour = shapeAndColour[1];
		shape = shape.replace("The ", "");
		colour = colour.replace("is ", "");
		currentColours.put(shape, FlagColour.findByKey(colour));
	}

	public void updateSteps()
	{
		updateCurrentColours();
		updateColours();
		FlagColour topColour = shapeColours.get("top");
		FlagColour bottomColour = shapeColours.get("bottom");
		FlagColour skullColour = shapeColours.get("skull");
		FlagColour currentTopColour = currentColours.get("top");
		FlagColour currentBottomColour = currentColours.get("bottom");
		FlagColour currentSkullColour = currentColours.get("skull");
		if (!coloursKnown &&
			topColour != FlagColour.WHITE &&
			skullColour != FlagColour.WHITE &&
			bottomColour != FlagColour.WHITE)
		{
			coloursKnown = true;
			dyeTop.setRequirements(Arrays.asList(topColour.getItem(), modelShip));
			dyeTop.setText("Dye the top of the model ship's flag " + topColour.getColourText() + " If you already have, inspect the ship.");
			dyeTop.addDialogStep("Top half");
			dyeBottom.setRequirements(Arrays.asList(bottomColour.getItem(), modelShip));
			dyeBottom.setText("Dye the bottom of the model ship's flag " + bottomColour.getColourText() + " If you already have, inspect the ship.");
			dyeBottom.addDialogStep("Bottom half");
			dyeSkull.setRequirements(Arrays.asList(skullColour.getItem(), modelShip));
			dyeSkull.addDialogStep("Skull emblem");
			dyeSkull.setText("Dye the skull on the model ship's flag " + skullColour.getColourText() + " If you already have, inspect the ship.");
		}
		if (!coloursKnown)
		{
			if (onDeck.check(client))
			{
				startUpStep(goUpToMast);
			}
			else if (onTopOfShip.check(client))
			{
				startUpStep(searchMast);
			}
			else
			{
				startUpStep(goUpToDeckForMast);
			}
			return;
		}

		if (topColour != currentTopColour)
		{
			startUpStep(dyeTop);
		}
		else if (bottomColour != currentBottomColour)
		{
			startUpStep(dyeBottom);
		}
		else if (skullColour != currentSkullColour)
		{
			startUpStep(dyeSkull);
		}
		else if (onTopOfShip.check(client))
		{
			startUpStep(goDownToMan);
		}
		else if (onDeck.check(client))
		{
			startUpStep(talkToMan);
		}
		else
		{
			startUpStep(goUpToMan);
		}
	}

	public void updateColours()
	{
		Widget textWidget = client.getWidget(229, 1);
		if (textWidget != null)
		{
			String text = textWidget.getText();
			if (text.isEmpty())
			{
				return;
			}
			String[] splitText = text.split("The ");
			if (splitText.length < 2)
			{
				return;
			}
			String mainText = splitText[1];
			if (!mainText.contains("coloured"))
			{
				return;
			}

			String[] shapeAndColour = mainText.split(" (emblem|half of the flag) is coloured ");
			if (shapeAndColour.length < 2)
			{
				return;
			}
			String shape = shapeAndColour[0];
			String colour = shapeAndColour[1];
			shapeColours.put(shape, FlagColour.findByKey(colour));
		}
	}

	@Override
	protected void setupSteps()
	{
		modelShip = new ItemRequirement("Model ship", ItemID.MODEL_SHIP_4254);
		modelShip.setHighlightInInventory(true);

		searchMast = new ObjectStep(getQuestHelper(), ObjectID.MAST_16640, new WorldPoint(3619, 3543, 2), "Search the Mast repeatedly until you've found out all the colours for the toy boat.");
		dyeTop = new DetailedQuestStep(getQuestHelper(), "Dye the top of the model ship's flag to match the real ship.", modelShip);
		dyeBottom = new DetailedQuestStep(getQuestHelper(), "Dye the bottom of the model ship's flag to match the real ship.", modelShip);
		dyeSkull = new DetailedQuestStep(getQuestHelper(), "Dye the skull of the model ship's flag to match the real ship.", modelShip);
		talkToMan = new NpcStep(getQuestHelper(), NpcID.OLD_MAN, new WorldPoint(3616, 3543, 1), "Talk to the Old Man with the model ship to get a key.");
		talkToMan.addDialogStep("Is this your toy boat?");
		goDownToMan = new ObjectStep(getQuestHelper(), ObjectID.SHIPS_LADDER_16112, new WorldPoint(3615, 3541, 2), "Go to the main deck of the ship.");
		goUpToMan = new ObjectStep(getQuestHelper(), ObjectID.SHIPS_LADDER_16111, new WorldPoint(3613, 3543, 0), "Go up the ladder in the ship west of Port Phasmatys.");
		goDownToMan.addSubSteps(goUpToMan);

		goUpToDeckForMast = new ObjectStep(getQuestHelper(), ObjectID.SHIPS_LADDER_16111, new WorldPoint(3613, 3543, 0),
			"Go up the ladder in the ship west of Port Phasmatys.");
		goUpToMast = new ObjectStep(getQuestHelper(), ObjectID.SHIPS_LADDER_16111, new WorldPoint(3615, 3541, 1),
			"Go up to the mast of the ship.");
	}

	@Override
	public Collection<QuestStep> getSteps()
	{
		return Arrays.asList(searchMast, dyeTop, dyeBottom, dyeSkull, talkToMan, goDownToMan, goUpToMan, goUpToDeckForMast, goUpToMast);
	}

	public List<QuestStep> getDisplaySteps()
	{
		return Arrays.asList(goUpToDeckForMast, goUpToMast, searchMast, dyeTop, dyeBottom, dyeSkull, goDownToMan,
			talkToMan);
	}

	private enum FlagColour
	{
		RED("red.", new ItemRequirement("Red dye", ItemID.RED_DYE)),
		BLUE("blue.", new ItemRequirement("Blue dye", ItemID.BLUE_DYE)),
		YELLOW("yellow.", new ItemRequirement("Yellow dye", ItemID.YELLOW_DYE)),
		GREEN("green.", new ItemRequirement("Green dye", ItemID.GREEN_DYE)),
		PURPLE("purple.", new ItemRequirement("Purple dye", ItemID.PURPLE_DYE)),
		ORANGE("orange.", new ItemRequirement("Orange dye", ItemID.ORANGE_DYE)),
		WHITE("white.", new ItemRequirement("White", -1, -1));

		private final String colourText;
		private final ItemRequirement item;

		FlagColour(String colourText, ItemRequirement item)
		{
			this.colourText = colourText;
			item.setHighlightInInventory(true);
			this.item = item;
		}

		public String getColourText()
		{
			return colourText;
		}

		public ItemRequirement getItem()
		{
			return item;
		}

		public static FlagColour findByKey(String colour) {
			FlagColour[] flagColours = FlagColour.values();
			for (FlagColour flagColour : flagColours) {
				if (flagColour.colourText.equals(colour)) {
					return flagColour;
				}
			}
			return null;
		}
	}
}

