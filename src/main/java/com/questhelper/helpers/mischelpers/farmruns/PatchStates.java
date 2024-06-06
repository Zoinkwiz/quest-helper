/*
 * Copyright (c) 2024, Zoinkwiz <https://github.com/Zoinkwiz/>
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
	private final ManualRequirement isProtected = new ManualRequirement();
	private final ManualRequirement isGrowing = new ManualRequirement();
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
