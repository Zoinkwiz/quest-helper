package com.questhelper.quests.lunardiplomacy;

import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.steps.NpcStep;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.eventbus.Subscribe;

public class BringLunarItems extends NpcStep
{
	Requirement handedInHelm, handedInCape, handedInAmulet, handedInTorso, handedInGloves, handedInBoots, handedInLegs, handedInRing;

	ItemRequirement helm, cape, amulet, torso, gloves, boots, legs, ring, sealOfPassage;

	public BringLunarItems(QuestHelper questHelper)
	{
		super(questHelper, NpcID.ONEIROMANCER, new WorldPoint(2151, 3867, 0),
			"Bring all the items to the Oneiromancer in the south east of Lunar Isle.");
		setupRequirements();
		setupConditions();
	}

	public void setupRequirements()
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
		sealOfPassage.setTooltip("You can get another from Brundt");

		handedInHelm = new VarbitRequirement(2436, 1);
		handedInCape = new VarbitRequirement(2437, 1);
		handedInAmulet = new VarbitRequirement(2438, 1);
		handedInTorso = new VarbitRequirement(2439, 1);
		handedInGloves = new VarbitRequirement(2441, 1);
		handedInBoots = new VarbitRequirement(2440, 1);
		handedInLegs = new VarbitRequirement(2442, 1);
		handedInRing = new VarbitRequirement(2443, 1);
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
