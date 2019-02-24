package net.twasidependency.discordbot.commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.twasi.core.database.models.User;
import net.twasidependency.discordbot.GuildType;
import net.twasidependency.discordbot.Plugin;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

public abstract class DiscordBotCommand {

    private CommandPermissionLevel requiredLevel; // The level that is required to run the command
    private String commandName; // The name that should trigger the command
    private List<String> commandAliases; // Optional aliases for the command name
    private String commandHelpText; // A description for the command
    private List<CommandArgument> args; // A list of args that are optional or required (required first)

    protected DiscordBotCommand(CommandPermissionLevel requiredLevel, String commandName, List<String> commandAliases, String commandHelpText, List<CommandArgument> args) {
        this.requiredLevel = requiredLevel;
        this.commandName = commandName.toLowerCase();
        List<String> lowerCaseAliases = new ArrayList<>();
        for (String s : commandAliases) lowerCaseAliases.add(s.toLowerCase());
        this.commandAliases = lowerCaseAliases;
        this.commandHelpText = commandHelpText;
        this.args = args;
    }

    public CommandPermissionLevel getRequiredLevel() {
        return requiredLevel;
    }

    public String getCommandName() {
        return commandName;
    }

    public List<String> getCommandAliases() {
        return commandAliases;
    }

    public String getCommandHelpText() {
        return commandHelpText;
    }

    public List<CommandArgument> getArgs() {
        return args;
    }

    /**
     * @param usedCommandName The command name or a used alias
     * @param args            List of used arguments for the command
     * @param triggerMessage  The message that triggered the command
     * @param guildType       Whether the command cam from the official TWASI-Guild or a user guild
     * @param guildOwner      If guildType is USER_GUILD user is set to the guilds owning user. Null if guildType is DEFAULT_GUILD
     * @return What the bot should return to the user
     */
    public abstract MessageEmbed generateCommandOutput(String usedCommandName, List<String> args, Message triggerMessage, GuildType guildType, User guildOwner);

    public MessageEmbed getHelpMessage() {
        boolean messagesInline = true, blankFieldsInline = false;
        EmbedBuilder builder = new EmbedBuilder().setTitle(format("Helpfile for command %s%s:", Plugin.botPrefix, this.getCommandName()));
        StringBuilder usageBuilder = new StringBuilder(Plugin.botPrefix).append(this.getCommandName()),
                argExplainBuilder = new StringBuilder();
        for (CommandArgument s : getArgs()) {
            usageBuilder.append(" <").append(s.getArgName()).append(">");
            argExplainBuilder.append("\n**").append(s.getArgName()).append("** *").append(s.getDescription()).append("*");
            if (s.isRequired()) argExplainBuilder.append(" (required)");
            else argExplainBuilder.append(" (optional)");
        }
        builder.addField("Usage", format("```%s```", usageBuilder.toString()), messagesInline);
        builder.addBlankField(blankFieldsInline);
        if (getArgs().size() > 0) {
            builder.addField("Arguments", argExplainBuilder.toString(), messagesInline);
        }
        if (this.getCommandAliases().size() > 0) {
            StringBuilder aliasesBuilder = new StringBuilder();
            for (String s : getCommandAliases()) aliasesBuilder.append(Plugin.botPrefix).append("**").append(s).append("**").append(", ");
            String aliases = aliasesBuilder.toString();
            aliases = aliases.substring(0, aliases.length() - 2); // remove last ', '
            builder.addField("Aliases", aliases, messagesInline);
        }
        builder.addBlankField(blankFieldsInline);
        builder.addField("Description", getCommandHelpText(), messagesInline);
        builder.setColor(new Color(237, 220, 113));
        builder.setFooter("This command requires the permission level " + getRequiredLevel(), null);
        return builder.build();
    }

    public static class CommandArgument {
        private String argName;
        private String description;
        private boolean required;

        public CommandArgument(String argName, String description, boolean required) {
            this.argName = argName;
            this.description = description;
            this.required = required;
        }

        public String getArgName() {
            return argName;
        }

        public String getDescription() {
            return description;
        }

        public boolean isRequired() {
            return required;
        }
    }

}
