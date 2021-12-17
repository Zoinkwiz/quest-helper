/*
 * Copyright (c) 2021, Obasill <https://github.com/Obasill>
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
package com.questhelper.achievementdiaries.ardougne;

import com.questhelper.ItemCollections;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.panel.PanelDetails;
import com.questhelper.steps.QuestStep;

@QuestDescriptor(
	quest = QuestHelperQuest.ARDOUGNE_HARD
)
public class ArdougneHard extends ComplexStateQuestHelper
{
	// Items required
	ItemRequirement rechargableJewelry, earthRune, lawRune, rope, smallFishingNet, palmSap, spade,
		rake, seedDib, poisonIvySeed, mithBar, hammer, coins, shieldLeft, shieldRight, highEss, deathAccess, greeGree,
		crystalTrink, newKey, mournerBoots, mournerGloves, mournerCloak, mournerTop, mournerTrousers, mournersOutfit,
		gasMask, compost, papaya;

	// Items recommended
	ItemRequirement papayaOrCompost, lockpick;

	// Quests required
	Requirement legendsQuest, monkeyMadness, watchtower, mourningsEndII, redAtDoor, redAtAltar;

	Requirement notRecharge, notMagicGuild, notStealChest, notMonkeyCage, notTPWatchtower, notRedSally, notPalmTree,
		notPoisonIvy, notMithPlate, notYanPOH, notDragSquare, notDeathRune, notYanHouse, notYanHouse2;

	QuestStep claimReward, recharge, stealChest, monkeyCage, tPWatchtower, redSally, palmTree, poisonIvy, mithPlate,
		yanPOH, dragSquare, deathRune, moveHouse, moveToCastle, deathMoveUp1, deathMoveUp2, deathMoveDown1,
		deathMoveDown0, turnKeyMirror, deathAltar, enterMournerHQ, enterMournerBasement;

	ObjectStep magicGuild;

	Zone castle, death0, death1, death2, death12, death02, death, mournerHQ, mournerHQ2;

	ZoneRequirement inCastle, inDeath0, inDeath1, inDeath2, inDeath12, inDeath02, inDeath, inMournerHQ;

	@Override
	public QuestStep loadStep()
	{
		loadZones();
		setupRequirements();
		setupSteps();

		ConditionalStep doHard = new ConditionalStep(this, claimReward);
		doHard.addStep(notTPWatchtower, tPWatchtower);
		doHard.addStep(new Conditions(notYanPOH, notYanHouse2), yanPOH);
		doHard.addStep(notYanPOH, moveHouse);
		doHard.addStep(notMagicGuild, magicGuild);
		doHard.addStep(notMithPlate, mithPlate);
		doHard.addStep(notRedSally, redSally);
		doHard.addStep(notRecharge, recharge);
		doHard.addStep(notMonkeyCage, monkeyCage);
		doHard.addStep(new Conditions(notStealChest, inCastle), stealChest);
		doHard.addStep(notStealChest, moveToCastle);
		doHard.addStep(new Conditions(notDeathRune, redAtAltar, inDeath02), deathRune);
		doHard.addStep(new Conditions(notDeathRune, redAtDoor, inDeath02), turnKeyMirror);
		doHard.addStep(new Conditions(notDeathRune, inDeath12), deathMoveDown0);
		doHard.addStep(new Conditions(notDeathRune, inDeath2), deathMoveDown1);
		doHard.addStep(new Conditions(notDeathRune, inDeath1), deathMoveUp2);
		doHard.addStep(new Conditions(notDeathRune, inDeath0), deathMoveUp1);
		doHard.addStep(new Conditions(notDeathRune, inMournerHQ), enterMournerBasement);
		doHard.addStep(notDeathRune, enterMournerHQ);
		doHard.addStep(notPalmTree, palmTree);
		doHard.addStep(notPoisonIvy, poisonIvy);

		return doHard;
	}

	public void setupRequirements()
	{
		notRecharge = new VarplayerRequirement(1196, false, 26);
		notMagicGuild = new VarplayerRequirement(1196, false, 27);
		notStealChest = new VarplayerRequirement(1196, false, 28);
		notMonkeyCage = new VarplayerRequirement(1196, false, 29);
		notTPWatchtower = new VarplayerRequirement(1196, false, 30);
		notRedSally = new VarplayerRequirement(1196, false, 31);
		notPalmTree = new VarplayerRequirement(1197, false, 0);
		notPoisonIvy = new VarplayerRequirement(1197, false, 1);
		notMithPlate = new VarplayerRequirement(1197, false, 2);
		notYanPOH = new VarplayerRequirement(1197, false, 3);
		notDragSquare = new VarplayerRequirement(1197, false, 4);
		notDeathRune = new VarplayerRequirement(1197, false, 5);

		notYanHouse = new VarbitRequirement(2187, 6, Operation.NOT_EQUAL);
		notYanHouse2 = new VarbitRequirement(2187, 6);
		redAtDoor = new VarbitRequirement(1249, 1);
		redAtAltar = new VarbitRequirement(1250, 1);

		earthRune = new ItemRequirement("Earth rune", ItemID.EARTH_RUNE).showConditioned(notTPWatchtower);
		lawRune = new ItemRequirement("Law rune", ItemID.LAW_RUNE).showConditioned(notTPWatchtower);
		coins = new ItemRequirement("Coins", ItemCollections.getCoins())
			.showConditioned(new Conditions(notYanHouse, notYanPOH));
		mithBar = new ItemRequirement("Mithril bar", ItemID.MITHRIL_BAR).showConditioned(notMithPlate);
		hammer = new ItemRequirement("Hammer", ItemID.HAMMER)
			.showConditioned(new Conditions(LogicType.OR, notMithPlate, notDragSquare));
		rope = new ItemRequirement("Rope", ItemID.ROPE).showConditioned(notRedSally);
		smallFishingNet = new ItemRequirement("Small fishing net", ItemID.SMALL_FISHING_NET).showConditioned(notRedSally);
		rechargableJewelry = new ItemRequirement("Skills necklace or Combat bracelet under 4 charges",
			ItemCollections.getRechargeableNeckBracelet()).showConditioned(notRecharge);
		greeGree = new ItemRequirement("Karamja monkey greegree", ItemID.KARAMJAN_MONKEY_GREEGREE)
			.showConditioned(notMonkeyCage);
		lockpick = new ItemRequirement("Lockpick", ItemID.LOCKPICK).showConditioned(notStealChest);
		shieldLeft = new ItemRequirement("Shield left half", ItemID.SHIELD_LEFT_HALF).showConditioned(notDragSquare);
		shieldRight = new ItemRequirement("Shield right half", ItemID.SHIELD_RIGHT_HALF).showConditioned(notDragSquare);
		deathAccess = new ItemRequirement("Access to Death altar, or travel through abyss",
			ItemCollections.getDeathAltar()).showConditioned(notDeathRune);
		crystalTrink = new ItemRequirement("Crystal Trinket", ItemID.CRYSTAL_TRINKET).showConditioned(notDeathRune);
		highEss = new ItemRequirement("Pure or Daeyalt essence", ItemCollections.getEssenceHigh())
			.showConditioned(notDeathRune);
		newKey = new ItemRequirement("New key", ItemID.NEW_KEY).showConditioned(notDeathRune);
		newKey.setTooltip("Another can be found on the desk in the south-east room of the Mourner HQ basement.");
		mournerBoots = new ItemRequirement("Mourner boots", ItemID.MOURNER_BOOTS);
		gasMask = new ItemRequirement("Gas mask", ItemID.GAS_MASK);
		mournerGloves = new ItemRequirement("Mourner gloves", ItemID.MOURNER_GLOVES);
		mournerCloak = new ItemRequirement("Mourner cloak", ItemID.MOURNER_CLOAK);
		mournerTop = new ItemRequirement("Mourner top", ItemID.MOURNER_TOP);
		mournerTrousers = new ItemRequirement("Mourner trousers", ItemID.MOURNER_TROUSERS);
		mournersOutfit = new ItemRequirements("Full mourners' outfit", gasMask, mournerTop, mournerTrousers,
			mournerCloak, mournerBoots, mournerGloves);
		mournersOutfit.setTooltip("Another set can be obtained at the north entrance to Arandar.");
		rake = new ItemRequirement("Rake", ItemID.RAKE)
			.showConditioned(new Conditions(LogicType.OR, notPalmTree, notPoisonIvy));
		seedDib = new ItemRequirement("Seed dibber", ItemID.SEED_DIBBER)
			.showConditioned(new Conditions(LogicType.OR, notPalmTree, notPoisonIvy));
		spade = new ItemRequirement("Spade", ItemID.SPADE).showConditioned(notPalmTree);
		poisonIvySeed = new ItemRequirement("Poision ivy seed", ItemID.POISON_IVY_SEED).showConditioned(notPoisonIvy);
		palmSap = new ItemRequirement("Palm tree sapling", ItemID.PALM_SAPLING).showConditioned(notPalmTree);
		papaya = new ItemRequirement("Papaya fruit", ItemID.PAPAYA_FRUIT).showConditioned(notPalmTree);
		compost = new ItemRequirement("Compost", ItemCollections.getCompost()).showConditioned(notPalmTree);
		papayaOrCompost = new ItemRequirements(LogicType.OR, "15 Papaya fruit or Compost", papaya.quantity(15), compost)
			.showConditioned(notPalmTree);

		inCastle = new ZoneRequirement(castle);
		inDeath0 = new ZoneRequirement(death0);
		inDeath1 = new ZoneRequirement(death1);
		inDeath2 = new ZoneRequirement(death2);
		inDeath12 = new ZoneRequirement(death12);
		inDeath02 = new ZoneRequirement(death02);
		inDeath = new ZoneRequirement(death);
		inMournerHQ = new ZoneRequirement(mournerHQ, mournerHQ2);

		legendsQuest = new QuestRequirement(QuestHelperQuest.LEGENDS_QUEST, QuestState.FINISHED);
		monkeyMadness = new QuestRequirement(QuestHelperQuest.MONKEY_MADNESS_I, QuestState.IN_PROGRESS);
		mourningsEndII = new QuestRequirement(QuestHelperQuest.MOURNINGS_END_PART_II, QuestState.FINISHED);
		watchtower = new QuestRequirement(QuestHelperQuest.WATCHTOWER, QuestState.FINISHED);
	}

	public void loadZones()
	{
		castle = new Zone(new WorldPoint(2568, 3310, 1), new WorldPoint(2591, 3282, 1));
		death0 = new Zone(new WorldPoint(1855, 4681, 0), new WorldPoint(2047, 4602, 0));
		death1 = new Zone(new WorldPoint(1855, 4681, 1), new WorldPoint(2047, 4602, 1));
		death2 = new Zone(new WorldPoint(1855, 4681, 2), new WorldPoint(2047, 4602, 2));
		death12 = new Zone(new WorldPoint(1886, 4646, 1), new WorldPoint(1896, 4632, 1));
		death02 = new Zone(new WorldPoint(1850, 4654, 0), new WorldPoint(1896, 4624, 0));
		death = new Zone(new WorldPoint(2183, 4860, 0), new WorldPoint(2232, 4815, 0));
		mournerHQ = new Zone(new WorldPoint(2547, 3321, 0), new WorldPoint(2555, 3327, 0));
		mournerHQ2 = new Zone(new WorldPoint(2542, 3324, 0), new WorldPoint(2546, 3327, 0));
	}

	public void setupSteps()
	{
		tPWatchtower = new DetailedQuestStep(this, "Teleport to the Watchtower.", earthRune.quantity(2),
			lawRune.quantity(2));

		moveHouse = new NpcStep(this, NpcID.ESTATE_AGENT, new WorldPoint(2638, 3293, 0),
			"Talk to an Estate agent and relocate your house to Yanille.", coins.quantity(25000));
		moveHouse.addDialogStep("Can you move my house please?");
		yanPOH = new ObjectStep(this, 15482, new WorldPoint(2544, 3098, 0),
			"Enter your house from the portal in Yanille.");

		magicGuild = new ObjectStep(this, ObjectID.MAGIC_GUILD_DOOR, new WorldPoint(2584, 3088, 0),
			"Enter the Magic Guild.", true);
		magicGuild.addAlternateObjects(ObjectID.MAGIC_GUILD_DOOR_1733);

		mithPlate = new ObjectStep(this, ObjectID.ANVIL_2097, new WorldPoint(2613, 3081, 0),
			"Smith a Mithril platebody in Yanille.", mithBar.quantity(5), hammer);

		redSally = new ObjectStep(this, ObjectID.YOUNG_TREE_8990, new WorldPoint(2474, 3239, 0),
			"Catch a Red Salamander.", true, rope, smallFishingNet);

		recharge = new ObjectStep(this, 2638, new WorldPoint(2729, 3378, 0),
			"Recharge some jewellery at the Totem pole in the Legends' Guild.", rechargableJewelry);

		monkeyCage = new NpcStep(this, NpcID.MONKEY_MINDER, new WorldPoint(2607, 3277, 0),
			"Talk to a Monkey minder with the Karamja monkey greegree equipped.", greeGree.equipped());

		moveToCastle = new ObjectStep(this, ObjectID.STAIRCASE_15645, new WorldPoint(2572, 3296, 0),
			"Climb the stairs in the Ardougne Castle.", lockpick);
		stealChest = new ObjectStep(this, ObjectID.CHEST_11739, new WorldPoint(2588, 3291, 1),
			"Attempt to steal from a chest in Ardougne Castle.", lockpick);

		dragSquare = new ObjectStep(this, ObjectID.ANVIL_2097, new WorldPoint(2501, 3330, 0),
			"Smith a Dragon sq shield in West Ardougne.", shieldLeft, shieldRight, hammer);

		enterMournerHQ = new ObjectStep(this, ObjectID.DOOR_2036, new WorldPoint(2551, 3320, 0),
			"Enter the Mourner HQ, or enter the Death Altar via the Abyss.", deathAccess, highEss, crystalTrink, newKey,
			gasMask.equipped(),	mournerTop.equipped(), mournerTrousers.equipped(),
			mournerCloak.equipped(), mournerGloves.equipped(), mournerBoots.equipped());
		enterMournerBasement = new ObjectStep(this, ObjectID.TRAPDOOR_8783, new WorldPoint(2542, 3327, 0),
			"Enter the Mourner HQ basement.", deathAccess, highEss, crystalTrink, newKey);
		deathMoveUp1 = new ObjectStep(this, ObjectID.STAIRCASE_10015, new WorldPoint(1903, 4639, 0),
			"Go up the stairs to the first floor.", deathAccess, highEss, crystalTrink);
		deathMoveUp2 = new ObjectStep(this, ObjectID.STAIRCASE_10017, new WorldPoint(1894, 4620, 1),
			"Go up the south staircase to the second floor.", deathAccess, highEss, crystalTrink);
		deathMoveDown1 = new ObjectStep(this, ObjectID.STAIRCASE_10016, new WorldPoint(1891, 4636, 2),
			"Go down to the first floor.", deathAccess, highEss, crystalTrink);
		deathMoveDown0 = new ObjectStep(this, ObjectID.STAIRCASE_10016, new WorldPoint(1888, 4639, 1),
			"Go down to the ground floor.", deathAccess, highEss, crystalTrink);
		turnKeyMirror = new ObjectStep(this, NullObjectID.NULL_9939, new WorldPoint(1881, 4639, 0),
			"Enter the central area, and turn the pillar's mirror west.");
		deathAltar = new ObjectStep(this, 34823, new WorldPoint(1860, 4639, 0),
			"Enter the Death altar.", deathAccess, highEss, crystalTrink);
		deathAltar.addIcon(ItemID.DEATH_TALISMAN);
		deathRune = new ObjectStep(this, ObjectID.ALTAR_34770, new WorldPoint(2205, 4836, 0),
			"Craft some death runes from essence."
				+ "TURN THE MIDDLE PILLAR TO POINT BACK EAST OR YOU'LL HAVE TO RETURN VIA THE UNDERGROUND PASS.", highEss);

		poisonIvy = new ObjectStep(this, 7580, new WorldPoint(2618, 3226, 0),
			"Plant and harvest poison ivy in the Ardougne Monastery bush patch.", rake, seedDib, poisonIvySeed);

		palmTree = new ObjectStep(this, 7963, new WorldPoint(2490, 3180, 0),
			"Check the health of a palm tree near Tree Gnome Village", spade, rake, palmSap);

		claimReward = new NpcStep(this, NpcID.TWOPINTS, new WorldPoint(2574, 3323, 0),
			"Talk to Two-pints in the Flying Horse Inn at East Ardougne to claim your reward!");
		claimReward.addDialogStep("I have a question about my Achievement Diary.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(coins.quantity(25000), earthRune.quantity(2), lawRune.quantity(2),
			mithBar.quantity(5), hammer, rope, smallFishingNet, rechargableJewelry, greeGree, lockpick, shieldLeft,
			shieldRight, newKey, crystalTrink, mournersOutfit, highEss, deathAccess, spade, rake, palmSap, seedDib,
			poisonIvySeed);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Collections.singletonList(papayaOrCompost);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		List<Requirement> reqs = new ArrayList<>();
		reqs.add(new SkillRequirement(Skill.CONSTRUCTION, 50, false));
		reqs.add(new SkillRequirement(Skill.FARMING, 70, true));
		reqs.add(new SkillRequirement(Skill.HUNTER, 59));
		reqs.add(new SkillRequirement(Skill.MAGIC, 66));
		reqs.add(new SkillRequirement(Skill.RUNECRAFT, 65, true));
		reqs.add(new SkillRequirement(Skill.SMITHING, 68));
		reqs.add(new SkillRequirement(Skill.THIEVING, 72));

		reqs.add(monkeyMadness);
		reqs.add(legendsQuest);
		reqs.add(mourningsEndII);
		reqs.add(watchtower);

		return reqs;
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
			new ItemReward("Ardougne Cloak 3", ItemID.ARDOUGNE_CLOAK_3),
			new ItemReward("15,000 Exp. Lamp (Any skill over 50)", ItemID.ANTIQUE_LAMP)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
			new UnlockReward("Five daily teleports to Ardougne farm patch with the Ardougne cloak 3"),
			new UnlockReward("150 free noted pure essence every day from Wizard Cromperty"),
			new UnlockReward("Ability to toggle Watchtower Teleport to the centre of Yanille"),
			new UnlockReward("10% increased chance of succeeding when pickpocketing around Gielinor")
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		PanelDetails watchtowerSteps = new PanelDetails("Teleport to Watchtower",
			Collections.singletonList(tPWatchtower), new SkillRequirement(Skill.MAGIC, 58), watchtower,
			earthRune.quantity(2), lawRune.quantity(2));
		watchtowerSteps.setDisplayCondition(notTPWatchtower);
		allSteps.add(watchtowerSteps);

		PanelDetails yan2Steps = new PanelDetails("Yanille POH", Arrays.asList(moveHouse, yanPOH),
			new SkillRequirement(Skill.CONSTRUCTION, 50, false), coins.quantity(25000));
		yan2Steps.setDisplayCondition(new Conditions(notYanHouse, notYanPOH));
		allSteps.add(yan2Steps);

		PanelDetails yanSteps = new PanelDetails("Yanille POH", Collections.singletonList(yanPOH));
		yanSteps.setDisplayCondition(new Conditions(notYanHouse2, notYanPOH));
		allSteps.add(yanSteps);

		PanelDetails mgSteps = new PanelDetails("Magic Guild", Collections.singletonList(magicGuild),
			new SkillRequirement(Skill.MAGIC, 66));
		mgSteps.setDisplayCondition(notMagicGuild);
		allSteps.add(mgSteps);

		PanelDetails plateSteps = new PanelDetails("Mithril Platebody", Collections.singletonList(mithPlate),
			new SkillRequirement(Skill.SMITHING, 68), mithBar.quantity(5), hammer);
		plateSteps.setDisplayCondition(notMithPlate);
		allSteps.add(plateSteps);

		PanelDetails sallySteps = new PanelDetails("Red Salamander", Collections.singletonList(redSally),
			new SkillRequirement(Skill.HUNTER, 59), rope, smallFishingNet);
		sallySteps.setDisplayCondition(notRedSally);
		allSteps.add(sallySteps);

		PanelDetails rechargeSteps = new PanelDetails("Recharge Jewelry", Collections.singletonList(recharge),
			legendsQuest, rechargableJewelry);
		rechargeSteps.setDisplayCondition(notRecharge);
		allSteps.add(rechargeSteps);

		PanelDetails monkeySteps = new PanelDetails("Monkey in a Cage", Collections.singletonList(monkeyCage),
			monkeyMadness, greeGree);
		monkeySteps.setDisplayCondition(notMonkeyCage);
		allSteps.add(monkeySteps);

		PanelDetails chestSteps = new PanelDetails("Stealing from Ardougne Royalty", Arrays.asList(moveToCastle,
			stealChest), new SkillRequirement(Skill.THIEVING, 72), lockpick);
		chestSteps.setDisplayCondition(notStealChest);
		allSteps.add(chestSteps);

		PanelDetails dragSteps = new PanelDetails("Smith Dragon Square in West Ardougne",
			Collections.singletonList(dragSquare), new SkillRequirement(Skill.SMITHING, 60), shieldLeft, shieldRight,
			hammer);
		dragSteps.setDisplayCondition(notDragSquare);
		allSteps.add(dragSteps);

		PanelDetails deathSteps = new PanelDetails("Craft Death Runes", Arrays.asList(enterMournerHQ,
			enterMournerBasement, deathMoveUp1, deathMoveUp2, deathMoveDown1, deathMoveDown0, deathAltar, deathRune),
			new SkillRequirement(Skill.RUNECRAFT, 65, true), mourningsEndII, newKey, crystalTrink,
			mournersOutfit, highEss, deathAccess);
		deathSteps.setDisplayCondition(notDeathRune);
		allSteps.add(deathSteps);

		PanelDetails palmSteps = new PanelDetails("Tree Gnome Village Palm Tree", Collections.singletonList(palmTree),
			new SkillRequirement(Skill.FARMING, 70, true), spade, rake, palmSap);
		palmSteps.setDisplayCondition(notPalmTree);
		allSteps.add(palmSteps);

		PanelDetails ivySteps = new PanelDetails("Monastery Poison Ivy", Collections.singletonList(poisonIvy),
			new SkillRequirement(Skill.FARMING, 68, true), seedDib, rake, poisonIvySeed);
		ivySteps.setDisplayCondition(notPoisonIvy);
		allSteps.add(ivySteps);

		allSteps.add(new PanelDetails("Finishing off", Collections.singletonList(claimReward)));

		return allSteps;
	}
}
