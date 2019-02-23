package net.twasidependency.discordbot.listeners;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.twasi.core.services.ServiceRegistry;
import net.twasi.core.services.providers.DataService;
import net.twasidependency.discordbot.database.userservers.UserDiscordServerRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static net.twasidependency.discordbot.Plugin.config;
import static net.twasidependency.discordbot.Plugin.log;

public class GuildJoinListener extends ListenerAdapter {
    UserDiscordServerRepository repo = ServiceRegistry.get(DataService.class).get(UserDiscordServerRepository.class);
    private static String serverNotRegisteredMessage = "This server is not registered to a Twasi user yet." +
            " Please connect your Discord account to Twasi in Twasi Panel and enter this id: '%s'.";

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        log("Joining guild...");
        Guild guild = event.getGuild();
        if (guild.getId().equals(config.getDefaultDiscordServerId())) { // Compare guild ID to configured default guild
            log("Guild is default guild.");
            return; // No need to leave guild
        }
        if (repo.getUserDiscordServerByGuildId(guild.getId()) == null) { // True if there is no registered owner of that guild yet (in database)
            log("Guild is not default and not in database.");
            forceLeaveServerWithMessage(guild); // Leave the unknown guild with message
            return;
        }
        log("Guild is verified user-guild."); // No need to leave guild
    }

    public static void forceLeaveServerWithMessage(Guild guild) {
        AtomicInteger counter = new AtomicInteger(); // Counts the thread's sleep seconds (atomic because of shared access)
        AtomicBoolean leaved = new AtomicBoolean(false); // Knows whether the guild was successfully leaves or not (atomic because of shared access)
        List<TextChannel> channels = new ArrayList<>(); // A list of channels to try to send message in
        channels.add(guild.getDefaultChannel()); // Add default channel first to prioritize it
        channels.addAll(guild.getTextChannels()); // Add all other channels
        for (TextChannel channel : channels) {
            try {
                if (!leaved.get()) { // No need to create more threads if the first request was successful and not all threads were already created
                    new Thread(() -> { // Create new thread
                        try {
                            TimeUnit.SECONDS.sleep(counter.getAndIncrement()); // Try another channel every second
                            if (!leaved.get()) // Only continue if the bot hasn't left the guild yet
                                channel.sendMessageFormat(serverNotRegisteredMessage, guild.getId()).queue(success -> { // Try to send message to channel
                                    leaved.set(true); // If successful prevent other threads from trying
                                    guild.leave().queue(); // and leave the guild
                                });
                        } catch (InterruptedException ignored) { // Ignore interruption exceptions
                        }
                    }).start();
                }
            } catch (Exception ignored) { // Prevent unexpected exceptions
            }
        }
        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(counter.get()); // Get last value from counter and sleep that time (no need for another increment)
                if (!leaved.get()) guild.leave().queue(); // If the guild hasn't been left until now (because no message could be sent) just leave it
            } catch (Exception ignored) { // Prevent unexpected exceptions
            }
        }).start();
    }

}
