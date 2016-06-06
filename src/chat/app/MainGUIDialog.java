/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat.app;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 *
 * @author Shawn
 */
public class MainGUIDialog {
    
    private ClientBackEnd client;
    
    public MainGUIDialog(ClientBackEnd client) {
        this.client = client;
    }
    
    public void start() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainGUIFrontEnd.fxml"));
        try {
            AnchorPane mainPane = loader.load();
            Stage stage = new Stage();
            Scene scene = new Scene(mainPane);
            stage.setScene(scene);
            stage.show();
            MainGUIFrontEndController controller = (MainGUIFrontEndController) loader.getController();
            controller.setClient(client);
            client.setMainGUI(controller);
        } catch (IOException ex) {
            Logger.getLogger(MainGUIDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
