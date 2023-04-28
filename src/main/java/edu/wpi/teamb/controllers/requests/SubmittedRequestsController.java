package edu.wpi.teamb.controllers.requests;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import edu.wpi.teamb.DBAccess.Full.*;
import edu.wpi.teamb.DBAccess.ORMs.Request;
import edu.wpi.teamb.controllers.NavDrawerController;
import edu.wpi.teamb.controllers.components.InfoCardController;
import edu.wpi.teamb.entities.requests.EAllRequests;
import edu.wpi.teamb.navigation.Navigation;
import edu.wpi.teamb.navigation.Screen;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Collectors;

import io.github.palexdev.materialfx.controls.MFXComboBox;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class SubmittedRequestsController {
    @FXML private MFXButton btnCancel;
    @FXML private ImageView helpIcon;
    @FXML private JFXHamburger menuBurger;
    @FXML private JFXDrawer menuDrawer;
    @FXML private VBox allRequestsContainerVBox;
    @FXML private Pane navPane;
    @FXML VBox vboxActivateNav;
    @FXML VBox vboxActivateNav1;
    @FXML private ScrollPane allRequestsScrollPane;
    @FXML private MFXComboBox<String> cbFilterCategory;
    @FXML private MFXComboBox<String> cbFilterOptions;

    //entity object of class that contains all the methods to get the requests
    private EAllRequests allRequestsE;
    private boolean navLoaded;
    //regular class fields below


    @FXML
    public void initialize() throws IOException, SQLException {
        initNavBar();
        navPane.setPickOnBounds(false);
        menuDrawer.setPickOnBounds(false);
        allRequestsE = new EAllRequests();
        initScrollPane();
        hoverHelp();
        initComboBoxChangeListeners();
        loadRequestsIntoContainer();
        navPane.setMouseTransparent(true);
        activateNav();
        deactivateNav();
    }

    private void initScrollPane() {
        // Scroll pane preferences
        allRequestsScrollPane.setFitToWidth(true);
        allRequestsScrollPane.pannableProperty().set(true);
        allRequestsScrollPane.hbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        allRequestsScrollPane.vbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.ALWAYS);
    }

    private void loadRequestsIntoContainer() {
        //getting the list of all requests
        ArrayList<Request> listOfRequests = allRequestsE.getAllRequests();
        for (int i = 0; i < listOfRequests.size(); i++) {
            //implementation using FXMLLoader and InfoCardController
            FXMLLoader loader = null;
            //AnchorPane should be the root of the InfoCard.fxml
            AnchorPane requestInfoCardRoot = null;
            //controller for the corresponding InfoCard.fxml
            InfoCardController requestInfoCardController = null;
            try {
                loader = new FXMLLoader(getClass().getResource("/edu/wpi/teamb/views/components/InfoCard.fxml"));
                requestInfoCardRoot = loader.load();
                requestInfoCardController = loader.getController();
            } catch (IOException e) {
                System.out.println("IOException in loadRequestsIntoContainer of AllRequestsController: " + e.getMessage());
            }

            switch (listOfRequests.get(i).getRequestType()) {
                case "Meal":
                    IFull fullMealRequest = allRequestsE.getMealRequest(listOfRequests.get(i).getId());
                    if(fullMealRequest == null){continue;}
                    Objects.requireNonNull(requestInfoCardController).sendRequest(fullMealRequest);
                    break;
                case "Conference":
                    IFull fullConferenceRequest = allRequestsE.getConferenceRequest(listOfRequests.get(i).getId());
                    if(fullConferenceRequest == null) {continue;}
                    Objects.requireNonNull(requestInfoCardController).sendRequest(fullConferenceRequest);
                    break;
                case "Flower":
                    IFull fullFlowerRequest = allRequestsE.getFlowerRequest(listOfRequests.get(i).getId());
                    if(fullFlowerRequest == null) {continue;}
                    Objects.requireNonNull(requestInfoCardController).sendRequest(fullFlowerRequest);
                    break;
                case "Office":
                    IFull fullOfficeRequest = allRequestsE.getOfficeRequest(listOfRequests.get(i).getId());
                    if(fullOfficeRequest == null){continue;}
                    Objects.requireNonNull(requestInfoCardController).sendRequest(fullOfficeRequest);
                    break;
                case "Furniture":
                    IFull fullFurnitureRequest = allRequestsE.getFurnitureRequest(listOfRequests.get(i).getId());
                    if(fullFurnitureRequest == null){continue;}
                    Objects.requireNonNull(requestInfoCardController).sendRequest(fullFurnitureRequest);
                    break;
                default:
                    //continue statement to skip any unrecognized types of request to avoid occurrence of empty cards
                    continue;
            }


//           add the request info card to the request container VBox
            allRequestsContainerVBox.getChildren().add(requestInfoCardRoot);
//            add a margin to the children of allRequestsContainerVBox
            VBox.setMargin(requestInfoCardRoot, new Insets( 0, 0, 10, 0));
        }
    }

    private void loadRequestsIntoContainer(String filterCategory, String filterOption) {
        //clearing the container VBox
        allRequestsContainerVBox.getChildren().clear();
        //getting the list of all requests
        ArrayList<Request> listOfRequests = allRequestsE.getAllRequests();
        //applying status filter settings
        ArrayList<Request> filteredListOfRequests = new ArrayList<>();
        if (filterCategory.equals("Status")) {
            filteredListOfRequests = listOfRequests.stream()
                    .filter(request -> request.getRequestStatus().equals(filterOption))
                    .collect(Collectors.toCollection(ArrayList::new));
        } else if (filterCategory.equals("Request Type")) {
            filteredListOfRequests = listOfRequests.stream()
                    .filter(request -> request.getRequestType().equals(filterOption))
                    .collect(Collectors.toCollection(ArrayList::new));
        } else if (filterCategory.equals("Date Submitted")) {
            //convert the string date to timestamp and sort the arraylist of requests by timestamp by option to either do ascending or descending
            if (filterOption.equals("Ascending")) {
                filteredListOfRequests = listOfRequests.stream()
                        .sorted(Comparator.comparing(Request::getDateSubmitted))
                        .collect(Collectors.toCollection(ArrayList::new));
            } else if (filterOption.equals("Descending")) {
                filteredListOfRequests = listOfRequests.stream()
                        .sorted(Comparator.comparing(Request::getDateSubmitted).reversed())
                        .collect(Collectors.toCollection(ArrayList::new));
            }
        } else if (filterCategory.equals("employee")) {
            if (filterOption.equals("Unassigned")) {
                filteredListOfRequests = listOfRequests.stream()
                        .filter(request -> request.getEmployee().equals(filterOption))
                        .collect(Collectors.toCollection(ArrayList::new));
            }
        }


        for (int i = 0; i < filteredListOfRequests.size(); i++) {
            //implementation using FXMLLoader and InfoCardController
            FXMLLoader loader = null;
            //AnchorPane should be the root of the InfoCard.fxml
            AnchorPane requestInfoCardRoot = null;
            //controller for the corresponding InfoCard.fxml
            InfoCardController requestInfoCardController = null;
            try {
                loader = new FXMLLoader(getClass().getResource("/edu/wpi/teamb/views/components/InfoCard.fxml"));
                requestInfoCardRoot = loader.load();
                requestInfoCardController = loader.getController();
            } catch (IOException e) {
                System.out.println("IOException in loadRequestsIntoContainer of AllRequestsController: " + e.getMessage());
            }

            switch (filteredListOfRequests.get(i).getRequestType()) {
                case "Meal":
                    IFull fullMealRequest = allRequestsE.getMealRequest(filteredListOfRequests.get(i).getId());
                    if(fullMealRequest == null){continue;}
                    Objects.requireNonNull(requestInfoCardController).sendRequest(fullMealRequest);
                    break;
                case "Conference":
                    IFull fullConferenceRequest = allRequestsE.getConferenceRequest(filteredListOfRequests.get(i).getId());
                    if(fullConferenceRequest == null) {continue;}
                    Objects.requireNonNull(requestInfoCardController).sendRequest(fullConferenceRequest);
                    break;
                case "Flower":
                    IFull fullFlowerRequest = allRequestsE.getFlowerRequest(filteredListOfRequests.get(i).getId());
                    if(fullFlowerRequest == null) {continue;}
                    Objects.requireNonNull(requestInfoCardController).sendRequest(fullFlowerRequest);
                    break;
                case "Office":
                    IFull fullOfficeRequest = allRequestsE.getOfficeRequest(filteredListOfRequests.get(i).getId());
                    if(fullOfficeRequest == null){continue;}
                    Objects.requireNonNull(requestInfoCardController).sendRequest(fullOfficeRequest);
                    break;
                case "Furniture":
                    IFull fullFurnitureRequest = allRequestsE.getFurnitureRequest(filteredListOfRequests.get(i).getId());
                    if(fullFurnitureRequest == null){continue;}
                    Objects.requireNonNull(requestInfoCardController).sendRequest(fullFurnitureRequest);
                    break;
                default:
                    //continue statement to skip any unrecognized types of request to avoid occurrence of empty cards
                    continue;
            }


//           add the request info card to the request container VBox
            allRequestsContainerVBox.getChildren().add(requestInfoCardRoot);
//            add a margin to the children of allRequestsContainerVBox
            VBox.setMargin(requestInfoCardRoot, new Insets( 0, 0, 10, 0));
        }
    }

    @FXML
    public void clickCancel() {
        Navigation.navigate(Screen.CREATE_NEW_REQUEST);
    }

    @FXML
    public void hoverHelp() {
        helpIcon.setOnMouseEntered(
                event -> {
                    Tooltip helpTip =
                            new Tooltip(
                                    "On this page: Please use the filter to filter requests by \n"
                                            + "request status, request type, date submitted (ascending or  + \n" +
                                            "descending), or unassigned requests. The selected requests information \n" +
                                            "is listed on the right. You can decide to edit, delete, or mark a request \n + " +
                                            "as complete using the buttons on the side. \n");
                    helpTip.setStyle("-fx-size: 18");
                    helpTip.setShowDelay(Duration.millis(1));
                    helpTip.hideDelayProperty().set(Duration.seconds(1.5));
                    Tooltip.install(helpIcon, helpTip);
                });
        helpIcon.setOnMouseExited(event -> {});
    }

    /**
     * Utilizes a gate to swap between handling the navdrawer and the rest of the page
     * Swaps ownership of the strip to the navdraw
     */
    public void activateNav(){
        vboxActivateNav.setOnMouseEntered(event -> {
            if(!navLoaded) {
                System.out.println("on");
                navPane.setPickOnBounds(false);
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
                        menuDrawer.toFront();
                        menuDrawer.close();
                    } else {
                        menuDrawer.toFront();
                        menuDrawer.open();
                    }
                });
    }

    /**
     * This method will add changeListeners to the combo boxes
     * in order to update some state variables.
     */
    private void initComboBoxChangeListeners() {
        //at the beginning set cbFilterOptions to invisible
        cbFilterOptions.setVisible(false);
        //add filtering options to cbFilterCategory
        cbFilterCategory.getItems().addAll("", "Status", "Request Type", "Date Submitted", "Unassigned Task");
        //add change listener to cbFilterCategory
        cbFilterCategory.valueProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue == null) {
                        //if selection is null
                        loadRequestsIntoContainer();
                    } else if (newValue.equals("Unassigned Task")) {
                        loadRequestsIntoContainer("employee", "Unassigned");
                    } else if (!newValue.equals("")) {
                        //set cbFilterOptions to visible
                        cbFilterOptions.setVisible(true);
                        //clear the items in cbFilterOptions
                        cbFilterOptions.getItems().clear();
                        //add filtering options to cbFilterOptions based on filter category
                        switch (newValue) {
                            case "Status":
                                cbFilterOptions.getItems().addAll("", RequestStatus.PENDING.getStatus(), RequestStatus.COMPLETED.getStatus());
                                break;
                            case "Request Type":
                                cbFilterOptions.getItems().addAll(
                                        "",
                                        RequestType.MEAL.getType(),
                                        RequestType.CONFERENCE.getType(),
                                        RequestType.FLOWER.getType(),
                                        RequestType.OFFICE.getType(),
                                        RequestType.FURNITURE.getType());
                                break;
                            case "Date Submitted":
                                cbFilterOptions.getItems().addAll(
                                        "",
                                        AscendingDescending.ASCENDING.getAscendingDescending(),
                                        AscendingDescending.DESCENDING.getAscendingDescending());
                                break;
                        }
                    } else {
                        //if selection is empty
                        //set cbFilterOptions to invisible
                        cbFilterOptions.setVisible(false);
                        //clear the items in cbFilterOptions
                        cbFilterOptions.getItems().clear();
                        //clear cBFilterOptions selection
                        cbFilterOptions.getSelectionModel().clearSelection();
                        //reset all requests in the container
                        loadRequestsIntoContainer();
                    }
                });

        //add change listener to cbFilterOptions
        cbFilterOptions.valueProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue == null) {
                        //if selection is null
                        loadRequestsIntoContainer();
                    } else if (!newValue.equals("")) {
                        //if the new value is not empty
                        //set filterOption to the new value
                        loadRequestsIntoContainer(cbFilterCategory.getSelectedItem(), newValue);
                    } else {
                        //set filterOption to the new value
                        loadRequestsIntoContainer();
                    }
                });
    }

    private enum RequestStatus {
        PENDING("Pending"),
        COMPLETED("Completed");
        private final String status;

        RequestStatus(String status) {
            this.status = status;
        }

        public String getStatus() {
            return status;
        }
    }

    private enum RequestType {
        MEAL("Meal"),
        CONFERENCE("Conference"),
        FLOWER("Flower"),
        OFFICE("Office"),
        FURNITURE("Furniture");
        private final String type;

        RequestType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }

    private enum AscendingDescending {
        ASCENDING("Ascending"),
        DESCENDING("Descending");;

        private final String ascendingDescending;

        AscendingDescending(String ascendingDescending) {
            this.ascendingDescending = ascendingDescending;
        }

        public String getAscendingDescending() {
            return ascendingDescending;
        }
    }
}