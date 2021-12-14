import pandas as pd
from shapely.geometry import Point
import geopandas
import os

gtfs_dir = "output/PID_GTFS_1_11_2021_filtered_test/"
stops = gtfs_dir + "stops.txt"

df_stops = pd.read_csv(stops, encoding='utf-8')
df_stops['geometry'] = df_stops.apply(lambda x: Point((float(x.stop_lon), float(x.stop_lat))), axis=1)
df_stops = df_stops[['stop_id', 'stop_name', 'geometry']]
df_geo = geopandas.GeoDataFrame(df_stops, geometry='geometry')
df_geo.crs = "EPSG:4326"
df_geo_crs = df_geo.to_crs("EPSG:5514")

df_geo_crs.to_file('stops.shp', driver='ESRI Shapefile', encoding='utf-8')