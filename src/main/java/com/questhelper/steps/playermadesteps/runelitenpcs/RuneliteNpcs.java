package com.questhelper.steps.playermadesteps.runelitenpcs;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

// A group of RuneliteNpcs, which are used as a group in RuneliteObjectManager
public class RuneliteNpcs
{
	@Getter
	private final String groupName;
	List<RuneliteNpc> npcs = new ArrayList<>();

	List<RuneliteNpcs> subGroups = new ArrayList<>();

	public RuneliteNpcs(String groupName)
	{
		this.groupName = groupName;
	}

	public RuneliteNpcs(String groupName, RuneliteNpc npc)
	{
		this.groupName = groupName;
		this.npcs.add(npc);
	}

	public RuneliteNpcs(String groupName, List<RuneliteNpc> npcs)
	{
		this.groupName = groupName;
		this.npcs.addAll(npcs);
	}

	public void addNpc(RuneliteNpc npc)
	{
		npcs.add(npc);
	}

	public void addSubGroup(RuneliteNpcs subgroup)
	{
		subGroups.add(subgroup);
	}

	public void remove(RuneliteNpc npc)
	{
		npcs.remove(npc);
	}

	public void removeAll(RuneliteObjectManager runeliteObjectManager)
	{
		disableAll(runeliteObjectManager);
		npcs.clear();
	}

	public void disableAll(RuneliteObjectManager runeliteObjectManager)
	{
		for (RuneliteNpc npc : npcs)
		{
			npc.disable();
		}
	}

	public void disableAllIncludingSubgroups(RuneliteObjectManager runeliteObjectManager)
	{
		disableAll(runeliteObjectManager);
		// Remove all associated groups
		for (RuneliteNpcs subGroup : subGroups)
		{
			runeliteObjectManager.removeGroup(subGroup.getGroupName());
		}
	}

	public void removeAllIncludingSubgroups(RuneliteObjectManager runeliteObjectManager)
	{
		disableAllIncludingSubgroups(runeliteObjectManager);
		// Remove all associated groups
		for (RuneliteNpcs subGroup : subGroups)
		{
			runeliteObjectManager.removeGroupAndSubgroups(subGroup.getGroupName());
		}
		npcs.clear();
		subGroups.clear();
	}
}
