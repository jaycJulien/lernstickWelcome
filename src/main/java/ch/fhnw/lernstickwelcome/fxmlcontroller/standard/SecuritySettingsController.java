/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.fhnw.lernstickwelcome.fxmlcontroller.standard;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import ch.fhnw.lernstickwelcome.model.SecuritySettingsModel;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.freedesktop.dbus.exceptions.DBusException;

/**
 *
 * @author user
 */
public class SecuritySettingsController implements Initializable{
    
    //FXMLLoader loader = new FXMLLoader(lernstickWel)
    
   final ToggleGroup personalPhassphrasesGroup = new ToggleGroup();
    SecuritySettingsModel securitySettingsModel = new SecuritySettingsModel();
   
   
   @FXML
   private RadioButton noPassPhraseRadio= new RadioButton();
   
   @FXML
   private RadioButton yesPassPhraseRadio = new RadioButton();
      
   @FXML
   private PasswordField passPhraseField = new PasswordField();
     
   @FXML
   private PasswordField passPhraseFieldRepeat = new PasswordField();
   
   @FXML
   private PasswordField currentPassphraseField = new PasswordField();


   public String passphraseString = "";
   public String passphraseRepeatedString = "";
   public String currentPassphraseString = "";

   String globallyKnownPassword = "default";
   
   
   
   
   
    
        /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
       try {
           //securitySettingsModel.executeDeleteMasterPassphraseScript();
           //securitySettingsModel.executeDeletePersonalPassphraseScript("julien","hello");
           securitySettingsModel.executeChangePersonalPassphraseScript("waleed","julien");


    }  catch (IOException ex) {
           Logger.getLogger(SecuritySettingsController.class.getName()).log(Level.SEVERE, null, ex);
       }
       } 
    public void getCredentials(){
        
        if (yesPassPhraseRadio.isSelected()) {
            passphraseString = passPhraseField.getText();
            passphraseRepeatedString = passPhraseFieldRepeat.getText();
            currentPassphraseString = currentPassphraseField.getText();
                  
    }
    
    }
}
