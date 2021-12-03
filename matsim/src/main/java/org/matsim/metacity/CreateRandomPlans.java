package org.matsim.metacity;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.Population;
import org.matsim.api.core.v01.population.PopulationFactory;
import org.matsim.api.core.v01.population.PopulationWriter;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.NetworkConfigGroup;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.population.PopulationUtils;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.geometry.CoordUtils;

import java.util.*;

/**
 * Basic class to create random home-work plans for agents (population).
 * Used until the synthetic population module is finished and a real plans file will be created.
 *
 * FOR TESTING RUNS ONLY!
 *
 */
public class CreateRandomPlans {
    /*Edit if necessary*/
    private static final String dir = "/home/metakocour/IdeaProjects/Metacity-MATSim/matsim/data/matsim-files/";
    private static final String matsimConfigFile = dir + "config-prague.xml";
    private static final String networkFile = dir + "input/pt-network-prague.xml.gz";
    private static final int demandSize = 1000; //population size, usually 10% of real population size is enough
    private static final double radius = 5000;
    private static final String outputPopulationFile = dir + "input/test-population-" + Integer.toString(demandSize) + ".xml";
    //in hours
    private static final int earliestHomeDeparture = 7; // +/- offsetTime
    private static final int earliestHomeDepartureOffset = 2;

    //TODO: does not take into account travel time
    private static final int workDuration = 9; // +/- offsetTime
    private static final int workDurationOffsetTime = 3;

    private static Map<Id<Node>,? extends  Node> allNodesInNetwork;
    private static List<Id<Node>> keys;

    private static Node GetRandomNodeInNetwork(){
        //get random node Id
        Random gen = new Random();
        Id<Node> randomKey = keys.get(gen.nextInt(keys.size()));
        return allNodesInNetwork.get(randomKey);
    }

    private static void ProcessNetwork(Network network){
        allNodesInNetwork = network.getNodes();
        keys = new ArrayList<>(allNodesInNetwork.keySet());
    }

    private static Population CreatePopulation(Network network, Config config){
        //Population pop = PopulationUtils.createPopulation(config, network);
        //Population pop = scenario.getPopulation();
        Population pop = PopulationUtils.createPopulation(config);
        PopulationFactory pf = pop.getFactory();

        for (int i = 0; i < demandSize; ++i) {
            //create a person
            Person person = pf.createPerson(Id.createPersonId(i+1));
            pop.addPerson(person);

            //create person's plans
            Plan plan = pf.createPlan();

            //home activity
            Coord homeCoord = GetRandomNodeInNetwork().getCoord();

            //define 1 transport mode
            //TODO: something smarter
            String mode = "car";
            /*if (Math.random() > 0.5){
                mode = "pt";
                if(Math.random() > 0.5){
                    mode = "walk";
                }
            }*/

            Activity activity1 = pf.createActivityFromCoord("home", homeCoord);
            double homeTime = earliestHomeDeparture + (Math.random() * earliestHomeDepartureOffset * ((Math.random() < 0.5) ? -1 : 1));
            activity1.setEndTime(homeTime * 3600);
            plan.addActivity(activity1);
            plan.addLeg(pf.createLeg(mode));

            //work activity
            Coord workCoord = GetRandomNodeInNetwork().getCoord();
            while(CoordUtils.calcEuclideanDistance(homeCoord, workCoord) > radius){
                workCoord = GetRandomNodeInNetwork().getCoord();
            }

            Activity activity2 = pf.createActivityFromCoord("work", workCoord);
            double workTime = homeTime + workDuration + ( Math.random() * workDurationOffsetTime * ((Math.random() < 0.5) ? -1 : 1) );
            activity2.setEndTime(workTime* 3600);
            plan.addActivity(activity2);
            plan.addLeg(pf.createLeg(mode));

            Activity activity3 = pf.createActivityFromCoord("home", homeCoord);
            plan.addActivity(activity3);
            person.addPlan(plan);
        }
        return pop;
    }

    public static void main(String[] args){
        Config config = ConfigUtils.loadConfig(matsimConfigFile);
        NetworkConfigGroup ncg = config.network();

        //read the network file
        Network network = NetworkUtils.createNetwork();
        String epsg = ncg.getInputCRS();
        new MatsimNetworkReader(epsg, epsg, network).readFile(networkFile);

        //crate a list of all nodes in the network
        ProcessNetwork(network);

        //Create population from this network
        Population pop = CreatePopulation(network, config);
        new PopulationWriter(pop).write(outputPopulationFile);
    }
}