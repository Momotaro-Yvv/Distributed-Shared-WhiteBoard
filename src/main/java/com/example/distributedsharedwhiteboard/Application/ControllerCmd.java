package com.example.distributedsharedwhiteboard.Application;

public class ControllerCmd {
    protected String cmd, param;

    ControllerCmd(String cmd, String param) {
        this.cmd = cmd;
        this.param = param;
    }
}
