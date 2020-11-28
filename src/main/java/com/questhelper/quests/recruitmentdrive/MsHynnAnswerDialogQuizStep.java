package com.questhelper.quests.recruitmentdrive;

import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.conditional.ConditionForStep;
import com.questhelper.steps.conditional.VarbitCondition;
import java.util.ArrayList;
import net.runelite.api.Client;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import static net.runelite.api.widgets.WidgetID.DIALOG_NPC_GROUP_ID;

public class MsHynnAnswerDialogQuizStep extends ConditionalStep
{
	//Ms Hynn steps
	private QuestStep dialogQuizStep, leaveRoom, talkToMsHynnTerprett;
	private VarbitCondition finishedRoomCondition;

	private boolean dialogEntry = false;

	private final int VARBIT_FINISHED_ROOM = 665;

	private final String DIALOG_QUIZ = "Here is my riddle:<br>I estimate there to be 1 million inhabitants in the world<br>of Gielinor, creatures and people both.";
	private final String DIALOG_QUIZ2 = "What would be the number you would get if you<br>multiply the number of fingers on everythings left hand,<br>to the nearest million?";

	private static final String DIALOG_QUIZ3 = "My husband is four times older than my daughter.<br>In twenty years time, he will be twice as old as my<br>daughter.";
	public MsHynnAnswerDialogQuizStep(QuestHelper questHelper, QuestStep step, Requirement... requirements)
	{
		super(questHelper, step, requirements);
//Talk to Ms Hynn Terprett and answer the riddle.
		talkToMsHynnTerprett = step;
		step.addDialogSteps(
			"The wolves.",
			"Bucket A (32 degrees)",
			"Zero.", // TODO
			"The number of false statements here is three.",
			": Zero.");
		AddSteps();
	}

	private void AddSteps()
	{
		finishedRoomCondition = new VarbitCondition(VARBIT_FINISHED_ROOM, 1);
		leaveRoom = new ObjectStep(questHelper, 7354, "Leaves through the door to enter the portal and continue.");

		addStep(finishedRoomCondition, leaveRoom);
		AddDialogQuizStep();
	}

	public ArrayList<QuestStep> getPanelSteps()
	{
		ArrayList<QuestStep> steps = new ArrayList<>();
		if (dialogEntry)
		{
			steps.add(dialogQuizStep);
		}
		steps.add(leaveRoom);
		return steps;
	}

	private void AddDialogQuizStep()
	{
		ConditionForStep step = new ConditionForStep()
		{
			@Override
			public boolean checkCondition(Client client)
			{
				return dialogEntry;
			}
		};
		dialogQuizStep = new DetailedQuestStep(questHelper, "Enter the answer 10 to the dialog when prompted.");
		talkToMsHynnTerprett.addSubSteps(dialogQuizStep);
		addStep(step, dialogQuizStep);
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
		Widget widget = client.getWidget(WidgetID.DIALOG_NPC_GROUP_ID, 4);
		if (widget == null)
		{
			return;
		}
		String characterText = widget.getText();

		if (DIALOG_QUIZ.equals(characterText) ||
			DIALOG_QUIZ2.equals(characterText)){
			dialogEntry = true;
			talkToMsHynnTerprett.setText("Talk to Ms Hynn Terprett and answer the riddle with the code\n \n 0");
			dialogQuizStep.setText("Enter the answer \n \n 0 when prompted with a dialogue box.");
		} else if (DIALOG_QUIZ3.equals(characterText)){
			dialogEntry = true;
			talkToMsHynnTerprett.setText("Talk to Ms Hynn Terprett and answer the riddle with the code\n \n 10");
			dialogQuizStep.setText("Enter the answer \n \n 10 when prompted with a dialogue box.");

		}
	}
}
