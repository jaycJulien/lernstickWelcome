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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Toggle;
import javafx.scene.layout.VBox;

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

    String globallyKnownPassword = "default";
    String textInBetween = "";
    public WelcomeController controller;

    private String selectedMethod = "";

    private enum SELECTED_TOGGLE {
        NO_PASSWORD,
        EDIT_PERSONAL_PASSWORD;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        try {
            checkIfMasterExist();
        } catch (IOException ex) {
            Logger.getLogger(SecuritySettingsController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(SecuritySettingsController.class.getName()).log(Level.SEVERE, null, ex);
        }
        noPassPhraseRadio.setToggleGroup(personalPassphrasesGroup);
        yesPassPhraseRadio.setToggleGroup(personalPassphrasesGroup);
        noPassPhraseRadio.setUserData("noPassphrase");
        yesPassPhraseRadio.setUserData("yesPassphrase");

        //set spacing between the VBOX elements
        personalPassPhraseBox.setSpacing(10);
        masterPassPhraseBox.setSpacing(10);
        addValueChangedListeners();

        passPhraseField.setVisible(false);
        passPhraseFieldRepeat.setVisible(false);
        currentPassphraseField.setVisible(false);

        currentPassLabel.setVisible(false);
        newPassLabel.setVisible(false);
        newPassLabelRepeat.setVisible(false);

        try {
            checkIfMasterExist();
        } catch (IOException ex) {
            Logger.getLogger(SecuritySettingsController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(SecuritySettingsController.class.getName()).log(Level.SEVERE, null, ex);
        }
        if ("DISABLED".equals(textInBetween)) {
            masterPassphraseButton.setVisible(false);

            currentPassPhraseMasterLabel.setVisible(false);
            deletedMasterPassphraseLabel.setVisible(true);
            passWordFieldMaster.setVisible(false);

        } else if (!"DISABLED".equals(textInBetween)) {
            masterPassphraseButton.setVisible(true);

            currentPassPhraseMasterLabel.setVisible(true);
            deletedMasterPassphraseLabel.setVisible(false);
            passWordFieldMaster.setVisible(true);

        }

    }

    public void getCredentials() {

        if (yesPassPhraseRadio.isSelected()) {
            passphraseString = passPhraseField.getText();
            passphraseRepeatedString = passPhraseFieldRepeat.getText();
            currentPassphraseString = currentPassphraseField.getText();
        } else if (noPassPhraseRadio.isSelected()) {
            currentPassphraseString = currentPassphraseField.getText();

        }
    }

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
        // text contains the full text that you want to extract data
        Matcher matcher = pattern.matcher(fullOutput);

        while (matcher.find()) {
            textInBetween = matcher.group(1);
        }

    }

    public void deleteMasterPassPhraseOnClick() throws IOException, InterruptedException {
        // check if currentPassphrase is default or personal PassPhrase
        passWordFieldMasterString = passWordFieldMaster.getText();

        securitySettingsModel.executeDeleteMasterPassphraseScript(passWordFieldMasterString);

    }

    public void doActionOnSave() throws IOException {

        getCredentials();
        //checkTheInput();

        if (checkTheInput() == true) {
            switch (selectedMethod) {
                case "NO_PASSWORD":
                    securitySettingsModel.executeDeletePersonalPassphraseScript(currentPassphraseString);
                    break;

                case "EDIT_PERSONAL_PASSWORD":
                    securitySettingsModel.executeChangePersonalPassphraseScript(currentPassphraseString, passphraseString);
                    break;

                default:
                    throw new IllegalArgumentException("the option selected does not exist");
            }
        }
    }

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
                if (passPhraseField.getText() != passPhraseFieldRepeat.getText()) {
                    infoBox("The entered new Passphrases don't match", "Passphrases don't match");
                    return false;
                } else if (passPhraseField.getText() == passPhraseFieldRepeat.getText()) {
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
