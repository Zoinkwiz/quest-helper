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

	Requirement taskedWithFarming;
	Requirement taskedWithBowFiremaking;
	Requirement taskedWithPyre;
	Requirement taskedWithPotSmashing;
	Requirement taskedWithSpears;
	Requirement taskedWithHastae;
	Requirement plantedSeed;
	Requirement smashedPot;
	Requirement litFireWithBow;
	Requirement sacrificedRemains;
	Requirement madeSpear;
	Requirement madeHasta;
	Requirement finishedSeedPlanting;
	Requirement finishedPotSmashing;
	Requirement finishedFiremaking;
	Requirement finishedPyre;
	Requirement finishedSpear;
	Requirement finishedHasta;

	RequirementValidator reqs;


	public void startUp(ConfigManager configManager, EventBus eventBus)
	{
		taskedWithFarming = new RuneliteRequirement(
			configManager, ConfigKeys.BARBARIAN_TRAINING_STARTED_SEED_PLANTING.getKey(),
			new Conditions(true, LogicType.OR,
				new DialogRequirement("Remember to be calm, and good luck."),
				new DialogRequirement("I see you have yet to be successful in planting a seed with your fists."),
				new WidgetTextRequirement(InterfaceID.Questjournal.TEXTLAYER, true, "plant a seed with")
			)
		);

		taskedWithPotSmashing = new RuneliteRequirement(
			configManager, ConfigKeys.BARBARIAN_TRAINING_STARTED_POT_SMASHING.getKey(),
			new Conditions(true, LogicType.OR,
				new DialogRequirement("May the spirits guide you into success."),
				new DialogRequirement("You have not yet attempted to plant a tree. Why not?"),
				new WidgetTextRequirement(InterfaceID.Questjournal.TEXTLAYER, true, "Otto<col=000080> has tasked me with learning how to <col=800000>smash pots after")
			)
		);

		taskedWithBowFiremaking = new RuneliteRequirement(
			configManager, ConfigKeys.BARBARIAN_TRAINING_STARTED_FIREMAKING.getKey(),
			new Conditions(true, LogicType.OR,
				new DialogRequirement("The spirits will aid you. The power they supply will guide your hands. Go and benefit from their guidance upon oak logs."),
				new DialogRequirement("By now you know my response."),
				new WidgetTextRequirement(InterfaceID.Questjournal.TEXTLAYER, true, "light a fire with")
			)
		);

		taskedWithPyre = new RuneliteRequirement(
			configManager, ConfigKeys.BARBARIAN_TRAINING_STARTED_PYREMAKING.getKey(),
			new Conditions(true, LogicType.OR,
				new DialogRequirement("Dive into the whirlpool in the lake to the east. The spirits will use their abilities to ensure you arrive in the correct location. Be warned, their influence fades, so you must find y"),
				new DialogRequirement("I will repeat myself fully, since this is quite complex. Listen well."),
				new WidgetTextRequirement(InterfaceID.Questjournal.TEXTLAYER, true, "Otto<col=000080> has tasked me with learning how to <col=800000>create pyre ships")
			)
		);

		taskedWithSpears = new RuneliteRequirement(
			configManager, ConfigKeys.BARBARIAN_TRAINING_STARTED_SPEAR.getKey(),
			new Conditions(true, LogicType.OR,
				new DialogRequirement("Note well that you will require wood for the spear shafts. The quality of wood must be similar to that of the metal involved."),
				new DialogRequirement("You do not exude the presence of one who has poured his soul into manufacturing spears."),
				new WidgetTextRequirement(InterfaceID.Questjournal.TEXTLAYER, true, "Otto<col=000080> has tasked me with learning how to <col=800000>smith spears")
			)
		);

		taskedWithHastae = new RuneliteRequirement(
			configManager, ConfigKeys.BARBARIAN_TRAINING_STARTED_HASTA.getKey(),
			new Conditions(true, LogicType.OR,
				new DialogRequirement("Indeed. You may use our special anvil for this spear type too. The ways of black and dragon hastae are beyond our knowledge, however."),
				new DialogRequirement("Take some wood and metal and make a spear upon the<br>nearby anvil, then you may return to me. As an<br>example, you may use bronze bars with normal logs or<br>iron bars with oak logs."),
				new WidgetTextRequirement(InterfaceID.Questjournal.TEXTLAYER, true, " has tasked me with learning how to <col=800000>smith a hasta")
			)
		);

		// Finished tasks
		finishedSeedPlanting = new RuneliteRequirement(
			configManager, ConfigKeys.BARBARIAN_TRAINING_FINISHED_SEED_PLANTING.getKey(),
			new Conditions(true, LogicType.OR,
				new DialogRequirement("No child, but we all have potential to improve our strength."),
				new WidgetTextRequirement(InterfaceID.Questjournal.TEXTLAYER, true, "<str>I managed to plant a seed with my fists!")
			),
			"Finished Barbarian Seed Planting"
		);

		finishedPotSmashing = new RuneliteRequirement(
			configManager, ConfigKeys.BARBARIAN_TRAINING_FINISHED_POT_SMASHING.getKey(),
			new Conditions(true, LogicType.OR,
				new DialogRequirement("It will become more natural with practice."),
				new WidgetTextRequirement(InterfaceID.Questjournal.TEXTLAYER, true, "<str>I managed to smash a plant pot without littering!")
			),
			"Finished Barbarian Pot Smashing"
		);

		finishedFiremaking = new RuneliteRequirement(
			configManager, ConfigKeys.BARBARIAN_TRAINING_FINISHED_FIREMAKING.getKey(),
			new Conditions(true, LogicType.OR,
				new DialogRequirement("Fine news indeed!"),
				new WidgetTextRequirement(InterfaceID.Questjournal.TEXTLAYER, true, "I managed to light a fire with a bow!")
			),
			"Finished Barbarian Firemaking"
		);

		finishedPyre = new RuneliteRequirement(
			configManager, ConfigKeys.BARBARIAN_TRAINING_FINISHED_PYREMAKING.getKey(),
			new Conditions(true, LogicType.OR,
				new DialogRequirement("On this great day you have my eternal thanks. May you find riches while rescuing my spiritual ancestors in the caverns for many moons to come."),
				new WidgetTextRequirement(InterfaceID.Questjournal.TEXTLAYER, true, "I managed to create a pyre ship!")
			),
			"Finished Barbarian Pyremaking"
		);

		finishedSpear = new RuneliteRequirement(
			configManager, ConfigKeys.BARBARIAN_TRAINING_FINISHED_SPEAR.getKey(),
			new Conditions(true, LogicType.OR,
				new DialogRequirement("The manufacture of spears is now yours as a speciality. Use your skill well."),
				new WidgetTextRequirement(InterfaceID.Questjournal.TEXTLAYER, true, "I managed to smith a spear!")
			),
			"Finished Barbarian Spear Smithing"
		);

		finishedHasta = new RuneliteRequirement(
			configManager, ConfigKeys.BARBARIAN_TRAINING_FINISHED_HASTA.getKey(),
			new Conditions(true, LogicType.OR,
				new DialogRequirement("To live life to it's fullest of course - that you may be a peaceful spirit when your time ends."),
				new WidgetTextRequirement(InterfaceID.Questjournal.TEXTLAYER, true, "I managed to create a hasta!")
			),
			"Finished Barbarian Hasta Smithing"
		);

		// Mid-conditions
		plantedSeed = new RuneliteRequirement(
			configManager, ConfigKeys.BARBARIAN_TRAINING_PLANTED_SEED.getKey(),
			new Conditions(true, LogicType.OR,
				new MultiChatMessageRequirement(
					new ChatMessageRequirement("You plant "),
					new ChatMessageRequirement("You feel you have learned more of barbarian ways. Otto might wish to talk to you more.")
				),
				new WidgetTextRequirement(InterfaceID.Questjournal.TEXTLAYER, true, "I've managed to <col=800000>plant a seed with my fists<col=000080>!")
			)
		);

		smashedPot = new RuneliteRequirement(
			configManager, ConfigKeys.BARBARIAN_TRAINING_SMASHED_POT.getKey(),
			new Conditions(true, LogicType.OR,
				new MultiChatMessageRequirement(
					new ChatMessageRequirement("You plant "),
					new ChatMessageRequirement(" sapling"),
					new ChatMessageRequirement("You feel you have learned more of barbarian ways. Otto might wish to talk to you more.")
				),
				new WidgetTextRequirement(InterfaceID.Questjournal.TEXTLAYER, true, "I've managed to <col=800000>smash a pot without littering<col=000080>!")
			)
		);

		litFireWithBow = new RuneliteRequirement(
			configManager, ConfigKeys.BARBARIAN_TRAINING_BOW_FIRE.getKey(),
			new Conditions(true, LogicType.OR,
				new MultiChatMessageRequirement(
					new ChatMessageRequirement("The fire catches and the logs begin to burn."),
					new MesBoxRequirement("You feel you have learned more of barbarian ways. Otto might wish to talk to you more.")
				),
				new WidgetTextRequirement(InterfaceID.Questjournal.TEXTLAYER, true, "I've managed to <col=800000>light a fire with a bow<col=000080>!")
			)
		);

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

		madeSpear = new RuneliteRequirement(
			configManager, ConfigKeys.BARBARIAN_TRAINING_MADE_SPEAR.getKey(),
			new Conditions(true, LogicType.OR,
				new MultiChatMessageRequirement(
					new ChatMessageRequirement("You make a "),
					new ChatMessageRequirement(" spear."),
					new MesBoxRequirement("You feel you have learned more of barbarian ways. Otto might wish to talk to you more.")
				),
				new WidgetTextRequirement(InterfaceID.Questjournal.TEXTLAYER, true, "I've managed to <col=800000>smith a spear<col=000080>!")
			)
		);

		madeHasta = new RuneliteRequirement(
			configManager, ConfigKeys.BARBARIAN_TRAINING_MADE_HASTA.getKey(),
			new Conditions(true, LogicType.OR,
				new MultiChatMessageRequirement(
					new ChatMessageRequirement("You make a "),
					new ChatMessageRequirement(" hasta."),
					new MesBoxRequirement("You feel you have learned more of barbarian ways. Otto might wish to talk to you more.")
				),
				new WidgetTextRequirement(InterfaceID.Questjournal.TEXTLAYER, true, "I've managed to <col=800000>smith a hasta<col=000080>!")
			)
		);


		reqs = new RequirementValidator(client,
			eventBus,
			taskedWithFarming,
			taskedWithBowFiremaking,
			taskedWithPyre,
			taskedWithPotSmashing,
			taskedWithSpears,
			taskedWithHastae,
			plantedSeed,
			smashedPot,
			litFireWithBow,
			sacrificedRemains,
			madeSpear,
			madeHasta,
			finishedSeedPlanting,
			finishedPotSmashing,
			finishedFiremaking,
			finishedPyre,
			finishedSpear,
			finishedHasta
		);

		eventBus.register(reqs);
		reqs.startUp();
	}


	public void shutDown(EventBus eventBus)
	{
		eventBus.unregister(reqs);
	}
}
