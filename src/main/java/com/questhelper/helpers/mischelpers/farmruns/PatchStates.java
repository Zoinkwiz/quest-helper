package com.questhelper.helpers.mischelpers.farmruns;

import com.questhelper.requirements.ManualRequirement;
import com.questhelper.requirements.Requirement;
import lombok.Getter;
import net.runelite.api.Client;

@Getter
public class PatchStates
{
	private final String regionName;
	private final String patchName;
	private final ManualRequirement isHarvestable = new ManualRequirement();
	private final ManualRequirement isEmpty = new ManualRequirement();
	private final ManualRequirement isUnchecked = new ManualRequirement();
	private final ManualRequirement isStump = new ManualRequirement();
	private final Requirement requirement;

	public PatchStates(String regionName)
	{
		this.regionName = regionName;
		this.patchName = null;
		this.requirement = null;
	}

	public PatchStates(String regionName, String patchName)
	{
		this.regionName = regionName;
		this.patchName = patchName;
		this.requirement = null;
	}

	public PatchStates(String regionName, Requirement requirement)
	{
		this.regionName = regionName;
		this.patchName = null;
		this.requirement = requirement;
	}

	public boolean canAccess(Client client)
	{
		if (requirement == null) return true;
		return requirement.check(client);
	}
}
