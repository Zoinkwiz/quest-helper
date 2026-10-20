package com.questhelper.helpers.quests.animalmagnetism;

import com.questhelper.steps.PuzzleStep;
import com.questhelper.steps.widget.WidgetDetails;
import java.util.HashSet;

public class PuzzleSolver implements PuzzleStep.ButtonHighlighCalculator
{
	private final HashSet<WidgetDetails> highlights = new HashSet<>();

	public PuzzleSolver()
	{
		// Controls
		// TODO: gameval/interfaceid this
		highlights.add(new WidgetDetails(480, 26, 0));
		// Skip 2
		highlights.add(new WidgetDetails(480, 31, 0));
		highlights.add(new WidgetDetails(480, 34, 0));
		// Skip 5
		highlights.add(new WidgetDetails(480, 40, 0));
		highlights.add(new WidgetDetails(480, 43, 0));
		highlights.add(new WidgetDetails(480, 46, 0));
		// Skip 9
	}

	@Override
	public HashSet<WidgetDetails> getHighlightedButtons()
	{
		return highlights;
	}
}
