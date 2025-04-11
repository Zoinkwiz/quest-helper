package com.questhelper.runeliteobjects.extendedruneliteobjects;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

// A group of RuneliteNpcs, which are used as a group in RuneliteObjectManager
public class ExtendedRuneliteObjects
{
	@Getter
	private final String groupName;

	@Getter
	List<ExtendedRuneliteObject> extendedRuneliteObjects = new ArrayList<>();

	List<ExtendedRuneliteObjects> subGroups = new ArrayList<>();

	public ExtendedRuneliteObjects(String groupName)
	{
		this.groupName = groupName;
	}

	public ExtendedRuneliteObjects(String groupName, ExtendedRuneliteObject npc)
	{
		this.groupName = groupName;
		this.extendedRuneliteObjects.add(npc);
	}

	public ExtendedRuneliteObjects(String groupName, List<ExtendedRuneliteObject> extendedRuneliteObjects)
	{
		this.groupName = groupName;
		this.extendedRuneliteObjects.addAll(extendedRuneliteObjects);
	}

	public void addExtendedRuneliteObject(ExtendedRuneliteObject npc)
	{
		extendedRuneliteObjects.add(npc);
	}

	public void addSubGroup(ExtendedRuneliteObjects subgroup)
	{
		subGroups.add(subgroup);
	}

	public void remove(ExtendedRuneliteObject npc)
	{
		extendedRuneliteObjects.remove(npc);
	}

	public void removeAll(RuneliteObjectManager runeliteObjectManager)
	{
		disableAll(runeliteObjectManager);
		extendedRuneliteObjects.clear();
	}

	public void disableAll(RuneliteObjectManager runeliteObjectManager)
	{
		for (ExtendedRuneliteObject npc : extendedRuneliteObjects)
		{
			npc.disable();
		}
	}

	public void disableAllIncludingSubgroups(RuneliteObjectManager runeliteObjectManager)
	{
		disableAll(runeliteObjectManager);
		// Remove all associated groups
		for (ExtendedRuneliteObjects subGroup : subGroups)
		{
			runeliteObjectManager.removeGroup(subGroup.getGroupName());
		}
	}

	public void removeAllIncludingSubgroups(RuneliteObjectManager runeliteObjectManager)
	{
		disableAllIncludingSubgroups(runeliteObjectManager);
		// Remove all associated groups
		for (ExtendedRuneliteObjects subGroup : subGroups)
		{
			runeliteObjectManager.removeGroupAndSubgroups(subGroup.getGroupName());
		}
		extendedRuneliteObjects.clear();
		subGroups.clear();
	}
}
