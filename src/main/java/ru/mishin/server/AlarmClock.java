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
            pauseMillis.set(newSecondMillis.get() + 1000);
            time = 0;
            isStopped = true;
            observe();
        }
    }
    public void pause() {
        if (isPaused) {
            throw new RuntimeException("Alarm is already paused");
        } else if (isStopped) {
            throw new RuntimeException("Alarm is already stopped");
        } else {
                pauseMillis.set(System.currentTimeMillis());
                isPaused = true;
        }
    }

    public void launch(){
        if (isPaused) {
            isPaused = false;
            synchronized (syncObj) {
                syncObj.notify();
            }
        } else if (isStopped) {
            isStopped = false;
            synchronized (syncObj) {
                syncObj.notify();
            }
        } else {
            throw new RuntimeException("Alarm is already working");
        }
    }

    @Override
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
}
