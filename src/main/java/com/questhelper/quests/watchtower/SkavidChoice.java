package com.questhelper.quests.watchtower;

import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.choice.DialogChoiceSteps;
import net.runelite.api.NpcID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.eventbus.Subscribe;

public class SkavidChoice extends NpcStep
{
	public SkavidChoice(QuestHelper questHelper)
	{
		super(questHelper, NpcID.MAD_SKAVID, new WorldPoint(2526, 9413, 0), "Talk to the mad skavid.");
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		updateCorrectChoice();
	}

	private void updateCorrectChoice()
	{
		Widget widget = client.getWidget(WidgetInfo.DIALOG_NPC_TEXT);
		if (widget == null)
		{
			return;
		}

		switch (widget.getText())
		{
			case "Bidith ig...":
				choices = new DialogChoiceSteps();
				addDialogStep("Cur.");
				break;
			case "Ar cur...":
				choices = new DialogChoiceSteps();
				addDialogStep("Gor.");
				break;
			case "Gor nod...":
				choices = new DialogChoiceSteps();
				addDialogStep("Tanath.");
				break;
			case "Cur tanath...":
				choices = new DialogChoiceSteps();
				addDialogStep("Bidith.");
				break;
		}
	}
}
