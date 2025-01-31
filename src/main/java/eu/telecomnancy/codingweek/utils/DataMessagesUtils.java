package eu.telecomnancy.codingweek.utils;

import eu.telecomnancy.codingweek.global.FileAccess;
import eu.telecomnancy.codingweek.global.Messages;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

public class DataMessagesUtils {

    private final String filePath;
    private static DataMessagesUtils instance;
    private final JSONObject data;

    private DataMessagesUtils() throws IOException {
        FileAccess fileAccess = new FileAccess();
        this.filePath = fileAccess.getPathOf("messages.json");
        File file = new File(filePath);
        String fileContent = Files.readString(file.toPath());
        data = new JSONObject(fileContent);
    }

    public static synchronized DataMessagesUtils getInstance() throws IOException {
        if (instance == null) {
            instance = new DataMessagesUtils();
        }
        return instance;
    }

    public void addMessage(String message, String username, String idConversation) throws IOException {
        int id = newId();

        JSONObject messageObject = new JSONObject();
        messageObject.put("id", id);
        messageObject.put("message", message);
        messageObject.put("username", username);
        messageObject.put("idConversation", idConversation);
        DataConversationsUtils.getInstance().addIdMessagesToConversation(Integer.parseInt(idConversation), Integer.toString(id));

        data.put(Integer.toString(id), messageObject);
        try (FileWriter file = new FileWriter(filePath)) {
            file.write(data.toString());
            file.flush();
        }
    }

    public Messages getLastMessageFromConversation(String idConversation) throws IOException {
        Messages message = null;
        ArrayList<Messages> messages = getMessagesFromConversation(idConversation);
        if (!messages.isEmpty()) {
            message = messages.get(messages.size() - 1);
        }
        return message;
    }

    public ArrayList<Messages> getMessages() throws IOException {
        ArrayList<Messages> messages = new ArrayList<>();
        for (String key : data.keySet()) {
            JSONObject messageObject = data.getJSONObject(key);
            int id = messageObject.getInt("id");
            String message = messageObject.getString("message");
            String username = messageObject.getString("expediteur");
            int idConversation = messageObject.getInt("idConversation");
            messages.add(new Messages(id, message, username, idConversation));
        }
        return messages;
    }

    public ArrayList<Messages> getMessagesFromConversation(String idConversation) throws IOException {
        ArrayList<Messages> messages = new ArrayList<>();
        for (String key : data.keySet()) {
            JSONObject messageObject = data.getJSONObject(key);
            String idConversationMessage = messageObject.getString("idConversation");
            if (idConversationMessage.equals(idConversation)) {
                int id = messageObject.getInt("id");
                String message = messageObject.getString("message");
                String username = messageObject.getString("username");
                messages.add(new Messages(id, message, username, Integer.parseInt(idConversation)));
            }
        }
        // sort the messages by id
        messages.sort((o1, o2) -> {
            return Integer.compare(o1.getId(), o2.getId());
        });
        return messages;
    }

    public Messages getMessageById(int id) throws IOException {
        for (String key : data.keySet()) {
            JSONObject messageObject = data.getJSONObject(key);
            int idMessage = messageObject.getInt("id");
            if (idMessage == id) {
                String message = messageObject.getString("message");
                String username = messageObject.getString("username");
                int idConversation = messageObject.getInt("idConversation");
                return new Messages(id, message, username, idConversation);
            }
        }
        return null;
    }

    public int newId() {
        int id = 0;
        for (String key : data.keySet()) {
            JSONObject messageObject = data.getJSONObject(key);
            int idMessage = messageObject.getInt("id");
            if (idMessage > id) {
                id = idMessage;
            }
        }
        return id + 1;
    }

    public String getFilePath() {
        return filePath;
    }
}
