package net.twasidependency.discordbot.service;

import net.twasi.core.services.IService;
import net.twasidependency.discordbot.Plugin;
import net.twasidependency.discordbot.commands.DiscordBotCommand;

import java.util.ArrayList;
import java.util.List;

import static net.twasidependency.discordbot.Plugin.bot;

public class DiscordService implements IService {
    private List<DiscordBotCommand> globalGuildCommands = new ArrayList<>();

    public void registerDiscordEventListener(Object... eventListenersOrAdapters) {
        bot.addEventListener(eventListenersOrAdapters);
    }

    public List<DiscordBotCommand> getRegisteredCommands() {
        return globalGuildCommands;
    }

    public void registerDiscordBotCommand(DiscordBotCommand command) {
        globalGuildCommands.add(command);
    }

    public List<DiscordBotCommand> getCommandsByCommandName(String commandName, boolean includeAliases){
        commandName = commandName.toLowerCase();
        List<DiscordBotCommand> correspondingCommands = new ArrayList<>();
        for (DiscordBotCommand command : Plugin.service.getRegisteredCommands()) {
            if (command.getCommandName().equals(commandName) || includeAliases && command.getCommandAliases().contains(commandName)) {
                correspondingCommands.add(command);
            }
        }
        return correspondingCommands;
    }

    public List<DiscordBotCommand> getCommandsByCommandName(String commandName){
        return getCommandsByCommandName(commandName, true);
    }
}
