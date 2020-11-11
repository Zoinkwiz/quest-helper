package com.questhelper.quests.tribaltotem;

import com.questhelper.QuestHelperPlugin;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.conditional.WidgetTextCondition;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.WidgetHiddenChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.Subscribe;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class CombinationLockStep extends QuestStep {

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

    public CombinationLockStep(QuestHelper questHelper) {
        super(questHelper, "Click the highlighted arrows to move the pieces to the correct spots.");
        highlightButtons.put(ARROW_ONE_RIGHT, 1);
        highlightButtons.put(ARROW_TWO_LEFT, 2);
        highlightButtons.put(ARROW_THREE_LEFT, 3);
        highlightButtons.put(ARROW_FOUR_LEFT, 4);
    }

    @Subscribe
    public void onGameTick(GameTick gameTick)
    {
        updateSolvedPositionState();
    }

    private void updateSolvedPositionState()
    {
        int ArrowOneId = matchStateToSolution(SLOT_ONE, ENTRY_ONE, ARROW_ONE_RIGHT);
        int ArrowTwoId = matchStateToSolution(SLOT_TWO, ENTRY_TWO, ARROW_TWO_LEFT);
        int ArrowThreeId = matchStateToSolution(SLOT_THREE, ENTRY_THREE, ARROW_THREE_LEFT);
        int ArrowFourId = matchStateToSolution(SLOT_FOUR, ENTRY_FOUR, ARROW_FOUR_LEFT);

        if (ArrowOneId + ArrowTwoId + ArrowThreeId + ArrowFourId == 0)
        {
            highlightButtons.clear();
            highlightButtons.put(COMPLETE, 1);
            return;
        }

        highlightButtons.clear();
        if (ArrowOneId != 0) highlightButtons.put(ArrowOneId, 1);
        if (ArrowTwoId != 0) highlightButtons.put(ArrowTwoId, 2);
        if (ArrowThreeId != 0) highlightButtons.put(ArrowThreeId, 3);
        if (ArrowFourId != 0) highlightButtons.put(ArrowFourId, 4);
    }

    private int matchStateToSolution(int slot, String entry, int arrowId)
    {
        Widget widget = client.getWidget(369, slot);
        assert widget != null;
        String letter = widget.getText();
        if(!letter.equals(entry)) return arrowId;
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

            Widget widget = client.getWidget(369, entry.getKey());
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