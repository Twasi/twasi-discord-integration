package net.twasidependency.discordbot.commands.defaultcommands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.twasi.core.database.models.User;
import net.twasidependency.discordbot.GuildType;
import net.twasidependency.discordbot.Plugin;
import net.twasidependency.discordbot.commands.CommandPermissionLevel;
import net.twasidependency.discordbot.commands.DiscordBotCommand;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class HelpCommand extends DiscordBotCommand {
    private static boolean registered = false;

    public static void register() {
        if (!registered) new HelpCommand(); // Instantiate to register in constructor
        registered = true;
    }

    private HelpCommand() {
        super(
                CommandPermissionLevel.USER,
                "help",
                Arrays.asList("?", "h"),
                "Show all available commands and what they do",
                Collections.singletonList(new CommandArgument("command", "A command for which the help file should be displayed", false))
        );
        Plugin.service.registerDiscordBotCommand(this);
    }

    @Override
    public MessageEmbed generateCommandOutput(String usedCommandName, List<String> args, Message triggerMessage, GuildType guildType, User guildOwner) {
        if (args.size() == 0) {
            EmbedBuilder builder = new EmbedBuilder();
            for (DiscordBotCommand command : Plugin.service.getRegisteredCommands()) {
                builder.addField(command.getCommandName(), command.getCommandHelpText(), false);
            }
            return builder.build();
        }
        for (DiscordBotCommand command : Plugin.service.getCommandsByCommandName(args.get(0)))
            triggerMessage.getChannel().sendMessage(command.getHelpMessage()).queue();
        return null;
    }
}
