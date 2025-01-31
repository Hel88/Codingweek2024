package eu.telecomnancy.codingweek.utils;

import eu.telecomnancy.codingweek.global.Conversations;
import eu.telecomnancy.codingweek.global.FileAccess;
import eu.telecomnancy.codingweek.global.Messages;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

public class DataConversationsUtils {

    private final String filePath;
    private static DataConversationsUtils instance;
    private final JSONObject data;

    private DataConversationsUtils() throws IOException {
        FileAccess fileAccess = new FileAccess();
        this.filePath = fileAccess.getPathOf("conversations.json");
        File file = new File(filePath);
        String fileContent = Files.readString(file.toPath());
        data = new JSONObject(fileContent);
    }

    public static synchronized DataConversationsUtils getInstance() throws IOException {
        if (instance == null) {
            instance = new DataConversationsUtils();
        }
        return instance;
    }

    public int addConversation(String user1, String user2, String idMessages) throws IOException {
        int id = checkIfConversationExist(user1, user2);

        if(id == -1){
            id = newId();
        }
        else {
            return id;
        }

        JSONObject conversationObject = new JSONObject();
        conversationObject.put("id", id);
        conversationObject.put("user1", user1);
        conversationObject.put("user2", user2);
        conversationObject.put("idMessages", idMessages);

        data.put(Integer.toString(id), conversationObject);
        try (FileWriter file = new FileWriter(filePath)) {
            file.write(data.toString());
            file.flush();
        }
        return id;
    }

    public void addIdMessagesToConversation(int id, String idMessages) throws IOException {
        JSONObject conversationObject = data.getJSONObject(Integer.toString(id));
        if(!conversationObject.getString("idMessages").equals("")) {
            idMessages = conversationObject.getString("idMessages") + "," + idMessages;
        }
        conversationObject.put("idMessages", idMessages);
        data.put(Integer.toString(id), conversationObject);
        try (FileWriter file = new FileWriter(filePath)) {
            file.write(data.toString());
            file.flush();
        }
    }

    public void addIdMessagesToConversation(Conversations conversations, String idMessages) throws IOException {
        JSONObject conversationObject = data.getJSONObject(Integer.toString(conversations.getId()));
        if(!idMessages.equals("")) {
            idMessages = conversationObject.getString("idMessages") + "," + idMessages;
        }
        conversationObject.put("idMessages", idMessages);
        data.put(Integer.toString(conversations.getId()), conversationObject);
        try (FileWriter file = new FileWriter(filePath)) {
            file.write(data.toString());
            file.flush();
        }
    }

    public int checkIfConversationExist(String user1, String user2) throws IOException {
        for (String key : data.keySet()) {
            JSONObject conversationObject = data.getJSONObject(key);
            String user3 = conversationObject.getString("user1");
            String user4 = conversationObject.getString("user2");
            if ((user1.equals(user3) && user2.equals(user4)) || (user1.equals(user4) && user2.equals(user3))) {
                return conversationObject.getInt("id");
            }
        }
        return -1;
    }

    public ArrayList<Conversations> getConversations() throws IOException {
        ArrayList<Conversations> conversations = new ArrayList<>();
        for (String key : data.keySet()) {
            JSONObject conversationObject = data.getJSONObject(key);
            int id = conversationObject.getInt("id");
            String user1 = conversationObject.getString("user1");
            String user2 = conversationObject.getString("user2");
            String idMessages = conversationObject.getString("idMessages");
            conversations.add(new Conversations(id, user1, user2, idMessages));
        }
        return conversations;
    }

    public Conversations getConversationById(String id) {
        JSONObject conversationObject = data.getJSONObject(id);
        int idConversation = conversationObject.getInt("id");
        String user1 = conversationObject.getString("user1");
        String user2 = conversationObject.getString("user2");
        String idMessages = conversationObject.getString("idMessages");
        return new Conversations(idConversation, user1, user2, idMessages);
    }

    public ArrayList<Messages> getMessagesFromConversation(Conversations conversations) throws IOException {
        ArrayList<Messages> messages = new ArrayList<>();
        String[] idMessages = conversations.getIdMessages().split(",");
        if (idMessages[0].equals("")) {
            return messages;
        }
        for (String idMessage : idMessages) {
            messages.add(DataMessagesUtils.getInstance().getMessageById(Integer.parseInt(idMessage)));
        }
        messages.sort((o1, o2) -> {
            return Integer.compare(o1.getId(), o2.getId());
        });
        return messages;
    }

    public ArrayList<Conversations> getConversationsByUser(String username) throws IOException {
        ArrayList<Conversations> conversations = new ArrayList<>();
        for (String key : data.keySet()) {
            JSONObject conversationObject = data.getJSONObject(key);
            int id = conversationObject.getInt("id");
            String user1 = conversationObject.getString("user1");
            String user2 = conversationObject.getString("user2");
            String idMessages = conversationObject.getString("idMessages");
            if (user1.equals(username) || user2.equals(username)) {
                conversations.add(new Conversations(id, user1, user2, idMessages));
            }
        }
        return conversations;
    }

    public Conversations getNewConversationWith(String user1, String user2) throws IOException {
        Conversations conversation = getConversationWith(user1, user2);
        if (conversation == null) {
            int id = addConversation(user1, user2, "");
            DataUsersUtils.getInstance().addIdConversationToUser(user1, id);
            DataUsersUtils.getInstance().addIdConversationToUser(user2, id);
            return new Conversations(id, user1, user2, "");
        } else{
            return conversation;
        }
    }

    public Conversations getConversationWith(String user1, String user2) throws IOException {
        for (String key : data.keySet()) {
            JSONObject conversationObject = data.getJSONObject(key);
            String user3 = conversationObject.getString("user1");
            String user4 = conversationObject.getString("user2");
            if ((user1.equals(user3) && user2.equals(user4)) || (user1.equals(user4) && user2.equals(user3))) {
                int id = conversationObject.getInt("id");
                String idMessages = conversationObject.getString("idMessages");
                return new Conversations(id, user1, user2, idMessages);
            }
        }
        return null;
    }

    public int newId() {
        int id = 0;
        for (String key : data.keySet()) {
            int keyInt = Integer.parseInt(key);
            if (keyInt > id) {
                id = keyInt;
            }
        }
        return id + 1;
    }

    public String getFilePath() {
        return filePath;
    }
}
