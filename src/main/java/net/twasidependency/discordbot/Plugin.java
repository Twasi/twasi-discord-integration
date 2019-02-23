package net.twasidependency.discordbot;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.Permission;
import net.twasi.core.logger.TwasiLogger;
import net.twasi.core.plugin.TwasiDependency;
import net.twasi.core.services.ServiceRegistry;
import net.twasi.core.services.providers.DataService;
import net.twasidependency.discordbot.database.ConfigurationEntity;
import net.twasidependency.discordbot.database.ConfigurationRepository;
import net.twasidependency.discordbot.listeners.GuildJoinListener;
import net.twasidependency.discordbot.listeners.GuildLeaveListener;
import net.twasidependency.discordbot.listeners.MessageListener;

import javax.security.auth.login.LoginException;

public class Plugin extends TwasiDependency {
    public static String prefix = "[Discord] ";
    public static ConfigurationEntity config;
    private JDA bot = null;

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
            bot = new JDABuilder(entity.getToken()).build();
            bot.addEventListener(new MessageListener());
            bot.addEventListener(new GuildJoinListener());
            bot.addEventListener(new GuildLeaveListener());
            log("Let Twasibot join the Server that is set in configuration " + bot.asBot().getInviteUrl(Permission.ADMINISTRATOR));
            ServiceRegistry.register(new DiscordService());
        } catch (LoginException e) {
            logWarn("Unable to login to Discord API. Please check if the provided token in configuration is valid.");
            logDebug("Used token: " + entity.getToken());
        }
    }

    private boolean isConfigValueValid(String entity, String badValue) {
        if (entity == null || entity.replaceAll(" ", "").equals("")) return false;
        return !entity.equalsIgnoreCase(badValue);
    }

    public static void log(String log) {
        TwasiLogger.log.info(prefix + log);
    }

    public void logWarn(String log) {
        TwasiLogger.log.warn(prefix + log);
    }

    public static void logDebug(String log) {
        TwasiLogger.log.debug(prefix + log);
    }
}
