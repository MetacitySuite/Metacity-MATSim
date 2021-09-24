package org.matsim.metacity;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import static org.matsim.metacity.CreateMultiModalNetwork.ConvertOSM2MultimodalNetwork;
import static org.matsim.metacity.CreateMultiModalNetwork.PrepareConfiguration;

public class CreateMatsimNetwork {
    public static final String configFile = "/home/metakocour/IdeaProjects/Metacity-MATSim/matsim/resources/config.properties";

    public static void main(String[] args)  {
        Properties prop = new Properties();
        try (FileInputStream fis = new FileInputStream(configFile)) {
            prop.load(fis);
        } catch (FileNotFoundException ex) {
            System.out.println("Config file not found.");
        } catch (IOException ex) {
            System.out.println("Error");
        }

        String dataDir = prop.getProperty("dataDir");
        String matsimFileDir = prop.getProperty("matsimOutputFilesDir");

        //multimodal network
        String inputOSMFile = dataDir + prop.getProperty("osmFile");
        String networkOutputFile = matsimFileDir + prop.getProperty("networkOutputFile");
        String converterConfigFile = matsimFileDir + prop.getProperty("osmConverterConfigFile");

        PrepareConfiguration(converterConfigFile, inputOSMFile, networkOutputFile);
        ConvertOSM2MultimodalNetwork(converterConfigFile);
    }
}
