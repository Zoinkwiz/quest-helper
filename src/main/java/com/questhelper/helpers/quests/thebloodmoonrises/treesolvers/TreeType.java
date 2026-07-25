// SPDX-FileCopyrightText: 2026 pajlada <rasmus.karlsson@pajlada.com>
//
// SPDX-License-Identifier: BSD-2-Clause
package com.questhelper.helpers.quests.thebloodmoonrises.treesolvers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import net.runelite.api.gameval.ObjectID;

/// Groups of trees available in the Sangvestini forest during the "The Blood Moon Rises" quest
///
/// [OSRS wiki reference](https://oldschool.runescape.wiki/w/The_Blood_Moon_Rises#Over_the_river_and_through_the_woods)
@Getter
public enum TreeType
{
	Untouched(ObjectID.SOTFA_FOREST_DENSE_TREE, ObjectID.SOTFA_FOREST_DENSE_TREE02, ObjectID.SOTFA_FOREST_DENSE_TREE03),
	PartiallyChopped(ObjectID.SOTFA_FOREST_DENSE_TREE_CHOPPED, ObjectID.SOTFA_FOREST_DENSE_TREE_CHOPPED02, ObjectID.SOTFA_FOREST_DENSE_TREE_CHOPPED03),
	Stump(ObjectID.SOTFA_FOREST_DENSE_TREE_STUMP, ObjectID.SOTFA_FOREST_DENSE_TREE_STUMP02, ObjectID.SOTFA_FOREST_DENSE_TREE_STUMP03);

	private final List<Integer> objectIDs;

	TreeType(Integer... possibleObjectIDs)
	{
		this.objectIDs = new ArrayList<>();
		Collections.addAll(this.objectIDs, possibleObjectIDs);
	}
}
