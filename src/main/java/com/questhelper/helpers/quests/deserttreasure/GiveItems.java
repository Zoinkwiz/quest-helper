package com.questhelper.helpers.quests.deserttreasure;

import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.steps.NpcStep;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.VarbitID;

public class GiveItems extends NpcStep
{
	ItemRequirement magicLogs, steelBars, moltenGlass, ashes, charcoal, bloodRune, bones;

	public GiveItems(QuestHelper questHelper, int npcID, WorldPoint worldPoint, String text, ItemRequirement... itemRequirements)
	{
		super(questHelper, npcID, worldPoint, text, itemRequirements);
		magicLogs = new ItemRequirement("Magic logs", ItemID.MAGIC_LOGS, 12);
		magicLogs.addAlternates(ItemID.Cert.MAGIC_LOGS);
		steelBars = new ItemRequirement("Steel bar", ItemID.STEEL_BAR,  6);
		steelBars.addAlternates(ItemID.Cert.STEEL_BAR);
		moltenGlass = new ItemRequirement("Molten glass", ItemID.MOLTEN_GLASS, 6);
		moltenGlass.addAlternates(ItemID.Cert.MOLTEN_GLASS);
		ashes = new ItemRequirement("Ashes", ItemID.ASHES);
		charcoal = new ItemRequirement("Charcoal", ItemID.CHARCOAL);
		bloodRune = new ItemRequirement("Blood rune", ItemID.BLOODRUNE);
		bones = new ItemRequirement("Bones", ItemID.BONES);
	}

	@Override
	public void startUp()
	{
		super.startUp();
		itemQuantitiesLeft();
	}

	@Override
	public void onVarbitChanged(VarbitChanged varbitChanged)
	{
		super.onVarbitChanged(varbitChanged);
		itemQuantitiesLeft();
	}

	private void itemQuantitiesLeft()
	{
		emptyRequirements();
		if (client.getVarbitValue(VarbitID.FD_BONES) != 1)
		{
			addRequirement(bones);
		}
		if (client.getVarbitValue(VarbitID.FD_BLOODRUNE) != 1)
		{
			addRequirement(bloodRune);
		}
		if (client.getVarbitValue(VarbitID.FD_ASH) != 1)
		{
			addRequirement(ashes);
		}
		if (client.getVarbitValue(VarbitID.FD_CHARCOAL) != 1)
		{
			addRequirement(charcoal);
		}
		if (client.getVarbitValue(VarbitID.FD_MAGICLOG) != 12)
		{
			magicLogs.setQuantity(12 - client.getVarbitValue(VarbitID.FD_MAGICLOG));
			addRequirement(magicLogs);
		}
		if (client.getVarbitValue(VarbitID.FD_STEELBAR) != 6)
		{
			steelBars.setQuantity(6 - client.getVarbitValue(VarbitID.FD_STEELBAR));
			addRequirement(steelBars);
		}
		if (client.getVarbitValue(VarbitID.FD_GLASS) != 6)
		{
			moltenGlass.setQuantity(6 - client.getVarbitValue(VarbitID.FD_GLASS));
			addRequirement(moltenGlass);
		}
		if (getRequirements().isEmpty())
		{
			setText("Talk to Eblis in the east of the Bandit Camp.");
		}
	}
}
