package com.questhelper.helpers.guides;

import com.questhelper.QuestHelperConfig;
import com.questhelper.QuestHelperPlugin;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.ui.widgets.ModalDialog;
import com.questhelper.ui.widgets.TabContainer;
import com.questhelper.ui.widgets.ButtonGrid;
import com.questhelper.ui.widgets.WidgetFactory;
import com.questhelper.ui.widgets.VerticalScrollableContainer;
import com.questhelper.ui.widgets.QuestList;
import com.questhelper.ui.widgets.RowFirstButtonGrid;
import com.questhelper.managers.PathManager;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.SpriteID;
import net.runelite.api.widgets.JavaScriptCallback;
import net.runelite.api.widgets.Widget;
import net.runelite.api.ScriptEvent;
import net.runelite.api.widgets.WidgetSizeMode;
import org.apache.commons.lang3.tuple.Pair;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class EarlyGameGuide
{
	// Used for determining when the player has changed interface styles
	@Getter
	private Widget currentTopLevelWidget;
	private ModalDialog modalDialog;
	private TabContainer tabContainer;
	
	// Category collapse state tracking
	Map<UnlockCategory, Boolean> categoryCollapsed = new HashMap<>();
	
	// State tracking for unlock detail view
	private boolean showingUnlockDetail = false;
	
	// Pre-created UI layers for Paths tab
	private Widget mainPathsLayer = null;
	private Widget detailPathsLayer = null;
	
	// Detail content is per-invocation; back button is created persistently without storing

	// Track active content layers for main and detail views
	private Widget activeMainContent = null;
	private Widget activeDetailContent = null;
	
	// Pre-created main paths container reference (latest instance)
	private VerticalScrollableContainer mainPathsContainer = null;
	
	// Path management
	@Inject
	private PathManager pathManager;
	private Widget activePathButton = null;
	
	// Position memory for dialog placement
	private Integer savedX = null;
	private Integer savedY = null;
	public boolean isOpen;

	QuestHelperPlugin plugin;

	public void setPlugin(QuestHelperPlugin plugin)
	{
		this.plugin = plugin;
	}
	
	public void setPathManager(PathManager pathManager)
	{
		this.pathManager = pathManager;
	}

	public void setup(Client client)
	{
		// Create modal dialog
		currentTopLevelWidget = getTopLevelWidget(client);
		if (currentTopLevelWidget == null) return;

		var widthForHelper = 480;
		var heightForHelper = 326;
		
		// Calculate smart positioning
		var position = calculateDialogPosition(currentTopLevelWidget, widthForHelper, heightForHelper);
		var xForHelper = position.getLeft();
		var yForHelper = position.getRight();

		modalDialog = new ModalDialog(client, currentTopLevelWidget, "Early game helper", widthForHelper, heightForHelper, xForHelper, yForHelper, (ev) -> close(client), (ev) -> saveCurrentPosition());

		// Create tab container
		var tabTitles = new String[]{"Getting started", "First hour", "Money", "Training", "Paths", "Settings"};
		tabContainer = new TabContainer(modalDialog.getContentArea(), tabTitles);
		
		// Set up tab change callback to reset unlock detail view when switching away from Paths tab
		tabContainer.setOnTabChange((tabIndex) -> {
			if (tabIndex != 4 && showingUnlockDetail) { // 4 is the Paths tab index
				showingUnlockDetail = false;
			}
		});

		// Getting started content - button-based layout (3 rows for 6 buttons)
		var gettingStartedText = "Welcome to the Quest Helper's Early Game Guide. The purpose of this guide is to provide you with some routes and tips on what things to start persuing as you start your adventure into "  +
			"the world of Gielinor. The First Hour tab will let you know good things you can do straight away, 'Money' will give you tips on ways to start making some cash to buy stuff, 'Training' will let you know how to get your "
			+ " skills growing, and 'Paths' will let you know of various routes you can take to unlock some of the most impactful improvements to your character.";

		var detailsForGettingStarted = WidgetFactory.createParagraphSection(tabContainer.getTabContent(0), gettingStartedText, 5, 2, 400, 400);
//		ButtonGrid gettingStartedGrid = new ButtonGrid(tabContainer.getTabContent(0), 200, 5, 250, 2, client);
//		gettingStartedGrid.addButton("Banking Guide", SpriteID.AccountIcons._0, (ev) -> openBankingGuide());
//		gettingStartedGrid.addButton("Death Mechanics", SpriteID.AccountIcons._1, (ev) -> openDeathMechanics());
//		gettingStartedGrid.addButton("Home Teleport", SpriteID.AccountIcons._2, (ev) -> openHomeTeleport());
//		gettingStartedGrid.addButton("World Switching", SpriteID.AccountIcons._3, (ev) -> openWorldSwitching());
//		gettingStartedGrid.addButton("Bonds Guide", SpriteID.AccountIcons._4, (ev) -> openBondsGuide());
//		gettingStartedGrid.addButton("Wiki Access", SpriteID.AccountIcons._0, (ev) -> openWikiAccess());

		// First hour: curated buttons (2 rows for 4 buttons)
		var firstHourGrid = new ButtonGrid(tabContainer.getTabContent(1), 0, 0, 2, client);
		firstHourGrid.addButton("Cook's Assistant", SpriteID.OrbIcon._11, (ev) -> openCooksAssistant());
		firstHourGrid.addButton("Lumbridge cows", SpriteID.AccountIcons._0, (ev) -> openLumbridgeCows());
		firstHourGrid.addButton("Mining intro", SpriteID.Staticons.MINING, (ev) -> openMiningIntro());
		firstHourGrid.addButton("Smithing intro", SpriteID.Staticons.SMITHING, (ev) -> openSmithingIntro());

		// Money content - button-based layout (3 rows for 6 buttons)
		var moneyGrid = new ButtonGrid(tabContainer.getTabContent(2), 0, 0, 3, client);
		moneyGrid.addButton("Feather Trading", SpriteID.Staticons.FLETCHING, (ev) -> openFeatherTrading());
		moneyGrid.addButton("Wine of Zamorak", SpriteID.Staticons.THIEVING, (ev) -> openWineOfZamorak());
		moneyGrid.addButton("Stronghold Guide", SpriteID.Staticons.STRENGTH, (ev) -> openStrongholdGuide());
		moneyGrid.addButton("Cowhide Guide", SpriteID.Staticons.ATTACK, (ev) -> openCowhideGuide());
		moneyGrid.addButton("Lobster Fishing", SpriteID.Staticons.FISHING, (ev) -> openLobsterFishing());
		moneyGrid.addButton("Safety Tips", SpriteID.Staticons.DEFENCE, (ev) -> openSafetyTips());

		// Training content - button-based layout (3 rows for 6 buttons)
		var trainingGrid = new ButtonGrid(tabContainer.getTabContent(3), 0, 0, 3, client);
		trainingGrid.addButton("Combat Training", SpriteID.Staticons.ATTACK, (ev) -> openCombatTraining());
		trainingGrid.addButton("Mining Guide", SpriteID.Staticons.MINING, (ev) -> openMiningGuide());
		trainingGrid.addButton("Smithing Guide", SpriteID.Staticons.SMITHING, (ev) -> openSmithingGuide());
		trainingGrid.addButton("Cooking Guide", SpriteID.Staticons.COOKING, (ev) -> openCookingGuide());
		trainingGrid.addButton("Fishing Guide", SpriteID.Staticons.FISHING, (ev) -> openFishingGuide());
		trainingGrid.addButton("Quest Benefits", SpriteID.Staticons.ATTACK, (ev) -> openQuestBenefits());

		// Paths: progression goals
		buildPathsTab(tabContainer.getTabContent(4));

		// Settings: toggle for onboarding prompt
		var settingsLabel = WidgetFactory.createText(
			tabContainer.getTabContent(5),
			"Show early-game prompt on login",
			"ff9933",
			true,
			0, 0, 16, 20
		);
		settingsLabel.setWidthMode(WidgetSizeMode.MINUS);
		settingsLabel.revalidate();

		var toggleBg = WidgetFactory.createGraphic(tabContainer.getTabContent(5), SpriteID.TRADEBACKING, 0, 22, 60, 18);
		toggleBg.setHasListener(true);
		toggleBg.setOnClickListener((JavaScriptCallback) (ev) -> toggleOnboarding());
		toggleBg.revalidate();

		WidgetFactory.createCenteredText(
			tabContainer.getTabContent(5),
			getOnboardingEnabled() ? "On" : "Off",
			"c8aa6e",
			false,
			4, 24, 56, 14
		);

		// F2P-only filter toggle (placeholder)
		var f2pLabel = WidgetFactory.createText(
			tabContainer.getTabContent(5),
			"F2P-only recommendations (placeholder)",
			"ff9933",
			true,
			0, 48, 16, 20
		);
		f2pLabel.setWidthMode(WidgetSizeMode.MINUS);
		f2pLabel.revalidate();

		var ironLabel = WidgetFactory.createText(
			tabContainer.getTabContent(5),
			"Ironman hints (placeholder)",
			"ff9933",
			true,
			0, 66, 16, 20
		);
		ironLabel.setWidthMode(WidgetSizeMode.MINUS);
		ironLabel.revalidate();

		isOpen = true;
	}

	public void show(Client client)
	{
		// Create widgets if they don't exist yet
//		if (modalDialog == null)
//		{
//			setup(client);
//		}
		// TODO: Temporary for testing. Should keep created one usually
		// When reverting, will need to consider swapping between interface types (modern, classic, fixed)
		destroy();
//		System.out.println(isVisible());
		setup(client);
		
		// Make the guide visible
		if (modalDialog != null)
		{
			modalDialog.show();
			isOpen = true;
		}

		setupActivityAdvisorUI();
	}

	private void setupActivityAdvisorUI()
	{
	}

	private boolean getOnboardingEnabled()
	{
		if (plugin == null || plugin.getConfig() == null) return true;
		return plugin.getConfig().showOnboardingPrompt();
	}

	private void toggleOnboarding()
	{
		if (plugin == null || plugin.getConfig() == null || plugin.getConfigManager() == null) return;
		var next = !plugin.getConfig().showOnboardingPrompt();
		plugin.getConfigManager().setConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, "showOnboardingPrompt", next);
	}

	// First hour tab actions
	private void openCooksAssistant()
	{
		if (plugin == null || plugin.getClient() == null) return;
		
		plugin.getClientThread().invokeLater(() -> {
			plugin.displayPanel();
			var q = QuestHelperQuest.getByName("Cook's Assistant");
			if (q != null)
			{
				plugin.getQuestManager().startUpQuest(q, true);
			}
		});
	}
	
	private void openLumbridgeCows()
	{
		if (plugin == null || plugin.getClient() == null) return;
		
		plugin.getClientThread().invokeLater(() -> {
			plugin.displayPanel();
			var q = QuestHelperQuest.getByName("Boaty Guide");
			if (q != null)
			{
				plugin.getQuestManager().startUpQuest(q, true);
			}
		});
	}
	
	private void openMiningIntro()
	{
		if (plugin == null || plugin.getClient() == null) return;
		
		plugin.getClientThread().invokeLater(() -> {
			plugin.displayPanel();
			var q = QuestHelperQuest.getByName("Mining");
			if (q != null)
			{
				plugin.getQuestManager().startUpQuest(q, true);
			}
		});
	}
	
	private void openSmithingIntro()
	{
		if (plugin == null || plugin.getClient() == null) return;
		
		plugin.getClientThread().invokeLater(() -> {
			plugin.displayPanel();
			var q = QuestHelperQuest.getByName("Smithing");
			if (q != null)
			{
				plugin.getQuestManager().startUpQuest(q, true);
			}
		});
	}
	
	// Getting started tab actions
	private void openBankingGuide()
	{
		// TODO: Implement banking guide
		System.out.println("Banking Guide clicked - feature coming soon!");
	}
	
	private void openDeathMechanics()
	{
		// TODO: Implement death mechanics guide
		System.out.println("Death Mechanics clicked - feature coming soon!");
	}
	
	private void openHomeTeleport()
	{
		// TODO: Implement home teleport guide
		System.out.println("Home Teleport clicked - feature coming soon!");
	}
	
	private void openWorldSwitching()
	{
		// TODO: Implement world switching guide
		System.out.println("World Switching clicked - feature coming soon!");
	}
	
	private void openBondsGuide()
	{
		// TODO: Implement bonds guide
		System.out.println("Bonds Guide clicked - feature coming soon!");
	}
	
	private void openWikiAccess()
	{
		// TODO: Open wiki or show wiki info
		System.out.println("Wiki Access clicked - feature coming soon!");
	}
	
	// Money tab actions
	private void openFeatherTrading()
	{
		// TODO: Implement feather trading guide
		System.out.println("Feather Trading clicked - feature coming soon!");
	}
	
	private void openWineOfZamorak()
	{
		// TODO: Implement wine of zamorak guide
		System.out.println("Wine of Zamorak clicked - feature coming soon!");
	}
	
	private void openStrongholdGuide()
	{
		// TODO: Implement stronghold guide
		System.out.println("Stronghold Guide clicked - feature coming soon!");
	}
	
	private void openCowhideGuide()
	{
		// TODO: Implement cowhide guide
		System.out.println("Cowhide Guide clicked - feature coming soon!");
	}
	
	private void openLobsterFishing()
	{
		// TODO: Implement lobster fishing guide
		System.out.println("Lobster Fishing clicked - feature coming soon!");
	}
	
	private void openSafetyTips()
	{
		// TODO: Implement safety tips guide
		System.out.println("Safety Tips clicked - feature coming soon!");
	}
	
	// Training tab actions
	private void openCombatTraining()
	{
		// TODO: Implement combat training guide
		System.out.println("Combat Training clicked - feature coming soon!");
	}
	
	private void openMiningGuide()
	{
		// TODO: Implement mining guide
		System.out.println("Mining Guide clicked - feature coming soon!");
	}
	
	private void openSmithingGuide()
	{
		// TODO: Implement smithing guide
		System.out.println("Smithing Guide clicked - feature coming soon!");
	}
	
	private void openCookingGuide()
	{
		// TODO: Implement cooking guide
		System.out.println("Cooking Guide clicked - feature coming soon!");
	}
	
	private void openFishingGuide()
	{
		// TODO: Implement fishing guide
		System.out.println("Fishing Guide clicked - feature coming soon!");
	}
	
	private void openQuestBenefits()
	{
		// TODO: Implement quest benefits guide
		System.out.println("Quest Benefits clicked - feature coming soon!");
	}


	private void buildPathsTab(Widget pathsContainer)
	{
		if (pathsContainer == null) return;

		// Create the two layers if they don't exist
		if (mainPathsLayer == null)
		{
			createMainPathsLayer(pathsContainer);
		}
		
		if (detailPathsLayer == null)
		{
			createDetailPathsLayer(pathsContainer);
		}
		
		// Set initial visibility based on current state
		updatePathsLayerVisibility();
	}
	
	private void createMainPathsLayer(Widget pathsContainer)
	{
		// Hide any existing main content instance
		if (activeMainContent != null)
		{
			activeMainContent.setHidden(true);
		}

		// Create main paths layer
		mainPathsLayer = WidgetFactory.createLayer(pathsContainer, 0, 0, 0, 0);
		mainPathsLayer.setWidthMode(WidgetSizeMode.MINUS);
		mainPathsLayer.setHeightMode(WidgetSizeMode.MINUS);
		mainPathsLayer.revalidate();

		// Create vertical scrollable container for all categories as a per-build content layer
		mainPathsContainer = new VerticalScrollableContainer(
			mainPathsLayer, 0, 0, 0, 0, plugin.getClient()
		);
		activeMainContent = mainPathsContainer.getContainer();

		// Get unlocks organized by category
		var unlocksByCategory = UnlockRegistry.getUnlocksByCategory();

		var yPos = 0;
		for (UnlockCategory category : UnlockCategory.values())
		{
			var unlocks = unlocksByCategory.get(category);
			if (unlocks == null || unlocks.isEmpty()) continue;

			// Initialize collapse state if not set
			categoryCollapsed.putIfAbsent(category, false);
			var isCollapsed = categoryCollapsed.get(category);

			// Category header with clickable expand/collapse
			var collapseIcon = isCollapsed ? ">" : "^";
			var categoryHeader = WidgetFactory.createText(
				mainPathsContainer.getContentLayer(),
				collapseIcon + " " + category.getDisplayName() + " (" + unlocks.size() + " unlocks)",
				"ff981f",
				true,
				0, yPos, 16, 16
			);
			categoryHeader.setWidthMode(WidgetSizeMode.MINUS);
			categoryHeader.setHasListener(true);
			categoryHeader.setOnOpListener((JavaScriptCallback) (ev) -> toggleCategory(category));
			// Add hover effect to show it's clickable
			categoryHeader.setOnMouseOverListener((JavaScriptCallback) (ev) -> {
				categoryHeader.setTextColor(Integer.parseInt("ffffff", 16));
			});
			categoryHeader.setOnMouseLeaveListener((JavaScriptCallback) (ev) -> {
				categoryHeader.setTextColor(Integer.parseInt("ff981f", 16));
			});
			categoryHeader.revalidate();
			yPos += 18;

			// Unlocks in category (only show if not collapsed)
			if (!isCollapsed)
			{
				// Create row-first button grid with 4 columns
				var unlockGrid = new RowFirstButtonGrid(
					mainPathsContainer.getContentLayer(), 0, yPos, 4, plugin.getClient()
				);

				// Add unlock buttons to the grid
				for (Unlock unlock : unlocks)
				{
					unlockGrid.addButton(
						unlock.getName(),
						unlock.getIconSpriteId(),
						(ev) -> showUnlockDetail(unlock)
					);
				}

				// Use the grid's actual height plus some padding
				yPos += unlockGrid.getHeight() + 10; // 10px padding
			}

			yPos += 12; // Space between categories
		}

		// Update the vertical container's scrollbar
		mainPathsContainer.updateScrollbar();
	}
	
	private void createDetailPathsLayer(Widget pathsContainer)
	{
		// Create detail paths layer (initially hidden)
		detailPathsLayer = WidgetFactory.createLayer(pathsContainer, 0, 0, 0, 0);
		detailPathsLayer.setWidthMode(WidgetSizeMode.MINUS);
		detailPathsLayer.setHeightMode(WidgetSizeMode.MINUS);
		detailPathsLayer.setHidden(true);
		detailPathsLayer.revalidate();
		
		// Create back button (persistent)
		WidgetFactory.createLeftAlignedTextButton(
			detailPathsLayer,
			"Back to Paths",
			8, 4, 120, 16,
			(ev) -> showPathsMainView()
		);
		
		// No shared title/description/quest list; built per-detail
	}
	
	private void updatePathsLayerVisibility()
	{
		if (mainPathsLayer == null || detailPathsLayer == null) return;
		
		// Show main paths, hide detail
		mainPathsLayer.setHidden(showingUnlockDetail);
		detailPathsLayer.setHidden(!showingUnlockDetail);
	}

	private void toggleCategory(UnlockCategory category)
	{
		// Toggle collapse state
		categoryCollapsed.put(category, !categoryCollapsed.get(category));
		
		// Update the category header text to reflect the new state
		updateCategoryHeader(category);
		
		// Update the unlock grid visibility for this category
		updateCategoryUnlockGrid(category);
	}
	
	private void updateCategoryHeader(UnlockCategory category)
	{
		// Rebuild a new main paths content instance and hide previous
		if (tabContainer != null)
		{
			createMainPathsLayer(tabContainer.getTabContent(4));
		}
	}
	
	private void updateCategoryUnlockGrid(UnlockCategory category)
	{
		// This would need to be implemented to show/hide the specific unlock grid
		// For now, we'll rebuild the main paths layer as a fallback
		// In a more sophisticated implementation, we'd track individual category widgets
	}

	private void showUnlockDetail(Unlock unlock)
	{
		if (plugin == null || plugin.getClient() == null || detailPathsLayer == null) return;
		
		// Set state tracking
		showingUnlockDetail = true;
		
		// Hide and orphan previous detail content if exists
		if (activeDetailContent != null)
		{
			activeDetailContent.setHidden(true);
		}
		
		// Create a fresh container for this unlock detail
		var detailContent = WidgetFactory.createLayer(detailPathsLayer, 0, 20, 0, 20);
		detailContent.setWidthMode(WidgetSizeMode.MINUS);
		detailContent.setHeightMode(WidgetSizeMode.MINUS);
		detailContent.revalidate();
		activeDetailContent = detailContent;
		
		// Title
		WidgetFactory.createText(
			detailContent,
			unlock.getName(),
			"ff981f",
			true,
			8, 6, 16, 16
		);
		
		// Description
		WidgetFactory.createText(
			detailContent,
			unlock.getDescription(),
			"c8aa6e",
			true,
			8, 24, 16, 12
		);

		// Add Set Active/Stop Path button in top-right
		addActivePathButton(detailContent, unlock);
		
		// Quest list using QuestList component (per-detail)
		var questList = new QuestList(plugin.getClient(), detailContent);
		for (QuestHelperQuest quest : unlock.getPrerequisiteQuests())
		{
			boolean isCompleted = quest.getState(plugin.getClient()) == net.runelite.api.QuestState.FINISHED;
			boolean isActive = !isCompleted && quest.getState(plugin.getClient()) == net.runelite.api.QuestState.IN_PROGRESS;
			String statusIcon = isCompleted ? "[Done]" : (isActive ? "[Active]" : "[ ]");
			String baseColor = isCompleted ? "00ff00" : (isActive ? "ffff00" : "ff0000");
			questList.addItem(
				statusIcon + " " + quest.getName(),
				"",
				baseColor,
				(ev) -> startQuest(quest)
			);
		}
		questList.refresh();
		
		// Update layer visibility
		updatePathsLayerVisibility();
	}
	
	private void showPathsMainView()
	{
		// Reset state tracking
		showingUnlockDetail = false;
		
		// Hide active detail content if present
		if (activeDetailContent != null)
		{
			activeDetailContent.setHidden(true);
		}
		
		// Update layer visibility to show main paths
		updatePathsLayerVisibility();
	}
	
	private void startQuest(QuestHelperQuest quest)
	{
		if (plugin == null || plugin.getClient() == null) return;
		
		// Start the quest helper
		plugin.getClientThread().invokeLater(() -> {
			plugin.displayPanel();
			plugin.getQuestManager().startUpQuest(quest.getQuestHelper(), true);
		});
	}
	
	/**
	 * Add the Set Active/Stop Path button to the unlock detail view
	 */
	private void addActivePathButton(Widget detailContent, Unlock unlock)
	{
		if (pathManager == null) return;
		
		// Calculate button position (top-right corner)
		int buttonWidth = 120;
		int buttonHeight = 25;
		int buttonX = 400 - buttonWidth - 8; // Right edge minus padding
		int buttonY = 6;
		
		// Create button container
		Widget buttonContainer = WidgetFactory.createLayer(detailContent, buttonX, buttonY, buttonWidth + 8, buttonHeight + 8);
		
		// Create button background with hover effects
		activePathButton = WidgetFactory.createGraphic(buttonContainer, SpriteID.TRADEBACKING, 0, 0, buttonWidth, buttonHeight);
		activePathButton.setOnMouseOverListener((JavaScriptCallback) (ev) -> {
			activePathButton.setSpriteId(SpriteID.TRADEBACKING_DARK);
		});
		activePathButton.setOnMouseLeaveListener((JavaScriptCallback) (ev) -> {
			activePathButton.setSpriteId(SpriteID.TRADEBACKING);
		});
		activePathButton.setHasListener(true);
		activePathButton.setOnClickListener(new JavaScriptCallback() {
			@Override
			public void run(ScriptEvent ev) {
				handleActivePathButtonClick(unlock);
			}
		});
		activePathButton.revalidate();
		
		// Create button border pieces
		createActivePathButtonBorder(buttonContainer, buttonWidth, buttonHeight);
		
		// Create button text
		WidgetFactory.createCenteredText(buttonContainer, getActivePathButtonText(unlock), "c8aa6e", false, 0, 0, buttonWidth, buttonHeight);
	}
	
	/**
	 * Create button border pieces for the active path button
	 */
	private void createActivePathButtonBorder(Widget buttonContainer, int buttonWidth, int buttonHeight)
	{
		int buttonPadding = 0;
		int buttonEdgeSize = 4;
		
		// Corner pieces
		WidgetFactory.createGraphic(buttonContainer, SpriteID.V2StoneButton._0, 0, 0, buttonEdgeSize, buttonEdgeSize); // Top-left
		WidgetFactory.createGraphic(buttonContainer, SpriteID.V2StoneButton._1, buttonWidth - buttonEdgeSize - 1, 0, buttonEdgeSize, buttonEdgeSize); // Top-right
		WidgetFactory.createGraphic(buttonContainer, SpriteID.V2StoneButton._2, 0, buttonHeight - buttonEdgeSize, buttonEdgeSize, buttonEdgeSize); // Bottom-left
		WidgetFactory.createGraphic(buttonContainer, SpriteID.V2StoneButton._3, buttonWidth - buttonEdgeSize, buttonHeight - buttonEdgeSize, buttonEdgeSize, buttonEdgeSize); // Bottom-right

		// Edge pieces
		WidgetFactory.createGraphic(buttonContainer, SpriteID.V2StoneButton._4, 0, buttonEdgeSize, buttonEdgeSize, buttonHeight - (buttonEdgeSize * 2)); // Left
		WidgetFactory.createGraphic(buttonContainer, SpriteID.V2StoneButton._5, buttonEdgeSize, 0, buttonWidth - (buttonEdgeSize * 2), buttonEdgeSize); // Top
		WidgetFactory.createGraphic(buttonContainer, SpriteID.V2StoneButton._6, buttonWidth - buttonEdgeSize, buttonEdgeSize, buttonEdgeSize, buttonHeight - (buttonEdgeSize * 2)); // Right
		WidgetFactory.createGraphic(buttonContainer, SpriteID.V2StoneButton._7, buttonEdgeSize + buttonPadding, buttonHeight - buttonEdgeSize, buttonWidth - (buttonEdgeSize * 2), buttonEdgeSize); // Bottom
	}
	
	/**
	 * Get the appropriate button text based on current path state
	 */
	private String getActivePathButtonText(Unlock unlock)
	{
		if (pathManager == null) return "Set Active";
		
		if (pathManager.getActivePath() != null && pathManager.getActivePath().getId().equals(unlock.getId()))
		{
			if (pathManager.isPathPaused())
			{
				return "Resume";
			}
			else
			{
				return "Stop Path";
			}
		}
		else
		{
			return "Set Active";
		}
	}
	
	/**
	 * Handle the active path button click
	 */
	private void handleActivePathButtonClick(Unlock unlock)
	{
		if (pathManager == null) return;
		
		if (pathManager.getActivePath() != null && pathManager.getActivePath().getId().equals(unlock.getId()))
		{
			// This path is currently active
			if (pathManager.isPathPaused())
			{
				// Resume the path
				pathManager.resumeActivePath();
			}
			else
			{
				// Stop the path
				pathManager.stopActivePath();
			}
		}
		else
		{
			// Set this path as active
			pathManager.setActivePath(unlock.getId());
		}
		
		// Update button text
		updateActivePathButton(unlock);
		
		// Update sidebar banner if panel is available
		if (plugin != null && plugin.getPanel() != null)
		{
			plugin.getPanel().getQuestOverviewPanel().updatePathBanner();
		}
	}
	
	/**
	 * Update the active path button text
	 */
	private void updateActivePathButton(Unlock unlock)
	{
		if (activePathButton == null) return;
		
		// Find the text child widget and update its text
		// Note: This is a simplified approach - in practice you might need to recreate the button
		// For now, we'll recreate the button with new text
		Widget parent = activePathButton.getParent();
		int x = activePathButton.getOriginalX();
		int y = activePathButton.getOriginalY();
		int width = activePathButton.getWidth();
		int height = activePathButton.getHeight();
		
		// Remove old button
		activePathButton.setHidden(true);
		
		// Create new button with updated text
		activePathButton = WidgetFactory.createLeftAlignedTextButton(
			parent,
			getActivePathButtonText(unlock),
			x, y, width, height,
			(ev) -> handleActivePathButtonClick(unlock)
		);
	}
	
	/**
	 * Open the guide to a specific path
	 * Called from sidebar banner click
	 */
	public void openToPath(Client client, String unlockId)
	{
		// Find the unlock by ID
		Unlock unlock = null;
		for (UnlockCategory category : UnlockCategory.values())
		{
			var unlocksByCategory = UnlockRegistry.getUnlocksByCategory();
			var unlocks = unlocksByCategory.get(category);
			if (unlocks != null)
			{
				for (Unlock u : unlocks)
				{
					if (u.getId().equals(unlockId))
					{
						unlock = u;
						break;
					}
				}
			}
			if (unlock != null) break;
		}
		
		if (unlock == null)
		{
			System.out.println("Unlock not found: " + unlockId);
			return;
		}
		
		// Show the guide and switch to the specific path
		show(client);
		
		// Switch to Paths tab (index 4)
		if (tabContainer != null)
		{
			// Switch to Paths tab (index 4)
			tabContainer.setActiveTab(4);
		}
		
		// Show the unlock detail
		showUnlockDetail(unlock);
	}


	/**
	 * Calculate smart positioning for the dialog
	 * @param parentWidget The parent container widget
	 * @param dialogWidth The width of the dialog
	 * @param dialogHeight The height of the dialog
	 * @return Array with [x, y] coordinates
	 */
	private Pair<Integer, Integer> calculateDialogPosition(Widget parentWidget, int dialogWidth, int dialogHeight)
	{
		var parentWidth = parentWidget.getWidth();
		var parentHeight = parentWidget.getHeight();
		
		int x, y;
		
		if (savedX == null || savedY == null)
		{
			// First time: center the dialog perfectly
			x = (parentWidth - dialogWidth) / 2;
			y = (parentHeight - dialogHeight) / 2;
		}
		else
		{
			// Use saved position
			x = savedX;
			y = savedY;
		}
		
		// Ensure dialog stays within parent bounds
		x = Math.max(0, Math.min(x, parentWidth - dialogWidth));
		y = Math.max(0, Math.min(y, parentHeight - dialogHeight));
		
		// Save the final position for next time
		savedX = x;
		savedY = y;
		
		return Pair.of(savedX, savedY);
	}

	public Widget getTopLevelWidget(Client client)
	{
		// Resizable classic
		var classicContainer = client.getWidget(InterfaceID.ToplevelOsrsStretch.HUD_CONTAINER_FRONT);
		if (classicContainer != null && !classicContainer.isHidden())
		{
			return classicContainer;
		}

		// Resizable modern
		var modernContainer = client.getWidget(InterfaceID.ToplevelPreEoc.HUD_CONTAINER_FRONT);
		if (modernContainer != null && !modernContainer.isHidden())
		{
			return modernContainer;
		}

		// Fixed
		var fixedContainer = client.getWidget(InterfaceID.Toplevel.MAIN);
		return fixedContainer;
	}

	public void close(Client client)
	{
		if (modalDialog == null) return;

		// Just hide the widget, don't destroy it
		modalDialog.hide();
		isOpen = false;
	}

	public void destroy()
	{
		if (modalDialog == null) return;

		// Save current position before destroying
		saveCurrentPosition();

		// Close and destroy the modal
		modalDialog.close();
		modalDialog = null;
		tabContainer = null;
		categoryCollapsed.clear();
		
		// Reset unlock detail state and layer references
		showingUnlockDetail = false;
		mainPathsLayer = null;
		detailPathsLayer = null;
		mainPathsContainer = null;
		activeMainContent = null;
		activeDetailContent = null;
		
		isOpen = false;
	}
	
	/**
	 * Save the current position of the modal dialog
	 */
	private void saveCurrentPosition()
	{
		if (modalDialog != null && modalDialog.getModalWidget() != null)
		{
			savedX = modalDialog.getModalWidget().getOriginalX();
			savedY = modalDialog.getModalWidget().getOriginalY();
		}
	}

	/**
	 * Check if the dialog is currently open and visible
	 */
	public boolean isVisible()
	{
		return modalDialog != null && modalDialog.getModalWidget() != null && !modalDialog.getModalWidget().isHidden();
	}
	
	/**
	 * Check if the dialog position needs adjustment due to container size changes
	 * @param client The client to get the current container
	 * @return true if position needs adjustment
	 */
	public boolean needsPositionAdjustment(Client client)
	{
		if (modalDialog == null || modalDialog.getModalWidget() == null) return false;
		
		var parentWidget = getTopLevelWidget(client);
		if (parentWidget == null) return false;
		
		var modalWidget = modalDialog.getModalWidget();
		var currentX = modalWidget.getOriginalX();
		var currentY = modalWidget.getOriginalY();
		var dialogWidth = modalWidget.getWidth();
		var dialogHeight = modalWidget.getHeight();
		var parentWidth = parentWidget.getWidth();
		var parentHeight = parentWidget.getHeight();
		
		// Check if dialog is outside bounds
		return currentX < 0 || currentY < 0 || 
		       currentX + dialogWidth > parentWidth || 
		       currentY + dialogHeight > parentHeight;
	}
	
	/**
	 * Adjust the dialog position to stay within bounds without recreating it
	 * @param client The client to get the current container
	 */
	public void adjustPosition(Client client)
	{
		if (modalDialog == null || modalDialog.getModalWidget() == null) return;
		
		Widget parentWidget = getTopLevelWidget(client);
		if (parentWidget == null) return;
		
		Widget modalWidget = modalDialog.getModalWidget();
		var dialogWidth = modalWidget.getWidth();
		var dialogHeight = modalWidget.getHeight();
		
		// Calculate adjusted position
		var position = calculateDialogPosition(parentWidget, dialogWidth, dialogHeight);
		var newX = position.getLeft();
		var newY = position.getRight();
		
		// Update position
		modalDialog.setPos(newX, newY);
		
		// Also update the dragger position if it exists
		// Note: We'd need to expose the dragger from ModalDialog to update it too
		// For now, the position will be corrected on next drag
	}
}
