package com.questhelper.steps.playermadesteps.extendedruneliteobjects;

import lombok.Getter;
import net.runelite.api.MenuAction;

@Getter
public class MenuEntryWrapper
{
	String option;
	MenuAction type;
	String target;
	int identifier;
	int param0;
	int param1;

	public MenuEntryWrapper(String option, MenuAction type, String target, int identifier, int param0, int param1)
	{
		this.option = option;
		this.type = type;
		this.target = target;
		this.identifier = identifier;
		this.param0 = param0;
		this.param1 = param1;
	}
}
