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

    public void executeDeleteMasterPassphraseScript(String currentPassphrase) throws IOException, InterruptedException  {

        
            String deleteMasterPassphraseScript = createDeleteMasterKeyScript();
        //Process myProcess = Runtime.getRuntime().exec("sudo cryptsetup luksKillSlot -q /dev/sdb3  1");
        //PROCESS_EXECUTOR.executeScript(deleteMasterPassphraseScript);
            //Runtime.getRuntime().exec("sudo cryptsetup luksKillSlot -q /dev/sdb3  1");
            ProcessBuilder pb = new ProcessBuilder("src/main/java/ch/fhnw/lernstickwelcome/model/testFile.sh",currentPassphrase);
            Process p = pb.start();
             //int exitValue = PROCESS_EXECUTOR.executeProcess("sudo", "cryptsetup","luksKillSlot","-q","/dev/sdb3","1");
             BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
             String line = null;
             while ((line = reader.readLine()) != null)
             {
                System.out.println(line);
             }        
             System.err.println("deletemasterScript " + deleteMasterPassphraseScript);
    }

    /**
     * This method deletes the personal passphrase
     *
     * @param currentPassphrase
     * @param newPassphrase
     * @throws java.io.IOException
     */
    public void executeDeletePersonalPassphraseScript(String currentPassphrase) throws IOException {

        String deletePersonalPassphraseScript = "";
        deletePersonalPassphraseScript = createDeletePersonalKeyScript(currentPassphrase);

        System.err.println(deletePersonalPassphraseScript);
        PROCESS_EXECUTOR.executeScript(deletePersonalPassphraseScript);

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
        System.err.println("i am before");

        String changePassphraseScript = createChangeKeyScript(currentPassphrase, newPassphrase);

        PROCESS_EXECUTOR.executeScript(changePassphraseScript);
        
       // executeScript("src/main/java/ch/fhnw/lernstickwelcome/model/testFile.sh");
 
        
        System.err.println("i am after");
        /*
        if(exitValue != 0){
            System.err.println("*******************************nicht gegangen!!");
        } else {
            System.err.println("es ist gegangen exitValue "+ exitValue);
        }
            */
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
                + "printf \"" + currentPassphrase+"\\n"+newPassphrase
                + "\" | sudo cryptsetup luksChangeKey /dev/sdb3 -q -S 0";
        System.err.println("script is "+script);
               // + "\" | cryptsetup luksChangeKey /dev/" + partitonName + " -q";

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
               + "printf \"" + currentPassphrase+"\\ndefault\n"
                + "\" | sudo cryptsetup luksChangeKey /dev/sdb3 -S 0";
               // + "\" | cryptsetup luksChangeKey -q /dev/" + partitonName;

               System.err.println("the script is "+script);
        return script;
    }

    /**
     * The shell to delete the master passphrase
     *
     * @return
     * @throws org.freedesktop.dbus.exceptions.DBusException
     */
    public String createDeleteMasterKeyScript() {
        //getPartitionName();
        String script = "#!/bin/sh" + '\n'
                + "sudo cryptsetup luksKillSlot -q /dev/sdb3  1";
              //  + "cryptsetup luksKillSlot /dev/" + partitonName + " -q " + slotForMaster;

        return script;
    }

    public int checkConsoleOutput(String script) throws IOException {
        int exitValue = PROCESS_EXECUTOR.executeScript(script);

        return exitValue;

    }

}
