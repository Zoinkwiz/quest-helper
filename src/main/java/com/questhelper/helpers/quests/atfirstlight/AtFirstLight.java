/*
 * Copyright (c) 2024, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.helpers.quests.atfirstlight;

import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import static com.questhelper.requirements.util.LogicHelper.and;
import static com.questhelper.requirements.util.LogicHelper.or;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.coords.WorldPoint;

public class AtFirstLight extends BasicQuestHelper
{
	//Items Required
	ItemRequirement needle, boxTrap, hammer, jerboaTail, jerboaTailOrBoxTrap, jerboaTail2OrBoxTrap;

	// Items Recommended
	ItemRequirement staminaPotion;

	// Quest Items
	ItemRequirement toyMouse, toyMouseWound, smoothLeaf, stickyLeaf, makeshiftPoultice, furSample, trimmedFur, foxsReport;

	QuestStep talkToApatura, goDownTree, talkToVerity, talkToWolf, windUpToy, useToyOnKiko,
		checkBed, returnToWolf, goUpTree, talkToFox, takeLeaf, takeSecondLeaf, catchJerboa, useTailOnLeaves,
		returnToFox, talkToFoxAfterPoultice, talkToAtza, talkToAtzaAfterHandingFur, makeEquipmentPile, talkToAtzaForTrim,
		returnToFoxAfterTrim, getReportFromFox, goDownTreeEnd, talkToVerityEnd, useJerboaTailOnBed, goUpTreeToFinishQuest,
		talkToApaturaToFinishQuest;

	QuestStep buyBoxTrap, takeNeedle, takeHammer;

	//Zones
	Zone guild;

	Requirement inGuild, gotMouse, usedMouse, checkedBed, equipmentUsable, repairedEquipment, hadReport, handedInReport;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		Map<Integer, QuestStep> steps = new HashMap<>();
		initializeRequirements();
		setupConditions();
		setupSteps();

		// 9841 0->1?? Birb?
		steps.put(0, talkToApatura);

		ConditionalStep enterTree =  new ConditionalStep(this, goDownTree);

		ConditionalStep goTalkToVerity = enterTree.copy();
		goTalkToVerity.addStep(inGuild, talkToVerity);
		steps.put(1, goTalkToVerity);

		ConditionalStep goTalkToWolf = enterTree.copy();
		goTalkToWolf.addStep(inGuild, talkToWolf);
		steps.put(2, goTalkToWolf);

		ConditionalStep goDistractCat = enterTree.copy();
		goDistractCat.addStep(and(inGuild, checkedBed), returnToWolf);
		goDistractCat.addStep(and(inGuild, usedMouse), checkBed);
		goDistractCat.addStep(and(inGuild, toyMouseWound), useToyOnKiko);
		goDistractCat.addStep(and(inGuild, gotMouse), windUpToy);
		goDistractCat.addStep(inGuild, talkToWolf);
		steps.put(3, goDistractCat);

		ConditionalStep goFindFox = new ConditionalStep(this, buyBoxTrap);
		goFindFox.addStep(inGuild, goUpTree);
		goFindFox.addStep(jerboaTail2OrBoxTrap, talkToFox);
		steps.put(4, goFindFox);

		ConditionalStep goMakePoultice = new ConditionalStep(this, takeLeaf);
		goMakePoultice.addStep(and(makeshiftPoultice), returnToFox);
		goMakePoultice.addStep(and(smoothLeaf, stickyLeaf, jerboaTail.quantity(2)), useTailOnLeaves);
		goMakePoultice.addStep(and(smoothLeaf, stickyLeaf), catchJerboa);
		goMakePoultice.addStep(smoothLeaf, takeSecondLeaf);
		steps.put(5, goMakePoultice);

		steps.put(6, talkToFoxAfterPoultice);

		// You can get more fur even if some is in your bank
		ConditionalStep bringAtzaFur = new ConditionalStep(this, talkToFoxAfterPoultice);
		bringAtzaFur.addStep(furSample, talkToAtza);
		steps.put(7, bringAtzaFur);

		ConditionalStep goRepairEquipment = new ConditionalStep(this, talkToAtzaAfterHandingFur);
		goRepairEquipment.addStep(repairedEquipment, talkToAtzaForTrim);
		goRepairEquipment.addStep(and(equipmentUsable, hammer), makeEquipmentPile);
		goRepairEquipment.addStep(equipmentUsable, takeHammer);
		steps.put(8, goRepairEquipment);

		ConditionalStep goWithTrimToFox = new ConditionalStep(this, talkToAtzaForTrim);
		goWithTrimToFox.addStep(trimmedFur, returnToFoxAfterTrim);
		steps.put(9, goWithTrimToFox);

		ConditionalStep goToVerityWithReport = new ConditionalStep(this, talkToAtzaForTrim);
		goToVerityWithReport.addStep(and(inGuild, trimmedFur, handedInReport, needle), useJerboaTailOnBed);
		goToVerityWithReport.addStep(and(inGuild, trimmedFur, hadReport, needle), talkToVerityEnd);
		goToVerityWithReport.addStep(and(trimmedFur, hadReport, needle), goDownTreeEnd);
		goToVerityWithReport.addStep(and(trimmedFur, hadReport), takeNeedle);
		goToVerityWithReport.addStep(trimmedFur, getReportFromFox);
		steps.put(10, goToVerityWithReport);

		ConditionalStep goFinishQuest = new ConditionalStep(this, talkToApaturaToFinishQuest);
		goFinishQuest.addStep(inGuild, goUpTreeToFinishQuest);
		steps.put(11, goFinishQuest);

		return steps;
	}

	@Override
	protected void setupZones()
	{
		guild = new Zone(new WorldPoint(1540, 9409, 0), new WorldPoint(1580, 9470, 0));
	}

	@Override
	protected void setupRequirements()
	{
		// Required
		needle = new ItemRequirement("Needle", ItemID.NEEDLE).isNotConsumed();
		needle.canBeObtainedDuringQuest();
		boxTrap = new ItemRequirement("Box trap", ItemID.BOX_TRAP).isNotConsumed();
		boxTrap.setTooltip("You can buy one from Imia in the north of the Hunter Guild's surface area for 41gp.");
		hammer = new ItemRequirement("Hammer", ItemCollections.HAMMER).isNotConsumed();
		hammer.canBeObtainedDuringQuest();
		jerboaTail = new ItemRequirement("Jerboa tail", ItemID.JERBOA_TAIL);
		jerboaTail.canBeObtainedDuringQuest();

		jerboaTailOrBoxTrap = new ItemRequirements(LogicType.OR, "Jerboa tail, or a box trap to get some", jerboaTail, boxTrap);
		jerboaTail2OrBoxTrap = new ItemRequirements(LogicType.OR, "2 Jerboa tails, or a box trap to get some", jerboaTail.quantity(2), boxTrap);

		// Recommended
		staminaPotion = new ItemRequirement("Stamina potions", ItemCollections.STAMINA_POTIONS);

		// Quest Items
		toyMouse = new ItemRequirement("Toy mouse", ItemID.TOY_MOUSE);
		toyMouseWound = new ItemRequirement("Toy mouse (wound)", ItemID.TOY_MOUSE_WOUND);
		smoothLeaf = new ItemRequirement("Smooth leaf", ItemID.SMOOTH_LEAF);
		stickyLeaf = new ItemRequirement("Sticky leaf", ItemID.STICKY_LEAF);
		makeshiftPoultice = new ItemRequirement("Makeshift poultice", ItemID.MAKESHIFT_POULTICE);
		furSample = new ItemRequirement("Fur sample", ItemID.FUR_SAMPLE);
		trimmedFur = new ItemRequirement("Trimmed fur", ItemID.TRIMMED_FUR);
	}

	private void setupConditions()
	{
		inGuild = new ZoneRequirement(guild);
		gotMouse = new VarbitRequirement(9843, 1);
		usedMouse = new VarbitRequirement(9839, 1);
		checkedBed = new VarbitRequirement(9837, 1);

		// 9842 0->1, received pelt once
		equipmentUsable = new VarbitRequirement(9840, 1);
		repairedEquipment = new VarbitRequirement(9840, 2);
		handedInReport = new VarbitRequirement(9836, 1);

		foxsReport = new ItemRequirement("Fox's report", ItemID.FOXS_REPORT).hideConditioned(handedInReport);
		hadReport = or(foxsReport, handedInReport);

		// Bed repaired, 9838 0->1
	}

	private void setupSteps()
	{
		talkToApatura = new NpcStep(this, NpcID.GUILDMASTER_APATURA, new WorldPoint(1554, 3033, 0),
			"Talk to Guildmaster Apatura in the Hunter Guild, south-west of Civitas illa Fortis.");
		talkToApatura.addDialogSteps("Can I help you with anything?", "Yes.");
		goDownTree = new ObjectStep(this, ObjectID.STAIRS_51641, new WorldPoint(1557, 3048, 0),
			"Go down the stairs in the tree in the guild.");
		talkToVerity = new NpcStep(this, NpcID.GUILD_SCRIBE_VERITY, new WorldPoint(1559, 9464, 0),
			"Talk to Guild Scribe Verity behind the bar.");
		talkToWolf = new NpcStep(this, NpcID.GUILD_HUNTER_WOLF_MASTER, new WorldPoint(1555, 9462, 0),
			"Talk to Guild Hunter Wolf (Master), next to the bar.");
		windUpToy = new DetailedQuestStep(this, "Wind up a toy mouse.", toyMouse.highlighted());
		windUpToy.addIcon(ItemID.TOY_MOUSE_WOUND);
		useToyOnKiko = new NpcStep(this, NpcID.GUILD_HUNTER_KIKO, new WorldPoint(1552, 9460, 0),
			"Use the wound up toy mouse on Guild Hunter Kiko near the bar.", toyMouseWound.highlighted());
		checkBed = new ObjectStep(this, ObjectID.CAT_BED, new WorldPoint(1552, 9460, 0),
			"Check the cat's bed.");
		returnToWolf = new NpcStep(this, NpcID.GUILD_HUNTER_WOLF_MASTER, new WorldPoint(1555, 9462, 0),
			"Return to Guild Hunter Wolf (Master), next to the bar.");
		goUpTree = new ObjectStep(this, ObjectID.STAIRS_51642, new WorldPoint(1557, 9449, 0),
			"Go back up the stairs.");
		talkToFox = new NpcStep(this, NpcID.GUILD_HUNTER_FOX, new WorldPoint(1623, 2982, 0),
			"Talk to Guild Hunter Fox near the crevice south-east of the Hunter Guild.");
		takeLeaf = new ObjectStep(this, NullObjectID.NULL_50876, new WorldPoint(1618, 2979, 0),
			"Search the leafy bush south of the crevice for a smooth leaf.");
		takeSecondLeaf = new ObjectStep(this, NullObjectID.NULL_50877, new WorldPoint(1673, 2992, 0),
			"Search the rough-looking bush on the west side of the Locus Oasis.");
		catchJerboa = new DetailedQuestStep(this, new WorldPoint(1664, 3003, 0),
			"Catch two Embertailed Jerboa for their tails.", boxTrap.highlighted());
		catchJerboa.addIcon(ItemID.BOX_TRAP);
		useTailOnLeaves = new DetailedQuestStep(this, "Use the tails on the leaves.",
			jerboaTail.highlighted(), smoothLeaf.highlighted());
		returnToFox = new NpcStep(this, NpcID.GUILD_HUNTER_FOX, new WorldPoint(1623, 2982, 0),
			"Bring the poultice back to Guild Hunter Fox near the crevice.", makeshiftPoultice);
		talkToFoxAfterPoultice = new NpcStep(this, NpcID.GUILD_HUNTER_FOX, new WorldPoint(1623, 2982, 0),
			"Talk to Guild Hunter Fox.");
		talkToAtza = new NpcStep(this, NpcID.ATZA, new WorldPoint(1696, 3063, 0),
			"Talk to Atza in one of the buildings outside Civitas illa Fortis' south wall, west of the general store.",
			furSample);
		talkToAtzaAfterHandingFur = new NpcStep(this, NpcID.ATZA, new WorldPoint(1696, 3063, 0),
			"Talk to Atza in one of the buildings outside Civitas illa Fortis' south wall, west of the general store.");
		talkToAtza.addSubSteps(talkToAtzaAfterHandingFur);
		makeEquipmentPile = new ObjectStep(this, NullObjectID.NULL_52976, new WorldPoint(1697, 3063, 0),
			"Set-up the pile of equipment next to Atza.", hammer);
		talkToAtzaForTrim = new NpcStep(this, NpcID.ATZA, new WorldPoint(1696, 3063, 0),
			"Talk to Atza again for some trimmed fur.");
		returnToFoxAfterTrim = new NpcStep(this, NpcID.GUILD_HUNTER_FOX, new WorldPoint(1623, 2982, 0),
			"Return to Guild Hunter Fox near the crevice south-east of the Hunter Guild to get his report.", trimmedFur);
		getReportFromFox = new NpcStep(this, NpcID.GUILD_HUNTER_FOX, new WorldPoint(1623, 2982, 0),
			"Return back to Fox to get his report.");
		returnToFoxAfterTrim.addSubSteps(getReportFromFox);
		goDownTreeEnd = new ObjectStep(this, ObjectID.STAIRS_51641, new WorldPoint(1557, 3048, 0),
			"Go down the stairs in the tree in the Hunters Guild.", foxsReport, trimmedFur, jerboaTail, needle);
		talkToVerityEnd = new NpcStep(this, NpcID.GUILD_SCRIBE_VERITY, new WorldPoint(1559, 9464, 0),
			"Talk to Guild Scribe Verity behind the bar again.");
		useJerboaTailOnBed = new ObjectStep(this, ObjectID.CAT_BED, new WorldPoint(1552, 9460, 0),
			"Use a jerboa tail on the cat bed.", jerboaTail.highlighted(), trimmedFur, needle);
		useJerboaTailOnBed.addIcon(ItemID.JERBOA_TAIL);
		goUpTreeToFinishQuest = new ObjectStep(this, ObjectID.STAIRS_51642, new WorldPoint(1557, 9449, 0),
			"Go back up the stairs and talk to Guildmaster Apatura to finish the quest.");
		talkToApaturaToFinishQuest = new NpcStep(this, NpcID.GUILDMASTER_APATURA, new WorldPoint(1554, 3033, 0),
			"Talk to Guildmaster Apatura to finish the quest.");
		goUpTreeToFinishQuest.addSubSteps(talkToApaturaToFinishQuest);

		buyBoxTrap = new NpcStep(this, NpcID.IMIA, new WorldPoint(1562, 3060, 0),
			"Get a box trap. You can buy one from Imia in the north of the Hunter Guild's surface for 41gp.");
		buyBoxTrap.addWidgetHighlightWithItemIdRequirement(300, 16, ItemID.BOX_TRAP, true);
		takeNeedle = new ItemStep(this, new WorldPoint(1566, 3035, 0),
			"Take the needle in the Hunter Guild's surface area, in its south-east corner.", needle);
		takeHammer = new ItemStep(this, new WorldPoint(1696, 3070, 0), "Take a hammer from the house north of Atza.", hammer);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(jerboaTail2OrBoxTrap, hammer, needle);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return List.of(staminaPotion);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return List.of(
			new SkillRequirement(Skill.HUNTER, 46),
			new SkillRequirement(Skill.HERBLORE, 30),
			new SkillRequirement(Skill.CONSTRUCTION, 27),
			new QuestRequirement(QuestHelperQuest.CHILDREN_OF_THE_SUN, QuestState.FINISHED),
			new QuestRequirement(QuestHelperQuest.EAGLES_PEAK, QuestState.FINISHED)
		);
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return List.of(
			new ExperienceReward(Skill.HUNTER, 4500),
			new ExperienceReward(Skill.CONSTRUCTION, 800),
			new ExperienceReward(Skill.HERBLORE, 500)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Collections.singletonList(new UnlockReward("Access to Master Tier Hunters' Rumours"));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		allSteps.add(new PanelDetails("Helping out", List.of(
			talkToApatura, goDownTree, talkToVerity, talkToWolf, windUpToy, useToyOnKiko,
			checkBed, returnToWolf
		)));
		allSteps.add(new PanelDetails("On the hunt", List.of(
			goUpTree, buyBoxTrap, talkToFox, takeLeaf, takeSecondLeaf, catchJerboa, useTailOnLeaves,
			returnToFox, talkToFoxAfterPoultice
		), jerboaTailOrBoxTrap));
		allSteps.add(new PanelDetails("Bed repairs", List.of(
			talkToAtza, takeHammer, makeEquipmentPile, talkToAtzaForTrim, returnToFoxAfterTrim, takeNeedle,
			goDownTreeEnd, talkToVerityEnd, useJerboaTailOnBed, goUpTreeToFinishQuest
		), hammer, needle, jerboaTail));

		return allSteps;
	}
}
