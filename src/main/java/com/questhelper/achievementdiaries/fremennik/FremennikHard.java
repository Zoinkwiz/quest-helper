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
package com.questhelper.achievementdiaries.fremennik;

import com.questhelper.ItemCollections;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.player.SpellbookRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.Spellbook;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.*;
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
	quest = QuestHelperQuest.FREMENNIK_HARD
)

public class FremennikHard extends ComplexStateQuestHelper
{
	// Items required
	ItemRequirement bronzeNail, rope, lawRune, lawRune2, astralRune, waterRune, fireRune, teasingStick, knife, axe,
		pickaxe, hammer, arcticLog, whiteBerries, log, cadantineUnfPot, rake;

	Requirement notTPTroll, notCatchKyatt, notMixSuperDef, notStealGem, notCraftShield, notMineAddy, notMiscSupport,
		notTPWaterbirth, notFreeBlast;

	// Quest requirements
	Requirement giantDwarf, fremIsles, throneOfMisc, eadgarsRuse, lunarDiplomacy;

	Requirement normalBook, lunarBook;

	// Steps
	QuestStep tpTroll, catchKyatt, mixSuperDef, stealGem, craftShield, mineAddy, tpWaterbirth, freeBlast,
		moveToRiverGem, moveToCaveGem, moveToKeldagrimGem, moveToKeldagrimVarrockGem, moveToRiverBlast, moveToCaveBlast,
		moveToKeldagrimBlast, moveToKeldagrimVarrockBlast, moveToNeitiznot, moveToJatizso, moveToMisc, moveToMine,
		moveToBlast, claimReward, moveToRellekka;

	ObjectStep miscSupport;

	Zone misc, neitiznot, keldagrim, jatizso, caveArea, riverArea, varrockArea, hunterArea, rellekkaArea, mineArea,
		blastArea;

	ZoneRequirement inMisc, inNeitiznot, inKeldagrim, inJatizso, inCaveArea, inRiverArea, inVarrockArea, inHunterArea,
		inRellekka, inMineArea, inBlastArea;

	ConditionalStep tpTrollTask, catchKyattTask, mixSuperDefTask, stealGemTask, craftShieldTask, mineAddyTask,
		miscSupportTask, tpWaterbirthTask, freeBlastTask;

	@Override
	public QuestStep loadStep()
	{
		loadZones();
		setupRequirements();
		setupSteps();

		ConditionalStep doHard = new ConditionalStep(this, claimReward);

		miscSupportTask = new ConditionalStep(this, moveToMisc);
		miscSupportTask.addStep(inMisc, miscSupport);
		doHard.addStep(notMiscSupport, miscSupportTask);

		mineAddyTask = new ConditionalStep(this, moveToJatizso);
		mineAddyTask.addStep(inJatizso, moveToMine);
		mineAddyTask.addStep(inMineArea, mineAddy);
		doHard.addStep(notMineAddy, mineAddyTask);

		craftShieldTask = new ConditionalStep(this, moveToNeitiznot);
		craftShieldTask.addStep(inNeitiznot, craftShield);
		doHard.addStep(notCraftShield, craftShieldTask);

		mixSuperDefTask = new ConditionalStep(this, moveToRellekka);
		mixSuperDefTask.addStep(inRellekka, mixSuperDef);
		doHard.addStep(notMixSuperDef, mixSuperDefTask);

		catchKyattTask = new ConditionalStep(this, catchKyatt);
		doHard.addStep(notCatchKyatt, catchKyattTask);

		stealGemTask = new ConditionalStep(this, moveToCaveGem);
		stealGemTask.addStep(inVarrockArea, moveToKeldagrimVarrockGem);
		stealGemTask.addStep(inCaveArea, moveToRiverGem);
		stealGemTask.addStep(inRiverArea, moveToKeldagrimGem);
		stealGemTask.addStep(inKeldagrim, stealGem);
		doHard.addStep(notStealGem, stealGemTask);

		freeBlastTask = new ConditionalStep(this, moveToCaveBlast);
		freeBlastTask.addStep(inVarrockArea, moveToKeldagrimVarrockBlast);
		freeBlastTask.addStep(inCaveArea, moveToRiverBlast);
		freeBlastTask.addStep(inRiverArea, moveToKeldagrimBlast);
		freeBlastTask.addStep(inKeldagrim, moveToBlast);
		freeBlastTask.addStep(inBlastArea, freeBlast);
		doHard.addStep(notFreeBlast, freeBlastTask);

		tpTrollTask = new ConditionalStep(this, tpTroll);
		doHard.addStep(notTPTroll, tpTrollTask);

		tpWaterbirthTask = new ConditionalStep(this, tpWaterbirth);
		doHard.addStep(notTPWaterbirth, tpWaterbirthTask);

		return doHard;
	}

	@Override
	public void setupRequirements()
	{
		notTPTroll = new VarplayerRequirement(1184, false, 21);
		notCatchKyatt = new VarplayerRequirement(1184, false, 23);
		notMixSuperDef = new VarplayerRequirement(1184, false, 24);
		notStealGem = new VarplayerRequirement(1184, false, 25);
		notCraftShield = new VarplayerRequirement(1184, false, 26);
		notMineAddy = new VarplayerRequirement(1184, false, 27);
		notMiscSupport = new VarplayerRequirement(1184, false, 28);
		notTPWaterbirth = new VarplayerRequirement(1184, false, 29);
		notFreeBlast = new VarplayerRequirement(1184, false, 30);

		pickaxe = new ItemRequirement("Any pickaxe", ItemCollections.PICKAXES).showConditioned(notMineAddy).isNotConsumed();
		bronzeNail = new ItemRequirement("Bronze nails", ItemID.BRONZE_NAILS).showConditioned(notCraftShield);
		rope = new ItemRequirement("Rope", ItemID.ROPE).showConditioned(notCraftShield).isNotConsumed();
		lawRune = new ItemRequirement("Law rune", ItemID.LAW_RUNE).showConditioned(notTPTroll);
		lawRune2 = new ItemRequirement("Law rune", ItemID.LAW_RUNE).showConditioned(notTPWaterbirth);
		astralRune = new ItemRequirement("Astral rune", ItemID.ASTRAL_RUNE).showConditioned(notTPWaterbirth);
		waterRune = new ItemRequirement("Water rune", ItemID.WATER_RUNE).showConditioned(notTPWaterbirth);
		fireRune = new ItemRequirement("Fire rune", ItemID.FIRE_RUNE).showConditioned(notTPTroll);
		teasingStick = new ItemRequirement("Teasing Stick", ItemID.TEASING_STICK).showConditioned(notCatchKyatt).isNotConsumed();
		knife = new ItemRequirement("Knife", ItemID.KNIFE).showConditioned(notCatchKyatt).isNotConsumed();
		axe = new ItemRequirement("Any axe", ItemCollections.AXES).showConditioned(notCraftShield).isNotConsumed();
		hammer = new ItemRequirement("Hammer", ItemID.HAMMER).showConditioned(notCraftShield).isNotConsumed();
		arcticLog = new ItemRequirement("Arctic pine logs", ItemID.ARCTIC_PINE_LOGS).showConditioned(notCraftShield);
		cadantineUnfPot = new ItemRequirement("Cadantine unf pot", ItemID.CADANTINE_POTION_UNF).showConditioned(notMixSuperDef);
		whiteBerries = new ItemRequirement("White berries", ItemID.WHITE_BERRIES).showConditioned(notMixSuperDef);
		log = new ItemRequirement("Logs", ItemID.LOGS).showConditioned(notCatchKyatt);
		rake = new ItemRequirement("Rake", ItemID.RAKE).showConditioned(notMiscSupport).isNotConsumed();

		normalBook = new SpellbookRequirement(Spellbook.NORMAL);
		lunarBook = new SpellbookRequirement(Spellbook.LUNAR);

		giantDwarf = new QuestRequirement(QuestHelperQuest.THE_GIANT_DWARF, QuestState.IN_PROGRESS);
		fremIsles = new QuestRequirement(QuestHelperQuest.THE_FREMENNIK_ISLES, QuestState.FINISHED);
		throneOfMisc = new QuestRequirement(QuestHelperQuest.THRONE_OF_MISCELLANIA, QuestState.FINISHED);
		eadgarsRuse = new QuestRequirement(QuestHelperQuest.EADGARS_RUSE, QuestState.FINISHED);
		lunarDiplomacy = new QuestRequirement(QuestHelperQuest.LUNAR_DIPLOMACY, QuestState.FINISHED);

		inMisc = new ZoneRequirement(misc);
		inNeitiznot = new ZoneRequirement(neitiznot);
		inKeldagrim = new ZoneRequirement(keldagrim);
		inJatizso = new ZoneRequirement(jatizso);
		inCaveArea = new ZoneRequirement(caveArea);
		inRiverArea = new ZoneRequirement(riverArea);
		inHunterArea = new ZoneRequirement(hunterArea);
		inVarrockArea = new ZoneRequirement(varrockArea);
		inRellekka = new ZoneRequirement(rellekkaArea);
		inMineArea = new ZoneRequirement(mineArea);
		inBlastArea = new ZoneRequirement(blastArea);
	}

	public void loadZones()
	{
		misc = new Zone(new WorldPoint(2492, 3922, 0), new WorldPoint(2629, 3814, 0));
		neitiznot = new Zone(new WorldPoint(2306, 3825, 0), new WorldPoint(2367, 3779, 0));
		jatizso = new Zone(new WorldPoint(2369, 3838, 0), new WorldPoint(2434, 3778, 0));
		caveArea = new Zone(new WorldPoint(2767, 10165, 0), new WorldPoint(2802, 10127, 0));
		riverArea = new Zone(new WorldPoint(2816, 10148, 0), new WorldPoint(2893, 10114, 0));
		varrockArea = new Zone(new WorldPoint(3076, 3617, 0), new WorldPoint(3290, 3374, 0));
		keldagrim = new Zone(new WorldPoint(2816, 10238, 0), new WorldPoint(2943, 10158, 0));
		hunterArea = new Zone(new WorldPoint(2690, 3838, 0), new WorldPoint(2748, 3767, 0));
		rellekkaArea = new Zone(new WorldPoint(2616, 3728, 0), new WorldPoint(2754, 3607, 0));
		mineArea = new Zone(new WorldPoint(2373, 10234, 0), new WorldPoint(2429, 10179, 0));
		blastArea = new Zone(new WorldPoint(1933, 4975, 0), new WorldPoint(1958, 4955, 0));
	}

	public void setupSteps()
	{
		tpTroll = new DetailedQuestStep(this,
			"Teleport to Trollheim.", lawRune.quantity(2), fireRune.quantity(2), normalBook);
		catchKyatt = new NpcStep(this, NpcID.SABRETOOTHED_KYATT, new WorldPoint(2725, 3770, 0),
			"Place logs over a pit in the hunter area, and poke a kyatt with a teasing stick. " +
				"Jump over the pits until the kyatt falls in and loot it.", teasingStick, log, knife);
		moveToRellekka = new DetailedQuestStep(this, new WorldPoint(2659, 3671, 0),
			"Enter the Relleka province.");
		mixSuperDef = new ItemStep(this, new WorldPoint(2662, 3657, 0),
			"Mix a Super defence potion within the Fremennik Province (only near Rellekka).", cadantineUnfPot.highlighted(), whiteBerries.highlighted());
		moveToCaveGem = new ObjectStep(this, ObjectID.TUNNEL_5008, new WorldPoint(2732, 3713, 0),
			"Enter the tunnel that leads to Keldagrim. Alternatively teleport to Varrock and take a minecart near the Grand Exchange.");
		moveToRiverGem = new ObjectStep(this, ObjectID.CAVE_ENTRANCE_5973, new WorldPoint(2781, 10161, 0),
			"Go through the cave entrance.");
		moveToKeldagrimGem = new NpcStep(this, NpcID.DWARVEN_BOATMAN_7726, new WorldPoint(2842, 10129, 0),
			"Speak with the Dwarven Boatman to go to Keldagrim.");
		moveToKeldagrimGem.addDialogStep("Yes, please take me.");
		moveToKeldagrimVarrockGem = new ObjectStep(this, ObjectID.TRAPDOOR_16168, new WorldPoint(3140, 3504, 0),
			"Enter the trapdoor near the Grand Exchange.");
		moveToCaveBlast = new ObjectStep(this, ObjectID.TUNNEL_5008, new WorldPoint(2732, 3713, 0),
			"Enter the tunnel that leads to Keldagrim. Alternatively teleport to Varrock and take a minecart near the Grand Exchange.");
		moveToRiverBlast = new ObjectStep(this, ObjectID.CAVE_ENTRANCE_5973, new WorldPoint(2781, 10161, 0),
			"Go through the cave entrance.");
		moveToKeldagrimBlast = new NpcStep(this, NpcID.DWARVEN_BOATMAN_7726, new WorldPoint(2842, 10129, 0),
			"Speak with the Dwarven Boatman to go to Keldagrim.");
		moveToKeldagrimBlast.addDialogStep("Yes, please take me.");
		moveToKeldagrimVarrockBlast = new ObjectStep(this, ObjectID.TRAPDOOR_16168, new WorldPoint(3140, 3504, 0),
			"Enter the trapdoor near the Grand Exchange.");
		stealGem = new ObjectStep(this, ObjectID.GEM_STALL_6162, new WorldPoint(2888, 10211, 0),
			"Steal from the gem stall.");
		moveToNeitiznot = new NpcStep(this, NpcID.MARIA_GUNNARS_1883, new WorldPoint(2644, 3710, 0),
			"Speak with Maria Gunnars to travel to Neitiznot.");
		craftShield = new ObjectStep(this, ObjectID.WOODCUTTING_STUMP, new WorldPoint(2342, 3807, 0),
			"Craft a shield on the woodcutting stump.", axe, arcticLog.quantity(2), hammer, rope, bronzeNail);
		craftShield.addIcon(ItemID.NEITIZNOT_SHIELD);
		moveToJatizso = new NpcStep(this, NpcID.MORD_GUNNARS, new WorldPoint(2644, 3709, 0),
			"Speak with Mord Gunnars to go to Jatizso.");
		moveToJatizso.addDialogStep("Can you ferry me to Jatizso?");
		moveToMine = new ObjectStep(this, ObjectID.STAIRCASE_21455, new WorldPoint(2398, 3813, 0),
			"Go down the staircase.");
		mineAddy = new ObjectStep(this, 11374, new WorldPoint(2402, 10189, 0),
			"Mine 5 Adamantite ores.", pickaxe);
		mineAddy.addIcon(ItemID.RUNE_PICKAXE);
		moveToMisc = new NpcStep(this, NpcID.SAILOR_3936, new WorldPoint(2630, 3692, 0),
			"Speak to the sailor to go to Miscellania.");
		miscSupport = new ObjectStep(this, 15084, new WorldPoint(2527, 3849, 0),
			"Rake the herb and flax patch until 100% support.", true, rake);
		miscSupport.addAlternateObjects(15079);
		tpWaterbirth = new DetailedQuestStep(this,
			"Teleport to Waterbirth.", waterRune.quantity(1), astralRune.quantity(2), lawRune2.quantity(1), lunarBook);
		moveToBlast = new ObjectStep(this, ObjectID.STAIRS_9084, new WorldPoint(2930, 10197, 0),
			"Enter the blast furnace.");
		freeBlast = new NpcStep(this, NpcID.BLAST_FURNACE_FOREMAN, new WorldPoint(1942, 4958, 0),
			"Speak with the Foreman.");
		freeBlast.addDialogSteps("What?", "Can I use the furnace to smelt ore?", "I have level 60!");

		claimReward = new NpcStep(this, NpcID.THORODIN_5526, new WorldPoint(2658, 3627, 0),
			"Talk to Thorodin south of Rellekka to claim your reward!");
		claimReward.addDialogStep("I have a question about my Achievement Diary.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(rake, pickaxe, axe, arcticLog.quantity(2), hammer, rope, bronzeNail,
			cadantineUnfPot, whiteBerries, teasingStick, log, knife, fireRune.quantity(2), lawRune.quantity(3),
			astralRune.quantity(2), waterRune.quantity(1));
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new SkillRequirement(Skill.HERBLORE, 66, true));
		req.add(new SkillRequirement(Skill.HUNTER, 55, true));
		req.add(new SkillRequirement(Skill.MAGIC, 72));
		req.add(new SkillRequirement(Skill.MINING, 70, true));
		req.add(new SkillRequirement(Skill.SMITHING, 60, false));
		req.add(new SkillRequirement(Skill.THIEVING, 75, true));
		req.add(new SkillRequirement(Skill.WOODCUTTING, 56, true));
		req.add(new ItemRequirement("Normal and Lunar spellbooks", -1, -1));


		req.add(eadgarsRuse);
		req.add(lunarDiplomacy);
		req.add(giantDwarf);
		req.add(fremIsles);
		req.add(throneOfMisc);

		return req;
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
			new ItemReward("Fremennik Sea Boots (3)", ItemID.FREMENNIK_SEA_BOOTS_3, 1),
			new ItemReward("15,000 Exp. Lamp (Any skill over 50)", ItemID.ANTIQUE_LAMP, 1));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
			new UnlockReward("Ability to change enchanted lyre teleport desination to Waterbirth Island."),
			new UnlockReward("Aviansies in the God Wars Dungeon will drop noted adamantite bars."),
			new UnlockReward("Shortcut to roof on the Troll Stronghold"),
			new UnlockReward("Stony Basalt teleport destination can be changed to the roof of Troll Stronghold"),
			new UnlockReward("Access to 2 new Lunar Spells, Charge Dragonstone and Tan Leather"));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		PanelDetails miscSupportSteps = new PanelDetails("Miscellania 100% Support", Arrays.asList(moveToMisc,
			miscSupport), throneOfMisc, rake);
		miscSupportSteps.setDisplayCondition(notMiscSupport);
		miscSupportSteps.setLockingStep(miscSupportTask);
		allSteps.add(miscSupportSteps);

		PanelDetails mineAdamantiteOnJatizsoSteps = new PanelDetails("Mine Adamantite on Jatizso", Arrays.asList(moveToJatizso, moveToMine,
			mineAddy), fremIsles, new SkillRequirement(Skill.MINING, 70, true), pickaxe);
		mineAdamantiteOnJatizsoSteps.setDisplayCondition(notMineAddy);
		mineAdamantiteOnJatizsoSteps.setLockingStep(mineAddyTask);
		allSteps.add(mineAdamantiteOnJatizsoSteps);

		PanelDetails shieldOnNeitiznotSteps = new PanelDetails("Craft Shield on Neitiznot", Arrays.asList(moveToNeitiznot,
			craftShield), fremIsles, new SkillRequirement(Skill.WOODCUTTING, 56, true), axe, arcticLog.quantity(2),
			hammer, rope, bronzeNail);
		shieldOnNeitiznotSteps.setDisplayCondition(notCraftShield);
		shieldOnNeitiznotSteps.setLockingStep(craftShieldTask);
		allSteps.add(shieldOnNeitiznotSteps);

		PanelDetails superDefenseSteps = new PanelDetails("Mix a Super Defense", Arrays.asList(moveToRellekka,
			mixSuperDef),
			new SkillRequirement(Skill.HERBLORE, 66, true), cadantineUnfPot, whiteBerries);
		superDefenseSteps.setDisplayCondition(notMixSuperDef);
		superDefenseSteps.setLockingStep(mixSuperDefTask);
		allSteps.add(superDefenseSteps);

		PanelDetails kyattSteps = new PanelDetails("Catch a saber-toothed kyatt", Collections.singletonList(catchKyatt),
			new SkillRequirement(Skill.HUNTER, 55, true), teasingStick, log, knife);
		kyattSteps.setDisplayCondition(notCatchKyatt);
		kyattSteps.setLockingStep(catchKyattTask);
		allSteps.add(kyattSteps);

		PanelDetails gemStallSteps = new PanelDetails("Steal from Gem Stall", Arrays.asList(moveToCaveGem, moveToRiverGem,
			moveToKeldagrimGem, moveToKeldagrimVarrockGem, stealGem), giantDwarf, new SkillRequirement(Skill.THIEVING, 75, true));
		gemStallSteps.setDisplayCondition(notStealGem);
		gemStallSteps.setLockingStep(stealGemTask);
		allSteps.add(gemStallSteps);

		PanelDetails freeBlastFurnaceSteps = new PanelDetails("Free Blast Furnace", Arrays.asList(moveToCaveBlast,
			moveToRiverBlast, moveToKeldagrimBlast, moveToKeldagrimVarrockBlast, moveToBlast, freeBlast), giantDwarf,
			new SkillRequirement(Skill.SMITHING, 60, false));
		freeBlastFurnaceSteps.setDisplayCondition(notFreeBlast);
		freeBlastFurnaceSteps.setLockingStep(freeBlastTask);
		allSteps.add(freeBlastFurnaceSteps);

		PanelDetails teleportToTrollheimSteps = new PanelDetails("Teleport to Trollheim", Collections.singletonList(tpTroll),
			eadgarsRuse, new SkillRequirement(Skill.MAGIC, 61), normalBook, fireRune.quantity(2), lawRune.quantity(2));
		teleportToTrollheimSteps.setDisplayCondition(notTPTroll);
		teleportToTrollheimSteps.setLockingStep(tpTrollTask);
		allSteps.add(teleportToTrollheimSteps);

		PanelDetails teleportToWaterbirthSteps = new PanelDetails("Teleport to Waterbirth", Collections.singletonList(tpWaterbirth),
			lunarDiplomacy, new SkillRequirement(Skill.MAGIC, 72), lunarBook, waterRune.quantity(1), astralRune.quantity(2), lawRune2.quantity(1));
		teleportToWaterbirthSteps.setDisplayCondition(notTPWaterbirth);
		teleportToWaterbirthSteps.setLockingStep(tpWaterbirthTask);
		allSteps.add(teleportToWaterbirthSteps);

		PanelDetails finishOffSteps = new PanelDetails("Finishing off", Collections.singletonList(claimReward));
		allSteps.add(finishOffSteps);

		return allSteps;
	}
}
