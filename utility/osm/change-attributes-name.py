from argparse import ArgumentParser, FileType
import xml.etree.ElementTree as ET

parser = ArgumentParser(description="Change tag names in accordance with MATSim OSM reader.")
parser.add_argument('infile', type=FileType('r'), help='OSM file to be changed')
parser.add_argument('-o', '--outfile', dest='outfile', type=str, help='Path to output file')

if __name__ == '__main__':
    args = parser.parse_args()
    infile = args.infile
    outfile = args.outfile

    tree = ET.parse(infile)
    root = tree.getroot()

    for el in root.iter():
        for tag_el in el.findall('tag'):
            #remove path attribute
            if tag_el.attrib['k'] == '_path_': 
                el.remove(tag_el)

            #rename attributes
            tag_el.set('k', tag_el.attrib['k'].replace("_", ""))
            
            #remove attributes with empty values
            if tag_el.attrib['v'] == '':
                el.remove(tag_el)
    
    tree.write(outfile, encoding="UTF-8", xml_declaration=False)

