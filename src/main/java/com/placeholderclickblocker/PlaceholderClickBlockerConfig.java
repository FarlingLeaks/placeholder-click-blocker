package com.placeholderclickblocker;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(PlaceholderClickBlockerConfig.GROUP)
public interface PlaceholderClickBlockerConfig extends Config
{
	String GROUP = "placeholderclickblocker";

	@ConfigItem(
		keyName = "hideOptions",
		name = "Hide right-click options",
		description = "Also remove the placeholder's options (Release, Examine, etc.) from the right-click menu.<br>"
			+ "When off, left-clicking a placeholder does nothing, but its options are still in the right-click menu.",
		position = 1
	)
	default boolean hideOptions()
	{
		return false;
	}

	@ConfigItem(
		keyName = "disableWithShift",
		name = "Allow clicks while holding Shift",
		description = "Placeholders work normally while Shift is held down",
		position = 2
	)
	default boolean disableWithShift()
	{
		return true;
	}
}
