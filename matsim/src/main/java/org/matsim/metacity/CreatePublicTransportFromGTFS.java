package org.matsim.metacity;

import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.ConfigWriter;
import org.matsim.core.network.algorithms.NetworkCleaner;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Network;
import org.matsim.pt.transitSchedule.api.TransitSchedule;
import org.matsim.pt.utils.TransitScheduleValidator;
import org.matsim.pt2matsim.run.Gtfs2TransitSchedule;
import org.matsim.pt2matsim.config.PublicTransitMappingConfigGroup;
import org.matsim.pt2matsim.run.PublicTransitMapper;

import java.util.Set;
import java.util.HashSet;


/**
 * Consult
 * https://github.com/matsim-org/pt2matsim/blob/master/src/main/java/org/matsim/pt2matsim/examples/Workflow.java
 */

public class CreatePublicTransportFromGTFS {
    public static void ScheduleFromGTFS(String dataDir, String sampleDay, String epsg, String unmappedScheduleFile, String transitVehicleFile){
        //get unmapped PT schedule from GTFS
        Gtfs2TransitSchedule.run(dataDir, sampleDay, epsg, unmappedScheduleFile, transitVehicleFile);
    }

    public static void PrepareMappingConfig(String fullNetworkFile, String unmappedScheduleFile, String mappedScheduleFile, String mappedNetworkFile, String mappingConfigFile){
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

    public static void TransitSchedulePlausibilityCheck(String matsimConfigFile){
        Config config = ConfigUtils.loadConfig(matsimConfigFile);
        Scenario scenario = ScenarioUtils.loadScenario(config);
        Network network = scenario.getNetwork();
        TransitSchedule schedule = scenario.getTransitSchedule();

        if (!TransitScheduleValidator.validateAll(schedule, network).isValid())
            System.out.print("(warning) Invalid transit schedule");
        else
            System.out.print("(info) Valid transit schedule");
    }

    public static void MapGTFS2Network(String mappingConfigFile, String matsimConfigFile){
        //do the mapping according to pt mapping config
        PublicTransitMapper.run(mappingConfigFile);

        //check plausibility
        TransitSchedulePlausibilityCheck(matsimConfigFile);
    }


}
