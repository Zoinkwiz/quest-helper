/*
 * Copyright (c) 2019, Trevor <https://github.com/Trevor159>
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
package com.questhelper.questhelpers;

import com.google.inject.Binder;
import com.google.inject.CreationException;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.questhelper.QuestHelperConfig;
import com.questhelper.QuestHelperPlugin;
import com.questhelper.QuestHelperQuest;
import com.questhelper.panel.PanelDetails;
import com.questhelper.requirements.AbstractRequirement;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.requirements.Requirement;
import java.awt.Graphics;
import java.util.Collection;
import java.util.List;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.QuestState;
import net.runelite.client.eventbus.EventBus;
import com.questhelper.steps.OwnerStep;
import com.questhelper.steps.QuestStep;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;

public abstract class QuestHelper implements Module, QuestDebugRenderer
{
	@Inject
	protected Client client;

	@Getter
	@Setter
	protected QuestHelperConfig config;

	@Inject
	private EventBus eventBus;

	@Getter
	private QuestStep currentStep;

	@Getter
	@Setter
	private QuestHelperQuest quest;

	@Setter
	private Injector injector;

	@Override
	public void configure(Binder binder)
	{
	}

	public abstract void startUp(QuestHelperConfig config);

	public abstract void shutDown();

	public abstract boolean updateQuest();

	public void debugStartup(QuestHelperConfig config) {}

	protected void startUpStep(QuestStep step)
	{
		if (step != null)
		{
			currentStep = step;
			currentStep.startUp();
			eventBus.register(currentStep);
		}
		else
		{
			currentStep = null;
		}
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

	protected void instantiateSteps(Collection<QuestStep> steps)
	{
		for (QuestStep step : steps)
		{
			instantiateStep(step);
			if (step instanceof OwnerStep)
			{
				instantiateSteps(((OwnerStep) step).getSteps());
			}
		}
	}

	public void instantiateStep(QuestStep questStep)
	{
		try
		{
			if (questStep != null)
			{
				injector.injectMembers(questStep);
			}
		}
		catch (CreationException ex)
		{
			ex.printStackTrace();
		}
	}

	public boolean isCompleted()
	{
		return getState(client) == QuestState.FINISHED;
	}

	public QuestState getState(Client client)
	{
		return quest.getState(client);
	}

	public boolean clientMeetsRequirements()
	{
		if (getGeneralRequirements() == null)
		{
			return true;
		}

		for (Requirement generalRequirement : getGeneralRequirements())
		{
			if (generalRequirement != null && !generalRequirement.check(client))
			{
				return false;
			}
		}
		return true;
	}

	@Override
	public void renderDebugOverlay(Graphics graphics, QuestHelperPlugin plugin, QuestHelper quest, PanelComponent panelComponent)
	{
		if (!plugin.isDeveloperMode()) return;
		panelComponent.getChildren().add(LineComponent.builder()
			.left("Quest")
			.leftColor(ColorScheme.BRAND_ORANGE_TRANSPARENT)
			.right("Var")
			.rightColor(ColorScheme.BRAND_ORANGE_TRANSPARENT)
			.build()
		);
		panelComponent.getChildren().add(LineComponent.builder()
			.left(quest.getQuest().getName())
			.leftColor(quest.getConfig().debugColor())
			.right(quest.getVar() + "")
			.rightColor(quest.getConfig().debugColor())
			.build()
		);
	}

	public int getVar()
	{
		return quest.getVar(client);
	}

	public List<ItemRequirement> getItemRequirements()
	{
		return null;
	}

	public List<Requirement> getGeneralRequirements()
	{
		return null;
	}

	public List<ItemRequirement> getItemRecommended()
	{
		return null;
	}

	public List<Requirement> getGeneralRecommended()
	{
		return null;
	}

	public List<String> getCombatRequirements()
	{
		return null;
	}

	public List<String> getNotes()
	{
		return null;
	}

	public abstract List<PanelDetails> getPanels();
}
