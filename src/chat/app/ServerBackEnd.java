/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat.app;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

/**
 *
 * @author Shawn
 */
public class ServerBackEnd implements Runnable {

    private volatile boolean serverRunning;
    private ServerSocket serverSocket;
    private final int port = 25564;
    private HashMap threadMap;
    private final String userInfoPath = "UserInfos.txt";
    private int accountNumber;
    
    @Override
    public void run() {
        initFiles();
        try {
            serverSocket = new ServerSocket(port);
            threadMap = new HashMap();
            
            while(serverRunning) {
                Socket socket = serverSocket.accept();
                ServerThread serverThread = new ServerThread(socket);
                Thread thread = new Thread(serverThread);
                thread.start();
            }
        } catch (IOException ex) {
            
        }
    }
    
    public void shutdown() {
        Iterator iterator = threadMap.entrySet().iterator();
        while(iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            ServerThread serverThread = (ServerThread) entry.getValue();
            serverThread.setConnected(false);
        }
        try {
            //put accountNumber in the file
            String info, blockOfInfos = "";
            FileReader reader = new FileReader(userInfoPath);
            BufferedReader bufferedReader = new BufferedReader(reader);
            bufferedReader.readLine();
            File newFile = new File("temp.txt");
            FileWriter writer = new FileWriter(newFile, false);
            File oldFile = new File(userInfoPath);
            writer.write(accountNumber + System.getProperty("line.separator"));
            while((info = bufferedReader.readLine()) != null) {
                writer.write(info + System.getProperty("line.separator"));
            }
            
            writer.close();
            reader.close();
            oldFile.delete();
            newFile.renameTo(new File(userInfoPath));
        } catch (IOException ex) {
            
        }
    }
    
    private void initFiles() {
        
        File file = new File(userInfoPath);
        if (!(file.exists() && file.isFile())) {
            try {
                file.createNewFile();
                FileWriter fileWriter = new FileWriter(userInfoPath, true);
                fileWriter.write("0" + System.getProperty("line.separator"));
                fileWriter.close();
                accountNumber = 0;
            } catch (IOException ex) {
                System.out.println("Error in creating new files");
            }
        }
        else {
            try {
                FileReader fileReader = new FileReader(userInfoPath);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                String specialNumString = bufferedReader.readLine();
                accountNumber = Integer.parseInt(specialNumString);
                bufferedReader.close();
                fileReader.close();
            } catch (IOException ex) {
                System.out.println("Error in reading account number");
            }
        }
    }
    
    private synchronized void createUser(String userInfo) {
            try {
                FileWriter fileWriter = new FileWriter(userInfoPath, true);
                fileWriter.write(userInfo + " " + accountNumber + System.getProperty("line.separator"));
                fileWriter.close();
                accountNumber++;
            } catch (IOException ex) {
                System.out.println("Error caused by creating username");
            } 
    }
    //returns false if the username is found in the system and true if the username was not used already
    private boolean checkUserInfo(String userInfo) {
        String username = userInfo.substring(0, userInfo.indexOf(" "));    
            try {
                FileReader fileReader = new FileReader(userInfoPath);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                String line;
                bufferedReader.readLine();
                while((line = bufferedReader.readLine()) != null) {
                    
                    int space = line.indexOf(" ");
                    String storedUsernames = line.substring(0, space);
                    if(storedUsernames.equalsIgnoreCase(username)) {
                        return false;
                    }
                }
                bufferedReader.close();
            } catch (IOException ex) {
                System.out.println("Error caused by checking username (File not found)");
            } 
            createUser(userInfo);
            return true;
    }
    
    private List findUsers(String user) {
        user = user.trim();
        List listOfUsers = new ArrayList<String>();
        try {
            FileReader fileReader = new FileReader(userInfoPath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            bufferedReader.readLine();
            while((line = bufferedReader.readLine()) != null) {
                int space = line.indexOf(" ");
                String storedUsernames = line.substring(0, space);
                int lastIndexSpace = line.lastIndexOf(" ");
                String number = line.substring(lastIndexSpace + 1, line.length());
                String usernameAndNumber = storedUsernames + " " + number;
                
                if(storedUsernames.equalsIgnoreCase(user)) {
                    listOfUsers.add(usernameAndNumber);
                }
            }
            bufferedReader.close();
            } catch (IOException ex) {
                System.out.println("Error caused by checking username (File not found)");
            }
        return listOfUsers;
    }
    
    public void setServerRunning(boolean serverRunning) {
        this.serverRunning = serverRunning;
    }
    
    private class ServerThread implements Runnable {

        private Socket socket;
        private volatile boolean connected;
        private ObjectInputStream objectIn;
        private ObjectOutputStream objectOut;
        private DataInputStream dataIn;
        private DataOutputStream dataOut;
        
        private String user;
        
        public ServerThread(Socket socket) {
            this.socket = socket;
        }
        
        @Override
        public void run() {
            threadMap.put(socket, this);
            connected = true;
            try {
                objectIn = new ObjectInputStream(socket.getInputStream());
                objectOut = new ObjectOutputStream(socket.getOutputStream());
                dataIn = new DataInputStream(socket.getInputStream());
                dataOut = new DataOutputStream(socket.getOutputStream());
            } catch (IOException ex) {
                System.out.println("Error at creating new object input streams");
            }
            
            while(connected) {
                try {
                    Message clientMessage = (Message) objectIn.readObject();
                    Message serverMessage = new Message();
                    switch(clientMessage.getType()) {
                        case Message.LOGIN: {
                            boolean userLoggedIn = login(clientMessage.getMessage(), serverMessage);
                            serverMessage.setLoggedIn(userLoggedIn);
                            serverMessage.setType(Message.LOGIN);
                            serverMessage.setMessage("");
                            serverMessage.setUser(user);
                            break;
                        }
                        case Message.REGISTER: {
                            boolean userCreated = checkUserInfo(clientMessage.getMessage());
                            serverMessage.setUserCreated(userCreated);
                            serverMessage.setType(Message.REGISTER);
                            break;
                        }
                        case Message.SEARCH: {
                            String userToSearch = clientMessage.getMessage();
                            List usersFound = findUsers(userToSearch);
                            serverMessage.setUsersList(usersFound);
                            serverMessage.setType(Message.SEARCH);
                            break;
                        }
                        case Message.CHAT: {
                            ServerThread serverThread = (ServerThread) threadMap.get(clientMessage.getUserToSend());
                            clientMessage.setUser(user);
                            serverThread.sendMessage(clientMessage);
                            serverMessage = null;
                            break;
                        }
                        case Message.CALL: {
                            
                            ServerThread serverThread = (ServerThread) threadMap.get(clientMessage.getUserToSend());
                            Message message = new Message();
                            message.setType(Message.CALL);
                            message.setUser(user);
                            serverMessage = null;
                            
                            serverThread.sendMessage(message);
                            int numBytesRead = dataIn.readInt();
                            byte[] bytes = new byte[numBytesRead];
                            dataIn.readFully(bytes, 0, numBytesRead);
                            serverThread.dataOut.writeInt(numBytesRead);
                            serverThread.dataOut.write(bytes, 0, numBytesRead);
                            
                            break;
                        }
                    }
                    if(serverMessage != null) {
                        sendMessage(serverMessage);
                        
                    }
                } catch (IOException | ClassNotFoundException ex) {
                    System.out.append("Error in the thread" + ex);
                } 
            }
        }
        
        public void setConnected(boolean connected) {
            this.connected = connected;
        }
        
        private Socket getSocket() {
            return socket;
        }
        
        private boolean login(String userInfo, Message serverMessage) {
            try {
                userInfo = userInfo.trim();
                FileReader fileReader = new FileReader(userInfoPath);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                String line;
                bufferedReader.readLine();
                while((line = bufferedReader.readLine()) != null) {
                    String temp = line.trim();
                    String userInfoTemp = temp.substring(0, temp.lastIndexOf(" "));
                    if(userInfoTemp.equalsIgnoreCase(userInfo)) {
                        String userAccountName = userInfoTemp.substring(0, userInfoTemp.indexOf(" "));
                        String userAccountNumber = temp.substring(temp.lastIndexOf(" ") + 1, temp.length());
                        
                        user = userAccountName + " " + userAccountNumber;
                        
                        threadMap.put(user, threadMap.get(socket));
                        threadMap.remove(socket);
                        return true;
                    }
                }
                bufferedReader.close();
            } catch(IOException ex) {
                System.out.println("Error caused by login");
            }
            return false;
        } 
        private void sendMessage(Message message) {
            try {
                socket.getOutputStream().write(1);
                objectOut.writeObject(message);
            } catch (IOException ex) {
                System.out.println(ex);
            }
        }
        
    }
}
