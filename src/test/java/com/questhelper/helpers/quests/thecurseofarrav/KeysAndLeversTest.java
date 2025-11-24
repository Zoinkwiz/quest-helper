/*
 * Copyright (c) 2024, pajlada <https://github.com/pajlada>
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
package com.questhelper.helpers.quests.thecurseofarrav;

import com.questhelper.MockedTest;
import com.questhelper.domain.AccountType;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.tools.QuestPerspective;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.VarbitID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class KeysAndLeversTest extends MockedTest
{
	private MockedStatic<QuestPerspective> questPerspectiveMockedStatic;
	private MockedStatic<WorldPoint> worldPointMockedStatic;
	private TheCurseOfArrav helper;

	@BeforeEach
	protected void preTest()
	{
		when(playerStateManager.getAccountType()).thenReturn(AccountType.NORMAL);

		var mockedPlayer = Mockito.mock(Player.class);
		when(client.getLocalPlayer()).thenReturn(mockedPlayer);
		var worldView = Mockito.mock(WorldView.class);
		when(mockedPlayer.getWorldView()).thenReturn(worldView);
		when(worldView.getId()).thenReturn(-1);

		questPerspectiveMockedStatic = Mockito.mockStatic(QuestPerspective.class);

		worldPointMockedStatic = Mockito.mockStatic(WorldPoint.class);

		helper = new TheCurseOfArrav();

	}

	@AfterEach
	protected void postTest()
	{
		questPerspectiveMockedStatic.close();
		worldPointMockedStatic.close();
	}

	private ConditionalStep init(WorldPoint playerLocation)
	{
		return this.init(playerLocation, null);
	}

	private ConditionalStep init(WorldPoint playerLocation, Item[] mockedItems)
	{
		worldPointMockedStatic.when(() -> WorldPoint.fromLocalInstance(any(), any()))
				.thenReturn(playerLocation);

		var mockedItemContainer = Mockito.mock(ItemContainer.class);
		if (mockedItems == null) mockedItems = new Item[0];
		when(mockedItemContainer.getItems()).thenReturn(mockedItems);
		when(client.getItemContainer(InventoryID.INV)).thenReturn(mockedItemContainer);

		when(client.getPlane()).thenReturn(0);

		var mockedScene = Mockito.mock(Scene.class);
		when(mockedScene.getTiles()).thenReturn(new Tile[][][]{
				{}
		});
		when(client.getScene()).thenReturn(mockedScene);

		this.questHelperPlugin.onItemContainerChanged(new ItemContainerChanged(InventoryID.INV, mockedItemContainer));

		this.injector.injectMembers(helper);
		helper.setInjector(injector);
		helper.setQuest(QuestHelperQuest.THE_CURSE_OF_ARRAV);
		helper.setQuestHelperPlugin(questHelperPlugin);
		helper.setConfig(questHelperConfig);
		helper.init();

		helper.startUp(questHelperConfig);
		var conditionalStep = helper.unlockImposingDoors;
		conditionalStep.startUp();
		return conditionalStep;
	}

	@Test
	void ensureOutsideTomb()
	{
		var conditionalStep = this.init(new WorldPoint(3305, 3037, 0));

		assertEquals(this.helper.enterTomb, conditionalStep.getActiveStep());
	}

	@Test
	void getFirstKey()
	{
		var conditionalStep = this.init(new WorldPoint(3845, 4547, 0));

		assertEquals(this.helper.getFirstKey, conditionalStep.getActiveStep());
	}

	@Test
	void getSecondKey()
	{
		var mockedItems = new Item[]{new Item(ItemID.COA_MASTABA_KEY_1, 1)};
		var conditionalStep = this.init(new WorldPoint(3845, 4547, 0), mockedItems);

		assertEquals(this.helper.getSecondKey, conditionalStep.getActiveStep());
	}

	@Test
	void getToSouthLever()
	{
		var mockedItems = new Item[]{
				new Item(ItemID.COA_MASTABA_KEY_1, 1),
				new Item(ItemID.COA_MASTABA_KEY_2, 1),
		};
		var conditionalStep = this.init(new WorldPoint(3845, 4547, 0), mockedItems);

		assertEquals(this.helper.getToSouthLever, conditionalStep.getActiveStep());
	}

	@Test
	void insertKeyIntoSouthLever()
	{
		var mockedItems = new Item[]{
				new Item(ItemID.COA_MASTABA_KEY_1, 1),
				new Item(ItemID.COA_MASTABA_KEY_2, 1),
		};
		when(client.getVarbitValue(VarbitID.COA_MASTABA_LEVER_2)).thenReturn(0);
		var conditionalStep = this.init(new WorldPoint(3893, 4552, 0), mockedItems);

		assertEquals(this.helper.pullSouthLever, conditionalStep.getActiveStep());
	}

	@Test
	void getToSouthLeverAfterInsertingKey1()
	{
		var mockedItems = new Item[]{
				new Item(ItemID.COA_MASTABA_KEY_1, 1),
		};
		when(client.getVarbitValue(VarbitID.COA_MASTABA_LEVER_2)).thenReturn(1);
		var conditionalStep = this.init(new WorldPoint(3845, 4547, 0), mockedItems);

		assertEquals(this.helper.getToSouthLever, conditionalStep.getActiveStep());
	}

	@Test
	void pullSouthLeverAfterInsertingKey1()
	{
		var mockedItems = new Item[]{
				new Item(ItemID.COA_MASTABA_KEY_1, 1),
		};
		when(client.getVarbitValue(VarbitID.COA_MASTABA_LEVER_2)).thenReturn(1);
		var conditionalStep = this.init(new WorldPoint(3893, 4552, 0), mockedItems);

		assertEquals(this.helper.pullSouthLever, conditionalStep.getActiveStep());
	}

	@Test
	void leaveSouthLeverAfterInsertingKey1()
	{
		var mockedItems = new Item[]{
				new Item(ItemID.COA_MASTABA_KEY_1, 1),
		};
		when(client.getVarbitValue(VarbitID.COA_MASTABA_LEVER_2)).thenReturn(2);
		var conditionalStep = this.init(new WorldPoint(3893, 4552, 0), mockedItems);

		assertEquals(this.helper.leaveSouthLever, conditionalStep.getActiveStep());
	}

	@Test
	void goToNorthLeverAfterPullingSouthLeverKey1()
	{
		var mockedItems = new Item[]{
				new Item(ItemID.COA_MASTABA_KEY_1, 1),
		};
		when(client.getVarbitValue(VarbitID.COA_MASTABA_LEVER_2)).thenReturn(2);
		var conditionalStep = this.init(new WorldPoint(3845, 4547, 0), mockedItems);

		assertEquals(this.helper.getToNorthLever, conditionalStep.getActiveStep());
	}

	@Test
	void insertKeyIntoNorthLeverAfterPullingSouthLeverKey1()
	{
		var mockedItems = new Item[]{
				new Item(ItemID.COA_MASTABA_KEY_1, 1),
		};
		when(client.getVarbitValue(VarbitID.COA_MASTABA_LEVER_2)).thenReturn(2);
		var conditionalStep = this.init(new WorldPoint(3894, 4597, 0), mockedItems);

		assertEquals(this.helper.pullNorthLever, conditionalStep.getActiveStep());
	}

	@Test
	void getToSouthLeverAfterInsertingKey()
	{
		var mockedItems = new Item[]{
		};
		when(client.getVarbitValue(VarbitID.COA_MASTABA_LEVER_2)).thenReturn(2);
		when(client.getVarbitValue(VarbitID.COA_MASTABA_LEVER_1)).thenReturn(1);
		var conditionalStep = this.init(new WorldPoint(3845, 4547, 0), mockedItems);

		assertEquals(this.helper.getToNorthLever, conditionalStep.getActiveStep());
	}

	@Test
	void pullNorthLeverAfterPullingSouthLeverKey1()
	{
		var mockedItems = new Item[]{
		};
		when(client.getVarbitValue(VarbitID.COA_MASTABA_LEVER_2)).thenReturn(2);
		when(client.getVarbitValue(VarbitID.COA_MASTABA_LEVER_1)).thenReturn(1);
		var conditionalStep = this.init(new WorldPoint(3894, 4597, 0), mockedItems);

		assertEquals(this.helper.pullNorthLever, conditionalStep.getActiveStep());
	}
}
