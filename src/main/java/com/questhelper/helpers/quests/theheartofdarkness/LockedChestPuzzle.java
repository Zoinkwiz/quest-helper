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
package com.questhelper.helpers.quests.theheartofdarkness;

import com.google.inject.Inject;
import com.questhelper.QuestHelperPlugin;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.widget.WidgetTextRequirement;
import com.questhelper.steps.*;
import net.runelite.api.Client;
import net.runelite.api.ItemID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.overlay.components.PanelComponent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LockedChestPuzzle extends QuestStep implements OwnerStep
{
    @Inject
    protected EventBus eventBus;

    @Inject
    protected Client client;

    // Potion 1
    private static final Pattern WHITE_TEXT = Pattern.compile("<col=FAF9F6>([A-Z])(.*)");

    private String answer = null;

    protected QuestStep currentStep;

    ItemRequirement book;

    Requirement inChestInterface;

    final String[] letter1 = new String[]{"L", "W", "A", "B", "P", "E", "M", "D", "T", "R"};
    final String[] letter2 = new String[]{"A", "P", "O", "R", "I", "L", "C", "E", "T", "N"};
    final String[] letter3 = new String[]{"W", "E", "R", "I", "L", "A", "N", "U", "T", "O"};
    final String[] letter4 = new String[]{"E", "I", "D", "A", "O", "W", "K", "N", "R", "U"};

    PuzzleWrapperStep readBook;

    PuzzleWrapperStep openChest;

    ChestCodeStep solveChest;

    PuzzleWrapperStep solveChestPuzzleWrapped;


    public LockedChestPuzzle(QuestHelper questHelper)
    {
        super(questHelper, "");
        setupItemRequirements();
        setupZones();
        setupConditions();
        setupSteps();
    }

    @Override
    public void startUp()
    {
        updateSteps();
    }

    @Override
    public void shutDown()
    {
        shutDownStep();
        currentStep = null;
    }

    @Subscribe
    public void onGameTick(GameTick event)
    {
        updateSteps();
    }

    protected void updateSteps()
    {
        if (answer == null)
        {
            startUpStep(readBook);
        } else if (inChestInterface.check(client))
        {
            startUpStep(solveChest);
        } else
        {
            startUpStep(openChest);
        }
    }

    protected void startUpStep(QuestStep step)
    {
        if (currentStep == null)
        {
            currentStep = step;
            eventBus.register(currentStep);
            currentStep.startUp();
            return;
        }

        if (!step.equals(currentStep))
        {
            shutDownStep();
            eventBus.register(step);
            step.startUp();
            currentStep = step;
        }
    }

    protected void shutDownStep()
    {
        if (currentStep != null)
        {
            eventBus.unregister(currentStep);
            currentStep.shutDown();
            currentStep = null;
        }
    }

    @Override
    public void makeOverlayHint(PanelComponent panelComponent, QuestHelperPlugin plugin, List<String> additionalText, List<Requirement> requirements)
    {
        if (currentStep != null)
        {
            currentStep.makeOverlayHint(panelComponent, plugin, additionalText, requirements);
        }
    }

    @Override
    public void makeWorldOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin)
    {
        if (currentStep != null)
        {
            currentStep.makeWorldOverlayHint(graphics, plugin);
        }
    }

    @Override
    public void makeWorldArrowOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin)
    {
        if (currentStep != null)
        {
            currentStep.makeWorldArrowOverlayHint(graphics, plugin);
        }
    }

    @Override
    public void makeWorldLineOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin)
    {
        if (currentStep != null)
        {
            currentStep.makeWorldLineOverlayHint(graphics, plugin);
        }
    }

    @Override
    public QuestStep getActiveStep()
    {
        if (currentStep != this)
        {
            return currentStep.getActiveStep();
        } else
        {
            return this;
        }
    }

    private void setupItemRequirements()
    {
        book = new ItemRequirement("Book", ItemID.BOOK_29878);
    }

    private void setupConditions()
    {
        inChestInterface = new WidgetTextRequirement(809, 5, 9, "Confirm");
    }

    private void setupZones()
    {

    }

    private void setupSteps()
    {
        readBook = new DetailedQuestStep(getQuestHelper(), "Read the book.", book.highlighted())
                .puzzleWrapStep()
                .withNoHelpHiddenInSidebar(true);
        openChest = new ObjectStep(getQuestHelper(), ObjectID.CHEST_54376, new WorldPoint(1638, 3217, 1), "Search the south-west chest.")
                .puzzleWrapStep()
                .withNoHelpHiddenInSidebar(true);
        solveChest = new ChestCodeStep(getQuestHelper(), 10);
        solveChestPuzzleWrapped = solveChest.puzzleWrapStep().withNoHelpHiddenInSidebar(true);
    }

    @Override
    public Collection<QuestStep> getSteps()
    {
        return Arrays.asList(readBook, openChest, solveChestPuzzleWrapped);
    }

    int[] rotationPosOfAnswer = new int[4];

    @Subscribe
    public void onWidgetLoaded(WidgetLoaded widgetLoaded)
    {
        if (answer != null) return;

        int GROUP_ID = 392;
        List<String> tmpChars = new ArrayList<>();
        int firstLineChildId = 43;
        int maxChildId = 74;
        if (widgetLoaded.getGroupId() == GROUP_ID)
        {
            Widget line;
            for (int i = 0; i <= maxChildId - firstLineChildId; i++)
            {
                line = client.getWidget(GROUP_ID, firstLineChildId + i);
                if (line == null) break;
                Matcher matcher = WHITE_TEXT.matcher(line.getText());
                if (matcher.find())
                {
                    tmpChars.add(matcher.group(1));
                }
            }
        }

        if (tmpChars.size() == 4)
        {
            answer = "";
            for (String tmpChar : tmpChars)
            {
                answer += tmpChar;
            }

            rotationPosOfAnswer[0] = getPosForLetter(tmpChars.get(0), letter1);
            rotationPosOfAnswer[1] = getPosForLetter(tmpChars.get(1), letter2);
            rotationPosOfAnswer[2] = getPosForLetter(tmpChars.get(2), letter3);
            rotationPosOfAnswer[3] = getPosForLetter(tmpChars.get(3), letter4);

            solveChest.setAnswer(answer, rotationPosOfAnswer);
        }
    }


    private int getPosForLetter(String letter, String[] rotationLetters)
    {
        for (int i = 0; i < rotationLetters.length; i++)
        {
            if (rotationLetters[i].equals(letter))
            {
                return i;
            }
        }

        return -1;
    }
}
