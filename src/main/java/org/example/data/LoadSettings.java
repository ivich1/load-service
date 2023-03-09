package org.example.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoadSettings {

    String url;

    int requestPerClient;

    int clientCount;

    int time;

    boolean randomUrl = false;

    ArrayList<String> urlPull;
}
