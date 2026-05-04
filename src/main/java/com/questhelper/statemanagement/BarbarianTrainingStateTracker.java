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
package com.questhelper.statemanagement;

import com.questhelper.config.ConfigKeys;
import com.questhelper.requirements.*;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.npc.DialogRequirement;
import com.questhelper.requirements.runelite.RuneliteRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.widget.WidgetTextRequirement;
import net.runelite.api.Client;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class BarbarianTrainingStateTracker
{
	@Inject
	Client client;

	Requirement taskedWithPyre;
	Requirement sacrificedRemains;
	Requirement finishedPyre;

	RequirementValidator reqs;


	public void startUp(ConfigManager configManager, EventBus eventBus)
	{
		taskedWithPyre = new RuneliteRequirement(
			configManager, ConfigKeys.BARBARIAN_TRAINING_STARTED_PYREMAKING.getKey(),
			new Conditions(true, LogicType.OR,
				new DialogRequirement("Dive into the whirlpool in the lake to the east. The spirits will use their abilities to ensure you arrive in the correct location. Be warned, their influence fades, so you must find y"),
				new DialogRequirement("I will repeat myself fully, since this is quite complex. Listen well."),
				new WidgetTextRequirement(InterfaceID.Questjournal.TEXTLAYER, true, "Otto<col=000080> has tasked me with learning how to <col=800000>create pyre ships")
			)
		);

		// Finished tasks
		finishedPyre = new RuneliteRequirement(
			configManager, ConfigKeys.BARBARIAN_TRAINING_FINISHED_PYREMAKING.getKey(),
			new Conditions(true, LogicType.OR,
				new DialogRequirement("On this great day you have my eternal thanks. May you find riches while rescuing my spiritual ancestors in the caverns for many moons to come."),
				new WidgetTextRequirement(InterfaceID.Questjournal.TEXTLAYER, true, "I managed to create a pyre ship!")
			),
			"Finished Barbarian Pyremaking"
		);

		// Mid-conditions
		sacrificedRemains = new RuneliteRequirement(
			configManager, ConfigKeys.BARBARIAN_TRAINING_PYRE_MADE.getKey(),
			new Conditions(true, LogicType.OR,
				new MultiChatMessageRequirement(
					new ChatMessageRequirement("The ancient barbarian is laid to rest."),
					new ChatMessageRequirement("You feel you have learned more of barbarian ways. Otto might wish to talk to you more.")
				),
				new WidgetTextRequirement(InterfaceID.Questjournal.TEXTLAYER, true, "I've managed to <col=800000>create a pyre ship<col=000080>! I should let")
			)
		);


		reqs = new RequirementValidator(client,
			eventBus,
			taskedWithPyre,
			sacrificedRemains,
			finishedPyre
		);

		eventBus.register(reqs);
		reqs.startUp();
	}


	public void shutDown(EventBus eventBus)
	{
		eventBus.unregister(reqs);
	}
}
