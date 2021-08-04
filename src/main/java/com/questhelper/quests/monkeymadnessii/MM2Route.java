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
package com.questhelper.quests.monkeymadnessii;

import com.questhelper.Zone;
import com.questhelper.requirements.ChatMessageRequirement;
import com.questhelper.requirements.ZoneRequirement;
import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

/*
* Routes are from Start (S) X to End (E) Y. The nodes are as labeled in https://imgur.com/a/CRQTC1d
* */
@AllArgsConstructor
@Getter
public enum MM2Route
{
	S0E1(0, 1, new Zone(new WorldPoint(2581, 9224, 1)), new Zone(new WorldPoint(2586, 9224, 1)),
		new ChatMessageRequirement(new ZoneRequirement(new WorldPoint(2581, 9224, 1)), "Something about this route feels wrong."),
		Arrays.asList(new WorldPoint(2581, 9224, 1), new WorldPoint(2586, 9224, 1))),
	S1E0(1, 0, new Zone(new WorldPoint(2586, 9223, 1)), new Zone(new WorldPoint(2581, 9223, 1)),
		new ChatMessageRequirement(new ZoneRequirement(new WorldPoint(2586, 9223, 1)), "Something about this route feels wrong."),
		Arrays.asList(new WorldPoint(2586, 9224, 1), new WorldPoint(2581, 9224, 1))),

	S0E3(0, 3, new Zone(new WorldPoint(2576, 9224, 1)), new Zone(new WorldPoint(2576, 9225, 1)),
		new ChatMessageRequirement(new ZoneRequirement(new WorldPoint(2576, 9224, 1)), "Something about this route feels wrong."),
		Arrays.asList(new WorldPoint(2576, 9224, 1), new WorldPoint(2576, 9225, 1), new WorldPoint(2577, 9225, 1),
			new WorldPoint(2577, 9227, 1), new WorldPoint(2575, 9227, 1), new WorldPoint(2575, 9229, 1), new WorldPoint(2572, 9229, 1),
			new WorldPoint(2572, 9230, 1), new WorldPoint(2570, 9230, 1), new WorldPoint(2570, 9232, 1), new WorldPoint(2569, 9232, 1),
			new WorldPoint(2569, 9234, 1), new WorldPoint(2571, 9234, 1), new WorldPoint(2571, 9237, 1))),

	S1E2(1, 2, new Zone(new WorldPoint(2592, 9221, 1)), new Zone(new WorldPoint(2593, 9221, 1)),
		new ChatMessageRequirement(new ZoneRequirement(new Zone(new WorldPoint(2589, 9221, 1), new WorldPoint(2592, 9221, 1))), "Something about this route feels wrong."),
		Arrays.asList(new WorldPoint(2589, 9221, 1), new WorldPoint(2597, 9221, 1))),
	S2E1(2, 1, new Zone(new WorldPoint(2593, 9221, 1)), new Zone(new WorldPoint(2592, 9221, 1)),
		new ChatMessageRequirement(new ZoneRequirement(new Zone(new WorldPoint(2594, 9221, 1), new WorldPoint(2596, 9221, 1))), "Something about this route feels wrong."),
		Arrays.asList(new WorldPoint(2597, 9221, 1), new WorldPoint(2589, 9221, 1))),

	S1E5(1, 5, new Zone(new WorldPoint(2587, 9226, 1)), new Zone(new WorldPoint(2587, 9231, 1)),
		new ChatMessageRequirement(new ZoneRequirement(new WorldPoint(2587, 9226, 1)), "Something about this route feels wrong."),
		Arrays.asList(new WorldPoint(2587, 9226, 1), new WorldPoint(2587, 9231, 1))),
	S5E1(5, 1, new Zone(new WorldPoint(2588, 9231, 1)), new Zone(new WorldPoint(2588, 9226, 1)),
		new ChatMessageRequirement(new ZoneRequirement(new WorldPoint(2588, 9231, 1)), "Something about this route feels wrong."),
		Arrays.asList(new WorldPoint(2588, 9231, 1), new WorldPoint(2588, 9226, 1))),

	S2E6(2, 6, new Zone(new WorldPoint(2604, 9229, 1)), new Zone(new WorldPoint(2604, 9230, 1)),
		new ChatMessageRequirement(new ZoneRequirement(new WorldPoint(2604, 9229, 1)), "Something about this route feels wrong."),
		Arrays.asList(new WorldPoint(2604, 9228, 1), new WorldPoint(2604, 9230, 1), new WorldPoint(2602, 9230, 1),
			new WorldPoint(2602, 9232, 1))),

	S3E4(3, 4, new Zone(new WorldPoint(2578, 9238, 1)), new Zone(new WorldPoint(2579, 9238, 1)),
	new ChatMessageRequirement(new ZoneRequirement(new WorldPoint(2576, 9238, 1), new WorldPoint(2578, 9238, 1)), "Something about this route feels wrong."),
	Arrays.asList(new WorldPoint(2575, 9238, 1), new WorldPoint(2583, 9238, 1))),
	S4E3(4, 3, new Zone(new WorldPoint(2580, 9238, 1)), new Zone(new WorldPoint(2579, 9238, 1)),
		new ChatMessageRequirement(new ZoneRequirement(new WorldPoint(2580, 9238, 1), new WorldPoint(2582, 9238, 1)), "Something about this route feels wrong."),
		Arrays.asList(new WorldPoint(2583, 9238, 1), new WorldPoint(2575, 9238, 1))),

	S3E7(3, 7, new Zone(new WorldPoint(2572, 9239, 1)), new Zone(new WorldPoint(2572, 9242, 1)),
		new ChatMessageRequirement(new ZoneRequirement(new WorldPoint(2572, 9239, 1)), "Something in the tunnel attacks you. Maybe this is the wrong route."),
		Arrays.asList(new WorldPoint(2572, 9239, 1), new WorldPoint(2572, 9247, 1))),

	S4E8(4, 8, new Zone(new WorldPoint(2585, 9240, 1)), new Zone(new WorldPoint(2585, 9241, 1)),
		new ChatMessageRequirement(new ZoneRequirement(new WorldPoint(2585, 9239, 1)), "Something about this route feels wrong."),
		Arrays.asList(new WorldPoint(2585, 9238, 1), new WorldPoint(2585, 9244, 1))),
	S8E4(8, 4, new Zone(new WorldPoint(2585, 9241, 1)), new Zone(new WorldPoint(2585, 9240, 1)),
	new ChatMessageRequirement(new ZoneRequirement(new WorldPoint(2585, 9243, 1)), "Something about this route feels wrong."),
	Arrays.asList(new WorldPoint(2585, 9244, 1), new WorldPoint(2585, 9238, 1))),

	S4E5(4, 5, new Zone(new WorldPoint(2588, 9237, 1)), new Zone(new WorldPoint(2589, 9237, 1)),
		new ChatMessageRequirement(new ZoneRequirement(new WorldPoint(2587, 9237, 1)), "Something about this route feels wrong."),
		Arrays.asList(new WorldPoint(2586, 9237, 1), new WorldPoint(2591, 9237, 1))),
	S5E4(5, 4, new Zone(new WorldPoint(2589, 9237, 1)), new Zone(new WorldPoint(2588, 9237, 1)),
		new ChatMessageRequirement(new ZoneRequirement(new WorldPoint(2590, 9237, 1)), "Something about this route feels wrong."),
		Arrays.asList(new WorldPoint(2591, 9237, 1), new WorldPoint(2586, 9237, 1))),

	S5E6(5, 6, new Zone(new WorldPoint(2593, 9232, 1)), new Zone(new WorldPoint(2600, 9237, 1)),
		new ChatMessageRequirement(new ZoneRequirement(new WorldPoint(2599, 9233, 1)), "Something about this route feels wrong."),
		Arrays.asList(new WorldPoint(2598, 9233, 1), new WorldPoint(2600, 9233, 1),
			new WorldPoint(2600, 9232, 1), new WorldPoint(2602, 9232, 1))),
	S6E5(6, 5, new Zone(new WorldPoint(2601, 9232, 1)), new Zone(new WorldPoint(2600, 9232, 1)),
		new ChatMessageRequirement(new ZoneRequirement(new WorldPoint(2601, 9232, 1)), "Something about this route feels wrong."),
		Arrays.asList(new WorldPoint(2602, 9232, 1), new WorldPoint(2600, 9232, 1),
			new WorldPoint(2600, 9233, 1), new WorldPoint(2598, 9233, 1))),

	S6E9(6, 9, new Zone(new WorldPoint(2603, 9232, 1)), new Zone(new WorldPoint(2604, 9232, 1)),
		new ChatMessageRequirement(new ZoneRequirement(new WorldPoint(2603, 9232, 1)), "Something about this route feels wrong."),
		Arrays.asList(new WorldPoint(2602, 9232, 1), new WorldPoint(2605, 9232, 1), new WorldPoint(2605, 9234, 1),
			new WorldPoint(2607, 9234, 1), new WorldPoint(2607, 9236, 1), new WorldPoint(2606, 9236, 1),
			new WorldPoint(2606, 9239, 1))),

	S7E8(7, 8, new Zone(new WorldPoint(2576, 9248, 1)), new Zone(new WorldPoint(2577, 9248, 1)),
		new ChatMessageRequirement(new ZoneRequirement(new WorldPoint(2575, 9248, 1)), "Something in the tunnel attacks you. Maybe this is the wrong route."),
		Arrays.asList(new WorldPoint(2574, 9248, 1), new WorldPoint(2585, 9248, 1))),
	S8E7(8, 7, new Zone(new WorldPoint(2582, 9248, 1)), new Zone(new WorldPoint(2579, 9248, 1)),
		new ChatMessageRequirement(new ZoneRequirement(new WorldPoint(2582, 9248, 1)), "Something in the tunnel attacks you. Maybe this is the wrong route."),
		Arrays.asList(new WorldPoint(2585, 9248, 1), new WorldPoint(2574, 9248, 1))),

	S7E10(7, 10, new Zone(new WorldPoint(2573, 9251, 1)), new Zone(new WorldPoint(2573, 9252, 1)),
		new ChatMessageRequirement(new ZoneRequirement(new WorldPoint(2573, 9250, 1)), "Something about this route feels wrong."),
		Arrays.asList(new WorldPoint(2573, 9249, 1), new WorldPoint(2573, 9261, 1), new WorldPoint(2579, 9261, 1),
			new WorldPoint(2582, 9259, 1), new WorldPoint(2590, 9259, 1), new WorldPoint(2590, 9261, 1), new WorldPoint(2592, 9261, 1),
			new WorldPoint(2592, 9262, 1), new WorldPoint(2593, 9262, 1), new WorldPoint(2593, 9263, 1), new WorldPoint(2595, 9263, 1),
			new WorldPoint(2595, 9265, 1))),

	S8E9(8, 9, new Zone(new WorldPoint(2592, 9247, 1)), new Zone(new WorldPoint(2595, 9247, 1)),
		new ChatMessageRequirement(new ZoneRequirement(new WorldPoint(2592, 9247, 1)), "Something in the tunnel attacks you. Maybe this is the wrong route."),
		Arrays.asList(new WorldPoint(2592, 9247, 1), new WorldPoint(2595, 9247, 1), new WorldPoint(2606, 9243, 1))),
	S9E8(9, 8, new Zone(new WorldPoint(2595, 9247, 1)), new Zone(new WorldPoint(2592, 9247, 1)),
		new ChatMessageRequirement(new ZoneRequirement(new WorldPoint(2595, 9247, 1)), "Something in the tunnel attacks you. Maybe this is the wrong route."),
		Arrays.asList(new WorldPoint(2606, 9243, 1), new WorldPoint(2595, 9247, 1), new WorldPoint(2592, 9247, 1))),

	S8E10(8, 10, new Zone(new WorldPoint(2589, 9251, 1)), new Zone(new WorldPoint(2589, 9254, 1)),
		new ChatMessageRequirement(new ZoneRequirement(new WorldPoint(2589, 9251, 1)), "Something in the tunnel attacks you. Maybe this is the wrong route."),
		Arrays.asList(new WorldPoint(2589, 9251, 1), new WorldPoint(2589, 9254, 1), new WorldPoint(2588, 9255, 1),
			new WorldPoint(2588, 9259, 1), new WorldPoint(2590, 9259, 1), new WorldPoint(2590, 9261, 1), new WorldPoint(2592, 9261, 1),
			new WorldPoint(2592, 9262, 1), new WorldPoint(2593, 9262, 1), new WorldPoint(2593, 9263, 1), new WorldPoint(2595, 9263, 1),
			new WorldPoint(2595, 9265, 1))),

	S9E10(9, 10, new Zone(new WorldPoint(2611, 9245, 1)), new Zone(new WorldPoint(2611, 9250, 1)),
		new ChatMessageRequirement(new ZoneRequirement(new WorldPoint(2611, 9245, 1)), "Something about this route feels wrong."),
		Arrays.asList(new WorldPoint(2611, 9245, 1), new WorldPoint(2611, 9250, 1), new WorldPoint(2609, 9253, 1),
			new WorldPoint(2609, 9256, 1), new WorldPoint(2607, 9256, 1), new WorldPoint(2607, 9254, 1), new WorldPoint(2605, 9254, 1),
			new WorldPoint(2605, 9256, 1), new WorldPoint(2604, 9256, 1), new WorldPoint(2604, 9258, 1), new WorldPoint(2603, 9258, 1),
			new WorldPoint(2603, 9259, 1), new WorldPoint(2602, 9259, 1), new WorldPoint(2602, 9261, 1), new WorldPoint(2600, 9261, 1),
			new WorldPoint(2599, 9262, 1), new WorldPoint(2597, 9262, 1), new WorldPoint(2597, 9261, 1), new WorldPoint(2595, 9261, 1),
			new WorldPoint(2595, 9260, 1), new WorldPoint(2594, 9260, 1), new WorldPoint(2594, 9259, 1), new WorldPoint(2592, 9259, 1),
			new WorldPoint(2592, 9262, 1), new WorldPoint(2593, 9262, 1), new WorldPoint(2593, 9263, 1), new WorldPoint(2595, 9263, 1),
			new WorldPoint(2595, 9265, 1)));

	private final int idStart;
	private final int idEnd;
	private final Zone startWp;
	private final Zone endWp;
	private final ChatMessageRequirement wrongWay;
	private final List<WorldPoint> path;
}