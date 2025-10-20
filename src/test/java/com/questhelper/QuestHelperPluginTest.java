package com.questhelper;

import com.questhelper.tools.StepCopyPlugin;
import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class QuestHelperPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(QuestHelperPlugin.class);
//		ExternalPluginManager.loadBuiltin(StepCopyPlugin.class);
		RuneLite.main(args);
	}
}
