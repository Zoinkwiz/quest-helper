/*
 * Copyright (c) 2023, jLereback <https://github.com/jLereback>
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
package com.questhelper.helpers.skills.agility;

import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.Arrays;
import java.util.Collections;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;

public class Pollnivneach extends AgilityCourse
{
	QuestStep pollnivneachSidebar;
	QuestStep climbBasket, jumpUpMarketStall, grabBanner, leapGap, jumpUpTree, climbRoughWall, crossMonkeyBars, jumpUpTree2, jumpUpDryingLine;
	Zone marketStallZone, bannerZone, gapZone, firstTreeZone, roughWallZone, monkeyBarsZone, secondTreeZone, dryingLineZone;
	ZoneRequirement inMarketStallZone, inBannerZone, inGapZone, inFirstTreeZone, inRoughWallZone, inMonkeyBarsZone, inSecondTreeZone, inDryingLineZone;

	ConditionalStep pollnivneachStep;
	PanelDetails pollnivneachPanels;

	public Pollnivneach(QuestHelper questHelper)
	{
		super(questHelper);
	}

	@Override
	protected ConditionalStep loadStep()
	{
		setupZones();
		setupConditions();
		setupSteps();
		addSteps();

		return pollnivneachStep;
	}

	@Override
	protected void setupConditions()
	{
		inMarketStallZone = new ZoneRequirement(marketStallZone);
		inBannerZone = new ZoneRequirement(bannerZone);
		inGapZone = new ZoneRequirement(gapZone);
		inFirstTreeZone = new ZoneRequirement(firstTreeZone);
		inRoughWallZone = new ZoneRequirement(roughWallZone);
		inMonkeyBarsZone = new ZoneRequirement(monkeyBarsZone);
		inSecondTreeZone = new ZoneRequirement(secondTreeZone);
		inDryingLineZone = new ZoneRequirement(dryingLineZone);
	}

	@Override
	protected void setupZones()
	{
		marketStallZone = new Zone(new WorldPoint(3346, 2963, 1), new WorldPoint(3351, 2973, 2));
		bannerZone = new Zone(new WorldPoint(3352, 2973, 1), new WorldPoint(3359, 2979, 2));
		gapZone = new Zone(new WorldPoint(3360, 2977, 1), new WorldPoint(3365, 2976, 2));
		firstTreeZone = new Zone(new WorldPoint(3366, 2974, 1), new WorldPoint(3370, 2981, 2));
		roughWallZone = new Zone(new WorldPoint(3365, 2982, 1), new WorldPoint(3369, 2986, 1));
		monkeyBarsZone = new Zone(new WorldPoint(3355, 2980, 2), new WorldPoint(3365, 2989, 3));
		secondTreeZone = new Zone(new WorldPoint(3357, 2990, 2), new WorldPoint(3370, 2999, 3));
		dryingLineZone = new Zone(new WorldPoint(3356, 2999, 2), new WorldPoint(3365, 3004, 1));
	}

	@Override
	protected void setupSteps()
	{
		//Pollnivneach obstacles
		climbBasket = new ObjectStep(this.questHelper, ObjectID.BASKET_14935, new WorldPoint(3351, 2962, 0),
			"Climb onto the basket south of the Camel Store in Pollnivneach.", Collections.EMPTY_LIST, Arrays.asList(recommendedItems));

		jumpUpMarketStall = new ObjectStep(this.questHelper, ObjectID.MARKET_STALL_14936, new WorldPoint(3350, 2971, 1),
			"Jump over the market stall.", Collections.EMPTY_LIST, Arrays.asList(recommendedItems));

		grabBanner = new ObjectStep(this.questHelper, ObjectID.BANNER_14937, new WorldPoint(3357, 2979, 1),
			"Swing over the banner.", Collections.EMPTY_LIST, Arrays.asList(recommendedItems));

		leapGap = new ObjectStep(this.questHelper, ObjectID.GAP_14938, new WorldPoint(3364, 2977, 1),
			"Leap across the gap.", Collections.EMPTY_LIST, Arrays.asList(recommendedItems));

		jumpUpTree = new ObjectStep(this.questHelper, ObjectID.TREE_14939, new WorldPoint(3368, 2978, 1),
			"Jump over the tree.", Collections.EMPTY_LIST, Arrays.asList(recommendedItems));

		climbRoughWall = new ObjectStep(this.questHelper, ObjectID.ROUGH_WALL_14940, new WorldPoint(3365, 2982, 1),
			"CLimb up the rough wall.", Collections.EMPTY_LIST, Arrays.asList(recommendedItems));

		crossMonkeyBars = new ObjectStep(this.questHelper, ObjectID.MONKEYBARS, new WorldPoint(3358, 2985, 2),
			"Cross the monkey bars.", Collections.EMPTY_LIST, Arrays.asList(recommendedItems));

		jumpUpTree2 = new ObjectStep(this.questHelper, ObjectID.TREE_14944, new WorldPoint(3360, 2997, 2),
			"Jump over the tree.", Collections.EMPTY_LIST, Arrays.asList(recommendedItems));

		jumpUpDryingLine = new ObjectStep(this.questHelper, ObjectID.DRYING_LINE, new WorldPoint(3363, 3000, 2),
			"Climb down using the drying line.", Collections.EMPTY_LIST, Arrays.asList(recommendedItems));
	}

	@Override
	protected void addSteps()
	{
		//Conditional step to group up the obstacles
		pollnivneachStep = new ConditionalStep(this.questHelper, climbBasket);
		pollnivneachStep.addStep(new Conditions(inMarketStallZone), jumpUpMarketStall);
		pollnivneachStep.addStep(new Conditions(inBannerZone), grabBanner);
		pollnivneachStep.addStep(new Conditions(inGapZone), leapGap);
		pollnivneachStep.addStep(new Conditions(inFirstTreeZone), jumpUpTree);
		pollnivneachStep.addStep(new Conditions(inRoughWallZone), climbRoughWall);
		pollnivneachStep.addStep(new Conditions(inMonkeyBarsZone), crossMonkeyBars);
		pollnivneachStep.addStep(new Conditions(inSecondTreeZone), jumpUpTree2);
		pollnivneachStep.addStep(new Conditions(inDryingLineZone), jumpUpDryingLine);

		pollnivneachSidebar = new DetailedQuestStep(this.questHelper, "Train agility at the Pollnivneach Rooftop Course, starting outside the Camel Store.");
		pollnivneachSidebar.addSubSteps(climbBasket, jumpUpMarketStall, grabBanner, leapGap, jumpUpTree, climbRoughWall, crossMonkeyBars, jumpUpTree2, jumpUpDryingLine, pollnivneachStep);
	}

	@Override
	protected PanelDetails getPanelDetails()
	{
		pollnivneachPanels = new PanelDetails("70 - 80: Pollnivneach", Collections.singletonList(pollnivneachSidebar)
		);
		return pollnivneachPanels;
	}
}
