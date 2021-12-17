/*
 * Copyright (c) 2021, Obasill <https://github.com/Obasill>
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
package com.questhelper.achievementdiaries.morytania;

import com.questhelper.ItemCollections;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.player.CombatLevelRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.TileStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.panel.PanelDetails;
import com.questhelper.steps.QuestStep;

@QuestDescriptor(
	quest = QuestHelperQuest.MORYTANIA_EASY
)
public class MorytaniaEasy extends ComplexStateQuestHelper
{
	// Items required
	ItemRequirement combatGear, chisel, snailShell, thinSnail, tannableHide, coins, scarecrow, bonemeal,
		bucketOfSlime, wolfbane, bones, pot, bucket;

	// Items recommended
	ItemRequirement food, earProtection, ectoToken, ghostSpeak;

	// Quests required
	Requirement natureSpirit, ghostsAhoy;

	Requirement notCraftSnelm, notCookSnail, notKillBanshee, notSbottTan, notEnterSwamp, notKillGhoul,
		notPlaceScarecrow, notOfferBonemeal, notKillWerewolf, notRestorePrayer, notMazchnaTask;

	QuestStep claimReward, craftSnelm, cookSnail, killBanshee, sbottTan, enterSwamp, killGhoul,
		placeScarecrow, killWerewolf, restorePrayer, mazchnaTask, moveToGrotto, moveToBonemeal,
		makeBonemeal, getSlime;

	ObjectStep offerBonemeal, moveToSlime;

	Zone grotto, bonezone, slimezone;

	ZoneRequirement inGrotto, inBonezone, inSlimezone;

	@Override
	public QuestStep loadStep()
	{
		loadZones();
		setupRequirements();
		setupSteps();

		ConditionalStep doEasy = new ConditionalStep(this, claimReward);
		doEasy.addStep(notKillGhoul, killGhoul);
		doEasy.addStep(notEnterSwamp, enterSwamp);
		doEasy.addStep(notCraftSnelm, craftSnelm);
		doEasy.addStep(new Conditions(notRestorePrayer, inGrotto), restorePrayer);
		doEasy.addStep(notRestorePrayer, moveToGrotto);
		doEasy.addStep(notKillBanshee, killBanshee);
		doEasy.addStep(notSbottTan, sbottTan);
		doEasy.addStep(notKillWerewolf, killWerewolf);
		doEasy.addStep(notMazchnaTask, mazchnaTask);
		doEasy.addStep(notPlaceScarecrow, placeScarecrow);
		doEasy.addStep(new Conditions(notOfferBonemeal, bonemeal, bucketOfSlime), offerBonemeal);
		doEasy.addStep(new Conditions(notOfferBonemeal, inSlimezone), getSlime);
		doEasy.addStep(notOfferBonemeal, moveToSlime);
		doEasy.addStep(new Conditions(notOfferBonemeal, inBonezone), makeBonemeal);
		doEasy.addStep(notOfferBonemeal, moveToBonemeal);
		doEasy.addStep(notCookSnail, cookSnail);

		return doEasy;
	}

	public void setupRequirements()
	{
		notCraftSnelm = new VarplayerRequirement(1180, false, 1);
		notCookSnail = new VarplayerRequirement(1180, false, 2);
		notMazchnaTask = new VarplayerRequirement(1180, false, 3);
		notKillBanshee = new VarplayerRequirement(1180, false, 4);
		notSbottTan = new VarplayerRequirement(1180, false, 5);
		notEnterSwamp = new VarplayerRequirement(1180, false, 6);
		notKillGhoul = new VarplayerRequirement(1180, false, 7);
		notPlaceScarecrow = new VarplayerRequirement(1180, false, 8);
		notOfferBonemeal = new VarplayerRequirement(1180, false, 9);
		notKillWerewolf = new VarplayerRequirement(1180, false, 10);
		notRestorePrayer = new VarplayerRequirement(1180, false, 11);

		chisel = new ItemRequirement("Chisel", ItemID.CHISEL).showConditioned(notCraftSnelm);
		snailShell = new ItemRequirement("Snail shell", ItemCollections.getSnailShells()).showConditioned(notCraftSnelm);
		thinSnail = new ItemRequirement("Thin snail", ItemID.THIN_SNAIL).showConditioned(notCookSnail);
		tannableHide = new ItemRequirement("Tannable hide", ItemCollections.getTannableHide()).showConditioned(notSbottTan);
		coins = new ItemRequirement("Coins", ItemCollections.getCoins()).showConditioned(notSbottTan);
		scarecrow = new ItemRequirement("Scarecrow", ItemID.SCARECROW).showConditioned(notPlaceScarecrow);
		scarecrow.setTooltip("Created by combining a bronze spear, watermelon, and hay sack (empty sack filled at a " +
			"hay bale, nearest to Morytania is North-West of Lumbridge)");
		bonemeal = new ItemRequirement("Bonemeal", ItemCollections.getBonemeal()).showConditioned(notOfferBonemeal);
		bucketOfSlime = new ItemRequirement("Bucket of slime", ItemID.BUCKET_OF_SLIME).showConditioned(notOfferBonemeal);
		wolfbane = new ItemRequirement("Wolfbane dagger", ItemID.WOLFBANE).showConditioned(notKillWerewolf);
		bones = new ItemRequirement("Bones", ItemCollections.getBones()).showConditioned(notOfferBonemeal);
		pot = new ItemRequirement("Pot", ItemID.POT).showConditioned(notOfferBonemeal);
		bucket = new ItemRequirement("Bucket", ItemID.BUCKET).showConditioned(notOfferBonemeal);

		combatGear = new ItemRequirement("Combat gear", -1, -1);
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

		food = new ItemRequirement("Food", ItemCollections.getGoodEatingFood(), -1);
		earProtection = new ItemRequirement("Ear protection", ItemCollections.getEarProtection()).showConditioned(notKillBanshee);
		ectoToken = new ItemRequirement("Ecto-Token", ItemID.ECTOTOKEN).showConditioned(notCookSnail);
		ghostSpeak = new ItemRequirement("Ghostspeak amulet", ItemID.GHOSTSPEAK_AMULET).showConditioned(notCookSnail);

		inGrotto = new ZoneRequirement(grotto);
		inBonezone = new ZoneRequirement(bonezone);
		inSlimezone = new ZoneRequirement(slimezone);

		natureSpirit = new QuestRequirement(QuestHelperQuest.NATURE_SPIRIT, QuestState.FINISHED);
		ghostsAhoy = new QuestRequirement(QuestHelperQuest.GHOSTS_AHOY, QuestState.IN_PROGRESS);
	}

	public void loadZones()
	{
		grotto = new Zone(new WorldPoint(2821, 9545, 0), new WorldPoint(2879, 9663, 0));
		bonezone = new Zone(new WorldPoint(3650, 3528, 1), new WorldPoint(3669, 3511, 1));
		slimezone = new Zone(new WorldPoint(3666, 9905, 0), new WorldPoint(3695, 9868, 3));
	}

	public void setupSteps()
	{
		killGhoul = new NpcStep(this, NpcID.GHOUL, new WorldPoint(3434, 3461, 0),
			"Kill a ghoul in Morytania.", combatGear);
		enterSwamp = new ObjectStep(this, ObjectID.GATE_3507, new WorldPoint(3443, 3458, 0),
			"Enter the Mort Myre Swamp.");
		craftSnelm = new ItemStep(this, "Craft a snelm in Morytania. Note: Do not be in the swamp when completing " +
			"this task", chisel.highlighted(), snailShell.highlighted());

		moveToGrotto = new ObjectStep(this, ObjectID.GROTTO, new WorldPoint(3440, 3338, 0),
			"Enter the grotto tree in Mort Myre Swamp.");
		restorePrayer = new ObjectStep(this, ObjectID.ALTAR_OF_NATURE, new WorldPoint(3442, 9741, 1),
			"Pray at the altar.");

		killBanshee = new NpcStep(this, NpcID.BANSHEE, new WorldPoint(3436, 3550, 0),
			"Kill a banshee.", earProtection.equipped(), combatGear);

		killWerewolf = new NpcStep(this, NpcID.ZOJA, new WorldPoint(3501, 3488, 0),
			"Kill any attackable NPC in Canifis with the wolfbane dagger.", wolfbane.equipped());
		mazchnaTask = new NpcStep(this, NpcID.MAZCHNA, new WorldPoint(3513, 3510, 0),
			"Get a slayer task from Mazchna.");
		sbottTan = new NpcStep(this, NpcID.SBOTT, new WorldPoint(3490, 3501, 0),
			"Tan a hide using Sbott's services.", tannableHide, coins.quantity(45));

		placeScarecrow = new ObjectStep(this, 7850, new WorldPoint(3602, 3526, 0),
			"Place a scarecrow at the Morytania flower patch, West of Port Phasmatys.", scarecrow.highlighted());
		placeScarecrow.addIcon(ItemID.SCARECROW);

		moveToBonemeal = new ObjectStep(this, ObjectID.STAIRCASE_16646, new WorldPoint(3667, 3520, 0),
			"Head upstairs above the ectofuntus to grind some bones into bonemeal.", bones, pot);
		moveToSlime = new ObjectStep(this, ObjectID.TRAPDOOR_16114, new WorldPoint(3653, 3519, 0),
			"Head downstairs at the ectofuntus to gather some slime.", bucket);
		moveToSlime.addAlternateObjects(ObjectID.TRAPDOOR_16113);
		getSlime = new TileStep(this, new WorldPoint(3682, 9888, 0),
			"Keep heading down and use your bucket on the slime. Afterwards head back up.", bucket);
		getSlime.addIcon(ItemID.BUCKET);
		makeBonemeal = new ObjectStep(this, ObjectID.LOADER, new WorldPoint(3660, 3526, 1),
			"Use your bones on the loader and grind them to make bonemeal. Afterwards head back down.", bones, pot);
		offerBonemeal = new ObjectStep(this, ObjectID.ECTOFUNTUS, new WorldPoint(3660, 3520, 0),
			"Worship the ectofuntus.", bonemeal, bucketOfSlime);
		offerBonemeal.addAlternateObjects(ObjectID.ECTOFUNTUS_16649);

		cookSnail = new ObjectStep(this, ObjectID.COOKING_RANGE_16641, new WorldPoint(3676, 3468, 0),
			"Cook a thin snail in Port Phasmatys.", thinSnail);

		claimReward = new NpcStep(this, NpcID.LESABR, new WorldPoint(3464, 3480, 0),
			"Talk to Le-Sabre near Canifis to claim your reward!");
		claimReward.addDialogStep("I have a question about my Achievement Diary.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(combatGear, chisel, snailShell, thinSnail, tannableHide, coins.quantity(45), scarecrow,
			bonemeal, bucketOfSlime, wolfbane, bones, pot, bucket, earProtection);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(food, ghostSpeak, ectoToken.quantity(2));
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		List<Requirement> reqs = new ArrayList<>();
		reqs.add(new CombatLevelRequirement(20));
		reqs.add(new SkillRequirement(Skill.COOKING, 12));
		reqs.add(new SkillRequirement(Skill.CRAFTING, 15));
		reqs.add(new SkillRequirement(Skill.FARMING, 23));
		reqs.add(new SkillRequirement(Skill.SLAYER, 15));

		reqs.add(ghostsAhoy);
		reqs.add(natureSpirit);

		return reqs;
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Arrays.asList("Ghoul (lvl 42)", "Banshee (lvl 23)", "Werewolf in human form (lvl 24)");
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		PanelDetails killGhoulSteps = new PanelDetails("Kill Ghoul", Collections.singletonList(killGhoul),
			combatGear, food);
		killGhoulSteps.setDisplayCondition(notKillGhoul);
		allSteps.add(killGhoulSteps);

		PanelDetails enterSwampSteps = new PanelDetails("Enter Mort Myre Swamp", Collections.singletonList(enterSwamp));
		enterSwampSteps.setDisplayCondition(notEnterSwamp);
		allSteps.add(enterSwampSteps);

		PanelDetails craftSnelmSteps = new PanelDetails("Craft Snelm", Collections.singletonList(craftSnelm),
			new SkillRequirement(Skill.CRAFTING, 15), snailShell, chisel);
		craftSnelmSteps.setDisplayCondition(notCraftSnelm);
		allSteps.add(craftSnelmSteps);

		PanelDetails restorePrayerSteps = new PanelDetails("Restore Prayer", Arrays.asList(moveToGrotto,
			restorePrayer), natureSpirit);
		restorePrayerSteps.setDisplayCondition(notRestorePrayer);
		allSteps.add(restorePrayerSteps);

		PanelDetails killBansheeSteps = new PanelDetails("Kill Banshee", Collections.singletonList(killBanshee),
			new SkillRequirement(Skill.SLAYER, 15), combatGear, food, earProtection);
		killBansheeSteps.setDisplayCondition(notKillBanshee);
		allSteps.add(killBansheeSteps);

		PanelDetails sbottTanningHideSteps = new PanelDetails("Sbott Tanning Hide",
			Collections.singletonList(sbottTan), tannableHide, coins.quantity(45));
		sbottTanningHideSteps.setDisplayCondition(notSbottTan);
		allSteps.add(sbottTanningHideSteps);

		PanelDetails werewolfSteps = new PanelDetails("Kill Werewolf in Human Form",
			Collections.singletonList(killWerewolf), wolfbane, combatGear, food);
		werewolfSteps.setDisplayCondition(notKillWerewolf);
		allSteps.add(werewolfSteps);

		PanelDetails mazchnaSteps = new PanelDetails("Mazchna Slayer Task", Collections.singletonList(mazchnaTask),
			new CombatLevelRequirement(20));
		mazchnaSteps.setDisplayCondition(notMazchnaTask);
		allSteps.add(mazchnaSteps);

		PanelDetails placeScarecrowSteps = new PanelDetails("Place Scarecrow",
			Collections.singletonList(placeScarecrow), new SkillRequirement(Skill.FARMING, 23), scarecrow);
		placeScarecrowSteps.setDisplayCondition(notPlaceScarecrow);
		allSteps.add(placeScarecrowSteps);

		PanelDetails offerBonemealSteps = new PanelDetails("Offer Bonemeal", Collections.singletonList(offerBonemeal),
			bones, pot, bucket);
		offerBonemealSteps.setDisplayCondition(notOfferBonemeal);
		allSteps.add(offerBonemealSteps);

		PanelDetails cookSnailSteps = new PanelDetails("Cook Thin Snail", Collections.singletonList(cookSnail),
			new SkillRequirement(Skill.COOKING, 12), thinSnail);
		cookSnailSteps.setDisplayCondition(notCookSnail);
		allSteps.add(cookSnailSteps);

		allSteps.add(new PanelDetails("Finishing off", Collections.singletonList(claimReward)));

		return allSteps;
	}
}