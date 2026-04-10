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

import com.questhelper.QuestHelperPlugin;
import com.questhelper.managers.HelperConstructManager;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.steps.overlay.WorldLines;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;

public class QuestHelperWorldLineOverlay extends Overlay
{
	private final QuestHelperPlugin plugin;
	private final HelperConstructManager helperConstructManager;

	@Inject
	public QuestHelperWorldLineOverlay(QuestHelperPlugin plugin, HelperConstructManager helperConstructManager)
	{
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ALWAYS_ON_TOP);
		this.plugin = plugin;
		this.helperConstructManager = helperConstructManager;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		boolean showQuestWorldLines = plugin.getConfig().showWorldLines();
		boolean showConstructPreview = helperConstructManager.isWorldMapRoutePreviewEnabled();
		if (!showQuestWorldLines && !showConstructPreview)
		{
			return null;
		}

		QuestHelper quest = plugin.getSelectedQuest();

		if (showQuestWorldLines && quest != null && quest.getCurrentStep() != null)
		{
			quest.getCurrentStep().makeWorldLineOverlayHint(graphics, plugin);
		}
		if (showConstructPreview)
		{
			WorldLines.createWorldMapLines(
				graphics,
				plugin.getClient(),
				helperConstructManager.getWorldMapRouteLinePoints(),
				plugin.getConfig().targetOverlayColor());
		}

		return null;
	}
}
