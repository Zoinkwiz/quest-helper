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
package com.questhelper.achievementdiaries.desert;

import com.questhelper.ItemCollections;
import com.questhelper.QuestHelperQuest;
import com.questhelper.QuestVarPlayer;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.player.CombatLevelRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.player.SpellbookRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.util.Spellbook;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.panel.PanelDetails;
import com.questhelper.steps.QuestStep;

@QuestDescriptor(
	quest = QuestHelperQuest.DESERT_HARD
)
public class DesertHard extends ComplexStateQuestHelper
{
	// Items required
	ItemRequirement combatGear, blackjack, pickaxe, fireRune, waterRune, astralRune, emptyWaterskin, slayerHelm, keris,
		lightsource, yewLog, tinderbox, mithBar, hammer;

	// Items recommended
	ItemRequirement food, waterskin, desertBoots, desertRobe, desertShirt;

	// Quests required
	Requirement theFued, dreamMentor, desertTreasure, contact, lunarBook;

	Requirement notMenaThug, notGranite, notRefillWaterskin, notKalphQueen, notPollRooftop, notKillDust,
		notAncientMagicks, notKillLocustRider, notBurnYew, notMithPlatebody;

	QuestStep claimReward, menaThug, granite, refillWaterskin, kalphQueen, pollRooftop, killDust,
		ancientMagicks, killLocustRider, burnYew, mithPlatebody, moveToKalph, moveToSmoke, moveToPyramid, moveToMayor,
		moveToSoph;

	Zone kalph, smoke, soph, pyramid, mayor;

	ZoneRequirement inKalph, inSmoke, inPyramid, inSoph, inMayor;

	@Override
	public QuestStep loadStep()
	{
		loadZones();
		setupRequirements();
		setupSteps();

		ConditionalStep doHard = new ConditionalStep(this, claimReward);
		doHard.addStep(new Conditions(notKalphQueen, inKalph), kalphQueen);
		doHard.addStep(notKalphQueen, moveToKalph);
		doHard.addStep(notPollRooftop, pollRooftop);
		doHard.addStep(notMenaThug, menaThug);
		doHard.addStep(notRefillWaterskin, refillWaterskin);
		doHard.addStep(new Conditions(notKillDust, inSmoke), killDust);
		doHard.addStep(notKillDust, moveToSmoke);
		doHard.addStep(new Conditions(notAncientMagicks, inPyramid), ancientMagicks);
		doHard.addStep(notAncientMagicks, moveToPyramid);
		doHard.addStep(notGranite, granite);
		doHard.addStep(new Conditions(notBurnYew, inMayor), burnYew);
		doHard.addStep(notBurnYew, moveToMayor);
		doHard.addStep(notMithPlatebody, mithPlatebody);
		doHard.addStep(new Conditions(notKillLocustRider, inSoph), killLocustRider);
		doHard.addStep(notKillLocustRider, moveToSoph);

		// 9, 10, 8

		return doHard;
	}

	public void setupRequirements()
	{
		notMenaThug = new VarplayerRequirement(1198, false, 24);
		notGranite = new VarplayerRequirement(1198, false, 25);
		notRefillWaterskin = new VarplayerRequirement(1198, false, 26);
		notKalphQueen = new VarplayerRequirement(1198, false, 27);
		notPollRooftop = new VarplayerRequirement(1198, false, 28);
		notKillDust = new VarplayerRequirement(1198, false, 29);
		notAncientMagicks = new VarplayerRequirement(1198, false, 30);
		notKillLocustRider = new VarplayerRequirement(1198, false, 31);
		notBurnYew = new VarplayerRequirement(1199, false, 0);
		notMithPlatebody = new VarplayerRequirement(1199, false, 1);

		lunarBook = new SpellbookRequirement(Spellbook.LUNAR);

		blackjack = new ItemRequirement("Blackjack", ItemCollections.getBlackjacks()).showConditioned(notMenaThug);
		pickaxe = new ItemRequirement("Any pickaxe", ItemCollections.getPickaxes()).showConditioned(notGranite);
		fireRune = new ItemRequirement("Fire rune", ItemID.FIRE_RUNE).showConditioned(notRefillWaterskin);
		waterRune = new ItemRequirement("Water rune", ItemID.WATER_RUNE).showConditioned(notRefillWaterskin);
		astralRune = new ItemRequirement("Astral rune", ItemID.ASTRAL_RUNE).showConditioned(notRefillWaterskin);
		emptyWaterskin = new ItemRequirement("Empty waterskin", ItemID.WATERSKIN0).showConditioned(notRefillWaterskin);
		slayerHelm = new ItemRequirement("Slayer Helmet", ItemCollections.getSlayerHelmets())
			.showConditioned(notKillDust);
		keris = new ItemRequirement("Keris", ItemCollections.getKeris()).showConditioned(notKillLocustRider);
		lightsource = new ItemRequirement("Light soruce", ItemCollections.getLightSources())
			.showConditioned(notKillLocustRider);
		yewLog = new ItemRequirement("Yew log", ItemID.YEW_LOGS).showConditioned(notBurnYew);
		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX).showConditioned(notBurnYew);
		mithBar = new ItemRequirement("Mithril bar", ItemID.MITHRIL_BAR).showConditioned(notMithPlatebody);
		hammer = new ItemRequirement("Hammer", ItemID.HAMMER).showConditioned(notMithPlatebody);

		desertBoots = new ItemRequirement("Desert boots", ItemID.DESERT_BOOTS);
		desertRobe = new ItemRequirement("Desert robe", ItemID.DESERT_ROBE);
		desertShirt = new ItemRequirement("Desert shirt", ItemID.DESERT_SHIRT);
		waterskin = new ItemRequirement("Waterskin", ItemCollections.getWaterskin());

		combatGear = new ItemRequirement("Combat gear", -1, -1);
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

		food = new ItemRequirement("Food", ItemCollections.getGoodEatingFood(), -1);

		inKalph = new ZoneRequirement(kalph);
		inSmoke = new ZoneRequirement(smoke);
		inPyramid = new ZoneRequirement(pyramid);
		inSoph = new ZoneRequirement(soph);
		inMayor = new ZoneRequirement(mayor);

		theFued = new QuestRequirement(QuestHelperQuest.THE_FEUD, QuestState.FINISHED);
		dreamMentor = new QuestRequirement(QuestHelperQuest.DREAM_MENTOR, QuestState.FINISHED);
		desertTreasure = new QuestRequirement(QuestHelperQuest.DESERT_TREASURE, QuestState.FINISHED);
		contact = new QuestRequirement(QuestHelperQuest.CONTACT, QuestState.FINISHED);
	}

	public void loadZones()
	{
		kalph = new Zone(new WorldPoint(2821, 9545, 0), new WorldPoint(2879, 9663, 0));
		smoke  = new Zone(new WorldPoint(2821, 9545, 0), new WorldPoint(2879, 9663, 0));
		soph = new Zone(new WorldPoint(2821, 9545, 0), new WorldPoint(2879, 9663, 0));
		pyramid = new Zone(new WorldPoint(2821, 9545, 0), new WorldPoint(2879, 9663, 0));
		mayor = new Zone(new WorldPoint(2821, 9545, 0), new WorldPoint(2879, 9663, 0));
	}

	public void setupSteps()
	{

		claimReward = new NpcStep(this, NpcID.JARR, new WorldPoint(3303, 3124, 0),
			"Talk to Jarr at the Shantay pass to claim your reward!");
		claimReward.addDialogStep("I have a question about my Achievement Diary.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(combatGear, blackjack, pickaxe, fireRune.quantity(1), waterRune.quantity(3),
			astralRune.quantity(1), emptyWaterskin, slayerHelm, keris, lightsource, yewLog, tinderbox,
			mithBar.quantity(5), hammer);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(food, waterskin, desertBoots, desertRobe, desertShirt);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		List<Requirement> reqs = new ArrayList<>();
		reqs.add(new SkillRequirement(Skill.AGILITY, 70));
		reqs.add(new SkillRequirement(Skill.ATTACK, 50));
		reqs.add(new SkillRequirement(Skill.CRAFTING, 55));
		reqs.add(new SkillRequirement(Skill.DEFENCE, 10));
		reqs.add(new SkillRequirement(Skill.FIREMAKING, 60));
		reqs.add(new SkillRequirement(Skill.MAGIC, 68));
		reqs.add(new SkillRequirement(Skill.MINING, 45));
		reqs.add(new SkillRequirement(Skill.SMITHING, 68));
		reqs.add(new SkillRequirement(Skill.SLAYER, 65));
		reqs.add(new SkillRequirement(Skill.THIEVING, 65));

		reqs.add(contact);
		reqs.add(dreamMentor);
		reqs.add(desertTreasure);
		reqs.add(theFued);

		return reqs;
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Collections.singletonList("Kill a Dust devil (lvl 93), Locust rider (lvl 98), Kalphite Queen, (lvl 333)");
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		// 4, 5, 1, 3, 6, 7, 2, 9, 10, 8

		PanelDetails kalphiteQueenSteps = new PanelDetails("Kalphite Queen", Arrays.asList(moveToKalph, kalphQueen),
			combatGear, food);
		kalphiteQueenSteps.setDisplayCondition(notKalphQueen);
		allSteps.add(kalphiteQueenSteps);

		PanelDetails pollRooftopSteps = new PanelDetails("Pollnivneach Rooftops",
			Collections.singletonList(pollRooftop), new SkillRequirement(Skill.AGILITY, 70));
		pollRooftopSteps.setDisplayCondition(notPollRooftop);
		allSteps.add(pollRooftopSteps);

		PanelDetails menaphiteThugSteps = new PanelDetails("Menaphite Thug", Collections.singletonList(menaThug),
			new SkillRequirement(Skill.THIEVING, 65), theFued, blackjack);
		menaphiteThugSteps.setDisplayCondition(notMenaThug);
		allSteps.add(menaphiteThugSteps);

		PanelDetails refillWaterskinsSteps = new PanelDetails("Refill Waterskins",
			Collections.singletonList(refillWaterskin), new SkillRequirement(Skill.MAGIC, 68), dreamMentor,
			lunarBook, fireRune.quantity(1), waterRune.quantity(3), astralRune.quantity(1), emptyWaterskin);
		refillWaterskinsSteps.setDisplayCondition(notRefillWaterskin);
		allSteps.add(refillWaterskinsSteps);

		PanelDetails dustDevilSteps = new PanelDetails("Dust Devil", Arrays.asList(moveToSmoke, killDust),
			new SkillRequirement(Skill.CRAFTING, 55), new SkillRequirement(Skill.DEFENCE, 10),
			new SkillRequirement(Skill.SLAYER, 65), desertTreasure, combatGear, food, slayerHelm);
		dustDevilSteps.setDisplayCondition(notKillDust);
		allSteps.add(dustDevilSteps);

		PanelDetails ancientMagicksSteps = new PanelDetails("Activate Ancient Magicks", Arrays.asList(moveToPyramid,
			ancientMagicks), desertTreasure);
		ancientMagicksSteps.setDisplayCondition(notAncientMagicks);
		allSteps.add(ancientMagicksSteps);

		PanelDetails mineGraniteSteps = new PanelDetails("Mine Granite", Collections.singletonList(granite),
			new SkillRequirement(Skill.MINING, 45), pickaxe);
		mineGraniteSteps.setDisplayCondition(notGranite);
		allSteps.add(mineGraniteSteps);

		PanelDetails burnYewSteps = new PanelDetails("Burn Yew on Nardah Mayor's Balcony", Arrays.asList(moveToMayor,
			burnYew), new SkillRequirement(Skill.FIREMAKING, 60), yewLog, tinderbox);
		burnYewSteps.setDisplayCondition(notBurnYew);
		allSteps.add(burnYewSteps);

		PanelDetails mithrilPlatebodySteps = new PanelDetails("Mithril Platebody",
			Collections.singletonList(mithPlatebody), new SkillRequirement(Skill.SMITHING, 68), mithBar.quantity(5),
			hammer);
		mithrilPlatebodySteps.setDisplayCondition(notMithPlatebody);
		allSteps.add(mithrilPlatebodySteps);

		PanelDetails locusRiderSteps = new PanelDetails("Kill Locus Rider with Keris", Arrays.asList(moveToSoph,
			killLocustRider), new SkillRequirement(Skill.ATTACK, 50), contact, keris, combatGear, lightsource, food);
		locusRiderSteps.setDisplayCondition(notKillLocustRider);
		allSteps.add(locusRiderSteps);



		allSteps.add(new PanelDetails("Finishing off", Collections.singletonList(claimReward)));

		return allSteps;
	}
}
