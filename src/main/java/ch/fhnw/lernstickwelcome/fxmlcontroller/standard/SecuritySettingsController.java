/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.fhnw.lernstickwelcome.fxmlcontroller.standard;

import ch.fhnw.lernstickwelcome.controller.WelcomeController;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import ch.fhnw.lernstickwelcome.model.SecuritySettingsModel;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Toggle;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author user
 */
public class SecuritySettingsController implements Initializable {

    //FXMLLoader loader = new FXMLLoader(lernstickWel)
    SecuritySettingsModel securitySettingsModel = new SecuritySettingsModel();

    @FXML
    private RadioButton noPassPhraseRadio = new RadioButton();

    @FXML
    private RadioButton yesPassPhraseRadio = new RadioButton();

    @FXML
    ToggleGroup personalPassphrasesGroup = new ToggleGroup();

    @FXML
    private PasswordField passPhraseField = new PasswordField();

    @FXML
    private Label currentPassLabel = new Label();

    @FXML
    private Label newPassLabel = new Label();

    @FXML
    private Label newPassLabelRepeat = new Label();

    @FXML
    private Label hintPersonal = new Label();

    @FXML
    private Label hintMaster = new Label();

    @FXML
    private PasswordField passPhraseFieldRepeat = new PasswordField();

    @FXML
    private PasswordField currentPassphraseField = new PasswordField();

    @FXML
    private PasswordField passWordFieldMaster = new PasswordField();

    @FXML
    VBox personalPassPhraseBox = new VBox();

    @FXML
    VBox masterPassPhraseBox = new VBox();

    @FXML
    Button masterPassphraseButton = new Button();

    @FXML
    Label deletedMasterPassphraseLabel = new Label();

    @FXML
    Label currentPassPhraseMasterLabel = new Label();

    //adding to the ToggleGroup
    private String passphraseString = "";
    private String passphraseRepeatedString = "";
    private String currentPassphraseString = "";
    private String passWordFieldMasterString = "";

    //temporary holder to get the state of keyslot 1
    String textInBetween = "";

    public WelcomeController controller;

    //the selected method for the personal passphrase
    private String selectedMethod = "";

    //the two alerts to show in case the personal passphrase will be deleted
    //or the Master passphrase will be deleted
    Alert alertDeleteMaster = new Alert(AlertType.CONFIRMATION);
    Alert alertDeletePersonal = new Alert(AlertType.CONFIRMATION);

    //the options keys avalible in each alert
    ButtonType continueMasterButtonType = new ButtonType("Continue");
    ButtonType cancleMasterButtonType = new ButtonType("Cancle");
    ButtonType continuePersonalButtonType = new ButtonType("Continue");
    ButtonType canclePersonalButtonType = new ButtonType("Cancle");

    //the allowed states for the personal passphrase
    private enum SELECTED_TOGGLE {
        NO_PASSWORD,
        EDIT_PERSONAL_PASSWORD;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        //adding the ToggleButtons to the group to just select one
        noPassPhraseRadio.setToggleGroup(personalPassphrasesGroup);
        yesPassPhraseRadio.setToggleGroup(personalPassphrasesGroup);
        noPassPhraseRadio.setUserData("noPassphrase");
        yesPassPhraseRadio.setUserData("yesPassphrase");

        //set spacing between the VBOX elements
        personalPassPhraseBox.setSpacing(10);
        masterPassPhraseBox.setSpacing(10);
        addValueChangedListeners();

        //hide all the input passphrases at the begging as no method is selected
        //for the personal passphrase
        passPhraseField.setVisible(false);
        passPhraseFieldRepeat.setVisible(false);
        currentPassphraseField.setVisible(false);
        currentPassLabel.setVisible(false);
        newPassLabel.setVisible(false);
        newPassLabelRepeat.setVisible(false);
        hintPersonal.setVisible(false);

        hintPersonal.setWrapText(true);
        hintMaster.setWrapText(true);

        try {
            //to determine whether or not to show delete master passphrase button
            checkIfMasterExist();
        } catch (IOException ex) {
            Logger.getLogger(SecuritySettingsController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(SecuritySettingsController.class.getName()).log(Level.SEVERE, null, ex);
        }

        //if the keyslot 1 is DISABLED, that means no password is stored, so 
        //the delete master passhrase button will not be visible
        if ("DISABLED".equals(textInBetween)) {
            masterPassphraseButton.setVisible(false);

            currentPassPhraseMasterLabel.setVisible(false);
            deletedMasterPassphraseLabel.setVisible(true);
            passWordFieldMaster.setVisible(false);
            hintMaster.setVisible(false);

            //if there is a value in keyslot 1, then show the delete master passphrase button
        } else if (!"DISABLED".equals(textInBetween)) {
            masterPassphraseButton.setVisible(true);

            currentPassPhraseMasterLabel.setVisible(true);
            deletedMasterPassphraseLabel.setVisible(false);
            passWordFieldMaster.setVisible(true);
            hintMaster.setVisible(true);

        }

        //inserting the text to the alerts to be shown
        alertDeleteMaster.setTitle("Confirmation Dialog");
        alertDeleteMaster.setContentText("Master user will not be able to access");

        alertDeletePersonal.setTitle("Confirmation Dialog");
        alertDeletePersonal.setContentText("No Password will be asked to login");

        alertDeleteMaster.getButtonTypes().setAll(continueMasterButtonType, cancleMasterButtonType);
        alertDeletePersonal.getButtonTypes().setAll(continuePersonalButtonType, canclePersonalButtonType);

    }

    //this methods populates the variables with the inserted text, based on the 
    //selected method of the personal passphrase.
    public void getCredentials() {
        if (yesPassPhraseRadio.isSelected()) {
            passphraseString = passPhraseField.getText();
            passphraseRepeatedString = passPhraseFieldRepeat.getText();
            currentPassphraseString = currentPassphraseField.getText();
        } else if (noPassPhraseRadio.isSelected()) {
            currentPassphraseString = currentPassphraseField.getText();

        }
    }

    //this methods checks if Keyskot 1 is ENABLED or DISABLED
    public void checkIfMasterExist() throws IOException, InterruptedException {
        Process p;
        String fullOutput = "";

        try {
            p = Runtime.getRuntime().exec("sudo cryptsetup luksDump /dev/sdb3");
            p.waitFor();
            BufferedReader reader
                    = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine()) != null) {
                fullOutput = fullOutput + line;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        String regexString = Pattern.quote("1: ") + "(.*?)" + Pattern.quote("Key");
        Pattern pattern = Pattern.compile(regexString);
        Matcher matcher = pattern.matcher(fullOutput);

        while (matcher.find()) {
            textInBetween = matcher.group(1);
        }

    }

    //this method is the action method of the delte master passphrase.
    public void deleteMasterPassPhraseOnClick() throws IOException, InterruptedException {
        Optional<ButtonType> result = alertDeleteMaster.showAndWait();
        if (result.get() == continueMasterButtonType) {
            passWordFieldMasterString = passWordFieldMaster.getText();
            securitySettingsModel.executeDeleteMasterPassphraseScript(passWordFieldMasterString);
        } else {
            // ... user chose CANCEL or closed the dialog
        }

    }

    //this method is triggered when the SAVE button is clicked, it checks then 
    //which selected method for the personal passphrase is chosen, then do the 
    //corresponding steps whether to to replace the personal passphrase with "default"
    //passphrase or change the passphrase to the entered new passphrase
    public void doActionOnSave() throws IOException {

        //show the alert message and only continue if the user clicks on continue
        Optional<ButtonType> result = alertDeletePersonal.showAndWait();
        if (result.get() == continuePersonalButtonType) {
            getCredentials();

            if (checkTheInput() == true) {
                switch (selectedMethod) {
                    case "NO_PASSWORD":

                        if (securitySettingsModel.executeDeletePersonalPassphraseScript(currentPassphraseString) != 0) {
                            infoBox("Sorry, the current password doesn't match, please try again", "Current Passphrase doesn't match");
                        }

                        break;

                    case "EDIT_PERSONAL_PASSWORD":

                        if (securitySettingsModel.executeChangePersonalPassphraseScript(currentPassphraseString, passphraseString) != 0) {
                            infoBox("Sorry, the current password doesn't match, please try again", "Current Passphrase doesn't match");
                        }

                        break;

                    default:
                        throw new IllegalArgumentException("the option selected does not exist");
                }
            }
        } else {
            // ... user chose CANCEL or closed the dialog
        }

    }

    //this method do all the checks regrading the user input, it checks if the 
    //input and repeatition dont match, or if the input is left input, also the 
    // backslash sign is not allowed
    public boolean checkTheInput() {
        if (noPassPhraseRadio.isSelected()) {
            if (currentPassphraseField.getText().isEmpty()) {
                infoBox("Please enter your current Passphrase", "Passphrase missing");
                return false;
            } else if (!currentPassphraseField.getText().isEmpty()) {
                return true;
            }
        } else if (yesPassPhraseRadio.isSelected()) {
            if (currentPassphraseField.getText().isEmpty()
                    || passPhraseField.getText().isEmpty()
                    || passPhraseFieldRepeat.getText().isEmpty()) {
                infoBox("Please enter the missing Passphrase(s)", "Passphrase(s) missing");
                return false;
            } else {
                if (!passPhraseField.getText().equals(passPhraseFieldRepeat.getText())) {
                    infoBox("The entered new Passphrases don't match", "Passphrases don't match");
                    return false;
                } else if (passPhraseField.getText().equals(passPhraseFieldRepeat.getText())) {
                    if (passPhraseField.getText().contains("\\") || passPhraseFieldRepeat.getText().contains("\\")) {
                        infoBox("Please dont use the Backslash in your passphrase", "Backslash not allowed");
                        return false;
                    } else {
                        return true;

                    }
                }
            }
        }

        return false;
    }

    //this method is responsible of monitoring the selected toggle to view the 
    //corresponding text fields. 
    public void addValueChangedListeners() {

        personalPassphrasesGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> ov, Toggle t, Toggle t1) {

                if (personalPassphrasesGroup.getSelectedToggle() != null) {
                    if (personalPassphrasesGroup.getSelectedToggle().getUserData().toString() == "yesPassphrase") {
                        setSelectedMethod(SELECTED_TOGGLE.EDIT_PERSONAL_PASSWORD.toString());
                        passPhraseField.setVisible(true);
                        passPhraseFieldRepeat.setVisible(true);
                        currentPassphraseField.setVisible(true);
                        hintPersonal.setVisible(true);

                        //set the input pass fields to true
                        currentPassLabel.setVisible(true);
                        newPassLabel.setVisible(true);
                        newPassLabelRepeat.setVisible(true);

                    } else if (personalPassphrasesGroup.getSelectedToggle().getUserData().toString() == "noPassphrase") {
                        setSelectedMethod(SELECTED_TOGGLE.NO_PASSWORD.toString());

                        passPhraseField.setVisible(false);
                        passPhraseFieldRepeat.setVisible(false);
                        currentPassphraseField.setVisible(true);

                        currentPassLabel.setVisible(true);
                        newPassLabel.setVisible(false);
                        newPassLabelRepeat.setVisible(false);
                        hintPersonal.setVisible(false);

                    }

                }

            }
        }
        );

    }

    //Getter and Setter methods for the Strings
    public String getPassphraseString() {
        return passphraseString;
    }

    public void setPassphraseString(String passphraseString) {
        this.passphraseString = passphraseString;
    }

    public String getPassphraseRepeatedString() {
        return passphraseRepeatedString;
    }

    public void setPassphraseRepeatedString(String passphraseRepeatedString) {
        this.passphraseRepeatedString = passphraseRepeatedString;
    }

    public String getCurrentPassphraseString() {
        return currentPassphraseString;
    }

    public void setCurrentPassphraseString(String currentPassphraseString) {
        this.currentPassphraseString = currentPassphraseString;
    }

    public String getPassWordFieldMasterString() {
        return passWordFieldMasterString;
    }

    public void setPassWordFieldMasterString(String passWordFieldMasterString) {
        this.passWordFieldMasterString = passWordFieldMasterString;
    }

    public String getSelectedMethod() {
        return selectedMethod;
    }

    public void setSelectedMethod(String selectedMethod) {
        this.selectedMethod = selectedMethod;
    }

    //those two infoboxes are the one that would pop-up if the checkTheInput method
    //fails, or if the exitValue of one of the commands regarding the personal password fails
    //because the exitValue is not zero, which means for example that the current password entered 
    //is not correct
    public static void infoBox(String infoMessage, String titleBar) {
        /* By specifying a null headerMessage String, we cause the dialog to
           not have a header */
        infoBox(infoMessage, titleBar, null);
    }

    public static void infoBox(String infoMessage, String titleBar, String headerMessage) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(titleBar);
        alert.setHeaderText(headerMessage);
        alert.setContentText(infoMessage);
        alert.showAndWait();
    }

}
