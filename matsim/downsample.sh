#!/bin/bash
if [ -z "$1" ]
then 
	echo "Empty downsampling scale factor argument!"
else
	java -cp matsim-14.0-v1.0.0-SNAPSHOT.jar org.matsim.project.DownsamplePopulation "$1"
fi

