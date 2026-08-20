package com.questhelper.requirements.player;

import com.questhelper.MockedTest;
import net.runelite.api.Skill;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class SkillRequirementTest extends MockedTest
{
	void initFishing(int realLevel, int boostedLevel) {
		when(client.getRealSkillLevel(Skill.FISHING)).thenReturn(realLevel);
		when(client.getBoostedSkillLevel(Skill.FISHING)).thenReturn(boostedLevel);
	}

	@Test
	void unboostable() {
		var req = new SkillRequirement(Skill.FISHING, 60);

		initFishing(60, 60);
		assertEquals(SkillRequirement.BoostStatus.Pass, req.checkBoosted(client, questHelperConfig));
		assertTrue(req.check(client));

		// User has temporarily lost some stats
		initFishing(60, 59);
		assertEquals(SkillRequirement.BoostStatus.Pass, req.checkBoosted(client, questHelperConfig));
		assertTrue(req.check(client));

		initFishing(59, 59);
		assertEquals(SkillRequirement.BoostStatus.Fail, req.checkBoosted(client, questHelperConfig));
		assertFalse(req.check(client));

		// User has boosted, but this requirement doesn't allow boosting
		initFishing(59, 61);
		assertEquals(SkillRequirement.BoostStatus.Fail, req.checkBoosted(client, questHelperConfig));
		assertFalse(req.check(client));
	}

	@Test
	void boostable() {
		var req = new SkillRequirement(Skill.FISHING, 61, true);

		// User is outside of boost range
		initFishing(54, 54);
		assertEquals(SkillRequirement.BoostStatus.Fail, req.checkBoosted(client, questHelperConfig));

		// User is outside of boost range
		initFishing(54, 59);
		assertEquals(SkillRequirement.BoostStatus.Fail, req.checkBoosted(client, questHelperConfig));

		// User can boost
		initFishing(57, 57);
		assertEquals(SkillRequirement.BoostStatus.CanPassWithBoost, req.checkBoosted(client, questHelperConfig));

		// User has already boosted
		initFishing(57, 62);
		assertEquals(SkillRequirement.BoostStatus.Pass, req.checkBoosted(client, questHelperConfig));
	}
}
