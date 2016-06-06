/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat.app;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Shawn
 */
public class LoginGUIFrontEndController implements Initializable {
    
    @FXML
    private AnchorPane mainPane;
    @FXML
    private Label errorLabel;
    @FXML
    private PasswordField password;
    @FXML
    private TextField account;

    private static ClientBackEnd client;
    private boolean registering;
    
    public LoginGUIFrontEndController () {
        client = new ClientBackEnd(this);
        Thread clientThread = new Thread(client);
        clientThread.setPriority(Thread.MIN_PRIORITY);
        clientThread.start();
    }
    
    public static ClientBackEnd getClient() {
        return client;
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    @FXML
    private void handlePassword(KeyEvent event) {
        if(event.getCode() == KeyCode.ENTER) {
            sendAccountInfos();
        }
    }
    
    
    @FXML
    public void handleRegister(MouseEvent event) {
        createRegisterDialog();
    }
    
    public void createRegisterDialog() {
        
        if(!registering) {
            RegisterDialog dialog = new RegisterDialog(client);
            dialog.start();
            registering = true;
        }
        
    }
    
    public void login() {
        Stage stage = (Stage) mainPane.getScene().getWindow();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                stage.close();
            }
        });
    }
    
    public void setRegistering(boolean registering) {
        this.registering = registering;
    }
    
    @FXML
    private void handleLogin(MouseEvent event) {
        sendAccountInfos();
    }
    
    private void sendAccountInfos() {
        Message message = new Message();
        message.setType(Message.LOGIN);
        message.setMessage(account.getText() + " " + password.getText());
        client.sendMessage(message);
    }
    
    public void displayError(String message) {
        errorLabel.setText(message);
    }
    
}
