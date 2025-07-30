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

import com.questhelper.QuestHelperPlugin;
import com.questhelper.requirements.ManualRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.widget.WidgetModelRequirement;
import com.questhelper.requirements.widget.WidgetPresenceRequirement;
import com.questhelper.requirements.widget.WidgetTextRequirement;
import com.questhelper.steps.*;
import com.questhelper.steps.widget.WidgetDetails;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.annotations.Interface;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.FontManager;

import javax.inject.Inject;
import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import static com.questhelper.requirements.util.LogicHelper.and;
import static com.questhelper.requirements.util.LogicHelper.not;

@Slf4j
public class MetalDoorSolver extends DetailedOwnerStep
{
	private static final int STRIP_1_INDEX = 2;
	private static final int STRIP_2_INDEX = 1;
	private static final int STRIP_3_INDEX = 3;
	private static final int STRIP_4_INDEX = 0;
	/**
	 * Maps the relevant numbers from the Metal door puzzle to their corresponding rows.
	 * The first column is for strip 4
	 * The second column is for strip 2
	 * The third column is for strip 1
	 * The fourth column is for strip 3
	 */
	private static final int[][] CODE_MAPPING = new int[][]{
		{ // A
			7, 9, 6, 4,
		},
		{ // B
			5, 3, 1, 0,
		},
		{ // C
			2, 8, 6, 3,
		},
		{ // D
			0, 6, 4, 7,
		},
		{ // E
			4, 3, 6, 4,
		},
		{ // F
			2, 2, 1, 9,
		},
		{ // G
			2, 3, 2, 6,
		},
		{ // H
			4, 3, 8, 1,
		},
		{ // I
			9, 3, 0, 9,
		},
	};

	private static final @Interface int PUZZLE_GROUP_ID = 903;
	private static final int PUZZLE_BTN_UP_CHILD_ID = 21;
	private static final int PUZZLE_BTN_DOWN_CHILD_ID = 22;

	private static final int PUZZLE_NUMBER_0_MODEL_ID = 50567;
	private static final int PUZZLE_NUMBER_1_MODEL_ID = 50542;
	private static final int PUZZLE_NUMBER_2_MODEL_ID = 50540;
	private static final int PUZZLE_NUMBER_3_MODEL_ID = 50557;
	private static final int PUZZLE_NUMBER_4_MODEL_ID = 50562;
	private static final int PUZZLE_NUMBER_5_MODEL_ID = 50555;
	private static final int PUZZLE_NUMBER_6_MODEL_ID = 50568;
	private static final int PUZZLE_NUMBER_7_MODEL_ID = 50566;
	private static final int PUZZLE_NUMBER_8_MODEL_ID = 50575;
	private static final int PUZZLE_NUMBER_9_MODEL_ID = 50553;
	private static final int[] PUZZLE_NUMBERS = new int[]{PUZZLE_NUMBER_0_MODEL_ID, PUZZLE_NUMBER_1_MODEL_ID, PUZZLE_NUMBER_2_MODEL_ID, PUZZLE_NUMBER_3_MODEL_ID, PUZZLE_NUMBER_4_MODEL_ID, PUZZLE_NUMBER_5_MODEL_ID, PUZZLE_NUMBER_6_MODEL_ID, PUZZLE_NUMBER_7_MODEL_ID, PUZZLE_NUMBER_8_MODEL_ID, PUZZLE_NUMBER_9_MODEL_ID};

	private static final int PUZZLE_ENTER_CHILD_ID = 23;
	private static final int PUZZLE_BACK_CHILD_ID = 24;
	private static final int PUZZLE_PASSWORD_CURRENT_CHILD_ID = 25;
	private static final int PUZZLE_PASSWORD_1_CHILD_ID = 26;
	private static final int PUZZLE_PASSWORD_2_CHILD_ID = 27;
	private static final int PUZZLE_PASSWORD_3_CHILD_ID = 28;
	private static final int PUZZLE_PASSWORD_4_CHILD_ID = 29;

	private static final Pattern CODE_PATTERN = Pattern.compile("It reads ([A-I]{4}).");

	@Inject
	Client client;

	/**
	 * The code read from the Code key
	 */
	private String code = null;

	/**
	 * The final password to the metal door, calculating using the code
	 */
	private int[] doorPassword = null;

	private ItemStep readCode;
	private ObjectStep clickMetalDoors;
	private ConditionalStep solvePuzzle;
	private QuestStep solvePuzzleFallback;
	private WidgetTextRequirement firstNumberCorrect;
	private WidgetTextRequirement secondNumberCorrect;
	private WidgetTextRequirement thirdNumberCorrect;
	private WidgetTextRequirement fourthNumberCorrect;
	private WidgetModelRequirement inputFirstCorrect;
	private WidgetModelRequirement inputSecondCorrect;
	private WidgetModelRequirement inputThirdCorrect;
	private WidgetModelRequirement inputFourthCorrect;

	private int distanceUp = 69;
	private int distanceDown = 69;
	private ManualRequirement shouldClickDownInteadOfUp;

	public MetalDoorSolver(TheCurseOfArrav theCurseOfArrav)
	{
		super(theCurseOfArrav, "Solve the Metal door puzzle by following the instructions in the overlay.");
	}

	/**
	 * The numbers of the metal door puzzle are always the same, and in the same position.
	 * The decoder strips always keep the hole in the same position.
	 * Because of this, given a code, we can automatically figure out the final password.
	 * <p>
	 * STRIP 4
	 * |
	 * |   STRIP 2
	 * |   |
	 * |   |   STRIP 1
	 * |   |   |
	 * |   |   |   STRIP 3
	 * |   |   |   |
	 * V   V   V   V
	 * A 3 7 2 9 1 6 5 4 3
	 * B 6 5 4 3 2 1 9 0 4
	 * C 7 2 1 8 7 6 4 3 2
	 * D 9 0 7 6 5 4 3 7 1
	 * E 9 4 1 3 3 6 2 4 8
	 * F 6 2 3 2 4 1 6 9 7
	 * G 0 2 1 3 7 2 5 6 3
	 * H 9 4 6 3 8 8 0 1 9
	 * I 4 9 2 3 1 0 4 9 2
	 *
	 * @param code The code from the "Code key" (e.g. IFCB)
	 * @return 4 ints corresponding to the final password for the metal door
	 */
	public static int[] calculate(String code)
	{
		if (code == null || code.length() != 4)
		{
			return null;
		}

		// In case they throw us lowercase codes in some random patch
		code = code.toUpperCase();

		// The first char of the code (e.g. 'I' in "IFCB")
		var pos1 = code.charAt(0);
		// The second char of the code (e.g. 'F' in "IFCB")
		var pos2 = code.charAt(1);
		// The third char of the code (e.g. 'C' in "IFCB")
		var pos3 = code.charAt(2);
		// The fourth char of the code (e.g. 'B' in "IFCB")
		var pos4 = code.charAt(3);

		// Convert those chars to indexes in our `CODE_MAPPING` array
		var pos1RowIndex = pos1 - 'A';
		var pos2RowIndex = pos2 - 'A';
		var pos3RowIndex = pos3 - 'A';
		var pos4RowIndex = pos4 - 'A';

		// Ensure the characters we've received have correctly mapped to indexes we support
		if (
			pos1RowIndex < 0 || pos1RowIndex > 8
				|| pos2RowIndex < 0 || pos2RowIndex > 8
				|| pos3RowIndex < 0 || pos3RowIndex > 8
				|| pos4RowIndex < 0 || pos4RowIndex > 8
		)
		{
			return null;
		}

		var pos1Result = CODE_MAPPING[pos1RowIndex][STRIP_1_INDEX];
		var pos2Result = CODE_MAPPING[pos2RowIndex][STRIP_2_INDEX];
		var pos3Result = CODE_MAPPING[pos3RowIndex][STRIP_3_INDEX];
		var pos4Result = CODE_MAPPING[pos4RowIndex][STRIP_4_INDEX];
		return new int[]{
			pos1Result,
			pos2Result,
			pos3Result,
			pos4Result
		};
	}

	public static int calculateDistanceUp(int currentNumber, int targetNumber)
	{
		if (currentNumber == targetNumber)
		{
			return 0;
		}

		if (currentNumber > targetNumber)
		{
			return targetNumber - currentNumber + 10;
		}
		else
		{
			return targetNumber - currentNumber;
		}
	}

	public static int calculateDistanceDown(int currentNumber, int targetNumber)
	{
		if (currentNumber == targetNumber)
		{
			return 0;
		}

		if (currentNumber < targetNumber)
		{
			return currentNumber - targetNumber + 10;
		}
		else
		{
			return currentNumber - targetNumber;
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (this.code != null)
		{
			var currentNumberWidget = client.getWidget(PUZZLE_GROUP_ID, PUZZLE_PASSWORD_CURRENT_CHILD_ID);
			if (currentNumberWidget != null)
			{
				var currentNumberModel = currentNumberWidget.getModelId();
				var currentNumber = IntStream.range(0, PUZZLE_NUMBERS.length)
					.filter(i -> PUZZLE_NUMBERS[i] == currentNumberModel)
					.findFirst()
					.orElse(-1);

				var input1 = client.getWidget(PUZZLE_GROUP_ID, PUZZLE_PASSWORD_1_CHILD_ID);
				var input2 = client.getWidget(PUZZLE_GROUP_ID, PUZZLE_PASSWORD_2_CHILD_ID);
				var input3 = client.getWidget(PUZZLE_GROUP_ID, PUZZLE_PASSWORD_3_CHILD_ID);
				var input4 = client.getWidget(PUZZLE_GROUP_ID, PUZZLE_PASSWORD_4_CHILD_ID);

				if (input1 == null || input2 == null || input3 == null || input4 == null)
				{
					// something very wrong
					this.distanceUp = -1;
					this.distanceDown = -1;
					return;
				}

				var input1Text = input1.getText();
				var input2Text = input2.getText();
				var input3Text = input3.getText();
				var input4Text = input4.getText();

				var targetNumber = -1;

				if (Objects.equals(input1Text, "-") || Integer.parseInt(input1.getText()) != this.doorPassword[0])
				{
					targetNumber = this.doorPassword[0];
				}
				else if (Objects.equals(input2Text, "-") || Integer.parseInt(input2.getText()) != this.doorPassword[1])
				{
					targetNumber = this.doorPassword[1];
				}
				else if (Objects.equals(input3Text, "-") || Integer.parseInt(input3.getText()) != this.doorPassword[2])
				{
					targetNumber = this.doorPassword[2];
				}
				else if (Objects.equals(input4Text, "-") || Integer.parseInt(input4.getText()) != this.doorPassword[3])
				{
					targetNumber = this.doorPassword[3];
				}

				if (currentNumber == -1 || targetNumber == -1)
				{
					// something very wrong
					this.distanceUp = -1;
					this.distanceDown = -1;
				}
				else
				{
					this.distanceUp = calculateDistanceUp(currentNumber, targetNumber);
					this.distanceDown = calculateDistanceDown(currentNumber, targetNumber);

					this.shouldClickDownInteadOfUp.setShouldPass(this.distanceDown < this.distanceUp);
				}
			}
			return;
		}

		var textWidget = client.getWidget(InterfaceID.Messagebox.TEXT);
		if (textWidget == null)
		{
			return;
		}

		var matcher = CODE_PATTERN.matcher(textWidget.getText());
		if (matcher.find())
		{
			this.code = matcher.group(1);
			this.doorPassword = calculate(this.code);
			if (this.doorPassword != null)
			{
				firstNumberCorrect.setText(String.valueOf(this.doorPassword[0]));
				inputFirstCorrect.setId(PUZZLE_NUMBERS[this.doorPassword[0]]);
				secondNumberCorrect.setText(String.valueOf(this.doorPassword[1]));
				inputSecondCorrect.setId(PUZZLE_NUMBERS[this.doorPassword[1]]);
				thirdNumberCorrect.setText(String.valueOf(this.doorPassword[2]));
				inputThirdCorrect.setId(PUZZLE_NUMBERS[this.doorPassword[2]]);
				fourthNumberCorrect.setText(String.valueOf(this.doorPassword[3]));
				inputFourthCorrect.setId(PUZZLE_NUMBERS[this.doorPassword[3]]);
			}
			updateSteps();
		}

	}

	@Override
	public void startUp()
	{
		updateSteps();
	}

	@Override
	protected void setupSteps()
	{
		this.shouldClickDownInteadOfUp = new ManualRequirement();
		var codeKey = new ItemRequirement("Code key", ItemID.COA_CODE_KEY);
		readCode = new ItemStep(getQuestHelper(), "Read the Code key in your inventory.", codeKey.highlighted());

		clickMetalDoors = new ObjectStep(getQuestHelper(), ObjectID.MAH3_BASE_DOOR, new WorldPoint(3612, 4582, 0), "Open the metal doors and solve the puzzle.", codeKey);

		var puzzleWidgetOpen = new WidgetPresenceRequirement(PUZZLE_GROUP_ID, PUZZLE_BTN_UP_CHILD_ID);

		solvePuzzleFallback = new DetailedQuestStep(getQuestHelper(), "solve the puzzle somehow");

		var firstNumberEmpty = new WidgetTextRequirement(PUZZLE_GROUP_ID, PUZZLE_PASSWORD_1_CHILD_ID, "-");
		var secondNumberEmpty = new WidgetTextRequirement(PUZZLE_GROUP_ID, PUZZLE_PASSWORD_2_CHILD_ID, "-");
		var thirdNumberEmpty = new WidgetTextRequirement(PUZZLE_GROUP_ID, PUZZLE_PASSWORD_3_CHILD_ID, "-");
		var fourthNumberEmpty = new WidgetTextRequirement(PUZZLE_GROUP_ID, PUZZLE_PASSWORD_4_CHILD_ID, "-");
		firstNumberCorrect = new WidgetTextRequirement(PUZZLE_GROUP_ID, PUZZLE_PASSWORD_1_CHILD_ID, "X");
		secondNumberCorrect = new WidgetTextRequirement(PUZZLE_GROUP_ID, PUZZLE_PASSWORD_2_CHILD_ID, "X");
		thirdNumberCorrect = new WidgetTextRequirement(PUZZLE_GROUP_ID, PUZZLE_PASSWORD_3_CHILD_ID, "X");
		fourthNumberCorrect = new WidgetTextRequirement(PUZZLE_GROUP_ID, PUZZLE_PASSWORD_4_CHILD_ID, "X");

		inputFirstCorrect = new WidgetModelRequirement(PUZZLE_GROUP_ID, PUZZLE_PASSWORD_CURRENT_CHILD_ID, -1);
		inputSecondCorrect = new WidgetModelRequirement(PUZZLE_GROUP_ID, PUZZLE_PASSWORD_CURRENT_CHILD_ID, -1);
		inputThirdCorrect = new WidgetModelRequirement(PUZZLE_GROUP_ID, PUZZLE_PASSWORD_CURRENT_CHILD_ID, -1);
		inputFourthCorrect = new WidgetModelRequirement(PUZZLE_GROUP_ID, PUZZLE_PASSWORD_CURRENT_CHILD_ID, -1);

		var clickUp = new WidgetStep(getQuestHelper(), "Click the Up button.", new WidgetDetails(PUZZLE_GROUP_ID, PUZZLE_BTN_UP_CHILD_ID));
		clickUp.addExtraWidgetOverlayHintFunction(this::drawDistanceUp);
		var clickDown = new WidgetStep(getQuestHelper(), "Click the Down button.", new WidgetDetails(PUZZLE_GROUP_ID, PUZZLE_BTN_DOWN_CHILD_ID));
		clickDown.addExtraWidgetOverlayHintFunction(this::drawDistanceDown);
		var submitNumber = new WidgetStep(getQuestHelper(), "Click the Enter button.", new WidgetDetails(PUZZLE_GROUP_ID, PUZZLE_ENTER_CHILD_ID));
		var pressBack = new WidgetStep(getQuestHelper(), "Click the Back button.", new WidgetDetails(PUZZLE_GROUP_ID, PUZZLE_BACK_CHILD_ID));

		var clickUpOrDown = new ConditionalStep(getQuestHelper(), clickUp);
		clickUpOrDown.addStep(shouldClickDownInteadOfUp, clickDown);

		solvePuzzle = new ConditionalStep(getQuestHelper(), pressBack);
		solvePuzzle.addStep(not(puzzleWidgetOpen), clickMetalDoors);
		solvePuzzle.addStep(and(fourthNumberCorrect, thirdNumberCorrect, secondNumberCorrect, firstNumberCorrect), submitNumber);
		solvePuzzle.addStep(and(fourthNumberEmpty, inputFourthCorrect, firstNumberCorrect, secondNumberCorrect, thirdNumberCorrect), submitNumber);
		solvePuzzle.addStep(and(fourthNumberEmpty, firstNumberCorrect, secondNumberCorrect, thirdNumberCorrect), clickUpOrDown);
		solvePuzzle.addStep(and(thirdNumberEmpty, inputThirdCorrect, firstNumberCorrect, secondNumberCorrect), submitNumber);
		solvePuzzle.addStep(and(thirdNumberEmpty, firstNumberCorrect, secondNumberCorrect), clickUpOrDown);
		solvePuzzle.addStep(and(secondNumberEmpty, inputSecondCorrect, firstNumberCorrect), submitNumber);
		solvePuzzle.addStep(and(secondNumberEmpty, firstNumberCorrect), clickUpOrDown);
		solvePuzzle.addStep(and(firstNumberEmpty, inputFirstCorrect), submitNumber);
		solvePuzzle.addStep(firstNumberEmpty, clickUpOrDown);
	}

	public void drawDistanceUp(Graphics2D graphics, QuestHelperPlugin plugin)
	{
		var arrow = client.getWidget(PUZZLE_GROUP_ID, PUZZLE_BTN_DOWN_CHILD_ID);
		if (arrow == null)
		{
			return;
		}

		int widgetX = arrow.getCanvasLocation().getX() + (arrow.getWidth() / 2) - 30;
		int widgetY = arrow.getCanvasLocation().getY() + (arrow.getHeight() / 2) + 4;
		Font font = FontManager.getRunescapeFont().deriveFont(Font.BOLD, 16);
		graphics.setFont(font);
		graphics.drawString(Integer.toString(this.distanceUp), widgetX, widgetY);
	}

	public void drawDistanceDown(Graphics2D graphics, QuestHelperPlugin plugin)
	{
		var arrow = client.getWidget(PUZZLE_GROUP_ID, PUZZLE_BTN_DOWN_CHILD_ID);
		if (arrow == null)
		{
			return;
		}

		int widgetX = arrow.getCanvasLocation().getX() + (arrow.getWidth() / 2) - 30;
		int widgetY = arrow.getCanvasLocation().getY() + (arrow.getHeight() / 2) + 4;
		Font font = FontManager.getRunescapeFont().deriveFont(Font.BOLD, 16);
		graphics.setFont(font);
		graphics.drawString(Integer.toString(this.distanceDown), widgetX, widgetY);
	}

	protected void updateSteps()
	{
		if (this.code == null)
		{
			startUpStep(readCode);
			return;
		}

		if (this.doorPassword == null)
		{
			startUpStep(solvePuzzleFallback);
			return;
		}

		startUpStep(solvePuzzle);
	}

	@Override
	public List<QuestStep> getSteps()
	{
		return List.of(
			this.readCode,
			this.clickMetalDoors,
			this.solvePuzzleFallback,
			this.solvePuzzle
		);
	}
}
