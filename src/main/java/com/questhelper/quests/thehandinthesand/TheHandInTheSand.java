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
package com.questhelper.quests.thehandinthesand;

import com.questhelper.ItemCollections;
import com.questhelper.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.requirements.conditional.Conditions;
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
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.QuestStep;

@QuestDescriptor(
	quest = QuestHelperQuest.THE_HAND_IN_THE_SAND
)
public class TheHandInTheSand extends BasicQuestHelper
{
	//Items Required
	ItemRequirement truthSerum, beer, redberries, whiteberries, pinkDye, roseLens, lanternLens, magicalOrb, redberryJuice, bottledWater, hand, beerHand, bertsRota, sandysRota, magicScroll, vial, vial2, sand, wizardsHead,
		beerOr2Coins, earthRunes5, coins, bucketOfSand, truthSerumHighlight, activatedOrb;

	//Items Recommended
	ItemRequirement teleportsToYanille, teleportsToBrimhaven;

	Requirement notTeleportedToSarim, inYanille, inLightSpot, receivedBottledWater, vialPlaced, madeTruthSerum;

	DetailedQuestStep talkToBert, talkToBertAboutRota, talkToBertAboutScroll, talkToBetty, talkToBettyOnceMore, talkToBettyAgain, talkToRarveAgain, talkToSandyWithPotion, giveCaptainABeer, useLensOnCounter,
		useDyeOnLanternLens, useSerumOnCoffee, searchSandysDesk, standInDoorway, ringBell, ringBellAgain, pickpocketSandy, addWhiteberries, addRedberries, activateMagicalOrb, interrogateSandy, ringBellAfterInterrogation,
		ringBellWithItems, talkToMazion, ringBellEnd;

	//Zones
	Zone yanille, lightSpot;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupItemRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToBert);
		// 1528, 1529, 1530 0->1
		steps.put(10, giveCaptainABeer);
		steps.put(20, ringBell);
		steps.put(30, talkToBertAboutRota);
		steps.put(40, searchSandysDesk);

		ConditionalStep goGetScroll = new ConditionalStep(this, pickpocketSandy);
		goGetScroll.addStep(sand, talkToBertAboutScroll);
		steps.put(50, goGetScroll);
		steps.put(60, ringBellAgain);

		ConditionalStep goToBetty = new ConditionalStep(this, talkToBetty);
		goToBetty.addStep(madeTruthSerum, talkToBettyOnceMore);
		goToBetty.addStep(new Conditions(roseLens, vialPlaced, inLightSpot), useLensOnCounter);
		goToBetty.addStep(new Conditions(roseLens, vialPlaced), standInDoorway);
		goToBetty.addStep(roseLens, talkToBettyAgain);
		goToBetty.addStep(pinkDye, useDyeOnLanternLens);
		goToBetty.addStep(redberryJuice, addWhiteberries);
		goToBetty.addStep(receivedBottledWater, addRedberries);
		goToBetty.addStep(new Conditions(inYanille, notTeleportedToSarim), talkToRarveAgain);

		steps.put(70, goToBetty);
		steps.put(80, talkToSandyWithPotion);
		steps.put(90, useSerumOnCoffee);
		steps.put(100, activateMagicalOrb);
		steps.put(110, interrogateSandy);
		steps.put(120, ringBellAfterInterrogation);
		steps.put(130, ringBellWithItems);
		steps.put(140, talkToMazion);
		steps.put(150, ringBellEnd);

		return steps;
	}

	public void setupItemRequirements()
	{
		beer = new ItemRequirement("Beer", ItemID.BEER);
		bottledWater = new ItemRequirement("Bottled water", ItemID.BOTTLED_WATER);
		bottledWater.setTooltip("You can get another from Betty");
		bottledWater.setHighlightInInventory(true);

		redberries = new ItemRequirement("Redberries", ItemID.REDBERRIES);
		redberries.setHighlightInInventory(true);
		whiteberries = new ItemRequirement("White berries", ItemID.WHITE_BERRIES);
		whiteberries.setHighlightInInventory(true);
		redberryJuice = new ItemRequirement("Red dye", ItemID.REDBERRY_JUICE);
		redberryJuice.setHighlightInInventory(true);

		pinkDye = new ItemRequirement("Pink dye", ItemID.PINK_DYE);
		pinkDye.setHighlightInInventory(true);
		truthSerum = new ItemRequirement("Truth serum", ItemID.TRUTH_SERUM);
		truthSerum.setTooltip("You can get another from Betty in Port Sarim");

		truthSerumHighlight = new ItemRequirement("Truth serum", ItemID.TRUTH_SERUM);
		truthSerumHighlight.setTooltip("You can get another from Betty in Port Sarim");
		truthSerumHighlight.setHighlightInInventory(true);

		lanternLens = new ItemRequirement("Lantern lens", ItemID.LANTERN_LENS);
		lanternLens.setHighlightInInventory(true);
		roseLens = new ItemRequirement("Rose-tinted lens", ItemID.ROSE_TINTED_LENS);
		roseLens.setHighlightInInventory(true);
		hand = new ItemRequirement("Sandy hand", ItemID.SANDY_HAND);
		hand.setTooltip("You can get another from Bert");
		beerHand = new ItemRequirement("Beer soaked hand", ItemID.BEER_SOAKED_HAND);
		beerHand.setTooltip("You can get another from Bert");
		bertsRota = new ItemRequirement("Bert's rota", ItemID.BERTS_ROTA);
		bertsRota.setTooltip("You can get another from Bert");
		sandysRota = new ItemRequirement("Sandy's rota", ItemID.SANDYS_ROTA);
		sandysRota.setTooltip("You can get another by searching Sandy's desk");

		magicalOrb = new ItemRequirement("Magical orb", ItemID.MAGICAL_ORB);
		magicalOrb.setTooltip("You can get another from Zavistic Rarve in Yanille");
		magicalOrb.setHighlightInInventory(true);

		activatedOrb = new ItemRequirement("Magical orb", ItemID.MAGICAL_ORB_A);
		activatedOrb.setTooltip("You can get another from Zavistic Rarve in Yanille");

		vial = new ItemRequirement("Vial", ItemID.VIAL);
		vial2 = new ItemRequirement("Vial", ItemID.VIAL, 2);

		magicScroll = new ItemRequirement("A magic scroll", ItemID.A_MAGIC_SCROLL);
		magicScroll.setTooltip("You can get another from Bert");

		sand = new ItemRequirement("Sand", ItemID.SAND);
		sand.setTooltip("You can get more by pickpocketing Sandy in Brimhaven");

		beerOr2Coins = new ItemRequirement("Beer or 2 gp", ItemID.BEER);
		earthRunes5 = new ItemRequirement("Earth runes", ItemID.EARTH_RUNE, 5);
		coins = new ItemRequirement("Coins or more for boat travel", ItemCollections.getCoins(), 150);

		bucketOfSand = new ItemRequirement("Bucket of sand", ItemID.BUCKET_OF_SAND);

		wizardsHead = new ItemRequirement("Wizard's head", ItemID.WIZARDS_HEAD);
		wizardsHead.setTooltip("You can get another from Mazion on Entrana");

		teleportsToBrimhaven = new ItemRequirement("Teleports to Brimhaven, or to near a boat to Brimhaven",
			ItemID.BRIMHAVEN_TELEPORT, 2);
		teleportsToYanille = new ItemRequirement("Teleports to Yanille, such as dueling ring or minigame teleport",
			ItemID.YANILLE_TELEPORT, 3);
	}

	public void loadZones()
	{
		yanille = new Zone(new WorldPoint(2528, 3063, 0), new WorldPoint(2628, 3122, 0));
		lightSpot = new Zone(new WorldPoint(3016, 3259, 0), new WorldPoint(3016, 3259, 0));
	}

	public void setupConditions()
	{
		notTeleportedToSarim = new VarbitRequirement(1531, 0);
		inYanille = new ZoneRequirement(yanille);
		inLightSpot = new ZoneRequirement(lightSpot);
		receivedBottledWater = new VarbitRequirement(1532, 1);
		vialPlaced = new VarbitRequirement(1537, 1);
		madeTruthSerum = new VarbitRequirement(1532, 5);
	}

	public void setupSteps()
	{
		talkToBert = new NpcStep(this, NpcID.BERT, new WorldPoint(2551, 3099, 0), "Talk to Bert in west Yanille.");
		talkToBert.addDialogStep("Eww a hand, in the sand! Why haven't you told the authorities?");
		talkToBert.addDialogStep("Sure, I'll give you a hand.");
		giveCaptainABeer = new NpcStep(this, NpcID.GUARD_CAPTAIN, new WorldPoint(2552, 3080, 0), "Give the Guard Captain in the pub south of Bert a beer. You can buy one for 2gp from the pub.", beer);
		ringBell = new ObjectStep(this, ObjectID.BELL_6847, new WorldPoint(2598, 3085, 0), "Ring the bell outside the Wizards' Guild in Yanille. Talk to Zavistic Rarve when he appears.", beerHand);
		ringBell.addDialogStep("I have a rather sandy problem that I'd like to palm off on you.");
		talkToBertAboutRota = new NpcStep(this, NpcID.BERT, new WorldPoint(2551, 3099, 0), "Return to Bert in west Yanille.");

		searchSandysDesk = new ObjectStep(this, ObjectID.SANDYS_DESK, new WorldPoint(2789, 3174, 0), "Travel to Brimhaven, then enter Sandy's building south of the restaurant. Search Sandy's desk for Sandy's rota.");

		pickpocketSandy = new NpcStep(this, NpcID.SANDY, new WorldPoint(2790, 3175, 0), "Pickpocket Sandy for some sand.");
		talkToBertAboutScroll = new NpcStep(this, NpcID.BERT, new WorldPoint(2551, 3099, 0), "Return to Bert in west Yanille with the rota and sand.", bertsRota, sandysRota);

		ringBellAgain = new ObjectStep(this, ObjectID.BELL_6847, new WorldPoint(2598, 3085, 0), "Ring the bell outside the Wizards' Guild in Yanille. Talk to Zavistic Rarve when he appears.", magicScroll);
		talkToRarveAgain = new ObjectStep(this, ObjectID.BELL_6847, new WorldPoint(2598, 3085, 0), "Talk to Zavistic Rarve again to get teleported to Port Sarim.", vial);
		talkToRarveAgain.addDialogStep("Can you help me more?");
		talkToRarveAgain.addDialogStep("Yes, that would be great!");

		talkToBetty = new NpcStep(this, NpcID.BETTY_5905, new WorldPoint(3014, 3258, 0), "Travel to Port Sarim, and talk to Betty in the magic shop.", vial);
		talkToBetty.addDialogStep("Talk to Betty about the Hand in the Sand.");
		addRedberries = new DetailedQuestStep(this, "Use redberries on the bottled water.", bottledWater, redberries);
		addWhiteberries = new DetailedQuestStep(this, "Use whiteberries on the red bottled water", redberryJuice, whiteberries);
		useDyeOnLanternLens = new DetailedQuestStep(this, "Use the pink dye on a lantern lens.", pinkDye, lanternLens);
		talkToBettyAgain = new NpcStep(this, NpcID.BETTY_5905, new WorldPoint(3014, 3258, 0), "Talk to Betty with the pink lens.");
		talkToBettyAgain.addDialogStep("Talk to Betty about the Hand in the Sand.");

		standInDoorway = new DetailedQuestStep(this, new WorldPoint(3016, 3259, 0), "Stand in the Betty's doorway and use the rose-tinted lens on the counter.");
		useLensOnCounter = new ObjectStep(this, ObjectID.COUNTER, new WorldPoint(3013, 3259, 0), "\"Stand in the Betty's doorway and use the rose-tinted lens on the counter.", roseLens);
		useLensOnCounter.addIcon(ItemID.ROSE_TINTED_LENS);
		useLensOnCounter.addSubSteps(standInDoorway);
		talkToBettyOnceMore =  new NpcStep(this, NpcID.BETTY_5905, new WorldPoint(3014, 3258, 0), "Talk to Betty again.", truthSerum, sand);
		talkToBettyOnceMore.addDialogStep("Talk to Betty about the Hand in the Sand.");
		talkToSandyWithPotion =  new NpcStep(this, NpcID.SANDY, new WorldPoint(2790, 3175, 0), "Talk to Sandy in Brimhaven again with the truth serum. Select distractions until one works.", truthSerum);
		useSerumOnCoffee = new ObjectStep(this, NullObjectID.NULL_10806, new WorldPoint(2789, 3176, 0), "Use the truth serum on Sandy's coffee mug.", truthSerum);
		useSerumOnCoffee.addIcon(ItemID.TRUTH_SERUM);
		activateMagicalOrb = new DetailedQuestStep(this, "Activate the magical orb next to Sandy.", magicalOrb);

		interrogateSandy = new NpcStep(this, NpcID.SANDY, new WorldPoint(2790, 3175, 0), "Ask Sandy all questions available with the Magical orb (a) in your inventory.", activatedOrb);
		interrogateSandy.addDialogSteps("Why is Bert's rota different from the original?", "Why doesn't Bert remember the change in his hours?", "What happened to the wizard?");

		ringBellAfterInterrogation = new ObjectStep(this, ObjectID.BELL_6847, new WorldPoint(2598, 3085, 0), "Return to the Wizards' Guild in Yanille and ring the bell outside. Talk to Zavistic Rarve when he appears.", activatedOrb, earthRunes5, bucketOfSand);
		ringBellWithItems = new ObjectStep(this, ObjectID.BELL_6847, new WorldPoint(2598, 3085, 0), "Give  Zavistic Rarve 5 earth runes and a bucket of sand.", earthRunes5, bucketOfSand);
		talkToMazion = new NpcStep(this, NpcID.MAZION, new WorldPoint(2815, 3340, 0), "Travel to Entrana (bank all combat gear), and talk to Mazion at the sand pit.");
		ringBellEnd = new ObjectStep(this, ObjectID.BELL_6847, new WorldPoint(2598, 3085, 0), "Return to the Wizards' Guild in Yanille and ring the bell outside. Talk to Zavistic Rarve when he appears to finish the quest.", wizardsHead);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(beerOr2Coins);
		reqs.add(coins);
		reqs.add(vial2);
		reqs.add(redberries);
		reqs.add(whiteberries);
		reqs.add(lanternLens);
		reqs.add(earthRunes5);
		reqs.add(bucketOfSand);
		return reqs;
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(teleportsToYanille);
		reqs.add(teleportsToBrimhaven);

		return reqs;
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return Arrays.asList(new SkillRequirement(Skill.THIEVING, 17), new SkillRequirement(Skill.CRAFTING, 49));
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
				new ExperienceReward(Skill.THIEVING, 1000),
				new ExperienceReward(Skill.CRAFTING, 9000));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
				new UnlockReward("Daily sand from Bert in Yanille."),
				new UnlockReward("Access to the Wizards Guild rune store."));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off", Collections.singletonList(talkToBert)));
		allSteps.add(new PanelDetails("Investigating", Arrays.asList(giveCaptainABeer, ringBell, talkToBertAboutRota, searchSandysDesk, pickpocketSandy), beerOr2Coins));
		allSteps.add(new PanelDetails("Making a truth serum", Arrays.asList(talkToBertAboutScroll, ringBellAgain, talkToRarveAgain, talkToBetty, addRedberries, addWhiteberries, useDyeOnLanternLens,
			talkToBettyAgain, useLensOnCounter, talkToBettyOnceMore), vial2, lanternLens, redberries, whiteberries));
		allSteps.add(new PanelDetails("Uncover the truth", Arrays.asList(talkToSandyWithPotion, useSerumOnCoffee, activateMagicalOrb, interrogateSandy), truthSerum, magicalOrb));
		allSteps.add(new PanelDetails("Finishing off", Arrays.asList(ringBellAfterInterrogation, talkToMazion, ringBellEnd), earthRunes5, bucketOfSand));
		return allSteps;
	}
}
