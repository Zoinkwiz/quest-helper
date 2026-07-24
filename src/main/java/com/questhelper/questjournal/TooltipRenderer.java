/*
 * Copyright (c) 2026, nanopink
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

package com.questhelper.questjournal;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.components.TooltipComponent;

/** Renders cursor-following tooltips and caches wrapped layouts. */
final class TooltipRenderer
{
	private static final int VIEWPORT_MARGIN = 4;
	private static final int CURSOR_OFFSET = 12;
	private static final int BLOCK_GAP = 2;
	private static final int COMPONENT_PADDING = 8;
	private static final int MOD_ICON_WIDTH = 13;
	private static final int MAX_CONTENT_WIDTH = 280;
	private static final Pattern BREAK_PATTERN =
		Pattern.compile("(?i)</?br\\s*/?>");
	private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");
	private static final String SETTINGS_TOOLTIP =
		"Open RuneLite Quest Helper settings";

	private final JournalOverlay overlay;
	private final Client client;
	private final ChromeRenderer chromeRenderer;
	private volatile TooltipLayoutCache layoutCache;

	TooltipRenderer(
		JournalOverlay overlay,
		Client client,
		ChromeRenderer chromeRenderer)
	{
		this.overlay = Objects.requireNonNull(overlay, "overlay");
		this.client = client;
		this.chromeRenderer = Objects.requireNonNull(
			chromeRenderer,
			"chromeRenderer");
	}

	void drawSettingsTooltip(
		Graphics2D graphics,
		Rectangle settingsBounds,
		Rectangle viewport,
		Point pointer)
	{
		if (pointer == null
			|| settingsBounds == null
			|| !settingsBounds.contains(pointer))
		{
			return;
		}
		drawCursorTooltip(graphics, viewport, pointer, settingsTooltipText());
	}

	void drawSemanticFeedback(
		Graphics2D graphics,
		Rectangle viewport,
		Point pointer,
		List<SemanticHit> semanticHits)
	{
		SemanticHit hit = semanticHitAt(pointer, semanticHits);
		if (hit == null)
		{
			return;
		}

		if (!hit.underlineBounds.isEmpty())
		{
			graphics.setColor(hit.underlineColor);
			for (Rectangle underline : hit.underlineBounds)
			{
				graphics.fillRect(
					underline.x,
					underline.y,
					underline.width,
					underline.height);
			}
		}
		if (!hit.checklistId.isEmpty())
		{
			chromeRenderer.drawHeaderControlHoverEdges(graphics, hit.bounds);
		}
		if (!hit.tooltipBlocks.isEmpty())
		{
			drawCursorTooltipBlocks(
				graphics,
				viewport,
				pointer,
				hit.tooltipBlocks);
		}
	}

	void drawMarkerTooltip(
		Graphics2D graphics,
		Rectangle viewport,
		Point pointer,
		List<MarkerTooltipHit> hits,
		boolean popupOpen)
	{
		if (pointer == null || hits == null || hits.isEmpty())
		{
			return;
		}
		for (int index = hits.size() - 1; index >= 0; index--)
		{
			MarkerTooltipHit hit = hits.get(index);
			if (hit.popup == popupOpen && hit.bounds.contains(pointer))
			{
				drawCursorTooltip(
					graphics,
					viewport,
					pointer,
					hit.tooltip);
				return;
			}
		}
	}

	void clear()
	{
		layoutCache = null;
	}

	private void drawCursorTooltip(
		Graphics2D graphics,
		Rectangle viewport,
		Point pointer,
		String text)
	{
		drawCursorTooltipBlocks(
			graphics,
			viewport,
			pointer,
			text == null || text.isEmpty()
				? Collections.emptyList()
				: Collections.singletonList(text));
	}

	private void drawCursorTooltipBlocks(
		Graphics2D graphics,
		Rectangle viewport,
		Point pointer,
		List<String> texts)
	{
		if (graphics == null
			|| viewport == null
			|| viewport.isEmpty()
			|| pointer == null
			|| texts == null
			|| texts.isEmpty()
			|| client != null && client.isMenuOpen())
		{
			return;
		}
		Font oldFont = graphics.getFont();
		try
		{
			graphics.setFont(JournalOverlay.contentFont());
			int maximumContentWidth = tooltipContentWidth(viewport);
			TooltipLayoutCache layout = cachedLayout(
				graphics,
				texts,
				maximumContentWidth);
			List<Rectangle> bounds = cursorTooltipBlockBounds(
				pointer,
				layout.sizes,
				viewport);
			if (bounds.isEmpty())
			{
				return;
			}
			for (int index = 0; index < bounds.size(); index++)
			{
				TooltipComponent tooltip = new TooltipComponent();
				tooltip.setText(layout.visibleTexts.get(index));
				tooltip.setPreferredLocation(bounds.get(index).getLocation());
				if (client != null)
				{
					tooltip.setModIcons(client.getModIcons());
				}
				tooltip.render(graphics);
			}
		}
		finally
		{
			graphics.setFont(oldFont);
		}
	}

	private TooltipLayoutCache cachedLayout(
		Graphics2D graphics,
		List<String> texts,
		int maximumContentWidth)
	{
		Font font = graphics.getFont();
		FontRenderContext fontRenderContext = graphics.getFontRenderContext();
		TooltipLayoutCache cached = layoutCache;
		if (cached != null && cached.matches(
			texts,
			maximumContentWidth,
			font,
			fontRenderContext))
		{
			return cached;
		}

		FontMetrics metrics = graphics.getFontMetrics();
		List<String> visibleTexts = new ArrayList<>(texts.size());
		List<Dimension> sizes = new ArrayList<>(texts.size());
		for (String text : texts)
		{
			if (text != null && !text.isEmpty())
			{
				String wrappedText = wrapTooltipText(
					metrics,
					text,
					maximumContentWidth);
				visibleTexts.add(wrappedText);
				sizes.add(tooltipSize(metrics, wrappedText));
			}
		}
		TooltipLayoutCache replacement = new TooltipLayoutCache(
			texts,
			visibleTexts,
			sizes,
			maximumContentWidth,
			font,
			fontRenderContext);
		layoutCache = replacement;
		return replacement;
	}

	static String settingsTooltipText()
	{
		return SETTINGS_TOOLTIP;
	}

	static Rectangle cursorTooltipBounds(
		Point pointer,
		Dimension tooltipSize,
		Rectangle viewport)
	{
		Objects.requireNonNull(pointer, "pointer");
		Objects.requireNonNull(tooltipSize, "tooltipSize");
		Objects.requireNonNull(viewport, "viewport");
		if (viewport.isEmpty())
		{
			return new Rectangle();
		}

		int horizontalMargin = Math.min(
			VIEWPORT_MARGIN,
			Math.max(0, viewport.width / 2));
		int verticalMargin = Math.min(
			VIEWPORT_MARGIN,
			Math.max(0, viewport.height / 2));
		int width = Math.min(
			Math.max(0, tooltipSize.width),
			Math.max(0, viewport.width - horizontalMargin * 2));
		int height = Math.min(
			Math.max(0, tooltipSize.height),
			Math.max(0, viewport.height - verticalMargin * 2));
		int minimumX = viewport.x + horizontalMargin;
		int minimumY = viewport.y + verticalMargin;
		int maximumX = Math.max(
			minimumX,
			viewport.x + viewport.width - horizontalMargin - width);
		int maximumY = Math.max(
			minimumY,
			viewport.y + viewport.height - verticalMargin - height);

		long rightCandidate = (long) pointer.x + CURSOR_OFFSET;
		int x = rightCandidate + width
			<= (long) viewport.x + viewport.width - horizontalMargin
				? (int) rightCandidate
				: pointer.x - CURSOR_OFFSET - width;
		long belowCandidate = (long) pointer.y + CURSOR_OFFSET;
		int y = belowCandidate + height
			<= (long) viewport.y + viewport.height - verticalMargin
				? (int) belowCandidate
				: pointer.y - CURSOR_OFFSET - height;
		return new Rectangle(
			JournalOverlay.clamp(x, minimumX, maximumX),
			JournalOverlay.clamp(y, minimumY, maximumY),
			width,
			height);
	}

	static List<Rectangle> cursorTooltipBlockBounds(
		Point pointer,
		List<Dimension> tooltipSizes,
		Rectangle viewport)
	{
		Objects.requireNonNull(tooltipSizes, "tooltipSizes");
		if (tooltipSizes.isEmpty())
		{
			return Collections.emptyList();
		}
		int width = 0;
		int height = 0;
		for (Dimension size : tooltipSizes)
		{
			if (size == null)
			{
				continue;
			}
			width = Math.max(width, Math.max(0, size.width));
			height += Math.max(0, size.height);
		}
		height += Math.max(0, tooltipSizes.size() - 1) * BLOCK_GAP;
		Rectangle envelope = cursorTooltipBounds(
			pointer,
			new Dimension(width, height),
			viewport);
		if (envelope.isEmpty())
		{
			return Collections.emptyList();
		}
		List<Rectangle> bounds = new ArrayList<>(tooltipSizes.size());
		int y = envelope.y;
		for (Dimension size : tooltipSizes)
		{
			int blockWidth = size == null
				? 0
				: Math.min(envelope.width, Math.max(0, size.width));
			int blockHeight = size == null ? 0 : Math.max(0, size.height);
			bounds.add(new Rectangle(
				envelope.x,
				y,
				blockWidth,
				blockHeight));
			y += blockHeight + BLOCK_GAP;
		}
		return Collections.unmodifiableList(bounds);
	}

	static int tooltipContentWidth(Rectangle viewport)
	{
		Objects.requireNonNull(viewport, "viewport");
		int horizontalMargin = Math.min(
			VIEWPORT_MARGIN,
			Math.max(0, viewport.width / 2));
		int availableWidth =
			viewport.width - horizontalMargin * 2 - COMPONENT_PADDING;
		return Math.max(1, Math.min(MAX_CONTENT_WIDTH, availableWidth));
	}

	static String wrapTooltipText(
		FontMetrics metrics,
		String tooltip,
		int maximumContentWidth)
	{
		Objects.requireNonNull(metrics, "metrics");
		if (tooltip == null || tooltip.isEmpty())
		{
			return "";
		}

		int widthLimit = Math.max(1, maximumContentWidth);
		String normalized = tooltip
			.replace("\r\n", "<br>")
			.replace('\r', '\n')
			.replace("\n", "<br>");
		normalized = BREAK_PATTERN.matcher(normalized).replaceAll("<br>");
		String[] logicalLines = BREAK_PATTERN.split(normalized, -1);
		StringBuilder wrapped = new StringBuilder(normalized.length() + 16);
		for (int lineIndex = 0; lineIndex < logicalLines.length; lineIndex++)
		{
			if (lineIndex > 0)
			{
				wrapped.append("<br>");
			}
			wrapTooltipLine(
				metrics,
				logicalLines[lineIndex],
				widthLimit,
				wrapped);
		}
		return wrapped.toString();
	}

	static Dimension tooltipSize(FontMetrics metrics, String tooltip)
	{
		String[] lines = BREAK_PATTERN.split(tooltip);
		int width = 0;
		for (String line : lines)
		{
			width = Math.max(width, tooltipLineWidth(metrics, line));
		}
		return new Dimension(
			width + COMPONENT_PADDING,
			Math.max(1, lines.length) * metrics.getHeight()
				+ COMPONENT_PADDING);
	}

	static String tooltipLineBreaks(String text)
	{
		return text == null
			? ""
			: text.trim()
				.replace("\r\n", "<br>")
				.replace("\r", "<br>")
				.replace("\n", "<br>");
	}

	static SemanticHit semanticHitAt(
		Point point,
		List<SemanticHit> semanticHits)
	{
		if (point == null || semanticHits == null || semanticHits.isEmpty())
		{
			return null;
		}
		for (SemanticHit hit : semanticHits)
		{
			if (hit.bounds.contains(point))
			{
				return hit;
			}
		}
		return null;
	}

	private static void wrapTooltipLine(
		FontMetrics metrics,
		String line,
		int maximumContentWidth,
		StringBuilder wrapped)
	{
		String trimmed = line.trim();
		if (trimmed.isEmpty())
		{
			return;
		}

		int currentWidth = 0;
		int spaceWidth = metrics.charWidth(' ');
		for (String word : WHITESPACE_PATTERN.split(trimmed))
		{
			int wordWidth = tooltipLineWidth(metrics, word);
			if (currentWidth > 0
				&& currentWidth + spaceWidth + wordWidth > maximumContentWidth)
			{
				wrapped.append("<br>");
				currentWidth = 0;
			}
			else if (currentWidth > 0)
			{
				wrapped.append(' ');
				currentWidth += spaceWidth;
			}

			currentWidth = appendTooltipWord(
				metrics,
				word,
				maximumContentWidth,
				currentWidth,
				wrapped);
		}
	}

	private static int appendTooltipWord(
		FontMetrics metrics,
		String word,
		int maximumContentWidth,
		int currentWidth,
		StringBuilder wrapped)
	{
		for (int index = 0; index < word.length();)
		{
			if (word.charAt(index) == '<')
			{
				int tagEnd = word.indexOf('>', index + 1);
				if (tagEnd >= 0)
				{
					String tag = word.substring(index, tagEnd + 1);
					int tagWidth = tooltipTagWidth(metrics, tag);
					if (currentWidth > 0
						&& tagWidth > 0
						&& currentWidth + tagWidth > maximumContentWidth)
					{
						wrapped.append("<br>");
						currentWidth = 0;
					}
					wrapped.append(tag);
					currentWidth += tagWidth;
					index = tagEnd + 1;
					continue;
				}
			}

			int codePoint = word.codePointAt(index);
			int characterCount = Character.charCount(codePoint);
			String character = word.substring(index, index + characterCount);
			int characterWidth = metrics.stringWidth(character);
			if (currentWidth > 0
				&& currentWidth + characterWidth > maximumContentWidth)
			{
				wrapped.append("<br>");
				currentWidth = 0;
			}
			wrapped.append(character);
			currentWidth += characterWidth;
			index += characterCount;
		}
		return currentWidth;
	}

	private static int tooltipLineWidth(FontMetrics metrics, String line)
	{
		int width = 0;
		int textStart = 0;
		for (int index = 0; index < line.length();)
		{
			if (line.charAt(index) != '<')
			{
				index++;
				continue;
			}

			width += metrics.stringWidth(line.substring(textStart, index));
			int tagEnd = line.indexOf('>', index + 1);
			if (tagEnd < 0)
			{
				return width + metrics.stringWidth(line.substring(index));
			}
			width += tooltipTagWidth(
				metrics,
				line.substring(index, tagEnd + 1));
			index = tagEnd + 1;
			textStart = index;
		}
		return width + metrics.stringWidth(line.substring(textStart));
	}

	private static int tooltipTagWidth(FontMetrics metrics, String tag)
	{
		String normalized = tag.toLowerCase(Locale.ROOT);
		if (normalized.startsWith("<img=") && normalized.endsWith(">"))
		{
			return MOD_ICON_WIDTH;
		}
		if ((normalized.startsWith("<col=") || normalized.equals("</col>"))
			&& normalized.endsWith(">"))
		{
			return 0;
		}
		return metrics.stringWidth(tag);
	}

	static final class MarkerTooltipHit
	{
		final Rectangle bounds;
		final String tooltip;
		final boolean popup;

		MarkerTooltipHit(Rectangle bounds, String tooltip, boolean popup)
		{
			this.bounds = new Rectangle(bounds);
			this.tooltip = tooltip == null ? "" : tooltip;
			this.popup = popup;
		}
	}

	static final class SemanticHit
	{
		final Rectangle bounds;
		final String linkedQuestId;
		final String wikiUrl;
		final List<String> tooltipBlocks;
		final List<Rectangle> underlineBounds;
		final Color underlineColor;
		final String checklistId;

		SemanticHit(
			Rectangle bounds,
			String linkedQuestId,
			String wikiUrl,
			List<String> tooltipBlocks,
			List<Rectangle> underlineBounds,
			Color underlineColor)
		{
			this(
				bounds,
				linkedQuestId,
				wikiUrl,
				tooltipBlocks,
				underlineBounds,
				underlineColor,
				"");
		}

		private SemanticHit(
			Rectangle bounds,
			String linkedQuestId,
			String wikiUrl,
			List<String> tooltipBlocks,
			List<Rectangle> underlineBounds,
			Color underlineColor,
			String checklistId)
		{
			this.bounds = new Rectangle(bounds);
			this.linkedQuestId = linkedQuestId == null ? "" : linkedQuestId;
			this.wikiUrl = wikiUrl == null ? "" : wikiUrl;
			List<String> copiedTooltipBlocks = new ArrayList<>();
			if (tooltipBlocks != null)
			{
				for (String tooltipBlock : tooltipBlocks)
				{
					if (tooltipBlock != null && !tooltipBlock.isEmpty())
					{
						copiedTooltipBlocks.add(tooltipBlock);
					}
				}
			}
			this.tooltipBlocks = Collections.unmodifiableList(
				copiedTooltipBlocks);
			List<Rectangle> copiedUnderlines =
				new ArrayList<>(underlineBounds.size());
			for (Rectangle underline : underlineBounds)
			{
				copiedUnderlines.add(new Rectangle(underline));
			}
			this.underlineBounds = Collections.unmodifiableList(
				copiedUnderlines);
			this.underlineColor = underlineColor == null
				? JournalOverlay.ACCENT
				: underlineColor;
			this.checklistId = checklistId == null ? "" : checklistId;
		}

		static SemanticHit checklistToggle(
			Rectangle bounds,
			String checklistId)
		{
			return new SemanticHit(
				bounds,
				"",
				"",
				Collections.emptyList(),
				Collections.emptyList(),
				JournalOverlay.ACCENT,
				checklistId);
		}

		SemanticHit translated(int deltaX, int deltaY)
		{
			List<Rectangle> translatedUnderlines =
				new ArrayList<>(underlineBounds.size());
			for (Rectangle underline : underlineBounds)
			{
				translatedUnderlines.add(new Rectangle(
					underline.x + deltaX,
					underline.y + deltaY,
					underline.width,
					underline.height));
			}
			return new SemanticHit(
				new Rectangle(
					bounds.x + deltaX,
					bounds.y + deltaY,
					bounds.width,
					bounds.height),
				linkedQuestId,
				wikiUrl,
				tooltipBlocks,
				translatedUnderlines,
				underlineColor,
				checklistId);
		}
	}

	private static final class TooltipLayoutCache
	{
		private final List<String> sourceTexts;
		private final List<String> visibleTexts;
		private final List<Dimension> sizes;
		private final int maximumContentWidth;
		private final Font font;
		private final FontRenderContext fontRenderContext;

		private TooltipLayoutCache(
			List<String> sourceTexts,
			List<String> visibleTexts,
			List<Dimension> sizes,
			int maximumContentWidth,
			Font font,
			FontRenderContext fontRenderContext)
		{
			this.sourceTexts = Collections.unmodifiableList(
				new ArrayList<>(sourceTexts));
			this.visibleTexts = Collections.unmodifiableList(
				new ArrayList<>(visibleTexts));
			this.sizes = Collections.unmodifiableList(new ArrayList<>(sizes));
			this.maximumContentWidth = maximumContentWidth;
			this.font = font;
			this.fontRenderContext = fontRenderContext;
		}

		private boolean matches(
			List<String> requestedTexts,
			int requestedMaximumContentWidth,
			Font requestedFont,
			FontRenderContext requestedFontRenderContext)
		{
			return maximumContentWidth == requestedMaximumContentWidth
				&& sourceTexts.equals(requestedTexts)
				&& font.equals(requestedFont)
				&& fontRenderContext.equals(requestedFontRenderContext);
		}
	}
}
