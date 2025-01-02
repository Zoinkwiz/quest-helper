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
import com.questhelper.requirements.MultiChatMessageRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.npc.DialogRequirement;
import com.questhelper.requirements.runelite.RuneliteRequirement;
import com.questhelper.requirements.conditional.InitializableRequirement;
import java.awt.Graphics2D;
import java.util.*;
import java.util.function.Consumer;
import lombok.NonNull;
import lombok.Setter;
import net.runelite.api.GameState;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.NpcChanged;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import com.questhelper.QuestHelperPlugin;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.ChatMessageRequirement;
import com.questhelper.requirements.conditional.NpcCondition;
import net.runelite.client.ui.overlay.components.PanelComponent;

/* Conditions are checked in the order they were added */
public class ConditionalStep extends QuestStep implements OwnerStep
{
	@Inject
	protected EventBus eventBus;

	protected boolean started = false;

	@Setter
	protected boolean checkAllChildStepsOnListenerCall = false;

	protected final LinkedHashMap<Requirement, QuestStep> steps;
	protected final List<ChatMessageRequirement> chatConditions = new ArrayList<>();
	protected final List<NpcCondition> npcConditions = new ArrayList<>();
	protected final List<DialogRequirement> dialogConditions = new ArrayList<>();
	protected final List<RuneliteRequirement> runeliteConditions = new ArrayList<>();

	protected QuestStep currentStep;

	protected List<Requirement> requirements = new ArrayList<>();

	public ConditionalStep(QuestHelper questHelper, QuestStep step, Requirement... requirements)
	{
		super(questHelper);
		this.requirements.addAll(Arrays.asList(requirements));
		this.steps = new LinkedHashMap<>();
		this.steps.put(null, step);
	}

	public ConditionalStep(QuestHelper questHelper, QuestStep step, String text, Requirement... requirements)
	{
		super(questHelper, text);
		this.requirements.addAll(Arrays.asList(requirements));
		this.steps = new LinkedHashMap<>();
		this.steps.put(null, step);
	}

	public void addStep(Requirement requirement, QuestStep step)
	{
		addStep(requirement, step, false);
	}

	public void addStep(Requirement requirement, QuestStep step, boolean isLockable)
	{
		step.setLockable(isLockable);
		this.steps.put(requirement, step);

		checkForConditions(requirement);
	}

	private void checkForConditions(Requirement requirement)
	{
		checkForChatConditions(requirement);
		checkForDialogConditions(requirement);
		checkForNpcConditions(requirement);
		checkForRuneliteConditions(requirement);

		if ((requirement instanceof InitializableRequirement))
		{
			((InitializableRequirement) requirement).getConditions().forEach(this::checkForConditions);
		}
		if (requirement instanceof RuneliteRequirement)
		{
			((RuneliteRequirement) requirement).getRequirements().values().forEach(this::checkForConditions);
		}
	}

	public void checkForChatConditions(Requirement requirement)
	{
		if (!(requirement instanceof InitializableRequirement))
		{
			return;
		}

		InitializableRequirement condition = (InitializableRequirement) requirement;

		if (condition instanceof MultiChatMessageRequirement && !chatConditions.contains(condition))
		{
			chatConditions.add((MultiChatMessageRequirement) condition);
		}

		if (condition instanceof ChatMessageRequirement && !chatConditions.contains(condition))
		{
			chatConditions.add((ChatMessageRequirement) condition);
		}
		condition.getConditions().forEach(this::checkForChatConditions);
	}

	public void checkForDialogConditions(Requirement requirement)
	{
		if (requirement instanceof DialogRequirement && !dialogConditions.contains(requirement))
		{
			DialogRequirement runeliteReq = (DialogRequirement) requirement;
			dialogConditions.add(runeliteReq);
		}
	}

	public void checkForNpcConditions(Requirement requirement)
	{
		if (!(requirement instanceof InitializableRequirement))
		{
			return;
		}

		InitializableRequirement condition = (InitializableRequirement) requirement;

		if (condition.getConditions().isEmpty())
		{
			if (condition instanceof NpcCondition && !npcConditions.contains(condition))
			{
				npcConditions.add((NpcCondition) condition);
			}
		}
	}

	public void checkForRuneliteConditions(Requirement requirement)
	{
		if (requirement instanceof RuneliteRequirement && !runeliteConditions.contains(requirement))
		{
			RuneliteRequirement runeliteReq = (RuneliteRequirement) requirement;
			runeliteConditions.add(runeliteReq);
		}
	}

	@Override
	public void startUp()
	{
		steps.keySet().stream()
			.filter(InitializableRequirement.class::isInstance)
			.forEach(req -> ((InitializableRequirement) req).initialize(client));
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
			checkRuneliteConditions(checkAllChildStepsOnListenerCall);
			updateSteps();
		}
	}

	public void checkRuneliteConditions(boolean parentDefinedRecursion)
	{
		for (RuneliteRequirement runeliteCondition : runeliteConditions)
		{
			runeliteCondition.validateCondition(client);
		}

		handleChildRequirementValidation(step -> step.checkRuneliteConditions(parentDefinedRecursion), parentDefinedRecursion);
	}

	@Subscribe
	public void onGameStateChanged(final GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOADING || event.getGameState() == GameState.HOPPING)
		{
			steps.keySet().stream()
				.filter(Objects::nonNull)
				.filter(InitializableRequirement.class::isInstance)
				.forEach(req -> ((InitializableRequirement) req).updateHandler());

			// TODO: Work out if this needs to account for checkAllChildStepsOnListenerCall
		}
	}

	public void addRequirement(Requirement requirement)
	{
		requirements.add(requirement);
	}

	@Subscribe
	public void onChatMessage(ChatMessage chatMessage)
	{
		handleChatMessage(chatMessage, checkAllChildStepsOnListenerCall);
	}

	public void handleChatMessage(ChatMessage chatMessage, boolean parentDefinedRecursion)
	{
		chatConditions.forEach(requirement -> requirement.validateCondition(client, chatMessage));
		dialogConditions.forEach(requirement -> requirement.validateCondition(chatMessage));

		handleChildRequirementValidation(step -> step.handleChatMessage(chatMessage, parentDefinedRecursion), parentDefinedRecursion);
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned event)
	{
		handleNpcSpawned(event, checkAllChildStepsOnListenerCall);
	}

	public void handleNpcSpawned(NpcSpawned npcSpawned, boolean parentDefinedRecursion)
	{
		npcConditions.forEach(npc -> npc.checkNpcSpawned(npcSpawned.getNpc()));
		handleChildRequirementValidation(step -> step.handleNpcSpawned(npcSpawned, parentDefinedRecursion), parentDefinedRecursion);
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned event)
	{
		handleNpcDespawned(event, checkAllChildStepsOnListenerCall);
	}

	public void handleNpcDespawned(NpcDespawned npcDespawned, boolean parentDefinedRecursion)
	{
		npcConditions.forEach(npc -> npc.checkNpcDespawned(npcDespawned.getNpc()));
		handleChildRequirementValidation(step -> step.handleNpcDespawned(npcDespawned, parentDefinedRecursion), parentDefinedRecursion);
	}

	@Subscribe
	public void onNpcChanged(NpcChanged npcCompositionChanged)
	{
		handleNpcChanged(npcCompositionChanged, checkAllChildStepsOnListenerCall);
	}

	public void handleNpcChanged(NpcChanged npcChanged, boolean parentDefinedRecursion)
	{
		npcConditions.forEach(npc -> npc.checkNpcChanged(npcChanged));
		handleChildRequirementValidation(step -> step.handleNpcChanged(npcChanged, parentDefinedRecursion), parentDefinedRecursion);
	}

	private void handleChildRequirementValidation(Consumer<ConditionalStep> stepAction, boolean parentDefinedRecursion)
	{
		if (checkAllChildStepsOnListenerCall || parentDefinedRecursion)
		{
			steps.values().stream()
				.filter(ConditionalStep.class::isInstance)
				.map(ConditionalStep.class::cast)
				.forEach(stepAction);
		}
	}

	protected void updateSteps()
	{
		Requirement lastPossibleCondition = null;

		for (Requirement conditions : steps.keySet())
		{
			boolean stepIsLocked = steps.get(conditions).isLocked();
			if (conditions != null && conditions.check(client) && !stepIsLocked)
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

		return this;
	}

	@Override
	public boolean containsSteps(QuestStep questStep, Set<QuestStep> checkedSteps)
	{
		if (super.containsSteps(questStep, checkedSteps)) return true;

		Set<QuestStep> stepSet = new HashSet<>(steps.values());
		stepSet.removeAll(checkedSteps);

		for (QuestStep child : stepSet)
		{
			if (child.containsSteps(questStep, checkedSteps)) return true;
		}

		return false;
	}

	public Collection<Requirement> getConditions()
	{
		return steps.keySet();
	}

	@Override
	public Collection<QuestStep> getSteps()
	{
		return steps.values();
	}

	public ConditionalStep copy()
	{
		ConditionalStep newStep = new ConditionalStep(getQuestHelper(), steps.get(null));
		if (text != null)
		{
			newStep.setText(text);
		}
		getConditions().stream()
			.filter(Objects::nonNull)
			.forEach(conditions -> newStep.addStep(conditions, steps.get(conditions)));
		return newStep;
	}
}
