package com.questhelper.quests.lunardiplomacy;

import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.steps.DetailedOwnerStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.EmoteStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.QuestStep;
import java.util.Arrays;
import java.util.Collection;
import net.runelite.api.NpcID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.cluescrolls.clues.emote.Emote;

public class MimicChallenge extends DetailedOwnerStep
{
	DetailedQuestStep cry, bow, dance, wave, think, talk;

	public MimicChallenge(QuestHelper questHelper)
	{
		super(questHelper, "Copy the emotes that the NPC does.");
		setupSteps();
	}

	@Override
	public void startUp()
	{
		super.startUp();
		setupCurrentStep();
	}

	public void setupSteps()
	{
		cry = new EmoteStep(getQuestHelper(), Emote.CRY, new WorldPoint(1769, 5058, 2), "Perform the cry emote.");
		bow = new EmoteStep(getQuestHelper(), Emote.BOW, new WorldPoint(1770, 5063, 2), "Perform the bow emote.");
		dance = new EmoteStep(getQuestHelper(), Emote.DANCE, new WorldPoint(1772, 5070, 2), "Perform the dance emote.");
		wave = new EmoteStep(getQuestHelper(), Emote.WAVE, new WorldPoint(1767, 5061, 2), "Perform the wave emote.");
		think = new EmoteStep(getQuestHelper(), Emote.THINK, new WorldPoint(1772, 5070, 2), "Perform the think emote.");
		talk = new NpcStep(getQuestHelper(), NpcID.ETHEREAL_MIMIC, "Talk to the Ethereal Mimic.");
		talk.addDialogStep("Suppose I may as well have a go.");
		addSubSteps(cry, bow, dance, wave, think, talk);
	}

	@Subscribe
	@Override
	public void onVarbitChanged(VarbitChanged varbitChanged)
	{
		super.onVarbitChanged(varbitChanged);
		setupCurrentStep();
	}

	public void setupCurrentStep()
	{
		if (client.getVarbitValue(2419) == 0)
		{
			startUpStep(talk);
			return;
		}

		switch (client.getVarbitValue(2420))
		{
			case 1:
				startUpStep(cry);
				break;
			case 2:
				startUpStep(bow);
				break;
			case 3:
				startUpStep(dance);
				break;
			case 4:
				startUpStep(wave);
				break;
			case 5:
				startUpStep(think);
				break;
			default:
				startUpStep(talk);
				break;
		}
	}

	public void chooseStepBasedOnIfTalked(QuestStep emoteStep)
	{
		if (client.getVarbitValue(2419) == 1)
		{
			startUpStep(emoteStep);
		}
		else
		{
			startUpStep(talk);
		}
	}

	@Override
	public Collection<QuestStep> getSteps()
	{
		return Arrays.asList(talk, cry, bow, dance, think, wave);
	}

	public Collection<QuestStep> getDisplaySteps()
	{
		return Arrays.asList(talk, cry, bow, dance, think, wave);
	}
}

