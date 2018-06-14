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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventType;
import javafx.scene.control.Toggle;
import javafx.scene.layout.VBox;
import org.freedesktop.dbus.exceptions.DBusException;

/**
 *
 * @author user
 */
public class SecuritySettingsController implements Initializable{
    
    //FXMLLoader loader = new FXMLLoader(lernstickWel)
    
  
   
   
   
   
    SecuritySettingsModel securitySettingsModel = new SecuritySettingsModel();
   
   
   @FXML
   private RadioButton noPassPhraseRadio= new RadioButton();
   
   @FXML
   private RadioButton yesPassPhraseRadio = new RadioButton();
   
    @FXML
   ToggleGroup personalPassphrasesGroup = new ToggleGroup();
      
   @FXML
   private PasswordField passPhraseField = new PasswordField();
     
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
   
   //adding to the ToggleGroup
   
   
   

   public String passphraseString = "";
   public String passphraseRepeatedString = "";
   public String currentPassphraseString = "";
   public String passWordFieldMasterString = "";
   String globallyKnownPassword = "default";
   
   
   
   
   
    
        /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        noPassPhraseRadio.setToggleGroup(personalPassphrasesGroup);
        yesPassPhraseRadio.setToggleGroup(personalPassphrasesGroup);
        noPassPhraseRadio.setUserData("noPassphrase");
        yesPassPhraseRadio.setUserData("yesPassphrase");

        
        //set spacing between the VBOX elements
        personalPassPhraseBox.setSpacing(10);
        masterPassPhraseBox.setSpacing(10);
       //getCredentials();
       addValueChangedListeners();
       //try {
          // securitySettingsModel.executeDeleteMasterPassphraseScript();
           //securitySettingsModel.executeDeletePersonalPassphraseScript("julien","hello");
           //securitySettingsModel.executeChangePersonalPassphraseScript("waleed","julien");

   /*}  catch (IOException ex) {
           Logger.getLogger(SecuritySettingsController.class.getName()).log(Level.SEVERE, null, ex);
       } catch (InterruptedException ex) {
           Logger.getLogger(SecuritySettingsController.class.getName()).log(Level.SEVERE, null, ex);
       }*/
       } 
    public void getCredentials(){
        
        if (yesPassPhraseRadio.isSelected()) {
            passphraseString = passPhraseField.getText();
            passphraseRepeatedString = passPhraseFieldRepeat.getText();
            currentPassphraseString = currentPassphraseField.getText();                
    }
    }
    
    
    public void deleteMasterPassPhraseOnClick() throws IOException, InterruptedException {
        System.out.println("Hello delete");
        // check if currentPassphrase is default or personal PassPhrase
   passWordFieldMasterString = passWordFieldMaster.getText(); 

    securitySettingsModel.executeDeleteMasterPassphraseScript(passWordFieldMasterString);

        //check if the currentPassPhrase input field is empty, 
        //if itrs empty the use default
        //if its with something inside, its 
        
        //trigger the Method to delete the masterPassphrase
        
    }
    
     public void addValueChangedListeners() {
        
    
    personalPassphrasesGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>()
    {
            @Override
            public void changed(ObservableValue<? extends Toggle> ov, Toggle t, Toggle t1){
             
            if (personalPassphrasesGroup.getSelectedToggle() != null) {
             if(personalPassphrasesGroup.getSelectedToggle().getUserData().toString() == "yesPassphrase"){
                 try {
                        passphraseString = passPhraseField.getText();
                        passphraseRepeatedString = passPhraseFieldRepeat.getText();
                        currentPassphraseString = currentPassphraseField.getText(); 
                        securitySettingsModel.executeChangePersonalPassphraseScript(currentPassphraseString,passphraseString);
                 } catch (IOException ex) {
                     Logger.getLogger(SecuritySettingsController.class.getName()).log(Level.SEVERE, null, ex);
                 }
             }
             
             else if(personalPassphrasesGroup.getSelectedToggle().getUserData().toString() == "noPassphrase"){
              try {
                    
                     currentPassphraseString = currentPassphraseField.getText(); 
                     securitySettingsModel.executeDeletePersonalPassphraseScript(currentPassphraseString);
                 } catch (IOException ex) {
                     Logger.getLogger(SecuritySettingsController.class.getName()).log(Level.SEVERE, null, ex);
                 }
              }
             
            }            
            
            }
            
        }
        );
        
        
    }
     
}
