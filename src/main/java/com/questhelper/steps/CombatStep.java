package com.questhelper.steps;

import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.Requirement;
import net.runelite.api.coords.WorldPoint;

public class CombatStep extends NpcStep
{
    private final String combatText;

    public CombatStep(QuestHelper questHelper, int npcID, String text, String combatText, Requirement... requirements)
    {
        super(questHelper, npcID, text, requirements);
        this.combatText = combatText;
    }

    public CombatStep(QuestHelper questHelper, int npcID, WorldPoint worldPoint, String text, String combatText, Requirement... requirements)
    {
        super(questHelper, npcID,worldPoint, text, requirements);
        this.combatText = combatText;
    }

    public CombatStep(QuestHelper questHelper, int npcID, WorldPoint worldPoint, String text, String combatText, boolean allowMultipleHighlights, Requirement... requirements) {
        super(questHelper, npcID, null, text, allowMultipleHighlights, requirements);
        this.combatText = combatText;
    }

    public CombatStep(QuestHelper questHelper, int npcID, String combatText) {
        super(questHelper, npcID, null);
        this.combatText = combatText;
    }

    public CombatStep(QuestHelper questHelper, int npcID, String text, String combatText, boolean allowMultipleHighlights)
    {
        super(questHelper, npcID, text, allowMultipleHighlights);
        this.combatText = combatText;
    }

    public CombatStep(QuestHelper questHelper, int npcID, String text, String combatText, boolean allowMultipleHighlights, Requirement... requirements) {
        super(questHelper, npcID, text, allowMultipleHighlights, requirements);
        this.combatText = combatText;
    }

    public String getCombatText(){
        return combatText;
    }
}
