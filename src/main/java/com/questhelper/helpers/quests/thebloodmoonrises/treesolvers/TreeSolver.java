// SPDX-FileCopyrightText: 2026 pajlada <rasmus.karlsson@pajlada.com>
//
// SPDX-License-Identifier: BSD-2-Clause
package com.questhelper.helpers.quests.thebloodmoonrises.treesolvers;

import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.conditional.ObjectCondition;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedOwnerStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.coords.Direction;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.gameval.SpriteID;
import net.runelite.client.eventbus.Subscribe;

@Slf4j
public abstract class TreeSolver extends DetailedOwnerStep
{
	@Inject
	protected Client client;

	private List<ObjectStep> solverSteps;
	private List<Requirement> conditions;
	private List<Requirement> inverseConditions;
	private ConditionalStep conditionalStep;

	@SuppressWarnings({"unused", "FieldCanBeLocal"})
	private int stepCounter;

	public TreeSolver(QuestHelper theCurseOfArrav, @SuppressWarnings("unused") String number)
	{
		super(theCurseOfArrav, "");
	}

	public static ObjectStep createStep(QuestHelper questHelper, int x, int y, TreeType treeType, Direction direction)
	{
		var validObjectIDs = treeType.getObjectIDs();
		assert !validObjectIDs.isEmpty();

		var wp = new WorldPoint(x, y, 0);
		// Useful for debugging
		// var stepCounter = this.stepCounter++;
		var text = String.format("Chop the tree from the %s side.", direction.toString().toLowerCase());
		// var spriteID = SpriteID.Staticons.WOODCUTTING;
		var spriteID = SpriteID.Combaticons.AXE_CHOP;
		if (treeType == TreeType.Stump)
		{
			text = String.format("Climb over the tree from the %s side.", direction.toString().toLowerCase());
			spriteID = SpriteID.Staticons.AGILITY;
		}
		var mainObjectID = validObjectIDs.get(0);
		var step = new ObjectStep(questHelper, mainObjectID, wp, text);
		var offsetX = x;
		var offsetY = y;
		switch (direction)
		{
			case NORTH:
				offsetY += 1;
				break;
			case SOUTH:
				offsetY -= 1;
				break;
			case WEST:
				offsetX -= 1;
				break;
			case EAST:
				offsetX += 1;
				break;
		}
		var posWp = new WorldPoint(offsetX, offsetY, 0);
		step.addTileMarker(posWp, spriteID);
		for (var alternateIDs : validObjectIDs)
		{
			// todo this adds the first object again xd
			step.addAlternateObjects(alternateIDs);
		}
		return step;
	}

	protected void addTreeStep(int x, int y, TreeType treeType, Direction direction)
	{
		var validObjectIDs = treeType.getObjectIDs();
		assert !validObjectIDs.isEmpty();

		var validIDSet = new HashSet<>(validObjectIDs);
		var step = TreeSolver.createStep(getQuestHelper(), x, y, treeType, direction);
		var wp = new WorldPoint(x, y, 0);

		var conditionText = String.format("Tree chopped from the %s side", direction.toString().toLowerCase());
		var inverseConditionText = String.format("Tree needs to be chopped from the %s side", direction.toString().toLowerCase());
		var conditionThatObjectIsStillThere = new ObjectCondition(validIDSet, wp);
		var conditionThatObjectIsGone = new Conditions(true, LogicType.NAND, conditionThatObjectIsStillThere);
		conditionThatObjectIsStillThere.setText(inverseConditionText);
		conditionThatObjectIsGone.setText(conditionText);

		this.solverSteps.add(step);
		this.conditions.add(conditionThatObjectIsGone);
		this.inverseConditions.add(conditionThatObjectIsStillThere);
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		updateSteps();
	}

	@Override
	public void startUp()
	{
		updateSteps();
	}

	protected abstract void setupRubbleSteps();

	@Override
	protected void setupSteps()
	{
		this.stepCounter = 1;
		this.solverSteps = new ArrayList<>();
		this.conditions = new ArrayList<>();
		this.inverseConditions = new ArrayList<>();

		var todo = new DetailedQuestStep(getQuestHelper(), "todo");

		this.setupRubbleSteps();

		conditionalStep = new ConditionalStep(getQuestHelper(), todo);

		assert this.solverSteps.size() == this.conditions.size();
		assert this.solverSteps.size() == this.inverseConditions.size();

		for (var i = 0; i < solverSteps.size(); i++)
		{
			var step = solverSteps.get(i);
			var inverseCondition = this.inverseConditions.get(i);

			// Useful for debugging
			// step.addRequirement(inverseCondition);

			conditionalStep.addStep(inverseCondition, step);
		}
	}

	protected void updateSteps()
	{
		startUpStep(this.conditionalStep);
	}

	@Override
	public List<QuestStep> getSteps()
	{
		var steps = new ArrayList<QuestStep>();

		steps.add(this.conditionalStep);
		steps.addAll(this.solverSteps);

		return steps;
	}

}
