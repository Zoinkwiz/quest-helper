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
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LauncherOverlayVisualTest
{
	@Test
	public void closedButtonUsesConcentricBrownRings()
	{
		BufferedImage image = render(false, false);
		Color rim = colorAt(image, 17, 1);
		Color innerRing = colorAt(image, 17, 2);

		assertEquals(new Color(0x32, 0x2B, 0x19), opaque(rim));
		assertEquals(new Color(0x5D, 0x54, 0x4A), opaque(innerRing));
		assertEquals(0, colorAt(image, 17, 0).getAlpha());
	}

	@Test
	public void openButtonFadesContentOverOpaqueBacking()
	{
		BufferedImage closed = render(false, false);
		BufferedImage open = render(false, true);
		Color center = colorAt(open, 7, 17);
		Color icon = colorAt(open, 11, 9);
		Color rim = colorAt(open, 17, 2);

		assertEquals(255, center.getAlpha());
		assertEquals(255, icon.getAlpha());
		assertTrue(center.getBlue() < colorAt(closed, 7, 17).getBlue());
		assertTrue(icon.getRed() < colorAt(closed, 11, 9).getRed());
		assertEquals(255, rim.getAlpha());
		assertEquals(255, colorAt(closed, 11, 9).getAlpha());
	}

	@Test
	public void hoveringOpenButtonRestoresOpaqueGradientAndIcon()
	{
		BufferedImage faded = render(false, true);
		BufferedImage hovered = render(true, true);

		assertTrue(colorAt(hovered, 11, 9).getRed() > colorAt(faded, 11, 9).getRed());
		assertEquals(255, colorAt(hovered, 7, 17).getAlpha());
		assertEquals(255, colorAt(hovered, 11, 9).getAlpha());
	}

	@Test
	public void hoveringClosedButtonBrightensBothRingsButNotIcon()
	{
		BufferedImage normal = render(false, false);
		BufferedImage hovered = render(true, false);

		assertEquals(new Color(0x4B, 0x43, 0x38), opaque(colorAt(hovered, 17, 1)));
		assertEquals(new Color(0x7D, 0x71, 0x62), opaque(colorAt(hovered, 17, 2)));
		assertEquals(normal.getRGB(11, 9), hovered.getRGB(11, 9));
	}

	@Test
	public void hoverTexturePaletteUsesExactColorMapping()
	{
		assertEquals(
			new Color(0x69, 0x5C, 0x4D),
			LauncherOverlay.hoverTextureColor(new Color(0x47, 0x3F, 0x35)));
	}

	@Test
	public void nativeRimUsesExactConcentricRingsAndBrownTexture()
	{
		BufferedImage source = new BufferedImage(34, 34, BufferedImage.TYPE_INT_ARGB);
		double center = 16.5;
		for (int y = 0; y < source.getHeight(); y++)
		{
			for (int x = 0; x < source.getWidth(); x++)
			{
				double deltaX = x - center;
				double deltaY = y - center;
				if (deltaX * deltaX + deltaY * deltaY > 16.5 * 16.5)
				{
					continue;
				}
				int shade = 40 + y * 5;
				source.setRGB(x, y, new Color(shade, shade, shade).getRGB());
			}
		}

		Color border = new Color(0x32, 0x2B, 0x19);
		BufferedImage rim = LauncherOverlay.createTexturedRim(
			source,
			border);

		assertEquals(0, colorAt(rim, 17, 17).getAlpha());
		assertEquals(border, opaque(colorAt(rim, 17, 1)));
		assertEquals(new Color(0x5D, 0x54, 0x4A), opaque(colorAt(rim, 17, 2)));
		Color texture = colorAt(rim, 17, 3);
		assertEquals(255, texture.getAlpha());
		assertEquals(255, colorAt(rim, 17, 4).getAlpha());
		assertEquals(255, colorAt(rim, 17, 5).getAlpha());
		assertEquals(0, colorAt(rim, 17, 6).getAlpha());
		assertEquals(border, opaque(colorAt(rim, 5, 5)));
		assertEquals(new Color(0x5D, 0x54, 0x4A), opaque(colorAt(rim, 6, 5)));
		assertTrue(texture.getRed() > texture.getGreen());
		assertTrue(texture.getGreen() > texture.getBlue());
		assertTrue(rim.getRGB(17, 3) != source.getRGB(17, 3));
	}

	@Test
	public void questIconGradientUsesWholePixelsWithoutChangingItsPixels()
	{
		BufferedImage source = new BufferedImage(5, 5, BufferedImage.TYPE_INT_ARGB);
		Color iconColor = new Color(0xE0, 0xBF, 0x6C);
		source.setRGB(2, 2, iconColor.getRGB());

		BufferedImage outlined = LauncherOverlay.createOutlinedIcon(
			source,
			new Color(0xAF, 0xAF, 0xA4),
			new Color(0x37, 0x37, 0x37));

		assertEquals(iconColor, opaque(colorAt(outlined, 2, 2)));
		assertEquals(new Color(0x91, 0x91, 0x89), opaque(colorAt(outlined, 2, 1)));
		assertEquals(new Color(0x91, 0x91, 0x89), opaque(colorAt(outlined, 1, 2)));
		assertEquals(new Color(0x55, 0x55, 0x52), opaque(colorAt(outlined, 3, 2)));
		assertEquals(new Color(0x55, 0x55, 0x52), opaque(colorAt(outlined, 2, 3)));
		assertEquals(new Color(0xAF, 0xAF, 0xA4), opaque(colorAt(outlined, 1, 1)));
		assertEquals(new Color(0x37, 0x37, 0x37), opaque(colorAt(outlined, 3, 3)));
		assertEquals(255, colorAt(outlined, 1, 1).getAlpha());
		assertEquals(255, colorAt(outlined, 3, 3).getAlpha());
		assertTrue(brightness(colorAt(outlined, 2, 1))
			> brightness(colorAt(outlined, 3, 2)));
		assertEquals(0, colorAt(outlined, 0, 0).getAlpha());
	}

	@Test
	public void solidOutlineUtilityUsesWholeOpaquePixels()
	{
		BufferedImage source = new BufferedImage(5, 5, BufferedImage.TYPE_INT_ARGB);
		source.setRGB(2, 2, Color.WHITE.getRGB());

		BufferedImage outlined = LauncherOverlay.createSolidOutlinedIcon(
			source,
			Color.BLACK);

		for (int y = 1; y <= 3; y++)
		{
			for (int x = 1; x <= 3; x++)
			{
				if (x == 2 && y == 2)
				{
					continue;
				}
				assertEquals(Color.BLACK.getRGB(), outlined.getRGB(x, y));
			}
		}
	}

	@Test
	public void completeNativeQuestIconLayerOutlinesOnlyTheIconOverTheBlueCircle()
	{
		BufferedImage source = new BufferedImage(18, 18, BufferedImage.TYPE_INT_ARGB);
		Color first = new Color(0x18, 0x79, 0xC5);
		Color second = new Color(0xD9, 0x9B, 0x31);
		for (int y = 0; y < source.getHeight(); y++)
		{
			int distance = Math.abs(y * 2 - 17);
			int halfWidth = Math.max(0, (17 - distance) / 2);
			for (int x = 8 - halfWidth; x <= 9 + halfWidth; x++)
			{
				source.setRGB(x, y, ((x + y) & 1) == 0
					? first.getRGB()
					: second.getRGB());
			}
		}

		BufferedImage layer = LauncherOverlay.createQuestIconLayer(source);
		BufferedImage backingOnly = LauncherOverlay.createQuestIconLayer(
			new BufferedImage(18, 18, BufferedImage.TYPE_INT_ARGB));
		for (int y = 0; y < source.getHeight(); y++)
		{
			for (int x = 0; x < source.getWidth(); x++)
			{
				if ((source.getRGB(x, y) >>> 24) != 0)
				{
					assertEquals(source.getRGB(x, y), layer.getRGB(x + 8, y + 8));
				}
			}
		}
		Color topLeft = colorAt(layer, 8, 8);
		Color bottomLeft = colorAt(layer, 8, 25);
		Color bottomRight = colorAt(layer, 25, 25);
		assertEquals(255, topLeft.getAlpha());
		assertEquals(255, bottomLeft.getAlpha());
		assertEquals(255, bottomRight.getAlpha());
		assertTrue(brightness(topLeft) > brightness(bottomLeft));
		assertTrue(brightness(topLeft) > brightness(bottomRight));
		assertEquals(backingOnly.getRGB(5, 12), layer.getRGB(5, 12));
		assertTrue(backingOnly.getRGB(16, 7) != layer.getRGB(16, 7));
		assertEquals(0, colorAt(layer, 3, 17).getAlpha());
	}

	@Test
	public void nativeRunOrbBackingKeepsItsMaskAndDirectionalLuminance()
	{
		BufferedImage nativeRunOrb = new BufferedImage(26, 26, BufferedImage.TYPE_INT_ARGB);
		nativeRunOrb.setRGB(0, 12, new Color(200, 200, 200).getRGB());
		nativeRunOrb.setRGB(25, 12, new Color(50, 50, 50).getRGB());

		BufferedImage backing = LauncherOverlay.createBlueRunOrbBacking(nativeRunOrb);
		Color highlight = colorAt(backing, 4, 16);
		Color shadow = colorAt(backing, 29, 16);

		assertEquals(new Color(0x98, 0xA8, 0xFF), opaque(highlight));
		assertEquals(new Color(38, 42, 64), opaque(shadow));
		assertEquals(230, highlight.getAlpha());
		assertEquals(230, shadow.getAlpha());
		assertEquals(0, colorAt(backing, 3, 16).getAlpha());
		assertEquals(0, colorAt(backing, 30, 16).getAlpha());
	}

	@Test
	public void blueBackingAndInsetShadowShareTheSameFullSizeWholePixelCircle()
	{
		BufferedImage backing = LauncherOverlay.createQuestIconLayer(
			new BufferedImage(18, 18, BufferedImage.TYPE_INT_ARGB));
		Rectangle bounds = new Rectangle(4, 4, 26, 26);

		for (int y = 0; y < bounds.height; y++)
		{
			for (int x = 0; x < bounds.width; x++)
			{
				int alpha = colorAt(backing, bounds.x + x, bounds.y + y).getAlpha();
				int centerX2 = x * 2 - (bounds.width - 1);
				int centerY2 = y * 2 - (bounds.height - 1);
				int expectedAlpha = centerX2 * centerX2 + centerY2 * centerY2 <= 650
					? 255
					: 0;
				assertEquals(expectedAlpha, alpha);
				assertEquals(
					alpha,
					colorAt(backing, bounds.x + bounds.width - 1 - x, bounds.y + y).getAlpha());
				assertEquals(
					alpha,
					colorAt(backing, bounds.x + x, bounds.y + bounds.height - 1 - y).getAlpha());
			}
		}

		assertEquals(255, colorAt(backing, bounds.x, bounds.y + 12).getAlpha());
		assertEquals(255, colorAt(backing, bounds.x + bounds.width - 1, bounds.y + 12).getAlpha());
		assertEquals(255, colorAt(backing, bounds.x + 12, bounds.y).getAlpha());
		assertEquals(255, colorAt(backing, bounds.x + 12, bounds.y + bounds.height - 1).getAlpha());
		assertEquals(0, colorAt(backing, bounds.x - 1, bounds.y + 12).getAlpha());
		assertEquals(0, colorAt(backing, bounds.x + bounds.width, bounds.y + 12).getAlpha());
	}

	@Test
	public void blueBackingUsesAnInsetShadowThatIsStrongestAtTheBottomRight()
	{
		BufferedImage backing = LauncherOverlay.createQuestIconLayer(
			new BufferedImage(18, 18, BufferedImage.TYPE_INT_ARGB));
		Rectangle bounds = new Rectangle(4, 4, 26, 26);
		int middle = bounds.width / 2 - 1;

		Color topEdge = colorAt(backing, bounds.x + middle, bounds.y);
		Color topInterior = colorAt(backing, bounds.x + middle, bounds.y + 1);
		Color leftEdge = colorAt(backing, bounds.x, bounds.y + middle);
		Color leftInterior = colorAt(backing, bounds.x + 1, bounds.y + middle);
		Color rightEdge = colorAt(
			backing,
			bounds.x + bounds.width - 1,
			bounds.y + middle);
		Color rightInterior = colorAt(
			backing,
			bounds.x + bounds.width - 2,
			bounds.y + middle);
		Color bottomEdge = colorAt(
			backing,
			bounds.x + middle,
			bounds.y + bounds.height - 1);
		Color bottomInterior = colorAt(
			backing,
			bounds.x + middle,
			bounds.y + bounds.height - 2);

		assertTrue(brightness(topEdge) < brightness(topInterior));
		assertTrue(brightness(leftEdge) < brightness(leftInterior));
		assertTrue(brightness(rightEdge) < brightness(rightInterior));
		assertTrue(brightness(bottomEdge) < brightness(bottomInterior));
		assertTrue(brightness(rightEdge) < brightness(leftEdge));
		assertTrue(brightness(bottomEdge) < brightness(topEdge));
	}

	@Test
	public void questIconGradientOutlineRemainsAboveExpandedBrownBand()
	{
		BufferedImage image = render(false, false);

		assertTrue(brightness(colorAt(image, 9, 7))
			> brightness(colorAt(image, 24, 26)));
	}

	@Test
	public void hoveredNativeRimUsesExactLighterContourRingAndTexturePalette()
	{
		BufferedImage source = new BufferedImage(34, 34, BufferedImage.TYPE_INT_ARGB);
		for (int y = 0; y < source.getHeight(); y++)
		{
			for (int x = 0; x < source.getWidth(); x++)
			{
				int shade = 40 + y * 5;
				source.setRGB(x, y, new Color(shade, shade, shade).getRGB());
			}
		}

		BufferedImage normal = LauncherOverlay.createTexturedRim(
			source,
			new Color(0x32, 0x2B, 0x19));
		BufferedImage hovered = LauncherOverlay.createTexturedRim(
			source,
			new Color(0x4B, 0x43, 0x38),
			new Color(0x7D, 0x71, 0x62),
			true);

		assertEquals(new Color(0x4B, 0x43, 0x38), opaque(colorAt(hovered, 0, 17)));
		assertEquals(new Color(0x7D, 0x71, 0x62), opaque(colorAt(hovered, 1, 17)));
		assertTrue(brightness(colorAt(hovered, 2, 17))
			> brightness(colorAt(normal, 2, 17)));
		assertTrue(source.getRGB(2, 17) != hovered.getRGB(2, 17));
		assertEquals(255, colorAt(hovered, 5, 17).getAlpha());
		assertEquals(0, colorAt(hovered, 6, 17).getAlpha());
	}

	private BufferedImage render(boolean hovered, boolean open)
	{
		BufferedImage image = new BufferedImage(
			LauncherOverlay.BUTTON_SIZE,
			LauncherOverlay.BUTTON_SIZE,
			BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = image.createGraphics();
		try
		{
			LauncherOverlay overlay = new LauncherOverlay(null, null, null, null, null);
			overlay.drawButton(graphics, hovered, open);
		}
		finally
		{
			graphics.dispose();
		}
		return image;
	}

	private Color colorAt(BufferedImage image, int x, int y)
	{
		return new Color(image.getRGB(x, y), true);
	}

	private Color opaque(Color color)
	{
		return new Color(color.getRed(), color.getGreen(), color.getBlue());
	}

	private int brightness(Color color)
	{
		return color.getRed() + color.getGreen() + color.getBlue();
	}

}
