package org.matsim.metacity;

import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;
import org.matsim.core.network.algorithms.NetworkCleaner;
import org.matsim.core.config.ConfigWriter;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.NetworkWriter;
import org.matsim.contrib.osm.networkReader.SupersonicOsmNetworkReader;
import org.matsim.pt2matsim.run.CreateDefaultOsmConfig;
import org.matsim.pt2matsim.config.OsmConverterConfigGroup;
import org.matsim.pt2matsim.run.Osm2MultimodalNetwork;

import java.util.Set;
import java.util.HashSet;

public class CreateMultiModalNetwork{
    public static void PrepareConfiguration(String converterConfigFile, String inputOSMFile, String networkOutputFile, String epsg){
        //create a config file to create a multimodal network
        CreateDefaultOsmConfig.main(new String[]{converterConfigFile});
        Config osmConverterConfig = ConfigUtils.loadConfig(converterConfigFile, new OsmConverterConfigGroup());

        //clear the default config file
        Set<String> toRemove = new HashSet<>(osmConverterConfig.getModules().keySet());
        toRemove.removeIf(s -> s.equals("OsmConverter"));
        toRemove.forEach(osmConverterConfig::removeModule); // remove modules automatically

        //specify input and output files
        OsmConverterConfigGroup osmConfig = ConfigUtils.addOrGetModule(osmConverterConfig, OsmConverterConfigGroup.class);
        osmConfig.setOsmFile(inputOSMFile);
        osmConfig.setOutputCoordinateSystem(epsg);
        osmConfig.setOutputNetworkFile(networkOutputFile);

        // Open the createOsmConfigFile Config and set the parameters to the required values
        // (usually done manually by opening the config with a simple editor)
        new ConfigWriter(osmConverterConfig).write(converterConfigFile);
    }

    public static void ConvertOSM2MultimodalNetwork(String converterConfigFile){
        //create a multimodal network based on the config file
        OsmConverterConfigGroup osmConfig = OsmConverterConfigGroup.loadConfig(converterConfigFile);
        Osm2MultimodalNetwork.run(osmConfig); //automatically cleans the network
    }
}