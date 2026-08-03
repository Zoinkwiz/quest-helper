/*
 * Copyright (c) 2026, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.helpers.quests.thebloodmoonrises;

import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.steps.DetailedOwnerStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;

class CastleDrakanRoomStep extends DetailedOwnerStep
{
	private final CastleDrakanRoomNetwork network;
	private final CastleDrakanRoomNetwork.RoomKey destination;
	private final List<String> destinationText;
	private final QuestStep arrivedStep;
	private final QuestStep noRouteStep;
	private final QuestStep unknownRoomStep;

	CastleDrakanRoomStep(QuestHelper questHelper, CastleDrakanRoomNetwork network,
		CastleDrakanRoomNetwork.RoomKey destination, String sidebarText)
	{
		super(questHelper, sidebarText);
		this.network = network;
		this.destination = destination;
		this.destinationText = List.of(sidebarText);
		this.arrivedStep = new DetailedQuestStep(questHelper, sidebarText);
		this.noRouteStep = new DetailedQuestStep(questHelper,
			"Quest Helper can't find a route from this room right now. Head back towards the castle lobby.");
		this.unknownRoomStep = new DetailedQuestStep(questHelper,
			"Quest Helper has lost track of which Castle Drakan room you're in. Head back towards the castle lobby.");
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		updateSteps();
	}

	@Override
	protected void updateSteps()
	{
		var currentRoom = network.currentRoom(client);
		if (currentRoom.isEmpty())
		{
			startUpStep(unknownRoomStep);
			return;
		}

		var destinationRoom = network.getRoom(destination);
		if (currentRoom.get() == destinationRoom)
		{
			startUpStep(arrivedStep);
			return;
		}

		startUpStep(network.nextEdge(client, currentRoom.get(), destinationRoom)
			.map(CastleDrakanRoomNetwork.Edge::getStep)
			.orElse(noRouteStep));
	}

	@Override
	public Collection<QuestStep> getSteps()
	{
		if (network == null)
		{
			return List.of();
		}

		var ownedSteps = new ArrayList<>(network.getDoorSteps());
		ownedSteps.add(arrivedStep);
		ownedSteps.add(noRouteStep);
		ownedSteps.add(unknownRoomStep);
		return Collections.unmodifiableList(ownedSteps);
	}

	@Override
	public boolean containsSteps(QuestStep questStep, Set<QuestStep> checkedSteps)
	{
		if (super.containsSteps(questStep, checkedSteps)) return true;

		var stepSet = new HashSet<>(getSteps());
		stepSet.removeAll(checkedSteps);
		for (QuestStep child : stepSet)
		{
			if (child.containsSteps(questStep, checkedSteps)) return true;
		}

		return false;
	}

	@Override
	public List<String> getText()
	{
		if (currentStep == null)
		{
			return destinationText;
		}

		var text = new ArrayList<>(destinationText);
		var currentText = currentStep.getText();
		if (currentText == null)
		{
			return text;
		}
		for (String line : currentText)
		{
			if (!destinationText.contains(line))
			{
				text.add(line);
			}
		}
		return text;
	}
}
