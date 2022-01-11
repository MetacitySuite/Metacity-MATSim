from argparse import ArgumentParser, FileType
import xml.etree.ElementTree as ET

parser = ArgumentParser(description="Change tag names in accordance with MATSim OSM reader.")
parser.add_argument('infile', type=FileType('r'), help='OSM file to be changed')
parser.add_argument('-o', '--outfile', dest='outfile', type=str, help='Path to output file')

node_attrib = ['timestamp', 'user', 'actor']

def main():
    args = parser.parse_args()
    infile = args.infile
    outfile = args.outfile

    tree = ET.parse(infile)
    root = tree.getroot()

    nodes = tree.findall(".//node")
    for n in nodes:
        for at in node_attrib:
            del n.attrib[at]

    for el in root.iter():
        for tag_el in el.findall('tag'):
            #remove path attribute ; remove attributes with empty values`
            if tag_el.attrib['k'] == '_path_' or tag_el.attrib['v'] == "": 
                el.remove(tag_el)
            
            #if tag_el.attrib['k'] == '_ONEWAY_':
            #    if tag_el.attrib['v'] == "":
            #        tag_el.set('v', "no")
            #    else:
            #        tag_el.set('v', "yes")
            #    
            #    tag_el.set('k', tag_el.attrib['k'].lower())

            #rename attributes
            tag_el.set('k', tag_el.attrib['k'].replace("_", ""))
        
        for way_el in el.findall('way'):
            for at in node_attrib:
                del way_el.attrib[at]
        
    tree.write(outfile, encoding="UTF-8", xml_declaration=False)



if __name__ == '__main__':
    main()

