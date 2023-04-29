package edu.wpi.teamb.controllers.signage;

import edu.wpi.teamb.DBAccess.DAO.Repository;
import edu.wpi.teamb.DBAccess.ORMs.Move;
import edu.wpi.teamb.DBAccess.ORMs.Sign;
import edu.wpi.teamb.entities.ESignage;
import edu.wpi.teamb.entities.requests.EMoveRequest;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTableView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.Date;
import java.util.ArrayList;

public class RemoveSignageController {
    @FXML TableView<Sign> tableView;
    @FXML MFXButton btnSubmit;
    @FXML MFXButton btnClose;
    private ESignage signageE;

    public void initialize(){
        signageE = new ESignage();
        init_table();
        btnSubmit.setOnMouseClicked(e -> handleRemoveSign());
        btnClose.setOnMouseClicked(e -> handleClose());
    }

    private void init_table(){
        signTable();
        ObservableList<Sign> signs = FXCollections.observableArrayList();
        signs.addAll(signageE.getAllSigns());
        tableView.setItems(signs);
    }

    private void signTable() {
        // add moverequests attributes to table, id, longname,and date
        TableColumn<Sign, String> groups = new TableColumn<>("Signage Group");
        groups.setCellValueFactory(new PropertyValueFactory<Sign, String>("signageGroup"));

        TableColumn<Sign, String> locs = new TableColumn<>("Location");
        locs.setCellValueFactory(new PropertyValueFactory<Sign, String>("locationName"));

        TableColumn<Sign, String> directions = new TableColumn<>("Direction");
        directions.setCellValueFactory(new PropertyValueFactory<Sign, String>("direction"));

        TableColumn<Sign, Date> startDates = new TableColumn<>("Start Date");
        startDates.setCellValueFactory(new PropertyValueFactory<Sign, Date>("startDate"));

        TableColumn<Sign, Date> endDates = new TableColumn<>("End Date");
        endDates.setCellValueFactory(new PropertyValueFactory<Sign, Date>("endDate"));

        TableColumn<Sign, Date> singleBlocks = new TableColumn<>("Single Block?");
        singleBlocks.setCellValueFactory(new PropertyValueFactory<Sign, Date>("singleBlock"));

        tableView.getColumns().addAll(groups, locs, directions, startDates,endDates, singleBlocks);

    }

    public void handleRemoveSign() {
        // when clicking on the remove button, the selected row is removed from the
        // table and the database
        if (tableView.getSelectionModel().getSelectedItem() != null) {
            Sign sign = tableView.getSelectionModel().getSelectedItem();
            Repository.getRepository().deleteSign(sign);
            tableView.getItems().remove(sign);

        }
    }

    private void handleClose() {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }

}
