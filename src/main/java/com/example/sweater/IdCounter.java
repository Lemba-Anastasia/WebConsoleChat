package com.example.sweater;

public class IdCounter {
    private static IdCounter idCounter = new IdCounter();
    private int id = 0;

    private IdCounter() {

    }

    public static IdCounter getInstance() {
        return idCounter;
    }

    public synchronized int getId(){
        return ++id;
    }
}
