/*
 * Copyright (c) 2020, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.quests.mountaindaughter;

import com.questhelper.QuestHelperQuest;
import com.questhelper.steps.conditional.Operation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;
import com.questhelper.ItemCollections;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.TileStep;
import com.questhelper.steps.conditional.Conditions;
import com.questhelper.steps.conditional.ItemRequirementCondition;
import com.questhelper.steps.conditional.VarbitCondition;
import com.questhelper.steps.conditional.ZoneCondition;

@QuestDescriptor(
	quest = QuestHelperQuest.MOUNTAIN_DAUGHTER
)
public class MountainDaughter extends BasicQuestHelper
{
	private Zone CAMP_ZONE_1, CAMP_ZONE_2, CAMP_ZONE_3, LAKE_ISLAND_1, LAKE_ISLAND_2, LAKE_ISLAND_3, KENDAL_CAVE;

	private ItemRequirement axe, pickaxe, spade, whitePearl, whitePearlSeed, mud, plank, muddyRocks, safetyGuarantee, halfRock, gloves, corpse, pole, rope;

	private Conditions onIsland1, onIsland2, onIsland3, inTheCamp, askedAboutDiplomacy, askedAboutFoodAndDiplomacy, spokenToSvidi, spokenToBrundt, minedRock, hasCorpse,
		gottenGuarantee, givenGuaranteeToSvidi, gottenFruit, gottenSeed, finishedDiplomacy, finishedFoodAndDiplomacy, inKendalCave, hasRocks, hasNecklace, hasBuried;

	private QuestStep enterCamp, enterCampOverRocks, talkToHamal, rubMudIntoTree, poleVaultRocks, plankRocks, listenToSpirit, plankRocksReturn, talkToHamalAfterSpirit,
		talkToJokul, talkToSvidi, speakToBrundt, getRockFragment, returnToBrundt, returnToSvidi, getFruit, eatFruit, returnToSpirit, returnToHamalAboutFood,
		returnToHamalAboutDiplomacy, talkToKendal, noPlankRocksReturn, enterCave, grabCorpse, bringCorpseToHamal, collectRocks, createCairn, buryCorpseOnIsland, speakRagnar;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		loadItemRequirements();
		loadConditions();
		loadQuestSteps();

		Map<Integer, QuestStep> steps = new HashMap<>();

		ConditionalStep enteringTheCamp = new ConditionalStep(this, enterCamp);
		enteringTheCamp.addStep(inTheCamp, talkToHamal);

		steps.put(0, enteringTheCamp);

		ConditionalStep speakToSpirit = new ConditionalStep(this, enterCampOverRocks);
		speakToSpirit.addStep(onIsland3, listenToSpirit);
		speakToSpirit.addStep(onIsland2, plankRocks);
		speakToSpirit.addStep(onIsland1, poleVaultRocks);
		speakToSpirit.addStep(inTheCamp, rubMudIntoTree);

		steps.put(10, speakToSpirit);

		ConditionalStep helpTheCamp = new ConditionalStep(this, enterCampOverRocks);
		helpTheCamp.addStep(finishedFoodAndDiplomacy, returnToSpirit);
		helpTheCamp.addStep(finishedDiplomacy, returnToHamalAboutFood);
		helpTheCamp.addStep(gottenSeed, returnToHamalAboutDiplomacy);
		helpTheCamp.addStep(gottenFruit, eatFruit);
		helpTheCamp.addStep(givenGuaranteeToSvidi, getFruit);
		helpTheCamp.addStep(gottenGuarantee, returnToSvidi);
		helpTheCamp.addStep(minedRock, returnToBrundt);
		helpTheCamp.addStep(spokenToBrundt, getRockFragment);
		helpTheCamp.addStep(spokenToSvidi, speakToBrundt);
		helpTheCamp.addStep(askedAboutFoodAndDiplomacy, talkToSvidi);
		helpTheCamp.addStep(askedAboutDiplomacy, talkToJokul);
		helpTheCamp.addStep(onIsland3, plankRocksReturn);
		helpTheCamp.addStep(inTheCamp, talkToHamalAfterSpirit);

		steps.put(20, helpTheCamp);

		ConditionalStep talkKendal = new ConditionalStep(this, enterCave);
		talkKendal.addStep(onIsland3, noPlankRocksReturn);
		talkKendal.addStep(inKendalCave, talkToKendal);

		steps.put(30, talkKendal);

		ConditionalStep killKendalStep = new ConditionalStep(this, enterCave);
		killKendalStep.addStep(inKendalCave, talkToKendal);

		steps.put(40, killKendalStep);

		ConditionalStep returnTheCorpse = new ConditionalStep(this, enterCave);
		returnTheCorpse.addStep(hasCorpse, bringCorpseToHamal);
		returnTheCorpse.addStep(inKendalCave, grabCorpse);

		steps.put(50, returnTheCorpse);

		ConditionalStep buryCorpse = new ConditionalStep(this, enterCampOverRocks);
		buryCorpse.addStep(hasBuried, createCairn);
		buryCorpse.addStep(hasNecklace, buryCorpseOnIsland);
		buryCorpse.addStep(hasRocks, speakRagnar);
		buryCorpse.addStep(inTheCamp, collectRocks);

		steps.put(60, buryCorpse);

		return steps;
	}

	private void loadZones() {
		CAMP_ZONE_1 = new Zone(new WorldPoint(2758,3660,0), new WorldPoint(2821,3664,0));
		CAMP_ZONE_2 = new Zone(new WorldPoint(2767,3653,0), new WorldPoint(2821,3712,0));
		CAMP_ZONE_3 = new Zone(new WorldPoint(2751,3671,0), new WorldPoint(2767,3712,0));

		LAKE_ISLAND_1 = new Zone(new WorldPoint(2770,3681,0), new WorldPoint(2775,3688,0));
		LAKE_ISLAND_2 = new Zone(new WorldPoint(2770,3689,0), new WorldPoint(2776,3694,0));
		LAKE_ISLAND_3 = new Zone(new WorldPoint(2776,3688,0), new WorldPoint(2787,3698,0));

		KENDAL_CAVE = new Zone(new WorldPoint(2828,10118,0), new WorldPoint(2746,10047,0));
	}

	private void loadItemRequirements() {
		spade = new ItemRequirement("Spade", ItemID.SPADE);
		rope = new ItemRequirement("Rope", ItemID.ROPE);
		pickaxe = new ItemRequirement("A pickaxe", ItemID.BRONZE_PICKAXE);
		pickaxe.addAlternates(ItemCollections.getPickaxes());

		axe = new ItemRequirement("An axe", ItemID.BRONZE_AXE);
		axe.addAlternates(ItemCollections.getAxes());
		plank = new ItemRequirement("Plank", ItemID.PLANK);
		pole = new ItemRequirement("A staff or a Pole", ItemID.POLE);
		pole.setTip("You can find one in the north part of the Mountain Camp.");
		gloves = new ItemRequirement("Gloves", ItemID.LEATHER_GLOVES);
		gloves.setTip("You can use most other gloves, with a few exceptions (Slayer, Mystic, Ranger, Moonclan, Lunar, Infinity, vambraces).");

		mud = new ItemRequirement("Mud", ItemID.MUD);
		mud.setTip("You can get some mud from the mud pool south of Hamal's tent. You'll need a spade.");

		halfRock = new ItemRequirement("Half a rock", ItemID.HALF_A_ROCK);
		halfRock.setTip("You can get another piece by using a pickaxe on the Ancient Rock in the Mountain Camp.");

		safetyGuarantee = new ItemRequirement("Safety Guarantee", ItemID.SAFETY_GUARANTEE);
		safetyGuarantee.setTip("You can get another guarantee from Brundt in Rellekka's longhall.");

		whitePearl = new ItemRequirement("White pearl", ItemID.WHITE_PEARL);
		whitePearlSeed = new ItemRequirement("White pearl seed", ItemID.WHITE_PEARL_SEED);
		corpse = new ItemRequirement("Corpse of woman", ItemID.CORPSE_OF_WOMAN);
		corpse.setTip("You can find this corpse again in the Kendal's cave.");
		muddyRocks = new ItemRequirement("Muddy rock", ItemID.MUDDY_ROCK, 5);
	}

	private void loadConditions() {
		onIsland1 = new Conditions(new ZoneCondition(LAKE_ISLAND_1));
		onIsland2 = new Conditions(new ZoneCondition(LAKE_ISLAND_2));
		onIsland3 = new Conditions(new ZoneCondition(LAKE_ISLAND_3));

		inTheCamp = new Conditions(new ZoneCondition(CAMP_ZONE_1, CAMP_ZONE_2, CAMP_ZONE_3));
		askedAboutDiplomacy = new Conditions(new VarbitCondition(262, 10));

		VarbitCondition askedAboutFood = new VarbitCondition(263, 10, Operation.GREATER_EQUAL);
		askedAboutFoodAndDiplomacy = new Conditions(new VarbitCondition(262, 10), askedAboutFood);
		spokenToSvidi = new Conditions(new VarbitCondition(262, 20), askedAboutFood);
		spokenToBrundt = new Conditions(new VarbitCondition(262, 30), askedAboutFood);
		minedRock = new Conditions(new VarbitCondition(262, 40), askedAboutFood);
		gottenGuarantee = new Conditions(new VarbitCondition(262, 50), askedAboutFood);
		givenGuaranteeToSvidi = new Conditions(new VarbitCondition(262, 60), askedAboutFood);
		gottenFruit = new Conditions(new ItemRequirementCondition(whitePearl));
		gottenSeed = new Conditions(new ItemRequirementCondition(whitePearlSeed));
		finishedDiplomacy = new Conditions(new VarbitCondition(266, 1));
		finishedFoodAndDiplomacy = new Conditions(new VarbitCondition(266, 1), new VarbitCondition(263, 20));
		inKendalCave = new Conditions(new ZoneCondition(KENDAL_CAVE));
		hasCorpse = new Conditions(new ItemRequirementCondition(corpse));
		hasRocks = new Conditions(new ItemRequirementCondition(new ItemRequirement("Muddy rock", ItemID.MUDDY_ROCK, 5)));
		hasNecklace = new Conditions(new ItemRequirementCondition(new ItemRequirement("Asleif's necklace", ItemID.ASLEIFS_NECKLACE)));
		hasBuried = new Conditions(new VarbitCondition(273, 1));
	}

	private void loadQuestSteps() {
		enterCamp = new ObjectStep(this, ObjectID.BOULDER_5842, new WorldPoint(2766, 3667, 0),
			"Use your rope on the boulder outside the Mountain Camp east of Rellekka.",
			rope);
		enterCamp.addIcon(ItemID.ROPE);

		enterCampOverRocks = new ObjectStep(this, ObjectID.ROCKSLIDE_5847,  new WorldPoint(2760, 3658, 0),
			"Return to the Mountain Camp.",
			rope);

		talkToHamal = new NpcStep(this, NpcID.HAMAL_THE_CHIEFTAIN, new WorldPoint(2810, 3672, 0), "Speak to Hamal the Chieftain in the Mountain Camp.",
			spade, rope, pickaxe, axe, plank, pole, gloves);
		talkToHamal.addDialogStep("Why is everyone so hostile?");
		talkToHamal.addDialogStep("So what are you doing up here?");
		talkToHamal.addDialogStep("I will search for her!");

		rubMudIntoTree = new ObjectStep(this, ObjectID.TALL_TREE, new WorldPoint(2772, 3679, 0),
			"Use mud on the Tall Tree on the lake north of the camp, and then climb it.",
			pole, mud, plank);
		rubMudIntoTree.addIcon(ItemID.MUD);

		poleVaultRocks = new ObjectStep(this, ObjectID.CLUMP_OF_ROCKS, new WorldPoint(2773, 3688, 0),
			"Use your pole or a staff on the clump of rocks.",
			pole, plank);
		poleVaultRocks.addIcon(ItemID.POLE);

		plankRocks = new ObjectStep(this, ObjectID.FLAT_STONE, new WorldPoint(2775, 3691, 0),
			"Use a plank on the flat stone.",
			plank);
		plankRocks.addIcon(ItemID.PLANK);

		listenToSpirit = new ObjectStep(this, ObjectID.SHINING_POOL_5897, new WorldPoint(2781, 3689, 0),
			"Listen to the Shining Pool.");
		listenToSpirit.addDialogStep("Hello! Who are you?");
		listenToSpirit.addDialogStep("So what exactly do you want from me?");
		listenToSpirit.addDialogStep("That sounds like something I can do.");
		listenToSpirit.addDialogStep("I'll get right on it.");

		plankRocksReturn = new ObjectStep(this, ObjectID.FLAT_STONE_5851, new WorldPoint(2777, 3691, 0),
			"Use a plank on the flat stone to return to shore.",
			plank);
		plankRocksReturn.addDialogStep("Yes.");
		plankRocksReturn.addIcon(ItemID.PLANK);

		talkToHamalAfterSpirit = new NpcStep(this, NpcID.HAMAL_THE_CHIEFTAIN, new WorldPoint(2810, 3672, 0),
			"Speak to Hamal the Chieftain in the Mountain Camp.",
			spade, rope, pickaxe, axe, plank, pole, gloves);
		talkToHamalAfterSpirit.addDialogStep("About the people of Rellekka...");

		talkToJokul = new NpcStep(this, NpcID.JOKUL, new WorldPoint(2812, 3680, 0),
			"Speak to Jokul north of Hamal's tent.");

		talkToSvidi = new NpcStep(this, NpcID.SVIDI, new WorldPoint(2717, 3666, 0),
			"Speak to Svidi who roams in the forest east of Rellekka.",
			pickaxe);
		talkToSvidi.addDialogStep("Can't I persuade you to go in there somehow?");

		speakToBrundt = new NpcStep(this, NpcID.BRUNDT_THE_CHIEFTAIN_9263, new WorldPoint(2658, 3669, 0),
			"Speak to Brundt the Chieftain in the Rellekka's longhall.",
			spade, rope, pickaxe, axe, plank, pole, gloves);
		speakToBrundt.addDialogStep("Ask about the mountain camp.");
		speakToBrundt.addDialogStep("Did it look pretty?");

		getRockFragment = new ObjectStep(this, ObjectID.ANCIENT_ROCK, new WorldPoint(2799, 3660, 0),
			"Use a pickaxe on the Ancient Rock in the Mountain Camp.",
			pickaxe);
		getRockFragment.addIcon(ItemID.BRONZE_PICKAXE);

		returnToBrundt = new NpcStep(this, NpcID.BRUNDT_THE_CHIEFTAIN_9263, new WorldPoint(2658, 3669, 0),
			"Return to Brundt the Chieftain in the Rellekka's longhall.",
			halfRock);
		returnToBrundt.addDialogStep("Ask about the mountain camp.");

		returnToSvidi = new NpcStep(this, NpcID.SVIDI, new WorldPoint(2717, 3666, 0),
			"Return to Svidi who roams in the forest east of Rellekka.",
			safetyGuarantee);

		getFruit = new ObjectStep(this, ObjectID.THORNY_BUSHES, new WorldPoint(2849, 3497, 0),
			"Go to the top of White Wolf Mountain and pick the Thorny Bushes whilst wearing gloves.",
			gloves);

		eatFruit = new DetailedQuestStep(this, "Eat the White Pearl.", whitePearl);

		returnToHamalAboutDiplomacy = new NpcStep(this, NpcID.HAMAL_THE_CHIEFTAIN, new WorldPoint(2810, 3672, 0),
			"Return to Hamal the Chieftain in the Mountain Camp.",
			whitePearlSeed);
		returnToHamalAboutDiplomacy.addDialogStep("About the people of Rellekka...");

		returnToHamalAboutFood = new NpcStep(this, NpcID.HAMAL_THE_CHIEFTAIN, new WorldPoint(2810, 3672, 0),
			"Return to Hamal the Chieftain in the Mountain Camp.",
			whitePearlSeed);
		returnToHamalAboutFood.addDialogStep("About your food supplies...");

		returnToSpirit = new ObjectStep(this, ObjectID.SHINING_POOL_5897, new WorldPoint(2781, 3689, 0),
			"Return to the centre of the pool north of the Mountain Camp and listen to it.",
			pole, plank);

		noPlankRocksReturn = new ObjectStep(this, ObjectID.FLAT_STONE_5851, new WorldPoint(2777, 3691, 0),
			"Attempt to jump across the flat stone WITHOUT a plank to return to the north shore.");

		enterCave = new ObjectStep(this, ObjectID.CAVE_ENTRANCE_5857, new WorldPoint(2809, 3703, 0),
			"Cut through the trees north east of the lake and enter the cave there. Bring combat gear.",
			axe);

		talkToKendal = new NpcStep(this, NpcID.THE_KENDAL, new WorldPoint(2788, 10081, 0),
			"Speak to the Kendal, then kill him.");
		talkToKendal.addDialogStep("It's just me, no one special.");
		talkToKendal.addDialogStep("You mean a sacrifice?");
		talkToKendal.addDialogStep("You look like a man in a bearsuit!");
		talkToKendal.addDialogStep("Can I see that corpse?");
		talkToKendal.addDialogStep("I humbly request to be given the remains.");
		talkToKendal.addDialogStep("I will kill you myself!");

		grabCorpse = new TileStep(this, new WorldPoint(2784, 10078, 0), "Pick up the Corpse of Woman.");
		bringCorpseToHamal = new NpcStep(this, NpcID.HAMAL_THE_CHIEFTAIN, new WorldPoint(2810, 3672, 0),
			"Bring the corpse to Hamal.",
			corpse);
		bringCorpseToHamal.addDialogStep("But he's not a god!");
		bringCorpseToHamal.addDialogStep("I will.");

		collectRocks = new DetailedQuestStep(this, "Collect Muddy Rocks from around the camp.", muddyRocks);

		speakRagnar = new NpcStep(this, NpcID.RAGNAR, new WorldPoint(2766, 3676, 0),
			"Speak to Ragnar.",
			corpse, muddyRocks);
		speakRagnar.addDialogStep("Thank you. I will make sure she's given a proper burial now.");

		buryCorpseOnIsland = new TileStep(this, new WorldPoint(2782, 3694, 0), "Return to the centre of the lake and bury the corpse.",
			corpse, pole, plank);

		createCairn = new ObjectStep(this, ObjectID.BURIAL_MOUND, new WorldPoint(2783, 3694, 0),
			"Use the Muddy rocks on the Burial Mound at the centre of the Mountain Camp's lake.",
			muddyRocks);
		createCairn.addIcon(ItemID.MUDDY_ROCK);
	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(spade);
		reqs.add(rope);
		reqs.add(pickaxe);
		reqs.add(axe);
		reqs.add(plank);
		reqs.add(gloves);

		return reqs;
	}

	@Override
	public ArrayList<String> getCombatRequirements()
	{
		return new ArrayList<>(Arrays.asList("The Kendal (level 70)"));
	}

	@Override
	public ArrayList<PanelDetails> getPanels() {
		ArrayList<PanelDetails> allSteps = new ArrayList<>();

		allSteps.add(new PanelDetails("Speak to Hamal", new ArrayList<>(Arrays.asList(enterCamp, talkToHamal)), rope, spade, plank, pickaxe));
		allSteps.add(new PanelDetails("Go to the centre of the lake", new ArrayList<>(Arrays.asList(rubMudIntoTree, poleVaultRocks, plankRocks, listenToSpirit))));
		allSteps.add(new PanelDetails("Find out how to help", new ArrayList<>(Arrays.asList(talkToHamalAfterSpirit, talkToJokul))));
		allSteps.add(new PanelDetails("Making peace with Rellekka", new ArrayList<>(Arrays.asList(talkToSvidi, speakToBrundt, getRockFragment, returnToBrundt, returnToSvidi))));
		allSteps.add(new PanelDetails("Find a new food source", new ArrayList<>(Arrays.asList(getFruit, eatFruit))));
		allSteps.add(new PanelDetails("Prepare for a fight", new ArrayList<>(Arrays.asList(new DetailedQuestStep(this, "Prepare to fight The Kendal (level 70)"))), pole, plank, axe, whitePearlSeed));
		allSteps.add(new PanelDetails("Tell Hamal about your success", new ArrayList<>(Arrays.asList(returnToHamalAboutDiplomacy, returnToHamalAboutFood))));
		allSteps.add(new PanelDetails("Tell Asleif about your success", new ArrayList<>(Arrays.asList(returnToSpirit))));
		allSteps.add(new PanelDetails("Find Asleif's corpse", new ArrayList<>(Arrays.asList(enterCave, talkToKendal, grabCorpse))));
		allSteps.add(new PanelDetails("Bring Asleif's corpse to Hamal", new ArrayList<>(Arrays.asList(bringCorpseToHamal))));
		allSteps.add(new PanelDetails("Bury Asleif", new ArrayList<>(Arrays.asList(collectRocks, speakRagnar, buryCorpseOnIsland, createCairn))));

		return allSteps;
	}
}