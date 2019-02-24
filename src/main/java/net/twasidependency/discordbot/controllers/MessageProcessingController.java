package net.twasidependency.discordbot.controllers;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.twasi.core.database.models.User;
import net.twasi.core.services.ServiceRegistry;
import net.twasi.core.services.providers.DataService;
import net.twasidependency.discordbot.GuildType;
import net.twasidependency.discordbot.Plugin;
import net.twasidependency.discordbot.database.userservers.UserDiscordServerRepository;
import net.twasidependency.discordbot.commands.DiscordBotCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MessageProcessingController extends ListenerAdapter {

    private List<DiscordBotCommand> registeredCommands;

    public MessageProcessingController(JDA jda) {
        jda.addEventListener(this);
        registeredCommands = new ArrayList<>();
    }

    public void registerBotCommand(DiscordBotCommand command) {
        registeredCommands.add(command);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.isFromType(ChannelType.TEXT)) return;
        Message message = event.getMessage();
        String content = message.getContentDisplay();
        if (!content.startsWith(Plugin.botPrefix)) return;
        GuildType type = GuildType.USER_GUILD;
        if (Plugin.config.getDefaultDiscordServerId().equals(message.getGuild().getId())) {
            type = GuildType.DEFAULT_GUILD;
        }
        String commandName = content.split(" ")[0].substring(Plugin.botPrefix.length()).toLowerCase();
        List<DiscordBotCommand> correspondingCommands = Plugin.service.getCommandsByCommandName(commandName);
        User user = null;
        if (type.equals(GuildType.USER_GUILD)) {
            user = ServiceRegistry.get(DataService.class).get(UserDiscordServerRepository.class).getUserDiscordServerByGuildId(message.getGuild().getId()).getUser();
        }
        ArrayList<String> args = new ArrayList<>(Arrays.asList(content.split(" ")));
        if (args.size() > 0) args.remove(0);
        int requiredArgs = 0;
        for (DiscordBotCommand command : correspondingCommands) {
            for (DiscordBotCommand.CommandArgument argument : command.getArgs()) {
                if (!argument.isRequired()) break;
                else requiredArgs++;
            }
            if (args.size() < requiredArgs) {
                event.getChannel().sendMessage("Please add all required arguments:\n").queue();
                event.getChannel().sendMessage(command.getHelpMessage()).queue();
                return;
            }
            MessageEmbed msg = command.generateCommandOutput(commandName, args, message, type, user);
            if (msg != null) event.getChannel().sendMessage(msg).queue();
        }
    }

}
