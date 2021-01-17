/*
 *
 *  * Copyright (c) 2021, Senmori
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  *
 *  * 1. Redistributions of source code must retain the above copyright notice, this
 *  *    list of conditions and the following disclaimer.
 *  * 2. Redistributions in binary form must reproduce the above copyright notice,
 *  *    this list of conditions and the following disclaimer in the documentation
 *  *    and/or other materials provided with the distribution.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package com.questhelper;

import com.questhelper.questhelpers.QuestDebugRenderer;
import com.questhelper.questhelpers.QuestHelper;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.components.PanelComponent;

public class QuestHelperDebugOverlay extends OverlayPanel implements QuestDebugRenderer
{
	private final QuestHelperPlugin plugin;
	@Inject
	public QuestHelperDebugOverlay(QuestHelperPlugin plugin)
	{
		this.plugin = plugin;
		setLayer(OverlayLayer.ALWAYS_ON_TOP);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		QuestHelper quest = plugin.getSelectedQuest();

		if (plugin.isDeveloperMode() && quest != null)
		{
			renderDebugOverlay(graphics, plugin, quest, panelComponent);
			renderDebugWorldOverlayHint(graphics, plugin, quest, panelComponent);
			renderDebugWidgetOverlayHint(graphics, plugin, quest, panelComponent);
		}

		return super.render(graphics);
	}

	@Override
	public void renderDebugOverlay(Graphics graphics, QuestHelperPlugin plugin, QuestHelper quest, PanelComponent panelComponent)
	{
		if (plugin.isDeveloperMode())
		{
			quest.renderDebugOverlay(graphics, plugin, quest, panelComponent);
		}
	}
}
