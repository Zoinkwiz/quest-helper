package com.questhelper.steps;

import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.player.ShipInPortRequirement;
import com.questhelper.requirements.util.Port;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.zone.ZoneRequirement;
import lombok.Getter;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
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

		Requirement onShip = new VarbitRequirement(VarbitID.SAILING_PLAYER_IS_ON_PLAYER_BOAT, 1);

		Collection<Integer> cargoHoldIds = new HashSet<>();
		for (int i = ObjectID.SAILING_BOAT_CARGO_HOLD_REGULAR_RAFT; i <= ObjectID.SAILING_BOAT_CARGO_HOLD_ROSEWOOD_LARGE; i++)
		{
			cargoHoldIds.add(i);
		}

		DetailedQuestStep pickupCargo = new ObjectStep(questHelper, ObjectID.DOCK_LOADING_BAY_LEDGER_TABLE_WITHDRAW, fromPort.getLedgerTableLocation(), "Pickup the cargo from the ledger table on the docks of " + fromPort.getName() + ".");
		var placeCargoInHold = new ObjectStep(questHelper, ObjectID.SAILING_BOAT_CARGO_HOLD_REGULAR_RAFT, "Place the cargo in the cargo hold of your ship.");
		placeCargoInHold.addAlternateObjects(cargoHoldIds);
		// SAILING_BOARDED_BOAT is the type of boat slot boarded?
		// TODO: Container tracker for boats
		var boardShipWithCargo = new BoardShipStep(questHelper);
		SailStep sailToToPort = new SailStep(questHelper, toPort);
		ObjectStep pickupCargoFromHold = new ObjectStep(questHelper, ObjectID.SAILING_BOAT_CARGO_HOLD_REGULAR_RAFT, "Pickup the cargo from the cargo hold of your ship.");
		pickupCargoFromHold.addAlternateObjects(cargoHoldIds);
		DetailedQuestStep deliverCargo = new ObjectStep(questHelper, ObjectID.DOCK_LOADING_BAY_LEDGER_TABLE_DEPOSIT, toPort.getLedgerTableLocation(), "Deliver the cargo to the ledger table on the docks of " + toPort.getName() + ".");
		super.addStep(and(toPortReq, cargoForTaskTaken, holdingCargo), deliverCargo);
		super.addStep(and(toPortReq, notHoldingCargo, cargoForTaskTaken), pickupCargoFromHold);
		super.addStep(and(fromPortReq, notHoldingCargo, cargoForTaskTaken), sailToToPort);
		super.addStep(and(fromPortReq, cargoForTaskTaken, holdingCargo, onShip), placeCargoInHold);
		super.addStep(and(fromPortReq, cargoForTaskTaken, holdingCargo), boardShipWithCargo);
		super.addStep(fromPortReq, pickupCargo);

		this.stepsList.add(pickupCargo);
		this.stepsList.add(boardShipWithCargo);
		this.stepsList.add(placeCargoInHold);
		this.stepsList.add(sailToToPort);
		this.stepsList.add(pickupCargoFromHold);
		this.stepsList.add(deliverCargo);
	}
}
