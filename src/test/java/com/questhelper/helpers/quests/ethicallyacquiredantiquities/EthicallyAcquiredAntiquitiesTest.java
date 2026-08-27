package com.questhelper.helpers.quests.ethicallyacquiredantiquities;

import com.questhelper.MockedTest;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.steps.NpcStep;
import net.runelite.api.gameval.NpcID;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EthicallyAcquiredAntiquitiesTest extends MockedTest
{
	private EthicallyAcquiredAntiquities setupHelper()
	{
		var quest = QuestHelperQuest.ETHICALLY_ACQUIRED_ANTIQUITIES;
		var helper = (EthicallyAcquiredAntiquities) quest.getQuestHelper();
		helper.setQuest(quest);
		this.injector.injectMembers(helper);
		helper.setQuestHelperPlugin(questHelperPlugin);
		helper.setConfig(questHelperConfig);
		helper.init();

		return helper;
	}

	@Test
	void ensurePortSarimStepHighlightsBothTraderStanAndCrewmembers()
	{
		var helper = setupHelper();

		var ids = ((NpcStep) helper.talkToTraderStan).allIds();
		assertTrue(ids.contains(NpcID.SAILING_TRANSPORT_TRADER_STAN_PORTSARIM),
			"Trader Stan should be highlighted, as talking to him progresses the quest");
		assertTrue(ids.contains(NpcID.SAILING_TRANSPORT_TRADER_STAN_CREW_MAN1_PORTSARIM),
			"Trader Crewmembers should be highlighted, as talking to them progresses the quest too");
	}

	@Test
	void ensureCharterCoinsAreRecommendedRatherThanRequired()
	{
		var helper = setupHelper();

		assertFalse(helper.getItemRequirements().contains(helper.coinsForCharter),
			"Coins are only needed if you charter a ship to Port Sarim, so they must not be required");
		assertTrue(helper.getItemRecommended().contains(helper.coinsForCharter),
			"Coins should be recommended for the charter ship to Port Sarim");
	}
}
