package com.questhelper.quests.myarmsbigadventure;

import com.questhelper.requirements.ItemRequirement;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.steps.ObjectStep;
import java.util.ArrayList;
import java.util.Collections;
import net.runelite.api.ItemID;
import net.runelite.api.NullObjectID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;

public class AddCompost extends ObjectStep
{
	ItemRequirement compost = new ItemRequirement("Supercompost",ItemID.SUPERCOMPOST, 7);

	public AddCompost(QuestHelper questHelper)
	{
		super(questHelper, NullObjectID.NULL_18867, new WorldPoint(2831, 3696, 0), "Add 7 supercompost on My Arm's soil patch.");
		this.addIcon(ItemID.SUPERCOMPOST);
		compost.setHighlightInInventory(true);
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		updateSteps();
	}

	protected void updateSteps()
	{
		int numCompToAdd = 7- client.getVarbitValue(2792);
		compost.setQuantity(numCompToAdd);
		this.setRequirements(new ArrayList<>(Collections.singletonList(compost)));
		this.setText("Add " + numCompToAdd + " supercompost on My Arm's soil patch.");
	}
}
