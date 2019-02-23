package net.twasidependency.discordbot.database;

import net.twasi.core.database.models.BaseEntity;
import org.mongodb.morphia.annotations.Entity;

@Entity(value = "discord-bot-configuration", noClassnameStored = true)
public class ConfigurationEntity extends BaseEntity {

    private String token = "YOUR-OAUTH-TOKEN-HERE";
    private String clientId = "YOUR-CLIENT-ID-HERE";
    private String clientSecret = "YOUR-CLIENT-SECRET-HERE";
    private String defaultDiscordServerId = "YOUR-DEFAULT-DISCORDS-SERVER-ID-HERE";

    public ConfigurationEntity() {
    }

    public String getToken() {
        return token;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getDefaultDiscordServerId() {
        return defaultDiscordServerId;
    }
}
