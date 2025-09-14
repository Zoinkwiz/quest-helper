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
package com.questhelper.helpers.quests.merlinscrystal;

import com.questhelper.bank.banktab.BankSlotIcons;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.conditional.NpcCondition;
import com.questhelper.requirements.conditional.ObjectCondition;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.npc.DialogRequirement;
import static com.questhelper.requirements.util.LogicHelper.and;
import static com.questhelper.requirements.util.LogicHelper.or;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.widget.WidgetTextRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;

public class MerlinsCrystal extends BasicQuestHelper
{
	// Required items
	ItemRequirement bread;
	ItemRequirement tinderbox;
	ItemRequirement bucketOfWax;
	ItemRequirement batBones;
	ItemRequirement combatGear;

	// Recommended items
	ItemRequirement varrockTeleport;
	ItemRequirement camelotTeleport;
	ItemRequirement twoFaladorTeleports;

	// Mid-quest item requirements
	ItemRequirement bucket;
	ItemRequirement repellent;
	ItemRequirement blackCandle;
	ItemRequirement litBlackCandle;
	ItemRequirement excalibur;
	ItemRequirement excaliburEquipped;

	// Zones
	Zone fayeGround;
	Zone faye1;
	Zone faye2;
	Zone camelotGround1;
	Zone camelotGround2;
	Zone camelotGround3;
	Zone camelot1;
	Zone camelot2;
	Zone star;
	Zone camelotTower1;
	Zone camelotTower2;

	// Miscellaneous requirements
	ZoneRequirement inFaye;
	ZoneRequirement inFayeGround;
	ZoneRequirement inFaye1;
	ZoneRequirement inFaye2;
	ZoneRequirement inCamelot1;
	ZoneRequirement inCamelot2;
	ZoneRequirement inCamelot;
	ZoneRequirement inStar;
	ZoneRequirement inCamelotTower1;
	ZoneRequirement inCamelotTower2;

	NpcCondition morganNearby;
	ObjectCondition clearedHive;
	Conditions hasAnyBlackCandle;
	NpcCondition beggarNearby;
	Conditions talkedToLady;
	Conditions hasReadSpell;
	NpcCondition thrantaxNearby;

	// Steps
	NpcStep startQuest;
	NpcStep talkToGawain;
	ObjectStep goUpstairsInCamelot;
	NpcStep talkToLancelot;
	ObjectStep goBackDownStairsCamelot;
	ObjectStep hideInArheinCrate;
	ObjectStep goToFirstFloor;
	ObjectStep goToSecondFloor;
	NpcStep attackMordred;
	DetailedQuestStep talkToMorgan;
	DetailedQuestStep goToCatherbyAfterFortress;
	DetailedQuestStep optionalGetRepellent;
	DetailedQuestStep optionalGetBucket;
	ObjectStep optionalUseRepellent;
	NpcStep talkToCandleMaker;
	NpcStep talkToLadyOfLake;
	ObjectStep enterSarimShopAndTalk;
	ObjectStep talkToBeggar;
	ObjectStep goReadMagicWords;
	ObjectStep returnToCamelot;
	ObjectStep returnToCamelotLit;
	DetailedQuestStep goStandInStar;
	DetailedQuestStep lightCandle;
	DetailedQuestStep dropBatBones;
	DetailedQuestStep sayWords;
	ObjectStep goUpLadder1Camelot;
	ObjectStep goUpLadder2Camelot;
	ObjectStep smashCrystal;
	ObjectStep goDownLadder1Camelot;
	ObjectStep goDownLadder2Camelot;
	NpcStep finishQuest;

	ConditionalStep getBlackCandle;
	ConditionalStep getExcalibur;

	@Override
	protected void setupZones()
	{
		fayeGround = new Zone(new WorldPoint(2764, 3395, 0), new WorldPoint(2781, 3410, 0));
		faye1 = new Zone(new WorldPoint(2764, 3395, 1), new WorldPoint(2781, 3410, 1));
		faye2 = new Zone(new WorldPoint(2764, 3395, 2), new WorldPoint(2781, 3410, 2));
		camelot1 = new Zone(new WorldPoint(2744, 3483, 1), new WorldPoint(2769, 3517, 1));
		camelot2 = new Zone(new WorldPoint(2744, 3483, 2), new WorldPoint(2769, 3517, 2));
		camelotGround1 = new Zone(new WorldPoint(2744, 3483, 0), new WorldPoint(2774, 3517, 0));
		camelotGround2 = new Zone(new WorldPoint(2775, 3511, 0), new WorldPoint(2783, 3517, 0));
		camelotGround3 = new Zone(new WorldPoint(2774, 3505, 0), new WorldPoint(2776, 3511, 0));
		star = new Zone(new WorldPoint(2780, 3515, 0), new WorldPoint(2780, 3515, 0));
		camelotTower1 = new Zone(new WorldPoint(2765, 3490, 1), new WorldPoint(2770, 3495, 1));
		camelotTower2 = new Zone(new WorldPoint(2765, 3490, 2), new WorldPoint(2770, 3494, 2));
	}

	@Override
	protected void setupRequirements()
	{
		bread = new ItemRequirement("Bread", ItemID.BREAD);
		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX).isNotConsumed();
		bucketOfWax = new ItemRequirement("Bucket of wax", ItemID.BUCKET_WAX).canBeObtainedDuringQuest();
		batBones = new ItemRequirement("Bat bones", ItemID.BAT_BONES).canBeObtainedDuringQuest();
		combatGear = new ItemRequirement("Combat gear + food for Sir Mordred (level 39)", -1, -1).isNotConsumed();
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

		varrockTeleport = new ItemRequirement("Teleport to Varrock", ItemID.POH_TABLET_VARROCKTELEPORT);
		camelotTeleport = new ItemRequirement("Teleport to Camelot", ItemID.POH_TABLET_CAMELOTTELEPORT);
		twoFaladorTeleports = new ItemRequirement("Teleports to Falador", ItemID.POH_TABLET_FALADORTELEPORT, 2);

		bucket = new ItemRequirement("Bucket", ItemID.BUCKET_EMPTY);
		repellent = new ItemRequirement("Insect repellent", ItemID.INSECT_REPELLENT).isNotConsumed();
		blackCandle = new ItemRequirement("Black candle", ItemID.UNLIT_BLACK_CANDLE);
		litBlackCandle = new ItemRequirement("Lit black candle", ItemID.LIT_BLACK_CANDLE);
		excalibur = new ItemRequirement("Excalibur", ItemID.EXCALIBUR).isNotConsumed();
		excaliburEquipped = excalibur.equipped();

		inFaye = new ZoneRequirement(faye1, fayeGround, faye2);
		inFayeGround = new ZoneRequirement(fayeGround);
		inFaye1 = new ZoneRequirement(faye1);
		inFaye2 = new ZoneRequirement(faye2);
		inCamelot = new ZoneRequirement(camelotGround1, camelotGround2, camelotGround3);
		inCamelot1 = new ZoneRequirement(camelot1);
		inCamelot2 = new ZoneRequirement(camelot2);
		inStar = new ZoneRequirement(star);
		inCamelotTower1 = new ZoneRequirement(camelotTower1);
		inCamelotTower2 = new ZoneRequirement(camelotTower2);

		morganNearby = new NpcCondition(NpcID.MORGAN_LE_FAYE);
		clearedHive = new ObjectCondition(ObjectID.BEEHIVE);
		hasAnyBlackCandle = or(blackCandle, litBlackCandle);
		beggarNearby = new NpcCondition(NpcID.LAKE_BEGGAR);
		talkedToLady = new Conditions(true, new DialogRequirement(questHelperPlugin.getPlayerStateManager().getPlayerName(), "Ok. That seems easy enough.", false));
		hasReadSpell = new Conditions(true, LogicType.AND, new WidgetTextRequirement(229, 1, "You find a small inscription"));
		thrantaxNearby = new NpcCondition(NpcID.THRANTAX);
	}

	public void setupSteps()
	{
		startQuest = new NpcStep(this, NpcID.KING_ARTHUR, new WorldPoint(2763, 3513, 0), "Talk to King Arthur in Camelot Castle to start.");
		startQuest.addDialogStep("Yes.");
		startQuest.addDialogStep("I want to become a Knight of the Round Table!");
		talkToGawain = new NpcStep(this, NpcID.SIR_GAWAIN, new WorldPoint(2758, 3504, 0), "Talk to Sir Gawain about how Merlin got trapped.");
		talkToGawain.addDialogStep("Do you know how Merlin got trapped?");

		goUpstairsInCamelot = new ObjectStep(this, ObjectID.KR_CAM_WOODENSTAIRS, new WorldPoint(2751, 3511, 0), "Go upstairs to talk to Lancelot.");

		talkToLancelot = new NpcStep(this, NpcID.SIR_LANCELOT, new WorldPoint(2760, 3511, 1), "Talk to Sir Lancelot about getting into Morgan Le Faye's stronghold.");
		talkToLancelot.addDialogStep("Any ideas on how to get into Morgan Le Faye's stronghold?");
		talkToLancelot.addDialogStep("Thank you for the information.");

		goBackDownStairsCamelot = new ObjectStep(this, ObjectID.KR_CAM_WOODENSTAIRSTOP, new WorldPoint(2751, 3512, 1), "Hide in Arhein's crate behind the Candle Maker's shop in Catherby.");

		hideInArheinCrate = new ObjectStep(this, ObjectID.MERLINCRATE_EMPTY, new WorldPoint(2801, 3442, 0), "Hide in Arhein's crate behind the Candle Maker's shop in Catherby.");
		hideInArheinCrate.addSubSteps(goBackDownStairsCamelot);
		hideInArheinCrate.addDialogStep("Yes.");

		goToFirstFloor = new ObjectStep(this, ObjectID.STAIRS, new WorldPoint(2770, 3405, 0), "Go up the stairs in the fortress.");
		goToSecondFloor = new ObjectStep(this, ObjectID.STAIRS, new WorldPoint(2770, 3399, 1), "Go up another floor.");
		attackMordred = new NpcStep(this, NpcID.SIR_MORDRED, new WorldPoint(2770, 3403, 2), "Attack Sir Mordred down to 0hp to cause Morgan Le Faye to spawn.");
		attackMordred.addDialogStep("Tell me how to untrap Merlin and I might.");
		attackMordred.addDialogStep("Ok I will do all that.");
		talkToMorgan = new DetailedQuestStep(this, "Go through Morgan le Faye's dialog. IF YOU EXIT FROM THIS DIALOG YOU WILL HAVE TO FIGHT MORDRED AGAIN.");
		talkToMorgan.addDialogStep("Tell me how to untrap Merlin and I might.");
		talkToMorgan.addDialogStep("Ok I will do all that.");

		goToCatherbyAfterFortress = new DetailedQuestStep(this, "Return to Catherby. If you still need bat bones, you can kill one of the bats just outside the fortress.");
		optionalGetRepellent = new DetailedQuestStep(this, new WorldPoint(2807, 3450, 0), "If you still need wax, go grab the insect repellent in a house in north Catherby. Otherwise, get your wax out.", repellent);
		optionalGetBucket = new DetailedQuestStep(this, new WorldPoint(2766, 3441, 0), "Go grab the bucket in the bee field west of Catherby.", bucket);

		optionalUseRepellent = new ObjectStep(this, ObjectID.MERLIN_BEEHIVE, new WorldPoint(2762, 3443, 0), "Use the insect repellent on a bee hive, then try to take some wax.", bucket, repellent.highlighted());
		talkToCandleMaker = new NpcStep(this, NpcID.CANDLE_MAKER, new WorldPoint(2797, 3440, 0), "Talk to the Candle Maker in Catherby twice until he gives you a black candle.", bucketOfWax);
		talkToCandleMaker.addDialogStep("Have you got any black candles?");

		talkToLadyOfLake = new NpcStep(this, NpcID.LADYOFTHELAKE, new WorldPoint(2924, 3404, 0), "Talk to the Lady of the Lake in Taverley.");
		talkToLadyOfLake.addDialogStep("I seek the sword Excalibur.");
		talkToLadyOfLake.setLockingCondition(talkedToLady);

		enterSarimShopAndTalk = new ObjectStep(this, ObjectID.JEWELLERSDOOR, new WorldPoint(3016, 3246, 0), "Attempt to enter the jewelery store in Port Sarim.", bread);
		enterSarimShopAndTalk.addDialogStep("Yes certainly.");
		talkToBeggar = new ObjectStep(this, ObjectID.JEWELLERSDOOR, new WorldPoint(3016, 3246, 0), "Talk to the beggar who appears and give him some bread.", bread);
		talkToBeggar.addDialogStep("Yes certainly.");

		goReadMagicWords = new ObjectStep(this, ObjectID.THRANTAXALTAR, new WorldPoint(3260, 3381, 0), "Check the altar in the Zamorak Temple in south east Varrock. If you've already learnt the spell, just mark this step complete in the Quest Helper sidebar.");
		goReadMagicWords.setLockingCondition(hasReadSpell);

		returnToCamelot = new ObjectStep(this, ObjectID.KR_CAMELOT_METALGATECLOSEDR, new WorldPoint(2758, 3482, 0), "Return to Camelot", excalibur, blackCandle, batBones, tinderbox);
		returnToCamelotLit = new ObjectStep(this, ObjectID.KR_CAMELOT_METALGATECLOSEDR, new WorldPoint(2758, 3482, 0), "Return to Camelot", excalibur, litBlackCandle, batBones);

		goStandInStar = new DetailedQuestStep(this, new WorldPoint(2780, 3515, 0), "Go stand in the star symbol north east of Camelot Castle.");
		goStandInStar.addDialogStep("Snarthon Candtrick Termanto");

		lightCandle = new DetailedQuestStep(this, "Light the Black candle with your tinderbox.", blackCandle.highlighted(), tinderbox.highlighted());
		dropBatBones = new DetailedQuestStep(this, "Right-click drop the bat bones in the star.", batBones.highlighted(), excalibur, litBlackCandle);
		dropBatBones.addDialogStep("Snarthon Candtrick Termanto");
		sayWords = new DetailedQuestStep(this, "Say the spell 'Snarthon Candtrick Termanto'. Be careful not to click the wrong option or you'll have to get another Black Candle.", excalibur);
		sayWords.addDialogStep("Snarthon Candtrick Termanto");

		goUpLadder1Camelot = new ObjectStep(this, ObjectID.KR_CAM_LADDER, new WorldPoint(2769, 3493, 0), "Go up the ladder in the south east of Camelot castle.", excaliburEquipped);
		goUpLadder2Camelot = new ObjectStep(this, ObjectID.KR_CAM_LADDER, new WorldPoint(2767, 3491, 1), "Go up the next ladder.", excaliburEquipped);

		smashCrystal = new ObjectStep(this, ObjectID.MERLINS_CRYSTAL, new WorldPoint(2768, 3494, 2), "Smash the Giant Crystal with Excalibur equipped.", excaliburEquipped);

		goDownLadder1Camelot = new ObjectStep(this, ObjectID.KR_CAM_LADDERTOP, new WorldPoint(2769, 3493, 1), "Tell King Arthur you've freed Merlin.");
		goDownLadder2Camelot = new ObjectStep(this, ObjectID.KR_CAM_LADDERTOP, new WorldPoint(2767, 3491, 2), "Tell King Arthur you've freed Merlin.");

		finishQuest = new NpcStep(this, NpcID.KING_ARTHUR, new WorldPoint(2763, 3513, 0), "Tell King Arthur you've freed Merlin.");
		finishQuest.addSubSteps(goDownLadder1Camelot, goDownLadder2Camelot);
	}


	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();

		var steps = new HashMap<Integer, QuestStep>();

		steps.put(0, startQuest);
		steps.put(1, talkToGawain);

		var findLancelot = new ConditionalStep(this, goUpstairsInCamelot);
		findLancelot.addStep(inCamelot1, talkToLancelot);

		steps.put(2, findLancelot);

		var discoverHowToFreeMerlin = new ConditionalStep(this, hideInArheinCrate);
		discoverHowToFreeMerlin.addStep(and(inFaye2, morganNearby), talkToMorgan);
		discoverHowToFreeMerlin.addStep(inFaye2, attackMordred);
		discoverHowToFreeMerlin.addStep(inFaye1, goToSecondFloor);
		discoverHowToFreeMerlin.addStep(inFayeGround, goToFirstFloor);
		discoverHowToFreeMerlin.addStep(inCamelot1, goBackDownStairsCamelot);

		steps.put(3, discoverHowToFreeMerlin);

		getBlackCandle = new ConditionalStep(this, optionalGetRepellent);
		getBlackCandle.addStep(inFaye, goToCatherbyAfterFortress);
		getBlackCandle.addStep(bucketOfWax, talkToCandleMaker);
		getBlackCandle.addStep(and(repellent, bucket), optionalUseRepellent);
		getBlackCandle.addStep(repellent, optionalGetBucket);
		getBlackCandle.setLockingCondition(hasAnyBlackCandle);

		getExcalibur = new ConditionalStep(this, talkToLadyOfLake);
		getExcalibur.addStep(beggarNearby, talkToBeggar);
		getExcalibur.addStep(talkedToLady, enterSarimShopAndTalk);
		getExcalibur.setLockingCondition(excalibur);

		var performSpell = new ConditionalStep(this, returnToCamelot);
		performSpell.addStep(thrantaxNearby, sayWords);
		performSpell.addStep(and(inStar, litBlackCandle), dropBatBones);
		performSpell.addStep(inStar, lightCandle);
		performSpell.addStep(inCamelot, goStandInStar);
		performSpell.addStep(litBlackCandle, returnToCamelotLit);

		var completeAllTasks = new ConditionalStep(this, getBlackCandle);
		completeAllTasks.addStep(and(hasAnyBlackCandle, excalibur, hasReadSpell), performSpell);
		completeAllTasks.addStep(and(hasAnyBlackCandle, excalibur), goReadMagicWords);
		completeAllTasks.addStep(hasAnyBlackCandle, getExcalibur);

		steps.put(4, completeAllTasks);

		var goFreeMerlin = new ConditionalStep(this, goUpLadder1Camelot);
		goFreeMerlin.addStep(inCamelotTower2, smashCrystal);
		goFreeMerlin.addStep(inCamelotTower1, goUpLadder2Camelot);

		steps.put(5, goFreeMerlin);

		var goTellArthur = new ConditionalStep(this, finishQuest);
		goTellArthur.addStep(inCamelotTower1, goDownLadder1Camelot);
		goTellArthur.addStep(inCamelotTower2, goDownLadder2Camelot);

		steps.put(6, goTellArthur);

		return steps;
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return List.of(
			combatGear,
			bread,
			tinderbox,
			bucketOfWax,
			batBones
		);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return List.of(
			varrockTeleport,
			camelotTeleport,
			twoFaladorTeleports
		);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return List.of(
			"Sir Mordred (level 39)"
		);
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(6);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return List.of(
			new ItemReward("Excalibur", ItemID.EXCALIBUR, 1)
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var sections = new ArrayList<PanelDetails>();

		sections.add(new PanelDetails("Starting off", List.of(
			startQuest,
			talkToGawain,
			goUpstairsInCamelot,
			talkToLancelot
		)));

		sections.add(new PanelDetails("Infiltrate the fortress", List.of(
			hideInArheinCrate,
			goToFirstFloor,
			goToSecondFloor,
			attackMordred,
			talkToMorgan
		), List.of(
			combatGear
		)));

		var getBlackCandlePanel = new PanelDetails("Get a Black Candle", List.of(
			goToCatherbyAfterFortress,
			optionalGetRepellent,
			optionalGetBucket,
			optionalUseRepellent,
			talkToCandleMaker
		));
		getBlackCandlePanel.setLockingStep(getBlackCandle);
		getBlackCandlePanel.setVars(4);

		sections.add(getBlackCandlePanel);

		var getExcaliburPanel = new PanelDetails("Get Excalibur", List.of(
			talkToLadyOfLake,
			enterSarimShopAndTalk,
			talkToBeggar
		), List.of(
			bread
		));
		getExcaliburPanel.setLockingStep(getExcalibur);
		getExcaliburPanel.setVars(4);

		sections.add(getExcaliburPanel);

		var readMagicWordsPanel = new PanelDetails("Learn magic words", List.of(
			goReadMagicWords
		));
		readMagicWordsPanel.setLockingStep(goReadMagicWords);
		readMagicWordsPanel.setVars(4);

		sections.add(readMagicWordsPanel);

		sections.add(new PanelDetails("Perform the spell", List.of(
			returnToCamelot,
			goStandInStar,
			lightCandle,
			dropBatBones,
			sayWords
		), List.of(
			blackCandle,
			batBones,
			excalibur,
			tinderbox
		)));

		sections.add(new PanelDetails("Free Merlin", List.of(
			goUpLadder1Camelot,
			goUpLadder2Camelot,
			smashCrystal,
			finishQuest
		)));

		return sections;
	}
}
