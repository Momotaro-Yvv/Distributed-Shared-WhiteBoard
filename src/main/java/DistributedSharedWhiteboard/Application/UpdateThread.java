package DistributedSharedWhiteboard.Application;

import DistributedSharedWhiteboard.Util.JsonSerializationException;
import DistributedSharedWhiteboard.Util.util;
import DistributedSharedWhiteboard.message.*;
import DistributedSharedWhiteboard.Logger;
import DistributedSharedWhiteboard.ShapeDrawing.ShapeDrawing;
import DistributedSharedWhiteboard.message.ApproveRequest;
import javafx.collections.ObservableList;

import javafx.application.Platform;

import java.io.*;
import java.util.ArrayList;

public class UpdateThread extends Thread {
    private final Logger logger;
    private final BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    private ObservableList<ControllerCmd> eventList;
    private ObservableList<ShapeDrawing> undrawedList;
    private ObservableList<String> msgList;
    private ObservableList<String> userList;
    private String username;

    public UpdateThread(String username, BufferedReader bufferedReader, BufferedWriter bufferedWriter, Logger logger,
                        ObservableList<ControllerCmd> eventList,
                        ObservableList<ShapeDrawing> undrawedList,
                        ObservableList<String> msgList,
                        ObservableList<String> userList) {
        this.username = username;
        this.logger = logger;
        this.bufferedReader = bufferedReader;
        this.bufferedWriter = bufferedWriter;
        this.eventList = eventList;
        this.undrawedList = undrawedList;
        this.msgList = msgList;
        this.userList = userList;
    }

    @Override
    public void run() {
        while (!isInterrupted()) {

            try {
                Message msgFromSvr = util.readMsg(bufferedReader);

                if (msgFromSvr.getClass().getName() == ApproveRequest.class.getName()) {
                    ApproveRequest approveRequest = (ApproveRequest) msgFromSvr;
                    String userJoining = approveRequest.username;

                    Platform.runLater(() -> {
                        eventList.add(new ControllerCmd("showJoinRequest", userJoining));
                    });
                } else if (msgFromSvr.getClass().getName() == KickReply.class.getName()) {
                    KickReply kickReply = (KickReply) msgFromSvr;
                    if (!kickReply.success) {
                        Platform.runLater(() -> {
                            eventList.add(new ControllerCmd("showErrorDialog",
                                    "failed to kick user: " + kickReply.userKickOut));
                        });
                    }

                } else if (msgFromSvr.getClass().getName() == DrawReply.class.getName()) {
                    DrawReply drawReply = (DrawReply) msgFromSvr;
                    if (!drawReply.success) {
                        Platform.runLater(() -> {
                            eventList.add(new ControllerCmd("showErrorDialog",
                                    "failed to update draw"));
                        });
                    }

                } else if (msgFromSvr.getClass().getName() == SendMsgReply.class.getName()) {

                    SendMsgReply sendMsgReply = (SendMsgReply) msgFromSvr;
                    if (!sendMsgReply.success) {
                        Platform.runLater(() -> {
                            eventList.add(new ControllerCmd("showErrorDialog",
                                    "failed to send message"));
                        });
                    }

                } else if (msgFromSvr.getClass().getName() == QuitReply.class.getName()) {
                    QuitReply quitReply = (QuitReply) msgFromSvr;
                    if (quitReply.success) {
                        Platform.runLater(() -> {
                            eventList.add(new ControllerCmd("handleQuit", null));
                        });
                        break;
                    } else {
                        Platform.runLater(() -> {
                            eventList.add(new ControllerCmd("showErrorDialog",
                                    "failed to quit"));
                        });
                    }
                } else if (msgFromSvr.getClass().getName() == UpdateMsgRequest.class.getName()) {

                    UpdateMsgRequest updateMsgRequest = (UpdateMsgRequest) msgFromSvr;
                    String byWhom = updateMsgRequest.byWhom;
                    if (!byWhom.equals(username)) {
                        String msg = updateMsgRequest.msg;
                        String line = byWhom + ": " + msg;
                        Platform.runLater(() -> {
                            msgList.add(line);
                        });
                    }

                } else if (msgFromSvr.getClass().getName() == UpdateDeleteUserRequest.class.getName()) {
                    UpdateDeleteUserRequest updateDeleteUserRequest = (UpdateDeleteUserRequest) msgFromSvr;
                    String userNameToDelete = updateDeleteUserRequest.deleteUserName;
                    if (!userNameToDelete.equals(username)) {
                        Platform.runLater(() -> {
                            userList.remove(userNameToDelete);
                        });
                    }

                } else if (msgFromSvr.getClass().getName() == UpdateShapeRequest.class.getName()) {
                    UpdateShapeRequest updateShapeRequest = (UpdateShapeRequest) msgFromSvr;
                    String byWhom = updateShapeRequest.byWhom;
                    String shapeString = updateShapeRequest.shape;

                    if (!byWhom.equals(username)) {
                        ShapeDrawing shapeDrawing = util.TransferToShape(shapeString);
                        Platform.runLater(() -> {
                            undrawedList.add(shapeDrawing);
                        });
                    }
                } else if (msgFromSvr.getClass().getName() == UpdateUserlistRequest.class.getName()) {
                    UpdateUserlistRequest updateUserlistRequest = (UpdateUserlistRequest) msgFromSvr;
                    String newUserName = updateUserlistRequest.newUserName;
                    if (!newUserName.equals(username)) {
                        Platform.runLater(() -> {
                            userList.add(newUserName);
                        });
                    }
                } else if (msgFromSvr.getClass().getName() == Goodbye.class.getName()) {
                    Goodbye goodbye = (Goodbye) msgFromSvr;

                    Platform.runLater(() -> {
                        if (!goodbye.goodbye.equals("See You!")) {
                            eventList.add(new ControllerCmd("showInfoDialog", goodbye.goodbye));
                        }
                        eventList.add(new ControllerCmd("handleQuit", null));
                    });
                    this.interrupt();
                } else if (msgFromSvr.getClass().getName() == ReloadRequest.class.getName()) {
                    ReloadRequest reloadRequest = (ReloadRequest) msgFromSvr;
                    String[] reloadShapes = reloadRequest.shapes;
                    ArrayList<ShapeDrawing> reloadShapesConverted = new ArrayList<>();
                    for (String shape : reloadShapes) {
                        reloadShapesConverted.add(util.TransferToShape(shape));
                    }

                    if (!reloadRequest.managerName.equals(username)) {
                        Platform.runLater(() -> {
                            // clear canvas
                            eventList.add(new ControllerCmd("clearCanvas", null));

                            // add undrawedList
                            if (reloadShapes.length > 0) {
                                for (ShapeDrawing shape : reloadShapesConverted) {
                                    undrawedList.add(shape);
                                }
                            }

                        });
                    }
                } else if (msgFromSvr.getClass().getName() == ErrorMsg.class.getName()) {
                    ErrorMsg errorMsg = (ErrorMsg) msgFromSvr;
                    logger.logError(errorMsg.msg);
                } else {
                    logger.logDebug(msgFromSvr.toString());
                }
            } catch (JsonSerializationException j) {
                logger.logError(j.toString());
                throw new RuntimeException(j);
            } catch (IOException e) {
                logger.logError(e.toString());
                throw new RuntimeException(e);
            }
        }
    }
}