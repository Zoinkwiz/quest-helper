package com.questhelper.quests.deserttreasure;

import com.questhelper.requirements.ItemRequirement;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.steps.NpcStep;
import java.util.ArrayList;
import net.runelite.api.ItemID;
import net.runelite.api.NullItemID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.eventbus.Subscribe;

public class GiveItems extends NpcStep
{
	int magicLogId = 365;
	int steelBarsId = 366;
	int moltenGlassId = 367;
	int bonesId = 368;
	int ashesId = 369;
	int charcoalId = 370;
	int bloodRuneId = 371;


	ItemRequirement magicLogs, steelBars, moltenGlass, ashes, charcoal, bloodRune, bones;

	public GiveItems(QuestHelper questHelper, int npcID, WorldPoint worldPoint, String text, ItemRequirement... itemRequirements)
	{
		super(questHelper, npcID, worldPoint, text, itemRequirements);
		magicLogs = new ItemRequirement("Magic logs", ItemID.MAGIC_LOGS, 12);
		magicLogs.addAlternates(NullItemID.NULL_1514);
		steelBars = new ItemRequirement("Steel bar", ItemID.STEEL_BAR,  6);
		steelBars.addAlternates(NullItemID.NULL_2354);
		moltenGlass = new ItemRequirement("Molten glass", ItemID.MOLTEN_GLASS, 6);
		moltenGlass.addAlternates(NullItemID.NULL_1776);
		ashes = new ItemRequirement("Ashes", ItemID.ASHES);
		charcoal = new ItemRequirement("Charcoal", ItemID.CHARCOAL);
		bloodRune = new ItemRequirement("Blood rune", ItemID.BLOOD_RUNE);
		bones = new ItemRequirement("Bones", ItemID.BONES);
	}

	@Override
	public void startUp()
	{
		super.startUp();
		itemQuantitiesLeft();
	}

	@Subscribe
	@Override
	public void onVarbitChanged(VarbitChanged varbitChanged)
	{
		super.onVarbitChanged(varbitChanged);
		itemQuantitiesLeft();
	}

	private void itemQuantitiesLeft()
	{
		requirements = new ArrayList<>();
		if (client.getVarbitValue(bonesId) != 1)
		{
			requirements.add(bones);
		}
		if (client.getVarbitValue(bloodRuneId) != 1)
		{
			requirements.add(bloodRune);
		}
		if (client.getVarbitValue(ashesId) != 1)
		{
			requirements.add(ashes);
		}
		if (client.getVarbitValue(charcoalId) != 1)
		{
			requirements.add(charcoal);
		}
		if (client.getVarbitValue(magicLogId) != 12)
		{
			magicLogs.setQuantity(12 - client.getVarbitValue(magicLogId));
			requirements.add(magicLogs);
		}
		if (client.getVarbitValue(steelBarsId) != 6)
		{
			steelBars.setQuantity(6 - client.getVarbitValue(steelBarsId));
			requirements.add(steelBars);
		}
		if (client.getVarbitValue(moltenGlassId) != 6)
		{
			moltenGlass.setQuantity(6 - client.getVarbitValue(moltenGlassId));
			requirements.add(moltenGlass);
		}
		if (requirements.isEmpty())
		{
			setText("Talk to Eblis in the east of the Bandit Camp.");
		}
	}
}
