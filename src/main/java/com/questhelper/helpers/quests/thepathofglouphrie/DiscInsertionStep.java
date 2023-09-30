package com.questhelper.helpers.quests.thepathofglouphrie;

import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.steps.ItemStep;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;

public class DiscInsertionStep extends ItemStep
{
	public DiscInsertionStep(QuestHelper questHelper, String text, Requirement... requirements)
	{
		super(questHelper, text, requirements);
	}

	@Override
	protected Widget getInventoryWidget() {
		return client.getWidget(852, 0);
	}
}