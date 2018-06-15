/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.fhnw.lernstickwelcome.model;

import ch.fhnw.util.Partition;
import ch.fhnw.util.ProcessExecutor;
import ch.fhnw.util.StorageDevice;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.freedesktop.dbus.exceptions.DBusException;
import sun.tools.jar.CommandLine;

/**
 *
 * @author Waleed Al-Hubaishi, Julien Christen
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
     * This method deletes the Master passphrase, it uses the deleteMaster.sh
     * which is implemented using the expect. Keyslot 1 will be empty and
     * ENABLED
     *
     * @param currentPassphrase
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    public void executeDeleteMasterPassphraseScript(String currentPassphrase) throws IOException, InterruptedException {

        //go the deleteMaster.sh file and excute the expect shell inside with the given partition name
        ProcessBuilder pb = new ProcessBuilder("src/main/java/ch/fhnw/lernstickwelcome/model/deleteMaster.sh", currentPassphrase, partitonName);
        Process p = pb.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line = null;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }

        //we tried to do it with script string, but it didnt work so that is why we have a file for this
        /*String script = "#!/bin/bash" + '\n'
                +"export HISTIGNORE=\"expect*\";"+ '\n'
                +"expect -c \""+ '\n'
                +"spawn sudo cryptsetup luksKillSlot /dev/sdb3 1"+ '\n'
                + "expect \"?assphrase:\""+ '\n'
                + "send \""+currentPassphrase+"\r\""+ '\n'
                +  "expect eof\""+ '\n'
                +"export HISTIGNORE=\"\";";
             
             System.err.print("the script is "+script);
             
             int exitValue = PROCESS_EXECUTOR.executeScript(script);
             
                          System.err.print("the exit moron value is  is "+script);

             
             return exitValue;*/
    }

    /**
     * This method deletes the personal passphrase
     *
     * @param currentPassphrase
     * @return
     * @throws java.io.IOException
     */
    public int executeDeletePersonalPassphraseScript(String currentPassphrase) throws IOException {

        String deletePersonalPassphraseScript = "";
        deletePersonalPassphraseScript = createDeletePersonalKeyScript(currentPassphrase);
        //the exiteValue is importnant to know if the command ended successfuly
        int exitValue = PROCESS_EXECUTOR.executeScript(deletePersonalPassphraseScript);

        return exitValue;
    }

    /**
     * This method change the personal passphrase with a new one
     *
     * @param currentPassphrase
     * @param newPassphrase
     * @return
     * @throws java.io.IOException
     */
    public int executeChangePersonalPassphraseScript(String currentPassphrase, String newPassphrase) throws IOException {

        String changePassphraseScript = createChangeKeyScript(currentPassphrase, newPassphrase);

        int exitValue = PROCESS_EXECUTOR.executeScript(changePassphraseScript);

        return exitValue;
    }

    /**
     * The shell to change key
     *
     * @param currentPassphrase
     * @param newPassphrase
     * @return
     */
    public String createChangeKeyScript(String currentPassphrase, String newPassphrase) {
        String script = "#!/bin/sh" + '\n'
                + "printf \"" + currentPassphrase + "\\n" + newPassphrase
                + "\" | sudo cryptsetup luksChangeKey /dev/" + partitonName + " -q -S 0";
        return script;
    }

    /**
     * The shell to delete the personal passphrase and set "default" instead in
     * keyslot 0
     *
     * @param currentPassphrase
     * @return
     */
    public String createDeletePersonalKeyScript(String currentPassphrase) {
        String script = "#!/bin/sh" + '\n'
                + "printf \"" + currentPassphrase + "\\ndefault\n"
                + "\" | sudo cryptsetup luksChangeKey /dev/" + partitonName + " -S 0";

        return script;
    }

}
