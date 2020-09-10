package com.questhelper.quests.rumdeal;

import com.questhelper.requirements.ItemRequirement;
import com.questhelper.Zone;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedOwnerStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.conditional.ConditionForStep;
import com.questhelper.steps.conditional.ZoneCondition;
import java.util.Arrays;
import java.util.Collection;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;

public class SlugSteps extends DetailedOwnerStep
{

	DetailedQuestStep addSluglings, talkToPete, goDownFromTop, fish5Slugs, goDownToSluglings, goUpFromSluglings, goUpToDropSluglings, goUpF1ToPressure, goUpToF2ToPressure, pressure;
	ConditionalStep getSluglings, pressureSluglings, pullPressureLever;

	Zone islandF0, islandF1, islandF2;

	ConditionForStep onIslandF0, onIslandF1, onIslandF2;


	// TODO: Support Karamthulu being used as well
	ItemRequirement sluglings = new ItemRequirement("Sluglings or Karamthulu", ItemID.SLUGLINGS, 5);
	ItemRequirement sluglingsHighlight = new ItemRequirement("Sluglings or Karamthulu", ItemID.SLUGLINGS, 5);
	ItemRequirement netBowl = new ItemRequirement("Fishbowl and net", ItemID.FISHBOWL_AND_NET);


	public SlugSteps(QuestHelper questHelper)
	{
		super(questHelper);

		netBowl.setTip("You can get another from Captain Braindeath, or make it with a fishbowl and large net");
		sluglingsHighlight.setHighlightInInventory(true);
		sluglingsHighlight.addAlternates(ItemID.KARAMTHULHU, ItemID.KARAMTHULHU_6717);
		sluglings.addAlternates(ItemID.KARAMTHULHU, ItemID.KARAMTHULHU_6717);


		islandF0 = new Zone(new WorldPoint(2110, 5054, 0), new WorldPoint(2178, 5185, 0));
		islandF1 = new Zone(new WorldPoint(2110, 5054, 1), new WorldPoint(2178, 5185, 1));
		islandF2 = new Zone(new WorldPoint(2110, 5054, 2), new WorldPoint(2178, 5185, 2));
		onIslandF0 = new ZoneCondition(islandF0);
		onIslandF1 = new ZoneCondition(islandF1);
		onIslandF2 = new ZoneCondition(islandF2);
		setupSteps();
	}

	@Override
	public void startUp()
	{
		updateSteps();
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		updateSteps();
	}

	protected void updateSteps()
	{
		int numHandedIn = client.getVarbitValue(1354);
		ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);

		int numInInv = 0;

		if (inventory != null)
		{
			Item[] inventoryItems = inventory.getItems();
			for (Item item : inventoryItems)
			{
				if (item.getId() == sluglings.getId())
				{
					numInInv++;
				}
			}
		}
		sluglings.setQuantity(5 - numHandedIn);
		if (numHandedIn >= 5)
		{
			startUpStep(pullPressureLever);
		}
		else if (numHandedIn + numInInv >= 5)
		{
			startUpStep(pressureSluglings);
		}
		else
		{
			startUpStep(getSluglings);
		}
	}

	private void setupSteps()
	{
		talkToPete = new NpcStep(getQuestHelper(), NpcID.PIRATE_PETE, new WorldPoint(3680, 3537, 0), "Talk to Pirate Pete north east of the Ectofuntus.");
		talkToPete.addDialogSteps("Okay!");
		addSluglings = new ObjectStep(getQuestHelper(), ObjectID.PRESSURE_BARREL, new WorldPoint(2142, 5102, 2),
			"Add the sea creatures to the pressure barrel on the top floor.", sluglings);
		addSluglings.addIcon(ItemID.SLUGLINGS);
		goDownFromTop = new ObjectStep(getQuestHelper(), ObjectID.LADDER_10168, new WorldPoint(2163, 5092, 2), "Go down the ladder and fish for sea creatures.");

		fish5Slugs = new NpcStep(getQuestHelper(), NpcID.FISHING_SPOT, "Fish 5 sluglings or karamthulu from around the coast of the island.", netBowl);
		goDownToSluglings = new ObjectStep(getQuestHelper(), ObjectID.WOODEN_STAIR_10137, new WorldPoint(2150, 5088, 1), "Go fish 5 sluglings.", netBowl);
		goUpFromSluglings = new ObjectStep(getQuestHelper(), ObjectID.WOODEN_STAIR, new WorldPoint(2150, 5088, 0),
			"Add the sea creatures to the pressure barrel on the top floor.", sluglings);

		fish5Slugs.addSubSteps(goDownFromTop, goDownToSluglings, talkToPete);

		goUpToDropSluglings = new ObjectStep(getQuestHelper(), ObjectID.LADDER_10167, new WorldPoint(2163, 5092, 1),
			"Add the sea creatures to the pressure barrel on the top floor.", sluglings);

		goUpFromSluglings = new ObjectStep(getQuestHelper(), ObjectID.WOODEN_STAIR, new WorldPoint(2150, 5088, 0),
			"Go to the top floor to pull the pressure lever.", sluglings);
		goUpF1ToPressure = new ObjectStep(getQuestHelper(), ObjectID.WOODEN_STAIR, new WorldPoint(2150, 5088, 0),
			"Go to the top floor to pull the pressure lever.");
		goUpToF2ToPressure = new ObjectStep(getQuestHelper(), ObjectID.LADDER_10167, new WorldPoint(2163, 5092, 1),
			"Go to the top floor to pull the pressure lever.");

		pressure = new ObjectStep(getQuestHelper(), NullObjectID.NULL_10164, new WorldPoint(2141, 5103, 2), "Pull the pressure lever.");
		pressure.addSubSteps(goUpToF2ToPressure, goUpF1ToPressure);

		getSluglings = new ConditionalStep(getQuestHelper(), talkToPete);
		getSluglings.addStep(onIslandF0, fish5Slugs);
		getSluglings.addStep(onIslandF1, goDownToSluglings);
		getSluglings.addStep(onIslandF2, goDownToSluglings);

		pressureSluglings = new ConditionalStep(getQuestHelper(), talkToPete);
		pressureSluglings.addStep(onIslandF2, addSluglings);
		pressureSluglings.addStep(onIslandF1, goUpToDropSluglings);
		pressureSluglings.addStep(onIslandF0, goUpFromSluglings);

		pullPressureLever = new ConditionalStep(getQuestHelper(), talkToPete);
		pullPressureLever.addStep(onIslandF2, pressure);
		pullPressureLever.addStep(onIslandF1, goUpToF2ToPressure);
		pullPressureLever.addStep(onIslandF0, goUpF1ToPressure);
	}

	@Override
	public Collection<QuestStep> getSteps()
	{
		return Arrays.asList(talkToPete, goDownToSluglings, fish5Slugs, goUpFromSluglings, goUpToDropSluglings, addSluglings,
			getSluglings, pressureSluglings, goUpF1ToPressure, goUpToF2ToPressure, pressure, pullPressureLever);
	}

	public Collection<QuestStep> getDisplaySteps()
	{
		return Arrays.asList(fish5Slugs, addSluglings, pressure);
	}
}

