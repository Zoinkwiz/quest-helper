package com.questhelper.helpers.quests.cooksassistant;

import com.questhelper.QuestHelperConfig;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questinfo.HelperConfig;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.widget.NormalSpells;
import com.questhelper.steps.widget.WidgetHighlight;
import net.runelite.api.WorldType;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static com.questhelper.helpers.quests.cooksassistant.CooksAssistant.QUEST_TYPES;
import static com.questhelper.requirements.util.LogicHelper.and;

public class CooksAssistantSpeedrun extends BasicQuestHelper
{
	CooksAssistant ca;

	// Required items
	ItemRequirement compost;

	// Zones
	Zone nearCastle;

	// Requirements
	ZoneRequirement isNearCastle;

	// Steps
	NpcStep startQuest;
	DetailedQuestStep pickUpPot;
	DetailedQuestStep emptyCompost;
	NpcStep buyCompost;
	DetailedQuestStep teleportToTheCastle;

	@Override
	protected void setupRequirements()
	{
		compost = new ItemRequirement("Compost", ItemID.BUCKET_COMPOST);

		nearCastle = new Zone(new WorldPoint(3197, 3200, 0), new WorldPoint(3234, 3238, 0));
		isNearCastle = new ZoneRequirement(nearCastle);
	}

	public void setupSteps()
	{
		startQuest = new NpcStep(this, NpcID.COOK, new WorldPoint(3206, 3214, 0),
			"Talk to the Cook in Lumbridge Castle's kitchen to start the quest.");
		startQuest.addDialogSteps("What's wrong?", "Can I help?", "Yes.");

		pickUpPot = new ItemStep(this, new WorldPoint(3209, 3214, 0), "Pick up the pot on the table next to the cook.", ca.pot);

		buyCompost = new NpcStep(this, NpcID.FARMING_GARDENER_TREE_4, new WorldPoint(3192, 3233, 0), "Buy a bucket of compost from Fayeth at the tree patch west of the castle.");
		buyCompost.addDialogSteps("Can you sell me something?");
		buyCompost.addWidgetHighlight(WidgetHighlight.createShopItemHighlight(ItemID.BUCKET_COMPOST));

		emptyCompost = new ItemStep(this, "Right-click empty the compost", compost.highlighted());

		teleportToTheCastle = new DetailedQuestStep(this, "Use the home teleport spell to return to the castle.");
		teleportToTheCastle.addSpellHighlight(NormalSpells.LUMBRIDGE_HOME_TELEPORT);
	}

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		ca = new CooksAssistant();
		ca.baseHelper = this;
		ca.loadSteps();

		initializeRequirements();
		setupSteps();

		var steps = new HashMap<Integer, QuestStep>();
		steps.put(0, startQuest);
		ConditionalStep doQuest = new ConditionalStep(this, pickUpPot);
		doQuest.addStep(and(ca.hasEgg, ca.hasFlour, ca.hasMilk, isNearCastle), ca.finishQuest);
		doQuest.addStep(and(ca.hasEgg, ca.hasFlour, ca.hasMilk), teleportToTheCastle);
		doQuest.addStep(and(ca.bucket, ca.hasEgg, ca.hasFlour), ca.milkCow);
		doQuest.addStep(and(ca.pot, ca.bucket, ca.hasEgg), ca.getFlour);
		doQuest.addStep(and(ca.pot, ca.bucket), ca.getEgg);
		doQuest.addStep(and(ca.pot, compost), emptyCompost);
		doQuest.addStep(ca.pot, buyCompost);

		steps.put(1, doQuest);

		return steps;
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (!event.getGroup().equals(QuestHelperConfig.QUEST_BACKGROUND_GROUP))
		{
			return;
		}

		if (event.getKey().equals(QUEST_TYPES))
		{
			if (CooksAssistant.HelperTypes.NORMAL.name().equals(event.getNewValue()))
			{
				questHelperPlugin.getQuestManager().startUpQuest(QuestHelperQuest.COOKS_ASSISTANT.getQuestHelper(), true);
			}
		}
	}

	@Override
	public List<HelperConfig> getConfigs()
	{
		var worldTypes = client.getWorldType();
		if (worldTypes == null || !worldTypes.contains(WorldType.QUEST_SPEEDRUNNING))
		{
			return null;
		}

		HelperConfig helperTypeConfig = new HelperConfig("Helper Type", QUEST_TYPES, CooksAssistant.HelperTypes.values());
		return List.of(helperTypeConfig);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var steps = new ArrayList<PanelDetails>();

		steps.add(new PanelDetails("Starting off", List.of(
			startQuest,
			pickUpPot,
			buyCompost,
			emptyCompost
		)));

		steps.add(new PanelDetails("Getting the Egg", List.of(
			ca.getEgg
		)));

		steps.add(new PanelDetails("Getting the Flour", List.of(
			ca.getWheat,
			ca.climbLadderOne,
			ca.fillHopper,
			ca.operateControls,
			ca.climbLadderThree,
			ca.collectFlour
		)));

		steps.add(new PanelDetails("Getting the Milk", List.of(
			ca.milkCow
		)));

		steps.add(new PanelDetails("Finishing up", List.of(
			teleportToTheCastle,
			ca.finishQuest
		)));

		return steps;
	}
}
