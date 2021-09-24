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
import org.matsim.core.population.PopulationUtils;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.geometry.CoordUtils;

import java.util.*;

public class CreateRandomPlans {
    private static final String dir = "/home/metakocour/IdeaProjects/Metacity-MATSim/matsim/data/matsim-files/";
    private static final String matsimConfigFile = dir + "config-prague.xml";
    private static final String populationFile = dir + "input/test-population-150k.xml";
    private static final int demandSize = 150000;
    private static final double radius = 5000;

    private static final Config config = ConfigUtils.loadConfig(matsimConfigFile);
    private static final Scenario scenario = ScenarioUtils.loadScenario(config);
    private static final Network network = scenario.getNetwork();
    private static final Map<Id<Node>,? extends  Node> allNodesInNetwork = network.getNodes();
    private static final List<Id<Node>> keys = new ArrayList<>(allNodesInNetwork.keySet());

    private static Node GetRandomNodeInNetwork(){
        //get random node Id
        Random gen = new Random();
        Id<Node> randomKey = keys.get(gen.nextInt(keys.size()));
        return allNodesInNetwork.get(randomKey);
    }

    private static void CreatePopulation(){
        //Population pop = PopulationUtils.createPopulation(config, network);
        Population pop = scenario.getPopulation();
        PopulationFactory pf = pop.getFactory();


        for (int i = 0; i < demandSize; ++i) {
            //create a person
            Person person = pf.createPerson(Id.createPersonId(i+1));
            pop.addPerson(person);

            //create person's plans
            Plan plan = pf.createPlan();

            //home activity
            Coord homeCoord = GetRandomNodeInNetwork().getCoord();

            Activity activity1 = pf.createActivityFromCoord("home", homeCoord);
            double h = 8 + Math.random() * 4;
            activity1.setEndTime(h * 3600);
            plan.addActivity(activity1);
            plan.addLeg(pf.createLeg("car"));

            //work activity
            Coord workCoord = GetRandomNodeInNetwork().getCoord();
            while(CoordUtils.calcEuclideanDistance(homeCoord, workCoord) > radius){
                workCoord = GetRandomNodeInNetwork().getCoord();
            }

            Activity activity2 = pf.createActivityFromCoord("work", workCoord);
            activity2.setEndTime((h + 4 + Math.random() * 4) * 3600);
            plan.addActivity(activity2);
            plan.addLeg(pf.createLeg("car"));

            Activity activity3 = pf.createActivityFromCoord("home", homeCoord);
            plan.addActivity(activity3);
            person.addPlan(plan);
        }
    }

    public static void main(String[] args){
        //Create population from nothing
        CreatePopulation();
        PopulationWriter populationWriter = new PopulationWriter(scenario.getPopulation(), scenario.getNetwork());
        populationWriter.write(populationFile);
    }
}