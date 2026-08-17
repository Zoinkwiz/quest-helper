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
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.SpriteID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.game.SpriteManager;
import com.questhelper.QuestHelperPlugin;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.tooltip.Tooltip;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;

final class LauncherOverlay extends Overlay
{
	static final int BUTTON_SIZE = LauncherGeometry.BUTTON_SIZE;
	private static final Color BUTTON_BORDER = new Color(0x32, 0x2B, 0x19);
	private static final Color BUTTON_BORDER_HOVER = new Color(0x4B, 0x43, 0x38);
	private static final Color BUTTON_INNER_RING = new Color(0x5D, 0x54, 0x4A);
	private static final Color BUTTON_INNER_RING_HOVER = new Color(0x7D, 0x71, 0x62);
	private static final Color QUEST_ICON_OUTLINE_START = new Color(0xAF, 0xAF, 0xA4);
	private static final Color QUEST_ICON_OUTLINE_END = new Color(0x37, 0x37, 0x37);
	private static final Color QUEST_ICON_BACKING_HIGHLIGHT = new Color(0x98, 0xA8, 0xFF);
	private static final Color QUEST_ICON_BACKING_MIDDLE = new Color(0x71, 0x80, 0xC5);
	private static final Color QUEST_ICON_BACKING_SHADOW = new Color(0x4D, 0x56, 0x88);
	private static final Color QUEST_ICON_BACKING_INSET_SHADOW = new Color(0x34, 0x3A, 0x5C);
	private static final Color BUTTON_BACKGROUND = new Color(0x4B, 0x43, 0x38);
	private static final Color BUTTON_LIGHT = new Color(0x52, 0x4A, 0x3F);
	private static final Color BUTTON_DARK = new Color(0x44, 0x3D, 0x33);
	private static final Color TEXTURE_HOVER_FROM = new Color(0x47, 0x3F, 0x35);
	private static final Color TEXTURE_HOVER_TO = new Color(0x69, 0x5C, 0x4D);
	private static final Color ACTIVE_CENTER_BACKING = BUTTON_BACKGROUND;
	private static final float ACTIVE_CENTER_OPACITY = 0.52f;
	private static final float CENTER_TEXTURE_OPACITY = 0.32f;
	private static final float ICON_DIAGONAL_OUTLINE_OPACITY = 1f;
	private static final int BUTTON_INSET = 3;
	private static final Rectangle QUEST_NATIVE_ICON_BOUNDS = new Rectangle(8, 8, 18, 18);
	private static final Rectangle QUEST_ICON_BACKING_BOUNDS = new Rectangle(4, 4, 26, 26);
	private static final int QUEST_ICON_BACKING_RADIUS_SQUARED_X4 = 650;
	private static final double QUEST_ICON_BACKING_LIGHT_POSITION = 5.0;
	private static final float QUEST_ICON_BACKING_INSET_MINIMUM = 0.24f;
	private static final float QUEST_ICON_BACKING_INSET_DIRECTIONAL = 0.36f;
	private static final int NATIVE_RUN_ORB_ALPHA = 230;
	private static final int RIM_VISUAL_INSET = 0;
	private static final int RIM_TEXTURE_EXTRA_WIDTH = 2;
	private static final int[][] CARDINAL_OFFSETS = {
		{0, -1},
		{-1, 0},
		{1, 0},
		{0, 1}
	};
	private static final int[] OCCUPIED_CONTROLS = {
		InterfaceID.Orbs.ORB_HEALTH,
		InterfaceID.Orbs.ORB_PRAYER,
		InterfaceID.Orbs.ORB_RUNENERGY,
		InterfaceID.Orbs.ORB_SPECENERGY,
		InterfaceID.Orbs.ORB_STORE,
		InterfaceID.Orbs.ORB_CONTENTRECOM,
		InterfaceID.Orbs.ORB_WORLDMAP,
		InterfaceID.Orbs.WIKI,
		InterfaceID.OrbsNomap.ORB_HEALTH,
		InterfaceID.OrbsNomap.ORB_PRAYER,
		InterfaceID.OrbsNomap.ORB_RUNENERGY,
		InterfaceID.OrbsNomap.ORB_SPECENERGY,
		InterfaceID.OrbsNomap.ORB_STORE,
		InterfaceID.OrbsNomap.ORB_CONTENTRECOM,
		InterfaceID.OrbsNomap.ORB_WORLDMAP,
		InterfaceID.OrbsNomap.WIKI,
		InterfaceID.OrbsOsm.ORB_HEALTH,
		InterfaceID.OrbsOsm.ORB_PRAYER,
		InterfaceID.OrbsOsm.ORB_RUNENERGY,
		InterfaceID.OrbsOsm.ORB_SPECENERGY,
		InterfaceID.OrbsOsm.ORB_STORE,
		InterfaceID.OrbsOsm.ORB_CONTENTRECOM,
		InterfaceID.OrbsOsm.ORB_WORLDMAP,
		InterfaceID.OrbsOsm.WIKI,
		InterfaceID.OrbsOsmNomap.ORB_HEALTH,
		InterfaceID.OrbsOsmNomap.ORB_PRAYER,
		InterfaceID.OrbsOsmNomap.ORB_RUNENERGY,
		InterfaceID.OrbsOsmNomap.ORB_SPECENERGY,
		InterfaceID.OrbsOsmNomap.ORB_STORE,
		InterfaceID.OrbsOsmNomap.ORB_CONTENTRECOM,
		InterfaceID.OrbsOsmNomap.ORB_WORLDMAP,
		InterfaceID.OrbsOsmNomap.WIKI,
		InterfaceID.Toplevel.SIDE_TOP,
		InterfaceID.Toplevel.SIDE_BOTTOM,
		InterfaceID.ToplevelOsrsStretch.SIDE_TOP,
		InterfaceID.ToplevelOsrsStretch.SIDE_BOTTOM,
		InterfaceID.ToplevelPreEoc.SIDE_STATIC,
		InterfaceID.ToplevelPreEoc.SIDE_MOVABLE,
		InterfaceID.ToplevelOsm.SIDE_RIGHT_TABS,
		InterfaceID.ToplevelOsm.TABS_RIGHT,
		InterfaceID.ToplevelOsm.TABS_LEFT
	};
	private static final int[] MINIMAP_COMPONENTS = {
		InterfaceID.Toplevel.MINIMAP,
		InterfaceID.ToplevelOsrsStretch.MINIMAP,
		InterfaceID.ToplevelPreEoc.MINIMAP,
		InterfaceID.ToplevelOsm.MINIMAP
	};
	private static final int[] NATIVE_WORLD_MAP_TOOLTIP_COMPONENTS = {
		InterfaceID.Orbs.TOOLTIP,
		InterfaceID.OrbsNomap.TOOLTIP
	};

	private final Client client;
	private final SpriteManager spriteManager;
	private final TooltipManager tooltipManager;
	private final QuestJournalManager manager;
	private volatile Rectangle hitBounds;
	private BufferedImage normalRimImage;
	private BufferedImage hoverRimImage;
	private BufferedImage centerTextureImage;
	private BufferedImage hoverCenterTextureImage;
	private BufferedImage[] contentImages = new BufferedImage[4];
	private BufferedImage questImage;
	private BufferedImage questIconBackingImage;
	private BufferedImage questIconLayerImage;
	private int automaticPlacementTick = Integer.MIN_VALUE;
	private int automaticPlacementTopLevel = Integer.MIN_VALUE;
	private Rectangle automaticPlacementViewport = new Rectangle();
	private Point automaticPlacementLocation;

	@Inject
	LauncherOverlay(
		Client client,
		SpriteManager spriteManager,
		TooltipManager tooltipManager,
		QuestHelperPlugin owner,
		QuestJournalManager manager)
	{
		super(owner);
		this.client = client;
		this.spriteManager = spriteManager;
		this.tooltipManager = tooltipManager;
		this.manager = manager;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
		setPriority(Overlay.PRIORITY_HIGH);
		setMovable(true);
		setResizable(false);
		setSnappable(false);
		setResettable(true);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			invalidateAutomaticPlacement();
			clearBounds();
			return null;
		}

		Dimension viewportSize = client.getRealDimensions();
		if (viewportSize == null || viewportSize.width <= 0 || viewportSize.height <= 0)
		{
			invalidateAutomaticPlacement();
			clearBounds();
			return null;
		}

		Rectangle viewport = new Rectangle(viewportSize);
		Rectangle host = new Rectangle(getBounds());
		Point location;
		if (getPreferredLocation() != null)
		{
			location = LauncherGeometry.clampLocation(host.getLocation(), viewport);
		}
		else
		{
			location = automaticLocation(viewport);
		}
		if (location == null)
		{
			clearBounds();
			return null;
		}
		Rectangle absoluteBounds = new Rectangle(location.x, location.y, BUTTON_SIZE, BUTTON_SIZE);
		Point pointer = manager.getPointerCanvasPoint();
		boolean hovered = LauncherGeometry.contains(absoluteBounds.getLocation(), pointer);

		loadSprites();
		Graphics2D output = (Graphics2D) graphics.create();
		try
		{
			output.translate(absoluteBounds.x - host.x, absoluteBounds.y - host.y);
			List<Rectangle> tooltipBounds = nativeWorldMapTooltipBounds(viewport);
			if (tooltipBounds.isEmpty())
			{
				output.clipRect(0, 0, BUTTON_SIZE, BUTTON_SIZE);
			}
			else
			{
				output.clip(launcherPaintArea(absoluteBounds, tooltipBounds));
			}
			applyQualityHints(output);
			drawButton(
				output,
				hovered,
				manager.isJournalOpen());
		}
		finally
		{
			output.dispose();
		}

		if (hovered && (manager == null || !manager.isClientMenuOpen()))
		{
			tooltipManager.add(new Tooltip(
				manager.isJournalOpen() ? "Close Quest Journal" : "Open Quest Journal"));
		}

		getBounds().setBounds(absoluteBounds);
		hitBounds = new Rectangle(absoluteBounds);
		return absoluteBounds.getSize();
	}

	private Point automaticLocation(Rectangle viewport)
	{
		int tick = client.getTickCount();
		int topLevel = client.getTopLevelInterfaceId();
		if (automaticPlacementTick == tick
			&& automaticPlacementTopLevel == topLevel
			&& automaticPlacementViewport.equals(viewport))
		{
			return automaticPlacementLocation;
		}

		automaticPlacementTick = tick;
		automaticPlacementTopLevel = topLevel;
		automaticPlacementViewport = new Rectangle(viewport);
		automaticPlacementLocation = LauncherGeometry.automaticLocation(
			findMinimapBounds(viewport),
			viewport,
			occupiedBounds(viewport));
		return automaticPlacementLocation;
	}

	private void invalidateAutomaticPlacement()
	{
		automaticPlacementTick = Integer.MIN_VALUE;
	}

	@Override
	public Rectangle getParentBounds()
	{
		Dimension dimensions = client.getRealDimensions();
		return dimensions == null ? new Rectangle() : new Rectangle(dimensions);
	}

	private Rectangle findMinimapBounds(Rectangle viewport)
	{
		for (int componentId : MINIMAP_COMPONENTS)
		{
			Widget widget = client.getWidget(componentId);
			if (isVisible(widget, viewport))
			{
				return widget.getBounds();
			}
		}
		return null;
	}

	private List<Rectangle> occupiedBounds(Rectangle viewport)
	{
		List<Rectangle> occupied = new ArrayList<>();
		for (int componentId : OCCUPIED_CONTROLS)
		{
			Widget widget = client.getWidget(componentId);
			if (isVisible(widget, viewport))
			{
				Rectangle bounds = new Rectangle(widget.getBounds());
				bounds.grow(3, 3);
				occupied.add(bounds);
			}
		}
		return occupied;
	}

	private List<Rectangle> nativeWorldMapTooltipBounds(Rectangle viewport)
	{
		List<Rectangle> tooltips = Collections.emptyList();
		for (int componentId : NATIVE_WORLD_MAP_TOOLTIP_COMPONENTS)
		{
			Widget widget = client.getWidget(componentId);
			if (isActiveNativeTooltip(widget, viewport))
			{
				if (tooltips.isEmpty())
				{
					tooltips = new ArrayList<>();
				}
				tooltips.add(new Rectangle(widget.getBounds()));
			}
		}
		return tooltips;
	}

	static boolean isActiveNativeTooltip(Widget widget, Rectangle viewport)
	{
		if (!isVisible(widget, viewport))
		{
			return false;
		}
		Widget[] dynamicChildren = widget.getDynamicChildren();
		return dynamicChildren != null && dynamicChildren.length >= 3;
	}

	static Area launcherPaintArea(Rectangle launcherBounds, List<Rectangle> tooltipBounds)
	{
		if (launcherBounds == null || launcherBounds.isEmpty())
		{
			return new Area();
		}
		Area visible = new Area(new Rectangle(0, 0, launcherBounds.width, launcherBounds.height));
		if (tooltipBounds == null)
		{
			return visible;
		}
		for (Rectangle tooltipBoundsEntry : tooltipBounds)
		{
			if (tooltipBoundsEntry == null || tooltipBoundsEntry.isEmpty())
			{
				continue;
			}
			Rectangle overlap = launcherBounds.intersection(tooltipBoundsEntry);
			if (!overlap.isEmpty())
			{
				overlap.translate(-launcherBounds.x, -launcherBounds.y);
				visible.subtract(new Area(overlap));
			}
		}
		return visible;
	}

	private static boolean isVisible(Widget widget, Rectangle viewport)
	{
		return widget != null
			&& !widget.isHidden()
			&& !widget.isSelfHidden()
			&& !widget.getBounds().isEmpty()
			&& viewport.intersects(widget.getBounds());
	}

	private void loadSprites()
	{
		if (normalRimImage == null)
		{
			BufferedImage normalButtonImage =
				spriteManager.getSprite(SpriteID.TliButton01Orb01_34x34._0, 0);
			if (normalButtonImage != null)
			{
				normalRimImage = createTexturedRim(normalButtonImage, BUTTON_BORDER);
				hoverRimImage = createTexturedRim(
					normalButtonImage,
					BUTTON_BORDER_HOVER,
					BUTTON_INNER_RING_HOVER,
					true);
				centerTextureImage = createBrownCenterTexture(normalButtonImage);
				hoverCenterTextureImage = createHoverTexture(centerTextureImage);
				invalidateContentImages();
			}
		}
		if (questImage == null)
		{
			BufferedImage nativeQuestImage = spriteManager.getSprite(
				SpriteID.SideIcons.QUEST,
				0);
			if (nativeQuestImage != null)
			{
				questImage = nativeQuestImage;
				questIconLayerImage = null;
			}
		}
		if (questIconBackingImage == null)
		{
			BufferedImage nativeRunOrbImage = spriteManager.getSprite(
				SpriteID.OrbFiller.RUN,
				0);
			if (nativeRunOrbImage != null
				&& nativeRunOrbImage.getWidth() == QUEST_ICON_BACKING_BOUNDS.width
				&& nativeRunOrbImage.getHeight() == QUEST_ICON_BACKING_BOUNDS.height)
			{
				questIconBackingImage = createBlueRunOrbBacking(nativeRunOrbImage);
				questIconLayerImage = null;
			}
		}
	}

	void drawButton(Graphics2D graphics, boolean hovered, boolean open)
	{
		BufferedImage content = buttonContent(open, hovered);
		if (open)
		{
			graphics.setColor(hovered
				? hoverTextureColor(ACTIVE_CENTER_BACKING)
				: ACTIVE_CENTER_BACKING);
			graphics.fillOval(
				BUTTON_INSET,
				BUTTON_INSET,
				BUTTON_SIZE - BUTTON_INSET * 2,
				BUTTON_SIZE - BUTTON_INSET * 2);
		}
		Composite oldComposite = graphics.getComposite();
		if (open && !hovered)
		{
			graphics.setComposite(AlphaComposite.getInstance(
				AlphaComposite.SRC_OVER,
				ACTIVE_CENTER_OPACITY));
		}
		graphics.drawImage(content, 0, 0, null);
		graphics.setComposite(oldComposite);

		BufferedImage rimImage = hovered
			? hoverRimImage != null ? hoverRimImage : normalRimImage
			: normalRimImage;
		if (rimImage != null)
		{
			graphics.drawImage(rimImage, 0, 0, BUTTON_SIZE, BUTTON_SIZE, null);
		}
		else
		{
			Area outerContour = ovalRing(RIM_VISUAL_INSET, RIM_VISUAL_INSET + 1);
			graphics.setColor(hovered ? BUTTON_BORDER_HOVER : BUTTON_BORDER);
			graphics.fill(outerContour);
			graphics.setColor(hovered ? BUTTON_INNER_RING_HOVER : BUTTON_INNER_RING);
			graphics.fill(ovalRing(RIM_VISUAL_INSET + 1, RIM_VISUAL_INSET + 2));
			graphics.setColor(hovered ? hoverTextureColor(BUTTON_BACKGROUND) : BUTTON_BACKGROUND);
			graphics.fill(ovalRing(
				RIM_VISUAL_INSET + 2,
				RIM_VISUAL_INSET + 3 + RIM_TEXTURE_EXTRA_WIDTH));
		}

		if (open && !hovered)
		{
			graphics.setComposite(AlphaComposite.getInstance(
				AlphaComposite.SRC_OVER,
				ACTIVE_CENTER_OPACITY));
		}
		drawQuestIcon(graphics);
		graphics.setComposite(oldComposite);
	}

	private Area ovalRing(int outerInset, int innerInset)
	{
		Area ring = new Area(new Ellipse2D.Double(
			outerInset,
			outerInset,
			BUTTON_SIZE - outerInset * 2,
			BUTTON_SIZE - outerInset * 2));
		ring.subtract(new Area(new Ellipse2D.Double(
			innerInset,
			innerInset,
			BUTTON_SIZE - innerInset * 2,
			BUTTON_SIZE - innerInset * 2)));
		return ring;
	}

	private BufferedImage buttonContent(boolean inset, boolean hovered)
	{
		int cacheIndex = (hovered ? 2 : 0) + (inset ? 1 : 0);
		BufferedImage cached = contentImages[cacheIndex];
		if (cached != null)
		{
			return cached;
		}

		BufferedImage image = new BufferedImage(BUTTON_SIZE, BUTTON_SIZE, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = image.createGraphics();
		try
		{
			applyQualityHints(graphics);
			drawCenter(graphics, inset, hovered);
		}
		finally
		{
			graphics.dispose();
		}

		contentImages[cacheIndex] = image;
		return image;
	}

	private void drawCenter(Graphics2D graphics, boolean inset, boolean hovered)
	{
		int innerSize = BUTTON_SIZE - BUTTON_INSET * 2;
		Color[] colors = centerGradientColors(inset, hovered);
		LinearGradientPaint gradient = new LinearGradientPaint(
			BUTTON_INSET,
			BUTTON_INSET,
			BUTTON_SIZE - BUTTON_INSET,
			BUTTON_SIZE - BUTTON_INSET,
			new float[] {0f, 0.5f, 1f},
			colors);
		java.awt.Paint oldPaint = graphics.getPaint();
		graphics.setPaint(gradient);
		graphics.fillOval(BUTTON_INSET, BUTTON_INSET, innerSize, innerSize);
		graphics.setPaint(oldPaint);

		BufferedImage texture = hovered ? hoverCenterTextureImage : centerTextureImage;
		if (texture != null)
		{
			Composite oldComposite = graphics.getComposite();
			graphics.setComposite(AlphaComposite.getInstance(
				AlphaComposite.SRC_OVER,
				CENTER_TEXTURE_OPACITY));
			graphics.drawImage(texture, 0, 0, BUTTON_SIZE, BUTTON_SIZE, null);
			graphics.setComposite(oldComposite);
		}
	}

	private static Color[] centerGradientColors(boolean inset, boolean hovered)
	{
		Color start = inset ? BUTTON_DARK : BUTTON_LIGHT;
		Color middle = BUTTON_BACKGROUND;
		Color end = inset ? BUTTON_LIGHT : BUTTON_DARK;
		if (hovered)
		{
			start = hoverTextureColor(start);
			middle = hoverTextureColor(middle);
			end = hoverTextureColor(end);
		}
		return new Color[]{start, middle, end};
	}

	private void drawQuestIcon(Graphics2D graphics)
	{
		if (questIconLayerImage != null)
		{
			graphics.drawImage(questIconLayerImage, 0, 0, null);
			return;
		}

		if (questImage != null)
		{
			questIconLayerImage = createQuestIconLayer(
				questImage,
				questIconBacking());
			graphics.drawImage(questIconLayerImage, 0, 0, null);
			return;
		}
		BufferedImage iconLayer = new BufferedImage(
			BUTTON_SIZE,
			BUTTON_SIZE,
			BufferedImage.TYPE_INT_ARGB);
		Graphics2D iconGraphics = iconLayer.createGraphics();
		try
		{
			applyQualityHints(iconGraphics);
			drawQuestIconContents(iconGraphics);
		}
		finally
		{
			iconGraphics.dispose();
		}
		questIconLayerImage = composeQuestIconLayer(iconLayer, questIconBacking());
		graphics.drawImage(questIconLayerImage, 0, 0, null);
	}

	static BufferedImage createQuestIconLayer(BufferedImage nativeQuestImage)
	{
		return createQuestIconLayer(nativeQuestImage, createQuestIconBacking());
	}

	private static BufferedImage createQuestIconLayer(
		BufferedImage nativeQuestImage,
		BufferedImage backing)
	{
		if (nativeQuestImage == null)
		{
			throw new IllegalArgumentException("nativeQuestImage cannot be null");
		}
		BufferedImage iconLayer = new BufferedImage(
			BUTTON_SIZE,
			BUTTON_SIZE,
			BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = iconLayer.createGraphics();
		try
		{
			drawPixelFittedCenteredImage(
				graphics,
				nativeQuestImage,
				JournalGeometry.visibleImageBounds(nativeQuestImage),
				QUEST_NATIVE_ICON_BOUNDS);
		}
		finally
		{
			graphics.dispose();
		}
		return composeQuestIconLayer(iconLayer, backing);
	}

	private static BufferedImage composeQuestIconLayer(
		BufferedImage centeredIconLayer,
		BufferedImage backing)
	{
		BufferedImage outlinedIcon = createOutlinedIcon(
			centeredIconLayer,
			QUEST_ICON_OUTLINE_START,
			QUEST_ICON_OUTLINE_END);
		BufferedImage combined = new BufferedImage(
			BUTTON_SIZE,
			BUTTON_SIZE,
			BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = combined.createGraphics();
		try
		{
			graphics.drawImage(backing, 0, 0, null);
			graphics.drawImage(outlinedIcon, 0, 0, null);
		}
		finally
		{
			graphics.dispose();
		}
		return combined;
	}

	private BufferedImage questIconBacking()
	{
		return questIconBackingImage != null
			? questIconBackingImage
			: createQuestIconBacking();
	}

	private void drawQuestIconContents(Graphics2D graphics)
	{
		graphics.setColor(new Color(224, 191, 108));
		graphics.fillRect(10, 8, 14, 18);
		graphics.setColor(new Color(80, 46, 22));
		graphics.drawLine(13, 13, 21, 13);
		graphics.drawLine(13, 17, 21, 17);
	}

	static BufferedImage createBlueRunOrbBacking(BufferedImage nativeRunOrb)
	{
		if (nativeRunOrb == null
			|| nativeRunOrb.getWidth() != QUEST_ICON_BACKING_BOUNDS.width
			|| nativeRunOrb.getHeight() != QUEST_ICON_BACKING_BOUNDS.height)
		{
			throw new IllegalArgumentException("nativeRunOrb must be 26x26");
		}

		int brightestLuminance = 1;
		for (int y = 0; y < nativeRunOrb.getHeight(); y++)
		{
			for (int x = 0; x < nativeRunOrb.getWidth(); x++)
			{
				int argb = nativeRunOrb.getRGB(x, y);
				if ((argb >>> 24) != 0)
				{
					brightestLuminance = Math.max(
						brightestLuminance,
						pixelLuminance(argb));
				}
			}
		}

		BufferedImage backing = new BufferedImage(
			BUTTON_SIZE,
			BUTTON_SIZE,
			BufferedImage.TYPE_INT_ARGB);
		for (int y = 0; y < nativeRunOrb.getHeight(); y++)
		{
			for (int x = 0; x < nativeRunOrb.getWidth(); x++)
			{
				int argb = nativeRunOrb.getRGB(x, y);
				int alpha = argb >>> 24;
				if (alpha == 0)
				{
					continue;
				}

				int luminance = pixelLuminance(argb);
				int red = (QUEST_ICON_BACKING_HIGHLIGHT.getRed() * luminance
					+ brightestLuminance / 2) / brightestLuminance;
				int green = (QUEST_ICON_BACKING_HIGHLIGHT.getGreen() * luminance
					+ brightestLuminance / 2) / brightestLuminance;
				int blue = (QUEST_ICON_BACKING_HIGHLIGHT.getBlue() * luminance
					+ brightestLuminance / 2) / brightestLuminance;
				int tintedAlpha = (alpha * NATIVE_RUN_ORB_ALPHA + 127) / 255;
				backing.setRGB(
					QUEST_ICON_BACKING_BOUNDS.x + x,
					QUEST_ICON_BACKING_BOUNDS.y + y,
					tintedAlpha << 24 | red << 16 | green << 8 | blue);
			}
		}
		return backing;
	}

	private static int pixelLuminance(int argb)
	{
		int red = argb >> 16 & 0xFF;
		int green = argb >> 8 & 0xFF;
		int blue = argb & 0xFF;
		return (red * 299 + green * 587 + blue * 114 + 500) / 1000;
	}

	private static BufferedImage createQuestIconBacking()
	{
		Rectangle bounds = QUEST_ICON_BACKING_BOUNDS;
		BufferedImage backing = new BufferedImage(
			BUTTON_SIZE,
			BUTTON_SIZE,
			BufferedImage.TYPE_INT_ARGB);
		for (int y = 0; y < bounds.height; y++)
		{
			for (int x = 0; x < bounds.width; x++)
			{
				if (!isQuestIconBackingPixel(x, y))
				{
					continue;
				}

				double lightX = x - QUEST_ICON_BACKING_LIGHT_POSITION;
				double lightY = y - QUEST_ICON_BACKING_LIGHT_POSITION;
				float position = (float) Math.min(
					1.0,
					Math.sqrt(lightX * lightX + lightY * lightY) / bounds.width);
				Color color = position <= 0.55f
					? interpolate(
						QUEST_ICON_BACKING_HIGHLIGHT,
						QUEST_ICON_BACKING_MIDDLE,
						position / 0.55f)
					: interpolate(
						QUEST_ICON_BACKING_MIDDLE,
						QUEST_ICON_BACKING_SHADOW,
						(position - 0.55f) / 0.45f);
				if (isQuestIconBackingBoundaryPixel(x, y))
				{
					float direction = (x + y) / (2f * (bounds.width - 1));
					float insetStrength = QUEST_ICON_BACKING_INSET_MINIMUM
						+ QUEST_ICON_BACKING_INSET_DIRECTIONAL * direction;
					color = interpolate(color, QUEST_ICON_BACKING_INSET_SHADOW, insetStrength);
				}
				backing.setRGB(bounds.x + x, bounds.y + y, color.getRGB());
			}
		}
		return backing;
	}

	private static boolean isQuestIconBackingPixel(int x, int y)
	{
		Rectangle bounds = QUEST_ICON_BACKING_BOUNDS;
		if (x < 0 || y < 0 || x >= bounds.width || y >= bounds.height)
		{
			return false;
		}

		int centerX2 = x * 2 - (bounds.width - 1);
		int centerY2 = y * 2 - (bounds.height - 1);
		return centerX2 * centerX2 + centerY2 * centerY2
			<= QUEST_ICON_BACKING_RADIUS_SQUARED_X4;
	}

	private static boolean isQuestIconBackingBoundaryPixel(int x, int y)
	{
		for (int offsetY = -1; offsetY <= 1; offsetY++)
		{
			for (int offsetX = -1; offsetX <= 1; offsetX++)
			{
				if ((offsetX != 0 || offsetY != 0)
					&& !isQuestIconBackingPixel(x + offsetX, y + offsetY))
				{
					return true;
				}
			}
		}
		return false;
	}

	static BufferedImage createOutlinedIcon(
		BufferedImage source,
		Color outlineStart,
		Color outlineEnd)
	{
		return createOutlinedIcon(
			source,
			outlineStart,
			outlineEnd,
			ICON_DIAGONAL_OUTLINE_OPACITY);
	}

	static BufferedImage createSolidOutlinedIcon(
		BufferedImage source,
		Color outlineColor)
	{
		return createOutlinedIcon(source, outlineColor, outlineColor, 1f);
	}

	private static BufferedImage createOutlinedIcon(
		BufferedImage source,
		Color outlineStart,
		Color outlineEnd,
		float diagonalOpacity)
	{
		BufferedImage outlined = new BufferedImage(
			source.getWidth(),
			source.getHeight(),
			BufferedImage.TYPE_INT_ARGB);
		int[][] outlineAlphas = new int[source.getHeight()][source.getWidth()];
		int minX = source.getWidth();
		int minY = source.getHeight();
		int maxX = -1;
		int maxY = -1;
		int minGradientCoordinate = Integer.MAX_VALUE;
		int maxGradientCoordinate = Integer.MIN_VALUE;
		for (int y = 0; y < source.getHeight(); y++)
		{
			for (int x = 0; x < source.getWidth(); x++)
			{
				if ((source.getRGB(x, y) >>> 24) != 0)
				{
					continue;
				}
				int outlineAlpha = iconOutlineAlpha(source, x, y, diagonalOpacity);
				if (outlineAlpha != 0)
				{
					outlineAlphas[y][x] = outlineAlpha;
					minX = Math.min(minX, x);
					minY = Math.min(minY, y);
					maxX = Math.max(maxX, x);
					maxY = Math.max(maxY, y);
					minGradientCoordinate = Math.min(minGradientCoordinate, x + y);
					maxGradientCoordinate = Math.max(maxGradientCoordinate, x + y);
				}
			}
		}

		int gradientLength = Math.max(1, maxGradientCoordinate - minGradientCoordinate);
		for (int y = minY; y <= maxY; y++)
		{
			for (int x = minX; x <= maxX; x++)
			{
				int outlineAlpha = outlineAlphas[y][x];
				if (outlineAlpha == 0)
				{
					continue;
				}
				float position = (x + y - minGradientCoordinate) / (float) gradientLength;
				Color outlineColor = interpolate(outlineStart, outlineEnd, position);
				outlined.setRGB(
					x,
					y,
					outlineAlpha << 24 | outlineColor.getRGB() & 0x00FFFFFF);
			}
		}

		Graphics2D graphics = outlined.createGraphics();
		try
		{
			graphics.drawImage(source, 0, 0, null);
		}
		finally
		{
			graphics.dispose();
		}
		return outlined;
	}

	private static Color interpolate(Color start, Color end, float position)
	{
		float clampedPosition = Math.max(0f, Math.min(1f, position));
		return new Color(
			Math.round(start.getRed() + (end.getRed() - start.getRed()) * clampedPosition),
			Math.round(start.getGreen() + (end.getGreen() - start.getGreen()) * clampedPosition),
			Math.round(start.getBlue() + (end.getBlue() - start.getBlue()) * clampedPosition));
	}

	private static int greatestCardinalNeighbourAlpha(BufferedImage source, int x, int y)
	{
		int greatestAlpha = 0;
		for (int[] offset : CARDINAL_OFFSETS)
		{
			int neighbourX = x + offset[0];
			int neighbourY = y + offset[1];
			if (neighbourX >= 0 && neighbourY >= 0
				&& neighbourX < source.getWidth()
				&& neighbourY < source.getHeight())
			{
				greatestAlpha = Math.max(
					greatestAlpha,
					source.getRGB(neighbourX, neighbourY) >>> 24);
			}
		}
		return greatestAlpha;
	}

	private static int iconOutlineAlpha(
		BufferedImage source,
		int x,
		int y,
		float diagonalOpacity)
	{
		int cardinalAlpha = greatestCardinalNeighbourAlpha(source, x, y);
		int diagonalAlpha = 0;
		for (int offsetY = -1; offsetY <= 1; offsetY += 2)
		{
			for (int offsetX = -1; offsetX <= 1; offsetX += 2)
			{
				int neighbourX = x + offsetX;
				int neighbourY = y + offsetY;
				if (neighbourX >= 0 && neighbourY >= 0
					&& neighbourX < source.getWidth()
					&& neighbourY < source.getHeight())
				{
					diagonalAlpha = Math.max(
						diagonalAlpha,
						source.getRGB(neighbourX, neighbourY) >>> 24);
				}
			}
		}
		return Math.max(
			cardinalAlpha,
			Math.round(diagonalAlpha * diagonalOpacity));
	}

	private void invalidateContentImages()
	{
		contentImages = new BufferedImage[4];
	}

	static BufferedImage createTexturedRim(
		BufferedImage source,
		Color borderColor)
	{
		return createTexturedRim(source, borderColor, BUTTON_INNER_RING, false);
	}

	static BufferedImage createTexturedRim(
		BufferedImage source,
		Color borderColor,
		Color innerRingColor,
		boolean hovered)
	{
		BufferedImage image = new BufferedImage(
			source.getWidth(),
			source.getHeight(),
			BufferedImage.TYPE_INT_ARGB);
		boolean[][] rimMask = createInsetMask(source, RIM_VISUAL_INSET);
		double centerX = (source.getWidth() - 1) / 2.0;
		double centerY = (source.getHeight() - 1) / 2.0;
		double innerRadius = Math.max(
			0.0,
			Math.min(source.getWidth(), source.getHeight()) * 0.39
				- RIM_VISUAL_INSET
				- RIM_TEXTURE_EXTRA_WIDTH);
		double innerRadiusSquared = innerRadius * innerRadius;
		for (int y = 0; y < source.getHeight(); y++)
		{
			for (int x = 0; x < source.getWidth(); x++)
			{
				double dx = x - centerX;
				double dy = y - centerY;
				if (dx * dx + dy * dy < innerRadiusSquared)
				{
					continue;
				}
				int sourceArgb = source.getRGB(x, y);
				if (rimMask[y][x])
				{
					int textureArgb = brownTexturePixel(sourceArgb);
					image.setRGB(x, y, hovered ? hoverTexturePixel(textureArgb) : textureArgb);
				}
			}
		}

		for (int y = 0; y < source.getHeight(); y++)
		{
			for (int x = 0; x < source.getWidth(); x++)
			{
				int sourceArgb = source.getRGB(x, y);
				if (!rimMask[y][x] || !isOuterEdgePixel(rimMask, x, y))
				{
					continue;
				}
				int alpha = sourceArgb >>> 24;
				image.setRGB(x, y, alpha << 24 | borderColor.getRGB() & 0x00FFFFFF);
			}
		}

		for (int y = 0; y < source.getHeight(); y++)
		{
			for (int x = 0; x < source.getWidth(); x++)
			{
				int sourceArgb = source.getRGB(x, y);
				if (!rimMask[y][x]
					|| isOuterEdgePixel(rimMask, x, y)
					|| !isInnerEdgePixel(rimMask, x, y))
				{
					continue;
				}
				int alpha = sourceArgb >>> 24;
				image.setRGB(x, y, alpha << 24 | innerRingColor.getRGB() & 0x00FFFFFF);
			}
		}
		return image;
	}

	private static boolean[][] createInsetMask(BufferedImage source, int inset)
	{
		boolean[][] mask = new boolean[source.getHeight()][source.getWidth()];
		for (int y = 0; y < source.getHeight(); y++)
		{
			for (int x = 0; x < source.getWidth(); x++)
			{
				boolean opaque = true;
				for (int offsetY = -inset; offsetY <= inset && opaque; offsetY++)
				{
					for (int offsetX = -inset; offsetX <= inset; offsetX++)
					{
						int neighbourX = x + offsetX;
						int neighbourY = y + offsetY;
						if (neighbourX < 0 || neighbourY < 0
							|| neighbourX >= source.getWidth()
							|| neighbourY >= source.getHeight()
							|| (source.getRGB(neighbourX, neighbourY) >>> 24) == 0)
						{
							opaque = false;
							break;
						}
					}
				}
				mask[y][x] = opaque;
			}
		}
		return mask;
	}

	private static boolean isInnerEdgePixel(boolean[][] mask, int x, int y)
	{
		for (int[] offset : CARDINAL_OFFSETS)
		{
			int neighbourX = x + offset[0];
			int neighbourY = y + offset[1];
			if (neighbourX >= 0 && neighbourY >= 0
				&& neighbourX < mask[0].length
				&& neighbourY < mask.length
				&& mask[neighbourY][neighbourX]
				&& isOuterEdgePixel(mask, neighbourX, neighbourY))
			{
				return true;
			}
		}
		return false;
	}

	private static boolean isOuterEdgePixel(boolean[][] mask, int x, int y)
	{
		for (int[] offset : CARDINAL_OFFSETS)
		{
			int neighbourX = x + offset[0];
			int neighbourY = y + offset[1];
			if (neighbourX < 0 || neighbourY < 0
				|| neighbourX >= mask[0].length
				|| neighbourY >= mask.length
				|| !mask[neighbourY][neighbourX])
			{
				return true;
			}
		}
		return false;
	}

	private static BufferedImage createBrownCenterTexture(BufferedImage source)
	{
		BufferedImage image = new BufferedImage(
			source.getWidth(),
			source.getHeight(),
			BufferedImage.TYPE_INT_ARGB);
		double centerX = (source.getWidth() - 1) / 2.0;
		double centerY = (source.getHeight() - 1) / 2.0;
		double radius = Math.min(source.getWidth(), source.getHeight()) * 0.41;
		double radiusSquared = radius * radius;
		for (int y = 0; y < source.getHeight(); y++)
		{
			for (int x = 0; x < source.getWidth(); x++)
			{
				double dx = x - centerX;
				double dy = y - centerY;
				if (dx * dx + dy * dy > radiusSquared)
				{
					continue;
				}
				image.setRGB(x, y, brownTexturePixel(source.getRGB(x, y)));
			}
		}
		return image;
	}

	private static BufferedImage createHoverTexture(BufferedImage source)
	{
		BufferedImage image = new BufferedImage(
			source.getWidth(),
			source.getHeight(),
			BufferedImage.TYPE_INT_ARGB);
		for (int y = 0; y < source.getHeight(); y++)
		{
			for (int x = 0; x < source.getWidth(); x++)
			{
				image.setRGB(x, y, hoverTexturePixel(source.getRGB(x, y)));
			}
		}
		return image;
	}

	static Color hoverTextureColor(Color color)
	{
		return new Color(
			Math.min(255, color.getRed() + TEXTURE_HOVER_TO.getRed() - TEXTURE_HOVER_FROM.getRed()),
			Math.min(255, color.getGreen() + TEXTURE_HOVER_TO.getGreen() - TEXTURE_HOVER_FROM.getGreen()),
			Math.min(255, color.getBlue() + TEXTURE_HOVER_TO.getBlue() - TEXTURE_HOVER_FROM.getBlue()),
			color.getAlpha());
	}

	private static int hoverTexturePixel(int argb)
	{
		int alpha = argb >>> 24;
		if (alpha == 0)
		{
			return 0;
		}
		Color hovered = hoverTextureColor(new Color(argb, true));
		return alpha << 24 | hovered.getRGB() & 0x00FFFFFF;
	}

	private static int brownTexturePixel(int argb)
	{
		int alpha = argb >>> 24;
		if (alpha == 0)
		{
			return 0;
		}
		int sourceRed = (argb >>> 16) & 0xFF;
		int sourceGreen = (argb >>> 8) & 0xFF;
		int sourceBlue = argb & 0xFF;
		float luminance = (sourceRed * 0.2126f + sourceGreen * 0.7152f + sourceBlue * 0.0722f) / 255f;
		float shade = 0.86f + luminance * 0.28f;
		int red = Math.min(255, Math.round(BUTTON_BACKGROUND.getRed() * shade));
		int green = Math.min(255, Math.round(BUTTON_BACKGROUND.getGreen() * shade));
		int blue = Math.min(255, Math.round(BUTTON_BACKGROUND.getBlue() * shade));
		return alpha << 24 | red << 16 | green << 8 | blue;
	}

	static void drawPixelFittedCenteredImage(
		Graphics2D graphics,
		BufferedImage image,
		Rectangle visible,
		Rectangle bounds)
	{
		if (visible == null || visible.isEmpty())
		{
			visible = new Rectangle(0, 0, image.getWidth(), image.getHeight());
		}
		if (visible.width <= 0 || visible.height <= 0 || bounds.isEmpty())
		{
			return;
		}
		double scale = Math.min(
			bounds.width / (double) visible.width,
			bounds.height / (double) visible.height);
		int width = Math.max(1, Math.min(bounds.width, (int) Math.round(visible.width * scale)));
		int height = Math.max(1, Math.min(bounds.height, (int) Math.round(visible.height * scale)));
		int destinationX = bounds.x + (bounds.width - width) / 2;
		int destinationY = bounds.y + (bounds.height - height) / 2;
		Graphics2D pixelGraphics = (Graphics2D) graphics.create();
		try
		{
			pixelGraphics.setRenderingHint(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_OFF);
			pixelGraphics.setRenderingHint(
				RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			pixelGraphics.drawImage(
				image,
				destinationX,
				destinationY,
				destinationX + width,
				destinationY + height,
				visible.x,
				visible.y,
				visible.x + visible.width,
				visible.y + visible.height,
				null);
		}
		finally
		{
			pixelGraphics.dispose();
		}
	}

	private void applyQualityHints(Graphics2D graphics)
	{
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setRenderingHint(
			RenderingHints.KEY_INTERPOLATION,
			RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	}

	boolean isRendered()
	{
		return hitBounds != null;
	}

	boolean contains(Point point)
	{
		Rectangle bounds = hitBounds;
		return bounds != null
			&& ownsInput(bounds, point);
	}

	static boolean ownsInput(Rectangle buttonBounds, Point point)
	{
		return buttonBounds != null && point != null && buttonBounds.contains(point);
	}

	void resetHitState()
	{
		invalidateAutomaticPlacement();
		clearBounds();
	}

	private void clearBounds()
	{
		hitBounds = null;
		getBounds().setBounds(0, 0, 0, 0);
	}
}
