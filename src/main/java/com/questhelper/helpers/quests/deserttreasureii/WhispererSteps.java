/*
 * Copyright (c) 2023, Zoinkwiz
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
package com.questhelper.helpers.quests.deserttreasureii;

import com.questhelper.ItemCollections;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.questhelpers.QuestUtil;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.conditional.ObjectCondition;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.FreeInventorySlotRequirement;
import static com.questhelper.requirements.util.LogicHelper.and;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.List;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;

public class WhispererSteps extends ConditionalStep
{
	DetailedQuestStep enterRuinsOfCamdozaal, talkToRamarno, talkToPrescott, attachRope, descendDownRope,
		activateTeleporter1, activateTeleporter2, activateTeleporter3, activateTeleporter4, activateTeleporter5,
		takeShadowBlockerSchematic, takeGreenShadowKey, takePurpleShadowKey, tryToEnterSunkenCathedral, talkToKetla,
		giveKetlaBlockerSchematic, claimShadowBlocker, enterSciencePuddle, retrieveShadowBlocker, placeBlockerInFurnaceBuilding;

	ItemRequirement magicCombatGear, food, prayerPotions, staminaPotions, nardahTeleport, ringOfVisibility, lassarTeleport;

	ItemRequirement whisperersMedallion, veryLongRope, shadowBlockerSchematic, greenShadowKey, purpleShadowKey, shadowBlocker;

	FreeInventorySlotRequirement freeSlot;

	Requirement inVault, inCamdozaal, talkedToRamarno, talkedToPrescott, ropeAttached, inLassar,
		activatedTeleporter1, activatedTeleporter2, activatedTeleporter3, activatedTeleporter4,
		activatedTeleporter5, passedOutAtCathedral, finishedTalkingToKetla, givenShadowBlockerSchematic,
		blockerNearby, blockerPlacedAtDoor;

	Zone vault, camdozaal, lassar;

	public WhispererSteps(QuestHelper questHelper, QuestStep defaultStep)
	{
		super(questHelper, defaultStep);
		setupItemRequirements();
		setupZones();
		setupConditions();
		setupSteps();

		Conditions activatedFirst5Teles = new Conditions(activatedTeleporter1, activatedTeleporter2, activatedTeleporter3,
			activatedTeleporter4, activatedTeleporter5);

		addStep(and(inLassar, activatedFirst5Teles, shadowBlocker, greenShadowKey, purpleShadowKey),
			enterSciencePuddle);
		addStep(and(inLassar, activatedFirst5Teles, givenShadowBlockerSchematic, greenShadowKey, purpleShadowKey),
			claimShadowBlocker);
		addStep(and(inLassar, activatedFirst5Teles, shadowBlockerSchematic, greenShadowKey, purpleShadowKey, finishedTalkingToKetla),
			giveKetlaBlockerSchematic);
		addStep(and(inLassar, activatedFirst5Teles, shadowBlockerSchematic, greenShadowKey, purpleShadowKey,passedOutAtCathedral),
			talkToKetla);
		addStep(and(inLassar, activatedFirst5Teles, shadowBlockerSchematic, greenShadowKey, purpleShadowKey), tryToEnterSunkenCathedral);
		addStep(and(inLassar, activatedTeleporter1, activatedTeleporter2, activatedTeleporter3,
			activatedTeleporter4, shadowBlockerSchematic, greenShadowKey, purpleShadowKey), activateTeleporter5);
		addStep(and(inLassar, activatedTeleporter1, activatedTeleporter2, activatedTeleporter3,
			shadowBlockerSchematic, greenShadowKey, purpleShadowKey), activateTeleporter4);
		addStep(and(inLassar, activatedTeleporter1, activatedTeleporter2, activatedTeleporter3,
			shadowBlockerSchematic, greenShadowKey), takePurpleShadowKey);
		addStep(and(inLassar, activatedTeleporter1, activatedTeleporter2, activatedTeleporter3,
			shadowBlockerSchematic), takeGreenShadowKey);
		addStep(and(inLassar, activatedTeleporter1, activatedTeleporter2, activatedTeleporter3), takeShadowBlockerSchematic);
		addStep(and(inLassar, activatedTeleporter1, activatedTeleporter2), activateTeleporter3);
		addStep(and(inLassar, activatedTeleporter1), activateTeleporter2);
		addStep(and(inLassar), activateTeleporter1);
		addStep(and(inCamdozaal, ropeAttached), descendDownRope);
		addStep(and(inCamdozaal, talkedToPrescott, veryLongRope), attachRope);
		addStep(and(inCamdozaal, talkedToRamarno), talkToPrescott);
		addStep(inCamdozaal, talkToRamarno);
		addStep(null, enterRuinsOfCamdozaal);
	}

	protected void setupItemRequirements()
	{
		ringOfVisibility = new ItemRequirement("Ring of visibility", ItemID.RING_OF_VISIBILITY);
		magicCombatGear = new ItemRequirement("Magic combat gear", -1, -1);
		magicCombatGear.setDisplayItemId(BankSlotIcons.getMagicCombatGear());
		food = new ItemRequirement("Food, as much as you can bring", ItemCollections.GOOD_EATING_FOOD);
		prayerPotions = new ItemRequirement("Prayer potions", ItemCollections.PRAYER_POTIONS);
		staminaPotions = new ItemRequirement("Stamina potions", ItemCollections.STAMINA_POTIONS);

		lassarTeleport = new ItemRequirement("Mind altar or lassar teleport", ItemID.MIND_ALTAR_TELEPORT);
		lassarTeleport.addAlternates(ItemID.LASSAR_TELEPORT);

		VarbitRequirement nardahTeleportInBook = new VarbitRequirement(5672, 1, Operation.GREATER_EQUAL);
		nardahTeleport = new ItemRequirement("Nardah teleport, or Fairy Ring to DLQ", ItemID.DESERT_AMULET_4);
		nardahTeleport.setAdditionalOptions(nardahTeleportInBook);
		nardahTeleport.addAlternates(ItemID.DESERT_AMULET_3, ItemID.NARDAH_TELEPORT, ItemID.DESERT_AMULET_2);
		nardahTeleport.addAlternates(ItemCollections.FAIRY_STAFF);

		freeSlot = new FreeInventorySlotRequirement(1);

		/* Quest items */
		whisperersMedallion = new ItemRequirement("Whisperer's medallion", ItemID.WHISPERERS_MEDALLION);
		veryLongRope = new ItemRequirement("Very long rope", ItemID.VERY_LONG_ROPE);
		shadowBlockerSchematic = new ItemRequirement("Shadow blocker schematic", ItemID.SHADOW_BLOCKER_SCHEMATIC);
		greenShadowKey = new ItemRequirement("Shadow key", ItemID.SHADOW_KEY_28374);
		purpleShadowKey = new ItemRequirement("Shadow key", ItemID.SHADOW_KEY);
		shadowBlocker = new ItemRequirement("Shadow blocker", ItemID.SHADOW_BLOCKER);
	}

	protected void setupZones()
	{
		vault = new Zone(new WorldPoint(3925, 9620, 1), new WorldPoint(3949, 9643, 1));
		camdozaal = new Zone(new WorldPoint(2897, 5757, 0), new WorldPoint(3047, 5869, 0));
		lassar = new Zone(new WorldPoint(2263, 6248, 0), new WorldPoint(2825, 6494, 3));
	}

	protected void setupConditions()
	{
		inVault = new ZoneRequirement(vault);
		inCamdozaal = new ZoneRequirement(camdozaal);
		inLassar = new ZoneRequirement(lassar);

		int WHISPERER_VARBIT = 15126;

		// Varbit is for learning about the area from the previous quest
		talkedToRamarno = new VarbitRequirement(12068, 2, Operation.GREATER_EQUAL);

		talkedToPrescott = new VarbitRequirement(WHISPERER_VARBIT, 4, Operation.GREATER_EQUAL);
		ropeAttached = new VarbitRequirement(WHISPERER_VARBIT, 6, Operation.GREATER_EQUAL);
		// Entered undercity:
		// 15064 0->100
		// 15126 6->8
		// 14862 78->80
		// varplayer 3575 36691712 -> 36699904

		activatedTeleporter1 = new VarbitRequirement(15088, 1);
		activatedTeleporter2 = new VarbitRequirement(15089, 1);
		activatedTeleporter3 = new VarbitRequirement(15091, 1);
		activatedTeleporter4 = new VarbitRequirement(15092, 1);
		activatedTeleporter5 = new VarbitRequirement(15093, 1);

		passedOutAtCathedral = new VarbitRequirement(WHISPERER_VARBIT, 10, Operation.GREATER_EQUAL);
		// 10->12, ketla wants to see the ring
		// 12->14, fragment is now safe
		// 14->16, tried to give me fragment

		finishedTalkingToKetla = new VarbitRequirement(WHISPERER_VARBIT, 16, Operation.GREATER_EQUAL);
		givenShadowBlockerSchematic = new VarbitRequirement(15082, 1);
		// Entered science puddle
		// 15162 0->1 (probably just 'is in shadow realm'?)
		// 15163 0->3->13->14->15->16->17...23, then left
		// 15064 = sanity, 0->100
		// 15069 0->453704->05->...09, left
		blockerNearby = new ObjectCondition(ObjectID.SHADOW_BLOCKER);
		blockerPlacedAtDoor = new ObjectCondition(ObjectID.SHADOW_BLOCKER,
			new Zone(new WorldPoint(2606, 6359, 0),
			new WorldPoint(2606, 6360, 0)));
	}

	protected void setupSteps()
	{
		enterRuinsOfCamdozaal = new ObjectStep(getQuestHelper(), NullObjectID.NULL_41357, new WorldPoint(3000, 3494, 0),
			"Enter Camdozaal, west of Ice Mountain.", ringOfVisibility);
		talkToRamarno = new NpcStep(getQuestHelper(), NpcID.RAMARNO_10685, new WorldPoint(2959, 5809, 0),
			"Talk to Ramarno to the north by the sacred forge.");
		talkToRamarno.addDialogStep("Have you seen any archeologists around here?");
		talkToPrescott = new NpcStep(getQuestHelper(), NpcID.PRESCOTT, new WorldPoint(2975, 5794, 0),
			"Talk to Prescott to the east.");

		attachRope = new ObjectStep(getQuestHelper(), NullObjectID.NULL_49477, new WorldPoint(2922, 5827, 0),
			"Tie the rope to a rock in the north west of the cavern, in the mine by the sinkhole.", veryLongRope.highlighted());
		attachRope.addIcon(ItemID.VERY_LONG_ROPE);

		descendDownRope = new ObjectStep(getQuestHelper(), NullObjectID.NULL_49477, new WorldPoint(2922, 5827, 0),
			"Descend into the sinkhole.");

		activateTeleporter1 = new ObjectStep(getQuestHelper(), NullObjectID.NULL_49479, new WorldPoint(2593, 6424, 0),
			"Activate the teleporter to the south-east of the rope. You can use teleporters you've activated to go to other activated teleporters.");
		activateTeleporter2 = new ObjectStep(getQuestHelper(), NullObjectID.NULL_49480, new WorldPoint(2617, 6417, 0),
			"Activate the teleporter further south-east.");
		activateTeleporter3 = new ObjectStep(getQuestHelper(), NullObjectID.NULL_49482, new WorldPoint(2611, 6379, 0),
			"Activate the teleporter to the south in the Science District.");
		activateTeleporter4 = new ObjectStep(getQuestHelper(), NullObjectID.NULL_49483, new WorldPoint(2599, 6341, 0),
			"Activate the teleporter in the far south of the Science District.");
		activateTeleporter5 = new ObjectStep(getQuestHelper(), NullObjectID.NULL_49484, new WorldPoint(2643, 6434, 0),
			"Teleport back to the Plaza, then run east. Activate the teleporter in the north-west of the resedential area.");
		activateTeleporter5.addDialogStep("The Plaza.");

		takeShadowBlockerSchematic = new ItemStep(getQuestHelper(), new WorldPoint(2590, 6380, 0),
			"Enter the north-western building in the Science District, and take the Shadow Schematic which is on top of a barrel in there.",
			shadowBlockerSchematic);
		takeShadowBlockerSchematic.addDialogStep("Northern Science District.");
		takeGreenShadowKey = new ItemStep(getQuestHelper(), new WorldPoint(2581, 6387, 0),
			"Take the shadow key from the north-western room.", greenShadowKey);
		takeGreenShadowKey.addDialogStep("Northern Science District.");
		takePurpleShadowKey = new ItemStep(getQuestHelper(), new WorldPoint(2593, 6352, 0),
			"Take the shadow key from the building to the south.",
			purpleShadowKey);
		takePurpleShadowKey.addDialogStep("Northern Science District.");

		tryToEnterSunkenCathedral = new DetailedQuestStep(getQuestHelper(), new WorldPoint(2656, 6386, 0),
			"Run south-east to try and enter the Sunken Cathedral.");
		tryToEnterSunkenCathedral.addDialogStep("Western Residential District.");

		talkToKetla = new NpcStep(getQuestHelper(), NpcID.KETLA, new WorldPoint(2648, 6442, 0),
			"Talk to Ketla, next to the Western Residential District teleport.", ringOfVisibility, freeSlot);
		talkToKetla.addDialogStep("Western Residential District.");

		giveKetlaBlockerSchematic = new NpcStep(getQuestHelper(), NpcID.KETLA, new WorldPoint(2648, 6442, 0),
			"Give the blocker schematic to Ketla, next to the Western Residential District teleport.", shadowBlockerSchematic);
		giveKetlaBlockerSchematic.addDialogSteps("Western Residential District.", "I have a schematic here.");

		claimShadowBlocker = new ObjectStep(getQuestHelper(), NullObjectID.NULL_49486, new WorldPoint(2645, 6440, 0),
			"Get the Shadow Blocker from the workbench next to Ketla, or from wherever you left it.", freeSlot);
		claimShadowBlocker.addDialogSteps("Take it.", "Western Residential District.");

		placeBlockerInFurnaceBuilding = new DetailedQuestStep(getQuestHelper(), new WorldPoint(2606, 6359, 0),
			"Return to the Science District, and enter the building with furnaces in it. Place the Shadow Blocker next to the locked doors inside.",
			shadowBlocker.highlighted());
		placeBlockerInFurnaceBuilding.addDialogStep("Southern Science District.");

		retrieveShadowBlocker = new ObjectStep(getQuestHelper(), ObjectID.SHADOW_BLOCKER, "Pick up the shadow blocker.");

		enterSciencePuddle = new ObjectStep(getQuestHelper(), NullObjectID.NULL_49478, new WorldPoint(2598, 6365, 0),
			"Return to the Science District, and enter the puddle outside of the building with furnaces in it. When you're in the Shadow Realm, you will slowly lose sanity. " +
				"If you're low on Sanity, you can escape using the Blackstone fragment.");
		enterSciencePuddle.addDialogStep("Southern Science District.");
	}

	protected List<QuestStep> getDisplaySteps()
	{
		return QuestUtil.toArrayList(enterRuinsOfCamdozaal, talkToRamarno, talkToPrescott, attachRope, descendDownRope,
			activateTeleporter1, activateTeleporter2, activateTeleporter3, takeShadowBlockerSchematic, takeGreenShadowKey,
			takePurpleShadowKey, activateTeleporter4, activateTeleporter5, tryToEnterSunkenCathedral, talkToKetla,
			giveKetlaBlockerSchematic, claimShadowBlocker, enterSciencePuddle);
	}
}
