package net.twasidependency.discordbot.commands;

public enum CommandPermissionLevel {
    ADMIN(15), STREAMER(10), MOD(5), USER(0);

    private final int level;

    CommandPermissionLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
