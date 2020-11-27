package com.questhelper.quests.recruitmentdrive;

import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.steps.ObjectStep;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
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

public class LadyTableStep extends ObjectStep
{

	private boolean startTracking = false;

	private final String TRACKING_TEXT = "You have 10 seconds to memorise the statues... starting<br>NOW!";

	private List<Model> statueModels = new ArrayList<>();

	private String[] triangleCountOrder = {"Halberd", "Axe", "Mace", "Sword"};

	private String[] colorOrder = {"Gold", "Silver", "Bronze"};
	private String[] weapons = new String[]{"Sword", "Halberd", "Axe", "Mace"};

	private Statue[] statues;

	private Statue missingStatue = null;

	public LadyTableStep(QuestHelper questHelper, int objectID, String text, Requirement... requirements)
	{
		super(questHelper, objectID, text, requirements);
		init();
	}

	private void init()
	{
		// Sword Halbered Axe Mace
		// Gold
		// Silver
		// Bronze

		int baseX = 2450;
		int baseY = 4982;
		int baseId = 7290;

		statues = new Statue[weapons.length*colorOrder.length];
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

	@Override
	public void onGameTick(final GameTick event)
	{
		super.onGameTick(event);

		if (!startTracking || statueModels.size() > 0)
		{
			return;
		}

		statueModels.clear();
		for (Statue statue : statues)
		{
			checkTileForObject1(statue.point);
		}
		procesModels();
	}

	private void procesModels()
	{
		HashMap<Integer, Integer> weaponMap = new HashMap<>();
		List<Integer> colorsList = new ArrayList<>();
		Integer numberOfStatues = 0;

		for (int i = 0; i < statueModels.size(); i++)
		{
			Model model = statueModels.get(i);

			if (model == null)
			{
				continue;
			}
			int triangleCount = model.getTrianglesCount();
			int[] faceColors = model.getFaceColors1();

			// Invalid data retrieved try again
			if (faceColors.length == 0)
			{
				statueModels.clear();
				return;
			}

			int firstFaceColor = faceColors[0];
			int triangleAmountCount = weaponMap.containsKey(triangleCount) ? weaponMap.get(triangleCount) : 0;
			colorsList.add(firstFaceColor);
			weaponMap.put(triangleCount, triangleAmountCount + 1);
			numberOfStatues = numberOfStatues + 1;
		}

		Collections.sort(colorsList);


		HashMap<Integer, Integer> colorsMap = new HashMap<>();

		for (int i = 1; i < colorsList.size(); i = i + 4)
		{
			Integer color = colorsList.get(i);
			colorsMap.put(color, 0);
		}

		Integer[] colorHeaders = colorsMap.keySet().toArray(new Integer[colorsMap.keySet().size()]);
		for (Integer color : colorsList)
		{
			// There should be 11
			for (int i = 0; i < colorHeaders.length; i++)
			{
				Integer colorHeader = colorHeaders[i];
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

		getStatue(colorsMap, weaponMap);
	}

	private void getStatue(HashMap<Integer, Integer> colorsMap, HashMap<Integer, Integer> weaponMap)
	{
		List<Integer> weaponList = new ArrayList(weaponMap.keySet());
		List<Integer> colorsList = new ArrayList(colorsMap.keySet());

		Integer lowestWeapon = getMinKey(weaponMap, weaponMap.keySet().toArray(new Integer[weaponMap.keySet().size()]));
		Integer lowestColor = getMinKey(colorsMap, colorsMap.keySet().toArray(new Integer[colorsMap.keySet().size()]));

		int weaponPosition = weaponList.indexOf(lowestWeapon);
		int colorsPosition = colorsList.indexOf(lowestColor);

		String missingWeapon = weapons[weaponPosition];
		String missingColor = colorOrder[colorsPosition];

		for (Statue statue : statues)
		{
			if (statue.weapon.equals(missingWeapon) && statue.color.equals(missingColor))
			{
				missingStatue = statue;
				worldPoint = statue.point;
				addAlternateObjects(statue.gameId);
				break;
			}
		}
	}

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

	public void checkTileForObject1(WorldPoint wp)
	{
		Collection<WorldPoint> localWorldPoints = toLocalInstance(client, wp);

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
			clientThread.invokeLater(() -> readWidget());
		}

		super.onWidgetLoaded(event);
	}

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
			startTracking = true;
		}
	}

	class Statue
	{
		private String weapon;
		private String color;
		private WorldPoint point;
		private int gameId;

		public Statue(String weapon, String color, WorldPoint point, int gameId)
		{
			this.weapon = weapon;
			this.color = color;
			this.point = point;
			this.gameId = gameId;
		}
	}

}
