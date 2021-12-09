#!/bin/bash

property_file="../utility/matsim/matsim-config/properties-config-run.json"
config_file="data/matsim-files/config-prague.xml"

#if [ "$#" -ne 2 ]; then
#    echo "Provide [path to property json file] and [path to base MATSim config file]"
#    exit 1
#fi

#property_file="$1"
#config_file="$2"

config_dir=${config_file%/*}

config_file_name=${config_file##*/}
new_config_file_name="${config_file_name%.*}""-run.xml"
new_config_file="$config_dir""/""$new_config_file_name"

python3 ../utility/matsim/matsim-config/GenMatsimConfig.py "$property_file" "$config_file" "$new_config_file"
