package net.twasidependency.discordbot.commands;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.twasi.core.database.models.User;
import net.twasidependency.discordbot.GuildType;
import net.twasidependency.discordbot.Plugin;
import net.twasidependency.discordbot.commands.DiscordBotCommand.CommandArgument;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DiscordBotCommandBuilder {

    // Same fields as in final command class
    private CommandPermissionLevel requiredLevel;
    private String commandName;
    private List<String> commandAliases;
    private String commandHelpText;
    private List<CommandArgument> args;

    public DiscordBotCommandBuilder(String commandName, String commandHelpText) {
        this.commandName = commandName.toLowerCase();
        this.commandHelpText = commandHelpText;
        commandAliases = new ArrayList<>();
        args = new ArrayList<>();
        requiredLevel = CommandPermissionLevel.USER;
    }

    public DiscordBotCommandBuilder requirePermissionlevel(CommandPermissionLevel level) {
        this.requiredLevel = level;
        return this;
    }

    public DiscordBotCommandBuilder setAliases(String... aliases) {
        commandAliases.addAll(Arrays.asList(aliases));
        return this;
    }

    public DiscordBotCommandBuilder setCommandArguments(CommandArgument... arguments) throws IllegalArgumentException {
        boolean optionalsReached = false;
        for (CommandArgument arg : arguments) {
            if (optionalsReached && arg.isRequired())
                throw new IllegalArgumentException("Required arguments cannot be set after optional arguments.");
            if (!arg.isRequired()) optionalsReached = true;
        }
        args.addAll(Arrays.asList(arguments));
        return this;
    }

    public void registerForDefaultGuildOnly(DefaultGuildCommandInterface handler) {
        DiscordBotCommand command = build((String usedCommandName, List<String> args, Message triggerMessage, GuildType guildType, User guildOwner) -> {
            if (guildType.equals(GuildType.DEFAULT_GUILD))
                return handler.getCommandAnswer(usedCommandName, args, triggerMessage);
            else return null;
        });
        Plugin.service.registerDiscordBotCommand(command);
    }

    public void registerForUserGuildsOnly(UserGuildCommandInterface handler) {
        DiscordBotCommand command = build((String usedCommandName, List<String> args, Message triggerMessage, GuildType guildType, User guildOwner) -> {
            if (guildType.equals(GuildType.USER_GUILD))
                return handler.getCommandAnswer(usedCommandName, args, triggerMessage, guildOwner);
            else return null;
        });
        Plugin.service.registerDiscordBotCommand(command);
    }

    public void registerGlobal(UserGuildCommandInterface userHandler, DefaultGuildCommandInterface defaultGuildHandler) {
        DiscordBotCommand command = build((String usedCommandName, List<String> args, Message triggerMessage, GuildType guildType, User guildOwner) -> {
            if (guildType.equals(GuildType.DEFAULT_GUILD))
                return defaultGuildHandler.getCommandAnswer(usedCommandName, args, triggerMessage);
            else return userHandler.getCommandAnswer(usedCommandName, args, triggerMessage, guildOwner);
        });
        Plugin.service.registerDiscordBotCommand(command);
    }

    public void registerGlobal(DiscordBotCommandInterface handler) {
        DiscordBotCommand command = build(handler);
        Plugin.service.registerDiscordBotCommand(command);
    }

    public DiscordBotCommand build(DiscordBotCommandInterface handler) {
        return new DiscordBotCommand(requiredLevel, commandName, commandAliases, commandHelpText, args) {
            @Override
            public MessageEmbed generateCommandOutput(String usedCommandName, List<String> args, Message triggerMessage, GuildType guildType, User guildOwner) {
                return handler.getCommandAnswer(usedCommandName, args, triggerMessage, guildType, guildOwner);
            }
        };
    }

    public interface DefaultGuildCommandInterface {
        MessageEmbed getCommandAnswer(String usedCommandName, List<String> args, Message triggerMessage);
    }

    public interface UserGuildCommandInterface {
        MessageEmbed getCommandAnswer(String usedCommandName, List<String> args, Message triggerMessage, User guildOwner);
    }

    public interface DiscordBotCommandInterface {
        MessageEmbed getCommandAnswer(String usedCommandName, List<String> args, Message triggerMessage, GuildType guildType, User guildOwner);
    }

}
