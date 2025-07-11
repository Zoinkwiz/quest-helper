package com.questhelper.steps;

import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.Requirement;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class ReorderableConditionalStep extends ConditionalStep
{

    List<Integer> sideOrder = new ArrayList<>();
    public ReorderableConditionalStep(QuestHelper questHelper, QuestStep step, Requirement... requirements)
    {
        super(questHelper, step, requirements);
    }

    private void organiseSteps()
    {
        LinkedHashMap<Requirement, QuestStep> newSteps = new LinkedHashMap<>();
        for (Integer sidebarId : sideOrder)
        {
            for (Requirement req : steps.keySet())
            {
                QuestStep step = steps.get(req);
                if (step.getId() == null) continue;
                if (step.getId().equals(sidebarId))
                {
                    newSteps.put(req, step);
                    break;
                }
            }
        }
        for (Requirement req : steps.keySet())
        {
            QuestStep step = steps.get(req);
            if (step.getId() == null) newSteps.put(req, step);
        }
        steps = newSteps;
    }

    @Override
    protected void updateSteps()
    {
        List<Integer> sidebarOrder = questHelper.getSidebarOrder();
        if (sidebarOrder != this.sideOrder)
        {
            this.sideOrder = sidebarOrder;
            organiseSteps();
        }

        super.updateSteps();
    }
}
