package com.questhelper.helpers.quests.dwarfcannon;

import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import static com.questhelper.requirements.util.LogicHelper.and;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.PuzzleWrapperStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;

public class DwarfCannon extends BasicQuestHelper
{
	// Required items
	ItemRequirement hammer;

	// Recommended items
	ItemRequirement staminas;
	ItemRequirement teleToAsg;
	ItemRequirement teleToKand;

	// Zones
	Zone cave;
	Zone tower1;
	Zone tower2;
	Zone lawgofArea;

	// Miscellaneous requirements
	ItemRequirement railing;
	ItemRequirement dwarfRemains;
	ItemRequirement toolkit;
	ItemRequirement cannonballMould;
	ItemRequirement nulodionsNotes;

	ZoneRequirement upTower1;
	ZoneRequirement upTower2;
	ZoneRequirement inCave;
	VarbitRequirement bar1;
	VarbitRequirement bar2;
	VarbitRequirement bar3;
	VarbitRequirement bar4;
	VarbitRequirement bar5;
	VarbitRequirement bar6;
	ZoneRequirement nearLawgof;
	VarbitRequirement springFixed;
	VarbitRequirement safetyFixed;
	VarbitRequirement cannonFixed;

	// Steps
	QuestStep talkToCaptainLawgof;
	QuestStep talkToCaptainLawgof2;
	QuestStep gotoTower;
	QuestStep goToTower2;
	QuestStep talkToCaptainLawgof3;
	QuestStep gotoCave;
	QuestStep inspectRailings1;
	QuestStep inspectRailings2;
	QuestStep inspectRailings3;
	QuestStep inspectRailings4;
	QuestStep inspectRailings5;
	QuestStep inspectRailings6;
	QuestStep getRemainsStep;
	QuestStep downTower;
	QuestStep downTower2;
	QuestStep searchCrates;
	QuestStep talkToCaptainLawgof4;
	QuestStep useToolkit;
	QuestStep talkToCaptainLawgof5;
	QuestStep talkToNulodion;
	QuestStep talkToCaptainLawgof6;

	@Override
	protected void setupZones()
	{
		cave = new Zone(new WorldPoint(2557, 9790, 0), new WorldPoint(2624, 9859, 0));
		tower1 = new Zone(new WorldPoint(2568, 3439, 1), new WorldPoint(2572, 3445, 1));
		tower2 = new Zone(new WorldPoint(2566, 3445, 2), new WorldPoint(2572, 3441, 2));
		lawgofArea = new Zone(new WorldPoint(2551, 3477, 0), new WorldPoint(2595, 3434, 0));
	}

	@Override
	protected void setupRequirements()
	{
		hammer = new ItemRequirement("Hammer", ItemCollections.HAMMER).isNotConsumed().canBeObtainedDuringQuest();
		hammer.setTooltip("Can be found in the small building east of Captain Lawgof.");

		staminas = new ItemRequirement("Stamina Potions", ItemCollections.STAMINA_POTIONS);
		teleToAsg = new ItemRequirement("Teleport to Falador, Amulet of Glory, or Combat Bracelet",
			ItemID.POH_TABLET_FALADORTELEPORT);
		teleToAsg.addAlternates(ItemCollections.AMULET_OF_GLORIES);
		teleToAsg.addAlternates(ItemCollections.COMBAT_BRACELETS);
		teleToKand = new ItemRequirement("Teleport to Ardougne, Skills Necklace, or Games Necklace",
			ItemCollections.GAMES_NECKLACES);
		teleToKand.addAlternates(ItemCollections.SKILLS_NECKLACES);
		teleToKand.addAlternates(ItemID.POH_TABLET_ARDOUGNETELEPORT);

		railing = new ItemRequirement("Railing", ItemID.MCANNONRAILING1_OBJ);
		railing.setTooltip("You can get more from Captain Lawgof");
		toolkit = new ItemRequirement("Toolkit", ItemID.MCANNONTOOLKIT);
		toolkit.setHighlightInInventory(true);
		dwarfRemains = new ItemRequirement("Dwarf Remains", ItemID.MCANNONREMAINS);
		cannonballMould = new ItemRequirement("Cannonball Mould", ItemID.AMMO_MOULD);
		nulodionsNotes = new ItemRequirement("Nulodion's Notes", ItemID.NULODIONS_NOTES);

		bar1 = new VarbitRequirement(VarbitID.MCANNON_RAILING1_FIXED, 1);
		bar2 = new VarbitRequirement(VarbitID.MCANNON_RAILING2_FIXED, 1);
		bar3 = new VarbitRequirement(VarbitID.MCANNON_RAILING3_FIXED, 1);
		bar4 = new VarbitRequirement(VarbitID.MCANNON_RAILING4_FIXED, 1);
		bar5 = new VarbitRequirement(VarbitID.MCANNON_RAILING5_FIXED, 1);
		bar6 = new VarbitRequirement(VarbitID.MCANNON_RAILING6_FIXED, 1);

		springFixed = new VarbitRequirement(VarbitID.MCANNON_SPRING_SET, 1);
		safetyFixed = new VarbitRequirement(VarbitID.MCANNON_SAFETY_ON, 1);
		cannonFixed = new VarbitRequirement(VarbitID.MCANNONMULTI_TOOL1, 1);

		upTower1 = new ZoneRequirement(tower1);
		upTower2 = new ZoneRequirement(tower2);
		inCave = new ZoneRequirement(cave);
		nearLawgof = new ZoneRequirement(lawgofArea);
	}

	public void setupSteps()
	{
		talkToCaptainLawgof = new NpcStep(this, NpcID.LAWGOF2, new WorldPoint(2567, 3460, 0), "Talk to Captain Lawgof near the Coal Truck Mining Site (north of Fishing Guild, West of McGrubor's Wood).");
		talkToCaptainLawgof.addDialogStep("Yes.");

		//Fix the 6 bent railings, these railings don't have different IDs from the normal railings
		inspectRailings1 = new ObjectStep(this, ObjectID.MCANNON_RAILING1_MULTILOC, new WorldPoint(2555, 3479, 0), "Inspect the 6 damaged railings around the  camp to fix them.", hammer, railing);
		inspectRailings2 = new ObjectStep(this, ObjectID.MCANNON_RAILING2_MULTILOC, new WorldPoint(2557, 3468, 0), "Inspect the railings to fix them.", hammer, railing);
		inspectRailings3 = new ObjectStep(this, ObjectID.MCANNON_RAILING3_MULTILOC, new WorldPoint(2559, 3458, 0), "Inspect the railings to fix them.", hammer, railing);
		inspectRailings4 = new ObjectStep(this, ObjectID.MCANNON_RAILING4_MULTILOC, new WorldPoint(2563, 3457, 0), "Inspect the railings to fix them.", hammer, railing);
		inspectRailings5 = new ObjectStep(this, ObjectID.MCANNON_RAILING5_MULTILOC, new WorldPoint(2573, 3457, 0), "Inspect the railings to fix them.", hammer, railing);
		inspectRailings6 = new ObjectStep(this, ObjectID.MCANNON_RAILING6_MULTILOC, new WorldPoint(2577, 3457, 0), "Inspect the railings to fix them.", hammer, railing);
		inspectRailings1.addSubSteps(inspectRailings2, inspectRailings3, inspectRailings4, inspectRailings5, inspectRailings6);

		//Get dwarf remains
		talkToCaptainLawgof2 = new NpcStep(this, NpcID.LAWGOF2, new WorldPoint(2567, 3460, 0), "Talk to Captain Lawgof again.  Make sure to complete the entire dialogue.");
		gotoTower = new ObjectStep(this, ObjectID.LADDER, new WorldPoint(2570, 3441, 0), "Go to the top floor of the tower south of Captain Lawgof and get the remains there.");
		goToTower2 = new ObjectStep(this, ObjectID.MCANNONLADDER, new WorldPoint(2570, 3443, 1), "Go up the second ladder.");

		getRemainsStep = new ObjectStep(this, ObjectID.MCANNONREMAINS_MULTILOC, new WorldPoint(2567, 3444, 2), "Get the dwarf remains at the top of the tower.");
		gotoTower.addSubSteps(goToTower2, getRemainsStep);

		downTower = new ObjectStep(this, ObjectID.LADDERTOP, new WorldPoint(2570, 3443, 2), "Go down the first ladder.");
		downTower2 = new ObjectStep(this, ObjectID.LADDERTOP, new WorldPoint(2570, 3441, 1), "Go down the second ladder.");
		talkToCaptainLawgof3 = new NpcStep(this, NpcID.LAWGOF2, new WorldPoint(2567, 3460, 0), "Return the remains to Captain Lawgof.");
		talkToCaptainLawgof3.addSubSteps(downTower, downTower2);

		//Cave
		gotoCave = new ObjectStep(this, ObjectID.MCANNONCAVE, new WorldPoint(2624, 3393, 0), "Go to the cave entrance east of the Fishing Guild door.");
		searchCrates = new ObjectStep(this, ObjectID.MCANNONCRATEBOY, new WorldPoint(2571, 9850, 0), "Search the crates in the north west corner to find Lollk.");
		talkToCaptainLawgof4 = new NpcStep(this, NpcID.LAWGOF2, new WorldPoint(2567, 3460, 0), "Return to Captain Lawgof.");
		talkToCaptainLawgof4.addDialogStep("Okay, I'll see what I can do.");

		//Fix cannon
		// TODO: Update this to highlight widgets as you progress, indicating what tool to use on what
		useToolkit = new PuzzleWrapperStep(this,
			new ObjectStep(this, ObjectID.MCANNON_CANNON_MULTILOC, new WorldPoint(2563, 3462, 0),
				"Use the toolkit on the broken multicannon.  Use the right tool on the spring, the middle tool on the Safety switch, and the left tool on the gear."),
			new ObjectStep(this, ObjectID.MCANNON_CANNON_MULTILOC, new WorldPoint(2563, 3462, 0), "Use the toolkit on the broken multicannon."));
		useToolkit.addIcon(ItemID.MCANNONTOOLKIT);
		talkToCaptainLawgof5 = new NpcStep(this, NpcID.LAWGOF2, new WorldPoint(2567, 3460, 0), "Talk to Captain Lawgof (There will be a short pause in dialogue.  Both need to be completed.).");
		talkToCaptainLawgof5.addDialogStep("Okay then, just for you!");

		//Cannonball mould
		talkToNulodion = new NpcStep(this, NpcID.NULODION, new WorldPoint(3012, 3453, 0), "Go talk to Nulodion at the Dwarven Black Guard camp (north-east of Falador, South of Ice Mountain).");
		talkToCaptainLawgof6 = new NpcStep(this, NpcID.LAWGOF2, new WorldPoint(2567, 3460, 0), "Finally, return to Captain Lawgof with the ammo mould and Nulodion's Notes.", nulodionsNotes, cannonballMould);
	}

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();

		var steps = new HashMap<Integer, QuestStep>();

		// Start
		steps.put(0, talkToCaptainLawgof);

		//Repair Bars
		var fixedRailings = new ConditionalStep(this, inspectRailings1);
		fixedRailings.addStep(bar6, talkToCaptainLawgof2);
		fixedRailings.addStep(and(hammer, railing, bar5), inspectRailings6);
		fixedRailings.addStep(and(hammer, railing, bar4), inspectRailings5);
		fixedRailings.addStep(and(hammer, railing, bar3), inspectRailings4);
		fixedRailings.addStep(and(hammer, railing, bar2), inspectRailings3);
		fixedRailings.addStep(and(hammer, railing, bar1), inspectRailings2);

		steps.put(1, fixedRailings);

		//Go to tower, get remains, come back
		var getRemains = new ConditionalStep(this, gotoTower);
		getRemains.addStep(and(dwarfRemains, nearLawgof), talkToCaptainLawgof3);
		getRemains.addStep(and(dwarfRemains, upTower1), downTower2);
		getRemains.addStep(and(dwarfRemains, upTower2), downTower);
		getRemains.addStep(upTower2, getRemainsStep);
		getRemains.addStep(upTower1, goToTower2);
		steps.put(2, getRemains);
		steps.put(3, getRemains);

		//Go to the cave, find Lollk, return and fix cannon
		var findLollk = new ConditionalStep(this, gotoCave);
		findLollk.addStep(inCave, searchCrates);
		steps.put(4, findLollk);
		steps.put(5, findLollk);

		steps.put(6, talkToCaptainLawgof4);

		steps.put(7, useToolkit);
		steps.put(8, talkToCaptainLawgof5);

		//Ammo mould and back
		var captainLawgofFinal = new ConditionalStep(this, talkToNulodion);
		captainLawgofFinal.addStep(and(nulodionsNotes, cannonballMould), talkToCaptainLawgof6);
		steps.put(9, captainLawgofFinal);
		steps.put(10, captainLawgofFinal);

		return steps;
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return List.of(
			hammer
		);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return List.of(
			staminas,
			teleToAsg,
			teleToKand
		);
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return List.of(
			new ExperienceReward(Skill.CRAFTING, 750)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return List.of(
			new UnlockReward("Ability to purchase and use the Dwarf Multicannon."),
			new UnlockReward("Ability to make cannonballs.")
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var sections = new ArrayList<PanelDetails>();

		sections.add(new PanelDetails("Starting off", List.of(
			talkToCaptainLawgof
		)));

		sections.add(new PanelDetails("Repair and Retrieval", List.of(
			inspectRailings1,
			talkToCaptainLawgof2,
			gotoTower,
			talkToCaptainLawgof3
		), List.of(
			hammer
		)));

		sections.add(new PanelDetails("Find Lollk and Fix Cannon", List.of(
			gotoCave,
			searchCrates,
			talkToCaptainLawgof4,
			useToolkit,
			talkToCaptainLawgof5
		)));

		sections.add(new PanelDetails("Get Ammo Mould", List.of(
			talkToNulodion,
			talkToCaptainLawgof6
		)));

		return sections;
	}
}
