package com.questhelper.helpers.quests.thecurseofarrav.rubblesolvers;

import lombok.Getter;
import net.runelite.api.ObjectID;
import java.util.ArrayList;
import java.util.List;

@Getter
public enum RubbleType
{
	Three(ObjectID.RUBBLE_50603, ObjectID.RUBBLE_50604),
	Two(ObjectID.RUBBLE_50598, ObjectID.RUBBLE_50602),
	One(ObjectID.RUBBLE_50587, ObjectID.RUBBLE_50589, ObjectID.RUBBLE_50590, ObjectID.RUBBLE_50594, ObjectID.RUBBLE_50597);

	private final List<Integer> objectIDs;

	RubbleType(Integer... possibleObjectIDs) {
		this.objectIDs = new ArrayList<Integer>();
		for (var xd : possibleObjectIDs) {
			this.objectIDs.add(xd);
		}
		// Collections.addAll(this.objectIDs, possibleObjectIDs);
	}
}
