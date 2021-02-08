/*
 * Copyright (c) 2020, Patyfatycake <https://github.com/Patyfatycake/>
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
 * ON ANY THEORY OF LIABI`LITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.questhelper.quests.recruitmentdrive;

import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.steps.DetailedOwnerStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.tools.QuestPerspective;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.GameObject;
import net.runelite.api.Model;
import net.runelite.api.Tile;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import static net.runelite.api.widgets.WidgetID.DIALOG_NPC_GROUP_ID;
import net.runelite.client.eventbus.Subscribe;

public class LadyTableStep extends DetailedOwnerStep
{

	private final int VARBIT_FINISHED_ROOM = 660;

	private Statue missingStatue = null;
	private WaitForStatueStep waitForStatueStep;
	private ObjectStep clickMissingStatue, leaveRoom;

	VarbitRequirement finishedRoom = new VarbitRequirement(VARBIT_FINISHED_ROOM, 1);

	public LadyTableStep(QuestHelper questHelper, Requirement... requirements)
	{
		super(questHelper, requirements);
		waitForStatueStep = new WaitForStatueStep(questHelper, "Wait to select the missing statue.");
		leaveRoom = new ObjectStep(questHelper, 7302, "Leaves through the door to enter the portal and continue.");
	}

	@Override
	public Collection<QuestStep> getSteps()
	{
		List<QuestStep> step = new ArrayList<>();

		step.add(waitForStatueStep);
		if (missingStatue != null)
		{
			step.add(clickMissingStatue);
		}
		step.add(leaveRoom);
		return step;
	}

	@Override
	protected void updateSteps()
	{
		if (finishedRoom.check(client))
		{
			startUpStep(leaveRoom);
		}
		else if (missingStatue != null)
		{
			startUpStep(clickMissingStatue);
		}
		else
		{
			startUpStep(waitForStatueStep);
		}
	}

	public List<QuestStep> getPanelSteps()
	{
		List<QuestStep> steps = new ArrayList<>();

		steps.add(waitForStatueStep);
		if (missingStatue != null)
		{
			steps.add(clickMissingStatue);
		}
		steps.add(leaveRoom);
		return steps;
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		updateSteps();
	}

	static class Statue
	{
		private final String weapon;
		private final String color;
		private final WorldPoint point;
		private final int gameId;

		public Statue(String weapon, String color, WorldPoint point, int gameId)
		{
			this.weapon = weapon;
			this.color = color;
			this.point = point;
			this.gameId = gameId;
		}
	}

	class WaitForStatueStep extends DetailedQuestStep
	{
		private final String TRACKING_TEXT = "You have 10 seconds to memorise the statues... starting<br>NOW!";

		private List<Model> statueModels = new ArrayList<>();

		//	Highest values go first(Descending order)
		private String[] triangleCountOrder = {"Halberd", "Axe", "Mace", "Sword"};
		private String[] faceColorOrder = {"Gold", "Bronze", "Silver"};

		private String[] colorOrder = {"Gold", "Silver", "Bronze"};
		private String[] weapons = new String[]{"Sword", "Halberd", "Axe", "Mace"};

		private Statue[] statues;

		public WaitForStatueStep(QuestHelper questHelper, String text, Requirement... requirements)
		{
			super(questHelper, text, requirements);
			init();
		}

		/**
		 * Setup basic information required to run.
		 */
		private void init()
		{
			int baseX = 2450;
			int baseY = 4982;
			int baseId = 7290;

			statues = new Statue[weapons.length * colorOrder.length];
			for (int w = 0; w < weapons.length; w++)
			{
				String weapon = weapons[w];
				for (int c = 0; c < colorOrder.length; c++)
				{
					String color = colorOrder[c];
					int yPosition = baseY - (c * 3);
					int xPosition = baseX + (w * 2);

					int bonusId = (w * 3) + c;
					int gameId = baseId + bonusId;

					statues[bonusId] = new Statue(weapon, color, new WorldPoint(xPosition, yPosition, 0), gameId);
				}
			}
		}

		/**
		 * Process the models of the statues we have and gather the relevant data used to determine the missing statue
		 */
		private void processModels()
		{
			HashMap<Integer, Integer> weaponMap = new HashMap<>();
			List<Integer> colorsList = new ArrayList<>();
			int numberOfStatues = 0;

			for (int i = 0; i < statueModels.size(); i++)
			{
				Model model = statueModels.get(i);

				// No model generally emans the missing one.
				if (model == null)
				{
					continue;
				}

				// Triangles are used to calculate weapon, face color for statue color.
				int triangleCount = model.getTrianglesCount();
				int[] faceColors = model.getFaceColors1();

				// Invalid data retrieved try again
				if (faceColors.length == 0)
				{
					statueModels.clear();
					return;
				}

				int firstFaceColor = faceColors[0];
				int triangleAmountCount = weaponMap.getOrDefault(triangleCount, 0);
				colorsList.add(firstFaceColor);
				weaponMap.put(triangleCount, triangleAmountCount + 1);
				numberOfStatues = numberOfStatues + 1;
			}

			// Sort them so we add highest first.
			colorsList.sort(Collections.reverseOrder());

			HashMap<Integer, Integer> colorsMap = new HashMap<>();

			for (int i = 1; i < colorsList.size(); i = i + 4)
			{
				Integer color = colorsList.get(i);
				colorsMap.put(color, 0);
			}

			Integer[] colorHeaders = colorsMap.keySet().toArray(new Integer[colorsMap.keySet().size()]);
			for (Integer color : colorsList)
			{
				for (Integer colorHeader : colorHeaders)
				{
					// Give some leeway for the header colors, generally they are 1000~ difference between gold silver bronze etc.
					if (color >= colorHeader - 500 && color <= colorHeader + 500)
					{
						Integer count = colorsMap.get(colorHeader);
						colorsMap.put(colorHeader, count + 1);
						break;
					}
				}
			}

			if (numberOfStatues == 12)
			{
				// Too late because they started after the 16 knights have spawned
				statueModels.clear();
				return;
			}

			getMissingStatue(colorsMap, weaponMap);
		}

		/***
		 * 	Gets the missing statue based on the information retrieved from the colors and triangleCount retrieved.
		 * @param colorsMap    List of firstFaceColor
		 * @param weaponMap    List of triangleCount
		 */
		private void getMissingStatue(HashMap<Integer, Integer> colorsMap, HashMap<Integer, Integer> weaponMap)
		{
			List<Integer> weaponList = new ArrayList<>(weaponMap.keySet());
			List<Integer> colorsList = new ArrayList<>(colorsMap.keySet());

			// Minimum key for each value will correlate to the missing statue
			Integer lowestWeapon = getMinKey(weaponMap, weaponMap.keySet().toArray(new Integer[0]));
			Integer lowestColor = getMinKey(colorsMap, colorsMap.keySet().toArray(new Integer[0]));

			// Sort them from highest to lowest to check against the expected highest values
			weaponList.sort(Collections.reverseOrder());
			colorsList.sort(Collections.reverseOrder());
			int weaponPosition = weaponList.indexOf(lowestWeapon);
			int colorsPosition = colorsList.indexOf(lowestColor);

			// Compare the missing value against the expected order.
			String missingWeapon = triangleCountOrder[weaponPosition];
			String missingColor = faceColorOrder[colorsPosition];

			for (Statue statue : statues)
			{
				if (statue.weapon.equals(missingWeapon) && statue.color.equals(missingColor))
				{
					missingStatue = statue;
					worldPoint = statue.point;
					UpdateMissingText();
					break;
				}
			}
		}

		/**
		 * Updates missing text to make it clearer.
		 */
		private void UpdateMissingText()
		{
			String missingStatueText = "Click on the " + missingStatue.color + " " + missingStatue.weapon + " statue. ";
			clickMissingStatue = new ObjectStep(questHelper, missingStatue.gameId, missingStatue.point, missingStatueText);
			waitForStatueStep.setText("Wait to select the missing statue " + missingStatue.color + " " + missingStatue.weapon);
			waitForStatueStep.addSubSteps(clickMissingStatue);
			questHelper.instantiateStep(clickMissingStatue);
		}

		/***
		 * 	Gets the minimum value from a hashmap
		 * @param map    hash map you wish to search in
		 * @param keys    A set of keys you can search
		 * @return The integer value from the key which correclates to the lowest value.
		 */
		private Integer getMinKey(Map<Integer, Integer> map, Integer... keys)
		{
			Integer minKey = null;
			int minValue = Integer.MAX_VALUE;
			for (Integer key : keys)
			{
				int value = map.get(key);
				if (value < minValue)
				{
					minValue = value;
					minKey = key;
				}
			}
			return minKey;
		}

		/**
		 * Gets the statue at a certain point to get its rendering details to process.
		 *
		 * @param wp The point of the statue you are looking for
		 */
		public void checkForStatues(WorldPoint wp)
		{
			Collection<WorldPoint> localWorldPoints = QuestPerspective.toLocalInstance(client, wp);

			for (WorldPoint point : localWorldPoints)
			{
				LocalPoint localPoint = LocalPoint.fromWorld(client, point);
				if (localPoint == null)
				{
					continue;
				}
				Tile[][][] tiles = client.getScene().getTiles();
				if (tiles == null)
				{
					continue;
				}

				Tile tile = tiles[client.getPlane()][localPoint.getSceneX()][localPoint.getSceneY()];
				if (tile != null)
				{
					for (GameObject object : tile.getGameObjects())
					{
						if (object == null || object.getRenderable() == null)
						{
							continue;
						}
						// Get the models for all the statues. May return null if there is none.
						Model model = object.getRenderable().getModel();
						statueModels.add(model);
						break;
					}
				}
			}
		}

		@Override
		/**
		 * {@inheritDoc}
		 */
		public void onWidgetLoaded(WidgetLoaded event)
		{
			int groupId = event.getGroupId();
			if (groupId == DIALOG_NPC_GROUP_ID)
			{
				clientThread.invokeLater(this::readWidget);
			}

			super.onWidgetLoaded(event);
		}

		/***
		 * 	 Reads the dialog looking for a certain text to scan for the statues which have changed.
		 */
		private void readWidget()
		{
			Widget widget = client.getWidget(WidgetID.DIALOG_NPC_GROUP_ID, 4);
			if (widget == null)
			{
				return;
			}

			String characterText = widget.getText();

			if (TRACKING_TEXT.equals(characterText))
			{
				retrieveStatues();
			}
		}

		/**
		 * Retrieves information about all the statues and then processes the data.
		 */
		private void retrieveStatues()
		{
			for (Statue statue : statues)
			{
				checkForStatues(statue.point);
			}
			processModels();
		}

	}

}
