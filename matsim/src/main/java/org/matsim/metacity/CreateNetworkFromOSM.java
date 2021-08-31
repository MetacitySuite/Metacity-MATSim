package org.matsim.metacity;

import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.NetworkWriter;
import org.matsim.contrib.osm.networkReader.LinkProperties;
import org.matsim.contrib.osm.networkReader.SupersonicOsmNetworkReader;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.ConfigWriter;
import org.matsim.pt2matsim.config.OsmConverterConfigGroup;
import org.matsim.pt2matsim.run.CreateDefaultOsmConfig;
import org.matsim.pt2matsim.run.Osm2MultimodalNetwork;
import org.matsim.core.network.algorithms.MultimodalNetworkCleaner;
import org.matsim.core.network.algorithms.NetworkCleaner;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * 1. It is possible to apply a link filter. The below example includes all links which are of hierarchy level "motorway".
 * For simplicity filtering for a coordinate is skipped but this is the place to test whether a coordinate is within a
 * certain area. This method is called for the from- and to nodes of a link.
 * 2. The reader generally tries to simplify the network as much as possible. If it is necessary to preserve certain nodes
 * for e.g. implementing counts it is possible to omit the simplification for certain node ids. The below example
 * prevents the reader to remove the node with id: 2.
 * 3. It is possible to override the default properties wich are assigned to a link of a certain hierarchy level. E.g. one could change the freespeed of highways by adding a new LinkProperties object for the 'highway' tag. The example below adds LinkProperties for residential links, which are otherwise ignored.
 * 4. After creating a link the reader will call the 'afterLinkCreated' hook with the newly created link, the original osm
 * tags, and a flag whether it is the forward or reverse direction of an osm-way. The below example sets the allowed
 * transport mode on all links to 'car' and 'bike'.
 *
 * https://github.com/matsim-org/matsim-libs/blob/master/contribs/osm/src/main/java/org/matsim/contrib/osm/examples/RunFullyConfiguredNetworkReader.java
 */

public class CreateNetworkFromOSM {

    //CreateNetworkFromOSM(String dir )

    private static final String dir = "/home/metakocour/IdeaProjects/matsim-example-project/scenarios/test-prague/";

    //network
//    private static final String inputOSMFile = dir + "prague-data/prague.osm.pbf";
//    private static final String networkOutputFile = dir + "network-prague.xml.gz";

    //multimodal network
    private static final String inputOSMFile = dir + "prague-data/prague2.osm";
    private static final String networkOutputFile = dir + "multi-network-prague.xml.gz";

    private static final String converterConfigFile = "osm2networkConfigFile.xml";
    private static final CoordinateTransformation coordinateTransformation = TransformationFactory.getCoordinateTransformation(TransformationFactory.WGS84, "EPSG:25832");

    public static void PrepareConfiguration(){
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
        osmConfig.setOutputCoordinateSystem("EPSG:25832");
        osmConfig.setOutputNetworkFile(networkOutputFile);

        // Save the createOsmConfigFile config (usually done manually)
        new ConfigWriter(osmConverterConfig).write(converterConfigFile);
    }

    public static void ConvertOSM2Network(){
        Network network = new SupersonicOsmNetworkReader.Builder()
                .setCoordinateTransformation(coordinateTransformation)
//                .setIncludeLinkAtCoordWithHierarchy((coord, hierachyLevel) -> hierachyLevel == LinkProperties.LEVEL_SECONDARY)
//                .setPreserveNodeWithId(id -> id == 2)
//                .addOverridingLinkProperties("residential", new LinkProperties(9, 1, 30.0 / 3.6, 1500, false))
//                .setAfterLinkCreated((link, osmTags, isReverse) -> link.setAllowedModes(new HashSet<>(Arrays.asList(TransportMode.car, TransportMode.bike))))
                .build()
                .read(inputOSMFile);
        new NetworkCleaner().run(network); //clean
        new NetworkWriter(network).write(networkOutputFile);
    }

    public static void ConvertOSM2MultimodalNetwork(){
        //create a multimodal network based on the config file
        OsmConverterConfigGroup osmConfig = OsmConverterConfigGroup.loadConfig(converterConfigFile);
        Osm2MultimodalNetwork.run(osmConfig); //automatically cleans the network

    }
    public static void main(String[] args) {
        //PrepareConfiguration(); //for multimodal network
        ConvertOSM2MultimodalNetwork();
    }
}
