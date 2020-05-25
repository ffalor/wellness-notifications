package com.wellness;

import com.google.inject.Provides;
import javax.inject.Inject;
import java.time.Instant;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.Notifier;

@Slf4j
@PluginDescriptor(
	name = "Wellness Notifications"
)
public class WellnessPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private WellnessConfig config;

	@Inject
	private Notifier notifier;

	private Instant lastNotifyTime;

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN && lastNotifyTime == null)
		{
			lastNotifyTime = Instant.now();
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		final Player local = client.getLocalPlayer();
		final Duration eyeDuration = Duration.ofMinutes(config.eyeinterval());
		
		if (config.eyenotify() && Instant.now().compareTo(lastNotifyTime.plus(eyeDuration)) >= 0) {
		 	notifier.notify(local.getName() + " it has been " + config.eyeinterval() + " minutes. Consider taking a small 20 second break from looking at the screen.");
		 	resetTimers();
		}
	}

	private void resetTimers()
	{
		lastNotifyTime = Instant.now();
	}

	@Provides
    WellnessConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(WellnessConfig.class);
	}
}
