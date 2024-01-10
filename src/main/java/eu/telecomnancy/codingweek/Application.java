package eu.telecomnancy.codingweek;

import java.io.File;
import java.util.ArrayList;

import com.calendarfx.model.Calendar;
import com.calendarfx.view.CalendarView;
import eu.telecomnancy.codingweek.controllers.Observer;
import eu.telecomnancy.codingweek.controllers.SceneController;
import eu.telecomnancy.codingweek.save.Serialize;
import eu.telecomnancy.codingweek.utils.Annonce;
import eu.telecomnancy.codingweek.utils.DataUsersUtils;
import eu.telecomnancy.codingweek.utils.FileAccess;
import eu.telecomnancy.codingweek.utils.User;
import javafx.stage.Stage;

public class Application extends javafx.application.Application {

    private SceneController sceneController;
    private DataUsersUtils dataUsersUtils;
    private User mainUser;
    private ArrayList<Observer> observers = new ArrayList<Observer>();
    private Annonce annonceAffichee;
    private String categorieAnnonceACreer;


    @Override
    public void start(Stage stage) {
        try {
            this.sceneController = new SceneController(stage, this);
            this.dataUsersUtils = DataUsersUtils.getInstance();
            this.mainUser = null;
        } catch (Exception e) {
            System.out.println("Error while loading the scene controller");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }

    public SceneController getSceneController() {
        return sceneController;
    }

    public DataUsersUtils getDataUsersUtils() {
        return dataUsersUtils;
    }

    public User getMainUser() {
        return mainUser;
    }

    public void setMainUser(User mainUser) {
        this.mainUser = mainUser;
    }

    public void addObserver(Observer o) {
        observers.add(o);
    }

    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    public void notifyObservers(String type) {
        for (Observer obs : observers) {
            obs.update(type);
        }
    }

    public Annonce getAnnonceAffichee() {
        return annonceAffichee;
    }

    public void setAnnonceAffichee(Annonce annonceAffichee) {
        this.annonceAffichee = annonceAffichee;
    }

    public String getCategorieAnnonceACreer() {
        return categorieAnnonceACreer;
    }

    public void setCategorieAnnonceACreer(String categorieAnnonceACreer) {
        this.categorieAnnonceACreer = categorieAnnonceACreer;
    }
}