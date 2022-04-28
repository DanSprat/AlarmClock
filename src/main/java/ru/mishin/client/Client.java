package ru.mishin.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.mishin.database.models.Event;
import ru.mishin.server.Server;
import ru.mishin.utils.SubscribeInfo;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.google.gson.reflect.TypeToken;

public class Client extends Thread {

    public static enum ClientEvent {

        ADD_EVENT("ADD_EVENT"),
        DISCONNECT("DISCONNECT");

        String name;

        ClientEvent(String name) {
            this.name = name;
        }
    }

    private static final String TAG = Client.class.getSimpleName();
    private static AtomicInteger clients = new AtomicInteger(0);

    private static final Type eventsListType = new TypeToken<List<Event>>(){}.getType();
    private int clientsID;
    private Socket socket;
    private ClientUIListener clientUIListener;
    private PrintWriter printWriter;
    private Scanner scanner;
    private Gson gson = new GsonBuilder().create();
    public int getClientsID() {
        return clientsID;
    }

    public Client(String host, int port)  {
        try {
            clientsID = clients.getAndIncrement();
            socket = new Socket(host,port);
            scanner = new Scanner(socket.getInputStream());
            printWriter = new PrintWriter(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void setClientUIListener(ClientUIListener clientUIListener) {
        this.clientUIListener = clientUIListener;
    }

    public void disconnect() {
        send(ClientEvent.DISCONNECT.toString());
    }

    public void addAlarm(long time, String desc) {
        send(ClientEvent.ADD_EVENT + "/" + "time="+ time + "&desc=" +desc);
    }

    private void send(String text) {
        printWriter.println(text);
        printWriter.flush();
    }

    @Override
    public void run() {
        while (true) {
           if (scanner.hasNextLine()){
               String request = scanner.nextLine();
               String split [] = request.split("/");
               String type = split[0];
               switch (Server.ServerEvents.valueOf(type)) {
                   case TIME -> {
                       long time = Long.parseLong(split[1]);
                       new Thread(() -> { clientUIListener.onTime(time);}).start();
                       Logger.getLogger(TAG).log(Level.INFO,"Updating time to " + time + " for " + clientsID);
                       break;
                   }
                   case ADD_EVENT -> {
                       new Thread(() -> { clientUIListener.onAddEvent(gson.fromJson(split[1],Event.class));}).start();;
                       Logger.getLogger(TAG).log(Level.INFO,"Added new Event " + split[1] + " for " + clientsID);
                       break;
                   }
                   case SUBSCRIBE -> {
                       SubscribeInfo info = gson.fromJson(split[1], SubscribeInfo.class);
                       new Thread(() -> {clientUIListener.onSubscribe(info.getTime(),info.getEvents());}).start();;
                       break;
                   }
                   case ALARM_EVENT -> {
                       new Thread(() -> clientUIListener.onAlarm(gson.fromJson(split[1],eventsListType))).start();;
                       Logger.getLogger(TAG).log(Level.INFO,"Updating time and Alerting Event Added " + split[1] + " for " + clientsID);
                       break;
                   }
                   case RESET -> {

                   }
                   case SHUTDOWN -> {
                       clientUIListener.onShutDown();
                       Logger.getLogger(TAG).log(Level.INFO,"client " + clientsID + "has benn disconnected. Reason: Server is shut down" );

                       break;
                   }
               }
           }
        }
    }
}
