/*
 * Copyright (c) 2026, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.api;

import java.util.List;

/**
 * Stable, public surface that other RuneLite plugins can use to integrate with
 * Quest Helper.
 * <p>
 * The Quest Helper plugin class itself implements this interface, so external
 * callers can either:
 * <ul>
 *   <li>Cast directly to {@code QuestHelperApi} if they share a classloader
 *       (e.g. RuneLite core plugins), or</li>
 *   <li>Invoke the methods reflectively after locating the plugin via
 *       {@code PluginManager#getPlugins()} — the method signatures are
 *       intentionally restricted to JDK and {@link QuestSummary} types so
 *       this works across plugin-hub classloader boundaries.</li>
 * </ul>
 *
 * <h2>Compatibility</h2>
 * Method signatures on this interface and {@link QuestSummary} are treated as
 * a public contract — they will not be renamed or have parameters changed
 * without a deprecation cycle.
 */
public interface QuestHelperApi
{
	/**
	 * Returns the full list of quest helpers known to the plugin, sorted by
	 * display name. Includes developer-only entries (see
	 * {@link QuestSummary#isDeveloperOnly()}) so callers can filter as they
	 * wish.
	 *
	 * @return an unmodifiable list of {@link QuestSummary}, never null.
	 */
	List<QuestSummary> getQuests();

	/**
	 * Opens the Quest Helper side panel and selects the quest with the given
	 * name. The name must match {@link QuestSummary#getName()} (i.e. the
	 * underlying {@code QuestHelperQuest} enum constant name).
	 * <p>
	 * Safe to call from any thread; the actual quest start-up is scheduled on
	 * the client thread and panel opening on the Swing EDT.
	 *
	 * @param questName the stable quest name from {@link QuestSummary#getName()}.
	 * @return {@code true} if a matching quest was found and start-up was
	 *         scheduled, {@code false} if the name was unknown.
	 */
	boolean openQuest(String questName);

	/**
	 * Opens the Quest Helper side panel without changing the selected quest.
	 * Safe to call from any thread.
	 */
	void openPanel();
}
