package com.questhelper.helpers.guides;

import com.questhelper.QuestHelperConfig;
import com.questhelper.QuestHelperPlugin;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.ui.widgets.ModalDialog;
import com.questhelper.ui.widgets.TabContainer;
import com.questhelper.ui.widgets.ButtonGrid;
import com.questhelper.ui.widgets.WidgetFactory;
import net.runelite.api.Client;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.SpriteID;
import net.runelite.api.widgets.JavaScriptCallback;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetPositionMode;
import net.runelite.api.widgets.WidgetSizeMode;
import javax.inject.Singleton;

@Singleton
public class EarlyGameGuide
{
	private ModalDialog modalDialog;
	private TabContainer tabContainer;
	
	// Category collapse state tracking
	java.util.Map<GoalCategory, Boolean> categoryCollapsed = new java.util.HashMap<>();
	
	// Position memory for dialog placement
	private Integer savedX = null;
	private Integer savedY = null;
	private boolean isFirstTime = true;
	public boolean isOpen;

	QuestHelperPlugin plugin;

	public void setPlugin(QuestHelperPlugin plugin)
	{
		this.plugin = plugin;
	}

	public void setup(Client client)
	{
		// Create modal dialog
		Widget parentWidget = getTopLevelWidget(client);
		if (parentWidget == null) return;

		var widthForHelper = 480;
		var heightForHelper = 326;
		
		// Calculate smart positioning
		int[] position = calculateDialogPosition(parentWidget, widthForHelper, heightForHelper);
		var xForHelper = position[0];
		var yForHelper = position[1];

		modalDialog = new ModalDialog(client, parentWidget, "Early game helper", widthForHelper, heightForHelper, xForHelper, yForHelper, (ev) -> close(client), (ev) -> saveCurrentPosition());

		// Create tab container
		String[] tabTitles = new String[]{"Getting started", "First hour", "Money", "Training", "Paths", "Settings"};
		tabContainer = new TabContainer(modalDialog.getContentArea(), tabTitles);

		// Getting started content - button-based layout (3 rows for 6 buttons)
		ButtonGrid gettingStartedGrid = new ButtonGrid(tabContainer.getTabContent(0), 0, 0, 300, 2, client);
		gettingStartedGrid.addButton("Banking Guide", SpriteID.AccountIcons._0, (ev) -> openAction("Banking Guide"));
		gettingStartedGrid.addButton("Death Mechanics", SpriteID.AccountIcons._1, (ev) -> openAction("Death Mechanics"));
		gettingStartedGrid.addButton("Home Teleport", SpriteID.AccountIcons._2, (ev) -> openAction("Home Teleport"));
		gettingStartedGrid.addButton("World Switching", SpriteID.AccountIcons._3, (ev) -> openAction("World Switching"));
		gettingStartedGrid.addButton("Bonds Guide", SpriteID.AccountIcons._4, (ev) -> openAction("Bonds Guide"));
		gettingStartedGrid.addButton("Wiki Access", SpriteID.AccountIcons._0, (ev) -> openAction("Wiki Access"));

		// First hour: curated buttons (2 rows for 4 buttons)
		ButtonGrid firstHourGrid = new ButtonGrid(tabContainer.getTabContent(1), 0, 0, 2, client);
		firstHourGrid.addButton("Cook's Assistant", SpriteID.OrbIcon._11, (ev) -> openAction("Cook's Assistant"));
		firstHourGrid.addButton("Lumbridge cows", SpriteID.AccountIcons._0, (ev) -> openAction("Lumbridge cows"));
		firstHourGrid.addButton("Mining intro", SpriteID.Staticons.MINING, (ev) -> openAction("Mining intro"));
		firstHourGrid.addButton("Smithing intro", SpriteID.Staticons.SMITHING, (ev) -> openAction("Smithing intro"));

		// Money content - button-based layout (3 rows for 6 buttons)
		ButtonGrid moneyGrid = new ButtonGrid(tabContainer.getTabContent(2), 0, 0, 3, client);
		moneyGrid.addButton("Feather Trading", SpriteID.Staticons.FLETCHING, (ev) -> openAction("Feather Trading"));
		moneyGrid.addButton("Wine of Zamorak", SpriteID.Staticons.THIEVING, (ev) -> openAction("Wine of Zamorak"));
		moneyGrid.addButton("Stronghold Guide", SpriteID.Staticons.STRENGTH, (ev) -> openAction("Stronghold Guide"));
		moneyGrid.addButton("Cowhide Guide", SpriteID.Staticons.ATTACK, (ev) -> openAction("Cowhide Guide"));
		moneyGrid.addButton("Lobster Fishing", SpriteID.Staticons.FISHING, (ev) -> openAction("Lobster Fishing"));
		moneyGrid.addButton("Safety Tips", SpriteID.Staticons.DEFENCE, (ev) -> openAction("Safety Tips"));
		moneyGrid.addButton("Feather Trading", SpriteID.Staticons.FLETCHING, (ev) -> openAction("Feather Trading"));
		moneyGrid.addButton("Wine of Zamorak", SpriteID.Staticons.THIEVING, (ev) -> openAction("Wine of Zamorak"));
		moneyGrid.addButton("Stronghold Guide", SpriteID.Staticons.STRENGTH, (ev) -> openAction("Stronghold Guide"));
		moneyGrid.addButton("Cowhide Guide", SpriteID.Staticons.ATTACK, (ev) -> openAction("Cowhide Guide"));
		moneyGrid.addButton("Lobster Fishing", SpriteID.Staticons.FISHING, (ev) -> openAction("Lobster Fishing"));
		moneyGrid.addButton("Safety Tips", SpriteID.Staticons.DEFENCE, (ev) -> openAction("Safety Tips"));
		moneyGrid.addButton("Safety Tips", SpriteID.Staticons.DEFENCE, (ev) -> openAction("Safety Tips"));
		moneyGrid.addButton("Feather Trading", SpriteID.Staticons.FLETCHING, (ev) -> openAction("Feather Trading"));
		moneyGrid.addButton("Wine of Zamorak", SpriteID.Staticons.THIEVING, (ev) -> openAction("Wine of Zamorak"));
		moneyGrid.addButton("Stronghold Guide", SpriteID.Staticons.STRENGTH, (ev) -> openAction("Stronghold Guide"));
		moneyGrid.addButton("Cowhide Guide", SpriteID.Staticons.ATTACK, (ev) -> openAction("Cowhide Guide"));
		moneyGrid.addButton("Lobster Fishing", SpriteID.Staticons.FISHING, (ev) -> openAction("Lobster Fishing"));
		moneyGrid.addButton("Safety Tips", SpriteID.Staticons.DEFENCE, (ev) -> openAction("Safety Tips"));

		// Training content - button-based layout (3 rows for 6 buttons)
		ButtonGrid trainingGrid = new ButtonGrid(tabContainer.getTabContent(3), 0, 0, 3, client);
		trainingGrid.addButton("Combat Training", SpriteID.Staticons.ATTACK, (ev) -> openAction("Combat Training"));
		trainingGrid.addButton("Mining Guide", SpriteID.Staticons.MINING, (ev) -> openAction("Mining Guide"));
		trainingGrid.addButton("Smithing Guide", SpriteID.Staticons.SMITHING, (ev) -> openAction("Smithing Guide"));
		trainingGrid.addButton("Cooking Guide", SpriteID.Staticons.COOKING, (ev) -> openAction("Cooking Guide"));
		trainingGrid.addButton("Fishing Guide", SpriteID.Staticons.FISHING, (ev) -> openAction("Fishing Guide"));
		trainingGrid.addButton("Quest Benefits", SpriteID.Staticons.ATTACK, (ev) -> openAction("Quest Benefits"));

		// Paths: progression goals
		buildPathsTab(tabContainer.getTabContent(4));

		// Settings: toggle for onboarding prompt
		Widget settingsLabel = WidgetFactory.createText(
			tabContainer.getTabContent(5),
			"Show early-game prompt on login",
			"ff9933",
			true,
			0, 0, 16, 20
		);
		settingsLabel.setWidthMode(WidgetSizeMode.MINUS);
		settingsLabel.revalidate();

		Widget toggleBg = WidgetFactory.createGraphic(tabContainer.getTabContent(5), SpriteID.TRADEBACKING, 0, 22, 60, 18);
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
		Widget f2pLabel = WidgetFactory.createText(
			tabContainer.getTabContent(5),
			"F2P-only recommendations (placeholder)",
			"ff9933",
			true,
			0, 48, 16, 20
		);
		f2pLabel.setWidthMode(WidgetSizeMode.MINUS);
		f2pLabel.revalidate();

		Widget ironLabel = WidgetFactory.createText(
			tabContainer.getTabContent(5),
			"Ironman hints (placeholder)",
			"ff9933",
			true,
			0, 66, 16, 20
		);
		ironLabel.setWidthMode(WidgetSizeMode.MINUS);
		ironLabel.revalidate();
	}

	public void show(Client client)
	{
		// Create widgets if they don't exist yet
		if (modalDialog == null)
		{
//			setup(client);
		}
		// TODO: Temporary for testing. Should keep created one usually
		// When reverting, will need to consider swapping between interface types (modern, classic, fixed)
		destroy();
		setup(client);
		
		// Make the guide visible
		if (modalDialog != null)
		{
			modalDialog.show();
			isOpen = true;
		}
	}

	private boolean getOnboardingEnabled()
	{
		if (plugin == null || plugin.getConfig() == null) return true;
		return plugin.getConfig().showOnboardingPrompt();
	}

	private void toggleOnboarding()
	{
		if (plugin == null || plugin.getConfig() == null || plugin.getConfigManager() == null) return;
		boolean next = !plugin.getConfig().showOnboardingPrompt();
		plugin.getConfigManager().setConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, "showOnboardingPrompt", next);
	}

	private void openAction(String label)
	{
		if (plugin == null || plugin.getClient() == null) return;
		
		// First hour tab actions
		if ("Cook's Assistant".equals(label))
		{
			plugin.getClientThread().invokeLater(() -> {
				plugin.displayPanel();
				QuestHelper q = QuestHelperQuest.getByName("Cook's Assistant");
				if (q != null)
				{
					plugin.getQuestManager().startUpQuest(q, true);
				}
			});
		}
		else if ("Lumbridge cows".equals(label))
		{
			plugin.getClientThread().invokeLater(() -> {
				plugin.displayPanel();
				QuestHelper q = QuestHelperQuest.getByName("Boaty Guide");
				if (q != null)
				{
					plugin.getQuestManager().startUpQuest(q, true);
				}
			});
		}
		else if ("Mining intro".equals(label))
		{
			plugin.getClientThread().invokeLater(() -> {
				plugin.displayPanel();
				QuestHelper q = QuestHelperQuest.getByName("Mining");
				if (q != null)
				{
					plugin.getQuestManager().startUpQuest(q, true);
				}
			});
		}
		else if ("Smithing intro".equals(label))
		{
			plugin.getClientThread().invokeLater(() -> {
				plugin.displayPanel();
				QuestHelper q = QuestHelperQuest.getByName("Smithing");
				if (q != null)
				{
					plugin.getQuestManager().startUpQuest(q, true);
				}
			});
		}
		
		// Getting started tab actions
		else if ("Banking Guide".equals(label))
		{
			// TODO: Implement banking guide
			System.out.println("Banking Guide clicked - feature coming soon!");
		}
		else if ("Death Mechanics".equals(label))
		{
			// TODO: Implement death mechanics guide
			System.out.println("Death Mechanics clicked - feature coming soon!");
		}
		else if ("Home Teleport".equals(label))
		{
			// TODO: Implement home teleport guide
			System.out.println("Home Teleport clicked - feature coming soon!");
		}
		else if ("World Switching".equals(label))
		{
			// TODO: Implement world switching guide
			System.out.println("World Switching clicked - feature coming soon!");
		}
		else if ("Bonds Guide".equals(label))
		{
			// TODO: Implement bonds guide
			System.out.println("Bonds Guide clicked - feature coming soon!");
		}
		else if ("Wiki Access".equals(label))
		{
			// TODO: Open wiki or show wiki info
			System.out.println("Wiki Access clicked - feature coming soon!");
		}
		
		// Money tab actions
		else if ("Feather Trading".equals(label))
		{
			// TODO: Implement feather trading guide
			System.out.println("Feather Trading clicked - feature coming soon!");
		}
		else if ("Wine of Zamorak".equals(label))
		{
			// TODO: Implement wine of zamorak guide
			System.out.println("Wine of Zamorak clicked - feature coming soon!");
		}
		else if ("Stronghold Guide".equals(label))
		{
			// TODO: Implement stronghold guide
			System.out.println("Stronghold Guide clicked - feature coming soon!");
		}
		else if ("Cowhide Guide".equals(label))
		{
			// TODO: Implement cowhide guide
			System.out.println("Cowhide Guide clicked - feature coming soon!");
		}
		else if ("Lobster Fishing".equals(label))
		{
			// TODO: Implement lobster fishing guide
			System.out.println("Lobster Fishing clicked - feature coming soon!");
		}
		else if ("Safety Tips".equals(label))
		{
			// TODO: Implement safety tips guide
			System.out.println("Safety Tips clicked - feature coming soon!");
		}
		
		// Training tab actions
		else if ("Combat Training".equals(label))
		{
			// TODO: Implement combat training guide
			System.out.println("Combat Training clicked - feature coming soon!");
		}
		else if ("Mining Guide".equals(label))
		{
			// TODO: Implement mining guide
			System.out.println("Mining Guide clicked - feature coming soon!");
		}
		else if ("Smithing Guide".equals(label))
		{
			// TODO: Implement smithing guide
			System.out.println("Smithing Guide clicked - feature coming soon!");
		}
		else if ("Cooking Guide".equals(label))
		{
			// TODO: Implement cooking guide
			System.out.println("Cooking Guide clicked - feature coming soon!");
		}
		else if ("Fishing Guide".equals(label))
		{
			// TODO: Implement fishing guide
			System.out.println("Fishing Guide clicked - feature coming soon!");
		}
		else if ("Quest Benefits".equals(label))
		{
			// TODO: Implement quest benefits guide
			System.out.println("Quest Benefits clicked - feature coming soon!");
		}
	}


	private void buildPathsTab(Widget pathsContainer)
	{
		if (pathsContainer == null) return;

		// Create scrollable content for goals
		Widget scrollableContent = WidgetFactory.createScrollableContent(pathsContainer, 16, 96);

		int yPos = 0;
		var goalsByCategory = ProgressionGoals.getGoalsByCategory();
		String activeGoalId = plugin != null && plugin.getConfigManager() != null ? 
			plugin.getConfigManager().getRSProfileConfiguration(QuestHelperConfig.QUEST_BACKGROUND_GROUP, "activeProgressionGoalId") : "";

		for (GoalCategory category : GoalCategory.values())
		{
			var goals = goalsByCategory.get(category);
			if (goals == null || goals.isEmpty()) continue;

			// Initialize collapse state if not set
			categoryCollapsed.putIfAbsent(category, false);
			boolean isCollapsed = categoryCollapsed.get(category);

			// Category header with clickable expand/collapse
			String collapseIcon = isCollapsed ? "▶" : "▼";
			Widget categoryHeader = WidgetFactory.createText(
				scrollableContent,
				collapseIcon + " " + category.getDisplayName() + " (" + goals.size() + " goals)",
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

			// Goals in category (only show if not collapsed)
			if (!isCollapsed)
			{
				for (ProgressionGoal goal : goals)
				{
					// Check if this is the active goal
					boolean isActiveGoal = goal.getId().equals(activeGoalId);
					
					// Goal name and difficulty stars
					String stars = "★".repeat(goal.getDifficulty().getStars());
					String progressText = "";
					if (plugin != null && plugin.getClient() != null)
					{
						int completed = goal.getCompletedCount(plugin.getClient());
						int total = goal.getTotalCount();
						progressText = " (" + completed + "/" + total + ")";
					}
					
					Widget goalName = WidgetFactory.createText(
						scrollableContent,
						goal.getName() + " " + stars + progressText,
						isActiveGoal ? "00ff00" : "ff9933",
						true,
						4, yPos, 12, 12
					);
					goalName.setWidthMode(WidgetSizeMode.MINUS);
					goalName.revalidate();

					// Set as Goal button (only show if not completed)
					boolean isCompleted = plugin != null && plugin.getClient() != null && goal.isCompleted(plugin.getClient());
					if (!isCompleted)
					{
						WidgetFactory.createTextButton(
							scrollableContent,
							isActiveGoal ? "Active" : "Set",
							0, yPos, 40, 12,
							(ev) -> setGoal(goal)
						);
					}
					else
					{
						// Show completed indicator
						Widget completedText = WidgetFactory.createCenteredText(
							scrollableContent,
							"✓ Complete",
							"00ff00",
							false,
							0, yPos + 1, 40, 10
						);
						completedText.setXPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
						completedText.revalidate();
					}

					yPos += 16; // Increased spacing to prevent overlap

					// Benefit text
					Widget benefitText = WidgetFactory.createText(
						scrollableContent,
						goal.getBenefit(),
						"c8aa6e",
						true,
						4, yPos, 16, 12
					);
					benefitText.setWidthMode(WidgetSizeMode.MINUS);
					benefitText.revalidate();

					yPos += 18; // Increased spacing for better separation

					// Clickable prerequisite chain view
					if (goal.getPrerequisites().size() > 1)
					{
						Widget prereqButton = WidgetFactory.createText(
							scrollableContent,
							"View prerequisites (" + goal.getPrerequisites().size() + " quests)",
							"808080",
							true,
							4, yPos, 16, 12
						);
						prereqButton.setWidthMode(WidgetSizeMode.MINUS);
						prereqButton.setHasListener(true);
						prereqButton.setOnOpListener((JavaScriptCallback) (ev) -> showPrerequisites(goal));
						// Add hover effect to show it's clickable
						prereqButton.setOnMouseOverListener((JavaScriptCallback) (ev) -> {
							prereqButton.setTextColor(Integer.parseInt("ffffff", 16));
						});
						prereqButton.setOnMouseLeaveListener((JavaScriptCallback) (ev) -> {
							prereqButton.setTextColor(Integer.parseInt("808080", 16));
						});
						prereqButton.revalidate();
						yPos += 16; // Increased spacing
					}
				}
			}

			yPos += 12; // Increased space between categories
		}

		// Set scroll height
		scrollableContent.setScrollHeight(yPos);
	}

	private void toggleCategory(GoalCategory category)
	{
		// Toggle collapse state
		categoryCollapsed.put(category, !categoryCollapsed.get(category));
		
		// Rebuild the Paths tab to reflect the change
		if (tabContainer != null)
		{
			buildPathsTab(tabContainer.getTabContent(4));
		}
	}

	private void showPrerequisites(ProgressionGoal goal)
	{
		if (plugin == null || plugin.getClient() == null) return;
		
		// Create a simple dialog showing the prerequisite chain
		StringBuilder sb = new StringBuilder();
		sb.append("Prerequisites for ").append(goal.getName()).append(":\n\n");
		
		for (int i = 0; i < goal.getPrerequisites().size(); i++)
		{
			QuestHelperQuest quest = goal.getPrerequisites().get(i);
			boolean isCompleted = quest.getState(plugin.getClient()) == net.runelite.api.QuestState.FINISHED;
			String status = isCompleted ? "✓" : "○";
			sb.append(status).append(" ").append(quest.getName()).append("\n");
		}
		
		// For now, just print to console - in a real implementation, you'd show a proper dialog
		System.out.println(sb.toString());
	}

	private void setGoal(ProgressionGoal goal)
	{
		if (plugin == null || plugin.getClient() == null) return;

		// Find next incomplete quest
		QuestHelper nextHelper = goal.getNextIncompleteQuestHelper(plugin.getClient());
		if (nextHelper == null)
		{
			// Goal is complete
			return;
		}

		// Set as active goal
		plugin.getConfigManager().setRSProfileConfiguration(QuestHelperConfig.QUEST_BACKGROUND_GROUP, "activeProgressionGoalId", goal.getId());

		// Start the quest helper
		plugin.getClientThread().invokeLater(() -> {
			plugin.displayPanel();
			plugin.getQuestManager().startUpQuest(nextHelper, true);
		});
		
		// Rebuild the Paths tab to show the new active goal
		if (tabContainer != null)
		{
			buildPathsTab(tabContainer.getTabContent(4));
		}
	}


	/**
	 * Calculate smart positioning for the dialog
	 * @param parentWidget The parent container widget
	 * @param dialogWidth The width of the dialog
	 * @param dialogHeight The height of the dialog
	 * @return Array with [x, y] coordinates
	 */
	private int[] calculateDialogPosition(Widget parentWidget, int dialogWidth, int dialogHeight)
	{
		int parentWidth = parentWidget.getWidth();
		int parentHeight = parentWidget.getHeight();
		
		int x, y;
		
		if (isFirstTime || savedX == null || savedY == null)
		{
			// First time: center the dialog perfectly
			x = (parentWidth - dialogWidth) / 2;
			y = (parentHeight - dialogHeight) / 2;
			isFirstTime = false;
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
		
		return new int[]{x, y};
	}

	private Widget getTopLevelWidget(Client client)
	{
		// Resizable classic
		Widget classicContainer = client.getWidget(InterfaceID.ToplevelOsrsStretch.HUD_CONTAINER_FRONT);
		if (classicContainer != null && !classicContainer.isHidden())
		{
			return classicContainer;
		}
		// Resizable modern
		Widget modernContainer = client.getWidget(InterfaceID.ToplevelPreEoc.HUD_CONTAINER_FRONT);
		if (modernContainer != null && !modernContainer.isHidden())
		{
			return modernContainer;
		}

		// Fixed
		Widget fixedContainer = client.getWidget(InterfaceID.Toplevel.MAIN);
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
	 * Reset position memory to force centering on next creation
	 */
	public void resetPosition()
	{
		savedX = null;
		savedY = null;
		isFirstTime = true;
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
		
		Widget parentWidget = getTopLevelWidget(client);
		if (parentWidget == null) return false;
		
		Widget modalWidget = modalDialog.getModalWidget();
		int currentX = modalWidget.getOriginalX();
		int currentY = modalWidget.getOriginalY();
		int dialogWidth = modalWidget.getWidth();
		int dialogHeight = modalWidget.getHeight();
		int parentWidth = parentWidget.getWidth();
		int parentHeight = parentWidget.getHeight();
		
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
		int dialogWidth = modalWidget.getWidth();
		int dialogHeight = modalWidget.getHeight();
		
		// Calculate adjusted position
		int[] position = calculateDialogPosition(parentWidget, dialogWidth, dialogHeight);
		int newX = position[0];
		int newY = position[1];
		
		// Update position
		modalWidget.setPos(newX, newY);
		modalWidget.revalidate();
		
		// Also update the dragger position if it exists
		// Note: We'd need to expose the dragger from ModalDialog to update it too
		// For now, the position will be corrected on next drag
	}
}
