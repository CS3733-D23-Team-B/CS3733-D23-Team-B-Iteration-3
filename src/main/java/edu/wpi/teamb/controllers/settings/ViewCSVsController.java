package edu.wpi.teamb.controllers.settings;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import edu.wpi.teamb.DBAccess.DAO.Repository;
import edu.wpi.teamb.DBAccess.DBoutput;
import edu.wpi.teamb.controllers.NavDrawerController;
import edu.wpi.teamb.entities.EMapEditor;
import edu.wpi.teamb.navigation.Navigation;
import edu.wpi.teamb.navigation.Screen;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class ViewCSVsController {

    @FXML
    private JFXHamburger menuBurger;
    @FXML private JFXDrawer menuDrawer;
    private final EMapEditor editor;
    @FXML
    private MFXButton uploadBtn;
    @FXML
    private MFXButton exportBtn;
    @FXML
    private FileChooser fileChooser;

    @FXML
    private MFXComboBox<String> NodeSelector;
    @FXML
    private MFXListView NodeInfo;
    @FXML
    private VBox VboxNodes;
    @FXML private MFXButton btnBack;
    @FXML private VBox vboxActivateNav;
    @FXML private VBox vboxActivateNav1;
    @FXML private Pane navPane;
    private boolean navLoaded;

    public ViewCSVsController() throws SQLException {
        this.editor = new EMapEditor();
    }

    @FXML
    public void initialize() throws IOException, SQLException {
        initNavBar();
        initializeFields();
        initButtons();
    }

    /**
     * Handles the click event for the upload button
     */
    public void handleUploadBtn() {
        fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV", "*.csv"));
        File file = fileChooser.showOpenDialog(null);
        String selectedItem = getSelectedItem();
        String absolutePath = null;
        if (file != null) {
            switch (selectedItem) {
                case "Moves" -> Repository.getRepository().importMovesFromCSV(file.getAbsolutePath(), 0);
                case "Nodes" -> Repository.getRepository().importNodesFromCSV(file.getAbsolutePath(), 0);
                case "Edges" -> Repository.getRepository().importEdgesFromCSV(file.getAbsolutePath(), 0);
                case "Location Names" -> Repository.getRepository().importLocationNamesFromCSV(file.getAbsolutePath(), 0);
                case "Users" -> Repository.getRepository().importUsersFromCSV(file.getAbsolutePath(), 0);
                case "Requests" -> Repository.getRepository().importRequestsFromCSV(file.getAbsolutePath(), 0);
                case "Conference Requests" -> Repository.getRepository().importConferenceRequestsFromCSV(file.getAbsolutePath(), 0);
                case "Flower Requests" -> Repository.getRepository().importFlowerRequestsFromCSV(file.getAbsolutePath(), 0);
                case "Furniture Requests" -> Repository.getRepository().importFurnitureRequestsFromCSV(file.getAbsolutePath(), 0);
                case "Meal Requests" -> Repository.getRepository().importMealRequestsFromCSV(file.getAbsolutePath(), 0);
                case "Office Requests" -> Repository.getRepository().importOfficeRequestsFromCSV(file.getAbsolutePath(), 0);
                case "Alerts" -> Repository.getRepository().importAlertsFromCSV(file.getAbsolutePath(), 0);
                case "Signs" -> Repository.getRepository().importSignsFromCSV(file.getAbsolutePath(), 0);
            }
        }
    }

    /**
     * Handles the click event for the export button
     */
    public void handleExportBtn() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(null);
        String absolutePath = null;
        String item = getSelectedItem();
        if (selectedDirectory != null) {
            switch (item) {
                case "Moves" -> absolutePath = selectedDirectory.getAbsolutePath() + "/moves";
                case "Nodes" -> absolutePath = selectedDirectory.getAbsolutePath() + "/nodes";
                case "Edges" -> absolutePath = selectedDirectory.getAbsolutePath() + "/edges";
                case "Location Names" -> absolutePath = selectedDirectory.getAbsolutePath() + "/locationNames";
                case "Users" -> absolutePath = selectedDirectory.getAbsolutePath() + "/users";
                case "Requests" -> absolutePath = selectedDirectory.getAbsolutePath() + "/requests";
                case "Conference Requests" -> absolutePath = selectedDirectory.getAbsolutePath() + "/conferenceRequests";
                case "Flower Requests" -> absolutePath = selectedDirectory.getAbsolutePath() + "/flowerRequests";
                case "Furniture Requests" -> absolutePath = selectedDirectory.getAbsolutePath() + "/furnitureRequests";
                case "Meal Requests" -> absolutePath = selectedDirectory.getAbsolutePath() + "/mealRequests";
                case "Office Requests" -> absolutePath = selectedDirectory.getAbsolutePath() + "/officeRequests";
                case "Alerts" -> absolutePath = selectedDirectory.getAbsolutePath() + "/alerts";
                case "Signs" -> absolutePath = selectedDirectory.getAbsolutePath() + "/signs";

                default -> absolutePath = selectedDirectory.getAbsolutePath() + "/defaulttest";
            };
            switch (item) {
                case "Nodes" -> Repository.getRepository().exportNodesToCSV(absolutePath, 2);
                case "Location Names" -> DBoutput.exportLocationNamesToCSV(absolutePath, 2);
                case "Moves" -> Repository.getRepository().exportMovesToCSV(absolutePath, 2);
                case "Edges" -> Repository.getRepository().exportEdgesToCSV(absolutePath, 2);
                case "Users" -> Repository.getRepository().exportUsersToCSV(absolutePath, 2);
                case "Requests" -> Repository.getRepository().exportRequestsToCSV(absolutePath, 2);
                case "Conference Requests" -> Repository.getRepository().exportConferenceRequestsToCSV(absolutePath, 2);
                case "Flower Requests" -> Repository.getRepository().exportFlowerRequestsToCSV(absolutePath, 2);
                case "Furniture Requests" -> Repository.getRepository().exportFurnitureRequestsToCSV(absolutePath, 2);
                case "Meal Requests" -> Repository.getRepository().exportMealRequestsToCSV(absolutePath, 2);
                case "Office Requests" -> Repository.getRepository().exportOfficeRequestsToCSV(absolutePath, 2);
                case "Alerts" -> Repository.getRepository().exportAlertsToCSV(absolutePath, 2);
                case "Signs" -> Repository.getRepository().exportSignsToCSV(absolutePath, 2);
                default -> new ArrayList<>();
            };
        }
        System.out.println("here"); // 1 means success, 0 means failure
    }

    private void initializeFields() {
        ArrayList<String> tables = new ArrayList<String>();
        tables.add("Nodes");
        tables.add("Edges");
        tables.add("Moves");
        tables.add("Location Names");
        tables.add("Users");
        tables.add("Requests");
        tables.add("Conference Requests");
        tables.add("Flower Requests");
        tables.add("Furniture Requests");
        tables.add("Meal Requests");
        tables.add("Office Requests");
        tables.add("Alerts");
        tables.add("Signs");
        NodeSelector.setItems(FXCollections.observableList(tables));
    }

    public void clickNodeSelector() {
        displaySelection();
    }

    public void displaySelection() {
        String item = getSelectedItem();
        ArrayList<?> itemsList = switch (item) {
            case "Moves" -> editor.getMoveList();
            case "Nodes" -> editor.getNodeList();
            case "Edges" -> editor.getEdgeList();
            case "Location Names" -> editor.getLocationNameList();
            default -> new ArrayList<>();
        };

        ObservableList<?> items = FXCollections.observableList(itemsList);
        NodeInfo.setItems(items);
        VboxNodes.getChildren().clear();
        VboxNodes.getChildren().addAll(NodeInfo);
    }

    public String getSelectedItem() {
        return NodeSelector.getSelectedItem();
    }

    public void initButtons() {
        uploadBtn.setOnMouseClicked(event -> {
            handleUploadBtn();
        });
        exportBtn.setOnMouseClicked(event -> {
            handleExportBtn();
        });
        btnBack.setOnMouseClicked(event -> Navigation.navigate(Screen.SETTINGS));
    }

    /**
     * Utilizes a gate to swap between handling the navdrawer and the rest of the page
     * Swaps ownership of the strip to the navdraw
     */

    public void activateNav(){
        vboxActivateNav.setOnMouseEntered(event -> {
            if(!navLoaded) {
                System.out.println("on");
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
                System.out.println("off");
                navPane.setMouseTransparent(true);
                vboxActivateNav.setDisable(false);
                navLoaded = false;
                vboxActivateNav1.setDisable(true);
            }
        });
    }

    /**
     * Utilizes a gate to swap between handling the navdrawer and the rest of the page
     * Swaps ownership of the strip to the navdraw
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
                        vboxActivateNav1.toFront();
                    } else {
                        menuDrawer.toFront();
                        menuBurger.toFront();
                        menuDrawer.open();
                    }
                });
    }
}
