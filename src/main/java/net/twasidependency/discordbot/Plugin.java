package net.twasidependency.discordbot;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.Permission;
import net.twasi.core.logger.TwasiLogger;
import net.twasi.core.plugin.TwasiDependency;
import net.twasi.core.services.ServiceRegistry;
import net.twasi.core.services.providers.DataService;
import net.twasidependency.discordbot.controllers.BotGuildJoinController;
import net.twasidependency.discordbot.database.ConfigurationEntity;
import net.twasidependency.discordbot.database.ConfigurationRepository;
import net.twasidependency.discordbot.service.DiscordService;

import javax.security.auth.login.LoginException;

public class Plugin extends TwasiDependency {
    public static String prefix = "[Discord] ";
    public static ConfigurationEntity config;
    public static DiscordService service;
    public static JDA bot = null;

    // Controllers:
    public static BotGuildJoinController guildJoinController;

    @Override
    public void onActivate() {
        log("Discord Bot starting up...");
        log("Trying to load configuration...");
        ConfigurationRepository repo = ServiceRegistry.get(DataService.class).get(ConfigurationRepository.class);
        ConfigurationEntity entity = repo.getConfiguration();
        if (entity == null) {
            log("No configuration in database. Generating a new one...");
            entity = new ConfigurationEntity();
            repo.add(entity);
            repo.commitAll();
            log("Configuration generated.");
            log("Please enter bot details in 'discord-bot-configuration' collection an restart Twasi.");
            return;
        }
        config = entity;
        ConfigurationEntity compare = new ConfigurationEntity();
        boolean niceConfig = true;
        if (!isConfigValueValid(entity.getClientId(), compare.getClientId())) {
            niceConfig = false;
            logWarn("You need to enter a ClientID in the configuration.");
        }
        if (!isConfigValueValid(entity.getClientSecret(), compare.getClientSecret())) {
            niceConfig = false;
            logWarn("You need to enter a ClientSecret in the configuration.");
        }
        if (!isConfigValueValid(entity.getDefaultDiscordServerId(), compare.getDefaultDiscordServerId())) {
            niceConfig = false;
            logWarn("You need to enter a default Discord Server-ID in the configuration.");
        }
        if (!isConfigValueValid(entity.getToken(), compare.getToken())) {
            niceConfig = false;
            logWarn("You need to enter a Bot-Token in the configuration.");
        }
        if (!niceConfig) {
            log("Please enter correct settings in the bot configuration and restart Twasi.");
            return;
        }
        try {
            // Build jda and connect
            bot = new JDABuilder(entity.getToken()).build();

            log("Waiting for bot to connect...");
            bot.awaitReady();
            log("Connected! c:");

            // Instantiate controllers
            guildJoinController = new BotGuildJoinController(bot);

            // Instantiate and register service
            service = new DiscordService();
            ServiceRegistry.register(service);

            // Inform user about bot join link
            log("Let Twasibot join the Server that is set in configuration ==> %s <<==", bot.asBot().getInviteUrl(Permission.ADMINISTRATOR));
        } catch (LoginException e) {
            logWarn("Unable to login to Discord API. Please check if the provided token in configuration is valid.");
            logDebug("Used token: " + entity.getToken());
        } catch (InterruptedException e) {
            logWarn("Discord bot was interrupted on startup.");
            logDebug(e.getLocalizedMessage());
        }
    }

    private boolean isConfigValueValid(String entity, String badValue) {
        if (entity == null || entity.replaceAll(" ", "").equals("")) return false;
        return !entity.equalsIgnoreCase(badValue);
    }

    public static void log(String log) {
        TwasiLogger.log.info(prefix + log);
    }

    public static void log(String log, Object... objects) {
        log(String.format(log, objects));
    }

    public void logWarn(String log) {
        TwasiLogger.log.warn(prefix + log);
    }

    public static void logDebug(String log) {
        TwasiLogger.log.debug(prefix + log);
    }
}
