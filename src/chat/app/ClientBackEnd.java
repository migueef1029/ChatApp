/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat.app;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javafx.application.Platform;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

/**
 *
 * @author Shawn
 */
public class ClientBackEnd implements Runnable {
    
    private volatile boolean running;
    private final String ip = "localhost";
    private final int port = 25564;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private LoginGUIFrontEndController controller;
    private RegisterDialogGUIController registerController;
    private MainGUIFrontEndController mainGUI;
    private Socket socket;
    private String username;
    private DataOutputStream dOut;
    private DataInputStream dIn;
    private boolean alreadyCalling;
    private boolean callRunning;
   
    public ClientBackEnd(LoginGUIFrontEndController controller) {
        this.controller = controller;
    }
    
    public void setAlreadyCalling(boolean alreadyCalling) {
        this.alreadyCalling = alreadyCalling;
    }
    
    public void setCallRunning(boolean callRunning) {
        this.callRunning = callRunning;
    }
    
    public void setRegisterController(RegisterDialogGUIController registerController) {
        this.registerController = registerController;
    }
    
    public void setMainGUI(MainGUIFrontEndController mainGUI) {
        this.mainGUI = mainGUI;
    }

    @Override
    public void run() {
        running = true;
        try {
            socket = new Socket(ip, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException ex) {
            System.out.println("Cannot connect to the server");
            Platform.runLater(new Runnable() {
                public void run() {
                     controller.displayError("Cannot connect to the server");
                }
            });
        }
        while(running) {
            if(socket != null && socket.isConnected()) {
                try {
                    int num = socket.getInputStream().read();
                    if(num == 1) {
                    Message serverMessage = (Message) in.readObject();
                    switch(serverMessage.getType()) {
                        case Message.LOGIN: {
                            if(serverMessage.isLoggedIn()) {
                                controller.login();
                                MainGUIDialog mainGUIDialog = new MainGUIDialog(this);
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        mainGUIDialog.start();
                                    }
                                });
                                String userInfo = serverMessage.getUser();
                                int spaceIndex = userInfo.indexOf(" ");
                                username = userInfo.substring(0, spaceIndex);
                                Platform.runLater(new Runnable() {
                                   @Override
                                   public void run() {
                                       mainGUI.setUsername(username);
                                   }
                                });
                            }
                            else {
                                Platform.runLater(new Runnable() {
                                   @Override
                                   public void run() {
                                       controller.displayError("User information is incorrect");
                                   }
                                });
                            }
                            break;
                        }
                        case Message.REGISTER: {
                            if(serverMessage.isUserCreated()) {
                                registerController.closeWindow();
                                controller.setRegistering(false);
                            }
                            else {
                                Platform.runLater(new Runnable() {
                                    public void run() {
                                         registerController.displayError("User cannot be created");
                                    }
                                });
                            }
                            break;
                        }
                        case Message.SEARCH: {
                            mainGUI.displayUsersSearched(serverMessage.getUsersList());
                            break;
                        }
                        case Message.CHAT: {
                            mainGUI.handleChatting(serverMessage);
                            break;
                        }
                        case Message.CALL: {
                            if(serverMessage.getUser() != null) {
                                //create thread that writes first before reading
                                handleCall(serverMessage.getUser());
                            }
                        }
                    }
                    }
                    else {
                        
                    }
                } catch (IOException | ClassNotFoundException ex) {
                    System.out.println(ex + "Error at message sorter");
                } 
                
            }
            
        }
    }
    
    public void sendMessage(Message message) {
        if(out != null) {
            try {
                out.writeObject(message);
            } catch (IOException ex) {
                System.out.println("Error cause by attempt to send message to server");
            }
        }
    }
    
    public void shutdown() {
        running = false;
    }
    
    public void handleCall(String userToSendTo) {
        if(!callRunning) {
            Call call = new Call(userToSendTo);
            Thread thread = new Thread(call);
            thread.setPriority(Thread.NORM_PRIORITY);
            thread.start();
            callRunning = true;
        }
    }
    
    private class Call implements Runnable {
        
        private TargetDataLine targetLine;
        private SourceDataLine sourceLine;
        private DataOutputStream outCall;
        private DataInputStream inCall;
        private volatile boolean calling;
        private String userToSendTo;
        
        public Call(String userToSendTo) {
            this.userToSendTo = userToSendTo;
        }
        
        @Override
        public void run() {
        System.out.println("Call running");
        calling = true;
        AudioFormat format = new AudioFormat(44100, 16, 2, true, true);
        DataLine.Info targetInfo = new DataLine.Info(TargetDataLine.class, format);
        DataLine.Info sourceInfo = new DataLine.Info(SourceDataLine.class, format);
        
        try {
            System.out.println("Call is still running");
            outCall = new DataOutputStream(socket.getOutputStream());
            inCall = new DataInputStream(socket.getInputStream());
            
            targetLine = (TargetDataLine) AudioSystem.getLine(targetInfo);
            targetLine.open(format);
            targetLine.start();
            
            sourceLine = (SourceDataLine) AudioSystem.getLine(sourceInfo);
            sourceLine.open(format);
            sourceLine.start();
            System.out.println("Call is still running");
        } catch(LineUnavailableException | IOException ex) {
            System.out.println(ex);
        }
        int numBytesRead;
        byte[] data = new byte[targetLine.getBufferSize() / 5];
            
        try {
            if(alreadyCalling) {
                //write first and then read
                
                while(calling) {
                    numBytesRead = targetLine.read(data, 0, data.length);
                    Message message = new Message();
                    message.setType(Message.CALL);
                    message.setUserToSend(userToSendTo);
                    out.writeObject(message);
                    outCall.writeInt(numBytesRead);
                    outCall.write(data, 0, numBytesRead);
                    
                    int tempNum = inCall.readInt();
                    byte[] bytesData = new byte[tempNum];
                    dIn.readFully(bytesData, 0, bytesData.length);
                    sourceLine.write(bytesData, 0, bytesData.length);
                }
            }
            else {
                //read first and then write
                numBytesRead = targetLine.read(data, 0, data.length);
                
                Message message = new Message();
                message.setType(Message.CALL);
                message.setUserToSend(userToSendTo);
                out.writeObject(message);
                
                int tempNum = inCall.readInt();
                byte[] bytesData = new byte[tempNum];
                dIn.readFully(bytesData, 0, bytesData.length);
                sourceLine.write(bytesData, 0, bytesData.length);
                
                outCall.writeInt(numBytesRead);
                outCall.write(data, 0, numBytesRead);    
            }
            }catch(IOException ex) {
                System.out.println("Error at while loop" + ex);
            }
        }
        
        public void setCalling(boolean calling) {
            this.calling = calling;
        }
    }
}
    
    

