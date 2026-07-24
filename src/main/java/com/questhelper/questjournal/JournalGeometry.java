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

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Objects;

final class JournalGeometry
{
	private static final int DEFAULT_WIDTH = 790;
	private static final int DEFAULT_HEIGHT = 490;
	private static final int MINIMUM_WIDTH = 420;
	private static final int MINIMUM_HEIGHT = 300;
	private static final int HEADER_HEIGHT = 30;
	private static final int TITLE_SEPARATOR_HEIGHT = 6;
	private static final int HEADER_CONTROL_SIZE = 21;
	private static final int HEADER_CONTROL_HORIZONTAL_PADDING = 10;
	private static final int HEADER_CONTROL_GAP = 4;
	private static final int HEADER_TOP_FRAME_INSET = 7;
	private static final int HEADER_SEPARATOR_OVERLAP = 2;
	private static final int BODY_PADDING = 6;
	private static final int PANE_GAP = 1;
	private static final int QUEST_LIST_WIDTH = 212;
	private static final int WIDE_LAYOUT_WIDTH = 760;
	private static final int DETAIL_PANE_WIDTH = 220;
	private static final int QUEST_LIST_FILTER_HEADER_HEIGHT = 180;
	private static final int QUEST_LIST_COMPACT_HEADER_HEIGHT = 58;
	private static final int QUEST_LIST_ACTIVE_CONTROL_SPACE = 30;
	private static final int PANE_HEADER_HEIGHT = 36;
	private static final int COMPACT_HEADER_HEIGHT = 36;
	private static final int CONTENT_INSET = 5;
	private static final int RESIZE_HANDLE_SIZE = 16;
	private static final int RESIZE_HANDLE_HIT_SIZE = 20;

	private final Rectangle requestedBounds;
	private final Rectangle panelBounds;
	private final Rectangle headerBounds;
	private final Rectangle closeButtonBounds;
	private final Rectangle maximizeButtonBounds;
	private final Rectangle resizeHandleBounds;
	private final Rectangle questListPaneBounds;
	private final Rectangle mainPaneBounds;
	private final Rectangle detailPaneBounds;
	private final Rectangle compactHeaderBounds;
	private final Rectangle questListContentBounds;
	private final Rectangle mainContentBounds;
	private final Rectangle detailContentBounds;
	private final boolean detailPaneVisible;

	private JournalGeometry(
		Rectangle requestedBounds,
		Rectangle panelBounds,
		Rectangle headerBounds,
		Rectangle closeButtonBounds,
		Rectangle maximizeButtonBounds,
		Rectangle resizeHandleBounds,
		Rectangle questListPaneBounds,
		Rectangle mainPaneBounds,
		Rectangle detailPaneBounds,
		Rectangle compactHeaderBounds,
		Rectangle questListContentBounds,
		Rectangle mainContentBounds,
		Rectangle detailContentBounds,
		boolean detailPaneVisible)
	{
		this.requestedBounds = copy(requestedBounds);
		this.panelBounds = copy(panelBounds);
		this.headerBounds = copy(headerBounds);
		this.closeButtonBounds = copy(closeButtonBounds);
		this.maximizeButtonBounds = copy(maximizeButtonBounds);
		this.resizeHandleBounds = copy(resizeHandleBounds);
		this.questListPaneBounds = copy(questListPaneBounds);
		this.mainPaneBounds = copy(mainPaneBounds);
		this.detailPaneBounds = copy(detailPaneBounds);
		this.compactHeaderBounds = copy(compactHeaderBounds);
		this.questListContentBounds = copy(questListContentBounds);
		this.mainContentBounds = copy(mainContentBounds);
		this.detailContentBounds = copy(detailContentBounds);
		this.detailPaneVisible = detailPaneVisible;
	}

	static JournalGeometry create(Rectangle requestedBounds, Rectangle viewportBounds)
	{
		return create(requestedBounds, viewportBounds, false, true);
	}

	static JournalGeometry create(
		Rectangle requestedBounds,
		Rectangle viewportBounds,
		boolean filtersVisible)
	{
		return create(requestedBounds, viewportBounds, filtersVisible, true);
	}

	static JournalGeometry create(
		Rectangle requestedBounds,
		Rectangle viewportBounds,
		boolean filtersVisible,
		boolean manualActiveControls)
	{
		Rectangle requested = copy(Objects.requireNonNull(requestedBounds, "requestedBounds"));
		Rectangle panel = clampPanelBounds(
			requested,
			Objects.requireNonNull(viewportBounds, "viewportBounds"));
		Rectangle header = new Rectangle(
			panel.x,
			panel.y,
			panel.width,
			Math.min(HEADER_HEIGHT, panel.height));

		Rectangle close = headerControl(header, 0);
		Rectangle maximize = headerControl(header, 1);
		Rectangle resizeHandle = resizeHandle(panel, RESIZE_HANDLE_SIZE);

		int bodyY = safeAdd(panel.y, header.height);
		Rectangle body = new Rectangle(
			panel.x,
			bodyY,
			panel.width,
			Math.max(0, panel.height - header.height));
		Rectangle paneArea = inset(body, BODY_PADDING);
		int firstGap = Math.min(PANE_GAP, paneArea.width);
		int paneWidthAfterGap = Math.max(0, paneArea.width - firstGap);
		int questListWidth = Math.min(
			QUEST_LIST_WIDTH,
			paneWidthAfterGap * 2 / 5);
		Rectangle questListPane = new Rectangle(
			paneArea.x,
			paneArea.y,
			questListWidth,
			paneArea.height);

		int mainX = safeAdd(paneArea.x, safeAdd(questListWidth, firstGap));
		int remainingWidth = Math.max(0, paneArea.width - questListWidth - firstGap);
		boolean showDetailPane = panel.width >= WIDE_LAYOUT_WIDTH && remainingWidth > 0;
		Rectangle mainPane;
		Rectangle detailPane;
		Rectangle compactHeader;
		Rectangle mainContent;
		Rectangle detailContent;

		if (showDetailPane)
		{
			int detailGap = Math.min(PANE_GAP, remainingWidth);
			int splitWidth = Math.max(0, remainingWidth - detailGap);
			int detailWidth = Math.min(DETAIL_PANE_WIDTH, splitWidth * 2 / 5);
			int mainWidth = Math.max(0, splitWidth - detailWidth);
			mainPane = new Rectangle(mainX, paneArea.y, mainWidth, paneArea.height);
			detailPane = new Rectangle(
				safeAdd(mainX, safeAdd(mainWidth, detailGap)),
				paneArea.y,
				detailWidth,
				paneArea.height);
			compactHeader = emptyAt(mainPane.x, mainPane.y);
			mainContent = scrollContent(mainPane, PANE_HEADER_HEIGHT);
			detailContent = scrollContent(detailPane, 0);
		}
		else
		{
			mainPane = new Rectangle(mainX, paneArea.y, remainingWidth, paneArea.height);
			detailPane = emptyAt(safeAdd(mainPane.x, mainPane.width), mainPane.y);
			compactHeader = new Rectangle(
				mainPane.x,
				mainPane.y,
				mainPane.width,
				Math.min(COMPACT_HEADER_HEIGHT, mainPane.height));
			mainContent = scrollContent(mainPane, compactHeader.height);
			detailContent = copy(mainContent);
		}

		int questListHeaderHeight = filtersVisible
			? QUEST_LIST_FILTER_HEADER_HEIGHT
			: QUEST_LIST_COMPACT_HEADER_HEIGHT;
		if (!manualActiveControls)
		{
			questListHeaderHeight = Math.max(
				0,
				questListHeaderHeight - QUEST_LIST_ACTIVE_CONTROL_SPACE);
		}
		Rectangle questListContent = scrollContent(
			questListPane,
			questListHeaderHeight);
		return new JournalGeometry(
			requested,
			panel,
			header,
			close,
			maximize,
			resizeHandle,
			questListPane,
			mainPane,
			detailPane,
			compactHeader,
			questListContent,
			mainContent,
			detailContent,
			showDetailPane);
	}

	static Rectangle clampPanelBounds(Rectangle requestedBounds, Rectangle viewportBounds)
	{
		Objects.requireNonNull(requestedBounds, "requestedBounds");
		Objects.requireNonNull(viewportBounds, "viewportBounds");
		int viewportWidth = Math.max(0, viewportBounds.width);
		int viewportHeight = Math.max(0, viewportBounds.height);
		int width = Math.min(Math.max(0, requestedBounds.width), viewportWidth);
		int height = Math.min(Math.max(0, requestedBounds.height), viewportHeight);
		long maximumX = (long) viewportBounds.x + viewportWidth - width;
		long maximumY = (long) viewportBounds.y + viewportHeight - height;
		int x = clamp(requestedBounds.x, viewportBounds.x, maximumX);
		int y = clamp(requestedBounds.y, viewportBounds.y, maximumY);
		return new Rectangle(x, y, width, height);
	}

	static Rectangle defaultPanelBounds(Rectangle contentBounds)
	{
		Objects.requireNonNull(contentBounds, "contentBounds");
		int availableWidth = Math.max(0, contentBounds.width);
		int availableHeight = Math.max(0, contentBounds.height);
		int width = Math.min(DEFAULT_WIDTH, availableWidth);
		int height = Math.min(DEFAULT_HEIGHT, availableHeight);
		return new Rectangle(
			contentBounds.x + Math.max(0, (availableWidth - width) / 2),
			contentBounds.y + Math.max(0, (availableHeight - height) / 2),
			width,
			height);
	}

	static Rectangle contentBounds(
		Rectangle canvasBounds,
		Iterable<Rectangle> preferredBounds,
		Rectangle viewportBounds)
	{
		Objects.requireNonNull(canvasBounds, "canvasBounds");
		Rectangle canvas = new Rectangle(
			canvasBounds.x,
			canvasBounds.y,
			Math.max(0, canvasBounds.width),
			Math.max(0, canvasBounds.height));
		if (preferredBounds != null)
		{
			for (Rectangle preferred : preferredBounds)
			{
				Rectangle clipped = clippedCandidate(preferred, canvas);
				if (!clipped.isEmpty())
				{
					return clipped;
				}
			}
		}

		Rectangle viewport = clippedCandidate(viewportBounds, canvas);
		return viewport.isEmpty() ? canvas : viewport;
	}

	static Rectangle dragRequestedBounds(
		Rectangle currentRequestedBounds,
		Point pointer,
		Point pointerOffset)
	{
		Objects.requireNonNull(currentRequestedBounds, "currentRequestedBounds");
		Objects.requireNonNull(pointer, "pointer");
		Objects.requireNonNull(pointerOffset, "pointerOffset");
		return new Rectangle(
			saturatingSubtract(pointer.x, pointerOffset.x),
			saturatingSubtract(pointer.y, pointerOffset.y),
			currentRequestedBounds.width,
			currentRequestedBounds.height);
	}

	static Rectangle dragBounds(
		Rectangle currentBounds,
		Point pointer,
		Point pointerOffset,
		Rectangle viewportBounds)
	{
		return clampPanelBounds(
			dragRequestedBounds(currentBounds, pointer, pointerOffset),
			viewportBounds);
	}

	static Rectangle resizeBoundsFromBottomRight(
		Rectangle currentBounds,
		Point pointer,
		Rectangle viewportBounds)
	{
		return resizeBoundsFromBottomRight(
			currentBounds,
			pointer,
			new Point(),
			viewportBounds);
	}

	static Rectangle resizeBoundsFromBottomRight(
		Rectangle currentBounds,
		Point pointer,
		Point pointerOffset,
		Rectangle viewportBounds)
	{
		Objects.requireNonNull(currentBounds, "currentBounds");
		Objects.requireNonNull(pointer, "pointer");
		Objects.requireNonNull(pointerOffset, "pointerOffset");
		Objects.requireNonNull(viewportBounds, "viewportBounds");
		int viewportWidth = Math.max(0, viewportBounds.width);
		int viewportHeight = Math.max(0, viewportBounds.height);
		long viewportRight = (long) viewportBounds.x + viewportWidth;
		long viewportBottom = (long) viewportBounds.y + viewportHeight;
		int x = clamp(currentBounds.x, viewportBounds.x, viewportRight);
		int y = clamp(currentBounds.y, viewportBounds.y, viewportBottom);
		long maximumWidth = Math.max(0L, viewportRight - x);
		long maximumHeight = Math.max(0L, viewportBottom - y);
		long requestedWidth = Math.max(
			MINIMUM_WIDTH,
			(long) pointer.x + pointerOffset.x - x);
		long requestedHeight = Math.max(
			MINIMUM_HEIGHT,
			(long) pointer.y + pointerOffset.y - y);
		int width = boundedDimension(Math.min(maximumWidth, requestedWidth));
		int height = boundedDimension(Math.min(maximumHeight, requestedHeight));
		return new Rectangle(x, y, width, height);
	}

	static Point resizeGrabOffset(Rectangle currentBounds, Point pointer)
	{
		Objects.requireNonNull(currentBounds, "currentBounds");
		Objects.requireNonNull(pointer, "pointer");
		long right = (long) currentBounds.x + Math.max(0, currentBounds.width);
		long bottom = (long) currentBounds.y + Math.max(0, currentBounds.height);
		return new Point(
			boundedCoordinate(right - pointer.x),
			boundedCoordinate(bottom - pointer.y));
	}

	static Rectangle clip(Rectangle bounds, Rectangle clippingBounds)
	{
		Objects.requireNonNull(bounds, "bounds");
		Objects.requireNonNull(clippingBounds, "clippingBounds");
		Rectangle normalizedClip = new Rectangle(
			clippingBounds.x,
			clippingBounds.y,
			Math.max(0, clippingBounds.width),
			Math.max(0, clippingBounds.height));
		Rectangle intersection = bounds.intersection(normalizedClip);
		return intersection.isEmpty()
			? emptyAt(normalizedClip.x, normalizedClip.y)
			: intersection;
	}

	static int minimumWidth()
	{
		return MINIMUM_WIDTH;
	}

	static int minimumHeight()
	{
		return MINIMUM_HEIGHT;
	}

	static int defaultWidth()
	{
		return DEFAULT_WIDTH;
	}

	static int defaultHeight()
	{
		return DEFAULT_HEIGHT;
	}

	static Rectangle visibleImageBounds(BufferedImage image)
	{
		int minX = image.getWidth();
		int minY = image.getHeight();
		int maxX = -1;
		int maxY = -1;
		for (int y = 0; y < image.getHeight(); y++)
		{
			for (int x = 0; x < image.getWidth(); x++)
			{
				if ((image.getRGB(x, y) >>> 24) == 0)
				{
					continue;
				}
				minX = Math.min(minX, x);
				minY = Math.min(minY, y);
				maxX = Math.max(maxX, x);
				maxY = Math.max(maxY, y);
			}
		}
		return maxX < minX || maxY < minY
			? new Rectangle(0, 0, image.getWidth(), image.getHeight())
			: new Rectangle(minX, minY, maxX - minX + 1, maxY - minY + 1);
	}

	Rectangle requestedBounds()
	{
		return copy(requestedBounds);
	}

	Rectangle panelBounds()
	{
		return copy(panelBounds);
	}

	Rectangle headerBounds()
	{
		return copy(headerBounds);
	}

	Rectangle draggableHeaderBounds()
	{
		int expandedHeight = (int) Math.min(
			Math.max(0, panelBounds.height),
			(long) headerBounds.height + TITLE_SEPARATOR_HEIGHT);
		return new Rectangle(
			headerBounds.x,
			headerBounds.y,
			headerBounds.width,
			expandedHeight);
	}

	static int titleSeparatorHeight()
	{
		return TITLE_SEPARATOR_HEIGHT;
	}

	static int contentPadding()
	{
		return CONTENT_INSET;
	}

	Rectangle closeButtonBounds()
	{
		return copy(closeButtonBounds);
	}

	Rectangle maximizeButtonBounds()
	{
		return copy(maximizeButtonBounds);
	}

	Rectangle resizeHandleBounds()
	{
		return copy(resizeHandleBounds);
	}

	Rectangle resizeHandleHitBounds()
	{
		return resizeHandle(panelBounds, RESIZE_HANDLE_HIT_SIZE);
	}

	Rectangle questListPaneBounds()
	{
		return copy(questListPaneBounds);
	}

	Rectangle mainPaneBounds()
	{
		return copy(mainPaneBounds);
	}

	Rectangle detailPaneBounds()
	{
		return copy(detailPaneBounds);
	}

	Rectangle compactHeaderBounds()
	{
		return copy(compactHeaderBounds);
	}

	Rectangle questListContentBounds()
	{
		return copy(questListContentBounds);
	}

	Rectangle mainContentBounds()
	{
		return copy(mainContentBounds);
	}

	Rectangle detailContentBounds()
	{
		return copy(detailContentBounds);
	}

	boolean hasDetailPane()
	{
		return detailPaneVisible;
	}

	private static Rectangle headerControl(Rectangle header, int indexFromRight)
	{
		Rectangle headerContent = headerContent(header);
		int horizontalPadding = Math.min(HEADER_CONTROL_HORIZONTAL_PADDING, header.width / 4);
		int availableHeight = headerContent.height;
		int availableWidth = Math.max(0, header.width - horizontalPadding * 2);
		int size = Math.min(HEADER_CONTROL_SIZE, Math.min(availableHeight, availableWidth));
		int requiredWidth = size * (indexFromRight + 1) + HEADER_CONTROL_GAP * indexFromRight;
		if (size <= 0 || requiredWidth > availableWidth)
		{
			return emptyAt(header.x, header.y);
		}
		int right = safeAdd(header.x, header.width - horizontalPadding);
		int x = right - size * (indexFromRight + 1) - HEADER_CONTROL_GAP * indexFromRight;
		int y = headerContent.y + (headerContent.height - size) / 2;
		return new Rectangle(x, y, size, size);
	}

	static Rectangle headerContent(Rectangle header)
	{
		Objects.requireNonNull(header, "header");
		int topInset = Math.min(HEADER_TOP_FRAME_INSET, Math.max(0, header.height));
		int height = Math.max(
			0,
			header.height - topInset - Math.min(HEADER_SEPARATOR_OVERLAP, header.height - topInset));
		return new Rectangle(header.x, safeAdd(header.y, topInset), header.width, height);
	}

	private static Rectangle resizeHandle(Rectangle panel, int requestedSize)
	{
		int size = Math.min(requestedSize, Math.min(panel.width, panel.height));
		if (size <= 0)
		{
			return emptyAt(panel.x, panel.y);
		}
		return new Rectangle(
			safeAdd(panel.x, panel.width - size),
			safeAdd(panel.y, panel.height - size),
			size,
			size);
	}

	private static Rectangle scrollContent(Rectangle pane, int reservedTop)
	{
		int safeReservedTop = Math.min(Math.max(0, reservedTop), pane.height);
		Rectangle contentArea = new Rectangle(
			pane.x,
			safeAdd(pane.y, safeReservedTop),
			pane.width,
			Math.max(0, pane.height - safeReservedTop));
		return inset(contentArea, CONTENT_INSET);
	}

	private static Rectangle inset(Rectangle bounds, int amount)
	{
		int horizontalInset = Math.min(Math.max(0, amount), bounds.width / 2);
		int verticalInset = Math.min(Math.max(0, amount), bounds.height / 2);
		return new Rectangle(
			safeAdd(bounds.x, horizontalInset),
			safeAdd(bounds.y, verticalInset),
			Math.max(0, bounds.width - horizontalInset * 2),
			Math.max(0, bounds.height - verticalInset * 2));
	}

	private static Rectangle emptyAt(int x, int y)
	{
		return new Rectangle(x, y, 0, 0);
	}

	private static Rectangle clippedCandidate(Rectangle candidate, Rectangle canvas)
	{
		if (candidate == null || candidate.width <= 0 || candidate.height <= 0)
		{
			return emptyAt(canvas.x, canvas.y);
		}
		Rectangle intersection = candidate.intersection(canvas);
		return intersection.isEmpty() ? emptyAt(canvas.x, canvas.y) : intersection;
	}

	private static Rectangle copy(Rectangle bounds)
	{
		return new Rectangle(bounds);
	}

	private static int clamp(int value, long minimum, long maximum)
	{
		return (int) Math.max(minimum, Math.min(maximum, value));
	}

	private static int safeAdd(int left, int right)
	{
		long result = (long) left + right;
		return (int) Math.max(Integer.MIN_VALUE, Math.min(Integer.MAX_VALUE, result));
	}

	private static int saturatingSubtract(int left, int right)
	{
		long result = (long) left - right;
		return (int) Math.max(Integer.MIN_VALUE, Math.min(Integer.MAX_VALUE, result));
	}

	private static int boundedDimension(long value)
	{
		return (int) Math.min(Integer.MAX_VALUE, Math.max(0, value));
	}

	private static int boundedCoordinate(long value)
	{
		return (int) Math.min(Integer.MAX_VALUE, Math.max(Integer.MIN_VALUE, value));
	}
}
