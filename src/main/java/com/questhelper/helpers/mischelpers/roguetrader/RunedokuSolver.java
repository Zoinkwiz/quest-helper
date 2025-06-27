/*
 * Copyright (c) 2025, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.helpers.mischelpers.roguetrader;

import com.questhelper.QuestHelperPlugin;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.steps.QuestStep;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.Subscribe;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.questhelper.helpers.mischelpers.roguetrader.SudokuLayouts.*;

public class RunedokuSolver extends QuestStep
{
    List<Integer> tilesToHighlight = new ArrayList<>();
    Set<Integer> runesToUse = new HashSet<>();

    public RunedokuSolver(QuestHelper questHelper)
    {
        super(questHelper, "Solve the Runedoku puzzle. you'll be told where to click when you select runes on the left side of the interface.");
    }

    @Override
    public void startUp()
    {
        int selectedRune = client.getVarbitValue(VarbitID.ROGUETRADER_SUDOKU_SELECTEDRUNE);
        int difficulty = client.getVarbitValue(VarbitID.ROGUETRADER_SUDOKU_GAME_DIFFICULTY);
        if (difficulty == 0)
        {
            solveSmall(selectedRune);
        }
        else if (difficulty == 1)
        {
            solve(selectedRune);
        }
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged event)
    {
        super.onVarbitChanged(event);

        // Need to calculate highlights
        int selectedRune = client.getVarbitValue(VarbitID.ROGUETRADER_SUDOKU_SELECTEDRUNE);
        int difficulty = client.getVarbitValue(VarbitID.ROGUETRADER_SUDOKU_GAME_DIFFICULTY);
        if (difficulty == 0)
        {
            solveSmall(selectedRune);
        }
        else if (difficulty == 1)
        {
            solve(selectedRune);
        }
    }

    @Override
    public void makeWidgetOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin)
    {
        super.makeWidgetOverlayHint(graphics, plugin);

        Widget gameWidget = client.getWidget(InterfaceID.RoguetraderSudoku.GAME_CLICKZONES);
        if (gameWidget == null) return;
        for (Integer pos : tilesToHighlight)
        {
            Widget tileWidget = gameWidget.getChild(pos);
            if (tileWidget == null) continue;
            graphics.setColor(new Color(questHelper.getConfig().targetOverlayColor().getRed(),
                    questHelper.getConfig().targetOverlayColor().getGreen(),
                    questHelper.getConfig().targetOverlayColor().getBlue(), 65));
            graphics.fill(tileWidget.getBounds());
            graphics.setColor(questHelper.getConfig().targetOverlayColor());
            graphics.draw(tileWidget.getBounds());
        }

        Widget runePanelWidget = client.getWidget(InterfaceID.RoguetraderSudoku.RUNES);
        if (runePanelWidget == null) return;

        if (tilesToHighlight.isEmpty() && !runesToUse.isEmpty())
        {
            Widget tileWidget = runePanelWidget.getChild(runesToUse.stream().findFirst().get());
            if (tileWidget == null) return;
            graphics.setColor(new Color(questHelper.getConfig().targetOverlayColor().getRed(),
                    questHelper.getConfig().targetOverlayColor().getGreen(),
                    questHelper.getConfig().targetOverlayColor().getBlue(), 65));
            graphics.fill(tileWidget.getBounds());
            graphics.setColor(questHelper.getConfig().targetOverlayColor());
            graphics.draw(tileWidget.getBounds());
            return;
        }

        Widget openCasketWidget = client.getWidget(InterfaceID.RoguetraderSudoku.BTN_OPENCASKET);
        if (tilesToHighlight.isEmpty() && runesToUse.isEmpty())
        {
            graphics.setColor(new Color(questHelper.getConfig().targetOverlayColor().getRed(),
                    questHelper.getConfig().targetOverlayColor().getGreen(),
                    questHelper.getConfig().targetOverlayColor().getBlue(), 65));
            graphics.fill(openCasketWidget.getBounds());
            graphics.setColor(questHelper.getConfig().targetOverlayColor());
            graphics.draw(openCasketWidget.getBounds());
        }
    }

    public void solveSmall(int currentRune)
    {
        int puzzleID = client.getVarbitValue(VarbitID.ROGUETRADER_SUDOKU_PUZZLEID);

        tilesToHighlight.clear();
        runesToUse.clear();

        for (int i = 0; i <= VarPlayerID.ROGUETRADER_TEMP2 - VarPlayerID.ROGUETRADER_TEMP1; i++)
        {
            int currentState = client.getVarpValue(VarPlayerID.ROGUETRADER_TEMP1 + i);
            for (int runeInSetOf8 = 0; runeInSetOf8 < 8; runeInSetOf8++)
            {
                int posInFullPuzzle = i * 8 + runeInSetOf8;
                int transposedRuneForTile = getTransposedRune(0, 0, puzzleID, posInFullPuzzle);
                if (transposedRuneForTile == -1) return;

                int mask = 0xF << (runeInSetOf8 * 4);
                if ((currentState & mask) != (transposedRuneForTile << (runeInSetOf8 * 4)))
                {
                    runesToUse.add(transposedRuneForTile);
                    if (transposedRuneForTile == currentRune)
                    {
                        tilesToHighlight.add(posInFullPuzzle);
                    }
                }
            }
        }
    }

    public void solve(int currentRune)
    {
        int transpose = client.getVarbitValue(VarbitID.ROGUETRADER_SUDOKUTRANSPOSE);
        int puzzleID = client.getVarbitValue(VarbitID.ROGUETRADER_SUDOKU_PUZZLEID);

        tilesToHighlight.clear();
        runesToUse.clear();

        for (int i = 0; i <= VarPlayerID.ROGUETRADER_TEMP11 - VarPlayerID.ROGUETRADER_TEMP1; i++)
        {
            int currentState = client.getVarpValue(VarPlayerID.ROGUETRADER_TEMP1 + i);
            for (int runeInSetOf8 = 0; runeInSetOf8 < 8; runeInSetOf8++)
            {
                int posInFullPuzzle = i * 8 + runeInSetOf8;
                if (posInFullPuzzle >= 81) break;
                int transposedRuneForTile = getTransposedRune(1, transpose, puzzleID, posInFullPuzzle);
                if (transposedRuneForTile == -1) return;

                int mask = 0xF << (runeInSetOf8 * 4);
                if ((currentState & mask) != (transposedRuneForTile << (runeInSetOf8 * 4)))
                {
                    runesToUse.add(transposedRuneForTile);
                    if (transposedRuneForTile == currentRune)
                    {
                        tilesToHighlight.add(posInFullPuzzle);
                    }
                }
            }
        }
    }

    private int getTransposedRune(int difficulty, int transpose, int puzzleID, int pos)
    {
        Integer[] currentPuzzle = puzzles.get(difficulty).get(puzzleID);
        if (currentPuzzle == null) return -1;

        int result = currentPuzzle[pos] + transpose;
        return ((result - 1) % 9) + 1;
    }
}
