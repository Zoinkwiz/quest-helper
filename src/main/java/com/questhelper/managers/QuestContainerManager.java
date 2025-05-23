/*
 * Copyright (c) 2017, Tyler <http://github.com/tylerthardy>
 * Copyright (c) 2022, Adam <Adam@sigterm.info>
 * Copyright (c) 2024, Zoinkwiz <https://github.com/Zoinkwiz>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.questhelper.managers;

import com.questhelper.requirements.item.TrackedContainers;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.annotations.Varbit;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.VarbitID;

import java.util.*;

public class QuestContainerManager
{
    @Getter
    private final static ItemAndLastUpdated equippedData = new ItemAndLastUpdated(TrackedContainers.EQUIPPED);

    @Getter
    private final static ItemAndLastUpdated inventoryData = new ItemAndLastUpdated(TrackedContainers.INVENTORY);

    @Getter
    private final static ItemAndLastUpdated bankData = new ItemAndLastUpdated(TrackedContainers.BANK);

    @Getter
    private final static ItemAndLastUpdated potionData = new ItemAndLastUpdated(TrackedContainers.POTION_STORAGE);

    @Getter
    private final static ItemAndLastUpdated groupStorageData = new ItemAndLastUpdated(TrackedContainers.GROUP_STORAGE);

    @Getter
    private final static ItemAndLastUpdated runePouchData = new ItemAndLastUpdated(TrackedContainers.RUNE_POUCH);

    @Getter
    private final static List<ItemAndLastUpdated> orderedListOfContainers = List.of(equippedData, inventoryData, bankData, runePouchData, potionData, groupStorageData);

    static Set<Integer> RUNE_POUCHES = Set.of(ItemID.BH_RUNE_POUCH, ItemID.BH_RUNE_POUCH_TROUVER, ItemID.DIVINE_RUNE_POUCH, ItemID.DIVINE_RUNE_POUCH_TROUVER);
    private static final int NUM_SLOTS = 6;
    private static final int[] AMOUNT_VARBITS = {
            VarbitID.RUNE_POUCH_QUANTITY_1, VarbitID.RUNE_POUCH_QUANTITY_2, VarbitID.RUNE_POUCH_QUANTITY_3, VarbitID.RUNE_POUCH_QUANTITY_4,
            VarbitID.RUNE_POUCH_QUANTITY_5, VarbitID.RUNE_POUCH_QUANTITY_6
    };
    private static final int[] RUNE_VARBITS = {
            VarbitID.RUNE_POUCH_TYPE_1, VarbitID.RUNE_POUCH_TYPE_2, VarbitID.RUNE_POUCH_TYPE_3, VarbitID.RUNE_POUCH_TYPE_4,
            VarbitID.RUNE_POUCH_TYPE_5, VarbitID.RUNE_POUCH_TYPE_6
    };

    private static final Set<Integer> ALL_RUNE_POUCH_VARBITS = new HashSet<>();

    static
    {
        for (var varbit : AMOUNT_VARBITS)
        {
            ALL_RUNE_POUCH_VARBITS.add(varbit);
        }

        for (var varbit : RUNE_VARBITS)
        {
            ALL_RUNE_POUCH_VARBITS.add(varbit);
        }
    }

    public static void updateInventory(Client client)
    {
        ItemContainer invContainer = client.getItemContainer(InventoryID.INV);
        if (invContainer == null) return;
        Item[] allInventoryItems = invContainer.getItems();
        Map<Integer, Item> inventoryMap = new HashMap<>();

        for (Item item : allInventoryItems)
        {
            inventoryMap.computeIfPresent(item.getId(), (currentVal, existingItem) -> new Item(currentVal, existingItem.getQuantity() + item.getQuantity()));
            inventoryMap.putIfAbsent(item.getId(), item);
        }
        boolean result = Arrays.stream(allInventoryItems).map(Item::getId).anyMatch(RUNE_POUCHES::contains);
        if (result)
        {
            Item[] runesInPouch = QuestContainerManager.getRunePouchData().getItems();
            assert runesInPouch != null;
            for (Item runePouchItem : runesInPouch)
            {
                inventoryMap.computeIfPresent(runePouchItem.getId(), (currentVal, existingItem) -> new Item(currentVal, existingItem.getQuantity() + runePouchItem.getQuantity()));
                inventoryMap.putIfAbsent(runePouchItem.getId(), runePouchItem);
            }
        }
        QuestContainerManager.getInventoryData().update(client.getTickCount(), inventoryMap.values().toArray(new Item[0]));
    }

    static public void updateRunePouch(Client client, int varbitIdChanged)
    {
        if (!ALL_RUNE_POUCH_VARBITS.contains(varbitIdChanged))
        {
            return;
        }

        List<Item> runes = new ArrayList<>();
        final EnumComposition runepouchEnum = client.getEnum(EnumID.RUNEPOUCH_RUNE);

        for (int i = 0; i < NUM_SLOTS; i++)
        {
            @Varbit int amountVarbit = AMOUNT_VARBITS[i];
            int amount = client.getVarbitValue(amountVarbit);

            @Varbit int runeVarbit = RUNE_VARBITS[i];
            int runeId = client.getVarbitValue(runeVarbit);

            runes.add(new Item(runepouchEnum.getIntValue(runeId), amount));
        }

        ItemAndLastUpdated runePouchData = QuestContainerManager.getRunePouchData();
        runePouchData.update(client.getTickCount(), runes.toArray(new Item[0]));

        updateInventory(client);
    }
}
