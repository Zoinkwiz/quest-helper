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

package com.questhelper.helpers.achievementdiaries.falador;


import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.ComplexRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.item.TeleportItemRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.*;
import com.questhelper.steps.emote.QuestEmote;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarPlayerID;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

	ConditionalStep craftedAirRunesTask, purchasedWhite2hSwordTask, gotMagicRootsTask, performedSkillCapeEmoteTask,
		jumpedOverStrangeFloorTask, madeSaraBrewTask;

	@Override
	public QuestStep loadStep()
	{
		initializeRequirements();
		setupSteps();

		ConditionalStep doElite = new ConditionalStep(this, claimReward);

		gotMagicRootsTask = new ConditionalStep(this, growMagicTree);
		gotMagicRootsTask.addStep(magicTreeNearbyNotCheckedVar, chopMagicTree);
		gotMagicRootsTask.addStep(magicTreeNearbyCheckedVar, chopMagicTree);
		gotMagicRootsTask.addStep(stumpNearbyVar, digUpStumpForRoots);
		doElite.addStep(notGotMagicRoots, gotMagicRootsTask);

		craftedAirRunesTask = new ConditionalStep(this, enterAirAltar);
		craftedAirRunesTask.addStep(inAirAltar, craftAirRunes);
		doElite.addStep(notCraftedAirRunes, craftedAirRunesTask);

		performedSkillCapeEmoteTask = new ConditionalStep(this, goUpFaladorCastle1Emote);
		performedSkillCapeEmoteTask.addStep(inFaladorCastle1, goUpFaladorCastle2Emote);
		performedSkillCapeEmoteTask.addStep(inFaladorCastle2, goUpFaladorCastle3Emote);
		performedSkillCapeEmoteTask.addStep(inFaladorCastle3, performEmote);
		doElite.addStep(notPerformedSkillCapeEmote, performedSkillCapeEmoteTask);

		jumpedOverStrangeFloorTask = new ConditionalStep(this, goToTavDungeon);
		jumpedOverStrangeFloorTask.addStep(inTavDungeon, crossStrangeFloor);
		doElite.addStep(notJumpedOverStrangeFloor, jumpedOverStrangeFloorTask);

		madeSaraBrewTask = new ConditionalStep(this, goToEastBank);
		madeSaraBrewTask.addStep(inEastBank, craftSaraBrew);
		doElite.addStep(new Conditions(notMadeSaraBrew), madeSaraBrewTask);

		purchasedWhite2hSwordTask = new ConditionalStep(this, goUpFaladorCastle1);
		purchasedWhite2hSwordTask.addStep(inFaladorCastle1, goUpFaladorCastle2);
		purchasedWhite2hSwordTask.addStep(inFaladorCastle2, purchaseWhite2hSword);
		doElite.addStep(notPurchasedWhite2hSword, purchasedWhite2hSwordTask);

		return doElite;
	}

	@Override
	protected void setupRequirements()
	{
		notCraftedAirRunes = new VarplayerRequirement(VarPlayerID.FALADOR_ACHIEVEMENT_DIARY2, false, 5);
		notPurchasedWhite2hSword = new VarplayerRequirement(VarPlayerID.FALADOR_ACHIEVEMENT_DIARY2, false, 6);
		notGotMagicRoots = new VarplayerRequirement(VarPlayerID.FALADOR_ACHIEVEMENT_DIARY2, false, 7);
		notPerformedSkillCapeEmote = new VarplayerRequirement(VarPlayerID.FALADOR_ACHIEVEMENT_DIARY2, false, 8);
		notJumpedOverStrangeFloor = new VarplayerRequirement(VarPlayerID.FALADOR_ACHIEVEMENT_DIARY2, false, 9);
		notMadeSaraBrew = new VarplayerRequirement(VarPlayerID.FALADOR_ACHIEVEMENT_DIARY2, false, 10);

		pureEss28 = new ItemRequirement("Pure Essence", ItemID.BLANKRUNE_HIGH, 28).showConditioned(notCraftedAirRunes);
		airTiara = new ItemRequirement("Air Tiara", ItemID.TIARA_AIR, 1, true).showConditioned(notCraftedAirRunes).isNotConsumed();
		coins1920 = new ItemRequirement("Coins", ItemCollections.COINS, 1920).showConditioned(notPurchasedWhite2hSword);
		spade = new ItemRequirement("Spade", ItemID.SPADE).showConditioned(notGotMagicRoots).isNotConsumed();
		axe = new ItemRequirement("Any Axe", ItemCollections.AXES).showConditioned(notGotMagicRoots).isNotConsumed();
		rake = new ItemRequirement("Rake", ItemID.RAKE).showConditioned(notGotMagicRoots).isNotConsumed();
		magicTreeSapling = new ItemRequirement("Magic Sapling", ItemID.PLANTPOT_MAGIC_TREE_SAPLING).showConditioned(notGotMagicRoots);
		skillCape = new ItemRequirement("Any Skill Cape or Quest Cape", ItemCollections.SKILLCAPE).showConditioned(notPerformedSkillCapeEmote);
		toadflaxPotionUnf = new ItemRequirement("Toadflax Potion (unf)", ItemID.TOADFLAXVIAL).showConditioned(notMadeSaraBrew);
		crushedNest = new ItemRequirement("Crushed Nest", ItemID.CRUSHED_BIRD_NEST).showConditioned(notMadeSaraBrew);

		faladorTeleport = new TeleportItemRequirement("Multiple Teleports to Falador", ItemID.POH_TABLET_FALADORTELEPORT, -1);

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

	@Override
	protected void setupZones()
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
		enterAirAltar = new ObjectStep(this, ObjectID.AIRTEMPLE_RUINED_NEW, new WorldPoint(2985, 3292, 0),
			"Go to the Air Altar south of Falador", pureEss28, airTiara);
		craftAirRunes = new ObjectStep(this, ObjectID.AIR_ALTAR, new WorldPoint(2843, 4833, 0),
			"Use your essence on the Altar to craft the Air Runes.", pureEss28);
		craftAirRunes.addIcon(ItemID.BLANKRUNE_HIGH);
		enterAirAltar.addSubSteps(craftAirRunes);

		//Step 2 - Purchase 2H Sword
		goUpFaladorCastle1 = new ObjectStep(this, ObjectID.FAI_FALADOR_CASTLE_LADDER_UP, new WorldPoint(2994, 3341, 0),
			"Climb up the east ladder in Falador Castle.", coins1920);
		goUpFaladorCastle2 = new ObjectStep(this, ObjectID.FAI_FALADOR_CASTLE_STAIRS, new WorldPoint(2985, 3338, 1),
			"Go up the staircase west of the ladder on the 1st floor.", coins1920);
		purchaseWhite2hSword = new NpcStep(this, NpcID.SIR_VYVIN, new WorldPoint(2981, 3338, 2),
			"Speak to Sir Vyvin to purchase a White 2H Sword.", coins1920);
		purchaseWhite2hSword.addDialogStep("Do you have anything to trade?");

		//Step 3 - Magic Roots
		growMagicTree = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_2, new WorldPoint(3004, 3373, 0),
			"Grow and check the health of a magic tree in Falador Park, afterwards dig up the stump to get the Magic Roots. " +
				"If you're waiting for it to grow and want to complete further tasks, use the tick box on panel.",
			magicTreeSapling, rake, spade);
		chopMagicTree = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_2, new WorldPoint(3004, 3373, 0),
			"Chop the magic tree that you grew in Falador Park, afterwards dig up the stump to get the Magic Roots.", axe, spade);
		digUpStumpForRoots = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_2, new WorldPoint(3004, 3373, 0),
			"Dig up the stump to get the magic roots.", spade);

		//Step 4 - Emote Fal Castle
		goUpFaladorCastle1Emote = new ObjectStep(this, ObjectID.FAI_FALADOR_CASTLE_SPIRALSTAIRS, new WorldPoint(2954, 3338, 0),
			"Climb the staircase to the First Floor of the White Knights Castle.", skillCape);
		goUpFaladorCastle2Emote = new ObjectStep(this, ObjectID.FAI_FALADOR_CASTLE_SPIRALSTAIRS, new WorldPoint(2960, 3338, 1),
			"Climb the staircase to the Second Floor of the White Knights Castle.", skillCape);
		goUpFaladorCastle3Emote = new ObjectStep(this, ObjectID.FAI_FALADOR_CASTLE_SPIRALSTAIRS, new WorldPoint(2957, 3338, 2),
			"Climb the staircase to the Top Floor of the White Knights Castle", skillCape);
		performEmote = new EmoteStep(this, QuestEmote.SKILL_CAPE, new WorldPoint(2960, 3338, 3),
			"Equip your skill cape and perform its emote!", skillCape);

		//Step 5 - Tav Dungeon
		goToTavDungeon = new ObjectStep(this, ObjectID.LADDER_OUTSIDE_TO_UNDERGROUND, new WorldPoint(2884, 3397, 0),
			"Go to the Taverley dungeon.");
		crossStrangeFloor = new ObjectStep(this, ObjectID.TAVERLY_DUNGEON_FLOOR_SPIKES_SC, new WorldPoint(2879, 9813, 0),
			"Cross the Strange Floor to complete the task!");

		//Step 6 - Sara Brew
		goToEastBank = new DetailedQuestStep(this, new WorldPoint(3013, 3356, 0),
			"Go to the Falador East Bank");
		craftSaraBrew = new DetailedQuestStep(this, new WorldPoint(3013, 3356, 0),
			"Craft a Saradomin Brew while inside the Falador East Bank.", toadflaxPotionUnf.highlighted(), crushedNest.highlighted());

		//Claim Reward
		claimReward = new NpcStep(this, NpcID.WHITE_KNIGHT_DIARY, new WorldPoint(2977, 3346, 0),
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
		req.add(new SkillRequirement(Skill.FARMING, 91, true));
		req.add(new SkillRequirement(Skill.HERBLORE, 81, true));
		req.add(new ComplexRequirement(LogicType.OR, "88 Runecraft or 55 with Raiments of the Eye set",
			new SkillRequirement(Skill.RUNECRAFT, 88, true, "88 Runecraft"),
			new ItemRequirements("55 with Raiments of the Eye set",
				new ItemRequirement("Hat", ItemCollections.EYE_HAT),
				new ItemRequirement("Top", ItemCollections.EYE_TOP),
				new ItemRequirement("Bottom", ItemCollections.EYE_BOTTOM),
				new ItemRequirement("Boot", ItemID.BOOTS_OF_THE_EYE))
		));
		req.add(new SkillRequirement(Skill.WOODCUTTING, 75, true));

		req.add(wanted);

		return req;
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
			new ItemReward("Falador Shield (4)", ItemID.FALADOR_SHIELD_ELITE, 1),
			new ItemReward("50,000 Exp. Lamp (Any skill over 70)", ItemID.THOSF_REWARD_LAMP, 1));
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

		PanelDetails magicRootsSteps = new PanelDetails("Root of all magic", Arrays.asList(growMagicTree, chopMagicTree,
			digUpStumpForRoots), new SkillRequirement(Skill.FARMING, 91, true),
			new SkillRequirement(Skill.WOODCUTTING, 75, true), axe, spade);
		magicRootsSteps.setDisplayCondition(notGotMagicRoots);
		magicRootsSteps.setLockingStep(gotMagicRootsTask);
		allSteps.add(magicRootsSteps);

		PanelDetails airRunesSteps = new PanelDetails("One with the wind..", Arrays.asList(enterAirAltar,
			craftAirRunes), new SkillRequirement(Skill.RUNECRAFT, 88, true), airTiara, pureEss28);
		airRunesSteps.setDisplayCondition(notCraftedAirRunes);
		airRunesSteps.setLockingStep(craftedAirRunesTask);
		allSteps.add(airRunesSteps);

		PanelDetails capeEmoteSteps = new PanelDetails("Peak Efficiency", Arrays.asList(goUpFaladorCastle1Emote,
			goUpFaladorCastle2Emote, goUpFaladorCastle3Emote, performEmote), skillCape);
		capeEmoteSteps.setDisplayCondition(notPerformedSkillCapeEmote);
		capeEmoteSteps.setLockingStep(performedSkillCapeEmoteTask);
		allSteps.add(capeEmoteSteps);

		PanelDetails strangeFloorSteps = new PanelDetails("The River Styx", Arrays.asList(goToTavDungeon,
			crossStrangeFloor), new SkillRequirement(Skill.AGILITY, 80, true));
		strangeFloorSteps.setDisplayCondition(notJumpedOverStrangeFloor);
		strangeFloorSteps.setLockingStep(jumpedOverStrangeFloorTask);
		allSteps.add(strangeFloorSteps);

		PanelDetails saraBrewSteps = new PanelDetails("Pot Head", Arrays.asList(goToEastBank, craftSaraBrew),
			new SkillRequirement(Skill.HERBLORE, 81, true), toadflaxPotionUnf, crushedNest);
		saraBrewSteps.setDisplayCondition(notMadeSaraBrew);
		saraBrewSteps.setLockingStep(madeSaraBrewTask);
		allSteps.add(saraBrewSteps);

		PanelDetails swordSteps = new PanelDetails("*Tips Fedora*", Arrays.asList(goUpFaladorCastle1,
			goUpFaladorCastle2, purchaseWhite2hSword), wanted, coins1920);
		swordSteps.setDisplayCondition(notPurchasedWhite2hSword);
		swordSteps.setLockingStep(purchasedWhite2hSwordTask);
		allSteps.add(swordSteps);

		PanelDetails finishOffSteps = new PanelDetails("Finishing off", Collections.singletonList(claimReward));
		allSteps.add(finishOffSteps);

		return allSteps;
	}
}
