package com.questhelper.quests.thequeenofthieves;

import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.conditional.Conditions;
import com.questhelper.steps.conditional.ZoneCondition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.World;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.THE_QUEEN_OF_THIEVES
)
public class TheQueenOfThieves extends BasicQuestHelper
{
	ItemRequirement stew;

	QuestStep talkToLawry, talkToPoorLookingPerson, talkToOReilly, talkToDevan, exitWarrens, killConrad,
	tellDevanAboutConrad, exitWarrens2, goToKingstown, openChest, leaveKingtstown, talkToLawry2,
	talkToShauna;

	ObjectStep enterWarrens, enterWarrens2, enterWarrens3;
	NpcStep talkToQueenOfThieves;

	ZoneCondition inWarrens, inKingstown, inUpstairsHughesHouse;
	Zone warrens, kingstown, upstairsHughesHouse;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupItemRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		ConditionalStep searchHughesChest = new ConditionalStep(this, goToKingstown);
		searchHughesChest.addStep(inWarrens, exitWarrens);
		searchHughesChest.addStep(inUpstairsHughesHouse, openChest);

		ConditionalStep returnToLawry = new ConditionalStep(this, talkToLawry);


		steps.put(0, talkToLawry);
		steps.put(1, talkToPoorLookingPerson);
		steps.put(2, talkToOReilly);
		// Switches to 3 after mention of stew, but before giving it to him.
		steps.put(3, talkToOReilly);

		ConditionalStep findDevan = new ConditionalStep(this, enterWarrens);
		findDevan.addStep(inWarrens, talkToDevan);

		steps.put(4, findDevan);
		// Switches to 5 when you enter the manhole.
		steps.put(5, findDevan);

		ConditionalStep killConradConditional = new ConditionalStep(this, killConrad);
		killConradConditional.addStep(inWarrens, exitWarrens);

		steps.put(6, killConradConditional);

		ConditionalStep tellDevanAboutConradConditional = new ConditionalStep(this, enterWarrens2);
		tellDevanAboutConradConditional.addStep(inWarrens, tellDevanAboutConrad);
		steps.put(7, tellDevanAboutConradConditional);

		steps.put(8, talkToQueenOfThieves);
		steps.put(9, talkToQueenOfThieves);

		ConditionalStep exposeHughes = new ConditionalStep(this, goToKingstown);
		exposeHughes.addStep(inWarrens, exitWarrens2);
		exposeHughes.addStep(inUpstairsHughesHouse, openChest);

		steps.put(10, exposeHughes);

		ConditionalStep talkToLawry2Conditional = new ConditionalStep(this, talkToLawry2);
		talkToLawry2Conditional.addStep(inUpstairsHughesHouse, leaveKingtstown);
		steps.put(11, talkToLawry2Conditional);

		ConditionalStep talkToShaunaConditional = new ConditionalStep(this, enterWarrens3);
		talkToShaunaConditional.addStep(inWarrens, talkToShauna);

		steps.put(12, talkToShaunaConditional);

		return steps;
	}

	public void setupItemRequirements() {
		stew = new ItemRequirement("Stew", ItemID.STEW);
	}

	public void loadZones() {
		warrens = new Zone(new WorldPoint(1728, 10115, 0), new WorldPoint(1814, 10177, 0));
		kingstown = new Zone(new WorldPoint(1668,  3675, 1), new WorldPoint(1685, 3684, 1));
		upstairsHughesHouse = new Zone(new WorldPoint(1668,  3675, 1), new WorldPoint(1685, 3684, 1));
	}

	public void setupConditions()
	{
		inWarrens = new ZoneCondition(warrens);
		inKingstown = new ZoneCondition(kingstown);
		inUpstairsHughesHouse = new ZoneCondition(upstairsHughesHouse);
	}

	public void setupSteps() {
		WorldPoint manholePoint = new WorldPoint(1813, 3745, 0);
		WorldPoint ladderPoint = new WorldPoint(1813, 10145, 0);
		WorldPoint devanPoint = new WorldPoint(1767, 10146, 0);
		WorldPoint queenOfThievesPoint = new WorldPoint(1764, 10158, 0);

		talkToLawry = new NpcStep(this, NpcID.TOMAS_LAWRY, new WorldPoint(1796, 3781, 0), "Speak to Tomas Lawry in Port Piscarilius.");
		talkToLawry.addDialogStep("I'm looking for a quest.");
		talkToLawry.addDialogStep("Absolutely.");

		talkToPoorLookingPerson = new NpcStep(this, NpcID.POOR_LOOKING_WOMAN_7923, new WorldPoint(1803, 3738, 0), "Speak to the poor looking woman.");
		talkToOReilly = new NpcStep(this, NpcID.ROBERT_OREILLY, new WorldPoint(1794, 3757, 0), "Speak to Robert O'Reilly, and give him the bowl of stew.");
		talkToOReilly.addDialogStep("Okay.");

		enterWarrens = new ObjectStep(this, ObjectID.MANHOLE_31706, manholePoint, "Enter the Warrens.");
		enterWarrens.addAlternateObjects(ObjectID.MANHOLE_31707);

		talkToDevan = new NpcStep(this, NpcID.DEVAN_RUTTER, devanPoint, "Speak to Devan Rutter.");

		exitWarrens = new ObjectStep(this, ObjectID.LADDER_31708, ladderPoint, "Exit the Warrens.");

		killConrad = new NpcStep(this, NpcID.CONRAD_KING, new WorldPoint(1847, 3734, 0), "Murder Conrad King.");

		// Enter the Warrens again.
		enterWarrens2 = new ObjectStep(this, ObjectID.MANHOLE_31706, manholePoint, "Enter the Warrens.");
		enterWarrens2.addAlternateObjects(ObjectID.MANHOLE_31707);

		tellDevanAboutConrad = new NpcStep(this, NpcID.DEVAN_RUTTER, devanPoint, "Tell Devan Rutter about the murder.");

		talkToQueenOfThieves = new NpcStep(this, NpcID.THE_QUEEN_OF_THIEVES, queenOfThievesPoint, "Talk to the Queen of Thieves.");
		talkToQueenOfThieves.addAlternateNpcs(NpcID.LADY_SHAUNA_PISCARILIUS);

		// Exit the Warrens again.
		exitWarrens2 = new ObjectStep(this, ObjectID.LADDER_31708, ladderPoint, "Exit the Warrens.");

		goToKingstown = new ObjectStep(this, ObjectID.STAIRCASE_11796, new WorldPoint(1672, 3681, 0), "Go up the stairs in Councillor Hughes' home in Kingstown.");
		openChest = new ObjectStep(this, ObjectID.CHEST_31710, new WorldPoint(1681, 3677, 1), "Pick the locked chest.");
		leaveKingtstown = new ObjectStep(this, ObjectID.STAIRCASE_11799, new WorldPoint(1672, 3681, 1), "Go downstairs.");

		// Talk to Lawry again
		talkToLawry2 = new NpcStep(this, NpcID.TOMAS_LAWRY, new WorldPoint(1796, 3781, 0), "Speak to Tomas Lawry in Port Piscarilius.");

		// Enter the Warrens again
		enterWarrens3 = new ObjectStep(this, ObjectID.MANHOLE_31706, manholePoint, "Enter the Warrens.");
		enterWarrens3.addAlternateObjects(ObjectID.MANHOLE_31707);

		talkToShauna = new NpcStep(this, NpcID.LADY_SHAUNA_PISCARILIUS, queenOfThievesPoint, "Talk to Lady Shauna Piscarilius.");
	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		return new ArrayList<>(Arrays.asList(stew));
	}

	@Override
	public ArrayList<ItemRequirement> getItemRecommended()
	{
		return new ArrayList<>(Arrays.asList());
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Finding the Queen of Thieves", new ArrayList<>(Arrays.asList(talkToLawry, talkToPoorLookingPerson, talkToOReilly)), stew));
		allSteps.add(new PanelDetails("Gaining Trust", new ArrayList<>(Arrays.asList(enterWarrens, talkToDevan, exitWarrens, killConrad, enterWarrens2, tellDevanAboutConrad))));
		allSteps.add(new PanelDetails("Exposing Councillor Hughes", new ArrayList<>(Arrays.asList(talkToQueenOfThieves, exitWarrens2, goToKingstown, openChest, leaveKingtstown, talkToLawry2, enterWarrens3, talkToShauna))));
		return allSteps;
	}
}
