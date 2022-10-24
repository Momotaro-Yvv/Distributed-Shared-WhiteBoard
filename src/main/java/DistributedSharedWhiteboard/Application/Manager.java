package DistributedSharedWhiteboard.Application;

import DistributedSharedWhiteboard.message.*;

import DistributedSharedWhiteboard.ShapeDrawing.ShapeDrawing;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

import static DistributedSharedWhiteboard.Util.util.TransferToShapeList;
import static DistributedSharedWhiteboard.Util.util.writeMsg;

public class Manager extends User {

    public Manager(String username, Socket socket) throws IOException {
        super(username, socket);
    }

    /**
     * Send manager's response of JoinRequest dialog to server
     *
     * @param decision - manager's decision
     */
    void approveJoinRequest(boolean decision, String approvedUser) {

        try {
            writeMsg(bufferedWriter, new ApproveReply(decision, approvedUser));
        } catch (IOException e) {
            logger.logError("Failed to send SendMsgRequest...");
            throw new RuntimeException(e);
        }
    }

    /**
     * This method is called when manager decides to kick a user from whiteboard,
     * which will send a KickRequest to server.
     *
     * @param userKicked - The username of the user to kick
     */
    void sendKickUserMsg(String userKicked) {
        try {
            writeMsg(bufferedWriter, new KickRequest(userNameProperty().getValue(), userKicked));
        } catch (IOException e) {
            logger.logError("Failed to send KickRequest...");
            throw new RuntimeException(e);
        }
    }

    /**
     * This method is called when manager decide to terminate the application,
     * which will send a TerminateWB to server.
     */
    void sendTerminateMsg() {
        try {
            writeMsg(bufferedWriter, new TerminateWB(userNameProperty().getValue()));
        } catch (IOException e) {
            logger.logError("Failed to send TerminateWB Request...");
            throw new RuntimeException(e);
        }
    }

    /**
     * This method is called when manager create new whiteBoard or open saved whiteboard,
     * which will send a ReloadRequest to server.
     *
     * @param shapeDrawings - list of shapes for new whiteboard
     */
    void sendReloadRequest(List<ShapeDrawing> shapeDrawings) {
        try {
            writeMsg(bufferedWriter, new ReloadRequest(TransferToShapeList(shapeDrawings), userNameProperty().getValue()));
        } catch (IOException e) {
            logger.logError("Failed to send Reload Request...");
            throw new RuntimeException(e);
        }
    }

}
