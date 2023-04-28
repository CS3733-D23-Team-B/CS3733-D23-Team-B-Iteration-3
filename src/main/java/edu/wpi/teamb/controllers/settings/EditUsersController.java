package edu.wpi.teamb.controllers.settings;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import edu.wpi.teamb.DBAccess.DAO.Repository;
import edu.wpi.teamb.DBAccess.ORMs.Move;
import edu.wpi.teamb.DBAccess.ORMs.User;
import edu.wpi.teamb.controllers.NavDrawerController;
import edu.wpi.teamb.navigation.Navigation;
import edu.wpi.teamb.navigation.Screen;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Date;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;

public class EditUsersController {
    @FXML
    private JFXHamburger menuBurger;
    @FXML private JFXDrawer menuDrawer;

    @FXML private MFXComboBox<String> cbPermissionLevel;
    @FXML private MFXTextField textPassword;
    @FXML private MFXTextField textUsername;
    @FXML private MFXTextField textEmail;
    @FXML private MFXTextField textName;

    @FXML private MFXButton btnAddUser;
    @FXML private MFXButton btnDeleteUser;
    @FXML private MFXButton btnEditUser;
    @FXML private VBox vboxEditUser;
    @FXML private VBox tableVbox;
    @FXML private Pane navPane;
    @FXML private TableView<User> tbUsers;
    private int tableSize = 0;
    @FXML VBox vboxActivateNav;
    @FXML VBox vboxActivateNav1;

    private boolean navLoaded;

    @FXML
    public void initialize() throws IOException {
        navPane.setPickOnBounds(false);
        menuDrawer.setPickOnBounds(false);
        initNavBar();
        initializeFields();
        initButtons();
    }

    //EditUserController editUserController = new EditUserController();

    public void initializeFields() {
        ArrayList<User> listOfUsers = Repository.getRepository().getAllUsers();
        ObservableList<String> usernames = FXCollections.observableArrayList();
        ObservableList<String> permissionLevels = FXCollections.observableArrayList();

        // Initialize the permission level combo boxes
        permissionLevels.add("ADMIN");
        permissionLevels.add("EMPLOYEE");
        cbPermissionLevel.setItems(permissionLevels);

        userTable();
        navLoaded = false;
        activateNav();
        deactivateNav();
        // Hide the edit vbox
        //vboxEditUser.setVisible(false);
    }

    public void initButtons() {
        btnAddUser.setOnMouseClicked(event -> handleAddUser());
        btnEditUser.setOnMouseClicked(event -> {
            try {
                handleEditUser();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        btnDeleteUser.setOnMouseClicked(event -> handleDeleteUser());
        btnEditUser.setDisable(true);
        btnDeleteUser.setDisable(true);
    }

    private void handleDeleteUser() {
        User user = tbUsers.getSelectionModel().getSelectedItem();
        Repository.getRepository().deleteUser(user); // Delete the user
        //tbUsers.refresh(); // Refresh the table
        updateTable();
        createAlert("User Deleted", "User Deleted Successfully");
    }

    private void handleEditUser() throws IOException {
        User user = tbUsers.getSelectionModel().getSelectedItem();
        System.out.println("Edit user button");
        showEditMenu(user);
        tbUsers.refresh(); // Refresh the table
    }

    private void showEditMenu(User user) throws IOException {
        Parent root;
        EditUserController.setCurrentUser(user);
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("edu/wpi/teamb/views/settings/EditUser.fxml")));
        Stage stage = new Stage();
        stage.setTitle("Edit User Details");
        stage.setScene(new Scene(root, 400, 600));
        stage.show();
    }

    private void handleAddUser() {
        User newUser = new User();
        newUser.setName(textName.getText());
        newUser.setUsername(textUsername.getText().toLowerCase());
        newUser.setPassword(textPassword.getText());
        newUser.setEmail(textEmail.getText().toLowerCase());
        newUser.setPermissionLevel(permissionLevelToInt(cbPermissionLevel.getValue()));
        if (usernameDoesNotExist(newUser) && emailDoesNotExist(newUser)) {
            Repository.getRepository().addUser(newUser);
            createAlert("User added", "User added successfully");
        }
        else if (!emailDoesNotExist(newUser)) {
            createAlert("Email already exists", "Please enter a different email");
        }
        else
        {
            createAlert("Username already exists", "Please enter a different username");
        }
        initializeFields(); // Refresh the combo box
    }


    /**
     * Checks if the username already exists in the database
     * @param user
     * @return
     */
    boolean usernameDoesNotExist(User user) {
        ArrayList<User> listOfUsers = Repository.getRepository().getAllUsers();
        for (User u : listOfUsers) {
            if (u.getUsername().equals(user.getUsername())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the email already exists in the database
     * @param user
     * @return
     */
    boolean emailDoesNotExist(User user) {
        ArrayList<User> listOfUsers = Repository.getRepository().getAllUsers();
        for (User u : listOfUsers) {
            if (u.getEmail().equals(user.getEmail())) {
                return false;
            }
        }
        return true;
    }

    private void handleSaveEdits() {
        User user = new User();
//        user.setUsername(textUsernameEdit.getText());
//        user.setPassword(textPasswordEdit.getText());
//        user.setPosition(textPositionEdit.getText());
//        if (cbPermissionLevel != null)
//            user.setPermissionLevel(getPermissionLevel(cbPermissionLevelEdit.getValue()));
        Repository.getRepository().updateUser(user);
        initializeFields(); // Refresh the combo box
    }

    private void createAlert(String title, String context) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(context);
        alert.showAndWait();
    }

    @FXML
    public void tbItemSelected() {
        // when clicking on a row in the table, the remove button is enabled and the
        // data is loaded into the form
        tbUsers.setOnMouseClicked(event -> {
            if (tbUsers.getSelectionModel().getSelectedItem() != null) {
                btnDeleteUser.setDisable(false);
                btnEditUser.setDisable(false);

                User user = tbUsers.getSelectionModel().getSelectedItem();
                // Clear fields
            }
        });

    }

    private void userTable() {
        tbUsers.getColumns().clear();
        // add User attributes to the table (Name, Username, Password, Email, Permission Level)
        TableColumn<User, String> names = new TableColumn<>("Name");
        names.setCellValueFactory(new PropertyValueFactory<User, String>("name"));

        TableColumn<User, String> usernames = new TableColumn<>("Username");
        usernames.setCellValueFactory(new PropertyValueFactory<User, String>("username"));

        TableColumn<User, String> passwords = new TableColumn<>("Password");
        passwords.setCellValueFactory(new PropertyValueFactory<User, String>("password"));

        TableColumn<User, String> emails = new TableColumn<>("Email");
        emails.setCellValueFactory(new PropertyValueFactory<User, String>("email"));

        TableColumn<User, Integer> permissions = new TableColumn<>("Permission Level");
        permissions.setCellValueFactory(new PropertyValueFactory<User, Integer>("permissionLevel"));

        tbUsers.getColumns().addAll(names, usernames, passwords, emails, permissions);
        updateTable();
    }

    private void updateTable() {
        // empty the table and refill database
        tbUsers.getItems().clear();
        ArrayList<User> users = Repository.getRepository().getAllUsers();
        for (User user : users) {
            tbUsers.getItems().add(user);
            tableSize++;
        }
        tbUsers.refresh();
    }

    /**
     * Method to convert string permission level to int
     * @param permissionLevel
     * @return
     */
    private int permissionLevelToInt(String permissionLevel) {
        if (permissionLevel.equals("ADMIN")) {
            return 0;
        }
        if (permissionLevel.equals("EMPLOYEE")) {
            return 1;
        }
        else
            return 2; // Error
    }

    private String permissionLevelToString(int permissionLevel) {
        if (permissionLevel == 0) {
            return "ADMIN";
        }
        if (permissionLevel == 1) {
            return "EMPLOYEE";
        }
        else
            return "Error"; // Error
    }


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
}
