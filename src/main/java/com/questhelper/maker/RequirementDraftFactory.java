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
		return r;
	}
}
