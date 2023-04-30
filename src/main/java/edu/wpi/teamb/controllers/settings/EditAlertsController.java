package edu.wpi.teamb.controllers.settings;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import edu.wpi.teamb.DBAccess.DAO.Repository;
import edu.wpi.teamb.DBAccess.ORMs.User;
import edu.wpi.teamb.controllers.NavDrawerController;
import edu.wpi.teamb.navigation.Navigation;
import edu.wpi.teamb.navigation.Screen;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

public class EditAlertsController {
    @FXML
    private JFXHamburger menuBurger;
    @FXML private JFXDrawer menuDrawer;

    @FXML private MFXTextField textTitle;
    @FXML private MFXTextField textDescription;

    @FXML private MFXFilterComboBox<String> cbEmployees;

    @FXML private MFXButton btnAddAlert;
    @FXML private MFXButton btnDeleteAlert;
    @FXML private MFXButton btnEditAlert;
    @FXML private MFXButton btnRefresh;
    @FXML private MFXButton btnReset;
    @FXML private Pane navPane;
    @FXML private MFXButton btnBack;
    @FXML private TableView<edu.wpi.teamb.DBAccess.ORMs.Alert> tbAlerts;
    private int tableSize = 0;

    @FXML private VBox vboxActivateNav;
    @FXML private VBox vboxActivateNav1;
    private boolean navLoaded;

    @FXML
    public void initialize() throws IOException {
        initButtons();
        initNavBar();
        initializeFields();
        navLoaded = false;
        activateNav();
        deactivateNav();

    }

    //EditUserController editUserController = new EditUserController();

    public void initializeFields() {
        ArrayList<edu.wpi.teamb.DBAccess.ORMs.Alert> listOfAlerts = Repository.getRepository().getAllAlerts();
        ArrayList<User> users = Repository.getRepository().getAllUsers();
        ArrayList<String> usernames = new ArrayList<>();
        for(int i = 0; i < users.size(); i++){
            usernames.add(users.get(i).getUsername());
        }
        Collections.sort(usernames);
        usernames.add(0, "Unassigned");
        cbEmployees.getItems().addAll(usernames);

        alertTable();
        // Hide the edit vbox
        //vboxEditUser.setVisible(false);
    }

    public void initButtons() {
        btnAddAlert.setTooltip(new Tooltip("Click to add alert"));
        btnEditAlert.setTooltip(new Tooltip("Click to edit alert"));
        btnRefresh.setTooltip(new Tooltip("Click to refresh table"));
        btnDeleteAlert.setTooltip(new Tooltip("Click to delete alert"));
        btnReset.setTooltip(new Tooltip("Click to reset fields"));
        btnBack.setTooltip(new Tooltip("Click to go to settings"));
        btnAddAlert.setOnMouseClicked(event -> handleAddAlert());
        btnEditAlert.setOnMouseClicked(event -> {
            try {
                handleEditAlert();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        btnRefresh.setOnMouseClicked(event-> {
            updateTable();
        });

        btnDeleteAlert.setOnMouseClicked(event -> handleDeleteAlert());
        btnEditAlert.setDisable(true);
        btnDeleteAlert.setDisable(true);
        btnBack.setOnMouseClicked(event -> Navigation.navigate(Screen.SETTINGS));
        btnReset.setOnMouseClicked(event -> handleReset());
    }

    private void handleReset() {
        textTitle.clear();
        textDescription.clear();
        cbEmployees.clear();
    }

    private void handleDeleteAlert() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Edge");
        alert.setContentText("Are you sure you want to delete this alert?");
        Optional<ButtonType> action = alert.showAndWait();
        if (action.get() == ButtonType.OK) {
            edu.wpi.teamb.DBAccess.ORMs.Alert alert1 = tbAlerts.getSelectionModel().getSelectedItem();
            Repository.getRepository().deleteAlert(alert1); // Delete the user
            updateTable();
            createAlert("Alert Deleted", "Alert Deleted Successfully");
        }
    }

    private void handleEditAlert() throws IOException {
        if(btnRefresh.isVisible()){
            createAlert("Table not up-to-date", "Cannot edit while table not up-to-date");
        } else {
            edu.wpi.teamb.DBAccess.ORMs.Alert a = tbAlerts.getSelectionModel().getSelectedItem();
            System.out.println("Edit alert button");
            showEditMenu(a);
            tbAlerts.refresh();
            sortTable();
            btnRefresh.setVisible(true);
        }
    }

    public void sortTable(){
        tbAlerts.getSortOrder().setAll(tbAlerts.getColumns().get(3));
    }


    private void showEditMenu(edu.wpi.teamb.DBAccess.ORMs.Alert a) throws IOException {
        Parent root;
        EditAlertController.setCurrentAlert(a);
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("edu/wpi/teamb/views/settings/EditAlert.fxml")));
        Stage stage = new Stage();
        stage.setTitle("Edit Alert Details");
        stage.setScene(new Scene(root, 400, 600));
        stage.show();
    }

    private void handleAddAlert() {
        edu.wpi.teamb.DBAccess.ORMs.Alert newAlert = new edu.wpi.teamb.DBAccess.ORMs.Alert();
        newAlert.setTitle(textTitle.getText());
        newAlert.setDescription(textDescription.getText().toLowerCase());
        newAlert.setCreated_at(new Timestamp(System.currentTimeMillis()));
        if(cbEmployees.getValue() == null){
            newAlert.setEmployee("Unassigned");
        } else {
            newAlert.setEmployee(cbEmployees.getValue());
        }
        if (!nullInputs()) {
            createAlert("Alert added", "Alert added successfully");
            Repository.getRepository().addAlert(newAlert);
            handleReset();
            tbAlerts.refresh();
            updateTable();
        }
        else
            createAlert("Alert not added", "Please fill out the required fields");
    }

    private boolean nullInputs() {
        return textTitle.getText().isEmpty() || textDescription.getText().isEmpty();
    }

    private void createAlert(String title, String context) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(context);
        alert.showAndWait();
    }

    private void alertTable() {
        // add User attributes to the table (Title, Description)
        //tbAlerts.getColumns().clear();
        tbAlerts.setEditable(false);
        TableColumn<edu.wpi.teamb.DBAccess.ORMs.Alert, String> titles = new TableColumn<>("Title");
        titles.setMinWidth(60);
        titles.setStyle("-fx-alignment: CENTER;");
//        titles.setMaxWidth(60);
        titles.setCellValueFactory(new PropertyValueFactory<edu.wpi.teamb.DBAccess.ORMs.Alert, String>("title"));

        TableColumn<edu.wpi.teamb.DBAccess.ORMs.Alert, String> descriptions = new TableColumn<>("Description");
        descriptions.setMinWidth(260);
        descriptions.setStyle("-fx-alignment: CENTER;");
//        descriptions.setMaxWidth(260);
        descriptions.setCellValueFactory(new PropertyValueFactory<edu.wpi.teamb.DBAccess.ORMs.Alert, String>("description"));


        TableColumn<edu.wpi.teamb.DBAccess.ORMs.Alert, Timestamp> time = new TableColumn<>("Created at");
        time.setStyle("-fx-alignment: CENTER;");
        time.setCellValueFactory((new PropertyValueFactory<edu.wpi.teamb.DBAccess.ORMs.Alert, Timestamp>("createdAt")));

        TableColumn<edu.wpi.teamb.DBAccess.ORMs.Alert, String> employees = new TableColumn<>("Assigned employee");
        employees.setCellValueFactory((new PropertyValueFactory<edu.wpi.teamb.DBAccess.ORMs.Alert, String>("employee")));
        time.setMinWidth(150);
//        time.setMaxWidth(150);
        employees.setStyle("-fx-alignment: CENTER;");
        employees.setMinWidth(150);
//        ObservableList<edu.wpi.teamb.DBAccess.ORMs.Alert> data = FXCollections.observableArrayList();

        tbAlerts.getColumns().addAll(employees, titles, descriptions, time);
        time.setSortType(TableColumn.SortType.DESCENDING);
        tbAlerts.getSortOrder().setAll(time);
        updateTable();
    }



    @FXML
    public void tbItemSelected() {
        // when clicking on a row in the table, the remove button is enabled and the
        // data is loaded into the form
        tbAlerts.setOnMouseClicked(event -> {
            if (tbAlerts.getSelectionModel().getSelectedItem() != null) {
                btnDeleteAlert.setDisable(false);
                btnEditAlert.setDisable(false);

                edu.wpi.teamb.DBAccess.ORMs.Alert alert = tbAlerts.getSelectionModel().getSelectedItem();
                // Clear fields
            }
        });

    }

    private void updateTable() {
        // empty the table and refill database
        tbAlerts.getItems().clear();
        ArrayList<edu.wpi.teamb.DBAccess.ORMs.Alert> alerts = Repository.getRepository().getAllAlerts();
        for (edu.wpi.teamb.DBAccess.ORMs.Alert alert : alerts) {
            tbAlerts.getItems().add(alert);
            tableSize++;
        }
        tbAlerts.refresh();
        sortTable();
        btnRefresh.setVisible(false);
    }



    /**
     * Utilizes a gate to swap between handling the navdrawer and the rest of the page
     * Swaps ownership of the strip to the navdraw
     */

    public void activateNav(){
        vboxActivateNav.setOnMouseEntered(event -> {
            if(!navLoaded) {
                navPane.setMouseTransparent(false);
                navLoaded = true;
                vboxActivateNav.setDisable(true);
                vboxActivateNav1.setDisable(false);
            }
        });
    }

    /**
     * Utilizes a gate to swap between handling the navdrawer and the rest of the page
     * Swaps ownership of the strip to the page
     */
    public void deactivateNav(){
        vboxActivateNav1.setOnMouseEntered(event -> {
            if(navLoaded){
                navPane.setMouseTransparent(true);
                vboxActivateNav.setDisable(false);
                navLoaded = false;
                vboxActivateNav1.setDisable(true);
            }
        });
    }

    /**
     * Method to convert string permission level to int
     * @param
     * @return
     */

    public void initNavBar() {
        // https://github.com/afsalashyana/JavaFX-Tutorial-Codes/tree/master/JavaFX%20Navigation%20Drawer/src/genuinecoder
        try {
            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource("/edu/wpi/teamb/views/components/NavDrawer.fxml"));
            VBox vbox = loader.load();
            NavDrawerController navDrawerController = loader.getController();
            menuDrawer.setSidePane(vbox);
            navPane.setMouseTransparent(true);
            navLoaded = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        HamburgerBackArrowBasicTransition burgerOpen =
                new HamburgerBackArrowBasicTransition(menuBurger);
        burgerOpen.setRate(-1);
        menuBurger.addEventHandler(
                javafx.scene.input.MouseEvent.MOUSE_PRESSED,
                (e) -> {
                    burgerOpen.setRate(burgerOpen.getRate() * -1);
                    burgerOpen.play();
                    if (menuDrawer.isOpened()) {
                        menuDrawer.close();
                        vboxActivateNav1.toFront();
                    } else {
                        menuDrawer.toFront();
                        menuBurger.toFront();
                        menuDrawer.open();
                    }
                });
    }
}
