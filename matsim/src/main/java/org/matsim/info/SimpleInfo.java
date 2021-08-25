package org.matsim.info;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Population;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.scenario.ScenarioUtils;


public class SimpleInfo {
    private static final String dir = "/home/metakocour/IdeaProjects/matsim-example-project/scenarios/test-prague/";
    private static final String matsimConfigFile = dir + "config-prague.xml";

    private static final Config config = ConfigUtils.loadConfig(matsimConfigFile);
    private static final Scenario scenario = ScenarioUtils.loadScenario(config);
    private static final Network network = scenario.getNetwork();
    private static final Population population = scenario.getPopulation();

    public static void main(String[] args) {
        System.out.println("============ INPUT DATA INFO ============");
        System.out.println("NETWORK");
        System.out.println("# nodes: " + network.getNodes().size());
        System.out.println("# links: " + network.getLinks().size());
        System.out.println("POPULATION");
        System.out.println("# agents: " + population.getPersons().size());
    }
}
