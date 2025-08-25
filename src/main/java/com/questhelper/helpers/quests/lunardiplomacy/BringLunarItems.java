package com.questhelper.helpers.quests.lunardiplomacy;

import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.steps.NpcStep;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.VarbitID;

public class BringLunarItems extends NpcStep
{
	Requirement handedInHelm, handedInCape, handedInAmulet, handedInTorso, handedInGloves, handedInBoots, handedInLegs, handedInRing;

	ItemRequirement helm, cape, amulet, torso, gloves, boots, legs, ring, sealOfPassage;

	public BringLunarItems(QuestHelper questHelper)
	{
		super(questHelper, NpcID.LUNAR_ONEIROMANCER, new WorldPoint(2151, 3867, 0),
			"Bring all the items to the Oneiromancer in the south east of Lunar Isle.");
		setupRequirements();
		setupConditions();
	}

	public void setupRequirements()
	{
		helm = new ItemRequirement("Lunar helm", ItemID.LUNAR_HELMET);
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
		sealOfPassage = new ItemRequirement("Seal of passage", ItemID.LUNAR_SEAL_OF_PASSAGE);
		sealOfPassage.setTooltip("You can get another from Brundt");

		handedInHelm = new VarbitRequirement(VarbitID.LUNAR_PT2_ONEIRO_GIVEN_HELM, 1);
		handedInCape = new VarbitRequirement(VarbitID.LUNAR_PT2_ONEIRO_GIVEN_CAPE, 1);
		handedInAmulet = new VarbitRequirement(VarbitID.LUNAR_PT2_ONEIRO_GIVEN_AMULET, 1);
		handedInTorso = new VarbitRequirement(VarbitID.LUNAR_PT2_ONEIRO_GIVEN_TORSO, 1);
		handedInGloves = new VarbitRequirement(VarbitID.LUNAR_PT2_ONEIRO_GIVEN_BOOTS, 1);
		handedInBoots = new VarbitRequirement(VarbitID.LUNAR_PT2_ONEIRO_GIVEN_GLOVES, 1);
		handedInLegs = new VarbitRequirement(VarbitID.LUNAR_PT2_ONEIRO_GIVEN_TROUSERS, 1);
		handedInRing = new VarbitRequirement(VarbitID.LUNAR_PT2_ONEIRO_GIVEN_RING, 1);
	}

	@Override
	public void startUp()
	{
		super.startUp();
		updateStep();
	}

	@Override
	public void onVarbitChanged(VarbitChanged varbitChanged)
	{
		super.onVarbitChanged(varbitChanged);
		updateStep();
	}

	public void updateStep()
	{
		emptyRequirements();
		addRequirement(sealOfPassage);

		if (!handedInAmulet.check(client))
		{
			addRequirement(amulet);
		}
		if (!handedInBoots.check(client))
		{
			addRequirement(boots);
		}
		if (!handedInCape.check(client))
		{
			addRequirement(cape);
		}
		if (!handedInGloves.check(client))
		{
			addRequirement(gloves);
		}
		if (!handedInHelm.check(client))
		{
			addRequirement(helm);
		}
		if (!handedInLegs.check(client))
		{
			addRequirement(legs);
		}
		if (!handedInRing.check(client))
		{
			addRequirement(ring);
		}
		if (!handedInTorso.check(client))
		{
			addRequirement(torso);
		}
	}
}
