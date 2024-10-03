/*
 * Copyright (c) 2024, Zoinkwiz <https://github.com/Zoinkwiz>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.questhelper.steps;

import com.questhelper.QuestHelperConfig;
import com.questhelper.QuestHelperPlugin;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.ConfigRequirement;
import com.questhelper.requirements.ManualRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import static com.questhelper.requirements.util.LogicHelper.not;
import com.questhelper.requirements.util.LogicType;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import lombok.NonNull;
import net.runelite.client.ui.overlay.components.PanelComponent;

public class PuzzleWrapperStep extends ConditionalStep
{
	final QuestHelperConfig questHelperConfig;
	final DetailedQuestStep noSolvingStep;
	ManualRequirement shouldHideHiddenPuzzleHintInSidebar = new ManualRequirement();

	public PuzzleWrapperStep(QuestHelper questHelper, QuestStep step, DetailedQuestStep hiddenStep, String text, Requirement... requirements)
	{
		super(questHelper, step, text, requirements);
		this.noSolvingStep = hiddenStep;
		this.questHelperConfig = questHelper.getConfig();
		addStep(not(new ConfigRequirement(questHelper.getConfig()::solvePuzzles)), noSolvingStep);
		super.addSubSteps(noSolvingStep, step);
		conditionToHideInSidebar(new Conditions(shouldHideHiddenPuzzleHintInSidebar, new Conditions(LogicType.NOR, new ConfigRequirement(questHelper.getConfig()::solvePuzzles))));
	}

	public PuzzleWrapperStep(QuestHelper questHelper, QuestStep step, Requirement... requirements)
	{
		this(questHelper, step, new DetailedQuestStep(questHelper, "If you want help with this, enable 'Show Puzzle Solutions' in the Quest Helper configuration settings."), requirements);
	}

	public PuzzleWrapperStep(QuestHelper questHelper, QuestStep step, String text, Requirement... requirements)
	{
		this(questHelper, step, new DetailedQuestStep(questHelper, "If you want help with this, enable 'Show Puzzle Solutions' in the Quest Helper configuration settings."), text, requirements);
	}

	public PuzzleWrapperStep(QuestHelper questHelper, QuestStep step, DetailedQuestStep hiddenStep, Requirement... requirements)
	{
		this(questHelper, step, hiddenStep, "", requirements);
	}

	@Override
	protected void updateSteps()
	{
		if (!questHelperConfig.solvePuzzles() && currentStep != noSolvingStep)
		{
			startUpStep(noSolvingStep);
			return;
		}

		super.updateSteps();
	}

	@Override
	public List<String> getText()
	{
		if (questHelperConfig.solvePuzzles())
		{
			if (currentStep == null)
			{
				return steps.get(null).getText();
			}
			return currentStep.getText();
		}
		return super.getText();
	}

	public PuzzleWrapperStep withNoHelpHiddenInSidebar(boolean isHidden)
	{
		shouldHideHiddenPuzzleHintInSidebar.setShouldPass(isHidden);
		return this;
	}

	@Override
	public void makeOverlayHint(PanelComponent panelComponent, QuestHelperPlugin plugin, @NonNull List<String> additionalText, @NonNull List<Requirement> additionalRequirements)
	{
		if (questHelperConfig.solvePuzzles())
		{
			currentStep.makeOverlayHint(panelComponent, plugin, additionalText, additionalRequirements);
		}
		else
		{
			super.makeOverlayHint(panelComponent, plugin, additionalText, additionalRequirements);
		}
	}

	@Override
	public QuestStep addDialogStep(String choice)
	{
		steps.get(null).addDialogStep(choice);
		return this;
	}

	@Override
	public QuestStep addDialogStep(Pattern pattern)
	{
		steps.get(null).addDialogStep(pattern);
		return this;
	}

	@Override
	public void resetDialogSteps()
	{
		steps.get(null).resetDialogSteps();
	}

	@Override
	public QuestStep addDialogStepWithExclusion(String choice, String exclusionString)
	{
		steps.get(null).addDialogStepWithExclusion(choice, exclusionString);
		return this;
	}

	@Override
	public QuestStep addDialogStepWithExclusions(String choice, String... exclusionString)
	{
		steps.get(null).addDialogStepWithExclusions(choice, exclusionString);
		return this;
	}

	@Override
	public QuestStep addDialogStep(int id, String choice)
	{
		steps.get(null).addDialogStep(id, choice);
		return this;
	}

	@Override
	public QuestStep addDialogStep(int id, Pattern pattern)
	{
		steps.get(null).addDialogStep(id, pattern);
		return this;
	}

	@Override
	public QuestStep addDialogSteps(String... newChoices)
	{
		steps.get(null).addDialogSteps(newChoices);
		return this;
	}

	@Override
	public QuestStep addDialogConsideringLastLineCondition(String dialogString, String choiceValue)
	{
		steps.get(null).addDialogConsideringLastLineCondition(dialogString, choiceValue);
		return this;
	}

	@Override
	public QuestStep addDialogChange(String choice, String newText)
	{
		steps.get(null).addDialogChange(choice, newText);
		return this;
	}

	@Override
	public QuestStep addWidgetChoice(String text, int groupID, int childID)
	{
		steps.get(null).addWidgetChoice(text, groupID, childID);
		return this;
	}

	@Override
	public QuestStep addWidgetChoice(String text, int groupID, int childID, int groupIDForChecking)
	{
		steps.get(null).addWidgetChoice(text, groupID, childID, groupIDForChecking);
		return this;
	}

	@Override
	public QuestStep addWidgetChoice(int id, int groupID, int childID)
	{
		steps.get(null).addWidgetChoice(id, groupID, childID);
		return this;
	}

	@Override
	public QuestStep addWidgetChange(String choice, int groupID, int childID, String newText)
	{
		steps.get(null).addWidgetChange(choice, groupID, childID, newText);
		return this;
	}

	@Override
	public void clearWidgetHighlights()
	{
		steps.get(null).clearWidgetHighlights();
	}

	@Override
	public QuestStep addWidgetHighlight(int groupID, int childID)
	{
		steps.get(null).addWidgetHighlight(groupID, childID);
		return this;
	}

	@Override
	public QuestStep addWidgetHighlight(int groupID, int childID, int childChildID)
	{
		steps.get(null).addWidgetHighlight(groupID, childID, childChildID);
		return this;
	}

	@Override
	protected void setupIcon()
	{
		steps.get(null).setupIcon();
	}

	@Override
	public QuestStep addIcon(int iconItemID)
	{
		steps.get(null).addIcon(iconItemID);
		return this;
	}

	@Override
	public List<QuestStep> getSubsteps()
	{
		if (questHelperConfig.solvePuzzles())
		{
			return steps.get(null).getSubsteps();
		}
		else
		{
			return noSolvingStep.getSubsteps();
		}
	}

	@Override
	public void addSubSteps(QuestStep... substep)
	{
		noSolvingStep.addSubSteps(Arrays.asList(substep));
	}

	@Override
	public void addSubSteps(Collection<QuestStep> substeps)
	{
		noSolvingStep.addSubSteps(substeps);
	}
}
