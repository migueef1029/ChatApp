/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat.app;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 *
 * @author Shawn
 */
public class RegisterDialog {
    
    private ClientBackEnd client;
    
    public RegisterDialog(ClientBackEnd client) {
        this.client = client;
    }

    
    public void start() {
        try {
            // Parent root = FXMLLoader.load(getClass().getResource("RegisterDialogGUI.fxml"));
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("RegisterDialogGUI.fxml"));
            AnchorPane roots = loader.load();
            RegisterDialogGUIController registerController = loader.getController();
            registerController.setClient(client);
            client.setRegisterController(registerController);
            
            Scene scene = new Scene(roots);
            Stage primaryStage = new Stage();
            primaryStage.setScene(scene);
            primaryStage.show();
            primaryStage.setResizable(false);
            
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
}
