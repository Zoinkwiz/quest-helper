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
import com.questhelper.panel.PanelDetails;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.requirements.Requirement;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.DynamicGridLayout;

/**
 * This is the panel that is displayed during quest steps to specify
 * which item(s) are needed during that section.
 *
 * This will display all the {@link Requirement}s for a particular step
 * as determined by the provided {@link PanelDetails}.
 */
public class QuestStepRequirementsPanel extends JPanel implements RequirementContainer
{
	private final JPanel headerPanel;
	private final JPanel listOfRequiredItems;
	private final JLabel title;

	@Getter
	private List<QuestRequirementPanel> requirementPanels = new LinkedList<>();

	public QuestStepRequirementsPanel(PanelDetails panelDetails)
	{
		setBackground(ColorScheme.DARKER_GRAY_COLOR);
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(0, 0, 10, 0));

		headerPanel = createHeaderPanel();
		title = createTitle("Bring the following items: ");

		headerPanel.add(title, BorderLayout.NORTH);

		listOfRequiredItems = createItemListPanel();

		if (!panelDetails.getItemRequirements().isEmpty())
		{
			Collection<ItemRequirement> itemRequirements = panelDetails.getItemRequirements();
			for (ItemRequirement requirement : itemRequirements)
			{
				QuestRequirementPanel panel = new QuestRequirementPanel(requirement);
				requirementPanels.add(panel);
				listOfRequiredItems.add(panel);
			}
		}

		add(headerPanel, BorderLayout.NORTH);
		add(listOfRequiredItems, BorderLayout.CENTER);
	}

	private JPanel createHeaderPanel()
	{
		JPanel header = new JPanel();
		header.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		header.setLayout(new BorderLayout());
		header.setBorder(new EmptyBorder(5, 5, 5, 10));
		return header;
	}

	private JPanel createItemListPanel()
	{
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new DynamicGridLayout(0, 1, 0, 1));
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		return contentPanel;
	}

	private JLabel createTitle(String text)
	{
		JLabel title = new JLabel();
		title.setForeground(Color.WHITE);
		title.setText(text);
		title.setMinimumSize(new Dimension(1, headerPanel.getPreferredSize().height));
		return title;
	}

	@Override
	public void updateRequirements(Client client, BankItems bankItems)
	{
		requirementPanels.forEach( panel -> panel.updateRequirements(client, bankItems));
	}

	@Override
	public List<Requirement> getRequirements()
	{
		return StreamUtil.getRequirements(requirementPanels);
	}
}
