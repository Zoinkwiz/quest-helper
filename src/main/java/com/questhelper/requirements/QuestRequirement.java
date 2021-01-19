package com.questhelper.requirements;

import java.util.Locale;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.Quest;
import net.runelite.api.QuestState;

@Getter
public class QuestRequirement extends Requirement
{
	private final Quest quest;
	private final QuestState requiredState;
	private String displayText = null;

	public QuestRequirement(Quest quest, QuestState requiredState)
	{
		this.quest = quest;
		this.requiredState = requiredState;
	}

	public QuestRequirement(Quest quest, QuestState requiredState, String displayText)
	{
		this(quest, requiredState);
		this.displayText = displayText;
	}

	@Override
	public boolean check(Client client)
	{
		QuestState state = quest.getState(client);
		if (requiredState == QuestState.IN_PROGRESS && state == QuestState.FINISHED)
		{
			return true;
		}
		return state == requiredState;
	}

	@Override
	public String getDisplayText()
	{
		if (displayText != null && !displayText.isEmpty())
		{
			return displayText;
		}
		String text = Character.toUpperCase(requiredState.name().charAt(0)) + requiredState.name().toLowerCase(Locale.ROOT).substring(1);
		return text.replaceAll("_", " ") + " " + quest.getName();
	}
}
