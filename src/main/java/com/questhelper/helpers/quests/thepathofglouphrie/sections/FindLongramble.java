package com.questhelper.helpers.quests.thepathofglouphrie.sections;

import com.questhelper.ItemCollections;
import com.questhelper.helpers.quests.thepathofglouphrie.ThePathOfGlouphrie;
import com.questhelper.requirements.item.TeleportItemRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.List;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;

public class FindLongramble
{
	public ConditionalStep talkToLongramble;
	public ConditionalStep talkToSpiritTree;
	public ObjectStep useCrystalChime;
	public ConditionalStep talkToSpiritTreeAgain;

	public void setup(ThePathOfGlouphrie quest)
	{
		var teleToLongramble = new TeleportItemRequirement("Fairy Ring BKP or Ring of Dueling to Castle Wars",
			ItemCollections.RING_OF_DUELINGS, 1);
		teleToLongramble.addAlternates(ItemCollections.FAIRY_STAFF);

		var goToLongramble = new ObjectStep(quest, ObjectID.TREE_49590, new WorldPoint(2333, 3081, 0), "");
		goToLongramble.addRecommended(quest.earmuffsOrSlayerHelmet);
		goToLongramble.addDialogStep("Castle Wars Arena.");
		goToLongramble.addTeleport(teleToLongramble);
		var actuallyTalkToLongramble = new NpcStep(quest, NpcID.LONGRAMBLE, new WorldPoint(2340, 3094, 0), "");
		actuallyTalkToLongramble.addRecommended(quest.earmuffsOrSlayerHelmet);


		talkToLongramble = new ConditionalStep(quest, goToLongramble,
			"Go to Longramble, make sure to head to a bank & gear up first. You can drop all leftover discs.",
			quest.combatGear, quest.crossbow.equipped().highlighted(), quest.mithGrapple.equipped().highlighted(),
			quest.prayerPotions, quest.food, quest.crystalChime);
		talkToLongramble.addStep(quest.nearLongramble, actuallyTalkToLongramble);

		{
			var talk = new ObjectStep(quest, NullObjectID.NULL_49598, new WorldPoint(2339, 3111, 0), "");
			talkToSpiritTree = new ConditionalStep(quest, talk, "Talk to the Spirit Tree",
				quest.combatGear, quest.prayerPotions, quest.food, quest.crystalChime);
		}

		useCrystalChime = new ObjectStep(quest, NullObjectID.NULL_49598, new WorldPoint(2339, 3111, 0),
			"Use the Crystal Chime on the Spirit Tree", quest.crystalChime.highlighted());
		useCrystalChime.addIcon(ItemID.CRYSTAL_CHIME);

		{
			var talk = new ObjectStep(quest, NullObjectID.NULL_49598, new WorldPoint(2339, 3111, 0), "");
			talkToSpiritTreeAgain = new ConditionalStep(quest, talk, "Talk to the Spirit Tree again",
				quest.combatGear, quest.prayerPotions, quest.food, quest.crystalChime);
		}
	}

	public List<QuestStep> getSteps()
	{
		return List.of(
			talkToLongramble, talkToSpiritTree, useCrystalChime, talkToSpiritTreeAgain
		);
	}
}
