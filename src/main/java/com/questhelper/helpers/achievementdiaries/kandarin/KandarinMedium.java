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

import com.questhelper.bank.banktab.BankSlotIcons;
import com.questhelper.collections.ItemCollections;
import com.questhelper.collections.KeyringCollection;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.questinfo.QuestVarbits;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.KeyringRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.player.SpellbookRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.util.Spellbook;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.*;
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
		notMineCoal, not70Agility;

	Requirement normalBook;

	// Quest steps
	Requirement alfredBar, eleWorkII, waterfallQuest, fairyTaleII;

	QuestStep barbAgi, superAnti, enterRange, grapOb, cookBass, tpCAM, stringMaple, plantAndPickLimp,
		makeMindHelmet, barbAss, stealHemen, travelMcGrubor, mineCoal, claimReward, moveToTavDungeon,
		moveToBank, moveToSeersCath, moveToOb, moveToWorkshop, moveToWaterfall, mixUnf, crushHorn;

	NpcStep catchBass, fireGiant;

	Zone bank, seersCath, tavDungeon, workshop, obIsland, waterfall;

	ZoneRequirement inBank, inSeersCath, inTavDungeon, inWorkshop, inObIsland, inWaterfall;

	ConditionalStep barbAgiTask, superAntiTask, enterRangeTask, grapObTask, catchCookBassTask, tpCamTask,
		stringMapleTask, pickLimpTask, mindHelmTask, fireGiantTask, barbAssTask, stealHemenTask, travelMcGruborTask,
		mineCoalTask;

	@Override
	public QuestStep loadStep()
	{
		initializeRequirements();
		setupSteps();

		ConditionalStep doMedium = new ConditionalStep(this, claimReward);

		pickLimpTask = new ConditionalStep(this, plantAndPickLimp);
		doMedium.addStep(notPickLimp, pickLimpTask);

		grapObTask = new ConditionalStep(this, moveToTavDungeon);
		grapObTask.addStep(inTavDungeon, moveToOb);
		grapObTask.addStep(inObIsland, grapOb);
		doMedium.addStep(notGrapOb, grapObTask);

		catchCookBassTask = new ConditionalStep(this, catchBass);
		catchCookBassTask.addStep(rawBass, cookBass);
		doMedium.addStep(notCatchCookBass, catchCookBassTask);

		stringMapleTask = new ConditionalStep(this, moveToBank);
		stringMapleTask.addStep(inBank, stringMaple);
		doMedium.addStep(notStringMaple, stringMapleTask);

		superAntiTask = new ConditionalStep(this, moveToSeersCath);
		superAntiTask.addStep(new Conditions(inSeersCath, unfIrit, hornDust), superAnti);
		superAntiTask.addStep(new Conditions(inSeersCath, unfIrit), crushHorn);
		superAntiTask.addStep(inSeersCath, mixUnf);
		doMedium.addStep(notSuperAnti, superAntiTask);

		mindHelmTask = new ConditionalStep(this, makeMindHelmet);
		mindHelmTask.addStep(inWorkshop, makeMindHelmet);
		doMedium.addStep(notMindHelm, mindHelmTask);

		enterRangeTask = new ConditionalStep(this, enterRange);
		doMedium.addStep(notEnterRange, enterRangeTask);

		stealHemenTask = new ConditionalStep(this, stealHemen);
		doMedium.addStep(notStealHemen, stealHemenTask);

		mineCoalTask = new ConditionalStep(this, mineCoal);
		doMedium.addStep(notMineCoal, mineCoalTask);

		fireGiantTask = new ConditionalStep(this, moveToWaterfall);
		fireGiantTask.addStep(inWaterfall, fireGiant);
		doMedium.addStep(notFireGiant, fireGiantTask);

		barbAgiTask = new ConditionalStep(this, barbAgi);
		doMedium.addStep(notBarbAgi, barbAgiTask);

		barbAssTask = new ConditionalStep(this, barbAss);
		doMedium.addStep(notBarbAss, barbAssTask);

		travelMcGruborTask = new ConditionalStep(this, travelMcGrubor);
		doMedium.addStep(notTravelMcGrubor, travelMcGruborTask);

		tpCamTask = new ConditionalStep(this, tpCAM);
		doMedium.addStep(notTPCam, tpCamTask);

		return doMedium;
	}

	@Override
	protected void setupRequirements()
	{
		notBarbAgi = new VarplayerRequirement(VarPlayerID.KANDARIN_ACHIEVEMENT_DIARY, false, 12);
		notSuperAnti = new VarplayerRequirement(VarPlayerID.KANDARIN_ACHIEVEMENT_DIARY, false, 13);
		notEnterRange = new VarplayerRequirement(VarPlayerID.KANDARIN_ACHIEVEMENT_DIARY, false, 14);
		notGrapOb = new VarplayerRequirement(VarPlayerID.KANDARIN_ACHIEVEMENT_DIARY, false, 15);
		notCatchCookBass = new VarplayerRequirement(VarPlayerID.KANDARIN_ACHIEVEMENT_DIARY, false, 16);
		notTPCam = new VarplayerRequirement(VarPlayerID.KANDARIN_ACHIEVEMENT_DIARY, false, 17);
		notStringMaple = new VarplayerRequirement(VarPlayerID.KANDARIN_ACHIEVEMENT_DIARY, false, 18);
		notPickLimp = new VarplayerRequirement(VarPlayerID.KANDARIN_ACHIEVEMENT_DIARY, false, 19);
		notMindHelm = new VarplayerRequirement(VarPlayerID.KANDARIN_ACHIEVEMENT_DIARY, false, 20);
		notFireGiant = new VarplayerRequirement(VarPlayerID.KANDARIN_ACHIEVEMENT_DIARY, false, 21);
		notBarbAss = new VarplayerRequirement(VarPlayerID.KANDARIN_ACHIEVEMENT_DIARY, false, 22);
		notStealHemen = new VarplayerRequirement(VarPlayerID.KANDARIN_ACHIEVEMENT_DIARY, false, 23);
		notTravelMcGrubor = new VarplayerRequirement(VarPlayerID.KANDARIN_ACHIEVEMENT_DIARY, false, 24);
		notMineCoal = new VarplayerRequirement(VarPlayerID.KANDARIN_ACHIEVEMENT_DIARY, false, 25);

		not70Agility = new Conditions(LogicType.NOR, new SkillRequirement(Skill.AGILITY, 70, true));

		mithGrap = new ItemRequirement("Mith grapple", ItemID.XBOWS_GRAPPLE_TIP_BOLT_MITHRIL_ROPE).showConditioned(notGrapOb).isNotConsumed();
		crossbow = new ItemRequirement("Any crossbow", ItemCollections.CROSSBOWS).showConditioned(notGrapOb).isNotConsumed();
		unfIrit = new ItemRequirement("Unfinished irit potion", ItemID.IRITVIAL, 1).showConditioned(notSuperAnti);
		unicornHorn = new ItemRequirement("Unicorn horn", ItemID.UNICORN_HORN, 1).showConditioned(notSuperAnti);
		mortarPest = new ItemRequirement("Pestle and mortar", ItemID.PESTLE_AND_MORTAR).showConditioned(notSuperAnti).isNotConsumed();
		hornDust = new ItemRequirement("Horn Dust", ItemID.UNICORN_HORN_DUST, 1).showConditioned(notSuperAnti);
		vialOfWater = new ItemRequirement("Vial of water", ItemID.VIAL_WATER, 1).showConditioned(notSuperAnti);
		iritLeaf = new ItemRequirement("Irit leaf", ItemID.IRIT_LEAF, 1).showConditioned(notSuperAnti);
		dustyKey = new KeyringRequirement("Dusty Key", configManager, KeyringCollection.DUSTY_KEY).showConditioned(new Conditions(not70Agility,
			notGrapOb)).isNotConsumed();
		dustyKey.setTooltip("You can get this by killing the Jailer in the Black Knights Base in Taverley Dungeon and" +
			" using the key he drops to enter the jail cell there to talk to Velrak for the dusty key");
		bigFishingNet = new ItemRequirement("Big fishing net", ItemID.BIG_NET).showConditioned(notCatchCookBass).isNotConsumed();
		lawRune = new ItemRequirement("Law rune", ItemID.LAWRUNE, 1).showConditioned(notTPCam);
		airRune = new ItemRequirement("Air Rune", ItemID.AIRRUNE, 5).showConditioned(notTPCam);
		mapleUnstrung = new ItemRequirement("Maple shortbow (u)", ItemID.UNSTRUNG_MAPLE_SHORTBOW).showConditioned(notStringMaple);
		bowString = new ItemRequirement("Bow string", ItemID.BOW_STRING).showConditioned(notStringMaple);
		limpSeed = new ItemRequirement("Limpwurt seed", ItemID.LIMPWURT_SEED).showConditioned(notPickLimp);
		rake = new ItemRequirement("Rake", ItemID.RAKE).showConditioned(notPickLimp).isNotConsumed();
		seedDib = new ItemRequirement("Seed dibber", ItemID.DIBBER).showConditioned(notPickLimp).isNotConsumed();
		primedMind = new ItemRequirement("Mind bar", ItemID.ELEM_MIND_BAR).showConditioned(notMindHelm);
		hammer = new ItemRequirement("Hammer", ItemID.HAMMER).showConditioned(notMindHelm).isNotConsumed();
		beatenBook = new ItemRequirement("Beaten Book", ItemID.ELEMENTAL_WORKSHOP_HELM_BOOK).showConditioned(notMindHelm);
		batteredKey = new KeyringRequirement("Battered Key", configManager, KeyringCollection.BATTERED_KEY).showConditioned(notMindHelm);
		batteredKey.setTooltip("You can get another by searching the bookcase in the house south of the Elemental " +
			"Workshop, then reading the book you get from it");

		rope = new ItemRequirement("Rope", ItemID.ROPE).showConditioned(notFireGiant).isNotConsumed();
		lockpick = new ItemRequirement("Lockpick", ItemID.LOCKPICK).showConditioned(notStealHemen).isNotConsumed();
		staff = new ItemRequirement("Dramen or Lunar staff", ItemCollections.FAIRY_STAFF).showConditioned(notTravelMcGrubor).isNotConsumed();
		pickaxe = new ItemRequirement("Any pickaxe", ItemCollections.PICKAXES).showConditioned(notMineCoal).isNotConsumed();
		rawBass = new ItemRequirement("Raw bass", ItemID.RAW_BASS).showConditioned(notCatchCookBass);
		compost = new ItemRequirement("Compost", ItemCollections.COMPOST).showConditioned(notPickLimp);

		combatGear = new ItemRequirement("Combat gear to kill a fire giant and complete a wave of Barbarian Assault", -1, -1)
			.showConditioned(new Conditions(LogicType.OR, notFireGiant, notBarbAss)).isNotConsumed();
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, -1);

		normalBook = new SpellbookRequirement(Spellbook.NORMAL);

		inBank = new ZoneRequirement(bank);
		inSeersCath = new ZoneRequirement(seersCath);
		inTavDungeon = new ZoneRequirement(tavDungeon);
		inWorkshop = new ZoneRequirement(workshop);
		inObIsland = new ZoneRequirement(obIsland);
		inWaterfall = new ZoneRequirement(waterfall);

		setupGeneralRequirements();
	}

	@Override
	protected void setupZones()
	{
		bank = new Zone(new WorldPoint(2721, 3495, 0), new WorldPoint(2730, 3490, 0));
		seersCath = new Zone(new WorldPoint(2687, 3510, 0), new WorldPoint(2839, 3430, 0));
		tavDungeon = new Zone(new WorldPoint(2813, 9857, 0), new WorldPoint(2972, 9669, 0));
		obIsland = new Zone(new WorldPoint(2833, 3427, 0), new WorldPoint(2849, 3415, 0));
		waterfall = new Zone(new WorldPoint(2534, 9918, 0), new WorldPoint(2613, 9860, 0));
		workshop = new Zone(new WorldPoint(2682, 9862, 0), new WorldPoint(2747, 9927, 0));
	}

	public void setupSteps()
	{
		barbAgi = new ObjectStep(this, ObjectID.AGILITY_OBSTICAL_PIPE_BARBARIAN, new WorldPoint(2552, 3560, 0),
			"Complete a lap of the Barbarian Outpost agility course.");
		barbAss = new ObjectStep(this, ObjectID.BARBASSAULT_RECRUITMENT_ENTRANCE, new WorldPoint(2534, 3572, 0),
			"Complete a wave of Barbarian Assault. If it's your first time here, speak with Captain Cain for the " +
				"tutorial.");
		enterRange = new ObjectStep(this, ObjectID.RANGING_GUILD_DOOR, new WorldPoint(2658, 3438, 0),
			"Enter the Ranging Guild.");
		stealHemen = new ObjectStep(this, ObjectID.PICKCHEST3, new WorldPoint(2639, 3424, 0),
			"Steal from the chest in Hemenster.", lockpick);
		mineCoal = new ObjectStep(this, ObjectID.COALROCK1, new WorldPoint(2590, 3476, 0),
			"Mine coal near the Coal Trucks.", pickaxe);
		mineCoal.addIcon(ItemID.RUNE_PICKAXE);
		moveToWaterfall = new ObjectStep(this, ObjectID.WATERFALL_LEDGE_DOOR, new WorldPoint(2511, 3464, 0),
			"Enter the waterfall dungeon.", rope, combatGear);
		fireGiant = new NpcStep(this, NpcID.FIREGIANT3, new WorldPoint(2566, 9887, 0),
			"Kill a Fire giant.", true, combatGear);
		fireGiant.addAlternateNpcs(NpcID.FIREGIANT2, NpcID.FIREGIANT);
		moveToWorkshop = new ObjectStep(this, ObjectID.ELEMENTAL_WORKSHOP_SPIRALSTAIRSTOP, new WorldPoint(2711, 3498, 0),
			"Enter the Elemental Workshop.", batteredKey, primedMind);
		makeMindHelmet = new ObjectStep(this, ObjectID.ELEMENTAL_WORKSHOP_WORKBENCH, new WorldPoint(2717, 9888, 0),
			"Make a mind helm.", beatenBook, primedMind);
		moveToBank = new DetailedQuestStep(this, new WorldPoint(2725, 3492, 0),
			"Go to Seers' Village bank to string a maple shortbow.");
		stringMaple = new DetailedQuestStep(this, "String a maple shortbow.", mapleUnstrung.highlighted(),
			bowString.highlighted());

		int agilityLevel = client.getRealSkillLevel(Skill.AGILITY);
		if (agilityLevel >= 65 && agilityLevel < 70)
		{
			moveToTavDungeon = new ObjectStep(this, ObjectID.LADDER_OUTSIDE_TO_UNDERGROUND, new WorldPoint(2884, 3397, 0),
				"Enter the Taverley Dungeon. Bring a summer pie to boost for the 70 Agility shortcut if you'd like to save time.", dustyKey, mithGrap, crossbow);
		}
		else
		{
			moveToTavDungeon = new ObjectStep(this, ObjectID.LADDER_OUTSIDE_TO_UNDERGROUND, new WorldPoint(2884, 3397, 0),
				"Enter the Taverley Dungeon.", dustyKey, mithGrap, crossbow);
		}

		moveToOb = new ObjectStep(this, ObjectID.LADDER_FROM_CELLAR, new WorldPoint(2842, 9824, 0),
			"Make your way through Taverley Dungeon to the end, and climb the ladder there. If you're 70+ " +
				"Agility, use one of the shortcuts near the entrance to get there quickly.",
			dustyKey, mithGrap.equipped(), crossbow.equipped());
		grapOb = new ObjectStep(this, ObjectID.XBOWS_BEACH_TO_ISLAND_TREE_BASIC, new WorldPoint(2842, 3435, 0),
			"Grapple across!", mithGrap.equipped(), crossbow.equipped());
		moveToSeersCath = new DetailedQuestStep(this, new WorldPoint(2755, 3475, 0),
			"Move to the Seers' Village / Catherby area.", iritLeaf, vialOfWater, unicornHorn, mortarPest);
		mixUnf = new DetailedQuestStep(this,
			"Create an unfinished Irit potion.", iritLeaf.highlighted(), vialOfWater.highlighted());
		crushHorn = new DetailedQuestStep(this,
			"Crush a unicorn horn.", unicornHorn.highlighted(), mortarPest.highlighted());
		superAnti = new DetailedQuestStep(this,
			"Create a super antipoison potion.", hornDust.highlighted(), unfIrit.highlighted());
		plantAndPickLimp = new ObjectStep(this, ObjectID.FARMING_FLOWER_PATCH_2, new WorldPoint(2810, 3464, 0),
			"Plant a limpwurt seed in the Catherby Flower Patch, wait for it to grow then pick it. " +
				"If you're waiting for it to grow and want to complete further tasks, use the tick box on panel.",
			rake, limpSeed, seedDib);
		catchBass = new NpcStep(this, NpcID._0_44_53_MEMBERFISH, new WorldPoint(2837, 3431, 0),
			"Catch a bass on Catherby Beach.", bigFishingNet);
		cookBass = new ObjectStep(this, ObjectID.RANGE, new WorldPoint(2818, 3444, 0),
			"Cook the bass on the range in Catherby.", rawBass);
		travelMcGrubor = new DetailedQuestStep(this, "Take a fairy ring to McGrubor's Woods (ALS)", staff.equipped());
		tpCAM = new DetailedQuestStep(this, "Teleport to Camelot.", lawRune.quantity(1), airRune.quantity(5),
			normalBook);

		claimReward = new NpcStep(this, NpcID.SEERS_DIARY_WEDGE, new WorldPoint(2760, 3476, 0),
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
		req.add(new SkillRequirement(Skill.AGILITY, 36, true));
		req.add(new SkillRequirement(Skill.COOKING, 43, true));
		req.add(new SkillRequirement(Skill.FARMING, 26, true));
		req.add(new SkillRequirement(Skill.FISHING, 46, true));
		req.add(new SkillRequirement(Skill.FLETCHING, 50, true));
		req.add(new SkillRequirement(Skill.HERBLORE, 48, true));
		req.add(new SkillRequirement(Skill.MAGIC, 45, true));
		req.add(new SkillRequirement(Skill.MINING, 30, true));
		req.add(new SkillRequirement(Skill.RANGED, 40, true));
		req.add(new SkillRequirement(Skill.STRENGTH, 22, true));
		req.add(new SkillRequirement(Skill.THIEVING, 47, true));

		req.add(alfredBar);
		req.add(eleWorkII);
		req.add(waterfallQuest);
		req.add(fairyTaleII);

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
			new ItemReward("Kandarin headgear (2)", ItemID.SEERS_HEADBAND_MEDIUM, 1),
			new ItemReward("7,500 Exp. Lamp (Any skill over 40)", ItemID.THOSF_REWARD_LAMP, 1));
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

		PanelDetails pickLimpSteps = new PanelDetails("Pick Limpwurt in Catherby", Collections.singletonList(plantAndPickLimp),
			new SkillRequirement(Skill.FARMING, 26, true), limpSeed, seedDib, compost, rake);
		pickLimpSteps.setDisplayCondition(notPickLimp);
		pickLimpSteps.setLockingStep(pickLimpTask);
		allSteps.add(pickLimpSteps);

		PanelDetails grappleStep = new PanelDetails("Grapple from Water Obelisk", Arrays.asList(moveToTavDungeon, moveToOb, grapOb),
			new SkillRequirement(Skill.AGILITY, 36, true), new SkillRequirement(Skill.STRENGTH, 22, true),
			new SkillRequirement(Skill.RANGED, 39, true), mithGrap, crossbow, dustyKey);
		grappleStep.setDisplayCondition(notGrapOb);
		grappleStep.setLockingStep(grapObTask);
		allSteps.add(grappleStep);

		PanelDetails catchCookBassSteps = new PanelDetails("Catch and Cook Bass", Arrays.asList(catchBass, cookBass),
			new SkillRequirement(Skill.FISHING, 46, true), new SkillRequirement(Skill.COOKING, 43, true),
			bigFishingNet);
		catchCookBassSteps.setDisplayCondition(notCatchCookBass);
		catchCookBassSteps.setLockingStep(catchCookBassTask);
		allSteps.add(catchCookBassSteps);

		PanelDetails stringMapleShortbow = new PanelDetails("String Maple Shortbow in Seers' Bank",
			Arrays.asList(moveToBank, stringMaple), new SkillRequirement(Skill.FLETCHING, 50, true),
			mapleUnstrung, bowString);
		stringMapleShortbow.setDisplayCondition(notStringMaple);
		stringMapleShortbow.setLockingStep(stringMapleTask);
		allSteps.add(stringMapleShortbow);

		PanelDetails mixSuperantiSteps = new PanelDetails("Mix Superantipoison", Arrays.asList(moveToSeersCath, mixUnf, crushHorn, superAnti),
			new SkillRequirement(Skill.HERBLORE, 48, true), unicornHorn, mortarPest, vialOfWater, iritLeaf);
		mixSuperantiSteps.setDisplayCondition(notSuperAnti);
		mixSuperantiSteps.setLockingStep(superAntiTask);
		allSteps.add(mixSuperantiSteps);

		PanelDetails makeMindHelmStep = new PanelDetails("Make a Mind Helm", Arrays.asList(moveToWorkshop,
			makeMindHelmet), eleWorkII, primedMind, batteredKey, beatenBook, hammer);
		makeMindHelmStep.setDisplayCondition(notMindHelm);
		makeMindHelmStep.setLockingStep(mindHelmTask);
		allSteps.add(makeMindHelmStep);

		PanelDetails enterRangeSteps = new PanelDetails("Enter the Ranging Guild", Collections.singletonList(enterRange),
			new SkillRequirement(Skill.RANGED, 40, true));
		enterRangeSteps.setDisplayCondition(notEnterRange);
		enterRangeSteps.setLockingStep(enterRangeTask);
		allSteps.add(enterRangeSteps);

		PanelDetails stealHemSteps = new PanelDetails("Steal from Hemenster Chest",
			Collections.singletonList(stealHemen),
			new SkillRequirement(Skill.THIEVING, 47, true),
			lockpick);
		stealHemSteps.setDisplayCondition(notStealHemen);
		stealHemSteps.setLockingStep(stealHemenTask);
		allSteps.add(stealHemSteps);

		PanelDetails mineCoalSteps = new PanelDetails("Mine Coal", Collections.singletonList(mineCoal),
			new SkillRequirement(Skill.MINING, 30, true), pickaxe);
		mineCoalSteps.setDisplayCondition(notMineCoal);
		mineCoalSteps.setLockingStep(mineCoalTask);
		allSteps.add(mineCoalSteps);

		PanelDetails killFireGiantSteps = new PanelDetails("Kill a Fire Giant", Arrays.asList(moveToWaterfall, fireGiant), waterfallQuest,
			combatGear, food, rope);
		killFireGiantSteps.setDisplayCondition(notFireGiant);
		killFireGiantSteps.setLockingStep(fireGiantTask);
		allSteps.add(killFireGiantSteps);

		PanelDetails barbAgiSteps = new PanelDetails("Barbarian Agility Course Lap", Collections.singletonList(barbAgi),
			alfredBar,
			new SkillRequirement(Skill.AGILITY, 35, true));
		barbAgiSteps.setDisplayCondition(notBarbAgi);
		barbAgiSteps.setLockingStep(barbAgiTask);
		allSteps.add(barbAgiSteps);

		PanelDetails barbAssSteps = new PanelDetails("Barbarian Assault Wave", Collections.singletonList(barbAss));
		barbAssSteps.setDisplayCondition(notBarbAss);
		barbAssSteps.setLockingStep(barbAssTask);
		allSteps.add(barbAssSteps);

		PanelDetails travelGrubSteps = new PanelDetails("Fairy Ring to McGrubor's Woods",
			Collections.singletonList(travelMcGrubor), fairyTaleII, staff);
		travelGrubSteps.setDisplayCondition(notTravelMcGrubor);
		travelGrubSteps.setLockingStep(travelMcGruborTask);
		allSteps.add(travelGrubSteps);

		PanelDetails teleCamSteps = new PanelDetails("Teleport to Camelot", Collections.singletonList(tpCAM),
			new SkillRequirement(Skill.MAGIC, 45, true), lawRune.quantity(1), airRune.quantity(5), normalBook);
		teleCamSteps.setDisplayCondition(notTPCam);
		teleCamSteps.setLockingStep(tpCamTask);
		allSteps.add(teleCamSteps);

		PanelDetails finishingOffSteps = new PanelDetails("Finishing off", Collections.singletonList(claimReward));
		allSteps.add(finishingOffSteps);

		return allSteps;
	}
}
