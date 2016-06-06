/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat.app;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.TextAlignment;

/**
 * FXML Controller class
 *
 * @author Shawn
 */
public class MainGUIFrontEndController implements Initializable {

    @FXML
    private Label usersName, contactsLabel, recentLabel, infoLabel;
    @FXML
    private TextField searchField;
    private ClientBackEnd client;
    @FXML
    private VBox vbox, chatBox;
    @FXML
    private BorderPane borderPane;
    private Label labelSelected;
    private volatile HashMap<String, ChatInfoHolder> contactMap;
    private volatile HashMap<String, ChatInfoHolder> recentMap;
    private volatile HashMap<String, ChatInfoHolder> searchMap;
    private String currentCatagory;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        contactMap = new HashMap();
        recentMap = new HashMap();
        searchMap = new HashMap();
        currentCatagory = "contacts";
    }    
    
    public void setClient(ClientBackEnd client) {
        this.client = client;
    }
    
    public void setUsername(String user) {
        usersName.setText(user);
    }
    
    @FXML
    private void handleSearch(KeyEvent event) {
        if(event.getCode() == KeyCode.ENTER) {
            //search the person and display the search
            Message message = new Message();
            message.setMessage(searchField.getText());
            message.setType(Message.SEARCH);
            client.sendMessage(message);
        }
    }
    
    public void displayUsersSearched(List<String> users) {
        Platform.runLater(new Runnable() {
           @Override
           public void run() {
               vbox.getChildren().clear();
           }
        });
        for(String user : users) {
            Label label = new Label(user);
            label.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if(labelSelected != null) 
                        labelSelected.setStyle("-fx-background-color: #FFFFFF");
                    label.setStyle("-fx-background-color: #cceeff;");
                    labelSelected = label;
                    Platform.runLater(new Runnable() {
                       @Override
                       public void run() {
                           chatBox.getChildren().clear();
                       }
                    });
                    ChatInfoHolder holder;
                    if(searchMap.get(labelSelected.getText()) == null) {
                        holder = new ChatInfoHolder();
                        searchMap.put(labelSelected.getText(), holder);
                    }
                    else {
                        holder = searchMap.get(labelSelected.getText());
                    }
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            infoLabel.setText(labelSelected.getText());
                            TextArea textToSend = holder.getMessageField();
                            TextArea textArea = holder.getMessageArea();
                            chatBox.getChildren().add(textArea);
                            chatBox.getChildren().add(textToSend);
                            
                            textToSend.setOnKeyReleased(new EventHandler<KeyEvent>() {
                                final KeyCombination combo = new KeyCodeCombination(KeyCode.ENTER);
                                @Override
                                public void handle(KeyEvent event) {
                                    if(combo.match(event)) {
                                        String message = usersName.getText() + System.lineSeparator() + textToSend.getText() + System.lineSeparator();
                                        textArea.appendText(message);
                                        Message clientMessage = new Message();
                                        clientMessage.setType(Message.CHAT);
                                        clientMessage.setMessage(textToSend.getText());
                                        clientMessage.setUser(usersName.getText());
                                        clientMessage.setUserToSend(label.getText());
                                        client.sendMessage(clientMessage);
                                        textToSend.clear();
                                        event.consume();
                                    }
                                } 
                            });
                        }
                    });
                }
            });
            label.setPrefSize(vbox.getMaxWidth(), 75);
            label.setTextAlignment(TextAlignment.CENTER);
            label.setAlignment(Pos.CENTER);
            Platform.runLater(new Runnable() {
               @Override
               public void run() {
                   vbox.getChildren().add(label);
               }
            });
        }
    }
    
    public void handleChatting(Message serverMessage) {
        ChatInfoHolder holder;
        if(contactMap.get(serverMessage.getUser()) == null && recentMap.get(serverMessage.getUser()) == null &&
                searchMap.get(serverMessage.getUser()) == null) {
            holder = new ChatInfoHolder();
            String userSending = serverMessage.getUser();
            TextArea textArea = holder.getMessageArea();
            String textToAppend = userSending + System.lineSeparator() + serverMessage.getMessage() + System.lineSeparator();
            Platform.runLater(new Runnable() {
               @Override
               public void run() {
                   textArea.appendText(textToAppend);
               }
            });
            recentMap.put(serverMessage.getUser(), holder);
        }
        else if(contactMap.get(serverMessage.getUser()) != null){
            holder = contactMap.get(serverMessage.getUser());
            TextArea textArea = holder.getMessageArea();
            String userSending = serverMessage.getUser();
            String textToAppend = userSending + System.lineSeparator() + serverMessage.getMessage() + System.lineSeparator();
            Platform.runLater(new Runnable() {
               @Override
               public void run() {
                   textArea.appendText(textToAppend);
               }
            });
        }
        else if(recentMap.get(serverMessage.getUser()) != null) {
            holder = recentMap.get(serverMessage.getUser());
            TextArea textArea = holder.getMessageArea();
            String userSending = serverMessage.getUser();
            String textToAppend = userSending + System.lineSeparator() + serverMessage.getMessage() + System.lineSeparator();
            Platform.runLater(new Runnable() {
               @Override
               public void run() {
                   textArea.appendText(textToAppend);
               }
            });
        }
        
        else if(searchMap.get(serverMessage.getUser()) != null) {
            holder = searchMap.get(serverMessage.getUser());
            TextArea textArea = holder.getMessageArea();
            String userSending = serverMessage.getUser();
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    textArea.appendText(userSending + System.lineSeparator() + serverMessage.getMessage() + System.lineSeparator());
                }
            });  
        }
    }
    
    public void call(boolean callSuccessful) {
        if(callSuccessful) {
            labelSelected.setStyle("-fx-background-color: ff9966;");
        }
    }
    
    @FXML
    private void handleCall(MouseEvent event) {
        if(currentCatagory.equalsIgnoreCase("contacts") && labelSelected != null) {
            //call the person
            client.setAlreadyCalling(true);
            client.handleCall(labelSelected.getText());
        }
        else if(currentCatagory.equalsIgnoreCase("recent")) {
            //call the group
            
        }
    }
    
    @FXML
    private void handleEnd(MouseEvent event) {
        
    }
    
    @FXML
    private void handleAdd(MouseEvent event) {
        if(currentCatagory.equalsIgnoreCase("contacts")) {
            //add the person
            
        }
        else if(currentCatagory.equalsIgnoreCase("recent")) {
            
        }
    }
    
    @FXML
    private void handleContacts(MouseEvent event) {
        contactsLabel.setTextFill(Paint.valueOf("#3399FF"));
        recentLabel.setTextFill(Paint.valueOf("black"));
        currentCatagory = "contacts";
        //display contactsMap
        Iterator iterator = contactMap.entrySet().iterator();
        
    }
    
    @FXML
    private void handleRecent(MouseEvent event) {
        vbox.getChildren().clear();
        contactsLabel.setTextFill(Paint.valueOf("black"));
        recentLabel.setTextFill(Paint.valueOf("#3399FF"));
        currentCatagory = "recent";
        //display recentMap
        Iterator iterator = recentMap.entrySet().iterator();
        while(iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();
            Label label = new Label(pair.getKey().toString());
            label.setPrefSize(vbox.getMaxWidth(), 75);
            label.setTextAlignment(TextAlignment.CENTER);
            label.setAlignment(Pos.CENTER);
            label.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if(labelSelected != null)
                        labelSelected.setStyle("-fx-background-color: #FFFFFF;");
                    label.setStyle("-fx-background-color: #cceeff;");
                    labelSelected = label;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            chatBox.getChildren().clear();
                            infoLabel.setText(label.getText());
                            if(recentMap.get(label.getText()) == null) {
                                ChatInfoHolder holder = new ChatInfoHolder();
                                recentMap.put(label.getText(), holder);
                                chatBox.getChildren().add(holder.getMessageArea());
                                chatBox.getChildren().add(holder.getMessageField());
                            }
                            else {
                                ChatInfoHolder holder = recentMap.get(label.getText());
                                TextArea textToSend = holder.getMessageField();
                                TextArea textArea = holder.getMessageArea();
                                textToSend.setOnKeyPressed(new EventHandler<KeyEvent>() {
                                    final KeyCombination combo = new KeyCodeCombination(KeyCode.ENTER);
                                    @Override
                                    public void handle(KeyEvent event) {
                                        if(combo.match(event)) {
                                            String message = usersName.getText() + System.lineSeparator() + textToSend.getText() + System.lineSeparator();
                                            textArea.appendText(message);
                                            Message clientMessage = new Message();
                                            clientMessage.setType(Message.CHAT);
                                            clientMessage.setMessage(textToSend.getText());
                                            clientMessage.setUser(usersName.getText());
                                            clientMessage.setUserToSend(label.getText());
                                            client.sendMessage(clientMessage);
                                            textToSend.clear();
                                            event.consume();
                                        }
                                    }
                                });
                                chatBox.getChildren().add(holder.getMessageArea());
                                chatBox.getChildren().add(holder.getMessageField());
                            }
                        }
                    });
                }
            });
            Platform.runLater(new Runnable() {
               @Override
               public void run() {
                   vbox.getChildren().add(label);
               }
            });

        }
    }
    
}
