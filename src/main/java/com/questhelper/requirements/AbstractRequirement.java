/*
 * Copyright (c) 2020, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.requirements;

import com.questhelper.QuestHelperConfig;
import java.util.List;
import javax.annotation.Nullable;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.components.LineComponent;

public abstract class AbstractRequirement implements Requirement
{
	private String tooltip;
	private Requirement panelReplacement = null;

	protected boolean shouldCountForFilter = false;

	abstract public boolean check(Client client);

	@Override
	public boolean shouldConsiderForFilter()
	{
		return shouldCountForFilter;
	}

	abstract public String getDisplayText();

	@Nullable
	@Override
	public String getTooltip()
	{
		return tooltip;
	}

	@Override
	public void setTooltip(String tooltip)
	{
		this.tooltip = tooltip;
	}

	@Override
	public List<LineComponent> getDisplayTextWithChecks(Client client, QuestHelperConfig config)
	{
		if (getOverlayReplacement() != null && !this.check(client))
		{
			return getOverlayReplacement().getDisplayTextWithChecks(client, config);
		}
		return getOverlayDisplayText(client, config);
	}

	protected List<LineComponent> getOverlayDisplayText(Client client, QuestHelperConfig config)
	{
		return Requirement.super.getDisplayTextWithChecks(client, config);
	}

	public void appendToTooltip(String text)
	{
		StringBuilder builder = new StringBuilder();
		String currentTooltip = getTooltip();
		if (currentTooltip != null)
		{
			builder.append(currentTooltip);
			builder.append(currentTooltip.isEmpty() ? "" : "\n");
		}
		if (text != null)
		{
			builder.append(text);
		}
		this.tooltip = builder.toString();
	}

	@Override
	public Requirement getOverlayReplacement()
	{
		return panelReplacement;
	}

	@Override
	public void setOverlayReplacement(Requirement panelReplacement)
	{
		this.panelReplacement = panelReplacement;
	}
}
