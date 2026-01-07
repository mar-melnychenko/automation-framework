package org.example.config;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.ConfigFactory;

@Config.Sources("classpath:${env}.properties")
public interface ConfigProps extends Config {

    ConfigProps APP_PROPERTY = loadProperties();

    static ConfigProps loadProperties() {
        return ConfigFactory.create(
                ConfigProps.class, System.getProperties(), System.getenv());
    }

    @Key("browser")
    String browser();

    @Key("base_url")
    String baseUrl();

    @Key("base_api_url")
    String baseApiUrl();

}
