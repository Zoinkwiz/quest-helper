/*
 * Copyright (c) 2024, pajlada <https://github.com/pajlada>
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
package com.questhelper.helpers.quests.meatandgreet;

import com.questhelper.bank.banktab.BankSlotIcons;
import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.TeleportItemRequirement;
import com.questhelper.requirements.player.CombatLevelRequirement;
import com.questhelper.requirements.player.FreeInventorySlotRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.widget.WidgetPresenceRequirement;
import com.questhelper.requirements.widget.WidgetTextRequirement;
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
import com.questhelper.steps.widget.WidgetDetails;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static com.questhelper.requirements.util.LogicHelper.and;
import static com.questhelper.requirements.util.LogicHelper.nor;
import static com.questhelper.requirements.util.LogicHelper.not;

/**
 * The quest guide for the "Meat and Greet" OSRS quest
 * <p>
 * <a href="https://oldschool.runescape.wiki/w/Death_on_the_Isle">The OSRS wiki guide</a> was referenced for this guide
 */
public class MeatAndGreet extends BasicQuestHelper
{
	// TODO: Confirm this is the correct region ID, otherwise the coords were 1489, 9511, 1 to 1471, 9493, 1
	final static private int WOLF_DEN_REGION_ID = 6036;
	final static private int FORTIS_COLOSSEUM_REGION_ID = 7316;

	/// Recommended items
	private TeleportItemRequirement civitasIllaFortisTeleport;
	private ItemRequirement combatGear;
	private ItemRequirement food;
	private ItemRequirement staminaPotion;
	private ItemRequirement prayerPotion;

	/// Mid-quest item requirements
	private ItemRequirement experimentalKebab;
	private ItemRequirement goodTestKebab;
	private ItemRequirement goodTestKebabs;

	/// Zones & their requirements
	private Zone wolfDen;
	private ZoneRequirement inWolfDen;
	private Zone fortisColosseum;
	private ZoneRequirement inFortisColosseum;

	/// Steps
	private NpcStep talkToEmelioToStartQuest;
	private NpcStep talkToSpiceMerchantInBazaar;
	private PuzzleWrapperStep enterCodeWrapper;
	private NpcStep talkToAlba;
	private ObjectStep headWestAndEnterTheDungeon;
	private NpcStep killDireWolfAlpha;
	private NpcStep returnToAlba;
	private NpcStep returnToEmelio;
	private PuzzleWrapperStep recipeStepWrapper;
	private NpcStep returnToEmelioWithNewsOfYourKebabSuccess;
	private ConditionalStep solveSupplyChainIssues;
	private NpcStep talkToLelia;
	private ConditionalStep giveKebabToLelia;
	private NpcStep talkToLeliaAfterGivingHerTheKebab;
	private ConditionalStep fightMinotaurWithKebab;
	private NpcStep enterArenaWithKebab;
	private NpcStep fightMinotaur;
	private ConditionalStep fightMinotaurWithKebabAfterEnteringAtLeastOnce;
	private ConditionalStep talkToLeliaAfterBeatingTheMinotaurStep;
	private NpcStep talkToLeliaAfterBeatingTheMinotaur;
	private ConditionalStep returnToEmelioWithNewsOfYourAdvertisingSuccessStep;
	private NpcStep returnToEmelioWithNewsOfYourAdvertisingSuccess;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();

		var steps = new HashMap<Integer, QuestStep>();

		steps.put(0, talkToEmelioToStartQuest);
		steps.put(2, talkToEmelioToStartQuest);
		steps.put(4, solveSupplyChainIssues);
		steps.put(6, returnToEmelio);
		steps.put(8, recipeStepWrapper);
		steps.put(10, returnToEmelioWithNewsOfYourKebabSuccess);
		steps.put(12, returnToEmelioWithNewsOfYourKebabSuccess);
		steps.put(14, giveKebabToLelia);
		steps.put(16, talkToLeliaAfterGivingHerTheKebab);
		steps.put(18, fightMinotaurWithKebab);
		steps.put(20, fightMinotaurWithKebabAfterEnteringAtLeastOnce);
		steps.put(22, talkToLeliaAfterBeatingTheMinotaurStep);
		steps.put(24, returnToEmelioWithNewsOfYourAdvertisingSuccessStep);

		return steps;
	}

	@Override
	protected void setupZones()
	{
		wolfDen = new Zone(WOLF_DEN_REGION_ID);
		fortisColosseum = new Zone(FORTIS_COLOSSEUM_REGION_ID);
	}

	@Override
	protected void setupRequirements()
	{
		staminaPotion = new ItemRequirement("Stamina potion", ItemCollections.STAMINA_POTIONS, 1);
		prayerPotion = new ItemRequirement("Prayer potion", ItemCollections.PRAYER_POTIONS, 1);
		combatGear = new ItemRequirement("Combat gear", -1, -1);
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, -1);

		civitasIllaFortisTeleport = new TeleportItemRequirement("Teleport to Civitas illa Fortis", ItemID.CIVITAS_ILLA_FORTIS_TELEPORT, 1);

		inWolfDen = new ZoneRequirement(wolfDen);
		inFortisColosseum = new ZoneRequirement(fortisColosseum);

		experimentalKebab = new ItemRequirement("Test kebab", ItemID.TEST_KEBAB);

		goodTestKebab = new ItemRequirement("Test kebab", ItemID.TEST_KEBAB_29899, 1);
		goodTestKebab.setTooltip("You can return to Emelio if you need a test kebab.");
		goodTestKebabs = goodTestKebab.quantity(2);
	}

	public void setupSteps()
	{
		/// 0 + 2
		talkToEmelioToStartQuest = new NpcStep(this, NpcID.EMELIO, new WorldPoint(1753, 3074, 0), "Talk to Emelio in Outer Fortis at the south-eastern entrance of Civitas illa Fortis to start the quest.");
		talkToEmelioToStartQuest.addDialogStep("Yes.");
		talkToEmelioToStartQuest.addTeleport(civitasIllaFortisTeleport);

		// 11183 0->1
		// 11184 0->1

		/// 4
		// TODO: recommend teleport?
		talkToSpiceMerchantInBazaar = new NpcStep(this, NpcID.SPICE_MERCHANT, new WorldPoint(1685, 3101, 0), "Talk to the Spice Merchant in the Bazaar in the middle of Civitas illa Fortis about the missing delivery.");
		talkToSpiceMerchantInBazaar.addDialogStep("I'm here about a missing delivery.");
		talkToSpiceMerchantInBazaar.addDialogStep("Could I have a look at that locked box?");
		talkToSpiceMerchantInBazaar.addDialogStep("About that missing delivery...");
		// 11183 1->2 (Marks that you've talked about the missing delivery. Future discussions with Spice Merchants will have you ask about the locked box.)
		var pinPadOpen = new WidgetPresenceRequirement(888, 1);
		var submitCode = new WidgetStep(this, "Submit the code.", 888, 7); // Submit button (right-facing arrow)
		var resetCode = new WidgetStep(this, "Reset the code.", new WidgetDetails(888, 6, 20)); // Reset button (left-facing arrow)
		var enterFirstNumber = new WidgetStep(this, "Click 2.", new WidgetDetails(888, 6, 2));
		var firstNumberEmpty = new WidgetTextRequirement(888, 5, 0, "-");
		var secondNumberEmpty = new WidgetTextRequirement(888, 5, 1, "-");
		var thirdNumberEmpty = new WidgetTextRequirement(888, 5, 2, "-");
		var fourthNumberEmpty = new WidgetTextRequirement(888, 5, 3, "-");
		var firstNumberCorrect = new WidgetTextRequirement(888, 5, 0, "2");
		var secondNumberCorrect = new WidgetTextRequirement(888, 5, 1, "5");
		var thirdNumberCorrect = new WidgetTextRequirement(888, 5, 2, "4");
		var fourthNumberCorrect = new WidgetTextRequirement(888, 5, 3, "6");
		var enterSecondNumber = new WidgetStep(this, "Click 5.", new WidgetDetails(888, 6, 8));
		var enterThirdNumber = new WidgetStep(this, "Click 4.", new WidgetDetails(888, 6, 6));
		var enterFourthNumber = new WidgetStep(this, "Click 6.", new WidgetDetails(888, 6, 10));
		var enterCode = new ConditionalStep(this, resetCode, "Enter the code 2546 to unlock the box.");
		enterCode.addStep(and(fourthNumberCorrect, thirdNumberCorrect, secondNumberCorrect, firstNumberCorrect), submitCode);
		enterCode.addStep(and(fourthNumberEmpty, thirdNumberCorrect, secondNumberCorrect, firstNumberCorrect), enterFourthNumber);
		enterCode.addStep(and(thirdNumberEmpty, secondNumberCorrect, firstNumberCorrect), enterThirdNumber);
		enterCode.addStep(and(secondNumberEmpty, firstNumberCorrect), enterSecondNumber);
		enterCode.addStep(firstNumberEmpty, enterFirstNumber);
		enterCodeWrapper = new PuzzleWrapperStep(this, enterCode, "Figure out the correct code to unlock the box of spices.");
		// Pin code correctly entered: 11183 2->3
		// Spices sent: 11183 3->4
		var needToSendSpicesFromSpiceMerchant = new VarbitRequirement(11183, 4, Operation.LESS);


		var needToTalkToAlbaAboutWolves = new VarbitRequirement(11184, 2, Operation.LESS);
		talkToAlba = new NpcStep(this, NpcID.ALBA, new WorldPoint(1587, 3126, 0), "Talk to Alba in the farmhouse west of Civitas illa Fortis.");
		headWestAndEnterTheDungeon = new ObjectStep(this, ObjectID.WOLF_DEN_54692, new WorldPoint(1498, 3132, 0), "Head west from the farm and enter the Wolf Den, prepared to kill the Dire Wolf Alpha.", combatGear, food);
		var enterWolfDenAndKillTheDireWolfAlpha = new ConditionalStep(this, headWestAndEnterTheDungeon);
		// enterWolfDenAndKillTheDireWolfAlpha
		killDireWolfAlpha = new NpcStep(this, NpcID.DIRE_WOLF_ALPHA, "Kill the Dire Wolf Alpha. Use Protect from Melee to avoid most damage. His pups deal ranged damage.", combatGear, food);
		// TODO: Add check for "need to kill wolf"
		// killed wolf: 11184 2->3
		var needToKillWolf = new VarbitRequirement(11184, 2);
		var exitWolfDen = new ObjectStep(this, ObjectID.CAVE_54691, new WorldPoint(1488, 9502, 1), "Exit the wolf den, then head back to Alba in the farmhouse west of Civitas illa Fortis.");
		// 13092 0->100
		// 13093 0->100
		// 13094 0->100
		// 13095 0->100
		returnToAlba = new NpcStep(this, NpcID.ALBA, new WorldPoint(1587, 3126, 0), "Return to Alba in the farmhouse west of Civitas illa Fortis to tell them about the Dire Wolf Alpha's demise.");
		returnToAlba.addSubSteps(exitWolfDen);
		var needToReturnToAlba = new VarbitRequirement(11184, 3);
		// 11184 3->4 after returning to alba
		// TODO: Confirm the return to alba step works
		solveSupplyChainIssues = new ConditionalStep(this, returnToAlba);
		solveSupplyChainIssues.addStep(pinPadOpen, enterCodeWrapper);
		solveSupplyChainIssues.addStep(needToSendSpicesFromSpiceMerchant, talkToSpiceMerchantInBazaar);
		solveSupplyChainIssues.addStep(and(needToReturnToAlba, inWolfDen), exitWolfDen);
		solveSupplyChainIssues.addStep(and(needToKillWolf, inWolfDen), killDireWolfAlpha);
		solveSupplyChainIssues.addStep(needToKillWolf, enterWolfDenAndKillTheDireWolfAlpha);
		solveSupplyChainIssues.addStep(needToTalkToAlbaAboutWolves, talkToAlba);

		/// 6
		// Can save 50% of the time by teleporting to Civitas illa Fortis & running south-east, or using a Quetzal whistle to (?), but I don't have a whistle!
		returnToEmelio = new NpcStep(this, NpcID.EMELIO, new WorldPoint(1753, 3074, 0), "Return to Emelio in Outer Fortis at the south-eastern entrance of Civitas illa Fortis to find the perfect ratio of ingredients for the kebab.");

		/// 8
		var giveRecipeToEmelio = new NpcStep(this, NpcID.EMELIO, new WorldPoint(1753, 3074, 0), "Tell Emelio the recipe is perfect.");
		giveRecipeToEmelio.addDialogStep("I think so.");
		giveRecipeToEmelio.addDialogStep("I think that's perfect!");
		giveRecipeToEmelio.addDialogStep("Sounds good to me.");
		var adjustRecipe = new NpcStep(this, NpcID.EMELIO, new WorldPoint(1753, 3074, 0), "Adjust the recipe with Emelio. Four portions of meat, two portions of salad, one portion of spice, and three portions of sauce.");
		adjustRecipe.addDialogStep("Adjust meat (currently at one portion).");
		adjustRecipe.addDialogStep("Adjust meat (currently at two portions).");
		adjustRecipe.addDialogStep("Adjust meat (currently at three portions).");
		adjustRecipe.addDialogStep("Adjust salad (currently at one portion).");
		adjustRecipe.addDialogStep("Adjust salad (currently at three portions).");
		adjustRecipe.addDialogStep("Adjust salad (currently at four portions).");
		adjustRecipe.addDialogStep("Adjust spice (currently at two portions).");
		adjustRecipe.addDialogStep("Adjust spice (currently at three portions).");
		adjustRecipe.addDialogStep("Adjust spice (currently at four portions).");
		adjustRecipe.addDialogStep("Adjust sauce (currently at one portion).");
		adjustRecipe.addDialogStep("Adjust sauce (currently at two portions).");
		adjustRecipe.addDialogStep("Adjust sauce (currently at four portions).");
		adjustRecipe.addDialogStepWithExclusion("Cancel.", "Four.");
		adjustRecipe.addDialogStepWithExclusions(
			"Four (current).",
			"Adjust number of salad portions to:",
			"Adjust number of spice portions to:",
			"Adjust number of sauce portions to:"
		);
		adjustRecipe.addDialogStepWithExclusions(
			"Four.",
			"Adjust number of salad portions to:",
			"Adjust number of spice portions to:",
			"Adjust number of sauce portions to:"
		);
		adjustRecipe.addDialogStepWithExclusions(
			"Two (current).",
			"Adjust number of meat portions to:",
			"Adjust number of spice portions to:",
			"Adjust number of sauce portions to:"
		);
		adjustRecipe.addDialogStepWithExclusions(
			"Two.",
			"Adjust number of meat portions to:",
			"Adjust number of spice portions to:",
			"Adjust number of sauce portions to:"
		);
		adjustRecipe.addDialogStepWithExclusions(
			"One (current).",
			"Adjust number of meat portions to:",
			"Adjust number of salad portions to:",
			"Adjust number of sauce portions to:"
		);
		adjustRecipe.addDialogStepWithExclusions(
			"One.",
			"Adjust number of meat portions to:",
			"Adjust number of salad portions to:",
			"Adjust number of sauce portions to:"
		);
		adjustRecipe.addDialogStepWithExclusions(
			"Three (current).",
			"Adjust number of meat portions to:",
			"Adjust number of salad portions to:",
			"Adjust number of spice portions to:"
		);
		adjustRecipe.addDialogStepWithExclusions(
			"Three.",
			"Adjust number of meat portions to:",
			"Adjust number of salad portions to:",
			"Adjust number of spice portions to:"
		);
		var meatCorrect = new VarbitRequirement(11185, 4, Operation.EQUAL);
		var saladCorrect = new VarbitRequirement(11186, 2);
		var spiceCorrect = new VarbitRequirement(11187, 1);
		var sauceCorrect = new VarbitRequirement(11188, 3);
		var recipeStep = new ConditionalStep(this, adjustRecipe, "Configure the kebab recipe with Emelio.");
		// 11189 0->1 = received test kebab
		var giveExperimentalKebabToRenata = new NpcStep(this, NpcID.RENATA, new WorldPoint(1750, 3072, 0), "Talk to Renata to have them test your test kebab.", experimentalKebab);
		recipeStep.addStep(experimentalKebab, giveExperimentalKebabToRenata);
		recipeStep.addStep(and(meatCorrect, saladCorrect, spiceCorrect, sauceCorrect), giveRecipeToEmelio);
		recipeStepWrapper = new PuzzleWrapperStep(this, recipeStep, "Figure out the correct ratio of meat, salad, spice, and sauce by talking to the kebab connoisseurs outside then give the final recipe to Emelio.");
		// 11182 8 -> 10 when found perfect kebab recipe & given it to someone

		/// 10 + 12
		returnToEmelioWithNewsOfYourKebabSuccess = new NpcStep(this, NpcID.EMELIO, new WorldPoint(1754, 3074, 0), "Return to Emelio with news of your kebab success!");

		/// 14
		var headIntoColosseum = new ObjectStep(this, ObjectID.COLOSSEUM_ENTRANCE, new WorldPoint(1796, 3106, 0), "Head into the Fortis Colosseum and talk to Lelia about kebab sales.", combatGear, food, goodTestKebabs);
		talkToLelia = new NpcStep(this, NpcID.LELIA, new WorldPoint(1819, 9484, 0), "Head into the Fortis Colosseum, then into the southern room and talk to Lelia about kebab sales.", combatGear, food, goodTestKebabs);
		talkToLelia.addSubSteps(headIntoColosseum);
		giveKebabToLelia = new ConditionalStep(this, headIntoColosseum);
		giveKebabToLelia.addStep(inFortisColosseum, talkToLelia);

		/// 16
		//14 -> 16 after giving kebab to Lelia
		// TECHNICALLY the player could leave, and we won't guide the player into the colosseum here
		talkToLeliaAfterGivingHerTheKebab = new NpcStep(this, NpcID.LELIA, new WorldPoint(1819, 9484, 0), "Talk to Lelia after letting her taste your test kebab.", combatGear, food, goodTestKebab);

		/// 18
		var headIntoColosseum2 = new ObjectStep(this, ObjectID.COLOSSEUM_ENTRANCE, new WorldPoint(1796, 3106, 0), "Head into the Fortis Colosseum, ready to fight the Minotaur.", combatGear, food, goodTestKebab);
		enterArenaWithKebab = new NpcStep(this, NpcID.LELIA, new WorldPoint(1819, 9484, 0), "Talk to Lelia in the southern room, ready to fight the Minotaur.", combatGear, food, goodTestKebab);
		enterArenaWithKebab.addDialogStep("I think so.");
		// 13989 0->1 ?
		var arena = new Zone(new WorldPoint(1808, 3123, 0), new WorldPoint(1840, 3090, 0));
		var inArena = new ZoneRequirement(arena);
		fightMinotaur = new NpcStep(this, NpcID.MINOTAUR_13815, new WorldPoint(1823, 3105, 0), "Fight the Minotaur. Protect from Melee to avoid most damage. When it shouts Moo! switch to Protect from Magic for one attack.");
		var leaveColosseumToGetAnotherKebabFromEmelio = new ObjectStep(this, ObjectID.STAIRS_50750, new WorldPoint(1798, 9506, 0), "Talk to Emelio to get another test kebab.", combatGear, food, goodTestKebab);
		var getAnotherKebabFromEmelio = new NpcStep(this, NpcID.EMELIO, new WorldPoint(1754, 3074, 0), "Talk to Emelio to get another test kebab before you can fight the Minotaur.", combatGear, food, goodTestKebab);
		enterArenaWithKebab.addSubSteps(leaveColosseumToGetAnotherKebabFromEmelio, getAnotherKebabFromEmelio);
		fightMinotaurWithKebab = new ConditionalStep(this, enterArenaWithKebab);
		fightMinotaurWithKebab.addStep(inArena, fightMinotaur);
		fightMinotaurWithKebab.addStep(nor(goodTestKebab, inFortisColosseum), getAnotherKebabFromEmelio);
		fightMinotaurWithKebab.addStep(and(not(goodTestKebab), inFortisColosseum), leaveColosseumToGetAnotherKebabFromEmelio);
		fightMinotaurWithKebab.addStep(and(goodTestKebab, not(inFortisColosseum)), headIntoColosseum2);
		// 9807 = have spoken to Minimus about Colosseum (unrelated to quest xd)

		/// 20
		var enterArenaAfterFailing = new NpcStep(this, NpcID.LELIA, new WorldPoint(1819, 9484, 0), "Talk to Lelia in the southern room, ready to fight the Minotaur again.", combatGear, food);
		enterArenaAfterFailing.addDialogStep("Okay, I'm ready.");
		enterArenaWithKebab.addSubSteps(enterArenaAfterFailing);
		var headIntoColosseumAfterFailing = new ObjectStep(this, ObjectID.COLOSSEUM_ENTRANCE, new WorldPoint(1796, 3106, 0), "Head into the Fortis Colosseum, ready to fight the Minotaur again.", combatGear, food);
		headIntoColosseum2.addSubSteps(headIntoColosseumAfterFailing);
		fightMinotaurWithKebabAfterEnteringAtLeastOnce = new ConditionalStep(this, enterArenaAfterFailing);
		fightMinotaurWithKebabAfterEnteringAtLeastOnce.addStep(inArena, fightMinotaur);
		fightMinotaurWithKebabAfterEnteringAtLeastOnce.addStep(not(inFortisColosseum), headIntoColosseumAfterFailing);

		/// 22
		talkToLeliaAfterBeatingTheMinotaur = new NpcStep(this, NpcID.LELIA, new WorldPoint(1818, 9484, 0), "Talk to Lelia in the southern room of the Fortis Colosseum after defeating the Minotaur.");
		var headIntoColosseumAfterDefeatingTheMinotaur = new ObjectStep(this, ObjectID.COLOSSEUM_ENTRANCE, new WorldPoint(1796, 3106, 0), "Head into the Fortis Colosseum and talk to Lelia.");
		talkToLeliaAfterBeatingTheMinotaurStep = new ConditionalStep(this, talkToLeliaAfterBeatingTheMinotaur);
		fightMinotaurWithKebabAfterEnteringAtLeastOnce.addStep(not(inFortisColosseum), headIntoColosseumAfterDefeatingTheMinotaur);

		/// 24
		var leaveColosseumToReturnToEmelio = new ObjectStep(this, ObjectID.STAIRS_50750, new WorldPoint(1798, 9506, 0), "Exit the colosseum, then return to Emelio in Outer Fortis at the south-eastern entrance of Civitas illa Fortis and tell him about your success.");
		returnToEmelioWithNewsOfYourAdvertisingSuccess = new NpcStep(this, NpcID.EMELIO, new WorldPoint(1754, 3074, 0), "Return to Emelio in Outer Fortis at the south-eastern entrance of Civitas illa Fortis and tell him about your success.");
		returnToEmelioWithNewsOfYourAdvertisingSuccess.addSubSteps(leaveColosseumToReturnToEmelio);
		returnToEmelioWithNewsOfYourAdvertisingSuccessStep = new ConditionalStep(this, returnToEmelioWithNewsOfYourAdvertisingSuccess);
		returnToEmelioWithNewsOfYourAdvertisingSuccessStep.addStep(inFortisColosseum, leaveColosseumToReturnToEmelio);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return List.of();
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return List.of(
			staminaPotion,
			prayerPotion,
			combatGear,
			food
		);
	}

	@Override
	public List<Requirement> getGeneralRecommended()
	{
		return List.of(
			new CombatLevelRequirement(60),
			new SkillRequirement(Skill.PRAYER, 43, false, "43+ Prayer to use Protect from Melee and Protect from Magic"),
			new FreeInventorySlotRequirement(2)
		);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return List.of(
			new QuestRequirement(QuestHelperQuest.CHILDREN_OF_THE_SUN, QuestState.FINISHED)
		);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return List.of(
			"Dire Wolf Alpha (lvl 113)",
			"Minotaur (lvl 193)"
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
			new ExperienceReward(Skill.COOKING, 8000)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return List.of(
			new UnlockReward("Access to Emelio's Kebab Shop")
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var panels = new ArrayList<PanelDetails>();

		panels.add(new PanelDetails("Supply chain issues", List.of(
			talkToEmelioToStartQuest,
			talkToSpiceMerchantInBazaar,
			enterCodeWrapper,
			talkToAlba,
			headWestAndEnterTheDungeon,
			killDireWolfAlpha), List.of(staminaPotion, prayerPotion, combatGear, food)));
		panels.add(new PanelDetails("The golden ratio", List.of(
			returnToAlba,
			returnToEmelio,
			recipeStepWrapper, // TODO: Dooes the text look nice here? and do we continue progress nicely?
			returnToEmelioWithNewsOfYourKebabSuccess
		), List.of(staminaPotion, prayerPotion, combatGear, food)));
		panels.add(new PanelDetails("Guerilla marketing", List.of(
			talkToLelia,
			talkToLeliaAfterGivingHerTheKebab,
			enterArenaWithKebab,
			fightMinotaur,
			talkToLeliaAfterBeatingTheMinotaur,
			returnToEmelioWithNewsOfYourAdvertisingSuccess
		), List.of(prayerPotion, combatGear, food)));

		return panels;
	}
}
