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
package com.questhelper.achievementdiaries.kandarin;

import com.questhelper.ItemCollections;
import com.questhelper.QuestHelperQuest;
import com.questhelper.QuestVarbits;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.player.SpellbookRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.*;
import com.questhelper.requirements.util.Spellbook;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.panel.PanelDetails;

@QuestDescriptor(
        quest = QuestHelperQuest.KANDARIN_MEDIUM
)

public class KandarinMedium extends ComplexStateQuestHelper
{
	// Items required
	ItemRequirement combatGear, mithGrap, crossbow, unicornHorn, mortarPest, vialOfWater, dustyKey,
		bigFishingNet, lawRune, airRune, mapleUnstrung, bowString, limpSeed, rake, seedDib, primedMind,
		hammer, beatenBook, batteredKey, rope, lockpick, staff, pickaxe, iritLeaf;

	// Items recommended
	ItemRequirement food, compost;

	// unlisted item reqs
	ItemRequirement rawBass, unfIrit, hornDust;

	Requirement notBarbAgi, notSuperAnti, notEnterRange, notGrapOb, notCatchCookBass, notTPCam,
		notStringMaple, notPickLimp, notMindHelm, notFireGiant, notBarbAss, notStealHemen, notTravelMcGrubor,
		notMineCoal, limpReady, limpGrowing, notLimpGrowing, not70Agility;

	Requirement normalBook;

	// Quest steps
	Requirement alfredBar, eleWorkII, waterfallQuest, fairyTaleII;

	QuestStep barbAgi, superAnti, enterRange, grapOb, cookBass, tpCAM, stringMaple, plantAndPickLimp,
		mindHelm, barbAss, stealHemen, travelMcGrubor, mineCoal, claimReward, moveToTavDungeon,
		moveToBank, moveToSeersCath, moveToOb, moveToWorkshop, moveToWaterfall, mixUnf, crushHorn;

	NpcStep catchBass, fireGiant;

	Zone bank, seersCath, tavDungeon, workshop, obIsland, waterfall;

	ZoneRequirement inBank, inSeersCath, inTavDungeon, inWorkshop, inObIsland, inWaterfall;

	@Override
	public QuestStep loadStep()
	{
		loadZones();
		setupRequirements();
		setupSteps();

		ConditionalStep doMedium = new ConditionalStep(this, claimReward);

		doMedium.addStep(new Conditions(notGrapOb, inObIsland), grapOb);
		doMedium.addStep(new Conditions(notGrapOb, inTavDungeon), moveToOb);
		doMedium.addStep(notGrapOb, moveToTavDungeon);
		doMedium.addStep(new Conditions(notSuperAnti, inSeersCath, unfIrit, hornDust), superAnti);
		doMedium.addStep(new Conditions(notSuperAnti, inSeersCath, unfIrit), crushHorn);
		doMedium.addStep(new Conditions(notSuperAnti, inSeersCath), mixUnf);
		doMedium.addStep(notSuperAnti, moveToSeersCath);
		doMedium.addStep(new Conditions(notCatchCookBass, rawBass), cookBass);
		doMedium.addStep(notCatchCookBass, catchBass);
		doMedium.addStep(new Conditions(notStringMaple, inBank), stringMaple);
		doMedium.addStep(notStringMaple, moveToBank);
		doMedium.addStep(notEnterRange, enterRange);
		doMedium.addStep(notStealHemen, stealHemen);
		doMedium.addStep(notMineCoal, mineCoal);
		doMedium.addStep(new Conditions(notFireGiant, inWaterfall), fireGiant);
		doMedium.addStep(notFireGiant, moveToWaterfall);
		doMedium.addStep(notBarbAgi, barbAgi);
		doMedium.addStep(notBarbAss, barbAss);
		doMedium.addStep(notTravelMcGrubor, travelMcGrubor);
		doMedium.addStep(notTPCam, tpCAM);

		doMedium.addStep(new Conditions(notPickLimp), plantAndPickLimp);

		return doMedium;
	}

	public void setupRequirements()
	{
		notBarbAgi = new VarplayerRequirement(1178, false, 12);
		notSuperAnti = new VarplayerRequirement(1178, false, 13);
		notEnterRange = new VarplayerRequirement(1178, false, 14);
		notGrapOb = new VarplayerRequirement(1178, false, 15);
		notCatchCookBass = new VarplayerRequirement(1178, false, 16);
		notTPCam = new VarplayerRequirement(1178, false, 17);
		notStringMaple = new VarplayerRequirement(1178, false, 18);
		notPickLimp = new VarplayerRequirement(1178, false, 19);
		notMindHelm = new VarplayerRequirement(1178, false, 20);
		notFireGiant = new VarplayerRequirement(1178, false, 21);
		notBarbAss = new VarplayerRequirement(1178, false, 22);
		notStealHemen = new VarplayerRequirement(1178, false, 23);
		notTravelMcGrubor = new VarplayerRequirement(1178, false, 24);
		notMineCoal = new VarplayerRequirement(1178, false, 25);

		not70Agility = new Conditions(LogicType.NOR, new SkillRequirement(Skill.AGILITY, 70, true));

		limpReady = new VarbitRequirement(4957, 0);
		limpGrowing = new VarbitRequirement(4957, 4, Operation.GREATER_EQUAL);
		notLimpGrowing = new VarbitRequirement(4957, 3, Operation.LESS_EQUAL);

		mithGrap = new ItemRequirement("Mith grapple", ItemID.MITH_GRAPPLE_9419).showConditioned(notGrapOb);
		crossbow = new ItemRequirement("Any crossbow", ItemCollections.getCrossbows()).showConditioned(notGrapOb);
		unfIrit = new ItemRequirement("Unfinished Irit potion", ItemID.IRIT_POTION_UNF, 1).showConditioned(notSuperAnti);
		unicornHorn = new ItemRequirement("Unicorn horn", ItemID.UNICORN_HORN, 1).showConditioned(notSuperAnti);
		mortarPest = new ItemRequirement("Pestle and mortar", ItemID.PESTLE_AND_MORTAR).showConditioned(notSuperAnti);
		hornDust = new ItemRequirement("Horn Dust", ItemID.UNICORN_HORN_DUST, 1).showConditioned(notSuperAnti);
		vialOfWater = new ItemRequirement("Vial of water", ItemID.VIAL_OF_WATER, 1).showConditioned(notSuperAnti);
		iritLeaf = new ItemRequirement("Irit leaf", ItemID.IRIT_LEAF, 1).showConditioned(notSuperAnti);
		dustyKey = new ItemRequirement("Dusty key", ItemID.DUSTY_KEY).showConditioned(new Conditions(not70Agility,
			notGrapOb));
		dustyKey.setTooltip("You can get this by killing the Jailor in the Black Knights Base in Taverley Dungeon and" +
			" using the key he drops to enter the jail cell there to talk to Velrak for the dusty key");
		bigFishingNet = new ItemRequirement("Big fishing net", ItemID.BIG_FISHING_NET).showConditioned(notCatchCookBass);
		lawRune = new ItemRequirement("Law rune", ItemID.LAW_RUNE, 1).showConditioned(notTPCam);
		airRune = new ItemRequirement("Air Rune", ItemID.AIR_RUNE, 5).showConditioned(notTPCam);
		mapleUnstrung = new ItemRequirement("Unstring Maple shortbow", ItemID.MAPLE_SHORTBOW_U).showConditioned(notStringMaple);
		bowString = new ItemRequirement("Bow string", ItemID.BOW_STRING).showConditioned(notStringMaple);
		limpSeed = new ItemRequirement("Limpwurt seed", ItemID.LIMPWURT_SEED).showConditioned(notPickLimp);
		rake = new ItemRequirement("Rake", ItemID.RAKE).showConditioned(notPickLimp);
		seedDib = new ItemRequirement("Seed dibber", ItemID.SEED_DIBBER).showConditioned(notPickLimp);
		primedMind = new ItemRequirement("Mind bar", ItemID.PRIMED_MIND_BAR).showConditioned(notMindHelm);
		hammer = new ItemRequirement("Hammer", ItemID.HAMMER).showConditioned(notMindHelm);
		beatenBook = new ItemRequirement("Beaten Book", ItemID.BEATEN_BOOK).showConditioned(notMindHelm);
		batteredKey = new ItemRequirement("Battered Key", ItemID.BATTERED_KEY).showConditioned(notMindHelm);
		batteredKey.setTooltip("You can get another by searching the bookcase in the house south of the Elemental " +
			"Workshop, then reading the book you get from it");

		rope = new ItemRequirement("Rope", ItemID.ROPE).showConditioned(notFireGiant);
		lockpick = new ItemRequirement("Lockpick", ItemID.LOCKPICK).showConditioned(notStealHemen);
		staff = new ItemRequirement("Dramen or Lunar staff", ItemCollections.getFairyStaff()).showConditioned(notTravelMcGrubor);
		pickaxe = new ItemRequirement("Any pickaxe", ItemCollections.getPickaxes()).showConditioned(notMineCoal);
		rawBass = new ItemRequirement("Raw bass", ItemID.RAW_BASS).showConditioned(notCatchCookBass);
		compost = new ItemRequirement("Compost", ItemCollections.getCompost()).showConditioned(notPickLimp);

		combatGear = new ItemRequirement("Combat gear to kill a fire giant and complete a wave of Barbarian Assault", -1, -1).showConditioned(new Conditions(LogicType.OR, notFireGiant, notBarbAss));
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

		food = new ItemRequirement("Food", ItemCollections.getGoodEatingFood(), -1);

		normalBook = new SpellbookRequirement(Spellbook.NORMAL);

		inBank = new ZoneRequirement(bank);
		inSeersCath = new ZoneRequirement(seersCath);
		inTavDungeon = new ZoneRequirement(tavDungeon);
		inWorkshop = new ZoneRequirement(workshop);
		inObIsland = new ZoneRequirement(obIsland);
		inWaterfall = new ZoneRequirement(waterfall);

		setupGeneralRequirements();
	}

	public void loadZones()
	{
		bank = new Zone(new WorldPoint(2721, 3495, 0), new WorldPoint(2730, 3490, 0));
		seersCath = new Zone(new WorldPoint(2687, 3510, 0), new WorldPoint(2839, 3430, 0));
		tavDungeon = new Zone(new WorldPoint(2813, 9857, 0), new WorldPoint(2972, 9669, 0));
		obIsland = new Zone(new WorldPoint(2833, 3427, 0), new WorldPoint(2849, 3415, 0));
		waterfall = new Zone(new WorldPoint(2534, 9918, 0), new WorldPoint(2613, 9860, 0));
	}

	public void setupSteps()
	{
		barbAgi = new ObjectStep(this, ObjectID.OBSTACLE_PIPE_20210, new WorldPoint(2552, 3560, 0),
			"Complete a lap of the Barbarian Outpost agility course.");
		barbAss = new ObjectStep(this, ObjectID.LADDER_20226, new WorldPoint(2534, 3572, 0),
			"Complete a wave of Barbarian Assault. If it's your first time here, speak with Captain Cain for the " +
				"tutorial.");
		enterRange = new ObjectStep(this, ObjectID.GUILD_DOOR, new WorldPoint(2658, 3438, 0),
			"Enter the Ranging Guild.");
		stealHemen = new ObjectStep(this, ObjectID.CHEST_11742, new WorldPoint(2639, 3424, 0),
			"Steal from the chest in Hemenster.", lockpick);
		mineCoal = new ObjectStep(this, ObjectID.ROCKS_11366, new WorldPoint(2590, 3476, 0),
			"Mine coal near the Coal Trucks.", pickaxe);
		mineCoal.addIcon(ItemID.RUNE_PICKAXE);
		moveToWaterfall = new ObjectStep(this, ObjectID.DOOR_2010, new WorldPoint(2511, 3464, 0),
			"Enter the waterfall dungeon.", rope, combatGear);
		fireGiant = new NpcStep(this, NpcID.FIRE_GIANT_2080, new WorldPoint(2566, 9887, 0),
			"Kill a Fire giant.", true, combatGear);
		fireGiant.addAlternateNpcs(NpcID.FIRE_GIANT_2079, NpcID.FIRE_GIANT_2078);
		moveToWorkshop = new ObjectStep(this, ObjectID.STAIRCASE_3415, new WorldPoint(2711, 3498, 0),
			"Enter the Elemental Workshop.", batteredKey, primedMind);
		mindHelm = new ObjectStep(this, 123, new WorldPoint(2719, 9889, 0),
			"Make a mind helm.", primedMind);
		moveToBank = new DetailedQuestStep(this, new WorldPoint(2725, 3492, 0),
			"Go to Seers' Village bank to string a maple shortbow.");
		stringMaple = new DetailedQuestStep(this, "String a maple shortbow.", mapleUnstrung.highlighted(),
			bowString.highlighted());
		moveToTavDungeon = new ObjectStep(this, ObjectID.LADDER_16680, new WorldPoint(2884, 3397, 0),
			"Enter the Taverly Dungeon.", dustyKey, mithGrap, crossbow);
		moveToOb = new ObjectStep(this, ObjectID.LADDER_17385, new WorldPoint(2842, 9824, 0),
			"Make your way through Taverley Dungeon to the end, and climb the ladder there. If you're 70+ " +
				"Agility, use one of the shortcuts near the entrance to get there quickly.",
			dustyKey, mithGrap.equipped(), crossbow.equipped());
		grapOb = new ObjectStep(this, ObjectID.CROSSBOW_TREE_17062, new WorldPoint(2842, 3435, 0),
			"Grapple across!", mithGrap.equipped(), crossbow.equipped());
		moveToSeersCath = new DetailedQuestStep(this, new WorldPoint(2755, 3475, 0),
			"Move to the Seers' Village / Catherby area.", iritLeaf, vialOfWater, unicornHorn, mortarPest);
		mixUnf = new DetailedQuestStep(this,
			"Create an unfinished Irit potion.", iritLeaf.highlighted(), vialOfWater.highlighted());
		crushHorn = new DetailedQuestStep(this,
			"Crush a unicorn horn.", unicornHorn.highlighted(), mortarPest.highlighted());
		superAnti = new DetailedQuestStep(this,
			"Create a super antipoision potion.", hornDust.highlighted(), unfIrit.highlighted());
		plantAndPickLimp = new ObjectStep(this, NullObjectID.NULL_7848, new WorldPoint(2810, 3464, 0),
			"Plant a limpwurt seed in the Catherby Flower Patch, wait for it to grow then pick it.", rake,
			limpSeed, seedDib);
		catchBass = new NpcStep(this, NpcID.FISHING_SPOT_1520, new WorldPoint(2837, 3431, 0),
			"Catch a bass.", bigFishingNet);
		cookBass = new ObjectStep(this, ObjectID.RANGE_26181, new WorldPoint(2818, 3444, 0),
			"Cook the bass on the ranged in Catherby.", rawBass);
		travelMcGrubor = new DetailedQuestStep(this, "Take a fairy ring to McGrubor's Woods (ALS)", staff.equipped());
		tpCAM = new DetailedQuestStep(this, "Teleport to Camelot.", lawRune.quantity(1), airRune.quantity(5),
			normalBook);

		claimReward = new NpcStep(this, NpcID.THE_WEDGE, new WorldPoint(2760, 3476, 0),
			"Talk to the 'Wedge' in front of Camelot Castle to claim your reward!");
		claimReward.addDialogStep("I have a question about my Achievement Diary.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(mithGrap, crossbow, dustyKey, bigFishingNet, unicornHorn, mortarPest,
			vialOfWater, iritLeaf, mapleUnstrung, bowString, lockpick, pickaxe, lawRune.quantity(1),
			airRune.quantity(5), limpSeed, seedDib, compost, rake, rope, primedMind, batteredKey,
			beatenBook, hammer, staff, combatGear);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Collections.singletonList(food);
	}

	private void setupGeneralRequirements()
	{
		alfredBar = new QuestRequirement(QuestHelperQuest.ALFRED_GRIMHANDS_BARCRAWL, QuestState.FINISHED);
		eleWorkII = new QuestRequirement(QuestHelperQuest.ELEMENTAL_WORKSHOP_II, QuestState.FINISHED);
		waterfallQuest = new QuestRequirement(QuestHelperQuest.WATERFALL_QUEST, QuestState.FINISHED);
		fairyTaleII = new VarbitRequirement(QuestVarbits.QUEST_FAIRYTALE_II_CURE_A_QUEEN.getId(),
			Operation.GREATER_EQUAL, 40, "Partial completion of Fairytale II for access to fairy rings");
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		setupGeneralRequirements();

		ArrayList<Requirement> req = new ArrayList<>();
		req.add(alfredBar);
		req.add(eleWorkII);
		req.add(waterfallQuest);
		req.add((fairyTaleII));
		req.add(new SkillRequirement(Skill.AGILITY, 36));
		req.add(new SkillRequirement(Skill.COOKING, 43));
		req.add(new SkillRequirement(Skill.FARMING, 26));
		req.add(new SkillRequirement(Skill.FISHING, 46));
		req.add(new SkillRequirement(Skill.FLETCHING, 50));
		req.add(new SkillRequirement(Skill.HERBLORE, 48));
		req.add(new SkillRequirement(Skill.MAGIC, 45));
		req.add(new SkillRequirement(Skill.MINING, 30));
		req.add(new SkillRequirement(Skill.RANGED, 40));
		req.add(new SkillRequirement(Skill.STRENGTH, 22));
		req.add(new SkillRequirement(Skill.THIEVING, 47));
		return req;
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Collections.singletonList("Fire giant (Level 86)");
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
				new ItemReward("Kandarin headgear (2)", ItemID.KANDARIN_HEADGEAR_2, 1),
				new ItemReward("7,500 Exp. Lamp (Any skill over 40)", ItemID.ANTIQUE_LAMP, 1));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
				new UnlockReward("Coal trucks can hold up to 280 coal."),
				new UnlockReward("The Flax keeper will exchange 60 noted flax for 60 noted bow strings daily"),
				new UnlockReward("10% more marks of grace on Seers' Village Rooftop Course"),
				new UnlockReward("5% increased chance to save a harvest life from the Catherby herb patch"));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		PanelDetails grappleStep = new PanelDetails("Grapple from Water Obelisk", Arrays.asList(moveToTavDungeon, moveToOb, grapOb),
			new SkillRequirement(Skill.AGILITY, 36),
			new SkillRequirement(Skill.STRENGTH, 22),
			new SkillRequirement(Skill.RANGED, 39),
			mithGrap, crossbow, dustyKey);
		grappleStep.setDisplayCondition(notGrapOb);
		allSteps.add(grappleStep);

		PanelDetails stringMapleShortbow = new PanelDetails("String Maple Shortbow in Seers' Bank",
			Arrays.asList(moveToBank, stringMaple),
			new SkillRequirement(Skill.FLETCHING, 50, true),
			mapleUnstrung, bowString);
		stringMapleShortbow.setDisplayCondition(notStringMaple);
		allSteps.add(stringMapleShortbow);

		PanelDetails catchCookBassSteps = new PanelDetails("Catch and Cook Bass", Arrays.asList(catchBass, cookBass),
			new SkillRequirement(Skill.FISHING, 46, true),
			new SkillRequirement(Skill.COOKING, 43, true),
			bigFishingNet);
		catchCookBassSteps.setDisplayCondition(notCatchCookBass);
		allSteps.add(catchCookBassSteps);

		PanelDetails mixSuperantiSteps = new PanelDetails("Mix Superantipoison", Arrays.asList(moveToSeersCath, mixUnf, crushHorn, superAnti),
			new SkillRequirement(Skill.HERBLORE, 48, true),
			unicornHorn, mortarPest, vialOfWater, iritLeaf);
		mixSuperantiSteps.setDisplayCondition(notSuperAnti);
		allSteps.add(mixSuperantiSteps);

		PanelDetails makeMindHelmStep = new PanelDetails("Make a Mind Helm", Collections.singletonList(mindHelm), eleWorkII,
			primedMind, batteredKey, beatenBook, hammer);
		makeMindHelmStep.setDisplayCondition(notMindHelm);
		allSteps.add(makeMindHelmStep);

		PanelDetails enterRangeSteps = new PanelDetails("Enter the Ranging Guild", Collections.singletonList(enterRange),
			new SkillRequirement(Skill.RANGED, 40));
		enterRangeSteps.setDisplayCondition(notEnterRange);
		allSteps.add(enterRangeSteps);

		PanelDetails stealHemSteps = new PanelDetails("Steal from Hemenster Chest",
			Collections.singletonList(stealHemen),
			new SkillRequirement(Skill.THIEVING, 47),
			lockpick);
		stealHemSteps.setDisplayCondition(notStealHemen);
		allSteps.add(stealHemSteps);

		PanelDetails mineCoalSteps = new PanelDetails("Mine Coal", Collections.singletonList(mineCoal),
			new SkillRequirement(Skill.MINING, 30, true), pickaxe);
		mineCoalSteps.setDisplayCondition(notMineCoal);
		allSteps.add(mineCoalSteps);

		PanelDetails killFireGiantSteps = new PanelDetails("Kill a Fire Giant", Arrays.asList(moveToWaterfall, fireGiant), waterfallQuest,
			combatGear, food, rope);
		killFireGiantSteps.setDisplayCondition(notFireGiant);
		allSteps.add(killFireGiantSteps);

		PanelDetails barbAgiSteps = new PanelDetails("Barbarian Agility Course Lap", Collections.singletonList(barbAgi),
			alfredBar,
			new SkillRequirement(Skill.AGILITY, 35));
		barbAgiSteps.setDisplayCondition(notBarbAgi);
		allSteps.add(barbAgiSteps);

		PanelDetails barbAssSteps = new PanelDetails("Barbarian Assault Wave", Collections.singletonList(barbAss));
		barbAssSteps.setDisplayCondition(notBarbAss);
		allSteps.add(barbAssSteps);

		PanelDetails travelGrubSteps = new PanelDetails("Fairy Ring to McGrubor's Woods",
			Collections.singletonList(travelMcGrubor), fairyTaleII, staff);
		travelGrubSteps.setDisplayCondition(notTravelMcGrubor);
		allSteps.add(travelGrubSteps);

		PanelDetails teleCamSteps = new PanelDetails("Teleport to Camelot", Collections.singletonList(tpCAM),
			new SkillRequirement(Skill.MAGIC, 45, true),
			lawRune.quantity(1), airRune.quantity(5), normalBook);
		teleCamSteps.setDisplayCondition(notTPCam);
		allSteps.add(teleCamSteps);

		PanelDetails pickLimpSteps = new PanelDetails("Pick Limpwurt in Catherby",
			Collections.singletonList(plantAndPickLimp),
			new SkillRequirement(Skill.FARMING, 26, true),
			limpSeed, seedDib, compost, rake);
		pickLimpSteps.setDisplayCondition(notPickLimp);
		allSteps.add(pickLimpSteps);

		PanelDetails finishingOffSteps = new PanelDetails("Finishing off", Collections.singletonList(claimReward));
		allSteps.add(finishingOffSteps);

		return allSteps;
	}
}
