package com.questhelper.helpers.quests.myarmsbigadventure;

import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.steps.ObjectStep;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.client.eventbus.Subscribe;

import java.util.Arrays;

public class AddDung extends ObjectStep
{
	ItemRequirement dung = new ItemRequirement("Ugthanki dung", ItemID.FEUD_CAMEL_POOH_BUCKET, 3);
	ItemRequirement spade = new ItemRequirement("Spade", ItemID.SPADE);

	public AddDung(QuestHelper questHelper)
	{
		super(questHelper, ObjectID.MYARM_FAKEFARMINGPATCH, new WorldPoint(2831, 3696, 0), "Add 3 ugthanki dung on My Arm's soil patch.");
		this.addIcon(ItemID.FEUD_CAMEL_POOH_BUCKET);
		dung.setTooltip("You can get some by feeding the camels in Pollnivneach hot sauce, then using a bucket on their dung");
		dung.setHighlightInInventory(true);
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		updateSteps();
	}

	protected void updateSteps()
	{
		int numCompToAdd = 3 - client.getVarbitValue(2791);
		dung.setQuantity(numCompToAdd);
		this.setRequirements(Arrays.asList(dung, spade));
		this.setText("Add " + numCompToAdd + " ugthanki dung on My Arm's soil patch.");
	}
}

