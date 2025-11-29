/*
 * Copyright (c) 2025, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.helpers.activities.charting;

import com.questhelper.requirements.Requirement;
import lombok.Getter;
import net.runelite.api.coords.WorldPoint;
import java.util.ArrayList;
import java.util.List;

@Getter
public final class ChartingTaskDefinition
{
	private final ChartingType type;
	private final String description;
	private final WorldPoint worldPoint;
	private final WorldPoint secondaryWorldPoint;
	private final List<Integer> itemIds;
	private final String ocean;
	private final int level;
	private final int varbitId;
	private final String answerText;
	private final List<Requirement> additionalRequirements = new ArrayList<>();
	private final List<Requirement> additionalRecommended = new ArrayList<>();
	
	public ChartingTaskDefinition(ChartingType type, String description, WorldPoint worldPoint, String ocean, int level, int varbitId)
	{
		this(type, description, worldPoint, null, ocean, level, varbitId);
	}
	
	public ChartingTaskDefinition(ChartingType type, String description, WorldPoint worldPoint, WorldPoint secondaryWorldPoint, String ocean, int level, int varbitId)
	{
		this.type = type;
		this.description = description;
		this.worldPoint = worldPoint;
		this.secondaryWorldPoint = secondaryWorldPoint;
		this.ocean = ocean;
		this.level = level;
		this.varbitId = varbitId;
		this.itemIds = null;
		this.answerText = "";
	}

	public ChartingTaskDefinition(ChartingType type, String description, WorldPoint worldPoint, String ocean, int level, int varbitId, String answerText, List<Integer> itemIds)
	{
		this.type = type;
		this.description = description;
		this.worldPoint = worldPoint;
		this.secondaryWorldPoint = null;
		this.itemIds = itemIds;
		this.ocean = ocean;
		this.level = level;
		this.varbitId = varbitId;
		this.answerText = answerText;
	}

	public ChartingTaskDefinition withRequirements(List<Requirement> requirements)
	{
		additionalRequirements.addAll(requirements);
		return this;
	}

	public ChartingTaskDefinition withRecommended(List<Requirement> recommended)
	{
		additionalRecommended.addAll(recommended);
		return this;
	}
}
