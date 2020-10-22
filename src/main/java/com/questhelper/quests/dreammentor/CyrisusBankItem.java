/*
 * Copyright (c) 2020, Zoinkwiz
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
package com.questhelper.quests.dreammentor;

import lombok.Getter;

enum CyrisusBankItem
{
	DRAGON_CHAINBODY("Dragon chainbody", 3140, 1),
	AHRIM_ROBETOP("Ahrim's robetop", 2, 4712),
	KARILS_TOP("Karil's leathertop", 3, 4736),
	DRAGON_MED_HELM("Dragon med helm", 15, 1149),
	SPLITBARK_HELM("Splitbark helm", 16, 3385),
	ROBIN_HOOD("Robin hood hat", 17, 2581),
	ABYSSAL_WHIP("Abyssal whip", 19, 4151),
	ANCIENT_STAFF("Ancient staff", 20, 4675),
	MAGIC_SHORTBOW("Magic shortbown", 21, 861),
	TORAG_LEG("Torag's platelegs", 26, 4970),
	AHRIM_SKIRT("Ahrim's robeskirt", 27, 4714),
	BLACK_CHAPS("Black d'hide chaps", 28, 2497),
	INFINITY_BOOTS("Infinity boots", 29, 6920),
	RANGER_BOOTS("Ranger boots", 30, 2577),
	ADAMANT_BOOTS("Adamant boots", 31, 4129);


	@Getter
	private final String name;

	@Getter
	private final int varbitID;

	@Getter
	private final int widgetID;


	CyrisusBankItem(String name, int varbitID, int widgetID)
	{
		this.name = name;
		this.varbitID = varbitID;
		this.widgetID = widgetID;
	}
}

