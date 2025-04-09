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
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.steps.*;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;

import java.util.*;

import static com.questhelper.requirements.util.LogicHelper.not;

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
		varrockTeleport = new TeleportItemRequirement("Varrock Teleport", ItemID.POH_TABLET_VARROCKTELEPORT);
		varrockTeleport.addAlternates(ItemCollections.RING_OF_WEALTHS);
		varrockTeleport.addAlternates(ItemID.CHRONICLE);
		civitasIllaFortisTeleport = new TeleportItemRequirement("Civitas Illa Fortis Teleport", ItemID.POH_TABLET_FORTISTELEPORT);
		coinsForCharter = new ItemRequirement("Coins", ItemID.COINS, 3000);
		staminaPotion = new ItemRequirement("Stamina potion", ItemID._4DOSESTAMINA);

		// Quest items
		tatteredSail = new ItemRequirement("Tattered sails", ItemID.EAA_TATTERED_SAIL);
		tatteredSail.setTooltip("You can get another from one of the trader crewmembers in the Fortis Cothon");
		fixedSail = new ItemRequirement("Sails", ItemID.EAA_FIXED_SAIL);
		bettysNotes = new ItemRequirement("Betty's Notes", ItemID.EAA_RUNE_ORDER);
		bettysNotes.setTooltip("Talk to Betty in Port Sarim for them");
		bettysNotes.setHighlightInInventory(true);
		storeRoomKey = new ItemRequirement("Key for Storeroom", ItemID.EAA_STORE_ROOM_KEY);
	}

	public void setupConditions()
	{
		investigatedToolsBehindEmptyCase = new VarbitRequirement(VarbitID.EAA_INVESTIGATED_TOOLS, 1, Operation.GREATER_EQUAL);
		investigatedCaseAgain = new VarbitRequirement(VarbitID.EAA_INVESTIGATED_DISPLAY, 1, Operation.GREATER_EQUAL);
		talkedToCitizen = new VarbitRequirement(VarbitID.EAA_QUESTIONED_CITIZEN, 1, Operation.GREATER_EQUAL);
		talkedToAcademic = new VarbitRequirement(VarbitID.EAA_QUESTIONED_ACADEMIC, 1, Operation.GREATER_EQUAL);
		talkedToTourist = new VarbitRequirement(VarbitID.EAA_QUESTIONED_TOURIST, 1, Operation.GREATER_EQUAL);
		talkedToArtima = new VarbitRequirement(VarbitID.EAA_CRAFTING_FAVOUR_ASKED, 1, Operation.GREATER_EQUAL);
	}

	public void setupSteps()
	{
		inspectEmptyDisplayCase = new ObjectStep(this, ObjectID.CIVITAS_MUSEUM_DISPLAY_DIADEM_EMPTY, new WorldPoint(1721, 3165, 0), "Inspect the empty display case at the " +
				"Grand Museum in Civitas illa Fortis to start the quest.");
		inspectEmptyDisplayCase.addDialogStep("Yes.");
		inspectEmptyDisplayCase.addTeleport(civitasIllaFortisTeleport);
		talkToCuratorHerminius = new NpcStep(this, NpcID.FORTIS_MUSEUM_CURATOR, new WorldPoint(1712, 3163, 0), "Speak to Curator Herminius in the centre of the " +
				"Grand Museum.");
		talkToCuratorHerminius.addDialogStep("Could you tell me more about that empty display?");
		investigateToolsBehindDisplayCase = new ObjectStep(this, ObjectID.EAA_TOOLS_INVESTIGATE, new WorldPoint(1722, 3167, 0), "Investigate the tools behind the empty " +
				"display case.");
		investigateCaseAgain = new ObjectStep(this, ObjectID.CIVITAS_MUSEUM_DISPLAY_DIADEM_EMPTY, new WorldPoint(1721, 3165, 0), "Inspect the empty display case again.");
		talkToCitizen = new NpcStep(this, NpcID.VARLAMORE_CITIZEN_NORMAL_M_5, new WorldPoint(1712, 3165, 0), "Talk to any citizen in the room.");
		((NpcStep) talkToCitizen).addAlternateNpcs(NpcID.VARLAMORE_CITIZEN_RICH_M_1);
		talkToAcademic = new NpcStep(this, NpcID.FORTIS_ACADEMIC_01, new WorldPoint(1712, 3165, 0), "Talk to any academic in the room.");
		((NpcStep) talkToAcademic).addAlternateNpcs(NpcID.FORTIS_ACADEMIC_02, NpcID.FORTIS_ACADEMIC_03, NpcID.FORTIS_ACADEMIC_04);
		talkToTourist = new NpcStep(this, NpcID.VARLAMORE_TOURIST_M_1, new WorldPoint(1712, 3165, 0), "Talk to any tourist in the room.");
		((NpcStep) talkToTourist).addAlternateNpcs(NpcID.VARLAMORE_TOURIST_M_2, NpcID.VARLAMORE_TOURIST_M_3, NpcID.VARLAMORE_TOURIST_F_1, NpcID.VARLAMORE_TOURIST_F_2, NpcID.VARLAMORE_TOURIST_F_3);
		talkToRegulus = new NpcStep(this, NpcID.VMQ2_QUETZAL_KEEPER_FORTIS, new WorldPoint(1701, 3143, 0), "Speak to Regulus Cento south of the Grand Museum.");
		talkToRegulus.addDialogStep("Have you seen anybody suspicious around?");
		talkToCrewmember = new NpcStep(this, NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_MAN1_BASE, new WorldPoint(1742, 3135, 0), "Head east to the docks and speak to any Trader " +
				"Crewmember.");
		((NpcStep) talkToCrewmember).addAlternateNpcs(getCrewMembers());

		talkToCrewmember.addDialogSteps("Have you seen a man with a case?", "As long as it is just the one favour...");
		talkToArtima = new NpcStep(this, NpcID.FORTIS_SHOP_CRAFTING, new WorldPoint(1766, 3101, 0), "Head south of the Fortis Cothon to Artima's crafting store and speak to" +
				" her.", tatteredSail);
		talkToArtima.addDialogSteps("I was hoping for some help.", "Go on then.");
		talkToArtima.addDialogStep("So about those sails...");
		returnToCrewmember = new NpcStep(this, NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_MAN1_BASE, new WorldPoint(1742, 3135, 0), "Return up north and speak to any Trader Crewmember.",
				fixedSail.hideConditioned(new VarbitRequirement(QuestVarbits.QUEST_ETHICALLY_ACQUIRED_ANTIQUITIES.getId(), 14, Operation.GREATER_EQUAL)));
		((NpcStep) returnToCrewmember).addAlternateNpcs(getCrewMembers());

		returnToCrewmember.addDialogStep("So, about that man with the case.");
		talkToTraderStan = new NpcStep(this, NpcID.SAILING_TRANSPORT_TRADER_STAN_BASE, new WorldPoint(3036, 3194, 0), "Head to Port Sarim and speak to Trader Stan on the southern " +
				"deck. Charter at a cost of 3000 coins.", coinsForCharter);
		talkToTraderStan.addDialogSteps("Port Sarim (wily cats)", "Have you seen a grey-haired man with a case?");
		((NpcStep) talkToTraderStan).addAlternateNpcs(NpcID.SAILING_TRANSPORT_TRADER_STAN_BRIMHAVEN, NpcID.SAILING_TRANSPORT_TRADER_STAN_CATHERBY, NpcID.SAILING_TRANSPORT_TRADER_STAN_MOSLEHARMLESS, NpcID.SAILING_TRANSPORT_TRADER_STAN_MUSAPOINT,
				NpcID.SAILING_TRANSPORT_TRADER_STAN_PORTKHAZARD, NpcID.SAILING_TRANSPORT_TRADER_STAN_PORTPHASMATYS, NpcID.SAILING_TRANSPORT_TRADER_STAN_PORTSARIM, NpcID.SAILING_TRANSPORT_TRADER_STAN_SHIPYARD, NpcID.SAILING_TRANSPORT_TRADER_STAN_CORSAIRCOVE, NpcID.SAILING_TRANSPORT_TRADER_STAN_PRIFDDINAS
				, NpcID.SAILING_TRANSPORT_TRADER_STAN_PORTTYRAS);
		talkToBetty = new NpcStep(this, NpcID.SARIM_BETTY, new WorldPoint(3011, 3260, 0), "Head to the Rune Shop in north western Port Sarim and speak to Betty" +
				".");
		talkToBetty.addDialogStep("Have you seen a grey-haired man with a case?");
		// 11206 0->1 is received betty's notes first time
		readBettysNotes = new DetailedQuestStep(this, "Read Betty's notes.", bettysNotes);
		pickpocketCuratorHaig = new NpcStep(this, NpcID.CURATOR, new WorldPoint(3257, 3449, 0), "Right-click pickpocket Curator Haig Halen.");
		pickpocketCuratorHaig.addIcon(ItemID.SKILLCAPE_THIEVING);
		talkToCuratorHaigHalen = new NpcStep(this, NpcID.CURATOR, new WorldPoint(3257, 3449, 0), "Head to Varrock Museum and speak to Curator Haig " +
				"Halen.");
		talkToCuratorHaigHalen.addDialogStep("I'm looking for Xerna's Diadem.");
		talkToCuratorHaigHalen.addTeleport(varrockTeleport);
		searchStoreroomCrate = new ObjectStep(this, ObjectID.EAA_LARGE_CRATE, new WorldPoint(3266, 3458, 0), "Open the door to the room in the north east and " +
				"search the crate.");
		talkToCuratorBeforeShaming = new NpcStep(this, NpcID.CURATOR, new WorldPoint(3257, 3449, 0), "Go speak to Curator Haig Halen again.");
		talkToCuratorBeforeShaming.addDialogStep("I found Xerna's Diadem...");
		shameCuratorHaigHalen = new NpcStep(this, NpcID.CURATOR, new WorldPoint(3257, 3449, 0), "Shame Curator Haig Halen, get the meter to 100%.")
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
		talkToCuratorBeforeCutscene = new NpcStep(this, NpcID.CURATOR, new WorldPoint(3257, 3449, 0), "Speak to Curator before a cutscene.");
		watchCutscene = new NpcStep(this, NpcID.CURATOR, new WorldPoint(3257, 3449, 0), "Watch cutscene.");
		returnToCuratorHerminius = new NpcStep(this, NpcID.FORTIS_MUSEUM_CURATOR, new WorldPoint(1712, 3163, 0), "Speak to the Curator in the centre of the Grand " +
				"Museum to complete the quest.");
		returnToCuratorHerminius.addDialogStep("About that empty display...");
		returnToCuratorHerminius.addTeleport(civitasIllaFortisTeleport);

	}

	private List<Integer> getCrewMembers()
	{
		return List.of(NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_WOMAN2_MUSAPOINT, NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_MAN1_BRIMHAVEN, NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_MAN2_MUSAPOINT,
				NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_MAN1_CATHERBY, NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_MAN1_MOSLEHARMLESS, NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_MAN1_MUSAPOINT, NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_WOMAN2_MUSAPOINT,
				NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_MAN1_PORTKHAZARD, NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_MAN1_PORTPHASMATYS, NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_MAN1_PORTSARIM, NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_MAN1_SHIPYARD,
				NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_MAN1_PORTTYRAS, NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_MAN1_CORSAIRCOVE, NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_MAN1_PRIFDDINAS, NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_MAN2_BASE,
				NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_MAN2_BRIMHAVEN, NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_MAN2_CATHERBY, NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_MAN2_MOSLEHARMLESS, NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_MAN2_MUSAPOINT,
				NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_MAN2_PORTKHAZARD, NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_MAN2_PORTPHASMATYS, NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_MAN2_PORTSARIM, NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_MAN2_SHIPYARD,
				NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_MAN2_CORSAIRCOVE, NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_MAN2_PRIFDDINAS, NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_MAN3_BASE, NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_MAN3_BRIMHAVEN,
				NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_MAN3_CATHERBY, NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_MAN3_MOSLEHARMLESS, NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_MAN3_MUSAPOINT, NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_MAN3_PORTKHAZARD,
				NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_MAN3_PORTPHASMATYS, NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_MAN3_PORTSARIM, NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_MAN3_SHIPYARD, NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_MAN3_PORTTYRAS,
				NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_MAN3_CORSAIRCOVE, NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_MAN3_PRIFDDINAS, NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_WOMAN1_BASE, NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_WOMAN1_BRIMHAVEN,
				NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_WOMAN1_CATHERBY, NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_WOMAN1_MOSLEHARMLESS, NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_WOMAN1_MUSAPOINT, NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_WOMAN1_PORTKHAZARD,
				NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_WOMAN1_PORTPHASMATYS, NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_WOMAN1_PORTSARIM, NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_WOMAN1_SHIPYARD, NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_WOMAN1_PORTTYRAS,
				NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_WOMAN1_CORSAIRCOVE, NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_WOMAN1_PRIFDDINAS, NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_WOMAN2_BASE, NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_WOMAN2_BRIMHAVEN,
				NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_WOMAN2_CATHERBY, NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_WOMAN2_MOSLEHARMLESS, NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_WOMAN2_MUSAPOINT, NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_WOMAN2_PORTKHAZARD,
				NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_WOMAN2_PORTPHASMATYS, NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_WOMAN2_PORTSARIM, NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_WOMAN2_SHIPYARD, NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_WOMAN2_PORTTYRAS,
				NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_WOMAN2_CORSAIRCOVE, NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_WOMAN2_PRIFDDINAS, NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_WOMAN3_BASE, NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_WOMAN3_BRIMHAVEN,
				NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_WOMAN3_CATHERBY, NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_WOMAN3_MOSLEHARMLESS, NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_WOMAN3_MUSAPOINT, NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_WOMAN3_PORTKHAZARD,
				NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_WOMAN3_PORTPHASMATYS, NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_WOMAN3_PORTSARIM, NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_WOMAN3_SHIPYARD, NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_WOMAN3_PORTTYRAS,
				NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_WOMAN3_CORSAIRCOVE, NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_WOMAN3_PRIFDDINAS, NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_MAN1_PISCARILIUS, NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_MAN1_LANDSEND,
				NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_MAN2_PISCARILIUS, NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_MAN2_LANDSEND, NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_MAN3_PISCARILIUS,
				NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_MAN3_LANDSEND, NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_MAN3_LANDSEND, NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_WOMAN1_PISCARILIUS, NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_WOMAN1_LANDSEND,
				NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_WOMAN2_PISCARILIUS, NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_WOMAN2_LANDSEND, NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_WOMAN3_PISCARILIUS, NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_WOMAN3_LANDSEND);
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
				new ItemReward("Coins", ItemID.COINS, 5000));
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
