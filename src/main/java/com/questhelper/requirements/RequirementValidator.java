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
package com.questhelper.requirements;

import com.questhelper.requirements.conditional.InitializableRequirement;
import com.questhelper.requirements.conditional.NpcCondition;
import com.questhelper.requirements.npc.DialogRequirement;
import com.questhelper.requirements.runelite.RuneliteRequirement;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.NpcChanged;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Acts as an always-on quest helper validating incoming RuneLite events
 * <p> 
 * Useful for keeping state about characters that OSRS doesn't provide as varbits (e.g. Barbarian Training)
 */
public class RequirementValidator
{
	protected Client client;

	protected EventBus eventBus;

	protected boolean started = false;

	protected final List<ChatMessageRequirement> chatConditions = new ArrayList<>();
	protected final List<NpcCondition> npcConditions = new ArrayList<>();
	protected final List<DialogRequirement> dialogConditions = new ArrayList<>();
	protected final List<RuneliteRequirement> runeliteConditions = new ArrayList<>();

	protected List<Requirement> requirements = new ArrayList<>();

	public RequirementValidator(Client client, EventBus eventBus, Requirement... requirements)
	{
		this.client = client;
		this.eventBus = eventBus;

		this.requirements.addAll(Arrays.asList(requirements));
		for (Requirement requirement : requirements)
		{
			checkForConditions(requirement);
		}
	}

	public void startUp()
	{
		requirements.stream()
			.filter(InitializableRequirement.class::isInstance)
			.forEach(req -> ((InitializableRequirement) req).initialize(client));
		started = true;
	}

	public void addRequirement(Requirement requirement)
	{
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

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (started)
		{
			checkRuneliteConditions();
			for (Requirement requirement : requirements)
			{
				requirement.check(client);
			}
		}
	}

	public void checkRuneliteConditions()
	{
		for (RuneliteRequirement runeliteCondition : runeliteConditions)
		{
			runeliteCondition.validateCondition(client);
		}
	}

	@Subscribe
	public void onGameStateChanged(final GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOADING || event.getGameState() == GameState.HOPPING)
		{
			requirements.stream()
				.filter(Objects::nonNull)
				.filter(InitializableRequirement.class::isInstance)
				.forEach(req -> ((InitializableRequirement) req).updateHandler());
		}
	}

	@Subscribe
	public void onChatMessage(ChatMessage chatMessage)
	{
		handleChatMessage(chatMessage);
	}

	public void handleChatMessage(ChatMessage chatMessage)
	{
		chatConditions.forEach(requirement -> requirement.validateCondition(client, chatMessage));
		dialogConditions.forEach(requirement -> requirement.validateCondition(chatMessage));
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned event)
	{
		handleNpcSpawned(event);
	}

	public void handleNpcSpawned(NpcSpawned npcSpawned)
	{
		npcConditions.forEach(npc -> npc.checkNpcDespawned(npcSpawned.getNpc()));
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned event)
	{
		handleNpcDespawned(event);
	}

	public void handleNpcDespawned(NpcDespawned npcDespawned)
	{
		npcConditions.forEach(npc -> npc.checkNpcDespawned(npcDespawned.getNpc()));
	}

	@Subscribe
	public void onNpcChanged(NpcChanged npcCompositionChanged)
	{
		handleNpcChanged(npcCompositionChanged);
	}

	public void handleNpcChanged(NpcChanged npcChanged)
	{
		npcConditions.forEach(npc -> npc.checkNpcChanged(npcChanged));
	}
}

