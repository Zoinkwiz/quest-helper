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

import com.questhelper.QuestHelperPlugin;
import com.questhelper.managers.QuestManager;
import com.questhelper.panel.PanelDetails;
import com.questhelper.panel.QuestOverviewPanel;
import com.questhelper.panel.TopLevelPanelDetails;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.steps.QuestStep;
import net.runelite.api.Client;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.SwingUtil;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class QuestSectionSection extends AbstractQuestSection implements MouseListener
{
	// Idea is to contain multiple sections or queststeppanel
	private static final int TITLE_PADDING = 5;
	private static final ImageIcon DRAG_ICON = new ImageIcon(ImageUtil.loadImageResource(QuestHelperPlugin.class, "/hamburger.png"));

	private final QuestOverviewPanel questOverviewPanel;
	private final QuestHelperPlugin questHelperPlugin;

	private final JPanel stepsPanel = new JPanel();

	private final List<AbstractQuestSection> subPanels = new ArrayList<>();

	private boolean stepAutoLocked;
	private final QuestHelper questHelper;
	private AbstractQuestSection lastHighlightedSection = null;
	private final boolean draggable;
	public QuestSectionSection(QuestOverviewPanel questOverviewPanel, TopLevelPanelDetails panelDetails, QuestStep currentStep, QuestManager questManager, QuestHelperPlugin questHelperPlugin)
	{
		this.questOverviewPanel = questOverviewPanel;
		this.panelDetails = panelDetails;
		this.questHelperPlugin = questHelperPlugin;
		this.questHelper = questManager.getSelectedQuest();

		setLayout(new BorderLayout(0, 1));
		setBorder(new EmptyBorder(5, 0, 0, 0));

		leftTitleContainer = new JPanel(new BorderLayout(5, 0));

		headerLabel.addMouseListener(this);
		addMouseListener(this);

		headerLabel.setText(panelDetails.getHeader());
		headerLabel.setFont(FontManager.getRunescapeBoldFont());

		headerLabel.setMinimumSize(new Dimension(1, headerLabel.getPreferredSize().height));

		headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.X_AXIS));
		headerPanel.setBorder(new EmptyBorder(7, 7, 3, 7));

		headerPanel.add(Box.createRigidArea(new Dimension(TITLE_PADDING, 0)));
		leftTitleContainer.add(headerLabel, BorderLayout.CENTER);
		headerPanel.add(leftTitleContainer, BorderLayout.WEST);

		viewControls = new JPanel(new GridLayout(1, 3, 10, 0));

		SwingUtil.addModalTooltip(lockStep, "Mark section as incomplete", "Mark section as complete");
		lockStep.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		lockStep.addActionListener(ev -> lockSection(lockStep.isSelected()));
		lockStep.setVisible(false);
		headerPanel.add(lockStep, BorderLayout.EAST);

		viewControls.add(lockStep);

		headerPanel.add(viewControls, BorderLayout.EAST);

		if (panelDetails.contains(currentStep))
		{
			headerLabel.setForeground(Color.BLACK);
			headerPanel.setBackground(ColorScheme.BRAND_ORANGE);
			viewControls.setBackground(ColorScheme.BRAND_ORANGE);
			leftTitleContainer.setBackground(ColorScheme.BRAND_ORANGE);
		}
		else
		{
			headerLabel.setForeground(Color.WHITE);
			headerPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR.darker());
			viewControls.setBackground(ColorScheme.DARKER_GRAY_COLOR.darker());
			leftTitleContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR.darker());
		}

		/* Body */
		stepsPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		stepsPanel.setLayout(new BoxLayout(stepsPanel, BoxLayout.Y_AXIS));
		stepsPanel.setBorder(new EmptyBorder(10, 5, 10, 5));

		// Dragging functionality
		this.draggable = panelDetails.getPanelDetails().stream().anyMatch((pDetails -> pDetails.getId() != Integer.MIN_VALUE));
		List<Integer> order = questHelperPlugin.loadSidebarOrder(questManager.getSelectedQuest());

		List<PanelDetails> panelDetailsList = panelDetails.getPanelDetails();

		if (draggable && order != null)
		{
			Map<Integer, Integer> idx = new HashMap<>();
			for (int i = 0; i < order.size(); i++)
			{
				idx.put(order.get(i), i);
			}

			panelDetailsList.sort(Comparator.comparingInt(
				pd -> idx.getOrDefault(pd.getId(), Integer.MAX_VALUE)
			));
		}

		for (PanelDetails panelDetail : panelDetailsList)
		{
			AbstractQuestSection newSection;
			if (panelDetail instanceof TopLevelPanelDetails)
			{
				var topLevelPanelDetails = (TopLevelPanelDetails) panelDetail;
				newSection = new QuestSectionSection(questOverviewPanel, topLevelPanelDetails, currentStep, questManager, questHelperPlugin);
			}
			else
			{
				newSection = new QuestStepPanel(panelDetail, currentStep, questManager, questHelperPlugin);
			}

			if (panelDetail.getLockingQuestSteps() != null &&
				(panelDetail.getVars() == null
					|| questManager.getSelectedQuest() == null
					|| panelDetail.getVars().contains(questManager.getSelectedQuest().getVar())))
			{
				newSection.setLockable(true);
			}

			stepsPanel.add(newSection);
			subPanels.add(newSection);

			if (draggable) makeDraggable(newSection);
		}


		add(headerPanel, BorderLayout.NORTH);
		add(stepsPanel, BorderLayout.CENTER);

		if (!panelDetails.getSteps().contains(currentStep))
		{
			collapse();
		}

		// Setup right-click context menu for resetting order
		if (draggable)
		{
			setupContextMenu();
		}
	}

	private void setupContextMenu()
	{
		JPopupMenu contextMenu = new JPopupMenu();
		JMenuItem resetOrderItem = new JMenuItem("Reset order");
		resetOrderItem.addActionListener(e -> resetSectionOrder());
		contextMenu.add(resetOrderItem);

		headerPanel.setComponentPopupMenu(contextMenu);
		headerLabel.setComponentPopupMenu(contextMenu);
	}

	private void resetSectionOrder()
	{
		if (questHelper == null) return;

		List<Integer> sectionIds = getIds();
		questHelperPlugin.resetSidebarOrderForSection(questHelper, sectionIds);
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		if (e.getButton() == MouseEvent.BUTTON1)
		{
			if (isCollapsed())
			{
				expand();
			}
			else
			{
				collapse();
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent mouseEvent)
	{
	}

	@Override
	public void mouseReleased(MouseEvent mouseEvent)
	{
	}

	@Override
	public void mouseEntered(MouseEvent mouseEvent)
	{
	}

	@Override
	public void mouseExited(MouseEvent mouseEvent)
	{
	}

	public boolean updateStepVisibility(Client client)
	{
		boolean stepVisibilityChanged = false;

		for (AbstractQuestSection questSectionPanel : subPanels)
		{
			if (questSectionPanel.updateStepVisibility(client)) stepVisibilityChanged = true;
		}

		if (stepVisibilityChanged)
		{
			updateHighlightCheck(client, currentlyActiveQuestSidebarStep(), questHelper);
		}

		return stepVisibilityChanged;
	}

	protected QuestStep currentlyActiveQuestSidebarStep()
	{
		var selectedQuest = questHelperPlugin.getSelectedQuest();
		if (selectedQuest == null) return null;
		var currentStep = selectedQuest.getCurrentStep();
		return currentStep.getActiveStep();
	}

	public void setLockable(boolean canLock)
	{
		lockStep.setVisible(canLock);
	}

	public boolean updateHighlightCheck(Client client, QuestStep newStep, QuestHelper currentQuest)
	{
		if (panelDetails.getHideCondition() == null || !panelDetails.getHideCondition().check(client))
		{
			setVisible(true);
			boolean highlighted = false;
			setLockable(panelDetails.getLockingQuestSteps() != null &&
				(panelDetails.getVars() == null || panelDetails.getVars().contains(currentQuest.getVar())));

			for (AbstractQuestSection panel : subPanels)
			{
				if (panel.updateHighlightCheck(client, newStep, currentQuest))
				{
					highlighted = true;
					lastHighlightedSection = panel;
				}
			}

			if (!highlighted)
			{
				removeHighlight();
			}
			else
			{
				updateHighlight();
			}

			return highlighted;
		}
		else
		{
			setVisible(false);
			return false;
		}
	}


	public void updateHighlight()
	{
		expand();

		headerLabel.setForeground(Color.BLACK);
		headerPanel.setBackground(ColorScheme.BRAND_ORANGE);
		viewControls.setBackground(ColorScheme.BRAND_ORANGE);
		leftTitleContainer.setBackground(ColorScheme.BRAND_ORANGE);
	}

	public void removeHighlight()
	{
		headerLabel.setForeground(Color.WHITE);
		if (isCollapsed())
		{
			applyDimmer(false, headerPanel);
		}

		headerPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR.darker());
		viewControls.setBackground(ColorScheme.DARKER_GRAY_COLOR.darker());
		leftTitleContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR.darker());


		if (lastHighlightedSection != null)
		{
			lastHighlightedSection.removeHighlight();
		}

		collapse();
	}

	public void updateLock()
	{
		if (panelDetails.getLockingQuestSteps() == null)
		{
			return;
		}

		if (panelDetails.getLockingQuestSteps().isUnlockable())
		{
			stepAutoLocked = false;
			lockStep.setEnabled(true);
		}
		else
		{
			if (!stepAutoLocked)
			{
				collapse();
			}
			stepAutoLocked = true;
			lockStep.setEnabled(false);
		}

		if (panelDetails.getLockingQuestSteps().isLocked())
		{
			lockStep.setSelected(true);
		}
	}

	protected void lockSection(boolean locked)
	{
		if (locked)
		{
			panelDetails.getLockingQuestSteps().setLockedManually(true);
			if (!isCollapsed())
			{
				collapse();
			}
		}
		else
		{
			panelDetails.getLockingQuestSteps().setLockedManually(false);
			if (isCollapsed())
			{
				expand();
			}
		}
	}

	protected void collapse()
	{
		if (!isCollapsed())
		{
			stepsPanel.setVisible(false);
			applyDimmer(false, headerPanel);
		}
	}

	protected void expand()
	{
		if (isCollapsed())
		{
			stepsPanel.setVisible(true);
			applyDimmer(true, headerPanel);
		}
	}

	public boolean isCollapsed()
	{
		return !stepsPanel.isVisible();
	}

	protected void applyDimmer(boolean brighten, JPanel panel)
	{
		for (Component component : panel.getComponents())
		{
			Color color = component.getForeground();
			component.setForeground(brighten ? color.brighter() : color.darker());
		}
	}

	public void updateRequirements(Client client)
	{
		for (AbstractQuestSection subPanel : subPanels)
		{
			subPanel.updateRequirements(client);
		}
		updateStepVisibility(client);
	}

	@Override
	public void updateAllText()
	{
		for (AbstractQuestSection subPanel : subPanels)
		{
			subPanel.updateAllText();
		}
	}

	public HashMap<QuestStep, JTextPane> getStepsLabels()
	{
		return new HashMap<>();
	}

	private void makeDraggable(AbstractQuestSection newStep)
	{
		JLabel grip = new JLabel(DRAG_ICON);
		grip.setBorder(new EmptyBorder(0, 0, 3, 0));
		grip.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));

		GripDragListener listener = new GripDragListener(newStep);
		grip.addMouseListener(listener);
		grip.addMouseMotionListener(listener);

		newStep.leftTitleContainer.add(grip, BorderLayout.WEST);
	}

	private void swapPanels(AbstractQuestSection a, AbstractQuestSection b)
	{
		int ia = subPanels.indexOf(a);
		int ib = subPanels.indexOf(b);
		if (ia < 0 || ib < 0) return;

		Collections.swap(subPanels, ia, ib);

		stepsPanel.removeAll();
		// Create again in new order
		for (AbstractQuestSection p : subPanels)
		{
			stepsPanel.add(p);
		}
		revalidate();
		repaint();
	}

	public List<Integer> getIds()
	{
		List<Integer> allIds = new ArrayList<>();
		if (panelDetails.getId() != Integer.MIN_VALUE) allIds.add(panelDetails.getId());

		allIds.addAll(subPanels.stream()
			.map(AbstractQuestSection::getIds)
			.flatMap(Collection::stream)
			.collect(Collectors.toList()));
		return allIds;
	}

	private class GripDragListener extends MouseAdapter implements MouseMotionListener
	{
		private final AbstractQuestSection panel;

		private AbstractQuestSection draggingPanel = null;


		GripDragListener(AbstractQuestSection panel)
		{
			this.panel = panel;
		}

		@Override
		public void mousePressed(MouseEvent e)
		{
			if (e.getButton() != MouseEvent.BUTTON1)
			{
				return;
			}
			draggingPanel = panel;
		}

		@Override
		public void mouseDragged(MouseEvent e)
		{
			if (draggingPanel == null) return;

			// Convert mouse position to coordinates relative to stepsPanel
			// This ensures scrolling doesn't affect the drag calculations
			Point mousePoint = e.getLocationOnScreen();
			SwingUtilities.convertPointFromScreen(mousePoint, stepsPanel);
			int currentY = mousePoint.y;

			for (AbstractQuestSection other : subPanels)
			{
				if (other == draggingPanel) continue;

				Rectangle r = other.getBounds();
				// Use bounds relative to stepsPanel, not screen coordinates
				int midY = r.y + r.height / 2;

				int fromIndex = subPanels.indexOf(draggingPanel);
				int toIndex = subPanels.indexOf(other);

				// dragged down
				if (fromIndex < toIndex && currentY > midY)
				{
					swapPanels(draggingPanel, other);
					break;
				}
				// dragged up
				else if (fromIndex > toIndex && currentY < midY)
				{
					swapPanels(draggingPanel, other);
					break;
				}
			}
		}

		@Override
		public void mouseReleased(MouseEvent e)
		{
			if (e.getButton() != MouseEvent.BUTTON1)
			{
				return;
			}
			draggingPanel = null;

			questOverviewPanel.saveSidebar();
		}

		@Override public void mouseMoved(MouseEvent e) { }
	}
}
