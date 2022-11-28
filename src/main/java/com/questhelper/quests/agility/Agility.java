package com.questhelper.quests.agility;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.runelite.api.ItemID;
import net.runelite.api.ObjectID;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.AGILITY
)
public class Agility extends ComplexStateQuestHelper
{

	// Items recommended
	ItemRequirement bootsOfLightness, gracefulOutfit, gracefulHood, gracefulTop, gracefulLegs, gracefulGloves, gracefulBoots, gracefulCape;

	SkillRequirement ag10, ag20, ag30, ag40, ag50, ag60, ag70, ag80, ag90;

	SkillRequirement ag45;

	QuestStep gnomeStronghold, draynorVillage, alKharid, varrock, canifis, falador, seersVillage, pollnivneach, rellekka, ardougne;

	@Override
	public QuestStep loadStep()
	{
		setupRequirements();
		setupSteps();

		ConditionalStep fullTraining = new ConditionalStep(this, gnomeStronghold);
		fullTraining.addStep(ag10, draynorVillage, true);
		fullTraining.addStep(ag20, alKharid, true);
		fullTraining.addStep(ag30, varrock, true);
		fullTraining.addStep(ag40, canifis, true);
		fullTraining.addStep(ag50, falador, true);
		fullTraining.addStep(ag60, seersVillage, true);
		fullTraining.addStep(ag70, pollnivneach, true);
		fullTraining.addStep(ag80, rellekka, true);
		fullTraining.addStep(ag90, ardougne, true);

		return fullTraining;
	}

	private void setupRequirements()
	{
		ag10 = new SkillRequirement(Skill.AGILITY, 10);
		ag20 = new SkillRequirement(Skill.AGILITY, 20);
		ag30 = new SkillRequirement(Skill.AGILITY, 30);
		ag40 = new SkillRequirement(Skill.AGILITY, 40);
		ag50 = new SkillRequirement(Skill.AGILITY, 50);
		ag60 = new SkillRequirement(Skill.AGILITY, 60);
		ag70 = new SkillRequirement(Skill.AGILITY, 70);
		ag80 = new SkillRequirement(Skill.AGILITY, 80);
		ag90 = new SkillRequirement(Skill.AGILITY, 90);


		ag45 = new SkillRequirement(Skill.AGILITY, 45);


		bootsOfLightness = new ItemRequirement(
			"Boots of Lightness", ItemID.BOOTS_OF_LIGHTNESS).showConditioned(
			new Conditions(LogicType.NOR, ag45)
		);

		gracefulHood = new ItemRequirement(
			"Graceful hood", ItemCollections.GRACEFUL_HOOD, 1, true).showConditioned(
			new Conditions(ag45)
		);

		gracefulTop = new ItemRequirement(
			"Graceful top", ItemCollections.GRACEFUL_TOP, 1, true).showConditioned(
			new Conditions(ag45)
		);

		gracefulLegs = new ItemRequirement(
			"Graceful legs", ItemCollections.GRACEFUL_LEGS, 1, true).showConditioned(
			new Conditions(ag45)
		);

		gracefulCape = new ItemRequirement(
			"Graceful cape", ItemCollections.GRACEFUL_CAPE, 1, true).showConditioned(
			new Conditions(ag45)
		);

		gracefulGloves = new ItemRequirement(
			"Graceful gloves", ItemCollections.GRACEFUL_GLOVES, 1, true).showConditioned(
			new Conditions(ag45)
		);

		gracefulBoots = new ItemRequirement(
			"Graceful boots", ItemCollections.GRACEFUL_BOOTS, 1, true).showConditioned(
			new Conditions(ag45)
		);
		gracefulBoots.addAlternates(ItemID.BOOTS_OF_LIGHTNESS);

		gracefulOutfit = new ItemRequirements(
			"Full graceful outfit sets (6 pieces)(equipped)",
			gracefulHood, gracefulTop, gracefulLegs, gracefulGloves, gracefulBoots, gracefulCape);

	}

	private void setupSteps()
	{
		gnomeStronghold = new ObjectStep(this, ObjectID.LOG_BALANCE, new WorldPoint(3192, 3223, 0),
			"Train agility at the Gnome Stronghold Agility Course", bootsOfLightness);

		draynorVillage = new ObjectStep(this, ObjectID.ROUGH_WALL, new WorldPoint(3190, 3247, 0),
			"Train agility at the Draynor Village Rooftop Course", bootsOfLightness);

		alKharid = new ObjectStep(this, ObjectID.CABLE, new WorldPoint(2335, 3048, 0),
			"Train agility at the Al Kharid Rooftop Course", bootsOfLightness);

		varrock = new ObjectStep(this, ObjectID.CABLE, new WorldPoint(2335, 3048, 0),
			"Train agility at the Varrock Rooftop Course", bootsOfLightness);

		canifis = new ObjectStep(this, ObjectID.CABLE, new WorldPoint(2335, 3048, 0),
			"Train agility at the Canifis Rooftop Course", gracefulOutfit);

		falador = new ObjectStep(this, ObjectID.CABLE, new WorldPoint(2335, 3048, 0),
			"Train agility at the Falador Rooftop Course", gracefulOutfit);

		seersVillage = new ObjectStep(this, ObjectID.EDGE_14925, new WorldPoint(2335, 3048, 0),
			"Train agility at Seer's Village Rooftop Course", gracefulOutfit);

		pollnivneach = new ObjectStep(this, ObjectID.EDGE, new WorldPoint(2335, 3048, 0),
			"Train agility at the Pollnivneach Rooftop Course", gracefulOutfit);

		rellekka = new ObjectStep(this, ObjectID.GAP_2831, new WorldPoint(2335, 3048, 0),
			"Train agility at the Rellekka Rooftop Course", gracefulOutfit);

		ardougne = new ObjectStep(this, ObjectID.GAP, new WorldPoint(2335, 3048, 0),
			"Train agility at the Ardougne Rooftop Course", gracefulOutfit);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Collections.singletonList(
			new UnlockReward("Ability to purchase AGILITY Cape for 99k")
		);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(
			bootsOfLightness, gracefulHood, gracefulTop, gracefulLegs, gracefulGloves, gracefulBoots, gracefulCape
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(
			new PanelDetails("1 - 10: Gnome Stronghold Agility Course", Collections.singletonList(gnomeStronghold))
		);
		allSteps.add(
			new PanelDetails("10 - 20: Draynor Village Rooftop Course", Collections.singletonList(draynorVillage), ag10)
		);
		allSteps.add(
			new PanelDetails("20 - 30: Al Kharid Rooftop Course", Collections.singletonList(alKharid), ag20)
		);
		allSteps.add(
			new PanelDetails("30 - 40: Varrock Rooftop Course", Collections.singletonList(varrock), ag30)
		);
		allSteps.add(
			new PanelDetails("40 - 50: Canifis Rooftop Course", Collections.singletonList(canifis), ag40)
		);
		allSteps.add(
			new PanelDetails("50 - 60: Falador Rooftop Course", Collections.singletonList(falador), ag50)
		);
		allSteps.add(
			new PanelDetails("60 - 70: Seer's Village Rooftop Course", Collections.singletonList(seersVillage), ag60)
		);
		allSteps.add(
			new PanelDetails("70 - 80: Pollnivneach Rooftop Course", Collections.singletonList(pollnivneach), ag70)
		);
		allSteps.add(
			new PanelDetails("80 - 90: Rellekka Rooftop Course", Collections.singletonList(rellekka), ag80)
		);
		allSteps.add(
			new PanelDetails("90 - 99: Ardougne Rooftop Course", Collections.singletonList(ardougne), ag90)
		);
		return allSteps;
	}
}
