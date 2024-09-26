/*
 *
 *  * Copyright (c) 2024, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.requirements.var;

import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.AbstractRequirement;
import com.questhelper.requirements.util.Operation;
import java.math.BigInteger;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.function.ToIntBiFunction;

import com.questhelper.util.Utils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Varbits;
import net.runelite.client.util.Text;

import javax.annotation.Nonnull;

/**
 * Checks if a player's varbit value is meets the required value as determined by the
 * {@link Operation}
 */
@Getter
@Slf4j
public class VarComparisonRequirement extends AbstractRequirement
{
    private final VarType v1Type;
    private final int v1Id;

    private final VarType v2Type;
    private final int v2Id;
    private final Operation operation;
    private final String displayText;

    private boolean hasFiredWarning = false;

    /**
     * Check if the player's varbit value meets the required level using the given
     * {@link Operation}.
     *
     * @param varbitID      the {@link Varbits} id to use
     * @param operation     the {@link Operation} to check with
     * @param requiredValue the required varbit value to pass this requirement
     * @param displayText   the display text
     */
    public VarComparisonRequirement(VarType v1Type, int v1Id, VarType v2Type, int v2Id, Operation operation, String displayText)
    {
        this.v1Type = v1Type;
        this.v1Id = v1Id;
        this.v2Type = v2Type;
        this.v2Id = v2Id;
        this.operation = operation;
        this.displayText = displayText;
        shouldCountForFilter = true;
    }

    @Override
    public boolean check(Client client)
    {
        try {
            int v1Value = v1Type.getValue(client, v1Id);
            int v2Value = v2Type.getValue(client, v2Id);

            return operation.check(v1Value, v2Value);
        } catch (IndexOutOfBoundsException e) {
            if (!hasFiredWarning) {
                var message = String.format("Error reading varbit %d or %d, please report this in the Quest Helper discord.", v1Id, v2Id);
                log.warn(message);
                Utils.addChatMessage(client, message);
                hasFiredWarning = true;
            }
            return false;
        }
    }

    @Nonnull
    @Override
    public String getDisplayText()
    {
        if (displayText != null)
        {
            return displayText;
        }

        return v1Type.name() + " " + v1Id + " must be + " + operation.name().toLowerCase(Locale.ROOT) + " to " + v2Type.name() + " " + v2Id;
    }
}
