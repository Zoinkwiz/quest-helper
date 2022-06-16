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

package com.questhelper.quests.gettingahead;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
        quest = QuestHelperQuest.GETTING_AHEAD
)
public class GettingAhead extends BasicQuestHelper
{
	//Items Required
	ItemRequirement bearFur, softClay, hammer, saw, planks, nails, knife, redDye, potOfFlour, needle, thread;

	//Items Recommended
	ItemRequirement food, staminaPotions, combatGear, skillsNeck, clayHead, clayHeadHighlighted, furHead, furHeadHighlighted, bloodyHead;

	//Other items used
	ItemRequirement clay, pickaxe, bucket, bucketOfWater, itemsTip;

	//NPC Steps
	QuestStep talkToGordon1, talkToMary, talkToGordon2, talkToMary2, talkToGordonGen, talkToGordonGen2, talkToGordonGen3, talkToGordonFinal;

	//Object/items Steps
	QuestStep usePotOfFlour, goToMine, leaveCave, killBeast, useKnifeOnClay, useFurOnHead, useDyeOnHead, buildBearHead;

	//Getting items steps
	QuestStep goUpstairsHouse, takePot, goDownstairsHouse, getNeedle, getThread, takePickaxe, mineClay, takeKnife, fillBucket, wetClay, takeBucket, takeDye,
		takeNails, takeSaw, takeHammer, takePlanks;

	ConditionalStep goUseFlourOnGate, returnToGordon, makeClayHead, addFurToHead, dyeHead, putUpHead;

	//Conditions
	Requirement inMine, inUpstairsHouse;

	//Zones
	Zone kebosMine, upstairsHouse;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToGordon1);
		steps.put(2, talkToMary);

		goUseFlourOnGate = new ConditionalStep(this, goUpstairsHouse, "Use the pot of flour on the cow pen gate.");
		goUseFlourOnGate.addStep(new Conditions(inUpstairsHouse, potOfFlour), goDownstairsHouse);
		goUseFlourOnGate.addStep(new Conditions(potOfFlour), usePotOfFlour);
		goUseFlourOnGate.addStep(inUpstairsHouse, takePot);
		steps.put(4, goUseFlourOnGate);

		ConditionalStep killBeastStep = new ConditionalStep(this, goToMine);
		killBeastStep.addStep(inMine, killBeast);
		steps.put(6, killBeastStep);
		steps.put(8, killBeastStep);
		steps.put(10, killBeastStep);
		steps.put(12, killBeastStep);

		returnToGordon = new ConditionalStep(this, takePickaxe, "Return to Gordon and talk to him.");
		returnToGordon.addStep(inMine, leaveCave);
		returnToGordon.addStep(new Conditions(new Conditions(LogicType.OR, clay, softClay), planks),
			talkToGordon2);
		returnToGordon.addStep(new Conditions(LogicType.OR, clay, softClay), takePlanks);
		returnToGordon.addStep(pickaxe, mineClay);

		steps.put(14, returnToGordon);
		steps.put(16, talkToMary2);

		makeClayHead = new ConditionalStep(this, takePickaxe, "Use the knife on the soft clay then talk to Gordon.");
		makeClayHead.addStep(new Conditions(softClay, knife), useKnifeOnClay);
		makeClayHead.addStep(softClay, takeKnife);
		makeClayHead.addStep(new Conditions(bucketOfWater, clay), wetClay);
		makeClayHead.addStep(new Conditions(bucket, clay), fillBucket);
		makeClayHead.addStep(clay, takeBucket);
		makeClayHead.addStep(pickaxe, mineClay);
		steps.put(18, makeClayHead);

		steps.put(20, talkToGordonGen);
		addFurToHead = new ConditionalStep(this, goUpstairsHouse, "Use the bear fur on the clay head then talk to Gordon.");
		addFurToHead.addStep(new Conditions(thread, needle), useFurOnHead);
		addFurToHead.addStep(new Conditions(inUpstairsHouse, needle), getThread);
		addFurToHead.addStep(inUpstairsHouse, getNeedle);
		steps.put(22, addFurToHead);
		steps.put(24, talkToGordonGen2);

		dyeHead = new ConditionalStep(this, takeDye, "Use the red dye on the fur head then talk to Gordon.");
		dyeHead.addStep(redDye, useDyeOnHead);
		steps.put(26, dyeHead);
		steps.put(28, talkToGordonGen3);

		putUpHead = new ConditionalStep(this, takePlanks, "Build the Mounted Head Space inside the farmhouse.");
		putUpHead.addStep(new Conditions(planks, hammer, saw, nails), buildBearHead);
		putUpHead.addStep(new Conditions(planks, hammer, saw), takeNails);
		putUpHead.addStep(new Conditions(planks, hammer), takeSaw);
		putUpHead.addStep(planks, takeHammer);
		steps.put(30, putUpHead);
		steps.put(32, talkToGordonFinal);

		return steps;
	}

	@Override
	public void setupRequirements()
	{
		itemsTip = new ItemRequirement("You can get all the required items during the quest.", -1, -1);

		//Recommended
		combatGear = new ItemRequirement("Combat gear", -1, -1);
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, -1);
		staminaPotions = new ItemRequirement("Stamina Potion", ItemCollections.STAMINA_POTIONS);
		skillsNeck = new ItemRequirement("Skills Necklace", ItemCollections.SKILLS_NECKLACES);

		//Required
		bearFur = new ItemRequirement("Bear Fur", ItemID.BEAR_FUR);
		bearFur.canBeObtainedDuringQuest();
		bearFur.setTooltip("You can kill a bear west of the farm for some fur");
		bearFur.setHighlightInInventory(true);
		softClay = new ItemRequirement("Soft Clay", ItemID.SOFT_CLAY, 1);
		softClay.canBeObtainedDuringQuest();
		softClay.setHighlightInInventory(true);
		hammer = new ItemRequirement("Hammer", ItemCollections.HAMMER, 1);
		hammer.canBeObtainedDuringQuest();
		saw = new ItemRequirement("Any saw", ItemID.SAW, 1);
		saw.canBeObtainedDuringQuest();
		saw.addAlternates(ItemID.CRYSTAL_SAW, ItemID.AMYS_SAW);
		planks = new ItemRequirement("Planks", ItemID.PLANK, 2);
		planks.canBeObtainedDuringQuest();
		nails = new ItemRequirement("Nails", ItemCollections.NAILS, 6);
		nails.canBeObtainedDuringQuest();
		knife = new ItemRequirement("Knife", ItemID.KNIFE, 1);
		knife.canBeObtainedDuringQuest();
		knife.setHighlightInInventory(true);
		redDye = new ItemRequirement("Red Dye", ItemID.RED_DYE, 1);
		redDye.canBeObtainedDuringQuest();
		redDye.setHighlightInInventory(true);
		potOfFlour = new ItemRequirement("Pot of Flour", ItemID.POT_OF_FLOUR, 1);
		potOfFlour.canBeObtainedDuringQuest();
		potOfFlour.setHighlightInInventory(true);
		needle = new ItemRequirement("Needle", ItemID.NEEDLE, 1);
		needle.canBeObtainedDuringQuest();
		thread = new ItemRequirement("Thread", ItemID.THREAD, 1);
		thread.canBeObtainedDuringQuest();
		pickaxe = new ItemRequirement("Any pickaxe", ItemCollections.PICKAXES);
		pickaxe.canBeObtainedDuringQuest();
		clay = new ItemRequirement("Clay", ItemID.CLAY);
		clay.canBeObtainedDuringQuest();
		clay.setHighlightInInventory(true);
		bucket = new ItemRequirement("Bucket", ItemID.BUCKET);
		bucket.canBeObtainedDuringQuest();
		bucket.setHighlightInInventory(true);
		bucketOfWater = new ItemRequirement("Bucket of water", ItemID.BUCKET_OF_WATER);
		bucketOfWater.canBeObtainedDuringQuest();
		bucketOfWater.setHighlightInInventory(true);

		//Making the fake head
		clayHead = new ItemRequirement("Clay Head", ItemID.CLAY_HEAD);
		clayHead.setTooltip("You can make another by using a knife on some soft clay");

		clayHeadHighlighted = new ItemRequirement("Clay Head", ItemID.CLAY_HEAD);
		clayHeadHighlighted.setTooltip("You can make another by using a knife on some soft clay");
		clayHeadHighlighted.setHighlightInInventory(true);
		furHead = new ItemRequirement("Fur Head", ItemID.FUR_HEAD);
		furHead.setTooltip("You can make another by using a knife on soft clay, then adding bear fur");
		furHeadHighlighted = new ItemRequirement("Fur Head", ItemID.FUR_HEAD);
		furHeadHighlighted.setTooltip("You can make another by using a knife on soft clay, then adding bear fur");
		furHeadHighlighted.setHighlightInInventory(true);
		bloodyHead = new ItemRequirement("Bloody Head", ItemID.BLOODY_HEAD);
	}

	public void setupConditions()
	{
		inMine = new ZoneRequirement(kebosMine);
		inUpstairsHouse = new ZoneRequirement(upstairsHouse);
	}

	public void loadZones()
	{
		kebosMine = new Zone(new WorldPoint(1174, 10000, 0), new WorldPoint(1215, 10035, 0));
		upstairsHouse = new Zone(new WorldPoint(1238, 3677, 1), new WorldPoint(1244, 3687, 1));
	}

	public void setupSteps()
	{
		//Starting Off
		talkToGordon1 = new NpcStep(this, NpcID.GORDON, new WorldPoint(1248, 3686, 0), "Talk to Gordon south of the Farming Guild.");
		talkToGordon1.addDialogStep("Need a hand with anything?");
		talkToGordon1.addDialogStep("Yes.");

		talkToMary = new NpcStep(this, NpcID.MARY_10502, new WorldPoint(1237, 3678, 0), "Talk to Mary inside the main building, west of Gordon.");

		//Killing the beast
		goUpstairsHouse = new ObjectStep(this, ObjectID.STAIRCASE_34502, new WorldPoint(1240, 3686, 0), "Climb up the nearby house's staircase.");
		takePot = new ItemStep(this, "Take the nearby pot of flour, or fill a pot with the barrel of flour in the south west room.", potOfFlour);
		goDownstairsHouse = new ObjectStep(this, ObjectID.STAIRCASE_34503, new WorldPoint(1240, 3686, 1), "Go back downstairs.");

		usePotOfFlour = new ObjectStep(this, NullObjectID.NULL_40427, new WorldPoint(1257, 3686, 0), "", potOfFlour);
		usePotOfFlour.addIcon(ItemID.POT_OF_FLOUR);

		goToMine = new ObjectStep(this, ObjectID.CAVE_20852, new WorldPoint(1212, 3647, 0), "Enter the Kebos Lowlands mine just west of the bridge and kill the Headless Beast (level 82).");
		goToMine.addDialogStep("Yes.");
		killBeast = new NpcStep(this, NpcID.HEADLESS_BEAST_10506, new WorldPoint(1191, 10021, 0), "Kill the headless " +
			"beast. You can safespot it from the south west corner of the pond in the cave.");
		((NpcStep) killBeast).addSafeSpots(new WorldPoint(1190, 10017, 0));
		goToMine.addSubSteps(killBeast);

		talkToGordon2 = new NpcStep(this, NpcID.GORDON, new WorldPoint(1248, 3686, 0), "");
		leaveCave = new ObjectStep(this, ObjectID.CAVE_20853, new WorldPoint(1190, 10029, 0), "Leave the cave.");
		talkToGordon2.addSubSteps(leaveCave);

		//Making the fake head
		talkToGordonGen = new NpcStep(this, NpcID.GORDON, new WorldPoint(1248, 3686, 0), "Talk with Gordon.", clayHead);
		talkToGordonGen2 = new NpcStep(this, NpcID.GORDON, new WorldPoint(1248, 3686, 0), "Talk with Gordon.", furHead);
		talkToGordonGen3 = new NpcStep(this, NpcID.GORDON, new WorldPoint(1248, 3686, 0), "Talk with Gordon.", bloodyHead);

		talkToMary2 = new NpcStep(this, NpcID.MARY_10502, new WorldPoint(1237, 3678, 0), "Talk to Mary inside the house.");
		takePickaxe = new ObjectStep(this, ObjectID.ROCKS_40365, new WorldPoint(1222, 3653, 0), "Take the pickaxe in the mine.");
		mineClay = new ObjectStep(this, ObjectID.ROCKS_11362, new WorldPoint(1217, 3657, 0), "Mine some clay.", pickaxe);
		takeKnife = new DetailedQuestStep(this, new WorldPoint(1241, 3679, 0), "Take the knife near Mary.", knife);
		takeBucket = new DetailedQuestStep(this, new WorldPoint(1244, 3682, 0), "Take the bucket in the house.", bucket);
		fillBucket = new ObjectStep(this, ObjectID.SINK_1763, new WorldPoint(1240, 3677, 0), "Fill the bucket on the sink.", bucket);
		fillBucket.addIcon(ItemID.BUCKET);
		wetClay = new DetailedQuestStep(this, "Use the bucket of water on the clay.", bucketOfWater, clay);
		useKnifeOnClay = new DetailedQuestStep(this, "", knife, softClay);
		useKnifeOnClay.addDialogStep("Yes.");

		getNeedle = new DetailedQuestStep(this, new WorldPoint(1244, 3685, 1), "Take the needle.", needle);
		getThread = new DetailedQuestStep(this, new WorldPoint(1244, 3684, 1), "Take the thread.", thread);
		useFurOnHead = new DetailedQuestStep(this, "Use the bear fur on the clay head then talk to Gordon.", bearFur, clayHeadHighlighted, thread, needle);
		useFurOnHead.addDialogStep("Yes.");

		takeDye = new ObjectStep(this, ObjectID.SHELVES_40362, new WorldPoint(1240, 3688, 0), "Get some red dye from the shelves in the farm house.");
		takeDye.addDialogStep("Take some red dye.");
		useDyeOnHead = new DetailedQuestStep(this, "", redDye, furHeadHighlighted);
		useDyeOnHead.addDialogStep("Yes.");
		useDyeOnHead.addSubSteps(talkToGordonGen3);

		takePlanks = new ItemStep(this, new WorldPoint(1202, 3649, 0), "Take the planks outside the monster's cave.", planks);
		takeSaw = new DetailedQuestStep(this, new WorldPoint(1239, 3696, 0), "Take the saw in the building north of the farmhouse.", saw);
		takeHammer = new DetailedQuestStep(this, new WorldPoint(1259, 3686, 0), "Take the hammer east of the farmhouse.", hammer);
		takeNails = new ObjectStep(this, ObjectID.WORKBENCH_40364, new WorldPoint(1239, 3699, 0), "Take some nails from the farm's workbench.");
		takeNails.addDialogStep("Take the nails.");
		buildBearHead = new ObjectStep(this, NullObjectID.NULL_20858, new WorldPoint(1240, 3683, 0), "", bloodyHead, nails, planks, saw, hammer);
		buildBearHead.addDialogStep("Yes.");
		talkToGordonFinal = new NpcStep(this, NpcID.GORDON, new WorldPoint(1248, 3686, 0), "Return to Gordon to finish the quest.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(itemsTip, bearFur, softClay, hammer, saw, planks, nails, knife, redDye, potOfFlour, needle, thread);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(food, staminaPotions, combatGear, skillsNeck);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Collections.singletonList("Headless Beast (level 82, safespottable)");
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Arrays.asList(
				new ExperienceReward(Skill.CRAFTING, 4000),
				new ExperienceReward(Skill.CONSTRUCTION, 3200));
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Collections.singletonList(new ItemReward("3,000 Coins", ItemID.COINS_995, 3000));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Collections.singletonList(new UnlockReward("Access to the tannery on Kebos Lowlands"));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off", Arrays.asList(talkToGordon1, talkToMary)));
		allSteps.add(new PanelDetails("Killing the Beast", Arrays.asList(goUseFlourOnGate, goToMine, returnToGordon),
			potOfFlour));
		allSteps.add(new PanelDetails("Making the fake head", Arrays.asList(talkToMary2, makeClayHead, addFurToHead,
			useDyeOnHead, putUpHead, talkToGordonFinal), bearFur, softClay, hammer, saw, planks, nails, knife, redDye, needle, thread));
		return allSteps;
	}
	
	@Override
	public List<Requirement> getGeneralRequirements()
	{
		List<Requirement> requirements = new ArrayList<>();
		requirements.add(new SkillRequirement(Skill.CRAFTING, 30));
		requirements.add(new SkillRequirement(Skill.CONSTRUCTION, 26));
		return requirements;
	}
}
