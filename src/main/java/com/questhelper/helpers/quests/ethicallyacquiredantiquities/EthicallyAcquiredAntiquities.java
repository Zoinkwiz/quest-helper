/*
 * Copyright (c) 2024, Haavardaw <https://github.com/Haavardaw>
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
package com.questhelper.helpers.quests.ethicallyacquiredantiquities;

import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.questinfo.QuestVarbits;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.TeleportItemRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import static com.questhelper.requirements.util.LogicHelper.not;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.steps.*;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
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

public class EthicallyAcquiredAntiquities extends BasicQuestHelper
{
	//Items Recommended
	ItemRequirement varrockTeleport, civitasIllaFortisTeleport, coinsForCharter, staminaPotion, fixedSail, bettysNotes, storeRoomKey;

	// Quest Items
	ItemRequirement tatteredSail;

	QuestStep talkToCuratorHerminius, investigateToolsBehindDisplayCase, investigateCaseAgain, talkToCitizen, talkToAcademic,
			talkToTourist, talkToRegulus, talkToCrewmember, talkToArtima, returnToCrewmember, talkToTraderStan, talkToBetty, readBettysNotes,
			pickpocketCuratorHaig, searchStoreroomCrate, talkToCuratorBeforeShaming, talkToCuratorBeforeCutscene, watchCutscene;

	PuzzleWrapperStep shameCuratorHaigHalen;

	DetailedQuestStep inspectEmptyDisplayCase, talkToCuratorHaigHalen, returnToCuratorHerminius;

	Requirement investigatedToolsBehindEmptyCase, investigatedCaseAgain, talkedToCitizen, talkedToAcademic, talkedToTourist, talkedToArtima;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		Map<Integer, QuestStep> steps = new HashMap<>();
		initializeRequirements();
		setupConditions();
		setupSteps();

		steps.put(0, inspectEmptyDisplayCase);
		steps.put(2, talkToCuratorHerminius);
		ConditionalStep goInvestigateMuseum = new ConditionalStep(this, investigateToolsBehindDisplayCase);
		goInvestigateMuseum.addStep(investigatedToolsBehindEmptyCase, investigateCaseAgain);
		steps.put(4, goInvestigateMuseum);
		ConditionalStep goTalkToVisitors = new ConditionalStep(this, talkToCitizen);
		goTalkToVisitors.addStep(not(talkedToAcademic), talkToAcademic);
		goTalkToVisitors.addStep(not(talkedToTourist), talkToTourist);
		steps.put(6, goTalkToVisitors);
		steps.put(8, talkToRegulus);
		steps.put(10, talkToCrewmember);
		ConditionalStep goRepairSail = new ConditionalStep(this, talkToArtima);
		goRepairSail.addStep(fixedSail.alsoCheckBank(questBank), returnToCrewmember);
		steps.put(12, goRepairSail);
		steps.put(14, returnToCrewmember);
		steps.put(16, talkToTraderStan);
		steps.put(18, talkToBetty);
		steps.put(20, readBettysNotes);
		steps.put(22, talkToCuratorHaigHalen);
		ConditionalStep goLootRoom = new ConditionalStep(this, pickpocketCuratorHaig);
		goLootRoom.addStep(storeRoomKey, searchStoreroomCrate);
		steps.put(24, goLootRoom);
		steps.put(26, searchStoreroomCrate);
		steps.put(28, talkToCuratorBeforeShaming);
		steps.put(30, shameCuratorHaigHalen);
		steps.put(32, talkToCuratorBeforeCutscene);
		steps.put(34, watchCutscene);
		steps.put(36, returnToCuratorHerminius);

		return steps;
	}

	@Override
	protected void setupZones()
	{

	}

	@Override
	protected void setupRequirements()
	{
		varrockTeleport = new TeleportItemRequirement("Varrock Teleport", ItemID.VARROCK_TELEPORT);
		varrockTeleport.addAlternates(ItemCollections.RING_OF_WEALTHS);
		varrockTeleport.addAlternates(ItemID.CHRONICLE);
		civitasIllaFortisTeleport = new TeleportItemRequirement("Civitas Illa Fortis Teleport", ItemID.CIVITAS_ILLA_FORTIS_TELEPORT);
		coinsForCharter = new ItemRequirement("Coins", ItemID.COINS_995, 3000);
		staminaPotion = new ItemRequirement("Stamina potion", ItemID.STAMINA_POTION4);

		// Quest items
		tatteredSail = new ItemRequirement("Tattered sails", ItemID.TATTERED_SAILS);
		tatteredSail.setTooltip("You can get another from one of the trader crewmembers in the Fortis Cothon");
		fixedSail = new ItemRequirement("Sails", ItemID.SAILS);
		bettysNotes = new ItemRequirement("Betty's Notes", ItemID.BETTYS_NOTES);
		bettysNotes.setTooltip("Talk to Betty in Port Sarim for them");
		bettysNotes.setHighlightInInventory(true);
		storeRoomKey = new ItemRequirement("Key for Storeroom", ItemID.STOREROOM_KEY_29906);
	}

	public void setupConditions()
	{
		investigatedToolsBehindEmptyCase = new VarbitRequirement(11195, 1, Operation.GREATER_EQUAL);
		investigatedCaseAgain = new VarbitRequirement(11194, 1, Operation.GREATER_EQUAL);
		talkedToCitizen = new VarbitRequirement(11196, 1, Operation.GREATER_EQUAL);
		talkedToAcademic = new VarbitRequirement(11198, 1, Operation.GREATER_EQUAL);
		talkedToTourist = new VarbitRequirement(11197, 1, Operation.GREATER_EQUAL);
		talkedToArtima = new VarbitRequirement(11201, 1, Operation.GREATER_EQUAL);
	}

	public void setupSteps()
	{
		inspectEmptyDisplayCase = new ObjectStep(this, ObjectID.EMPTY_DISPLAY_54693, new WorldPoint(1721, 3165, 0), "Inspect the empty display case at the " +
				"Grand Museum in Civitas illa Fortis to start the quest.");
		inspectEmptyDisplayCase.addDialogStep("Yes.");
		inspectEmptyDisplayCase.addTeleport(civitasIllaFortisTeleport);
		talkToCuratorHerminius = new NpcStep(this, NpcID.CURATOR_HERMINIUS, new WorldPoint(1712, 3163, 0), "Speak to Curator Herminius in the centre of the " +
				"Grand Museum.");
		talkToCuratorHerminius.addDialogStep("Could you tell me more about that empty display?");
		investigateToolsBehindDisplayCase = new ObjectStep(this, ObjectID.TOOLS_54700, new WorldPoint(1722, 3167, 0), "Investigate the tools behind the empty " +
				"display case.");
		investigateCaseAgain = new ObjectStep(this, ObjectID.EMPTY_DISPLAY_54693, new WorldPoint(1721, 3165, 0), "Inspect the empty display case again.");
		talkToCitizen = new NpcStep(this, NpcID.CITIZEN_13182, new WorldPoint(1712, 3165, 0), "Talk to any citizen in the room.");
		((NpcStep) talkToCitizen).addAlternateNpcs(NpcID.CITIZEN_13192);
		talkToAcademic = new NpcStep(this, NpcID.ACADEMIC, new WorldPoint(1712, 3165, 0), "Talk to any academic in the room.");
		((NpcStep) talkToAcademic).addAlternateNpcs(NpcID.ACADEMIC_13299, NpcID.ACADEMIC_13300, NpcID.ACADEMIC_13301);
		talkToTourist = new NpcStep(this, NpcID.TOURIST, new WorldPoint(1712, 3165, 0), "Talk to any tourist in the room.");
		((NpcStep) talkToTourist).addAlternateNpcs(NpcID.TOURIST_13207, NpcID.TOURIST_13208, NpcID.TOURIST_13209, NpcID.TOURIST_13210, NpcID.TOURIST_13211);
		talkToRegulus = new NpcStep(this, NpcID.REGULUS_CENTO_12885, new WorldPoint(1701, 3143, 0), "Speak to Regulus Cento south of the Grand Museum.");
		talkToRegulus.addDialogStep("Have you seen anybody suspicious around?");
		talkToCrewmember = new NpcStep(this, NpcID.TRADER_CREWMEMBER, new WorldPoint(1742, 3135, 0), "Head east to the docks and speak to any Trader " +
				"Crewmember.");
		((NpcStep) talkToCrewmember).addAlternateNpcs(getCrewMembers());

		talkToCrewmember.addDialogSteps("Have you seen a man with a case?", "As long as it is just the one favour...");
		talkToArtima = new NpcStep(this, NpcID.ARTIMA, new WorldPoint(1766, 3101, 0), "Head south of the Fortis Cothon to Artima's crafting store and speak to" +
				" her.", tatteredSail);
		talkToArtima.addDialogSteps("I was hoping for some help.", "Go on then.");
		talkToArtima.addDialogStep("So about those sails...");
		returnToCrewmember = new NpcStep(this, NpcID.TRADER_CREWMEMBER, new WorldPoint(1742, 3135, 0), "Return up north and speak to any Trader Crewmember.",
				fixedSail.hideConditioned(new VarbitRequirement(QuestVarbits.QUEST_ETHICALLY_ACQUIRED_ANTIQUITIES.getId(), 14, Operation.GREATER_EQUAL)));
		((NpcStep) returnToCrewmember).addAlternateNpcs(getCrewMembers());

		returnToCrewmember.addDialogStep("So, about that man with the case.");
		talkToTraderStan = new NpcStep(this, NpcID.TRADER_STAN, new WorldPoint(3036, 3194, 0), "Head to Port Sarim and speak to Trader Stan on the southern " +
				"deck. Charter at a cost of 3000 coins.", coinsForCharter);
		talkToTraderStan.addDialogSteps("Port Sarim (wily cats)", "Have you seen a grey-haired man with a case?");
		((NpcStep) talkToTraderStan).addAlternateNpcs(NpcID.TRADER_STAN_9300, NpcID.TRADER_STAN_9301, NpcID.TRADER_STAN_9302, NpcID.TRADER_STAN_9303,
				NpcID.TRADER_STAN_9304, NpcID.TRADER_STAN_9305, NpcID.TRADER_STAN_9307, NpcID.TRADER_STAN_9308, NpcID.TRADER_STAN_9309, NpcID.TRADER_STAN_9310
				, NpcID.TRADER_STAN_9311);
		talkToBetty = new NpcStep(this, NpcID.BETTY_5905, new WorldPoint(3011, 3260, 0), "Head to the Rune Shop in north western Port Sarim and speak to Betty" +
				".");
		talkToBetty.addDialogStep("Have you seen a grey-haired man with a case?");
		// 11206 0->1 is received betty's notes first time
		readBettysNotes = new DetailedQuestStep(this, "Read Betty's notes.", bettysNotes);
		pickpocketCuratorHaig = new NpcStep(this, NpcID.CURATOR_HAIG_HALEN, new WorldPoint(3257, 3449, 0), "Right-click pickpocket Curator Haig Halen.");
		pickpocketCuratorHaig.addIcon(ItemID.THIEVING_CAPE);
		talkToCuratorHaigHalen = new NpcStep(this, NpcID.CURATOR_HAIG_HALEN, new WorldPoint(3257, 3449, 0), "Head to Varrock Museum and speak to Curator Haig " +
				"Halen.");
		talkToCuratorHaigHalen.addDialogStep("I'm looking for Xerna's Diadem.");
		talkToCuratorHaigHalen.addTeleport(varrockTeleport);
		searchStoreroomCrate = new ObjectStep(this, ObjectID.CRATE_54697, new WorldPoint(3266, 3458, 0), "Open the door to the room in the north east and " +
				"search the crate.");
		talkToCuratorBeforeShaming = new NpcStep(this, NpcID.CURATOR_HAIG_HALEN, new WorldPoint(3257, 3449, 0), "Go speak to Curator Haig Halen again.");
		talkToCuratorBeforeShaming.addDialogStep("I found Xerna's Diadem...");
		shameCuratorHaigHalen = new NpcStep(this, NpcID.CURATOR_HAIG_HALEN, new WorldPoint(3257, 3449, 0), "Shame Curator Haig Halen, get the meter to 100%.")
				.addDialogStep("I found Xerna's Diadem...")
				.addDialogSteps("Aren't you embarrassed to have stolen items in your collection?",
				"Are you even a real historian, or just a petty criminal?", "Did you know Varlamore sends thieves to the Colosseum?",
				"How will Varlamorians learn their history now?", "You're supposed to protect history, not steal it!",
				"You're setting a terrible example", "You've betrayed the trust of Varlamore.", "You're not above the law! You stole this stuff!",
				"Is everything here stolen from other museums?", "You're a disgrace to your entire profession!",
				"You're setting a terrible example.", "Varlamore might never let you and your colleagues visit again!",
				"Nobody wants to see stolen goods on display!", "Look me in the eye and tell me you did the right thing.", "Stealing is against the law!",
				"You're a loose cannon.", "If you're looking for stolen goods, I can get my hands on plenty.", "A real historian would never steal!",
				"Hand that artefact back this instant!", "You can't justify theft by saying it's for preservation.", "Do you really think you did the right " +
						"thing?",
				"I guess you were just in it for the glory, were you?", "You've committed a crime.", "You ought to be ashamed.",
				"You're hoarding artefacts, but you should be 1sharing them!", "Varlamore might close their lands again after this.",
				"So can anyone just go around stealing and call it archaeology?", "Stealing is stealing, no matter how you try to justify it.", "Just give it " +
						"back already!",
				"Think of the Varlamorian children who won't get to see this artefact.", "You should give Varlamore a chance before stealing their stuff.",
				"This is stealing! Thieving! Taking what's not yours!", "I thought archaeology was cool. I didn't realise it was just thieving!",
				"How would you feel if someone came in here and stole all your stuff?", "You're hoarding artefacts, but you should be sharing them!")
				.puzzleWrapStep("Work out how to shame Curator Haig Halen, get the meter to 100%.");
		talkToCuratorBeforeCutscene = new NpcStep(this, NpcID.CURATOR_HAIG_HALEN, new WorldPoint(3257, 3449, 0), "Speak to Curator before a cutscene.");
		watchCutscene = new NpcStep(this, NpcID.CURATOR_HAIG_HALEN, new WorldPoint(3257, 3449, 0), "Watch cutscene.");
		returnToCuratorHerminius = new NpcStep(this, NpcID.CURATOR_HERMINIUS, new WorldPoint(1712, 3163, 0), "Speak to the Curator in the centre of the Grand " +
				"Museum to complete the quest.");
		returnToCuratorHerminius.addDialogStep("About that empty display...");
		returnToCuratorHerminius.addTeleport(civitasIllaFortisTeleport);

	}

	private List<Integer> getCrewMembers()
	{
		return List.of(NpcID.TRADER_CREWMEMBER_9364, NpcID.TRADER_CREWMEMBER_9313, NpcID.TRADER_CREWMEMBER_9328,
				NpcID.TRADER_CREWMEMBER_9314, NpcID.TRADER_CREWMEMBER_9315, NpcID.TRADER_CREWMEMBER_9316, NpcID.TRADER_CREWMEMBER_9364,
				NpcID.TRADER_CREWMEMBER_9317, NpcID.TRADER_CREWMEMBER_9318, NpcID.TRADER_CREWMEMBER_9319, NpcID.TRADER_CREWMEMBER_9320,
				NpcID.TRADER_CREWMEMBER_9321, NpcID.TRADER_CREWMEMBER_9322, NpcID.TRADER_CREWMEMBER_9323, NpcID.TRADER_CREWMEMBER_9324,
				NpcID.TRADER_CREWMEMBER_9325, NpcID.TRADER_CREWMEMBER_9326, NpcID.TRADER_CREWMEMBER_9327, NpcID.TRADER_CREWMEMBER_9328,
				NpcID.TRADER_CREWMEMBER_9329, NpcID.TRADER_CREWMEMBER_9330, NpcID.TRADER_CREWMEMBER_9331, NpcID.TRADER_CREWMEMBER_9332,
				NpcID.TRADER_CREWMEMBER_9334, NpcID.TRADER_CREWMEMBER_9335, NpcID.TRADER_CREWMEMBER_9336, NpcID.TRADER_CREWMEMBER_9337,
				NpcID.TRADER_CREWMEMBER_9338, NpcID.TRADER_CREWMEMBER_9339, NpcID.TRADER_CREWMEMBER_9340, NpcID.TRADER_CREWMEMBER_9341,
				NpcID.TRADER_CREWMEMBER_9342, NpcID.TRADER_CREWMEMBER_9343, NpcID.TRADER_CREWMEMBER_9344, NpcID.TRADER_CREWMEMBER_9345,
				NpcID.TRADER_CREWMEMBER_9346, NpcID.TRADER_CREWMEMBER_9347, NpcID.TRADER_CREWMEMBER_9348, NpcID.TRADER_CREWMEMBER_9349,
				NpcID.TRADER_CREWMEMBER_9350, NpcID.TRADER_CREWMEMBER_9351, NpcID.TRADER_CREWMEMBER_9352, NpcID.TRADER_CREWMEMBER_9353,
				NpcID.TRADER_CREWMEMBER_9354, NpcID.TRADER_CREWMEMBER_9355, NpcID.TRADER_CREWMEMBER_9356, NpcID.TRADER_CREWMEMBER_9357,
				NpcID.TRADER_CREWMEMBER_9358, NpcID.TRADER_CREWMEMBER_9359, NpcID.TRADER_CREWMEMBER_9360, NpcID.TRADER_CREWMEMBER_9361,
				NpcID.TRADER_CREWMEMBER_9362, NpcID.TRADER_CREWMEMBER_9363, NpcID.TRADER_CREWMEMBER_9364, NpcID.TRADER_CREWMEMBER_9365,
				NpcID.TRADER_CREWMEMBER_9366, NpcID.TRADER_CREWMEMBER_9367, NpcID.TRADER_CREWMEMBER_9368, NpcID.TRADER_CREWMEMBER_9369,
				NpcID.TRADER_CREWMEMBER_9370, NpcID.TRADER_CREWMEMBER_9371, NpcID.TRADER_CREWMEMBER_9372, NpcID.TRADER_CREWMEMBER_9373,
				NpcID.TRADER_CREWMEMBER_9374, NpcID.TRADER_CREWMEMBER_9375, NpcID.TRADER_CREWMEMBER_9376, NpcID.TRADER_CREWMEMBER_9377,
				NpcID.TRADER_CREWMEMBER_9378, NpcID.TRADER_CREWMEMBER_9379, NpcID.TRADER_CREWMEMBER_9380, NpcID.TRADER_CREWMEMBER_9381,
				NpcID.TRADER_CREWMEMBER_9382, NpcID.TRADER_CREWMEMBER_9383, NpcID.TRADER_CREWMEMBER_12632, NpcID.TRADER_CREWMEMBER_12633,
				NpcID.TRADER_CREWMEMBER_12634, NpcID.TRADER_CREWMEMBER_12635, NpcID.TRADER_CREWMEMBER_12636,
				NpcID.TRADER_CREWMEMBER_12637, NpcID.TRADER_CREWMEMBER_12637, NpcID.TRADER_CREWMEMBER_12638, NpcID.TRADER_CREWMEMBER_12639,
				NpcID.TRADER_CREWMEMBER_12640, NpcID.TRADER_CREWMEMBER_12641, NpcID.TRADER_CREWMEMBER_12642, NpcID.TRADER_CREWMEMBER_12643);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(coinsForCharter);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(varrockTeleport, civitasIllaFortisTeleport.quantity(2), staminaPotion);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return List.of(
				new QuestRequirement(QuestHelperQuest.CHILDREN_OF_THE_SUN, QuestState.FINISHED),
				new QuestRequirement(QuestHelperQuest.SHIELD_OF_ARRAV_BLACK_ARM_GANG, QuestState.FINISHED, "Finished Shield of Arrav"),
				new SkillRequirement(Skill.THIEVING, 25, false)
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
		return Collections.singletonList(
				new ExperienceReward(Skill.THIEVING, 6000)
		);
	}

	@Override
	public List<ItemReward> getItemRewards() {
		return Arrays.asList(
				new ItemReward("Coins", ItemID.COINS_995, 5000));
	}

	@Override
	public List<PanelDetails> getPanels() {
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Grand Museum", Arrays.asList(inspectEmptyDisplayCase, talkToCuratorHerminius, investigateToolsBehindDisplayCase, investigateCaseAgain, talkToAcademic, talkToTourist, talkToCitizen), Arrays.asList(civitasIllaFortisTeleport)));

		allSteps.add(new PanelDetails("Done with museum visit", Arrays.asList(talkToRegulus, talkToCrewmember, talkToArtima, returnToCrewmember, talkToTraderStan, talkToBetty, readBettysNotes), Arrays.asList(coinsForCharter)));

		allSteps.add(new PanelDetails("Nevermind, another one", Arrays.asList(talkToCuratorHaigHalen, pickpocketCuratorHaig, searchStoreroomCrate, talkToCuratorBeforeShaming,
				shameCuratorHaigHalen, talkToCuratorBeforeCutscene, watchCutscene, returnToCuratorHerminius), Arrays.asList(varrockTeleport, civitasIllaFortisTeleport)));
		return allSteps;

	}
}
