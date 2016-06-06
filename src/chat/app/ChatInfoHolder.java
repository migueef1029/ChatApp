/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat.app;

import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 *
 * @author Shawn
 */
public class ChatInfoHolder {
    private Label user;
    private TextArea messageArea;
    private TextArea messageField;

    public ChatInfoHolder() {
        messageArea = new TextArea();
        messageField = new TextArea();
        messageArea.setPrefRowCount(20);
        messageField.setPrefHeight(1);
        messageField.setPrefRowCount(1);
        messageArea.setWrapText(true);
        messageField.setWrapText(true);
        messageArea.setEditable(false);
    }
    
    /**
     * @return the user
     */
    public Label getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(Label user) {
        this.user = user;
    }

    /**
     * @return the messageArea
     */
    public TextArea getMessageArea() {
        return messageArea;
    }

    /**
     * @param messageArea the messageArea to set
     */
    public void setMessageArea(TextArea messageArea) {
        this.messageArea = messageArea;
    }

    /**
     * @return the messageField
     */
    public TextArea getMessageField() {
        return messageField;
    }

    /**
     * @param messageField the messageField to set
     */
    public void setMessageField(TextArea messageField) {
        this.messageField = messageField;
    }
}
