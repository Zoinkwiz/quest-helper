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
import com.questhelper.QuestHelperPlugin;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.questhelpers.QuestUtil;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.steps.choice.*;
import com.questhelper.steps.overlay.IconOverlay;
import com.questhelper.steps.tools.QuestPerspective;
import com.questhelper.steps.widget.AbstractWidgetHighlight;
import com.questhelper.steps.widget.Spell;
import com.questhelper.steps.widget.SpellWidgetHighlight;
import com.questhelper.steps.widget.WidgetHighlight;
import com.questhelper.tools.VisibilityHelper;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.SpriteID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.ImageUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.*;
import java.util.regex.Pattern;

import static com.questhelper.overlays.QuestHelperOverlay.TITLED_CONTENT_COLOR;

@Slf4j
public abstract class QuestStep implements Module
{
	@Inject
	protected Client client;

	@Inject
	protected ClientThread clientThread;

	@Inject
	ItemManager itemManager;

	@Inject
	protected SpriteManager spriteManager;

	@Inject
	protected ModelOutlineRenderer modelOutlineRenderer;

	@Inject
	VisibilityHelper visibilityHelper;

	@Inject
	TooltipManager tooltipManager;

	@Getter
	protected Integer id = null;

	@Getter
	protected List<String> text;

	@Getter
	protected List<String> overlayText = new ArrayList<>();

	protected int ARROW_SHIFT_Y = 15;

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
	private Requirement lockingCondition;

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
	protected List<AbstractWidgetHighlight> widgetsToHighlight = new ArrayList<>();

	@Getter
	private final List<QuestStep> substeps = new ArrayList<>();

	@Getter
	private Requirement conditionToHide;

	@Getter
	private Requirement fadeCondition;

	@Getter
	@Setter
	private boolean showInSidebar = true;

	protected String lastDialogSeen = "";

	@Setter
	@Getter
	protected String worldTooltipText;

	@Setter
	@Getter
	protected String backgroundWorldTooltipText;

	@Getter
	@Setter
	protected boolean shouldOverlayWidget;

	@Getter
	@Setter
	protected List<Integer> geInterfaceIcon;

	public QuestStep(QuestHelper questHelper)
	{
		this.questHelper = questHelper;
	}

	public QuestStep(QuestHelper questHelper, String text)
	{
		// use explicit ArrayList because we need the 'text' list to be mutable
		this.text = new ArrayList<>(Collections.singletonList(text));
		this.questHelper = questHelper;
	}

	public QuestStep(QuestHelper questHelper, List<String> text)
	{
		this.text = text;
		this.questHelper = questHelper;
	}

	public void setOverlayText(String text)
	{
		this.overlayText.add(text);
	}

	public void setOverlayText(String... text)
	{
		this.overlayText.addAll(Arrays.asList(text));
	}

	@Override
	public void configure(Binder binder)
	{
	}

	public void startUp()
	{
		clientThread.invokeLater(this::highlightChoice);
		clientThread.invokeLater(this::highlightWidgetChoice);

		setupIcon();
	}

	public void shutDown()
	{
	}

	public QuestStep withId(Integer id)
	{
		this.id = id;
		for (QuestStep substep : substeps)
		{
			substep.withId(id);
		}
		return this;
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
			int newCutsceneStatus = client.getVarbitValue(VarbitID.CUTSCENE_STATUS);
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
		if (event.getGroupId() == InterfaceID.CHATMENU)
		{
			clientThread.invokeLater(this::highlightChoice);
		}

		for (WidgetChoiceStep choice : widgetChoices.getChoices())
		{
			if (event.getGroupId() == choice.getGroupIdForChecking())
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
		choices.checkChoices(client, lastDialogSeen);
	}

	public void setText(String text)
	{
		this.text = QuestUtil.toArrayList(text);
	}

	public void setText(List<String> text)
	{
		this.text = text;
	}

	public void highlightWidgetChoice()
	{
		widgetChoices.checkChoices(client);
	}

	public QuestStep addDialogStep(String choice)
	{
		choices.addChoice(new DialogChoiceStep(questHelper.getConfig(), choice));
		return this;
	}

	public QuestStep addDialogStep(Pattern pattern)
	{
		choices.addChoice(new DialogChoiceStep(questHelper.getConfig(), pattern));
		return this;
	}

	public void resetDialogSteps()
	{
		choices.resetChoices();
	}

	public QuestStep addDialogStepWithExclusion(String choice, String exclusionString)
	{
		choices.addDialogChoiceWithExclusion(new DialogChoiceStep(questHelper.getConfig(), choice), exclusionString);
		return this;
	}

	public QuestStep addDialogStepWithExclusions(String choice, String... exclusionString)
	{
		choices.addDialogChoiceWithExclusions(new DialogChoiceStep(questHelper.getConfig(), choice), exclusionString);
		return this;
	}

	public QuestStep addDialogStep(int id, String choice)
	{
		choices.addChoice(new DialogChoiceStep(questHelper.getConfig(), id, choice));
		return this;
	}

	public QuestStep addDialogStep(int id, Pattern pattern)
	{
		choices.addChoice(new DialogChoiceStep(questHelper.getConfig(), id, pattern));
		return this;
	}

	public QuestStep addDialogSteps(String... newChoices)
	{
		for (String choice : newChoices)
		{
			choices.addChoice(new DialogChoiceStep(questHelper.getConfig(), choice));
		}
		return this;
	}

	public QuestStep addDialogConsideringLastLineCondition(String dialogString, String choiceValue)
	{
		DialogChoiceStep choice = new DialogChoiceStep(questHelper.getConfig(), dialogString);
		choice.setExpectedPreviousLine(choiceValue);
		choices.addChoice(choice);
		return this;
	}

	public QuestStep addDialogChange(String choice, String newText)
	{
		choices.addChoice(new DialogChoiceChange(questHelper.getConfig(), choice, newText));
		return this;
	}

	public QuestStep addWidgetChoice(String text, int groupID, int childID)
	{
		widgetChoices.addChoice(new WidgetChoiceStep(questHelper.getConfig(), text, groupID, childID));
		return this;
	}

	public QuestStep addWidgetChoice(String text, int groupID, int childID, int groupIDForChecking)
	{
		WidgetChoiceStep newChoice = new WidgetChoiceStep(questHelper.getConfig(), text, groupID, childID);
		newChoice.setGroupIdForChecking(groupIDForChecking);
		widgetChoices.addChoice(newChoice);
		return this;

	}

	public QuestStep addWidgetChoice(int id, int groupID, int childID)
	{
		widgetChoices.addChoice(new WidgetChoiceStep(questHelper.getConfig(), id, groupID, childID));
		return this;
	}

	public QuestStep addWidgetChange(String choice, int groupID, int childID, String newText)
	{
		widgetChoices.addChoice(new WidgetTextChange(questHelper.getConfig(), choice, groupID, childID, newText));
		return this;
	}

	@Subscribe
	public void onChatMessage(ChatMessage chatMessage)
	{
		if (chatMessage.getType() == ChatMessageType.DIALOG)
		{
			lastDialogSeen = chatMessage.getMessage();
		}
	}

	public void clearWidgetHighlights() {
		widgetsToHighlight.clear();
	}

	public QuestStep addSpellHighlight(Spell spell)
	{
		widgetsToHighlight.add(new SpellWidgetHighlight(spell));
		return this;
	}

	public QuestStep addWidgetHighlight(WidgetHighlight widgetHighlight)
	{
		widgetsToHighlight.add(widgetHighlight);
		return this;
	}

	public QuestStep addWidgetHighlight(int groupID, int childID)
	{
		widgetsToHighlight.add(new WidgetHighlight(groupID, childID));
		return this;
	}

	public QuestStep addWidgetHighlight(int interfaceID)
	{
		widgetsToHighlight.add(new WidgetHighlight(interfaceID));
		return this;
	}

	public QuestStep addWidgetHighlight(int groupID, int childID, int childChildID)
	{
		widgetsToHighlight.add(new WidgetHighlight(groupID, childID, childChildID));
		return this;
	}

	public QuestStep addWidgetHighlightWithItemIdRequirement(int groupID, int childID, int itemID, boolean checkChildren)
	{
		widgetsToHighlight.add(new WidgetHighlight(groupID, childID, itemID, checkChildren));
		return this;
	}

	public void addWidgetHighlightWithTextRequirement(int groupID, int childID, String requiredText, boolean checkChildren)
	{
		widgetsToHighlight.add(new WidgetHighlight(groupID, childID, requiredText, checkChildren));
	}

	public void makeOverlayHint(PanelComponent panelComponent, QuestHelperPlugin plugin, @NonNull List<String> additionalText, @NonNull List<Requirement> additionalRequirements)
	{
		addTitleToPanel(panelComponent);

		additionalText.stream()
			.filter(s -> !s.isEmpty())
			.forEach(line -> addTextToPanel(panelComponent, line));

		if (text != null && (text.size() > 0 && !text.get(0).isEmpty()))
		{
			addTextToPanel(panelComponent, "");
		}

		if (!overlayText.isEmpty())
		{
			overlayText.stream()
				.filter(s -> !s.isEmpty())
				.forEach(line -> addTextToPanel(panelComponent, line));
		}
		else if (text != null)
		{
			text.stream()
				.filter(s -> !s.isEmpty())
				.forEach(line -> addTextToPanel(panelComponent, line));
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

	public QuestStep addIcon(int iconItemID)
	{
		this.iconItemID = iconItemID;
		return this;
	}

	public void makeWorldOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin)
	{
	}

	public void makeWorldArrowOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin)
	{
	}

	public void makeWorldLineOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin)
	{
	}

	public void makeWidgetOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin)
	{
	}

	public void makeDirectionOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin)
	{
	}

	public void setLockedManually(boolean isLocked)
	{
		locked = isLocked;
	}

	public boolean isLocked()
	{
		boolean autoLocked = lockingCondition != null && lockingCondition.check(client);
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

	public boolean containsSteps(QuestStep questStep, Set<QuestStep> checkedSteps)
	{
		if (checkedSteps.contains(this)) return false;
		checkedSteps.add(this);
		return this == questStep || this.getSubsteps().stream().anyMatch((subStep) ->
		{
			if (subStep == null)
			{
				log.warn("Substep null for " + getText());
				return false;
			}
			return subStep.containsSteps(questStep, checkedSteps);
		});
	}

	protected void setupIcon()
	{
		if (iconItemID != -1 && icon == null)
		{
			icon = IconOverlay.createIconImage(itemManager.getImage(iconItemID));
		}
		else if (icon == null)
		{
			icon = getQuestImage();
		}
	}

	public void conditionToHideInSidebar(Requirement hideCondition)
	{
		conditionToHide = hideCondition;
	}

	public void conditionToFadeInSidebar(Requirement fadeCondition)
	{
		this.fadeCondition = fadeCondition;
	}

	public BufferedImage getQuestImage()
	{
		return spriteManager.getSprite(SpriteID.SideiconsInterface.QUESTS, 0);
	}


	public void renderQuestStepTooltip(PanelComponent panelComponent, boolean isMenuOpen, boolean isBackgroundHelper)
	{
		String tooltipText = isBackgroundHelper ? getBackgroundWorldTooltipText() : getWorldTooltipText();
		if (tooltipText == null) return;

		if (isMenuOpen)
		{
			renderHoveredItemTooltip(tooltipText);
		}
		else
		{
			renderHoveredMenuEntryPanel(panelComponent, tooltipText);
		}
	}

	protected Widget getInventoryWidget()
	{
		return client.getWidget(InterfaceID.Inventory.ITEMS);
	}

	protected void renderInventory(Graphics2D graphics, WorldPoint worldPoint, List<ItemRequirement> passedRequirements, boolean distanceLimit)
	{
		Widget inventoryWidget = getInventoryWidget();
		if (inventoryWidget == null || inventoryWidget.isHidden())
		{
			return;
		}

		Color baseColor = questHelper.getConfig().targetOverlayColor();

		if (passedRequirements.isEmpty()) return;
		if (inventoryWidget.getDynamicChildren() == null) return;


		for (Widget item : inventoryWidget.getDynamicChildren())
		{
			for (Requirement requirement : passedRequirements)
			{
				if (distanceLimit)
				{
					WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();
					WorldPoint goalWp = QuestPerspective.getWorldPointConsideringWorldView(client, client.getTopLevelWorldView(), worldPoint);
					if (goalWp != null && playerLocation.distanceTo(goalWp) <= 100) continue;
				}

				if (isValidRequirementForRenderInInventory(requirement, item))
				{
					highlightInventoryItem(item, baseColor, graphics);
				}
			}
		}
	}

	private void highlightInventoryItem(Widget item, Color color, Graphics2D graphics)
	{
		Rectangle slotBounds = item.getBounds();
		switch (questHelper.getConfig().highlightStyleInventoryItems())
		{
			case SQUARE:
				graphics.setColor(ColorUtil.colorWithAlpha(color, 65));
				graphics.fill(slotBounds);
				graphics.setColor(color);
				graphics.draw(slotBounds);
				break;
			case OUTLINE:
				BufferedImage outlined = itemManager.getItemOutline(item.getItemId(), item.getItemQuantity(), color);
				graphics.drawImage(outlined, (int) slotBounds.getX(), (int) slotBounds.getY(), null);
				break;
			case FILLED_OUTLINE:
				BufferedImage outline = itemManager.getItemOutline(item.getItemId(), item.getItemQuantity(), color);
				graphics.drawImage(outline, (int) slotBounds.getX(), (int) slotBounds.getY(), null);
				Image image = ImageUtil.fillImage(itemManager.getImage(item.getItemId(), item.getItemQuantity(), false), ColorUtil.colorWithAlpha(color, 65));
				graphics.drawImage(image, (int) slotBounds.getX(), (int) slotBounds.getY(), null);
				break;
			default:
		}
	}

	private boolean isValidRequirementForRenderInInventory(Requirement requirement, Widget item)
	{
		return requirement instanceof ItemRequirement && isValidRenderRequirementInInventory((ItemRequirement) requirement, item);
	}

	protected boolean isValidRenderRequirementInInventory(ItemRequirement requirement, Widget item)
	{
		return (requirement.shouldHighlightInInventory(client)) && requirement.getAllIds().contains(item.getItemId());
	}

	protected void renderHoveredItemTooltip(String tooltipText)
	{
	}

	protected void renderHoveredMenuEntryPanel(PanelComponent panelComponent, String tooltipText)
	{
	}

	public void setShortestPath()
	{
	}

	public void removeShortestPath()
	{
	}

	public void disableShortestPath()
	{
	}

	public PuzzleWrapperStep puzzleWrapStep()
	{
		return new PuzzleWrapperStep(getQuestHelper(), this);
	}

	public PuzzleWrapperStep puzzleWrapStep(QuestStep questStep)
	{
		return new PuzzleWrapperStep(getQuestHelper(), this, questStep);
	}

	public PuzzleWrapperStep puzzleWrapStep(QuestStep questStep, boolean hiddenInSidebar)
	{
		return new PuzzleWrapperStep(getQuestHelper(), this, questStep).withNoHelpHiddenInSidebar(hiddenInSidebar);
	}

	public PuzzleWrapperStep puzzleWrapStep(String alternateText)
	{
		return new PuzzleWrapperStep(getQuestHelper(), this, alternateText);
	}

	/// Wraps this step in a PuzzleWrapperStep with the given alternate text and the default text on a new line.
	public PuzzleWrapperStep puzzleWrapStepWithDefaultText(String alternateText)
	{
		return new PuzzleWrapperStep(getQuestHelper(), this, alternateText + "\n" + PuzzleWrapperStep.DEFAULT_TEXT);
	}

	public PuzzleWrapperStep puzzleWrapStep(boolean hiddenInSidebar)
	{
		return new PuzzleWrapperStep(getQuestHelper(), this).withNoHelpHiddenInSidebar(hiddenInSidebar);
	}
}
