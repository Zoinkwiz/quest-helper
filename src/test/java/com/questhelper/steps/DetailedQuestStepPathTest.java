package com.questhelper.steps;

import com.questhelper.MockedTest;
import com.questhelper.questhelpers.QuestHelper;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.events.PluginMessage;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DetailedQuestStepPathTest extends MockedTest
{
	@Test
	void ordinaryQuestStepsKeepTheOriginalPathRequest()
	{
		QuestHelper helper = mock(QuestHelper.class);
		when(helper.getConfig()).thenReturn(questHelperConfig);
		when(questHelperConfig.useShortestPath()).thenReturn(true);
		Player player = mock(Player.class);
		WorldPoint start = new WorldPoint(3200, 3200, 0), target = new WorldPoint(3220, 3220, 0);
		when(player.getWorldLocation()).thenReturn(start);
		when(client.getLocalPlayer()).thenReturn(player);
		DetailedQuestStep step = new DetailedQuestStep(helper, target, "Walk to the target.");
		injector.injectMembers(step);
		clearInvocations(eventBus);
		step.setShortestPath();
		ArgumentCaptor<PluginMessage> message = ArgumentCaptor.forClass(PluginMessage.class);
		verify(eventBus).post(message.capture());
		assertEquals("shortestpath", message.getValue().getNamespace());
		assertEquals("path", message.getValue().getName());
		assertEquals(start, message.getValue().getData().get("start"));
		assertEquals(target, message.getValue().getData().get("target"));
		assertFalse(message.getValue().getData().containsKey("config"));
	}

	@Test
	void disablingAnActivePathClearsItsTemporaryOverrides()
	{
		QuestHelper helper = mock(QuestHelper.class);
		when(helper.getConfig()).thenReturn(questHelperConfig);
		when(questHelperConfig.useShortestPath()).thenReturn(true);
		DetailedQuestStep step = new DetailedQuestStep(helper, new WorldPoint(3220, 3220, 0), "Walk.");
		injector.injectMembers(step);
		step.started = true;
		clearInvocations(eventBus);
		step.configureShortestPath(false, Map.of("useTeleportationItems", "None"));
		step.setShortestPath();
		ArgumentCaptor<PluginMessage> message = ArgumentCaptor.forClass(PluginMessage.class);
		verify(eventBus).post(message.capture());
		assertEquals("clear", message.getValue().getName());
	}
}
