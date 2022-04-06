package ru.mishin.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.istack.NotNull;
import ru.mishin.client.Client;
import ru.mishin.database.IDataBase;
import ru.mishin.database.PSQLDataBase;
import ru.mishin.utils.SubscribeInfo;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import ru.mishin.database.models.Event;

public class Server extends Thread implements Observer{
    private static final String TAG = Server.class.getSimpleName();

    public static enum ServerEvents {

        TIME("TIME"),
        ALARM_EVENT("ALARM_EVENT"),
        ADD_EVENT("ADD_EVENT"),
        SUBSCRIBE("SUBSCRIBE"),
        RESET("RESET");
        String name;

        ServerEvents(String name) {
            this.name = name;
        }
    }
    private Gson gson = new GsonBuilder().create();
    private IDataBase events;

    @NotNull
    private AlarmClock alarmClock;

    private ServerUIListener serverUIListener;
    private final CopyOnWriteArrayList<ProcessThread> subscribers;
    private int port;
    public Server(int port) {
        events = new PSQLDataBase();
        subscribers = new CopyOnWriteArrayList<>();
        this.port = port;
        alarmClock = new AlarmClock(0);
    }

    @Override
    public void run() {
       alarmClock.start();
       alarmClock.subscribe(this::handleEvent);
       Logger.getLogger(TAG).log(Level.INFO, "Server has been started on port: "+ port);
       while(true){
           try(ServerSocket serverSocket = new ServerSocket(port)) {
               Socket socket = serverSocket.accept();
               ProcessThread processThread = new ProcessThread(socket);
               processThread.start();
               subscribers.add(processThread);
               serverUIListener.onChangeNumberClients(subscribers.size());
           } catch (IOException exception) {
               exception.printStackTrace();
           }
       }
    }

    @Override
    public void handleEvent(long time) {
        new Thread(() -> {subscribers.forEach(x->x.sendTime(time));}).start();

        new Thread(() -> {
            List<Event> eventsByTime = events.getEventsByTime(time);
            if (eventsByTime != null && eventsByTime.size() != 0) {
                events.removeByTime(time);
                subscribers.forEach(x->x.sendEvent(gson.toJson(eventsByTime)));
            }
        }).start();
        serverUIListener.onUpdateTime(time);

    }


    public class ProcessThread extends Thread {

        private final String TAG = ProcessThread.class.getSimpleName();
        private Socket incoming;
        private final Set<String> addEventParams = new HashSet<>(Arrays.asList("time","desc"));
        private Scanner scanner;
        private PrintWriter printWriter;

        public ProcessThread(Socket incoming) throws IOException {
            Logger.getLogger(TAG).log(Level.INFO,"Subscribed new client");
            this.incoming = incoming;
            this.printWriter = new PrintWriter(incoming.getOutputStream());
            this.scanner = new Scanner(incoming.getInputStream());
        }
        @Override
        public void run() {
               Logger.getLogger(TAG).log(Level.INFO,"New Client Connected with id" + getId());
               new Thread(() -> onSubscribed(alarmClock.getTime(),events.getAll())).start();
               while (!isInterrupted()){
                   if (scanner.hasNextLine()){
                       String request = scanner.nextLine();
                       Logger.getLogger(TAG).log(Level.INFO,"New request for " + getId() + ": " + request);
                       String split [] = request.split("/");
                       String type = split[0];
                       switch (Client.ClientEvent.valueOf(type)){
                           case ADD_EVENT: {
                               String params [] = split[1].split("&");
                               if (params.length !=2){
                                   throw new RuntimeException("Incorrect numbers of params");
                               }
                               HashMap<String,String> mapParams = new HashMap<>();
                               for (var param:params){
                                   var keyValue = param.split("=");
                                   mapParams.put(keyValue[0].toLowerCase(),keyValue[1]);
                               }
                               if (!addEventParams.equals(mapParams.keySet())){
                                   throw new RuntimeException("Incorrect names of params");
                               }
                               Event newEvent = new Event(Long.parseLong(mapParams.get("time")),mapParams.get("desc"));
                               events.addEvent(newEvent);
                               subscribers.forEach(x -> x.sendAddEvent(newEvent));
                               break;
                           }
                           case DISCONNECT: {
                               interrupt();
                               subscribers.remove(this);
                               serverUIListener.onChangeNumberClients(subscribers.size());
                               try {
                                   incoming.close();
                               } catch (IOException exception) {
                                   exception.printStackTrace();
                               }
                               break;
                           }
                           default: {
                               throw new RuntimeException("NO!");
                           }
                       }
                   }
               }
        }

        public void onSubscribed(long time, List<Event> events) {
            var subscribeInfo = gson.toJson(new SubscribeInfo(time,events));
            send(ServerEvents.SUBSCRIBE + "/" + subscribeInfo);
        }
        public void sendReset() {
            send(ServerEvents.RESET.name);
        }
        public void sendTime(long time){
            send(ServerEvents.TIME + "/" + time);
        }

        public void sendEvent(String text){
            send(ServerEvents.ALARM_EVENT + "/" + text);
        }

        public void sendAddEvent(Event event){
            send(ServerEvents.ADD_EVENT +"/" + gson.toJson(event));
        }
        private void send(String text){
            printWriter.println(text);
            printWriter.flush();
        }
    }

    public void playTimer() {
        alarmClock.launch();
    }

    public void pauseTimer() {
        alarmClock.pause();
    }

    public void stopTimer() {
        alarmClock.hang();
        subscribers.forEach(x->x.sendReset());
    }

    public void setServerUIListener(ServerUIListener serverUIListener) {
        this.serverUIListener = serverUIListener;
    }
}
