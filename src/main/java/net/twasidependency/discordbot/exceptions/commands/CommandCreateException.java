package net.twasidependency.discordbot.exceptions.commands;

public abstract class CommandCreateException extends Exception {

    public CommandCreateException(String reason){
        super(reason);
    }

}
