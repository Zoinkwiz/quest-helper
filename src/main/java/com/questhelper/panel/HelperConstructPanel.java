package com.questhelper.panel;

import com.questhelper.managers.HelperConstructManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.SwingUtil;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class HelperConstructPanel extends PluginPanel
{
	private final HelperConstructManager helperConstructManager;
	private final JTextArea draftInfo = JGenerator.makeJTextArea();
	private final JPanel npcStepsList = new JPanel();
	private final JPanel objectStepsList = new JPanel();
	private final JPanel requirementsList = new JPanel();
	private final Timer refreshTimer;
	private final JScrollPane scrollPane;
	private String lastRenderSignature = "";

	public HelperConstructPanel(HelperConstructManager helperConstructManager)
	{
		super(false);
		this.helperConstructManager = helperConstructManager;

		setBackground(ColorScheme.DARK_GRAY_COLOR);
		setLayout(new BorderLayout());

		FixedWidthPanel root = new FixedWidthPanel();
		root.setBackground(ColorScheme.DARK_GRAY_COLOR);
		root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
		root.setBorder(new EmptyBorder(8, 8, 8, 8));
		root.setAlignmentX(Component.LEFT_ALIGNMENT);

		var title = JGenerator.makeJTextArea("Quest Helper Construct");
		title.setForeground(Color.WHITE);
		root.add(title);
		root.add(Box.createVerticalStrut(8));

		draftInfo.setForeground(Color.LIGHT_GRAY);
		draftInfo.setText("Draft: " + helperConstructManager.getCurrentDraftClassName());
		draftInfo.setLineWrap(true);
		draftInfo.setWrapStyleWord(true);
		draftInfo.setMaximumSize(new Dimension(PluginPanel.PANEL_WIDTH - 20, Integer.MAX_VALUE));
		root.add(draftInfo);
		root.add(Box.createVerticalStrut(8));

		JPanel buttonRow = new JPanel(new GridLayout(1, 2, 6, 0));
		buttonRow.setBackground(ColorScheme.DARK_GRAY_COLOR);
		buttonRow.setMaximumSize(new Dimension(PluginPanel.PANEL_WIDTH - 20, 28));
		JButton buildButton = new JButton("Build");
		JButton resetButton = new JButton("Reset");
		SwingUtil.removeButtonDecorations(buildButton);
		SwingUtil.removeButtonDecorations(resetButton);
		buildButton.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		resetButton.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		buildButton.setForeground(Color.WHITE);
		resetButton.setForeground(Color.WHITE);
		buildButton.addActionListener(e -> helperConstructManager.buildToClipboardFromUi());
		resetButton.addActionListener(e ->
		{
			helperConstructManager.resetDraftAndClearSavedStateFromUi();
			refresh();
		});
		buttonRow.add(buildButton);
		buttonRow.add(resetButton);
		root.add(buttonRow);
		root.add(Box.createVerticalStrut(10));

		var borderNpcSteps = new JPanel(new BorderLayout());
		npcStepsList.setLayout(new BoxLayout(npcStepsList, BoxLayout.Y_AXIS));
		npcStepsList.setBorder(new EmptyBorder(10, 5, 10, 5));
		borderNpcSteps.add(npcStepsList);

		var borderObjectSteps = new JPanel(new BorderLayout());
		objectStepsList.setLayout(new BoxLayout(objectStepsList, BoxLayout.Y_AXIS));
		objectStepsList.setBorder(new EmptyBorder(10, 5, 10, 5));
		borderObjectSteps.add(objectStepsList);

		var borderRequirements = new JPanel(new BorderLayout());
		requirementsList.setLayout(new BoxLayout(requirementsList, BoxLayout.Y_AXIS));
		requirementsList.setBorder(new EmptyBorder(10, 5, 10, 5));
		borderRequirements.add(requirementsList);

		root.add(sectionLabel("NPC Steps"));
		root.add(borderNpcSteps);
		root.add(Box.createVerticalStrut(10));
		root.add(sectionLabel("Object Steps"));
		root.add(borderObjectSteps);
		root.add(Box.createVerticalStrut(10));
		root.add(sectionLabel("Captured Requirements"));
		root.add(borderRequirements);

		scrollPane = new JScrollPane(root);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		add(scrollPane, BorderLayout.CENTER);

		refresh();

		// Keep panel list in sync with right-click captures made outside this panel.
		refreshTimer = new Timer(1000, e -> refreshIfChanged());
		refreshTimer.setRepeats(true);
		refreshTimer.start();
	}

	public void refresh()
	{
		int scrollValue = scrollPane.getVerticalScrollBar().getValue();
		draftInfo.setText("Draft: " + helperConstructManager.getCurrentDraftClassName());
		rebuildNpcStepRows();
		rebuildObjectStepRows();
		rebuildRequirementRows();
		lastRenderSignature = computeSignature();
		revalidate();
		repaint();
		SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(scrollValue));
	}

	public void shutDown()
	{
		refreshTimer.stop();
	}

	private void rebuildNpcStepRows()
	{
		npcStepsList.removeAll();
		var rows = helperConstructManager.getNpcStepSummaries();
		if (rows.isEmpty())
		{
			npcStepsList.add(emptyLabel("No NPC steps captured."));
			return;
		}

		for (int i = 0; i < rows.size(); i++)
		{
			JPanel row = makeStepRow(rows.get(i), i, StepCategory.NPC);
			row.setAlignmentX(Component.LEFT_ALIGNMENT);
			row.setMaximumSize(new Dimension(Integer.MAX_VALUE, row.getPreferredSize().height));
			npcStepsList.add(row);
		}
	}

	private void rebuildObjectStepRows()
	{
		objectStepsList.removeAll();
		var rows = helperConstructManager.getObjectStepSummaries();
		if (rows.isEmpty())
		{
			objectStepsList.add(emptyLabel("No object steps captured."));
			return;
		}

		for (int i = 0; i < rows.size(); i++)
		{
			JPanel row = makeStepRow(rows.get(i), i, StepCategory.OBJECT);
			row.setAlignmentX(Component.LEFT_ALIGNMENT);
			row.setMaximumSize(new Dimension(Integer.MAX_VALUE, row.getPreferredSize().height));
			objectStepsList.add(row);
		}
	}

	private void refreshIfChanged()
	{
		String currentSignature = computeSignature();
		if (!currentSignature.equals(lastRenderSignature))
		{
			refresh();
		}
	}

	private String computeSignature()
	{
		return helperConstructManager.getCurrentDraftClassName()
			+ "|N|" + String.join("\n", helperConstructManager.getNpcStepSummaries())
			+ "|O|" + String.join("\n", helperConstructManager.getObjectStepSummaries())
			+ "|R|" + String.join("\n", helperConstructManager.getRequirementSummaries());
	}

	private void rebuildRequirementRows()
	{
		requirementsList.removeAll();
		var rows = helperConstructManager.getRequirementSummaries();
		if (rows.isEmpty())
		{
			requirementsList.add(emptyLabel("No requirements captured."));
			return;
		}

		for (int i = 0; i < rows.size(); i++)
		{
			JPanel row = makeRequirementRow(rows.get(i), i);
			row.setAlignmentX(Component.LEFT_ALIGNMENT);
			row.setMaximumSize(new Dimension(Integer.MAX_VALUE, row.getPreferredSize().height));
			requirementsList.add(row);
		}
	}

	private JPanel makeStepRow(String text, int index, StepCategory category)
	{
		JPanel row = new FixedWidthPanel();
		row.setLayout(new BorderLayout(0, 4));
		row.setBackground(ColorScheme.DARK_GRAY_COLOR);
		row.setBorder(new EmptyBorder(4, 0, 4, 0));
		row.setAlignmentX(Component.LEFT_ALIGNMENT);

		var textArea = JGenerator.makeJTextArea(text);
		textArea.setForeground(Color.WHITE);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setEditable(false);
		textArea.setFocusable(false);
		textArea.setBackground(ColorScheme.DARK_GRAY_COLOR);
		textArea.setBorder(new EmptyBorder(0, 0, 0, 0));
		row.add(textArea, BorderLayout.CENTER);

		JButton remove = new JButton("Remove");
		SwingUtil.removeButtonDecorations(remove);
		remove.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		remove.setForeground(Color.WHITE);
		remove.setPreferredSize(new Dimension(68, 22));
		remove.addActionListener(e ->
		{
			if (category == StepCategory.NPC)
			{
				helperConstructManager.removeNpcStepAt(index);
			}
			else
			{
				helperConstructManager.removeObjectStepAt(index);
			}
			refresh();
		});
		JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		actionRow.setBackground(ColorScheme.DARK_GRAY_COLOR);
		actionRow.add(remove);
		row.add(actionRow, BorderLayout.SOUTH);
		return row;
	}

	private JPanel makeRequirementRow(String text, int index)
	{
		JPanel row = new FixedWidthPanel();
		row.setLayout(new BorderLayout(0, 4));
		row.setBackground(ColorScheme.DARK_GRAY_COLOR);
		row.setBorder(new EmptyBorder(4, 0, 4, 0));
		row.setAlignmentX(Component.LEFT_ALIGNMENT);

		var textArea = JGenerator.makeJTextArea(text);
		textArea.setForeground(Color.WHITE);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setEditable(false);
		textArea.setFocusable(false);
		textArea.setBackground(ColorScheme.DARK_GRAY_COLOR);
		textArea.setBorder(new EmptyBorder(0, 0, 0, 0));
		row.add(textArea, BorderLayout.CENTER);

		JButton remove = new JButton("Remove");
		SwingUtil.removeButtonDecorations(remove);
		remove.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		remove.setForeground(Color.WHITE);
		remove.setPreferredSize(new Dimension(68, 22));
		remove.addActionListener(e ->
		{
			helperConstructManager.removeRequirementAt(index);
			refresh();
		});
		JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		actionRow.setBackground(ColorScheme.DARK_GRAY_COLOR);
		actionRow.add(remove);
		row.add(actionRow, BorderLayout.SOUTH);
		return row;
	}

	private JTextArea sectionLabel(String text)
	{
		var label = JGenerator.makeJTextArea(text);
		label.setForeground(Color.GRAY);
		label.setLineWrap(true);
		label.setWrapStyleWord(true);
		return label;
	}

	private JTextArea emptyLabel(String text)
	{
		var label = JGenerator.makeJTextArea(text);
		label.setForeground(Color.GRAY);
		label.setAlignmentX(Component.LEFT_ALIGNMENT);
		label.setLineWrap(true);
		label.setWrapStyleWord(true);
		return label;
	}

	private enum StepCategory
	{
		NPC,
		OBJECT
	}
}
