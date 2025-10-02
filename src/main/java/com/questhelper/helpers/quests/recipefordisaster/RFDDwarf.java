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
package com.questhelper.helpers.quests.recipefordisaster;

import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.questinfo.QuestVarbits;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.*;
import net.runelite.api.Client;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;

import java.util.*;

public class RFDDwarf extends BasicQuestHelper
{
	ItemRequirement coins320, milk, flour, egg, bowlOfWater, asgarniaAle4, iceGloves, rockCake, rockCakeHot,
		coin, asgarnianAle, asgoldianAle, asgoldianAle4, coins100,
		rockCakeHighlighted;

	ItemRequirement taverleyTeleport, teleportFalador, teleportLumbridge;

	Requirement inDiningRoom, inTunnel, learnedHowToMakeAle, givenAle, has4AleOrGivenAle;

	DetailedQuestStep enterDiningRoom, inspectDwarf, talkToKaylee, makeAle, enterTunnels, talkToOldDwarf, talkToOldDwarfMore,
		talkToOldDwarfWithIngredients, pickUpRockCake, coolRockCake, coolRockCakeSidebar, enterDiningRoomAgain,
		useRockCakeOnDwarf;

	//Zones
	Zone diningRoom, tunnel;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
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
		giveCakeToDwarf.addStep(rockCake.alsoCheckBank(), enterDiningRoomAgain);
		giveCakeToDwarf.addStep(rockCakeHot, coolRockCake);
		steps.put(50, giveCakeToDwarf);

		return steps;
	}

	@Override
	protected void setupRequirements()
	{
		coins320 = new ItemRequirement("Coins", ItemCollections.COINS, 320);
		coins320.setTooltip("You only need 120 if you wear a Ring of Charos(a)");
		coins100 = new ItemRequirement("Coins", ItemCollections.COINS, 100);
		milk = new ItemRequirement("Bucket of milk", ItemID.BUCKET_MILK);
		milk.setTooltip("You can buy this from the  Culinaromancer's Chest");
		flour = new ItemRequirement("Pot of flour", ItemID.POT_FLOUR);
		flour.setTooltip("You can buy this from the  Culinaromancer's Chest");
		egg = new ItemRequirement("Egg", ItemID.EGG);
		egg.setTooltip("You can buy this from the  Culinaromancer's Chest");
		bowlOfWater = new ItemRequirement("Bowl of water", ItemID.BOWL_WATER);
		bowlOfWater.setTooltip("You can find a bowl in Lumbridge Castle's Basement and fill it in the nearby sink");
		asgarniaAle4 = new ItemRequirement("Asgarnian ale", ItemID.ASGARNIAN_ALE, 4);
		asgarniaAle4.setTooltip("You can buy them for 3 coins each from Kaylee during the quest");
		iceGloves = new ItemRequirement("Ice gloves/smiths gloves(i)/normal gloves/telekinetic grab", ItemID.ICE_GLOVES).isNotConsumed();
		iceGloves.addAlternates(ItemID.LEATHER_GLOVES, ItemID.SMITHING_UNIFORM_GLOVES_ICE);
		iceGloves.setTooltip("You can use normal gloves/telekenetic grab instead, but you'll then need to kill an Ice " +
			"Fiend");
		rockCake = new ItemRequirement("Dwarven rock cake", ItemID.HUNDRED_DWARF_COOL_ROCKCAKE);
		rockCakeHighlighted = new ItemRequirement("Dwarven rock cake", ItemID.HUNDRED_DWARF_COOL_ROCKCAKE);
		rockCakeHighlighted.setHighlightInInventory(true);
		rockCakeHot = new ItemRequirement("Dwarven rock cake", ItemID.HUNDRED_DWARF_HOT_ROCKCAKE);

		coin = new ItemRequirement("Coin", ItemCollections.COINS);
		coin.setHighlightInInventory(true);

		asgarnianAle = new ItemRequirement("Asgarnian ale", ItemID.ASGARNIAN_ALE);
		asgarnianAle.setHighlightInInventory(true);

		asgoldianAle = new ItemRequirement("Asgoldian ale", ItemID.HUNDRED_DWARF_ASGARNIAN_ALE);
		asgoldianAle.setHighlightInInventory(true);

		asgoldianAle4 = new ItemRequirement("Asgoldian ale", ItemID.HUNDRED_DWARF_ASGARNIAN_ALE, 4);

		// Recommended
		taverleyTeleport = new ItemRequirement("Teleport to taverley", ItemID.NZONE_TELETAB_TAVERLEY);
		taverleyTeleport.addAlternates(ItemCollections.COMBAT_BRACELETS);
		teleportFalador = new ItemRequirement("Teleport to Falador", ItemID.POH_TABLET_FALADORTELEPORT);
		teleportLumbridge = new ItemRequirement("Teleport to Lumbridge", ItemID.POH_TABLET_LUMBRIDGETELEPORT);
	}

	@Override
	protected void setupZones()
	{
		diningRoom = new Zone(new WorldPoint(1856, 5313, 0), new WorldPoint(1870, 5333, 0));
		tunnel = new Zone(new WorldPoint(2815, 9859, 0), new WorldPoint(2879, 9885, 0));
	}

	public void setupConditions()
	{
		inDiningRoom = new ZoneRequirement(diningRoom);
		inTunnel = new ZoneRequirement(tunnel);

		learnedHowToMakeAle = new VarbitRequirement(VarbitID.HUNDRED_DWARF_BEER, 1);
		givenAle = new VarbitRequirement(VarbitID.HUNDRED_DWARF_DRUNK, 1);
		has4AleOrGivenAle = new Conditions(LogicType.OR, asgoldianAle4, givenAle);
	}

	public void setupSteps()
	{
		enterDiningRoom = new ObjectStep(this, ObjectID.HUNDRED_LUMBRIDGE_DOUBLEDOORL, new WorldPoint(3213, 3221, 0), "Go inspect " +
			"the dwarf.");
		inspectDwarf = new ObjectStep(this, ObjectID.HUNDRED_DWARF_AMBASSADOR_BASE, new WorldPoint(1862, 5321, 0), "Inspect the dwarf.");
		inspectDwarf.addSubSteps(enterDiningRoom);

		talkToKaylee = new NpcStep(this, NpcID.RISINGSUN_BARMAID2, new WorldPoint(2957, 3371, 0), "Talk to Kaylee in the pub in " +
			"Falador.", coins320);
		talkToKaylee.addDialogSteps("What can you tell me about dwarves and ale?", "I could offer you some in return," +
			" how about 200 gold?");
		talkToKaylee.addTeleport(teleportFalador);

		makeAle = new DetailedQuestStep(this, "Add coins to asgarnian ale to make 4 asgoldian ale. You can buy ale " +
			"from Kaylee for 3gp each.", coin, asgarnianAle);
		enterTunnels = new ObjectStep(this, ObjectID.TUNNELSTAIRSTOP2, new WorldPoint(2877, 3481, 0), "Enter the tunnel " +
			"under White Wolf Mountain.", asgoldianAle4, milk, flour, egg, bowlOfWater, iceGloves);
		enterTunnels.addTeleport(taverleyTeleport);
		talkToOldDwarf = new GetRohakDrunk(this, asgoldianAle4);
		talkToOldDwarfMore = new NpcStep(this, NpcID.HUNDRED_DWARF_DAD_DRUNK, new WorldPoint(2865, 9876, 0),
			"Talk to Rohak more.", coins100, milk, flour, egg, bowlOfWater);

		talkToOldDwarfWithIngredients = new NpcStep(this, NpcID.HUNDRED_DWARF_DAD_DRUNK, new WorldPoint(2865, 9876, 0),
			"Talk to Rohak more.", milk, flour, egg, bowlOfWater);
		talkToOldDwarfMore.addSubSteps(talkToOldDwarfWithIngredients);

		pickUpRockCake = new ItemStep(this, "Pick up the dwarven rock cake. If you have ice gloves, wear them to pick" +
			" it up. Otherwise, wear other gloves or telegrab it. If it despawns you'll need to bring Rohak more " +
			"ingredients.",	rockCakeHot);
		coolRockCake = new NpcStep(this, NpcID.GODWARS_ICEFIEND_1, new WorldPoint(3008, 3471, 0),
			"Kill an icefiend to cool the rock cake.", rockCakeHot);
		coolRockCakeSidebar = new NpcStep(this, NpcID.GODWARS_ICEFIEND_1, new WorldPoint(3008, 3471, 0),
			"If you didn't pick up the rock cake with ice gloves, kill an icefiend to cool the rock cake.",
			rockCakeHot);
		coolRockCakeSidebar.addSubSteps(coolRockCake);
		enterDiningRoomAgain = new ObjectStep(this, ObjectID.HUNDRED_LUMBRIDGE_DOUBLEDOORL, new WorldPoint(3213, 3221, 0),
			"Go use the dwarven rock cake on the dwarf.");
		enterDiningRoomAgain.addTeleport(teleportLumbridge);
		useRockCakeOnDwarf = new ObjectStep(this, ObjectID.HUNDRED_DWARF_AMBASSADOR_BASE, new WorldPoint(1862, 5321, 0),
			"Use the dwarven rock cake on the dwarf.", rockCakeHighlighted);
		useRockCakeOnDwarf.addIcon(ItemID.HUNDRED_DWARF_COOL_ROCKCAKE);
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
		return Arrays.asList(teleportFalador.quantity(2), teleportLumbridge, taverleyTeleport);
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
		return Collections.singletonList(new ItemReward("A Dwarven Rock Cake", ItemID.HUNDRED_DWARF_HOT_ROCKCAKE, 1));
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
		int questState = client.getVarbitValue(VarbitID.HUNDRED_DWARF_QUEST);
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
		return (getQuest().getVar(client) >= 60 || client.getVarbitValue(VarbitID.HUNDRED_MAIN_QUEST_VAR) < 3);
	}
}
