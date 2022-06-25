/*
 * Copyright (c) 2022, zct8002 <https://github.com/zct8002>
 * Copyright (c) 2022, Fincap <https://github.com/Fincap>
 * Copyright (c) 2022, Zoinkwiz <https://github.com/Zoinkwiz>
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

package com.questhelper.quests.templeoftheeye;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
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
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.TEMPLE_OF_THE_EYE
)
public class TempleOfTheEye extends BasicQuestHelper
{
	//Items Required
	ItemRequirement bucketOfWater, strongTea, eyeAmulet, chisel, pickaxe, abyssalIncantation;
	//Items Recommended
	ItemRequirement varrock, alkharid;

	QuestStep talkToPersten1, talkToMage1, getTeaForMage, talkToMage2, talkToDarkMage1,
		talkToDarkMage2, talkToPersten2, talktoArchmage, talktoTrailborn1,
		talktoApprentices, talktoTrailborn2, talktoArchmage2;
	ObjectStep touchRunes;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupItemRequirements();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToPersten1);
		steps.put(10, talkToMage1);

		ConditionalStep goToAbyss = new ConditionalStep(this, getTeaForMage);
		goToAbyss.addStep(new Conditions(strongTea, bucketOfWater, eyeAmulet), talkToMage2);

		steps.put(15, goToAbyss);
		steps.put(25, talkToDarkMage1);
		steps.put(35, touchRunes);
		steps.put(40, talkToDarkMage2);
		steps.put(45, talkToPersten2);
		return steps;
	}

	public void setupItemRequirements()
	{
		bucketOfWater = new ItemRequirement("Bucket of water", ItemID.BUCKET_OF_WATER);
		bucketOfWater.setHighlightInInventory(true);
		chisel = new ItemRequirement("Chisel", ItemID.CHISEL);
		chisel.canBeObtainedDuringQuest();
		pickaxe = new ItemRequirement("Pickaxe", ItemCollections.getPickaxes());
		pickaxe.canBeObtainedDuringQuest();

		varrock = new ItemRequirement("Method of teleportation to Varrock", ItemID.VARROCK_TELEPORT);
		alkharid = new ItemRequirement("Method of teleportation to Al Kharid", ItemID.LUMBRIDGE_TELEPORT);

		strongTea = new ItemRequirement("Strong Cup of Tea", ItemID.STRONG_CUP_OF_TEA);
		eyeAmulet = new ItemRequirement("Eye Amulet", ItemID.EYE_AMULET);
		abyssalIncantation = new ItemRequirement("Abyssal Incantation", ItemID.ABYSSAL_INCANTATION);

	}

	public void setupSteps()
	{
		talkToPersten1 = new NpcStep(this, NpcID.WIZARD_PERSTEN, new WorldPoint(3285, 3232, 0),
			"Talk to Wizard Persten east of the Al Kharid gate");
		talkToPersten1.addDialogStep("What's a wizard doing in Al Kharid");
		talkToPersten1.addDialogStep("Yes.");

		talkToMage1 = new NpcStep(this, NpcID.MAGE_OF_ZAMORAK_2582, new WorldPoint(3258, 3383, 0),
			"Talk to Mage of Zamorak in the Varrock chaos temple");
		talkToMage1.addDialogStep("I need your help with an amulet");

		getTeaForMage = new NpcStep(this, NpcID.TEA_SELLER, new WorldPoint(3271, 3411, 0),
			"Talk to Tea Seller near the Varrock east gate");
		getTeaForMage.addDialogStep("Could I have a strong cup of tea?");

		talkToMage2 = new NpcStep(this, NpcID.MAGE_OF_ZAMORAK_2582, new WorldPoint(3258, 3383, 0),
			"Talk to Mage of Zamorak in the Varrock chaos temple");
		talkToMage2.addDialogStep("Could you help me with that amulet now?");
		talkToMage2.addDialogStep("Yes.");

		talkToDarkMage1 = new NpcStep(this, NpcID.DARK_MAGE, new WorldPoint(3039, 4834, 0),
			"Talk to Dark Mage in the Abyss");
		talkToDarkMage1.addDialogStep("I need your help with an amulet.");

		touchRunes = new ObjectStep(this, 43768,
			"Interact with the runic energy (pattern is different for everyone)");
		touchRunes.addAlternateObjects(43769, 43770, 43771, 43772, 43773);
		talkToDarkMage2 = new NpcStep(this, NpcID.DARK_MAGE, new WorldPoint(3039, 4834, 0),
			"Talk to Dark Mage in the Abyss");
		talkToPersten2 = new NpcStep(this, NpcID.WIZARD_PERSTEN, new WorldPoint(3285, 3232, 0),
			"Talk to Wizard Persten east of the Al Kharid gate");
		talkToPersten2.addDialogStep("Yes.");


	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(bucketOfWater);
		reqs.add(chisel);
		reqs.add(pickaxe);
		return reqs;
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(varrock);
		reqs.add(alkharid);
		return reqs;
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Collections.singletonList(new ItemReward("Medium pouch", ItemID.MEDIUM_POUCH));
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Collections.singletonList(new ExperienceReward(Skill.RUNECRAFT, 9210));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Collections.singletonList(new UnlockReward("Access to the Guardians of the Rift minigame"));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off", talkToPersten1));
		allSteps.add(new PanelDetails("An Eye for a Favour", Arrays.asList(talkToMage1, getTeaForMage, talkToMage2, talkToDarkMage1, touchRunes, talkToDarkMage2), bucketOfWater, eyeAmulet));
		allSteps.add(new PanelDetails("Help from Some Wizards", Arrays.asList(talkToPersten2), abyssalIncantation, eyeAmulet));//, talktoArchmage, talktoTrailborn1, talktoApprentices, talktoTrailborn2, talktoArchmage2)));
		return allSteps;
	}
}