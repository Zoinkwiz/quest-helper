//package com.questhelper.quests.sinsofthefather;
//
//import net.runelite.api.*;
//import net.runelite.api.coords.WorldPoint;
//import net.runelite.client.plugins.questhelper.AxeRequirement;
//import net.runelite.client.plugins.questhelper.ItemRequirement;
//import net.runelite.client.plugins.questhelper.dialog.DialogueChoiceStep;
//import net.runelite.client.plugins.questhelper.questhelpers.QuestHelper;
//import net.runelite.client.plugins.questhelper.steps.MultiStageStep;
//import net.runelite.client.plugins.questhelper.steps.NpcTalkStep;
//import net.runelite.client.plugins.questhelper.steps.ObjectStep;
//import net.runelite.client.plugins.questhelper.steps.QuestStep;
//
//public class GetBlisterwoodStep extends MultiStageStep {
//
//    private enum Steps {
//        CUT_BLISTERWOOD(0),
//        SECOND_STEP(1);
//
//        private final int value;
//
//        Steps(int value) {
//            this.value = value;
//        }
//
//        public int getValue() {
//            return value;
//        }
//    }
//
//    QuestStep step2;
//
//    public GetBlisterwoodStep(QuestHelper questHelper, QuestStep step2) {
//        super(questHelper, null);
//        this.step2 = step2;
//        loadSteps();
//    }
//
//    @Override
//    public void startUp() {
//        updateSteps();
//    }
//
//    protected void updateSteps() {
//        if (hasLogsInInventory()) {
//            startUpStep(steps.get(Steps.SECOND_STEP.getValue()));
//        } else {
//            startUpStep(steps.get(Steps.CUT_BLISTERWOOD.getValue()));
//        }
//    }
//
//    public void gameTick() {
//    }
//
//    private boolean hasLogsInInventory() {
//        ItemContainer itemsContainer = client.getItemContainer(InventoryID.INVENTORY);
//        int sum = 0;
//        if(itemsContainer != null) {
//            Item[] items = itemsContainer.getItems();
//            for (Item item : items) {
//                if (item.getId() == ItemID.BLISTERWOOD_LOGS) {
//                    sum++;
//                }
//            }
//            if(sum >= 8) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    @Override
//    protected void loadSteps()
//    {
//        ItemRequirement chestKey = new ItemRequirement(ItemID.CHEST_KEY);
//        chestKey.setTip("You can get another one from Redbeard Frank");
//        ItemRequirement scentedTop =  new ItemRequirement(ItemID.VYRE_NOBLE_TOP, "You can get a replacement from a chest in Old Man Ral's basement.", true);
//        ItemRequirement scentedLegs = new ItemRequirement(ItemID.VYRE_NOBLE_LEGS, "You can get a replacement from a chest in Old Man Ral's basement.", true);
//        ItemRequirement scentedShoes = new ItemRequirement(ItemID.VYRE_NOBLE_SHOES, "You can get a replacement from a chest in Old Man Ral's basement.", true);
//
//        steps.put(Steps.CUT_BLISTERWOOD.getValue(), new ObjectStep(getQuestHelper(), ObjectID.BLISTERWOOD_TREE, new WorldPoint(3633, 3359, 0),
//                "Gather 8 logs from the Blisterwood tree.",
//                scentedTop, scentedLegs, scentedShoes, new ItemRequirement(ItemID.BLISTERWOOD_LOGS, 8),
//                new AxeRequirement()));
//
//        steps.put(Steps.SECOND_STEP.getValue(), step2);
//    }
//}