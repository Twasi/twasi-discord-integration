package net.twasidependency.discordbot.database.userservers;

import net.twasi.core.database.lib.Repository;
import net.twasi.core.database.models.User;

public class UserDiscordServerRepository extends Repository<UserDiscordServerEntity> {

    public UserDiscordServerEntity getUserDiscordServerByUser(User user) {
        return store.createQuery(UserDiscordServerEntity.class).field("user").equal(user).get();
    }

    public UserDiscordServerEntity getUserDiscordServerByGuildId(String guildId) {
        return store.createQuery(UserDiscordServerEntity.class).field("guildId").equal(guildId).get();
    }

}
