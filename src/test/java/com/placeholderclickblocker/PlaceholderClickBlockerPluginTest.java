package com.placeholderclickblocker;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class PlaceholderClickBlockerPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(PlaceholderClickBlockerPlugin.class);
		RuneLite.main(args);
	}
}
