package com.questhelper.helpers.quests.dwarfcannon;

import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import static com.questhelper.requirements.util.LogicHelper.and;
import static com.questhelper.requirements.util.LogicHelper.not;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.widget.WidgetPresenceRequirement;
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
import com.questhelper.steps.WidgetStep;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.InterfaceID;
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
	Conditions allBarsFixed;

	WidgetPresenceRequirement isPuzzleOpen;
	VarbitRequirement toothedToolSelected;
	VarbitRequirement pliersSelected;
	VarbitRequirement hookSelected;
	VarbitRequirement springFixed;
	VarbitRequirement safetyFixed;

	// Steps
	QuestStep talkToCaptainLawgof;

	NpcStep getRailings;
	QuestStep inspectRailings1;
	QuestStep inspectRailings2;
	QuestStep inspectRailings3;
	QuestStep inspectRailings4;
	QuestStep inspectRailings5;
	QuestStep inspectRailings6;
	ConditionalStep cInspectRailings;
	QuestStep talkToCaptainLawgof2;

	QuestStep gotoTower;
	QuestStep goToTower2;
	QuestStep talkToCaptainLawgof3;
	QuestStep getRemainsStep;
	QuestStep downTower;
	QuestStep downTower2;

	QuestStep gotoCave;
	QuestStep searchCrates;
	ObjectStep exitCave;
	QuestStep talkToCaptainLawgof4;

	PuzzleWrapperStep pwFixMulticannon;
	QuestStep talkToCaptainLawgof5;
	QuestStep talkToNulodion;
	QuestStep talkToCaptainLawgof6;

	@Override
	protected void setupZones()
	{
		cave = new Zone(new WorldPoint(2557, 9790, 0), new WorldPoint(2624, 9859, 0));
		tower1 = new Zone(new WorldPoint(2568, 3439, 1), new WorldPoint(2572, 3445, 1));
		tower2 = new Zone(new WorldPoint(2566, 3445, 2), new WorldPoint(2572, 3441, 2));
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
		cannonballMould = new ItemRequirement("Ammo mould", ItemID.AMMO_MOULD);
		nulodionsNotes = new ItemRequirement("Nulodion's notes", ItemID.NULODIONS_NOTES);

		bar1 = new VarbitRequirement(VarbitID.MCANNON_RAILING1_FIXED, 1);
		bar2 = new VarbitRequirement(VarbitID.MCANNON_RAILING2_FIXED, 1);
		bar3 = new VarbitRequirement(VarbitID.MCANNON_RAILING3_FIXED, 1);
		bar4 = new VarbitRequirement(VarbitID.MCANNON_RAILING4_FIXED, 1);
		bar5 = new VarbitRequirement(VarbitID.MCANNON_RAILING5_FIXED, 1);
		bar6 = new VarbitRequirement(VarbitID.MCANNON_RAILING6_FIXED, 1);

		allBarsFixed = and(bar1, bar2, bar3, bar4, bar5, bar6);

		isPuzzleOpen = new WidgetPresenceRequirement(InterfaceID.McannonInterface.ROOT_RECT0);

		toothedToolSelected = new VarbitRequirement(VarbitID.MCANNONMULTI_TOOL1, 1);
		pliersSelected = new VarbitRequirement(VarbitID.MCANNONMULTI_TOOL2, 1);
		hookSelected = new VarbitRequirement(VarbitID.MCANNONMULTI_TOOL3, 1);

		springFixed = new VarbitRequirement(VarbitID.MCANNON_SPRING_SET, 1);
		safetyFixed = new VarbitRequirement(VarbitID.MCANNON_SAFETY_ON, 1);

		upTower1 = new ZoneRequirement(tower1);
		upTower2 = new ZoneRequirement(tower2);
		inCave = new ZoneRequirement(cave);
	}

	public void setupSteps()
	{
		talkToCaptainLawgof = new NpcStep(this, NpcID.LAWGOF2, new WorldPoint(2567, 3460, 0), "Talk to Captain Lawgof near the Coal Truck Mining Site (north of Fishing Guild, West of McGrubor's Wood).");
		talkToCaptainLawgof.addDialogStep("Yes.");

		// Fix the 6 bent railings, these railings don't have different IDs from the normal railings
		getRailings = new NpcStep(this, NpcID.LAWGOF2, new WorldPoint(2567, 3460, 0), "Talk to Captain Lawgof to get more railings.", hammer, railing);

		inspectRailings1 = new ObjectStep(this, ObjectID.MCANNON_RAILING1_MULTILOC, new WorldPoint(2555, 3479, 0), "", hammer, railing);
		inspectRailings2 = new ObjectStep(this, ObjectID.MCANNON_RAILING2_MULTILOC, new WorldPoint(2557, 3468, 0), "", hammer, railing);
		inspectRailings3 = new ObjectStep(this, ObjectID.MCANNON_RAILING3_MULTILOC, new WorldPoint(2559, 3458, 0), "", hammer, railing);
		inspectRailings4 = new ObjectStep(this, ObjectID.MCANNON_RAILING4_MULTILOC, new WorldPoint(2563, 3457, 0), "", hammer, railing);
		inspectRailings5 = new ObjectStep(this, ObjectID.MCANNON_RAILING5_MULTILOC, new WorldPoint(2573, 3457, 0), "", hammer, railing);
		inspectRailings6 = new ObjectStep(this, ObjectID.MCANNON_RAILING6_MULTILOC, new WorldPoint(2577, 3457, 0), "", hammer, railing);

		cInspectRailings = new ConditionalStep(this, getRailings, "Inspect the six damaged railings around the camp to fix them.");
		cInspectRailings.addStep(and(railing, not(bar1)), inspectRailings1);
		cInspectRailings.addStep(and(railing, not(bar2)), inspectRailings2);
		cInspectRailings.addStep(and(railing, not(bar3)), inspectRailings3);
		cInspectRailings.addStep(and(railing, not(bar4)), inspectRailings4);
		cInspectRailings.addStep(and(railing, not(bar5)), inspectRailings5);
		cInspectRailings.addStep(and(railing, not(bar6)), inspectRailings6);

		//Get dwarf remains
		talkToCaptainLawgof2 = new NpcStep(this, NpcID.LAWGOF2, new WorldPoint(2567, 3460, 0), "Talk to Captain Lawgof after repairing the damaged railings.");

		gotoTower = new ObjectStep(this, ObjectID.LADDER, new WorldPoint(2570, 3441, 0), "Go to the top floor of the tower, south of Captain Lawgof, and get the remains there.");
		goToTower2 = new ObjectStep(this, ObjectID.MCANNONLADDER, new WorldPoint(2570, 3443, 1), "Go up the second ladder.");

		getRemainsStep = new ObjectStep(this, ObjectID.MCANNONREMAINS_MULTILOC, new WorldPoint(2567, 3444, 2), "Get the dwarf remains at the top of the tower.");
		gotoTower.addSubSteps(goToTower2, getRemainsStep);

		downTower = new ObjectStep(this, ObjectID.LADDERTOP, new WorldPoint(2570, 3443, 2), "Go down the first ladder.");
		downTower2 = new ObjectStep(this, ObjectID.LADDERTOP, new WorldPoint(2570, 3441, 1), "Go down the second ladder.");
		talkToCaptainLawgof3 = new NpcStep(this, NpcID.LAWGOF2, new WorldPoint(2567, 3460, 0), "Return the remains to Captain Lawgof.");
		talkToCaptainLawgof3.addSubSteps(downTower, downTower2);

		// Find Lollk in the goblin cave
		gotoCave = new ObjectStep(this, ObjectID.MCANNONCAVE, new WorldPoint(2622, 3392, 0), "Enter the goblin cave, east of the Fishing Guild entrance.");
		searchCrates = new ObjectStep(this, ObjectID.MCANNONCRATEBOY, new WorldPoint(2571, 9850, 0), "Search the crates in the north west corner to find Lollk.");
		exitCave = new ObjectStep(this, ObjectID.MCANMUDPILE, new WorldPoint(2621, 9796, 0), "Exit the goblin cave and return to Captain Lawgof.");
		talkToCaptainLawgof4 = new NpcStep(this, NpcID.LAWGOF2, new WorldPoint(2567, 3460, 0), "Return to Captain Lawgof.");
		talkToCaptainLawgof4.addDialogStep("Okay, I'll see what I can do.");
		talkToCaptainLawgof4.addSubSteps(exitCave);

		// Fix cannon
		var actuallyUseToolkit = new ObjectStep(this, ObjectID.MCANNON_CANNON_MULTILOC, new WorldPoint(2563, 3462, 0), "", toolkit.highlighted());
		actuallyUseToolkit.addIcon(ItemID.MCANNONTOOLKIT);

		var clickHook = new WidgetStep(this, "Click the hook and use it on the spring.", InterfaceID.McannonInterface.MCANNON_TOOL3);
		var clickSpring = new WidgetStep(this, "Click the hook and use it on the spring.", InterfaceID.McannonInterface.MCANNON_SPRING);

		var clickPliers = new WidgetStep(this, "Click the pliers and use it on the safety switch at the bottom.", InterfaceID.McannonInterface.MCANNON_TOOL2);
		var clickSafety = new WidgetStep(this, "Click the pliers and use it on the safety switch at the bottom.", InterfaceID.McannonInterface.MCANNON_SAFETY);

		var clickToothedTool = new WidgetStep(this, "Click the toothed tool and use it on the gear at the bottom.", InterfaceID.McannonInterface.MCANNON_TOOL1);
		var clickGear = new WidgetStep(this, "Click the toothed tool and use it on the gear at the bottom.", InterfaceID.McannonInterface.MCANNON_GEAR);

		var fixCannon = new ConditionalStep(this, actuallyUseToolkit, "Use the toolkit on the broken multicannon, then use the highlighted tool on the highlighted part to fix it.");
		fixCannon.addStep(and(isPuzzleOpen, safetyFixed, springFixed, toothedToolSelected), clickGear);
		fixCannon.addStep(and(isPuzzleOpen, safetyFixed, springFixed), clickToothedTool);
		fixCannon.addStep(and(isPuzzleOpen, not(safetyFixed), pliersSelected), clickSafety);
		fixCannon.addStep(and(isPuzzleOpen, not(springFixed), hookSelected), clickSpring);
		fixCannon.addStep(and(isPuzzleOpen, not(safetyFixed)), clickPliers);
		fixCannon.addStep(and(isPuzzleOpen, not(springFixed)), clickHook);

		pwFixMulticannon = fixCannon.puzzleWrapStepWithDefaultText("Use the toolkit on the broken multicannon.");
		pwFixMulticannon.addIcon(ItemID.MCANNONTOOLKIT);
		talkToCaptainLawgof5 = new NpcStep(this, NpcID.LAWGOF2, new WorldPoint(2567, 3460, 0), "Talk to Captain Lawgof after repairing the multicannon.");
		talkToCaptainLawgof5.addDialogStep("Okay then, just for you!");

		// Get notes & mould from Nulodion
		talkToNulodion = new NpcStep(this, NpcID.NULODION, new WorldPoint(3012, 3453, 0), "Talk to Nulodion at the Dwarven Black Guard camp, south of Ice Mountain.");
		talkToCaptainLawgof6 = new NpcStep(this, NpcID.LAWGOF2, new WorldPoint(2567, 3460, 0), "Return to Captain Lawgof with the ammo mould and Nulodion's Notes.", nulodionsNotes, cannonballMould);
	}

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();

		var steps = new HashMap<Integer, QuestStep>();

		// Start
		steps.put(0, talkToCaptainLawgof);

		// Repair damaged railings
		var fixRailings = new ConditionalStep(this, cInspectRailings);
		fixRailings.addStep(allBarsFixed, talkToCaptainLawgof2);
		fixRailings.addStep(not(railing), getRailings);
		steps.put(1, fixRailings);

		// Go to tower, get remains, come back
		var getRemains = new ConditionalStep(this, gotoTower);
		getRemains.addStep(and(dwarfRemains, upTower1), downTower2);
		getRemains.addStep(and(dwarfRemains, upTower2), downTower);
		getRemains.addStep(dwarfRemains, talkToCaptainLawgof3);
		getRemains.addStep(upTower2, getRemainsStep);
		getRemains.addStep(upTower1, goToTower2);
		steps.put(2, getRemains);
		steps.put(3, getRemains);

		//Go to the cave, find Lollk, return and fix cannon
		var findLollk = new ConditionalStep(this, gotoCave);
		findLollk.addStep(inCave, searchCrates);
		steps.put(4, findLollk);
		steps.put(5, findLollk);

		var step6 = new ConditionalStep(this, talkToCaptainLawgof4);
		step6.addStep(inCave, exitCave);
		steps.put(6, step6);

		steps.put(7, pwFixMulticannon);
		steps.put(8, talkToCaptainLawgof5);

		// Ammo mould and back
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
			getRailings,
			cInspectRailings,
			talkToCaptainLawgof2,
			gotoTower,
			talkToCaptainLawgof3
		), List.of(
			hammer
		)));

		sections.add(new PanelDetails("Finding the lad", List.of(
			gotoCave,
			searchCrates,
			talkToCaptainLawgof4,
			pwFixMulticannon,
			talkToCaptainLawgof5
		)));

		sections.add(new PanelDetails("Get Ammo Mould", List.of(
			talkToNulodion,
			talkToCaptainLawgof6
		)));

		return sections;
	}
}
