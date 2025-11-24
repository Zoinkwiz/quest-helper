package com.questhelper.steps;

import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.player.ShipInPortRequirement;
import com.questhelper.requirements.util.Port;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.zone.ZoneRequirement;
import lombok.Getter;
import net.runelite.api.gameval.VarbitID;
import java.util.ArrayList;
import java.util.List;
import static com.questhelper.requirements.util.LogicHelper.and;
import static com.questhelper.requirements.util.LogicHelper.or;

public class PortTaskStep extends ConditionalStep
{

	@Getter
	private final List<QuestStep> stepsList = new ArrayList<>();

	public PortTaskStep(QuestHelper questHelper, Port fromPort, Port toPort, Requirement... requirements)
	{
		this(questHelper, fromPort, toPort, -1, requirements);
	}

	public PortTaskStep(QuestHelper questHelper, Port fromPort, Port toPort, int portTaskId, Requirement... requirements)
	{
		super(questHelper, new SailStep(questHelper, fromPort), requirements);
		stepsList.add(super.getStepsMap().get(null));
		ShipInPortRequirement fromPortReq = new ShipInPortRequirement(fromPort);
		ZoneRequirement toPortReq = new ZoneRequirement(toPort.getDockZone());

		Requirement cargoForTaskTaken = or(
			and(new VarbitRequirement(VarbitID.PORT_TASK_SLOT_0_ID, portTaskId), new VarbitRequirement(VarbitID.PORT_TASK_SLOT_0_CARGO_TAKEN, 1)),
			and(new VarbitRequirement(VarbitID.PORT_TASK_SLOT_1_ID, portTaskId), new VarbitRequirement(VarbitID.PORT_TASK_SLOT_1_CARGO_TAKEN, 1)),
			and(new VarbitRequirement(VarbitID.PORT_TASK_SLOT_2_ID, portTaskId), new VarbitRequirement(VarbitID.PORT_TASK_SLOT_2_CARGO_TAKEN, 1)),
			and(new VarbitRequirement(VarbitID.PORT_TASK_SLOT_3_ID, portTaskId), new VarbitRequirement(VarbitID.PORT_TASK_SLOT_3_CARGO_TAKEN, 1)),
			and(new VarbitRequirement(VarbitID.PORT_TASK_SLOT_4_ID, portTaskId), new VarbitRequirement(VarbitID.PORT_TASK_SLOT_4_CARGO_TAKEN, 1))
		);
		Requirement holdingCargo = new VarbitRequirement(VarbitID.SAILING_CARRYING_CARGO, 1);
		Requirement notHoldingCargo = new VarbitRequirement(VarbitID.SAILING_CARRYING_CARGO, 0);

		DetailedQuestStep pickupCargo = new DetailedQuestStep(questHelper, "Pickup the cargo");
		DetailedQuestStep placeCargoInHold = new DetailedQuestStep(questHelper, "Place the cargo in the cargo hold.");
		SailStep sailToToPort = new SailStep(questHelper, toPort);
		DetailedQuestStep pickupCargoFromHold = new DetailedQuestStep(questHelper, "Pickup the cargo from the cargo hold.");
		DetailedQuestStep deliverCargo = new DetailedQuestStep(questHelper, "Deliver the cargo.");
		super.addStep(and(toPortReq, cargoForTaskTaken, holdingCargo), deliverCargo);
		super.addStep(and(toPortReq, notHoldingCargo, cargoForTaskTaken), pickupCargoFromHold);
		super.addStep(and(fromPortReq, notHoldingCargo, cargoForTaskTaken), sailToToPort);
		super.addStep(and(fromPortReq, cargoForTaskTaken, holdingCargo), placeCargoInHold);
		super.addStep(fromPortReq, pickupCargo);

		this.stepsList.add(pickupCargo);
		this.stepsList.add(placeCargoInHold);
		this.stepsList.add(sailToToPort);
		this.stepsList.add(pickupCargoFromHold);
		this.stepsList.add(deliverCargo);
	}
}
