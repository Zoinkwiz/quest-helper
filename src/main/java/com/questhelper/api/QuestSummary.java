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

import java.util.Objects;

/**
 * Immutable description of a quest exposed via {@link QuestHelperApi#getQuests()}.
 * <p>
 * Only primitive / JDK types are used so the DTO survives travelling across the
 * RuneLite plugin classloader boundary (e.g. when another plugin obtains the
 * Quest Helper plugin through {@code PluginManager#getPlugins()} and invokes
 * the API reflectively).
 */
public final class QuestSummary
{
	private final String name;
	private final String displayName;
	private final String questType;
	private final String difficulty;
	private final boolean developerOnly;

	public QuestSummary(String name, String displayName, String questType, String difficulty, boolean developerOnly)
	{
		this.name = Objects.requireNonNull(name, "name");
		this.displayName = Objects.requireNonNull(displayName, "displayName");
		this.questType = questType;
		this.difficulty = difficulty;
		this.developerOnly = developerOnly;
	}

	/**
	 * Stable identifier for the quest — matches {@code QuestHelperQuest.name()}
	 * and is the value accepted by {@link QuestHelperApi#openQuest(String)}.
	 */
	public String getName()
	{
		return name;
	}

	/** Human-readable quest title (e.g. "Cook's Assistant"). */
	public String getDisplayName()
	{
		return displayName;
	}

	/** Quest type bucket (e.g. "F2P", "P2P", "MINIQUEST", "SKILL_F2P"). May be null. */
	public String getQuestType()
	{
		return questType;
	}

	/** Difficulty bucket (e.g. "NOVICE", "INTERMEDIATE", "EXPERIENCED", "MASTER"). May be null. */
	public String getDifficulty()
	{
		return difficulty;
	}

	/** True if the quest is only visible when RuneLite is launched in developer mode. */
	public boolean isDeveloperOnly()
	{
		return developerOnly;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (!(o instanceof QuestSummary)) return false;
		QuestSummary that = (QuestSummary) o;
		return name.equals(that.name);
	}

	@Override
	public int hashCode()
	{
		return name.hashCode();
	}

	@Override
	public String toString()
	{
		return "QuestSummary{" + name + " -> " + displayName + "}";
	}
}
