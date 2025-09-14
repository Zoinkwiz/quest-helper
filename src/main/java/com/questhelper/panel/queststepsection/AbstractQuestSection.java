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
package com.questhelper.panel.queststepsection;

import com.questhelper.panel.JGenerator;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.steps.QuestStep;
import net.runelite.api.Client;
import javax.swing.*;
import java.util.HashMap;
import java.util.List;

public abstract class AbstractQuestSection extends JPanel
{
	protected final JPanel headerPanel = new JPanel();
	protected final JTextPane headerLabel = JGenerator.makeJTextPane();
	protected final JPanel bodyPanel = new JPanel();
	protected final JCheckBox lockStep = new JCheckBox();

	protected JPanel leftTitleContainer;
	protected JPanel viewControls;

	protected PanelDetails panelDetails;


	public abstract boolean updateStepVisibility(Client client);

	protected abstract QuestStep currentlyActiveQuestSidebarStep();

	public abstract void setLockable(boolean canLock);

	public abstract boolean updateHighlightCheck(Client client, QuestStep newStep, QuestHelper currentQuest);

	public abstract void removeHighlight();

	public abstract void updateLock();

	protected abstract void lockSection(boolean locked);

	protected abstract void collapse();

	protected abstract void expand();

	public abstract boolean isCollapsed();

	protected abstract void applyDimmer(boolean brighten, JPanel panel);

	public abstract void updateRequirements(Client client);

	public abstract void updateAllText();

	public abstract HashMap<QuestStep, JTextPane> getStepsLabels();

	public abstract List<Integer> getIds();
}
