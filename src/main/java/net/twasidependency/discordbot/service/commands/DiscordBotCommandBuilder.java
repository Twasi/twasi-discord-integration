package net.twasidependency.discordbot.service.commands;

import net.dv8tion.jda.core.entities.Message;
import net.twasi.core.database.models.User;
import net.twasidependency.discordbot.GuildType;

import java.util.Arrays;
import java.util.List;

public class DiscordBotCommandBuilder {

    // Same fields as in final command class
    private CommandPermissionLevel requiredLevel;
    private String commandName;
    private List<String> commandAliases;
    private String commandHelpText;
    protected List<DiscordBotCommand.CommandArgument> args;

    public DiscordBotCommandBuilder(String commandName, String commandHelpText) {
        this.commandName = commandName;
        this.commandHelpText = commandHelpText;
    }

    public DiscordBotCommandBuilder requirePermissionlevel(CommandPermissionLevel level) {
        this.requiredLevel = level;
        return this;
    }

    public DiscordBotCommandBuilder setAliases(String... aliases) {
        commandAliases.addAll(Arrays.asList(aliases));
        return this;
    }

    public interface CommandInterface {
        void onCommandTriggered(List<String> args, Message triggerMessage, GuildType guildType, User guildOwner);
    }

}
