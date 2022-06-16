/*
 *
 *  * Copyright (c) 2021
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
package com.questhelper.requirements;

import com.questhelper.QuestHelperConfig;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.components.LineComponent;

/**
 * A requirement that must be passed.
 * This is used in both rendering overlays and quest logic.<br>
 * All {@link Requirement}s are run on the {@link net.runelite.client.callback.ClientThread}.
 */
public interface Requirement
{
	/**
	 * Check the {@link Client} that it meets this requirement.
	 * @param client client to check
	 * @return true if the client meets this requirement
	 */
	boolean check(Client client);

	/**
	 * @return whether the requirement should be considered for filtering in the sidebar
	 */
	default boolean shouldConsiderForFilter()
	{
		return false;
	}

	/**
	 * @return display text to be used for rendering either on overlays or panels. Cannot be null.
	 */
	@Nonnull
	String getDisplayText();

	/**
	 * The {@link Color} used to render the {@link #getDisplayText()} depending on what {@link #check(Client)}
	 * returns.<br>
	 * By default, if {@link #check(Client)} returns true, {@link Color#GREEN} is used, otherwise {@link Color#RED}.
	 *
	 * @param client client to check
	 * @return the {@link Color} to use
	 */
	default Color getColor(Client client, QuestHelperConfig config)
	{
		return check(client) ? config.passColour() : config.failColour();
	}

	/**
	 * If this requirement will be displayed on a quest's side panels they can have tooltips to
	 * give extra information.
	 *
	 * @return the tooltip text to display
	 */
	@Nullable
	default String getTooltip()
	{
		return null;
	}

	/**
	 * Set the tooltip for this requirement
	 *
	 * @param tooltip the new tooltip
	 */
	default void setTooltip(@Nullable String tooltip) {}
	
	/**
	 * If a custom suffix has been set it will be used
	 * over the default ItemID.
	 *
	 * @return custom url
	 */
	@Nullable
	default String getUrlSuffix()
	{
		return null;
	}
	
	/**
	 * Set the suffix of the URL to the argument provided,
	 *  overrides the url set from the ItemID.
	 *
	 * @param urlSuffix the new url
	 */
	default void setUrlSuffix(@Nullable String urlSuffix) {}
	

	default List<LineComponent> getDisplayTextWithChecks(Client client, QuestHelperConfig config)
	{
		List<LineComponent> lines = new ArrayList<>();

		if (!shouldDisplayText(client)) return lines;

		String text = getDisplayText();
		Color color = getColor(client, config);

		lines.add(LineComponent.builder()
			.left(text)
			.leftColor(color)
			.build());

		return lines;
	}

	/**
	 * @return If not null, this requirement will be displayed if this requirement fails during checks
	 */
	default Requirement getOverlayReplacement()
	{
		return null;
	}

	/**
	 * @return If requirements pass, returns true
	 */
	default boolean shouldDisplayText(Client client)
	{
		return true;
	}

	/**
	 * Set the new {@link Requirement} to display if this one fails
	 *
	 * @param requirement the new requirement
	 */
	default void setOverlayReplacement(Requirement requirement) {}
}
