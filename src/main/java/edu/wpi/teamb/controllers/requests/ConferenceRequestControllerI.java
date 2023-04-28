package edu.wpi.teamb.controllers.requests;

import edu.wpi.teamb.Bapp;
import edu.wpi.teamb.DBAccess.DAO.Repository;
import edu.wpi.teamb.DBAccess.Full.FullConferenceRequest;
import edu.wpi.teamb.controllers.components.InfoCardController;
import edu.wpi.teamb.entities.requests.EConferenceRequest;
import edu.wpi.teamb.entities.requests.IRequest;
import edu.wpi.teamb.navigation.Navigation;
import edu.wpi.teamb.navigation.Screen;
import edu.wpi.teamb.utils.TimeFormattingHelpers;
import io.github.palexdev.materialfx.controls.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.controlsfx.control.PopOver;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;


public class ConferenceRequestControllerI implements IRequestController{

    @FXML private MFXComboBox<Integer> reservationHour;
    @FXML private MFXComboBox<String> reservationMinute;
    @FXML private MFXComboBox<String> reservationAmPm;
    @FXML private MFXDatePicker datePicker;
    @FXML private MFXComboBox<String> cbEmployeesToAssign;
    @FXML private MFXTextField eventNameTextField;
    @FXML private MFXTextField bookingReasonTextField;
    @FXML private MFXComboBox<Integer> cbDuration;
    @FXML private MFXFilterComboBox<String> cbLongName;
    @FXML private MFXTextField tfNotes;
    @FXML private MFXButton resetBtn;
    @FXML private MFXButton cancelBtn;
    @FXML private MFXButton btnSubmit;
    @FXML private ImageView helpIcon;

    private final EConferenceRequest EConferenceRequest;

    public ConferenceRequestControllerI(){
        this.EConferenceRequest = new EConferenceRequest();
    }
    @FXML
    public void initialize() throws IOException, SQLException {
        initBtns();
        initializeFields();
    }

    @Override
    public void initBtns() {
        btnSubmit.setOnAction(e -> handleSubmit());
        resetBtn.setOnAction(e -> handleReset());
        cancelBtn.setOnAction(e -> handleCancel());
        helpIcon.setOnMouseClicked(e -> handleHelp());
    }

    @Override
    public void initializeFields() throws SQLException {
        //Initialize the list of locations to direct request to via dropdown
        ObservableList<String> longNames = FXCollections.observableArrayList();
        longNames.addAll(Repository.getRepository().getLongNameByType("CONF"));
        cbLongName.setItems(longNames);

        //Dropdown for employee selection
        ObservableList<String> employees =
                FXCollections.observableArrayList();
        employees.add("Unassigned");
        employees.addAll(EConferenceRequest.getUsernames());
        cbEmployeesToAssign.setItems(employees);

        //Dropdown for duration selection
        ObservableList<Integer> duration =
                FXCollections.observableArrayList();
        duration.addAll(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        cbDuration.setItems(duration);

        // Dropdown for reservationHour
        ObservableList<Integer> hoursListItems =
                FXCollections.observableArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
        reservationHour.setItems(hoursListItems);

        // Dropdown for reservationMinute
        ObservableList<String> minutesListItems = FXCollections.observableArrayList("00", "15", "30", "45");
        reservationMinute.setItems(minutesListItems);

        // Dropdown for reservationAmPm
        ObservableList<String> AmPm = FXCollections.observableArrayList("AM", "PM");
        reservationAmPm.setItems(AmPm);

        // changing some properties of the text fields
        // makes cursor visible
        eventNameTextField.setCaretVisible(true);
        bookingReasonTextField.setCaretVisible(true);
        // setting max character limits
        eventNameTextField.textLimitProperty().set(100);
        bookingReasonTextField.textLimitProperty().set(250);
    }

    @Override
    public void handleSubmit() {
        // Check if the input fields are null
        if (nullInputs()) {
            showPopOver();
        }
        else {
        String timerequested = "";
        if (reservationAmPm.getText().equals("AM") && reservationHour.getText().equals("12")) {
            timerequested = "00:" + reservationMinute.getText() + ":00";
        } else if (reservationAmPm.getText().equals("AM")) {
            timerequested = reservationHour.getText() + ":" + reservationMinute.getText() + ":00";
        }  else if (reservationAmPm.getText().equals("PM") && reservationHour.getText().equals("12")) {
            timerequested = reservationHour.getText() + ":" + reservationMinute.getText() + ":00";
        } else if (reservationAmPm.getText().equals("PM")) {
            int hour = Integer.parseInt(reservationHour.getText()) + 12;
            timerequested = "" + hour + ":" + reservationMinute.getText() + ":00";
        }
        String daterequested = datePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));;
        String timeStamp = daterequested + " " + timerequested;

            // Get the standard request fields
            EConferenceRequest.setEmployee(cbEmployeesToAssign.getValue());
            EConferenceRequest.setLocationName(cbLongName.getValue());
            EConferenceRequest.setRequestStatus(IRequest.RequestStatus.Pending);
            EConferenceRequest.setNotes(tfNotes.getText());

            // Get the conference specific fields
            EConferenceRequest.setDateRequested(Timestamp.valueOf(timeStamp));
            EConferenceRequest.setEventName(eventNameTextField.getText());
            EConferenceRequest.setBookingReason(bookingReasonTextField.getText());
            EConferenceRequest.setDuration(cbDuration.getValue());

            //Check for required fields before allowing submittion
            if (EConferenceRequest.checkRequestFields() && EConferenceRequest.checkSpecialRequestFields()) {
                //Set the gathered fields into a string array
                String[] output = {EConferenceRequest.getEmployee(),
                        String.valueOf(EConferenceRequest.getRequestStatus()),
                        EConferenceRequest.getLocationName(),
                        EConferenceRequest.getNotes(),
                        String.valueOf(EConferenceRequest.getDateRequested()),
                        EConferenceRequest.getEventName(),
                        EConferenceRequest.getBookingReason(),
                        String.valueOf(EConferenceRequest.getDuration())
                };

                EConferenceRequest.submitRequest(output);
                handleReset();
                Navigation.navigate(Screen.CREATE_NEW_REQUEST);
            }
            submissionAlert();
        }
    }

    @Override
    public void handleReset() {
        datePicker.setValue(null);
        reservationHour.setValue(12);
        reservationMinute.setValue("00");
        reservationAmPm.setValue("AM");
        datePicker.setValue(null);
        cbDuration.setValue(null);
        eventNameTextField.setText("");
        bookingReasonTextField.setText("");
        tfNotes.setText("");
        cbEmployeesToAssign.setText("Employees Available");
        cbLongName.clear();
        cbLongName.replaceSelection("All Room Names: ");
    }

    @Override
    public void handleHelp() {
        final FXMLLoader popupLoader = new FXMLLoader(Bapp.class.getResource("views/components/popovers/ConferenceRequestHelpPopOver.fxml"));
        PopOver popOver = new PopOver();
        popOver.setDetachable(true);
        popOver.setArrowLocation(PopOver.ArrowLocation.BOTTOM_RIGHT);
        popOver.setArrowSize(0.0);
        try {
            popOver.setContentNode(popupLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        popOver.show(helpIcon);
    }

    @Override
    public boolean nullInputs() {
        return cbEmployeesToAssign.getValue() == null
                || cbLongName.getValue() == null
                || eventNameTextField.getText().isEmpty()
                || bookingReasonTextField.getText().isEmpty()
                || cbDuration.getValue() == null
                || reservationHour.getValue() == null
                || reservationMinute.getValue() == null
                || reservationAmPm.getValue() == null
                || datePicker.getValue() == null;
    }

    @Override
    public void showPopOver() {
        final FXMLLoader popupLoader = new FXMLLoader(Bapp.class.getResource("views/components/NotAllFieldsCompleteError.fxml"));
        PopOver popOver = new PopOver();
        popOver.setDetachable(true);
        popOver.setArrowLocation(PopOver.ArrowLocation.BOTTOM_CENTER);
        popOver.setArrowSize(0.0);
        try {
            popOver.setContentNode(popupLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        popOver.show(btnSubmit);
    }

    //functions for editable stage in InfoCardController
    public void enterConferenceRequestEditableMode(FullConferenceRequest fullConferenceRequest, InfoCardController currentInfoCardController) {
        //set the editable fields to the values of the request
        cbEmployeesToAssign.getSelectionModel().selectItem(fullConferenceRequest.getEmployee());
        cbLongName.getSelectionModel().selectItem(fullConferenceRequest.getLocationName());
        tfNotes.setText(fullConferenceRequest.getNotes());
        eventNameTextField.setText(fullConferenceRequest.getEventName());
        bookingReasonTextField.setText(fullConferenceRequest.getBookingReason());
        cbDuration.getSelectionModel().selectItem(fullConferenceRequest.getDuration());
        datePicker.setValue(fullConferenceRequest.getDateRequested().toLocalDateTime().toLocalDate());
        reservationHour.getSelectionModel().selectItem(TimeFormattingHelpers.get24to12Hour(fullConferenceRequest.getDateRequested().toLocalDateTime().getHour()));
        String minutes;
        if (fullConferenceRequest.getDateRequested().toLocalDateTime().getMinute() < 10) {
            minutes = "0" + fullConferenceRequest.getDateRequested().toLocalDateTime().getMinute();
        } else {
            minutes = "" + fullConferenceRequest.getDateRequested().toLocalDateTime().getMinute();
        }
        reservationMinute.getSelectionModel().selectItem(minutes);
        reservationAmPm.getSelectionModel().selectItem(fullConferenceRequest.getDateRequested().toLocalDateTime().getHour() < 12 ? "AM" : "PM");

        //set the submit button to say update
        btnSubmit.setText("Update");
        //remove the current onAction event
        btnSubmit.setOnAction(null);
        //add a new onAction event
        btnSubmit.setOnAction(e -> {
            //update all the fields of the fullConferenceRequest
            fullConferenceRequest.setEmployee(cbEmployeesToAssign.getValue());
            fullConferenceRequest.setLocationName(cbLongName.getValue());
            fullConferenceRequest.setNotes(tfNotes.getText());
            fullConferenceRequest.setEventName(eventNameTextField.getText());
            fullConferenceRequest.setBookingReason(bookingReasonTextField.getText());
            fullConferenceRequest.setDuration(cbDuration.getValue());
            String timerequested = "";
            if (reservationAmPm.getText().equals("AM") && reservationHour.getText().equals("12")) {
                timerequested = "00:" + reservationMinute.getText() + ":00";
            } else if (reservationAmPm.getText().equals("AM")) {
                timerequested = reservationHour.getText() + ":" + reservationMinute.getText() + ":00";
            }  else if (reservationAmPm.getText().equals("PM") && reservationHour.getText().equals("12")) {
                timerequested = reservationHour.getText() + ":" + reservationMinute.getText() + ":00";
            } else if (reservationAmPm.getText().equals("PM")) {
                int hour = Integer.parseInt(reservationHour.getText()) + 12;
                timerequested = "" + hour + ":" + reservationMinute.getText() + ":00";
            }
            String daterequested = datePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));;
            String timeStamp = daterequested + " " + timerequested;

            fullConferenceRequest.setDateRequested(Timestamp.valueOf(timeStamp));
            //update the database
            EConferenceRequest.updateConferenceRequest(fullConferenceRequest);
            //close the window
            Stage stage = (Stage) btnSubmit.getScene().getWindow();
            stage.close();
            //send the fullConferenceRequest to the info card controller
            currentInfoCardController.sendRequest(fullConferenceRequest);
        });

        //set the cancel and reset buttons to not be visible
        cancelBtn.setVisible(false);
        resetBtn.setVisible(false);
    }
}
