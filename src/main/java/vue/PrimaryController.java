package vue;

import entities.Oeuvre;
import service.OeuvreService;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class PrimaryController implements Initializable {

    // ── Champs du formulaire (Liaison FXML préservée) ─────────────────
    @FXML private TextField txtRecherche;
    @FXML private TextField txtTitre;
    @FXML private TextField txtArtiste;
    @FXML private TextField txtGenre;
    @FXML private TextField txtPrix;
    @FXML private TextField txtQuantite;
    @FXML private CheckBox  chkDisponible;

    // ── Tableau ──────────────────────────────────────────────────
    @FXML private TableView<Oeuvre>            tableOeuvres;
    @FXML private TableColumn<Oeuvre, Integer> colId;
    @FXML private TableColumn<Oeuvre, String>  colTitre;
    @FXML private TableColumn<Oeuvre, String>  colArtiste;
    @FXML private TableColumn<Oeuvre, String>  colGenre;
    @FXML private TableColumn<Oeuvre, Double>  colPrix;
    @FXML private TableColumn<Oeuvre, Integer> colQuantite;
    @FXML private TableColumn<Oeuvre, Boolean> colDisponible;

    // ── Boutons ──────────────────────────────────────────────────
    @FXML private Button btnAjouter;
    @FXML private Button btnModifier;
    @FXML private Button btnSupprimer;
    @FXML private Button btnVider;

    private final OeuvreService            oeuvreService      = new OeuvreService();
    private final ObservableList<Oeuvre>   oeuvreList         = FXCollections.observableArrayList();
    private       Oeuvre                   oeuvreSelectionnee = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // 1. Liaison classique des colonnes du TableView
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colArtiste.setCellValueFactory(new PropertyValueFactory<>("artiste"));
        colGenre.setCellValueFactory(new PropertyValueFactory<>("genre"));
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prix"));
        colQuantite.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        colDisponible.setCellValueFactory(new PropertyValueFactory<>("disponible"));
        
        // 2. Nettoyage et application des icônes sur les boutons
        btnAjouter.setText("➕ Ajouter");
        btnModifier.setText("🔄 Modifier");
        btnSupprimer.setText("🗑️ Supprimer");
        btnVider.setText("🧹 Vider");

        // 3. RECONSTRUCTION RADICALE DE L'INTERFACE (Look Dashboard)
        reconstruireInterfaceMagnifique();
        
        // 4. Charger les données MySQL
        rafraichirTableau();
    }

    private void reconstruireInterfaceMagnifique() {
        try {
            Pane rootParent = (Pane) tableOeuvres.getParent();
            
            // On nettoie l'interface FXML d'origine
            rootParent.getChildren().clear(); 

            // --- BARRE LATÉRALE GAUCHE (Formulaire de saisie) ---
            VBox sidebar = new VBox(15);
            sidebar.setPadding(new Insets(25));
            sidebar.setPrefWidth(320);
            sidebar.setStyle("-fx-background-color: #FFFFFF; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.03), 10, 0, 0, 0);");

            // Titre de la barre latérale
            Label lblSideTitre = new Label("ŒUVRE D'ART");
            lblSideTitre.setId("lblSideTitre");
            lblSideTitre.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
            lblSideTitre.setStyle("-fx-padding: 0 0 10 0;");

            sidebar.getChildren().addAll(
                lblSideTitre,
                new Label("Titre de l'œuvre"), txtTitre,
                new Label("Nom de l'artiste"), txtArtiste,
                new Label("Genre artistique"), txtGenre,
                new Label("Prix (€)"), txtPrix,
                new Label("Quantité en stock"), txtQuantite,
                chkDisponible
            );

            // --- ZONE DE DROITE (Titre, Recherche, Boutons et Tableau) ---
            VBox mainContent = new VBox(20);
            mainContent.setPadding(new Insets(25));
            HBox.setHgrow(mainContent, Priority.ALWAYS);

            // En-tête : Titre principal à gauche, Barre de recherche à droite
            HBox headerBox = new HBox();
            headerBox.setAlignment(Pos.CENTER_LEFT);
            
            // Titre principal
            Label mainTitle = new Label("Galerie d'Art");
            mainTitle.setId("mainTitle");
            mainTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
            
            Region spacerHeader = new Region();
            HBox.setHgrow(spacerHeader, Priority.ALWAYS);
            
            txtRecherche.setPromptText("🔍 Rechercher une œuvre ou un genre...");
            txtRecherche.setPrefWidth(280);
            
            headerBox.getChildren().addAll(mainTitle, spacerHeader, txtRecherche);

            // Ligne de boutons (Alignés impeccablement sur une ligne)
            HBox barreBoutons = new HBox(12);
            barreBoutons.setAlignment(Pos.CENTER_LEFT);
            barreBoutons.getChildren().addAll(btnAjouter, btnModifier, btnSupprimer, btnVider);

            // Rendre le tableau flexible pour occuper tout l'espace restant
            VBox.setVgrow(tableOeuvres, Priority.ALWAYS);
            tableOeuvres.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); 

            mainContent.getChildren().addAll(headerBox, barreBoutons, tableOeuvres);

            // --- ASSEMBLAGE FINAL ---
            HBox layoutPrincipal = new HBox();
            layoutPrincipal.setPrefSize(1100, 650); 
            layoutPrincipal.getChildren().addAll(sidebar, mainContent);

            // Ancrage complet aux bordures de la fenêtre
            AnchorPane.setTopAnchor(layoutPrincipal, 0.0);
            AnchorPane.setBottomAnchor(layoutPrincipal, 0.0);
            AnchorPane.setLeftAnchor(layoutPrincipal, 0.0);
            AnchorPane.setRightAnchor(layoutPrincipal, 0.0);

            // 🌟 CHARGEMENT DYNAMIQUE DU FICHIER CSS ICI 🌟
            layoutPrincipal.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

            // Injection dans la scène principale
            rootParent.getChildren().add(layoutPrincipal);

        } catch (Exception e) {
            System.out.println("Erreur mise en page : " + e.getMessage());
        }
    }

    // ── Les méthodes d'actions restent inchangées et fonctionnelles ──
    @FXML
    void handleAjouter() {
        try {
            Oeuvre o = new Oeuvre();
            o.setTitre(txtTitre.getText().trim());
            o.setArtiste(txtArtiste.getText().trim());
            o.setGenre(txtGenre.getText().trim());
            o.setPrix(Double.parseDouble(txtPrix.getText().trim()));
            o.setQuantite(Integer.parseInt(txtQuantite.getText().trim()));
            o.setDisponible(chkDisponible.isSelected());

            oeuvreService.saveOeuvre(o);
            rafraichirTableau();
            handleVider();
        } catch (NumberFormatException e) {
            showAlert("Erreur de saisie", "Le prix et la quantité doivent être des nombres.");
        }
    }

    @FXML
    void handleModifier() {
        if (oeuvreSelectionnee == null) {
            showAlert("Aucune sélection", "Sélectionnez une œuvre dans le tableau.");
            return;
        }
        try {
            oeuvreSelectionnee.setTitre(txtTitre.getText().trim());
            oeuvreSelectionnee.setArtiste(txtArtiste.getText().trim());
            oeuvreSelectionnee.setGenre(txtGenre.getText().trim());
            oeuvreSelectionnee.setPrix(Double.parseDouble(txtPrix.getText().trim()));
            oeuvreSelectionnee.setQuantite(Integer.parseInt(txtQuantite.getText().trim()));
            oeuvreSelectionnee.setDisponible(chkDisponible.isSelected());

            oeuvreService.updateOeuvre(oeuvreSelectionnee);
            rafraichirTableau();
            handleVider();
        } catch (NumberFormatException e) {
            showAlert("Erreur de saisie", "Le prix et la quantité doivent être des nombres.");
        }
    }

    @FXML
    void handleSupprimer() {
        if (oeuvreSelectionnee == null) {
            showAlert("Aucune sélection", "Sélectionnez une œuvre dans le tableau.");
            return;
        }
        oeuvreService.deleteOeuvre(oeuvreSelectionnee.getId());
        rafraichirTableau();
        handleVider();
    }

    @FXML
    void handleTableSelection(MouseEvent event) {
        Oeuvre sel = tableOeuvres.getSelectionModel().getSelectedItem();
        if (sel != null) {
            oeuvreSelectionnee = sel;
            txtTitre.setText(sel.getTitre());
            txtArtiste.setText(sel.getArtiste());
            txtGenre.setText(sel.getGenre());
            txtPrix.setText(String.valueOf(sel.getPrix()));
            txtQuantite.setText(String.valueOf(sel.getQuantite()));
            chkDisponible.setSelected(sel.isDisponible());
        }
    }

    @FXML
    void handleVider() {
        txtTitre.clear();
        txtArtiste.clear();
        txtGenre.clear();
        txtPrix.clear();
        txtQuantite.clear();
        chkDisponible.setSelected(true);
        if (txtRecherche != null) txtRecherche.clear();
        tableOeuvres.getSelectionModel().clearSelection();
        oeuvreSelectionnee = null;
    }

    @FXML
    void handleRecherche() {
        String filtre = txtRecherche.getText().trim().toLowerCase();
        if (filtre.isEmpty()) {
            tableOeuvres.setItems(oeuvreList);
        } else {
            List<Oeuvre> filtrees = oeuvreList.stream()
                .filter(o -> o.getTitre().toLowerCase().contains(filtre)
                          || o.getGenre().toLowerCase().contains(filtre))
                .collect(Collectors.toList());
            tableOeuvres.setItems(FXCollections.observableArrayList(filtrees));
        }
    }

    private void rafraichirTableau() {
        oeuvreList.clear();
        List<Oeuvre> liste = oeuvreService.getAllOeuvres();
        if (liste != null) oeuvreList.addAll(liste);
        tableOeuvres.setItems(oeuvreList);
    }

    private void showAlert(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}