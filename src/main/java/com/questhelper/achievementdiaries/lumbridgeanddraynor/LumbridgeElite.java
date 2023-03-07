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
package com.questhelper.achievementdiaries.lumbridgeanddraynor;

import com.questhelper.ItemCollections;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.questhelpers.QuestDetails;
import com.questhelper.requirements.ComplexRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.EmoteStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.TileStep;
import com.questhelper.steps.emote.QuestEmote;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.runelite.api.Client;
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
import javax.annotation.Nonnull;

@QuestDescriptor(
	quest = QuestHelperQuest.LUMBRIDGE_ELITE
)
public class LumbridgeElite extends ComplexStateQuestHelper
{
	// Items required
	ItemRequirement lockpick, crossbow, mithgrap, lightsource, axe, addyBar, hammer, essence, waterAccessOrAbyss, qcCape;

	// Items recommended
	ItemRequirement ringOfDueling, dorgSphere;

	Requirement notRichChest, notMovario, notChopMagic, notAddyPlatebody, notWaterRunes, notQCEmote, allQuests,
		deathToDorg, templeOfIkov;

	QuestStep claimReward, richChest, movario, chopMagic, addyPlatebody, waterRunes, qcEmote, moveToWater,
		dorgStairsChest, dorgStairsMovario, moveToOldman, moveToUndergroundChest,
		moveToUndergroundMovario, moveToDorgAgi;

	ObjectStep moveToDraySewer, moveToDorgChest, moveToDorgMovario;

	Zone underground, dorg1, dorg2, draySewer, oldman, waterAltar, dorgAgi;

	ZoneRequirement inUnderground, inDorg1, inDorg2, inDraySewer, inOldman, inWaterAltar, inDorgAgi;

	ConditionalStep richChestTask, movarioTask, chopMagicTask, addyPlatebodyTask, waterRunesTask, qcEmoteTask;

	@Override
	public QuestStep loadStep()
	{
		loadZones();
		setupRequirements();
		setupSteps();

		ConditionalStep doElite = new ConditionalStep(this, claimReward);

		addyPlatebodyTask = new ConditionalStep(this, moveToDraySewer);
		addyPlatebodyTask.addStep(inDraySewer, addyPlatebody);
		doElite.addStep(notAddyPlatebody, addyPlatebodyTask);

		qcEmoteTask = new ConditionalStep(this, moveToOldman);
		qcEmoteTask.addStep(inOldman, qcEmote);
		doElite.addStep(notQCEmote, qcEmoteTask);

		richChestTask = new ConditionalStep(this, moveToUndergroundChest);
		richChestTask.addStep(inUnderground, moveToDorgChest);
		richChestTask.addStep(inDorg1, dorgStairsChest);
		richChestTask.addStep(inDorg2, richChest);
		doElite.addStep(notRichChest, richChestTask);

		movarioTask = new ConditionalStep(this, moveToUndergroundMovario);
		movarioTask.addStep(inUnderground, moveToDorgMovario);
		movarioTask.addStep(inDorg1, dorgStairsMovario);
		movarioTask.addStep(inDorg2, moveToDorgAgi);
		movarioTask.addStep(inDorgAgi, movario);
		doElite.addStep(notMovario, movarioTask);

		waterRunesTask = new ConditionalStep(this, moveToWater);
		waterRunesTask.addStep(inWaterAltar, waterRunes);
		doElite.addStep(notWaterRunes, waterRunesTask);

		chopMagicTask = new ConditionalStep(this, chopMagic);
		doElite.addStep(notChopMagic, chopMagicTask);

		return doElite;
	}

	@Override
	public void setupRequirements()
	{
		notRichChest = new VarplayerRequirement(1195, false, 4);
		notMovario = new VarplayerRequirement(1195, false, 5);
		notChopMagic = new VarplayerRequirement(1195, false, 6);
		notAddyPlatebody = new VarplayerRequirement(1195, false, 7);
		notWaterRunes = new VarplayerRequirement(1195, false, 8);
		notQCEmote = new VarplayerRequirement(1195, false, 9);

		allQuests = new Requirement()
		{
			@Override
			public boolean check(Client client)
			{
				boolean allQuestsCompleted = true;
				for (QuestHelperQuest quest : QuestHelperQuest.values())
				{
					if (quest.getQuestType() == QuestDetails.Type.F2P
						|| quest.getQuestType() == QuestDetails.Type.P2P)
					{
						if (quest.getState(client) != QuestState.FINISHED)
						{
							allQuestsCompleted = false;
							break;
						}
					}
				}

				return allQuestsCompleted;
			}

			@Nonnull
			@Override
			public String getDisplayText()
			{
				return "All Quests are Completed";
			}
		};

		lockpick = new ItemRequirement("Lockpick", ItemID.LOCKPICK).showConditioned(notRichChest).isNotConsumed();
		crossbow = new ItemRequirement("Crossbow", ItemCollections.CROSSBOWS).showConditioned(notMovario).isNotConsumed();
		mithgrap = new ItemRequirement("Mith grapple", ItemID.MITH_GRAPPLE_9419).showConditioned(notMovario).isNotConsumed();
		lightsource = new ItemRequirement("A lightsource", ItemCollections.LIGHT_SOURCES).showConditioned(notMovario).isNotConsumed();
		axe = new ItemRequirement("Any axe", ItemCollections.AXES).showConditioned(notChopMagic).isNotConsumed();
		addyBar = new ItemRequirement("Adamantite bar", ItemID.ADAMANTITE_BAR).showConditioned(notAddyPlatebody);
		hammer = new ItemRequirement("Hammer", ItemID.HAMMER).showConditioned(notAddyPlatebody).isNotConsumed();
		essence = new ItemRequirement("Essence", ItemCollections.ESSENCE_LOW).showConditioned(notWaterRunes);
		waterAccessOrAbyss = new ItemRequirement("Access to water altar, or travel through abyss",
			ItemID.WATER_TIARA).showConditioned(notWaterRunes).isNotConsumed();
		waterAccessOrAbyss.setTooltip("Water talisman or tiara");
		qcCape = new ItemRequirement("Quest cape", ItemCollections.QUEST_CAPE).showConditioned(notQCEmote).isNotConsumed();
		dorgSphere = new ItemRequirement("Dorgesh-kann Sphere", ItemID.DORGESHKAAN_SPHERE)
			.showConditioned(new Conditions(notMovario, notRichChest));
		ringOfDueling = new ItemRequirement("Ring of dueling", ItemCollections.RING_OF_DUELINGS)
			.showConditioned(notChopMagic);

		inUnderground = new ZoneRequirement(underground);
		inDorg1 = new ZoneRequirement(dorg1);
		inDorg2 = new ZoneRequirement(dorg2);
		inDraySewer = new ZoneRequirement(draySewer);
		inWaterAltar = new ZoneRequirement(waterAltar);
		inOldman = new ZoneRequirement(oldman);
		inDorgAgi = new ZoneRequirement(dorgAgi);

		deathToDorg = new QuestRequirement(QuestHelperQuest.DEATH_TO_THE_DORGESHUUN, QuestState.FINISHED);
		templeOfIkov = new QuestRequirement(QuestHelperQuest.TEMPLE_OF_IKOV, QuestState.FINISHED);
	}

	public void loadZones()
	{
		waterAltar = new Zone(new WorldPoint(2688, 4863, 0), new WorldPoint(2751, 4800, 0));
		underground = new Zone(new WorldPoint(3137, 9706, 0), new WorldPoint(3332, 9465, 2));
		draySewer = new Zone(new WorldPoint(3077, 9699, 0), new WorldPoint(3132, 9641, 0));
		dorg1 = new Zone(new WorldPoint(2688, 5377, 0), new WorldPoint(2751, 5251, 0));
		dorg2 = new Zone(new WorldPoint(2688, 5377, 1), new WorldPoint(2751, 5251, 1));
		oldman = new Zone(new WorldPoint(3087, 3255, 0), new WorldPoint(3094, 3251, 0));
		dorgAgi = new Zone(new WorldPoint(2688, 5247, 0), new WorldPoint(2752, 5183, 3));
	}

	public void setupSteps()
	{
		moveToDraySewer = new ObjectStep(this, ObjectID.TRAPDOOR_6435, new WorldPoint(3118, 3244, 0),
			"Climb down into the Draynor Sewer.");
		moveToDraySewer.addAlternateObjects(ObjectID.TRAPDOOR_6434);
		addyPlatebody = new ObjectStep(this, ObjectID.ANVIL_2097, new WorldPoint(3112, 9689, 0),
			"Smith a adamant platebody at the anvil in Draynor Sewer.", addyBar.quantity(5), hammer);

		moveToOldman = new TileStep(this, new WorldPoint(3088, 3253, 0),
			"Go to the Wise Old Man's house in Draynor Village.");
		qcEmote = new EmoteStep(this, QuestEmote.SKILL_CAPE, new WorldPoint(3088, 3253, 0),
			"Perform the skill cape emote with the quest cape equipped.", qcCape.equipped());

		moveToWater = new ObjectStep(this, 34815, new WorldPoint(3185, 3165, 0),
			"Enter the water altar.", waterAccessOrAbyss.highlighted(), essence.quantity(28));
		waterRunes = new ObjectStep(this, ObjectID.ALTAR_34762, new WorldPoint(2716, 4836, 0),
			"Craft water runes.", essence.quantity(28));

		moveToUndergroundMovario = new ObjectStep(this, ObjectID.TRAPDOOR_14880, new WorldPoint(3209, 3216, 0),
			"Climb down the trapdoor in the Lumbridge Castle.", mithgrap, crossbow, lightsource);
		moveToUndergroundChest = new ObjectStep(this, ObjectID.TRAPDOOR_14880, new WorldPoint(3209, 3216, 0),
			"Climb down the trapdoor in the Lumbridge Castle.", lockpick, lightsource);

		moveToDorgChest = new ObjectStep(this, ObjectID.DOOR_6919, new WorldPoint(3317, 9601, 0),
			"Go through the doors to Dorgesh-Kaan.", true, lockpick, lightsource);
		moveToDorgChest.addAlternateObjects(ObjectID.DOOR_6920);
		moveToDorgMovario = new ObjectStep(this, ObjectID.DOOR_6919, new WorldPoint(3317, 9601, 0),
			"Go through the doors to Dorgesh-Kaan.", true, mithgrap, crossbow, lightsource);
		moveToDorgMovario.addAlternateObjects(ObjectID.DOOR_6920);

		dorgStairsMovario = new ObjectStep(this, ObjectID.STAIRS_22939, new WorldPoint(2721, 5360, 0),
			"Climb the stairs to the second level of Dorgesh-Kaan.", mithgrap, crossbow, lightsource);
		dorgStairsChest = new ObjectStep(this, ObjectID.STAIRS_22939, new WorldPoint(2721, 5360, 0),
			"Climb the stairs to the second level of Dorgesh-Kaan.", lockpick);

		richChest = new ObjectStep(this, ObjectID.CHEST_22681, new WorldPoint(2703, 5348, 1),
			"Lockpick the chest.", lockpick);
		moveToDorgAgi = new ObjectStep(this, ObjectID.STAIRS_22941, new WorldPoint(2723, 5253, 1),
			"Climb the stairs to enter the Dorgesh-Kaan agility course.");
		movario = new NpcStep(this, NpcID.MOVARIO, new WorldPoint(2706, 5237, 3),
			"Pickpocket Movario near the end of the agility course.");

		chopMagic = new ObjectStep(this, ObjectID.MAGIC_TREE_10834, new WorldPoint(3357, 3312, 0),
			"Chop some magic logs near the Magic Training Arena.", axe);

		claimReward = new NpcStep(this, NpcID.HATIUS_COSAINTUS, new WorldPoint(3235, 3213, 0),
			"Talk to Hatius Cosaintus in Lumbridge to claim your reward!");
		claimReward.addDialogStep("I have a question about my Achievement Diary.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(qcCape, lockpick, mithgrap, hammer, waterAccessOrAbyss, axe, addyBar.quantity(5),
			essence.quantity(28), crossbow);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(ringOfDueling, dorgSphere);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		List<Requirement> reqs = new ArrayList<>();
		reqs.add(new SkillRequirement(Skill.AGILITY, 70));
		reqs.add(new SkillRequirement(Skill.RANGED, 70));
		reqs.add(new ComplexRequirement(LogicType.OR, "76 Runecraft or 57 with Raiments of the Eye set",
			new SkillRequirement(Skill.RUNECRAFT, 76, true, "76 Runecraft"),
			new ItemRequirements("57 with Raiments of the Eye set",
				new ItemRequirement("Hat", ItemCollections.EYE_HAT).alsoCheckBank(questBank),
				new ItemRequirement("Top", ItemCollections.EYE_TOP).alsoCheckBank(questBank),
				new ItemRequirement("Bottom", ItemCollections.EYE_BOTTOM).alsoCheckBank(questBank),
				new ItemRequirement("Boot", ItemID.BOOTS_OF_THE_EYE)).alsoCheckBank(questBank)
		));
		reqs.add(new SkillRequirement(Skill.SMITHING, 88));
		reqs.add(new SkillRequirement(Skill.STRENGTH, 70));
		reqs.add(new SkillRequirement(Skill.THIEVING, 78));
		reqs.add(new SkillRequirement(Skill.WOODCUTTING, 75));

		reqs.add(allQuests);

		return reqs;
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
			new ItemReward("Explorer's ring 4", ItemID.EXPLORERS_RING_4),
			new ItemReward("50,000 Exp. Lamp (Any skill over 70)", ItemID.ANTIQUE_LAMP)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
			new UnlockReward("100% run energy replenish 3 times a day from Explorer's ring"),
			new UnlockReward("30 casts of High Level Alchemy per day (does not provide experience) from Explorer's ring"),
			new UnlockReward("20% discount on items in the Culinaromancer's Chest"),
			new UnlockReward("Ability to use Fairy rings without the need of a Dramen or Lunar staff"),
			new UnlockReward("Unlocked the 6th slot for blocking Slayer tasks")
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		PanelDetails adamantitePlatebodySteps = new PanelDetails("Adamantite Platebody",
			Arrays.asList(moveToDraySewer, addyPlatebody), new SkillRequirement(Skill.SMITHING, 88),
			addyBar.quantity(5), hammer);
		adamantitePlatebodySteps.setDisplayCondition(notAddyPlatebody);
		adamantitePlatebodySteps.setLockingStep(addyPlatebodyTask);
		allSteps.add(adamantitePlatebodySteps);

		PanelDetails questCapeEmoteSteps = new PanelDetails("Quest Cape Emote", Arrays.asList(moveToOldman, qcEmote),
			allQuests, qcCape);
		questCapeEmoteSteps.setDisplayCondition(notQCEmote);
		questCapeEmoteSteps.setLockingStep(qcEmoteTask);
		allSteps.add(questCapeEmoteSteps);

		PanelDetails richChestSteps = new PanelDetails("Dorgesh-Kaan Rich Chest", Arrays.asList(moveToUndergroundChest,
			moveToDorgChest, dorgStairsChest, richChest), new SkillRequirement(Skill.THIEVING, 78), deathToDorg,
			lightsource, lockpick);
		richChestSteps.setDisplayCondition(notRichChest);
		richChestSteps.setLockingStep(richChestTask);
		allSteps.add(richChestSteps);

		PanelDetails movarioSteps = new PanelDetails("Movario", Arrays.asList(moveToUndergroundMovario, moveToDorgMovario,
			dorgStairsMovario, moveToDorgAgi, movario), new SkillRequirement(Skill.THIEVING, 42),
			new SkillRequirement(Skill.AGILITY, 70), new SkillRequirement(Skill.RANGED, 70),
			new SkillRequirement(Skill.STRENGTH, 70), deathToDorg, templeOfIkov, mithgrap, crossbow, lightsource);
		movarioSteps.setDisplayCondition(notMovario);
		movarioSteps.setLockingStep(movarioTask);
		allSteps.add(movarioSteps);

		PanelDetails waterRunesSteps = new PanelDetails("140 Water Runes", Arrays.asList(moveToWater, waterRunes),
			new SkillRequirement(Skill.RUNECRAFT, 76), essence.quantity(28), waterAccessOrAbyss);
		waterRunesSteps.setDisplayCondition(notWaterRunes);
		waterRunesSteps.setLockingStep(waterRunesTask);
		allSteps.add(waterRunesSteps);

		PanelDetails chopMagicsSteps = new PanelDetails("Chop Magics", Collections.singletonList(chopMagic),
			new SkillRequirement(Skill.WOODCUTTING, 75), axe);
		chopMagicsSteps.setDisplayCondition(notChopMagic);
		chopMagicsSteps.setLockingStep(chopMagicTask);
		allSteps.add(chopMagicsSteps);

		allSteps.add(new PanelDetails("Finishing off", Collections.singletonList(claimReward)));

		return allSteps;
	}
}
