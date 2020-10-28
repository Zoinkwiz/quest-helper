///*
// * Copyright (c) 2020, Zoinkwiz
// * All rights reserved.
// *
// * Redistribution and use in source and binary forms, with or without
// * modification, are permitted provided that the following conditions are met:
// *
// * 1. Redistributions of source code must retain the above copyright notice, this
// *    list of conditions and the following disclaimer.
// * 2. Redistributions in binary form must reproduce the above copyright notice,
// *    this list of conditions and the following disclaimer in the documentation
// *    and/or other materials provided with the distribution.
// *
// * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
// * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
// */
//package com.questhelper.quests.sinsofthefather;
//
//import com.questhelper.Zone;
//import com.questhelper.requirements.ItemRequirement;
//import com.questhelper.questhelpers.QuestHelper;
//import com.questhelper.steps.DetailedOwnerStep;
//import com.questhelper.steps.DetailedQuestStep;
//import com.questhelper.steps.NpcStep;
//import com.questhelper.steps.ObjectStep;
//import com.questhelper.steps.QuestStep;
//import java.util.Arrays;
//import java.util.Collection;
//import net.runelite.api.InventoryID;
//import net.runelite.api.Item;
//import net.runelite.api.ItemContainer;
//import net.runelite.api.ItemID;
//import net.runelite.api.NpcID;
//import net.runelite.api.NullObjectID;
//import net.runelite.api.ObjectID;
//import net.runelite.api.coords.WorldPoint;
//import net.runelite.api.events.GameTick;
//import net.runelite.client.eventbus.Subscribe;
//
//public class ValveStep extends DetailedOwnerStep
//{
//	private int valveTotalValue;
//	private int northTurns;
//	private int southTurns;
//
//	private final int GALLONS_NORTH = 7;
//	private final int GALLONS_SOUTH = 4;
//
//	private final Zone STANDING_AT_NORTH_VALVE = new Zone(new WorldPoint(3620, 3363, 0), new WorldPoint(3621, 3365, 0));
//	private final Zone STANDING_AT_SOUTH_VALVE = new Zone(new WorldPoint(3620, 3358, 0), new WorldPoint(3622, 3360, 0));
//
//	private boolean foundSum = false;
//	private boolean solving = false;
//	private boolean solved = false;
//	private boolean atSouthValve = false;
//	private boolean atNorthValve = false;
//	private boolean northDone = false;
//	private boolean southDone = false;
//
//
//	public ValveStep(QuestHelper questHelper)
//	{
//		super(questHelper);
//		smallNet.setTip("You can get one from Arnold");
//	}
//
//	@Subscribe
//	public void onGameTick(GameTick event)
//	{
//		updateSteps();
//	}
//
//	@Override
//	protected void updateSteps()
//	{
//
//	}
//
//	@Override
//	protected void setupSteps()
//	{
//		fishMonkfish = new ObjectStep(getQuestHelper(), NullObjectID.NULL_13477, new WorldPoint(2311, 3696, 0), "Fish at least 5 fresh monkfish. Sea Trolls will appear, and you'll need to kill them.", smallNet, combatGear);
//		cookMonkfish = new ObjectStep(getQuestHelper(), ObjectID.RANGE_12611, new WorldPoint(2316, 3669, 0), "Cook 5 monkfish. If you burn any, catch some more.", rawMonkfish);
//		talkToArnoldWithMonkfish = new NpcStep(getQuestHelper(), NpcID.ARNOLD_LYDSPOR, new WorldPoint(2329, 3688, 0), "Bring the monkfish to Arnold at the bank.", cookedMonkfish);
//	}
//
//	@Override
//	public Collection<QuestStep> getSteps()
//	{
//		return Arrays.asList(fishMonkfish, cookMonkfish, talkToArnoldWithMonkfish);
//	}
//}
