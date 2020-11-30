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

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MM2AgilityNodes
{
	NODE0(new MM2Route[] { MM2Route.S0E3, MM2Route.S0E1, null, null }),
	NODE1(new MM2Route[] { MM2Route.S1E5, MM2Route.S1E2, null, MM2Route.S1E0 }),
	NODE2(new MM2Route[] { MM2Route.S2E6, null, null, MM2Route.S2E1 }),
	NODE3(new MM2Route[] { MM2Route.S3E7, null, MM2Route.S3E4, null }),
	NODE4(new MM2Route[] { MM2Route.S4E8, MM2Route.S4E5, null, MM2Route.S4E3 }),
	NODE5(new MM2Route[] { null, MM2Route.S5E6, MM2Route.S5E1, MM2Route.S5E4 }),
	NODE6(new MM2Route[] { MM2Route.S6E9, null, null, MM2Route.S6E5 }),
	NODE7(new MM2Route[] { MM2Route.S7E10, MM2Route.S7E8, null, null }),
	NODE8(new MM2Route[] { MM2Route.S8E10, MM2Route.S8E9, MM2Route.S8E4, MM2Route.S8E7 }),
	NODE9(new MM2Route[] { MM2Route.S9E10, null, null, MM2Route.S9E8 }),
	NODE10(new MM2Route[] { null, null, null, null });

	private final MM2Route[] paths;
}
