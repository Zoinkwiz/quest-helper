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
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.requirements.Requirement;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;

/**
 * The panel which holds all Quest Requirements, not just ItemRequirements.
 */
@Getter
@Slf4j
public class QuestRequirementOverviewPanel extends JPanel implements RequirementContainer
{
	private final QuestRequirementSection generalRequirements = new QuestRequirementSection("General Requirements:");
	private final QuestRequirementSection generalRecommended = new QuestRequirementSection("Recommended:");
	private final QuestRequirementSection itemRequirements = new QuestRequirementSection("Item Requirements:");
	private final QuestRequirementSection recommendedItems = new QuestRequirementSection("Recommended Items:");
	private final QuestRequirementSection enemiesToDefeat = new QuestRequirementSection("Enemies to defeat:");
	private final QuestRequirementSection notes = new QuestRequirementSection("Notes:");

	private final List<QuestRequirementSection> questRequirements = new LinkedList<>();

	private final JPanel wrapper;
	public QuestRequirementOverviewPanel(JPanel wrapper)
	{
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.wrapper = wrapper;
		addSection(generalRequirements);
		addSection(generalRecommended);
		addSection(itemRequirements);
		addSection(recommendedItems);
		addSection(enemiesToDefeat);
		addSection(notes);
	}

	public void initRequirements(QuestHelper quest)
	{
		/* Non-item requirements */
		generalRequirements.addOrUpdateRequirements(quest.getGeneralRequirements());
		generalRequirements.setVisible(quest.getGeneralRequirements() != null);

		generalRecommended.addOrUpdateRequirements(quest.getGeneralRecommended());
		generalRecommended.setVisible(quest.getGeneralRecommended() != null);

		/* Required items */
		ArrayList<ItemRequirement> itemReq = quest.getItemRequirements();
		if (itemReq != null)
		{
			this.itemRequirements.addOrUpdateRequirements(quest.getItemRequirements());
		}
		else
		{
			JLabel itemRequiredLabel = new JLabel();
			itemRequiredLabel.setForeground(Color.GRAY);
			itemRequiredLabel.setText("None");
			this.itemRequirements.add(itemRequiredLabel);
		}

		/* Recommended items */
		ArrayList<ItemRequirement> itemRecommended = quest.getItemRecommended();
		if (itemRecommended != null)
		{
			this.recommendedItems.addOrUpdateRequirements(quest.getItemRecommended());
		}
		else
		{
			JLabel itemRecommendedLabel = new JLabel();
			itemRecommendedLabel.setForeground(Color.GRAY);
			itemRecommendedLabel.setText("None");
			this.recommendedItems.add(itemRecommendedLabel);
		}

		/* Combat requirements */
		JLabel combatLabel = new JLabel();
		combatLabel.setForeground(Color.GRAY);
		ArrayList<String> combatRequirementList = quest.getCombatRequirements();
		StringBuilder textCombat = new StringBuilder();
		if (combatRequirementList == null)
		{
			textCombat.append("None");
		}
		else
		{
			for (String combatRequirement : combatRequirementList)
			{
				textCombat.append(combatRequirement);
				textCombat.append("<br>");
			}
		}
		combatLabel.setText("<html><body style = 'text-align:left'>" + textCombat + "</body></html>");

		this.enemiesToDefeat.add(combatLabel);

		/* Quest overview */
		JLabel overviewLabel = new JLabel();
		overviewLabel.setForeground(Color.GRAY);
		ArrayList<String> notes = quest.getNotes();
		StringBuilder textNote = new StringBuilder();
		if (notes != null)
		{
			for (String note : notes)
			{
				textNote.append(note);
				textNote.append("<br><br>");
			}
			overviewLabel.setText("<html><body style = 'text-align:left'>" + textNote + "</body></html>");

			this.notes.add(overviewLabel);
			this.notes.setVisible(true);
		}
		else
		{
			this.notes.setVisible(false);
		}
	}

	@Override
	public void setVisible(boolean visible)
	{
		super.setVisible(visible);
		getWrapper().setVisible(visible);
		questRequirements.forEach(s -> s.setVisible(visible));
	}

	public void addSection(QuestRequirementSection section)
	{
		questRequirements.add(section);
		Component comp = add(section);
		log.debug("Added QuestReqSection " + section.getTitleLabel().getText() + " with " + section.getRequirements().size() + " Requirement(s)");
		log.debug("QuestReqSection was added: " + (comp == section));
	}

	@Override
	public void removeAll()
	{
		super.removeAll();
		questRequirements.forEach(Container::removeAll);
	}

	@Override
	public void updateRequirements(Client client, BankItems bankItems)
	{
		questRequirements.forEach(req -> req.updateRequirements(client, bankItems));
		revalidate();
	}

	@Override
	public List<Requirement> getRequirements()
	{
		return StreamUtil.getRequirements(questRequirements);
	}

	/**
	 * Gets all the {@link QuestRequirementPanel} that are in each registered {@link QuestRequirementSection}
	 * @return a list of all registered {@link QuestRequirementPanel}s
	 */
	public List<QuestRequirementPanel> getRequirementPanels()
	{
		return questRequirements.stream()
			.map(QuestRequirementSection::getQuestRequirementPanels)
			.flatMap(Collection::stream)
			.collect(Collectors.toList());
	}
}
