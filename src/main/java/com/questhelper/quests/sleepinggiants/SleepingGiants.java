package com.questhelper.quests.sleepinggiants;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.widget.WidgetPresenceRequirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.FreeInventorySlotRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.widget.WidgetSpriteRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;

import com.questhelper.steps.WidgetDetails;
import com.questhelper.steps.WidgetStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(quest = QuestHelperQuest.SLEEPING_GIANTS)
@Slf4j
public class SleepingGiants extends BasicQuestHelper
{
	// Items Required
	ItemRequirement oakLogs, wool, nails, hammer, chisel, iceGloves;

	// Optional Items
	ItemRequirement alKharidTeleport, bucket, bucketOfWater;
	FreeInventorySlotRequirement freeInventorySpace;

	// Quest (Step-related) Items
	ItemRequirement oneOakLog, twoOakLogs, fiveNails, preform;

	// Primary Quest Line Steps
	QuestStep strikeHillGiant, speakToKovac, fixPolishingStone, fixGrindstone, fixHammer, speakToKovacAfterRepairs, searchCrate, fillCrucible, pourMetal, coolDownSword, speakToKovacContinue, interactWithMould, selectForteTab, selectForte, selectBladesTab, selectBlade, selectTipsTab, selectTip, setMould, forgeSword;

	// Optional & Supportive Quest Steps
	QuestStep goToDesertPlateau, takeHammer, enterFoundry, takeBucket, fillBucketWaterfall;

	ZoneRequirement onDesertPlateau, inGiantsFoundry, inNextDoorArea;

	VarbitRequirement grindstoneFixed, polishingStoneFixed, hammerFixed, commissionReceived, crateSearched, shouldSetMould, mouldSet, shouldPourMetal, metalPoured;

	WidgetPresenceRequirement selectingMould, noForteSelected, noBladeSelected, noTipSelected, forteTabOpen, bladeTabOpen, tipTabOpen;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupZones();
		setupItemRequirements();
		setupConditions();
		setupSteps();

		Map<Integer, QuestStep> steps = new HashMap<>();

		ConditionalStep startQuest = new ConditionalStep(this, strikeHillGiant);
		startQuest.addStep(new Conditions(LogicType.NOR, onDesertPlateau), goToDesertPlateau);
		startQuest.addStep(new Conditions(LogicType.NOR, hammer), takeHammer);
		steps.put(0, startQuest);

		ConditionalStep speakToKovacOnDesertPlateau = new ConditionalStep(this, speakToKovac);
		speakToKovacOnDesertPlateau.addStep(new Conditions(LogicType.NOR, onDesertPlateau), goToDesertPlateau);
		startQuest.addStep(new Conditions(LogicType.NOR, hammer), takeHammer);
		steps.put(5, speakToKovacOnDesertPlateau);

		ConditionalStep fixMachinery = new ConditionalStep(this, speakToKovacAfterRepairs);
		fixMachinery.addStep(new Conditions(LogicType.NOR, inGiantsFoundry), enterFoundry);
		fixMachinery.addStep(new Conditions(LogicType.NOR, polishingStoneFixed), fixPolishingStone);
		fixMachinery.addStep(new Conditions(LogicType.NOR, grindstoneFixed), fixGrindstone);
		fixMachinery.addStep(new Conditions(LogicType.NOR, hammerFixed), fixHammer);
		steps.put(10, fixMachinery);
		steps.put(15, fixMachinery);

		ConditionalStep speakToKovacContinueAfterRepairs = new ConditionalStep(this, speakToKovacAfterRepairs);
		speakToKovacContinueAfterRepairs.addStep(new Conditions(LogicType.NOR, inGiantsFoundry), enterFoundry);
		steps.put(20, speakToKovacContinueAfterRepairs);

		// TODO: This needs validation, as it's a very complex varbit-base step. It is very possible that the varbits are not checked properly.
		ConditionalStep speakToKovacForCommission = new ConditionalStep(this, speakToKovacContinue);
		speakToKovacForCommission.addStep(new Conditions(LogicType.NOR, inGiantsFoundry), enterFoundry);
		speakToKovacForCommission.addStep(new Conditions(commissionReceived), searchCrate);
		speakToKovacForCommission.addStep(new Conditions(crateSearched), fillCrucible);
		speakToKovacForCommission.addStep(new Conditions(selectingMould, noForteSelected, forteTabOpen), selectForte);
		speakToKovacForCommission.addStep(new Conditions(selectingMould, noForteSelected), selectForteTab);
		speakToKovacForCommission.addStep(new Conditions(selectingMould, noBladeSelected, bladeTabOpen), selectBlade);
		speakToKovacForCommission.addStep(new Conditions(selectingMould, noBladeSelected), selectBladesTab);
		speakToKovacForCommission.addStep(new Conditions(selectingMould, noTipSelected, tipTabOpen), selectTip);
		speakToKovacForCommission.addStep(new Conditions(selectingMould, noTipSelected), selectBlade);
		speakToKovacForCommission.addStep(new Conditions(selectingMould), setMould);
		speakToKovacForCommission.addStep(new Conditions(shouldSetMould), interactWithMould);
		speakToKovacForCommission.addStep(new Conditions(mouldSet), speakToKovac);
		speakToKovacForCommission.addStep(new Conditions(shouldPourMetal), pourMetal);
		speakToKovacForCommission.addStep(new Conditions(metalPoured, new Conditions(LogicType.OR, iceGloves, bucketOfWater)), coolDownSword);
		speakToKovacForCommission.addStep(new Conditions(metalPoured, bucket), fillBucketWaterfall);
		speakToKovacForCommission.addStep(new Conditions(metalPoured, new Conditions(LogicType.NOR, bucket)), takeBucket);
		speakToKovacForCommission.addStep(new Conditions(metalPoured, new Conditions(LogicType.NOR, bucket)), takeBucket);
		speakToKovacForCommission.addStep(new Conditions(preform), forgeSword);

		steps.put(25, speakToKovacForCommission);

		return steps;
	}

	public void setupItemRequirements()
	{
		oakLogs = new ItemRequirement("Oak Logs", ItemID.OAK_LOGS);
		oakLogs.setQuantity(3);

		wool = new ItemRequirement("Wool", ItemID.WOOL);

		nails = new ItemRequirement("Nails", ItemID.BRONZE_NAILS);
		nails.addAlternates(ItemID.IRON_NAILS, ItemID.STEEL_NAILS, ItemID.BLACK_NAILS, ItemID.MITHRIL_NAILS, ItemID.ADAMANTITE_NAILS, ItemID.RUNE_NAILS);
		nails.setQuantity(10);

		hammer = new ItemRequirement("Hammer", ItemID.HAMMER);
		hammer.canBeObtainedDuringQuest();

		chisel = new ItemRequirement("Chisel", ItemID.CHISEL);

		iceGloves = new ItemRequirement("Ice Gloves", ItemID.ICE_GLOVES);
		iceGloves.setTooltip("Allows you to skip (filling and) using a bucket of water.");

		bucket = new ItemRequirement("Bucket", ItemID.BUCKET);
		bucket.canBeObtainedDuringQuest();
		bucket.setHighlightInInventory(true);

		bucketOfWater = new ItemRequirement("Bucket of Water", ItemID.BUCKET_OF_WATER);
		bucketOfWater.canBeObtainedDuringQuest();

		freeInventorySpace = new FreeInventorySlotRequirement(InventoryID.INVENTORY, 20);

		alKharidTeleport = new ItemRequirement("Al Kharid Teleport", ItemCollections.getRingOfDuelings());
		alKharidTeleport.addAlternates(ItemCollections.getAmuletOfGlories());
		alKharidTeleport.setTooltip("Ring of Dueling or Amulet of Glory; Can be used to quickly reach the Giants' Foundry.");

		twoOakLogs = new ItemRequirement("Oak Logs", ItemID.OAK_LOGS);
		twoOakLogs.setQuantity(2);

		fiveNails = new ItemRequirement("Nails", ItemID.BRONZE_NAILS);
		fiveNails.addAlternates(ItemID.IRON_NAILS, ItemID.STEEL_NAILS, ItemID.BLACK_NAILS, ItemID.MITHRIL_NAILS, ItemID.ADAMANTITE_NAILS, ItemID.RUNE_NAILS);
		fiveNails.setQuantity(5);

		oneOakLog = new ItemRequirement("Oak Logs", ItemID.OAK_LOGS);

		// TODO: This might not be the right Item ID.
		preform = new ItemRequirement("Preform", 27010);
		preform.equipped();
	}

	public void setupZones()
	{
		Zone desertPlateau = new Zone(new WorldPoint(3375, 3169, 0), new WorldPoint(3349, 3143, 0));
		onDesertPlateau = new ZoneRequirement(desertPlateau);

		Zone giantsFoundry = new Zone(new WorldPoint(3357, 11480, 0), new WorldPoint(3380, 11510, 0));
		inGiantsFoundry = new ZoneRequirement(giantsFoundry);
	}

	public void setupConditions()
	{
		grindstoneFixed = new VarbitRequirement(13905, 2, Operation.EQUAL);
		polishingStoneFixed = new VarbitRequirement(13906, 2, Operation.EQUAL);
		hammerFixed = new VarbitRequirement(13904, 2, Operation.EQUAL);

		commissionReceived = new VarbitRequirement(13903, 10, Operation.EQUAL);
		crateSearched = new VarbitRequirement(13903, 25, Operation.LESS_EQUAL);
		shouldSetMould = new VarbitRequirement(13903, 30, Operation.LESS_EQUAL);
		mouldSet = new VarbitRequirement(13903, 35, Operation.EQUAL);
		shouldPourMetal = new VarbitRequirement(13903, 40, Operation.EQUAL);
		metalPoured = new VarbitRequirement(13903, 45, Operation.EQUAL);

		selectingMould = new WidgetPresenceRequirement(718, 2);
		noForteSelected = new WidgetSpriteRequirement(718, 21, 3593);
		noBladeSelected = new WidgetSpriteRequirement(718, 22, 3593);
		noTipSelected = new WidgetSpriteRequirement(718, 23, 3593);
		forteTabOpen = new WidgetSpriteRequirement(718, 12, 1, 297);
		bladeTabOpen = new WidgetSpriteRequirement(718, 12, 10, 297);
		tipTabOpen = new WidgetSpriteRequirement(718, 12, 19, 297);
	}

	public void setupSteps()
	{
		goToDesertPlateau = new DetailedQuestStep(this, new WorldPoint(3439, 9225, 0), "Go to the Desert Plateau to start the quest.", oakLogs, wool, nails, chisel, iceGloves);
		((DetailedQuestStep) goToDesertPlateau).setLinePoints(Arrays.asList(new WorldPoint(3293, 3180, 0), new WorldPoint(3306, 3180, 0), new WorldPoint(3313, 3171, 0), new WorldPoint(3320, 3171, 0), new WorldPoint(3333, 3175, 0), new WorldPoint(3339, 3175, 0), new WorldPoint(3343, 3173, 0), new WorldPoint(3353, 3173, 0), new WorldPoint(3364, 3184, 0), new WorldPoint(3376, 3184, 0), new WorldPoint(3381, 3178, 0), new WorldPoint(3381, 3175, 0), new WorldPoint(3375, 3170, 0)));

		takeHammer = new DetailedQuestStep(this, new WorldPoint(3350, 3162, 0), "Take a hammer from the crate north of the Giants' Foundry.");
		takeHammer.addIcon(ItemID.HAMMER);

		strikeHillGiant = new NpcStep(this, 11467 /* Hill Giant */, new WorldPoint(3361, 3147, 0), "Attempt to Strike the Hill Giant to start the quest.");
		speakToKovac = new NpcStep(this, 11468 /* Kovac */, new WorldPoint(3361, 3147, 0), "Speak to Kovac.");

		enterFoundry = new ObjectStep(this, 44635 /* Cave */, new WorldPoint(3361, 3150, 0), "Enter the Giants' Foundry.");

		fixPolishingStone = new ObjectStep(this, 44775 /* Broken polishing wheel */, new WorldPoint(3363, 11485, 0), "Fix the Broken polishing wheel", twoOakLogs, fiveNails, hammer);
		fixPolishingStone.addDialogStep("Yes.");
		fixGrindstone = new ObjectStep(this, 44774 /* Broken grindstone */, new WorldPoint(3362, 11492, 0), "Fix the Broken grindstone", chisel);
		fixGrindstone.addDialogStep("Yes.");
		fixHammer = new ObjectStep(this, 44773 /* Broken trip hammer */, new WorldPoint(3364, 11497, 0), "Fix the Broken trip hammer", oneOakLog, fiveNails, hammer);
		fixHammer.addDialogStep("Yes.");

		speakToKovacAfterRepairs = new NpcStep(this, 11468 /* Kovac */, "Speak to Kovac to continue.");
		speakToKovacContinue = new NpcStep(this, 11470 /* Kovac */, "Speak to Kovac to continue.");

		searchCrate = new ObjectStep(this, 44779 /* Crate */, new WorldPoint(3370, 11483, 0), "Take some bars & weapons from the Crate.", freeInventorySpace);
		searchCrate.addDialogStep("Yes.");

		fillCrucible = new ObjectStep(this, 44776 /* Crucible (empty */, "Fill the Crucible with items & bars until it is full.");
		interactWithMould = new ObjectStep(this, 44777 /* Mould jig (Empty) */, "Select the mould for the forte, blade and tip.");

		selectForteTab = new WidgetStep(this, "Select the Forte tab", new WidgetDetails(718, 12, 1));
		selectForte = new WidgetStep(this, "Select the Forte that matches your commission best", 718, 9);
		selectBladesTab = new WidgetStep(this, "Select the Blades tab", new WidgetDetails(718, 12, 9));
		selectBlade = new WidgetStep(this, "Select the Blade that matches your commission best", 718, 9);
		selectTipsTab = new WidgetStep(this, "Select the Tips tab", new WidgetDetails(718, 12, 18));
		selectTip = new WidgetStep(this, "Select the Tip that matches your commission best", 718, 9);
		setMould = new WidgetStep(this, "Set the Mould", 718, 6);

		pourMetal = new ObjectStep(this, 44776 /* Crucible (empty */, "Pour the full Crucible into the mould.");

		coolDownSword = new ObjectStep(this, 44777 /* Mould jig (Poured Metal) */, "Cool down the sword");
		fillBucketWaterfall = new ObjectStep(this, 44632 /* Waterfall */, "Fill the bucket with water by using it on the Waterfall.");

		takeBucket = new ObjectStep(this, 33309 /* Pile of Buckets */, "Take a Bucket from the Pile of Buckets");

		forgeSword = new NpcStep(this, 11470 /* Kovac */, "Forge the sword using the on-screen instructions, and hand it in to complete the quest.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(oakLogs, wool, nails, hammer, chisel);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(iceGloves, alKharidTeleport);
	}

	public List<Requirement> getGeneralRecommended()
	{
		return Arrays.asList(freeInventorySpace);
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Collections.singletonList(new ExperienceReward(Skill.SMITHING, 6000));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Collections.singletonList(new UnlockReward("Access to the Giants' Foundry"));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting out", Arrays.asList(strikeHillGiant, speakToKovac, enterFoundry, fixPolishingStone, fixGrindstone, fixHammer, speakToKovacAfterRepairs, speakToKovacContinue, searchCrate), Arrays.asList(oakLogs, wool, nails, hammer, chisel), Arrays.asList(freeInventorySpace)));
		allSteps.add(new PanelDetails("Creating the sword", Arrays.asList(fillCrucible, setMould, pourMetal, coolDownSword, forgeSword), Arrays.asList(iceGloves, bucket)));

		return allSteps;
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return Collections.singletonList(new SkillRequirement(Skill.SMITHING, 15));
	}
}
