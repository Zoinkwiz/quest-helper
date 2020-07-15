//package com.questhelper.quests.sinsofthefather;
//
//import net.runelite.api.*;
//import net.runelite.api.coords.WorldPoint;
//import net.runelite.client.plugins.questhelper.ItemRequirement;
//import net.runelite.client.plugins.questhelper.Zone;
//import net.runelite.client.plugins.questhelper.dialog.DialogueChoiceStep;
//import net.runelite.client.plugins.questhelper.questhelpers.QuestHelper;
//import net.runelite.client.plugins.questhelper.steps.ItemInteractionStep;
//import net.runelite.client.plugins.questhelper.steps.MultiStageStep;
//import net.runelite.client.plugins.questhelper.steps.NpcTalkStep;
//import net.runelite.client.plugins.questhelper.steps.ObjectStep;
//
//public class TempleTrekStep extends MultiStageStep {
//    private Zone[] zones;
//
//    private enum Steps {
//        START(0),
//        SWING_ROOM_KNIFE(1),
//        SWING_ROOM_CUT(2),
//        SWING_ROOM_ASSEMBLE(3),
//        SWING_ROOM_SWING(4),
//        NAIL_BEAST_ROOM(5),
//        BRIDGE_ROOM_KILL(6),
//        BRIDGE_ROOM_CUT(7),
//        BRIDGE_ROOM_REPAIR(8),
//        JUVENILES_ROOM(9);
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
//    public TempleTrekStep(QuestHelper questHelper) {
//        super(questHelper, null);
//        loadSteps();
//    }
//
//    @Override
//    public void startUp()
//    {
//        loadZones();
//        updateSteps();
//    }
//
//    protected void updateSteps() {
//        WorldPoint location = client.getLocalPlayer().getWorldLocation();
//        hasSet = false;
//        if (zones[0].contains(location)) {
//            if (hasItem(ItemID.LONG_VINE)) {
//                startUpStep(steps.get(Steps.SWING_ROOM_SWING.getValue()));
//            } else if (hasItem(ItemID.SHORT_VINE, 3)) {
//                startUpStep(steps.get(Steps.SWING_ROOM_ASSEMBLE.getValue()));
//            } else if (hasItem(ItemID.KNIFE)) {
//                startUpStep(steps.get(Steps.SWING_ROOM_CUT.getValue()));
//            } else {
//                startUpStep(steps.get(Steps.SWING_ROOM_KNIFE.getValue()));
//            }
//        } else if (zones[1].contains(location)) {
//            startUpStep(steps.get(Steps.NAIL_BEAST_ROOM.getValue()));
//        } else if (zones[2].contains(location)) {
//            if (hasLogsOrPlanks()) {
//                startUpStep(steps.get(Steps.BRIDGE_ROOM_REPAIR.getValue()));
//            } else if(hasAxe()) {
//                startUpStep(steps.get(Steps.BRIDGE_ROOM_CUT.getValue()));
//            } else {
//                startUpStep(steps.get(Steps.BRIDGE_ROOM_KILL.getValue()));
//            }
//        } else if (zones[3].contains(location)) {
//            startUpStep(steps.get(Steps.JUVENILES_ROOM.getValue()));
//        } else {
//          startUpStep(steps.get(Steps.START.getValue()));
//        }
//    }
//
//    public void gameTick() {
//    }
//
//    private boolean hasItem(int itemId) {
//        Item[] items = client.getItemContainer(InventoryID.INVENTORY).getItems();
//        for (Item item : items)
//        {
//            if (item.getId() == itemId)
//            {
//                return true;
//            }
//        }
//        Item[] wornItems = client.getItemContainer(InventoryID.EQUIPMENT).getItems();
//        for (Item item : wornItems)
//        {
//            if (item.getId() == itemId)
//            {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    private boolean hasItem(int itemId, int minimum) {
//        int total = 0;
//        Item[] items = client.getItemContainer(InventoryID.INVENTORY).getItems();
//        for (Item item : items)
//        {
//            if (item.getId() == itemId)
//            {
//                total++;
//            }
//        }
//        Item[] wornItems = client.getItemContainer(InventoryID.EQUIPMENT).getItems();
//        for (Item item : wornItems)
//        {
//            if (item.getId() == itemId)
//            {
//                total++;
//            }
//        }
//        if(total >= minimum) {
//            return true;
//        }
//        return false;
//    }
//
//    private boolean hasAxe() {
//        if (hasItem(ItemID.BRONZE_AXE)
//                || hasItem(ItemID.IRON_AXE)
//                || hasItem(ItemID.STEEL_AXE)
//                || hasItem(ItemID.BLACK_AXE)
//                || hasItem(ItemID.MITHRIL_AXE)
//                || hasItem(ItemID.ADAMANT_AXE)
//                || hasItem(ItemID.GILDED_AXE)
//                || hasItem(ItemID.RUNE_AXE)
//                || hasItem(ItemID.DRAGON_AXE)
//                || hasItem(ItemID.INFERNAL_AXE)
//                || hasItem(ItemID._3RD_AGE_AXE)
//                || hasItem(ItemID.CRYSTAL_AXE)) {
//            return true;
//        }
//        return false;
//    }
//
//    private boolean hasLogsOrPlanks() {
//        if (hasItem(ItemID.PLANK, 3)
//                || hasItem(ItemID.LOGS, 3)) {
//            return true;
//        }
//        return false;
//    }
//
//    @Override
//    protected void loadSteps()
//    {
//        steps.put(Steps.START.getValue(), new NpcTalkStep(getQuestHelper(), 9530, new WorldPoint(3444, 3485, 0),
//                "Speak to Ivan Strom outside the east entrance of Paterdomus, and go on a Temple Trek with him."));
//        steps.put(Steps.SWING_ROOM_KNIFE.getValue(), new ObjectStep(getQuestHelper(), ObjectID.BACKPACK, new WorldPoint(6662, 843, 0),
//                "Search the backpack for a knife."));
//        steps.put(Steps.SWING_ROOM_CUT.getValue(), new ObjectStep(getQuestHelper(), ObjectID.SWAMP_TREE_13847, new WorldPoint(6672, 843, 0),
//                "Cut three vines from the Swamp Tree."));
//        steps.put(Steps.SWING_ROOM_CUT.getValue(), new ObjectStep(getQuestHelper(), ObjectID.SWAMP_TREE_13847, new WorldPoint(6672, 843, 0),
//                "Cut three vines from the Swamp Tree."));
//        steps.put(Steps.SWING_ROOM_ASSEMBLE.getValue(), new ItemInteractionStep(getQuestHelper(), "Combine the short vines in your inventory together.",
//                new ItemRequirement(ItemID.SHORT_VINE)));
//        steps.put(Steps.SWING_ROOM_SWING.getValue(), new ObjectStep(getQuestHelper(), 38004, new WorldPoint(6669, 846, 0),
//                "Use the long vine on the Swamp tree branch and use it to swing across. Continue along the path."));
//        steps.get(Steps.SWING_ROOM_SWING.getValue()).addDialogueStep(new DialogueChoiceStep("Yes."));
//
//        /* 9612, 9613 Nail Beast IDs*/
//        steps.put(Steps.NAIL_BEAST_ROOM.getValue(), new NpcTalkStep(getQuestHelper(), 9612, new WorldPoint(9159, 4324, 0),
//                "Kill the Nail Beasts and and continue along the path."));
//
//        steps.put(Steps.BRIDGE_ROOM_KILL.getValue(), new NpcTalkStep(getQuestHelper(), NpcID.ZOMBIE_5647, new WorldPoint(11655, 4876, 0),
//                "Kill the Zombie for an axe."));
//        steps.put(Steps.BRIDGE_ROOM_CUT.getValue(),  new ObjectStep(getQuestHelper(), ObjectID.DEAD_TREE_1365, new WorldPoint(11649, 4868, 0),
//                "Cut three logs to repair the bridge."));
//        steps.put(Steps.BRIDGE_ROOM_REPAIR.getValue(), new ObjectStep(getQuestHelper(), ObjectID.BROKEN_BRIDGE_13834, new WorldPoint(11658, 4871, 0),
//                "Repair the bridge, cross over, and continue along the path."));
//
//        /* 9614, 9615*/
//        steps.put(Steps.JUVENILES_ROOM.getValue(), new NpcTalkStep(getQuestHelper(), 9614, new WorldPoint(11655, 4876, 0),
//                "Kill the Zombie for an axe."));
//    }
//
//    private void loadZones()
//    {
//        zones = new Zone[]{
//                new Zone(new WorldPoint(6675, 883, 0), new WorldPoint(6656, 857, 0)), // Vine
//                new Zone(new WorldPoint(9149, 4313, 0), new WorldPoint(9175, 4285, 0)), // Nail Beasts
//                new Zone(new WorldPoint(11646, 4883, 0), new WorldPoint(11674, 4860, 0)),  // Bridge
//                new Zone(new WorldPoint(6675, 883, 0), new WorldPoint(6656, 857, 0)) // Juveniles
//        };
//    }
//}
