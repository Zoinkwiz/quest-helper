package com.questhelper.helpers.quests.thefremennikisles;

import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.steps.NpcStep;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.gameval.NpcID;
import net.runelite.client.eventbus.Subscribe;

public class KillTrolls extends NpcStep
{
	public KillTrolls(QuestHelper questHelper)
	{
		super(questHelper, NpcID.FRIS_TROLLM_LOWXP, new WorldPoint(2390, 10280, 1), "Kill 10 ice trolls.", true);
		this.addAlternateNpcs(NpcID.FRIS_TROLLM_PC, NpcID.FRIS_TROLLF_LOWXP, NpcID.FRIS_TROLLF_PC, NpcID.FRIS_BABY_TROLL_LOWXP, NpcID.FRIS_BABY_TROLL_PC);
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

