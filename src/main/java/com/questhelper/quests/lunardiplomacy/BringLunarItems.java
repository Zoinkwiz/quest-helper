package com.questhelper.quests.lunardiplomacy;

import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.conditional.ConditionForStep;
import com.questhelper.steps.conditional.VarbitCondition;
import java.util.ArrayList;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.eventbus.Subscribe;

public class BringLunarItems extends NpcStep
{
	ConditionForStep handedInHelm, handedInCape, handedInAmulet, handedInTorso, handedInGloves, handedInBoots, handedInLegs, handedInRing;

	ItemRequirement helm, cape, amulet, torso, gloves, boots, legs, ring, sealOfPassage;

	public BringLunarItems(QuestHelper questHelper)
	{
		super(questHelper, NpcID.ONEIROMANCER, new WorldPoint(2151, 3867, 0),
			"Bring all the items to the Oneiromancer in the south east of Lunar Isle.");
		setupItemRequirements();
		setupConditions();
	}

	public void setupItemRequirements()
	{
		helm = new ItemRequirement("Lunar helm", ItemID.LUNAR_HELM);
		amulet = new ItemRequirement("Lunar amulet", ItemID.LUNAR_AMULET);
		ring = new ItemRequirement("Lunar ring", ItemID.LUNAR_RING);
		cape = new ItemRequirement("Lunar cape", ItemID.LUNAR_CAPE);
		torso = new ItemRequirement("Lunar torso", ItemID.LUNAR_TORSO);
		gloves = new ItemRequirement("Lunar gloves", ItemID.LUNAR_GLOVES);
		boots = new ItemRequirement("Lunar boots", ItemID.LUNAR_BOOTS);
		legs = new ItemRequirement("Lunar legs", ItemID.LUNAR_LEGS);
	}

	public void setupConditions()
	{
		sealOfPassage = new ItemRequirement("Seal of passage", ItemID.SEAL_OF_PASSAGE);
		sealOfPassage.setTip("You can get another from Brundt");

		handedInHelm = new VarbitCondition(2436, 1);
		handedInCape = new VarbitCondition(2437, 1);
		handedInAmulet = new VarbitCondition(2438, 1);
		handedInTorso = new VarbitCondition(2439, 1);
		handedInGloves = new VarbitCondition(2441, 1);
		handedInBoots = new VarbitCondition(2440, 1);
		handedInLegs = new VarbitCondition(2442, 1);
		handedInRing = new VarbitCondition(2443, 1);
	}

	@Override
	public void startUp()
	{
		super.startUp();
		updateStep();
	}

	@Subscribe
	@Override
	public void onVarbitChanged(VarbitChanged varbitChanged)
	{
		super.onVarbitChanged(varbitChanged);
		updateStep();
	}

	public void updateStep()
	{
		this.requirements = new ArrayList<>();
		this.requirements.add(sealOfPassage);

		if (!handedInAmulet.checkCondition(client))
		{
			this.requirements.add(amulet);
		}
		if (!handedInBoots.checkCondition(client))
		{
			this.requirements.add(boots);
		}
		if (!handedInCape.checkCondition(client))
		{
			this.requirements.add(cape);
		}
		if (!handedInGloves.checkCondition(client))
		{
			this.requirements.add(gloves);
		}
		if (!handedInHelm.checkCondition(client))
		{
			this.requirements.add(helm);
		}
		if (!handedInLegs.checkCondition(client))
		{
			this.requirements.add(legs);
		}
		if (!handedInRing.checkCondition(client))
		{
			this.requirements.add(ring);
		}
		if (!handedInTorso.checkCondition(client))
		{
			this.requirements.add(torso);
		}
	}
}
