/*
 * Copyright (c) 2018, Lotto <https://github.com/devLotto>
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
package com.questhelper.overlays;

import com.questhelper.QuestHelperConfig;
import com.questhelper.QuestHelperPlugin;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import javax.inject.Inject;
import com.questhelper.questhelpers.QuestHelper;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.JagexColors;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

public class QuestHelperWorldOverlay extends Overlay
{
	public static final int IMAGE_Z_OFFSET = 30;

	private final QuestHelperPlugin plugin;

	@Inject
	public QuestHelperWorldOverlay(QuestHelperPlugin plugin)
	{
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
		this.plugin = plugin;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		boolean noOverlaysDrawn = !plugin.getConfig().showSymbolOverlay()
			&& plugin.getConfig().highlightStyleGroundItems() == QuestHelperConfig.GroundItemHighlightStyle.NONE
			&& plugin.getConfig().highlightStyleNpcs() == QuestHelperConfig.NpcHighlightStyle.NONE
			&& plugin.getConfig().highlightStyleObjects() == QuestHelperConfig.ObjectHighlightStyle.NONE;
		if (noOverlaysDrawn)
		{
			return null;
		}

		if (plugin.getCheerer() != null)
		{
			LocalPoint lp = LocalPoint.fromWorld(plugin.getClient(), plugin.getCheerer().worldPoint);
			if (lp != null)
			{
				Point p = Perspective.localToCanvas(plugin.getClient(), lp, plugin.getClient().getPlane(),
					plugin.getCheerer().runeLiteObject.getModelHeight());
				if (p != null)
				{
					Font overheadFont = FontManager.getRunescapeBoldFont();
					FontMetrics metrics = graphics.getFontMetrics(overheadFont);
					Point shiftedP = new Point(p.getX() - (metrics.stringWidth(plugin.getCheerer().getMessage()) / 2), p.getY());

					graphics.setFont(overheadFont);
					OverlayUtil.renderTextLocation(graphics, shiftedP, plugin.getCheerer().getMessage(),
						JagexColors.YELLOW_INTERFACE_TEXT);
				}
			}
		}

		QuestHelper quest = plugin.getSelectedQuest();

		if (quest != null && quest.getCurrentStep() != null)
		{
			quest.getCurrentStep().makeWorldOverlayHint(graphics, plugin);
		}

		plugin.backgroundHelpers.forEach((name, questHelper) -> questHelper.getCurrentStep().makeWorldOverlayHint(graphics, plugin));

		plugin.getRuneliteObjectManager().makeWidgetOverlayHint(graphics);

		return null;
	}
}
