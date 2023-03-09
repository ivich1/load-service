package org.example.log;

import lombok.SneakyThrows;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class FileLoger {

    BufferedWriter writer;
    private static FileLoger instance;

    private FileLoger(){
        File fileName = new File("log" + LocalDate.now() + ".txt");
        try {
            writer = new BufferedWriter(new FileWriter(fileName)) ;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    public void log(String logInfo){
        writer.append( LocalDate.now() + " " + logInfo + "\n");
    }

    @SneakyThrows
    public void info(String logInfo){
        writer.append( LocalDateTime.now() + " " + logInfo + "\n");
        System.out.print(LocalDateTime.now()  + " " + logInfo + "\n");
    }

    @SneakyThrows
    public void close(){
        writer.close();
    }

    public static FileLoger getInstance(){
        if(instance == null){
            instance = new FileLoger();
        }
        return instance;
    }
}
