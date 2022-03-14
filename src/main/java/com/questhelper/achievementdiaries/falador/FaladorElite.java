/*
 * Copyright (c) 2021, Kerpackie <https://github.com/Kerpackie/>
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

package com.questhelper.achievementdiaries.falador;


import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.*;
import com.questhelper.steps.emote.QuestEmote;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@QuestDescriptor(
	quest = QuestHelperQuest.FALADOR_ELITE
)

public class FaladorElite extends ComplexStateQuestHelper
{

	//Items Required
	ItemRequirement pureEss28, airTiara, coins1920, spade, axe, skillCape, toadflaxPotionUnf, crushedNest, rake, magicTreeSapling;

	//Items Recommended
	ItemRequirement faladorTeleport;

	Requirement wanted;

	Requirement notCraftedAirRunes, notPurchasedWhite2hSword, notGotMagicRoots, notPerformedSkillCapeEmote, notJumpedOverStrangeFloor,
		notMadeSaraBrew, magicTreeNearbyNotCheckedVar, stumpNearbyVar, magicTreeNearbyCheckedVar;

	QuestStep claimReward, enterAirAltar, craftAirRunes, goUpFaladorCastle2, goUpFaladorCastle1, purchaseWhite2hSword, chopMagicTree,
		goUpFaladorCastle1Emote, goUpFaladorCastle2Emote, goUpFaladorCastle3Emote, performEmote, goToTavDungeon, crossStrangeFloor,
		goToEastBank, craftSaraBrew, growMagicTree, digUpStumpForRoots;

	Zone airAltar, faladorCastle1, faladorCastle2, faladorCastle3, tavDungeon, eastBank;

	ZoneRequirement inAirAltar, inFaladorCastle1, inFaladorCastle2, inFaladorCastle3, inTavDungeon, inEastBank;

	@Override
	public QuestStep loadStep()
	{
		loadZones();
		setupRequirements();
		setupSteps();

		ConditionalStep doElite = new ConditionalStep(this, claimReward);
		doElite.addStep(new Conditions(notCraftedAirRunes, inAirAltar), craftAirRunes);
		doElite.addStep(notCraftedAirRunes, enterAirAltar);
		doElite.addStep(new Conditions(notGotMagicRoots, stumpNearbyVar), digUpStumpForRoots);
		doElite.addStep(new Conditions(notGotMagicRoots, magicTreeNearbyCheckedVar), chopMagicTree);
		doElite.addStep(new Conditions(notGotMagicRoots, magicTreeNearbyNotCheckedVar), chopMagicTree);
		doElite.addStep(notGotMagicRoots, growMagicTree);
		doElite.addStep(new Conditions(notPerformedSkillCapeEmote, inFaladorCastle3), performEmote);
		doElite.addStep(new Conditions(notPerformedSkillCapeEmote, inFaladorCastle2), goUpFaladorCastle3Emote);
		doElite.addStep(new Conditions(notPerformedSkillCapeEmote, inFaladorCastle1), goUpFaladorCastle2Emote);
		doElite.addStep(notPerformedSkillCapeEmote, goUpFaladorCastle1Emote);
		doElite.addStep(new Conditions(notJumpedOverStrangeFloor, inTavDungeon), crossStrangeFloor);
		doElite.addStep(notJumpedOverStrangeFloor, goToTavDungeon);
		doElite.addStep(new Conditions(notMadeSaraBrew, inEastBank), craftSaraBrew);
		doElite.addStep(new Conditions(notMadeSaraBrew), goToEastBank);
		doElite.addStep(new Conditions(notPurchasedWhite2hSword, inFaladorCastle2), purchaseWhite2hSword);
		doElite.addStep(new Conditions(notPurchasedWhite2hSword, inFaladorCastle1), goUpFaladorCastle2);
		doElite.addStep(notPurchasedWhite2hSword, goUpFaladorCastle1);

		return doElite;
	}

	public void setupRequirements()
	{
		notCraftedAirRunes = new VarplayerRequirement(1187, false, 5);
		notPurchasedWhite2hSword = new VarplayerRequirement(1187, false, 6);
		notGotMagicRoots = new VarplayerRequirement(1187, false, 7);
		notPerformedSkillCapeEmote = new VarplayerRequirement(1187, false, 8);
		notJumpedOverStrangeFloor = new VarplayerRequirement(1187, false, 9);
		notMadeSaraBrew = new VarplayerRequirement(1187, false, 10);

		pureEss28 = new ItemRequirement("Pure Essence", ItemID.PURE_ESSENCE, 28).showConditioned(notCraftedAirRunes);
		airTiara = new ItemRequirement("Air Tiara", ItemID.AIR_TIARA, 1, true).showConditioned(notCraftedAirRunes);
		coins1920 = new ItemRequirement("Coins", ItemCollections.getCoins(), 1920).showConditioned(notPurchasedWhite2hSword);
		spade = new ItemRequirement("Spade", ItemID.SPADE).showConditioned(notGotMagicRoots);
		axe = new ItemRequirement("Axe", ItemCollections.getAxes()).showConditioned(notGotMagicRoots);
		rake = new ItemRequirement("Rake", ItemID.RAKE).showConditioned(notGotMagicRoots);
		magicTreeSapling = new ItemRequirement("Magic Sapling", ItemID.MAGIC_SAPLING).showConditioned(notGotMagicRoots);
		skillCape = new ItemRequirement("Any Skill Cape or Quest Cape", ItemCollections.getSkillCape()).showConditioned(notPerformedSkillCapeEmote);
		toadflaxPotionUnf = new ItemRequirement("Toadflax Potion (unf)", ItemID.TOADFLAX_POTION_UNF).showConditioned(notMadeSaraBrew);
		crushedNest = new ItemRequirement("Crushed Nest", ItemID.CRUSHED_NEST).showConditioned(notMadeSaraBrew);

		faladorTeleport = new ItemRequirement("Multiple Teleports to Falador", ItemID.FALADOR_TELEPORT, -1);

		magicTreeNearbyNotCheckedVar = new VarbitRequirement(4471, 60);
		magicTreeNearbyCheckedVar = new VarbitRequirement(4471, 61);
		stumpNearbyVar = new VarbitRequirement(4471, 62);

		inAirAltar = new ZoneRequirement(airAltar);
		inFaladorCastle1 = new ZoneRequirement(faladorCastle1);
		inFaladorCastle2 = new ZoneRequirement(faladorCastle2);
		inFaladorCastle3 = new ZoneRequirement(faladorCastle3);
		inTavDungeon = new ZoneRequirement(tavDungeon);
		inEastBank = new ZoneRequirement(eastBank);

		wanted = new QuestRequirement(QuestHelperQuest.WANTED, QuestState.FINISHED);
	}

	public void loadZones()
	{
		airAltar = new Zone(new WorldPoint(2895, 4851, 0), new WorldPoint(2859, 4819, 0));
		faladorCastle1 = new Zone(new WorldPoint(2954, 3328, 1), new WorldPoint(2997, 3353, 1));
		faladorCastle2 = new Zone(new WorldPoint(2954, 3328, 2), new WorldPoint(2997, 3353, 2));
		faladorCastle3 = new Zone(new WorldPoint(2954, 3328, 3), new WorldPoint(2997, 3353, 3));
		tavDungeon = new Zone(new WorldPoint(2809, 9846, 0), new WorldPoint(2949, 9786, 0));
		eastBank = new Zone(new WorldPoint(3009, 3358, 0), new WorldPoint(3018, 3355, 0));
	}

	public void setupSteps()
	{
		//Step 1 - Air Runes
		enterAirAltar = new ObjectStep(this, ObjectID.MYSTERIOUS_RUINS_29090, new WorldPoint(2985, 3292, 0),
			"Go to the Air Altar south of Falador", pureEss28, airTiara);
		craftAirRunes = new ObjectStep(this, ObjectID.ALTAR_34760, new WorldPoint(2843, 4833, 0),
			"Use your essence on the Altar to craft the Air Runes.", pureEss28);
		craftAirRunes.addIcon(ItemID.PURE_ESSENCE);
		enterAirAltar.addSubSteps(craftAirRunes);

		//Step 2 - Purchase 2H Sword
		goUpFaladorCastle1 = new ObjectStep(this, ObjectID.LADDER_24070, new WorldPoint(2994, 3341, 0),
			"Climb up the east ladder in Falador Castle.", coins1920);
		goUpFaladorCastle2 = new ObjectStep(this, ObjectID.STAIRCASE_24077, new WorldPoint(2985, 3338, 1),
			"Go up the staircase west of the ladder on the 1st floor.", coins1920);
		purchaseWhite2hSword = new NpcStep(this, NpcID.SIR_VYVIN, new WorldPoint(2981, 3338, 2),
			"Speak to Sir Vyvin to purchase a White 2H Sword.", coins1920);
		purchaseWhite2hSword.addDialogStep("Do you have anything to trade?");

		//Step 3 - Magic Roots
		growMagicTree = new ObjectStep(this, NullObjectID.NULL_8389, new WorldPoint(3004, 3373, 0),
			"Grow and check the health of a magic tree in Falador Park, afterwards dig up the stump to get the Magic Roots.", magicTreeSapling, rake, spade);
		chopMagicTree = new ObjectStep(this, NullObjectID.NULL_8389, new WorldPoint(3004, 3373, 0),
			"Chop the magic tree that you grew in Falador Park, afterwards dig up the stump to get the Magic Roots.", axe, spade);
		digUpStumpForRoots = new ObjectStep(this, NullObjectID.NULL_8389, new WorldPoint(3004, 3373, 0),
			"Dig up the stump to get the magic roots.", spade);

		//Step 4 - Emote Fal Castle
		goUpFaladorCastle1Emote = new ObjectStep(this, ObjectID.STAIRCASE_24072, new WorldPoint(2954, 3338, 0),
			"Climb the staircase to the First Floor of the White Knights Castle.", skillCape);
		goUpFaladorCastle2Emote = new ObjectStep(this, ObjectID.STAIRCASE_24072, new WorldPoint(2960, 3338, 1),
			"Climb the staircase to the Second Floor of the White Knights Castle.", skillCape);
		goUpFaladorCastle3Emote = new ObjectStep(this, ObjectID.STAIRCASE_24072, new WorldPoint(2957, 3338, 2),
			"Climb the staircase to the Top Floor of the White Knights Castle", skillCape);
		performEmote = new EmoteStep(this, QuestEmote.SKILL_CAPE, new WorldPoint(2960, 3338, 3),
			"Equip your skill cape and perform its emote!", skillCape);

		//Step 5 - Tav Dungeon
		goToTavDungeon = new ObjectStep(this, ObjectID.LADDER_16680, new WorldPoint(2884, 3397, 0),
			"Go to the Taverly dungeon.");
		crossStrangeFloor = new ObjectStep(this, ObjectID.STRANGE_FLOOR, new WorldPoint(2879, 9813, 0),
			"Cross the Strange Floor to complete the task!");

		//Step 6 - Sara Brew
		goToEastBank = new DetailedQuestStep(this, new WorldPoint(3013, 3356, 0),
			"Go to the Falador East Bank");
		craftSaraBrew = new DetailedQuestStep(this, new WorldPoint(3013, 3356, 0),
			"Craft a Saradomin Brew while inside the Falador East Bank.", toadflaxPotionUnf.highlighted(), crushedNest.highlighted());

		//Claim Reward
		claimReward = new NpcStep(this, NpcID.SIR_REBRAL, new WorldPoint(2977, 3346, 0),
			"Congratulations! Talk to Sir Rebral in the courtyard of The White Knight Castle to claim your reward!");
		claimReward.addDialogStep("I have a question about my Achievement Diary.");
	}

	@Override
	public List<String> getCombatRequirements()
	{
		ArrayList<String> reqs = new ArrayList<>();
		reqs.add("Giant Mole (level 230)");
		reqs.add("Skeletal Wyvern (level 140)");
		reqs.add("Blue dragon (level 140)");
		return reqs;
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(pureEss28, airTiara, coins1920, spade, axe, skillCape, toadflaxPotionUnf, crushedNest);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Collections.singletonList(faladorTeleport);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();

		req.add(new SkillRequirement(Skill.AGILITY, 80, true));
		req.add(new SkillRequirement(Skill.RUNECRAFT, 88, true));
		req.add(new SkillRequirement(Skill.FARMING, 91, true));
		req.add(new SkillRequirement(Skill.WOODCUTTING, 75, true));
		req.add(new SkillRequirement(Skill.HERBLORE, 81, true));

		req.add(wanted);

		return req;
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
				new ItemReward("Falador Shield (4)", ItemID.FALADOR_SHIELD_4, 1),
				new ItemReward("50,000 Exp. Lamp (Any skill over 70)", ItemID.ANTIQUE_LAMP, 1));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
				new UnlockReward("Tree patch in Falador is now disease free."),
				new UnlockReward("Increased chance at higher level ores when clearing pay-dirt in the Motherlode Mine."),
				new UnlockReward("Access to the alternative Amethyst mining spot."));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		PanelDetails airRunesSteps = new PanelDetails("One with the wind..", Arrays.asList(enterAirAltar,
			craftAirRunes),	new SkillRequirement(Skill.RUNECRAFT, 88, true), airTiara, pureEss28);
		airRunesSteps.setDisplayCondition(notCraftedAirRunes);
		allSteps.add(airRunesSteps);

		PanelDetails magicRootsSteps = new PanelDetails("Root of all magic", Collections.singletonList(chopMagicTree),
			new SkillRequirement(Skill.FARMING, 91, true),
			new SkillRequirement(Skill.WOODCUTTING, 75, true), axe, spade);
		magicRootsSteps.setDisplayCondition(notGotMagicRoots);
		allSteps.add(magicRootsSteps);

		PanelDetails capeEmoteSteps = new PanelDetails("Peak Efficiency", Arrays.asList(goUpFaladorCastle1Emote,
			goUpFaladorCastle2Emote, goUpFaladorCastle3Emote, performEmote), skillCape);
		capeEmoteSteps.setDisplayCondition(notPerformedSkillCapeEmote);
		allSteps.add(capeEmoteSteps);

		PanelDetails strangeFloorSteps = new PanelDetails("The River Styx", Arrays.asList(goToTavDungeon,
			crossStrangeFloor), new SkillRequirement(Skill.AGILITY, 80, true));
		strangeFloorSteps.setDisplayCondition(notJumpedOverStrangeFloor);
		allSteps.add(strangeFloorSteps);

		PanelDetails saraBrewSteps = new PanelDetails("Pot Head", Arrays.asList(goToEastBank, craftSaraBrew),
			new SkillRequirement(Skill.HERBLORE, 81, true), toadflaxPotionUnf, crushedNest);
		saraBrewSteps.setDisplayCondition(notMadeSaraBrew);
		allSteps.add(saraBrewSteps);

		PanelDetails swordSteps = new PanelDetails("*Tips Fedora*", Arrays.asList(goUpFaladorCastle1,
			goUpFaladorCastle2,	purchaseWhite2hSword), wanted, coins1920);
		swordSteps.setDisplayCondition(notPurchasedWhite2hSword);
		allSteps.add(swordSteps);

		PanelDetails finishOffSteps = new PanelDetails("Finishing off", Collections.singletonList(claimReward));
		allSteps.add(finishOffSteps);

		return allSteps;
	}
}