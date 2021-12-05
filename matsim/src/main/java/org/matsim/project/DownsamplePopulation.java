package org.matsim.project;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Population;
import org.matsim.api.core.v01.population.PopulationWriter;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.gbl.MatsimRandom;
import org.matsim.core.scenario.ScenarioUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
public class DownsamplePopulation {
    private static final double DOWNSAMPLE_FACTOR = 0.1;
    private static final String dir = "data/matsim-files/";
    private static final String matsimConfigFile = dir + "config-prague.xml";
    private static final String matsimOutputFilesDir = "input/";

    public static void Downsample(final Map<Id<Person>, ? extends Person> map, final double sample ){
        final Random rnd = MatsimRandom.getLocalInstance();
        System.out.println("(warn) Population downsampled from " + map.size() + " agents.");
        map.values().removeIf( person -> rnd.nextDouble() > sample ) ;
        System.out.println("(warn) Population downsampled to " + map.size() + " agents.");
    }

    /*public static Scenario DownsamplePopulation(Config config){
    //Config config = ConfigUtils.loadConfig(matsimConfigFile);
    final Scenario scenario = ScenarioUtils.loadScenario(config);
    Downsample(scenario.getPopulation().getPersons(), DOWNSAMPLE_FACTOR);
    return scenario;
    }*/

    public static void main(String[] args){
        double sampleFactor = DOWNSAMPLE_FACTOR;
        if ( args==null || args.length==0 || args[0]==null ){
            System.out.println("(info) missing arguments: sample factor of 0.1 will be used.");
        } else {
            sampleFactor = Double.parseDouble(args[0]);
        }

        Config config = ConfigUtils.loadConfig(matsimConfigFile);
        Scenario scenario = ScenarioUtils.loadScenario(config);
        Downsample(scenario.getPopulation().getPersons(), sampleFactor);

        //write downsampled population file
        int percent = (int)(sampleFactor * 100);
        String outputPopulationFile = dir + matsimOutputFilesDir + "population-" + Integer.toString(percent) + "pct.xml";
        Population pop = scenario.getPopulation();
        new PopulationWriter(pop).write(outputPopulationFile);
    }
}
