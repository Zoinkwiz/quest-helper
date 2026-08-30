/*
 * Copyright (c) 2026, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.maker;

import com.questhelper.maker.HelperConstructModels.DraftRequirement;
import com.questhelper.maker.HelperConstructModels.DraftSkillRequirement;
import net.runelite.api.Skill;

/**
 * Default {@link DraftRequirement} rows for the Quest Helper Maker (item library tab).
 */
public final class RequirementDraftFactory
{
	private RequirementDraftFactory()
	{
	}

	/** Blank item requirement row from Add on the Item reqs tab. */
	public static DraftRequirement newPlaceholderItemRequirement()
	{
		DraftRequirement r = new DraftRequirement();
		r.setRawId(0);
		r.setDisplayName("Item");
		return r;
	}

	/** Blank skill requirement row from Add on the Skill reqs tab. */
	public static DraftSkillRequirement newPlaceholderSkillRequirement()
	{
		DraftSkillRequirement r = new DraftSkillRequirement();
		r.setSkillName(Skill.ATTACK.name());
		r.setRequiredLevel(1);
		r.setCanBeBoosted(true);
		r.setDisplayText("");
		r.setOperation("GREATER_EQUAL");
		r.setRequirementId("skill:" + Skill.ATTACK.name() + ":1:GREATER_EQUAL:true:");
		return r;
	}
}
