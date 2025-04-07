/*
 * Copyright (c) 2020, Zoinkwiz
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

import com.google.inject.Inject;
import com.questhelper.QuestHelperPlugin;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.Requirement;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import lombok.NonNull;
import net.runelite.api.Client;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.ui.overlay.components.PanelComponent;

public class DetailedOwnerStep extends QuestStep implements OwnerStep
{
	protected QuestStep currentStep;

	protected List<Requirement> requirements = new ArrayList<>();

	@Inject
	protected EventBus eventBus;

	@Inject
	protected Client client;

	public DetailedOwnerStep(QuestHelper questHelper, Requirement... requirements)
	{
		super(questHelper);
		this.requirements.addAll(Arrays.asList(requirements));
		setupSteps();
		addSubSteps(getSteps());
	}

	public DetailedOwnerStep(QuestHelper questHelper, String text, Requirement... requirements)
	{
		super(questHelper, text);
		this.requirements.addAll(Arrays.asList(requirements));
		setupSteps();
		addSubSteps(getSteps());
	}

	@Override
	public void startUp()
	{
		updateSteps();
	}

	@Override
	public void shutDown()
	{
		shutDownStep();
		currentStep = null;
	}

	protected void startUpStep(QuestStep step)
	{
		if (step.equals(currentStep)) return;

		if (currentStep != null)
		{
			shutDownStep();
		}

		eventBus.register(step);
		step.startUp();
		currentStep = step;
	}

	protected void shutDownStep()
	{
		if (currentStep != null)
		{
			eventBus.unregister(currentStep);
			currentStep.shutDown();
			currentStep = null;
		}
	}

	protected void updateSteps()
	{
	}

	// This should only have been called from a parent ConditionalStep, so default the additional text to the passed in text
	@Override
	public void makeOverlayHint(PanelComponent panelComponent, QuestHelperPlugin plugin, @NonNull List<String> additionalText, @NonNull List<Requirement> additionalRequirements)
	{
		List<Requirement> allRequirements = new ArrayList<>(additionalRequirements);
		allRequirements.addAll(requirements);

		List<String> allAdditionalText = new ArrayList<>(additionalText);
		if (text != null) allAdditionalText.addAll(text);

		if (currentStep != null)
		{
			currentStep.makeOverlayHint(panelComponent, plugin, allAdditionalText, allRequirements);
		}
	}

	@Override
	public void makeWorldOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin)
	{
		if (currentStep != null)
		{
			currentStep.makeWorldOverlayHint(graphics, plugin);
		}
	}

	@Override
	public void makeWorldArrowOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin)
	{
		if (currentStep != null)
		{
			currentStep.makeWorldArrowOverlayHint(graphics, plugin);
		}
	}

	@Override
	public void makeWorldLineOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin)
	{
		if (currentStep != null)
		{
			currentStep.makeWorldLineOverlayHint(graphics, plugin);
		}
	}

	@Override
	public void makeWidgetOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin)
	{
		if (currentStep != null)
		{
			currentStep.makeWidgetOverlayHint(graphics, plugin);
		}
	}

	@Override
	public void renderQuestStepTooltip(PanelComponent panelComponent, boolean isMenuOpen, boolean isBackgroundHelper)
	{
		if (currentStep != null)
		{
			currentStep.renderQuestStepTooltip(panelComponent, isMenuOpen, isBackgroundHelper);
		}
	}
	@Override
	public QuestStep getActiveStep()
	{
		if (currentStep != null)
		{
			return currentStep.getActiveStep();
		}
		else
		{
			return this;
		}
	}

	protected void setupSteps()
	{
	}

	@Override
	public Collection<QuestStep> getSteps()
	{
		return null;
	}
}

