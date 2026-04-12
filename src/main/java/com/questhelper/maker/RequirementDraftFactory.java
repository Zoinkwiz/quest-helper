package com.questhelper.maker;

import com.questhelper.maker.HelperConstructModels.DraftRequirement;

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
}
