package com.questhelper.steps;

import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.Requirement;
import net.runelite.api.NPC;

public class NpcFollowerStep extends NpcStep
{
	public NpcFollowerStep(QuestHelper questHelper, int npcID, String text, Requirement... requirements)
	{
		super(questHelper, npcID, text, requirements);
	}

	@Override
	protected boolean npcPassesChecks(NPC npc)
	{
		boolean passesBaseCheck = super.npcPassesChecks(npc);
		if (!passesBaseCheck) return false;

		int followerVarp = client.getVarpValue(447);
		int followerID = followerVarp & 0x0000FFFF;
		return followerID == npc.getIndex();
	}

}
