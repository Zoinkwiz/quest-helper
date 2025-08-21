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
package com.questhelper.helpers.quests.twilightspromise;

import com.questhelper.bank.banktab.BankSlotIcons;
import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.npc.NpcRequirement;
import com.questhelper.requirements.player.InInstanceRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.*;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.*;

import java.util.*;

import static com.questhelper.requirements.util.LogicHelper.and;
import static com.questhelper.requirements.util.LogicHelper.nor;

public class TwilightsPromise extends BasicQuestHelper
{
	//Items Required
	ItemRequirement twoCombatStyles;

	ItemRequirement staminatPotion;

	ItemRequirement varlamoreCrest, stolenAmulet, incriminatingLetter, quetzalFeed;

	Requirement beenToVarlamore, talkedToCothonKnight, foundCrate, finishedCothonKnight;

	// Pre-quest
	QuestStep talkToRegulusStart;

	QuestStep talkToEnnius, talkToMetzli, enterCrypt, talkToPrince, leaveCrypt, talkToEnnius2;

	/* Knights */

	// Cothon
	QuestStep talkToCothonKnight, searchCrate, returnToCothonKnight;

	// Colosseum
	QuestStep enterColosseum, talkToColosseumKnight, defeatColosseumKnight, talkToColosseumKnight2;
	Requirement talkedToColosseumKnight, knightFightNearby, defeatedColosseumKnight, finishedColosseumKnight;

	// Bazzar
	QuestStep talkToBazaarKnight, pickpocketCitizen, returnAmulet;
	Requirement talkedToBazaarKnight, finishedBazaarKnight;

	// Pub
	QuestStep talkToPubKnights, takePubKnightsToFountain, talkToPubKnightAtFountain;
	Requirement talkedToPubKnights, pubKnightFollowing, pubKnightSobered, finishedPubKnights;

	QuestStep talkToEnniusAfterKnights, climbStairs, goUpHQ, goUpHQ2, searchHQChest, readLetter,
		goDownHQ2To1, goDownHQ1To0, returnToEnniusAfterLetter;

	QuestStep talkToRegulusForTransport, feedRenu, travelToTeomat, talkToPrinceInTemple,
		talkToMetzliNearTemple, defeat8Cultists, finishQuest;

	//Zones
	Zone crypt, colosseumUnderground, colosseum, hq1, hq2, teomat;

	Requirement inCrypt, inColosseumUnderground, inColosseum, inHQ1, inHQ2, nearTeomat;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		Map<Integer, QuestStep> steps = new HashMap<>();
		initializeRequirements();
		setupConditions();
		setupSteps();

		ConditionalStep goStart = new ConditionalStep(this, talkToRegulusStart);
		goStart.addStep(beenToVarlamore, talkToEnnius);
		steps.put(0, goStart);
		steps.put(2, goStart);

		steps.put(4, talkToMetzli);
		steps.put(6, talkToMetzli);

		ConditionalStep goTalkToPrince = new ConditionalStep(this, enterCrypt);
		goTalkToPrince.addStep(inCrypt, talkToPrince);
		steps.put(8, goTalkToPrince);
		steps.put(10, goTalkToPrince);

		ConditionalStep returnToEnnius = new ConditionalStep(this, talkToEnnius2);
		returnToEnnius.addStep(inCrypt, leaveCrypt);
		steps.put(12, returnToEnnius);

		// Cothon
		ConditionalStep findCothonKnight = new ConditionalStep(this, talkToCothonKnight);
		findCothonKnight.addStep(foundCrate, returnToCothonKnight);
		findCothonKnight.addStep(talkedToCothonKnight, searchCrate);

		// Colosseum
		ConditionalStep findColosseumKnight = new ConditionalStep(this, enterColosseum);
		findColosseumKnight.addStep(and(inColosseumUnderground, defeatedColosseumKnight), talkToColosseumKnight2);
		findColosseumKnight.addStep(knightFightNearby, defeatColosseumKnight);
		findColosseumKnight.addStep(inColosseumUnderground, talkToColosseumKnight);

		// Pub
		ConditionalStep findPubKnights = new ConditionalStep(this, talkToPubKnights);
		// TODO: Verify this is right, as I progressed by talking to the one at the pub
		findPubKnights.addStep(pubKnightSobered, talkToPubKnightAtFountain);
		findPubKnights.addStep(pubKnightFollowing, takePubKnightsToFountain);

		ConditionalStep findBazaarKnight = new ConditionalStep(this, talkToBazaarKnight);
		findBazaarKnight.addStep(stolenAmulet.alsoCheckBank(questBank), returnAmulet);
		findBazaarKnight.addStep(talkedToBazaarKnight, pickpocketCitizen);

		ConditionalStep findKnights = new ConditionalStep(this, findColosseumKnight);
		findKnights.addStep(nor(finishedBazaarKnight), findBazaarKnight);
		findKnights.addStep(nor(finishedCothonKnight), findCothonKnight);
		findKnights.addStep(nor(finishedPubKnights), findPubKnights);

		steps.put(14, findKnights);
		steps.put(16, findKnights);
		steps.put(18, findKnights);
		steps.put(20, findKnights);

		ConditionalStep enniusAfterKnights = new ConditionalStep(this, talkToEnniusAfterKnights);
		enniusAfterKnights.addStep(inColosseumUnderground, climbStairs);
		steps.put(22, enniusAfterKnights);


		ConditionalStep goReadLetter = new ConditionalStep(this, goUpHQ);
		goReadLetter.addStep(incriminatingLetter.alsoCheckBank(questBank), readLetter);
		goReadLetter.addStep(inHQ2, searchHQChest);
		goReadLetter.addStep(inHQ1, goUpHQ2);
		steps.put(24, goReadLetter);
		steps.put(26, goReadLetter);

		ConditionalStep goBringLetter = new ConditionalStep(this, returnToEnniusAfterLetter);
		goBringLetter.addStep(inHQ2, goDownHQ2To1);
		goBringLetter.addStep(inHQ1, goDownHQ1To0);
		steps.put(28, goBringLetter);
		steps.put(30, goBringLetter);
		steps.put(32, goBringLetter);
		// varp 3679 -1 -> 3512

		steps.put(34, talkToRegulusForTransport);
		steps.put(36, feedRenu);

		ConditionalStep goToTeomat = new ConditionalStep(this, travelToTeomat);
		goToTeomat.addStep(nearTeomat, talkToPrinceInTemple);
		steps.put(38, goToTeomat);
		steps.put(40, goToTeomat);

		steps.put(42, talkToMetzliNearTemple);

		ConditionalStep goDefeatCultists = new ConditionalStep(this, talkToMetzliNearTemple);
		goDefeatCultists.addStep(and(nearTeomat, new InInstanceRequirement()), defeat8Cultists);
		steps.put(44, goDefeatCultists);

		steps.put(46, finishQuest);
		steps.put(48, finishQuest);
		
		return steps;
	}

	@Override
	protected void setupZones()
	{
		crypt = new Zone(6804);
		colosseumUnderground = new Zone(7316);
		colosseum = new Zone(new WorldPoint(1806, 3088, 0), new WorldPoint(1843, 3125, 0));
		hq1 = new Zone(new WorldPoint(1636, 3137, 1), new WorldPoint(1654, 3158, 1));
		hq2 = new Zone(new WorldPoint(1636, 3137, 2), new WorldPoint(1654, 3158, 2));
		teomat = new Zone(new WorldPoint(1377, 2951, 0), new WorldPoint(1536, 3262, 3));
	}

	@Override
	protected void setupRequirements()
	{
		twoCombatStyles = new ItemRequirement("Two combat styles", -1, -1).isNotConsumed();
		twoCombatStyles.setDisplayItemId(BankSlotIcons.getCombatGear());
		staminatPotion = new ItemRequirement("Stamina potions", ItemCollections.STAMINA_POTIONS, 3);

		varlamoreCrest = new ItemRequirement("Varlamore crest", ItemID.VMQ2_CREST);
		varlamoreCrest.setTooltip("You can get another from Ennius in the palace.");
		stolenAmulet = new ItemRequirement("Stolen amulet", ItemID.VMQ2_AMULET);
		incriminatingLetter = new ItemRequirement("Incriminating letter", ItemID.VMQ2_INCRIMINATING_LETTER);
		quetzalFeed = new ItemRequirement("Quetzel feed", ItemID.VMQ2_QUETZAL_FEED);
		quetzalFeed.setTooltip("You can get more from Regulus Cento");
	}

	private void setupConditions()
	{
		beenToVarlamore = new VarbitRequirement(VarbitID.VARLAMORE_VISITED, 1);
		inCrypt = new ZoneRequirement(crypt);
		inColosseumUnderground = new ZoneRequirement(colosseumUnderground);
		inColosseum = new ZoneRequirement(colosseum);

		talkedToBazaarKnight = new VarbitRequirement(VarbitID.VMQ2_BAZAAR_KNIGHTS, 1, Operation.GREATER_EQUAL);
		// 2 is first time pickpocketing amulet
		finishedBazaarKnight = new VarbitRequirement(VarbitID.VMQ2_BAZAAR_KNIGHTS, 3, Operation.GREATER_EQUAL);

		talkedToCothonKnight = new VarbitRequirement(VarbitID.VMQ2_COTHON_KNIGHT, 1, Operation.GREATER_EQUAL);
		foundCrate = new VarbitRequirement(VarbitID.VMQ2_COTHON_KNIGHT, 2, Operation.GREATER_EQUAL);
		finishedCothonKnight = new VarbitRequirement(VarbitID.VMQ2_COTHON_KNIGHT, 3, Operation.GREATER_EQUAL);

		talkedToPubKnights = new VarbitRequirement(VarbitID.VMQ2_PUB_KNIGHTS, 1, Operation.GREATER_EQUAL);
		pubKnightFollowing = new VarplayerRequirement(VarPlayerID.FOLLOWER_NPC, List.of(13393, NpcID.VMQ2_KNIGHT_5_DRUNK), 16);
		pubKnightSobered = new VarbitRequirement(VarbitID.VMQ2_PUB_KNIGHTS, 2, Operation.GREATER_EQUAL);
		finishedPubKnights = new VarbitRequirement(VarbitID.VMQ2_PUB_KNIGHTS, 3, Operation.GREATER_EQUAL);

		talkedToColosseumKnight = new VarbitRequirement(VarbitID.VMQ2_COLOSSEUM_KNIGHT, 1, Operation.GREATER_EQUAL);
		knightFightNearby = new NpcRequirement(NpcID.VMQ2_KNIGHT_6_COMBAT);
		defeatedColosseumKnight = new VarbitRequirement(VarbitID.VMQ2_COLOSSEUM_KNIGHT, 2, Operation.GREATER_EQUAL);
		finishedColosseumKnight = new VarbitRequirement(VarbitID.VMQ2_COLOSSEUM_KNIGHT, 3, Operation.GREATER_EQUAL);

		inHQ1 = new ZoneRequirement(hq1);
		inHQ2 = new ZoneRequirement(hq2);
		nearTeomat = new ZoneRequirement(teomat);
	}

	private void setupSteps()
	{
		talkToRegulusStart = new NpcStep(this, NpcID.VMQ2_QUETZAL_KEEPER_1OP, new WorldPoint(3281, 3413, 0),
			"Talk to Regulus Cento outside Varrock's East Gate to travel to Varlamore.");
		talkToRegulusStart.addDialogStep("Let's do it!");

		talkToEnnius = new NpcStep(this, NpcID.VMQ2_ENNIUS_VIS, new WorldPoint(1687, 3141, 0),
			"Talk to Ennius Tullus just west of where you arrived, next to the fountain.");
		talkToEnnius.addDialogSteps("Yes.");
		talkToMetzli = new NpcStep(this, NpcID.VMQ2_METZLI_VIS, new WorldPoint(1697, 3087, 0),
			"Talk to Metzli in the temple in the south of the city.");
		talkToMetzli.addDialogStep("I'm meant to be meeting Prince Itzla here.");
		enterCrypt = new ObjectStep(this, ObjectID.VMQ2_TEMPLE_STAIRS_TOP, new WorldPoint(1692, 3088, 0),
			"Enter the temple's crypt.");
		enterCrypt.addDialogStep("I'd better head down there.");
		talkToPrince = new NpcStep(this, NpcID.VMQ2_ITZLA_VIS, new WorldPoint(1685, 9514, 0),
			"Talk to Prince Itzla Arkan in the north room.");
		leaveCrypt = new ObjectStep(this, ObjectID.VMQ2_TEMPLE_STAIRS_BOTTOM, new WorldPoint(1692, 9492, 0),
			"Leave the crypt.");
		talkToEnnius2 = new NpcStep(this, NpcID.VMQ2_ENNIUS_VIS, new WorldPoint(1684, 3156, 0),
			"Return to Ennius Tullus, who is now in the palace.");
		// Crest given
		// 12->14
		// 9834 0->1
		// 9833 0->1
		talkToCothonKnight = new NpcStep(this, NpcID.VMQ2_KNIGHT_3_VIS, new WorldPoint(1746, 3120, 0),
			"Talk to the Knight of Varlamore on the south western part of the Fortis Cothon (docks) in the east of the city.", varlamoreCrest);
		searchCrate = new ObjectStep(this, ObjectID.VMQ2_COTHON_CRATE, new WorldPoint(1778, 3149, 0),
			"Search the crate in the north-east of the Cothon, next to two barrels of fish.");
		returnToCothonKnight = new NpcStep(this, NpcID.VMQ2_KNIGHT_3_VIS, new WorldPoint(1746, 3120, 0),
		"Return to the Knight of Varlamore on the south part of the Fortis Cothon.");

		enterColosseum = new ObjectStep(this, ObjectID.COLOSSEUM_ENTRANCE_OUTSIDE, new WorldPoint(1796, 3106, 0),
			"Enter the Colosseum in the east of the city.", varlamoreCrest, twoCombatStyles);
		talkToColosseumKnight = new NpcStep(this, NpcID.VMQ2_KNIGHT_6_VIS, new WorldPoint(1805, 9522, 0),
			"Talk to the Knight of Varlamore in the north training room, ready to fight.", varlamoreCrest, twoCombatStyles);
		talkToColosseumKnight.addDialogStep("I'm ready. Let's do this.");
		// 12401 0->1 with bar?
		defeatColosseumKnight = new NpcStep(this, NpcID.VMQ2_KNIGHT_6_COMBAT, new WorldPoint(1824, 3105, 0),
			"Defeat the knight, swapping combat styles to bypass his prayers.", twoCombatStyles);
		talkToColosseumKnight2 = new NpcStep(this, NpcID.VMQ2_KNIGHT_6_VIS, new WorldPoint(1805, 9522, 0),
			"Talk to the Knight of Varlamore in the northern training room once more.");

		talkToPubKnights = new NpcStep(this, NpcID.VMQ2_KNIGHT_5_DRUNK, new WorldPoint(1721, 3075, 0),
			"Talk to the Knights of Varlamore in the pub OUTSIDE the walls of the city to the south.", varlamoreCrest);
		takePubKnightsToFountain = new DetailedQuestStep(this, new WorldPoint(1757, 3069, 0),
			"Lead the knight to the fountain to the east. Whenever they stop following you, talk to them again.");
		// 9834 1->2 when following you
		talkToPubKnightAtFountain = new NpcStep(this, NpcID.VMQ2_KNIGHT_5_VIS, new WorldPoint(1756, 3069, 0),
			"Talk to the sobered knight at the fountain.");

		talkToBazaarKnight = new NpcStep(this, NpcID.VMQ2_KNIGHT_1_VIS, new WorldPoint(1682, 3104, 0),
			"Talk to the Knight of Varlamore in the bazaar south of the palace.", varlamoreCrest);
		pickpocketCitizen = new NpcStep(this, NpcID.VMQ2_CITIZEN_VIS, new WorldPoint(1686, 3109, 0),
			"Pickpocket the citizen in turquoise robes in the bazaar.");
		returnAmulet = new NpcStep(this, NpcID.VMQ2_KNIGHT_1_VIS, new WorldPoint(1682, 3104, 0),
		"Return the amulet to the knight in the bazaar.", stolenAmulet);

		talkToEnniusAfterKnights = new NpcStep(this, NpcID.VMQ2_ENNIUS_VIS, new WorldPoint(1684, 3156, 0), "Return to Ennius Tullus in the palace.");
		climbStairs = new ObjectStep(this, ObjectID.COLOSSEUM_EXIT_LOBBY, new WorldPoint(1796, 9506, 0), "Return to Ennius Tullus in the palace.");
		talkToEnniusAfterKnights.addSubSteps(climbStairs);

		goUpHQ = new ObjectStep(this, ObjectID.CIVITAS_STAIRS_SPIRAL, new WorldPoint(1638, 3155, 0),
			"Go to the top floor of the Kualti Headquarters, just west of the palace.");
		goUpHQ2 = new ObjectStep(this, ObjectID.CIVITAS_STAIRS_SPIRAL, new WorldPoint(1650, 3155, 1),
			"Go to the top floor of the Kualti Headquarters, just west of the palace.");
		goUpHQ.addSubSteps(goUpHQ2);
		searchHQChest = new ObjectStep(this, ObjectID.VMQ2_KNIGHT_4_CHEST, new WorldPoint(1648, 3142, 2),
			"Search the chest in the south-east room on the south wall.");
		readLetter = new DetailedQuestStep(this, "Read the incriminating letter.", incriminatingLetter.highlighted());
		goDownHQ2To1 = new ObjectStep(this, ObjectID.CIVITAS_STAIRS_SPIRAL_DOWN, new WorldPoint(1651, 3155, 2),
			"Return to Ennius and Furia in the palace.");
		goDownHQ1To0 = new ObjectStep(this, ObjectID.CIVITAS_STAIRS_SPIRAL_DOWN, new WorldPoint(1638, 3155, 1),
			"Return to Ennius and Furia in the palace.");
		returnToEnniusAfterLetter = new NpcStep(this, NpcID.VMQ2_ENNIUS_VIS, new WorldPoint(1684, 3156, 0),
			"Return to Ennius and Furia in the palace.");
		returnToEnniusAfterLetter.addSubSteps(goDownHQ2To1, goDownHQ1To0);

		talkToRegulusForTransport = new NpcStep(this, NpcID.VMQ2_QUETZAL_KEEPER_FORTIS, new WorldPoint(1699, 3141, 0),
			"TALK to Regulus Cento south-east of the palace.");
		talkToRegulusForTransport.addDialogStep("I was told to ask you about getting to the Teomat.");

		feedRenu = new NpcStep(this, NpcID.QUETZAL_CHILD_GREEN_NOOP, new WorldPoint(1703, 3142, 0), "Feed Renu the feed.", quetzalFeed.highlighted());
		feedRenu.addDialogStep("I was told to ask you about getting to the Teomat.");
		feedRenu.addIcon(ItemID.VMQ2_QUETZAL_FEED);

		travelToTeomat = new NpcStep(this, NpcID.QUETZAL_CHILD_GREEN, new WorldPoint(1697, 3142, 0), "Travel with Renu to Teomat.");
		((NpcStep) travelToTeomat).addAlternateNpcs(NpcID.QUETZAL_CHILD_BLUE, NpcID.QUETZAL_CHILD_CYAN, NpcID.QUETZAL_CHILD_GREEN_ORANGE);
		travelToTeomat.addWidgetHighlight(874, 15, 1);

		talkToPrinceInTemple = new NpcStep(this, NpcID.VMQ2_ITZLA_VIS, new WorldPoint(1454, 3173, 0), "" +
			"Talk to Prince Itzla Arkan in the temple, near the altar.");

		talkToMetzliNearTemple = new NpcStep(this, NpcID.VMQ2_METZLI_VIS, new WorldPoint(1448, 3196, 0),
			"Talk to Metzli in the northern building of the Teomat.");

		defeat8Cultists = new NpcStep(this, NpcID.VMQ2_CULTIST_M_1, "Defeat the cultists.", true);
		((NpcStep) defeat8Cultists).addAlternateNpcs(NpcID.VMQ2_CULTIST_M_2, NpcID.VMQ2_CULTIST_M_3, NpcID.VMQ2_CULTIST_F_1, NpcID.VMQ2_CULTIST_F_2, NpcID.VMQ2_CULTIST_F_3);

		finishQuest = new NpcStep(this, NpcID.VMQ2_ITZLA_VIS, new WorldPoint(1454, 3173, 0),
			"Talk to Prince Itzla Arkan in Teomat, near the altar, to finish the quest.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return List.of(twoCombatStyles);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return List.of(staminatPotion);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return List.of(
			new QuestRequirement(QuestHelperQuest.CHILDREN_OF_THE_SUN, QuestState.FINISHED)
		);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return List.of("Knight of Varlamore (level 81)", "8 Cultists (level 34)");
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
			new ExperienceReward(Skill.THIEVING, 3000)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return List.of(
			new UnlockReward("Ability to use the Civitas illa Fortis Teleport spell"),
			new UnlockReward("Ability to use the Quetzal Transport System")
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		allSteps.add(new PanelDetails("Cryptic",
			List.of(talkToRegulusStart, talkToEnnius, talkToMetzli, enterCrypt, talkToPrince,
				leaveCrypt, talkToEnnius2)));
		allSteps.add(new PanelDetails("Knights - Bazaar", List.of(
			talkToBazaarKnight, pickpocketCitizen, returnAmulet
		), varlamoreCrest));
		allSteps.add(new PanelDetails("Knights - Cothon", List.of(
			talkToCothonKnight, searchCrate, returnToCothonKnight
		), varlamoreCrest));
		allSteps.add(new PanelDetails("Knights - Pub", List.of(
			talkToPubKnights, takePubKnightsToFountain, talkToPubKnightAtFountain
		), varlamoreCrest));
		allSteps.add(new PanelDetails("Knights - Colosseum", List.of(
			enterColosseum, talkToColosseumKnight, defeatColosseumKnight, talkToColosseumKnight2
		), varlamoreCrest, twoCombatStyles));
		allSteps.add(new PanelDetails("Cost of Betrayal", List.of(
			talkToEnniusAfterKnights, goUpHQ, searchHQChest, readLetter, returnToEnniusAfterLetter
		)));
		allSteps.add(new PanelDetails("The Twilight Emissaries", List.of(
			talkToRegulusForTransport, feedRenu, travelToTeomat, talkToPrinceInTemple, talkToMetzliNearTemple,
			defeat8Cultists, finishQuest
		)));
		return allSteps;
	}
}
