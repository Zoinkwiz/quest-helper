package com.questhelper.quests.dwarfcannon;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
        quest = QuestHelperQuest.DWARF_CANNON
)

public class DwarfCannon extends BasicQuestHelper
{
	//Items Recommended
	ItemRequirement staminas, teleToAsg, teleToKand;

	//Items Required
	ItemRequirement hammer, railing, dwarfRemains, toolkit, cannonballMould, nulodionsNotes;

	Requirement upTower1, upTower2, inCave, bar1, bar2, bar3, bar4, bar5, bar6, nearLawgof, springFixed, safetyFixed, cannonFixed;

	QuestStep talkToCaptainLawgof, talkToCaptainLawgof2, gotoTower, goToTower2, talkToCaptainLawgof3, gotoCave, inspectRailings1, inspectRailings2,
		inspectRailings3, inspectRailings4, inspectRailings5, inspectRailings6, getRemainsStep, downTower, downTower2, searchCrates,
		talkToCaptainLawgof4, useToolkit, talkToCaptainLawgof5, talkToNulodion, talkToCaptainLawgof6;

	//Zones
	Zone cave, tower1, tower2, lawgofArea;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupRequirements();
		setupZones();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		//Start
		steps.put(0, talkToCaptainLawgof);

		//Repair Bars
		ConditionalStep fixedRailings = new ConditionalStep(this, inspectRailings1);
		fixedRailings.addStep(new Conditions(bar6), talkToCaptainLawgof2);
		fixedRailings.addStep(new Conditions(hammer, railing, bar5), inspectRailings6);
		fixedRailings.addStep(new Conditions(hammer, railing, bar4), inspectRailings5);
		fixedRailings.addStep(new Conditions(hammer, railing, bar3), inspectRailings4);
		fixedRailings.addStep(new Conditions(hammer, railing, bar2), inspectRailings3);
		fixedRailings.addStep(new Conditions(hammer, railing, bar1), inspectRailings2);

		steps.put(1, fixedRailings);

		//Go to tower, get remains, come back
		ConditionalStep getRemains = new ConditionalStep(this, gotoTower);
		getRemains.addStep(new Conditions(dwarfRemains, nearLawgof), talkToCaptainLawgof3);
		getRemains.addStep(new Conditions(dwarfRemains, upTower1), downTower2);
		getRemains.addStep(new Conditions(dwarfRemains, upTower2), downTower);
		getRemains.addStep(upTower2, getRemainsStep);
		getRemains.addStep(upTower1, goToTower2);
		steps.put(2, getRemains);
		steps.put(3, getRemains);

		//Go to the cave, find Lollk, return and fix cannon
		ConditionalStep findLollk = new ConditionalStep(this, gotoCave);
		findLollk.addStep(inCave, searchCrates);
		steps.put(4, findLollk);
		steps.put(5, findLollk);

		steps.put(6, talkToCaptainLawgof4);

		steps.put(7, useToolkit);
		steps.put(8, talkToCaptainLawgof5);

		//Ammo mould and back
		ConditionalStep captainLawgofFinal = new ConditionalStep(this, talkToNulodion);
		captainLawgofFinal.addStep(new Conditions(nulodionsNotes, cannonballMould), talkToCaptainLawgof6);
		steps.put(9, captainLawgofFinal);
		steps.put(10, captainLawgofFinal);

		return steps;
	}

	@Override
	public void setupRequirements()
	{
		staminas = new ItemRequirement("Stamina Potions", ItemCollections.STAMINA_POTIONS);
		teleToAsg = new ItemRequirement("Teleport to Falador, Amulet of Glory, or Combat Bracelet",
			ItemID.FALADOR_TELEPORT);
		teleToAsg.addAlternates(ItemCollections.AMULET_OF_GLORIES);
		teleToAsg.addAlternates(ItemCollections.COMBAT_BRACELETS);
		teleToKand = new ItemRequirement("Teleport to Ardougne, Skills Necklace, or Games Necklace",
			ItemCollections.GAMES_NECKLACES);
		teleToKand.addAlternates(ItemCollections.SKILLS_NECKLACES);
		teleToKand.addAlternates(ItemID.ARDOUGNE_TELEPORT);

		hammer = new ItemRequirement("Hammer", ItemCollections.HAMMER).isNotConsumed();
		railing = new ItemRequirement("Railing", ItemID.RAILING);
		railing.setTooltip("You can get more from Captain Lawgof");
		toolkit = new ItemRequirement("Toolkit", ItemID.TOOLKIT);
		toolkit.setHighlightInInventory(true);
		dwarfRemains = new ItemRequirement("Dwarf Remains", ItemID.DWARF_REMAINS);
		cannonballMould = new ItemRequirement("Cannonball Mould", ItemID.AMMO_MOULD);
		nulodionsNotes = new ItemRequirement("Nulodion's Notes", ItemID.NULODIONS_NOTES);
	}

	public void setupConditions()
	{
		//Varbits
		bar1 = new VarbitRequirement(2240, 1);
		bar2 = new VarbitRequirement(2241, 1);
		bar3 = new VarbitRequirement(2242, 1);
		bar4 = new VarbitRequirement(2243, 1);
		bar5 = new VarbitRequirement(2244, 1);
		bar6 = new VarbitRequirement(2245, 1);
		//All Complete varbit 2246

		springFixed = new VarbitRequirement(2239, 1);
		safetyFixed = new VarbitRequirement(2238, 1);
		cannonFixed = new VarbitRequirement(2235, 1);

		//Zones
		upTower1 = new ZoneRequirement(tower1);
		upTower2 = new ZoneRequirement(tower2);
		inCave = new ZoneRequirement(cave);
		nearLawgof = new ZoneRequirement(lawgofArea);

	}

	public void setupZones()
	{
		cave = new Zone(new WorldPoint(2557, 9790, 0), new WorldPoint(2624, 9859, 0));
		tower1 = new Zone(new WorldPoint(2568, 3439, 1), new WorldPoint(2572, 3445, 1));
		tower2 = new Zone(new WorldPoint(2566, 3445, 2), new WorldPoint(2572, 3441, 2));
		lawgofArea = new Zone(new WorldPoint(2551, 3477, 0), new WorldPoint(2595, 3434, 0));
	}

	public void setupSteps()
	{
		talkToCaptainLawgof = new NpcStep(this, NpcID.CAPTAIN_LAWGOF, new WorldPoint(2567, 3460, 0), "Talk to Captain Lawgof near the Coal Truck Mining Site (north of Fishing Guild, West of McGrubor's Wood).");
		talkToCaptainLawgof.addDialogStep("Sure, I'd be honoured to join.");

		//Fix the 6 bent railings, these railings don't have different IDs from the normal railings
		inspectRailings1 = new ObjectStep(this, NullObjectID.NULL_15590, new WorldPoint(2555, 3479, 0), "Inspect the 6 damaged railings around the  camp to fix them.", hammer, railing);
		inspectRailings2 = new ObjectStep(this, NullObjectID.NULL_15591, new WorldPoint(2557, 3468, 0), "Inspect the railings to fix them.", hammer, railing);
		inspectRailings3 = new ObjectStep(this, NullObjectID.NULL_15592, new WorldPoint(2559, 3458, 0), "Inspect the railings to fix them.", hammer, railing);
		inspectRailings4 = new ObjectStep(this, NullObjectID.NULL_15593, new WorldPoint(2563, 3457, 0), "Inspect the railings to fix them.", hammer, railing);
		inspectRailings5 = new ObjectStep(this, NullObjectID.NULL_15594, new WorldPoint(2573, 3457, 0), "Inspect the railings to fix them.", hammer, railing);
		inspectRailings6 = new ObjectStep(this, NullObjectID.NULL_15595, new WorldPoint(2577, 3457, 0), "Inspect the railings to fix them.", hammer, railing);
		inspectRailings1.addSubSteps(inspectRailings2, inspectRailings3, inspectRailings4, inspectRailings5, inspectRailings6);

		//Get dwarf remains
		talkToCaptainLawgof2 = new NpcStep(this, NpcID.CAPTAIN_LAWGOF, new WorldPoint(2567, 3460, 0), "Talk to Captain Lawgof again.  Make sure to complete the entire dialogue.");
		gotoTower = new ObjectStep(this, ObjectID.LADDER_16683, new WorldPoint(2570, 3441, 0), "Go to the top floor of the tower south of Captain Lawgof and get the remains there.");
		goToTower2 = new ObjectStep(this, ObjectID.LADDER_11, new WorldPoint(2570, 3443, 1), "Go up the second ladder.");

		getRemainsStep = new ObjectStep(this, NullObjectID.NULL, new WorldPoint(2567, 3444, 2), "Get the dwarf remains at the top of the tower.");
		gotoTower.addSubSteps(goToTower2, getRemainsStep);

		downTower = new ObjectStep(this, ObjectID.LADDER_16679, new WorldPoint(2570, 3443, 2), "Go down the first ladder.");
		downTower2 = new ObjectStep(this, ObjectID.LADDER_16679, new WorldPoint(2570, 3441, 1), "Go down the second ladder.");
		talkToCaptainLawgof3 = new NpcStep(this, NpcID.CAPTAIN_LAWGOF, new WorldPoint(2567, 3460, 0), "Return the remains to Captain Lawgof.");
		talkToCaptainLawgof3.addSubSteps(downTower, downTower2);

		//Cave
		gotoCave = new ObjectStep(this, ObjectID.CAVE_ENTRANCE, new WorldPoint(2624, 3393, 0), "Go to the cave entrance east of the Fishing Guild door.");
		searchCrates = new ObjectStep(this, ObjectID.CRATE, new WorldPoint(2571, 9850, 0), "Search the crates in the north west corner to find Lollk.");
		talkToCaptainLawgof4 = new NpcStep(this, NpcID.CAPTAIN_LAWGOF, new WorldPoint(2567, 3460, 0), "Return to Captain Lawgof.");
		talkToCaptainLawgof4.addDialogStep("Okay, I'll see what I can do.");

		//Fix cannon
		// TODO: Update this to highlight widgets as you progress, indicating what tool to use on what
		useToolkit = new ObjectStep(this, NullObjectID.NULL_15597, new WorldPoint(2563, 3462, 0), "Use the toolkit on the broken multicannon.  Use the right tool on the spring, the middle tool on the Safety switch, and the left tool on the gear.");
		useToolkit.addIcon(ItemID.TOOLKIT);
		talkToCaptainLawgof5 = new NpcStep(this, NpcID.CAPTAIN_LAWGOF, new WorldPoint(2567, 3460, 0), "Talk to Captain Lawgof (There will be a short pause in dialogue.  Both need to be completed.).");
		talkToCaptainLawgof5.addDialogStep("Okay then, just for you!");

		//Cannonball mould
		talkToNulodion = new NpcStep(this, NpcID.NULODION, new WorldPoint(3012, 3453, 0), "Go talk to Nulodion at the Dwarven Black Guard camp (north-east of Falador, South of Ice Mountain).");
		talkToCaptainLawgof6 = new NpcStep(this, NpcID.CAPTAIN_LAWGOF, new WorldPoint(2567, 3460, 0), "Finally, return to Captain Lawgof with the ammo mould and Nulodion's Notes.", nulodionsNotes, cannonballMould);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(staminas);
		reqs.add(teleToAsg);
		reqs.add(teleToKand);
		return reqs;
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Collections.singletonList(new ExperienceReward(Skill.CRAFTING, 750));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
				new UnlockReward("Ability to purchase and use the Dwarf Multicannon."),
				new UnlockReward("Ability to make cannonballs."));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off", Collections.singletonList(talkToCaptainLawgof)));
		allSteps.add(new PanelDetails("Repair and Retrieval", Arrays.asList(inspectRailings1, talkToCaptainLawgof2, gotoTower, talkToCaptainLawgof3)));
		allSteps.add(new PanelDetails("Find Lollk and Fix Cannon", Arrays.asList(gotoCave, searchCrates, talkToCaptainLawgof4, useToolkit, talkToCaptainLawgof5)));
		allSteps.add(new PanelDetails("Get Ammo Mould", Arrays.asList(talkToNulodion, talkToCaptainLawgof6)));
		return allSteps;
	}
}
