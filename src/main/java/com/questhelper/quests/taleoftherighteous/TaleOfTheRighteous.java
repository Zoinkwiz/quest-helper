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
package com.questhelper.quests.taleoftherighteous;

import com.questhelper.ItemCollections;
import com.questhelper.QuestHelperQuest;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.player.Favour;
import com.questhelper.requirements.player.FavourRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.conditional.NpcCondition;
import com.questhelper.requirements.conditional.ObjectCondition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;

@QuestDescriptor(
	quest = QuestHelperQuest.TALE_OF_THE_RIGHTEOUS
)
public class TaleOfTheRighteous extends BasicQuestHelper
{
	//Items Required
	ItemRequirement pickaxe, rangedWeapon, runesForCombat, rope, combatGear, meleeWeapon;

	//Items Recommended
	ItemRequirement antiPoison, xericTalisman;

	Requirement inArchive, inPuzzleRoom, strangeObjectEast, strangeObjectWest, isSouthWestWhite, isSouthEastWhite,
		isNorthWestWhite, isNorthEastWhite, inShiroRoom, inCavern, rockfallNearby, boulderBlockingPath, corruptLizardmanNearby;

	QuestStep talkToPhileas, teleportToArchive, talkToPagida, pushStrangeDeviceWest, attackWithMagic, attackWithMelee,
		pushStrangeDeviceEast, attackWithRanged, investigateSkeleton, talkToPhileasAgain, goUpToShiro, talkToShiro,
		talkToDuffy, useRopeOnCrevice, enterCrevice, mineRockfall, pushBoulder, tryToEnterBarrier, killLizardman,
		inspectUnstableAltar, leaveCave, returnToDuffy, enterCreviceAgain, talkToDuffyInCrevice, talkToGnosi,
		returnUpToShiro, returnToShiro, returnToPhileasTent, goUpToShrioToFinish, finishQuest;

	//Zones
	Zone archive, puzzleRoom, shiroRoom, cavern;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToPhileas);

		ConditionalStep travelToPuzzle = new ConditionalStep(this, teleportToArchive);
		travelToPuzzle.addStep(new Conditions(inPuzzleRoom, strangeObjectEast, isSouthWestWhite, isNorthWestWhite), attackWithRanged);
		travelToPuzzle.addStep(new Conditions(inPuzzleRoom, isSouthWestWhite, isNorthWestWhite), pushStrangeDeviceEast);
		travelToPuzzle.addStep(new Conditions(inPuzzleRoom, strangeObjectWest, isSouthWestWhite), attackWithMelee);
		travelToPuzzle.addStep(new Conditions(inPuzzleRoom, strangeObjectWest), attackWithMagic);
		travelToPuzzle.addStep(inPuzzleRoom, pushStrangeDeviceWest);
		travelToPuzzle.addStep(inArchive, talkToPagida);

		steps.put(1, travelToPuzzle);
		steps.put(2, travelToPuzzle);
		steps.put(3, travelToPuzzle);

		ConditionalStep getSkeletonItem = new ConditionalStep(this, teleportToArchive);
		getSkeletonItem.addStep(inPuzzleRoom, investigateSkeleton);
		getSkeletonItem.addStep(inArchive, talkToPagida);

		steps.put(4, getSkeletonItem);

		steps.put(5, talkToPhileasAgain);

		ConditionalStep reportToShiro = new ConditionalStep(this, goUpToShiro);
		reportToShiro.addStep(inShiroRoom, talkToShiro);

		steps.put(6, reportToShiro);

		steps.put(7, talkToDuffy);

		steps.put(8, useRopeOnCrevice);

		ConditionalStep goIntoCavern = new ConditionalStep(this, enterCrevice);
		goIntoCavern.addStep(corruptLizardmanNearby, killLizardman);
		goIntoCavern.addStep(new Conditions(inCavern, rockfallNearby), mineRockfall);
		goIntoCavern.addStep(new Conditions(inCavern, boulderBlockingPath), pushBoulder);
		goIntoCavern.addStep(inCavern, tryToEnterBarrier);

		steps.put(9, goIntoCavern);

		ConditionalStep investigateAltar = new ConditionalStep(this, enterCrevice);
		investigateAltar.addStep(inCavern, inspectUnstableAltar);

		steps.put(10, investigateAltar);

		ConditionalStep returnToSurface = new ConditionalStep(this, returnToDuffy);
		returnToSurface.addStep(inCavern, leaveCave);

		steps.put(11, returnToSurface);

		ConditionalStep enterCaveAgain = new ConditionalStep(this, enterCreviceAgain);
		enterCaveAgain.addStep(inCavern, talkToDuffyInCrevice);

		steps.put(12, enterCaveAgain);

		ConditionalStep talkWithGnosi = new ConditionalStep(this, enterCreviceAgain);
		talkWithGnosi.addStep(inCavern, talkToGnosi);

		steps.put(13, talkWithGnosi);

		ConditionalStep reportBackToShiro = new ConditionalStep(this, returnUpToShiro);
		reportBackToShiro.addStep(inShiroRoom, returnToShiro);

		steps.put(14, reportBackToShiro);

		steps.put(15, returnToPhileasTent);

		ConditionalStep finishOffTheQuest = new ConditionalStep(this, goUpToShrioToFinish);
		finishOffTheQuest.addStep(inShiroRoom, finishQuest);

		steps.put(16, finishOffTheQuest);

		return steps;
	}

	@Override
	public void setupRequirements()
	{
		pickaxe = new ItemRequirement("A pickaxe", ItemCollections.PICKAXES).isNotConsumed();
		rangedWeapon = new ItemRequirement("Any ranged weapon + ammo", -1, -1).isNotConsumed();
		rangedWeapon.setDisplayItemId(BankSlotIcons.getRangedCombatGear());
		runesForCombat = new ItemRequirement("Runes for a few casts of a combat spell", -1, -1);
		runesForCombat.setDisplayItemId(ItemID.DEATH_RUNE);
		rope = new ItemRequirement("Rope", ItemID.ROPE).isNotConsumed();
		combatGear = new ItemRequirement("Combat gear for a level 46 corrupt lizardman", -1, -1).isNotConsumed();
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
		xericTalisman = new ItemRequirement("Xeric's Talisman", ItemID.XERICS_TALISMAN).isNotConsumed();
		meleeWeapon = new ItemRequirement("A melee weapon, or your bare hands", -1, -1).isNotConsumed();
		meleeWeapon.setDisplayItemId(BankSlotIcons.getCombatGear());
		antiPoison = new ItemRequirement("Antipoison for lizardmen", ItemCollections.ANTIPOISONS);
	}

	public void loadZones()
	{
		archive = new Zone(new WorldPoint(1538, 10210, 0), new WorldPoint(1565, 10237, 0));
		puzzleRoom = new Zone(new WorldPoint(1563, 10186, 0), new WorldPoint(1591, 10213, 0));
		shiroRoom = new Zone(new WorldPoint(1480, 3640, 1), new WorldPoint(1489, 3631, 1));
		cavern = new Zone(new WorldPoint(1157, 9928, 0), new WorldPoint(1205, 9977, 0));
	}

	public void setupConditions()
	{
		inArchive = new ZoneRequirement(archive);
		inPuzzleRoom = new ZoneRequirement(puzzleRoom);
		inShiroRoom = new ZoneRequirement(shiroRoom);
		inCavern = new ZoneRequirement(cavern);
		strangeObjectEast = new NpcCondition(NpcID.STRANGE_DEVICE, new WorldPoint(1581, 10200, 0));
		strangeObjectWest = new NpcCondition(NpcID.STRANGE_DEVICE, new WorldPoint(1575, 10200, 0));
		isSouthWestWhite = new ObjectCondition(ObjectID.WHITE_CRYSTAL_31960, new WorldPoint(1574, 10196, 0));
		isSouthEastWhite = new ObjectCondition(ObjectID.WHITE_CRYSTAL_31960, new WorldPoint(1581, 10196, 0));
		isNorthWestWhite = new ObjectCondition(ObjectID.WHITE_CRYSTAL_31960, new WorldPoint(1574, 10203, 0));
		isNorthEastWhite = new ObjectCondition(ObjectID.WHITE_CRYSTAL_31960, new WorldPoint(1581, 10203, 0));
		rockfallNearby = new ObjectCondition(ObjectID.BOULDER_32503, new WorldPoint(1182, 9974, 0));
		boulderBlockingPath = new ObjectCondition(ObjectID.BOULDER_32504, new WorldPoint(1201, 9960, 0));
		corruptLizardmanNearby = new NpcCondition(NpcID.CORRUPT_LIZARDMAN_8000);
	}

	public void setupSteps()
	{
		talkToPhileas = new NpcStep(this, NpcID.PHILEAS_RIMOR, new WorldPoint(1542, 3570, 0), "Talk to Phileas Rimor in Shayzien.");
		talkToPhileas.addDialogStep("Do you need help with anything?");
		talkToPhileas.addDialogStep("Yes.");
		talkToPhileas.addDialogStep("What do you need?");
		teleportToArchive = new NpcStep(this, NpcID.ARCHEIO, new WorldPoint(1625, 3808, 0), "Bring a melee weapon, " +
			"ranged weapon, and runes for magic attacks and teleport with Archeio in the Arceuus Library.", rangedWeapon, runesForCombat);
		teleportToArchive.addDialogStep("Yes please!");
		talkToPagida = new NpcStep(this, NpcID.PAGIDA, new WorldPoint(1553, 10223, 0),
			"Talk to Pagida in the Library Historical Archive.");
		talkToPagida.addDialogStep("I have a question about King Shayzien VII.");
		talkToPagida.addDialogStep("Yes please.");
		pushStrangeDeviceWest = new NpcStep(this, NpcID.STRANGE_DEVICE, new WorldPoint(1580, 10199, 0),
			"Push the Strange Device all the way to the west.");
		pushStrangeDeviceEast = new NpcStep(this, NpcID.STRANGE_DEVICE, new WorldPoint(1580, 10199, 0),
			"Push the Strange Device all the way to the east.");
		attackWithMagic = new NpcStep(this, NpcID.STRANGE_DEVICE, new WorldPoint(1580, 10199, 0),
			"Attack the Strange Device with magic from the north side.", runesForCombat);
		attackWithMagic.addIcon(ItemID.MIND_RUNE);
		attackWithRanged = new NpcStep(this, NpcID.STRANGE_DEVICE, new WorldPoint(1580, 10199, 0),
			"Attack the Strange Device with ranged from the south side.", rangedWeapon);
		attackWithRanged.addIcon(ItemID.BRONZE_ARROW);
		attackWithMelee = new NpcStep(this, NpcID.STRANGE_DEVICE, new WorldPoint(1580, 10199, 0),
			"Attack the Strange Device with melee from the south side.", meleeWeapon);
		attackWithMelee.addIcon(ItemID.BRONZE_SWORD);
		investigateSkeleton = new ObjectStep(this, ObjectID.SKELETON_31962, new WorldPoint(1577, 10213, 0),
			"Investigate the skeleton in the north cell.");

		talkToPhileasAgain = new NpcStep(this, NpcID.PHILEAS_RIMOR, new WorldPoint(1542, 3570, 0),
			"Report back to Phileas Rimor in Shayzien.");
		goUpToShiro = new ObjectStep(this, ObjectID.LADDER_42207, new WorldPoint(1481, 3633, 0),
			"Talk to Shiro upstairs in the War Tent located on the west side of the Shayzien Encampment.");
		talkToShiro = new NpcStep(this, NpcID.LORD_SHIRO_SHAYZIEN_11038, new WorldPoint(1484, 3634, 1),
			"Talk to Shiro upstairs in the War Tent located on the west side of the Shayzien Encampment.");
		talkToShiro.addSubSteps(goUpToShiro);

		talkToDuffy = new NpcStep(this, NpcID.HISTORIAN_DUFFY_8163, new WorldPoint(1278, 3561, 0),
			"Travel to Mount Quidamortem and talk to Historian Duffy.");
		useRopeOnCrevice = new ObjectStep(this, NullObjectID.NULL_32502, new WorldPoint(1215, 3559, 0),
			"Use a rope on the crevice west side of Quidamortem.", rope, pickaxe, combatGear);
		useRopeOnCrevice.addIcon(ItemID.ROPE);
		enterCrevice = new ObjectStep(this, NullObjectID.NULL_32502, new WorldPoint(1215, 3559, 0),
			"Enter the crevice west side of Quidamortem, ready to fight a corrupted lizardman (level 46).", pickaxe, combatGear);
		enterCrevice.addDialogStep("Yes.");
		mineRockfall = new ObjectStep(this, ObjectID.BOULDER_32503, new WorldPoint(1182, 9974, 0), "Mine the rockfall.", pickaxe);
		pushBoulder = new ObjectStep(this, ObjectID.BOULDER_32504, new WorldPoint(1201, 9960, 0),
			"Push the boulder further along the path.");

		tryToEnterBarrier = new ObjectStep(this, ObjectID.MAGIC_GATE, new WorldPoint(1172, 9947, 0),
			"Attempt to enter the magic gate to the south room. You will need to kill the corrupted lizardman who appears.");
		killLizardman = new NpcStep(this, NpcID.CORRUPT_LIZARDMAN_8000, new WorldPoint(1172, 9947, 0),
			"Kill the corrupt lizardman.");
		tryToEnterBarrier.addSubSteps(killLizardman);

		inspectUnstableAltar = new ObjectStep(this, ObjectID.UNSTABLE_ALTAR, new WorldPoint(1172, 9929, 0),
			"Inspect the Unstable Altar in the south room.");
		leaveCave = new ObjectStep(this, ObjectID.ROPE_31967, new WorldPoint(1168, 9973, 0),
			"Leave the cavern. You can hop worlds to appear back at the entrance.");
		returnToDuffy = new NpcStep(this, NpcID.HISTORIAN_DUFFY_8163, new WorldPoint(1278, 3561, 0),
			"Return to Historian Duffy.");
		enterCreviceAgain = new ObjectStep(this, NullObjectID.NULL_32502, new WorldPoint(1215, 3559, 0),
			"Enter the crevice west side of Quidamortem again.");
		enterCreviceAgain.addDialogStep("Yes.");
		talkToDuffyInCrevice = new NpcStep(this, NpcID.HISTORIAN_DUFFY, new WorldPoint(1172, 9929, 0),
			"Talk to Historian Duffy near the Unstable Altar.");
		talkToGnosi = new NpcStep(this, NpcID.GNOSI, new WorldPoint(1172, 9929, 0),
			"Talk to Gnosi near the Unstable Altar.");
		returnUpToShiro = new ObjectStep(this, ObjectID.LADDER_42207, new WorldPoint(1481, 3633, 0),
			"Return to Shiro upstairs in the War Tent located on the west side of the Shayzien Encampment.");
		returnToShiro = new NpcStep(this, NpcID.LORD_SHIRO_SHAYZIEN_11038, new WorldPoint(1484, 3634, 1),
			"Return to Shiro upstairs in the War Tent located on the west side of the Shayzien Encampment.");
		returnToShiro.addSubSteps(returnUpToShiro);
		returnToPhileasTent = new DetailedQuestStep(this, new WorldPoint(1542, 3570, 0), "Return to Phileas Rimor's house in Shayzien.");
		goUpToShrioToFinish = new ObjectStep(this, ObjectID.LADDER_42207, new WorldPoint(1481, 3633, 0),
			"Return to Shiro upstairs in the War Tent in west Shayzien to complete the quest.");
		finishQuest = new NpcStep(this, NpcID.LORD_SHIRO_SHAYZIEN_11038, new WorldPoint(1484, 3634, 1),
			"Return to Shiro upstairs in the War Tent in west Shayzien to complete the quest.");
		finishQuest.addSubSteps(goUpToShrioToFinish);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(pickaxe);
		reqs.add(runesForCombat);
		reqs.add(rangedWeapon);
		reqs.add(rope);
		return reqs;
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(xericTalisman);
		reqs.add(antiPoison);
		return reqs;
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new QuestRequirement(QuestHelperQuest.X_MARKS_THE_SPOT, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.CLIENT_OF_KOUREND, QuestState.FINISHED));
		req.add(new FavourRequirement(Favour.SHAYZIEN, 20));
		req.add(new SkillRequirement(Skill.STRENGTH, 16));
		req.add(new SkillRequirement(Skill.MINING, 10));
		return req;
	}

	@Override
	public List<String> getCombatRequirements()
	{
		ArrayList<String> reqs = new ArrayList<>();
		reqs.add("Corrupt Lizardman (level 46)");
		return reqs;
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
				new ItemReward("8,000 Coins", ItemID.COINS_995, 8000),
				new ItemReward("Shayzien Favour Certificate", ItemID.SHAYZIEN_FAVOUR_CERTIFICATE, 1),
				new ItemReward("A Memoir Page", ItemID.KHAREDSTS_MEMOIRS, 1));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off", Collections.singletonList(talkToPhileas)));
		allSteps.add(new PanelDetails("Discovery", Arrays.asList(teleportToArchive, talkToPagida, pushStrangeDeviceWest,
			attackWithMagic, attackWithMelee, pushStrangeDeviceEast, attackWithRanged, investigateSkeleton, talkToPhileasAgain, talkToShiro),
			rangedWeapon, runesForCombat));
		allSteps.add(new PanelDetails("Investigate Quidamortem", Arrays.asList(talkToDuffy, useRopeOnCrevice, enterCrevice,
			mineRockfall, pushBoulder, tryToEnterBarrier, inspectUnstableAltar, returnToDuffy, enterCreviceAgain, talkToDuffyInCrevice, talkToGnosi),
			rope, pickaxe, combatGear));
		allSteps.add(new PanelDetails("Finishing off", Arrays.asList(returnToShiro, returnToPhileasTent, finishQuest)));
		return allSteps;
	}
}
