/*
 * Copyright (c) 2023, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.managers;

import com.questhelper.overlays.*;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class QuestOverlayManager
{
	@Inject
	private OverlayManager overlayManager;

	@Inject
	private QuestHelperOverlay questHelperOverlay;

	@Inject
	private QuestHelperWidgetOverlay questHelperWidgetOverlay;

	@Inject
	private QuestHelperMinimapOverlay questHelperMinimapOverlay;

	@Inject
	private QuestHelperWorldOverlay questHelperWorldOverlay;

	@Inject
	private QuestHelperWorldArrowOverlay questHelperWorldArrowOverlay;

	@Inject
	private QuestHelperWorldLineOverlay questHelperWorldLineOverlay;

	@Inject
	private QuestHelperTooltipOverlay questHelperTooltipOverlay;

	@Inject
	private QuestHelperDebugOverlay questHelperDebugOverlay;

	public void startUp()
	{
		overlayManager.add(questHelperOverlay);
		overlayManager.add(questHelperWorldOverlay);
		overlayManager.add(questHelperWorldArrowOverlay);
		overlayManager.add(questHelperWorldLineOverlay);
		overlayManager.add(questHelperWidgetOverlay);
		overlayManager.add(questHelperMinimapOverlay);
		overlayManager.add(questHelperTooltipOverlay);
	}

	public void shutDown()
	{
		overlayManager.remove(questHelperOverlay);
		overlayManager.remove(questHelperWorldOverlay);
		overlayManager.remove(questHelperWorldArrowOverlay);
		overlayManager.remove(questHelperWorldLineOverlay);
		overlayManager.remove(questHelperWidgetOverlay);
		overlayManager.remove(questHelperDebugOverlay);
		overlayManager.remove(questHelperMinimapOverlay);
		overlayManager.remove(questHelperTooltipOverlay);
	}

	public void addDebugOverlay()
	{
		overlayManager.add(questHelperDebugOverlay);
	}

	public void removeDebugOverlay()
	{
		overlayManager.remove(questHelperDebugOverlay);
	}
}
