package org.example.load;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.SneakyThrows;
import org.example.data.TimeInfo;
import org.example.log.FileLoger;

import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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

    private final HttpClient client;
    private HttpRequest request;

    private final ObjectMapper mapper;

    @SneakyThrows
    public LoadClient(int id, String url, int requestCount, int timeout) {

        this.id = id;
        this.url = new URL(url);;
        this.requestCount = requestCount;
        this.timeout = timeout;

        this.client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(this.url.toURI())
                .build();

        mapper = new ObjectMapper();

        random_url = false;

    }

    @SneakyThrows
    public LoadClient(int id, List<String> urlPool, int requestCount, int timeout) {

        this.id = id;
        this.urlPool = new ArrayList<>(urlPool);;
        this.requestCount = requestCount;
        this.timeout = timeout;

        this.client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(URI.create(this.urlPool.get(0)))
                .build();

        mapper = new ObjectMapper();

        random_url = true;
        random = new Random();

    }

    @SneakyThrows
    public void sendAll(){
        for(int i = 0; i < requestCount; i++){
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
            request = HttpRequest.newBuilder()
                    .uri(URI.create(this.urlPool.get(random.nextInt(this.urlPool.size() -1))))
                    .build();
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
        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());
        return response.body();

    }

    @Override
    public void run() {
        if(random_url)
            sendAllRandom();
        else
            sendAll();
    }
}
