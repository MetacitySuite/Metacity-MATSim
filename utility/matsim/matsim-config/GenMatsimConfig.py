import xml.etree.ElementTree as ET
import json
from argparse import ArgumentParser

#configFilePath = "config-prague.xml"
#propertiesFilePath = "config-properties.json"
#editedConfigFilePath = "config-prague-edit.xml"

def open_file(file):
    opened_file = open(file, encoding='utf-8')
    if not opened_file:
        print("Missing \"config-properties.json\"")
        return
    return opened_file

class MatsimConfig:
    def __init__(self, template_config_file):
        self.tree = ET.parse(template_config_file)
        self.root = self.tree.getroot()

    def edit_attributes(self, properties):
        for x in properties.items():
            for k, v in properties[str(x[0])].items():
                #ax = root.find(".//*[@name=' + " + x[0] + "']/*[@name='" + k + "']")
                element_str = ".//*[@name='" + x[0] + "']/*[@name='" + k + "']"
                ax = self.root.find(element_str)
                ax.attrib['value'] = str(v)

    def save_config(self, path):
        with open(path, 'wb') as out:
            out.write(b'<?xml version="1.0" ?>\n')
            out.write(b'<!DOCTYPE config SYSTEM "http://www.matsim.org/files/dtd/config_v2.dtd">\n')
            self.tree.write(out, xml_declaration=False)

def main():
    parser = ArgumentParser(description="Generate new MATSim config.xml with new property values defined in a json file.")
    parser.add_argument('properties_file', type=str, help='File with property names and values - json')
    parser.add_argument('config_input_file', type=str, help='Path to MATSim configuration file')
    parser.add_argument('config_output_file', type=str, help='Path to newly generated MATSim configuration file')
    args = parser.parse_args()

    propertiesFile = open_file(args.properties_file)
    properties = json.load(propertiesFile)
    config_file = MatsimConfig(args.config_input_file)
    config_file.edit_attributes(properties)
    config_file.save_config(args.config_output_file)

if __name__ == "__main__":
    main()