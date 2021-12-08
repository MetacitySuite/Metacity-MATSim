import xml.etree.ElementTree as ET
import json

configFilePath = "config-prague.xml"
propertiesFilePath = "config-properties.json"
editedConfigFilePath = "config-prague-edit.xml"

propertiesFile = open(propertiesFilePath, encoding='utf-8')
properties = json.load(propertiesFile)

tree = ET.parse(configFilePath)
root = tree.getroot()

for x in properties.items():
    for k, v in properties[str(x[0])].items():
        #print(x[0], k, v)
        #ax = root.find(".//*[@name=' + " + x[0] + "']/*[@name='" + k + "']")
        element_str = ".//*[@name='" + x[0] + "']/*[@name='" + k + "']"
        ax = root.find(element_str)
        #print(ax)
        ax.attrib['value'] = str(v)

with open(editedConfigFilePath, 'wb') as out:
    out.write(b'<?xml version="1.0" ?>\n')
    out.write(b'<!DOCTYPE config SYSTEM "http://www.matsim.org/files/dtd/config_v2.dtd">\n')
    tree.write(out, xml_declaration=False)