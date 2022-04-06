package ru.mishin.server;

public interface ServerUIListener {
    public void onChangeNumberClients(int count);
    public void onUpdateTime(long time);
}
