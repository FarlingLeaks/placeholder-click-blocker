package com.placeholderclickblocker;

import com.google.inject.Provides;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.ItemComposition;
import net.runelite.api.MenuEntry;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.HotkeyListener;
import net.runelite.client.config.Keybind;

@PluginDescriptor(
	name = "Placeholder Click Blocker",
	description = "Stops accidental clicks on bank placeholders",
	tags = {"bank", "placeholder", "misclick", "click", "block", "menu"}
)
public class PlaceholderClickBlockerPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private PlaceholderClickBlockerConfig config;

	@Inject
	private KeyManager keyManager;

	private boolean shiftHeld;

	private final HotkeyListener shiftListener = new HotkeyListener(() -> Keybind.SHIFT)
	{
		@Override
		public void hotkeyPressed()
		{
			shiftHeld = true;
		}

		@Override
		public void hotkeyReleased()
		{
			shiftHeld = false;
		}
	};

	@Override
	protected void startUp()
	{
		keyManager.registerKeyListener(shiftListener);
	}

	@Override
	protected void shutDown()
	{
		keyManager.unregisterKeyListener(shiftListener);
		shiftHeld = false;
	}

	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded event)
	{
		final MenuEntry entry = event.getMenuEntry();

		if (entry.getParam1() != InterfaceID.Bankmain.ITEMS)
		{
			return;
		}

		if (shiftHeld && config.disableWithShift())
		{
			return;
		}

		if (!isPlaceholder(entry.getWidget()))
		{
			return;
		}

		if (config.hideOptions())
		{
			client.getMenu().removeMenuEntry(entry);
		}
		else
		{
			// Deprioritized entries are sorted below "Cancel", so a left-click does nothing
			// while the options stay available in the right-click menu.
			entry.setDeprioritized(true);
		}
	}

	private boolean isPlaceholder(Widget widget)
	{
		if (widget == null || widget.getItemId() <= 0)
		{
			return false;
		}

		final ItemComposition composition = client.getItemDefinition(widget.getItemId());
		return composition.getPlaceholderTemplateId() != -1;
	}

	@Provides
	PlaceholderClickBlockerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(PlaceholderClickBlockerConfig.class);
	}
}
