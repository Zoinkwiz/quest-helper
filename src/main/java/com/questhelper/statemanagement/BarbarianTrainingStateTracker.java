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
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.RequirementValidator;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.npc.DialogRequirement;
import com.questhelper.requirements.runelite.RuneliteRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.widget.WidgetTextRequirement;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import javax.inject.Inject;
import javax.inject.Singleton;
import static com.questhelper.requirements.util.LogicHelper.or;

@Singleton
public class BarbarianTrainingStateTracker
{
	@Inject
	Client client;

	Requirement taskedWithFishing, taskedWithHarpooning, taskedWithFarming, taskedWithBowFiremaking, taskedWithPyre, taskedWithPotSmashing,
		taskedWithSpears, taskedWithHastae, taskedWithHerblore, plantedSeed, smashedPot, litFireWithBow, sacrificedRemains, caughtBarbarianFish,
		caughtFishWithoutHarpoon, madePotion, madeSpear, madeHasta, finishedFishing, finishedHarpoon, finishedSeedPlanting, finishedPotSmashing,
		finishedFiremaking, finishedPyre, finishedSpear, finishedHasta, finishedHerblore;

	RequirementValidator reqs;


	public void startUp(ConfigManager configManager, EventBus eventBus)
	{
		taskedWithFishing = new RuneliteRequirement(
			configManager, ConfigKeys.BARBARIAN_TRAINING_STARTED_FISHING.getKey(),
			new Conditions(true, LogicType.OR,
				new DialogRequirement("Certainly. Take the rod from under my bed and fish in the lake. When you have caught a few fish, I am sure you will be ready to talk more with me."),
				new DialogRequirement("Alas, I do not sense that you have been successful in your fishing yet. The look in your eyes is not that of the osprey."),
				new WidgetTextRequirement(119, 3, true, "fish with a new")
			)
		);

		taskedWithHarpooning = new RuneliteRequirement(
			configManager, ConfigKeys.BARBARIAN_TRAINING_STARTED_HARPOON.getKey(),
			new Conditions(true, LogicType.OR,
				new DialogRequirement("... and I thought fishing was a safe way to pass the time."),
				new DialogRequirement("I see you need encouragement in learning the ways of fishing without a harpoon."),
				new WidgetTextRequirement(119, 3, true, "fish with my")
			)
		);

		taskedWithFarming = new RuneliteRequirement(
			configManager, ConfigKeys.BARBARIAN_TRAINING_STARTED_SEED_PLANTING.getKey(),
			new Conditions(true, LogicType.OR,
				new DialogRequirement("Remember to be calm, and good luck."),
				new DialogRequirement("I see you have yet to be successful in planting a seed with your fists."),
				new WidgetTextRequirement(119, 3, true, "plant a seed with")
			)
		);

		taskedWithPotSmashing = new RuneliteRequirement(
			configManager, ConfigKeys.BARBARIAN_TRAINING_STARTED_POT_SMASHING.getKey(),
			new Conditions(true, LogicType.OR,
				new DialogRequirement("May the spirits guide you into success."),
				new DialogRequirement("You have not yet attempted to plant a tree. Why not?"),
				new WidgetTextRequirement(119, 3, true, "Otto<col=000080> has tasked me with learning how to <col=800000>smash pots after")
			)
		);

		taskedWithBowFiremaking = new RuneliteRequirement(
			configManager, ConfigKeys.BARBARIAN_TRAINING_STARTED_FIREMAKING.getKey(),
			new Conditions(true, LogicType.OR,
				new DialogRequirement("The spirits will aid you. The power they supply will guide your hands. Go and benefit from their guidance upon oak logs."),
				new DialogRequirement("By now you know my response."),
				new WidgetTextRequirement(119, 3, true, "light a fire with")
			)
		);

		taskedWithPyre = new RuneliteRequirement(
			configManager, ConfigKeys.BARBARIAN_TRAINING_STARTED_PYREMAKING.getKey(),
			new Conditions(true, LogicType.OR,
				new DialogRequirement("Dive into the whirlpool in the lake to the east. The spirits will use their abilities to ensure you arrive in the correct location. Be warned, their influence fades, so you must find y"),
				new DialogRequirement("I will repeat myself fully, since this is quite complex. Listen well."),
				new WidgetTextRequirement(119, 3, true, "Otto<col=000080> has tasked me with learning how to <col=800000>create pyre ships")
			)
		);

		taskedWithHerblore = new RuneliteRequirement(
			configManager, ConfigKeys.BARBARIAN_TRAINING_STARTED_HERBLORE.getKey(),
			new Conditions(true, LogicType.OR,
				new DialogRequirement("Have I become so predictable? But yes, I do indeed require a potion. It is of the highest importance that you bring me a lesser attack potion combined with fish roe."),
				new DialogRequirement("Do you have my potion?"),
				new WidgetTextRequirement(119, 3, true, "Otto<col=000080> has tasked me with learning how to make a <col=800000>new type")
			)
		);

		taskedWithSpears = new RuneliteRequirement(
			configManager, ConfigKeys.BARBARIAN_TRAINING_STARTED_SPEAR.getKey(),
			new Conditions(true, LogicType.OR,
				new DialogRequirement("Note well that you will require wood for the spear shafts. The quality of wood must be similar to that of the metal involved."),
				new DialogRequirement("You do not exude the presence of one who has poured his soul into manufacturing spears."),
				new WidgetTextRequirement(119, 3, true, "Otto<col=000080> has tasked me with learning how to <col=800000>smith spears")
			)
		);

		taskedWithHastae = new RuneliteRequirement(
			configManager, ConfigKeys.BARBARIAN_TRAINING_STARTED_HASTA.getKey(),
			new Conditions(true, LogicType.OR,
				new DialogRequirement("Indeed. You may use our special anvil for this spear type too. The ways of black and dragon hastae are beyond our knowledge, however."),
				new DialogRequirement("Take some wood and metal and make a spear upon the<br>nearby anvil, then you may return to me. As an<br>example, you may use bronze bars with normal logs or<br>iron bars with oak logs."),
				new WidgetTextRequirement(119, 3, true, " has tasked me with learning how to <col=800000>smith a hasta")
			)
		);

		// Finished tasks
		finishedFishing = new RuneliteRequirement(
			configManager, ConfigKeys.BARBARIAN_TRAINING_FINISHED_FISHING.getKey(),
			new Conditions(true, LogicType.OR,
				new DialogRequirement("Patience young one. These are fish which are fat with eggs rather than fat of flesh. It is these eggs that are the thing to make use of."),
				new WidgetTextRequirement(119, 3, true, "I managed to catch a fish with the new rod!")
			),
			"Finished Barbarian Fishing"
		);

		finishedHarpoon = new RuneliteRequirement(
			configManager, ConfigKeys.BARBARIAN_TRAINING_FINISHED_HARPOON.getKey(),
			new Conditions(true, LogicType.OR,
				new DialogRequirement("I mean that when you eventually die and find peace, at least the spirits you encounter will be your friends. Alas for you adventurous sort, the natural ways of passing are close to imp"),
				new WidgetTextRequirement(119, 3, true, "I managed to fish with my hands!")
			),
			"Finished Barbarian Harpooning"
		);

		finishedSeedPlanting = new RuneliteRequirement(
			configManager, ConfigKeys.BARBARIAN_TRAINING_FINISHED_SEED_PLANTING.getKey(),
			new Conditions(true, LogicType.OR,
				new DialogRequirement("No child, but we all have potential to improve our strength."),
				new WidgetTextRequirement(119, 3, true, "<str>I managed to plant a seed with my fists!")
			),
			"Finished Barbarian Seed Planting"
		);

		finishedPotSmashing = new RuneliteRequirement(
			configManager, ConfigKeys.BARBARIAN_TRAINING_FINISHED_POT_SMASHING.getKey(),
			new Conditions(true, LogicType.OR,
				new DialogRequirement("It will become more natural with practice."),
				new WidgetTextRequirement(119, 3, true, "<str>I managed to smash a plant pot without littering!")
			),
			"Finished Barbarian Pot Smashing"
		);

		finishedFiremaking = new RuneliteRequirement(
			configManager, ConfigKeys.BARBARIAN_TRAINING_FINISHED_FIREMAKING.getKey(),
			new Conditions(true, LogicType.OR,
				new DialogRequirement("Fine news indeed!"),
				new WidgetTextRequirement(119, 3, true, "I managed to light a fire with a bow!")
			),
			"Finished Barbarian Firemaking"
		);

		finishedPyre = new RuneliteRequirement(
			configManager, ConfigKeys.BARBARIAN_TRAINING_FINISHED_PYREMAKING.getKey(),
			new Conditions(true, LogicType.OR,
				new DialogRequirement("On this great day you have my eternal thanks. May you find riches while rescuing my spiritual ancestors in the caverns for many moons to come."),
				new WidgetTextRequirement(119, 3, true, "I managed to create a pyre ship!")
			),
			"Finished Barbarian Pyremaking"
		);

		finishedSpear = new RuneliteRequirement(
			configManager, ConfigKeys.BARBARIAN_TRAINING_FINISHED_SPEAR.getKey(),
			new Conditions(true, LogicType.OR,
				new DialogRequirement("The manufacture of spears is now yours as a speciality. Use your skill well."),
				new WidgetTextRequirement(119, 3, true, "I managed to smith a spear!")
			),
			"Finished Barbarian Spear Smithing"
		);

		finishedHasta = new RuneliteRequirement(
			configManager, ConfigKeys.BARBARIAN_TRAINING_FINISHED_HASTA.getKey(),
			new Conditions(true, LogicType.OR,
				new DialogRequirement("To live life to it's fullest of course - that you may be a peaceful spirit when your time ends."),
				new WidgetTextRequirement(119, 3, true, "I managed to create a hasta!")
			),
			"Finished Barbarian Hasta Smithing"
		);

		finishedHerblore = new RuneliteRequirement(
			configManager, ConfigKeys.BARBARIAN_TRAINING_FINISHED_HERBLORE.getKey(),
			new Conditions(true, LogicType.OR,
				new DialogRequirement("I will take that off your hands now. I will say no more than that I am eternally grateful."),
				new WidgetTextRequirement(119, 3, true, "I managed to create a new potion!")
			),
			"Finished Barbarian Herblore"
		);

		reqs = new RequirementValidator(client, eventBus, (or(
			taskedWithFishing, taskedWithHarpooning, taskedWithFarming, taskedWithBowFiremaking, taskedWithPyre, taskedWithPotSmashing,
			taskedWithSpears, taskedWithHastae, taskedWithHerblore, plantedSeed, smashedPot, litFireWithBow, sacrificedRemains, caughtBarbarianFish,
			caughtFishWithoutHarpoon, madePotion, madeSpear, madeHasta, finishedFishing, finishedHarpoon, finishedSeedPlanting, finishedPotSmashing,
			finishedFiremaking, finishedPyre, finishedSpear, finishedHasta, finishedHerblore
		)));

		eventBus.register(reqs);
		reqs.startUp();
	}


	public void shutDown(EventBus eventBus)
	{
		eventBus.unregister(reqs);
	}
}
