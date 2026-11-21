/*
 * Copyright (c) 2026, pajlada <rasmus.karlsson@pajlada.com>
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
package com.questhelper.helpers.miniquests.thefracturedarchive;

import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.ManualRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.npc.NpcRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import static com.questhelper.requirements.util.LogicHelper.and;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.QuestState;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;

public class TheFracturedArchive extends BasicQuestHelper
{
	// Required items
	ItemRequirement sapphireLantern;
	ItemRequirement combatGearAndSupplies;

	// Mid-quest item requirements
	ItemRequirement alliancesMessage;

	// Miscellaneous requirements
	VarbitRequirement needToUnlockShortcut;
	ZoneRequirement inChasm;
	VarbitRequirement inCutscene;
	ZoneRequirement inTFALobbyArea;
	ZoneRequirement inTFA;
	ZoneRequirement inOcellusRoom;
	Conditions determination;
	Conditions skill;

	// Steps
	DetailedQuestStep readAlliancesMessage;
	NpcStep travelIntoGuthixChasm;
	ObjectStep enterCave;
	ObjectStep enterArchiveDoor;
	DetailedQuestStep finishTheRaid;
	NpcStep startOcellusFight;
	DetailedQuestStep finishTheRaid2;
	DetailedQuestStep lootThenLeaveRaid;
	NpcStep speakToArmacus;

	@Override
	protected void setupRequirements()
	{
		sapphireLantern = new ItemRequirement("Sapphire lantern", ItemID.TOG_SAPPHIRE_LANTERN_LIT);
		combatGearAndSupplies = new ItemRequirement("Combat gear and supplies for the raid", -1, -1);

		alliancesMessage = new ItemRequirement("Alliance's message", ItemID.TFA_START_MESSAGE);

		needToUnlockShortcut = new VarbitRequirement(VarbitID.WGS_TFA_SHORTCUT_UNLOCKED, 0);
		inChasm = new ZoneRequirement(new Zone(new WorldPoint(4092, 4610, 0), new WorldPoint(4039, 4557, 0)));
		inCutscene = new VarbitRequirement(VarbitID.CUTSCENE_STATUS, 1);
		inTFALobbyArea = new ZoneRequirement(new Zone(new WorldPoint(4095, 5505, 0), new WorldPoint(4147, 5565, 0)));

		inTFA = new ZoneRequirement(
			new Zone(16471), // Archive Guardian
			new Zone(16214), // Ezekiel
			new Zone(16727), // Bork
			new Zone(16216), // Moia
			new Zone(16728), // Dreadnoth the Jellied
			new Zone(16215), // Dagon'hai Elder
			new Zone(16726), // Xarth
			new Zone(16472), // Ocellus
			new Zone(16474) // Loot room
		);

		inOcellusRoom = new ZoneRequirement(new Zone(16472));

		var m = new ManualRequirement();
		m.setShouldPass(true);
		determination = new Conditions(m);
		determination.setText("Determination");
		skill = new Conditions(m);
		skill.setText("Skill");
	}

	void setupSteps()
	{
		readAlliancesMessage = new DetailedQuestStep(this, "Be approached by a Guardian of Armadyl and read the message they bring you.", alliancesMessage.highlighted());

		travelIntoGuthixChasm = new NpcStep(this, NpcID.TOG_LIGHT_CREATURE_OP, new WorldPoint(3228, 9525, 2), "Use a lit sapphire lantern on a Light creature to head down towards The Fractured Archive.", sapphireLantern);
		travelIntoGuthixChasm.addDialogStep("Travel into the chasm.");

		enterCave = new ObjectStep(this, ObjectID.LUC2_RAID_ENTRY_OPEN, new WorldPoint(4050, 4567, 0), "Crawl through the opening towards The Fractured Archive.");

		enterArchiveDoor = new ObjectStep(this, ObjectID.TFA_LOBBY_RAID_ENTRY, new WorldPoint(4123, 5562, 0), "Enter the the archive door, starting the raid.");
		enterArchiveDoor.addDialogStep("Yes.");

		finishTheRaid = new DetailedQuestStep(this, "Finish the raid.", skill, determination);
		finishTheRaid.addDialogStep("Yes.");

		startOcellusFight = new NpcStep(this, new int[]{NpcID.TFA_OCELLUS_INTRO, NpcID.TFA_OCELLUS_INTRO_MET, NpcID.TFA_OCELLUS_INTRO_NOT_MET}, new WorldPoint(1, 1, 1), "Talk to Ocellus to start the final fight.");

		finishTheRaid2 = new DetailedQuestStep(this, "Defeat Ocellus.", skill, determination);
		finishTheRaid2.addDialogStep("Yes.");

		lootThenLeaveRaid = new DetailedQuestStep(this, "Loot your chest, then leave the raid and speak to Armacus Selles outside the entrance to The Fractured Archive.");
		lootThenLeaveRaid.addDialogStep("Yes.");

		speakToArmacus = new NpcStep(this, NpcID.ARMACUS_SELLES, new WorldPoint(4129, 5547, 0), "Speak to Armacus Selles outside the entrance to The Fractured Archive.");
	}

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();

		var steps = new HashMap<Integer, QuestStep>();

		// 0: Read letter from the npc that approaches you to unlock shortcut, then enter the TFA lobby area
		var cEnterCave = new ConditionalStep(this, travelIntoGuthixChasm);
		cEnterCave.addStep(needToUnlockShortcut, readAlliancesMessage);
		cEnterCave.addStep(inChasm, cEnterCave);
		cEnterCave.addStep(inCutscene, enterCave.cutscene());
		steps.put(0, cEnterCave);

		// 2: You have entered the TFA lobby for the first area
		var cGetToTFA = new ConditionalStep(this, travelIntoGuthixChasm);
		cGetToTFA.addStep(inChasm, cEnterCave);
		cGetToTFA.addStep(inCutscene, enterCave.cutscene());
		cGetToTFA.addStep(inTFALobbyArea, enterArchiveDoor);
		steps.put(2, cGetToTFA);

		// 4: You have entered the raid
		// When in Ocellus room, you need to speak to him and say Yes.
		// [2026-11-21T14:46:12Z 6168] varbit TFA_OCELLUS_MET (16384) 0 -> 1
		var cDoTFA = new ConditionalStep(this, cGetToTFA);
		var ocellusTalkable = new NpcRequirement(NpcID.TFA_OCELLUS_INTRO);
		cDoTFA.addStep(and(inOcellusRoom, ocellusTalkable), startOcellusFight);
		cDoTFA.addStep(inOcellusRoom, finishTheRaid2);
		cDoTFA.addStep(inTFA, finishTheRaid);
		steps.put(4, cDoTFA);

		// 6: Finished TFA. You still need to loot stuff, exit the raid, then you can talk to Armacus Selles
		var cFinishedTFA = new ConditionalStep(this, speakToArmacus);
		cFinishedTFA.addStep(inTFA, lootThenLeaveRaid);
		steps.put(6, cFinishedTFA);

		return steps;
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return List.of(
			new QuestRequirement(QuestHelperQuest.WHILE_GUTHIX_SLEEPS, QuestState.FINISHED)
		);
	}

	@Override
	public List<Requirement> getGeneralRecommended()
	{
		return List.of(
			//
		);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return List.of(
			sapphireLantern,
			combatGearAndSupplies
		);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return List.of(
			"Archive Guardian (lvl 984)",
			"Ezekiel or Bork and the Smorks",
			"Moia or Dreadnoth the Jellied",
			"The Dagon'hai or Xarth",
			"Ocellus (lvl 1601)"
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return List.of(
			new UnlockReward("Access to The Fractured Archive")
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var sections = new ArrayList<PanelDetails>();

		sections.add(new PanelDetails("Starting off", List.of(
			readAlliancesMessage,
			travelIntoGuthixChasm,
			enterCave
		), List.of(
			sapphireLantern
		)));

		sections.add(new PanelDetails("Complete The Fractured Archive", List.of(
			enterArchiveDoor,
			finishTheRaid,
			startOcellusFight,
			finishTheRaid2
		), List.of(
			combatGearAndSupplies
		)));

		sections.add(new PanelDetails("Reporting back", List.of(
			lootThenLeaveRaid,
			speakToArmacus
		)));

		return sections;
	}
}
