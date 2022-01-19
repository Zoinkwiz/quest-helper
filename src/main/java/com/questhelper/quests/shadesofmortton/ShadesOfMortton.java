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
package com.questhelper.quests.shadesofmortton;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.conditional.ObjectCondition;
import com.questhelper.requirements.util.Operation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.SHADES_OF_MORTTON
)
public class ShadesOfMortton extends BasicQuestHelper
{
	//Items Required
	ItemRequirement combatGear, tarrominUnf2, tarrominUnfHighlighted, tinderbox, ashes2, ashesHighlighted, coins5000, hammerOrFlam, log, pyreLog,
		serum207Highlighted, serum208, sacredOilHighlighted, oliveOil, timber5, swampPaste25, lime5, diary, loar5, loar, serum207, oliveOilHighlighted, logHighlighted;

	//Items Recommended
	ItemRequirement morttonTele, food, flamHammer, flamtaerBracelet;

	Requirement razmirePartlyCured, ulsquirePartlyCured, repairedTemple, litFire, has20Sanctity, curedRazmire, curedUlsquire;

	QuestStep searchShelf, readDiary, addAshes, use207OnRazmire, talkToRazmire, kill5Shades, kill4Shades, kill3Shades, kill2Shades, kill1Shades, use207OnRazmireAgain, talkToRazmireAgain, buyTimberLimeAndSwamp,
		use207OnUlsquire, talkToUlsquire, talkToUlsquireAgain, repairTemple, lightAltar, useOilOnFlame, use207OnFlame, useOilOnLog, burnCorpse, repairTo20Sanctity, use208OnRazmire, use208OnUlsquire, talkToUlsquireToFinish;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupItemRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		ConditionalStep goReadDiary = new ConditionalStep(this, searchShelf);
		goReadDiary.addStep(diary.alsoCheckBank(questBank), readDiary);
		steps.put(0, goReadDiary);

		steps.put(5, addAshes);

		ConditionalStep goTalkToRazmire = new ConditionalStep(this, use207OnRazmire);
		goTalkToRazmire.addStep(razmirePartlyCured, talkToRazmire);
		steps.put(10, goTalkToRazmire);

		steps.put(15, kill5Shades);
		steps.put(20, kill4Shades);
		steps.put(25, kill3Shades);
		steps.put(30, kill2Shades);
		steps.put(35, kill1Shades);

		ConditionalStep goTalkToRazmireAgain = new ConditionalStep(this, use207OnRazmireAgain);
		goTalkToRazmireAgain.addStep(razmirePartlyCured, talkToRazmireAgain);
		steps.put(40, goTalkToRazmireAgain);

		ConditionalStep goTalkToUlsquire = new ConditionalStep(this, use207OnUlsquire);
		goTalkToUlsquire.addStep(ulsquirePartlyCured, talkToUlsquire);
		steps.put(45, goTalkToUlsquire);

		ConditionalStep goTalkToUlsquireAgain = new ConditionalStep(this, use207OnUlsquire);
		goTalkToUlsquireAgain.addStep(ulsquirePartlyCured, talkToUlsquireAgain);
		steps.put(47, goTalkToUlsquireAgain);

		ConditionalStep makeSacredOil = new ConditionalStep(this, repairTemple);
		makeSacredOil.addStep(new Conditions(litFire, has20Sanctity, sacredOilHighlighted), use207OnFlame);
		makeSacredOil.addStep(new Conditions(litFire, has20Sanctity), useOilOnFlame);
		makeSacredOil.addStep(litFire, repairTo20Sanctity);
		makeSacredOil.addStep(repairedTemple, lightAltar);
		steps.put(50, makeSacredOil);
		steps.put(55, makeSacredOil);
		steps.put(60, makeSacredOil);

		ConditionalStep saveRemains = new ConditionalStep(this, repairTemple);
		saveRemains.addStep(new Conditions(serum208.alsoCheckBank(questBank), sacredOilHighlighted), useOilOnLog);
		saveRemains.addStep(new Conditions(litFire, has20Sanctity, sacredOilHighlighted), use207OnFlame);
		saveRemains.addStep(new Conditions(litFire, has20Sanctity), useOilOnFlame);
		saveRemains.addStep(litFire, repairTo20Sanctity);
		saveRemains.addStep(repairedTemple, lightAltar);
		steps.put(65, saveRemains);

		steps.put(70, burnCorpse);
		steps.put(75, burnCorpse);

		ConditionalStep goCureProperly = new ConditionalStep(this, use208OnRazmire);
		goCureProperly.addStep(new Conditions(curedRazmire, curedUlsquire), talkToUlsquireToFinish);
		goCureProperly.addStep(curedRazmire, use208OnUlsquire);
		steps.put(80, goCureProperly);

		return steps;
	}

	public void setupItemRequirements()
	{
		combatGear = new ItemRequirement("Combat gear", -1, -1);
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
		tarrominUnf2 = new ItemRequirement("Tarromin potion (unf)", ItemID.TARROMIN_POTION_UNF, 2);
		tarrominUnfHighlighted = new ItemRequirement("Tarromin potion (unf)", ItemID.TARROMIN_POTION_UNF);
		tarrominUnfHighlighted.setHighlightInInventory(true);
		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX);
		ashes2 = new ItemRequirement("Ashes", ItemID.ASHES, 2);
		ashesHighlighted = new ItemRequirement("Ashes", ItemID.ASHES);
		ashesHighlighted.setHighlightInInventory(true);
		coins5000 = new ItemRequirement("Coins, more if you want to buy a Flamtaer Hammer", ItemCollections.getCoins(), 5000);
		hammerOrFlam = new ItemRequirement("Hammer or Flamtaer Hammer", ItemCollections.getHammer());
		hammerOrFlam.addAlternates(ItemID.FLAMTAER_HAMMER);
		flamHammer = new ItemRequirement("Flamtaer hammer", ItemID.FLAMTAER_HAMMER);
		flamHammer.setTooltip("This speeds up the repair section of the quest considerably");
		morttonTele = new ItemRequirement("A Mort'ton teleport or Barrows teleport tablet", ItemID.MORTTON_TELEPORT);
		morttonTele.addAlternates(ItemID.BARROWS_TELEPORT);
		food = new ItemRequirement("Food", ItemCollections.getGoodEatingFood(), -1);

		flamtaerBracelet = new ItemRequirement("Flamtaer bracelet", ItemID.FLAMTAER_BRACELET);
		flamtaerBracelet.setTooltip("This speeds up the repair section of the quest considerably");
		log = new ItemRequirement("A log whose pyre version you can burn", ItemID.LOGS);
		log.addAlternates(ItemID.OAK_LOGS, ItemID.MAPLE_LOGS, ItemID.WILLOW_LOGS, ItemID.YEW_LOGS, ItemID.MAGIC_LOGS, ItemID.REDWOOD_LOGS);
		logHighlighted = new ItemRequirement("A log whose pyre version you can burn", ItemID.LOGS);
		logHighlighted.addAlternates(ItemID.OAK_LOGS, ItemID.MAPLE_LOGS, ItemID.WILLOW_LOGS, ItemID.YEW_LOGS, ItemID.MAGIC_LOGS, ItemID.REDWOOD_LOGS);
		logHighlighted.setHighlightInInventory(true);

		pyreLog = new ItemRequirement("A pyre log you can burn", ItemID.PYRE_LOGS);
		pyreLog.addAlternates(ItemID.OAK_PYRE_LOGS, ItemID.MAPLE_PYRE_LOGS, ItemID.WILLOW_PYRE_LOGS, ItemID.YEW_PYRE_LOGS, ItemID.MAGIC_PYRE_LOGS,
			ItemID.REDWOOD_PYRE_LOGS);
		pyreLog.setHighlightInInventory(true);
		serum207Highlighted = new ItemRequirement("Serum 207", ItemID.SERUM_207_1);
		serum207Highlighted.setTooltip("You can make more with a tarromin potion (unf) and some ashes");
		serum207Highlighted.setHighlightInInventory(true);
		serum207Highlighted.addAlternates(ItemID.SERUM_207_2, ItemID.SERUM_207_3, ItemID.SERUM_207_4);

		serum207 = new ItemRequirement("Serum 207", ItemID.SERUM_207_1);
		serum207.setTooltip("You can make more with a tarromin potion (unf) and some ashes");
		serum207.addAlternates(ItemID.SERUM_207_2, ItemID.SERUM_207_3, ItemID.SERUM_207_4);

		serum208 = new ItemRequirement("Serum 208", ItemID.SERUM_208_1);
		serum208.setHighlightInInventory(true);
		serum208.addAlternates(ItemID.SERUM_208_2, ItemID.SERUM_208_3, ItemID.SERUM_208_4);
		sacredOilHighlighted = new ItemRequirement("Sacred oil", ItemID.SACRED_OIL1);
		sacredOilHighlighted.setHighlightInInventory(true);
		sacredOilHighlighted.addAlternates(ItemID.SACRED_OIL2, ItemID.SACRED_OIL3, ItemID.SACRED_OIL4);

		oliveOil = new ItemRequirement("Olive oil", ItemID.OLIVE_OIL4);
		oliveOil.addAlternates(ItemID.OLIVE_OIL2, ItemID.OLIVE_OIL3, ItemID.OLIVE_OIL4);

		oliveOilHighlighted = new ItemRequirement("Olive oil", ItemID.OLIVE_OIL4);
		oliveOilHighlighted.addAlternates(ItemID.OLIVE_OIL2, ItemID.OLIVE_OIL3, ItemID.OLIVE_OIL4);
		oliveOilHighlighted.setHighlightInInventory(true);

		swampPaste25 = new ItemRequirement("Swamp paste", ItemID.SWAMP_PASTE, 25);
		timber5 = new ItemRequirement("Timber beam", ItemID.TIMBER_BEAM, 5);
		lime5 = new ItemRequirement("Limestone brick", ItemID.LIMESTONE_BRICK, 5);

		diary = new ItemRequirement("Diary", ItemID.DIARY_3395);
		diary.setHighlightInInventory(true);
		loar = new ItemRequirement("Loar remains", ItemID.LOAR_REMAINS);
		loar.setHighlightInInventory(true);
		loar5 = new ItemRequirement("Loar remains", ItemID.LOAR_REMAINS, 5);
	}

	public void setupConditions()
	{
		has20Sanctity = new VarplayerRequirement(341, 20);

		razmirePartlyCured = new VarplayerRequirement(340, true, 3);
		curedRazmire = new VarplayerRequirement(340, true, 6); //64
		ulsquirePartlyCured = new VarplayerRequirement(340, true, 1);
		curedUlsquire = new VarplayerRequirement(340, true, 5);

		repairedTemple = new VarplayerRequirement(343, 100);
		has20Sanctity = new VarplayerRequirement(345, 20, Operation.GREATER_EQUAL);
		litFire = new ObjectCondition(ObjectID.FLAMING_FIRE_ALTAR, new WorldPoint(3506, 3316, 0));
	}

	public void setupSteps()
	{
		searchShelf = new ObjectStep(this, ObjectID.SHELF_4062, new WorldPoint(3481, 3279, 0), "Search the shelf in the south building of Mort'ton.");
		readDiary = new DetailedQuestStep(this, "Read the diary.", diary);
		addAshes = new DetailedQuestStep(this, "Add ashes to a tarromin potion (unf).", tarrominUnfHighlighted, ashesHighlighted);
		use207OnRazmire = new NpcStep(this, NpcID.AFFLICTEDRAZMIRE, new WorldPoint(3488, 3296, 0), "Use the serum 207 on Razmire in the north of Mort'ton.", serum207Highlighted);
		((NpcStep) (use207OnRazmire)).addAlternateNpcs(NpcID.RAZMIRE_KEELGAN);
		use207OnRazmire.addIcon(ItemID.SERUM_207_4);
		talkToRazmire = new NpcStep(this, NpcID.AFFLICTEDRAZMIRE, new WorldPoint(3488, 3296, 0), "Talk to Razmire in the north of Mort'ton.", serum207Highlighted);
		talkToRazmire.addDialogSteps("What are all these shadowy creatures?", "Yes, I'll dispatch those dark and evil creatures.");
		((NpcStep) (talkToRazmire)).addAlternateNpcs(NpcID.RAZMIRE_KEELGAN);

		kill5Shades = new NpcStep(this, NpcID.LOAR_SHADOW, new WorldPoint(3488, 3287, 0), "Kill 5 Loar Shades and pick up their remains.", true, loar5);
		((NpcStep) (kill5Shades)).addAlternateNpcs(NpcID.LOAR_SHADE);
		kill4Shades = new NpcStep(this, NpcID.LOAR_SHADOW, new WorldPoint(3488, 3287, 0), "Kill 4 Loar Shades and pick up their remains.", true, loar5);
		((NpcStep) (kill4Shades)).addAlternateNpcs(NpcID.LOAR_SHADE);
		kill3Shades = new NpcStep(this, NpcID.LOAR_SHADOW, new WorldPoint(3488, 3287, 0), "Kill 3 Loar Shades and pick up their remains.", true, loar5);
		((NpcStep) (kill3Shades)).addAlternateNpcs(NpcID.LOAR_SHADE);
		kill2Shades = new NpcStep(this, NpcID.LOAR_SHADOW, new WorldPoint(3488, 3287, 0), "Kill 2 Loar Shades and pick up their remains.", true, loar5);
		((NpcStep) (kill2Shades)).addAlternateNpcs(NpcID.LOAR_SHADE);
		kill1Shades = new NpcStep(this, NpcID.LOAR_SHADOW, new WorldPoint(3488, 3287, 0), "Kill 1 Loar Shades and pick up their remains.", true, loar5);
		((NpcStep) (kill1Shades)).addAlternateNpcs(NpcID.LOAR_SHADE);
		kill5Shades.addSubSteps(kill1Shades, kill2Shades, kill3Shades, kill4Shades);

		use207OnRazmireAgain = new NpcStep(this, NpcID.AFFLICTEDRAZMIRE, new WorldPoint(3488, 3296, 0), "Use the serum 207 on Razmire in the north of Mort'ton.", serum207Highlighted, loar5);
		((NpcStep) (use207OnRazmireAgain)).addAlternateNpcs(NpcID.RAZMIRE_KEELGAN);
		use207OnRazmireAgain.addIcon(ItemID.SERUM_207_4);
		talkToRazmireAgain = new NpcStep(this, NpcID.AFFLICTEDRAZMIRE, new WorldPoint(3488, 3296, 0), "Talk to Razmire in the north of Mort'ton.", serum207Highlighted, loar5);
		((NpcStep) (talkToRazmireAgain)).addAlternateNpcs(NpcID.RAZMIRE_KEELGAN);
		buyTimberLimeAndSwamp = new NpcStep(this, NpcID.AFFLICTEDRAZMIRE, new WorldPoint(3488, 3296, 0), "Buy 5 timber beams, 5 limestone bricks, and 25 swamp paste from Razmire's builders' store.", timber5, lime5, swampPaste25);
		buyTimberLimeAndSwamp.addDialogSteps("Can you open a store for me?", "Can I see the building store please?");
		((NpcStep) (buyTimberLimeAndSwamp)).addAlternateNpcs(NpcID.RAZMIRE_KEELGAN);

		use207OnUlsquire = new NpcStep(this, NpcID.AFFLICTEDULSQUIRE, new WorldPoint(3496, 3289, 0),
			"Buy some olive oil from Razmire's general store, and 5 timber beams, 5 limestone bricks, and 25 swamp paste from their builders' store. Afterwards, use some serum 207 on Ulsquire.", serum207Highlighted);
		use207OnUlsquire.addIcon(ItemID.SERUM_207_4);
		((NpcStep) (use207OnUlsquire)).addAlternateNpcs(NpcID.ULSQUIRE_SHAUNCY);
		use207OnUlsquire.addDialogSteps("Can you open a store for me?", "Can I see the general store please?");

		talkToUlsquire = new NpcStep(this, NpcID.AFFLICTEDULSQUIRE, new WorldPoint(3496, 3289, 0), "Talk to Ulsquire in the east of Mort'ton.");
		((NpcStep) (talkToUlsquire)).addAlternateNpcs(NpcID.ULSQUIRE_SHAUNCY);
		talkToUlsquireAgain = new NpcStep(this, NpcID.AFFLICTEDULSQUIRE, new WorldPoint(3496, 3289, 0), "Talk to Ulsquire again.");
		talkToUlsquireAgain.addDialogSteps("What can you tell me about that temple?");
		((NpcStep) (talkToUlsquireAgain)).addAlternateNpcs(NpcID.ULSQUIRE_SHAUNCY);

		repairTemple = new DetailedQuestStep(this, new WorldPoint(3506, 3316, 0), "Repair the temple north east of Mort'ton. Do this on World 377 to make it faster and easier.", oliveOil, serum207, hammerOrFlam, lime5, timber5, swampPaste25);
		repairTo20Sanctity = new DetailedQuestStep(this, new WorldPoint(3506, 3316, 0), "Keep repairing the temple until 20 sanctity.", oliveOil, serum207, hammerOrFlam, lime5, timber5, swampPaste25);

		lightAltar = new ObjectStep(this, ObjectID.FIRE_ALTAR, new WorldPoint(3506, 3316, 0), "Light the fire altar", tinderbox);
		useOilOnFlame = new ObjectStep(this, ObjectID.FLAMING_FIRE_ALTAR, new WorldPoint(3506, 3316, 0), "Use the olive oil on the temple's fire.", oliveOilHighlighted);
		useOilOnFlame.addIcon(ItemID.OLIVE_OIL4);
		use207OnFlame = new ObjectStep(this, ObjectID.FLAMING_FIRE_ALTAR, new WorldPoint(3506, 3316, 0), "Use a serum 207 (with 2 or more doses) on the temple's fire.", serum207Highlighted);
		use207OnFlame.addIcon(ItemID.SERUM_207_4);
		useOilOnLog = new DetailedQuestStep(this, "Use the sacred oil on a log.", logHighlighted, sacredOilHighlighted);
		burnCorpse = new ObjectStep(this, ObjectID.FUNERAL_PYRE, new WorldPoint(3507, 3277, 0), "Use the pyre log in a funeral pyre, then a loar remain. Finally, burn it.", pyreLog, loar);
		use208OnRazmire = new NpcStep(this, NpcID.AFFLICTEDRAZMIRE, new WorldPoint(3488, 3296, 0), "Use the serum 208 on Razmire in the north of Mort'ton.", serum208);
		use208OnRazmire.addIcon(ItemID.SERUM_208_4);
		((NpcStep) (use207OnRazmire)).addAlternateNpcs(NpcID.RAZMIRE_KEELGAN);
		use208OnUlsquire = new NpcStep(this, NpcID.AFFLICTEDULSQUIRE, new WorldPoint(3496, 3289, 0),
			"Use some serum 208 on Ulsquire in the east of Mort'ton.", serum208);
		use208OnUlsquire.addIcon(ItemID.SERUM_208_4);
		((NpcStep) (talkToUlsquire)).addAlternateNpcs(NpcID.ULSQUIRE_SHAUNCY);

		talkToUlsquireToFinish = new NpcStep(this, NpcID.AFFLICTEDULSQUIRE, new WorldPoint(3496, 3289, 0), "Talk to Ulsquire to finish the quest.");
		((NpcStep) (talkToUlsquireToFinish)).addAlternateNpcs(NpcID.ULSQUIRE_SHAUNCY);

	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(tarrominUnf2, tinderbox, log, ashes2, coins5000, hammerOrFlam);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(combatGear, morttonTele, food, flamHammer, flamtaerBracelet);
	}

	@Override
	public List<String> getNotes()
	{
		return Arrays.asList("Whilst in Mort Myre, the Ghasts will occasionally rot the food in your inventory and steal charges from your Druid Pouch.", "It's strongly recommended to bring a Flamtaer hammer and Flamtaer bracelets, as they speed up the temple repair section massively.");
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Collections.singletonList("5 Loar Shades (level 40)");
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new QuestRequirement(QuestHelperQuest.PRIEST_IN_PERIL, QuestState.FINISHED));
		req.add(new SkillRequirement(Skill.CRAFTING, 20));
		req.add(new SkillRequirement(Skill.HERBLORE, 15, true));
		req.add(new SkillRequirement(Skill.FIREMAKING, 5));
		return req;
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
				new ExperienceReward(Skill.CRAFTING, 2000),
				new ExperienceReward(Skill.HERBLORE, 2000));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Collections.singletonList(new UnlockReward("Ability to play the Shades of Mort'ton Minigame"));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Investigating the town", Arrays.asList(searchShelf, readDiary, addAshes), tarrominUnf2, ashes2, tinderbox, log, coins5000, hammerOrFlam));
		allSteps.add(new PanelDetails("Helping Razmire",
			Arrays.asList(use207OnRazmire, talkToRazmire, kill5Shades, use207OnRazmireAgain, talkToRazmireAgain), serum207Highlighted, tinderbox, log, coins5000, hammerOrFlam));
		allSteps.add(new PanelDetails("Helping Ulsquire",
			Arrays.asList(use207OnUlsquire, talkToUlsquire, talkToUlsquireAgain, repairTemple, lightAltar, repairTo20Sanctity, useOilOnFlame, use207OnFlame, useOilOnLog, burnCorpse, use208OnRazmire, use208OnUlsquire, talkToUlsquireToFinish), serum207Highlighted, tinderbox, log, coins5000, hammerOrFlam));
		return allSteps;
	}
}
