package net.twasidependency.discordbot.service.commands;

import net.dv8tion.jda.core.entities.Message;
import net.twasi.core.database.models.User;
import net.twasidependency.discordbot.GuildType;

import java.util.List;

public abstract class DiscordBotCommand {

    protected CommandPermissionLevel requiredLevel; // The level that is required to run the command
    protected String commandName; // The name that should trigger the command
    protected List<String> commandAliases; // Optional aliases for the command name
    protected String commandHelpText; // A description for the command
    protected List<CommandArgument> args; // A list of args that are optional or required (required first)

    protected DiscordBotCommand(CommandPermissionLevel requiredLevel, String commandName, List<String> commandAliases, String commandHelpText, List<CommandArgument> args) {
        this.requiredLevel = requiredLevel;
        this.commandName = commandName;
        this.commandAliases = commandAliases;
        this.commandHelpText = commandHelpText;
        this.args = args;
    }

    /**
     * @param triggerMessage The message that triggered the command
     * @param guildType Whether the command cam from the official TWASI-Guild or a user guild
     * @param guildOwner If guildType is USER_GUILD user is set to the guilds owning user. Null if guildType is DEFAULT_GUILD
     * @return What the bot should return to the user
     */
    public abstract String generateCommandOutput(Message triggerMessage, GuildType guildType, User guildOwner);

    public class CommandArgument {
        private String description;
        private boolean required;

        public CommandArgument(String description, boolean required) {
            this.description = description;
            this.required = required;
        }

        public String getDescription() {
            return description;
        }

        public boolean isRequired() {
            return required;
        }
    }

}
