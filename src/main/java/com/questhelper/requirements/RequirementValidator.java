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

import com.questhelper.requirements.runelite.RuneliteRequirement;
import net.runelite.api.Client;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
		requirements.forEach(req -> req.register(client, eventBus));
		started = true;
	}

	public void shutDown()
	{
		started = false;
		requirements.forEach(req -> req.unregister(eventBus));
	}


	private void checkForConditions(Requirement requirement)
	{
		checkForRuneliteConditions(requirement);

		if (requirement instanceof RuneliteRequirement)
		{
			((RuneliteRequirement) requirement).getRequirements().values().forEach(this::checkForConditions);
		}
	}

	private void checkForRuneliteConditions(Requirement requirement)
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

	private void checkRuneliteConditions()
	{
		for (RuneliteRequirement runeliteCondition : runeliteConditions)
		{
			runeliteCondition.validateCondition(client);
		}
	}
}

