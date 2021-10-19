package org.matsim.project;

import org.geotools.referencing.CRS;
import org.junit.Assert;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.NetworkWriter;
import org.matsim.core.config.ConfigWriter;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.MatsimNetworkReader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.matsim.core.utils.geometry.geotools.MGC;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.geotools.referencing.CRS;


import static org.matsim.metacity.CreateMultiModalNetwork.ConvertOSM2MultimodalNetwork;
import static org.matsim.metacity.CreateMultiModalNetwork.PrepareConfiguration;
import static org.matsim.metacity.CreatePublicTransportFromGTFS.*;

public class CreateMatsimNetwork {
    /**
     * Configuration file containing various file names and data directories
     */
    public static final String configFile = "/home/metakocour/IdeaProjects/Metacity-MATSim/matsim/resources/config.properties";

    public static String dataDir;

    /**
     * Output for the converter = input files for MATSim simulation
     */
    public static String matsimOutputFilesDir;

    /**
     * Folder with configuration files: MATSim, OSM converter, PT mapping
     */
    public static String matsimConfigFilesDir;

    /**
     * Creates a configuration file specifying how OSM should be transformed to MATSim multimodal network
     * The OSM converter config file can be manually edited to set desirable parameters for the simulation
     * @param prop
     */
    public static void PrepareNetworkConfig(Properties prop){
        //multimodal network
        String converterConfigFile = matsimConfigFilesDir + prop.getProperty("osmConverterConfigFile");
        String inputOSMFile = dataDir + prop.getProperty("osmFile");
        String networkOutputFile = matsimOutputFilesDir + prop.getProperty("networkOutputFile");
        String epsg = prop.getProperty("intermediateEPSG");

        PrepareConfiguration(converterConfigFile, inputOSMFile, networkOutputFile, epsg);
    }

    /**
     * Creates a MATSim network based on the converter configuration file
     * @param prop
     */
    public static void CreateNetwork(Properties prop){
        String converterConfigFile = matsimConfigFilesDir + prop.getProperty("osmConverterConfigFile");
        //multimodal network
        ConvertOSM2MultimodalNetwork(converterConfigFile);
    }

    /**
     * Processes GTFS files and creates an unmapped schedule file and transit vehicle file - intermediate outputs for mapping public transport to MATSim network
     * @param prop
     */
    public static void PrepareGTFS(Properties prop){
        String gtfsDir = dataDir + prop.getProperty("gtfsFolder");
        String sampleDay = prop.getProperty("gtfsSampleDay");
        String epsg = prop.getProperty("intermediateEPSG");

        String unmappedScheduleFile = matsimOutputFilesDir + prop.getProperty("unmappedScheduleFile");
        String transitVehicleFile = matsimOutputFilesDir + prop.getProperty("transitVehicleFile");

        ScheduleFromGTFS(gtfsDir, sampleDay, epsg, unmappedScheduleFile, transitVehicleFile);
    }

    /**
     * Creates a configuration file specifying how public transport should be mapped to MATSim multimodal network
     * The config file can be manually edited to set desirable parameters for the mapping
     * @param prop
     */
    public static void PreparePublicTransportConfig(Properties prop){

        String networkFile = matsimOutputFilesDir + prop.getProperty("networkOutputFile");
        String unmappedScheduleFile = matsimOutputFilesDir + prop.getProperty("unmappedScheduleFile");
        String mappedScheduleFile = matsimOutputFilesDir + prop.getProperty("mappedScheduleFile");
        String mappedNetworkFile = matsimOutputFilesDir + prop.getProperty("mappedNetworkFile");

        String ptMappingConfigFile = matsimConfigFilesDir + prop.getProperty("ptMappingConfigFile");

        //add tram transportModeAssignment manually
        PrepareMappingConfig(networkFile, unmappedScheduleFile, mappedScheduleFile, mappedNetworkFile, ptMappingConfigFile); //prepare mapping config, mostly copied from org.matsim.pt2matsim.run.CreateDefaultPTMapperConfig
    }

    /**
     * Maps public transport to multimodal MATSim network according to the config file
     * @param prop
     */
    public static void MapPublicTransport(Properties prop){
        String ptMappingConfigFile = matsimConfigFilesDir + prop.getProperty("ptMappingConfigFile");
        String matsimConfigFile = matsimConfigFilesDir + prop.getProperty("matsimConfigFile");
        MapGTFS2Network(ptMappingConfigFile, matsimConfigFile);
    }

    /**
     *
     * @param prop
     */
    public static void TransformNetworkToKrovak(Properties prop){
        String networkFile = matsimOutputFilesDir + prop.getProperty("mappedNetworkFile");
        String epsg = prop.getProperty("intermediateEPSG");
        String newNetworkFile = matsimOutputFilesDir + prop.getProperty("transformedMappedNetworkFile");

        //read and transform to a new coordinate system
        Network network = NetworkUtils.createNetwork();
        String KROVAK = "PROJCS[\"S-JTSK / Krovak East North\",GEOGCS[\"S-JTSK\",DATUM[\"System_Jednotne_Trigonometricke_Site_Katastralni\",SPHEROID[\"Bessel 1841\",6377397.155,299.1528128,AUTHORITY[\"EPSG\",\"7004\"]],TOWGS84[570.8,85.7,462.8,4.998,1.587,5.261,3.56],AUTHORITY[\"EPSG\",\"6156\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.0174532925199433,AUTHORITY[\"EPSG\",\"9122\"]],AUTHORITY[\"EPSG\",\"4156\"]],PROJECTION[\"Krovak\"],PARAMETER[\"latitude_of_center\",49.5],PARAMETER[\"longitude_of_center\",24.83333333333333],PARAMETER[\"azimuth\",30.28813972222222],PARAMETER[\"pseudo_standard_parallel_1\",78.5],PARAMETER[\"scale_factor\",0.9999],PARAMETER[\"false_easting\",0],PARAMETER[\"false_northing\",0],UNIT[\"metre\",1,AUTHORITY[\"EPSG\",\"9001\"]],AXIS[\"X\",EAST],AXIS[\"Y\",NORTH],AUTHORITY[\"EPSG\",\"5514\"]]";
        //String EPSGKROVAK = "PROJCS[\"S-JTSK / Krovak East North\",GEOGCS[\"S-JTSK\",DATUM[\"System_Jednotne_Trigonometricke_Site_Katastralni\",SPHEROID[\"Bessel 1841\",6377397.155,299.1528128,AUTHORITY[\"EPSG\",\"7004\"]],TOWGS84[589,76,480,0,0,0,0],AUTHORITY[\"EPSG\",\"6156\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.0174532925199433,AUTHORITY[\"EPSG\",\"9122\"]],AUTHORITY[\"EPSG\",\"4156\"]],PROJECTION[\"Krovak\"],PARAMETER[\"latitude_of_center\",49.5],PARAMETER[\"longitude_of_center\",24.83333333333333],PARAMETER[\"azimuth\",30.28813972222222],PARAMETER[\"pseudo_standard_parallel_1\",78.5],PARAMETER[\"scale_factor\",0.9999],PARAMETER[\"false_easting\",0],PARAMETER[\"false_northing\",0],UNIT[\"metre\",1,AUTHORITY[\"EPSG\",\"9001\"]],AXIS[\"X\",EAST],AXIS[\"Y\",NORTH],AUTHORITY[\"EPSG\",\"5514\"]]";

        new MatsimNetworkReader(epsg, KROVAK, network).readFile(networkFile);
        new NetworkWriter(network).write(newNetworkFile);
    }

    public static void main(String[] args)  {
        Properties prop = new Properties();
        try (FileInputStream fis = new FileInputStream(configFile)) {
            prop.load(fis);
        } catch (FileNotFoundException ex) {
            System.out.println("Config file not found.");
        } catch (IOException ex) {
            System.out.println("Error");
        }

        dataDir = prop.getProperty("dataDir") + prop.getProperty("dataFolder");
        matsimOutputFilesDir = prop.getProperty("dataDir") + prop.getProperty("matsimOutputFilesFolder");
        matsimConfigFilesDir = prop.getProperty("dataDir") + prop.getProperty("matsimConfigFilesFolder");

        // Open the createOsmConfigFile Config and set the parameters to the required values (usually done manually by opening the config with a simple editor)
        // Define a wayDefaultParams section, which converts "railway=tram" OSM links to "tram" MATSim links.
        // By default there is a block that converts them to MATSim "rail" links, so you can simply change this value.
        // <param name="allowedTransportModes" value="tram" />
        // Uncomment if you want to create a new config file
        //PrepareNetworkConfig(prop);

        CreateNetwork(prop); //Creates multimodal network

        //get unmapped PT schedule from GTFS
        PrepareGTFS(prop);

        // add tram transportModeAssignment manually
        // Uncomment if you want to create a new config file
        //PreparePublicTransportConfig(prop);
        MapPublicTransport(prop);

        //Transform to different CRS
        TransformNetworkToKrovak(prop);
    }
}
