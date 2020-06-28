/*
 * Copyright (c) 2020, Zoinkwiz <https://github.com/Zoinkwiz>
 * Copyright (c) 2019, Trevor <https://github.com/Trevor159>
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
package com.questhelper;

import com.google.common.collect.ImmutableSet;
import com.google.common.primitives.Ints;
import com.google.common.reflect.ClassPath;
import com.google.inject.Binder;
import com.google.inject.CreationException;
import com.google.inject.Injector;
import com.google.inject.Module;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.RuneLite;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import com.questhelper.panel.QuestHelperPanel;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.steps.QuestStep;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.Text;

@PluginDescriptor(
	name = "Quest Helper",
	description = "Helps you with your quests"
)
@Slf4j
public class QuestHelperPlugin extends Plugin
{

	private static int RESIZABLE_VIEWPORT_BOTTOM_LINE_GROUP_ID = 164;
	private static int QUESTTAB_GROUP_ID = 629;

	private static final int[] QUESTLIST_WIDGET_IDS = new int[]
		{
			WidgetInfo.QUESTLIST_FREE_CONTAINER.getId(),
			WidgetInfo.QUESTLIST_MEMBERS_CONTAINER.getId(),
			WidgetInfo.QUESTLIST_MINIQUEST_CONTAINER.getId(),
		};

	private static final int[] QUESTTAB_WIDGET_IDS = new int[]
		{
			RESIZABLE_VIEWPORT_BOTTOM_LINE_GROUP_ID,
			WidgetInfo.RESIZABLE_VIEWPORT_QUESTS_TAB.getId(),
			WidgetInfo.FIXED_VIEWPORT_QUESTS_TAB.getId(),
			QUESTTAB_GROUP_ID
		};

	private static final String QUEST_PACKAGE = "com.questhelper.quests";

	private static final String MENUOP_STARTHELPER = "Start Quest Helper";
	private static final String MENUOP_STOPHELPER = "Stop Quest Helper";

	private static final String MENUOP_PHOENIXGANG = "Start Quest Helper (Phoenix Gang)";
	private static final String MENUOP_BLACKARMGANG = "Start Quest Helper (Black Arm Gang)";

	private static final String SHIELD_OF_ARRAV_PHOENIX_GANG = "Shield of Arrav - Phoenix Gang";
	private static final String SHIELD_OF_ARRAV_BLACK_ARM_GANG = "Shield of Arrav - Black Arm Gang";

	@Inject
	private Client client;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private EventBus eventBus;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private QuestHelperOverlay questHelperOverlay;

	@Inject
	private QuestHelperWidgetOverlay questHelperWidgetOverlay;

	@Inject
	private QuestHelperWorldOverlay questHelperWorldOverlay;

	@Getter
	private QuestHelper selectedQuest = null;

	private QuestStep lastStep = null;

	private Map<String, QuestHelper> quests;


	@Inject
	SpriteManager spriteManager;

	private QuestHelperPanel panel;

	private NavigationButton navButton;

	@Override
	protected void startUp() throws IOException
	{
		quests = scanAndInstantiate(getClass().getClassLoader(), QUEST_PACKAGE);
		overlayManager.add(questHelperOverlay);
		overlayManager.add(questHelperWorldOverlay);
		overlayManager.add(questHelperWidgetOverlay);

		final BufferedImage icon = ImageUtil.getResourceStreamFromClass(getClass(), "/quest_icon.png");
		panel = new QuestHelperPanel(this, client);
		navButton = NavigationButton.builder()
			.tooltip("Quest Helper")
			.icon(icon)
			.priority(7)
			.panel(panel)
			.build();

		clientToolbar.addNavigation(navButton);
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(questHelperOverlay);
		overlayManager.remove(questHelperWorldOverlay);
		overlayManager.remove(questHelperWidgetOverlay);
		clientToolbar.removeNavigation(navButton);
		quests = null;
		shutDownQuest();
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (selectedQuest != null)
		{
			if (selectedQuest.getCurrentStep() != null)
			{
				QuestStep currentStep = selectedQuest.getCurrentStep().getActiveStep();
				if (currentStep != null && currentStep != lastStep)
				{
					lastStep = currentStep;
					panel.updateHighlight(currentStep);
				}
				panel.updateLocks();
			}
		}
	}

	@Subscribe
	public void onGameStateChanged(final GameStateChanged event)
	{
		final GameState state = event.getGameState();

		if (state == GameState.LOGIN_SCREEN && selectedQuest != null && selectedQuest.getCurrentStep() != null)
		{
			shutDownQuest();
		}
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		if (!(client.getGameState() == GameState.LOGGED_IN))
		{
			return;
		}

		if (selectedQuest != null
			&& selectedQuest.updateQuest()
			&& selectedQuest.getCurrentStep() == null)
		{
			shutDownQuest();
		}
	}

	@Subscribe
	private void onMenuOptionClicked(MenuOptionClicked event)
	{
		if (event.getMenuAction() == MenuAction.RUNELITE)
		{
			switch (event.getMenuOption())
			{
				case MENUOP_STARTHELPER:
					event.consume();
					shutDownQuest();
					String quest = Text.removeTags(event.getMenuTarget());
					startUpQuest(quests.get(quest));
					break;
				case MENUOP_STOPHELPER:
					event.consume();
					shutDownQuest();
					break;
				case MENUOP_PHOENIXGANG:
					event.consume();
					shutDownQuest();
					startUpQuest(quests.get(SHIELD_OF_ARRAV_PHOENIX_GANG));
					break;
				case MENUOP_BLACKARMGANG:
					event.consume();
					shutDownQuest();
					startUpQuest(quests.get(SHIELD_OF_ARRAV_BLACK_ARM_GANG));
					break;
			}
		}
	}

	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded event)
	{
		int widgetIndex = event.getActionParam0();
		int widgetID = event.getActionParam1();
		MenuEntry[] menuEntries = client.getMenuEntries();
		String target = Text.removeTags(event.getTarget());

		if (Ints.contains(QUESTLIST_WIDGET_IDS, widgetID) && "Read Journal:".equals(event.getOption()))
		{
			if (target.equals("Shield of Arrav"))
			{
				QuestHelper questHelperPhoenix = quests.get(SHIELD_OF_ARRAV_PHOENIX_GANG);
				QuestHelper questHelperBlackArm = quests.get(SHIELD_OF_ARRAV_BLACK_ARM_GANG);
				if (questHelperBlackArm != null && !questHelperBlackArm.isCompleted()
				|| questHelperPhoenix != null && !questHelperPhoenix.isCompleted())
				{
					if (selectedQuest != null &&
						(selectedQuest.getQuest().getName().equals(SHIELD_OF_ARRAV_PHOENIX_GANG) ||
							selectedQuest.getQuest().getName().equals(SHIELD_OF_ARRAV_BLACK_ARM_GANG)))
					{
						menuEntries = addNewEntry(menuEntries, MENUOP_STOPHELPER, event.getTarget(), widgetIndex, widgetID);
					}
					else
					{
						if (questHelperBlackArm.getVar() < 3)
						{
							// TODO: Change Var to actual var
							menuEntries = addNewEntry(menuEntries, MENUOP_PHOENIXGANG, event.getTarget(), widgetIndex, widgetID);
						}
						if (questHelperPhoenix.getVar() < 6)
						{
							menuEntries = addNewEntry(menuEntries, MENUOP_BLACKARMGANG, event.getTarget(), widgetIndex, widgetID);
						}
					}
				}
			}
			else
			{
				QuestHelper questHelper = quests.get(target);
				if (questHelper != null && !questHelper.isCompleted())
				{
					if (selectedQuest != null && selectedQuest.getQuest().getName().equals(target))
					{
						menuEntries = addNewEntry(menuEntries, MENUOP_STOPHELPER, event.getTarget(), widgetIndex, widgetID);
					}
					else
					{
						menuEntries = addNewEntry(menuEntries, MENUOP_STARTHELPER, event.getTarget(), widgetIndex, widgetID);
					}
				}
			}
		}

		if (Ints.contains(QUESTTAB_WIDGET_IDS, widgetID)
			&& "Quest List".equals(event.getOption())
			&& selectedQuest != null)
		{
			menuEntries = addNewEntry(menuEntries, MENUOP_STOPHELPER, event.getTarget(), widgetIndex, widgetID);
		}
	}

	private MenuEntry[] addNewEntry(MenuEntry[] menuEntries, String newEntry, String target, int widgetIndex, int widgetID) {
		menuEntries = Arrays.copyOf(menuEntries, menuEntries.length + 1);

		MenuEntry menuEntry = menuEntries[menuEntries.length - 1] = new MenuEntry();
		menuEntry.setTarget(target);
		menuEntry.setParam0(widgetIndex);
		menuEntry.setParam1(widgetID);
		menuEntry.setType(MenuAction.RUNELITE.getId());
		menuEntry.setOption(newEntry);

		client.setMenuEntries(menuEntries);

		return menuEntries;
	}

	private void startUpQuest(QuestHelper questHelper)
	{
		if (!questHelper.isCompleted())
		{
			selectedQuest = questHelper;
			eventBus.register(selectedQuest);
			selectedQuest.startUp();
			SwingUtilities.invokeLater(() -> {
				panel.removeQuest();
				panel.addQuest((BasicQuestHelper) questHelper);
			});
		}
		else
		{
			selectedQuest = null;
		}
	}

	private void shutDownQuest()
	{
		if (selectedQuest != null)
		{
			selectedQuest.shutDown();
			SwingUtilities.invokeLater(() -> panel.removeQuest());
			eventBus.unregister(selectedQuest);
			selectedQuest = null;
		}
	}

	private Map<String, QuestHelper> scanAndInstantiate(ClassLoader classLoader, String packageName) throws IOException
	{
		Map<QuestHelperQuest, Class<? extends QuestHelper>> quests = new HashMap<>();

		Map<String, QuestHelper> scannedQuests = new HashMap<>();
		ClassPath classPath = ClassPath.from(classLoader);

		ImmutableSet<ClassPath.ClassInfo> classes = packageName == null ? classPath.getAllClasses()
			: classPath.getTopLevelClassesRecursive(packageName);
		for (ClassPath.ClassInfo classInfo : classes)
		{
			Class<?> clazz = classInfo.load();
			QuestDescriptor questDescriptor = clazz.getAnnotation(QuestDescriptor.class);

			if (questDescriptor == null)
			{
				if (clazz.getSuperclass() == QuestHelper.class)
				{
					log.warn("Class {} is a quest helper, but has no quest descriptor",
						clazz);
				}
				continue;
			}

			if (clazz.isAssignableFrom(QuestHelper.class))
			{
				log.warn("Class {} has quest descriptor, but is not a quest helper",
					clazz);
				continue;
			}

			Class<QuestHelper> questClass = (Class<QuestHelper>) clazz;
			quests.put(questDescriptor.quest(), questClass);
		}

		for (Map.Entry<QuestHelperQuest, Class<? extends QuestHelper>> questClazz : quests.entrySet())
		{
			QuestHelper questHelper;
			try
			{
				questHelper = instantiate((Class<QuestHelper>) questClazz.getValue(), questClazz.getKey());
			}
			catch (QuestInstantiationException ex)
			{
				log.warn("Error instantiating quest helper!", ex);
				continue;
			}

			scannedQuests.put(questClazz.getKey().getName(), questHelper);
		}

		return scannedQuests;
	}

	private QuestHelper instantiate(Class<QuestHelper> clazz, QuestHelperQuest quest) throws QuestInstantiationException
	{
		QuestHelper questHelper;
		try
		{
			questHelper = clazz.newInstance();
			questHelper.setQuest(quest);
		}
		catch (InstantiationException | IllegalAccessException ex)
		{
			throw new QuestInstantiationException(ex);
		}

		try
		{
			Module questModule = (Binder binder) ->
			{
				binder.bind(clazz).toInstance(questHelper);
				binder.install(questHelper);
			};
			Injector questInjector = RuneLite.getInjector().createChildInjector(questModule);
			questInjector.injectMembers(questHelper);
			questHelper.setInjector(questInjector);
		}
		catch (CreationException ex)
		{
			throw new QuestInstantiationException(ex);
		}

		log.debug("Loaded quest helper {}", clazz.getSimpleName());
		return questHelper;
	}
}
