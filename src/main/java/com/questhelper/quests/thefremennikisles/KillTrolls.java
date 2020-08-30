package com.questhelper.quests.thefremennikisles;

import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.steps.NpcStep;
import net.runelite.api.NpcID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;

public class KillTrolls extends NpcStep
{
	public KillTrolls(QuestHelper questHelper)
	{
		super(questHelper, NpcID.ICE_TROLL_MALE_5824, new WorldPoint(2390, 10280, 1), "Kill 10 ice trolls.", true);
		this.addAlternateNpcs(NpcID.ICE_TROLL_MALE_5829, NpcID.ICE_TROLL_FEMALE_5825, NpcID.ICE_TROLL_FEMALE_5830, NpcID.ICE_TROLL_RUNT_5823, NpcID.ICE_TROLL_RUNT_5828);
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		updateSteps();
	}

	protected void updateSteps()
	{
		int numToKill =  client.getVarbitValue(3312);
		this.setText("Kill " + numToKill + " trolls to continue.");
	}
}

