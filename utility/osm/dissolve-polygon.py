"""Consult: https://gis.stackexchange.com/questions/118223/merge-geojson-polygons-with-wgs84-coordinate"""

from json import load, dump
from argparse import ArgumentParser, FileType
from shapely.geometry import Polygon, mapping
from shapely.ops import cascaded_union

parser = ArgumentParser(description="Join the GeoJSON geometries")
parser.add_argument('infile', type=FileType('r'), help='GeoJSON file which geometries will be merged')
parser.add_argument('-o', '--outfile', dest='outfile', type=str, help='Path to output file')

if __name__ == '__main__':
    args = parser.parse_args()
    infile = args.infile
    outfile = args.outfile

    file = load(infile)

    polygons = []

    for feat in file['features']:
        polygon = Polygon([ (coor[0], coor[1]) for coor in  feat['geometry']['coordinates'][0] ])
        polygons.append(polygon)

    new_geometry = mapping(cascaded_union(polygons)) # This line merges the polygons
    new_feature = dict(type='Feature', id="", properties=dict(Name=""),geometry=dict(type=new_geometry['type'], coordinates=new_geometry['coordinates']))

    outjson = dict(type='FeatureCollection', features=[new_feature])

    with open(outfile, 'w') as f:
        dump(outjson, f)