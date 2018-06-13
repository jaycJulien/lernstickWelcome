/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.fhnw.lernstickwelcome.model;
import ch.fhnw.util.Partition;
import ch.fhnw.util.ProcessExecutor;
import ch.fhnw.util.StorageDevice;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.freedesktop.dbus.exceptions.DBusException;

/**
 *
 * @author user
 */
public class SecuritySettingsModel {
    
    /**
     * the reference to the data partition
     */
    private static Partition dataPartition;
    int slotForMaster = 1;
    int slotForInitialOrPersonal = 0;
    String partitonName = ""; //sda3


    
    /**
     * this method gets the partition name and location
     * @param currentPassphrase
     * @param newPassphrase
     * @return 
     * @throws org.freedesktop.dbus.exceptions.DBusException
     */
    public static String getPartitionName(String currentPassphrase, String newPassphrase) throws DBusException{
        System.out.println("***we are insisde ");
        StorageDevice systemStorageDevice
                = WelcomeModelFactory.getSystemStorageDevice();
        
       
        if (systemStorageDevice != null) {
            dataPartition = systemStorageDevice.getDataPartition();
            try {
                  
                  Pattern p = Pattern.compile( "persistence/(.*)" );
                    Matcher m = p.matcher(dataPartition.getMountPath());
                    if ( m.find() ) {
                         partitonName = m.group(1); 
                    }
                    
                } catch (Error e) {
                    
                }
                        
        }
        
        return partitonName;
    }
    /**
     * This method updates the current personal pass phrase with a new one
     * which the end-user has set
     */
    
    /**
     * this method deleted the master pass phrase
     */
   
    /**
     * this method sets the globally Known pass phrase "default"
     */
    
    /**
     * The shell to change key
     * @param currentPassphrase
     * @param newPassphrase
     * @return 
     */
    
    public String createChangeKeyScript(String currentPassphrase, String newPassphrase){
                    String script = "#!/bin/sh" + '\n'
                             + "printf \"" + newPassphrase
                            + "\" | printf \""+currentPassphrase
                             + "\" | cryptsetup luksChangeKey /dev/"+partitonName+" -q";
                    
                    return script;
                }
    /**
     * The shell to delete the master passphrase
     */
    
    
    
    
    
}
