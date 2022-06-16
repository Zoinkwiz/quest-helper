/*
 * Copyright (c) 2021, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.quests.fairytaleii;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.QuestVarbits;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.conditional.NpcCondition;
import com.questhelper.requirements.item.ItemOnTileRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.FAIRYTALE_II__CURE_A_QUEEN
)
public class FairytaleII extends BasicQuestHelper
{
	ItemRequirement dramenOrLunarStaff, vialOfWater, pestleAndMortar;

	ItemRequirement combatGear, food;

	ItemRequirement fairyCertificate, queensSecateurs, starFlower, gorakClaw, gorakClawPowder, magicEssenceUnf,
		magicEssence;

	Zone zanaris, hideout, starPlane, gorakPlane;

	Requirement inZanaris, inHideout, inStarPlane, inGorakPlane, hasReadSign, hasInvestigatedCertificate,
		talkedToGodfather, pickedStarFlower, starflowerNearby, clawNearby, herbReq, farmReq, thievingReq,
		addedFlowerCorrectly, addedClawCorrectly, starflowerOrUnfCorrectlyMade;

	QuestStep talkToMartin, waitForMartin, talkToMartinAgain;

	QuestStep enterZanaris, takeCertificate, studyCertificate, readSign, talkToGodfather;

	QuestStep goToHideout, goToHideoutSurface, talkToNuff, returnToZanarisFromBase, goToZanarisToPickpocket,
		pickpocketGodfather, goToHideoutWithSec, goToHideoutSurfaceWithSec, giveSecateursToNuff,
		goToHideoutAfterSec, goToHideoutSurfaceAfterSec, talkToNuffAfterSec;

	QuestStep goToCkp, waitForStarFlower, pickStarFlower;

	QuestStep goToDir, killGorak, pickupGorakClaw;

	QuestStep goToHideout2, useStarFlowerOnVial, usePestleOnClaw, usePowderOnPotion,
		usePotionOnQueen, goToHideoutToFinish, talkToQueen;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupRequirements();
		setupZones();
		setupConditions();
		setupSteps();

		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToMartin);
		steps.put(5, waitForMartin);
		steps.put(10, talkToMartinAgain);
		ConditionalStep goInvestigate = new ConditionalStep(this, enterZanaris);
		goInvestigate.addStep(new Conditions(inHideout), talkToNuff);
		goInvestigate.addStep(new Conditions(inZanaris, hasInvestigatedCertificate, hasReadSign, talkedToGodfather), goToHideout);
		goInvestigate.addStep(new Conditions(hasInvestigatedCertificate, hasReadSign, talkedToGodfather), goToHideoutSurface);
		goInvestigate.addStep(new Conditions(inZanaris, hasInvestigatedCertificate, hasReadSign), talkToGodfather);
		goInvestigate.addStep(new Conditions(inZanaris, hasInvestigatedCertificate), readSign);
		goInvestigate.addStep(new Conditions(inZanaris, fairyCertificate.alsoCheckBank(questBank)), studyCertificate);
		goInvestigate.addStep(inZanaris, takeCertificate);
		steps.put(20, goInvestigate);
		steps.put(30, goInvestigate);
		steps.put(40, goInvestigate);
		steps.put(45, goInvestigate);

		ConditionalStep goGetSecateurs = new ConditionalStep(this, goToZanarisToPickpocket);
		goGetSecateurs.addStep(new Conditions(inHideout, queensSecateurs.alsoCheckBank(questBank)), giveSecateursToNuff);
		goGetSecateurs.addStep(new Conditions(inZanaris, queensSecateurs.alsoCheckBank(questBank)), goToHideoutWithSec);
		goGetSecateurs.addStep(queensSecateurs.alsoCheckBank(questBank), goToHideoutSurfaceWithSec);
		goGetSecateurs.addStep(inHideout, returnToZanarisFromBase);
		goGetSecateurs.addStep(inZanaris, pickpocketGodfather);
		steps.put(50, goGetSecateurs);
		steps.put(60, goGetSecateurs);

		ConditionalStep goTalkNuffAfterSec = new ConditionalStep(this, goToHideoutSurfaceAfterSec);
		goTalkNuffAfterSec.addStep(inHideout, talkToNuffAfterSec);
		goTalkNuffAfterSec.addStep(inZanaris, goToHideoutAfterSec);
		steps.put(65, goTalkNuffAfterSec);

		ConditionalStep goMakePotion = new ConditionalStep(this, goToCkp);
		goMakePotion.addStep(new Conditions(addedClawCorrectly, magicEssence.alsoCheckBank(questBank), inHideout),
			usePotionOnQueen);
		goMakePotion.addStep(new Conditions(addedClawCorrectly, magicEssence.alsoCheckBank(questBank)), goToHideout2);
		goMakePotion.addStep(new Conditions(addedFlowerCorrectly, gorakClawPowder.alsoCheckBank(questBank),
			magicEssenceUnf.alsoCheckBank(questBank)), usePowderOnPotion);
		goMakePotion.addStep(new Conditions(addedFlowerCorrectly, gorakClaw.alsoCheckBank(questBank),
			magicEssenceUnf.alsoCheckBank(questBank)), usePestleOnClaw);
		goMakePotion.addStep(new Conditions(gorakClaw.alsoCheckBank(questBank), starFlower.alsoCheckBank(questBank)),
			useStarFlowerOnVial);
		goMakePotion.addStep(new Conditions(clawNearby, starflowerOrUnfCorrectlyMade), pickupGorakClaw);
		goMakePotion.addStep(new Conditions(inGorakPlane, starflowerOrUnfCorrectlyMade), killGorak);
		goMakePotion.addStep(starflowerOrUnfCorrectlyMade, goToDir);
		goMakePotion.addStep(starflowerNearby, pickStarFlower);
		goMakePotion.addStep(inStarPlane, waitForStarFlower);
		steps.put(70, goMakePotion);

		// 72, added flower to vial
		steps.put(72, goMakePotion);
		steps.put(73, goMakePotion);

		ConditionalStep goFinish = new ConditionalStep(this, goToHideoutToFinish);
		goFinish.addStep(inHideout, talkToQueen);
		steps.put(75, goFinish);
		steps.put(80, goFinish);

		return steps;
	}

	@Override
	public void setupRequirements()
	{
		dramenOrLunarStaff = new ItemRequirement("Dramen or lunar staff", ItemID.DRAMEN_STAFF);
		dramenOrLunarStaff.addAlternates(ItemID.LUNAR_STAFF);
		dramenOrLunarStaff.setDisplayMatchedItemName(true);
		vialOfWater = new ItemRequirement("Vial of water", ItemID.VIAL_OF_WATER);
		pestleAndMortar = new ItemRequirement("Pestle and mortar", ItemID.PESTLE_AND_MORTAR);

		combatGear = new ItemRequirement("Combat gear", -1, -1);
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, -1);

		fairyCertificate = new ItemRequirement("Nuff's certificate", ItemID.NUFFS_CERTIFICATE);
		fairyCertificate.setTooltip("You can get another from Nuff's room in north west Zanaris");
		starFlower = new ItemRequirement("Star flower", ItemID.STAR_FLOWER);
		gorakClaw = new ItemRequirement("Gorak claws", ItemID.GORAK_CLAWS);
		gorakClawPowder = new ItemRequirement("Gorak claw powder", ItemID.GORAK_CLAW_POWDER);
		magicEssenceUnf = new ItemRequirement("Magic essence (unf)", ItemID.MAGIC_ESSENCE_UNF);
		magicEssence = new ItemRequirement("Magic essence", ItemID.MAGIC_ESSENCE1);
		magicEssence.addAlternates(ItemID.MAGIC_ESSENCE2, ItemID.MAGIC_ESSENCE3, ItemID.MAGIC_ESSENCE4);
		queensSecateurs = new ItemRequirement("Queen's secateurs", ItemID.QUEENS_SECATEURS_9020);
	}

	public void setupZones()
	{
		zanaris = new Zone(new WorldPoint(2368, 4353, 0), new WorldPoint(2495, 4479, 0));
		hideout = new Zone(new WorldPoint(2324, 4420, 0), new WorldPoint(2367, 4468, 0));
		starPlane = new Zone(new WorldPoint(2060, 4806, 0), new WorldPoint(2098, 4862, 0));
		gorakPlane = new Zone(new WorldPoint(3009, 5312, 0), new WorldPoint(3072, 5380, 0));
	}

	public void setupConditions()
	{
		inZanaris = new ZoneRequirement(zanaris);
		inHideout = new ZoneRequirement(hideout);
		inStarPlane = new ZoneRequirement(starPlane);
		inGorakPlane = new ZoneRequirement(gorakPlane);

		thievingReq = new SkillRequirement(Skill.THIEVING, 40);
		farmReq = new SkillRequirement(Skill.FARMING, 49, true);
		herbReq = new SkillRequirement(Skill.HERBLORE, 57, true);

		// Started, 2333 0->1

		// Properly started after talking again: 10->20
		// 1807 1->2
		// 2327 0->1
		// 2332 0->1

		// Entered Nuff room
		// 2339 0->1

		// Talked to godfather for fairy rings
		// quest = 40
		// 2328 0->1
		// 2329 0->2

		hasReadSign = new VarbitRequirement(2338, 4);

		hasInvestigatedCertificate = new VarbitRequirement(2336, 1);

		talkedToGodfather = new VarbitRequirement(QuestVarbits.QUEST_FAIRYTALE_II_CURE_A_QUEEN.getId(), 40, Operation.GREATER_EQUAL);
		addedFlowerCorrectly = new VarbitRequirement(QuestVarbits.QUEST_FAIRYTALE_II_CURE_A_QUEEN.getId(), 72, Operation.GREATER_EQUAL);
		addedClawCorrectly = new VarbitRequirement(QuestVarbits.QUEST_FAIRYTALE_II_CURE_A_QUEEN.getId(), 73, Operation.GREATER_EQUAL);
		// 2331 fairy ring used?

		// 2334?
		starflowerNearby = new NpcCondition(NpcID.STARFLOWER_1857);
		pickedStarFlower = new VarbitRequirement(2330, 1);

		clawNearby = new ItemOnTileRequirement(gorakClaw);
		// 2327 1->2 when searched for certificate

		// 1808 1->2, presumably godfather becoming pickpocketable

		// Killed Gorak, 2382=1

		starflowerOrUnfCorrectlyMade = new Conditions(LogicType.OR,
			starFlower.alsoCheckBank(questBank),
			new Conditions(addedFlowerCorrectly, magicEssenceUnf.alsoCheckBank(questBank))
		);
	}

	public void setupSteps()
	{
		talkToMartin = new NpcStep(this, NpcID.MARTIN_THE_MASTER_GARDENER, new WorldPoint(3078, 3256, 0),
			"Talk to Martin in the Draynor Market.");
		talkToMartin.addDialogSteps("Ask about the quest.");

		waitForMartin = new DetailedQuestStep(this, "Wait 5 minutes.");
		talkToMartinAgain = new NpcStep(this, NpcID.MARTIN_THE_MASTER_GARDENER, new WorldPoint(3078, 3256, 0),
			"Return to Martin in the Draynor Market.");
		talkToMartinAgain.addDialogSteps("Ask about the quest.",
			"I suppose I'd better go and see what the problem is then.");

		takeCertificate = new ObjectStep(this, NullObjectID.NULL_16315, new WorldPoint(2389, 4471, 0),
			"Search the healing certificate in Fairy Nuff's room.");
		studyCertificate = new DetailedQuestStep(this, "Right-click study the certificate.", fairyCertificate.highlighted());
		readSign = new ObjectStep(this, ObjectID.RUNE_TEMPLE_SIGN, new WorldPoint(2409, 4369, 0),
			"Read the sign near the Cosmic Temple in the south of Zanaris.");
		talkToGodfather = new NpcStep(this, NpcID.FAIRY_GODFATHER_5837, new WorldPoint(2447, 4430, 0),
			"Talk to the Fairy Godfather in the Throne Room.");
		talkToGodfather.addDialogSteps("Where is the Fairy Queen?", "Where could she have been taken to?", "Yes, okay.");

		enterZanaris = new ObjectStep(this, ObjectID.DOOR_2406, new WorldPoint(3202, 3169, 0), "Travel to Zanaris.",
			dramenOrLunarStaff.equipped());

		goToHideout = new ObjectStep(this, NullObjectID.NULL_29560, new WorldPoint(2412, 4434, 0),
			"Use the Fairy Rings to travel to A.I.R., D.L.R., D.J.Q. then A.J.S.", dramenOrLunarStaff.equipped(),
			fairyCertificate);
		goToHideoutSurface = new ObjectStep(this, NullObjectID.NULL_29495,
			"Use the Fairy Rings to travel to A.I.R., D.L.R., D.J.Q. then A.J.S.", dramenOrLunarStaff.equipped(),
			fairyCertificate);
		goToHideout.addSubSteps(goToHideoutSurface);
		//varp 817 2->112->133->31 for each destination
		// Arrived in hideout, 4026=1
		talkToNuff = new NpcStep(this, NpcID.FAIRY_NUFF, new WorldPoint(2355, 4455, 0),
			"Talk to Fairy Nuff.");
		returnToZanarisFromBase = new ObjectStep(this, NullObjectID.NULL_29495, new WorldPoint(2328, 4426, 0),
			"Pickpocket the Fairy Godfather in the Zanaris Throne Room.", dramenOrLunarStaff.equipped());
		goToZanarisToPickpocket = new ObjectStep(this, ObjectID.DOOR_2406, new WorldPoint(3202, 3169, 0),
			"Pickpocket the Fairy Godfather in the Zanaris Throne Room.", dramenOrLunarStaff.equipped());
		pickpocketGodfather = new NpcStep(this, NpcID.FAIRY_GODFATHER, new WorldPoint(2447, 4430, 0),
			"Pickpocket the Fairy Godfather in the Zanaris Throne Room.", thievingReq);
		pickpocketGodfather.addSubSteps(returnToZanarisFromBase, goToZanarisToPickpocket);

		goToHideoutWithSec = new ObjectStep(this, NullObjectID.NULL_29560, new WorldPoint(2412, 4434, 0),
			"Use the Fairy Rings to travel to A.I.R., D.L.R., D.J.Q. then A.J.S.", dramenOrLunarStaff.equipped(),
			fairyCertificate, queensSecateurs);
		goToHideoutSurfaceWithSec = new ObjectStep(this, NullObjectID.NULL_29495,
			"Use the Fairy Rings to travel to A.I.R., D.L.R., D.J.Q. then A.J.S.", dramenOrLunarStaff.equipped(),
			fairyCertificate, queensSecateurs);

		giveSecateursToNuff = new NpcStep(this, NpcID.FAIRY_NUFF, new WorldPoint(2355, 4455, 0),
			"Give Fairy Nuff the Queen's Secateurs.", queensSecateurs);

		goToHideoutAfterSec = new ObjectStep(this, NullObjectID.NULL_29560, new WorldPoint(2412, 4434, 0),
			"Use the Fairy Rings to travel to A.I.R., D.L.R., D.J.Q. then A.J.S.", dramenOrLunarStaff.equipped(),
			fairyCertificate);
		goToHideoutSurfaceAfterSec = new ObjectStep(this, NullObjectID.NULL_29495,
			"Use the Fairy Rings to travel to A.I.R., D.L.R., D.J.Q. then A.J.S.", dramenOrLunarStaff.equipped(),
			fairyCertificate);
		talkToNuffAfterSec = new NpcStep(this, NpcID.FAIRY_NUFF, new WorldPoint(2355, 4455, 0),
			"Talk to Fairy Nuff.");

		giveSecateursToNuff.addSubSteps(goToHideoutWithSec, goToHideoutSurfaceWithSec, goToHideoutAfterSec,
			goToHideoutSurfaceAfterSec, talkToNuffAfterSec);

		goToCkp = new ObjectStep(this, NullObjectID.NULL_29560, "Travel to C.K.P with a Fairy Ring. Even if you've " +
			"already made a magic essence (unf), you'll need to make another.", dramenOrLunarStaff.equipped());
		((ObjectStep) goToCkp).addAlternateObjects(NullObjectID.NULL_29495);
		waitForStarFlower = new DetailedQuestStep(this, "Wait for a star flower to appear.");
		pickStarFlower = new NpcStep(this, NpcID.STARFLOWER_1857, new WorldPoint(2070, 4841, 0),
			"Pick a star flower.", true, farmReq);

		goToDir = new ObjectStep(this, NullObjectID.NULL_29560, "Travel to D.I.R. with a Fairy Ring, ready to kill a " +
			"Gorak. Goraks can hit through protection prayers.", dramenOrLunarStaff.equipped(), combatGear);
		((ObjectStep) goToDir).addAlternateObjects(NullObjectID.NULL_29495);
		killGorak = new NpcStep(this, NpcID.GORAK, "Kill Goraks for their claws.", true);
		pickupGorakClaw = new ItemStep(this, "Pickup the gorak's claws.", gorakClaw);

		useStarFlowerOnVial = new DetailedQuestStep(this, "Add the star flower to a vial of water.",
			starFlower.highlighted(), vialOfWater.highlighted(), herbReq);
		usePestleOnClaw = new DetailedQuestStep(this, "Use a pestle and mortar on the gorak claws.",
			pestleAndMortar.highlighted(), gorakClaw.highlighted());
		usePowderOnPotion = new DetailedQuestStep(this, "Add the gorak claw powder on the unfinished potion.",
			gorakClawPowder.highlighted(), magicEssenceUnf.highlighted(), herbReq);

		goToHideout2 = new ObjectStep(this, NullObjectID.NULL_29495,
			"Use the Fairy Rings to travel to A.I.R., D.L.R., D.J.Q. then A.J.S.", dramenOrLunarStaff.equipped(),
			fairyCertificate, magicEssence);
		((ObjectStep) goToHideout2).addAlternateObjects(NullObjectID.NULL_29560);

		usePotionOnQueen = new ObjectStep(this, NullObjectID.NULL_16316, new WorldPoint(2352, 4457, 0),
			"Use the magic essence on the fairy queen.", magicEssence.highlighted());
		usePotionOnQueen.addIcon(ItemID.MAGIC_ESSENCE3);

		goToHideoutToFinish = new ObjectStep(this, NullObjectID.NULL_29495,
			"Use the Fairy Rings to travel to A.I.R., D.L.R., D.J.Q. then A.J.S.", dramenOrLunarStaff.equipped(), fairyCertificate);
		((ObjectStep) goToHideoutToFinish).addAlternateObjects(NullObjectID.NULL_29560);
		talkToQueen = new NpcStep(this, NpcID.FAIRY_QUEEN_1842, new WorldPoint(2354, 4455, 0),
			"Talk to the Fairy Queen.");
		talkToQueen.addSubSteps(goToHideoutToFinish);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(dramenOrLunarStaff, vialOfWater, pestleAndMortar);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Collections.singletonList(combatGear);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		List<Requirement> reqs = new ArrayList<>();
		reqs.add(new QuestRequirement(QuestHelperQuest.FAIRYTALE_I__GROWING_PAINS, QuestState.FINISHED));
		reqs.add(thievingReq);
		reqs.add(farmReq);
		reqs.add(herbReq);
		return reqs;
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Collections.singletonList("At least 1 Gorak (level 145)");
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(2);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Arrays.asList(
				new ExperienceReward(Skill.HERBLORE, 3500),
				new ExperienceReward(Skill.THIEVING, 2500));
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Collections.singletonList(new ItemReward("2 x 2,500 Experience Lamps (Any skill over level 30.)", ItemID.ANTIQUE_LAMP, 2)); //4447 Is placeholder for filter.
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
				new UnlockReward("Access to Fairy Rings."),
				new UnlockReward("Access to Fairy Fixit's Fairy Enhancement Store."));
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();

		allSteps.add(new PanelDetails("Starting off",
			Arrays.asList(talkToMartin, waitForMartin, talkToMartinAgain)));

		allSteps.add(new PanelDetails("Betrayal",
			Arrays.asList(enterZanaris, takeCertificate, studyCertificate, readSign, talkToGodfather, goToHideout,
				talkToNuff, pickpocketGodfather, giveSecateursToNuff), dramenOrLunarStaff, vialOfWater, pestleAndMortar));

		allSteps.add(new PanelDetails("Making a cure", Arrays.asList(goToCkp, waitForStarFlower, pickStarFlower,
			goToDir, killGorak, pickupGorakClaw, useStarFlowerOnVial, usePestleOnClaw, usePowderOnPotion,
			goToHideout2, usePotionOnQueen, talkToQueen), dramenOrLunarStaff, vialOfWater, pestleAndMortar));

		return allSteps;
	}
}
