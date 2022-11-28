package com.questhelper.quests.agility;

import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
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
	ItemRequirement bootsOfLightness, gracefulHood, gracefulTop, gracefulLegs, gracefulGloves, gracefulBoots, gracefulCape;

	SkillRequirement ag10, ag20, ag30, ag40, ag50, ag60, ag70, ag80, ag90;

	SkillRequirement ag45;

	QuestStep gnomeStronghold, draynorVillage, alKharid, varrock, canifis, falador, seersVillage, pollnivneach, rellekka, ardougne;

	@Override
	public QuestStep loadStep()
	{
		setupRequirements();
		setupSteps();

		ConditionalStep fullTraining = new ConditionalStep(this, gnomeStronghold);
		fullTraining.addStep(ag10, draynorVillage);
		fullTraining.addStep(ag20, alKharid);
		fullTraining.addStep(ag30, varrock);
		fullTraining.addStep(ag40, canifis);
		fullTraining.addStep(ag50, falador);
		fullTraining.addStep(ag60, seersVillage);
		fullTraining.addStep(ag70, pollnivneach);
		fullTraining.addStep(ag80, rellekka);
		fullTraining.addStep(ag90, ardougne);

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

		bootsOfLightness = new ItemRequirement("Boots of Lightness", ItemID.BOOTS_OF_LIGHTNESS).showConditioned(
			new Conditions(LogicType.NOR, ag45)
		);
		bootsOfLightness = bootsOfLightness.showConditioned(bootsOfLightness.alsoCheckBank(questBank));


		gracefulHood = new ItemRequirement("Graceful hood", ItemID.GRACEFUL_HOOD).showConditioned(
			new Conditions(ag45)
		);

		gracefulHood = gracefulHood.showConditioned(gracefulHood.alsoCheckBank(questBank));

		gracefulTop = new ItemRequirement("Graceful top", ItemID.GRACEFUL_TOP).showConditioned(
			new Conditions(ag45)
		);
			gracefulTop = gracefulTop.showConditioned(gracefulTop.alsoCheckBank(questBank));

		gracefulLegs = new ItemRequirement("Graceful legs", ItemID.GRACEFUL_LEGS).showConditioned(
			new Conditions(ag45)
		);
		gracefulLegs = gracefulLegs.showConditioned(gracefulLegs.alsoCheckBank(questBank));

		gracefulGloves = new ItemRequirement("Graceful gloves", ItemID.GRACEFUL_GLOVES).showConditioned(
			new Conditions(ag45)
		);
		gracefulGloves = gracefulGloves.showConditioned(gracefulGloves.alsoCheckBank(questBank));

		gracefulGloves = new ItemRequirement("Graceful boots", ItemID.GRACEFUL_BOOTS).showConditioned(
			new Conditions(ag45)
		);
		gracefulGloves = gracefulBoots.showConditioned(gracefulBoots.alsoCheckBank(questBank));

		gracefulGloves = new ItemRequirement("Graceful cape", ItemID.GRACEFUL_CAPE).showConditioned(
			new Conditions(ag45)
		);
		gracefulGloves = gracefulCape.showConditioned(gracefulCape.alsoCheckBank(questBank));
	}

	private void setupSteps()
	{
		gnomeStronghold = new ObjectStep(this, ObjectID.LOG_BALANCE, new WorldPoint(3192, 3223, 0),
			"Train agility at the Gnome Stronghold Agility Course", true, bootsOfLightness);

		draynorVillage = new ObjectStep(this, ObjectID.ROUGH_WALL, new WorldPoint(3190, 3247, 0),
			"Train agility at the Draynor Village Rooftop Course", true, bootsOfLightness);

		alKharid = new ObjectStep(this, ObjectID.CABLE, new WorldPoint(2335, 3048, 0),
			"Train agility at the Al Kharid Rooftop Course", true, bootsOfLightness);

		varrock = new ObjectStep(this, ObjectID.CABLE, new WorldPoint(2335, 3048, 0),
			"Train agility at the Varrock Rooftop Course", true, bootsOfLightness);

		canifis = new ObjectStep(this, ObjectID.CABLE, new WorldPoint(2335, 3048, 0),
			"Train agility at the Canifis Rooftop Course", true, bootsOfLightness);

		falador = new ObjectStep(this, ObjectID.CABLE, new WorldPoint(2335, 3048, 0),
			"Train agility at the Falador Rooftop Course", true, bootsOfLightness);

		seersVillage = new ObjectStep(this, ObjectID.EDGE_14925, new WorldPoint(2335, 3048, 0),
			"Train agility at Seer's Village Rooftop Course", true, bootsOfLightness);

		pollnivneach = new ObjectStep(this, ObjectID.EDGE, new WorldPoint(2335, 3048, 0),
			"Train agility at the Pollnivneach Rooftop Course", true, bootsOfLightness);

		rellekka = new ObjectStep(this, ObjectID.GAP_2831, new WorldPoint(2335, 3048, 0),
			"Train agility at the Rellekka Rooftop Course", true, bootsOfLightness);

		ardougne = new ObjectStep(this, ObjectID.GAP, new WorldPoint(2335, 3048, 0),
			"Train agility at the Ardougne Rooftop Course", true, bootsOfLightness);
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
			new PanelDetails("10 - 20: Draynor Village Rooftop Course", Collections.singletonList(draynorVillage))
		);
		allSteps.add(
			new PanelDetails("20 - 30: Al Kharid Rooftop Course", Collections.singletonList(alKharid))
		);
		allSteps.add(
			new PanelDetails("30 - 40: Varrock Rooftop Course", Collections.singletonList(alKharid))
		);
		allSteps.add(
			new PanelDetails("40 - 50: Canifis Rooftop Course", Collections.singletonList(alKharid),
				gracefulHood, gracefulTop, gracefulLegs, gracefulGloves, gracefulBoots, gracefulCape)
		);
		allSteps.add(
			new PanelDetails("50 - 60: Falador Rooftop Course", Collections.singletonList(alKharid),
				gracefulHood, gracefulTop, gracefulLegs, gracefulGloves, gracefulBoots, gracefulCape)
		);
		allSteps.add(
			new PanelDetails("60 - 70: Seer's Village Rooftop Course", Collections.singletonList(alKharid),
				gracefulHood, gracefulTop, gracefulLegs, gracefulGloves, gracefulBoots, gracefulCape)
		);
		allSteps.add(
			new PanelDetails("70 - 80: Pollnivneach Rooftop Course", Collections.singletonList(alKharid),
				gracefulHood, gracefulTop, gracefulLegs, gracefulGloves, gracefulBoots, gracefulCape)
		);
		allSteps.add(
			new PanelDetails("80 - 90: Rellekka Rooftop Course", Collections.singletonList(alKharid),
				gracefulHood, gracefulTop, gracefulLegs, gracefulGloves, gracefulBoots, gracefulCape)
		);
		allSteps.add(
			new PanelDetails("90 - 99: Ardougne Rooftop Course", Collections.singletonList(alKharid),
				gracefulHood, gracefulTop, gracefulLegs, gracefulGloves, gracefulBoots, gracefulCape)
		);
		return allSteps;
	}
}
