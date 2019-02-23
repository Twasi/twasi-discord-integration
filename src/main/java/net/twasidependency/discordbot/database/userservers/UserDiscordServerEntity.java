package net.twasidependency.discordbot.database.userservers;

import net.twasi.core.database.models.BaseEntity;
import net.twasi.core.database.models.User;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.annotations.Reference;

public class UserDiscordServerEntity extends BaseEntity {

    @Reference
    @Indexed(unique = true)
    private User user;

    @Indexed(unique = true)
    private String guildId;

    public User getUser() {
        return user;
    }

    public String getGuildId() {
        return guildId;
    }

    public UserDiscordServerEntity() {
    }

    public UserDiscordServerEntity(User user, String guildId) {
        this.user = user;
        this.guildId = guildId;
    }
}
