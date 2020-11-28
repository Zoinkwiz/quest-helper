package com.questhelper.quests.recruitmentdrive;

import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.conditional.ConditionForStep;
import com.questhelper.steps.conditional.Conditions;
import com.questhelper.steps.conditional.VarbitCondition;
import com.questhelper.steps.conditional.WidgetTextCondition;
import java.util.ArrayList;
import net.runelite.api.Client;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import static net.runelite.api.widgets.WidgetID.DIALOG_NPC_GROUP_ID;

public class SirRenItchoodStep extends ConditionalStep
{
	private String answer = null;

	private String[] answers = {
		"BITE", "FISH", "LAST", "MEAT", "RAIN", "TIME"
	};

	private final int VARBIT_FINISHED_ROOM = 663;

	private ConditionForStep hasAnswer, answerWidgetOpen;
	private DetailedQuestStep enterDoorcode;
	private QuestStep talkToRen, openAnswerWidget, leaveRoom;
	private VarbitCondition finishedRoomCondition;

	public SirRenItchoodStep(QuestHelper questHelper, QuestStep step, Requirement... requirements)
	{
		super(questHelper, step, requirements);
		loadConditions();

		talkToRen = step;
		addRenSteps();
	}

	private void loadConditions()
	{
		hasAnswer = new ConditionForStep()
		{
			@Override
			public boolean checkCondition(Client client)
			{
				return answer != null;
			}
		};
	}

	private void addRenSteps()
	{
		finishedRoomCondition = new VarbitCondition(VARBIT_FINISHED_ROOM, 1);
		openAnswerWidget = new ObjectStep(questHelper, 7323, "Open the door to be prompted to enter a code.");
		answerWidgetOpen = new WidgetTextCondition(285, 55, "Combination Lock Door");
		enterDoorcode = new DetailedQuestStep(questHelper, "");
		leaveRoom = new ObjectStep(questHelper, 7323, "Leaves through the door to enter the portal and continue.");

		addStep(finishedRoomCondition, leaveRoom);
		addStep(new Conditions(hasAnswer, answerWidgetOpen), enterDoorcode);
		addStep(hasAnswer, openAnswerWidget);
	}

	public ArrayList<QuestStep> getPanelSteps()
	{
		ArrayList<QuestStep> steps = new ArrayList<>();
		steps.add(talkToRen);
		steps.add(openAnswerWidget);
		steps.add(enterDoorcode);
		steps.add(leaveRoom);
		return steps;
	}

	@Override
	/**
	 * {@inheritDoc}
	 */
	public void onWidgetLoaded(WidgetLoaded event)
	{
		int groupId = event.getGroupId();
		if (groupId == DIALOG_NPC_GROUP_ID)
		{
			clientThread.invokeLater(() -> readWidget());
		}

		super.onWidgetLoaded(event);
	}

	private void readWidget()
	{
		if (this.answer != null)
		{
			return;
		}
		Widget widget = client.getWidget(WidgetID.DIALOG_NPC_GROUP_ID, 4);
		if (widget == null)
		{
			return;
		}
		String characterText = widget.getText();
		String[] splitText = characterText.split("<br>");

		if (splitText.length != 4)
		{
			return;
		}

		StringBuilder sb = new StringBuilder();
		for (String line : splitText)
		{
			sb = sb.append(line.charAt(0));
		}
		String answer = sb.toString();
		for (String value : answers)
		{
			if (value.equals(answer))
			{
				this.answer = answer;
				enterDoorcode.setText("Enter the code: " + answer + " into the combination lock.");
				return;
			}
		}
	}
}
