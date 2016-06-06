/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat.app;

import java.io.Serializable;
import java.net.Socket;
import java.util.List;

/**
 *
 * @author Shawn
 */
public class Message implements Serializable {
    public static final int REGISTER = 0, LOGIN = 1, SEARCH = 2, ADD = 3, CALL = 4, END = 5, CHAT = 6;
    private String message;
    private int type;
    private boolean userCreated;
    private boolean loggedIn;
    private List usersList;
    private String user;
    private String userToSend;
    private Socket socket;

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the type
     */
    public int getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * @return the userCreated
     */
    public boolean isUserCreated() {
        return userCreated;
    }

    /**
     * @param userCreated the canRegister to set
     */
    public void setUserCreated(boolean userCreated) {
        this.userCreated = userCreated;
    }

    /**
     * @return the loggedIn
     */
    public boolean isLoggedIn() {
        return loggedIn;
    }

    /**
     * @param loggedIn the loggedIn to set
     */
    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }
    
    /**
     * @return the usersList
     */
    public List getUsersList() {
        return usersList;
    }

    /**
     * @param usersList the usersList to set
     */
    public void setUsersList(List usersList) {
        this.usersList = usersList;
    }

    /**
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * @return the userToSend
     */
    public String getUserToSend() {
        return userToSend;
    }

    /**
     * @param userToSend the userToSend to set
     */
    public void setUserToSend(String userToSend) {
        this.userToSend = userToSend;
    }

    /**
     * @return the socket
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * @param socket the socket to set
     */
    public void setSocket(Socket socket) {
        this.socket = socket;
    }
    
    
    
    
}
