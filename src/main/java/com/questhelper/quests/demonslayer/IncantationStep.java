package com.questhelper.quests.demonslayer;

import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.QuestStep;
import java.util.HashMap;

public class IncantationStep extends ConditionalStep
{
	private final HashMap<Integer, String> words;

	private QuestStep incantationStep;

	int bing = 0;

	public IncantationStep(QuestHelper questHelper, QuestStep incantationStep)
	{
		super(questHelper, incantationStep);
		this.incantationStep = incantationStep;
		this.steps.get(null).getText().add("Incantation is currently unknown.");
		words = new HashMap<>();
		words.put(0, "Carlem");
		words.put(1, "Aber");
		words.put(2, "Camerinthum");
		words.put(3, "Purchai");
		words.put(4, "Gabindo");
	}

	@Override
	protected void updateSteps()
	{
		bing++;
		if (client.getVarbitValue(2562) == 0 && client.getVarbitValue(2563) == 0)
		{
			return;
		}

		String incantString = "Say the following in order: ";
		incantString += words.get(client.getVarbitValue(2562)) + ", ";
		incantString += words.get(client.getVarbitValue(2563)) + ", ";
		incantString += words.get(client.getVarbitValue(2564)) + ", ";
		incantString += words.get(client.getVarbitValue(2565)) + ", ";
		incantString += words.get(client.getVarbitValue(2566)) + ".";
		steps.get(null).getText().set(1, incantString);

		startUpStep(incantationStep);
	}

}
