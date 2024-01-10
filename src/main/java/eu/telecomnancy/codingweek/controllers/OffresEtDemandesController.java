package eu.telecomnancy.codingweek.controllers;

import eu.telecomnancy.codingweek.Application;
import eu.telecomnancy.codingweek.utils.Annonce;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;

public class OffresEtDemandesController implements Observer{

    private final Application app;
    private String type;
    private ArrayList<Annonce> annonces = new ArrayList<Annonce>();
    @FXML
    private VBox services;
    @FXML
    private VBox materiel;
    @FXML
    private Label serviceLabel;
    @FXML
    private Label materielLabel;


    public OffresEtDemandesController(Application app, String type) {
        this.app = app;
        this.type = type;
        app.addObserver(this);
    }

    public void setType(String type){
        this.type = type;
    }

    public void initialize() throws IOException {

        if (app.getDataAnnoncesUtils() != null) {
            this.annonces = app.getDataAnnoncesUtils().getAnnonces();
        }

        // savoir si on doit afficher les offres ou les demandes

        if (type.equals("Offre")){
            serviceLabel.setText("Offres de services");
            materielLabel.setText("Offres de matériel");
        }
        else if (type.equals("Demande")){
            serviceLabel.setText("Demandes de services");
            materielLabel.setText("Demandes de matériel");
        }

        //afficher les annonces

        for (Annonce annonce : this.annonces){
            HBox hbox = new HBox();
            hbox.setStyle("-fx-background-color: #eeeeee; prefHeight:\"279.0\"");

            Label titre = new Label(annonce.getTitre());
            titre.setPrefWidth(300);
            titre.setPrefHeight(10);
            titre.setWrapText(true);

            Label prix = new Label(annonce.getPrix()+"");
            prix.setPrefWidth(100);
            prix.setPrefHeight(10);
            prix.setWrapText(true);

            Button details = new Button();
            details.setText("Voir les détails");
            details.setOnAction(e -> app.getSceneController().switchToConsulterAnnonce(annonce.getId()));

            hbox.getChildren().addAll(titre, prix, details);

            // choisir dans quelle catégorie afficher l'annonce

            if (type.equals("Offre")){
                if (annonce.getCategorie().equals("OffreMateriel")){
                    materiel.getChildren().add(hbox);
                }
                else if (annonce.getCategorie().equals("OffreService")){
                    services.getChildren().add(hbox);
                }
            }
            else if (type.equals("Demande")){
                if (annonce.getCategorie().equals("DemandeMateriel")){
                    materiel.getChildren().add(hbox);
                }
                else if (annonce.getCategorie().equals("DemandeService")){
                    services.getChildren().add(hbox);
                }
            }
        }    
    }

    @Override
    public void update(String type) {
        if (type == "annonce"){
            try {
                initialize();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
