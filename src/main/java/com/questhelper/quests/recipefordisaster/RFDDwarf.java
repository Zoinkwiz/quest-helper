/*
 * Copyright (c) 2021, Zoinkwiz
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
package com.questhelper.quests.recipefordisaster;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.QuestVarbits;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
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

import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.RECIPE_FOR_DISASTER_DWARF
)
public class RFDDwarf extends BasicQuestHelper
{
	ItemRequirement coins320, milk, flour, egg, bowlOfWater, asgarniaAle4, iceGloves, rockCake, rockCakeHot,
		teleportFalador2, teleportLumbridge, coin, asgarnianAle, asgoldianAle, asgoldianAle4, coins100,
		rockCakeHighlighted;

	Requirement inDiningRoom, inTunnel, learnedHowToMakeAle, givenAle, has4AleOrGivenAle;

	QuestStep enterDiningRoom, inspectDwarf, talkToKaylee, makeAle, enterTunnels, talkToOldDwarf, talkToOldDwarfMore,
		talkToOldDwarfWithIngredients, pickUpRockCake, coolRockCake, coolRockCakeSidebar, enterDiningRoomAgain,
		useRockCakeOnDwarf;

	//Zones
	Zone diningRoom, tunnel;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		ConditionalStep goInspectDwarf = new ConditionalStep(this, enterDiningRoom);
		goInspectDwarf.addStep(inDiningRoom, inspectDwarf);
		steps.put(0, goInspectDwarf);

		ConditionalStep goGiveAle = new ConditionalStep(this, talkToKaylee);
		goGiveAle.addStep(new Conditions(has4AleOrGivenAle, inTunnel), talkToOldDwarf);
		goGiveAle.addStep(new Conditions(has4AleOrGivenAle), enterTunnels);
		goGiveAle.addStep(learnedHowToMakeAle, makeAle);
		steps.put(10, goGiveAle);
		steps.put(20, goGiveAle);

		ConditionalStep haveCakeMade = new ConditionalStep(this, enterTunnels);
		haveCakeMade.addStep(inTunnel, talkToOldDwarfMore);
		steps.put(30, haveCakeMade);

		ConditionalStep haveCakeMadeP2 = new ConditionalStep(this, enterTunnels);
		haveCakeMadeP2.addStep(inTunnel, talkToOldDwarfWithIngredients);
		steps.put(40, haveCakeMadeP2);

		ConditionalStep giveCakeToDwarf = new ConditionalStep(this, pickUpRockCake);
		giveCakeToDwarf.addStep(new Conditions(rockCake, inDiningRoom), useRockCakeOnDwarf);
		giveCakeToDwarf.addStep(rockCake.alsoCheckBank(questBank), enterDiningRoomAgain);
		giveCakeToDwarf.addStep(rockCakeHot, coolRockCake);
		steps.put(50, giveCakeToDwarf);

		return steps;
	}

	@Override
	public void setupRequirements()
	{
		coins320 = new ItemRequirement("Coins", ItemCollections.COINS, 320);
		coins320.setTooltip("You only need 120 if you wear a Ring of Charos(a)");
		coins100 = new ItemRequirement("Coins", ItemCollections.COINS, 100);
		milk = new ItemRequirement("Bucket of milk", ItemID.BUCKET_OF_MILK);
		milk.setTooltip("You can buy this from the  Culinaromancer's Chest");
		flour = new ItemRequirement("Pot of flour", ItemID.POT_OF_FLOUR);
		flour.setTooltip("You can buy this from the  Culinaromancer's Chest");
		egg = new ItemRequirement("Egg", ItemID.EGG);
		egg.setTooltip("You can buy this from the  Culinaromancer's Chest");
		bowlOfWater = new ItemRequirement("Bowl of water", ItemID.BOWL_OF_WATER);
		bowlOfWater.setTooltip("You can find a bowl in Lumbridge Castle's Basement and fill it in the nearby sink");
		asgarniaAle4 = new ItemRequirement("Asgarnian ale", ItemID.ASGARNIAN_ALE, 4);
		asgarniaAle4.setTooltip("You can buy them for 3 coins each from Kaylee during the quest");
		iceGloves = new ItemRequirement("Ice gloves/smiths gloves(i)/normal gloves/telekinetic grab", ItemID.ICE_GLOVES);
		iceGloves.addAlternates(ItemID.LEATHER_GLOVES, ItemID.SMITHS_GLOVES_I);
		iceGloves.setTooltip("You can use normal gloves/telekenetic grab instead, but you'll then need to kill an Ice " +
			"Fiend");
		rockCake = new ItemRequirement("Dwarven rock cake", ItemID.DWARVEN_ROCK_CAKE_7510);
		rockCakeHighlighted = new ItemRequirement("Dwarven rock cake", ItemID.DWARVEN_ROCK_CAKE_7510);
		rockCakeHighlighted.setHighlightInInventory(true);
		rockCakeHot = new ItemRequirement("Dwarven rock cake", ItemID.DWARVEN_ROCK_CAKE);
		teleportFalador2 = new ItemRequirement("Teleport to Falador", ItemID.FALADOR_TELEPORT, 2);
		teleportLumbridge = new ItemRequirement("Teleport to Lumbridge", ItemID.LUMBRIDGE_TELEPORT);

		coin = new ItemRequirement("Coin", ItemCollections.COINS);
		coin.setHighlightInInventory(true);

		asgarnianAle = new ItemRequirement("Asgarnian ale", ItemID.ASGARNIAN_ALE);
		asgarnianAle.setHighlightInInventory(true);

		asgoldianAle = new ItemRequirement("Asgoldian ale", ItemID.ASGOLDIAN_ALE);
		asgoldianAle.setHighlightInInventory(true);

		asgoldianAle4 = new ItemRequirement("Asgoldian ale", ItemID.ASGOLDIAN_ALE, 4);
	}

	public void loadZones()
	{
		diningRoom = new Zone(new WorldPoint(1856, 5313, 0), new WorldPoint(1870, 5333, 0));
		tunnel = new Zone(new WorldPoint(2815, 9859, 0), new WorldPoint(2879, 9885, 0));
	}

	public void setupConditions()
	{
		inDiningRoom = new ZoneRequirement(diningRoom);
		inTunnel = new ZoneRequirement(tunnel);

		learnedHowToMakeAle = new VarbitRequirement(1891, 1);
		givenAle = new VarbitRequirement(1893, 1);
		has4AleOrGivenAle = new Conditions(LogicType.OR, asgoldianAle4, givenAle);
	}

	public void setupSteps()
	{
		enterDiningRoom = new ObjectStep(this, ObjectID.LARGE_DOOR_12349, new WorldPoint(3213, 3221, 0), "Go inspect " +
			"the dwarf.");
		inspectDwarf = new ObjectStep(this, ObjectID.DWARF_12330, new WorldPoint(1862, 5321, 0), "Inspect the dwarf.");
		inspectDwarf.addSubSteps(enterDiningRoom);

		talkToKaylee = new NpcStep(this, NpcID.KAYLEE, new WorldPoint(2957, 3371, 0), "Talk to Kaylee in the pub in " +
			"Falador.", coins320);
		talkToKaylee.addDialogSteps("What can you tell me about dwarves and ale?", "I could offer you some in return," +
			" how about 200 gold?");

		makeAle = new DetailedQuestStep(this, "Add coins to asgarnian ale to make 4 asgoldian ale. You can buy ale " +
			"from Kaylee for 3gp " +
			"each."
			, coin, asgarnianAle);
		enterTunnels = new ObjectStep(this, ObjectID.STAIRS_57, new WorldPoint(2877, 3481, 0), "Enter the tunnel " +
			"under White Wolf Mountain.", asgoldianAle4, milk, flour, egg, bowlOfWater, iceGloves);
		talkToOldDwarf = new GetRohakDrunk(this, asgoldianAle4);
		talkToOldDwarfMore = new NpcStep(this, NpcID.ROHAK_4812, new WorldPoint(2865, 9876, 0),
			"Talk to Rohak more.", coins100, milk, flour, egg, bowlOfWater);

		talkToOldDwarfWithIngredients = new NpcStep(this, NpcID.ROHAK_4812, new WorldPoint(2865, 9876, 0),
			"Talk to Rohak more.", milk, flour, egg, bowlOfWater);
		talkToOldDwarfMore.addSubSteps(talkToOldDwarfWithIngredients);

		pickUpRockCake = new ItemStep(this, "Pick up the dwarven rock cake. If you have ice gloves, wear them to pick" +
			" it up. Otherwise, wear other gloves or telegrab it. If it despawns you'll need to bring Rohak more " +
			"ingredients.",	rockCakeHot);
		coolRockCake = new NpcStep(this, NpcID.ICEFIEND, new WorldPoint(3008, 3471, 0),
			"Kill an icefiend to cool the rock cake.", rockCakeHot);
		coolRockCakeSidebar = new NpcStep(this, NpcID.ICEFIEND, new WorldPoint(3008, 3471, 0),
			"If you didn't pick up the rock cake with ice gloves, kill an icefiend to cool the rock cake.",
			rockCakeHot);
		coolRockCakeSidebar.addSubSteps(coolRockCakeSidebar);
		enterDiningRoomAgain = new ObjectStep(this, ObjectID.LARGE_DOOR_12349, new WorldPoint(3213, 3221, 0),
			"Go use the dwarven rock cake on the dwarf.");
		useRockCakeOnDwarf = new ObjectStep(this, ObjectID.DWARF_12330, new WorldPoint(1862, 5321, 0),
			"Use the dwarven rock cake on the dwarf.", rockCakeHighlighted);
		useRockCakeOnDwarf.addIcon(ItemID.DWARVEN_ROCK_CAKE_7510);
		useRockCakeOnDwarf.addSubSteps(enterDiningRoomAgain);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(coins320, milk, flour, egg, bowlOfWater, asgarniaAle4,
			iceGloves);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(teleportFalador2, teleportLumbridge);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Collections.singletonList("Icefiend if you don't have Ice Gloves");
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> reqs = new ArrayList<>();
		reqs.add(new VarbitRequirement(QuestVarbits.QUEST_RECIPE_FOR_DISASTER.getId(), Operation.GREATER_EQUAL, 3,
			"Started Recipe for Disaster"));
		reqs.add(new QuestRequirement(QuestHelperQuest.FISHING_CONTEST, QuestState.FINISHED));

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
		return Arrays.asList(
				new ExperienceReward(Skill.COOKING, 1000),
				new ExperienceReward(Skill.SLAYER, 1000));
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Collections.singletonList(new ItemReward("A Dwarven Rock Cake", ItemID.DWARVEN_ROCK_CAKE, 1));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Collections.singletonList(new UnlockReward("Increased access to the Culinaromancer's Chest"));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Saving the Dwarf", Arrays.asList(inspectDwarf, talkToKaylee,
			makeAle, enterTunnels, talkToOldDwarf, talkToOldDwarfMore, pickUpRockCake, coolRockCakeSidebar,
			useRockCakeOnDwarf),
			coins320, milk, flour, egg, bowlOfWater, asgarniaAle4, iceGloves));
		return allSteps;
	}

	@Override
	public QuestState getState(Client client)
	{
		int questState = client.getVarbitValue(QuestVarbits.QUEST_RECIPE_FOR_DISASTER_DWARF.getId());
		if (questState == 0)
		{
			return QuestState.NOT_STARTED;
		}

		if (questState < 60)
		{
			return QuestState.IN_PROGRESS;
		}

		return QuestState.FINISHED;
	}

	@Override
	public boolean isCompleted()
	{
		return (getQuest().getVar(client) >= 60 || client.getVarbitValue(QuestVarbits.QUEST_RECIPE_FOR_DISASTER.getId()) < 3);
	}
}
