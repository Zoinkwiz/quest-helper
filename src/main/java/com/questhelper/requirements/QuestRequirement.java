package com.questhelper.requirements;

import com.questhelper.QuestHelperQuest;
import java.util.Locale;
import javax.annotation.Nullable;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.QuestState;

/**
 * Requirement that checks if a {@link net.runelite.api.Quest} has a certain state.
 * Usually {@link QuestState#FINISHED}.
 */
@Getter
public class QuestRequirement extends AbstractRequirement
{
	private final QuestHelperQuest quest;
	private final QuestState requiredState;
	private String displayText = null;

	/**
	 * Check if a {@link net.runelite.api.Quest} meets the required {@link QuestState}
	 *
	 * @param quest the quest to check
	 * @param requiredState the required quest state
	 */
	public QuestRequirement(QuestHelperQuest quest, QuestState requiredState)
	{
		this(quest, requiredState, null);
	}

	/**
	 * Check if a {@link net.runelite.api.Quest} meets the required {@link QuestState}.
	 *
	 * @param quest the quest to check
	 * @param requiredState the required quest state
	 * @param displayText display text
	 */
	public QuestRequirement(QuestHelperQuest quest, QuestState requiredState, @Nullable String displayText)
	{
		this.quest = quest;
		this.requiredState = requiredState;
		this.displayText = displayText;
	}

	public QuestRequirement(QuestHelperQuest quest)
	{
		this(quest, QuestState.FINISHED, null);
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
