/*
 * Copyright (c) 2024, Zoinkwiz
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
package com.questhelper.helpers.guides;

import com.questhelper.QuestHelperConfig;
import com.questhelper.QuestHelperPlugin;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.helpers.guides.ProgressionGoal;
import com.questhelper.helpers.guides.GoalCategory;
import com.questhelper.helpers.guides.ProgressionGoals;
import com.questhelper.ui.widgets.SimpleWidgetBuilder;
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
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Refactored version of EarlyGameGuide using the SimpleWidgetBuilder
 * This demonstrates how much cleaner the code becomes with proper abstraction
 */
@Singleton
public class EarlyGameGuideRefactored
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

	final int BUTTONS_PER_COLUMN = 2;
	final int BUTTONS_Y_POS = 160;
	final int BUTTON_LAYER_WIDTH = 460;
	final int BUTTON_AREA_HEIGHT = 90;

	final int BUTTON_BACKGROUND_WIDTH = 95;
	final int BUTTON_BACKGROUND_HEIGHT = 71;
	final int BUTTON_PADDING = 4;
	final int BUTTON_WIDTH = 90;
	final int BUTTON_HEIGHT = 60;
	final int BUTTON_EDGE_SIZE = 9;
	final int ICON_SIZE = 24;

	// Scrollbar
	final int SCROLLBAR_HEIGHT = 20;
	final int DRAGGER_WIDTH = 20;
	final int PADDING = 16;
	final int DRAGGER_END_WIDTH = 5;

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

		// Create title separator border
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

		// Create tab headers
		createTabHeaders(topLevelWidget);

		// Create content container
		contentContainer = SimpleWidgetBuilder.createContentContainer(topLevelWidget, 8, 55, 16, 96);

		// Create per-tab content layers
		createTabContents();

		// Create tab content
		createTabContent();

		// Initial tab selection
		applyTabVisibility(0);
		babyWidget = topLevelWidget;
	}

	private void createTabHeaders(Widget parent)
	{
		String[] tabTitles = new String[]{"Getting started", "First hour", "Money", "Training", "Paths", "Settings"};
		tabHeaders = new Widget[tabTitles.length];
		int headerX = 12;
		
		for (int i = 0; i < tabTitles.length; i++)
		{
			final int idx = i;
			tabHeaders[i] = SimpleWidgetBuilder.createTabHeader(
				parent, 
				tabTitles[i], 
				headerX, 
				36, 
				i == 0, 
				(ev) -> selectTab(idx)
			);
			headerX += tabTitles[i].length() * 6 + 18; // rough width spacing
		}
	}

	private void createTabContents()
	{
		tabContents = new Widget[6];
		for (int i = 0; i < 6; i++)
		{
			tabContents[i] = SimpleWidgetBuilder.createScrollableContent(contentContainer, 16, 96);
		}
	}

	private void createTabContent()
	{
		// Getting started content
		Widget gettingStarted = SimpleWidgetBuilder.createText(tabContents[0], 
			"Welcome! Essential F2P basics:\n\n• Banking: Deposit items safely\n• Death: Items lost after 60min\n• Home teleport: Free every 30min\n• Worlds: Switch for less crowded areas\n• Bonds: Buy membership with GP\n\nWiki: oldschool.runescape.wiki",
			"ff9933", false, 0, 0, 16, 90);
		gettingStarted.setWidthMode(WidgetSizeMode.MINUS);

		// First hour: curated buttons
		Widget buttonLayer = createButtonLayer(tabContents[1]);
		Widget scrollbar = createScrollbar(buttonLayer);
		createButton(buttonLayer, scrollbar, "Cook's Helper", SpriteID.OrbIcon._11);
		createButton(buttonLayer, scrollbar, "Lumbridge cows", SpriteID.AccountIcons._0);
		createButton(buttonLayer, scrollbar, "Mining intro", SpriteID.Staticons.MINING);
		createButton(buttonLayer, scrollbar, "Smithing intro", SpriteID.Staticons.SMITHING);

		// Money content
		Widget money = SimpleWidgetBuilder.createText(tabContents[2], 
			"Fast F2P money methods:\n\n• Feathers: Buy 5gp, sell 6gp\n• Wine of Zamorak: 35gp each\n• Stronghold of Security: 10k GP\n• Cowhides: ~100gp each\n• Fishing: Raw lobsters ~200gp\n\nSafety: Don't carry all GP!",
			"ff9933", false, 0, 0, 16, 90);
		money.setWidthMode(WidgetSizeMode.MINUS);

		// Training content
		Widget training = SimpleWidgetBuilder.createText(tabContents[3], 
			"F2P training goals:\n\n• Combat: 40 Attack/Strength/Def\n• Mining: 15+ for better ores\n• Smithing: 30+ for steel items\n• Cooking: 40+ for better food\n• Fishing: 20+ for lobsters\n\nQuests unlock better training!",
			"ff9933", false, 0, 0, 16, 90);
		training.setWidthMode(WidgetSizeMode.MINUS);

		// Paths: progression goals
		buildPathsTab(tabContents[4]);

		// Settings: toggle for onboarding prompt
		createSettingsTab(tabContents[5]);
	}

	private void createSettingsTab(Widget container)
	{
		// Onboarding toggle
		Widget onboardingLabel = SimpleWidgetBuilder.createText(container, "Show early-game prompt on login", "ff9933", false, 0, 0, 16, 20);
		onboardingLabel.setWidthMode(WidgetSizeMode.MINUS);

		SimpleWidgetBuilder.createTextButton(container, getOnboardingEnabled() ? "On" : "Off", 0, 22, 60, 18, (ev) -> toggleOnboarding());

		// F2P-only filter toggle (placeholder)
		Widget f2pLabel = SimpleWidgetBuilder.createText(container, "F2P-only recommendations (placeholder)", "ff9933", false, 0, 48, 16, 20);
		f2pLabel.setWidthMode(WidgetSizeMode.MINUS);

		Widget ironLabel = SimpleWidgetBuilder.createText(container, "Ironman hints (placeholder)", "ff9933", false, 0, 66, 16, 20);
		ironLabel.setWidthMode(WidgetSizeMode.MINUS);
	}

	private Widget createButtonLayer(Widget parent)
	{
		Widget buttonLayer = SimpleWidgetBuilder.createLayer(parent, 6, BUTTONS_Y_POS, BUTTON_LAYER_WIDTH, BUTTON_AREA_HEIGHT);
		buttonLayer.setHeightMode(WidgetSizeMode.MINUS);
		buttonLayer.setHasListener(true);
		buttonLayer.revalidate();
		return buttonLayer;
	}

	private Widget createScrollbar(Widget scrollableContainer)
	{
		// This would use the existing scrollbar logic but with the new builders
		// For brevity, I'm showing the pattern but not implementing the full scrollbar
		Widget scrollbar = SimpleWidgetBuilder.createLayer(scrollableContainer.getParent(), 0, 0, BUTTON_LAYER_WIDTH, SCROLLBAR_HEIGHT);
		scrollbar.setXPositionMode(WidgetPositionMode.ABSOLUTE_CENTER);
		scrollbar.setYPositionMode(WidgetPositionMode.ABSOLUTE_BOTTOM);
		scrollbar.setOriginalY(SCROLLBAR_HEIGHT / 2);
		scrollbar.revalidate();
		return scrollbar;
	}

	private void createButton(Widget buttonLayer, Widget scrollbar, String text, int icon)
	{
		int existingButtons = (buttonLayer.getDynamicChildren() == null) ? 0 : buttonLayer.getDynamicChildren().length;
		int xShift = ((existingButtons) / BUTTONS_PER_COLUMN) * BUTTON_BACKGROUND_WIDTH;
		int yShift = (existingButtons % BUTTONS_PER_COLUMN) * BUTTON_BACKGROUND_HEIGHT;

		Widget buttonContainer = SimpleWidgetBuilder.createLayer(buttonLayer, xShift, yShift, BUTTON_BACKGROUND_WIDTH, BUTTON_BACKGROUND_HEIGHT);

		// Button background with hover effects
		Widget buttonBackground = SimpleWidgetBuilder.createGraphic(buttonContainer, SpriteID.TRADEBACKING, 0, 0, BUTTON_WIDTH, BUTTON_HEIGHT);
		buttonBackground.setHasListener(true);
		buttonBackground.setAction(0, "Open");
		buttonBackground.setOnOpListener((JavaScriptCallback) (ev) -> openAction(text));
		buttonBackground.setOnMouseOverListener((JavaScriptCallback) (ev) -> buttonBackground.setSpriteId(SpriteID.TRADEBACKING_DARK));
		buttonBackground.setOnMouseLeaveListener((JavaScriptCallback) (ev) -> buttonBackground.setSpriteId(SpriteID.TRADEBACKING));

		// Button borders (simplified - would create all 8 border pieces)
		createButtonBorders(buttonContainer);

		// Button text
		SimpleWidgetBuilder.createCenteredText(buttonContainer, text, "ff9933", true, BUTTON_PADDING, BUTTON_PADDING + 1, BUTTON_WIDTH, 12);

		// Button icon
		SimpleWidgetBuilder.createGraphic(buttonContainer, icon, (BUTTON_WIDTH - ICON_SIZE) / 2, 25, ICON_SIZE, ICON_SIZE);

		buttonLayer.setScrollWidth(xShift - BUTTON_LAYER_WIDTH);
		scrollbar.setHidden(buttonLayer.getScrollWidth() <= buttonLayer.getOriginalWidth());
	}

	private void createButtonBorders(Widget container)
	{
		// Top-left corner
		SimpleWidgetBuilder.createGraphic(container, SpriteID.V2StoneButton._0, BUTTON_PADDING, 0, BUTTON_EDGE_SIZE, BUTTON_EDGE_SIZE);

		// Top-right corner
		SimpleWidgetBuilder.createGraphic(container, SpriteID.V2StoneButton._1, BUTTON_WIDTH - BUTTON_EDGE_SIZE, 0, BUTTON_EDGE_SIZE, BUTTON_EDGE_SIZE);

		// Bottom-left corner
		SimpleWidgetBuilder.createGraphic(container, SpriteID.V2StoneButton._2, BUTTON_PADDING, BUTTON_HEIGHT - BUTTON_EDGE_SIZE, BUTTON_EDGE_SIZE, BUTTON_EDGE_SIZE);

		// Bottom-right corner
		SimpleWidgetBuilder.createGraphic(container, SpriteID.V2StoneButton._3, BUTTON_WIDTH - BUTTON_EDGE_SIZE, BUTTON_HEIGHT - BUTTON_EDGE_SIZE, BUTTON_EDGE_SIZE, BUTTON_EDGE_SIZE);

		// Left edge
		SimpleWidgetBuilder.createGraphic(container, SpriteID.V2StoneButton._4, BUTTON_PADDING, BUTTON_EDGE_SIZE, BUTTON_EDGE_SIZE, BUTTON_HEIGHT - (BUTTON_EDGE_SIZE * 2));

		// Top edge
		SimpleWidgetBuilder.createGraphic(container, SpriteID.V2StoneButton._5, BUTTON_EDGE_SIZE + BUTTON_PADDING, 0, BUTTON_WIDTH - (BUTTON_EDGE_SIZE * 2), BUTTON_EDGE_SIZE);

		// Right edge
		SimpleWidgetBuilder.createGraphic(container, SpriteID.V2StoneButton._6, BUTTON_WIDTH - BUTTON_EDGE_SIZE, BUTTON_EDGE_SIZE, BUTTON_EDGE_SIZE, BUTTON_HEIGHT - (BUTTON_EDGE_SIZE * 2));

		// Bottom edge
		SimpleWidgetBuilder.createGraphic(container, SpriteID.V2StoneButton._7, BUTTON_EDGE_SIZE + BUTTON_PADDING, BUTTON_HEIGHT - BUTTON_EDGE_SIZE, BUTTON_WIDTH - (BUTTON_EDGE_SIZE * 2), BUTTON_EDGE_SIZE);
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
			Widget categoryHeader = SimpleWidgetBuilder.createText(scrollableContent, 
				collapseIcon + " " + category.getDisplayName() + " (" + goals.size() + " goals)",
				"ff981f", true, 0, yPos, 16, 16);
			categoryHeader.setWidthMode(WidgetSizeMode.MINUS);
			categoryHeader.setHasListener(true);
			categoryHeader.setOnOpListener((JavaScriptCallback) (ev) -> toggleCategory(category));
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
					
					Widget goalName = SimpleWidgetBuilder.createText(scrollableContent, 
						goal.getName() + " " + stars + progressText,
						isActiveGoal ? "00ff00" : "ff9933", true, 4, yPos, 12, 12);
					goalName.setWidthMode(WidgetSizeMode.MINUS);
					goalName.revalidate();

					// Set as Goal button (only show if not completed)
					boolean isCompleted = plugin != null && plugin.getClient() != null && goal.isCompleted(plugin.getClient());
					if (!isCompleted)
					{
						SimpleWidgetBuilder.createTextButton(scrollableContent, 
							isActiveGoal ? "Active" : "Set", 
							0, yPos, 40, 12, 
							(ev) -> setGoal(goal));
					}
					else
					{
						// Show completed indicator
						SimpleWidgetBuilder.createCenteredText(scrollableContent, "✓ Complete", "00ff00", false, 0, yPos + 1, 40, 10);
					}

					yPos += 14;

					// Benefit text
					Widget benefitText = SimpleWidgetBuilder.createText(scrollableContent, goal.getBenefit(), "c8aa6e", false, 4, yPos, 16, 12);
					benefitText.setWidthMode(WidgetSizeMode.MINUS);

					yPos += 16;

					// Clickable prerequisite chain view
					if (goal.getPrerequisites().size() > 1)
					{
						Widget prereqButton = SimpleWidgetBuilder.createText(scrollableContent, 
							"View prerequisites (" + goal.getPrerequisites().size() + " quests)",
							"808080", false, 4, yPos, 16, 12);
						prereqButton.setWidthMode(WidgetSizeMode.MINUS);
						prereqButton.setHasListener(true);
						prereqButton.setOnOpListener((JavaScriptCallback) (ev) -> showPrerequisites(goal));
						prereqButton.revalidate();
						yPos += 14;
					}
				}
			}

			yPos += 8; // Space between categories
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
