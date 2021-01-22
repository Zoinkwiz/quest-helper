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

package com.questhelper.panel.components;

import com.questhelper.BankItems;
import com.questhelper.QuestHelperPlugin;
import com.questhelper.StreamUtil;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.steps.QuestStep;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import lombok.Getter;
import net.runelite.api.Client;

/**
 * Contains and manages the list of quest steps
 * as well as their updating/highlighting
 */
public class QuestStepContainer extends JPanel implements RequirementContainer
{
	@Getter
	private final List<QuestStepPanel> questStepPanels = new LinkedList<>();

	private final QuestHelperPlugin plugin;
	public QuestStepContainer(QuestHelperPlugin plugin)
	{
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.plugin = plugin;
	}

	public void initQuestSteps(QuestHelper currentQuest, QuestStep currentStep, BiConsumer<QuestStepPanel, MouseEvent> mouseListener)
	{
		List<PanelDetails> panelDetails = currentQuest.getPanels();
		for (PanelDetails panel : panelDetails)
		{
			QuestStepPanel newStep = new QuestStepPanel(panel, currentStep);
			boolean hasLockingSteps = panel.getLockingQuestSteps() != null;
			int var = plugin.getQuestVar(currentQuest.getQuest());
			boolean hasVars = panel.getVars() == null || panel.getVars().contains(var);
			if (hasLockingSteps && hasVars)
			{
				newStep.setLockable(true);
			}
			questStepPanels.add(newStep);
			add(newStep);
			newStep.addMouseListener(mouseListener);
			newStep.setVisible(true);
			repaint();
			revalidate();
		}
		setVisible(true);
	}

	@Override
	public void setVisible(boolean visible)
	{
		super.setVisible(visible);
		questStepPanels.forEach(p -> p.setVisible(visible));
	}

	public void updateHighlight(QuestStep newStep, QuestHelper currentQuest)
	{
		AtomicBoolean highlighted = new AtomicBoolean(false);
		questStepPanels.forEach(panel -> {
			highlighted.set(false);
			boolean hasLockingSteps = panel.getPanelDetails().getLockingQuestSteps() != null;
			boolean questHasVar = currentQuest != null && panel.getPanelDetails().getVars().contains(currentQuest.getVar());
			boolean hasValidVar = panel.getPanelDetails().getVars() == null || questHasVar;
			panel.setLockable(hasLockingSteps && hasValidVar);

			panel.getSteps()
				.stream()
				.filter(p -> p == newStep || p.getSubsteps().contains(newStep))
				.findFirst()
				.ifPresent(step -> {
					panel.updateHighlight(step);
					highlighted.set(true);
				});
			if (!highlighted.get())
			{
				panel.removeHighlight();
			}
		});

		repaint();
		revalidate();
	}

	public void updateSteps()
	{
		questStepPanels.forEach(panel -> {
			for (QuestStep step : panel.getSteps())
			{
				JLabel label = panel.getStepsLabels().get(step);
				if (label != null)
				{
					label.setText(panel.generateText(step));
				}
			}
		});

		repaint();
		revalidate();
	}

	public void updateLocks()
	{
		questStepPanels.forEach(QuestStepPanel::updateLock);
		repaint();
		revalidate();
	}

	@Override
	public void removeAll()
	{
		super.removeAll();
		questStepPanels.clear();
	}

	public boolean isAllCollapsed()
	{
		return questStepPanels.stream().filter(QuestStepPanel::isCollapsed).count() == questStepPanels.size();
	}

	@Override
	public void updateRequirements(Client client, BankItems bankItems)
	{
		questStepPanels.forEach(panel -> panel.updateRequirements(client, bankItems));
	}

	@Override
	public List<Requirement> getRequirements()
	{
		return StreamUtil.getRequirements(questStepPanels);
	}
}
