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
package com.questhelper.managers;

import com.questhelper.requirements.item.TrackedContainers;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Item;
import javax.annotation.Nullable;

import java.util.concurrent.Callable;

@Slf4j
public class ItemAndLastUpdated
{
    @Getter
    private TrackedContainers containerType;

    // last game tick item container was changed
    @Getter
    private int lastUpdated = -1;
    private Item[] items;

    @Setter
    private Callable<Item[]> specialMethodToObtainItems;

    public ItemAndLastUpdated(TrackedContainers containerType)
    {
        this.containerType = containerType;
    }

    public void update(int updateTick, Item[] items)
    {
        this.lastUpdated = updateTick;
        this.items = items;
    }

    /**
     * Get the Items contained within the Tracked Container.
     * If this instance of ItemAndLastUpdated has a method in specialMethodToObtainItems to obtain the current state of the Container other than
     * from the {@link Item}[] items variable, it will use that and return the value.
     *
     * @return an {@link Item}[] of items currently thought to be in the container.
     */
    public @Nullable Item[] getItems()
    {
        if (specialMethodToObtainItems != null)
        {
            try
            {
                return specialMethodToObtainItems.call();
            } catch (Exception e)
            {
                log.warn("Failed to load container from method");
            }
        }

        return items;
    }
}
