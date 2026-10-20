package com.placeholderclickblocker;

import com.google.inject.Provides;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.ItemComposition;
import net.runelite.api.MenuEntry;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.MenuOpened;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.Keybind;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.HotkeyListener;

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

	/**
	 * True while the right-click menu is open, so clicks chosen from the menu are let through.
	 */
	private boolean menuOpened;

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
		menuOpened = false;
	}

	@Subscribe
	public void onMenuOpened(MenuOpened event)
	{
		menuOpened = true;
	}

	@Subscribe
	public void onClientTick(ClientTick event)
	{
		if (!client.isMenuOpen())
		{
			menuOpened = false;
		}
	}

	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded event)
	{
		if (!config.hideOptions() || allowedByShift())
		{
			return;
		}

		final MenuEntry entry = event.getMenuEntry();
		if (isPlaceholderEntry(entry))
		{
			client.getMenu().removeMenuEntry(entry);
		}
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		final boolean fromMenu = menuOpened;
		menuOpened = false;

		if (fromMenu || allowedByShift())
		{
			return;
		}

		if (isPlaceholderEntry(event.getMenuEntry()))
		{
			// Left-click on a placeholder: do nothing
			event.consume();
		}
	}

	private boolean allowedByShift()
	{
		return shiftHeld && config.disableWithShift();
	}

	private boolean isPlaceholderEntry(MenuEntry entry)
	{
		if (entry == null || entry.getParam1() != InterfaceID.Bankmain.ITEMS)
		{
			return false;
		}

		final Widget widget = entry.getWidget();
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
