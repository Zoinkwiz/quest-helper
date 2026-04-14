/*
 * Copyright (c) 2026, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.maker;

import com.questhelper.panel.JGenerator;
import com.questhelper.requirements.util.Operation;
import com.questhelper.steps.choice.DialogChoiceStep;
import net.runelite.api.Skill;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.util.SwingUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.RowFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Consumer;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.KeyboardFocusManager;

public final class HelperConstructEditorPanel extends JPanel
{
	private static final Color LEAGUE_STEP_BACKGROUND = new Color(87, 52, 20);
	// TODO: Review text
	private static final String USAGE_GUIDE = String.join("\n",
		"1. In-game: right-click NPCs, objects, items, or Walk here on a tile and use the Construct: menu entries to capture definitions.",
		"2. NPC / Object / Generic tabs: edit Name/Var, id (NPC/object), world point, and instruction; click Attachments to pick requirements, toggle Highlight for item rows, and save. Select one or more rows and use Add step / Remove at the bottom right (no in-table remove column). Ctrl/Cmd+N and Ctrl/Cmd+O convert the selected row to an NPC or Object step when focus is not in the filter field. Use the search field above each table to filter rows by any column. In Step attachments → Add…, search filters the pick list.",
		"3. Item reqs tab: Name and ID columns (editable); Add / Remove at the bottom right for empty rows or deleting the selected row. Search filters by name or id.",
		"4. Quest order tab: add references to definitions, section dividers, and step text. Drag rows to reorder. With a row selected, Add Step / Add Section insert the new row directly under that selection (or the lowest selected row when several are selected). Select one or more rows and use Remove selected to delete them from quest order. Click Conditions in a row to edit branch requirements (logic groups, varbits, zones, captured items, NOT) and choose semantics (show when true vs continue when true). Varbit values are edited on the Varbit reqs tab; zone corners on the Zone reqs tab — not on the order tree leaf nodes. Search filters by var name, section text, or instruction. Add Step lists each definition by instruction / readable text; its search matches those fields (and ids) but does not show internal step ids.",
		"5. Varbit reqs tab: one row per quest-order slot that uses a varbit (Conditions includes an order varbit slot, or this tab already has a row for that slot). Values are stored on the order row (not the step definition). Edit Var name, varbit id, value, Operation (e.g. EQUAL), and optional display text. Add appends a placeholder generic step and order row with varbit id 0, required value 0, and a generic var name. Remove clears varbit routing and order-varbit conditions for that row only; it does not remove the step from quest order. Search filters varbit rows.",
		"6. Zone reqs tab: one row per quest-order slot that uses a zone (Conditions contains zone requirements, or corners are already set on the order row). Edit Var name (on the referenced step), two corners as \"x, y, plane\", and optional display text. Add attaches default corners plus a zone condition to the selected Quest order row; if no row is selected it creates a new generic step + order row with zone routing. You can also add zones from Quest order → Conditions (Create new zone… / Add zone). Remove clears zone routing and zone conditions for that row only. Search filters zone rows.",
		"7. Build copies generated Java to the clipboard.",
		"8. JSON export/import uses extended Tasks Tracker route JSON: `sections`/`items` for the plugin wiki schema, plus `questHelperMaker` with the full maker snapshot. The draft auto-saves to `quest-helper/construct-draft.json` under your RuneLite user folder (same shape as Export / Save JSON)."
	);

	private final HelperConstructManager helperConstructManager;
	/** Step library tabs in display order (NPC, Object, Generic); add a {@link ConstructStepKind} here to add a tab. */
	private final List<StepLibraryTab> stepLibraryTabs = new ArrayList<>();
	private final LibraryTableModel requirementLibraryModel = new LibraryTableModel();
	private JTable requirementTable;
	private final JTable varbitReqsTable;
	private final JTable zoneReqsTable;
	private final JTable skillReqsTable;
	private final JTabbedPane mainTabs;
	private int zoneTabIndex = -1;
	private int orderTabIndex = -1;
	private JTable orderTable;
	private final StepOrderTableModel stepOrderTableModel = new StepOrderTableModel();
	private final VarbitRoutingTableModel varbitRoutingTableModel = new VarbitRoutingTableModel();
	private final ZoneRoutingTableModel zoneRoutingTableModel = new ZoneRoutingTableModel();
	private final SkillRoutingTableModel skillRoutingTableModel = new SkillRoutingTableModel();
	private final Runnable draftChangeListener;
	private final JButton worldMapRouteButton;
	private final JButton routeRevealButton;
	private JDialog routeRevealDialog;
	private JSlider routeRevealSlider;
	private JLabel routeRevealValueLabel;
	/** Maker header actions; reflows into a "More…" menu when the panel is too narrow. */
	private final OverflowingMakerToolbar makerToolbarRow;

	public HelperConstructEditorPanel(HelperConstructManager helperConstructManager)
	{
		this.helperConstructManager = helperConstructManager;

		setBackground(ColorScheme.DARK_GRAY_COLOR);
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(8, 8, 8, 8));

		JPanel header = new JPanel(new BorderLayout(0, 6));
		header.setOpaque(false);

		JPanel titleRow = new JPanel(new BorderLayout(8, 0));
		titleRow.setOpaque(false);
		var title = JGenerator.makeJTextArea("Quest Helper Maker");
		title.setForeground(Color.WHITE);
		titleRow.add(title, BorderLayout.CENTER);
		JButton helpButton = new JButton("How to use…");
		SwingUtil.removeButtonDecorations(helpButton);
		helpButton.setForeground(new Color(143, 188, 187));
		helpButton.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		helpButton.setToolTipText("Open the full usage guide");
		helpButton.addActionListener(e -> JOptionPane.showMessageDialog(this, USAGE_GUIDE, "Quest Helper Maker", JOptionPane.INFORMATION_MESSAGE));
		titleRow.add(helpButton, BorderLayout.LINE_END);
		header.add(titleRow, BorderLayout.NORTH);

		JButton buildButton = new JButton("Build code");
		JButton previewButton = new JButton("Preview");
		JButton resetButton = new JButton("Reset");
		worldMapRouteButton = new JButton("WorldMap");
		routeRevealButton = new JButton("Route reveal");
		JButton exportJsonButton = new JButton("Export \u25be");
		JButton importJsonButton = new JButton("Import \u25be");
		applyMakerToolbarStyle(buildButton, previewButton, resetButton, worldMapRouteButton, routeRevealButton,
			exportJsonButton, importJsonButton);
		buildButton.setToolTipText("Copy generated Java helper source to the clipboard.");
		previewButton.setToolTipText("Load this draft as a preview in the main Quest Helper sidebar.");
		resetButton.setToolTipText("Clear the draft and reset the auto-saved maker file (asks for confirmation).");
		worldMapRouteButton.setToolTipText("Toggle in-game world map markers for the ordered route.");
		routeRevealButton.setToolTipText("Open/close a small slider to reveal ordered world map route steps progressively.");
		exportJsonButton.setToolTipText("Export as Quest Helper format or League Route format.");
		importJsonButton.setToolTipText("<html>Import from QH format, League Route, or leagues-full task data.<br>Modes: Full fresh, Just order, Just more data.</html>");
		setOverflowHandler(importJsonButton, this::showImportMenu);
		setOverflowHandler(exportJsonButton, this::showExportMenu);
		syncWorldMapRouteButtonLabel();

		buildButton.addActionListener(e -> helperConstructManager.buildToClipboardFromUi());
		resetButton.addActionListener(e ->
		{
			int confirm = JOptionPane.showConfirmDialog(this,
				"Clear the maker draft and reset the auto-saved file under your RuneLite folder?\nExport or save first if you need a backup.",
				"Reset draft",
				JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.WARNING_MESSAGE);
			if (confirm != JOptionPane.OK_OPTION)
			{
				return;
			}
			helperConstructManager.resetDraftAndClearSavedStateFromUi();
			refresh();
		});
		worldMapRouteButton.addActionListener(e ->
		{
			helperConstructManager.toggleWorldMapRoutePreviewFromUi();
			syncWorldMapRouteButtonLabel();
		});
		routeRevealButton.addActionListener(e -> toggleRouteRevealDialog());
		previewButton.addActionListener(e -> helperConstructManager.previewInSidebarFromUi());
		exportJsonButton.addActionListener(e -> showExportMenu(exportJsonButton));
		importJsonButton.addActionListener(e -> showImportMenu(importJsonButton));

		List<JComponent> makerToolbarChain = new ArrayList<>();
		makerToolbarChain.add(importJsonButton);
		makerToolbarChain.add(exportJsonButton);
		makerToolbarChain.add(newToolbarSeparator());
		makerToolbarChain.add(buildButton);
		makerToolbarChain.add(previewButton);
		makerToolbarChain.add(resetButton);
		makerToolbarChain.add(worldMapRouteButton);
		makerToolbarChain.add(routeRevealButton);
		makerToolbarRow = new OverflowingMakerToolbar(makerToolbarChain, this::applyMakerToolbarStyle);
		makerToolbarRow.setOpaque(false);
		header.add(makerToolbarRow, BorderLayout.SOUTH);

		JPanel orderedView = buildOrderedView();
		mainTabs = new JTabbedPane(SwingConstants.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
		mainTabs.setBackground(ColorScheme.DARK_GRAY_COLOR);
		styleConstructTabbedPane(mainTabs);
		for (ConstructStepKind k : List.of(ConstructStepKind.NPC, ConstructStepKind.OBJECT, ConstructStepKind.TEXT))
		{
			StepLibraryTableModel model = new StepLibraryTableModel(k);
			JTable table = new JTable(model);
			table.getSelectionModel().addListSelectionListener(e ->
			{
				if (e.getValueIsAdjusting())
				{
					return;
				}
				int sel = selectedModelRow(table);
				if (sel < 0)
				{
					helperConstructManager.clearConstructMenuSelectedStep();
					helperConstructManager.clearSelectedRenderedStep();
				}
				else
				{
					helperConstructManager.setConstructMenuSelectedStep(k, sel);
					helperConstructManager.setSelectedRenderedStepFromLibrary(k, sel);
				}
			});
			mainTabs.addTab(stepLibraryTabTitle(k), wrapStepLibraryTableWithToolbar(table, k));
			stepLibraryTabs.add(new StepLibraryTab(k, model, table));
		}
		mainTabs.addTab("Item reqs", buildItemRequirementsPanel());

		varbitReqsTable = new JTable(varbitRoutingTableModel);
		styleVarbitRoutingTable(varbitReqsTable);
		varbitReqsTable.setRowHeight(24);
		varbitReqsTable.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(new JTextField()));
		varbitReqsTable.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(new JTextField()));
		varbitReqsTable.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(new JTextField()));
		JComboBox<Operation> varbitOperationCombo = getOperationJComboBox();
		varbitReqsTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer()
		{
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
			{
				Object label = value instanceof Operation ? operationChoiceLabel((Operation) value) : value;
				return super.getTableCellRendererComponent(table, label, isSelected, hasFocus, row, column);
			}
		});
		varbitReqsTable.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(varbitOperationCombo));
		varbitReqsTable.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(new JTextField()));
		JScrollPane varbitScroll = new JScrollPane(varbitReqsTable);
		varbitScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		varbitScroll.setBorder(new EmptyBorder(0, 0, 0, 0));
		JPanel varbitPanel = new JPanel(new BorderLayout(0, 6));
		varbitPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);
		JPanel varbitCenter = new JPanel(new BorderLayout(0, 4));
		varbitCenter.setOpaque(false);
		JTextField varbitSearch = newMakerSearchField("Filter by var name, varbit id, value, operation, or display text.");
		wireTableSearchField(varbitSearch, varbitReqsTable);
		varbitCenter.add(varbitSearch, BorderLayout.NORTH);
		varbitCenter.add(varbitScroll, BorderLayout.CENTER);
		varbitPanel.add(varbitCenter, BorderLayout.CENTER);
		JButton addVarbitSlotButton = new JButton("Add");
		JButton removeVarbitSlotButton = new JButton("Remove");
		applyMakerToolbarStyle(addVarbitSlotButton, removeVarbitSlotButton);
		addVarbitSlotButton.addActionListener(e ->
		{
			helperConstructManager.addEmptyVarbitSlotFromUi();
			refresh();
		});
		addVarbitSlotButton.setToolTipText("Append a placeholder generic step, a quest-order row, and a routing varbit (0 / 0) stored on that order row for the Varbit tab.");
		removeVarbitSlotButton.addActionListener(e ->
		{
			int r = selectedModelRow(varbitReqsTable);
			if (r < 0)
			{
				return;
			}
			String orderSlotId = varbitRoutingTableModel.getOrderSlotIdAt(r);
			if (orderSlotId != null && helperConstructManager.clearVarbitRoutingForOrderSlotId(orderSlotId))
			{
				refresh();
			}
		});
		JPanel varbitActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
		varbitActions.setOpaque(false);
		varbitActions.add(addVarbitSlotButton);
		varbitActions.add(removeVarbitSlotButton);
		varbitPanel.add(varbitActions, BorderLayout.SOUTH);
		mainTabs.addTab("Varbit reqs", varbitPanel);

		zoneReqsTable = new JTable(zoneRoutingTableModel);
		styleZoneRoutingTable(zoneReqsTable);
		zoneReqsTable.setRowHeight(24);
		zoneReqsTable.getSelectionModel().addListSelectionListener(e ->
		{
			if (e.getValueIsAdjusting())
			{
				return;
			}
			int r = selectedModelRow(zoneReqsTable);
			if (r < 0)
			{
				helperConstructManager.clearSelectedZoneOverlay();
				return;
			}
			String orderSlotId = zoneRoutingTableModel.getOrderSlotIdAt(r);
			helperConstructManager.setSelectedZoneOverlayByOrderSlotId(orderSlotId);
		});
		zoneReqsTable.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				if (zoneReqsTable.rowAtPoint(e.getPoint()) < 0)
				{
					zoneReqsTable.clearSelection();
					helperConstructManager.clearSelectedZoneOverlay();
				}
			}
		});
		zoneReqsTable.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(new JTextField()));
		zoneReqsTable.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(new JTextField()));
		zoneReqsTable.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(new JTextField()));
		zoneReqsTable.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(new JTextField()));
		JScrollPane zoneScroll = new JScrollPane(zoneReqsTable);
		zoneScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		zoneScroll.setBorder(new EmptyBorder(0, 0, 0, 0));
		JPanel zonePanel = new JPanel(new BorderLayout(0, 6));
		zonePanel.setBackground(ColorScheme.DARK_GRAY_COLOR);
		JPanel zoneCenter = new JPanel(new BorderLayout(0, 4));
		zoneCenter.setOpaque(false);
		JTextField zoneSearch = newMakerSearchField("Filter by var name, corner coordinates, or display text.");
		wireTableSearchField(zoneSearch, zoneReqsTable);
		zoneCenter.add(zoneSearch, BorderLayout.NORTH);
		zoneCenter.add(zoneScroll, BorderLayout.CENTER);
		zonePanel.add(zoneCenter, BorderLayout.CENTER);
		JButton addZoneSlotButton = new JButton("Add");
		JButton removeZoneSlotButton = new JButton("Remove");
		applyMakerToolbarStyle(addZoneSlotButton, removeZoneSlotButton);
		addZoneSlotButton.addActionListener(e ->
		{
			int ord = selectedSingleQuestOrderStepOrderIndexOrNeg1();
			if (ord < 0)
			{
				if (helperConstructManager.addEmptyZoneSlotFromUi())
				{
					refresh();
					return;
				}
				JOptionPane.showMessageDialog(this,
					"Could not create a new zone row.\n"
						+ "Try adding a generic step first, then use Zone reqs Add on that order row.",
					"Zone reqs",
					JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			if (helperConstructManager.addZoneSlotToOrderRowFromUi(ord))
			{
				refresh();
				return;
			}
			JOptionPane.showMessageDialog(this,
				"Could not add a zone slot to the selected row. It may already use a zone, or the conditions tree could not be updated.\n"
					+ "Use Conditions on that row to add or edit zone logic.",
				"Zone reqs",
				JOptionPane.INFORMATION_MESSAGE);
		});
		addZoneSlotButton.setToolTipText("Adds zone routing to the selected Quest order row; if none is selected, creates a new generic step + order row with default zone corners.");
		removeZoneSlotButton.addActionListener(e ->
		{
			int r = selectedModelRow(zoneReqsTable);
			if (r < 0)
			{
				return;
			}
			String orderSlotId = zoneRoutingTableModel.getOrderSlotIdAt(r);
			if (orderSlotId != null && helperConstructManager.clearZoneRoutingForOrderSlotId(orderSlotId))
			{
				refresh();
			}
		});
		JPanel zoneActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
		zoneActions.setOpaque(false);
		zoneActions.add(addZoneSlotButton);
		zoneActions.add(removeZoneSlotButton);
		zonePanel.add(zoneActions, BorderLayout.SOUTH);
		mainTabs.addTab("Zone reqs", zonePanel);
		zoneTabIndex = mainTabs.indexOfComponent(zonePanel);

		skillReqsTable = new JTable(skillRoutingTableModel);
		styleVarbitRoutingTable(skillReqsTable);
		skillReqsTable.setRowHeight(24);
		JComboBox<Skill> skillCombo = new JComboBox<>(Skill.values());
		skillCombo.setMaximumRowCount(Skill.values().length);
		skillReqsTable.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(skillCombo));
		skillReqsTable.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(new JTextField()));
		skillReqsTable.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(new JCheckBox()));
		skillReqsTable.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(new JTextField()));
		JComboBox<Operation> skillOperationCombo = getOperationJComboBox();
		skillReqsTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer()
		{
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
			{
				Object label = value instanceof Operation ? operationChoiceLabel((Operation) value) : value;
				return super.getTableCellRendererComponent(table, label, isSelected, hasFocus, row, column);
			}
		});
		skillReqsTable.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(skillOperationCombo));
		JScrollPane skillScroll = new JScrollPane(skillReqsTable);
		skillScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		skillScroll.setBorder(new EmptyBorder(0, 0, 0, 0));
		JPanel skillPanel = new JPanel(new BorderLayout(0, 6));
		skillPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);
		JPanel skillCenter = new JPanel(new BorderLayout(0, 4));
		skillCenter.setOpaque(false);
		JTextField skillSearch = newMakerSearchField("Filter by skill, required level, boostable flag, display text, or operation.");
		wireTableSearchField(skillSearch, skillReqsTable);
		skillCenter.add(skillSearch, BorderLayout.NORTH);
		skillCenter.add(skillScroll, BorderLayout.CENTER);
		skillPanel.add(skillCenter, BorderLayout.CENTER);
		JButton addSkillReqButton = new JButton("Add");
		JButton removeSkillReqButton = new JButton("Remove");
		applyMakerToolbarStyle(addSkillReqButton, removeSkillReqButton);
		addSkillReqButton.addActionListener(e ->
		{
			helperConstructManager.addEmptySkillRequirementFromUi();
			refresh();
		});
		removeSkillReqButton.addActionListener(e ->
		{
			int r = selectedModelRow(skillReqsTable);
			if (r < 0)
			{
				return;
			}
			if (helperConstructManager.removeSkillRequirementAt(r))
			{
				refresh();
			}
		});
		JPanel skillActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
		skillActions.setOpaque(false);
		skillActions.add(addSkillReqButton);
		skillActions.add(removeSkillReqButton);
		skillPanel.add(skillActions, BorderLayout.SOUTH);
		mainTabs.addTab("Skill reqs", skillPanel);

		mainTabs.addTab("Quest order", orderedView);
		orderTabIndex = mainTabs.indexOfComponent(orderedView);
		mainTabs.addChangeListener(e ->
		{
			clearStepRenderSourceSelectionsOnTabSwitch();
			syncZoneOverlaySelectionState();
			syncConstructMenuSelectionState();
			syncSelectedStepRenderSelectionState();
		});

		add(header, BorderLayout.NORTH);
		add(mainTabs, BorderLayout.CENTER);

		refresh();
		draftChangeListener = () -> SwingUtilities.invokeLater(this::refresh);
		helperConstructManager.addDraftChangeListener(draftChangeListener);
	}

	private static @NotNull JComboBox<Operation> getOperationJComboBox()
	{
		JComboBox<Operation> varbitOperationCombo = new JComboBox<>(Operation.values());
		varbitOperationCombo.setMaximumRowCount(Operation.values().length);
		varbitOperationCombo.setRenderer(new DefaultListCellRenderer()
		{
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
			{
				Object label = value instanceof Operation ? operationChoiceLabel((Operation) value) : value;
				return super.getListCellRendererComponent(list, label, index, isSelected, cellHasFocus);
			}
		});
		return varbitOperationCombo;
	}

	private void showExportMenu(JButton anchor)
	{
		JPopupMenu menu = new JPopupMenu();
		JMenuItem copyQh = new JMenuItem("Copy QH format to clipboard");
		copyQh.addActionListener(e -> exportJsonToClipboard(HelperConstructManager.ExportFormat.QH_FORMAT));
		JMenuItem saveQh = new JMenuItem("Save QH format to file...");
		saveQh.addActionListener(e -> saveJsonToFile(HelperConstructManager.ExportFormat.QH_FORMAT));
		JMenuItem copyRoute = new JMenuItem("Copy League Route to clipboard");
		copyRoute.addActionListener(e -> exportJsonToClipboard(HelperConstructManager.ExportFormat.LEAGUE_ROUTE));
		JMenuItem saveRoute = new JMenuItem("Save League Route to file...");
		saveRoute.addActionListener(e -> saveJsonToFile(HelperConstructManager.ExportFormat.LEAGUE_ROUTE));
		menu.add(copyQh);
		menu.add(saveQh);
		menu.addSeparator();
		menu.add(copyRoute);
		menu.add(saveRoute);
		menu.show(anchor, 0, anchor.getHeight());
	}

	private void showImportMenu(JButton anchor)
	{
		JPopupMenu menu = new JPopupMenu();
		JMenuItem fullFresh = new JMenuItem("Full fresh import...");
		fullFresh.addActionListener(e -> showImportJsonDialog(HelperConstructManager.ImportMode.FULL_FRESH));
		JMenuItem orderOnly = new JMenuItem("Just order import...");
		orderOnly.addActionListener(e -> showImportJsonDialog(HelperConstructManager.ImportMode.ORDER_ONLY));
		JMenuItem dataOnly = new JMenuItem("Just more data import...");
		dataOnly.addActionListener(e -> showImportJsonDialog(HelperConstructManager.ImportMode.DATA_ONLY));
		JMenuItem itemData = new JMenuItem("Import item data (QH)...");
		itemData.addActionListener(e -> showImportItemDataDialog());
		menu.add(fullFresh);
		menu.add(orderOnly);
		menu.add(dataOnly);
		menu.addSeparator();
		menu.add(itemData);
		menu.show(anchor, 0, anchor.getHeight());
	}

	private static void setOverflowHandler(JButton button, Consumer<JButton> handler)
	{
		button.putClientProperty("makerOverflowHandler", handler);
	}

	private void exportJsonToClipboard(HelperConstructManager.ExportFormat format)
	{
		String json = helperConstructManager.exportDraftJson(format);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(json), null);
		String label = format == HelperConstructManager.ExportFormat.QH_FORMAT ? "QH format" : "League Route format";
		JOptionPane.showMessageDialog(this,
			label + " copied to clipboard.",
			"Export",
			JOptionPane.INFORMATION_MESSAGE);
	}

	private void saveJsonToFile(HelperConstructManager.ExportFormat format)
	{
		JFileChooser fc = new JFileChooser();
		String base = helperConstructManager.getCurrentDraftClassName();
		if (base == null || base.isBlank())
		{
			base = "construct-draft";
		}
		String suffix = format == HelperConstructManager.ExportFormat.QH_FORMAT ? "-qh.json" : "-route.json";
		fc.setSelectedFile(new File(base.replaceAll("[^a-zA-Z0-9_-]+", "_") + suffix));
		if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION)
		{
			return;
		}
		File f = fc.getSelectedFile();
		try
		{
			Files.writeString(f.toPath(), helperConstructManager.exportDraftJson(format));
			JOptionPane.showMessageDialog(this, "Saved to:\n" + f.getAbsolutePath(), "Save JSON", JOptionPane.INFORMATION_MESSAGE);
		}
		catch (IOException ex)
		{
			JOptionPane.showMessageDialog(this, ex.getMessage(), "Save failed", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void showImportJsonDialog(HelperConstructManager.ImportMode mode)
	{
		String title;
		String warning;
		switch (mode)
		{
			case FULL_FRESH:
				title = "Full fresh import";
				warning = "Replace the current maker draft with imported JSON?\n"
					+ "Accepts QH format, League Route, or leagues-full task data.";
				break;
			case ORDER_ONLY:
				title = "Just order import";
				warning = "Replace current quest order from imported JSON?\n"
					+ "Existing steps are reused by ID where possible; missing IDs create new steps.";
				break;
			case DATA_ONLY:
				title = "Just more data import";
				warning = "Import additional missing step definitions from imported JSON?\n"
					+ "Current quest order remains unchanged; dedupe is ID-only.";
				break;
			default:
				title = "Import";
				warning = "Run import?";
				break;
		}
		int confirm = JOptionPane.showConfirmDialog(this, warning, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
		if (confirm != JOptionPane.OK_OPTION)
		{
			return;
		}

		JTextArea textArea = new JTextArea(14, 44);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		JButton browse = new JButton("Load from file...");
		browse.addActionListener(ev ->
		{
			JFileChooser fc = new JFileChooser();
			if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
			{
				try
				{
					textArea.setText(Files.readString(fc.getSelectedFile().toPath()));
				}
				catch (IOException ex)
				{
					JOptionPane.showMessageDialog(this, ex.getMessage(), "Read failed", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		JPanel panel = new JPanel(new BorderLayout(0, 6));
		panel.add(new JScrollPane(textArea), BorderLayout.CENTER);
		panel.add(browse, BorderLayout.SOUTH);

		int r = JOptionPane.showConfirmDialog(this, panel, "Paste JSON to import", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (r != JOptionPane.OK_OPTION)
		{
			return;
		}

		HelperConstructManager.ImportDraftResult result = helperConstructManager.importFromJson(textArea.getText(), mode);
		showImportResult(result);
	}

	private void showImportResult(HelperConstructManager.ImportDraftResult result)
	{
		if (result.isSuccess())
		{
			refresh();
			String info = result.getInfoMessage() == null ? "Import completed and saved to maker draft file." : result.getInfoMessage();
			JOptionPane.showMessageDialog(this, info, "Import", JOptionPane.INFORMATION_MESSAGE);
		}
		else
		{
			JOptionPane.showMessageDialog(this, result.getErrorMessage(), "Import failed", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void showImportItemDataDialog()
	{
		int confirm = JOptionPane.showConfirmDialog(this,
			"Import item requirement rows from QH canonical JSON (questHelperMaker) into Item reqs?\n"
				+ "This merges missing rows and leaves steps/order untouched.",
			"Import item data",
			JOptionPane.OK_CANCEL_OPTION,
			JOptionPane.WARNING_MESSAGE);
		if (confirm != JOptionPane.OK_OPTION)
		{
			return;
		}

		JTextArea textArea = new JTextArea(14, 44);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		JButton browse = new JButton("Load from file...");
		browse.addActionListener(ev ->
		{
			JFileChooser fc = new JFileChooser();
			if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
			{
				try
				{
					textArea.setText(Files.readString(fc.getSelectedFile().toPath()));
				}
				catch (IOException ex)
				{
					JOptionPane.showMessageDialog(this, ex.getMessage(), "Read failed", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		JPanel panel = new JPanel(new BorderLayout(0, 6));
		panel.add(new JScrollPane(textArea), BorderLayout.CENTER);
		panel.add(browse, BorderLayout.SOUTH);

		int r = JOptionPane.showConfirmDialog(this, panel, "Paste QH canonical JSON", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (r != JOptionPane.OK_OPTION)
		{
			return;
		}

		HelperConstructManager.ImportDraftResult result = helperConstructManager.importItemRequirementsFromJson(textArea.getText());
		showImportResult(result);
	}

	private void applyMakerToolbarStyle(JButton... buttons)
	{
		for (JButton b : buttons)
		{
			SwingUtil.removeButtonDecorations(b);
			b.setBackground(ColorScheme.DARKER_GRAY_COLOR);
			b.setForeground(Color.WHITE);
		}
	}

	private void syncWorldMapRouteButtonLabel()
	{
		if (worldMapRouteButton != null)
		{
			worldMapRouteButton.setText(helperConstructManager.isWorldMapRoutePreviewEnabled()
				? "WorldMap (on)"
				: "WorldMap (off)");
		}
		if (makerToolbarRow != null)
		{
			makerToolbarRow.reconcile();
		}
	}

	private void syncRouteRevealButtonLabel()
	{
		if (routeRevealButton == null)
		{
			return;
		}
		boolean open = routeRevealDialog != null && routeRevealDialog.isShowing();
		routeRevealButton.setText(open ? "Route reveal (open)" : "Route reveal");
		if (makerToolbarRow != null)
		{
			makerToolbarRow.reconcile();
		}
	}

	private void toggleRouteRevealDialog()
	{
		if (routeRevealDialog != null && routeRevealDialog.isShowing())
		{
			routeRevealDialog.dispose();
			return;
		}
		Window owner = SwingUtilities.getWindowAncestor(this);
		if (owner == null)
		{
			return;
		}
		JDialog dialog = new JDialog(owner, "Route reveal", Dialog.ModalityType.MODELESS);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dialog.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosed(WindowEvent e)
			{
				routeRevealDialog = null;
				routeRevealSlider = null;
				routeRevealValueLabel = null;
				syncRouteRevealButtonLabel();
			}
		});

		JSlider slider = new JSlider(0, 100, helperConstructManager.getWorldMapRouteRevealPercent());
		slider.setOpaque(false);
		slider.setPaintTicks(true);
		slider.setMajorTickSpacing(25);
		slider.setMinorTickSpacing(5);
		JButton stepLeftButton = new JButton("<");
		JButton stepRightButton = new JButton(">");
		applyMakerToolbarStyle(stepLeftButton, stepRightButton);
		stepLeftButton.setToolTipText("Show one fewer step.");
		stepRightButton.setToolTipText("Show one more step.");
		JLabel valueLabel = new JLabel();
		valueLabel.setForeground(Color.WHITE);
		JLabel hintLabel = new JLabel("Left shows earliest step only; right shows full route.");
		hintLabel.setForeground(Color.GRAY);
		updateRouteRevealDisplayState(slider, valueLabel);
		slider.addChangeListener(e ->
		{
			helperConstructManager.setWorldMapRouteRevealPercent(slider.getValue());
			updateRouteRevealDisplayState(slider, valueLabel);
		});
		stepLeftButton.addActionListener(e ->
		{
			helperConstructManager.stepWorldMapRouteRevealBy(-1);
			slider.setValue(helperConstructManager.getWorldMapRouteRevealPercent());
			updateRouteRevealDisplayState(slider, valueLabel);
		});
		stepRightButton.addActionListener(e ->
		{
			helperConstructManager.stepWorldMapRouteRevealBy(1);
			slider.setValue(helperConstructManager.getWorldMapRouteRevealPercent());
			updateRouteRevealDisplayState(slider, valueLabel);
		});

		JPanel sliderRow = new JPanel(new BorderLayout(6, 0));
		sliderRow.setOpaque(false);
		sliderRow.add(stepLeftButton, BorderLayout.WEST);
		sliderRow.add(slider, BorderLayout.CENTER);
		sliderRow.add(stepRightButton, BorderLayout.EAST);

		JPanel content = new JPanel(new BorderLayout(0, 6));
		content.setBackground(ColorScheme.DARK_GRAY_COLOR);
		content.setBorder(new EmptyBorder(8, 8, 8, 8));
		content.add(valueLabel, BorderLayout.NORTH);
		content.add(sliderRow, BorderLayout.CENTER);
		content.add(hintLabel, BorderLayout.SOUTH);
		dialog.setContentPane(content);
		dialog.pack();
		dialog.setLocationRelativeTo(this);
		dialog.setVisible(true);

		routeRevealDialog = dialog;
		routeRevealSlider = slider;
		routeRevealValueLabel = valueLabel;
		syncRouteRevealButtonLabel();
	}

	private void updateRouteRevealDisplayState(JSlider slider, JLabel valueLabel)
	{
		int totalSteps = helperConstructManager.getOrderedRouteStepCountForPreview();
		int percent = slider.getValue();
		int visibleSteps;
		if (totalSteps <= 0)
		{
			visibleSteps = 0;
		}
		else
		{
			visibleSteps = helperConstructManager.getVisibleRouteStepCountForPreview();
		}
		valueLabel.setText(String.format("Reveal: %d%% (%d/%d steps)", percent, visibleSteps, totalSteps));
		slider.setEnabled(totalSteps > 0);
	}

	private static JComponent newToolbarSeparator()
	{
		JSeparator sep = new JSeparator(SwingConstants.VERTICAL);
		Dimension d = sep.getPreferredSize();
		sep.setPreferredSize(new Dimension(Math.max(d.width, 2), 22));
		return sep;
	}

	private static void styleConstructTabbedPane(JTabbedPane tabs)
	{
		tabs.setOpaque(true);
		tabs.setBackground(ColorScheme.DARK_GRAY_COLOR);
	}

	private JTextField newMakerSearchField(String tooltip)
	{
		JTextField f = new JTextField();
		f.setForeground(Color.WHITE);
		f.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		f.setCaretColor(Color.WHITE);
		f.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(ColorScheme.DARKER_GRAY_COLOR),
			new EmptyBorder(2, 6, 2, 6)));
		if (tooltip != null && !tooltip.isBlank())
		{
			f.setToolTipText(tooltip);
		}
		return f;
	}

	private static void wireTableSearchField(JTextField searchField, JTable table)
	{
		TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
		sorter.setSortKeys(null);
		for (int i = 0; i < table.getColumnCount(); i++)
		{
			sorter.setSortable(i, false);
		}
		table.setRowSorter(sorter);
		Runnable applyFilter = () ->
		{
			String q = searchField.getText().trim().toLowerCase(Locale.ROOT);
			if (q.isEmpty())
			{
				sorter.setRowFilter(null);
				return;
			}
			sorter.setRowFilter(new RowFilter<>()
			{
				@Override
				public boolean include(RowFilter.Entry<? extends TableModel, ? extends Integer> entry)
				{
					for (int i = 0; i < entry.getValueCount(); i++)
					{
						Object v = entry.getValue(i);
						if (v != null && v.toString().toLowerCase(Locale.ROOT).contains(q))
						{
							return true;
						}
					}
					return false;
				}
			});
		};
		DocumentListener dl = new DocumentListener()
		{
			@Override
			public void insertUpdate(DocumentEvent e)
			{
				applyFilter.run();
			}

			@Override
			public void removeUpdate(DocumentEvent e)
			{
				applyFilter.run();
			}

			@Override
			public void changedUpdate(DocumentEvent e)
			{
				applyFilter.run();
			}
		};
		searchField.getDocument().addDocumentListener(dl);
	}

	private static int selectedModelRow(JTable table)
	{
		int v = table.getSelectedRow();
		if (v < 0)
		{
			return -1;
		}
		return table.convertRowIndexToModel(v);
	}

	/**
	 * Distinct model indices for all selected view rows, sorted descending (safe for removing from a list by index).
	 */
	private static List<Integer> selectedModelRowsDescending(JTable table)
	{
		int[] viewRows = table.getSelectedRows();
		if (viewRows == null || viewRows.length == 0)
		{
			return List.of();
		}
		Set<Integer> model = new LinkedHashSet<>();
		for (int v : viewRows)
		{
			int m = table.convertRowIndexToModel(v);
			if (m >= 0)
			{
				model.add(m);
			}
		}
		List<Integer> out = new ArrayList<>(model);
		out.sort(Collections.reverseOrder());
		return out;
	}

	/**
	 * @return model index at which to insert a new quest-order row so it appears directly under the selection
	 *         (below the lowest selected row when several are selected), or {@code -1} if nothing is selected
	 *         (caller should append).
	 */
	private int questOrderInsertIndexBelowSelection()
	{
		int[] viewRows = orderTable.getSelectedRows();
		if (viewRows == null || viewRows.length == 0)
		{
			return -1;
		}
		int maxModel = -1;
		for (int v : viewRows)
		{
			int m = orderTable.convertRowIndexToModel(v);
			if (m >= 0)
			{
				maxModel = Math.max(maxModel, m);
			}
		}
		if (maxModel < 0)
		{
			return -1;
		}
		return maxModel + 1;
	}

	/**
	 * @return persisted quest-order index when exactly one table row is selected and it is not a section divider;
	 *         otherwise {@code -1}
	 */
	private int selectedSingleQuestOrderStepOrderIndexOrNeg1()
	{
		int[] viewRows = orderTable.getSelectedRows();
		if (viewRows == null || viewRows.length != 1)
		{
			return -1;
		}
		int m = orderTable.convertRowIndexToModel(viewRows[0]);
		var entry = stepOrderTableModel.getRow(m);
		if (entry == null || entry.isSectionDivider())
		{
			return -1;
		}
		return entry.getIndex();
	}

	/** Maps persisted operation name to enum; unknown values become {@link Operation#EQUAL}. */
	private static Operation operationFromPersistedName(String name)
	{
		if (name == null || name.isBlank())
		{
			return Operation.EQUAL;
		}
		try
		{
			return Operation.valueOf(name.trim());
		}
		catch (IllegalArgumentException ex)
		{
			return Operation.EQUAL;
		}
	}

	private static String operationChoiceLabel(Operation o)
	{
		return o.name() + " (" + o.getDisplayText() + ")";
	}

	private JPanel buildItemRequirementsPanel()
	{
		JPanel panel = new JPanel(new BorderLayout(0, 6));
		panel.setBackground(ColorScheme.DARK_GRAY_COLOR);
		JPanel north = new JPanel(new BorderLayout(0, 6));
		north.setOpaque(false);
		north.add(sectionLabel("Item requirements (quest order overrides and step attachments)"), BorderLayout.NORTH);
		requirementTable = new JTable(requirementLibraryModel);
		wrapRequirementLibraryTable(requirementTable);
		JTextField reqSearch = newMakerSearchField("Filter by name or item id (any column, case-insensitive).");
		wireTableSearchField(reqSearch, requirementTable);
		north.add(reqSearch, BorderLayout.SOUTH);
		panel.add(north, BorderLayout.NORTH);
		JScrollPane reqScroll = new JScrollPane(requirementTable);
		reqScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		reqScroll.setBorder(new EmptyBorder(0, 0, 0, 0));
		panel.add(reqScroll, BorderLayout.CENTER);
		JButton addReqButton = new JButton("Add");
		JButton removeReqButton = new JButton("Remove");
		applyMakerToolbarStyle(addReqButton, removeReqButton);
		addReqButton.addActionListener(e ->
		{
			helperConstructManager.addEmptyItemRequirementFromUi();
			refresh();
		});
		removeReqButton.addActionListener(e ->
		{
			int r = selectedModelRow(requirementTable);
			if (r >= 0)
			{
				helperConstructManager.removeRequirementAt(r);
				refresh();
			}
		});
		JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
		actions.setOpaque(false);
		actions.add(addReqButton);
		actions.add(removeReqButton);
		panel.add(actions, BorderLayout.SOUTH);
		return panel;
	}

	/**
	 * Converts the selected step-library row; used by the Convert button and Ctrl/Cmd+N / Ctrl/Cmd+O.
	 *
	 * @return true if a row was converted successfully
	 */
	private boolean convertSelectedStepLibraryRowToKind(JTable table, ConstructStepKind fromKind, ConstructStepKind toKind)
	{
		if (fromKind == toKind)
		{
			return false;
		}
		int r = selectedModelRow(table);
		if (r < 0)
		{
			JOptionPane.showMessageDialog(this, "Select a step row first.", "Convert step",
				JOptionPane.INFORMATION_MESSAGE);
			return false;
		}
		if (!helperConstructManager.convertStepDefinitionKind(fromKind, r, toKind))
		{
			JOptionPane.showMessageDialog(this, "Could not convert that step.", "Convert step",
				JOptionPane.WARNING_MESSAGE);
			return false;
		}
		refresh();
		return true;
	}

	private static boolean isFocusWithinStepLibrarySearch(JTextField stepSearch)
	{
		Component fo = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
		return fo != null && (fo == stepSearch || SwingUtilities.isDescendingFrom(fo, stepSearch));
	}

	private void wireStepLibraryQuickConvertKeys(JPanel wrap, JTextField stepSearch, JTable table, ConstructStepKind stepKind)
	{
		int menuShortcutMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
		InputMap im = wrap.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		ActionMap am = wrap.getActionMap();
		String toNpc = "makerStepLibConvertNpc_" + stepKind.name();
		String toObj = "makerStepLibConvertObject_" + stepKind.name();
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_N, menuShortcutMask), toNpc);
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_O, menuShortcutMask), toObj);
		am.put(toNpc, new AbstractAction()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (isFocusWithinStepLibrarySearch(stepSearch) || stepKind == ConstructStepKind.NPC)
				{
					return;
				}
				if (table.isEditing())
				{
					TableCellEditor ce = table.getCellEditor();
					if (ce != null)
					{
						ce.stopCellEditing();
					}
				}
				convertSelectedStepLibraryRowToKind(table, stepKind, ConstructStepKind.NPC);
			}
		});
		am.put(toObj, new AbstractAction()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (isFocusWithinStepLibrarySearch(stepSearch) || stepKind == ConstructStepKind.OBJECT)
				{
					return;
				}
				if (table.isEditing())
				{
					TableCellEditor ce = table.getCellEditor();
					if (ce != null)
					{
						ce.stopCellEditing();
					}
				}
				convertSelectedStepLibraryRowToKind(table, stepKind, ConstructStepKind.OBJECT);
			}
		});
	}

	private JPanel wrapStepLibraryTableWithToolbar(JTable table, ConstructStepKind stepKind)
	{
		JPanel wrap = new JPanel(new BorderLayout(0, 6));
		wrap.setOpaque(false);
		JButton addStep = new JButton("Add step");
		JButton removeStep = new JButton("Remove");
		JComboBox<String> convertKindCombo = new JComboBox<>();
		convertKindCombo.addItem(null);
		if (stepKind != ConstructStepKind.NPC)
		{
			convertKindCombo.addItem("NPC");
		}
		if (stepKind != ConstructStepKind.OBJECT)
		{
			convertKindCombo.addItem("Object");
		}
		if (stepKind != ConstructStepKind.TEXT)
		{
			convertKindCombo.addItem("Generic");
		}
		convertKindCombo.setRenderer(new DefaultListCellRenderer()
		{
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
			{
				String label = value == null ? "Convert to…" : String.valueOf(value);
				return super.getListCellRendererComponent(list, label, index, isSelected, cellHasFocus);
			}
		});
		JButton convertStep = new JButton("Convert");
		applyMakerToolbarStyle(addStep, removeStep, convertStep);
		convertStep.setToolTipText("Convert the selected row to another step type (step id is preserved for quest order). "
			+ "Shortcuts: Ctrl/Cmd+N → NPC, Ctrl/Cmd+O → Object (disabled while the filter field above the table has focus).");
		addStep.addActionListener(e ->
		{
			helperConstructManager.addEmptyStepFromUi(stepKind);
			refresh();
		});
		removeStep.addActionListener(e ->
		{
			List<Integer> selected = selectedModelRowsDescending(table);
			if (!selected.isEmpty())
			{
				for (int r : selected)
				{
					helperConstructManager.removeStepAt(stepKind, r);
				}
				refresh();
			}
		});
		convertStep.addActionListener(e ->
		{
			Object sel = convertKindCombo.getSelectedItem();
			if (sel == null)
			{
				JOptionPane.showMessageDialog(this, "Choose a target type in the Convert dropdown.", "Convert step",
					JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			ConstructStepKind toKind = convertStepKindLabelToEnum(String.valueOf(sel));
			if (toKind == null)
			{
				return;
			}
			if (convertSelectedStepLibraryRowToKind(table, stepKind, toKind))
			{
				convertKindCombo.setSelectedIndex(0);
			}
		});
		JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
		actions.setOpaque(false);
		actions.add(addStep);
		actions.add(removeStep);
		actions.add(Box.createHorizontalStrut(8));
		actions.add(convertKindCombo);
		actions.add(convertStep);
		String searchHint;
		switch (stepKind)
		{
			case NPC:
				searchHint = "Filter NPC steps by var name, ids, world point, reqs, or instruction.";
				break;
			case OBJECT:
				searchHint = "Filter object steps by var name, ids, world point, reqs, or instruction.";
				break;
			default:
				searchHint = "Filter generic steps by var name, world point, reqs, or instruction.";
				break;
		}
		JTextField stepSearch = newMakerSearchField(searchHint);
		wireTableSearchField(stepSearch, table);
		stepSearch.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				table.clearSelection();
				helperConstructManager.clearConstructMenuSelectedStep();
				helperConstructManager.clearSelectedRenderedStep();
			}
		});
		JPanel centerStack = new JPanel(new BorderLayout(0, 4));
		centerStack.setOpaque(false);
		centerStack.add(stepSearch, BorderLayout.NORTH);
		centerStack.add(wrapStepLibraryTable(table, stepKind), BorderLayout.CENTER);
		MouseAdapter clearLibrarySelection = new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				table.clearSelection();
				helperConstructManager.clearConstructMenuSelectedStep();
				helperConstructManager.clearSelectedRenderedStep();
			}
		};
		wrap.addMouseListener(clearLibrarySelection);
		centerStack.addMouseListener(clearLibrarySelection);
		wrap.add(centerStack, BorderLayout.CENTER);
		wrap.add(actions, BorderLayout.SOUTH);
		wireStepLibraryQuickConvertKeys(wrap, stepSearch, table, stepKind);
		return wrap;
	}

	private static ConstructStepKind convertStepKindLabelToEnum(String label)
	{
		switch (label)
		{
			case "NPC":
				return ConstructStepKind.NPC;
			case "Object":
				return ConstructStepKind.OBJECT;
			case "Generic":
				return ConstructStepKind.TEXT;
			default:
				return null;
		}
	}

	private static int stepLibIdColumn(ConstructStepKind k)
	{
		return k == ConstructStepKind.TEXT ? -1 : 1;
	}

	private static int stepLibWorldColumn(ConstructStepKind k)
	{
		return k == ConstructStepKind.TEXT ? 1 : 2;
	}

	private static int stepLibAttachmentsColumn(ConstructStepKind k)
	{
		return k == ConstructStepKind.TEXT ? 2 : 3;
	}

	private static int stepLibInstructionColumn(ConstructStepKind k)
	{
		return k == ConstructStepKind.TEXT ? 3 : 4;
	}

	private JScrollPane wrapStepLibraryTable(JTable table, ConstructStepKind stepKind)
	{
		styleStepLibraryTable(table, stepKind);
		table.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(new JTextField()));
		int worldCol = stepLibWorldColumn(stepKind);
		int instrCol = stepLibInstructionColumn(stepKind);
		table.getColumnModel().getColumn(worldCol).setCellEditor(new DefaultCellEditor(new JTextField()));
		table.getColumnModel().getColumn(instrCol).setCellEditor(new DefaultCellEditor(new JTextField()));
		int idCol = stepLibIdColumn(stepKind);
		if (idCol >= 0)
		{
			table.getColumnModel().getColumn(idCol).setCellEditor(new DefaultCellEditor(new JTextField()));
		}
		int attachCol = stepLibAttachmentsColumn(stepKind);
		table.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				maybeShowStepLibraryContextMenu(table, stepKind, e);
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				maybeShowStepLibraryContextMenu(table, stepKind, e);
			}

			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (e.isPopupTrigger() || SwingUtilities.isRightMouseButton(e))
				{
					return;
				}
				int row = table.rowAtPoint(e.getPoint());
				int col = table.columnAtPoint(e.getPoint());
				if (row < 0 || col < 0)
				{
					table.clearSelection();
					helperConstructManager.clearConstructMenuSelectedStep();
					helperConstructManager.clearSelectedRenderedStep();
					return;
				}
				if (col == attachCol)
				{
					openStepRequirementsEditor(stepKind, table.convertRowIndexToModel(row));
				}
			}
		});
		JScrollPane sp = new JScrollPane(table);
		sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sp.setBorder(new EmptyBorder(0, 0, 0, 0));
		table.getTableHeader().addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				table.clearSelection();
				helperConstructManager.clearConstructMenuSelectedStep();
				helperConstructManager.clearSelectedRenderedStep();
			}
		});
		sp.getViewport().addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				if (table.rowAtPoint(e.getPoint()) < 0 || table.columnAtPoint(e.getPoint()) < 0)
				{
					table.clearSelection();
					helperConstructManager.clearConstructMenuSelectedStep();
					helperConstructManager.clearSelectedRenderedStep();
				}
			}
		});
		return sp;
	}

	private void openStepRequirementsEditor(ConstructStepKind kind, int row)
	{
		List<HelperConstructManager.StepAttachmentEdit> initial = new ArrayList<>(helperConstructManager.getStepAttachmentsAt(kind, row));

		DefaultListModel<HelperConstructManager.StepAttachmentEdit> model = new DefaultListModel<>();
		for (HelperConstructManager.StepAttachmentEdit e : initial)
		{
			HelperConstructManager.StepAttachmentEdit c = HelperConstructManager.StepAttachmentEdit.copyOf(e);
			if (c != null)
			{
				model.addElement(c);
			}
		}

		JList<HelperConstructManager.StepAttachmentEdit> list = new JList<>(model);
		list.setVisibleRowCount(8);
		list.setCellRenderer(new DefaultListCellRenderer()
		{
			@Override
			public Component getListCellRendererComponent(JList<?> jList, Object value, int index, boolean isSelected, boolean cellHasFocus)
			{
				HelperConstructManager.StepAttachmentEdit e = (HelperConstructManager.StepAttachmentEdit) value;
				String text = helperConstructManager.summarizeStepAttachmentEdit(e);
				return super.getListCellRendererComponent(jList, text, index, isSelected, cellHasFocus);
			}
		});

		JCheckBox highlightCb = new JCheckBox("Highlight (item attachments only)");
		highlightCb.setOpaque(false);
		highlightCb.setForeground(Color.WHITE);
		JCheckBox equippedCb = new JCheckBox("Equipped");
		equippedCb.setOpaque(false);
		equippedCb.setForeground(Color.WHITE);
		JLabel qtyLabel = new JLabel("Quantity (items):");
		qtyLabel.setForeground(Color.WHITE);
		JSpinner qtySpinner = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
		Runnable syncItemAttachmentOptionsFromSelection = () ->
		{
			HelperConstructManager.StepAttachmentEdit sel = list.getSelectedValue();
			boolean item = sel != null && "ITEM".equalsIgnoreCase(sel.getKind());
			highlightCb.setEnabled(item);
			equippedCb.setEnabled(item);
			qtyLabel.setEnabled(item);
			qtySpinner.setEnabled(item);
			if (item)
			{
				highlightCb.setSelected(sel.isItemHighlighted());
				equippedCb.setSelected(sel.isItemMustBeEquipped());
				qtySpinner.setValue(Math.max(1, sel.getItemQuantity()));
			}
			else
			{
				highlightCb.setSelected(false);
				equippedCb.setSelected(false);
				qtySpinner.setValue(1);
			}
		};
		list.addListSelectionListener(e ->
		{
			if (!e.getValueIsAdjusting())
			{
				syncItemAttachmentOptionsFromSelection.run();
			}
		});
		highlightCb.addActionListener(ev ->
		{
			HelperConstructManager.StepAttachmentEdit sel = list.getSelectedValue();
			if (sel != null && "ITEM".equalsIgnoreCase(sel.getKind()))
			{
				sel.setItemHighlighted(highlightCb.isSelected());
				list.repaint();
			}
		});
		equippedCb.addActionListener(ev ->
		{
			HelperConstructManager.StepAttachmentEdit sel = list.getSelectedValue();
			if (sel != null && "ITEM".equalsIgnoreCase(sel.getKind()))
			{
				sel.setItemMustBeEquipped(equippedCb.isSelected());
				list.repaint();
			}
		});
		qtySpinner.addChangeListener(e ->
		{
			HelperConstructManager.StepAttachmentEdit sel = list.getSelectedValue();
			if (sel != null && "ITEM".equalsIgnoreCase(sel.getKind()))
			{
				int q = ((Number) qtySpinner.getValue()).intValue();
				sel.setItemQuantity(Math.max(q, 1));
				list.repaint();
			}
		});

		JPanel toolbar = getToolbar(kind, row, model, list, syncItemAttachmentOptionsFromSelection);

		JLabel hint = new JLabel("<html>Choose from captured item requirements and varbit routing rows (same list as the Varbit reqs tab).<br>Preview and generated code use <code>QuestStep.addRequirement</code> where supported.");
		hint.setForeground(Color.GRAY);

		JPanel itemAttachOpts = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
		itemAttachOpts.setOpaque(false);
		itemAttachOpts.add(highlightCb);
		itemAttachOpts.add(equippedCb);
		itemAttachOpts.add(qtyLabel);
		itemAttachOpts.add(qtySpinner);

		JPanel south = new JPanel(new BorderLayout(0, 6));
		south.setOpaque(false);
		south.add(itemAttachOpts, BorderLayout.NORTH);
		south.add(hint, BorderLayout.CENTER);

		JPanel panel = new JPanel(new BorderLayout(8, 8));
		panel.setOpaque(false);
		panel.add(toolbar, BorderLayout.NORTH);
		panel.add(new JScrollPane(list), BorderLayout.CENTER);
		panel.add(south, BorderLayout.SOUTH);
		panel.setPreferredSize(new Dimension(500, 420));

		if (!model.isEmpty())
		{
			list.setSelectedIndex(0);
			syncItemAttachmentOptionsFromSelection.run();
		}

		Window owner = SwingUtilities.getWindowAncestor(this);
		JDialog dialog = new JDialog(owner, "Step attachments", Dialog.ModalityType.MODELESS);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		JPanel content = new JPanel(new BorderLayout(0, 8));
		content.setBorder(new EmptyBorder(10, 10, 10, 10));
		content.add(panel, BorderLayout.CENTER);
		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(e -> dialog.dispose());
		JButton save = new JButton("Save");
		save.addActionListener(e ->
		{
			List<HelperConstructManager.StepAttachmentEdit> out = new ArrayList<>();
			for (int i = 0; i < model.size(); i++)
			{
				out.add(model.get(i));
			}

			boolean applied = helperConstructManager.applyStepAttachmentsAt(kind, row, out);
			if (!applied)
			{
				JOptionPane.showMessageDialog(dialog,
					"Could not save requirements (check varbit id/value and Operation name, e.g. EQUAL).",
					"Step requirements",
					JOptionPane.WARNING_MESSAGE);
				return;
			}
			refresh();
			dialog.dispose();
		});
		buttons.add(cancel);
		buttons.add(save);
		content.add(buttons, BorderLayout.SOUTH);
		dialog.setContentPane(content);
		dialog.pack();
		dialog.setLocationRelativeTo(owner);
		dialog.setVisible(true);
	}

	private @NotNull JPanel getToolbar(
		ConstructStepKind kind,
		int row,
		DefaultListModel<HelperConstructManager.StepAttachmentEdit> model,
		JList<HelperConstructManager.StepAttachmentEdit> list,
		Runnable syncItemAttachmentOptionsFromSelection)
	{
		JButton addPickButton = new JButton("Add…");
		addPickButton.addActionListener(ev -> appendRequirementPickToAttachmentModel(model));
		JButton addWidgetButton = new JButton("Widget…");
		addWidgetButton.addActionListener(ev -> appendWidgetAttachmentToModel(kind, row, model, addWidgetButton));

		JButton removeButton = new JButton("Remove selected");
		removeButton.addActionListener(ev ->
		{
			int i = list.getSelectedIndex();
			if (i >= 0)
			{
				model.remove(i);
				syncItemAttachmentOptionsFromSelection.run();
			}
		});

		JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
		toolbar.setOpaque(false);
		toolbar.add(addPickButton);
		toolbar.add(addWidgetButton);
		toolbar.add(removeButton);
		return toolbar;
	}

	private enum WidgetAttachmentMode
	{
		GROUP_CHILD,
		ITEM_ID,
		DIALOG_TEXT
	}

	private void appendWidgetAttachmentToModel(
		ConstructStepKind kind,
		int row,
		DefaultListModel<HelperConstructManager.StepAttachmentEdit> model,
		Component ownerComponent)
	{
		HelperConstructManager.StepAttachmentEdit initial = null;
		List<HelperConstructManager.StepAttachmentEdit> existing = helperConstructManager.getStepAttachmentsAt(kind, row);
		for (HelperConstructManager.StepAttachmentEdit e : existing)
		{
			if (e != null && HelperConstructModels.StepAttachmentKind.WIDGET.name().equalsIgnoreCase(e.getKind()))
			{
				initial = e;
				break;
			}
		}

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		JRadioButton byGroupChild = new JRadioButton("Defined by group/child");
		JRadioButton byItemId = new JRadioButton("Defined by itemID");
		JRadioButton byDialogText = new JRadioButton("Defined by dialog text");
		byGroupChild.setOpaque(false);
		byItemId.setOpaque(false);
		byDialogText.setOpaque(false);
		byGroupChild.setForeground(Color.WHITE);
		byItemId.setForeground(Color.WHITE);
		byDialogText.setForeground(Color.WHITE);
		ButtonGroup modeGroup = new ButtonGroup();
		modeGroup.add(byGroupChild);
		modeGroup.add(byItemId);
		modeGroup.add(byDialogText);

		JSpinner groupSp = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
		JSpinner childSp = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
		JSpinner childChildSp = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
		JLabel pickerHint = new JLabel("<html><small>Use the in-game picker next to the compass to select widget IDs while this window is open.</small></html>");
		pickerHint.setForeground(Color.LIGHT_GRAY);
		JCheckBox useChildChildCb = new JCheckBox("Use childChild");
		useChildChildCb.setOpaque(false);
		useChildChildCb.setForeground(Color.WHITE);

		JSpinner itemIdSp = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
		JCheckBox checkChildrenItemCb = new JCheckBox("Check children");
		checkChildrenItemCb.setOpaque(false);
		checkChildrenItemCb.setForeground(Color.WHITE);

		JTextField dialogTextField = new JTextField(24);

		if (initial != null)
		{
			if (initial.getWidgetGroupId() != null)
			{
				groupSp.setValue(initial.getWidgetGroupId());
			}
			if (initial.getWidgetChildId() != null)
			{
				childSp.setValue(initial.getWidgetChildId());
			}
			if (initial.getWidgetChildChildId() != null)
			{
				useChildChildCb.setSelected(true);
				childChildSp.setValue(initial.getWidgetChildChildId());
			}
			if (initial.getWidgetItemId() != null)
			{
				byItemId.setSelected(true);
				itemIdSp.setValue(initial.getWidgetItemId());
				checkChildrenItemCb.setSelected(initial.isWidgetCheckChildren());
			}
			else if (initial.getWidgetDialogText() != null && !initial.getWidgetDialogText().isBlank())
			{
				byDialogText.setSelected(true);
				dialogTextField.setText(initial.getWidgetDialogText());
			}
			else
			{
				byGroupChild.setSelected(true);
			}
		}
		else
		{
			byGroupChild.setSelected(true);
		}

		JPanel modesPanel = new JPanel(new GridLayout(0, 1));
		modesPanel.setOpaque(false);
		modesPanel.add(byGroupChild);
		modesPanel.add(byItemId);
		modesPanel.add(byDialogText);
		panel.add(modesPanel);
		panel.add(Box.createVerticalStrut(8));
		panel.add(labeled("Group", groupSp));
		panel.add(labeled("Child", childSp));
		panel.add(pickerHint);
		panel.add(useChildChildCb);
		panel.add(labeled("ChildChild", childChildSp));
		panel.add(Box.createVerticalStrut(6));
		panel.add(labeled("Item ID", itemIdSp));
		panel.add(checkChildrenItemCb);
		panel.add(Box.createVerticalStrut(6));
		panel.add(labeled("Dialog text", dialogTextField));

		Runnable syncModeControls = () ->
		{
			WidgetAttachmentMode mode = byItemId.isSelected()
				? WidgetAttachmentMode.ITEM_ID
				: (byDialogText.isSelected() ? WidgetAttachmentMode.DIALOG_TEXT : WidgetAttachmentMode.GROUP_CHILD);
			boolean groupMode = mode == WidgetAttachmentMode.GROUP_CHILD;
			boolean dialogMode = mode == WidgetAttachmentMode.DIALOG_TEXT;
			if (dialogMode)
			{
				groupSp.setValue(DialogChoiceStep.DIALOG_WIDGET_GROUP_ID);
				childSp.setValue(DialogChoiceStep.DIALOG_WIDGET_CHILD_ID);
			}
			groupSp.setEnabled(!dialogMode);
			childSp.setEnabled(!dialogMode);
			childChildSp.setEnabled(groupMode && useChildChildCb.isSelected());
			useChildChildCb.setEnabled(groupMode);
			itemIdSp.setEnabled(mode == WidgetAttachmentMode.ITEM_ID);
			checkChildrenItemCb.setEnabled(mode == WidgetAttachmentMode.ITEM_ID);
			dialogTextField.setEnabled(mode == WidgetAttachmentMode.DIALOG_TEXT);
		};
		useChildChildCb.addActionListener(e -> syncModeControls.run());
		byGroupChild.addActionListener(e -> syncModeControls.run());
		byItemId.addActionListener(e -> syncModeControls.run());
		byDialogText.addActionListener(e -> syncModeControls.run());
		syncModeControls.run();

		Window owner = ownerComponent == null ? null : SwingUtilities.getWindowAncestor(ownerComponent);
		JDialog dialog = new JDialog(owner, "Add widget highlight", Dialog.ModalityType.MODELESS);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		if (owner != null)
		{
			owner.setEnabled(false);
		}
		dialog.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosed(WindowEvent e)
			{
				helperConstructManager.closeWidgetInspectorPick();
				if (owner != null)
				{
					owner.setEnabled(true);
					owner.toFront();
					owner.requestFocus();
				}
			}
		});
		JPanel content = new JPanel(new BorderLayout(0, 8));
		content.setBorder(new EmptyBorder(10, 10, 10, 10));
		content.add(panel, BorderLayout.CENTER);
		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(e -> dialog.dispose());
		JButton addBtn = new JButton("Add");
		addBtn.addActionListener(e ->
		{
			int group = ((Number) groupSp.getValue()).intValue();
			int child = ((Number) childSp.getValue()).intValue();
			HelperConstructManager.StepAttachmentEdit add;
			if (byItemId.isSelected())
			{
				int itemId = ((Number) itemIdSp.getValue()).intValue();
				add = HelperConstructManager.StepAttachmentEdit.widgetByItemId(group, child, itemId, checkChildrenItemCb.isSelected());
			}
			else if (byDialogText.isSelected())
			{
				String text = dialogTextField.getText() == null ? "" : dialogTextField.getText().trim();
				if (text.isBlank())
				{
					JOptionPane.showMessageDialog(dialog, "Dialog text is required for text mode.", "Widget highlight", JOptionPane.WARNING_MESSAGE);
					return;
				}
				add = HelperConstructManager.StepAttachmentEdit.widgetByDialogText(
					DialogChoiceStep.DIALOG_WIDGET_GROUP_ID,
					DialogChoiceStep.DIALOG_WIDGET_CHILD_ID,
					text,
					true);
			}
			else
			{
				Integer childChild = useChildChildCb.isSelected() ? ((Number) childChildSp.getValue()).intValue() : null;
				add = HelperConstructManager.StepAttachmentEdit.widgetByGroupChild(group, child, childChild);
			}
			model.addElement(add);
			dialog.dispose();
		});
		buttons.add(cancel);
		buttons.add(addBtn);
		content.add(buttons, BorderLayout.SOUTH);
		dialog.setContentPane(content);
		dialog.pack();
		dialog.setLocationRelativeTo(owner);
		helperConstructManager.openWidgetInspectorForPick(pick ->
		{
			groupSp.setValue(pick.getGroupId());
			childSp.setValue(pick.getChildId());
			if (byDialogText.isSelected() && pick.getText() != null && !pick.getText().isBlank())
			{
				dialogTextField.setText(pick.getText());
			}
		});
		dialog.setVisible(true);
	}

	private HelperConstructManager.WidgetPickerRow showWidgetPickerDialog()
	{
		List<HelperConstructManager.WidgetPickerRow> picks = helperConstructManager.getVisibleWidgetPickerRows();
		if (picks.isEmpty())
		{
			JOptionPane.showMessageDialog(this,
				"No visible widgets found. Open the relevant interface first, or enter group/child manually.",
				"Widget select",
				JOptionPane.INFORMATION_MESSAGE);
			return null;
		}
		List<HelperConstructManager.WidgetPickerRow> allPicks = new ArrayList<>(picks);
		DefaultListModel<HelperConstructManager.WidgetPickerRow> listModel = new DefaultListModel<>();
		JTextField pickSearch = newMakerSearchField("Filter by group,child or label.");
		Runnable refillPickList = () ->
		{
			String q = pickSearch.getText().trim().toLowerCase(Locale.ROOT);
			listModel.clear();
			for (HelperConstructManager.WidgetPickerRow o : allPicks)
			{
				String lab = o.getLabel() == null ? "" : o.getLabel();
				if (q.isEmpty() || lab.toLowerCase(Locale.ROOT).contains(q))
				{
					listModel.addElement(o);
				}
			}
		};
		pickSearch.getDocument().addDocumentListener(new DocumentListener()
		{
			@Override
			public void insertUpdate(DocumentEvent e)
			{
				refillPickList.run();
			}

			@Override
			public void removeUpdate(DocumentEvent e)
			{
				refillPickList.run();
			}

			@Override
			public void changedUpdate(DocumentEvent e)
			{
				refillPickList.run();
			}
		});
		refillPickList.run();
		JList<HelperConstructManager.WidgetPickerRow> list = new JList<>(listModel);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setVisibleRowCount(12);
		list.setCellRenderer(new DefaultListCellRenderer()
		{
			@Override
			public Component getListCellRendererComponent(JList<?> jList, Object value, int index, boolean isSelected, boolean cellHasFocus)
			{
				HelperConstructManager.WidgetPickerRow o = (HelperConstructManager.WidgetPickerRow) value;
				return super.getListCellRendererComponent(jList, o.getLabel(), index, isSelected, cellHasFocus);
			}
		});
		JPanel p = new JPanel(new BorderLayout(0, 6));
		p.setOpaque(false);
		p.add(pickSearch, BorderLayout.NORTH);
		p.add(new JScrollPane(list), BorderLayout.CENTER);
		p.setPreferredSize(new Dimension(560, 420));
		if (JOptionPane.showConfirmDialog(this, p, "Select visible widget", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION)
		{
			return null;
		}
		return list.getSelectedValue();
	}

	private static JPanel labeled(String title, JComponent field)
	{
		JPanel row = new JPanel(new BorderLayout(6, 0));
		row.setOpaque(false);
		JLabel label = new JLabel(title + ":");
		label.setForeground(Color.WHITE);
		row.add(label, BorderLayout.WEST);
		row.add(field, BorderLayout.CENTER);
		return row;
	}

	private void appendRequirementPickToAttachmentModel(DefaultListModel<HelperConstructManager.StepAttachmentEdit> model)
	{
		List<HelperConstructManager.StepAttachmentPickOption> picks = helperConstructManager.getStepAttachmentPickOptions();
		if (picks.isEmpty())
		{
			JOptionPane.showMessageDialog(this,
				"Add item requirements (Item reqs tab) or quest-order rows that use varbit routing first.",
				"Add attachment",
				JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		List<HelperConstructManager.StepAttachmentPickOption> allPicks = new ArrayList<>(picks);
		DefaultListModel<HelperConstructManager.StepAttachmentPickOption> listModel = new DefaultListModel<>();
		JTextField pickSearch = newMakerSearchField("Filter by label (case-insensitive).");
		Runnable refillPickList = () ->
		{
			String q = pickSearch.getText().trim().toLowerCase(Locale.ROOT);
			listModel.clear();
			for (HelperConstructManager.StepAttachmentPickOption o : allPicks)
			{
				String lab = o.getLabel() == null ? "" : o.getLabel();
				if (q.isEmpty() || lab.toLowerCase(Locale.ROOT).contains(q))
				{
					listModel.addElement(o);
				}
			}
		};
		pickSearch.getDocument().addDocumentListener(new DocumentListener()
		{
			@Override
			public void insertUpdate(DocumentEvent e)
			{
				refillPickList.run();
			}

			@Override
			public void removeUpdate(DocumentEvent e)
			{
				refillPickList.run();
			}

			@Override
			public void changedUpdate(DocumentEvent e)
			{
				refillPickList.run();
			}
		});
		refillPickList.run();

		JList<HelperConstructManager.StepAttachmentPickOption> list = new JList<>(listModel);
		list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		list.setVisibleRowCount(10);
		list.setCellRenderer(new DefaultListCellRenderer()
		{
			@Override
			public Component getListCellRendererComponent(JList<?> jList, Object value, int index, boolean isSelected, boolean cellHasFocus)
			{
				HelperConstructManager.StepAttachmentPickOption o = (HelperConstructManager.StepAttachmentPickOption) value;
				return super.getListCellRendererComponent(jList, o.getLabel(), index, isSelected, cellHasFocus);
			}
		});
		JPanel p = new JPanel(new BorderLayout(0, 6));
		p.setOpaque(false);
		JPanel north = new JPanel(new BorderLayout(0, 4));
		north.setOpaque(false);
		north.add(new JLabel("Select one or more (Ctrl/Cmd+click):", SwingConstants.LEFT), BorderLayout.NORTH);
		north.add(pickSearch, BorderLayout.SOUTH);
		p.add(north, BorderLayout.NORTH);
		p.add(new JScrollPane(list), BorderLayout.CENTER);
		p.setPreferredSize(new Dimension(520, 360));
		if (JOptionPane.showConfirmDialog(this, p, "Add attachment", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION)
		{
			return;
		}
		for (HelperConstructManager.StepAttachmentPickOption o : list.getSelectedValuesList())
		{
			if (o == null || o.getEdit() == null)
			{
				continue;
			}
			HelperConstructManager.StepAttachmentEdit add = HelperConstructManager.StepAttachmentEdit.copyOf(o.getEdit());
			if (add != null)
			{
				model.addElement(add);
			}
		}
	}

	private void wrapRequirementLibraryTable(JTable table)
	{
		styleRequirementLibraryTable(table);
		table.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(new JTextField()));
		table.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(new JTextField()));
	}

	private void styleStepLibraryTable(JTable table, ConstructStepKind stepKind)
	{
		int attachCol = stepLibAttachmentsColumn(stepKind);
		table.setFillsViewportHeight(true);
		table.setRowHeight(24);
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setShowGrid(false);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer()
		{
			@Override
			public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int column)
			{
				Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
				if (!isSelected)
				{
					int modelRow = t.convertRowIndexToModel(row);
					boolean leagueStep = modelRow >= 0
						&& t.getModel() instanceof StepLibraryTableModel
						&& ((StepLibraryTableModel) t.getModel()).isLeagueRow(modelRow);
					c.setBackground(leagueStep ? LEAGUE_STEP_BACKGROUND : ColorScheme.DARK_GRAY_COLOR);
					c.setForeground(column == attachCol ? new Color(100, 180, 255) : Color.WHITE);
				}
				return c;
			}
		});
		int n = table.getColumnModel().getColumnCount();
		if (n >= 4)
		{
			table.getColumnModel().getColumn(0).setPreferredWidth(160);
			if (n >= 5)
			{
				table.getColumnModel().getColumn(1).setPreferredWidth(72);
				table.getColumnModel().getColumn(2).setPreferredWidth(110);
				table.getColumnModel().getColumn(3).setPreferredWidth(200);
				table.getColumnModel().getColumn(4).setPreferredWidth(220);
			}
			else
			{
				table.getColumnModel().getColumn(1).setPreferredWidth(110);
				table.getColumnModel().getColumn(2).setPreferredWidth(220);
				table.getColumnModel().getColumn(3).setPreferredWidth(260);
			}
		}
	}

	private void styleRequirementLibraryTable(JTable table)
	{
		table.setFillsViewportHeight(true);
		table.setRowHeight(24);
		table.setShowGrid(false);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer()
		{
			@Override
			public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int column)
			{
				Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
				if (!isSelected)
				{
					c.setBackground(ColorScheme.DARK_GRAY_COLOR);
					c.setForeground(Color.WHITE);
				}
				return c;
			}
		});
		if (table.getColumnModel().getColumnCount() >= 2)
		{
			table.getColumnModel().getColumn(0).setPreferredWidth(320);
			table.getColumnModel().getColumn(1).setPreferredWidth(120);
		}
	}

	private void styleVarbitRoutingTable(JTable table)
	{
		table.setFillsViewportHeight(true);
		table.setShowGrid(false);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer()
		{
			@Override
			public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int column)
			{
				Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
				if (!isSelected)
				{
					c.setBackground(ColorScheme.DARK_GRAY_COLOR);
					c.setForeground(Color.WHITE);
				}
				return c;
			}
		});
		if (table.getColumnModel().getColumnCount() >= 5)
		{
			table.getColumnModel().getColumn(0).setPreferredWidth(320);
			table.getColumnModel().getColumn(1).setPreferredWidth(88);
			table.getColumnModel().getColumn(2).setPreferredWidth(96);
			table.getColumnModel().getColumn(3).setPreferredWidth(100);
			table.getColumnModel().getColumn(4).setPreferredWidth(220);
		}
	}

	private void styleZoneRoutingTable(JTable table)
	{
		table.setFillsViewportHeight(true);
		table.setShowGrid(false);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer()
		{
			@Override
			public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int column)
			{
				Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
				if (!isSelected)
				{
					c.setBackground(ColorScheme.DARK_GRAY_COLOR);
					c.setForeground(Color.WHITE);
				}
				return c;
			}
		});
		if (table.getColumnModel().getColumnCount() >= 4)
		{
			table.getColumnModel().getColumn(0).setPreferredWidth(200);
			table.getColumnModel().getColumn(1).setPreferredWidth(160);
			table.getColumnModel().getColumn(2).setPreferredWidth(160);
			table.getColumnModel().getColumn(3).setPreferredWidth(220);
		}
	}

	private final class LibraryTableModel extends AbstractTableModel
	{
		private final String[] columns = {"Name", "ID"};
		private List<String> displayNames = new ArrayList<>();
		private List<String> rawIds = new ArrayList<>();

		void setRows(List<String> names, List<String> ids)
		{
			displayNames = new ArrayList<>(names);
			rawIds = new ArrayList<>(ids);
			int n = displayNames.size();
			while (rawIds.size() < n)
			{
				rawIds.add("");
			}
			if (rawIds.size() > n)
			{
				rawIds.subList(n, rawIds.size()).clear();
			}
			fireTableDataChanged();
		}

		@Override
		public int getRowCount()
		{
			return displayNames.size();
		}

		@Override
		public int getColumnCount()
		{
			return columns.length;
		}

		@Override
		public String getColumnName(int column)
		{
			return columns[column];
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex)
		{
			if (columnIndex == 0)
			{
				return displayNames.get(rowIndex);
			}
			return rowIndex < rawIds.size() ? rawIds.get(rowIndex) : "";
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex)
		{
			return columnIndex == 0 || columnIndex == 1;
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex)
		{
			if (rowIndex < 0 || rowIndex >= displayNames.size())
			{
				return;
			}
			String text = aValue == null ? "" : String.valueOf(aValue);
			boolean ok;
			if (columnIndex == 0)
			{
				ok = helperConstructManager.updateRequirementDisplayNameAt(rowIndex, text);
				if (ok)
				{
					displayNames.set(rowIndex, text);
				}
			}
			else
			{
				ok = helperConstructManager.updateRequirementRawIdAt(rowIndex, text);
				if (ok)
				{
					rawIds.set(rowIndex, helperConstructManager.getRequirementRawIdStrings().get(rowIndex));
				}
			}
			if (ok)
			{
				fireTableCellUpdated(rowIndex, columnIndex);
			}
		}
	}

	private final class StepLibraryTableModel extends AbstractTableModel
	{
		private final ConstructStepKind kind;
		private final String[] columns;
		private List<String> summaries = new ArrayList<>();
		private List<String> rawIdTexts = new ArrayList<>();
		private List<String> worldPoints = new ArrayList<>();
		private List<String> requirementDisplays = new ArrayList<>();
		private List<String> instructions = new ArrayList<>();
		private List<Boolean> leagueFlags = new ArrayList<>();

		private StepLibraryTableModel(ConstructStepKind kind)
		{
			this.kind = kind;
			this.columns = kind == ConstructStepKind.TEXT
				? new String[]{"Var name", "World (x, y, plane)", "Extras", "Text"}
				: new String[]{"Var name", "Id to use", "World (x, y, plane)", "Extras", "Text"};
		}

		void setRows(
			List<String> updatedSummaries,
			List<String> updatedRawIdTexts,
			List<String> updatedWorldPoints,
			List<String> updatedRequirementDisplays,
			List<String> updatedInstructions)
		{
			summaries = new ArrayList<>(updatedSummaries);
			rawIdTexts = new ArrayList<>(updatedRawIdTexts);
			worldPoints = new ArrayList<>(updatedWorldPoints);
			requirementDisplays = new ArrayList<>(updatedRequirementDisplays);
			instructions = new ArrayList<>(updatedInstructions);
			leagueFlags = new ArrayList<>(helperConstructManager.getStepLeagueFlags(kind));
			int n = summaries.size();
			padToSize(rawIdTexts, n);
			padToSize(worldPoints, n);
			padToSize(requirementDisplays, n);
			padToSize(instructions, n);
			padToSize(leagueFlags, n, Boolean.FALSE);
			fireTableDataChanged();
		}

		private void padToSize(List<String> list, int n)
		{
			while (list.size() < n)
			{
				list.add("");
			}
			if (list.size() > n)
			{
				list.subList(n, list.size()).clear();
			}
		}

		private <T> void padToSize(List<T> list, int n, T defaultValue)
		{
			while (list.size() < n)
			{
				list.add(defaultValue);
			}
			if (list.size() > n)
			{
				list.subList(n, list.size()).clear();
			}
		}

		boolean isLeagueRow(int rowIndex)
		{
			return rowIndex >= 0 && rowIndex < leagueFlags.size() && Boolean.TRUE.equals(leagueFlags.get(rowIndex));
		}

		@Override
		public int getRowCount()
		{
			return summaries.size();
		}

		@Override
		public int getColumnCount()
		{
			return columns.length;
		}

		@Override
		public String getColumnName(int column)
		{
			return columns[column];
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex)
		{
			if (kind == ConstructStepKind.TEXT)
			{
				switch (columnIndex)
				{
					case 0:
						return summaries.get(rowIndex);
					case 1:
						return rowIndex < worldPoints.size() ? worldPoints.get(rowIndex) : "";
					case 2:
						return rowIndex < requirementDisplays.size() ? requirementDisplays.get(rowIndex) : "";
					case 3:
					default:
						return rowIndex < instructions.size() ? instructions.get(rowIndex) : "";
				}
			}
			switch (columnIndex)
			{
				case 0:
					return summaries.get(rowIndex);
				case 1:
					return rowIndex < rawIdTexts.size() ? rawIdTexts.get(rowIndex) : "";
				case 2:
					return rowIndex < worldPoints.size() ? worldPoints.get(rowIndex) : "";
				case 3:
					return rowIndex < requirementDisplays.size() ? requirementDisplays.get(rowIndex) : "";
				case 4:
				default:
					return rowIndex < instructions.size() ? instructions.get(rowIndex) : "";
			}
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex)
		{
			if (kind == ConstructStepKind.TEXT)
			{
				return columnIndex == 0 || columnIndex == 1 || columnIndex == 3;
			}
			return columnIndex == 0 || columnIndex == 1 || columnIndex == 2 || columnIndex == 4;
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex)
		{
			if (rowIndex < 0 || rowIndex >= summaries.size() || !isCellEditable(rowIndex, columnIndex))
			{
				return;
			}
			String text = aValue == null ? "" : String.valueOf(aValue);
			boolean ok = applyStepLibraryEdit(kind, rowIndex, columnIndex, text);
			if (!ok)
			{
				return;
			}
			if (kind == ConstructStepKind.TEXT)
			{
				if (columnIndex == 0)
				{
					summaries.set(rowIndex, text);
				}
				else if (columnIndex == 1)
				{
					worldPoints.set(rowIndex, text);
				}
				else
				{
					instructions.set(rowIndex, text);
				}
			}
			else if (columnIndex == 0)
			{
				summaries.set(rowIndex, text);
			}
			else if (columnIndex == 1)
			{
				rawIdTexts.set(rowIndex, helperConstructManager.getStepRawIdTexts(kind).get(rowIndex));
			}
			else if (columnIndex == 2)
			{
				worldPoints.set(rowIndex, text);
			}
			else
			{
				instructions.set(rowIndex, text);
			}
			fireTableCellUpdated(rowIndex, columnIndex);
		}
	}

	private boolean applyStepLibraryEdit(ConstructStepKind kind, int row, int column, String text)
	{
		if (kind == ConstructStepKind.TEXT)
		{
			switch (column)
			{
				case 0:
					return helperConstructManager.updateStepVarNameAt(kind, row, text);
				case 1:
					return helperConstructManager.updateStepWorldPointAt(kind, row, text);
				case 3:
					return helperConstructManager.updateStepInstructionAt(kind, row, text);
				default:
					return false;
			}
		}
		switch (column)
		{
			case 0:
				return helperConstructManager.updateStepVarNameAt(kind, row, text);
			case 1:
				return helperConstructManager.updateStepRawIdAt(kind, row, text);
			case 2:
				return helperConstructManager.updateStepWorldPointAt(kind, row, text);
			case 4:
				return helperConstructManager.updateStepInstructionAt(kind, row, text);
			default:
				return false;
		}
	}

	private JPanel buildOrderedView()
	{
		JPanel ordered = new JPanel(new BorderLayout(0, 6));
		ordered.setBackground(ColorScheme.DARK_GRAY_COLOR);

		orderTable = new JTable(stepOrderTableModel)
		{
			@Override
			public TableCellEditor getCellEditor(int row, int column)
			{
				int mRow = convertRowIndexToModel(row);
				if (column == 2)
				{
					var entry = stepOrderTableModel.getRow(mRow);
					if (entry != null && !entry.isSectionDivider())
					{
						return new DefaultCellEditor(new JTextField());
					}
				}
				return super.getCellEditor(row, column);
			}
		};
		orderTable.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				maybeShowOrderContextMenu(e);
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				if (maybeShowOrderContextMenu(e))
				{
					return;
				}
				int vRow = orderTable.rowAtPoint(e.getPoint());
				int vCol = orderTable.columnAtPoint(e.getPoint());
				if (vRow < 0 || vCol < 0)
				{
					orderTable.clearSelection();
					helperConstructManager.clearSelectedRenderedStep();
					return;
				}
				if (vCol != 1)
				{
					return;
				}
				int mRow = orderTable.convertRowIndexToModel(vRow);
				var entry = stepOrderTableModel.getRow(mRow);
				if (entry == null)
				{
					return;
				}
				openOrderConditionsEditor(entry.getIndex(), entry.getVarName());
			}
		});
		orderTable.setDragEnabled(true);
		orderTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		// StepOrderTableModel uses fixed logical column indices in setValueAt; keep header order stable.
		orderTable.getTableHeader().setReorderingAllowed(false);
		// Reorder drag uses StepReorderTransferHandler's edge autoscroll (ramps with drag time).
		orderTable.setAutoscrolls(false);
		orderTable.setDropMode(DropMode.INSERT_ROWS);
		orderTable.setFillsViewportHeight(true);
		orderTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		orderTable.setRowHeight(24);
		orderTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer()
		{
			@Override
			public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int column)
			{
				Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
				int mRow = t.convertRowIndexToModel(row);
				var entry = stepOrderTableModel.getRow(mRow);
				if (entry != null && entry.isSectionDivider())
				{
					if (!isSelected)
					{
						c.setBackground(new Color(46, 52, 64));
						c.setForeground(new Color(143, 188, 187));
					}
					setFont(getFont().deriveFont(Font.BOLD));
				}
				else
				{
					if (!isSelected)
					{
						c.setBackground(entry != null && entry.isLeagueStep() ? LEAGUE_STEP_BACKGROUND : ColorScheme.DARK_GRAY_COLOR);
						c.setForeground(Color.WHITE);
					}
					setFont(getFont().deriveFont(Font.PLAIN));
				}
				return c;
			}
		});
		orderTable.getColumnModel().getColumn(0).setPreferredWidth(160);
		orderTable.getColumnModel().getColumn(1).setPreferredWidth(220);
		orderTable.getColumnModel().getColumn(2).setPreferredWidth(260);
		orderTable.setTransferHandler(new StepReorderTransferHandler(orderTable));
		orderTable.getSelectionModel().addListSelectionListener(e ->
		{
			if (e.getValueIsAdjusting())
			{
				return;
			}
			int mRow = selectedModelRow(orderTable);
			if (mRow < 0)
			{
				helperConstructManager.clearSelectedRenderedStep();
				return;
			}
			HelperConstructManager.CombinedStepRow row = stepOrderTableModel.getRow(mRow);
			if (row == null || row.isSectionDivider())
			{
				helperConstructManager.clearSelectedRenderedStep();
				return;
			}
			helperConstructManager.setSelectedRenderedStepFromOrderRow(row.getIndex());
		});

		JScrollPane tableScroll = new JScrollPane(orderTable);
		tableScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		tableScroll.setBorder(new EmptyBorder(0, 0, 0, 0));
		orderTable.getTableHeader().addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				orderTable.clearSelection();
				helperConstructManager.clearSelectedRenderedStep();
			}
		});
		tableScroll.getViewport().addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				if (orderTable.rowAtPoint(e.getPoint()) < 0 || orderTable.columnAtPoint(e.getPoint()) < 0)
				{
					orderTable.clearSelection();
					helperConstructManager.clearSelectedRenderedStep();
				}
			}
		});
		JTextField orderSearch = newMakerSearchField("Filter quest order by var name, requirement label, section condition, or instruction.");
		wireTableSearchField(orderSearch, orderTable);
		orderSearch.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				orderTable.clearSelection();
				helperConstructManager.clearSelectedRenderedStep();
			}
		});
		JPanel orderCenter = new JPanel(new BorderLayout(0, 4));
		orderCenter.setOpaque(false);
		orderCenter.add(orderSearch, BorderLayout.NORTH);
		orderCenter.add(tableScroll, BorderLayout.CENTER);
		MouseAdapter clearOrderSelection = new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				orderTable.clearSelection();
				helperConstructManager.clearSelectedRenderedStep();
			}
		};
		ordered.addMouseListener(clearOrderSelection);
		orderCenter.addMouseListener(clearOrderSelection);
		ordered.add(orderCenter, BorderLayout.CENTER);

		JButton removeSelectedButton = new JButton("Remove selected");
		JButton addStepButton = new JButton("Add Step");
		JButton addSectionButton = new JButton("Add Section");
		applyMakerToolbarStyle(removeSelectedButton, addStepButton, addSectionButton);
		removeSelectedButton.addActionListener(e ->
		{
			List<Integer> selected = selectedModelRowsDescending(orderTable);
			if (!selected.isEmpty())
			{
				helperConstructManager.removeOrderLinesAtModelIndices(selected);
				refresh();
			}
		});
		addStepButton.addActionListener(e -> showAddStepDialog());
		addSectionButton.addActionListener(e ->
		{
			int ins = questOrderInsertIndexBelowSelection();
			if (ins < 0)
			{
				helperConstructManager.addSectionDivider();
			}
			else
			{
				helperConstructManager.addSectionDivider(ins);
			}
			refresh();
		});
		JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		actions.setBackground(ColorScheme.DARK_GRAY_COLOR);
		actions.add(addStepButton);
		actions.add(Box.createHorizontalStrut(6));
		actions.add(addSectionButton);
		actions.add(Box.createHorizontalStrut(6));
		actions.add(removeSelectedButton);
		ordered.add(actions, BorderLayout.SOUTH);
		return ordered;
	}

	private boolean maybeShowStepLibraryContextMenu(JTable table, ConstructStepKind stepKind, MouseEvent e)
	{
		if (!e.isPopupTrigger())
		{
			return false;
		}
		int viewRow = table.rowAtPoint(e.getPoint());
		if (viewRow < 0)
		{
			return false;
		}
		table.setRowSelectionInterval(viewRow, viewRow);
		int modelRow = table.convertRowIndexToModel(viewRow);
		JPopupMenu menu = new JPopupMenu();
		Integer currentStructId = helperConstructManager.getStepStructIdAt(stepKind, modelRow);
		JMenuItem setItem = new JMenuItem(currentStructId == null ? "Set League task ID..." : "Edit League task ID...");
		setItem.addActionListener(ev -> promptSetStepLibraryLeagueTaskId(stepKind, modelRow, currentStructId));
		menu.add(setItem);
		JMenuItem clearItem = new JMenuItem("Clear League task status");
		clearItem.setEnabled(currentStructId != null);
		clearItem.addActionListener(ev ->
		{
			helperConstructManager.updateStepStructIdAt(stepKind, modelRow, null);
			refresh();
		});
		menu.add(clearItem);
		menu.show(e.getComponent(), e.getX(), e.getY());
		return true;
	}

	private boolean maybeShowOrderContextMenu(MouseEvent e)
	{
		if (!e.isPopupTrigger())
		{
			return false;
		}
		int viewRow = orderTable.rowAtPoint(e.getPoint());
		if (viewRow < 0)
		{
			return false;
		}
		orderTable.setRowSelectionInterval(viewRow, viewRow);
		int modelRow = orderTable.convertRowIndexToModel(viewRow);
		HelperConstructManager.CombinedStepRow row = stepOrderTableModel.getRow(modelRow);
		if (row == null || row.isSectionDivider())
		{
			return false;
		}
		Integer currentStructId = helperConstructManager.getOrderReferencedStepStructId(row.getIndex());
		JPopupMenu menu = new JPopupMenu();
		JMenuItem setItem = new JMenuItem(currentStructId == null ? "Set League task ID..." : "Edit League task ID...");
		setItem.addActionListener(ev -> promptSetOrderLeagueTaskId(row.getIndex(), currentStructId));
		menu.add(setItem);
		JMenuItem clearItem = new JMenuItem("Clear League task status");
		clearItem.setEnabled(currentStructId != null);
		clearItem.addActionListener(ev ->
		{
			helperConstructManager.updateOrderReferencedStepStructId(row.getIndex(), null);
			refresh();
		});
		menu.add(clearItem);
		menu.show(e.getComponent(), e.getX(), e.getY());
		return true;
	}

	private void promptSetStepLibraryLeagueTaskId(ConstructStepKind kind, int modelRow, Integer currentStructId)
	{
		Integer chosen = promptForLeagueTaskId(currentStructId);
		if (chosen == null)
		{
			return;
		}
		if (helperConstructManager.updateStepStructIdAt(kind, modelRow, chosen))
		{
			refresh();
		}
	}

	private void promptSetOrderLeagueTaskId(int orderIndex, Integer currentStructId)
	{
		Integer chosen = promptForLeagueTaskId(currentStructId);
		if (chosen == null)
		{
			return;
		}
		if (helperConstructManager.updateOrderReferencedStepStructId(orderIndex, chosen))
		{
			refresh();
		}
	}

	private Integer promptForLeagueTaskId(Integer currentStructId)
	{
		String initial = currentStructId == null ? "" : String.valueOf(currentStructId);
		String input = (String) JOptionPane.showInputDialog(
			this,
			"Enter League task ID (taskId/structId):",
			"Set League task ID",
			JOptionPane.PLAIN_MESSAGE,
			null,
			null,
			initial);
		if (input == null)
		{
			return null;
		}
		String trimmed = input.trim();
		if (trimmed.isEmpty())
		{
			JOptionPane.showMessageDialog(this, "Task ID cannot be empty.", "Invalid task ID", JOptionPane.WARNING_MESSAGE);
			return null;
		}
		try
		{
			int id = Integer.parseInt(trimmed);
			if (id <= 0)
			{
				throw new NumberFormatException("Task ID must be positive");
			}
			return id;
		}
		catch (NumberFormatException ex)
		{
			JOptionPane.showMessageDialog(this, "Task ID must be a positive integer.", "Invalid task ID", JOptionPane.WARNING_MESSAGE);
			return null;
		}
	}

	private void showAddStepDialog()
	{
		final int insertAt = questOrderInsertIndexBelowSelection();
		var options = helperConstructManager.getStepDefinitionPickOptions();
		if (options.isEmpty())
		{
			JOptionPane.showMessageDialog(this,
				"Capture NPC, object, or generic steps in the maker tabs first, then add them to the quest order here.",
				"Add Step",
				JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		List<HelperConstructManager.StepDefinitionPickOption> allOptions = new ArrayList<>(options);
		DefaultListModel<HelperConstructManager.StepDefinitionPickOption> listModel = new DefaultListModel<>();
		JList<HelperConstructManager.StepDefinitionPickOption> list = new JList<>(listModel);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JTextField stepPickSearch = newMakerSearchField("Filter by instruction, name, target, option, or id (case-insensitive).");
		Runnable refillStepPickList = () ->
		{
			String q = stepPickSearch.getText().trim().toLowerCase(Locale.ROOT);
			listModel.clear();
			for (HelperConstructManager.StepDefinitionPickOption o : allOptions)
			{
				String hay = (o.getFilterText() == null ? "" : o.getFilterText()).toLowerCase(Locale.ROOT);
				if (q.isEmpty() || hay.contains(q))
				{
					listModel.addElement(o);
				}
			}
			if (listModel.isEmpty())
			{
				list.clearSelection();
			}
			else
			{
				int si = list.getSelectedIndex();
				if (si < 0 || si >= listModel.getSize())
				{
					list.setSelectedIndex(0);
				}
			}
		};
		stepPickSearch.getDocument().addDocumentListener(new DocumentListener()
		{
			@Override
			public void insertUpdate(DocumentEvent e)
			{
				refillStepPickList.run();
			}

			@Override
			public void removeUpdate(DocumentEvent e)
			{
				refillStepPickList.run();
			}

			@Override
			public void changedUpdate(DocumentEvent e)
			{
				refillStepPickList.run();
			}
		});
		refillStepPickList.run();
		JScrollPane sp = new JScrollPane(list);
		sp.setPreferredSize(new Dimension(420, 240));
		JPanel panel = new JPanel(new BorderLayout(0, 6));
		panel.setOpaque(false);
		panel.add(stepPickSearch, BorderLayout.NORTH);
		panel.add(sp, BorderLayout.CENTER);
		panel.setPreferredSize(new Dimension(440, 300));
		int r = JOptionPane.showConfirmDialog(this, panel, "Add Step to order", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (r == JOptionPane.OK_OPTION && list.getSelectedValue() != null)
		{
			if (insertAt < 0)
			{
				helperConstructManager.addOrderRef(list.getSelectedValue().getStepId());
			}
			else
			{
				helperConstructManager.addOrderRef(list.getSelectedValue().getStepId(), insertAt);
			}
			refresh();
		}
	}

	private void openOrderConditionsEditor(int orderIndex, String varNameHint)
	{
		String label = varNameHint == null || varNameHint.isBlank() ? "order row " + (orderIndex + 1) : varNameHint;
		if (OrderStepRequirementTreeEditorDialog.showEditor(SwingUtilities.getWindowAncestor(this), helperConstructManager, orderIndex, label))
		{
			refresh();
		}
	}

	/**
	 * Keeps maker header actions on one row; when width is insufficient, trailing items move under {@code More…} (popup menu).
	 */
	private static final class OverflowingMakerToolbar extends JPanel
	{
		private final List<JComponent> chain;
		private final JPanel inlinePanel;
		private final JButton moreButton;
		private final JPopupMenu overflowMenu;

		OverflowingMakerToolbar(List<JComponent> chain, Consumer<JButton> styleMoreLikeToolbar)
		{
			super(new BorderLayout());
			this.chain = List.copyOf(chain);
			setOpaque(false);
			inlinePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
			inlinePanel.setOpaque(false);
			moreButton = new JButton("More…");
			styleMoreLikeToolbar.accept(moreButton);
			moreButton.setToolTipText("Additional maker actions that do not fit in the toolbar.");
			overflowMenu = new JPopupMenu();
			moreButton.addActionListener(e -> overflowMenu.show(moreButton, 0, moreButton.getHeight()));
			add(inlinePanel, BorderLayout.CENTER);
			addComponentListener(new ComponentAdapter()
			{
				@Override
				public void componentResized(ComponentEvent e)
				{
					reconcile();
				}
			});
			SwingUtilities.invokeLater(this::reconcile);
		}

		void reconcile()
		{
			final int hgap = 8;
			int avail = getWidth() - getInsets().left - getInsets().right;
			if (avail <= 0)
			{
				return;
			}
			int n = chain.size();
			int moreW = moreButton.getPreferredSize().width + hgap;
			int best = 0;
			for (int k = n; k >= 0; k--)
			{
				int prefixW = prefixWidth(chain, 0, k, hgap);
				int reserveMore = (k < n) ? moreW : 0;
				if (prefixW + reserveMore <= avail)
				{
					best = k;
					break;
				}
			}
			while (best > 0 && chain.get(best - 1) instanceof JSeparator)
			{
				best--;
			}
			inlinePanel.removeAll();
			overflowMenu.removeAll();
			for (int i = 0; i < best; i++)
			{
				inlinePanel.add(chain.get(i));
			}
			if (best < n)
			{
				for (int i = best; i < n; i++)
				{
					JComponent c = chain.get(i);
					if (c instanceof JButton)
					{
						JButton b = (JButton) c;
						JMenuItem mi = new JMenuItem(b.getText());
						String tt = b.getToolTipText();
						if (tt != null && !tt.isBlank())
						{
							mi.setToolTipText(tt);
						}
						mi.addActionListener(ev ->
						{
							Object handler = b.getClientProperty("makerOverflowHandler");
							if (handler instanceof Consumer)
							{
								@SuppressWarnings("unchecked")
								Consumer<JButton> consumer = (Consumer<JButton>) handler;
								consumer.accept(moreButton);
							}
							else
							{
								b.doClick();
							}
						});
						overflowMenu.add(mi);
					}
				}
				inlinePanel.add(moreButton);
			}
			inlinePanel.revalidate();
			inlinePanel.repaint();
			revalidate();
			repaint();
		}

		private static int prefixWidth(List<JComponent> chain, int from, int toExclusive, int hgap)
		{
			int w = 0;
			for (int i = from; i < toExclusive; i++)
			{
				if (i > from)
				{
					w += hgap;
				}
				w += chain.get(i).getPreferredSize().width;
			}
			return w;
		}
	}

	public void refresh()
	{
		for (StepLibraryTab tab : stepLibraryTabs)
		{
			refreshStepLibraryTable(tab.model, tab.kind);
		}
		requirementLibraryModel.setRows(
			helperConstructManager.getRequirementDisplayNames(),
			helperConstructManager.getRequirementRawIdStrings());
		stepOrderTableModel.setRows(helperConstructManager.getCombinedStepRows());
		varbitRoutingTableModel.reloadFromManager();
		zoneRoutingTableModel.reloadFromManager();
		syncZoneOverlaySelectionState();
		syncConstructMenuSelectionState();
		skillRoutingTableModel.reloadFromManager();
		syncWorldMapRouteButtonLabel();
		syncRouteRevealButtonLabel();
		if (routeRevealSlider != null && routeRevealValueLabel != null)
		{
			int managerPercent = helperConstructManager.getWorldMapRouteRevealPercent();
			if (routeRevealSlider.getValue() != managerPercent)
			{
				routeRevealSlider.setValue(managerPercent);
			}
			updateRouteRevealDisplayState(routeRevealSlider, routeRevealValueLabel);
		}
		revalidate();
		repaint();
	}

	public void shutDown()
	{
		if (routeRevealDialog != null)
		{
			routeRevealDialog.dispose();
		}
		helperConstructManager.removeDraftChangeListener(draftChangeListener);
		helperConstructManager.disableWorldMapRoutePreview();
		helperConstructManager.clearSelectedZoneOverlay();
		helperConstructManager.setZoneSelectionOverlayEnabled(false);
		helperConstructManager.stopZoneCreationFromUi();
		helperConstructManager.clearConstructMenuSelectedStep();
		helperConstructManager.clearSelectedRenderedStep();
	}

	private void syncZoneOverlaySelectionState()
	{
		boolean zoneTabActive = zoneTabIndex >= 0 && mainTabs.getSelectedIndex() == zoneTabIndex;
		helperConstructManager.setZoneSelectionOverlayEnabled(zoneTabActive);
		if (!zoneTabActive)
		{
			helperConstructManager.clearSelectedZoneOverlay();
			return;
		}
		int r = selectedModelRow(zoneReqsTable);
		if (r < 0)
		{
			helperConstructManager.clearSelectedZoneOverlay();
			return;
		}
		String orderSlotId = zoneRoutingTableModel.getOrderSlotIdAt(r);
		helperConstructManager.setSelectedZoneOverlayByOrderSlotId(orderSlotId);
	}

	private void syncConstructMenuSelectionState()
	{
		// Step-library tabs are the first tabs (NPC/Object/Generic).
		if (mainTabs.getSelectedIndex() > 2)
		{
			helperConstructManager.clearConstructMenuSelectedStep();
		}
	}

	private void syncSelectedStepRenderSelectionState()
	{
		int tab = mainTabs.getSelectedIndex();
		if (tab >= 0 && tab <= 2)
		{
			StepLibraryTab active = tab < stepLibraryTabs.size() ? stepLibraryTabs.get(tab) : null;
			if (active == null)
			{
				helperConstructManager.clearSelectedRenderedStep();
				return;
			}
			int sel = selectedModelRow(active.table);
			if (sel < 0)
			{
				helperConstructManager.clearSelectedRenderedStep();
				return;
			}
			helperConstructManager.setSelectedRenderedStepFromLibrary(active.kind, sel);
			return;
		}
		if (orderTabIndex >= 0 && tab == orderTabIndex)
		{
			int mRow = selectedModelRow(orderTable);
			if (mRow < 0)
			{
				helperConstructManager.clearSelectedRenderedStep();
				return;
			}
			HelperConstructManager.CombinedStepRow row = stepOrderTableModel.getRow(mRow);
			if (row == null || row.isSectionDivider())
			{
				helperConstructManager.clearSelectedRenderedStep();
				return;
			}
			helperConstructManager.setSelectedRenderedStepFromOrderRow(row.getIndex());
			return;
		}
		{
			helperConstructManager.clearSelectedRenderedStep();
		}
	}

	private void clearStepRenderSourceSelectionsOnTabSwitch()
	{
		if (orderTable != null)
		{
			orderTable.clearSelection();
		}
		for (StepLibraryTab tab : stepLibraryTabs)
		{
			if (tab != null && tab.table != null)
			{
				tab.table.clearSelection();
			}
		}
		helperConstructManager.clearConstructMenuSelectedStep();
		helperConstructManager.clearSelectedRenderedStep();
	}

	private static String stepLibraryTabTitle(ConstructStepKind kind)
	{
		switch (kind)
		{
			case NPC:
				return "NPC steps";
			case OBJECT:
				return "Object steps";
			case TEXT:
				return "Generic steps";
			default:
				return kind.name();
		}
	}

	private static final class StepLibraryTab
	{
		final ConstructStepKind kind;
		final StepLibraryTableModel model;
		final JTable table;

		StepLibraryTab(ConstructStepKind kind, StepLibraryTableModel model, JTable table)
		{
			this.kind = kind;
			this.model = model;
			this.table = table;
		}
	}

	private void refreshStepLibraryTable(StepLibraryTableModel model, ConstructStepKind kind)
	{
		model.setRows(
			helperConstructManager.getStepVarNames(kind),
			helperConstructManager.getStepRawIdTexts(kind),
			helperConstructManager.getStepWorldPointTexts(kind),
			helperConstructManager.getStepRequirementsDisplays(kind),
			helperConstructManager.getStepInstructionTexts(kind));
	}

	private JTextArea sectionLabel(String text)
	{
		var label = JGenerator.makeJTextArea(text);
		label.setForeground(Color.GRAY);
		label.setLineWrap(true);
		label.setWrapStyleWord(true);
		return label;
	}

	private final class StepOrderTableModel extends AbstractTableModel
	{
		private final String[] columns = {"Var name", "Conditions", "Text"};
		private List<HelperConstructManager.CombinedStepRow> rows = new ArrayList<>();

		@Override
		public int getRowCount()
		{
			return rows.size();
		}

		@Override
		public int getColumnCount()
		{
			return columns.length;
		}

		@Override
		public String getColumnName(int column)
		{
			return columns[column];
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex)
		{
			var row = rows.get(rowIndex);
			if (columnIndex == 0)
			{
				return row.getVarName();
			}
			if (columnIndex == 2)
			{
				return row.isSectionDivider() ? "" : row.getInstructionText();
			}
			if (row.isSectionDivider())
			{
				if (row.hasOrderStepRequirementTree())
				{
					boolean continueMode = row.getOrderConditionMode() == HelperConstructModels.OrderConditionMode.CONTINUE_WHEN_TRUE;
					if (row.isCustomOrderStepRequirement())
					{
						return continueMode ? "Conditions (custom, continue)…" : "Conditions (custom, show)…";
					}
					return continueMode ? "Conditions (continue)…" : "Conditions (show)…";
				}
				return "Conditions…";
			}
			if (row.hasOrderStepRequirementTree())
			{
				boolean continueMode = row.getOrderConditionMode() == HelperConstructModels.OrderConditionMode.CONTINUE_WHEN_TRUE;
				boolean stickyComplete = row.isPassOnceCompletedOnce();
				if (row.isCustomOrderStepRequirement())
				{
					if (continueMode)
					{
						return stickyComplete ? "Conditions (custom, continue, pass-once)…" : "Conditions (custom, continue)…";
					}
					return stickyComplete ? "Conditions (custom, show, pass-once)…" : "Conditions (custom, show)…";
				}
				if (continueMode)
				{
					return stickyComplete ? "Conditions (continue, pass-once)…" : "Conditions (continue)…";
				}
				return stickyComplete ? "Conditions (show, pass-once)…" : "Conditions (show)…";
			}
			return "Conditions…";
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex)
		{
			var row = rows.get(rowIndex);
			if (columnIndex == 0)
			{
				return true;
			}
			if (columnIndex == 2)
			{
				return !row.isSectionDivider();
			}
			if (columnIndex == 1)
			{
				return false;
			}
			return false;
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex)
		{
			var row = rows.get(rowIndex);
			String updatedVarName = aValue == null ? "" : String.valueOf(aValue);
			if (columnIndex == 0)
			{
				helperConstructManager.updateStepVarName(row.getIndex(), updatedVarName);
				setRows(helperConstructManager.getCombinedStepRows());
				return;
			}
			if (columnIndex == 2 && !row.isSectionDivider())
			{
				helperConstructManager.updateOrderReferencedStepInstructionText(row.getIndex(), updatedVarName);
				setRows(helperConstructManager.getCombinedStepRows());
				return;
			}
			// Conditions are edited via the dedicated conditions editor dialog.
		}

		void setRows(List<HelperConstructManager.CombinedStepRow> updatedRows)
		{
			rows = new ArrayList<>(updatedRows);
			fireTableDataChanged();
		}

		HelperConstructManager.CombinedStepRow getRow(int index)
		{
			if (index < 0 || index >= rows.size())
			{
				return null;
			}
			return rows.get(index);
		}

		String signature()
		{
			List<String> parts = new ArrayList<>();
			for (HelperConstructManager.CombinedStepRow row : rows)
			{
				parts.add(row.getVarName() + "|" + row.getSummary() + "|" + row.getSectionCondition() + "|" + row.getOrderLinkedRequirementRawId()
					+ "|" + row.getInstructionText() + "|" + row.isCustomOrderStepRequirement() + "|" + row.hasOrderStepRequirementTree());
			}
			return String.join("\n", parts);
		}
	}

	private final class VarbitRoutingTableModel extends AbstractTableModel
	{
		private final String[] columns = {"Var name", "Varbit ID", "Required value", "Operation", "Display text"};
		private final List<VarbitRoutingRow> rows = new ArrayList<>();

		String getOrderSlotIdAt(int rowIndex)
		{
			if (rowIndex < 0 || rowIndex >= rows.size())
			{
				return null;
			}
			return rows.get(rowIndex).getOrderSlotId();
		}

		void reloadFromManager()
		{
			rows.clear();
			for (HelperConstructManager.VarbitSlotRow slot : helperConstructManager.getVarbitSlotsInQuestOrderForEditor())
			{
				rows.add(new VarbitRoutingRow(
					slot.getOrderSlotId(),
					slot.getVarName(),
					slot.getVarbitId(),
					slot.getRequiredValue(),
					slot.getOperation(),
					slot.getDisplayText()));
			}
			fireTableDataChanged();
		}

		String signature()
		{
			List<String> parts = new ArrayList<>();
			for (VarbitRoutingRow row : rows)
			{
				parts.add(row.getOrderSlotId() + "|" + row.varName + "|" + row.varbitId + "|" + row.requiredValue + "|" + row.operation + "|" + row.displayText);
			}
			return String.join("\n", parts);
		}

		@Override
		public int getRowCount()
		{
			return rows.size();
		}

		@Override
		public int getColumnCount()
		{
			return columns.length;
		}

		@Override
		public String getColumnName(int column)
		{
			return columns[column];
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex)
		{
			VarbitRoutingRow row = rows.get(rowIndex);
			switch (columnIndex)
			{
				case 0:
					return row.varName;
				case 1:
					return row.varbitId;
				case 2:
					return row.requiredValue;
				case 3:
					return operationFromPersistedName(row.operation);
				case 4:
					return row.displayText;
				default:
					return "";
			}
		}

		@Override
		public Class<?> getColumnClass(int columnIndex)
		{
			if (columnIndex == 1 || columnIndex == 2)
			{
				return Integer.class;
			}
			if (columnIndex == 3)
			{
				return Operation.class;
			}
			return String.class;
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex)
		{
			return columnIndex >= 0;
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex)
		{
			if (rowIndex < 0 || rowIndex >= rows.size())
			{
				return;
			}
			VarbitRoutingRow row = rows.get(rowIndex);
			if (columnIndex == 0)
			{
				if (!helperConstructManager.updateStepVarNameForOrderSlotId(row.getOrderSlotId(), String.valueOf(aValue)))
				{
					JOptionPane.showMessageDialog(HelperConstructEditorPanel.this,
						"Could not update var name.",
						"Varbit reqs",
						JOptionPane.WARNING_MESSAGE);
				}
				reloadFromManager();
				return;
			}
			int newVarbit = row.varbitId;
			int newReq = row.requiredValue;
			String newOp = row.operation;
			String newDisp = row.displayText;
			switch (columnIndex)
			{
				case 1:
					try
					{
						newVarbit = Integer.parseInt(String.valueOf(aValue).trim());
					}
					catch (NumberFormatException ex)
					{
						JOptionPane.showMessageDialog(HelperConstructEditorPanel.this,
							"Enter a valid integer for varbit id.",
							"Varbit reqs",
							JOptionPane.WARNING_MESSAGE);
						reloadFromManager();
						return;
					}
					break;
				case 2:
					try
					{
						newReq = Integer.parseInt(String.valueOf(aValue).trim());
					}
					catch (NumberFormatException ex)
					{
						JOptionPane.showMessageDialog(HelperConstructEditorPanel.this,
							"Enter a valid integer for required value.",
							"Varbit reqs",
							JOptionPane.WARNING_MESSAGE);
						reloadFromManager();
						return;
					}
					break;
				case 3:
					if (aValue instanceof Operation)
					{
						newOp = ((Operation) aValue).name();
					}
					else
					{
						newOp = operationFromPersistedName(String.valueOf(aValue)).name();
					}
					break;
				case 4:
					newDisp = String.valueOf(aValue);
					break;
				default:
					return;
			}
			if (!helperConstructManager.updateVarbitSlotForOrderSlot(row.getOrderSlotId(), newVarbit, newReq, newOp, newDisp))
			{
				JOptionPane.showMessageDialog(HelperConstructEditorPanel.this,
					"Could not apply varbit settings.",
					"Varbit reqs",
					JOptionPane.WARNING_MESSAGE);
			}
			reloadFromManager();
		}

		private final class VarbitRoutingRow
		{
			private final String orderSlotId;
			private final String varName;
			private final int varbitId;
			private final int requiredValue;
			private final String operation;
			private final String displayText;

			private VarbitRoutingRow(String orderSlotId, String varName, int varbitId, int requiredValue, String operation, String displayText)
			{
				this.orderSlotId = orderSlotId;
				this.varName = varName == null ? "" : varName;
				this.varbitId = varbitId;
				this.requiredValue = requiredValue;
				this.operation = operation;
				this.displayText = displayText;
			}

			private String getOrderSlotId()
			{
				return orderSlotId;
			}
		}
	}

	private final class ZoneRoutingTableModel extends AbstractTableModel
	{
		private final String[] columns = {"Var name", "Corner 1 (x, y, plane)", "Corner 2 (x, y, plane)", "Display text"};
		private final List<ZoneRoutingRow> rows = new ArrayList<>();

		String getOrderSlotIdAt(int rowIndex)
		{
			if (rowIndex < 0 || rowIndex >= rows.size())
			{
				return null;
			}
			return rows.get(rowIndex).getOrderSlotId();
		}

		void reloadFromManager()
		{
			rows.clear();
			for (HelperConstructManager.ZoneSlotRow slot : helperConstructManager.getZoneSlotsInQuestOrderForEditor())
			{
				rows.add(new ZoneRoutingRow(
					slot.getOrderSlotId(),
					slot.getVarName(),
					slot.getCorner1Text(),
					slot.getCorner2Text(),
					slot.getDisplayText()));
			}
			fireTableDataChanged();
		}

		String signature()
		{
			List<String> parts = new ArrayList<>();
			for (ZoneRoutingRow row : rows)
			{
				parts.add(row.getOrderSlotId() + "|" + row.varName + "|" + row.corner1 + "|" + row.corner2 + "|" + row.displayText);
			}
			return String.join("\n", parts);
		}

		@Override
		public int getRowCount()
		{
			return rows.size();
		}

		@Override
		public int getColumnCount()
		{
			return columns.length;
		}

		@Override
		public String getColumnName(int column)
		{
			return columns[column];
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex)
		{
			ZoneRoutingRow row = rows.get(rowIndex);
			switch (columnIndex)
			{
				case 0:
					return row.varName;
				case 1:
					return row.corner1;
				case 2:
					return row.corner2;
				case 3:
					return row.displayText;
				default:
					return "";
			}
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex)
		{
			return columnIndex >= 0;
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex)
		{
			if (rowIndex < 0 || rowIndex >= rows.size())
			{
				return;
			}
			ZoneRoutingRow row = rows.get(rowIndex);
			if (columnIndex == 0)
			{
				if (!helperConstructManager.updateStepVarNameForOrderSlotId(row.getOrderSlotId(), String.valueOf(aValue)))
				{
					JOptionPane.showMessageDialog(HelperConstructEditorPanel.this,
						"Could not update var name.",
						"Zone reqs",
						JOptionPane.WARNING_MESSAGE);
				}
				reloadFromManager();
				return;
			}
			String c1 = row.corner1;
			String c2 = row.corner2;
			String disp = row.displayText;
			switch (columnIndex)
			{
				case 1:
					c1 = String.valueOf(aValue);
					break;
				case 2:
					c2 = String.valueOf(aValue);
					break;
				case 3:
					disp = String.valueOf(aValue);
					break;
				default:
					return;
			}
			if (!helperConstructManager.updateZoneSlotForOrderSlot(row.getOrderSlotId(), c1, c2, disp))
			{
				JOptionPane.showMessageDialog(HelperConstructEditorPanel.this,
					"Enter corners as three integers: x, y, plane (e.g. 3200, 3200, 0).",
					"Zone reqs",
					JOptionPane.WARNING_MESSAGE);
			}
			reloadFromManager();
		}

		private final class ZoneRoutingRow
		{
			private final String orderSlotId;
			private final String varName;
			private final String corner1;
			private final String corner2;
			private final String displayText;

			private ZoneRoutingRow(String orderSlotId, String varName, String corner1, String corner2, String displayText)
			{
				this.orderSlotId = orderSlotId;
				this.varName = varName == null ? "" : varName;
				this.corner1 = corner1 == null ? "" : corner1;
				this.corner2 = corner2 == null ? "" : corner2;
				this.displayText = displayText == null ? "" : displayText;
			}

			private String getOrderSlotId()
			{
				return orderSlotId;
			}
		}
	}

	private final class SkillRoutingTableModel extends AbstractTableModel
	{
		private final String[] columns = {"Skill", "Required level", "Can boost", "Display text", "Operation"};
		private final List<SkillRoutingRow> rows = new ArrayList<>();

		void reloadFromManager()
		{
			rows.clear();
			for (HelperConstructManager.SkillReqRow row : helperConstructManager.getSkillRequirementsForEditor())
			{
				rows.add(new SkillRoutingRow(
					row.getIndex(),
					row.getSkillName(),
					row.getRequiredLevel(),
					row.isCanBeBoosted(),
					row.getDisplayText(),
					row.getOperation()));
			}
			fireTableDataChanged();
		}

		@Override
		public int getRowCount()
		{
			return rows.size();
		}

		@Override
		public int getColumnCount()
		{
			return columns.length;
		}

		@Override
		public String getColumnName(int column)
		{
			return columns[column];
		}

		@Override
		public Class<?> getColumnClass(int columnIndex)
		{
			if (columnIndex == 1)
			{
				return Integer.class;
			}
			if (columnIndex == 2)
			{
				return Boolean.class;
			}
			if (columnIndex == 4)
			{
				return Operation.class;
			}
			return String.class;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex)
		{
			SkillRoutingRow row = rows.get(rowIndex);
			switch (columnIndex)
			{
				case 0:
					return Skill.valueOf(row.skillName);
				case 1:
					return row.requiredLevel;
				case 2:
					return row.canBeBoosted;
				case 3:
					return row.displayText;
				case 4:
					return operationFromPersistedName(row.operation);
				default:
					return "";
			}
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex)
		{
			return columnIndex >= 0;
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex)
		{
			if (rowIndex < 0 || rowIndex >= rows.size())
			{
				return;
			}
			SkillRoutingRow row = rows.get(rowIndex);
			String newSkillName = row.skillName;
			int newRequiredLevel = row.requiredLevel;
			boolean newCanBoost = row.canBeBoosted;
			String newDisplayText = row.displayText;
			String newOperation = row.operation;
			switch (columnIndex)
			{
				case 0:
					if (aValue instanceof Skill)
					{
						newSkillName = ((Skill) aValue).name();
					}
					else
					{
						try
						{
							newSkillName = Skill.valueOf(String.valueOf(aValue).trim().toUpperCase(Locale.ROOT)).name();
						}
						catch (IllegalArgumentException ex)
						{
							JOptionPane.showMessageDialog(HelperConstructEditorPanel.this,
								"Choose a valid skill.",
								"Skill reqs",
								JOptionPane.WARNING_MESSAGE);
							reloadFromManager();
							return;
						}
					}
					break;
				case 1:
					try
					{
						newRequiredLevel = Integer.parseInt(String.valueOf(aValue).trim());
					}
					catch (NumberFormatException ex)
					{
						JOptionPane.showMessageDialog(HelperConstructEditorPanel.this,
							"Enter a valid integer for required level.",
							"Skill reqs",
							JOptionPane.WARNING_MESSAGE);
						reloadFromManager();
						return;
					}
					break;
				case 2:
					newCanBoost = Boolean.TRUE.equals(aValue);
					break;
				case 3:
					newDisplayText = String.valueOf(aValue);
					break;
				case 4:
					if (aValue instanceof Operation)
					{
						newOperation = ((Operation) aValue).name();
					}
					else
					{
						newOperation = operationFromPersistedName(String.valueOf(aValue)).name();
					}
					break;
				default:
					return;
			}
			if (!helperConstructManager.updateSkillRequirementAt(row.index, newSkillName, newRequiredLevel, newCanBoost, newDisplayText, newOperation))
			{
				JOptionPane.showMessageDialog(HelperConstructEditorPanel.this,
					"Could not apply skill requirement settings.",
					"Skill reqs",
					JOptionPane.WARNING_MESSAGE);
			}
			reloadFromManager();
		}

		private final class SkillRoutingRow
		{
			private final int index;
			private final String skillName;
			private final int requiredLevel;
			private final boolean canBeBoosted;
			private final String displayText;
			private final String operation;

			private SkillRoutingRow(int index, String skillName, int requiredLevel, boolean canBeBoosted, String displayText, String operation)
			{
				this.index = index;
				this.skillName = skillName == null ? Skill.ATTACK.name() : skillName;
				this.requiredLevel = Math.max(requiredLevel, 1);
				this.canBeBoosted = canBeBoosted;
				this.displayText = displayText == null ? "" : displayText;
				this.operation = operation == null || operation.isBlank() ? Operation.GREATER_EQUAL.name() : operation;
			}
		}
	}

	private final class StepReorderTransferHandler extends TransferHandler
	{
		private static final int REORDER_AUTOSCROLL_INTERVAL_MS = 40;
		private static final int REORDER_AUTOSCROLL_EDGE_PX = 28;

		private final JTable table;
		private int fromModelRow = -1;
		private Timer reorderAutoscrollTimer;
		private long reorderDragStartNanos = -1L;
		private int reorderAutoscrollEdgeTicks;

		private StepReorderTransferHandler(JTable table)
		{
			this.table = table;
		}

		@Override
		protected Transferable createTransferable(JComponent c)
		{
			int sel = table.getSelectedRow();
			if (sel < 0)
			{
				fromModelRow = -1;
				return null;
			}
			fromModelRow = table.convertRowIndexToModel(sel);
			reorderDragStartNanos = System.nanoTime();
			reorderAutoscrollEdgeTicks = 0;
			if (reorderAutoscrollTimer == null)
			{
				reorderAutoscrollTimer = new Timer(REORDER_AUTOSCROLL_INTERVAL_MS, e -> tickReorderDragAutoscroll());
				reorderAutoscrollTimer.setRepeats(true);
			}
			reorderAutoscrollTimer.start();
			return new StringSelection(String.valueOf(fromModelRow));
		}

		@Override
		public void exportDone(JComponent source, Transferable data, int action)
		{
			stopReorderDragAutoscroll();
			fromModelRow = -1;
		}

		private void stopReorderDragAutoscroll()
		{
			if (reorderAutoscrollTimer != null)
			{
				reorderAutoscrollTimer.stop();
			}
			reorderDragStartNanos = -1L;
			reorderAutoscrollEdgeTicks = 0;
		}

		private void tickReorderDragAutoscroll()
		{
			if (reorderDragStartNanos < 0L || fromModelRow < 0 || GraphicsEnvironment.isHeadless())
			{
				return;
			}
			PointerInfo pi = MouseInfo.getPointerInfo();
			if (pi == null)
			{
				return;
			}
			Point screen = pi.getLocation();
			Point loc = new Point(screen);
			SwingUtilities.convertPointFromScreen(loc, table);
			Rectangle vis = table.getVisibleRect();
			boolean inTop = loc.y < vis.y + REORDER_AUTOSCROLL_EDGE_PX;
			boolean inBottom = loc.y > vis.y + vis.height - REORDER_AUTOSCROLL_EDGE_PX;

			JScrollPane sp = (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, table);
			if (sp == null)
			{
				return;
			}
			JScrollBar bar = sp.getVerticalScrollBar();
			if (!bar.isEnabled())
			{
				return;
			}

			if (inTop || inBottom)
			{
				reorderAutoscrollEdgeTicks++;
				long elapsedMs = (System.nanoTime() - reorderDragStartNanos) / 1_000_000L;
				int fromEdge = Math.min(48, reorderAutoscrollEdgeTicks * 3);
				int fromDuration = (int) Math.min(40, elapsedMs / 100);
				int amount = Math.min(96, 5 + fromEdge + fromDuration);
				if (inTop)
				{
					bar.setValue(Math.max(bar.getMinimum(), bar.getValue() - amount));
				}
				else
				{
					int max = Math.max(bar.getMinimum(), bar.getMaximum() - bar.getVisibleAmount());
					bar.setValue(Math.min(max, bar.getValue() + amount));
				}
			}
			else
			{
				reorderAutoscrollEdgeTicks = 0;
			}
		}

		@Override
		public int getSourceActions(JComponent c)
		{
			return MOVE;
		}

		@Override
		public boolean canImport(TransferSupport support)
		{
			return support.isDrop();
		}

		@Override
		public boolean importData(TransferSupport support)
		{
			if (!canImport(support))
			{
				return false;
			}

			JTable.DropLocation dropLocation = (JTable.DropLocation) support.getDropLocation();
			int toView = dropLocation.getRow();
			if (fromModelRow < 0 || toView < 0)
			{
				return false;
			}

			int toModel;
			if (toView >= table.getRowCount())
			{
				toModel = stepOrderTableModel.getRowCount();
			}
			else
			{
				toModel = table.convertRowIndexToModel(toView);
			}

			int targetIndex = toModel;
			if (targetIndex > fromModelRow)
			{
				targetIndex--;
			}
			if (targetIndex < 0)
			{
				targetIndex = 0;
			}
			if (targetIndex >= stepOrderTableModel.getRowCount())
			{
				targetIndex = stepOrderTableModel.getRowCount() - 1;
			}

			boolean moved = helperConstructManager.moveStep(fromModelRow, targetIndex);
			if (moved)
			{
				setRowsAfterReorder();
			}
			return moved;
		}

		private void setRowsAfterReorder()
		{
			stepOrderTableModel.setRows(helperConstructManager.getCombinedStepRows());
		}
	}
}
