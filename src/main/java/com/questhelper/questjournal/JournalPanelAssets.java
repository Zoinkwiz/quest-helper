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
import java.awt.image.BufferedImage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.runelite.api.gameval.SpriteID;
import net.runelite.client.game.SpriteManager;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
final class JournalPanelAssets
{
	static final Color SEPARATOR_COLOR = new Color(0x2E, 0x2B, 0x23);
	static final int BORDER_SIZE = 6;
	static final int SEPARATOR_SIZE = 1;
	private static final float BANK_SURFACE_OPACITY = 234f / 255f;

	final BufferedImage texture;
	final float surfaceOpacity;
	final boolean preferredTexture;
	final BufferedImage top;
	final BufferedImage left;
	final BufferedImage bottom;
	final BufferedImage right;
	final BufferedImage topLeft;
	final BufferedImage topRight;
	final BufferedImage bottomLeft;
	final BufferedImage bottomRight;
	final BufferedImage titleSeparator;
	final BufferedImage questIcon;
	final BufferedImage diaryIcon;
	final BufferedImage skillIcon;
	final BufferedImage combatIcon;
	final BufferedImage questSearchIcon;
	final BufferedImage settingsButton;
	final BufferedImage settingsButtonHovered;
	final BufferedImage closeIcon;
	final BufferedImage closeIconHovered;
	final BufferedImage scrollbarTop;
	final BufferedImage scrollbarMiddle;
	final BufferedImage scrollbarBottom;
	final BufferedImage scrollbarTrack;

	static JournalPanelAssets load(SpriteManager spriteManager)
	{
		BufferedImage texture = spriteManager.getSprite(SpriteID.TRADEBACKING, 0);
		boolean preferredTexture = texture != null;
		float surfaceOpacity = BANK_SURFACE_OPACITY;
		if (texture == null)
		{
			texture = spriteManager.getSprite(SpriteID.TEX_BROWN, 0);
			surfaceOpacity = 1f;
		}
		return new JournalPanelAssets(
			texture,
			surfaceOpacity,
			preferredTexture,
			spriteManager.getSprite(SpriteID.Steelborder2.EDGE_TOP, 0),
			spriteManager.getSprite(SpriteID.Miscgraphics.IRON_RIVETS_VERTICAL, 0),
			spriteManager.getSprite(SpriteID.Miscgraphics.IRON_RIVETS_HORIZONTAL, 0),
			spriteManager.getSprite(SpriteID.Steelborder2.EDGE_RIGHT, 0),
			spriteManager.getSprite(SpriteID.Steelborder.TOP_LEFT, 0),
			spriteManager.getSprite(SpriteID.Steelborder.TOP_RIGHT, 0),
			spriteManager.getSprite(SpriteID.Steelborder.BOTTOM_LEFT, 0),
			spriteManager.getSprite(SpriteID.Steelborder.BOTTOM_RIGHT, 0),
			spriteManager.getSprite(SpriteID.SteelborderDivider._0, 0),
			spriteManager.getSprite(SpriteID.SideIcons.QUEST, 0),
			spriteManager.getSprite(SpriteID.AchievementDiaryIcons.GREEN_ACHIEVEMENT_DIARIES, 0),
			spriteManager.getSprite(SpriteID.SideIcons.STATS, 0),
			spriteManager.getSprite(JournalOverlay.activeQuestIconSpriteId(), 0),
			spriteManager.getSprite(SpriteID.GeSmallicons.SEARCH, 0),
			spriteManager.getSprite(SpriteID.MenuButtons._10, 0),
			spriteManager.getSprite(SpriteID.MenuButtons._11, 0),
			spriteManager.getSprite(SpriteID.CloseButtons._14, 0),
			spriteManager.getSprite(SpriteID.CloseButtons._15, 0),
			spriteManager.getSprite(SpriteID.ScrollbarDraggerV2.TOP, 0),
			spriteManager.getSprite(SpriteID.ScrollbarDraggerV2.MIDDLE, 0),
			spriteManager.getSprite(SpriteID.ScrollbarDraggerV2.BOTTOM, 0),
			spriteManager.getSprite(SpriteID.ScrollbarDraggerV2.TRACK, 0));
	}

	boolean hasTexture()
	{
		return texture != null;
	}

	boolean isComplete()
	{
		return preferredTexture
			&& texture != null
			&& hasFrame()
			&& titleSeparator != null
			&& questIcon != null
			&& diaryIcon != null
			&& skillIcon != null
			&& combatIcon != null
			&& questSearchIcon != null
			&& settingsButton != null
			&& settingsButtonHovered != null
			&& closeIcon != null
			&& closeIconHovered != null
			&& hasScrollbar();
	}

	boolean hasFrame()
	{
		return top != null
			&& left != null
			&& bottom != null
			&& right != null
			&& topLeft != null
			&& topRight != null
			&& bottomLeft != null
			&& bottomRight != null;
	}

	boolean hasScrollbar()
	{
		return scrollbarTop != null
			&& scrollbarMiddle != null
			&& scrollbarBottom != null
			&& scrollbarTrack != null;
	}
}
