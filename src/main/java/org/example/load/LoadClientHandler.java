package org.example.load;

import lombok.SneakyThrows;
import org.example.data.LoadSettings;
import org.example.log.FileLoger;

import java.util.ArrayList;

public class LoadClientHandler implements Runnable{

    //private static final Logger LOGGER = LogManager.getLogger();
    private static final FileLoger LOGGER = FileLoger.getInstance();

    final String url;
    private ArrayList<String> urlPool = new ArrayList<>();
    int requestPerClient;
    int clientCount;
    int clientTimeout;

    ArrayList<LoadClient> clients;
    int time; //seconds

    public LoadClientHandler(String url, int requestPerClient, int clientCount, int time) {
        this.url = url;
        this.requestPerClient = requestPerClient;
        this.clientCount = clientCount;
        this.clientTimeout = 1000/requestPerClient;
        this.time = time;

        clients = new ArrayList<>();
        createClients();
    }

    public LoadClientHandler(LoadSettings loadSettings){
        this.url = loadSettings.getUrl();
        this.requestPerClient = loadSettings.getRequestPerClient();
        this.clientCount = loadSettings.getClientCount();
        this.clientTimeout = 1000/requestPerClient;
        this.time = loadSettings.getTime();
        this.urlPool = new ArrayList<>(loadSettings.getUrlPull());

        clients = new ArrayList<>();
        if(loadSettings.isRandomUrl()){
            createRandomClients();
        }
        else {
            createClients();
        }


    }

    private void createClients(){
        for (int i = 0; i < clientCount; i++){
            clients.add(new LoadClient(i, url, requestPerClient, clientTimeout));
        }
    }

    private void createRandomClients(){
        for (int i = 0; i < clientCount; i++){
            clients.add(new LoadClient(i, urlPool, requestPerClient, clientTimeout));
        }
    }



    @SneakyThrows
    @Override
    public void run() {
        ArrayList<Thread> threads = new ArrayList<>();
        for (LoadClient client: clients) {
            LOGGER.info("client " + client.getId() + " created");
            threads.add(new Thread(client));
        }
        for (Thread thread :threads) {
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        LOGGER.close();
    }
}
