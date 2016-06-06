/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat.app;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
public class RegisterDialogGUIController implements Initializable {

    @FXML
    private AnchorPane mainPane;
    @FXML
    private TextField account;
    @FXML
    private PasswordField password;
    @FXML
    private Label errorLabel;
    
    private ClientBackEnd client;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    public void setClient(ClientBackEnd client) {
        this.client = client;
    }
    
    @FXML
    private void handleRegister(MouseEvent event) {
        handleRegisteration();
    }
    
    @FXML
    public void handleCancel(MouseEvent event) {
        closeWindow();
    }
    
    @FXML
    public void handlePasswordEnter(KeyEvent event) {
        if(event.getCode() == KeyCode.ENTER) {
            handleRegisteration();
        }
    }
    
    public void closeWindow() {
        Stage stage = (Stage) mainPane.getScene().getWindow();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                stage.close();
            }
        });
    }
    
    public void displayError(String error) {
        errorLabel.setText(error);
    }
    
    private void handleRegisteration() {
        String username = account.getText();
        String pass = password.getText();
        String userInfo = username + " " + pass;
        Message message = new Message();
        message.setMessage(userInfo);
        message.setType(Message.REGISTER);
        client.sendMessage(message);
    }
    
}
