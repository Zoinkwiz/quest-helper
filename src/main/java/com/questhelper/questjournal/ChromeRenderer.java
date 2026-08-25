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

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.TexturePaint;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.Objects;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.FontManager;

/**
 * Renders the journal frame, title chrome, resize handle, and scrollbars.
 */
final class ChromeRenderer
{
	private final JournalOverlay overlay;
	private final SpriteManager spriteManager;

	private BufferedImage backgroundSurface;
	private Dimension backgroundSize;
	private boolean backgroundHasDetailPane;
	private boolean backgroundFiltersVisible;
	private boolean backgroundManualActiveControls;
	private JournalPanelAssets panelAssets;
	private JournalPanelAssets backgroundAssets;
	private int panelAssetRetryFrames;

	ChromeRenderer(
		JournalOverlay overlay,
		SpriteManager spriteManager)
	{
		this.overlay = Objects.requireNonNull(overlay, "overlay");
		this.spriteManager = spriteManager;
	}

	JournalPanelAssets currentPanelAssets()
	{
		if (panelAssets != null && panelAssets.isComplete())
		{
			return panelAssets;
		}
		if (panelAssetRetryFrames > 0)
		{
			panelAssetRetryFrames--;
			return panelAssets;
		}

		JournalPanelAssets loaded = JournalPanelAssets.load(spriteManager);
		panelAssetRetryFrames = JournalOverlay.PANEL_ASSET_RETRY_FRAMES;
		if (panelAssets == null
			|| loaded.isComplete()
			|| !panelAssets.hasTexture() && loaded.hasTexture())
		{
			panelAssets = loaded;
			overlay.panelAssetsChanged();
		}
		return panelAssets;
	}

	JournalPanelAssets panelAssets()
	{
		return panelAssets;
	}

	BufferedImage cachedBackground(
		Dimension size,
		boolean hasDetailPane,
		boolean showFilters,
		boolean manualActiveControls,
		JournalPanelAssets assets)
	{
		if (backgroundSurface != null
			&& size.equals(backgroundSize)
			&& hasDetailPane == backgroundHasDetailPane
			&& showFilters == backgroundFiltersVisible
			&& manualActiveControls == backgroundManualActiveControls
			&& assets == backgroundAssets)
		{
			return backgroundSurface;
		}

		BufferedImage replacement = new BufferedImage(
			Math.max(1, size.width),
			Math.max(1, size.height),
			BufferedImage.TYPE_INT_ARGB);
		Graphics2D canvas = replacement.createGraphics();
		try
		{
			applyQualityHints(canvas);
			Rectangle localPanel = new Rectangle(
				0,
				0,
				size.width,
				size.height);
			JournalGeometry localGeometry = JournalGeometry.create(
				localPanel,
				localPanel,
				showFilters,
				manualActiveControls);
			drawStaticChrome(canvas, localGeometry, assets);
		}
		finally
		{
			canvas.dispose();
		}

		BufferedImage superseded = backgroundSurface;
		backgroundSurface = replacement;
		backgroundSize = new Dimension(size);
		backgroundHasDetailPane = hasDetailPane;
		backgroundFiltersVisible = showFilters;
		backgroundManualActiveControls = manualActiveControls;
		backgroundAssets = assets;
		flushSupersededBackground(superseded, replacement);
		return backgroundSurface;
	}

	Rectangle drawHeader(
		Graphics2D graphics,
		JournalGeometry geometry,
		Point pointer,
		boolean maximized,
		JournalPanelAssets assets)
	{
		Rectangle header = geometry.headerBounds();
		Rectangle headerContent = JournalGeometry.headerContent(header);
		Rectangle close = geometry.closeButtonBounds();
		Rectangle maximize = geometry.maximizeButtonBounds();
		Rectangle settings = JournalOverlay.settingsButtonBounds(maximize);
		Font oldFont = graphics.getFont();
		graphics.setFont(FontManager.getRunescapeBoldFont());
		FontMetrics metrics = graphics.getFontMetrics();
		int availableLeft =
			header.x + JournalPanelAssets.BORDER_SIZE + 6;
		int availableRight = Math.max(availableLeft, settings.x - 6);
		int iconSize = Math.min(
			JournalOverlay.TITLE_ICON_SIZE,
			Math.max(1, headerContent.height));
		int iconGap = 4;
		String title = overlay.fitText(
			metrics,
			overlay.headerTitleText(),
			Math.max(
				1,
				availableRight - availableLeft - iconSize - iconGap));
		int groupWidth = iconSize + iconGap + metrics.stringWidth(title);
		int groupX = JournalOverlay.centeredHeaderGroupX(
			header,
			groupWidth);
		Rectangle iconBounds = new Rectangle(
			groupX,
			headerContent.y + (headerContent.height - iconSize) / 2,
			iconSize,
			iconSize);
		drawQuestIcon(graphics, iconBounds, assets);
		graphics.setColor(JournalOverlay.journalTitleColor());
		JournalOverlay.drawShadowedString(
			graphics,
			title,
			iconBounds.x + iconBounds.width + iconGap,
			overlay.centeredTextBaseline(metrics, headerContent));
		graphics.setFont(oldFont);

		drawHeaderControl(
			graphics,
			settings,
			pointer,
			JournalOverlay.HeaderControlIcon.SETTINGS,
			assets);
		drawHeaderControl(
			graphics,
			maximize,
			pointer,
			maximized
				? JournalOverlay.HeaderControlIcon.RESTORE
				: JournalOverlay.HeaderControlIcon.MAXIMIZE,
			assets);
		drawHeaderControl(
			graphics,
			close,
			pointer,
			JournalOverlay.HeaderControlIcon.CLOSE,
			assets);
		return iconBounds;
	}

	void drawHeaderControl(
		Graphics2D graphics,
		Rectangle bounds,
		Point pointer,
		JournalOverlay.HeaderControlIcon icon,
		JournalPanelAssets assets)
	{
		if (bounds.isEmpty())
		{
			return;
		}
		boolean hovered = bounds.contains(pointer);
		if (icon == JournalOverlay.HeaderControlIcon.SETTINGS
			&& assets != null)
		{
			BufferedImage settingsButton = hovered
				? assets.settingsButtonHovered
				: assets.settingsButton;
			if (settingsButton == null)
			{
				settingsButton = hovered
					? assets.settingsButton
					: assets.settingsButtonHovered;
			}
			if (settingsButton != null)
			{
				overlay.drawPixelArtCenteredImage(
					graphics,
					settingsButton,
					bounds);
				return;
			}
		}
		drawHeaderControlSkin(graphics, bounds, hovered);

		Graphics2D iconGraphics = (Graphics2D) graphics.create();
		try
		{
			iconGraphics.setRenderingHint(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_OFF);
			Paint oldPaint = iconGraphics.getPaint();
			iconGraphics.setPaint(new GradientPaint(
				bounds.x,
				bounds.y,
				JournalOverlay.CONTROL_ICON_DARK,
				bounds.x + bounds.width,
				bounds.y + bounds.height,
				JournalOverlay.CONTROL_ICON_LIGHT));
			if (icon == JournalOverlay.HeaderControlIcon.CLOSE)
			{
				BufferedImage closeIcon = null;
				if (assets != null)
				{
					closeIcon = hovered
						? assets.closeIconHovered
						: assets.closeIcon;
					if (closeIcon == null)
					{
						closeIcon = hovered
							? assets.closeIcon
							: assets.closeIconHovered;
					}
				}
				if (closeIcon != null)
				{
					overlay.drawPixelArtCenteredImage(
						iconGraphics,
						closeIcon,
						closeIconBounds(bounds));
				}
				else
				{
					drawPointedCloseGlyph(iconGraphics, bounds);
				}
			}
			else if (icon == JournalOverlay.HeaderControlIcon.RESTORE)
			{
				int frameWidth = 11;
				int frameHeight = 9;
				int centeredX = bounds.x + (bounds.width - frameWidth) / 2;
				int centeredY = bounds.y + (bounds.height - frameHeight) / 2;
				int backX = centeredX + 1;
				int backY = centeredY - 1;
				drawPixelFrame(
					iconGraphics,
					backX,
					backY,
					frameWidth,
					frameHeight,
					2);
				int frontX = centeredX - 1;
				int frontY = centeredY + 1;
				iconGraphics.setColor(JournalOverlay.CONTROL_FACE_MIDDLE);
				iconGraphics.fillRect(
					frontX,
					frontY,
					frameWidth,
					frameHeight);
				iconGraphics.setPaint(new GradientPaint(
					bounds.x,
					bounds.y,
					JournalOverlay.CONTROL_ICON_DARK,
					bounds.x + bounds.width,
					bounds.y + bounds.height,
					JournalOverlay.CONTROL_ICON_LIGHT));
				drawPixelFrame(
					iconGraphics,
					frontX,
					frontY,
					frameWidth,
					frameHeight,
					2);
			}
			else if (icon == JournalOverlay.HeaderControlIcon.MAXIMIZE)
			{
				int size = Math.min(
					13,
					Math.max(
						4,
						Math.min(bounds.width, bounds.height) - 8));
				int x = bounds.x + (bounds.width - size) / 2;
				int y = bounds.y + (bounds.height - size) / 2;
				drawPixelFrame(iconGraphics, x, y, size, size, 3);
			}
			iconGraphics.setPaint(oldPaint);
		}
		finally
		{
			iconGraphics.dispose();
		}
	}

	void drawHeaderControlSkin(
		Graphics2D graphics,
		Rectangle bounds,
		boolean hovered)
	{
		Rectangle bevel = headerControlShadowBounds(bounds);
		Rectangle face = headerControlFaceBounds(bounds);
		graphics.setColor(JournalOverlay.CONTROL_BORDER);
		graphics.fillRect(
			bounds.x,
			bounds.y,
			bounds.width,
			bounds.height);
		drawBeveledRectangle(
			graphics,
			bevel,
			JournalOverlay.CONTROL_BEVEL_MIDDLE,
			hovered
				? JournalOverlay.CONTROL_BEVEL_DARK
				: JournalOverlay.CONTROL_BEVEL_LIGHT,
			hovered
				? JournalOverlay.CONTROL_BEVEL_LIGHT
				: JournalOverlay.CONTROL_BEVEL_DARK);
		drawBeveledRectangle(
			graphics,
			face,
			JournalOverlay.CONTROL_FACE_MIDDLE,
			hovered
				? JournalOverlay.CONTROL_FACE_DARK
				: JournalOverlay.CONTROL_FACE_LIGHT,
			hovered
				? JournalOverlay.CONTROL_FACE_LIGHT
				: JournalOverlay.CONTROL_FACE_DARK);
	}

	void drawHeaderControlHoverEdges(
		Graphics2D graphics,
		Rectangle bounds)
	{
		drawBeveledEdges(
			graphics,
			headerControlShadowBounds(bounds),
			JournalOverlay.CONTROL_BEVEL_DARK,
			JournalOverlay.CONTROL_BEVEL_LIGHT);
		drawBeveledEdges(
			graphics,
			headerControlFaceBounds(bounds),
			JournalOverlay.CONTROL_FACE_DARK,
			JournalOverlay.CONTROL_FACE_LIGHT);
	}

	void drawResizeHandle(
		Graphics2D graphics,
		JournalGeometry geometry,
		boolean maximized)
	{
		if (maximized)
		{
			return;
		}
		Rectangle bounds = geometry.resizeHandleBounds();
		if (bounds.isEmpty())
		{
			return;
		}
		Stroke oldStroke = graphics.getStroke();
		graphics.setStroke(new BasicStroke(
			2f,
			BasicStroke.CAP_SQUARE,
			BasicStroke.JOIN_MITER));
		graphics.setColor(JournalOverlay.ACCENT);
		graphics.drawLine(
			bounds.x + 5,
			bounds.y + bounds.height - 3,
			bounds.x + bounds.width - 3,
			bounds.y + 5);
		graphics.setColor(JournalOverlay.BORDER_LIGHT);
		graphics.drawLine(
			bounds.x + 9,
			bounds.y + bounds.height - 3,
			bounds.x + bounds.width - 3,
			bounds.y + 9);
		graphics.setStroke(oldStroke);
	}

	void drawScrollBar(
		Graphics2D graphics,
		Rectangle content,
		int totalHeight,
		int offset,
		int maximum)
	{
		ScrollbarGeometry scrollbar = ScrollbarGeometry.create(
			content,
			totalHeight,
			offset,
			maximum);
		if (!scrollbar.isVisible())
		{
			return;
		}
		Rectangle track = scrollbar.visualTrackBounds();
		Rectangle thumb = scrollbar.visualThumbBounds();
		JournalPanelAssets assets = panelAssets;
		if (assets != null && assets.hasScrollbar())
		{
			drawNativeScrollbar(graphics, track, thumb, assets);
			return;
		}
		Paint oldPaint = graphics.getPaint();
		graphics.setColor(JournalOverlay.SCROLL_BEZEL_DARK);
		graphics.fillRoundRect(
			track.x,
			track.y,
			track.width,
			track.height,
			JournalOverlay.scrollbarTrackArc(),
			JournalOverlay.scrollbarTrackArc());
		Rectangle trackFace = insetRectangle(track, 1);
		if (!trackFace.isEmpty())
		{
			int gradientStart =
				trackFace.x + Math.max(0, (trackFace.width - 1) * 4 / 5);
			graphics.setPaint(new GradientPaint(
				gradientStart,
				trackFace.y,
				JournalOverlay.SCROLL_TRACK,
				trackFace.x + Math.max(1, trackFace.width - 1),
				trackFace.y,
				JournalOverlay.SCROLL_TRACK_EDGE));
			graphics.fillRoundRect(
				trackFace.x,
				trackFace.y,
				trackFace.width,
				trackFace.height,
				Math.max(
					2,
					JournalOverlay.scrollbarTrackArc() - 2),
				Math.max(
					2,
					JournalOverlay.scrollbarTrackArc() - 2));
		}

		graphics.setColor(JournalOverlay.SCROLL_BEZEL_DARK);
		graphics.fillRoundRect(
			thumb.x,
			thumb.y,
			thumb.width,
			thumb.height,
			JournalOverlay.SCROLLBAR_THUMB_ARC,
			JournalOverlay.SCROLLBAR_THUMB_ARC);
		Rectangle thumbFace = insetRectangle(thumb, 1);
		if (!thumbFace.isEmpty())
		{
			graphics.setPaint(new GradientPaint(
				thumbFace.x,
				thumbFace.y,
				JournalOverlay.SCROLL_THUMB_LEFT,
				thumbFace.x + Math.max(1, thumbFace.width - 1),
				thumbFace.y,
				JournalOverlay.SCROLL_THUMB_RIGHT));
			graphics.fillRoundRect(
				thumbFace.x,
				thumbFace.y,
				thumbFace.width,
				thumbFace.height,
				Math.max(
					2,
					JournalOverlay.SCROLLBAR_THUMB_ARC - 2),
				Math.max(
					2,
					JournalOverlay.SCROLLBAR_THUMB_ARC - 2));
			graphics.setColor(JournalOverlay.SCROLL_BEZEL_LIGHT);
			graphics.drawLine(
				thumbFace.x,
				thumbFace.y,
				thumbFace.x + Math.max(0, thumbFace.width - 1),
				thumbFace.y);
			graphics.setColor(
				JournalOverlay.SCROLL_THUMB_RIGHT.darker());
			graphics.drawLine(
				thumbFace.x,
				thumbFace.y + Math.max(0, thumbFace.height - 1),
				thumbFace.x + Math.max(0, thumbFace.width - 1),
				thumbFace.y + Math.max(0, thumbFace.height - 1));
		}
		graphics.setPaint(oldPaint);
	}

	void release()
	{
		clearBackgroundCache();
		panelAssets = null;
		panelAssetRetryFrames = 0;
	}

	void clearBackgroundCache()
	{
		if (backgroundSurface != null)
		{
			backgroundSurface.flush();
		}
		backgroundSurface = null;
		backgroundSize = null;
		backgroundAssets = null;
	}

	static void flushSupersededBackground(
		BufferedImage superseded,
		BufferedImage replacement)
	{
		if (superseded != null && superseded != replacement)
		{
			superseded.flush();
		}
	}

	static Rectangle titleSeparatorBounds(
		int width,
		int height,
		int headerHeight)
	{
		int safeWidth = Math.max(0, width);
		int safeHeight = Math.max(0, height);
		int y = Math.max(0, Math.min(headerHeight, safeHeight));
		int targetInset = Math.max(
			0,
			JournalPanelAssets.BORDER_SIZE - 1);
		int inset = Math.min(targetInset, safeWidth / 2);
		return new Rectangle(
			inset,
			y,
			Math.max(0, safeWidth - inset * 2),
			Math.min(
				JournalOverlay.TITLE_SEPARATOR_HEIGHT,
				safeHeight - y));
	}

	static Rectangle headerControlShadowBounds(Rectangle bounds)
	{
		return insetRectangle(bounds, 1);
	}

	static Rectangle headerControlFaceBounds(Rectangle bounds)
	{
		return insetRectangle(bounds, 2);
	}

	static Rectangle closeIconBounds(Rectangle bounds)
	{
		int size = Math.min(
			13,
			Math.max(0, Math.min(bounds.width, bounds.height) - 2));
		return new Rectangle(
			bounds.x + (bounds.width - size) / 2,
			bounds.y + (bounds.height - size) / 2,
			size,
			size);
	}

	static Rectangle insetRectangle(Rectangle bounds, int inset)
	{
		int amount = Math.max(0, inset);
		return new Rectangle(
			bounds.x + Math.min(amount, Math.max(0, bounds.width)),
			bounds.y + Math.min(amount, Math.max(0, bounds.height)),
			Math.max(0, bounds.width - amount * 2),
			Math.max(0, bounds.height - amount * 2));
	}

	static void applyQualityHints(Graphics2D graphics)
	{
		graphics.setRenderingHint(
			RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setRenderingHint(
			RenderingHints.KEY_TEXT_ANTIALIASING,
			RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		graphics.setRenderingHint(
			RenderingHints.KEY_RENDERING,
			RenderingHints.VALUE_RENDER_QUALITY);
	}

	private void drawStaticChrome(
		Graphics2D graphics,
		JournalGeometry geometry,
		JournalPanelAssets assets)
	{
		Rectangle panel = geometry.panelBounds();
		drawPanelSurface(graphics, panel, assets);
		drawPaneSurface(
			graphics,
			geometry.questListPaneBounds(),
			geometry.questListContentBounds(),
			false);
		drawPaneSurface(
			graphics,
			geometry.mainPaneBounds(),
			geometry.mainContentBounds(),
			true);
		if (geometry.hasDetailPane())
		{
			drawPaneSurface(
				graphics,
				geometry.detailPaneBounds(),
				geometry.detailContentBounds(),
				false);
		}
		drawPaneSeparators(graphics, geometry);
		drawPanelFrame(
			graphics,
			panel,
			geometry.headerBounds().height,
			assets);
	}

	private void drawPanelSurface(
		Graphics2D graphics,
		Rectangle bounds,
		JournalPanelAssets assets)
	{
		if (assets == null || !assets.hasTexture())
		{
			graphics.setColor(JournalOverlay.BACKGROUND);
			graphics.fillRect(
				bounds.x,
				bounds.y,
				bounds.width,
				bounds.height);
			return;
		}

		Paint oldPaint = graphics.getPaint();
		Composite oldComposite = graphics.getComposite();
		float opacity = Math.max(
			0f,
			Math.min(1f, assets.surfaceOpacity));
		graphics.setComposite(
			AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
		graphics.setPaint(new TexturePaint(
			assets.texture,
			new Rectangle(
				0,
				0,
				Math.max(1, assets.texture.getWidth()),
				Math.max(1, assets.texture.getHeight()))));
		graphics.fillRect(
			bounds.x,
			bounds.y,
			bounds.width,
			bounds.height);
		graphics.setPaint(oldPaint);
		graphics.setComposite(oldComposite);
	}

	private void drawPaneSurface(
		Graphics2D graphics,
		Rectangle pane,
		Rectangle content,
		boolean drawHeaderSeparator)
	{
		if (pane.isEmpty())
		{
			return;
		}
		graphics.setColor(JournalOverlay.CELL_CONTOUR);
		graphics.drawRect(
			pane.x,
			pane.y,
			Math.max(1, pane.width - 1),
			Math.max(1, pane.height - 1));

		if (drawHeaderSeparator)
		{
			int separatorY = Math.max(
				pane.y,
				content.y - JournalOverlay.PANE_SEPARATOR_INSET);
			graphics.setColor(JournalPanelAssets.SEPARATOR_COLOR);
			graphics.fillRect(
				pane.x,
				separatorY,
				pane.width,
				JournalPanelAssets.SEPARATOR_SIZE);
		}
	}

	private void drawPaneSeparators(
		Graphics2D graphics,
		JournalGeometry geometry)
	{
		Rectangle list = geometry.questListPaneBounds();
		Rectangle main = geometry.mainPaneBounds();
		graphics.setColor(JournalPanelAssets.SEPARATOR_COLOR);
		fillGap(
			graphics,
			list.x + list.width,
			main.x,
			list.y,
			list.height);
		if (geometry.hasDetailPane())
		{
			Rectangle detail = geometry.detailPaneBounds();
			fillGap(
				graphics,
				main.x + main.width,
				detail.x,
				main.y,
				main.height);
		}
	}

	private static void fillGap(
		Graphics2D graphics,
		int fromX,
		int toX,
		int y,
		int height)
	{
		int width = Math.max(0, toX - fromX);
		if (width > 0 && height > 0)
		{
			graphics.fillRect(fromX, y, width, height);
		}
	}

	private void drawPanelFrame(
		Graphics2D graphics,
		Rectangle panel,
		int headerHeight,
		JournalPanelAssets assets)
	{
		if (assets == null || !assets.hasFrame())
		{
			drawFallbackFrame(graphics, panel.width, panel.height);
			drawTitleSeparator(
				graphics,
				panel.width,
				panel.height,
				headerHeight,
				assets);
			return;
		}

		int borderSize = JournalPanelAssets.BORDER_SIZE;
		drawTiledHorizontal(
			graphics,
			assets.top,
			new Rectangle(
				borderSize,
				0,
				Math.max(1, panel.width - borderSize * 2),
				borderSize));
		drawTiledHorizontal(
			graphics,
			assets.bottom,
			new Rectangle(
				borderSize,
				panel.height - borderSize,
				Math.max(1, panel.width - borderSize * 2),
				borderSize));
		drawTiledVertical(
			graphics,
			assets.left,
			new Rectangle(
				0,
				borderSize,
				borderSize,
				Math.max(1, panel.height - borderSize * 2)));
		drawTiledVertical(
			graphics,
			assets.right,
			new Rectangle(
				panel.width - borderSize,
				borderSize,
				borderSize,
				Math.max(1, panel.height - borderSize * 2)));

		graphics.drawImage(assets.topLeft, 0, 0, null);
		graphics.drawImage(
			assets.topRight,
			panel.width - assets.topRight.getWidth(),
			0,
			null);
		graphics.drawImage(
			assets.bottomLeft,
			0,
			panel.height - assets.bottomLeft.getHeight(),
			null);
		graphics.drawImage(
			assets.bottomRight,
			panel.width - assets.bottomRight.getWidth(),
			panel.height - assets.bottomRight.getHeight(),
			null);
		drawTitleSeparator(
			graphics,
			panel.width,
			panel.height,
			headerHeight,
			assets);
	}

	private void drawTitleSeparator(
		Graphics2D graphics,
		int width,
		int height,
		int headerHeight,
		JournalPanelAssets assets)
	{
		Rectangle bounds = titleSeparatorBounds(width, height, headerHeight);
		if (bounds.isEmpty())
		{
			return;
		}
		if (assets != null && assets.titleSeparator != null)
		{
			drawTiledHorizontal(
				graphics,
				assets.titleSeparator,
				bounds);
			return;
		}

		graphics.setColor(JournalOverlay.TITLE_SEPARATOR_EDGE);
		graphics.drawLine(
			bounds.x,
			bounds.y,
			bounds.x + bounds.width - 1,
			bounds.y);
		if (bounds.height > 2)
		{
			Paint oldPaint = graphics.getPaint();
			graphics.setPaint(new GradientPaint(
				bounds.x,
				bounds.y + 1,
				JournalOverlay.TITLE_SEPARATOR_TOP,
				bounds.x,
				bounds.y + bounds.height - 2,
				JournalOverlay.TITLE_SEPARATOR_BOTTOM));
			graphics.fillRect(
				bounds.x,
				bounds.y + 1,
				bounds.width,
				bounds.height - 2);
			graphics.setPaint(oldPaint);
		}
		if (bounds.height > 1)
		{
			graphics.setColor(JournalOverlay.TITLE_SEPARATOR_EDGE);
			graphics.drawLine(
				bounds.x,
				bounds.y + bounds.height - 1,
				bounds.x + bounds.width - 1,
				bounds.y + bounds.height - 1);
		}
	}

	private static void drawTiledHorizontal(
		Graphics2D graphics,
		BufferedImage image,
		Rectangle bounds)
	{
		int tileWidth = Math.max(
			1,
			(int) Math.round(
				image.getWidth() * bounds.height
					/ (double) Math.max(1, image.getHeight())));
		Shape oldClip = graphics.getClip();
		graphics.clip(bounds);
		for (int x = bounds.x; x < bounds.x + bounds.width; x += tileWidth)
		{
			graphics.drawImage(
				image,
				x,
				bounds.y,
				tileWidth,
				bounds.height,
				null);
		}
		graphics.setClip(oldClip);
	}

	private static void drawTiledVertical(
		Graphics2D graphics,
		BufferedImage image,
		Rectangle bounds)
	{
		int tileHeight = Math.max(
			1,
			(int) Math.round(
				image.getHeight() * bounds.width
					/ (double) Math.max(1, image.getWidth())));
		Shape oldClip = graphics.getClip();
		graphics.clip(bounds);
		for (int y = bounds.y; y < bounds.y + bounds.height; y += tileHeight)
		{
			graphics.drawImage(
				image,
				bounds.x,
				y,
				bounds.width,
				tileHeight,
				null);
		}
		graphics.setClip(oldClip);
	}

	private static void drawFallbackFrame(
		Graphics2D graphics,
		int width,
		int height)
	{
		Stroke oldStroke = graphics.getStroke();
		graphics.setStroke(new BasicStroke(4f));
		graphics.setColor(JournalOverlay.BORDER_DARK);
		graphics.drawRect(
			2,
			2,
			Math.max(1, width - 5),
			Math.max(1, height - 5));
		graphics.setStroke(new BasicStroke(2f));
		graphics.setColor(JournalOverlay.BORDER_LIGHT);
		graphics.drawRect(
			5,
			5,
			Math.max(1, width - 11),
			Math.max(1, height - 11));
		graphics.setStroke(oldStroke);
	}

	private void drawQuestIcon(
		Graphics2D graphics,
		Rectangle bounds,
		JournalPanelAssets assets)
	{
		BufferedImage icon = assets == null ? null : assets.questIcon;
		if (icon != null)
		{
			drawVisuallyCenteredImage(
				graphics,
				overlay.blackOutlinedIcon(icon),
				bounds);
			return;
		}

		int horizontalInset = Math.max(1, bounds.width / 5);
		int verticalInset = Math.max(1, bounds.height / 7);
		int pageWidth = Math.max(1, bounds.width - horizontalInset * 2);
		int pageHeight = Math.max(1, bounds.height - verticalInset * 2);
		graphics.setColor(JournalOverlay.CONTROL_BORDER);
		graphics.fillRect(
			bounds.x + horizontalInset + 1,
			bounds.y + verticalInset + 1,
			pageWidth,
			pageHeight);
		graphics.setColor(JournalOverlay.ACCENT);
		graphics.fillRect(
			bounds.x + horizontalInset,
			bounds.y + verticalInset,
			pageWidth,
			pageHeight);
		graphics.setColor(JournalOverlay.CONTROL_ICON);
		int lineLeft = bounds.x + horizontalInset + 2;
		int lineRight = Math.max(
			lineLeft,
			bounds.x + bounds.width - horizontalInset - 3);
		for (int row = 1; row <= 3; row++)
		{
			int lineY =
				bounds.y + verticalInset + row * pageHeight / 4;
			graphics.drawLine(lineLeft, lineY, lineRight, lineY);
		}
	}

	private void drawVisuallyCenteredImage(
		Graphics2D graphics,
		BufferedImage image,
		Rectangle bounds)
	{
		Rectangle visible = overlay.visibleImageBounds(image);
		double scale = Math.min(
			bounds.width / (double) Math.max(1, visible.width),
			bounds.height / (double) Math.max(1, visible.height));
		int width = Math.max(
			1,
			(int) Math.round(image.getWidth() * scale));
		int height = Math.max(
			1,
			(int) Math.round(image.getHeight() * scale));
		int x = (int) Math.round(
			bounds.getCenterX()
				- (visible.x + visible.width / 2.0) * scale);
		int y = (int) Math.round(
			bounds.getCenterY()
				- (visible.y + visible.height / 2.0) * scale);
		graphics.drawImage(image, x, y, width, height, null);
	}

	private static void drawBeveledRectangle(
		Graphics2D graphics,
		Rectangle bounds,
		Color fill,
		Color topLeft,
		Color bottomRight)
	{
		if (bounds.isEmpty())
		{
			return;
		}
		graphics.setColor(fill);
		graphics.fillRect(
			bounds.x,
			bounds.y,
			bounds.width,
			bounds.height);
		drawBeveledEdges(graphics, bounds, topLeft, bottomRight);
	}

	private static void drawBeveledEdges(
		Graphics2D graphics,
		Rectangle bounds,
		Color topLeft,
		Color bottomRight)
	{
		if (bounds.isEmpty())
		{
			return;
		}
		graphics.setColor(topLeft);
		graphics.drawLine(
			bounds.x,
			bounds.y,
			bounds.x + bounds.width - 1,
			bounds.y);
		graphics.drawLine(
			bounds.x,
			bounds.y,
			bounds.x,
			bounds.y + bounds.height - 1);
		graphics.setColor(bottomRight);
		graphics.drawLine(
			bounds.x,
			bounds.y + bounds.height - 1,
			bounds.x + bounds.width - 1,
			bounds.y + bounds.height - 1);
		graphics.drawLine(
			bounds.x + bounds.width - 1,
			bounds.y,
			bounds.x + bounds.width - 1,
			bounds.y + bounds.height - 1);
	}

	private static void drawPointedCloseGlyph(
		Graphics2D graphics,
		Rectangle bounds)
	{
		int size = Math.min(
			15,
			Math.max(1, Math.min(bounds.width, bounds.height) - 6));
		int x = bounds.x + (bounds.width - size) / 2;
		int y = bounds.y + (bounds.height - size) / 2;
		if (size < 5)
		{
			graphics.fillRect(x, y, size, size);
			return;
		}
		int max = size - 1;
		int center = max / 2;
		int bevel = Math.max(1, size / 5);
		int neck = Math.max(1, size / 5);
		int[] xPoints =
		{
			x,
			x + bevel,
			x + center,
			x + max - bevel,
			x + max,
			x + center + neck,
			x + max,
			x + max - bevel,
			x + center,
			x + bevel,
			x,
			x + center - neck
		};
		int[] yPoints =
		{
			y + bevel,
			y,
			y + center - neck,
			y,
			y + bevel,
			y + center,
			y + max - bevel,
			y + max,
			y + center + neck,
			y + max,
			y + max - bevel,
			y + center
		};
		graphics.fillPolygon(xPoints, yPoints, xPoints.length);
	}

	static void drawPixelFrame(
		Graphics2D graphics,
		int x,
		int y,
		int width,
		int height,
		int thickness)
	{
		int line = Math.max(
			1,
			Math.min(thickness, Math.min(width, height) / 2));
		graphics.fillRect(x, y, width, line);
		graphics.fillRect(x, y + height - line, width, line);
		graphics.fillRect(
			x,
			y + line,
			line,
			Math.max(0, height - line * 2));
		graphics.fillRect(
			x + width - line,
			y + line,
			line,
			Math.max(0, height - line * 2));
	}

	private void drawNativeScrollbar(
		Graphics2D graphics,
		Rectangle track,
		Rectangle thumb,
		JournalPanelAssets assets)
	{
		if (!track.isEmpty())
		{
			graphics.setColor(JournalOverlay.SCROLL_BEZEL_DARK);
			graphics.fillRoundRect(
				track.x,
				track.y,
				track.width,
				track.height,
				JournalOverlay.scrollbarTrackArc(),
				JournalOverlay.scrollbarTrackArc());
			Rectangle trackFace = new Rectangle(
				track.x,
				track.y + Math.min(1, track.height),
				track.width,
				Math.max(0, track.height - 2));
			if (!trackFace.isEmpty())
			{
				Shape oldClip = graphics.getClip();
				graphics.clip(new RoundRectangle2D.Float(
					trackFace.x,
					trackFace.y,
					trackFace.width,
					trackFace.height,
					Math.max(
						2,
						JournalOverlay.scrollbarTrackArc() - 2),
					Math.max(
						2,
						JournalOverlay.scrollbarTrackArc() - 2)));
				drawTiledVertical(
					graphics,
					assets.scrollbarTrack,
					trackFace);
				graphics.setClip(oldClip);
			}
		}
		Shape oldClip = graphics.getClip();
		graphics.clip(new RoundRectangle2D.Float(
			thumb.x,
			thumb.y,
			thumb.width,
			thumb.height,
			JournalOverlay.scrollbarThumbArc(),
			JournalOverlay.scrollbarThumbArc()));
		int topHeight = scaledStripHeight(
			assets.scrollbarTop,
			thumb.width,
			thumb.height / 2);
		int bottomHeight = scaledStripHeight(
			assets.scrollbarBottom,
			thumb.width,
			Math.max(0, thumb.height - topHeight));
		if (topHeight > 0)
		{
			graphics.drawImage(
				assets.scrollbarTop,
				thumb.x,
				thumb.y,
				thumb.width,
				topHeight,
				null);
		}
		int middleY = thumb.y + topHeight;
		int middleHeight = Math.max(
			0,
			thumb.height - topHeight - bottomHeight);
		if (middleHeight > 0)
		{
			drawTiledVertical(
				graphics,
				assets.scrollbarMiddle,
				new Rectangle(
					thumb.x,
					middleY,
					thumb.width,
					middleHeight));
		}
		if (bottomHeight > 0)
		{
			graphics.drawImage(
				assets.scrollbarBottom,
				thumb.x,
				thumb.y + thumb.height - bottomHeight,
				thumb.width,
				bottomHeight,
				null);
		}
		graphics.setClip(oldClip);
	}

	private static int scaledStripHeight(
		BufferedImage strip,
		int width,
		int maximumHeight)
	{
		if (strip == null || width <= 0 || maximumHeight <= 0)
		{
			return 0;
		}
		int scaled = (int) Math.round(
			strip.getHeight() * width
				/ (double) Math.max(1, strip.getWidth()));
		return Math.min(maximumHeight, Math.max(1, scaled));
	}
}
