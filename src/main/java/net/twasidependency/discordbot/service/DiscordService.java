package net.twasidependency.discordbot.service;

import net.twasi.core.services.IService;

import static net.twasidependency.discordbot.Plugin.bot;

public class DiscordService implements IService {

    public void registerDiscordEventListener(Object... eventListenersOrAdapters){
        bot.addEventListener(eventListenersOrAdapters);
    }

}
