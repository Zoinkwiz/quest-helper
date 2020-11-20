/*
 * Copyright (c) 2020, Zoinkwiz <https://github.com/Zoinkwiz>
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
import com.questhelper.requirements.Requirement;
import com.questhelper.steps.conditional.WidgetTextCondition;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import net.runelite.api.ChatMessageType;
import net.runelite.api.GameState;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import com.questhelper.QuestHelperPlugin;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.steps.conditional.ChatMessageCondition;
import com.questhelper.steps.conditional.ConditionForStep;
import com.questhelper.steps.conditional.Conditions;
import com.questhelper.steps.conditional.NpcCondition;
import net.runelite.client.ui.overlay.components.PanelComponent;
import org.apache.commons.lang3.ArrayUtils;

/* Conditions are checked in the order they were added */
public class ConditionalStep extends QuestStep implements OwnerStep
{
	@Inject
	protected EventBus eventBus;

	protected boolean started = false;

	protected final LinkedHashMap<Conditions, QuestStep> steps;
	protected final ArrayList<ChatMessageCondition> chatConditions = new ArrayList<>();
	protected final ArrayList<NpcCondition> npcConditions = new ArrayList<>();
	protected final ArrayList<WidgetTextCondition> widgetConditions = new ArrayList<>();

	protected QuestStep currentStep;

	protected Requirement[] requirements;

	public ConditionalStep(QuestHelper questHelper, QuestStep step, Requirement... requirements)
	{
		super(questHelper);
		this.requirements = requirements;
		this.steps = new LinkedHashMap<>();
		this.steps.put(null, step);
	}

	public ConditionalStep(QuestHelper questHelper, QuestStep step, String text, Requirement... requirements)
	{
		super(questHelper, text);
		this.requirements = requirements;
		this.steps = new LinkedHashMap<>();
		this.steps.put(null, step);
	}

	public void addStep(Conditions conditions, QuestStep step)
	{
		this.steps.put(conditions, step);

		checkForChatConditions(conditions);
		checkForNpcConditions(conditions);
		checkForWidgetConditions(conditions);
	}

	public void addStep(Conditions conditions, QuestStep step, boolean isLockable)
	{
		step.setLockable(isLockable);
		this.steps.put(conditions, step);

		checkForChatConditions(conditions);
		checkForNpcConditions(conditions);
		checkForWidgetConditions(conditions);
	}

	public void addStep(ConditionForStep condition, QuestStep step)
	{
		this.steps.put(new Conditions(condition), step);

		checkForChatConditions(condition);
		checkForNpcConditions(condition);
		checkForWidgetConditions(condition);
	}

	public void addStep(ConditionForStep condition, QuestStep step, boolean isLockable)
	{
		Conditions conditions = new Conditions(condition);
		step.setLockable(isLockable);
		this.steps.put(conditions, step);

		checkForChatConditions(condition);
		checkForNpcConditions(condition);
		checkForWidgetConditions(condition);
	}

	public void checkForChatConditions(ConditionForStep condition)
	{
		if (condition != null && condition.getConditions() == null)
		{
			if (condition.getClass() == ChatMessageCondition.class && !chatConditions.contains(condition))
			{
				chatConditions.add((ChatMessageCondition) condition);
			}
		}
		else
		{
			for (ConditionForStep subCondition : condition.getConditions())
			{
				checkForChatConditions(subCondition);
			}
		}
	}

	public void checkForNpcConditions(ConditionForStep condition)
	{
		if (condition != null && condition.getConditions() == null)
		{
			if (condition.getClass() == NpcCondition.class && !npcConditions.contains(condition))
			{
				npcConditions.add((NpcCondition) condition);
			}
		}
		else
		{
			for (ConditionForStep subCondition : condition.getConditions())
			{
				checkForNpcConditions(subCondition);
			}
		}
	}

	public void checkForWidgetConditions(ConditionForStep condition)
	{
		if (condition != null && condition.getConditions() == null)
		{
			if (condition.getClass() == WidgetTextCondition.class && !widgetConditions.contains(condition))
			{
				widgetConditions.add((WidgetTextCondition) condition);
			}
		}
		else
		{
			for (ConditionForStep subCondition : condition.getConditions())
			{
				checkForWidgetConditions(subCondition);
			}
		}
	}

	@Override
	public void startUp()
	{
		for (Conditions conditions : steps.keySet())
		{
			if (conditions != null)
			{
				conditions.initialize(client);
			}
		}
		updateSteps();
		started = true;
	}

	@Override
	public void shutDown()
	{
		started = false;
		shutDownStep();
		currentStep = null;
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (started)
		{
			updateSteps();
		}
	}

	@Subscribe
	public void onGameStateChanged(final GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOADING || event.getGameState() == GameState.HOPPING)
		{
			for (Conditions conditions : steps.keySet())
			{
				if (conditions != null)
				{
					conditions.loadingHandler();
				}
			}
		}
	}

	@Subscribe
	public void onChatMessage(ChatMessage chatMessage)
	{
		if (chatMessage.getType() == ChatMessageType.GAMEMESSAGE)
		{
			for (ChatMessageCondition step : chatConditions)
			{
				step.validateCondition(client, chatMessage.getMessage());
			}
		}
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned event)
	{
		for (NpcCondition condition : npcConditions)
		{
			condition.checkNpcSpawned(event.getNpc());
		}
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned event)
	{
		for (NpcCondition condition : npcConditions)
		{
			condition.checkNpcDespawned(event.getNpc().getId());
		}
	}

	@Override
	public void onWidgetLoaded(final WidgetLoaded event)
	{
		super.onWidgetLoaded(event);
		for (WidgetTextCondition condition : widgetConditions)
		{
			if (condition.getGroupId() == event.getGroupId())
			{
				condition.checkWidgetText(client);
			}
		}
	}

	protected void updateSteps()
	{
		Conditions lastPossibleCondition = null;

		for (Conditions conditions : steps.keySet())
		{
			boolean stepIsLocked = steps.get(conditions).isLocked();
			if (conditions != null && conditions.checkCondition(client) && !stepIsLocked)
			{
				startUpStep(steps.get(conditions));
				return;
			}
			else if (steps.get(conditions).isBlocker() && stepIsLocked)
			{
				startUpStep(steps.get(lastPossibleCondition));
				return;
			}
			else if (conditions != null && !stepIsLocked)
			{
				lastPossibleCondition = conditions;
			}
		}

		if (!steps.get(null).isLocked())
		{
			startUpStep(steps.get(null));
		}
		else
		{
			startUpStep(steps.get(lastPossibleCondition));
		}
	}

	protected void startUpStep(QuestStep step)
	{
		if (currentStep == null)
		{
			eventBus.register(step);
			step.startUp();
			currentStep = step;
			return;
		}

		if (!step.equals(currentStep))
		{
			shutDownStep();
			eventBus.register(step);
			step.startUp();
			currentStep = step;
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

	@Override
	public void makeOverlayHint(PanelComponent panelComponent, QuestHelperPlugin plugin, Requirement... additionalRequirements)
	{
		Requirement[] allRequirements = ArrayUtils.addAll(additionalRequirements, requirements);

		if (currentStep != null)
		{
			if (text == null)
			{
				currentStep.makeOverlayHint(panelComponent, plugin, allRequirements);
			}
			else
			{
				currentStep.makeOverlayHint(panelComponent, plugin, text, allRequirements);
			}
		}
	}

	// This should only have been called from a parent ConditionalStep, so default the additional text to the passed in text
	@Override
	public void makeOverlayHint(PanelComponent panelComponent, QuestHelperPlugin plugin, ArrayList<String> additionalText, Requirement... additionalRequirements)
	{
		Requirement[] allRequirements = ArrayUtils.addAll(additionalRequirements, requirements);

		if (currentStep != null)
		{
			currentStep.makeOverlayHint(panelComponent, plugin, additionalText, allRequirements);
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
	public QuestStep getActiveStep()
	{
		if (currentStep != null)
		{
			return currentStep.getActiveStep();
		}

		return this;
	}

	@Override
	public QuestStep getSidePanelStep()
	{
		if (text != null)
		{
			return this;
		}
		else if (currentStep != null)
		{
			return currentStep.getSidePanelStep();
		}

		return this;
	}

	@Override
	public Collection<QuestStep> getSteps()
	{
		return steps.values();
	}
}
