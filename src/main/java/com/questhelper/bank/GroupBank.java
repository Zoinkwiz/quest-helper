/*
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
package com.questhelper.bank;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Item;
import net.runelite.client.config.ConfigManager;

import java.util.Arrays;

@Slf4j
@Singleton
public class GroupBank extends QuestBank
{

    @Setter
    private Item[] groupBankDuringEditing;

    private Item[] groupInventoryDuringEditing;

    // Inventory change event occurs once before actual change from group editing
    // So we need to allow for this
    private int inventoryChangeEvents = 0;

    @Inject
    public GroupBank(Client client, ConfigManager configManager, Gson gson)
    {
        super(client, configManager, gson);
    }

    // Inventory changed. If it matches group Inventory
    public boolean updateAfterInventoryChange(Item[] inventoryItems)
    {
        if (groupBankDuringEditing == null || inventoryItems == null || groupInventoryDuringEditing == null) return false;
        if (Arrays.equals(inventoryItems, paddedItems(groupInventoryDuringEditing)))
        {
            updateLocalBank(groupBankDuringEditing);
            groupBankDuringEditing = null;
            groupInventoryDuringEditing = null;
            return true;
        }

        inventoryChangeEvents++;
        int MAX_INVENTORY_CHANGE_BEFORE_CLEAR = 2;
        if (inventoryChangeEvents == MAX_INVENTORY_CHANGE_BEFORE_CLEAR)
        {
            inventoryChangeEvents = 0;
            groupBankDuringEditing = null;
            groupInventoryDuringEditing = null;
        }

        return false;
    }

    public void setGroupInventoryDuringEditing(Item[] groupInventoryDuringEditing)
    {
        inventoryChangeEvents = 0;
        this.groupInventoryDuringEditing = groupInventoryDuringEditing;
    }

    private Item[] paddedItems(Item[] oldItems)
    {
        final int INVENTORY_SIZE = 28;

        Item[] newItems = new Item[28];

        System.arraycopy(oldItems, 0, newItems, 0, oldItems.length);

        for (int i = oldItems.length; i < INVENTORY_SIZE; i++)
        {
            newItems[i] = new Item(-1, 0);
        }

        return newItems;
    }

    @Override
    protected String getKey()
    {
        return "groupbankitems";
    }
}

