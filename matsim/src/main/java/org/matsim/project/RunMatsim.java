/* *********************************************************************** *
 * project: org.matsim.*												   *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2008 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */
package org.matsim.project;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.contrib.otfvis.OTFVisLiveModule;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.PlansCalcRouteConfigGroup;
//import org.matsim.core.config.groups.QSimConfigGroup;
import org.matsim.core.config.groups.QSimConfigGroup.SnapshotStyle;
import org.matsim.core.config.groups.QSimConfigGroup.TrafficDynamics;
import org.matsim.core.controler.Controler;
//import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import org.matsim.core.scenario.ScenarioUtils;
//import org.matsim.vis.otfvis.OTFVisConfigGroup;


/**
 * @author nagel
 *
 */
public class RunMatsim{

	public static void main(String[] args)  {
		Config config;

		if ( args==null || args.length==0 || args[0]==null ){
			config = null;
		} else {
			config = ConfigUtils.loadConfig( args );
		}

		// Possibly modify config here --------------------------------------------------------------------------------

		//qsim
		config.qsim().setTrafficDynamics( TrafficDynamics.kinematicWaves );
		config.qsim().setSnapshotStyle( SnapshotStyle.kinematicWaves );
		//config.qsim().setInsertingWaitingVehiclesBeforeDrivingVehicles(true);

		//https://github.com/matsim-org/matsim-code-examples/issues/395
		config.qsim().setPcuThresholdForFlowCapacityEasing( 0.3 );

		//planscalcroute
		//config.plansCalcRoute().setInsertingAccessEgressWalk(true);
		//config.plansCalcRoute().removeModeRoutingParams( TransportMode.bike );

		//controler
		config.controler().setOverwriteFileSetting( OverwriteFileSetting.deleteDirectoryIfExists );

		Scenario scenario = ScenarioUtils.loadScenario(config) ;

		// Possibly modify scenario here ------------------------------------------------------------------------------

		Controler controler = new Controler( scenario ) ;

		// Possibly modify controler here -----------------------------------------------------------------------------

		controler.addOverridingModule( new OTFVisLiveModule() ) ;

		// ---
		controler.run();
	}
	
}
