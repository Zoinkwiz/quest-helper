package com.questhelper.playerquests.puzzlewrapper;

import com.questhelper.helpers.mischelpers.farmruns.FarmingUtils;

public enum PuzzleWrapperStage implements FarmingUtils.ConfigEnum
{
	A,
	B,
	C,
	UNKNOWN;

	@Override
	public String getConfigKey()
	{
		return "playerquestpuzzlewrapper";
	}

	@Override
	public FarmingUtils.ConfigEnum getDefault()
	{
		return PuzzleWrapperStage.A;
	}
}
