/*
 * Copyright (c) 2020, Zoinkwiz <https://github.com/Zoinkwiz>
 * Copyright (c) 2020, Twinkle <https://github.com/twinkle-is-dum>
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
package com.questhelper.quests.tribaltotem;

import com.questhelper.QuestHelperPlugin;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.steps.QuestStep;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.Subscribe;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class PuzzleStep extends QuestStep {

    private final String ENTRY_ONE = "K";
    private final String ENTRY_TWO = "U";
    private final String ENTRY_THREE = "R";
    private final String ENTRY_FOUR = "T";

    private final int SLOT_ONE = 42;
    private final int SLOT_TWO = 43;
    private final int SLOT_THREE = 44;
    private final int SLOT_FOUR = 45;

    private final int ARROW_ONE_LEFT = 46;
    private final int ARROW_ONE_RIGHT = 47;
    private final int ARROW_TWO_LEFT = 48;
    private final int ARROW_TWO_RIGHT = 49;
    private final int ARROW_THREE_LEFT = 50;
    private final int ARROW_THREE_RIGHT = 51;
    private final int ARROW_FOUR_LEFT = 52;
    private final int ARROW_FOUR_RIGHT = 53;

    private final int COMPLETE = 55;

    private final HashMap<Integer, Integer> highlightButtons = new HashMap<>();

    public PuzzleStep(QuestHelper questHelper) {
        super(questHelper, "Click the highlighted arrows to move the slots to the solution 'KURT'.");
        highlightButtons.put(1, ARROW_ONE_RIGHT);
        highlightButtons.put(2, ARROW_TWO_RIGHT);
        highlightButtons.put(3, ARROW_THREE_RIGHT);
        highlightButtons.put(4, ARROW_FOUR_RIGHT);
    }

    @Subscribe
    public void onGameTick(GameTick gameTick)
    {
        updateSolvedPositionState();
    }

    private void updateSolvedPositionState()
    {
        highlightButtons.replace(1, matchStateToSolution(SLOT_ONE, ENTRY_ONE, ARROW_ONE_RIGHT, ARROW_ONE_LEFT));
        highlightButtons.replace(2, matchStateToSolution(SLOT_TWO, ENTRY_TWO, ARROW_TWO_RIGHT, ARROW_TWO_LEFT));
        highlightButtons.replace(3, matchStateToSolution(SLOT_THREE, ENTRY_THREE, ARROW_THREE_RIGHT, ARROW_THREE_LEFT));
        highlightButtons.replace(4, matchStateToSolution(SLOT_FOUR, ENTRY_FOUR,ARROW_FOUR_RIGHT, ARROW_FOUR_LEFT));
        if (highlightButtons.get(1) + highlightButtons.get(2) + highlightButtons.get(3) + highlightButtons.get(4) == 0)
        {
            highlightButtons.put(5, COMPLETE);
        } else {
            highlightButtons.put(5, 0);
        }
    }

    private int matchStateToSolution(int slot, String target, int arrowRightId, int arrowLeftId)
    {
        Widget widget = client.getWidget(369, slot);
        if (widget == null) return 0;
        char current = widget.getText().charAt(0);
        int currentPos = (int)current - (int)'A';
        int id = Math.floorMod(currentPos - target.charAt(0), 26) < Math.floorMod(target.charAt(0) - currentPos, 26) ? arrowRightId : arrowLeftId;
        if(current != target.charAt(0)) return id;
        return 0;
    }

    @Override
    public void makeWidgetOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin)
    {
        super.makeWidgetOverlayHint(graphics, plugin);
        for (Map.Entry<Integer, Integer> entry : highlightButtons.entrySet())
        {
            if (entry.getValue() == 0)
            {
                continue;
            }

            Widget widget = client.getWidget(369, entry.getValue());
            if (widget != null)
            {
                graphics.setColor(new Color(0, 255, 255, 65));
                graphics.fill(widget.getBounds());
                graphics.setColor(Color.CYAN);
                graphics.draw(widget.getBounds());
            }
        }
    }
}