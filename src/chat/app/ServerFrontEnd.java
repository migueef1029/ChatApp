/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat.app;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author Shawn
 */
public class ServerFrontEnd extends Application {
    
    private boolean clientRunning;
    private ServerBackEnd backEnd;
    
    @Override
    public void start(Stage primaryStage) {
        initGUI(primaryStage);
    }
    
    private void initGUI(Stage primaryStage) {
        
        Label serverStatus = new Label("Click start to operate the server");
        
        Button startButton = new Button("Start");
        Button endButton = new Button("End");
        
        startButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(!clientRunning) {
                    clientRunning = true;
                    backEnd = new ServerBackEnd();
                    backEnd.setServerRunning(true);
                    Thread serverThread = new Thread(backEnd);
                    serverThread.start();
                    serverStatus.setText("Server is running");
                }
            }
        });
        
        endButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(clientRunning) {
                    clientRunning = false;
                    backEnd.setServerRunning(false);
                    backEnd.shutdown();
                    serverStatus.setText("Server is shutdown");
                }
            }
        });
        
        GridPane root = new GridPane();
        root.add(serverStatus, 0, 0);
        root.add(startButton, 0, 1);
        root.add(endButton, 1, 1);
        
        Scene scene = new Scene(root, 250, 100);
        primaryStage.setTitle("Server");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                if(clientRunning) {
                    clientRunning = false;
                    backEnd.shutdown();
                    primaryStage.close();
                    event.consume();
                }
            }
        });
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
