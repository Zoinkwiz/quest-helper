/*
 * Copyright (c) 2025, TTvanWillegen
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
package com.questhelper.helpers.quests.pryingtimes;

import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.TeleportItemRequirement;
import com.questhelper.requirements.npc.NpcRequirement;
import com.questhelper.requirements.player.FreePortTaskSlotsRequirement;
import com.questhelper.requirements.player.ShipInPortRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.Port;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.PortTaskStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.regex.Pattern;
import com.questhelper.steps.SailStep;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.VarbitID;
import static com.questhelper.requirements.util.LogicHelper.and;
import static com.questhelper.requirements.util.LogicHelper.not;

public class PryingTimes extends BasicQuestHelper
{
	ItemRequirement hammerRequirement, steelBarRequirement, redberryPieRequirement, captainsLogRequirement, gotTheKey, gotStout;
	SkillRequirement sailingSkillRequirement, smithingSkillRequirement;
	QuestRequirement pandemoniumQuestRequirement, knightsSwordQuestRequirement;
	FreePortTaskSlotsRequirement freeTaskSlotRequirement;
	ShipInPortRequirement shipAtPortSarimDock;
	TeleportItemRequirement thurgoTeleportRecommend;
	NpcRequirement spawnedTroll;
	VarbitRequirement drankStout;

	Zone portSarimDockZone;

	NpcStep startQuest, letSteveKnow, getKey, giveKey, killTheTroll, goToSteve;
	PortTaskStep deliverCargo;
	ObjectStep testKey, openCrate;
	SailStep sailToCrate;
	ItemStep drinkTheStout;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();
		steps.put(0,  startQuest);
		steps.put(5,  deliverCargo);
		steps.put(10, letSteveKnow);
		steps.put(15, getKey);
		steps.put(20, giveKey);
		ConditionalStep cTestKey = new ConditionalStep(this, sailToCrate);
		cTestKey.addStep(and(sailToCrate.getZoneRequirement(), not(gotStout), not(drankStout)), testKey);
		cTestKey.addStep(gotStout, drinkTheStout);
		cTestKey.addStep(drankStout, goToSteve);
		steps.put(25, cTestKey);
		steps.put(30, openCrate);

		return steps;
	}

	public void setupConditions()
	{

	}

	@Override
	protected void setupZones()
	{
		portSarimDockZone = new Zone(new WorldPoint(3045, 3183, 0), new WorldPoint(3061, 3208, 0));
	}

	@Override
	protected void setupRequirements()
	{
		//Quest Requirements
		hammerRequirement = new ItemRequirement("Hammer", ItemCollections.HAMMER).isNotConsumed();
		hammerRequirement.setTooltip("You can pick this up at the shipyard.");
		steelBarRequirement = new ItemRequirement("Steel bar", ItemID.STEEL_BAR, 1);
		redberryPieRequirement = new ItemRequirement("Redberry pie", ItemID.REDBERRY_PIE, 1);
		captainsLogRequirement = new ItemRequirement("Captain's log", ItemID.SAILING_LOG, 1);
		sailingSkillRequirement = new SkillRequirement(Skill.SAILING, 12);
		smithingSkillRequirement = new SkillRequirement(Skill.SMITHING, 30);
		pandemoniumQuestRequirement = new QuestRequirement(QuestHelperQuest.PANDEMONIUM, QuestState.FINISHED);
		knightsSwordQuestRequirement = new QuestRequirement(QuestHelperQuest.THE_KNIGHTS_SWORD, QuestState.FINISHED);
		freeTaskSlotRequirement = new FreePortTaskSlotsRequirement(1);

		shipAtPortSarimDock = new ShipInPortRequirement(Port.PORT_SARIM);
		gotTheKey = new ItemRequirement("Crowbar", ItemID.SAILING_CHARTING_CROWBAR);
		gotStout = new ItemRequirement("Bottle of fish bladder stout", ItemID.SAILING_CHARTING_DRINK_CRATE_PRYING_TIMES);
		spawnedTroll = new NpcRequirement(NpcID.SAILING_CHARTING_DRINK_CRATE_PRYING_TIMES_EFFECT_TROLL);
		drankStout = new VarbitRequirement(VarbitID.SAILING_CHARTING_DRINK_CRATE_PRYING_TIMES_COMPLETE,1);

		//Recommended
		thurgoTeleportRecommend = new TeleportItemRequirement("Fairy Ring [AIQ]", ItemCollections.FAIRY_STAFF);
	}

	public void setupSteps()
	{
		startQuest = new NpcStep(this, NpcID.STEVE_BEANIE, new WorldPoint(3050, 2966, 0), "Talk to 'Squawking' Steve Beanie behind the Pandemonium bar to start the quest.", true, captainsLogRequirement);
		startQuest.addDialogStep("Any word from Old Grog?");
		startQuest.addDialogStep("Yes.");
		deliverCargo = new PortTaskStep(this, Port.PORT_SARIM, Port.PANDEMONIUM, 600);
		letSteveKnow = new NpcStep(this, NpcID.STEVE_BEANIE, new WorldPoint(3050, 2966, 0), "Let 'Squawking' Steve Beanie behind the bar know you delivered the looty.", true);
		letSteveKnow.addDialogStep("I delivered that cargo for you.");

		getKey = new NpcStep(this, NpcID.THURGO, new WorldPoint(3000, 3145, 0), "Get the key from Thurgo in the shed near Mudskipper's Point.", true,hammerRequirement, steelBarRequirement,redberryPieRequirement);
		getKey.addDialogStep(Pattern.compile("(I need some help with a 'special key'\\.)|(So, can you help me make a crowbar\\?)"));
		getKey.setRecommended(Arrays.asList(thurgoTeleportRecommend));
		giveKey = new NpcStep(this, NpcID.STEVE_BEANIE, new WorldPoint(3050, 2966, 0), "Give the 'key' to 'Squawking' Steve Beanie behind the Pandemonium bar.", true, gotTheKey);
		giveKey.addDialogStep("I made that 'special key' you needed.");
		testKey = new ObjectStep(this, 59283, new WorldPoint(3013, 2998, 0), "Open the Sealed crate with the newly acquired 'key'.", gotTheKey);
		sailToCrate = new SailStep(this, new WorldPoint(3013, 2998, 0));
		drinkTheStout = new ItemStep(this, "Drink the stout. Warning: you will be attacked by a level 14 Drink Troll.", gotStout.highlighted());
		killTheTroll = new NpcStep(this, NpcID.SAILING_CHARTING_DRINK_CRATE_PRYING_TIMES_EFFECT_TROLL, new WorldPoint(3013, 2998, 0), "Kill the Drink Troll, or log out.");
		goToSteve = new NpcStep(this, NpcID.STEVE_BEANIE, new WorldPoint(3050, 2966, 0), "Tell 'Squawking' Steve Beanie behind the Pandemonium bar the key works.", true, gotTheKey);
		goToSteve.addDialogStep("About that crate...");
		openCrate = new ObjectStep(this, 58405, new WorldPoint(3048, 2965, 0), "Open Steve's crate in the corner of behind the bar.", gotTheKey);
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(hammerRequirement, steelBarRequirement, redberryPieRequirement, captainsLogRequirement);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return Arrays.asList(sailingSkillRequirement, smithingSkillRequirement, pandemoniumQuestRequirement, knightsSwordQuestRequirement, freeTaskSlotRequirement);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Arrays.asList(new ExperienceReward(Skill.SAILING, 800), new ExperienceReward(Skill.SMITHING, 1000));
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
			new ItemReward("Sawmill Coupon (oak plank)", ItemID.SAWMILL_COUPON, 25)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
			new UnlockReward("Unlimited Crowbars from the crate of crowbars"),
			new UnlockReward("Ability to chart forgotten drinks"));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Looty!", Arrays.asList(startQuest, deliverCargo, letSteveKnow), captainsLogRequirement, freeTaskSlotRequirement));
		allSteps.add(new PanelDetails("A key!", Arrays.asList(getKey, giveKey, sailToCrate, testKey, drinkTheStout, killTheTroll, goToSteve, openCrate), hammerRequirement, steelBarRequirement, redberryPieRequirement));
		return allSteps;
	}
}
