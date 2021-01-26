/*
 * Copyright (c) 2020, RobertDIV <https://github.com/RobertDIV>
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
package com.questhelper.quests.toweroflife;

import com.questhelper.steps.WidgetDetails;
import java.util.List;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;

import java.util.Arrays;
import java.util.HashSet;
import java.util.function.Function;

public class PuzzleSolver
{

	private Client client;

	public PuzzleSolver(Client client)
	{
		this.client = client;
	}


	//region Pressure Solver Vars
	//Controls
	private static final WidgetDetails PRSS_1_WHEEL_LEFT = new WidgetDetails(510, 128, 0);
	private static final WidgetDetails PRSS_1_WHEEL_RIGHT = new WidgetDetails(510, 129, 0);
	private static final WidgetDetails PRSS_2_WHEEL_LEFT = new WidgetDetails(510, 130, 0);
	private static final WidgetDetails PRSS_2_WHEEL_RIGHT = new WidgetDetails(510, 131, 0);
	private static final WidgetDetails PRSS_3_WHEEL_LEFT = new WidgetDetails(510, 132, 0);
	private static final WidgetDetails PRSS_3_WHEEL_RIGHT = new WidgetDetails(510, 133, 0);
	private static final WidgetDetails PRSS_4_WHEEL_LEFT = new WidgetDetails(510, 134, 0);
	private static final WidgetDetails PRSS_4_WHEEL_RIGHT = new WidgetDetails(510, 135, 0);
	private static final WidgetDetails PRSS_LEVER_LEFT = new WidgetDetails(510, 136, 0);
	private static final WidgetDetails PRSS_LEVER_RIGHT = new WidgetDetails(510, 141, 0);
	//State Varbits
	private static final int PRSS_VBIT_LL = 3356;
	private static final int PRSS_VBIT_LR = 3357;
	private static final int PRSS_VBIT_P1 = 3347;
	private static final int PRSS_VBIT_B1 = 3351;
	private static final int PRSS_VBIT_P2 = 3348;
	private static final int PRSS_VBIT_B2 = 3352;
	private static final int PRSS_VBIT_P3 = 3349;
	private static final int PRSS_VBIT_B3 = 3353;
	private static final int PRSS_VBIT_P4 = 3350;
	private static final int PRSS_VBIT_B4 = 3355;
	//booleans
	private boolean prss_3_passed = false;
	private boolean prss_4_passed = false;

	//endregion
	public HashSet<WidgetDetails> pressureSolver()
	{
		HashSet<WidgetDetails> highlights = new HashSet<>();

		int ball1Pos = client.getVarbitValue(PRSS_VBIT_B1);
		boolean prss_1_locked = client.getVarbitValue(PRSS_VBIT_LL) != 0;
		boolean prss_1_plugged = client.getVarbitValue(PRSS_VBIT_P1) == 1;
		boolean prss_1_filled = (ball1Pos == 5);
		if (!prss_1_filled)
		{
			if (prss_1_locked)
			{
				highlights.add(PRSS_LEVER_LEFT);
			}
			else if (!prss_1_plugged)
			{
				highlights.add(PRSS_1_WHEEL_LEFT);
			}
			else
			{
				highlights.add(PRSS_1_WHEEL_RIGHT);
			}
			return highlights;
		}

		int ball2Pos = client.getVarbitValue(PRSS_VBIT_B2);
		boolean prss_2_locked = client.getVarbitValue(PRSS_VBIT_LL) != 1;
		boolean prss_2_plugged = client.getVarbitValue(PRSS_VBIT_P2) == 1;
		boolean prss_2_filled = (ball2Pos == 5);
		if (!prss_2_filled)
		{
			if (prss_2_locked)
			{
				highlights.add(PRSS_LEVER_LEFT);
			}
			else if (!prss_2_plugged)
			{
				highlights.add(PRSS_2_WHEEL_LEFT);
			}
			else
			{
				highlights.add(PRSS_2_WHEEL_RIGHT);
			}
			return highlights;
		}

		int ball3Pos = client.getVarbitValue(PRSS_VBIT_B3);
		boolean prss_3_locked = client.getVarbitValue(PRSS_VBIT_LR) != 0;
		boolean prss_3_plugged = client.getVarbitValue(PRSS_VBIT_P3) == 1;
		boolean prss_3_filled = (ball3Pos == 5);
		if (!prss_3_filled)
		{
			if (prss_3_locked)
			{
				highlights.add(PRSS_LEVER_RIGHT);
			}
			else if (!prss_3_plugged && ball3Pos >= 3)
			{ //Go past the hole
				highlights.add(PRSS_3_WHEEL_LEFT);
			}
			else if (!prss_3_plugged)
			{ //go back to plug the hole
				highlights.add(PRSS_3_WHEEL_RIGHT);
			}
			else
			{
				highlights.add(PRSS_3_WHEEL_RIGHT);
			}

			return highlights;
		}

		int ball4Pos = client.getVarbitValue(PRSS_VBIT_B4);
		boolean prss_4_locked = client.getVarbitValue(PRSS_VBIT_LR) != 1;
		boolean prss_4_plugged = client.getVarbitValue(PRSS_VBIT_P4) == 1;
		boolean prss_4_filled = (ball4Pos == 5);
		if (!prss_4_filled)
		{
			if (prss_4_locked)
			{
				highlights.add(PRSS_LEVER_RIGHT);
			}
			else if (!prss_4_plugged && ball4Pos >= 4)
			{
				highlights.add(PRSS_4_WHEEL_LEFT);
			}
			else if (!prss_4_plugged)
			{
				highlights.add(PRSS_4_WHEEL_RIGHT);
			}
			else
			{
				highlights.add(PRSS_4_WHEEL_RIGHT);
			}

			return highlights;
		}

		return highlights;
	}

	//region Pipe Solver Vars
	//Controls
	private static final WidgetDetails PIPE_CTRL_ROTATE = new WidgetDetails(511, 15, 0);
	private static final WidgetDetails PIPE_CTRL_LEFT = new WidgetDetails(511, 17, 0);
	private static final WidgetDetails PIPE_CTRL_RIGHT = new WidgetDetails(511, 19, 0);
	private static final WidgetDetails PIPE_CTRL_UP = new WidgetDetails(511, 21, 0);
	private static final WidgetDetails PIPE_CTRL_DOWN = new WidgetDetails(511, 23, 0);
	private static final WidgetDetails PIPE_PCE_1 = new WidgetDetails(511, 9, 0); // 5 Piece
	private static final WidgetDetails PIPE_PCE_2 = new WidgetDetails(511, 10, 0); // F piece
	private static final WidgetDetails PIPE_PCE_3 = new WidgetDetails(511, 11, 0); // T Piece
	private static final WidgetDetails PIPE_PCE_4 = new WidgetDetails(511, 12, 0); // Big bend
	private static final WidgetDetails PIPE_PCE_5 = new WidgetDetails(511, 13, 0); // Small bend
	//Varbits
	private static final int PIPE_VBIT_1_SELECT = 3343;
	private static final int PIPE_VBIT_2_SELECT = 3341;
	private static final int PIPE_VBIT_3_SELECT = 3344;
	private static final int PIPE_VBIT_4_SELECT = 3345;
	private static final int PIPE_VBIT_5_SELECT = 3342;
	private static final int PIPE_PCE_1_ORIENTATION = 22547;
	private static final int PIPE_PCE_2_ORIENTATION = 22531;
	private static final int PIPE_PCE_3_ORIENTATION = 22555;
	private static final int PIPE_PCE_4_ORIENTATION = 22562;
	private static final int PIPE_PCE_5_ORIENTATION = 22539;
	private List<PipeSolverSolution> pipeSolverSolutions;

	//endregion
	public HashSet<WidgetDetails> pipeSolver()
	{
		try
		{
			//Create if null
			if (pipeSolverSolutions == null)
			{
				pipeSolverSolutions = Arrays.asList(
					new PipeSolverSolution(PIPE_PCE_1, 159, 69, PIPE_PCE_1_ORIENTATION, PIPE_VBIT_1_SELECT),
					new PipeSolverSolution(PIPE_PCE_2, 83, 80, PIPE_PCE_2_ORIENTATION, PIPE_VBIT_2_SELECT),
					new PipeSolverSolution(PIPE_PCE_3, 256, 60, PIPE_PCE_3_ORIENTATION, PIPE_VBIT_3_SELECT),
					new PipeSolverSolution(PIPE_PCE_4, 237, 155, PIPE_PCE_4_ORIENTATION, PIPE_VBIT_4_SELECT),
					new PipeSolverSolution(PIPE_PCE_5, 126, 64, PIPE_PCE_5_ORIENTATION, PIPE_VBIT_5_SELECT)
				);
			}

			HashSet<WidgetDetails> highlights = new HashSet<>();

			for (PipeSolverSolution soln : pipeSolverSolutions)
			{
				if (soln.isSelected())
				{
					if (!soln.isSelected())
					{
						highlights.add(soln.pieceInfo);
					}
					else if (!soln.isOriented())
					{ //Rotate
						highlights.add(PIPE_CTRL_ROTATE);
					}
					else if (soln.isRight())
					{ //Align horizontal
						highlights.add(PIPE_CTRL_LEFT);
					}
					else if (soln.isLeft())
					{ //Align horizontal
						highlights.add(PIPE_CTRL_RIGHT);
					}
					else if (soln.isBelow())
					{ //Align vertical
						highlights.add(PIPE_CTRL_UP);
					}
					else if (soln.isAbove())
					{ //Align vertical
						highlights.add(PIPE_CTRL_DOWN);
					}
					return highlights;
				}
			}

			return highlights;
		}
		catch (WidgetNotFoundException e)
		{
			return new HashSet<>();
		}
	}

	private class PipeSolverSolution
	{

		private final WidgetDetails pieceInfo;
		private final Widget piece;
		private final int correctOrientation, selectedVBit, targetX, targetY;

		public PipeSolverSolution(WidgetDetails piece, int targetX, int targetY, int correctOrientation, int selectedVBit)
		{
			this(piece, targetX, targetY, correctOrientation, selectedVBit, Widget::getOriginalX, Widget::getOriginalY);
		}

		public PipeSolverSolution(WidgetDetails pieceDetails, int targetX, int targetY, int correctOrientation, int selectedVBit, Function<Widget, Integer> getPieceX, Function<Widget, Integer> getPieceY)
		{
			this.pieceInfo = pieceDetails;
			this.piece = PuzzleSolver.this.getWidget(pieceInfo);
			if (this.piece == null)
				throw new WidgetNotFoundException();
			this.targetX = targetX;
			this.targetY = targetY;
			this.correctOrientation = correctOrientation;
			this.selectedVBit = selectedVBit;
		}

		public boolean isSolved()
		{
			return !isSelected();
		}

		public boolean isSelected()
		{
			return PuzzleSolver.this.client.getVarbitValue(selectedVBit) == 1;
		}

		public boolean isLeft()
		{
			return piece.getOriginalX() < targetX - 4;
		}

		public boolean isRight()
		{
			return piece.getOriginalX() > targetX + 4;
		}

		public boolean isAbove()
		{
			return piece.getOriginalY() < targetY - 4;
		}

		public boolean isBelow()
		{
			return piece.getOriginalY() > targetY + 4;
		}

		public boolean isOriented()
		{
			return piece.getModelId() == correctOrientation;
		}

	}


	//region Cage Solver Vars
	private static final WidgetDetails CAGE_CTRL_LEFT = new WidgetDetails(509, 26, 0);
	private static final WidgetDetails CAGE_CTRL_RIGHT = new WidgetDetails(509, 29, 0);
	private static final WidgetDetails CAGE_CTRL_PLACE = new WidgetDetails(509, 32, 0);
	private static final WidgetDetails CAGE_CTRL_HORIZ = new WidgetDetails(509, 41, 0);
	private static final WidgetDetails CAGE_CTRL_VERT = new WidgetDetails(509, 44, 0);
	private static final WidgetDetails CAGE_CTRL_MINUS = new WidgetDetails(509, 49, 0);
	private static final WidgetDetails CAGE_CTRL_PLUS = new WidgetDetails(509, 52, 0);
	private static final WidgetDetails CAGE_INFO_SIZE = new WidgetDetails(509, 56, 0);
	private static final WidgetDetails CAGE_INFO_HORIZ = new WidgetDetails(509, 38, 0);
	private static final WidgetDetails CAGE_INFO_VERT = new WidgetDetails(509, 40, 0);
	private static final WidgetDetails CAGE_INFO_SIDE_1 = new WidgetDetails(509, 5, 0);
	private static final WidgetDetails CAGE_INFO_SIDE_2 = new WidgetDetails(509, 10, 0);
	private static final WidgetDetails CAGE_INFO_SIDE_3 = new WidgetDetails(509, 15, 0);
	private static final WidgetDetails CAGE_INFO_SIDE_4 = new WidgetDetails(509, 20, 0);
	private static final WidgetDetails CAGE_INFO_S1_P1 = new WidgetDetails(509, 7, 0);
	private static final WidgetDetails CAGE_INFO_S1_P2 = new WidgetDetails(509, 8, 0);
	private static final WidgetDetails CAGE_INFO_S1_P3 = new WidgetDetails(509, 9, 0);
	private static final WidgetDetails CAGE_INFO_S2_P1 = new WidgetDetails(509, 12, 0);
	private static final WidgetDetails CAGE_INFO_S2_P2 = new WidgetDetails(509, 13, 0);
	private static final WidgetDetails CAGE_INFO_S2_P3 = new WidgetDetails(509, 14, 0);
	private static final WidgetDetails CAGE_INFO_S3_P1 = new WidgetDetails(509, 17, 0);
	private static final WidgetDetails CAGE_INFO_S3_P2 = new WidgetDetails(509, 18, 0);
	private static final WidgetDetails CAGE_INFO_S3_P3 = new WidgetDetails(509, 19, 0);
	private static final WidgetDetails CAGE_INFO_S4_P1 = new WidgetDetails(509, 22, 0);
	private static final WidgetDetails CAGE_INFO_S4_P2 = new WidgetDetails(509, 23, 0);
	private static final WidgetDetails CAGE_INFO_S4_P3 = new WidgetDetails(509, 24, 0);
	//Model IDs
	private static final int CAGE_S1_P1_SOLVED = 22469;
	private static final int CAGE_S1_P2_SOLVED = 22472;
	private static final int CAGE_S1_P3_SOLVED = 22475;
	private static final int CAGE_S2_P1_SOLVED = 22480;
	private static final int CAGE_S2_P2_SOLVED = 22483;
	private static final int CAGE_S2_P3_SOLVED = 22486;
	private static final int CAGE_S3_P1_SOLVED = 22491;
	private static final int CAGE_S3_P2_SOLVED = 22494;
	private static final int CAGE_S3_P3_SOLVED = 22497;
	private static final int CAGE_S4_P1_SOLVED = 22502;
	private static final int CAGE_S4_P2_SOLVED = 22505;
	private static final int CAGE_S4_P3_SOLVED = 22508;

	private final HashSet<CageSideSolution> cage_solutions = new HashSet<>(Arrays.asList(
		new CageSideSolution(CAGE_INFO_SIDE_1,
			new CageBarSolution(CAGE_INFO_S1_P1, true, 2, CAGE_S1_P1_SOLVED),
			new CageBarSolution(CAGE_INFO_S1_P2, true, 2, CAGE_S1_P2_SOLVED),
			new CageBarSolution(CAGE_INFO_S1_P3, false, 2, CAGE_S1_P3_SOLVED)
		),
		new CageSideSolution(CAGE_INFO_SIDE_2,
			new CageBarSolution(CAGE_INFO_S2_P1, false, 2, CAGE_S2_P1_SOLVED),
			new CageBarSolution(CAGE_INFO_S2_P2, true, 3, CAGE_S2_P2_SOLVED),
			new CageBarSolution(CAGE_INFO_S2_P3, true, 2, CAGE_S2_P3_SOLVED)
		),
		new CageSideSolution(CAGE_INFO_SIDE_3,
			new CageBarSolution(CAGE_INFO_S3_P1, false, 2, CAGE_S3_P1_SOLVED),
			new CageBarSolution(CAGE_INFO_S3_P2, false, 4, CAGE_S3_P2_SOLVED),
			new CageBarSolution(CAGE_INFO_S3_P3, true, 2, CAGE_S3_P3_SOLVED)
		),
		new CageSideSolution(CAGE_INFO_SIDE_4,
			new CageBarSolution(CAGE_INFO_S4_P1, true, 4, CAGE_S4_P1_SOLVED),
			new CageBarSolution(CAGE_INFO_S4_P2, false, 2, CAGE_S4_P2_SOLVED),
			new CageBarSolution(CAGE_INFO_S4_P3, false, 3, CAGE_S4_P3_SOLVED)
		)
	));

	//endregion
	public HashSet<WidgetDetails> cageSolver()
	{
		HashSet<WidgetDetails> highlights = new HashSet<>();

		for (CageSideSolution side : cage_solutions)
		{
			if (side.isVisible())
			{
				if (side.isSolved())
				{
					//If solved move on
					highlights.add(CAGE_CTRL_RIGHT);
					break;
				}

				CageBarSolution curBar = side.getNext();
				if (!curBar.isCorrectOrientSelected())
				{
					if (curBar.isHorizontal)
					{
						highlights.add(CAGE_CTRL_HORIZ);
					}
					else
					{
						highlights.add(CAGE_CTRL_VERT);
					}
				}
				else if (curBar.sizeTooBig())
				{
					highlights.add(CAGE_CTRL_MINUS);
				}
				else if (curBar.sizeTooSmall())
				{
					highlights.add(CAGE_CTRL_PLUS);
				}
				else
				{
					highlights.add(CAGE_CTRL_PLACE);
				}

				break;
			}
		}

		return highlights;
	}

	private class CageSideSolution
	{
		WidgetDetails side;
		CageBarSolution p1, p2, p3;

		public CageSideSolution(WidgetDetails side, CageBarSolution p1, CageBarSolution p2, CageBarSolution p3)
		{
			this.side = side;
			this.p1 = p1;
			this.p2 = p2;
			this.p3 = p3;
		}

		public boolean isVisible()
		{
			Widget w = getWidget(side);
			return w != null && !w.isHidden();
		}

		public boolean isSolved()
		{
			return isVisible() && p1.isSolved() && p2.isSolved() && p3.isSolved();
		}

		public CageBarSolution getNext()
		{
			if (!p1.isSolved())
				return p1;
			else if (!p2.isSolved())
				return p2;
			else
				return p3;
		}
	}

	private class CageBarSolution
	{
		WidgetDetails target;
		boolean isHorizontal;
		int size;
		int solvedModel;

		public CageBarSolution(WidgetDetails target, boolean isHorizontal, int size, int solvedModelId)
		{
			this.target = target;
			this.isHorizontal = isHorizontal;
			this.size = size;
			this.solvedModel = solvedModelId;
		}

		public boolean isSolved()
		{
			return getWidget(target).getModelId() == solvedModel;
		}

		public boolean isCorrectOrientSelected()
		{
			if (isHorizontal)
			{
				Widget w = getWidget(CAGE_INFO_HORIZ);
				return w != null && !w.isHidden();
			}
			else
			{
				Widget w = getWidget(CAGE_INFO_VERT);
				return w != null && !w.isHidden();
			}
		}

		public boolean sizeTooSmall()
		{
			int curSize = Integer.parseInt(getWidget(CAGE_INFO_SIZE).getText());
			return curSize < size;
		}

		public boolean sizeTooBig()
		{
			int curSize = Integer.parseInt(getWidget(CAGE_INFO_SIZE).getText());
			return curSize > size;
		}
	}


	protected Widget getWidget(WidgetDetails wi)
	{
		return client.getWidget(wi.getGroupID(), wi.getChildID());
	}

	private static class WidgetNotFoundException extends RuntimeException
	{
		public WidgetNotFoundException()
		{
		}
	}
}
