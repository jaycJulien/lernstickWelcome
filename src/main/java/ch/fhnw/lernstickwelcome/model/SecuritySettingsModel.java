/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.fhnw.lernstickwelcome.model;

import ch.fhnw.util.Partition;
import ch.fhnw.util.ProcessExecutor;
import ch.fhnw.util.StorageDevice;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    static String partitonName = ""; //sda3
    private final static ProcessExecutor PROCESS_EXECUTOR
            = new ProcessExecutor();

    /**
     * this method gets the partition name and location
     *
     * @return
     * @throws org.freedesktop.dbus.exceptions.DBusException
     */
    public static void getPartitionName() throws DBusException {
        StorageDevice systemStorageDevice
                = WelcomeModelFactory.getSystemStorageDevice();

        if (systemStorageDevice != null) {
            dataPartition = systemStorageDevice.getDataPartition();
            try {

                Pattern p = Pattern.compile("persistence/(.*)");
                Matcher m = p.matcher(dataPartition.getMountPath());
                if (m.find()) {
                    partitonName = m.group(1);
                }

            } catch (Error e) {
                System.err.println("something went wrong");
            }

        }
    }

    /**
     * This method deletes the Master passphrase
     *
     * @throws java.io.IOException
     */

    public void executeDeleteMasterPassphraseScript() throws IOException {

        String deleteMasterPassphraseScript = "";
        try {
            deleteMasterPassphraseScript = createDeleteMasterKeyScript();
        } catch (DBusException ex) {
            Logger.getLogger(SecuritySettingsModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        PROCESS_EXECUTOR.executeScript(deleteMasterPassphraseScript);

        System.err.println("deletemasterScript " + deleteMasterPassphraseScript);
    }

    /**
     * This method deletes the personal passphrase
     *
     * @param currentPassphrase
     * @param newPassphrase
     * @throws java.io.IOException
     */
    public void executeDeletePersonalPassphraseScript(String currentPassphrase, String newPassphrase) throws IOException {

        String deletePersonalPassphraseScript = "";
        deletePersonalPassphraseScript = createDeletePersonalKeyScript(currentPassphrase);

        //PROCESS_EXECUTOR.executeScript(deletePersonalPassphraseScript);
    }

    /**
     * This method change the personal passphrase with a new one
     *
     * @param currentPassphrase
     * @param newPassphrase
     * @throws java.io.IOException
     */
    public void executeChangePersonalPassphraseScript(String currentPassphrase, String newPassphrase) throws IOException {

        String changePassphraseScript = "";
        changePassphraseScript = createChangeKeyScript(currentPassphrase, newPassphrase);
        int exitValue = PROCESS_EXECUTOR.executeScript(changePassphraseScript);

        if(exitValue != 0){
            System.err.println("*******************************nicht gegangen!!");
        } else {
            System.err.println("es ist gegangen exitValue "+ exitValue);
        }

    }

    /**
     * this method deleted the master pass phrase
     */
    /**
     * this method sets the globally Known pass phrase "default"
     */
    /**
     * The shell to change key
     *
     * @param currentPassphrase
     * @param newPassphrase
     * @return
     */
    public String createChangeKeyScript(String currentPassphrase, String newPassphrase) {
        String script = "#!/bin/sh" + '\n'
                + "printf \"" + newPassphrase
                + "\" | printf \"" + currentPassphrase
                + "\" | cryptsetup luksChangeKey /dev/" + partitonName + " -q";

        return script;
    }

    /**
     * The shell to delete the personal passphrase
     *
     * @param currentPassphrase
     * @param newPassphrase
     * @return
     */
    public String createDeletePersonalKeyScript(String currentPassphrase) {
        String script = "#!/bin/sh" + '\n'
                + "printf \"default"
                + "\" | printf \"" + currentPassphrase
                + "\" | cryptsetup luksChangeKey -q /dev/" + partitonName;

        return script;
    }

    /**
     * The shell to delete the master passphrase
     *
     * @return
     * @throws org.freedesktop.dbus.exceptions.DBusException
     */
    public String createDeleteMasterKeyScript() throws DBusException {
        getPartitionName();
        String script = "#!/bin/sh" + '\n'
                + "cryptsetup luksKillSlot /dev/" + partitonName + " -q " + slotForMaster;

        return script;
    }

    public int checkConsoleOutput(String script) throws IOException {
        int exitValue = PROCESS_EXECUTOR.executeScript(script);

        return exitValue;

    }

}
