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
package com.questhelper.quests.naturespirit;

import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.ChatMessageRequirement;
import com.questhelper.requirements.item.ItemOnTileRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.conditional.NpcCondition;
import com.questhelper.requirements.widget.WidgetTextRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.NATURE_SPIRIT
)
public class NatureSpirit extends BasicQuestHelper
{
	//Items Required
	ItemRequirement ghostspeak, silverSickle, washingBowl, mirror, journal, druidicSpell, druidPouch, blessedSickle,
		spellCard, mirrorHighlighted, journalHighlighted, mushroom, mushroomHighlighted, druidPouchFull;

	//Items Recommended
	ItemRequirement combatGear, salveTele;

	Requirement inUnderground, fillimanNearby, mirrorNearby, usedMushroom, onOrange,
		usedCard, inGrotto, natureSpiritNearby, ghastNearby;

	QuestStep goDownToDrezel, talkToDrezel, leaveDrezel, enterSwamp, tryToEnterGrotto, talkToFilliman, takeWashingBowl,
		takeMirror, useMirrorOnFilliman, searchGrotto, useJournalOnFilliman, goBackDownToDrezel, talkToDrezelForBlessing,
		castSpellAndGetMushroom, useMushroom, useSpellCard, standOnOrange, tellFillimanToCast, enterGrotto, searchAltar,
		blessSickle, fillPouches, killGhasts, killGhast, enterGrottoAgain, touchAltarAgain, talkToNatureSpiritToFinish,
		spawnFillimanForRitual, talkToFillimanInGrotto;

	//Zones
	Zone underground, orangeStone, grotto;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		ConditionalStep startQuest = new ConditionalStep(this, goDownToDrezel);
		startQuest.addStep(inUnderground, talkToDrezel);

		steps.put(0, startQuest);

		ConditionalStep goEnterSwamp = new ConditionalStep(this, enterSwamp);
		goEnterSwamp.addStep(inUnderground, leaveDrezel);
		steps.put(1, goEnterSwamp);
		steps.put(2, goEnterSwamp);
		steps.put(3, goEnterSwamp);
		steps.put(4, goEnterSwamp);
		steps.put(5, goEnterSwamp);

		ConditionalStep goTalkToFilliman = new ConditionalStep(this, tryToEnterGrotto);
		goTalkToFilliman.addStep(fillimanNearby, talkToFilliman);
		steps.put(10, goTalkToFilliman);
		steps.put(15, goTalkToFilliman);

		ConditionalStep showFillimanReflection = new ConditionalStep(this, takeWashingBowl);
		showFillimanReflection.addStep(new Conditions(mirror, fillimanNearby), useMirrorOnFilliman);
		showFillimanReflection.addStep(mirror, tryToEnterGrotto);
		showFillimanReflection.addStep(mirrorNearby, takeMirror);
		steps.put(20, showFillimanReflection);

		ConditionalStep goGetJournal = new ConditionalStep(this, searchGrotto);
		goGetJournal.addStep(new Conditions(journal, fillimanNearby), useJournalOnFilliman);
		goGetJournal.addStep(journal, tryToEnterGrotto);
		steps.put(25, goGetJournal);

		ConditionalStep goOfferHelp = new ConditionalStep(this, tryToEnterGrotto);
		goOfferHelp.addStep(fillimanNearby, useJournalOnFilliman);
		steps.put(30, goOfferHelp);

		ConditionalStep getBlessed = new ConditionalStep(this, goBackDownToDrezel);
		getBlessed.addStep(inUnderground, talkToDrezelForBlessing);
		steps.put(35, getBlessed);

		ConditionalStep performRitual = new ConditionalStep(this, castSpellAndGetMushroom);
		performRitual.addStep(new Conditions(usedMushroom, usedCard, fillimanNearby, onOrange), tellFillimanToCast);
		performRitual.addStep(new Conditions(usedMushroom, usedCard, fillimanNearby), standOnOrange);
		performRitual.addStep(new Conditions(usedMushroom, usedCard), spawnFillimanForRitual);
		performRitual.addStep(usedMushroom, useSpellCard);
		performRitual.addStep(mushroom, useMushroom);
		steps.put(40, performRitual);
		steps.put(45, performRitual);
		steps.put(50, performRitual);
		steps.put(55, performRitual);

		ConditionalStep goTalkInGrotto = new ConditionalStep(this, enterGrotto);
		goTalkInGrotto.addStep(new Conditions(inGrotto, fillimanNearby), talkToFillimanInGrotto);
		goTalkInGrotto.addStep(inGrotto, searchAltar);
		steps.put(60, goTalkInGrotto);

		ConditionalStep goBlessSickle = new ConditionalStep(this, enterGrotto);
		goBlessSickle.addStep(new Conditions(inGrotto, natureSpiritNearby), blessSickle);
		goBlessSickle.addStep(inGrotto, searchAltar);
		steps.put(65, goBlessSickle);
		steps.put(70, goBlessSickle);

		ConditionalStep goKillGhasts = new ConditionalStep(this, fillPouches);
		// TODO: Fix ghast changing form not counting towards becoming nearby
		goKillGhasts.addStep(ghastNearby, killGhast);
		goKillGhasts.addStep(druidPouchFull, killGhasts);
		steps.put(75, goKillGhasts);
		steps.put(80, goKillGhasts);
		steps.put(85, goKillGhasts);
		steps.put(90, goKillGhasts);
		steps.put(95, goKillGhasts);
		steps.put(100, goKillGhasts);

		ConditionalStep finishOff = new ConditionalStep(this, enterGrottoAgain);
		finishOff.addStep(new Conditions(inGrotto, natureSpiritNearby), talkToNatureSpiritToFinish);
		finishOff.addStep(inGrotto, touchAltarAgain);
		steps.put(105, finishOff);

		return steps;
	}

	@Override
	public void setupRequirements()
	{
		ghostspeak = new ItemRequirement("Ghostspeak amulet", ItemID.GHOSTSPEAK_AMULET, 1, true).isNotConsumed();
		silverSickle = new ItemRequirement("Silver sickle", ItemID.SILVER_SICKLE);
		washingBowl = new ItemRequirement("Washing bowl", ItemID.WASHING_BOWL);
		mirror = new ItemRequirement("Mirror", ItemID.MIRROR);
		mirrorHighlighted = new ItemRequirement("Mirror", ItemID.MIRROR);
		mirrorHighlighted.setHighlightInInventory(true);
		journal = new ItemRequirement("Journal", ItemID.JOURNAL);
		journalHighlighted = new ItemRequirement("Journal", ItemID.JOURNAL);
		journalHighlighted.setHighlightInInventory(true);
		druidicSpell = new ItemRequirement("Druidic spell", ItemID.DRUIDIC_SPELL);
		druidicSpell.setTooltip("You can get another from Filliman");
		druidPouch = new ItemRequirement("Druidic pouch", ItemID.DRUID_POUCH);
		druidPouch.setTooltip("You can get another from the Grotto");
		druidPouchFull = new ItemRequirement("Druidic pouch", ItemID.DRUID_POUCH_2958);
		blessedSickle = new ItemRequirement("Silver sickle (b)", ItemID.SILVER_SICKLE_B);
		blessedSickle.setTooltip("You can bless another silver sickle with the nature spirit");
		spellCard = new ItemRequirement("A used spell or druidic spell", ItemID.A_USED_SPELL);
		spellCard.addAlternates(ItemID.DRUIDIC_SPELL);
		spellCard.setTooltip("You can get another spell from Filliman");
		mushroom = new ItemRequirement("Mort myre fungus", ItemID.MORT_MYRE_FUNGUS);
		mushroomHighlighted = new ItemRequirement("Mort myre fungus", ItemID.MORT_MYRE_FUNGUS);
		mushroomHighlighted.setHighlightInInventory(true);
		salveTele = new ItemRequirement("Salve Graveyard Teleports", ItemID.SALVE_GRAVEYARD_TELEPORT, 2);
		combatGear = new ItemRequirement("Combat gear to kill the ghasts", -1, -1).isNotConsumed();
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
	}

	public void loadZones()
	{
		underground = new Zone(new WorldPoint(3402, 9880, 0), new WorldPoint(3443, 9907, 0));
		orangeStone = new Zone(new WorldPoint(3440, 3335, 0), new WorldPoint(3440, 3335, 0));
		grotto = new Zone(new WorldPoint(3435, 9733, 0), new WorldPoint(3448, 9746, 0));
	}

	public void setupConditions()
	{
		inUnderground = new ZoneRequirement(underground);
		onOrange = new ZoneRequirement(orangeStone);
		inGrotto = new ZoneRequirement(grotto);
		fillimanNearby = new NpcCondition(NpcID.FILLIMAN_TARLOCK);
		natureSpiritNearby = new NpcCondition(NpcID.NATURE_SPIRIT);
		mirrorNearby = new ItemOnTileRequirement(mirror);
		usedMushroom = new Conditions(true, LogicType.OR, new ChatMessageRequirement("The stone seems to absorb the fungus."),
			new WidgetTextRequirement(229, 1, "nature symbol<br>scratched into it. This stone seems complete in some way."),
			new WidgetTextRequirement(119, 3, true, "Mort Myre Fungi was absorbed"));
		usedCard = new Conditions(true, LogicType.OR, new ChatMessageRequirement("The stone seems to absorb the used spell scroll."),
			new ChatMessageRequirement("The stone seems to absorb the spell scroll."),
			new WidgetTextRequirement(229, 1, "spirit symbol<br>scratched into it. This stone seems to be complete"),
			new WidgetTextRequirement(119, 3, true, "spell scroll was absorbed"));

		ghastNearby = new NpcCondition(NpcID.GHAST_946);
	}

	public void setupSteps()
	{
		goDownToDrezel = new ObjectStep(this, ObjectID.TRAPDOOR_1579, new WorldPoint(3405, 3507, 0), "Talk to Drezel under the Paterdomus Temple.");
		goDownToDrezel.addDialogSteps("Well, what is it, I may be able to help?", "Yes.");
		((ObjectStep) (goDownToDrezel)).addAlternateObjects(ObjectID.TRAPDOOR_1581);
		talkToDrezel = new NpcStep(this, NpcID.DREZEL, new WorldPoint(3439, 9896, 0), "Talk to Drezel under the Paterdomus Temple.");
		talkToDrezel.addSubSteps(goDownToDrezel);
		leaveDrezel = new ObjectStep(this, ObjectID.HOLY_BARRIER, new WorldPoint(3440, 9886, 0), "Enter the Mort Myre from the north gate.");
		enterSwamp = new ObjectStep(this, ObjectID.GATE_3506, new WorldPoint(3444, 3458, 0), "Enter the Mort Myre from the north gate.", ghostspeak);
		tryToEnterGrotto = new ObjectStep(this, ObjectID.GROTTO, new WorldPoint(3440, 3337, 0), "Attempt to enter the Grotto in the south of Mort Myre.", ghostspeak);
		tryToEnterGrotto.addDialogStep("How long have you been a ghost?");
		talkToFilliman = new NpcStep(this, NpcID.FILLIMAN_TARLOCK, new WorldPoint(3440, 3336, 0), "Talk to Filliman Tarlock.", ghostspeak);
		talkToFilliman.addDialogStep("How long have you been a ghost?");
		takeWashingBowl = new DetailedQuestStep(this, new WorldPoint(3437, 3337, 0), "Pick up the washing bowl.", washingBowl);
		takeMirror = new DetailedQuestStep(this, new WorldPoint(3437, 3337, 0), "Pick up the mirror.", mirror);
		useMirrorOnFilliman = new NpcStep(this, NpcID.FILLIMAN_TARLOCK, new WorldPoint(3440, 3336, 0), "Use the mirror on Filliman Tarlock.", ghostspeak, mirrorHighlighted);
		useMirrorOnFilliman.addDialogStep("How long have you been a ghost?");
		useMirrorOnFilliman.addIcon(ItemID.MIRROR);
		searchGrotto = new ObjectStep(this, ObjectID.GROTTO_TREE, new WorldPoint(3440, 3339, 0), "Right-click search the grotto tree.");
		useJournalOnFilliman = new NpcStep(this, NpcID.FILLIMAN_TARLOCK, new WorldPoint(3440, 3336, 0), "Use the journal on Filliman Tarlock.", ghostspeak, journalHighlighted);
		useJournalOnFilliman.addIcon(ItemID.JOURNAL);
		useJournalOnFilliman.addDialogStep("How can I help?");
		goBackDownToDrezel = new ObjectStep(this, ObjectID.TRAPDOOR_3432, new WorldPoint(3422, 3485, 0), "Talk to Drezel to get blessed.");
		((ObjectStep) (goBackDownToDrezel)).addAlternateObjects(ObjectID.TRAPDOOR_3433);
		talkToDrezelForBlessing = new NpcStep(this, NpcID.DREZEL, new WorldPoint(3439, 9896, 0), "Talk to Drezel under the Paterdomus Temple.");
		talkToDrezelForBlessing.addSubSteps(goBackDownToDrezel);
		talkToDrezelForBlessing.addSubSteps(goBackDownToDrezel);
		castSpellAndGetMushroom = new DetailedQuestStep(this, "Cast the druidic spell next to a rotten log in Mort Myre to grow a mushroom. Pick it. If you already have, open the quest journal to re-sync your state.", druidicSpell);
		useMushroom = new ObjectStep(this, ObjectID.STONE, new WorldPoint(3439, 3336, 0), "Use the mushroom on the brown stone outside the grotto. If you already have, search it instead.", mushroomHighlighted);
		useMushroom.addIcon(ItemID.MORT_MYRE_FUNGUS);
		useSpellCard = new ObjectStep(this, ObjectID.STONE_3529, new WorldPoint(3441, 3336, 0), "Use the used spell on the gray stone outside the grotto. If you already have, search it instead.", spellCard);
		useSpellCard.addIcon(ItemID.A_USED_SPELL);
		standOnOrange = new DetailedQuestStep(this, new WorldPoint(3440, 3335, 0), "Stand on the orange stone outside the grotto.");
		tellFillimanToCast = new NpcStep(this, NpcID.FILLIMAN_TARLOCK, new WorldPoint(3440, 3336, 0), "Tell Filliman Tarlock you're ready.", ghostspeak);
		tellFillimanToCast.addDialogStep("I think I've solved the puzzle!");
		spawnFillimanForRitual = new ObjectStep(this, ObjectID.GROTTO, new WorldPoint(3440, 3337, 0), "Attempt to enter the Grotto in the south of Mort Myre to spawn Filliman.", ghostspeak);
		spawnFillimanForRitual.addDialogStep("I think I've solved the puzzle!");
		tellFillimanToCast.addSubSteps(spawnFillimanForRitual);
		enterGrotto = new ObjectStep(this, ObjectID.GROTTO, new WorldPoint(3440, 3337, 0), "Enter the Grotto in the south of Mort Myre.", ghostspeak, silverSickle);

		searchAltar = new ObjectStep(this, ObjectID.GROTTO_3520, new WorldPoint(3442, 9741, 0), "Search the grotto inside.", ghostspeak);
		talkToFillimanInGrotto = new NpcStep(this, NpcID.FILLIMAN_TARLOCK, new WorldPoint(3441, 9738, 0), "Talk to Filliman in the grotto to bless your sickle.", ghostspeak, silverSickle);
		blessSickle = new NpcStep(this, NpcID.NATURE_SPIRIT, new WorldPoint(3441, 9738, 0), "Talk to the Nature Spirit in the grotto to bless your sickle.", ghostspeak, silverSickle);
		fillPouches = new DetailedQuestStep(this, "Right-click 'bloom' the blessed sickle next to rotten logs for mort myre fungi. Use these to fill the druid pouch.", blessedSickle);
		killGhasts = new NpcStep(this, NpcID.GHAST, "Use the filled druid pouch on a ghast to make it attackable and kill it. You'll need to kill 3.", druidPouchFull);
		killGhast = new NpcStep(this, NpcID.GHAST_946, "Kill the ghast.", druidPouchFull);
		killGhasts.addSubSteps(killGhast);
		enterGrottoAgain = new ObjectStep(this, ObjectID.GROTTO, new WorldPoint(3440, 3337, 0), "Enter the Grotto in the south of Mort Myre.", ghostspeak);
		touchAltarAgain = new ObjectStep(this, ObjectID.GROTTO_3520, new WorldPoint(3442, 9741, 0), "Search the grotto inside.", ghostspeak);
		talkToNatureSpiritToFinish = new NpcStep(this, NpcID.NATURE_SPIRIT, new WorldPoint(3441, 9738, 0), "Talk to the Nature Spirit in the grotto to finish the quest!", ghostspeak);
		talkToNatureSpiritToFinish.addSubSteps(touchAltarAgain);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(ghostspeak, silverSickle);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(salveTele, combatGear);
	}

	@Override
	public List<String> getNotes()
	{
		return Collections.singletonList("Whilst in Mort Myre, the Ghasts will occasionally rot the food in your inventory.");
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Collections.singletonList("3 Ghasts (level 30)");
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new QuestRequirement(QuestHelperQuest.THE_RESTLESS_GHOST, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.PRIEST_IN_PERIL, QuestState.FINISHED));
		return req;
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(2);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Arrays.asList(
				new ExperienceReward(Skill.CRAFTING, 3000),
				new ExperienceReward(Skill.DEFENCE, 2000),
				new ExperienceReward(Skill.HITPOINTS, 2000));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
				new UnlockReward("Access to Mort Myre Swamp"),
				new UnlockReward("Ability to fight Ghasts."));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Start the quest",
			Arrays.asList(talkToDrezel, enterSwamp, tryToEnterGrotto, talkToFilliman, takeWashingBowl,
				takeMirror, useMirrorOnFilliman, searchGrotto, useJournalOnFilliman), ghostspeak, silverSickle));
		allSteps.add(new PanelDetails("Helping Filliman",
			Arrays.asList(talkToDrezelForBlessing, castSpellAndGetMushroom, useMushroom, useSpellCard, standOnOrange,
				tellFillimanToCast, enterGrotto, searchAltar, talkToFillimanInGrotto, blessSickle), ghostspeak, silverSickle));
		allSteps.add(new PanelDetails("Killing Ghasts",
			Arrays.asList(fillPouches, killGhasts, enterGrottoAgain, talkToNatureSpiritToFinish), ghostspeak, blessedSickle));

		return allSteps;
	}
}
