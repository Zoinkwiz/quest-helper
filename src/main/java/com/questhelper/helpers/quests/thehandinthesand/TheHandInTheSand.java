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
package com.questhelper.helpers.quests.thehandinthesand;

import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.*;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;

import java.util.*;

public class TheHandInTheSand extends BasicQuestHelper
{
	//Items Required
	ItemRequirement truthSerum, beer, redberries, whiteberries, pinkDye, roseLens, lanternLens, magicalOrb, redberryJuice, bottledWater, hand, beerHand, bertsRota, sandysRota, magicScroll, vial, vial2, sand, wizardsHead,
		beerOr2Coins, earthRunes5, coins, bucketOfSand, truthSerumHighlight, activatedOrb;

	//Items Recommended
	ItemRequirement teleportsToYanille, teleportsToBrimhaven, teleportToPortSarim;

	Requirement notTeleportedToSarim, inYanille, inLightSpot, receivedBottledWater, vialPlaced, madeTruthSerum;

	DetailedQuestStep talkToBert, talkToBertAboutRota, talkToBertAboutScroll, talkToBetty, talkToBettyOnceMore, talkToBettyAgain, talkToRarveAgain, talkToSandyWithPotion, giveCaptainABeer, useLensOnCounter,
		useDyeOnLanternLens, useSerumOnCoffee, searchSandysDesk, standInDoorway, ringBell, ringBellAgain, pickpocketSandy, addWhiteberries, addRedberries, activateMagicalOrb, interrogateSandy, ringBellAfterInterrogation,
		ringBellWithItems, talkToMazion, ringBellEnd;

	//Zones
	Zone yanille, lightSpot;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
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

	@Override
	protected void setupRequirements()
	{
		beer = new ItemRequirement("Beer", ItemID.BEER);
		bottledWater = new ItemRequirement("Bottled water", ItemID.HANDSAND_BOTTLE_WATER);
		bottledWater.setTooltip("You can get another from Betty");
		bottledWater.setHighlightInInventory(true);

		redberries = new ItemRequirement("Redberries", ItemID.REDBERRIES);
		redberries.setHighlightInInventory(true);
		whiteberries = new ItemRequirement("White berries", ItemID.WHITE_BERRIES);
		whiteberries.setHighlightInInventory(true);
		redberryJuice = new ItemRequirement("Red dye", ItemID.HANDSAND_REDBERRY_JUICE);
		redberryJuice.setHighlightInInventory(true);

		pinkDye = new ItemRequirement("Pink dye", ItemID.HANDSAND_PINK_DYE);
		pinkDye.setHighlightInInventory(true);
		truthSerum = new ItemRequirement("Truth serum", ItemID.HANDSAND_TRUTHSERUM);
		truthSerum.setTooltip("You can get another from Betty in Port Sarim");

		truthSerumHighlight = new ItemRequirement("Truth serum", ItemID.HANDSAND_TRUTHSERUM);
		truthSerumHighlight.setTooltip("You can get another from Betty in Port Sarim");
		truthSerumHighlight.setHighlightInInventory(true);

		lanternLens = new ItemRequirement("Lantern lens", ItemID.BULLSEYE_LANTERN_LENS);
		lanternLens.setHighlightInInventory(true);
		roseLens = new ItemRequirement("Rose-tinted lens", ItemID.HANDSAND_ROSE_LENS);
		roseLens.setHighlightInInventory(true);
		hand = new ItemRequirement("Sandy hand", ItemID.HANDSAND_SANDYHAND);
		hand.setTooltip("You can get another from Bert");
		beerHand = new ItemRequirement("Beer soaked hand", ItemID.HANDSAND_BEERHAND);
		beerHand.setTooltip("You can get another from Bert");
		bertsRota = new ItemRequirement("Bert's rota", ItemID.HANDSAND_ROTA_BERT);
		bertsRota.setTooltip("You can get another from Bert");
		sandysRota = new ItemRequirement("Sandy's rota", ItemID.HANDSAND_ROTA_SANDY);
		sandysRota.setTooltip("You can get another by searching Sandy's desk");

		magicalOrb = new ItemRequirement("Magical orb", ItemID.HANDSAND_ORB_STORAGE);
		magicalOrb.setTooltip("You can get another from Zavistic Rarve in Yanille");
		magicalOrb.setHighlightInInventory(true);

		activatedOrb = new ItemRequirement("Magical orb", ItemID.HANDSAND_ORB_RECORDING);
		activatedOrb.setTooltip("You can get another from Zavistic Rarve in Yanille");

		vial = new ItemRequirement("Vial", ItemID.VIAL_EMPTY);
		vial2 = new ItemRequirement("Vial", ItemID.VIAL_EMPTY, 2);

		magicScroll = new ItemRequirement("A magic scroll", ItemID.HANDSAND_SCROLL_MAGIC);
		magicScroll.setTooltip("You can get another from Bert");

		sand = new ItemRequirement("Sand", ItemID.HANDSAND_SAND);
		sand.setTooltip("You can get more by pickpocketing Sandy in Brimhaven");

		beerOr2Coins = new ItemRequirement("Beer or 2 gp", ItemID.BEER);
		earthRunes5 = new ItemRequirement("Earth runes", ItemID.EARTHRUNE, 5);
		coins = new ItemRequirement("Coins or more for boat travel", ItemCollections.COINS, 150);

		bucketOfSand = new ItemRequirement("Bucket of sand", ItemID.BUCKET_SAND);

		wizardsHead = new ItemRequirement("Wizard's head", ItemID.HANDSAND_WIZHEAD);
		wizardsHead.setTooltip("You can get another from Mazion on Entrana");

		teleportsToBrimhaven = new ItemRequirement("Teleports to Brimhaven, or to near a boat to Brimhaven",
			ItemID.NZONE_TELETAB_BRIMHAVEN, 2);
		teleportsToYanille = new ItemRequirement("Teleports to Yanille, such as dueling ring or minigame teleport",
			ItemID.NZONE_TELETAB_YANILLE, 3);
		teleportToPortSarim = new ItemRequirement("Teleport to Port Sarim", ItemCollections.EXPLORERS_RINGS);
		teleportToPortSarim.addAlternates(ItemCollections.AMULET_OF_GLORIES);
	}

	@Override
	protected void setupZones()
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
		talkToBert = new NpcStep(this, NpcID.HANDSAND_BERT_1OP, new WorldPoint(2551, 3099, 0), "Talk to Bert in west Yanille.");
		talkToBert.addDialogStep("Eww a hand, in the sand! Why haven't you told the authorities?");
		talkToBert.addDialogStep("Sure, I'll give you a hand.");
		talkToBert.addDialogStep("Yes.");
		giveCaptainABeer = new NpcStep(this, NpcID.HANDSAND_GUARD_CAPTAIN, new WorldPoint(2552, 3080, 0), "Give the Guard Captain in the pub south of Bert a beer. You can buy one for 2gp from the pub.", beer);
		ringBell = new ObjectStep(this, ObjectID.ZOGRE_OUTDOOR_BELL, new WorldPoint(2598, 3085, 0), "Ring the bell outside the Wizards' Guild in Yanille. Talk to Zavistic Rarve when he appears.", beerHand);
		ringBell.addDialogStep("I have a rather sandy problem that I'd like to palm off on you.");
		talkToBertAboutRota = new NpcStep(this, NpcID.HANDSAND_BERT_1OP, new WorldPoint(2551, 3099, 0), "Return to Bert in west Yanille.");

		searchSandysDesk = new ObjectStep(this, ObjectID.HANDSAND_DESK, new WorldPoint(2789, 3174, 0), "Travel to Brimhaven, then enter Sandy's building south of the restaurant. Search Sandy's desk for Sandy's rota.");

		pickpocketSandy = new NpcStep(this, NpcID.HANDSAND_SANDY0, new WorldPoint(2790, 3175, 0), "Pickpocket Sandy for some sand.");
		talkToBertAboutScroll = new NpcStep(this, NpcID.HANDSAND_BERT_1OP, new WorldPoint(2551, 3099, 0), "Return to Bert in west Yanille with the rota and sand.", bertsRota, sandysRota);

		ringBellAgain = new ObjectStep(this, ObjectID.ZOGRE_OUTDOOR_BELL, new WorldPoint(2598, 3085, 0), "Ring the bell outside the Wizards' Guild in Yanille. Talk to Zavistic Rarve when he appears.", magicScroll);
		ringBellAgain.addDialogStep("I have a rather sandy problem that I'd like to palm off on you.");
		talkToRarveAgain = new ObjectStep(this, ObjectID.ZOGRE_OUTDOOR_BELL, new WorldPoint(2598, 3085, 0), "Talk to Zavistic Rarve again to get teleported to Port Sarim.", vial);
		talkToRarveAgain.addDialogSteps("Can you help me more?", "Yes, that would be great!", "I have a rather sandy problem that I'd like to palm off on you.");

		talkToBetty = new NpcStep(this, NpcID.BETTY, new WorldPoint(3014, 3258, 0), "Travel to Port Sarim, and talk to Betty in the magic shop.", vial);
		talkToBetty.addDialogStep("Talk to Betty about the Hand in the Sand.");
		addRedberries = new DetailedQuestStep(this, "Use redberries on the bottled water.", bottledWater, redberries);
		addWhiteberries = new DetailedQuestStep(this, "Use whiteberries on the red bottled water", redberryJuice, whiteberries);
		useDyeOnLanternLens = new DetailedQuestStep(this, "Use the pink dye on a lantern lens.", pinkDye, lanternLens);
		talkToBettyAgain = new NpcStep(this, NpcID.BETTY, new WorldPoint(3014, 3258, 0), "Talk to Betty with the pink lens.");
		talkToBettyAgain.addDialogStep("Talk to Betty about the Hand in the Sand.");

		standInDoorway = new DetailedQuestStep(this, new WorldPoint(3016, 3259, 0), "Stand in Betty's doorway and use the rose-tinted lens on the counter.");
		useLensOnCounter = new ObjectStep(this, ObjectID.HANDSAND_COUNTER_MULTILOC, new WorldPoint(3013, 3258, 0), "Stand in Betty's doorway and use the rose-tinted lens on the counter.", roseLens);
		useLensOnCounter.addIcon(ItemID.HANDSAND_ROSE_LENS);
		useLensOnCounter.addSubSteps(standInDoorway);
		talkToBettyOnceMore =  new NpcStep(this, NpcID.BETTY, new WorldPoint(3014, 3258, 0), "Talk to Betty again.", truthSerum, sand);
		talkToBettyOnceMore.addDialogStep("Talk to Betty about the Hand in the Sand.");
		talkToSandyWithPotion =  new NpcStep(this, NpcID.HANDSAND_SANDY0, new WorldPoint(2790, 3175, 0), "Talk to Sandy in Brimhaven again with the truth serum. Select distractions until one works.", truthSerum);
		useSerumOnCoffee = new ObjectStep(this, ObjectID.HANDSAND_COFFEE_MULTILOC, new WorldPoint(2789, 3176, 0), "Use the truth serum on Sandy's coffee mug.",
			truthSerum.highlighted());
		useSerumOnCoffee.addIcon(ItemID.HANDSAND_TRUTHSERUM);
		activateMagicalOrb = new DetailedQuestStep(this, new WorldPoint(2789, 3175, 0), "Activate the magical orb next to Sandy.", magicalOrb);

		interrogateSandy = new NpcStep(this, NpcID.HANDSAND_SANDY0, new WorldPoint(2790, 3175, 0), "Ask Sandy all questions available with the Magical orb (a) in your inventory.", activatedOrb);
		interrogateSandy.addDialogSteps("Why is Bert's rota different from the original?", "Why doesn't Bert remember the change in his hours?", "What happened to the wizard?");

		ringBellAfterInterrogation = new ObjectStep(this, ObjectID.ZOGRE_OUTDOOR_BELL, new WorldPoint(2598, 3085, 0), "Return to the Wizards' Guild in Yanille and ring the bell outside. Talk to Zavistic Rarve when he appears.", activatedOrb, earthRunes5, bucketOfSand);
		ringBellAfterInterrogation.addDialogStep("I have a rather sandy problem that I'd like to palm off on you.");
		ringBellWithItems = new ObjectStep(this, ObjectID.ZOGRE_OUTDOOR_BELL, new WorldPoint(2598, 3085, 0), "Give Zavistic Rarve 5 earth runes and a bucket of sand.", earthRunes5, bucketOfSand);
		ringBellWithItems.addDialogStep("I have a rather sandy problem that I'd like to palm off on you.");
		talkToMazion = new NpcStep(this, NpcID.HANDSAND_NAZIOM, new WorldPoint(2815, 3340, 0), "Travel to Entrana (bank all combat gear), and talk to Mazion at the sand pit.");
		talkToMazion.addTeleport(teleportToPortSarim);
		ringBellEnd = new ObjectStep(this, ObjectID.ZOGRE_OUTDOOR_BELL, new WorldPoint(2598, 3085, 0), "Return to the Wizards' Guild in Yanille and ring the bell outside. Talk to Zavistic Rarve when he appears to finish the quest.", wizardsHead);
		ringBellEnd.addDialogStep("I have a rather sandy problem that I'd like to palm off on you.");
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
		reqs.add(teleportToPortSarim);

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
		allSteps.add(new PanelDetails("Finishing off", Arrays.asList(ringBellAfterInterrogation, ringBellWithItems, talkToMazion, ringBellEnd),
			List.of(earthRunes5, bucketOfSand), List.of(teleportToPortSarim)));
		return allSteps;
	}
}
