/*
 * Copyright (c) 2024, Adam <Adam@sigterm.info>
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
package com.questhelper.bank.banktab;

import java.util.*;
import javax.inject.Inject;
import javax.inject.Singleton;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.events.WidgetClosed;
import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetType;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.bank.BankSearch;

class Potion
{
    EnumComposition potionEnum;
    int itemId;
    int doses;
    int withdrawDoses;
}

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@Slf4j
public class PotionStorage
{
    static final int BANKTAB_POTIONSTORE = 15;
    static final int COMPONENTS_PER_POTION = 5;

    private final Client client;
    private final ItemManager itemManager;
    private final BankSearch bankSearch;

    private Potion[] potions;
    boolean cachePotions;
    private boolean layout;
    private Set<Integer> potionStoreVars;

    @Setter
    private QuestBankTabInterface questBankTabInterface;

    @Subscribe
    public void onClientTick(ClientTick event)
    {
        if (cachePotions)
        {
//            log.debug("Rebuilding potions");
            cachePotions = false;
            rebuildPotions();

            Widget w = client.getWidget(ComponentID.BANK_POTIONSTORE_CONTENT);
            if (w != null && potionStoreVars == null)
            {
                // cache varps that the potion store rebuilds on
                int[] trigger = w.getVarTransmitTrigger();
                potionStoreVars = new HashSet<>();
                Arrays.stream(trigger).forEach(potionStoreVars::add);
            }

            if (layout)
            {
                layout = false;
                if (questBankTabInterface.isQuestTabActive())
                {
                    bankSearch.layoutBank();
                }
            }
        }
    }

    // Use varp change event instead of a widget change listener so that we can recache the potions prior to
    // the cs2 vm running.
    @Subscribe
    public void onVarbitChanged(VarbitChanged varbitChanged)
    {
        if (potionStoreVars != null && potionStoreVars.contains(varbitChanged.getVarpId()))
        {
            cachePotions = true;
            layout = true; // trigger a bank rebuild as the qty has changed
        }
    }

    @Subscribe
    public void onWidgetClosed(WidgetClosed event)
    {
        if (event.getGroupId() == InterfaceID.BANK && event.isUnload())
        {
            log.debug("Invalidating potions");
            potions = null;
        }
    }

    private void rebuildPotions()
    {
        var potionStorePotions = client.getEnum(EnumID.POTIONSTORE_POTIONS);
        var potionStoreUnfinishedPotions = client.getEnum(EnumID.POTIONSTORE_UNFINISHED_POTIONS);
        potions = new Potion[potionStorePotions.size() + potionStoreUnfinishedPotions.size()];
        int potionsIdx = 0;
        for (EnumComposition e : new EnumComposition[]{potionStorePotions, potionStoreUnfinishedPotions})
        {
            for (int potionEnumId : e.getIntVals())
            {
                var potionEnum = client.getEnum(potionEnumId);
                client.runScript(ScriptID.POTIONSTORE_DOSES, potionEnumId);
                int doses = client.getIntStack()[0];
                client.runScript(ScriptID.POTIONSTORE_WITHDRAW_DOSES, potionEnumId);
                int withdrawDoses = client.getIntStack()[0];

                if (doses > 0 && withdrawDoses > 0)
                {
                    Potion p = new Potion();
                    p.potionEnum = potionEnum;
                    p.itemId = potionEnum.getIntValue(withdrawDoses);
                    p.doses = doses;
                    p.withdrawDoses = withdrawDoses;
                    potions[potionsIdx] = p;

                    if (log.isDebugEnabled())
                    {
//                        log.debug("Potion store has {} doses of {}", p.doses, itemManager.getItemComposition(p.itemId).getName());
                    }
                }

                ++potionsIdx;
            }
        }

        for (Potion potion : potions)
        {
            if (potion != null)
            {
                String name = itemManager.getItemComposition(potion.itemId).getName();
                if (name.contains("Hunter"))
                {
//                    System.out.println(itemManager.getItemComposition(potion.itemId).getName());
//                    System.out.println(potion.doses);
                }
            }
        }
//        System.out.println(Arrays.toString(potions));
    }

    public Item[] getItems()
    {
        if (potions == null)
        {
            return new Item[0];
        }

        List<Item> items = new ArrayList<>();

        for (Potion potion : potions)
        {
            var potionEnum = potion.potionEnum;
            // TODO: An issue due to potentially wanting a specific potion, or to default to full potion
            int potionItemId = potionEnum.getIntValue(potion.withdrawDoses);
            int quantity = potion.doses / potion.withdrawDoses;
            items.add(new Item(potionItemId, quantity));
        }

        return items.toArray(new Item[0]);
    }

    int matches(Set<Integer> bank, int itemId)
    {
        if (potions == null)
        {
            return -1;
        }

        for (Potion potion : potions)
        {
            if (potion == null)
            {
                continue;
            }

            var potionEnum = potion.potionEnum;
            int potionItemId1 = potionEnum.getIntValue(1);
            int potionItemId2 = potionEnum.getIntValue(2);
            int potionItemId3 = potionEnum.getIntValue(3);
            int potionItemId4 = potionEnum.getIntValue(4);

            if (potionItemId1 == itemId || potionItemId2 == itemId || potionItemId3 == itemId || potionItemId4 == itemId)
            {
                int potionStoreItem = potionEnum.getIntValue(potion.withdrawDoses);

                if (log.isDebugEnabled())
                {
                    log.debug("Item {} matches a potion from potion store {}", itemId, itemManager.getItemComposition(potionStoreItem).getName());
                }

                return potionStoreItem;
            }
        }

        return -1;
    }

    int count(int itemId)
    {
        if (potions == null)
        {
            return 0;
        }

        for (Potion potion : potions)
        {
            if (potion != null && potion.itemId == itemId)
            {
                return potion.doses / potion.withdrawDoses;
            }
        }
        return 0;
    }

    int find(int itemId)
    {
        if (potions == null)
        {
            return -1;
        }

        int potionIdx = 0;
        for (Potion potion : potions)
        {
            ++potionIdx;
            if (potion != null && potion.itemId == itemId)
            {
                return potionIdx - 1;
            }
        }
        return -1;
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event)
    {
        // Update widget index of the menu so withdraws work in laid out tabs.
        if (event.getParam1() == ComponentID.BANK_ITEM_CONTAINER && questBankTabInterface.isQuestTabActive())
        {
            MenuEntry menu = event.getMenuEntry();
            Widget w = menu.getWidget();
            if (w != null && w.getItemId() > -1)
            {
                ItemContainer bank = client.getItemContainer(InventoryID.BANK);
                int idx = bank.find(w.getItemId());
                if (idx > -1 && menu.getParam0() != idx)
                {
                    menu.setParam0(idx);
                    return;
                }

                idx = find(w.getItemId());
                if (idx > -1)
                {
                    prepareWidgets();
                    menu.setParam1(ComponentID.BANK_POTIONSTORE_CONTENT);
                    menu.setParam0(idx * COMPONENTS_PER_POTION);
                }
            }
        }
    }

    void prepareWidgets()
    {
        // if the potion store hasn't been opened yet, the client components won't have been made yet.
        // they need to exist for the click to work correctly.
        Widget potStoreContent = client.getWidget(ComponentID.BANK_POTIONSTORE_CONTENT);
        if (potStoreContent.getChildren() == null)
        {
            int childIdx = 0;
            for (int i = 0; i < potions.length; ++i) // NOPMD: ForLoopCanBeForeach
            {
                for (int j = 0; j < COMPONENTS_PER_POTION; ++j)
                {
                    potStoreContent.createChild(childIdx++, WidgetType.GRAPHIC);
                }
            }
        }
    }
}