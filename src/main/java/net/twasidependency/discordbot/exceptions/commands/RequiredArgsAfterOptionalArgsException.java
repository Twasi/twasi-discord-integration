package net.twasidependency.discordbot.exceptions.commands;

public class RequiredArgsAfterOptionalArgsException extends CommandCreateException {

    public RequiredArgsAfterOptionalArgsException() {
        super("Please add all required args before any optional args");
    }

}
