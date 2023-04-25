package edu.wpi.teamb.controllers.settings;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import edu.wpi.teamb.DBAccess.DAO.Repository;
import edu.wpi.teamb.controllers.NavDrawerController;
import edu.wpi.teamb.navigation.Navigation;
import edu.wpi.teamb.navigation.Screen;
import io.github.palexdev.materialfx.controls.MFXButton;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Objects;

public class EditAlertsController {
    @FXML
    private JFXHamburger menuBurger;
    @FXML private JFXDrawer menuDrawer;

    @FXML private MFXTextField textTitle;
    @FXML private MFXTextField textDescription;

    @FXML private MFXButton btnAddAlert;
    @FXML private MFXButton btnDeleteAlert;
    @FXML private MFXButton btnEditAlert;
    @FXML private VBox vboxEditUser;
    @FXML private VBox tableVbox;
    @FXML private TableView<edu.wpi.teamb.DBAccess.ORMs.Alert> tbAlerts;
    private int tableSize = 0;

    @FXML
    public void initialize() throws IOException {
        initNavBar();
        initializeFields();
        initButtons();
    }

    //EditUserController editUserController = new EditUserController();

    public void initializeFields() {
        ArrayList<edu.wpi.teamb.DBAccess.ORMs.Alert> listOfAlerts = Repository.getRepository().getAllAlerts();
//        ObservableList<String> usernames = FXCollections.observableArrayList();
//        ObservableList<String> permissionLevels = FXCollections.observableArrayList();


        alertTable();
        // Hide the edit vbox
        //vboxEditUser.setVisible(false);
    }

    public void initButtons() {
        btnAddAlert.setOnMouseClicked(event -> handleAddAlert());
        btnEditAlert.setOnMouseClicked(event -> {
            try {
                handleEditAlert();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        btnDeleteAlert.setOnMouseClicked(event -> handleDeleteAlert());
        btnEditAlert.setDisable(true);
        btnDeleteAlert.setDisable(true);
    }

    private void handleDeleteAlert() {
        edu.wpi.teamb.DBAccess.ORMs.Alert alert = tbAlerts.getSelectionModel().getSelectedItem();
        Repository.getRepository().deleteAlert(alert); // Delete the user
        //tbUsers.refresh(); // Refresh the table
        updateTable();
        createAlert("Alert Deleted", "Alert Deleted Successfully");
    }

    private void handleEditAlert() throws IOException {
        edu.wpi.teamb.DBAccess.ORMs.Alert a = tbAlerts.getSelectionModel().getSelectedItem();
        System.out.println("Edit alert button");
        showEditMenu(a);
        tbAlerts.refresh();
        updateTable();
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
        Repository.getRepository().addAlert(newAlert);
        createAlert("Alert added", "Alert added successfully");
        initializeFields(); // Refresh the combo box
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
        titles.setMaxWidth(60);
        titles.setCellValueFactory(new PropertyValueFactory<edu.wpi.teamb.DBAccess.ORMs.Alert, String>("title"));

        TableColumn<edu.wpi.teamb.DBAccess.ORMs.Alert, String> descriptions = new TableColumn<>("Description");
        descriptions.setMinWidth(260);
        descriptions.setMaxWidth(260);
        descriptions.setCellValueFactory(new PropertyValueFactory<edu.wpi.teamb.DBAccess.ORMs.Alert, String>("description"));


        TableColumn<edu.wpi.teamb.DBAccess.ORMs.Alert, Timestamp> time = new TableColumn<>("Created at");
        time.setSortType(TableColumn.SortType.DESCENDING);
        time.setCellValueFactory((new PropertyValueFactory<edu.wpi.teamb.DBAccess.ORMs.Alert, Timestamp>("createdAt")));
        time.setMinWidth(150);
        time.setMaxWidth(150);
//        ObservableList<edu.wpi.teamb.DBAccess.ORMs.Alert> data = FXCollections.observableArrayList();

        tbAlerts.getColumns().addAll(titles, descriptions, time);
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
        for (edu.wpi.teamb.DBAccess.ORMs.Alert a : alerts) {
            tbAlerts.getItems().add(a);
            tableSize++;
        }
        tbAlerts.getSortOrder().setAll(tbAlerts.getColumns().get(2));
        tbAlerts.refresh();
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
                    } else {
                        menuDrawer.open();
                    }
                });
    }
}