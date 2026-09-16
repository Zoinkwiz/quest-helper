/*
 * Copyright (c) 2026, Aknaus
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
package com.questhelper.helpers.mischelpers.cracktheclue;

import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ItemReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.DigStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import static com.questhelper.requirements.util.LogicHelper.and;
import static com.questhelper.requirements.util.LogicHelper.not;

/**
 * Helper for the original "Crack the Clue!" event (Crack the Clue I).
 *
 * The event has five fixed dig spots which award the Clue hunter outfit pieces
 * and the Helm of raedwald. There is no game varbit tracking completion, so this
 * is registered as a {@code GENERIC} helper that never auto-completes. The active
 * world hint follows the player to whichever dig area they are standing in; every
 * dig spot is also listed in the sidebar so they can be done in any order.
 *
 * Completion of each clue is normally inferred from owning the reward piece (incl.
 * bank). Rewards stored outside tracked containers, such as in a costume room, cannot
 * be detected, so each clue's sidebar section carries a "Mark section as complete"
 * checkbox (its locking step). A locked clue is skipped in the guidance and the section
 * collapses rather than hides, so it can be un-ticked again. Owning the reward auto-locks
 * the section the same way. The manual toggle is per-session (it is not persisted across
 * restarts).
 */
public class CrackTheClue extends ComplexStateQuestHelper
{
	// Required
	ItemRequirement spade;

	// Final clue (Helm of raedwald) specific items, which must be held while digging
	ItemRequirement natureRune, superantipoison, leatherBoots;

	// Reward pieces, checked against inventory, equipment and bank to detect which clues are already done
	ItemRequirement hasGloves, hasBoots, hasGarb, hasTrousers, hasCloak, hasHelm;
	Requirement glovesBootsDone, garbDone, trousersDone, cloakDone, helmDone;

	QuestStep digGlovesBoots, digGarb, digTrousers, digCloak, digHelm, allObtained;

	Zone glovesBootsArea, garbArea, trousersArea, cloakArea, helmArea;
	Requirement inGlovesBootsArea, inGarbArea, inTrousersArea, inCloakArea, inHelmArea;

	@Override
	public QuestStep loadStep()
	{
		initializeRequirements();
		setupZones();
		// Steps are built before conditions because setupConditions() attaches each clue's
		// "done" requirement to its dig step as the locking condition.
		setupSteps();
		setupConditions();

		// No completion varbit exists for this event, so completion is inferred from owning the
		// reward pieces (incl. bank). Prefer the dig spot for the area the player is standing in;
		// otherwise point at the next clue still to be done. Once everything is owned, show allObtained.
		ConditionalStep solveClues = new ConditionalStep(this, allObtained);
		solveClues.addStep(and(inHelmArea, not(helmDone)), digHelm);
		solveClues.addStep(and(inCloakArea, not(cloakDone)), digCloak);
		solveClues.addStep(and(inTrousersArea, not(trousersDone)), digTrousers);
		solveClues.addStep(and(inGarbArea, not(garbDone)), digGarb);
		solveClues.addStep(and(inGlovesBootsArea, not(glovesBootsDone)), digGlovesBoots);
		solveClues.addStep(not(glovesBootsDone), digGlovesBoots);
		solveClues.addStep(not(garbDone), digGarb);
		solveClues.addStep(not(trousersDone), digTrousers);
		solveClues.addStep(not(cloakDone), digCloak);
		solveClues.addStep(not(helmDone), digHelm);

		return solveClues;
	}

	@Override
	protected void setupRequirements()
	{
		spade = new ItemRequirement("Spade", ItemID.SPADE).isNotConsumed();

		natureRune = new ItemRequirement("Nature rune", ItemID.NATURERUNE);
		// Superantipoison(1) - must have exactly one dose remaining when digging for the helm.
		superantipoison = new ItemRequirement("Superantipoison (1 dose)", ItemID._1DOSE2ANTIPOISON);
		leatherBoots = new ItemRequirement("Leather boots", ItemID.LEATHER_BOOTS);

		hasGloves = new ItemRequirement("Clue hunter gloves", ItemID.CLUE_HUNTER_GLOVES).alsoCheckBank();
		hasBoots = new ItemRequirement("Clue hunter boots", ItemID.CLUE_HUNTER_BOOTS).alsoCheckBank();
		hasGarb = new ItemRequirement("Clue hunter garb", ItemID.CLUE_HUNTER_GARB).alsoCheckBank();
		hasTrousers = new ItemRequirement("Clue hunter trousers", ItemID.CLUE_HUNTER_TROUSERS).alsoCheckBank();
		hasCloak = new ItemRequirement("Clue hunter cloak", ItemID.CLUE_HUNTER_CLOAK).alsoCheckBank();
		hasHelm = new ItemRequirement("Helm of raedwald", ItemID.RAEDWALD_HELM).alsoCheckBank();
	}

	protected void setupZones()
	{
		// Each zone is a generous area around the dig spot, used only to pick which dig step is
		// surfaced as the active world hint when the player is nearby.
		glovesBootsArea = new Zone(new WorldPoint(2564, 3363, 0), new WorldPoint(2594, 3393, 0));
		garbArea = new Zone(new WorldPoint(1580, 3613, 0), new WorldPoint(1610, 3643, 0));
		trousersArea = new Zone(new WorldPoint(2804, 3111, 0), new WorldPoint(2834, 3141, 0));
		cloakArea = new Zone(new WorldPoint(2599, 3050, 0), new WorldPoint(2629, 3080, 0));
		helmArea = new Zone(new WorldPoint(2575, 3216, 0), new WorldPoint(2605, 3246, 0));
	}

	protected void setupConditions()
	{
		inGlovesBootsArea = new ZoneRequirement(glovesBootsArea);
		inGarbArea = new ZoneRequirement(garbArea);
		inTrousersArea = new ZoneRequirement(trousersArea);
		inCloakArea = new ZoneRequirement(cloakArea);
		inHelmArea = new ZoneRequirement(helmArea);

		// Clue 1 awards both gloves and boots in a single dig, so it is only done once both are owned.
		glovesBootsDone = and(hasGloves, hasBoots);
		garbDone = hasGarb;
		trousersDone = hasTrousers;
		cloakDone = hasCloak;
		helmDone = hasHelm;

		// Owning the reward auto-locks (and ticks) that clue's section. While it isn't owned, the
		// player can still tick the section's "Mark section as complete" box manually (see getPanels);
		// a locked dig step is skipped by the ConditionalStep in loadStep().
		digGlovesBoots.setLockingCondition(glovesBootsDone);
		digGarb.setLockingCondition(garbDone);
		digTrousers.setLockingCondition(trousersDone);
		digCloak.setLockingCondition(cloakDone);
		digHelm.setLockingCondition(helmDone);
	}

	private void setupSteps()
	{
		// Clue 1 - Clue hunter gloves & boots: oak tree south-west of the Fishing Guild,
		// dig two squares south and one square west of the tree's south-west corner.
		digGlovesBoots = new DigStep(this, new WorldPoint(2579, 3378, 0),
			"Dig by the oak tree south-west of the Fishing Guild (two squares south, one west of its " +
				"south-west corner) for the Clue hunter gloves and boots.");

		// Clue 2 - Clue hunter garb: rocky area just north of the East Shayzien minecart station (near Mogrim).
		digGarb = new DigStep(this, new WorldPoint(1595, 3628, 0),
			"Dig at the rocky area just north of the East Shayzien minecart station for the Clue hunter garb.");

		// Clue 3 - Clue hunter trousers: near the Pothole Dungeon entrance north of Tai Bwo Wannai.
		digTrousers = new DigStep(this, new WorldPoint(2819, 3126, 0),
			"Dig near the Pothole Dungeon entrance north of Tai Bwo Wannai for the Clue hunter trousers.");

		// Clue 4 - Clue hunter cloak: between two willow trees south-east of Yanille.
		digCloak = new DigStep(this, new WorldPoint(2614, 3065, 0),
			"Dig between the two willow trees south-east of Yanille for the Clue hunter cloak.");

		// Clue 5 - Helm of raedwald: iron rocks by the cave entrance east of the Clock Tower.
		// Requires a nature rune, a one-dose superantipoison and leather boots in the inventory.
		digHelm = new DigStep(this, new WorldPoint(2590, 3231, 0),
			"Dig by the iron rocks east of the Clock Tower for the Helm of raedwald. You must be carrying a " +
				"nature rune, a one-dose superantipoison and leather boots.",
			natureRune, superantipoison, leatherBoots);

		allObtained = new DetailedQuestStep(this, "You have obtained all of the Crack the Clue rewards.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(spade, natureRune, superantipoison, leatherBoots);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
			new ItemReward("Clue hunter gloves", ItemID.CLUE_HUNTER_GLOVES),
			new ItemReward("Clue hunter boots", ItemID.CLUE_HUNTER_BOOTS),
			new ItemReward("Clue hunter garb", ItemID.CLUE_HUNTER_GARB),
			new ItemReward("Clue hunter trousers", ItemID.CLUE_HUNTER_TROUSERS),
			new ItemReward("Clue hunter cloak", ItemID.CLUE_HUNTER_CLOAK),
			new ItemReward("Helm of raedwald", ItemID.RAEDWALD_HELM));
	}

	@Override
	public List<String> getNotes()
	{
		return Collections.singletonList("The clues can be completed in any order. Lost Clue hunter outfit " +
			"pieces can be reobtained by digging at their clue locations again.");
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		// One section per clue. Each gets a "Mark section as complete" checkbox in its header via its
		// locking step; ticking it (or owning the reward) collapses the section instead of hiding it,
		// so it stays visible and can be un-ticked. We deliberately do not use PanelDetails.lockedPanel
		// here as that adds a display/hide condition.
		allSteps.add(lockableSection("Clue hunter gloves & boots", digGlovesBoots, spade));
		allSteps.add(lockableSection("Clue hunter garb", digGarb, spade));
		allSteps.add(lockableSection("Clue hunter trousers", digTrousers, spade));
		allSteps.add(lockableSection("Clue hunter cloak", digCloak, spade));
		allSteps.add(lockableSection("Helm of raedwald", digHelm, spade, natureRune, superantipoison, leatherBoots));
		return allSteps;
	}

	private PanelDetails lockableSection(String header, QuestStep digStep, Requirement... requirements)
	{
		PanelDetails section = new PanelDetails(header, Collections.singletonList(digStep), requirements);
		section.setLockingStep(digStep);
		return section;
	}
}
