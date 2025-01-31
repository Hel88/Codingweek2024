package eu.telecomnancy.codingweek.utils;

import com.calendarfx.model.Calendar;
import eu.telecomnancy.codingweek.global.Annonce;
import eu.telecomnancy.codingweek.global.FileAccess;
import eu.telecomnancy.codingweek.global.User;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;


public class DataUsersUtils {

    // Fields
    private final String filePath;
    private static DataUsersUtils instance;
    private final JSONObject data;

    // Private constructor to prevent instantiation
    private DataUsersUtils() throws IOException {
        FileAccess fileAccess = new FileAccess();
        this.filePath = fileAccess.getPathOf("users.json");
        File file = new File(filePath);
        String fileContent = Files.readString(file.toPath());
        data = new JSONObject(fileContent);
    }

    // Public method to get the instance of the singleton
    public static synchronized DataUsersUtils getInstance() throws IOException {
        if (instance == null) {
            instance = new DataUsersUtils();
        }
        return instance;
    }

    // Getters
    public String getFilePath() {
        return filePath;
    }

    public JSONObject getData() {
        return data;
    }

    // Methods
    public boolean isUserNameUnique(String userName) throws IOException {
        // Method related to the creation of a new user

        // Check if the username is already in use
        return !data.has(userName);
    }

    public boolean doesUserExist(String userName) throws IOException {
        // Method related to the authentication of a user

        // Check if the username exists
        return data.has(userName);
    }

    public void addUser(String userName, String password, String email, String lastName, String firstName, String address, String city) throws IOException {
        // Method related to the creation of a new user

        // Create a JSON object with user information
        JSONObject userObject = new JSONObject();
        String hashedPassword = hashPassword(password);
        userObject.put("password", hashedPassword);
        userObject.put("email", email);
        userObject.put("lastName", lastName);
        userObject.put("firstName", firstName);
        userObject.put("address", address);
        userObject.put("city", city);
        userObject.put("annonces", "");
        userObject.put("transactionsReferent", "");
        userObject.put("transactionsClient", "");
        userObject.put("transactions", "");
        // Create a calendar for the user
        DataCalendarUtils dataCalendarUtils = DataCalendarUtils.getInstance();
        userObject.put("planning", String.valueOf(dataCalendarUtils.store(new Calendar("Agenda de "+ userName))));
        userObject.put("eval", "");
        userObject.put("solde", 100);
        userObject.put("isAdmin", false);
        userObject.put("idConversations", "");

        // Add the user to the JSON file
        data.put(userName, userObject);
        try (FileWriter file = new FileWriter(filePath)) {
            file.write(data.toString());
            file.flush();
        }
    }

    public User getUserByUserName(String userName) throws IOException {
        // Method related to the authentication of a user

        // Retrieve the User object from the JSON file
        JSONObject userObject = data.getJSONObject(userName);
        return new User(userName, userObject.getString("password"), userObject.getString("firstName"), userObject.getString("lastName"), userObject.getString("email"), userObject.getString("address"), userObject.getString("city"), userObject.getString("annonces"), userObject.getString("transactionsReferent"), userObject.getString("transactionsClient"), userObject.getInt("planning"), userObject.getString("eval"), userObject.getInt("solde"), userObject.getBoolean("isAdmin"), userObject.getString("idConversations"));
    }

    public void addIdConversationToUser(String userName, int id) throws IOException {
        User user = getUserByUserName(userName);
        String conversations = user.getIdConversations();
        if (conversations.isEmpty()) {
            conversations = String.valueOf(id);
        } else {
            conversations = conversations + "," + id;
        }

        user.setIdConversations(conversations);

        updateUser(user);
    }

    public static String hashPassword(String password) {
        try {
            // Create MessageDigest instance for MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // Add password bytes to digest
            md.update(password.getBytes());

            // Get the hash's bytes
            byte[] bytes = md.digest();

            // These bytes[] has bytes in decimal format. Convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }

            // Get complete hashed password in hex format
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean checkPassword(String userName, String password) throws IOException {
        // Method related to the authentication of a user
        User user = getUserByUserName(userName);
        String hashedPassword = hashPassword(password);
        return user.getPassword().equals(hashedPassword);
    }

    public void updateUser(User User) throws IOException {
        // Method related to the modification of a user

        // Create a JSON object with user information
        JSONObject userObject = new JSONObject();
        userObject.put("password", User.getPassword());
        userObject.put("email", User.getEmail());
        userObject.put("lastName", User.getLastName());
        userObject.put("firstName", User.getFirstName());
        userObject.put("address", User.getAddress());
        userObject.put("city", User.getCity());
        userObject.put("annonces", User.getAnnonces());
        userObject.put("transactionsReferent", User.getTransactionsReferent());
        userObject.put("transactionsClient", User.getTransactionsClient());
        userObject.put("planning", User.getPlanning());
        userObject.put("eval", User.getEval());
        userObject.put("solde", User.getSolde());
        userObject.put("isAdmin", User.getIsAdmin());
        userObject.put("idConversations", User.getIdConversations());

        // Add the user to the JSON file
        data.put(User.getUserName(), userObject);
        try (FileWriter file = new FileWriter(filePath)) {
            file.write(data.toString());
            file.flush();
        }
    }

    public void deleteUser(String userName) throws IOException {
        // Method related to the deletion of a user

        // Remove the user from the JSON file
        data.remove(userName);
        try (FileWriter file = new FileWriter(filePath)) {
            file.write(data.toString());
            file.flush();
        }
    }

    public int getCalendarOf(String userName) {
        // Method related to the modification of a user

        // Create a JSON object with user information
        JSONObject userObject = data.getJSONObject(userName);
        return userObject.getInt("planning");
    }

    public void addAnnonceToUser(String referent, int id) throws IOException {
        User user = getUserByUserName(referent);
        String annonces = user.getAnnonces();
        if (annonces.isEmpty()) {
            annonces = String.valueOf(id);
        } else {
            annonces = annonces + "," + id;
        }

        user.setAnnonces(annonces);

        updateUser(user);
    }

    public void addTransactionReferentToUser(String referent, int id) throws IOException {
        User user = getUserByUserName(referent);
        String transactions = user.getTransactionsReferent();
        if (transactions.isEmpty()) {
            transactions = String.valueOf(id);
        } else {
            transactions = transactions + "," + id;
        }

        user.setTransactionsReferent(transactions);

        updateUser(user);
    }

    public void addTransactionClientToUser(String client, int id) throws IOException {
        User user = getUserByUserName(client);
        String transactions = user.getTransactionsClient();
        if (transactions.isEmpty()) {
            transactions = String.valueOf(id);
        } else {
            transactions = transactions + "," + id;
        }

        user.setTransactionsClient(transactions);

        updateUser(user);
    }

    public void addConversationToUser(String userName, int id) throws IOException {
        User user = getUserByUserName(userName);
        String conversations = user.getIdConversations();
        if (conversations.isEmpty()) {
            conversations = String.valueOf(id);
        } else {
            conversations = conversations + "," + id;
        }

        user.setIdConversations(conversations);

        updateUser(user);
    }

    public void addEvalToUser(String referent, int id) throws IOException {
        User user = getUserByUserName(referent);
        String evals = user.getEval();
        if (evals.isEmpty()) {
            evals = String.valueOf(id);
        } else {
            evals = evals + "," + id;
        }

        user.setEval(evals);

        updateUser(user);
    }

    public void setUserSleeping(String userName) throws IOException {
        DataAnnoncesUtils.getInstance().getAnnoncesByUsername(userName).forEach(annonce -> {
            try {
                DataAnnoncesUtils.getInstance().modifyAnnonce(annonce.getId(), annonce.getTitre(), annonce.getDescription(), String.valueOf(annonce.getPrix()), annonce.getCategorie(), annonce.getReferent(), false, annonce.getPlanning());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void setUserNotSleeping(String userName) throws IOException {
        DataAnnoncesUtils.getInstance().getAnnoncesByUsername(userName).forEach(annonce -> {
            try {
                DataAnnoncesUtils.getInstance().modifyAnnonce(annonce.getId(), annonce.getTitre(), annonce.getDescription(), String.valueOf(annonce.getPrix()), annonce.getCategorie(), annonce.getReferent(), true, annonce.getPlanning());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public boolean isUserSleeping(String userName) throws IOException {
        ArrayList<Annonce> annonces = DataAnnoncesUtils.getInstance().getAnnoncesByUsername(userName);
        if(annonces.isEmpty()) return false;
        Annonce uneAnnonce = annonces.get(0);
        return !uneAnnonce.getActif();
    }

    public void deleteAnnonceFromUser(String referent, String s) throws IOException {
        // Method related to the deletion of an annonce

        // Create a JSON object with user information
        JSONObject userObject = data.getJSONObject(referent);
        String annonces = userObject.getString("annonces");
        String[] annoncesArray = annonces.split(",");
        String newAnnonces = "";
        for (String annonce : annoncesArray) {
            if (!annonce.equals(s)) {
                if (newAnnonces.isEmpty()) {
                    newAnnonces = annonce;
                } else {
                    newAnnonces = newAnnonces + "," + annonce;
                }
            }
        }
        userObject.put("annonces", newAnnonces);

        // Add the user to the JSON file
        data.put(referent, userObject);
        try (FileWriter file = new FileWriter(filePath)) {
            file.write(data.toString());
            file.flush();
        }
    }
}