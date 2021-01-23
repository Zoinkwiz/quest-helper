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
import com.questhelper.StreamUtil;
import com.questhelper.requirements.Requirement;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.DynamicGridLayout;

@Slf4j
/**
 * This panel holds all requirements of a specific type
 * (i.e. item requirements, general requirements, general recommended, etc)
 */
public class QuestRequirementSection extends JPanel implements RequirementContainer
{
	@Getter
	private final JPanel headerPanel;
	@Getter
	private final JPanel listPanel;
	@Getter
	private final JLabel titleLabel;
	@Getter
	private final Collection<QuestRequirementPanel> questRequirementPanels = new LinkedList<>();

	public QuestRequirementSection(String title)
	{
		setBackground(ColorScheme.DARKER_GRAY_COLOR);
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(0, 0, 0, 0));

		headerPanel = new JPanel();
		headerPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		headerPanel.setLayout(new BorderLayout());
		headerPanel.setBorder(new EmptyBorder(5, 5, 5, 10));

		titleLabel = new JLabel();
		titleLabel.setForeground(Color.WHITE);
		titleLabel.setText(title);
		titleLabel.setMinimumSize(new Dimension(1, headerPanel.getPreferredSize().height));
		headerPanel.add(titleLabel, BorderLayout.NORTH);

		listPanel = new JPanel();
		listPanel.setLayout(new DynamicGridLayout(0, 1, 0, 1));
		listPanel.setBorder(new EmptyBorder(10, 5, 10, 5));

		add(headerPanel, BorderLayout.NORTH);
		add(listPanel, BorderLayout.CENTER);
	}

	@Override
	public void setVisible(boolean visible)
	{
		headerPanel.setVisible(visible);
		listPanel.setVisible(visible);
	}

	public void addOrUpdateRequirements(Collection<? extends Requirement> requirements)
	{
		if (requirements != null)
		{
			if (!requirements.isEmpty())
			{
				listPanel.removeAll();
			}
			requirements.stream()
				.map(QuestRequirementPanel::new)
				.forEach(this::add);

			listPanel.revalidate();
			listPanel.repaint();

			log.debug("Found " + requirements.size() + " Requirement(s) for '" + getTitleLabel().getText() + "' Visible (" + isVisible() + ") Root (" + isVisible() + ")");
			long count = questRequirementPanels.stream().filter(Container::isVisible).count();
			log.debug("Panels: " + questRequirementPanels.size() + " - Visible: " + count + "/" + questRequirementPanels.size());
		}
	}

	@Override
	public void removeAll()
	{
		super.removeAll();
		questRequirementPanels.clear();
	}

	@Override
	public Component add(Component comp)
	{
		if (comp instanceof QuestRequirementPanel)
		{
			listPanel.add(comp);
			questRequirementPanels.add((QuestRequirementPanel)comp);
			return comp;
		}
		return super.add(comp);
	}

	@Override
	public void updateRequirements(Client client, BankItems bankItems)
	{
		questRequirementPanels.forEach(panel -> panel.updateRequirements(client, bankItems));
	}

	@Override
	public List<Requirement> getRequirements()
	{
		return StreamUtil.getRequirements(questRequirementPanels);
	}
}
