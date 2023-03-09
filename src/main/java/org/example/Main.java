package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.example.data.LoadSettings;
import org.example.load.LoadClientHandler;
import org.example.log.FileLoger;

import java.io.File;

public class Main {
    //private static final Logger LOGGER2 = LogManager.getLogger();
    private static final FileLoger LOGGER = FileLoger.getInstance();

    @SneakyThrows
    public static void main(String[] args) {
        System.out.println();
        LOGGER.info("Start working!");
        //10.1.6.28
        //localhost

        //reading settings
        ObjectMapper mapper = new ObjectMapper();
        LoadSettings loadSettings = mapper.readValue(new File("settings.json"), LoadSettings.class);

        //creating clients
        LoadClientHandler hc = new LoadClientHandler(loadSettings);

        //getting result
        Thread thread = new Thread(hc);
        thread.start();
    }
}