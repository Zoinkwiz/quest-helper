package com.questhelper.quests.gertrudescat;

import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.conditional.ConditionForStep;
import com.questhelper.steps.conditional.Conditions;
import com.questhelper.steps.conditional.ItemCondition;
import com.questhelper.steps.conditional.ItemRequirementCondition;
import com.questhelper.steps.conditional.ZoneCondition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import net.runelite.api.IconID;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.GERTRUDES_CAT
)
public class GertrudesCat extends BasicQuestHelper
{
	private ItemRequirement bucketOfMilk, coins, seasonedSardine;

	private QuestStep talkToGertrude, talkToChildren,
		gertrudesCat, gertrudesCat2, searchNearbyCrates, giveKittenToFluffy,
		finishQuest;

	private ConditionForStep isUpstairsLumberyard, hasFluffsKitten;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupItemRequirements();
		setupZones();
		setupConditions();

		return getSteps();
	}

	private Map<Integer, QuestStep> getSteps()
	{
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToGertrude = getTalkToGertrude());
		steps.put(1, talkToChildren = getTalkToChildren());
		steps.put(2, getLumberyard());
		steps.put(3, getFeedCat());
		steps.put(4, findFluffsKitten());
		steps.put(5, finishQuest = returnToGertrude());
		return steps;
	}

	private NpcStep returnToGertrude()
	{
		return new NpcStep(this, NpcID.GERTRUDE,
			new WorldPoint(3148, 3413, 0), "Talk to Gertrude.");
	}

	private QuestStep findFluffsKitten()
	{

		//Need to find to ways to hide arrow
		searchNearbyCrates = new NpcStep(this, NpcID.CRATE,
			"Search for a kitten.", true);
		ObjectStep climbDownLadderStep = goDownLadderStep();
		ObjectStep climbUpLadderStep = getClimbLadder();
		Conditions hasFluffsKittenUpstairs = new Conditions(hasFluffsKitten, isUpstairsLumberyard);

		giveKittenToFluffy = getGertrudesCat("Return the kitten to Gertrude's Cat");
		giveKittenToFluffy.addIcon(ItemID.FLUFFS_KITTEN);

		ConditionalStep conditionalKitten = new ConditionalStep(this, searchNearbyCrates);
		conditionalKitten.addStep(hasFluffsKittenUpstairs, giveKittenToFluffy);
		conditionalKitten.addStep(hasFluffsKitten, climbUpLadderStep);
		conditionalKitten.addStep(isUpstairsLumberyard, climbDownLadderStep);

		searchNearbyCrates.addSubSteps(climbDownLadderStep);
		giveKittenToFluffy.addSubSteps(climbUpLadderStep);

		return conditionalKitten;
	}

	private ObjectStep goDownLadderStep()
	{
		return new ObjectStep(this, ObjectID.LADDER_11795, new WorldPoint(3310, 3509, 1)
			, "Climb down ladder");
	}

	private QuestStep getFeedCat()
	{
		gertrudesCat2 = getGertrudesCat("Use seasoned Sardine on Gertrudes cat.");
		gertrudesCat2.addIcon(ItemID.SEASONED_SARDINE);

		ObjectStep climbLadder = new ObjectStep(this, ObjectID.LADDER_11794,
			new WorldPoint(3310, 3509, 0), "Climb ladder");

		ConditionalStep lumberyard = new ConditionalStep(this, climbLadder, seasonedSardine);
		lumberyard.addStep(isUpstairsLumberyard, gertrudesCat2);
		gertrudesCat2.addSubSteps(climbLadder);

		return lumberyard;
	}

	private QuestStep getLumberyard()
	{
		gertrudesCat = getGertrudesCat("Use bucket of milk on Gertrudes cat.");
		gertrudesCat.addIcon(ItemID.BUCKET_OF_MILK);

		ObjectStep climbLadder = getClimbLadder();

		ConditionalStep lumberyard = new ConditionalStep(this, climbLadder, seasonedSardine);
		lumberyard.addStep(isUpstairsLumberyard, gertrudesCat);
		gertrudesCat.addSubSteps(climbLadder);

		return lumberyard;
	}


	private NpcStep getGertrudesCat(String text)
	{
		return new NpcStep(this, NpcID.GERTRUDES_CAT_3497,
			new WorldPoint(3308, 3511, 1), text, bucketOfMilk);
	}

	private QuestStep getTalkToChildren()
	{
		NpcStep talkToChildren = new NpcStep(this, NpcID.SHILOP,
			new WorldPoint(3222, 3435, 0), "Talk to Shilop or Wilough.", true, coins);
		talkToChildren.addAlternateNpcs(NpcID.WILOUGH);
		talkToChildren.addDialogStep("What will make you tell me?");
		talkToChildren.addDialogStep("Okay then, I'll pay.");
		return talkToChildren;
	}

	private QuestStep getTalkToGertrude()
	{
		NpcStep talkToGertrude = new NpcStep(this, NpcID.GERTRUDE,
			new WorldPoint(3148, 3413, 0), "Talk to Gertrude.");
		talkToGertrude.addDialogStep("Yes.");
		return talkToGertrude;
	}

	public void setupItemRequirements()
	{
		bucketOfMilk = new ItemRequirement("Bucket of milk", ItemID.BUCKET_OF_MILK, 1);
		coins = new ItemRequirement("Coins", ItemID.COINS_995, 100);
		;
		seasonedSardine = new ItemRequirement("Seasoned Sardine", ItemID.SEASONED_SARDINE, 1);
		seasonedSardine.setTip("Can be created by using a sardine on Doogle leaves(South of Gertrudes House)");
	}

	private void setupZones()
	{
		Zone zone = new Zone(new WorldPoint(3306, 3507, 12), new WorldPoint(3312, 3513, 1));

		isUpstairsLumberyard = new ZoneCondition(zone);
	}

	private void setupConditions()
	{
		hasFluffsKitten = new ItemRequirementCondition(new ItemRequirement("Fluffs Kitten", ItemID.FLUFFS_KITTEN, 1));
	}

	private ObjectStep getClimbLadder()
	{
		return new ObjectStep(this, ObjectID.LADDER_11794,
			new WorldPoint(3310, 3509, 0), "Climb ladder");
	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		return new ArrayList<>(Arrays.asList(bucketOfMilk, coins, seasonedSardine));
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> steps = new ArrayList<>();

		PanelDetails startingPanel = new PanelDetails("Starting out",
			new ArrayList<>(Arrays.asList(talkToGertrude, talkToChildren)),
			coins);
		steps.add(startingPanel);

		PanelDetails lumberYardPanel = new PanelDetails("The secret playground (Lumber Yard)",
			new ArrayList<>(Arrays.asList(gertrudesCat, gertrudesCat2, searchNearbyCrates, giveKittenToFluffy)),
			seasonedSardine, bucketOfMilk);
		steps.add(lumberYardPanel);

		PanelDetails finishQuestPanel = new PanelDetails("Finish the quest",
			new ArrayList<>(Arrays.asList(finishQuest)));
		steps.add(finishQuestPanel);
		return steps;
	}
}
