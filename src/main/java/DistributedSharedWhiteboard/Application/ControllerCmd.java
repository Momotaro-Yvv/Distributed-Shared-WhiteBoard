package DistributedSharedWhiteboard.Application;

/**
 * This is a helper class that allow non-GUI thread to access
 * controller's methods
 */
public class ControllerCmd {

    protected String cmd, param;

    ControllerCmd(String cmd, String param) {
        this.cmd = cmd;
        this.param = param;
    }
}
