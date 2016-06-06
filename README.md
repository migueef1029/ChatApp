# ChatApp

### Description
This is a work in progress.

The project allows you to chat with other users similar to skype by using a serversocket for the server and sockets for the clients. Currently it allows you to create accounts and chat and in the future voice chat is expected to be added.

### How It Works
When the user creates a major event that requires access to server it sends a Message object which holds the message appropriate to the event through the ObjectOutputStream to the server. The server receives it and sends another Message object that include information that user wanted.

As of now in the main GUI (not the log in GUI) for the client the user can search another user's name through the search bar and press on the GUI that contains the searched user's id and two textareas will be shown. When the user types a message to the second user, the second user will receive the message and he can see the message in the recent tab. The second user has to be on for this to work.

