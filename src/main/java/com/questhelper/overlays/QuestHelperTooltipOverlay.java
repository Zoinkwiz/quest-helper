/*
 * Copyright (c) 2024, Zoinkwiz <https://github.com/Zoinkwiz>
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
import net.runelite.api.Client;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;
import javax.inject.Inject;
import java.awt.*;

public class QuestHelperTooltipOverlay extends OverlayPanel
{
	private final QuestHelperPlugin questHelperPlugin;
	private final Client client;
	private final QuestHelperConfig config;
	private final TooltipManager tooltipManager;

	protected ModelOutlineRenderer modelOutlineRenderer;
	private final ConfigManager configManager;

	@Inject
	public QuestHelperTooltipOverlay(QuestHelperPlugin questHelperPlugin, Client client, QuestHelperConfig config, TooltipManager tooltipManager, ModelOutlineRenderer modelOutlineRenderer, ConfigManager configManager)
	{
		setPriority(PRIORITY_HIGHEST);
		setLayer(OverlayLayer.ABOVE_WIDGETS);

		this.questHelperPlugin = questHelperPlugin;
		this.client = client;
		this.config = config;
		this.tooltipManager = tooltipManager;
		this.modelOutlineRenderer = modelOutlineRenderer;
		this.configManager = configManager;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		questHelperPlugin.getBackgroundHelpers().forEach(((s, questHelper) -> {
			questHelper.getCurrentStep().showWorldTooltips(panelComponent, !client.isMenuOpen());
		}));

		return super.render(graphics);
	}
}
