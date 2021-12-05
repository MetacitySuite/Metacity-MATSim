package org.matsim.metacity;

import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Population;
import org.matsim.api.core.v01.population.PopulationWriter;
import org.matsim.core.config.*;
//import org.matsim.core.config.Module;
import org.matsim.core.config.groups.GlobalConfigGroup;
import org.matsim.core.config.groups.NetworkConfigGroup;
import org.matsim.core.mobsim.jdeqsim.JDEQSimConfigGroup;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.MatsimNetworkReader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * First attempt to create a config file automatically.
 *
 *
 */


public class CreateConfig {
    //TODO
    public static final String configFile = "/home/metakocour/IdeaProjects/Metacity-MATSim/matsim/resources/config.properties";
    public static String matsimConfig = "/home/metakocour/IdeaProjects/Metacity-MATSim/matsim/data/matsim-files/config-test.xml";

    public static void main(String[] args){
        Properties prop = new Properties();
        try (FileInputStream fis = new FileInputStream(configFile)) {
            prop.load(fis);
        } catch (FileNotFoundException ex) {
            System.out.println("Config file not found.");
        } catch (IOException ex) {
            System.out.println("Error");
        }

        Config config = ConfigUtils.loadConfig(matsimConfig);
        //config.addCoreModules();
        //config.removeModule(JDEQSimConfigGroup.NAME);
        //config.controler().setMobsim("qsim");

        // global
        config.global().setCoordinateSystem(prop.getProperty("configEpsg"));
        config.global().setRandomSeed(Long.parseLong(prop.getProperty("seed")));
        config.global().setNumberOfThreads(Integer.parseInt(prop.getProperty("eventHandlerThreads")));
        config.removeModule(JDEQSimConfigGroup.NAME);
        new ConfigWriter(config).write(matsimConfig);
    }
}
