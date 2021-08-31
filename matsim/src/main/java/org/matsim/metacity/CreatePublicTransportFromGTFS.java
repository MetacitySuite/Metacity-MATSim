package org.matsim.metacity;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.ConfigWriter;
import org.matsim.core.network.algorithms.NetworkCleaner;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.pt.transitSchedule.api.TransitSchedule;
import org.matsim.pt2matsim.run.Gtfs2TransitSchedule;
import org.matsim.pt2matsim.config.PublicTransitMappingConfigGroup;
import org.matsim.pt2matsim.run.PublicTransitMapper;
import org.matsim.pt.utils.TransitScheduleValidator;

import java.util.Set;
import java.util.HashSet;


/**
 * Consult
 * https://github.com/matsim-org/pt2matsim/blob/master/src/main/java/org/matsim/pt2matsim/examples/Workflow.java
 */

public class CreatePublicTransportFromGTFS {
    private static final String dir = "/home/metakocour/IdeaProjects/matsim-example-project/scenarios/test-prague/";

    //input files
    private static final String GTFSDir = dir + "prague-data/PID_GTFS";
    private static final String fullNetworkFile = dir + "multi-network-prague.xml.gz";

    //intermediate output
    private static final String mappingConfigFile = dir + "01_ptmappingconfig.xml";
    private static final String unmappedScheduleFile = dir + "01_transitSchedule.xml.gz";
    private static final String transitVehicleFile = dir + "01_transitVehicles.xml.gz";

    //output files
    private static final String mappedScheduleFile = dir + "transitSchedule2.xml.gz";
    private static final String mappedNetworkFile = dir + "pt-network-prague2.xml.gz";


    private static final String matsimConfigFile = dir + "config-prague.xml";
    private static final String sampleDay = "20210723";


    public static void PrepareMappingConfig(){
        //create a mapping config file
        Config config = ConfigUtils.createConfig();
        Set<String> toRemove = new HashSet<>(config.getModules().keySet());
        toRemove.forEach(config::removeModule); // basically, remove all existing modules
        PublicTransitMappingConfigGroup group = PublicTransitMappingConfigGroup.createDefaultConfig();
        config.addModule(group); // now only add our special module
        group.setInputNetworkFile(fullNetworkFile);
        group.setInputScheduleFile(unmappedScheduleFile);
        group.setOutputScheduleFile(mappedScheduleFile);
        group.setOutputNetworkFile(mappedNetworkFile);
        new ConfigWriter(config).write(mappingConfigFile);
        //add tram transportModeAssignment manually
    }

    public static void TransitSchedulePlausibilityCheck(){
        Config config = ConfigUtils.loadConfig(matsimConfigFile);
        Scenario scenario = ScenarioUtils.loadScenario(config);
        Network network = scenario.getNetwork();
        TransitSchedule schedule = scenario.getTransitSchedule();

        if (!TransitScheduleValidator.validateAll(schedule, network).isValid())
            System.out.print("(warning) Invalid transit schedule");
        else
            System.out.print("(info) Valid transit schedule");


    }

    public static void main(String[] args) {
        //get unmapped PT schedule from GTFS
        //Gtfs2TransitSchedule.run(GTFSDir, sampleDay, "EPSG:25832", unmappedScheduleFile, transitVehicleFile);

        //mapping the PT schedule to the existing network
        //PrepareMappingConfig(); //prepare mapping config, mostly copied from org.matsim.pt2matsim.run.CreateDefaultPTMapperConfig

        //do the mapping according to pt mapping config
        PublicTransitMapper.run(mappingConfigFile);

        //check plausibility
        TransitSchedulePlausibilityCheck();
    }
}
