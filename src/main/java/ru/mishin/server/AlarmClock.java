package ru.mishin.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AlarmClock extends Thread implements Observable {

    private static final String TAG = AlarmClock.class.getSimpleName();


    private long time;
    private boolean isPaused;
    private boolean isStopped;
    private AtomicLong newSecondMillis;
    private AtomicLong pauseMillis;
    private Object testSyncObject = new Object();
    private Object syncObj = new Object();
    private ArrayList<Observer> subscribers;
    private final ExecutorService service = Executors.newFixedThreadPool(1);
    private final Runnable updateSubscribers = () -> subscribers.forEach(observer -> observer.handleEvent(time));

    public AlarmClock(long time) {
        newSecondMillis = new AtomicLong(0);
        pauseMillis = new AtomicLong(0);
        this.time = time;
        subscribers = new ArrayList<>();
        isPaused = false;
        isStopped = true;
    }

    public void subscribe(Observer observer){
        subscribers.add(observer);
    }

    public void hang(){
        if (isStopped) {
            throw new RuntimeException("Alarm is stopped");
        } else {
            synchronized (testSyncObject){
                isStopped = true;
                testSyncObject.notify();
            }

            pauseMillis.set(newSecondMillis.get() + 1000);
            time = 0;
            observe();
        }
    }
    public void pause() {
        if (isPaused) {
            throw new RuntimeException("Alarm is already paused");
        } else if (isStopped) {
            throw new RuntimeException("Alarm is already stopped");
        } else {
            synchronized (testSyncObject){
                isPaused = true;
                testSyncObject.notify();
            }
            pauseMillis.set(System.currentTimeMillis());
        }
    }

    public void launch(){
        if (isPaused) {
            synchronized (syncObj){
                isPaused = false;
                syncObj.notify();
            }

        } else if (isStopped) {
            synchronized (syncObj) {
                isStopped = false;
                syncObj.notify();
            }
        } else {
            throw new RuntimeException("Alarm is already working");
        }
    }

   /* @Override
    public void run() {
        isStopped = false;
        while (!isInterrupted()) {
            if (!isPaused && !isStopped) {
                try {
                    observe();
                    sleep(1000);
                    while (isPaused || isStopped) {
                        synchronized (syncObj) {
                            System.out.println("Enter in wait");
                            syncObj.wait();
                            System.out.println("exit from wait");
                        }
                        sleep(pauseMillis.get() - newSecondMillis.get());
                    }
                    synchronized (syncObj) {
                        time++;
                    }
                    newSecondMillis.set(System.currentTimeMillis());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }*/

    @Override
    public void run() {
        isStopped = false;
        new ClockThread().start();
        observe();
        while (!isInterrupted()){
            if (!isPaused && !isStopped){
                try {
                    synchronized (testSyncObject){
                        testSyncObject.wait();
                    }
                    if (isPaused || isStopped) {
                        continue;
                    }
                    time++;
                    observe();
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }

        }
    }

    @Override
    public void observe() {
        Logger.getLogger(TAG).log(Level.INFO,this::toString);
        service.submit(updateSubscribers);
    }

    @Override
    public String toString() {
        return new Date(time * 1000).toString();
    }

    public long getTime() {
        return time;
    }

    private class ClockThread extends Thread {
        @Override
        public void run() {

            while (!isInterrupted()){
                try {
                    if (isPaused || isStopped) {
                        synchronized (syncObj){
                            syncObj.wait();
                        }
                    }

                    sleep(1000);
                    synchronized (testSyncObject){
                        testSyncObject.notify();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
