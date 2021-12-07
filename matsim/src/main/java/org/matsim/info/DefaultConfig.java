package org.matsim.info;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Population;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.ConfigWriter;
import org.matsim.core.scenario.ScenarioUtils;

public class DefaultConfig {
    public static void main(String[] args) {
        System.out.println("============ CREATING DEFAULT CONFIG ============");
        Config config = ConfigUtils.createConfig();
        new ConfigWriter(config, ConfigWriter.Verbosity.all).write("defaultConfig.xml");
    }
}
