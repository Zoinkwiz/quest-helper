package com.questhelper.helpers.guides;

import com.questhelper.QuestHelperConfig;
import com.questhelper.QuestHelperPlugin;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.helpers.guides.ProgressionGoal;
import com.questhelper.helpers.guides.GoalCategory;
import com.questhelper.helpers.guides.ProgressionGoals;
import com.questhelper.ui.widgets.SimpleWidgetBuilder;
import com.questhelper.ui.widgets.ButtonSection;
import net.runelite.api.Client;
import net.runelite.api.FontID;
import net.runelite.api.KeyCode;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.SpriteID;
import net.runelite.api.widgets.JavaScriptCallback;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetPositionMode;
import net.runelite.api.widgets.WidgetSizeMode;
import net.runelite.api.widgets.WidgetTextAlignment;
import net.runelite.api.widgets.WidgetType;
import javax.inject.Singleton;

@Singleton
public class EarlyGameGuide
{
	Widget babyWidget;
	Widget contentContainer;
	Widget[] tabContents;
	Widget[] tabHeaders;
	int selectedTabIndex = 0;
	
	// Category collapse state tracking
	java.util.Map<GoalCategory, Boolean> categoryCollapsed = new java.util.HashMap<>();

	QuestHelperPlugin plugin;

	public void setPlugin(QuestHelperPlugin plugin)
	{
		this.plugin = plugin;
	}


	public void setup(Client client)
	{
		close(client);
		Widget parentWidget = getTopLevelWidget(client);
		if (parentWidget == null) return;

		// Create main modal container
		Widget topLevelWidget = SimpleWidgetBuilder.createLayer(parentWidget, 0, 0, 480, 326);
		topLevelWidget.setPos(0, 0, WidgetPositionMode.ABSOLUTE_CENTER, WidgetPositionMode.ABSOLUTE_CENTER);
		topLevelWidget.revalidate();

		// Create modal background with borders
		SimpleWidgetBuilder.createModalBackground(topLevelWidget, 480, 326);

		// Create title
		SimpleWidgetBuilder.createTitle(topLevelWidget, "Early game helper");

		// Title separator border
		Widget titleSeparator = SimpleWidgetBuilder.createGraphicAbsolute(topLevelWidget, SpriteID.SteelborderDivider._0, 0, 20, 10, 26);
		titleSeparator.setXPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		titleSeparator.setWidthMode(WidgetSizeMode.MINUS);
		titleSeparator.revalidate();

		// Create close button
		SimpleWidgetBuilder.createCloseButton(topLevelWidget, (ev) -> close(client));
		
		// Add ESC key handling to the entire modal
		topLevelWidget.setOnKeyListener((JavaScriptCallback) (ev) -> {
			if (client.isKeyPressed(KeyCode.KC_ESCAPE))
			{
				close(client);
			}
		});

		// Tabs header row
		String[] tabTitles = new String[]{"Getting started", "First hour", "Money", "Training", "Paths", "Settings"};
		tabHeaders = new Widget[tabTitles.length];
		int headerX = 12;
		for (int i = 0; i < tabTitles.length; i++)
		{
			final int idx = i;
			tabHeaders[i] = SimpleWidgetBuilder.createTabHeader(
				topLevelWidget, 
				tabTitles[i], 
				headerX, 
				36, 
				i == 0, 
				(ev) -> selectTab(idx)
			);
			headerX += tabTitles[i].length() * 6 + 18; // rough width spacing
		}

		// Content container (fills under header, above scrollbar area)
		contentContainer = SimpleWidgetBuilder.createContentContainer(topLevelWidget, 8, 55, 16, 58);

		// Create per-tab content layers
		tabContents = new Widget[tabTitles.length];
		for (int i = 0; i < tabTitles.length; i++)
		{
			tabContents[i] = SimpleWidgetBuilder.createScrollableContent(contentContainer, 6, 0);
		}

		// Getting started content - button-based layout (3 rows for 6 buttons)
		ButtonSection gettingStartedSection = SimpleWidgetBuilder.createButtonSection(tabContents[0], 6, 20, 3, client);
		SimpleWidgetBuilder.createStyledButton(gettingStartedSection, "Banking Guide", SpriteID.AccountIcons._0, (ev) -> openAction("Banking Guide"));
		SimpleWidgetBuilder.createStyledButton(gettingStartedSection, "Death Mechanics", SpriteID.AccountIcons._1, (ev) -> openAction("Death Mechanics"));
		SimpleWidgetBuilder.createStyledButton(gettingStartedSection, "Home Teleport", SpriteID.AccountIcons._2, (ev) -> openAction("Home Teleport"));
		SimpleWidgetBuilder.createStyledButton(gettingStartedSection, "World Switching", SpriteID.AccountIcons._3, (ev) -> openAction("World Switching"));
		SimpleWidgetBuilder.createStyledButton(gettingStartedSection, "Bonds Guide", SpriteID.AccountIcons._4, (ev) -> openAction("Bonds Guide"));
		SimpleWidgetBuilder.createStyledButton(gettingStartedSection, "Wiki Access", SpriteID.AccountIcons._0, (ev) -> openAction("Wiki Access"));


		// First hour: curated buttons (2 rows for 4 buttons)
		ButtonSection firstHourSection = SimpleWidgetBuilder.createButtonSection(tabContents[1], 6, 20, 2, client);
		SimpleWidgetBuilder.createStyledButton(firstHourSection, "Cook's Helper", SpriteID.OrbIcon._11, (ev) -> openAction("Cook's Helper"));
		SimpleWidgetBuilder.createStyledButton(firstHourSection, "Lumbridge cows", SpriteID.AccountIcons._0, (ev) -> openAction("Lumbridge cows"));
		SimpleWidgetBuilder.createStyledButton(firstHourSection, "Mining intro", SpriteID.Staticons.MINING, (ev) -> openAction("Mining intro"));
		SimpleWidgetBuilder.createStyledButton(firstHourSection, "Smithing intro", SpriteID.Staticons.SMITHING, (ev) -> openAction("Smithing intro"));

		// Money content - button-based layout (3 rows for 6 buttons)
		ButtonSection moneySection = SimpleWidgetBuilder.createButtonSection(tabContents[2], 6, 20, 3, client);
		SimpleWidgetBuilder.createStyledButton(moneySection, "Feather Trading", SpriteID.Staticons.FLETCHING, (ev) -> openAction("Feather Trading"));
		SimpleWidgetBuilder.createStyledButton(moneySection, "Wine of Zamorak", SpriteID.Staticons.THIEVING, (ev) -> openAction("Wine of Zamorak"));
		SimpleWidgetBuilder.createStyledButton(moneySection, "Stronghold Guide", SpriteID.Staticons.STRENGTH, (ev) -> openAction("Stronghold Guide"));
		SimpleWidgetBuilder.createStyledButton(moneySection, "Cowhide Guide", SpriteID.Staticons.ATTACK, (ev) -> openAction("Cowhide Guide"));
		SimpleWidgetBuilder.createStyledButton(moneySection, "Lobster Fishing", SpriteID.Staticons.FISHING, (ev) -> openAction("Lobster Fishing"));
		SimpleWidgetBuilder.createStyledButton(moneySection, "Safety Tips", SpriteID.Staticons.DEFENCE, (ev) -> openAction("Safety Tips"));
		SimpleWidgetBuilder.createStyledButton(moneySection, "Feather Trading", SpriteID.Staticons.FLETCHING, (ev) -> openAction("Feather Trading"));
		SimpleWidgetBuilder.createStyledButton(moneySection, "Wine of Zamorak", SpriteID.Staticons.THIEVING, (ev) -> openAction("Wine of Zamorak"));
		SimpleWidgetBuilder.createStyledButton(moneySection, "Stronghold Guide", SpriteID.Staticons.STRENGTH, (ev) -> openAction("Stronghold Guide"));
		SimpleWidgetBuilder.createStyledButton(moneySection, "Cowhide Guide", SpriteID.Staticons.ATTACK, (ev) -> openAction("Cowhide Guide"));
		SimpleWidgetBuilder.createStyledButton(moneySection, "Lobster Fishing", SpriteID.Staticons.FISHING, (ev) -> openAction("Lobster Fishing"));
		SimpleWidgetBuilder.createStyledButton(moneySection, "Safety Tips", SpriteID.Staticons.DEFENCE, (ev) -> openAction("Safety Tips"));

		SimpleWidgetBuilder.createStyledButton(moneySection, "Safety Tips", SpriteID.Staticons.DEFENCE, (ev) -> openAction("Safety Tips"));
		SimpleWidgetBuilder.createStyledButton(moneySection, "Feather Trading", SpriteID.Staticons.FLETCHING, (ev) -> openAction("Feather Trading"));
		SimpleWidgetBuilder.createStyledButton(moneySection, "Wine of Zamorak", SpriteID.Staticons.THIEVING, (ev) -> openAction("Wine of Zamorak"));
		SimpleWidgetBuilder.createStyledButton(moneySection, "Stronghold Guide", SpriteID.Staticons.STRENGTH, (ev) -> openAction("Stronghold Guide"));
		SimpleWidgetBuilder.createStyledButton(moneySection, "Cowhide Guide", SpriteID.Staticons.ATTACK, (ev) -> openAction("Cowhide Guide"));
		SimpleWidgetBuilder.createStyledButton(moneySection, "Lobster Fishing", SpriteID.Staticons.FISHING, (ev) -> openAction("Lobster Fishing"));
		SimpleWidgetBuilder.createStyledButton(moneySection, "Safety Tips", SpriteID.Staticons.DEFENCE, (ev) -> openAction("Safety Tips"));
		// Training content - button-based layout (3 rows for 6 buttons)
		ButtonSection trainingSection = SimpleWidgetBuilder.createButtonSection(tabContents[3], 6, 20, 3, client);
		SimpleWidgetBuilder.createStyledButton(trainingSection, "Combat Training", SpriteID.Staticons.ATTACK, (ev) -> openAction("Combat Training"));
		SimpleWidgetBuilder.createStyledButton(trainingSection, "Mining Guide", SpriteID.Staticons.MINING, (ev) -> openAction("Mining Guide"));
		SimpleWidgetBuilder.createStyledButton(trainingSection, "Smithing Guide", SpriteID.Staticons.SMITHING, (ev) -> openAction("Smithing Guide"));
		SimpleWidgetBuilder.createStyledButton(trainingSection, "Cooking Guide", SpriteID.Staticons.COOKING, (ev) -> openAction("Cooking Guide"));
		SimpleWidgetBuilder.createStyledButton(trainingSection, "Fishing Guide", SpriteID.Staticons.FISHING, (ev) -> openAction("Fishing Guide"));
		SimpleWidgetBuilder.createStyledButton(trainingSection, "Quest Benefits", SpriteID.Staticons.ATTACK, (ev) -> openAction("Quest Benefits"));


		// Paths: progression goals
		buildPathsTab(tabContents[4]);

		// Settings: toggle for onboarding prompt
		Widget settingsLabel = SimpleWidgetBuilder.createText(
			tabContents[5], 
			"Show early-game prompt on login",
			"ff9933", 
			true, 
			0, 0, 16, 20
		);
		settingsLabel.setWidthMode(WidgetSizeMode.MINUS);
		settingsLabel.revalidate();

		Widget toggleBg = SimpleWidgetBuilder.createGraphic(tabContents[5], SpriteID.TRADEBACKING, 0, 22, 60, 18);
		toggleBg.setHasListener(true);
		toggleBg.setOnClickListener((JavaScriptCallback) (ev) -> toggleOnboarding());
		toggleBg.revalidate();

		SimpleWidgetBuilder.createCenteredText(
			tabContents[5], 
			getOnboardingEnabled() ? "On" : "Off",
			"c8aa6e", 
			false, 
			4, 24, 56, 14
		);

		// F2P-only filter toggle (placeholder)
		Widget f2pLabel = SimpleWidgetBuilder.createText(
			tabContents[5], 
			"F2P-only recommendations (placeholder)",
			"ff9933", 
			true, 
			0, 48, 16, 20
		);
		f2pLabel.setWidthMode(WidgetSizeMode.MINUS);
		f2pLabel.revalidate();

		Widget ironLabel = SimpleWidgetBuilder.createText(
			tabContents[5], 
			"Ironman hints (placeholder)",
			"ff9933", 
			true, 
			0, 66, 16, 20
		);
		ironLabel.setWidthMode(WidgetSizeMode.MINUS);
		ironLabel.revalidate();

		// Initial tab selection
		applyTabVisibility(0);
		babyWidget = topLevelWidget;
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
		if ("Cook's Helper".equals(label))
		{
			plugin.getClientThread().invokeLater(() -> {
				plugin.displayPanel();
				QuestHelper q = QuestHelperQuest.getByName("Cook's Helper");
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

	private void selectTab(int index)
	{
		if (index < 0 || index >= tabContents.length) return;
		if (selectedTabIndex == index) return;
		selectedTabIndex = index;
		applyTabVisibility(index);
	}

	private void applyTabVisibility(int index)
	{
		if (tabContents == null) return;
		for (int i = 0; i < tabContents.length; i++)
		{
			boolean visible = (i == index);
			if (tabContents[i] != null)
			{
				tabContents[i].setHidden(!visible);
			}
			if (tabHeaders != null && tabHeaders[i] != null)
			{
				tabHeaders[i].setTextColor(Integer.parseInt(visible ? "ff981f" : "c8aa6e", 16));
			}
		}
	}

	private void buildPathsTab(Widget pathsContainer)
	{
		if (pathsContainer == null) return;

		// Create scrollable content for goals
		Widget scrollableContent = SimpleWidgetBuilder.createScrollableContent(pathsContainer, 16, 96);

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
			Widget categoryHeader = SimpleWidgetBuilder.createText(
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
					
					Widget goalName = SimpleWidgetBuilder.createText(
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
						SimpleWidgetBuilder.createTextButton(
							scrollableContent,
							isActiveGoal ? "Active" : "Set",
							0, yPos, 40, 12,
							(ev) -> setGoal(goal)
						);
					}
					else
					{
						// Show completed indicator
						Widget completedText = SimpleWidgetBuilder.createCenteredText(
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
					Widget benefitText = SimpleWidgetBuilder.createText(
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
						Widget prereqButton = SimpleWidgetBuilder.createText(
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
		if (tabContents != null && tabContents.length > 4 && tabContents[4] != null)
		{
			buildPathsTab(tabContents[4]);
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
		if (tabContents != null && tabContents.length > 4 && tabContents[4] != null)
		{
			buildPathsTab(tabContents[4]);
		}
	}


	private Widget getTopLevelWidget(Client client)
	{
		Widget fixedContainer = client.getWidget(InterfaceID.Toplevel.MAIN);
		if (fixedContainer != null)
		{
			return fixedContainer;
		}
		// Resizable classic
		Widget classicContainer = client.getWidget(InterfaceID.ToplevelOsrsStretch.HUD_CONTAINER_BACK);
		if (classicContainer != null)
		{
			return classicContainer;
		}
		// Resizable modern
		Widget modernContainer = client.getWidget(InterfaceID.ToplevelPreEoc.MAINMODAL);
		if (modernContainer != null)
		{
			return modernContainer;
		}

		return null;
	}

	public void close(Client client)
	{
		if (babyWidget == null) return;

		// Hide and clear only the root widget created by this guide
		babyWidget.setHidden(true);
		babyWidget.deleteAllChildren();
		babyWidget = null;
	}
}
