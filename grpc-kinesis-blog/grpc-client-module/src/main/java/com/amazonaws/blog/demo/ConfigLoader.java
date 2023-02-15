package com.amazonaws.blog.demo;

import lombok.Getter;
import lombok.Setter;
import org.yaml.snakeyaml.Yaml;
import software.amazon.kinesis.lifecycle.events.ProcessRecordsInput;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ArrayBlockingQueue;

@Getter
public enum ConfigLoader {

        INSTANCE;
        private volatile ConfigData configData = new ConfigData();

        public static void setConfigData(String clientId, String clientSecret , String tokenUrl) {
            INSTANCE.configData.setClientId(clientId);
            INSTANCE.configData.setClientSecret(clientSecret);
            INSTANCE.configData.setTokenUrl(tokenUrl); }

        public static ConfigData getConfigData(){return INSTANCE.configData; }

}