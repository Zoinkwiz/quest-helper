package com.questhelper.steps.conditional;

import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;

public class WidgetModelCondition extends ConditionForStep
{
	@Getter
	private final int groupId;

	private final int childId;
	private final int id;
	private int childChildId = -1;
	private boolean checkChildren;

	public WidgetModelCondition(int groupId, int childId, int childChildId, int id)
	{
		this.groupId = groupId;
		this.childId = childId;
		this.childChildId = childChildId;
		this.id = id;
	}

	@Override
	public boolean checkCondition(Client client)
	{
		if (onlyNeedToPassOnce && hasPassed)
		{
			return true;
		}
		return checkWidget(client);
	}

	public boolean checkWidget(Client client)
	{
		Widget widget = client.getWidget(groupId, childId);
		if (widget == null)
		{
			return false;
		}
		if (childChildId != -1)
		{
			widget = widget.getChild(childChildId);
		}
		if (widget != null)
		{
			return widget.getModelId() == id;
		}
		return false;
	}

	public void checkWidgetText(Client client)
	{
		hasPassed = hasPassed || checkWidget(client);
	}
}

