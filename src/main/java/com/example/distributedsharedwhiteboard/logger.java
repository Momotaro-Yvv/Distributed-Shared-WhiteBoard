package com.example.distributedsharedwhiteboard;

public class logger implements ILogger {

    @Override
    public void logInfo(String msg) {
        System.out.println("Info: "+msg);
    }

    @Override
    public void logWarn(String msg) {
        System.out.println("Warn: "+msg);
    }

    @Override
    public void logError(String msg) {
        System.out.println("Error: "+msg);
    }

    @Override
    public void logDebug(String msg) {
        System.out.println("Debug: "+msg);
    }
}
