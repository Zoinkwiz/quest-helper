/*
 * Copyright (c) 2023, pajlada <https://github.com/pajlada>
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
package com.questhelper.helpers.quests.thepathofglouphrie;

import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.widget.WidgetPresenceRequirement;
import com.questhelper.steps.DetailedOwnerStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.WidgetStep;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.eventbus.Subscribe;
import org.apache.commons.lang3.tuple.Pair;


@Slf4j
public class YewnocksPuzzle extends DetailedOwnerStep
{
	/**
	 * Region ID of the storeroom this puzzle takes place in
	 */
	private static final int STOREROOM_REGION = 11074;
	private static final int PUZZLE1_INSERTED_DISC_VARP_ID = 3994;
	private static final int PUZZLE2_UPPER_INSERTED_DISC_VARP_ID = 3995;
	private static final int PUZZLE2_LOWER_INSERTED_DISC_VARP_ID = 3996;
	private static final int PUZZLE1_LEFT_VARP_ID = 3997;
	private static final int PUZZLE1_RIGHT_VARP_ID = 3998;
	private static final int PUZZLE2_VARP_ID = 3999;
	/**
	 * ItemID to ItemRequirement map
	 */
	private final HashMap<Integer, ItemRequirement> discs = new HashMap<>();
	/**
	 * Value to ItemRequirement map
	 */
	private final HashMap<Integer, ItemRequirement> valueToRequirement = new HashMap<>();
	/**
	 * ItemID to Value map
	 */
	private final HashMap<Integer, Integer> discToValue = new HashMap<>();
	/**
	 * Value to list of possible requirements using exactly 2 different
	 */
	private final HashMap<Integer, List<ItemRequirements>> valueToDoubleDiscRequirement = new HashMap<>();
	private final HashMap<Integer, List<ItemRequirement>> valuePossibleSingleDiscExchangesRequirements = new HashMap<>();
	private final Solution solution = new Solution();
	private int puzzle1LeftItemID = -1;
	private int puzzle1RightItemID = -1;
	private int puzzle2ItemID = -1;
	private ObjectStep getMoreDiscs;
	private ObjectStep clickMachine;
	private ObjectStep clickMachineOnce;
	private ObjectStep useExchanger;
	private ItemStep machineInsertDisc;
	private WidgetStep machineReset;
	private WidgetStep machineSubmit;
	private ItemStep exchangerInsertDisc;
	private WidgetStep exchangerExchange;
	private DetailedQuestStep exchangerConfirm;
	private WidgetStep exchangerReset;

	private WidgetPresenceRequirement machineOpen;
	private WidgetPresenceRequirement exchangerOpen;

	public YewnocksPuzzle(ThePathOfGlouphrie pog)
	{
		super(pog, "Operate Yewnock's machine & solve the puzzle. All items left on the ground are lost.");

		loadDiscs(discs);
		loadValueToRequirement(discs, valueToRequirement);
		loadDiscToValue(discToValue);
		loadValueToDoubleDiscRequirement(valueToRequirement, valueToDoubleDiscRequirement);
		loadValuePossibleExchanges(discs, discToValue, valueToRequirement, valuePossibleSingleDiscExchangesRequirements);
	}

	static public void loadValuePossibleExchanges(final HashMap<Integer, ItemRequirement> discs, final HashMap<Integer, Integer> discToValue, final HashMap<Integer, ItemRequirement> valueToRequirement,
												  HashMap<Integer, List<ItemRequirement>> valuePossibleSingleDiscExchangesRequirements)
	{
		// List of exchanges that an input disc can make
		var validExchangesForDisc = new HashMap<Integer, HashSet<List<Integer>>>();

		validExchangesForDisc.put(ItemID.RED_PENTAGON, new HashSet<>(List.of(List.of(ItemID.GREEN_CIRCLE, ItemID.RED_CIRCLE), List.of(ItemID.RED_SQUARE, ItemID.RED_CIRCLE), List.of(ItemID.BLUE_CIRCLE), List.of(ItemID.RED_PENTAGON))));
		validExchangesForDisc.put(ItemID.ORANGE_CIRCLE, new HashSet<>(List.of(List.of(ItemID.ORANGE_CIRCLE))));
		validExchangesForDisc.put(ItemID.ORANGE_TRIANGLE, new HashSet<>(List.of(List.of(ItemID.INDIGO_CIRCLE), List.of(ItemID.ORANGE_TRIANGLE), List.of(ItemID.BLUE_CIRCLE, ItemID.RED_CIRCLE), List.of(ItemID.RED_SQUARE, ItemID.ORANGE_CIRCLE), List.of(ItemID.RED_PENTAGON, ItemID.RED_CIRCLE), List.of(ItemID.GREEN_CIRCLE, ItemID.ORANGE_CIRCLE))));
		validExchangesForDisc.put(ItemID.ORANGE_SQUARE, new HashSet<>(List.of(List.of(ItemID.ORANGE_SQUARE), List.of(ItemID.ORANGE_TRIANGLE, ItemID.ORANGE_CIRCLE), List.of(ItemID.INDIGO_CIRCLE, ItemID.ORANGE_CIRCLE), List.of(ItemID.BLUE_CIRCLE, ItemID.RED_TRIANGLE), List.of(ItemID.RED_PENTAGON, ItemID.RED_TRIANGLE), List.of(ItemID.VIOLET_CIRCLE, ItemID.RED_CIRCLE))));
		validExchangesForDisc.put(ItemID.ORANGE_PENTAGON, new HashSet<>(List.of(List.of(ItemID.ORANGE_PENTAGON), List.of(ItemID.ORANGE_SQUARE, ItemID.ORANGE_CIRCLE), List.of(ItemID.YELLOW_TRIANGLE, ItemID.RED_CIRCLE), List.of(ItemID.ORANGE_TRIANGLE, ItemID.GREEN_CIRCLE), List.of(ItemID.INDIGO_CIRCLE, ItemID.GREEN_CIRCLE), List.of(ItemID.VIOLET_CIRCLE, ItemID.RED_TRIANGLE))));
		validExchangesForDisc.put(ItemID.YELLOW_TRIANGLE, new HashSet<>(List.of(List.of(ItemID.YELLOW_TRIANGLE), List.of(ItemID.VIOLET_CIRCLE, ItemID.ORANGE_CIRCLE), List.of(ItemID.ORANGE_TRIANGLE, ItemID.RED_TRIANGLE), List.of(ItemID.INDIGO_CIRCLE, ItemID.RED_TRIANGLE), List.of(ItemID.ORANGE_SQUARE, ItemID.RED_CIRCLE))));
		validExchangesForDisc.put(ItemID.YELLOW_SQUARE, new HashSet<>(List.of(List.of(ItemID.VIOLET_CIRCLE, ItemID.RED_SQUARE, ItemID.RED_CIRCLE), List.of(ItemID.ORANGE_SQUARE, ItemID.GREEN_CIRCLE), List.of(ItemID.YELLOW_SQUARE), List.of(ItemID.VIOLET_CIRCLE, ItemID.RED_PENTAGON), List.of(ItemID.GREEN_TRIANGLE), List.of(ItemID.ORANGE_PENTAGON, ItemID.ORANGE_CIRCLE), List.of(ItemID.VIOLET_CIRCLE, ItemID.GREEN_CIRCLE, ItemID.RED_CIRCLE), List.of(ItemID.VIOLET_CIRCLE, ItemID.BLUE_CIRCLE), List.of(ItemID.YELLOW_TRIANGLE, ItemID.RED_TRIANGLE))));
		validExchangesForDisc.put(ItemID.YELLOW_PENTAGON, new HashSet<>(List.of(List.of(ItemID.ORANGE_PENTAGON, ItemID.GREEN_CIRCLE, ItemID.RED_CIRCLE), List.of(ItemID.YELLOW_TRIANGLE, ItemID.BLUE_CIRCLE, ItemID.RED_CIRCLE), List.of(ItemID.YELLOW_PENTAGON), List.of(ItemID.YELLOW_TRIANGLE, ItemID.INDIGO_CIRCLE), List.of(ItemID.BLUE_TRIANGLE), List.of(ItemID.ORANGE_PENTAGON, ItemID.BLUE_CIRCLE), List.of(ItemID.YELLOW_TRIANGLE, ItemID.GREEN_CIRCLE, ItemID.ORANGE_CIRCLE), List.of(ItemID.YELLOW_TRIANGLE, ItemID.RED_PENTAGON, ItemID.RED_CIRCLE), List.of(ItemID.ORANGE_PENTAGON, ItemID.RED_SQUARE, ItemID.RED_CIRCLE), List.of(ItemID.GREEN_TRIANGLE, ItemID.RED_TRIANGLE), List.of(ItemID.YELLOW_TRIANGLE, ItemID.RED_SQUARE, ItemID.ORANGE_CIRCLE), List.of(ItemID.ORANGE_PENTAGON, ItemID.RED_PENTAGON), List.of(ItemID.YELLOW_TRIANGLE, ItemID.ORANGE_TRIANGLE), List.of(ItemID.YELLOW_SQUARE, ItemID.RED_TRIANGLE))));
		validExchangesForDisc.put(ItemID.GREEN_CIRCLE, new HashSet<>(List.of(List.of(ItemID.GREEN_CIRCLE))));
		validExchangesForDisc.put(ItemID.GREEN_TRIANGLE, new HashSet<>(List.of(List.of(ItemID.VIOLET_CIRCLE, ItemID.RED_SQUARE, ItemID.RED_CIRCLE), List.of(ItemID.ORANGE_SQUARE, ItemID.GREEN_CIRCLE), List.of(ItemID.YELLOW_SQUARE), List.of(ItemID.VIOLET_CIRCLE, ItemID.RED_PENTAGON), List.of(ItemID.GREEN_TRIANGLE), List.of(ItemID.ORANGE_PENTAGON, ItemID.ORANGE_CIRCLE), List.of(ItemID.VIOLET_CIRCLE, ItemID.GREEN_CIRCLE, ItemID.RED_CIRCLE), List.of(ItemID.YELLOW_TRIANGLE, ItemID.RED_TRIANGLE), List.of(ItemID.VIOLET_CIRCLE, ItemID.BLUE_CIRCLE))));
		validExchangesForDisc.put(ItemID.GREEN_SQUARE, new HashSet<>(List.of(List.of(ItemID.ORANGE_PENTAGON, ItemID.BLUE_CIRCLE, ItemID.RED_CIRCLE), List.of(ItemID.YELLOW_TRIANGLE, ItemID.INDIGO_CIRCLE, ItemID.RED_CIRCLE), List.of(ItemID.GREEN_TRIANGLE, ItemID.GREEN_CIRCLE), List.of(ItemID.YELLOW_SQUARE, ItemID.GREEN_CIRCLE), List.of(ItemID.ORANGE_PENTAGON, ItemID.INDIGO_CIRCLE), List.of(ItemID.GREEN_SQUARE), List.of(ItemID.ORANGE_PENTAGON, ItemID.GREEN_CIRCLE, ItemID.ORANGE_CIRCLE), List.of(ItemID.YELLOW_TRIANGLE, ItemID.BLUE_CIRCLE, ItemID.ORANGE_CIRCLE), List.of(ItemID.ORANGE_PENTAGON, ItemID.RED_PENTAGON, ItemID.RED_CIRCLE), List.of(ItemID.YELLOW_PENTAGON, ItemID.RED_CIRCLE), List.of(ItemID.YELLOW_TRIANGLE, ItemID.ORANGE_TRIANGLE, ItemID.RED_CIRCLE), List.of(ItemID.BLUE_TRIANGLE, ItemID.RED_CIRCLE), List.of(ItemID.YELLOW_TRIANGLE, ItemID.VIOLET_CIRCLE), List.of(ItemID.YELLOW_TRIANGLE, ItemID.RED_PENTAGON, ItemID.ORANGE_CIRCLE), List.of(ItemID.ORANGE_PENTAGON, ItemID.RED_SQUARE, ItemID.ORANGE_CIRCLE), List.of(ItemID.ORANGE_PENTAGON, ItemID.ORANGE_TRIANGLE))));
		validExchangesForDisc.put(ItemID.GREEN_PENTAGON, new HashSet<>(List.of(List.of(ItemID.YELLOW_PENTAGON, ItemID.BLUE_CIRCLE), List.of(ItemID.GREEN_TRIANGLE, ItemID.VIOLET_CIRCLE, ItemID.RED_CIRCLE), List.of(ItemID.BLUE_TRIANGLE, ItemID.BLUE_CIRCLE), List.of(ItemID.YELLOW_SQUARE, ItemID.INDIGO_CIRCLE, ItemID.ORANGE_CIRCLE), List.of(ItemID.YELLOW_SQUARE, ItemID.BLUE_CIRCLE, ItemID.RED_TRIANGLE), List.of(ItemID.GREEN_TRIANGLE, ItemID.BLUE_CIRCLE, ItemID.RED_TRIANGLE), List.of(ItemID.YELLOW_PENTAGON, ItemID.GREEN_CIRCLE, ItemID.RED_CIRCLE), List.of(ItemID.GREEN_PENTAGON), List.of(ItemID.GREEN_TRIANGLE, ItemID.INDIGO_CIRCLE, ItemID.ORANGE_CIRCLE), List.of(ItemID.BLUE_TRIANGLE, ItemID.GREEN_CIRCLE, ItemID.RED_CIRCLE), List.of(ItemID.INDIGO_TRIANGLE, ItemID.ORANGE_CIRCLE), List.of(ItemID.GREEN_TRIANGLE, ItemID.RED_PENTAGON, ItemID.RED_TRIANGLE), List.of(ItemID.YELLOW_SQUARE, ItemID.RED_PENTAGON, ItemID.RED_TRIANGLE), List.of(ItemID.YELLOW_PENTAGON, ItemID.RED_SQUARE, ItemID.RED_CIRCLE), List.of(ItemID.BLUE_TRIANGLE, ItemID.RED_PENTAGON), List.of(ItemID.YELLOW_SQUARE, ItemID.ORANGE_TRIANGLE, ItemID.ORANGE_CIRCLE), List.of(ItemID.YELLOW_SQUARE, ItemID.ORANGE_SQUARE), List.of(ItemID.BLUE_TRIANGLE, ItemID.RED_SQUARE, ItemID.RED_CIRCLE), List.of(ItemID.GREEN_TRIANGLE, ItemID.ORANGE_TRIANGLE, ItemID.ORANGE_CIRCLE), List.of(ItemID.GREEN_SQUARE, ItemID.GREEN_CIRCLE), List.of(ItemID.YELLOW_SQUARE, ItemID.VIOLET_CIRCLE, ItemID.RED_CIRCLE), List.of(ItemID.YELLOW_PENTAGON, ItemID.RED_PENTAGON), List.of(ItemID.GREEN_TRIANGLE, ItemID.ORANGE_SQUARE))));
		validExchangesForDisc.put(ItemID.BLUE_CIRCLE, new HashSet<>(List.of(List.of(ItemID.GREEN_CIRCLE, ItemID.RED_CIRCLE), List.of(ItemID.RED_SQUARE, ItemID.RED_CIRCLE), List.of(ItemID.BLUE_CIRCLE), List.of(ItemID.RED_PENTAGON))));
		validExchangesForDisc.put(ItemID.BLUE_TRIANGLE, new HashSet<>(List.of(List.of(ItemID.ORANGE_PENTAGON, ItemID.GREEN_CIRCLE, ItemID.RED_CIRCLE), List.of(ItemID.YELLOW_PENTAGON), List.of(ItemID.YELLOW_TRIANGLE, ItemID.BLUE_CIRCLE, ItemID.RED_CIRCLE), List.of(ItemID.YELLOW_TRIANGLE, ItemID.INDIGO_CIRCLE), List.of(ItemID.BLUE_TRIANGLE), List.of(ItemID.ORANGE_PENTAGON, ItemID.BLUE_CIRCLE), List.of(ItemID.YELLOW_TRIANGLE, ItemID.GREEN_CIRCLE, ItemID.ORANGE_CIRCLE), List.of(ItemID.ORANGE_PENTAGON, ItemID.RED_SQUARE, ItemID.RED_CIRCLE), List.of(ItemID.YELLOW_TRIANGLE, ItemID.RED_PENTAGON, ItemID.RED_CIRCLE), List.of(ItemID.GREEN_TRIANGLE, ItemID.RED_TRIANGLE), List.of(ItemID.YELLOW_TRIANGLE, ItemID.RED_SQUARE, ItemID.ORANGE_CIRCLE), List.of(ItemID.YELLOW_TRIANGLE, ItemID.ORANGE_TRIANGLE), List.of(ItemID.ORANGE_PENTAGON, ItemID.RED_PENTAGON), List.of(ItemID.YELLOW_SQUARE, ItemID.RED_TRIANGLE))));
		validExchangesForDisc.put(ItemID.BLUE_SQUARE, new HashSet<>(List.of(List.of(ItemID.YELLOW_PENTAGON, ItemID.BLUE_CIRCLE), List.of(ItemID.GREEN_TRIANGLE, ItemID.VIOLET_CIRCLE, ItemID.RED_CIRCLE), List.of(ItemID.YELLOW_SQUARE, ItemID.INDIGO_CIRCLE, ItemID.ORANGE_CIRCLE), List.of(ItemID.BLUE_TRIANGLE, ItemID.BLUE_CIRCLE), List.of(ItemID.YELLOW_SQUARE, ItemID.BLUE_CIRCLE, ItemID.RED_TRIANGLE), List.of(ItemID.GREEN_TRIANGLE, ItemID.BLUE_CIRCLE, ItemID.RED_TRIANGLE), List.of(ItemID.YELLOW_PENTAGON, ItemID.GREEN_CIRCLE, ItemID.RED_CIRCLE), List.of(ItemID.GREEN_PENTAGON), List.of(ItemID.GREEN_TRIANGLE, ItemID.INDIGO_CIRCLE, ItemID.ORANGE_CIRCLE), List.of(ItemID.BLUE_TRIANGLE, ItemID.GREEN_CIRCLE, ItemID.RED_CIRCLE), List.of(ItemID.INDIGO_TRIANGLE, ItemID.ORANGE_CIRCLE), List.of(ItemID.GREEN_TRIANGLE, ItemID.RED_PENTAGON, ItemID.RED_TRIANGLE), List.of(ItemID.YELLOW_SQUARE, ItemID.RED_PENTAGON, ItemID.RED_TRIANGLE), List.of(ItemID.BLUE_TRIANGLE, ItemID.RED_PENTAGON), List.of(ItemID.YELLOW_PENTAGON, ItemID.RED_SQUARE, ItemID.RED_CIRCLE), List.of(ItemID.YELLOW_SQUARE, ItemID.ORANGE_TRIANGLE, ItemID.ORANGE_CIRCLE), List.of(ItemID.YELLOW_SQUARE, ItemID.ORANGE_SQUARE), List.of(ItemID.BLUE_TRIANGLE, ItemID.RED_SQUARE, ItemID.RED_CIRCLE), List.of(ItemID.GREEN_TRIANGLE, ItemID.ORANGE_TRIANGLE, ItemID.ORANGE_CIRCLE), List.of(ItemID.GREEN_SQUARE, ItemID.GREEN_CIRCLE), List.of(ItemID.YELLOW_SQUARE, ItemID.VIOLET_CIRCLE, ItemID.RED_CIRCLE), List.of(ItemID.YELLOW_PENTAGON, ItemID.RED_PENTAGON), List.of(ItemID.GREEN_TRIANGLE, ItemID.ORANGE_SQUARE))));
		validExchangesForDisc.put(ItemID.BLUE_PENTAGON, new HashSet<>(List.of(List.of(ItemID.INDIGO_TRIANGLE, ItemID.VIOLET_CIRCLE), List.of(ItemID.GREEN_PENTAGON, ItemID.BLUE_CIRCLE), List.of(ItemID.YELLOW_PENTAGON, ItemID.YELLOW_TRIANGLE, ItemID.RED_CIRCLE), List.of(ItemID.INDIGO_SQUARE, ItemID.RED_CIRCLE), List.of(ItemID.GREEN_SQUARE, ItemID.ORANGE_SQUARE, ItemID.RED_CIRCLE), List.of(ItemID.YELLOW_PENTAGON, ItemID.ORANGE_SQUARE, ItemID.ORANGE_CIRCLE), List.of(ItemID.INDIGO_TRIANGLE, ItemID.BLUE_CIRCLE, ItemID.ORANGE_CIRCLE), List.of(ItemID.BLUE_TRIANGLE, ItemID.INDIGO_CIRCLE, ItemID.GREEN_CIRCLE), List.of(ItemID.GREEN_SQUARE, ItemID.YELLOW_TRIANGLE), List.of(ItemID.BLUE_TRIANGLE, ItemID.ORANGE_SQUARE, ItemID.ORANGE_CIRCLE), List.of(ItemID.GREEN_PENTAGON, ItemID.RED_SQUARE, ItemID.RED_CIRCLE), List.of(ItemID.INDIGO_TRIANGLE, ItemID.RED_PENTAGON, ItemID.ORANGE_CIRCLE), List.of(ItemID.YELLOW_PENTAGON, ItemID.VIOLET_CIRCLE, ItemID.RED_TRIANGLE), List.of(ItemID.GREEN_SQUARE, ItemID.VIOLET_CIRCLE, ItemID.ORANGE_CIRCLE), List.of(ItemID.BLUE_TRIANGLE, ItemID.VIOLET_CIRCLE, ItemID.RED_TRIANGLE), List.of(ItemID.GREEN_SQUARE, ItemID.INDIGO_CIRCLE, ItemID.RED_TRIANGLE), List.of(ItemID.BLUE_TRIANGLE, ItemID.ORANGE_TRIANGLE, ItemID.GREEN_CIRCLE), List.of(ItemID.GREEN_PENTAGON, ItemID.GREEN_CIRCLE, ItemID.RED_CIRCLE), List.of(ItemID.YELLOW_PENTAGON, ItemID.INDIGO_CIRCLE, ItemID.GREEN_CIRCLE), List.of(ItemID.BLUE_PENTAGON), List.of(ItemID.INDIGO_TRIANGLE, ItemID.INDIGO_CIRCLE, ItemID.RED_CIRCLE), List.of(ItemID.VIOLET_TRIANGLE, ItemID.GREEN_CIRCLE), List.of(ItemID.GREEN_PENTAGON, ItemID.RED_PENTAGON), List.of(ItemID.BLUE_TRIANGLE, ItemID.ORANGE_PENTAGON), List.of(ItemID.GREEN_SQUARE, ItemID.ORANGE_TRIANGLE, ItemID.RED_TRIANGLE), List.of(ItemID.BLUE_TRIANGLE, ItemID.YELLOW_TRIANGLE, ItemID.RED_CIRCLE), List.of(ItemID.YELLOW_PENTAGON, ItemID.ORANGE_PENTAGON), List.of(ItemID.YELLOW_PENTAGON, ItemID.ORANGE_TRIANGLE, ItemID.GREEN_CIRCLE), List.of(ItemID.INDIGO_TRIANGLE, ItemID.ORANGE_TRIANGLE, ItemID.RED_CIRCLE))));
		validExchangesForDisc.put(ItemID.INDIGO_CIRCLE, new HashSet<>(List.of(List.of(ItemID.INDIGO_CIRCLE), List.of(ItemID.ORANGE_TRIANGLE), List.of(ItemID.BLUE_CIRCLE, ItemID.RED_CIRCLE), List.of(ItemID.RED_SQUARE, ItemID.ORANGE_CIRCLE), List.of(ItemID.RED_PENTAGON, ItemID.RED_CIRCLE), List.of(ItemID.GREEN_CIRCLE, ItemID.ORANGE_CIRCLE))));
		validExchangesForDisc.put(ItemID.INDIGO_TRIANGLE, new HashSet<>(List.of(List.of(ItemID.ORANGE_PENTAGON, ItemID.INDIGO_CIRCLE, ItemID.ORANGE_CIRCLE), List.of(ItemID.ORANGE_PENTAGON, ItemID.ORANGE_SQUARE), List.of(ItemID.YELLOW_SQUARE, ItemID.BLUE_CIRCLE, ItemID.RED_CIRCLE), List.of(ItemID.ORANGE_PENTAGON, ItemID.BLUE_CIRCLE, ItemID.RED_TRIANGLE), List.of(ItemID.GREEN_TRIANGLE, ItemID.BLUE_CIRCLE, ItemID.RED_CIRCLE), List.of(ItemID.GREEN_TRIANGLE, ItemID.INDIGO_CIRCLE), List.of(ItemID.YELLOW_SQUARE, ItemID.GREEN_CIRCLE, ItemID.ORANGE_CIRCLE), List.of(ItemID.YELLOW_SQUARE, ItemID.INDIGO_CIRCLE), List.of(ItemID.ORANGE_PENTAGON, ItemID.RED_PENTAGON, ItemID.RED_TRIANGLE), List.of(ItemID.INDIGO_TRIANGLE), List.of(ItemID.YELLOW_SQUARE, ItemID.RED_PENTAGON, ItemID.RED_CIRCLE), List.of(ItemID.GREEN_SQUARE, ItemID.ORANGE_CIRCLE), List.of(ItemID.YELLOW_PENTAGON, ItemID.RED_TRIANGLE), List.of(ItemID.GREEN_TRIANGLE, ItemID.GREEN_CIRCLE, ItemID.ORANGE_CIRCLE), List.of(ItemID.ORANGE_PENTAGON, ItemID.ORANGE_TRIANGLE, ItemID.ORANGE_CIRCLE), List.of(ItemID.GREEN_TRIANGLE, ItemID.RED_PENTAGON, ItemID.RED_CIRCLE), List.of(ItemID.BLUE_TRIANGLE, ItemID.RED_TRIANGLE), List.of(ItemID.YELLOW_SQUARE, ItemID.ORANGE_TRIANGLE), List.of(ItemID.GREEN_TRIANGLE, ItemID.RED_SQUARE, ItemID.ORANGE_CIRCLE), List.of(ItemID.ORANGE_PENTAGON, ItemID.VIOLET_CIRCLE, ItemID.RED_CIRCLE), List.of(ItemID.YELLOW_SQUARE, ItemID.RED_SQUARE, ItemID.ORANGE_CIRCLE), List.of(ItemID.GREEN_TRIANGLE, ItemID.ORANGE_TRIANGLE))));
		validExchangesForDisc.put(ItemID.INDIGO_SQUARE, new HashSet<>(List.of(List.of(ItemID.INDIGO_TRIANGLE, ItemID.RED_SQUARE, ItemID.ORANGE_CIRCLE), List.of(ItemID.INDIGO_TRIANGLE, ItemID.INDIGO_CIRCLE), List.of(ItemID.INDIGO_TRIANGLE, ItemID.BLUE_CIRCLE, ItemID.RED_CIRCLE), List.of(ItemID.YELLOW_PENTAGON, ItemID.ORANGE_SQUARE, ItemID.RED_CIRCLE), List.of(ItemID.BLUE_TRIANGLE, ItemID.ORANGE_SQUARE, ItemID.RED_CIRCLE), List.of(ItemID.INDIGO_TRIANGLE, ItemID.GREEN_CIRCLE, ItemID.ORANGE_CIRCLE), List.of(ItemID.GREEN_SQUARE, ItemID.ORANGE_SQUARE), List.of(ItemID.GREEN_SQUARE, ItemID.VIOLET_CIRCLE, ItemID.RED_CIRCLE), List.of(ItemID.YELLOW_PENTAGON, ItemID.VIOLET_CIRCLE, ItemID.ORANGE_CIRCLE), List.of(ItemID.YELLOW_PENTAGON, ItemID.INDIGO_CIRCLE, ItemID.RED_TRIANGLE), List.of(ItemID.GREEN_SQUARE, ItemID.INDIGO_CIRCLE, ItemID.ORANGE_CIRCLE), List.of(ItemID.BLUE_TRIANGLE, ItemID.INDIGO_CIRCLE, ItemID.RED_TRIANGLE), List.of(ItemID.GREEN_SQUARE, ItemID.BLUE_CIRCLE, ItemID.RED_TRIANGLE), List.of(ItemID.BLUE_TRIANGLE, ItemID.VIOLET_CIRCLE, ItemID.ORANGE_CIRCLE), List.of(ItemID.VIOLET_TRIANGLE, ItemID.RED_TRIANGLE), List.of(ItemID.YELLOW_PENTAGON, ItemID.ORANGE_TRIANGLE, ItemID.RED_TRIANGLE), List.of(ItemID.INDIGO_SQUARE), List.of(ItemID.INDIGO_TRIANGLE, ItemID.ORANGE_TRIANGLE), List.of(ItemID.GREEN_SQUARE, ItemID.RED_PENTAGON, ItemID.RED_TRIANGLE), List.of(ItemID.GREEN_PENTAGON, ItemID.GREEN_CIRCLE), List.of(ItemID.GREEN_SQUARE, ItemID.ORANGE_TRIANGLE, ItemID.ORANGE_CIRCLE), List.of(ItemID.YELLOW_PENTAGON, ItemID.YELLOW_TRIANGLE), List.of(ItemID.BLUE_TRIANGLE, ItemID.ORANGE_TRIANGLE, ItemID.RED_TRIANGLE), List.of(ItemID.INDIGO_TRIANGLE, ItemID.RED_PENTAGON, ItemID.RED_CIRCLE), List.of(ItemID.BLUE_TRIANGLE, ItemID.YELLOW_TRIANGLE))));
		validExchangesForDisc.put(ItemID.INDIGO_PENTAGON, new HashSet<>(List.of(List.of(ItemID.GREEN_SQUARE, ItemID.YELLOW_TRIANGLE, ItemID.GREEN_CIRCLE, ItemID.RED_CIRCLE), List.of(ItemID.GREEN_SQUARE, ItemID.ORANGE_SQUARE, ItemID.GREEN_CIRCLE, ItemID.ORANGE_CIRCLE), List.of(ItemID.INDIGO_TRIANGLE, ItemID.ORANGE_SQUARE, ItemID.GREEN_CIRCLE), List.of(ItemID.INDIGO_SQUARE, ItemID.RED_SQUARE, ItemID.ORANGE_CIRCLE), List.of(ItemID.GREEN_SQUARE, ItemID.YELLOW_TRIANGLE, ItemID.RED_SQUARE, ItemID.RED_CIRCLE), List.of(ItemID.GREEN_SQUARE, ItemID.ORANGE_SQUARE, ItemID.INDIGO_CIRCLE), List.of(ItemID.GREEN_SQUARE, ItemID.ORANGE_SQUARE, ItemID.RED_SQUARE, ItemID.ORANGE_CIRCLE), List.of(ItemID.VIOLET_SQUARE, ItemID.ORANGE_CIRCLE), List.of(ItemID.VIOLET_TRIANGLE, ItemID.INDIGO_CIRCLE, ItemID.RED_TRIANGLE), List.of(ItemID.INDIGO_SQUARE, ItemID.BLUE_CIRCLE, ItemID.RED_CIRCLE), List.of(ItemID.BLUE_PENTAGON, ItemID.GREEN_CIRCLE, ItemID.RED_CIRCLE), List.of(ItemID.INDIGO_TRIANGLE, ItemID.VIOLET_CIRCLE, ItemID.RED_SQUARE, ItemID.RED_CIRCLE), List.of(ItemID.GREEN_PENTAGON, ItemID.INDIGO_CIRCLE, ItemID.GREEN_CIRCLE), List.of(ItemID.VIOLET_TRIANGLE, ItemID.VIOLET_CIRCLE, ItemID.ORANGE_CIRCLE), List.of(ItemID.INDIGO_SQUARE, ItemID.ORANGE_TRIANGLE), List.of(ItemID.GREEN_SQUARE, ItemID.YELLOW_SQUARE, ItemID.ORANGE_CIRCLE), List.of(ItemID.INDIGO_SQUARE, ItemID.GREEN_CIRCLE, ItemID.ORANGE_CIRCLE), List.of(ItemID.GREEN_SQUARE, ItemID.ORANGE_SQUARE, ItemID.BLUE_CIRCLE, ItemID.RED_CIRCLE), List.of(ItemID.GREEN_PENTAGON, ItemID.ORANGE_SQUARE, ItemID.ORANGE_CIRCLE), List.of(ItemID.GREEN_SQUARE, ItemID.ORANGE_SQUARE, ItemID.ORANGE_TRIANGLE), List.of(ItemID.INDIGO_SQUARE, ItemID.INDIGO_CIRCLE), List.of(ItemID.INDIGO_TRIANGLE, ItemID.VIOLET_CIRCLE, ItemID.BLUE_CIRCLE), List.of(ItemID.VIOLET_TRIANGLE, ItemID.ORANGE_SQUARE, ItemID.RED_CIRCLE), List.of(ItemID.BLUE_PENTAGON, ItemID.RED_SQUARE, ItemID.RED_CIRCLE), List.of(ItemID.GREEN_PENTAGON, ItemID.ORANGE_TRIANGLE, ItemID.GREEN_CIRCLE), List.of(ItemID.VIOLET_TRIANGLE, ItemID.ORANGE_TRIANGLE, ItemID.RED_TRIANGLE), List.of(ItemID.GREEN_SQUARE, ItemID.YELLOW_TRIANGLE, ItemID.BLUE_CIRCLE), List.of(ItemID.GREEN_PENTAGON, ItemID.VIOLET_CIRCLE, ItemID.RED_TRIANGLE), List.of(ItemID.GREEN_SQUARE, ItemID.ORANGE_SQUARE, ItemID.RED_PENTAGON, ItemID.RED_CIRCLE), List.of(ItemID.INDIGO_TRIANGLE, ItemID.VIOLET_CIRCLE, ItemID.GREEN_CIRCLE, ItemID.RED_CIRCLE), List.of(ItemID.INDIGO_TRIANGLE, ItemID.VIOLET_CIRCLE, ItemID.RED_PENTAGON), List.of(ItemID.GREEN_PENTAGON, ItemID.YELLOW_TRIANGLE, ItemID.RED_CIRCLE), List.of(ItemID.INDIGO_TRIANGLE, ItemID.YELLOW_SQUARE), List.of(ItemID.INDIGO_PENTAGON), List.of(ItemID.GREEN_SQUARE, ItemID.GREEN_TRIANGLE, ItemID.ORANGE_CIRCLE), List.of(ItemID.BLUE_PENTAGON, ItemID.RED_PENTAGON), List.of(ItemID.VIOLET_TRIANGLE, ItemID.YELLOW_TRIANGLE), List.of(ItemID.GREEN_SQUARE, ItemID.YELLOW_TRIANGLE, ItemID.RED_PENTAGON), List.of(ItemID.BLUE_PENTAGON, ItemID.BLUE_CIRCLE), List.of(ItemID.INDIGO_TRIANGLE, ItemID.YELLOW_TRIANGLE, ItemID.RED_TRIANGLE), List.of(ItemID.INDIGO_TRIANGLE, ItemID.ORANGE_PENTAGON, ItemID.ORANGE_CIRCLE), List.of(ItemID.GREEN_PENTAGON, ItemID.ORANGE_PENTAGON), List.of(ItemID.GREEN_SQUARE, ItemID.ORANGE_PENTAGON, ItemID.GREEN_CIRCLE), List.of(ItemID.INDIGO_TRIANGLE, ItemID.GREEN_TRIANGLE), List.of(ItemID.INDIGO_SQUARE, ItemID.RED_PENTAGON, ItemID.RED_CIRCLE))));
		validExchangesForDisc.put(ItemID.VIOLET_CIRCLE, new HashSet<>(List.of(List.of(ItemID.BLUE_CIRCLE, ItemID.ORANGE_CIRCLE), List.of(ItemID.VIOLET_CIRCLE), List.of(ItemID.RED_PENTAGON, ItemID.ORANGE_CIRCLE), List.of(ItemID.ORANGE_TRIANGLE, ItemID.RED_CIRCLE), List.of(ItemID.INDIGO_CIRCLE, ItemID.RED_CIRCLE))));
		validExchangesForDisc.put(ItemID.VIOLET_TRIANGLE, new HashSet<>(List.of(List.of(ItemID.BLUE_TRIANGLE, ItemID.INDIGO_CIRCLE), List.of(ItemID.YELLOW_PENTAGON, ItemID.INDIGO_CIRCLE), List.of(ItemID.GREEN_TRIANGLE, ItemID.ORANGE_SQUARE, ItemID.RED_CIRCLE), List.of(ItemID.YELLOW_SQUARE, ItemID.ORANGE_SQUARE, ItemID.RED_CIRCLE), List.of(ItemID.GREEN_SQUARE, ItemID.RED_PENTAGON), List.of(ItemID.BLUE_TRIANGLE, ItemID.GREEN_CIRCLE, ItemID.ORANGE_CIRCLE), List.of(ItemID.GREEN_SQUARE, ItemID.RED_SQUARE, ItemID.RED_CIRCLE), List.of(ItemID.YELLOW_PENTAGON, ItemID.RED_SQUARE, ItemID.ORANGE_CIRCLE), List.of(ItemID.BLUE_TRIANGLE, ItemID.RED_SQUARE, ItemID.ORANGE_CIRCLE), List.of(ItemID.YELLOW_SQUARE, ItemID.VIOLET_CIRCLE, ItemID.ORANGE_CIRCLE), List.of(ItemID.YELLOW_SQUARE, ItemID.INDIGO_CIRCLE, ItemID.RED_TRIANGLE), List.of(ItemID.GREEN_TRIANGLE, ItemID.INDIGO_CIRCLE, ItemID.RED_TRIANGLE), List.of(ItemID.YELLOW_SQUARE, ItemID.YELLOW_TRIANGLE), List.of(ItemID.YELLOW_PENTAGON, ItemID.BLUE_CIRCLE, ItemID.RED_CIRCLE), List.of(ItemID.GREEN_SQUARE, ItemID.BLUE_CIRCLE), List.of(ItemID.GREEN_TRIANGLE, ItemID.VIOLET_CIRCLE, ItemID.ORANGE_CIRCLE), List.of(ItemID.INDIGO_TRIANGLE, ItemID.RED_TRIANGLE), List.of(ItemID.GREEN_SQUARE, ItemID.GREEN_CIRCLE, ItemID.RED_CIRCLE), List.of(ItemID.YELLOW_PENTAGON, ItemID.GREEN_CIRCLE, ItemID.ORANGE_CIRCLE), List.of(ItemID.BLUE_TRIANGLE, ItemID.BLUE_CIRCLE, ItemID.RED_CIRCLE), List.of(ItemID.YELLOW_SQUARE, ItemID.ORANGE_TRIANGLE, ItemID.RED_TRIANGLE), List.of(ItemID.BLUE_TRIANGLE, ItemID.ORANGE_TRIANGLE), List.of(ItemID.VIOLET_TRIANGLE), List.of(ItemID.GREEN_PENTAGON, ItemID.RED_CIRCLE), List.of(ItemID.YELLOW_PENTAGON, ItemID.RED_PENTAGON, ItemID.RED_CIRCLE), List.of(ItemID.GREEN_TRIANGLE, ItemID.ORANGE_TRIANGLE, ItemID.RED_TRIANGLE), List.of(ItemID.BLUE_TRIANGLE, ItemID.RED_PENTAGON, ItemID.RED_CIRCLE), List.of(ItemID.GREEN_TRIANGLE, ItemID.YELLOW_TRIANGLE), List.of(ItemID.YELLOW_PENTAGON, ItemID.ORANGE_TRIANGLE))));
		validExchangesForDisc.put(ItemID.VIOLET_SQUARE, new HashSet<>(List.of(List.of(ItemID.GREEN_SQUARE, ItemID.ORANGE_SQUARE, ItemID.GREEN_CIRCLE), List.of(ItemID.YELLOW_PENTAGON, ItemID.ORANGE_SQUARE, ItemID.BLUE_CIRCLE), List.of(ItemID.VIOLET_TRIANGLE, ItemID.VIOLET_CIRCLE), List.of(ItemID.BLUE_TRIANGLE, ItemID.ORANGE_SQUARE, ItemID.BLUE_CIRCLE), List.of(ItemID.YELLOW_PENTAGON, ItemID.GREEN_TRIANGLE, ItemID.RED_CIRCLE), List.of(ItemID.GREEN_SQUARE, ItemID.VIOLET_CIRCLE, ItemID.GREEN_CIRCLE, ItemID.RED_CIRCLE), List.of(ItemID.GREEN_SQUARE, ItemID.VIOLET_CIRCLE, ItemID.BLUE_CIRCLE), List.of(ItemID.BLUE_TRIANGLE, ItemID.YELLOW_SQUARE, ItemID.RED_CIRCLE), List.of(ItemID.INDIGO_SQUARE, ItemID.GREEN_CIRCLE), List.of(ItemID.VIOLET_TRIANGLE, ItemID.INDIGO_CIRCLE, ItemID.RED_CIRCLE), List.of(ItemID.YELLOW_PENTAGON, ItemID.ORANGE_SQUARE, ItemID.RED_PENTAGON), List.of(ItemID.BLUE_TRIANGLE, ItemID.ORANGE_SQUARE, ItemID.RED_PENTAGON), List.of(ItemID.GREEN_SQUARE, ItemID.YELLOW_SQUARE), List.of(ItemID.VIOLET_TRIANGLE, ItemID.BLUE_CIRCLE, ItemID.ORANGE_CIRCLE), List.of(ItemID.INDIGO_TRIANGLE, ItemID.INDIGO_CIRCLE, ItemID.GREEN_CIRCLE), List.of(ItemID.INDIGO_TRIANGLE, ItemID.ORANGE_SQUARE, ItemID.ORANGE_CIRCLE), List.of(ItemID.GREEN_PENTAGON, ItemID.VIOLET_CIRCLE, ItemID.RED_CIRCLE), List.of(ItemID.BLUE_TRIANGLE, ItemID.YELLOW_TRIANGLE, ItemID.GREEN_CIRCLE), List.of(ItemID.VIOLET_TRIANGLE, ItemID.ORANGE_TRIANGLE, ItemID.RED_CIRCLE), List.of(ItemID.VIOLET_TRIANGLE, ItemID.RED_PENTAGON, ItemID.ORANGE_CIRCLE), List.of(ItemID.GREEN_SQUARE, ItemID.GREEN_TRIANGLE), List.of(ItemID.BLUE_TRIANGLE, ItemID.ORANGE_SQUARE, ItemID.RED_SQUARE, ItemID.RED_CIRCLE), List.of(ItemID.GREEN_SQUARE, ItemID.VIOLET_CIRCLE, ItemID.RED_PENTAGON), List.of(ItemID.INDIGO_TRIANGLE, ItemID.VIOLET_CIRCLE, ItemID.RED_TRIANGLE), List.of(ItemID.GREEN_PENTAGON, ItemID.INDIGO_CIRCLE, ItemID.ORANGE_CIRCLE), List.of(ItemID.INDIGO_TRIANGLE, ItemID.ORANGE_TRIANGLE, ItemID.GREEN_CIRCLE), List.of(ItemID.BLUE_PENTAGON, ItemID.RED_TRIANGLE), List.of(ItemID.GREEN_PENTAGON, ItemID.BLUE_CIRCLE, ItemID.RED_TRIANGLE), List.of(ItemID.YELLOW_PENTAGON, ItemID.YELLOW_SQUARE, ItemID.RED_CIRCLE), List.of(ItemID.GREEN_SQUARE, ItemID.YELLOW_TRIANGLE, ItemID.RED_TRIANGLE), List.of(ItemID.BLUE_TRIANGLE, ItemID.GREEN_TRIANGLE, ItemID.RED_CIRCLE), List.of(ItemID.YELLOW_PENTAGON, ItemID.ORANGE_PENTAGON, ItemID.RED_TRIANGLE), List.of(ItemID.GREEN_SQUARE, ItemID.ORANGE_PENTAGON, ItemID.ORANGE_CIRCLE), List.of(ItemID.BLUE_TRIANGLE, ItemID.ORANGE_PENTAGON, ItemID.RED_TRIANGLE), List.of(ItemID.YELLOW_PENTAGON, ItemID.ORANGE_SQUARE, ItemID.RED_SQUARE, ItemID.RED_CIRCLE), List.of(ItemID.VIOLET_SQUARE), List.of(ItemID.INDIGO_TRIANGLE, ItemID.ORANGE_PENTAGON), List.of(ItemID.GREEN_PENTAGON, ItemID.ORANGE_TRIANGLE, ItemID.ORANGE_CIRCLE), List.of(ItemID.YELLOW_PENTAGON, ItemID.YELLOW_TRIANGLE, ItemID.GREEN_CIRCLE), List.of(ItemID.GREEN_SQUARE, ItemID.VIOLET_CIRCLE, ItemID.RED_SQUARE, ItemID.RED_CIRCLE), List.of(ItemID.INDIGO_TRIANGLE, ItemID.YELLOW_TRIANGLE, ItemID.RED_CIRCLE), List.of(ItemID.GREEN_PENTAGON, ItemID.RED_PENTAGON, ItemID.RED_TRIANGLE), List.of(ItemID.BLUE_TRIANGLE, ItemID.ORANGE_SQUARE, ItemID.GREEN_CIRCLE, ItemID.RED_CIRCLE), List.of(ItemID.GREEN_PENTAGON, ItemID.ORANGE_SQUARE), List.of(ItemID.YELLOW_PENTAGON, ItemID.ORANGE_SQUARE, ItemID.GREEN_CIRCLE, ItemID.RED_CIRCLE))));
		validExchangesForDisc.put(ItemID.RED_CIRCLE, new HashSet<>(List.of(List.of(ItemID.RED_CIRCLE))));
		validExchangesForDisc.put(ItemID.RED_TRIANGLE, new HashSet<>(List.of(List.of(ItemID.RED_TRIANGLE))));
		validExchangesForDisc.put(ItemID.RED_SQUARE, new HashSet<>(List.of(List.of(ItemID.GREEN_CIRCLE))));
		validExchangesForDisc.put(ItemID.VIOLET_PENTAGON, new HashSet<>(List.of(List.of(ItemID.INDIGO_PENTAGON, ItemID.RED_SQUARE, ItemID.RED_CIRCLE), List.of(ItemID.VIOLET_TRIANGLE, ItemID.ORANGE_SQUARE, ItemID.ORANGE_TRIANGLE), List.of(ItemID.GREEN_PENTAGON, ItemID.BLUE_TRIANGLE), List.of(ItemID.GREEN_PENTAGON, ItemID.YELLOW_TRIANGLE, ItemID.RED_PENTAGON, ItemID.RED_CIRCLE), List.of(ItemID.VIOLET_TRIANGLE, ItemID.ORANGE_SQUARE, ItemID.BLUE_CIRCLE, ItemID.RED_CIRCLE), List.of(ItemID.GREEN_PENTAGON, ItemID.GREEN_TRIANGLE, ItemID.RED_TRIANGLE), List.of(ItemID.GREEN_PENTAGON, ItemID.YELLOW_SQUARE, ItemID.RED_TRIANGLE), List.of(ItemID.VIOLET_SQUARE, ItemID.INDIGO_CIRCLE, ItemID.RED_CIRCLE), List.of(ItemID.INDIGO_SQUARE, ItemID.VIOLET_CIRCLE, ItemID.GREEN_CIRCLE), List.of(ItemID.INDIGO_PENTAGON, ItemID.GREEN_CIRCLE, ItemID.RED_CIRCLE), List.of(ItemID.BLUE_PENTAGON, ItemID.INDIGO_CIRCLE, ItemID.GREEN_CIRCLE), List.of(ItemID.VIOLET_SQUARE, ItemID.BLUE_CIRCLE, ItemID.ORANGE_CIRCLE), List.of(ItemID.VIOLET_SQUARE, ItemID.VIOLET_CIRCLE), List.of(ItemID.VIOLET_TRIANGLE, ItemID.YELLOW_SQUARE, ItemID.ORANGE_CIRCLE), List.of(ItemID.VIOLET_TRIANGLE, ItemID.ORANGE_SQUARE, ItemID.RED_PENTAGON, ItemID.RED_CIRCLE), List.of(ItemID.INDIGO_SQUARE, ItemID.ORANGE_SQUARE, ItemID.RED_TRIANGLE), List.of(ItemID.BLUE_PENTAGON, ItemID.ORANGE_SQUARE, ItemID.ORANGE_CIRCLE), List.of(ItemID.VIOLET_SQUARE, ItemID.ORANGE_TRIANGLE, ItemID.RED_CIRCLE), List.of(ItemID.GREEN_PENTAGON, ItemID.YELLOW_TRIANGLE, ItemID.INDIGO_CIRCLE), List.of(ItemID.BLUE_PENTAGON, ItemID.ORANGE_TRIANGLE, ItemID.GREEN_CIRCLE), List.of(ItemID.VIOLET_SQUARE, ItemID.RED_PENTAGON, ItemID.ORANGE_CIRCLE), List.of(ItemID.VIOLET_TRIANGLE, ItemID.ORANGE_SQUARE, ItemID.RED_SQUARE, ItemID.ORANGE_CIRCLE), List.of(ItemID.GREEN_PENTAGON, ItemID.ORANGE_PENTAGON, ItemID.BLUE_CIRCLE), List.of(ItemID.GREEN_PENTAGON, ItemID.ORANGE_PENTAGON, ItemID.RED_SQUARE, ItemID.RED_CIRCLE), List.of(ItemID.VIOLET_TRIANGLE, ItemID.ORANGE_PENTAGON, ItemID.GREEN_CIRCLE), List.of(ItemID.INDIGO_PENTAGON, ItemID.RED_PENTAGON), List.of(ItemID.BLUE_PENTAGON, ItemID.VIOLET_CIRCLE, ItemID.RED_TRIANGLE), List.of(ItemID.GREEN_PENTAGON, ItemID.YELLOW_TRIANGLE, ItemID.RED_SQUARE, ItemID.ORANGE_CIRCLE), List.of(ItemID.VIOLET_TRIANGLE, ItemID.YELLOW_TRIANGLE, ItemID.BLUE_CIRCLE), List.of(ItemID.VIOLET_TRIANGLE, ItemID.YELLOW_TRIANGLE, ItemID.GREEN_CIRCLE, ItemID.RED_CIRCLE), List.of(ItemID.BLUE_PENTAGON, ItemID.ORANGE_PENTAGON), List.of(ItemID.VIOLET_TRIANGLE, ItemID.ORANGE_SQUARE, ItemID.INDIGO_CIRCLE), List.of(ItemID.VIOLET_TRIANGLE, ItemID.ORANGE_SQUARE, ItemID.GREEN_CIRCLE, ItemID.ORANGE_CIRCLE), List.of(ItemID.BLUE_PENTAGON, ItemID.YELLOW_TRIANGLE, ItemID.RED_CIRCLE), List.of(ItemID.GREEN_PENTAGON, ItemID.YELLOW_TRIANGLE, ItemID.ORANGE_TRIANGLE), List.of(ItemID.VIOLET_PENTAGON), List.of(ItemID.GREEN_PENTAGON, ItemID.YELLOW_PENTAGON), List.of(ItemID.INDIGO_SQUARE, ItemID.ORANGE_PENTAGON, ItemID.RED_CIRCLE), List.of(ItemID.VIOLET_TRIANGLE, ItemID.YELLOW_TRIANGLE, ItemID.RED_SQUARE, ItemID.RED_CIRCLE), List.of(ItemID.GREEN_PENTAGON, ItemID.YELLOW_TRIANGLE, ItemID.BLUE_CIRCLE, ItemID.RED_CIRCLE), List.of(ItemID.GREEN_PENTAGON, ItemID.ORANGE_PENTAGON, ItemID.GREEN_CIRCLE, ItemID.RED_CIRCLE), List.of(ItemID.VIOLET_TRIANGLE, ItemID.YELLOW_TRIANGLE, ItemID.RED_PENTAGON), List.of(ItemID.INDIGO_PENTAGON, ItemID.BLUE_CIRCLE), List.of(ItemID.VIOLET_TRIANGLE, ItemID.GREEN_TRIANGLE, ItemID.ORANGE_CIRCLE), List.of(ItemID.INDIGO_SQUARE, ItemID.YELLOW_TRIANGLE, ItemID.ORANGE_CIRCLE))));

		for (var entry : validExchangesForDisc.entrySet())
		{
			var discID = entry.getKey();
			var discValue = discToValue.get(discID);
			var discRequirement = discs.get(discID);

			for (var exchanges : entry.getValue())
			{
				for (var exchangedDiscID : exchanges)
				{
					if (Objects.equals(exchangedDiscID, discID))
					{
						continue;
					}

					var exchangedDiscValue = discToValue.get(exchangedDiscID);

					if (Objects.equals(exchangedDiscValue, discValue))
					{
						continue;
					}

					valuePossibleSingleDiscExchangesRequirements.computeIfAbsent(exchangedDiscValue, sv2 -> new ArrayList<>());
					var v = valuePossibleSingleDiscExchangesRequirements.get(exchangedDiscValue);
					if (!v.contains(discRequirement)) {
						v.add(discRequirement);
					}
				}
			}
		}
	}

	static public void loadValueToDoubleDiscRequirement(final HashMap<Integer, ItemRequirement> valueToRequirement, HashMap<Integer, List<ItemRequirements>> valueToDoubleDiscRequirement)
	{
		for (int i = 0; i < 35; i++)
		{
			var shape1 = valueToRequirement.get(i);
			for (int j = 0; j < 35; j++)
			{
				var shape2 = valueToRequirement.get(j);

				if (shape1 == null || shape2 == null)
				{
					continue;
				}
				valueToDoubleDiscRequirement.computeIfAbsent(i + j, sv2 -> new ArrayList<>());
				if (shape1.getId() == shape2.getId())
				{
					valueToDoubleDiscRequirement.get(i + j).add(new ItemRequirements(shape1.quantity(2)));
				}
				else
				{
					valueToDoubleDiscRequirement.get(i + j).add(new ItemRequirements(LogicType.AND, shape1, shape2));
				}
			}
		}
	}

	public static void loadDiscs(HashMap<Integer, ItemRequirement> discs)
	{
		// 9597
		discs.put(ItemID.RED_CIRCLE, new ItemRequirement("Red circle", ItemID.RED_CIRCLE).highlighted());

		// 9601
		discs.put(ItemID.ORANGE_CIRCLE, new ItemRequirement("Orange circle", ItemID.ORANGE_CIRCLE).highlighted());

		// 9605
		discs.put(ItemID.YELLOW_CIRCLE, new ItemRequirement("Yellow circle", ItemID.YELLOW_CIRCLE).highlighted());

		// 9609
		discs.put(ItemID.GREEN_CIRCLE, new ItemRequirement("Green circle", ItemID.GREEN_CIRCLE).highlighted());

		// 9613
		discs.put(ItemID.BLUE_CIRCLE, new ItemRequirement("Blue circle", ItemID.BLUE_CIRCLE).highlighted());

		// 9617
		discs.put(ItemID.INDIGO_CIRCLE, new ItemRequirement("Indigo circle", ItemID.INDIGO_CIRCLE).highlighted());

		// 9621
		discs.put(ItemID.VIOLET_CIRCLE, new ItemRequirement("Violet circle", ItemID.VIOLET_CIRCLE).highlighted());

		// 9598
		discs.put(ItemID.RED_TRIANGLE, new ItemRequirement("Red triangle", ItemID.RED_TRIANGLE).highlighted());

		// 9602
		discs.put(ItemID.ORANGE_TRIANGLE, new ItemRequirement("Orange triangle", ItemID.ORANGE_TRIANGLE).highlighted());

		// 9606
		discs.put(ItemID.YELLOW_TRIANGLE, new ItemRequirement("Yellow triangle", ItemID.YELLOW_TRIANGLE).highlighted());

		// 9610
		discs.put(ItemID.GREEN_TRIANGLE, new ItemRequirement("Green triangle", ItemID.GREEN_TRIANGLE).highlighted());

		// 9614
		discs.put(ItemID.BLUE_TRIANGLE, new ItemRequirement("Blue triangle", ItemID.BLUE_TRIANGLE).highlighted());

		// 9618
		discs.put(ItemID.INDIGO_TRIANGLE, new ItemRequirement("Indigo triangle", ItemID.INDIGO_TRIANGLE).highlighted());

		// 9622
		discs.put(ItemID.VIOLET_TRIANGLE, new ItemRequirement("Violet triangle", ItemID.VIOLET_TRIANGLE).highlighted());

		// 9599
		discs.put(ItemID.RED_SQUARE, new ItemRequirement("Red square", ItemID.RED_SQUARE).highlighted());

		// 9603
		discs.put(ItemID.ORANGE_SQUARE, new ItemRequirement("Orange square", ItemID.ORANGE_SQUARE).highlighted());

		// 9607
		discs.put(ItemID.YELLOW_SQUARE, new ItemRequirement("Yellow square", ItemID.YELLOW_SQUARE).highlighted());

		// 9611
		discs.put(ItemID.GREEN_SQUARE, new ItemRequirement("Green square", ItemID.GREEN_SQUARE).highlighted());

		// 9615
		discs.put(ItemID.BLUE_SQUARE, new ItemRequirement("Blue square", ItemID.BLUE_SQUARE).highlighted());

		// 9619
		discs.put(ItemID.INDIGO_SQUARE, new ItemRequirement("Indigo square", ItemID.INDIGO_SQUARE).highlighted());

		// 9623
		discs.put(ItemID.VIOLET_SQUARE, new ItemRequirement("Violet square", ItemID.VIOLET_SQUARE).highlighted());

		// 9600
		discs.put(ItemID.RED_PENTAGON, new ItemRequirement("Red pentagon", ItemID.RED_PENTAGON).highlighted());

		// 9604
		discs.put(ItemID.ORANGE_PENTAGON, new ItemRequirement("Orange pentagon", ItemID.ORANGE_PENTAGON).highlighted());

		// 9608
		discs.put(ItemID.YELLOW_PENTAGON, new ItemRequirement("Yellow pentagon", ItemID.YELLOW_PENTAGON).highlighted());

		// 9612
		discs.put(ItemID.GREEN_PENTAGON, new ItemRequirement("Green pentagon", ItemID.GREEN_PENTAGON).highlighted());

		// 9616
		discs.put(ItemID.BLUE_PENTAGON, new ItemRequirement("Blue pentagon", ItemID.BLUE_PENTAGON).highlighted());

		// 9620
		discs.put(ItemID.INDIGO_PENTAGON, new ItemRequirement("Indigo pentagon", ItemID.INDIGO_PENTAGON).highlighted());

		// 9624
		discs.put(ItemID.VIOLET_PENTAGON, new ItemRequirement("Violet pentagon", ItemID.VIOLET_PENTAGON).highlighted());
	}

	public static void loadValueToRequirement(final HashMap<Integer, ItemRequirement> discs, HashMap<Integer, ItemRequirement> valueToRequirement)
	{
		var yellowCircleRedTri = new ItemRequirement("Yellow circle/red triangle", ItemID.RED_TRIANGLE).highlighted();
		yellowCircleRedTri.addAlternates(ItemID.YELLOW_CIRCLE);
		var greenCircleRedSquare = new ItemRequirement("Green circle/red square", ItemID.GREEN_CIRCLE).highlighted();
		greenCircleRedSquare.addAlternates(ItemID.RED_SQUARE);
		var blueCircleRedPentagon = new ItemRequirement("Blue circle/red pentagon", ItemID.BLUE_CIRCLE).highlighted();
		blueCircleRedPentagon.addAlternates(ItemID.RED_PENTAGON);
		var indigoCircleOrangeTriangle = new ItemRequirement("Indigo circle/orange triangle", ItemID.INDIGO_CIRCLE).highlighted();
		indigoCircleOrangeTriangle.addAlternates(ItemID.ORANGE_TRIANGLE);
		var yellowSquareGreenTriangle = new ItemRequirement("Yellow square/green triangle", ItemID.YELLOW_SQUARE).highlighted();
		yellowSquareGreenTriangle.addAlternates(ItemID.GREEN_TRIANGLE);
		var yellowPentagonBlueTriangle = new ItemRequirement("Yellow pentagon/blue triangle", ItemID.YELLOW_PENTAGON).highlighted();
		yellowPentagonBlueTriangle.addAlternates(ItemID.BLUE_TRIANGLE);
		var blueSquareGreenPentagon = new ItemRequirement("Blue square/green pentagon", ItemID.BLUE_SQUARE).highlighted();
		blueSquareGreenPentagon.addAlternates(ItemID.GREEN_PENTAGON);

		valueToRequirement.put(1, discs.get(ItemID.RED_CIRCLE));
		valueToRequirement.put(2, discs.get(ItemID.ORANGE_CIRCLE));
		valueToRequirement.put(3, yellowCircleRedTri);
		valueToRequirement.put(4, greenCircleRedSquare);
		valueToRequirement.put(5, blueCircleRedPentagon);
		valueToRequirement.put(6, indigoCircleOrangeTriangle);
		valueToRequirement.put(7, discs.get(ItemID.VIOLET_CIRCLE));
		valueToRequirement.put(8, discs.get(ItemID.ORANGE_SQUARE));
		valueToRequirement.put(9, discs.get(ItemID.YELLOW_TRIANGLE));
		valueToRequirement.put(10, discs.get(ItemID.ORANGE_PENTAGON));
		valueToRequirement.put(12, yellowSquareGreenTriangle);
		valueToRequirement.put(15, yellowPentagonBlueTriangle);
		valueToRequirement.put(16, discs.get(ItemID.GREEN_SQUARE));
		valueToRequirement.put(18, discs.get(ItemID.INDIGO_TRIANGLE));
		valueToRequirement.put(20, blueSquareGreenPentagon);
		valueToRequirement.put(21, discs.get(ItemID.VIOLET_TRIANGLE));
		valueToRequirement.put(24, discs.get(ItemID.INDIGO_SQUARE));
		valueToRequirement.put(25, discs.get(ItemID.BLUE_PENTAGON));
		valueToRequirement.put(28, discs.get(ItemID.VIOLET_SQUARE));
		valueToRequirement.put(30, discs.get(ItemID.INDIGO_PENTAGON));
		valueToRequirement.put(35, discs.get(ItemID.VIOLET_PENTAGON));
	}

	public static void loadDiscToValue(HashMap<Integer, Integer> discToValue)
	{
		discToValue.put(ItemID.RED_CIRCLE, 1);
		discToValue.put(ItemID.RED_TRIANGLE, 3);
		discToValue.put(ItemID.RED_SQUARE, 4);
		discToValue.put(ItemID.RED_PENTAGON, 5);
		discToValue.put(ItemID.ORANGE_CIRCLE, 2);
		discToValue.put(ItemID.ORANGE_TRIANGLE, 6);
		discToValue.put(ItemID.ORANGE_SQUARE, 8);
		discToValue.put(ItemID.ORANGE_PENTAGON, 10);
		discToValue.put(ItemID.YELLOW_CIRCLE, 3);
		discToValue.put(ItemID.YELLOW_TRIANGLE, 9);
		discToValue.put(ItemID.YELLOW_SQUARE, 12);
		discToValue.put(ItemID.YELLOW_PENTAGON, 15);
		discToValue.put(ItemID.GREEN_CIRCLE, 4);
		discToValue.put(ItemID.GREEN_TRIANGLE, 12);
		discToValue.put(ItemID.GREEN_SQUARE, 16);
		discToValue.put(ItemID.GREEN_PENTAGON, 20);
		discToValue.put(ItemID.BLUE_CIRCLE, 5);
		discToValue.put(ItemID.BLUE_TRIANGLE, 15);
		discToValue.put(ItemID.BLUE_SQUARE, 20);
		discToValue.put(ItemID.BLUE_PENTAGON, 25);
		discToValue.put(ItemID.INDIGO_CIRCLE, 6);
		discToValue.put(ItemID.INDIGO_TRIANGLE, 18);
		discToValue.put(ItemID.INDIGO_SQUARE, 24);
		discToValue.put(ItemID.INDIGO_PENTAGON, 30);
		discToValue.put(ItemID.VIOLET_CIRCLE, 7);
		discToValue.put(ItemID.VIOLET_TRIANGLE, 21);
		discToValue.put(ItemID.VIOLET_SQUARE, 28);
		discToValue.put(ItemID.VIOLET_PENTAGON, 35);
	}

	public static WorldPoint regionPoint(int regionX, int regionY)
	{
		return WorldPoint.fromRegion(STOREROOM_REGION, regionX, regionY, 0);
	}

	@Override
	public void startUp()
	{
		puzzle1LeftItemID = client.getVarpValue(PUZZLE1_LEFT_VARP_ID);
		puzzle1RightItemID = client.getVarpValue(PUZZLE1_RIGHT_VARP_ID);
		puzzle2ItemID = client.getVarpValue(PUZZLE2_VARP_ID);

		updateSteps();
	}

	@Override
	protected void setupSteps()
	{
		getMoreDiscs = new ObjectStep(getQuestHelper(), ObjectID.CHEST_49617, regionPoint(34, 31), "Get more discs from the chests outside. You can drop discs before you get more. You can also use the exchanger next to Yewnock's machine.", true);

		useExchanger = new ObjectStep(getQuestHelper(), ObjectID.YEWNOCKS_EXCHANGER, regionPoint(22, 33), "A solution has been calculated, exit the machine interface & click Yewnock's exchanger.");
		useExchanger.addWidgetHighlight(848, 27); // TODO: Verify that this is the "exit" button in the Machine widget

		clickMachine = new ObjectStep(getQuestHelper(), ObjectID.YEWNOCKS_MACHINE_49662, regionPoint(22, 32),
			"A solution has been found, click Yewnock's machine and insert the discs as prompted.");
		clickMachine.addWidgetHighlight(849, 41);

		clickMachineOnce = new ObjectStep(getQuestHelper(), ObjectID.YEWNOCKS_MACHINE_49662, regionPoint(22, 32), "Operate Yewnock's machine to calculate a solution.");

		machineInsertDisc = new DiscInsertionStep(getQuestHelper(), "Insert the highlighted disc into the highlighted slot.");
		machineReset = new WidgetStep(getQuestHelper(),
			"An incorrect disc has been inserted into the machine, click the reset button & follow the instructions.",
			848, 25);
		machineSubmit = new WidgetStep(getQuestHelper(),
			"Click the submit button.",
			848, 26);

		exchangerInsertDisc = new DiscInsertionStep(getQuestHelper(), "Insert the highlighted disc into the highlighted slot.");
		exchangerExchange = new WidgetStep(getQuestHelper(), "", 849, 40);
		exchangerConfirm = new DetailedQuestStep(getQuestHelper(), "Click the confirm button.");
		exchangerConfirm.addWidgetHighlight(849, 36);
		exchangerReset = new WidgetStep(getQuestHelper(), "Found unexpected disc(s) in the exchange input, reset & follow the instructions.");
		exchangerReset.addWidgetHighlight(849, 34);

		machineOpen = new WidgetPresenceRequirement(848, 0);
		exchangerOpen = new WidgetPresenceRequirement(849, 0);
	}

	@Subscribe
	@Override
	public void onVarbitChanged(VarbitChanged varbitChanged)
	{
		if (varbitChanged.getVarbitId() == -1)
		{
			if (varbitChanged.getVarpId() == PUZZLE1_LEFT_VARP_ID)
			{
				puzzle1LeftItemID = varbitChanged.getValue();
			}
			else if (varbitChanged.getVarpId() == PUZZLE1_RIGHT_VARP_ID)
			{
				puzzle1RightItemID = varbitChanged.getValue();
			}
			else if (varbitChanged.getVarpId() == PUZZLE2_VARP_ID)
			{
				puzzle2ItemID = varbitChanged.getValue();
			}
			else
			{
				// irrelevant value changed
				return;
			}

			updateSteps();
		}
	}

	@Subscribe
	public void onItemContainerChanged(final ItemContainerChanged ignoredEvent)
	{
		// TODO: don't update steps, just re-do the calculation & if its state has changed, then update steps
		// TODO: optimize
		updateSteps();
	}

	@Subscribe
	public void onGameTick(final GameTick ignoredEvent)
	{
		// TODO: optimize
		updateSteps();
	}

	@Override
	public void shutDown()
	{
		super.shutDown();
		puzzle1LeftItemID = -1;
		puzzle1RightItemID = -1;
		puzzle2ItemID = -1;
		solution.reset();
	}

	private int getWidgetItemId(int groupId, int childId)
	{
		var widget = client.getWidget(groupId, childId);
		if (widget == null)
		{
			return -1;
		}

		return widget.getItemId();
	}

	/**
	 * This function will add a widget highlight to the slot where it finds a good exchange, if any
	 *
	 * @return a pair of widget group + child IDs if there is an exchange we're looking for in one of the slots
	 */
	private Optional<Pair<Integer, Integer>> findGoodExchange()
	{
		var exchangeResultTL = getWidgetItemId(849, 21);
		var exchangeResultTR = getWidgetItemId(849, 24);
		var exchangeResultBL = getWidgetItemId(849, 27);
		var exchangeResultBR = getWidgetItemId(849, 30);

		for (var puzzleNeed : solution.puzzleNeeds)
		{
			if (puzzleNeed.getAllIds().contains(exchangeResultTL))
			{
				return Optional.of(Pair.of(849, 21));
			}
			if (puzzleNeed.getAllIds().contains(exchangeResultTR))
			{
				return Optional.of(Pair.of(849, 24));
			}
			if (puzzleNeed.getAllIds().contains(exchangeResultBL))
			{
				return Optional.of(Pair.of(849, 27));
			}
			if (puzzleNeed.getAllIds().contains(exchangeResultBR))
			{
				return Optional.of(Pair.of(849, 30));
			}
		}

		return Optional.empty();
	}

	/**
	 * @return true if the user has opened Yewnock's machine
	 */
	private boolean hasOpenedMachine()
	{
		return puzzle1LeftItemID > 0 && puzzle1RightItemID > 0 && puzzle2ItemID > 0;
	}

	/**
	 * @param inventoryId ID of the inventory to try to get discs from
	 * @return a list of discs as Items, or an empty list if inventory wasn't found
	 */
	@Nonnull
	private List<Item> getDiscs(InventoryID inventoryId)
	{
		var itemContainer = client.getItemContainer(inventoryId);
		if (itemContainer == null)
		{
			// Inventory not loaded
			return List.of();
		}

		return Stream.of(itemContainer.getItems()).filter(i -> discs.containsKey(i.getId())).collect(Collectors.toUnmodifiableList());
	}

	/**
	 * Get all discs in the player's inventory & try to make a solution for the puzzle from it
	 * Will early out if the puzzle values are not known (i.e. if the player has not clicked the machine yet)
	 */
	private void refreshSolution()
	{
		if (puzzle1LeftItemID <= 0 || puzzle1RightItemID <= 0 || puzzle2ItemID <= 0)
		{
			// Couldn't find the solution required, this shouldn't be the case when the widget is open
			return;
		}


		var puzzle2SolutionValue = discToValue.get(puzzle2ItemID);
		if (puzzle2SolutionValue == null)
		{
			// The item ID found in the puzzle2 box was invalid, not sure how to recover
			return;
		}

		var puzzle1SolutionValue1 = discToValue.get(puzzle1LeftItemID);
		var puzzle1SolutionValue2 = discToValue.get(puzzle1RightItemID);
		if (puzzle1SolutionValue1 == null || puzzle1SolutionValue2 == null)
		{
			// One of the item IDs found in the puzzle1 boxes were invalid, not sure how to recover
			return;
		}

		var items = getDiscs(InventoryID.INVENTORY);

		var puzzle1SolutionValue = puzzle1SolutionValue1 + puzzle1SolutionValue2;
		// Try to figure out a solution
		solution.load(client, items, puzzle1SolutionValue, puzzle2SolutionValue,
			discs,
			valueToRequirement, valueToDoubleDiscRequirement, discToValue, valuePossibleSingleDiscExchangesRequirements);
	}

	protected void updateSteps()
	{
		var numDiscs = getDiscs(InventoryID.INVENTORY).stream().mapToInt(Item::getQuantity).sum();
		if (numDiscs < 3)
		{
			// Player has fewer than 3 discs, no solution is possible
			// Ask the player to get discs at the chests
			startUpStep(getMoreDiscs);
			return;
		}

		if (!hasOpenedMachine())
		{
			// Player hasn't clicked the machine yet, so we haven't tried calculating a solution
			// Ask the player to click the machine
			startUpStep(clickMachineOnce);
			return;
		}

		if (!solution.isGood())
		{
			solution.reset();
			refreshSolution();
		}

		if (solution.isGood())
		{
			// A good solution has been calculated with the discs the player has in their inventory
			if (!machineOpen.check(client))
			{
				// Prompt the player to open the machine
				startUpStep(clickMachine);
				return;
			}

			var puzzle1InsertedDisc = client.getVarpValue(PUZZLE1_INSERTED_DISC_VARP_ID);
			var puzzle2UpperInsertedDisc = client.getVarpValue(PUZZLE2_UPPER_INSERTED_DISC_VARP_ID);
			var puzzle2LowerInsertedDisc = client.getVarpValue(PUZZLE2_LOWER_INSERTED_DISC_VARP_ID);

			if (!solution.puzzle1Requirement.getAllIds().contains(puzzle1InsertedDisc))
			{
				if (puzzle1InsertedDisc > 0)
				{
					// A disc has been inserted in the puzzle 1 slot, but it's not the correct one
					// Prompt the player to reset the machine
					startUpStep(machineReset);
					return;
				}

				// Puzzle 1 slot is empty
				// Prompt the player to insert the disc into the puzzle 1 slot
				machineInsertDisc.setRequirements(List.of(solution.puzzle1Requirement));
				machineInsertDisc.clearWidgetHighlights();
				machineInsertDisc.addWidgetHighlight(848, 19);
				startUpStep(machineInsertDisc);
				return;
			}

			if (!solution.puzzle2UpperRequirement.getAllIds().contains(puzzle2UpperInsertedDisc))
			{
				if (puzzle2UpperInsertedDisc > 0)
				{
					// A disc has been inserted in the puzzle 2 upper slot, but it's not the correct one
					// Prompt the player to reset the machine
					// NOTE: Technically we could allow all the puzzle 2 requirements in this slot, but I prefer
					// to keep things simple for now
					startUpStep(machineReset);
					return;
				}

				// Puzzle 2 upper slot is empty
				// Prompt the player to insert the disc into the puzzle 2 upper slot
				machineInsertDisc.setRequirements(List.of(solution.puzzle2UpperRequirement));
				machineInsertDisc.clearWidgetHighlights();
				machineInsertDisc.addWidgetHighlight(848, 20);
				startUpStep(machineInsertDisc);
				return;
			}

			if (!solution.puzzle2LowerRequirement.getAllIds().contains(puzzle2LowerInsertedDisc))
			{
				if (puzzle2LowerInsertedDisc > 0)
				{
					// A disc has been inserted in the puzzle 2 lower slot, but it's not the correct one
					// Prompt the player to reset the machine
					// NOTE: Technically we could allow all the puzzle 2 requirements in this slot, but I prefer
					// to keep things simple for now
					startUpStep(machineReset);
					return;
				}

				// Puzzle 2 lower slot is empty
				// Prompt the player to insert the disc into the puzzle 2 lower slot
				machineInsertDisc.setRequirements(List.of(solution.puzzle2LowerRequirement));
				machineInsertDisc.clearWidgetHighlights();
				machineInsertDisc.addWidgetHighlight(848, 21);
				startUpStep(machineInsertDisc);
				return;
			}

			// All the puzzle slots contain the correct discs
			// Prompt the player to click the submit button
			startUpStep(machineSubmit);
			return;
		}

		getMoreDiscs.setRequirements(solution.puzzleNeeds);

		if (solution.puzzleNeeds.isEmpty())
		{
			// Something is wrong - if the solution is bad, there should be some sort of requirement
			log.warn("No solution found for this puzzle at all, no clue how to proceed.");
			startUpStep(getMoreDiscs);
			return;
		}

		if (exchangerOpen.check(client))
		{
			// Exchanger widget is open

			var goodExchange = findGoodExchange();
			if (goodExchange.isPresent())
			{
				// There's a good exchange right now, highlight the confirm button
				var goodExchangeWidget = goodExchange.get();

				exchangerConfirm.clearWidgetHighlights();
				// Highlight the confirm button
				exchangerConfirm.addWidgetHighlight(849, 36);
				// Highlight the widget with the good exchange
				exchangerConfirm.addWidgetHighlight(goodExchangeWidget.getLeft(), goodExchangeWidget.getRight());
				startUpStep(exchangerConfirm);
				return;
			}

			exchangerInsertDisc.setRequirements(solution.toExchange);

			// Exchanger widget is open
			var exchangeInput1 = getWidgetItemId(849, 8);
			var exchangeInput2 = getWidgetItemId(849, 13);
			var exchangeInput3 = getWidgetItemId(849, 18);
			List<Integer> exchangeInputs = Stream.of(exchangeInput1, exchangeInput2, exchangeInput3).filter(itemId -> itemId > 0).collect(Collectors.toUnmodifiableList());

			// TODO: Validate that the correct disc is in one of the 3 exchange slots (need their widget IDs or varbits)
			var firstExchange = solution.toExchange.get(0);
			var firstExchangeIds = firstExchange.getAllIds();
			if (exchangeInputs.isEmpty())
			{
				// There's no disc in the exchanger

				// Highlight the first exchanger input
				exchangerInsertDisc.clearWidgetHighlights();
				exchangerInsertDisc.addWidgetHighlight(849, 8);
				startUpStep(exchangerInsertDisc);
				return;
			}

			if (exchangeInputs.size() == 1)
			{
				var idInExchanger = exchangeInputs.get(0);
				if (firstExchangeIds.contains(idInExchanger))
				{
					// There's one disc in the exchanger & it's the correct one

					var discWeAreLookingFor = solution.puzzleNeeds
						.stream()
						.map(ItemRequirement::getName)
						.collect(Collectors.joining(" or "));
					exchangerExchange.setText(String.format("Click the exchange button until a %s appears.", discWeAreLookingFor));
					startUpStep(exchangerExchange);
					return;
				}
			}

			// Too many
			// Highlight the reset button
			startUpStep(exchangerReset);
			return;
		}

		if (solution.toExchange.isEmpty())
		{
			// No solution found, but no exchange figured out.
			// Prompt the user to get more discs
			startUpStep(getMoreDiscs);
			return;
		}

		if (machineOpen.check(client))
		{
			// A partial solution is found, and it can be completed by using the exchanger
			// The machine is open, prompt the user to close it then click the exchanger
			if (solution.puzzle1Requirement == null)
			{
				useExchanger.setText("A partial solution has been calculated, exit the machine interface & click Yewnock's exchanger.");
			}
			else
			{
				useExchanger.setText("A solution has been calculated, exit the machine interface & click Yewnock's exchanger.");
			}
			startUpStep(useExchanger);
			return;
		}

		// A partial solution is found, and it can be completed by using the exchanger
		// The machine is not open, just prompt the user to click the exchanger
		if (solution.puzzle1Requirement == null)
		{
			useExchanger.setText("A partial solution has been calculated, click Yewnock's exchanger to start exchanging discs.");
		}
		else
		{
			useExchanger.setText("A solution has been calculated, click Yewnock's exchanger to start exchanging discs.");
		}
		startUpStep(useExchanger);
	}

	@Override
	public List<QuestStep> getSteps()
	{
		return List.of(
			getMoreDiscs,
			clickMachine,
			clickMachineOnce,
			useExchanger,
			machineInsertDisc,
			machineReset,
			machineSubmit,
			exchangerInsertDisc,
			exchangerExchange,
			exchangerConfirm,
			exchangerReset
		);
	}

	public static class SubsetSum
	{
		public static List<List<Integer>> findSubsetsWithSum(int[] numbers, int targetSum)
		{
			List<List<Integer>> allSubsets = new ArrayList<>();
			List<Integer> currentSubset = new ArrayList<>();
			boolean[] used = new boolean[numbers.length]; // Keep track of used numbers
			findSubsets(numbers, targetSum, 0, currentSubset, allSubsets, 3, used);
			return allSubsets;
		}

		private static void findSubsets(int[] numbers, int targetSum, int currentIndex, List<Integer> currentSubset, List<List<Integer>> allSubsets, int maxValues, boolean[] used)
		{
			if (targetSum == 0 && currentSubset.size() <= maxValues)
			{
				allSubsets.add(new ArrayList<>(currentSubset));
				return;
			}
			if (currentIndex >= numbers.length || targetSum < 0 || currentSubset.size() >= maxValues)
			{
				return;
			}

			// Include the current number in the subset if it's not already used
			if (!used[currentIndex])
			{
				currentSubset.add(numbers[currentIndex]);
				used[currentIndex] = true; // Mark the number as used
				findSubsets(numbers, targetSum - numbers[currentIndex], currentIndex, currentSubset, allSubsets, maxValues, used);
				used[currentIndex] = false; // Unmark the number to backtrack
				currentSubset.remove(currentSubset.size() - 1);
			}

			// Exclude the current number from the subset
			findSubsets(numbers, targetSum, currentIndex + 1, currentSubset, allSubsets, maxValues, used);
		}
	}
}
