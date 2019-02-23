package net.twasidependency.discordbot.database;

import net.twasi.core.database.lib.Repository;

public class ConfigurationRepository extends Repository<ConfigurationEntity> {

    public ConfigurationEntity getConfiguration() {
        try {
            return store.createQuery(ConfigurationEntity.class).get();
        } catch (Exception e) {
            return null;
        }
    }

}
