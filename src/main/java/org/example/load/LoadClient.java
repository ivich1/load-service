package org.example.load;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.SneakyThrows;
import org.example.data.TimeInfo;
import org.example.log.FileLoger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
//import java.net.http.HttpClient;
//import java.net.http.HttpRequest;
//import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LoadClient implements Runnable {

    //private static final Logger LOGGER = LogManager.getLogger();
    private static final FileLoger LOGGER = FileLoger.getInstance();
    @Getter
    int id;
    URL url;
    List<String> urlPool;
    private boolean random_url;
    Random random;
    int requestCount;
    int timeout;

    private HttpURLConnection connection;

    //private final HttpClient client;
    //private HttpRequest request;

    private final ObjectMapper mapper;

    @SneakyThrows
    public LoadClient(int id, String url, int requestCount, int timeout) {


        this.id = id;
        this.url = new URL(url);;
        this.requestCount = requestCount;
        this.timeout = timeout;

        //connection
        this.connection = (HttpURLConnection) this.url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json");

        //this.client = HttpClient.newHttpClient();
        //request = HttpRequest.newBuilder()
        //        .uri(this.url.toURI())
        //        .build();

        mapper = new ObjectMapper();

        random_url = false;

    }

    @SneakyThrows
    public LoadClient(int id, List<String> urlPool, int requestCount, int timeout) {

        this.id = id;
        this.urlPool = new ArrayList<>(urlPool);;
        this.requestCount = requestCount;
        this.timeout = timeout;

        //connection
        //this.connection = (HttpURLConnection) this.url.openConnection();
        //connection.setRequestMethod("GET");
        //connection.setRequestProperty("Content-Type", "application/json");

        //this.client = HttpClient.newHttpClient();
        //request = HttpRequest.newBuilder()
        //        .uri(URI.create(this.urlPool.get(0)))
        //        .build();

        mapper = new ObjectMapper();

        random_url = true;
        random = new Random();

    }

    @SneakyThrows
    public void sendAll(){
        for(int i = 0; i < requestCount; i++){
            //connection
            this.connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");

            long sendTime = System.currentTimeMillis();
            String result = sendRequest();
            long getTime = System.currentTimeMillis();
            long absoluteTime = getTime - sendTime;
            TimeInfo timeInfo = mapper.readValue(result, TimeInfo.class);
            LOGGER.info(prepareLogInfo(i, timeInfo, absoluteTime));
            Thread.sleep(timeout);
        }
    }

    @SneakyThrows
    public void sendAllRandom(){
        for(int i = 0; i < requestCount; i++){

            //request = HttpRequest.newBuilder()
            //        .uri(URI.create(this.urlPool.get())
            //        .build();
            URL tmpUrl = new URL(urlPool.get(random.nextInt(this.urlPool.size() -1)));
            this.connection = (HttpURLConnection) tmpUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");

            long sendTime = System.currentTimeMillis();
            String result = sendRequest();
            long getTime = System.currentTimeMillis();
            long absoluteTime = getTime - sendTime;
            TimeInfo timeInfo = mapper.readValue(result, TimeInfo.class);
            //System.out.println(prepareLogInfo(i, timeInfo, absoluteTime));
            LOGGER.info(prepareLogInfo(i, timeInfo, absoluteTime));
            Thread.sleep(timeout);
        }
    }

    private String prepareLogInfo(int requestId, TimeInfo timeInfo, long absoluteTime){
        if(timeInfo.getBackendWorkTime() == null || timeInfo.getDbWorkTime() == null){
            return "Client " + this.id + " send " + requestId + " requests;" + " no info about working time" + "; absolute time " + absoluteTime;
        }
        else{
            return "Client " + this.id + " send " + requestId + " requests; send time: " + System.currentTimeMillis() + "; "
                    + timeInfo.toString() + "; absolute time " + absoluteTime;
        }

    }

    @SneakyThrows
    public String sendRequest(){
        //HttpResponse<String> response = client.send(request,
        //        HttpResponse.BodyHandlers.ofString());
        //return response.body();

        //?
        //connection.setDoOutput(true);


        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }

    @Override
    public void run() {
        if(random_url)
            sendAllRandom();
        else
            sendAll();
    }
}
