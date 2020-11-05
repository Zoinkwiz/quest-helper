/*
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
package com.questhelper.steps;

import com.google.inject.Binder;
import com.google.inject.Inject;
import com.google.inject.Module;
import static com.questhelper.QuestHelperOverlay.TITLED_CONTENT_COLOR;
import com.questhelper.QuestVarbits;
import com.questhelper.requirements.Requirement;
import com.questhelper.steps.choice.WidgetChoiceStep;
import com.questhelper.steps.choice.WidgetChoiceSteps;
import com.questhelper.steps.conditional.ConditionForStep;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.SpriteID;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.SpriteManager;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.QuestHelperPlugin;
import com.questhelper.steps.choice.DialogChoiceStep;
import com.questhelper.steps.choice.DialogChoiceSteps;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.util.ImageUtil;

public abstract class QuestStep implements Module
{
	@Inject
	protected Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	SpriteManager spriteManager;

	@Getter
	protected ArrayList<String> text;

	protected int ARROW_SHIFT_X = 8;
	protected int ARROW_SHIFT_Y = 20;

	/* Locking applies to ConditionalSteps. Intended to be used as a method of forcing a step to run if it's been locked */
	private boolean locked;

	@Getter
	@Setter
	private boolean isLockable;

	@Getter
	@Setter
	private boolean blocker;

	@Getter
	private boolean unlockable = true;

	@Getter
	@Setter
	private ConditionForStep lockingCondition;

	private int currentCutsceneStatus = 0;
	protected boolean inCutscene;

	@Setter
	protected boolean allowInCutscene = false;

	protected int iconItemID = -1;
	protected BufferedImage icon;

	@Getter
	protected final QuestHelper questHelper;

	@Getter
	protected DialogChoiceSteps choices = new DialogChoiceSteps();

	@Getter
	protected WidgetChoiceSteps widgetChoices = new WidgetChoiceSteps();

	@Getter
	private final ArrayList<QuestStep> substeps = new ArrayList<>();

	@Getter
	@Setter
	private boolean showInSidebar = true;

	public QuestStep(QuestHelper questHelper)
	{
		this.questHelper = questHelper;
	}

	public QuestStep(QuestHelper questHelper, String text)
	{
		this.text = new ArrayList<>(Collections.singletonList(text));
		this.questHelper = questHelper;
	}

	public QuestStep(QuestHelper questHelper, ArrayList<String> text)
	{
		this.text = text;
		this.questHelper = questHelper;
	}

	@Override
	public void configure(Binder binder)
	{
	}

	public void startUp()
	{
		clientThread.invokeLater(this::highlightChoice);
		clientThread.invokeLater(this::highlightWidgetChoice);
	}

	public void shutDown()
	{
	}

	public void addSubSteps(QuestStep... substep)
	{
		this.substeps.addAll(Arrays.asList(substep));
	}

	public void addSubSteps(Collection<QuestStep> substeps)
	{
		this.substeps.addAll(substeps);
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		if (!allowInCutscene)
		{
			int newCutsceneStatus = client.getVarbitValue(QuestVarbits.CUTSCENE.getId());
			if (currentCutsceneStatus == 0 && newCutsceneStatus == 1)
			{
				enteredCutscene();
			}
			else if (currentCutsceneStatus == 1 && newCutsceneStatus == 0)
			{
				leftCutscene();
			}
			currentCutsceneStatus = newCutsceneStatus;
		}
	}

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded event)
	{
		if (event.getGroupId() == 219)
		{
			clientThread.invokeLater(this::highlightChoice);
		}

		for (WidgetChoiceStep choice : widgetChoices.getChoices())
		{
			if (event.getGroupId() == choice.getGroupId())
			{
				clientThread.invokeLater(this::highlightWidgetChoice);
			}
		}
	}

	public void addText(String newLine)
	{
		if (text == null)
		{
			text = new ArrayList<>();
		}

		text.add(newLine);
	}

	public void enteredCutscene()
	{
		inCutscene = true;
	}

	public void leftCutscene()
	{
		inCutscene = false;
	}

	public void highlightChoice()
	{
		choices.checkChoices(client);
	}

	public void setText(String text)
	{
		this.text = new ArrayList<>(Collections.singletonList(text));
	}

	public void setText(ArrayList<String> text)
	{
		this.text = text;
	}

	public void highlightWidgetChoice()
	{
		widgetChoices.checkChoices(client);
	}

	public void addDialogStep(String choice)
	{
		choices.addChoice(new DialogChoiceStep(choice));
	}

	public void addDialogStepWithExclusion(String choice, String exclusionString)
	{
		choices.addDialogChoiceWithExclusion(new DialogChoiceStep(choice), exclusionString);
	}

	public void addDialogStep(int id, String choice)
	{
		choices.addChoice(new DialogChoiceStep(id, choice));
	}

	public void addDialogSteps(String... newChoices)
	{
		for (String choice : newChoices)
		{
			choices.addChoice(new DialogChoiceStep(choice));
		}
	}

	public void addWidgetChoice(String text, int groupID, int childID)
	{
		widgetChoices.addChoice(new WidgetChoiceStep(text, groupID, childID));
	}

	public void addWidgetChoice(int id, int groupID, int childID)
	{
		widgetChoices.addChoice(new WidgetChoiceStep(id, groupID, childID));
	}

	public void makeOverlayHint(PanelComponent panelComponent, QuestHelperPlugin plugin, Requirement... additionalRequirements)
	{
		makeOverlayHint(panelComponent, plugin, null, additionalRequirements);
	}

	public void makeOverlayHint(PanelComponent panelComponent, QuestHelperPlugin plugin, ArrayList<String> additionalText, Requirement... additionalRequirements)
	{
		addTitleToPanel(panelComponent);

		if (additionalText != null)
		{
			for (String line : additionalText)
			{
				if (!line.isEmpty())
				{
					addTextToPanel(panelComponent, line);
				}
			}
			if (text != null && (text.size() > 0 && !text.get(0).isEmpty()))
			{
				addTextToPanel(panelComponent, "");
			}
		}

		if (text != null)
		{
			for (String line : text)
			{
				if (!line.isEmpty())
				{
					addTextToPanel(panelComponent, line);
				}
			}
		}
	}

	private void addTitleToPanel(PanelComponent panelComponent)
	{
		panelComponent.getChildren().add(LineComponent.builder()
			.left(questHelper.getQuest().getName())
			.build());
	}

	private void addTextToPanel(PanelComponent panelComponent, String line)
	{
		panelComponent.getChildren().add(LineComponent.builder()
			.left(line)
			.leftColor(TITLED_CONTENT_COLOR)
			.build());
	}

	public void addIcon(int iconItemID)
	{
		this.iconItemID = iconItemID;
	}

	public void makeWorldOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin)
	{
	}

	public void makeWidgetOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin)
	{
	}

	public void setLockedManually(boolean isLocked)
	{
		locked = isLocked;
	}

	public boolean isLocked()
	{
		boolean autoLocked = lockingCondition != null && lockingCondition.checkCondition(client);
		unlockable = !autoLocked;
		if (autoLocked)
		{
			locked = true;
		}
		return locked;
	}

	public QuestStep getActiveStep()
	{
		return this;
	}

	public QuestStep getSidePanelStep()
	{
		return getActiveStep();
	}

	public BufferedImage getQuestImage()
	{
		return spriteManager.getSprite(SpriteID.TAB_QUESTS, 0);
	}

	public BufferedImage getArrow()
	{
		return ImageUtil.getResourceStreamFromClass(getClass(), "/quest_arrow.png");
	}

	public BufferedImage getSmallArrow()
	{
		return ImageUtil.getResourceStreamFromClass(getClass(), "/quest_minimap_arrow_small.png");
	}
}