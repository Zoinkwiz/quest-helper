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
package com.questhelper.helpers.achievementdiaries.kandarin;

import com.questhelper.ItemCollections;
import com.questhelper.KeyringCollection;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.requirements.ChatMessageRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.KeyringRequirement;
import com.questhelper.requirements.player.PrayerRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.panel.PanelDetails;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.KANDARIN_HARD
)

public class KandarinHard extends ComplexStateQuestHelper
{
	// Items required
	ItemRequirement barbRod, feather, axe, bowString, knife, cosmicRune, waterRune, unpoweredOrb, dustyKey,
		mapleLogs, bow, ringOfVis, coins, addyBar, hammer, yewLogs, combatGear, antidragonfire;

	// Items recommended
	ItemRequirement food;

	// unlisted item reqs
	ItemRequirement unstrungYewLong;

	Requirement piety, choppedLogs;

	Requirement notCatchStur, notSeersRooftop, notYewLong, notPietyCourt, notWaterOrb, notBurnMaple,
		notShadowHound, notMithrilDrag, notBuyGranite, notFancyStone, notAddySpear;

	//Quest steps
	Requirement barbFishing, barbFiremaking, barbSmithing, taiBwoWannai, knightWaves, desertTreasure;

	QuestStep claimReward, moveToTavDungeon, moveToOb, waterOrb, seersRooftop, yewLong, cutLongbow, stringBow,
		pietyCourt, burnMaple, moveToSeers, fancyStone, moveToShadow, shadowHound, catchStur, addySpear, moveToWhirl, moveToAncient2, moveToAncient3, mithrilDrag, buyGranite;

	Zone tavDungeon, obIsland, seers, shadow, ancient1, ancient2, ancient3;

	ZoneRequirement inTavDungeon, inObIsland, inSeers, inShadow, inAncient1, inAncient2, inAncient3;

	ConditionalStep catchSturTask, seersRooftopTask, yewLongTask, pietyCourtTask, waterOrbTask, burnMapleTask, shadowHoundTask,
		mithrilDragTask, buyGraniteTask, fancyStoneTask, addySpearTask;

	@Override
	public QuestStep loadStep()
	{
		loadZones();
		setupRequirements();
		setupSteps();

		ConditionalStep doHard = new ConditionalStep(this, claimReward);

		waterOrbTask = new ConditionalStep(this, moveToTavDungeon);
		waterOrbTask.addStep(inTavDungeon, moveToOb);
		waterOrbTask.addStep(inObIsland, waterOrb);
		doHard.addStep(notWaterOrb, waterOrbTask);

		seersRooftopTask = new ConditionalStep(this, seersRooftop);
		doHard.addStep(notSeersRooftop, seersRooftopTask);

		yewLongTask = new ConditionalStep(this, yewLong);
		yewLongTask.addStep(unstrungYewLong, stringBow);
		yewLongTask.addStep(new Conditions(yewLogs, choppedLogs), cutLongbow);
		doHard.addStep(notYewLong, yewLongTask);

		pietyCourtTask = new ConditionalStep(this, pietyCourt);
		doHard.addStep(notPietyCourt, pietyCourtTask);

		burnMapleTask = new ConditionalStep(this, moveToSeers);
		burnMapleTask.addStep(inSeers, burnMaple);
		doHard.addStep(notBurnMaple, burnMapleTask);

		fancyStoneTask = new ConditionalStep(this, fancyStone);
		doHard.addStep(notFancyStone, fancyStoneTask);

		shadowHoundTask = new ConditionalStep(this, moveToShadow);
		shadowHoundTask.addStep(inShadow, shadowHound);
		doHard.addStep(notShadowHound, shadowHoundTask);

		catchSturTask = new ConditionalStep(this, catchStur);
		doHard.addStep(notCatchStur, catchSturTask);

		addySpearTask = new ConditionalStep(this, addySpear);
		doHard.addStep(notAddySpear, addySpearTask);

		mithrilDragTask = new ConditionalStep(this, moveToWhirl);
		mithrilDragTask.addStep(inAncient1, moveToAncient2);
		mithrilDragTask.addStep(inAncient2, moveToAncient3);
		mithrilDragTask.addStep(inAncient3, mithrilDrag);
		doHard.addStep(notMithrilDrag, mithrilDragTask);

		buyGraniteTask = new ConditionalStep(this, buyGranite);
		doHard.addStep(notBuyGranite, buyGraniteTask);

		return doHard;
	}

	@Override
	public void setupRequirements()
	{
		notCatchStur = new VarplayerRequirement(1178, false, 26);
		notSeersRooftop = new VarplayerRequirement(1178, false, 27);
		notYewLong = new VarplayerRequirement(1178, false, 28);
		notPietyCourt = new VarplayerRequirement(1178, false, 29);
		notWaterOrb = new VarplayerRequirement(1178, false, 30);
		notBurnMaple = new VarplayerRequirement(1178, false, 31);
		notShadowHound = new VarplayerRequirement(1179, false, 0);
		notMithrilDrag = new VarplayerRequirement(1179, false, 1);
		notBuyGranite = new VarplayerRequirement(1179, false, 2);
		notFancyStone = new VarplayerRequirement(1179, false, 3);
		notAddySpear = new VarplayerRequirement(1179, false, 4);

		piety = new PrayerRequirement("Piety", Prayer.PIETY);

		barbRod = new ItemRequirement("Barbarian fishing rod", ItemID.BARBARIAN_ROD).showConditioned(notCatchStur).isNotConsumed();
		feather = new ItemRequirement("Feathers", ItemID.FEATHER).showConditioned(notCatchStur);
		axe = new ItemRequirement("Any axe", ItemCollections.AXES).showConditioned(notYewLong).isNotConsumed();
		bowString = new ItemRequirement("Bow string", ItemID.BOW_STRING).showConditioned(notYewLong);
		knife = new ItemRequirement("Knife", ItemID.KNIFE).showConditioned(notYewLong).isNotConsumed();
		waterRune = new ItemRequirement("Water rune", ItemID.WATER_RUNE).showConditioned(notWaterOrb);
		cosmicRune = new ItemRequirement("Cosmic rune", ItemID.COSMIC_RUNE).showConditioned(notWaterOrb);
		unpoweredOrb = new ItemRequirement("Unpowered orb", ItemID.UNPOWERED_ORB).showConditioned(notWaterOrb);

		Conditions not70Agility = new Conditions(LogicType.NOR, new SkillRequirement(Skill.AGILITY, 70, true));

		dustyKey = new KeyringRequirement("Dusty Key", configManager, KeyringCollection.DUSTY_KEY).showConditioned(new Conditions(not70Agility,
			notWaterOrb)).isNotConsumed();
		dustyKey.setTooltip("You can get this by killing the Jailor in the Black Knights Base in Taverley Dungeon and" +
			" using the key he drops to enter the jail cell there to talk to Velrak for the dusty key");
		mapleLogs = new ItemRequirement("Maple logs", ItemID.MAPLE_LOGS).showConditioned(notBurnMaple);
		bow = new ItemRequirement("Any bow", ItemCollections.BOWS).showConditioned(notBurnMaple).isNotConsumed();
		ringOfVis = new ItemRequirement("Ring of Visibility", ItemID.RING_OF_VISIBILITY).showConditioned(notShadowHound).isNotConsumed();
		coins = new ItemRequirement("Coins", ItemCollections.COINS)
			.showConditioned(new Conditions(LogicType.OR, notFancyStone, notBuyGranite));
		addyBar = new ItemRequirement("Adamantite bar", ItemID.ADAMANTITE_BAR).showConditioned(notAddySpear);
		hammer = new ItemRequirement("Hammer", ItemID.HAMMER).showConditioned(notAddySpear).isNotConsumed();
		yewLogs = new ItemRequirement("Yew logs", ItemID.YEW_LOGS).showConditioned(notAddySpear);
		unstrungYewLong = new ItemRequirement("Unstrung yew longbow", ItemID.YEW_LONGBOW_U);

		combatGear = new ItemRequirement("Combat gear", -1, -1)
			.showConditioned(new Conditions(LogicType.OR, notShadowHound, notMithrilDrag)).isNotConsumed();
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

		antidragonfire = new ItemRequirement("An antifire shield", ItemCollections.ANTIFIRE_SHIELDS).isNotConsumed();

		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, -1);

		setupGeneralRequirements();

		inTavDungeon = new ZoneRequirement(tavDungeon);
		inObIsland = new ZoneRequirement(obIsland);
		inSeers = new ZoneRequirement(seers);
		inShadow = new ZoneRequirement(shadow);
		inAncient1 = new ZoneRequirement(ancient1);
		inAncient2 = new ZoneRequirement(ancient2);
		inAncient3 = new ZoneRequirement(ancient3);

		choppedLogs = new ChatMessageRequirement(
			inSeers,
			"<col=0040ff>Achievement Diary Stage Task - Current stage: 1.</col>"
		);
		((ChatMessageRequirement) choppedLogs).setInvalidateRequirement(
			new ChatMessageRequirement(
				new Conditions(LogicType.NOR, inSeers),
				"<col=0040ff>Achievement Diary Stage Task - Current stage: 1.</col>"
			)
		);

	}

	public void loadZones()
	{
		tavDungeon = new Zone(new WorldPoint(2813, 9857, 0), new WorldPoint(2972, 9669, 0));
		obIsland = new Zone(new WorldPoint(2833, 3427, 0), new WorldPoint(2849, 3415, 0));
		seers = new Zone(new WorldPoint(2682, 3510, 0), new WorldPoint(2742, 3455, 0));
		shadow = new Zone(new WorldPoint(2621, 5121, 0), new WorldPoint(2754, 5054, 0));
		ancient1 = new Zone(new WorldPoint(1761, 5369, 1), new WorldPoint(1770, 5363, 1));
		ancient2 = new Zone(new WorldPoint(1733, 5373, 0), new WorldPoint(1792, 5315, 0));
		ancient3 = new Zone(new WorldPoint(1734, 5362, 1), new WorldPoint(1792, 5279, 1));
	}

	public void setupSteps()
	{
		moveToTavDungeon = new ObjectStep(this, ObjectID.LADDER_16680, new WorldPoint(2884, 3397, 0),
			"Enter the Taverley Dungeon.", dustyKey, waterRune.quantity(30), cosmicRune.quantity(3), unpoweredOrb);
		moveToOb = new ObjectStep(this, ObjectID.LADDER_17385, new WorldPoint(2842, 9824, 0),
			"Make your way through Taverley Dungeon to the end, and climb the ladder there. If you're 70+ " +
				"Agility, use on of the shortcuts near the entrance to get there quickly.",
			dustyKey, waterRune.quantity(30), cosmicRune.quantity(3), unpoweredOrb);
		waterOrb = new ObjectStep(this, ObjectID.OBELISK_OF_WATER, new WorldPoint(2844, 3422, 0),
			"Use the charge water orb spell on the obelisk.", waterRune.quantity(30), cosmicRune.quantity(3),
			unpoweredOrb);
		waterOrb.addIcon(ItemID.WATER_ORB);
		seersRooftop = new ObjectStep(this, ObjectID.WALL_14927, new WorldPoint(2729, 3489, 0),
			"Complete a lap of the Seers' village Rooftop course.");
		yewLong = new ObjectStep(this, ObjectID.YEW_TREE_10822, new WorldPoint(2715, 3460, 0),
			"Cut some yew logs near Seers' Village. Make sure to use the knife on the ones you cut.", axe);
		cutLongbow = new ItemStep(this, "Use knife on yew logs to make a yew longbow (u)", yewLogs.highlighted(), knife.highlighted());
		stringBow = new ItemStep(this, "String the bow.", bowString.highlighted(), unstrungYewLong.highlighted());
		pietyCourt = new DetailedQuestStep(this, new WorldPoint(2735, 3469, 0),
			"Activate piety then enter the Seers' Village courthouse.", piety);
		moveToSeers = new DetailedQuestStep(this, new WorldPoint(2714, 3484, 0),
			"Go to Seers' Village.", bow, mapleLogs);
		burnMaple = new ItemStep(this, "Burn some maple logs with a bow.", bow.highlighted(), mapleLogs.highlighted());
		fancyStone = new NpcStep(this, NpcID.ESTATE_AGENT, new WorldPoint(2735, 3500, 0),
			"TALK to the estate agent to redecorate your house to Fancy Stone. Must be done through dialog.",
			coins.quantity(25000));
		fancyStone.addDialogStep("Can you redecorate my house please?");
		moveToShadow = new ObjectStep(this, NullObjectID.NULL_6560, new WorldPoint(2547, 3421, 0),
			"Climb down the shadow ladder south of Glarial's Tomb.", ringOfVis.equipped(), combatGear);
		shadowHound = new NpcStep(this, NpcID.SHADOW_HOUND, new WorldPoint(2699, 5095, 0),
			"Kill a shadow hound.", true);
		catchStur = new NpcStep(this, NpcID.FISHING_SPOT_1542, new WorldPoint(2501, 3504, 0),
			"Catch a leaping Sturgeon south of Barbarian Assault.", true, barbRod, feather.quantity(20));
		addySpear = new ObjectStep(this, ObjectID.BARBARIAN_ANVIL, new WorldPoint(2502, 3485, 0),
			"Smith an adamant spear on the barbarian anvil south of Barbarian Assault.", addyBar, yewLogs);
		addySpear.addIcon(ItemID.ADAMANTITE_BAR);
		moveToWhirl = new ObjectStep(this, ObjectID.WHIRLPOOL_25274, new WorldPoint(2512, 3508, 0),
			"Jump in the whirlpool south of Barbarian Assault.", combatGear, antidragonfire.equipped());
		moveToAncient2 = new ObjectStep(this, ObjectID.STAIRS_25338, new WorldPoint(1770, 5366, 1),
			"Go down the stairs.");
		moveToAncient3 = new ObjectStep(this, ObjectID.STAIRS_25339, new WorldPoint(1778, 5345, 0),
			"Go up the stairs in the east of the cavern.");
		mithrilDrag = new NpcStep(this, NpcID.MITHRIL_DRAGON, new WorldPoint(1779, 5344, 1),
			"Kill a mithril dragon.", true, combatGear, antidragonfire.equipped(), food);
		buyGranite = new NpcStep(this, NpcID.COMMANDER_CONNAD, new WorldPoint(2535, 3576, 0),
			"Buy and equip a granite body from Commander Connad. (Requires at least 1 Penance Queen kill)",
			coins.quantity(95000));

		claimReward = new NpcStep(this, NpcID.THE_WEDGE, new WorldPoint(2760, 3476, 0),
			"Talk to the 'Wedge' in front of Camelot Castle to claim your reward!");
		claimReward.addDialogStep("I have a question about my Achievement Diary.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(barbRod, feather, axe, bowString, knife, cosmicRune.quantity(3),
			waterRune.quantity(30), unpoweredOrb, dustyKey, mapleLogs, bow, ringOfVis,
			coins, addyBar, hammer, yewLogs, combatGear);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Collections.singletonList(food);
	}

	private void setupGeneralRequirements()
	{
		// TODO find a way to track barb training / knight waves
		barbFishing = new ItemRequirement("Completed Barbarian fishing", 1, -1);
		barbFiremaking = new ItemRequirement("Unlocked the Ancient Caverns through Barbarian Firemaking", 1, -1);
		barbSmithing = new ItemRequirement("Completed Barbarian Smithing", 1, -1);
		taiBwoWannai = new QuestRequirement(QuestHelperQuest.TAI_BWO_WANNAI_TRIO, QuestState.FINISHED);
		knightWaves = new QuestRequirement(QuestHelperQuest.KNIGHT_WAVES_TRAINING_GROUNDS, QuestState.FINISHED);
		desertTreasure = new QuestRequirement(QuestHelperQuest.DESERT_TREASURE, QuestState.FINISHED);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		setupGeneralRequirements();
		ArrayList<Requirement> req = new ArrayList<>();

		req.add(new SkillRequirement(Skill.AGILITY, 60, true));
		req.add(new SkillRequirement(Skill.CONSTRUCTION, 50));
		req.add(new SkillRequirement(Skill.DEFENCE, 70));
		req.add(new SkillRequirement(Skill.FIREMAKING, 65, true));
		req.add(new SkillRequirement(Skill.FISHING, 70, true));
		req.add(new SkillRequirement(Skill.FLETCHING, 70, true));
		req.add(new SkillRequirement(Skill.PRAYER, 70));
		req.add(new SkillRequirement(Skill.MAGIC, 56));
		req.add(new SkillRequirement(Skill.SMITHING, 75, true));
		req.add(new SkillRequirement(Skill.STRENGTH, 50));
		req.add(new SkillRequirement(Skill.THIEVING, 53));
		req.add(new SkillRequirement(Skill.WOODCUTTING, 60, true));

		req.add(taiBwoWannai);
		req.add(desertTreasure);
		req.add(barbFishing);
		req.add(barbFiremaking);
		req.add(barbSmithing);
		req.add(knightWaves);

		return req;
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Collections.singletonList("Shadow Hound (Level 63), Mithril Dragon (level 304)");
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
			new ItemReward("Kandarin headgear (3)", ItemID.KANDARIN_HEADGEAR_3, 1),
			new ItemReward("15,500 Exp. Lamp (Any skill over 50)", ItemID.ANTIQUE_LAMP, 1));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
			new UnlockReward("Coal trucks can hold up to 308 coal."),
			new UnlockReward("Thormac will enchant battlestaves for 30,000 coins each"),
			new UnlockReward("The Flax keeper will exchange 120 noted flax for 120 noted bow strings daily"),
			new UnlockReward("15% more marks of grace on Seers' Village Rooftop Course"),
			new UnlockReward("10% increased chance to save a harvest life from the Catherby herb patch"),
			new UnlockReward("10% increased reward points from Barbarian Assault"),
			new UnlockReward("10% increased activation chance (multiplicative) for the special effect from enchanted bolts (Including PvP)"),
			new UnlockReward("Ability to toggle Camelot Teleport to outside Seers' Village bank"));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		PanelDetails chargeOrbSteps = new PanelDetails("Charge Water Orb", Arrays.asList(moveToTavDungeon, moveToOb,
			waterOrb), waterRune.quantity(30), cosmicRune.quantity(3), unpoweredOrb, dustyKey);
		chargeOrbSteps.setDisplayCondition(notWaterOrb);
		chargeOrbSteps.setLockingStep(waterOrbTask);
		allSteps.add(chargeOrbSteps);

		PanelDetails agilitySteps = new PanelDetails("Seers' Village Rooftop", Collections.singletonList(seersRooftop),
			new SkillRequirement(Skill.AGILITY, 60, true));
		agilitySteps.setDisplayCondition(notSeersRooftop);
		agilitySteps.setLockingStep(seersRooftopTask);
		allSteps.add(agilitySteps);

		PanelDetails yewLongSteps = new PanelDetails("Yew Longbow from Scratch", Arrays.asList(yewLong, cutLongbow,
			stringBow), new SkillRequirement(Skill.FLETCHING, 70, true),
			new SkillRequirement(Skill.WOODCUTTING, 60, true), axe, bowString, knife);
		yewLongSteps.setDisplayCondition(notYewLong);
		yewLongSteps.setLockingStep(yewLongTask);
		allSteps.add(yewLongSteps);

		PanelDetails pietySteps = new PanelDetails("Piety in the Courthouse", Collections.singletonList(pietyCourt),
			new QuestRequirement(QuestHelperQuest.KINGS_RANSOM, QuestState.FINISHED),
			knightWaves, new SkillRequirement(Skill.PRAYER, 70), new SkillRequirement(Skill.DEFENCE, 70));
		pietySteps.setDisplayCondition(notPietyCourt);
		pietySteps.setLockingStep(pietyCourtTask);
		allSteps.add(pietySteps);

		PanelDetails burnMapleSteps = new PanelDetails("Burn Maple logs with a bow", Arrays.asList(moveToSeers, burnMaple),
			new SkillRequirement(Skill.FIREMAKING, 65, true), barbFiremaking, mapleLogs, bow);
		burnMapleSteps.setDisplayCondition(notBurnMaple);
		burnMapleSteps.setLockingStep(burnMapleTask);
		allSteps.add(burnMapleSteps);

		PanelDetails fancyStoneSteps = new PanelDetails("Fancy Stone Decoration", Collections.singletonList(fancyStone),
			new SkillRequirement(Skill.CONSTRUCTION, 50), coins.quantity(25000));
		fancyStoneSteps.setDisplayCondition(notFancyStone);
		fancyStoneSteps.setLockingStep(fancyStoneTask);
		allSteps.add(fancyStoneSteps);

		PanelDetails killHoundSteps = new PanelDetails("Kill a Shadow Hound", Arrays.asList(moveToShadow, shadowHound),
			desertTreasure, ringOfVis, combatGear, food);
		killHoundSteps.setDisplayCondition(notShadowHound);
		killHoundSteps.setLockingStep(shadowHoundTask);
		allSteps.add(killHoundSteps);

		PanelDetails fishSturgeonSteps = new PanelDetails("Fish a Leaping Sturgeon", Collections.singletonList(catchStur),
			new SkillRequirement(Skill.FISHING, 70, true), new SkillRequirement(Skill.AGILITY, 45),
			new SkillRequirement(Skill.STRENGTH, 45), barbFishing, barbRod, feather.quantity(20));
		fishSturgeonSteps.setDisplayCondition(notCatchStur);
		fishSturgeonSteps.setLockingStep(catchSturTask);
		allSteps.add(fishSturgeonSteps);

		PanelDetails smithSpearSteps = new PanelDetails("Smith an Adamant Spear", Collections.singletonList(addySpear),
			new SkillRequirement(Skill.SMITHING, 75, true), barbSmithing, taiBwoWannai, yewLogs, addyBar, hammer);
		smithSpearSteps.setDisplayCondition(notAddySpear);
		smithSpearSteps.setLockingStep(addySpearTask);
		allSteps.add(smithSpearSteps);

		PanelDetails killMithSteps = new PanelDetails("Kill a Mithril Dragon", Arrays.asList(moveToWhirl,
			moveToAncient2, moveToAncient3, mithrilDrag), barbFiremaking, combatGear, antidragonfire, food);
		killMithSteps.setDisplayCondition(notMithrilDrag);
		killMithSteps.setLockingStep(mithrilDragTask);
		allSteps.add(killMithSteps);

		PanelDetails buyGraniteSteps = new PanelDetails("Purchase Granite Body", Collections.singletonList(buyGranite),
			coins.quantity(95000), combatGear);
		buyGraniteSteps.setDisplayCondition(notBuyGranite);
		buyGraniteSteps.setLockingStep(buyGraniteTask);
		allSteps.add(buyGraniteSteps);

		PanelDetails finishOffSteps = new PanelDetails("Finishing off", Collections.singletonList(claimReward));
		allSteps.add(finishOffSteps);

		return allSteps;
	}
}
