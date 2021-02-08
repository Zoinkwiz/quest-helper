/*
 *
 *  * Copyright (c) 2021, Senmori
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  *
 *  * 1. Redistributions of source code must retain the above copyright notice, this
 *  *    list of conditions and the following disclaimer.
 *  * 2. Redistributions in binary form must reproduce the above copyright notice,
 *  *    this list of conditions and the following disclaimer in the documentation
 *  *    and/or other materials provided with the distribution.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package com.questhelper.banktab;

import com.questhelper.requirements.item.ItemRequirement;
import java.util.List;
import javax.annotation.Nullable;
import net.runelite.api.Client;
import net.runelite.api.Item;

/**
 * Represents anything that holds {@link ItemRequirement}s that are to be used
 * for displaying via the {@link QuestBankTab}.
 * <br>
 * Most requirements will not need this interface, however this interface does allow
 * that requirement to specify which {@link ItemRequirement} should be displayed
 * via the quest bank tab.
 */
public interface BankItemHolder
{
	/**
	 * Get a list of {@link ItemRequirement} to be displayed.
	 *
	 * @param client the {@link Client}
	 * @param checkConsideringSlotRestrictions if the client item container checks should respect slot restrictions
	 * @param bankItems the player's {@link com.questhelper.BankItems}, this can be null
	 * @return a list of {@link ItemRequirement} that should be displayed, or an empty list if none are found
	 */
	List<ItemRequirement> getRequirements(Client client, boolean checkConsideringSlotRestrictions, @Nullable Item[] bankItems);
}
