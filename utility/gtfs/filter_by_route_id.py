import pandas as pd
import numpy as np
import os
from shutil import copyfile

gtfs_dir = "data/PID_GTFS_1_11_2021/"
filtered_gtfs_dir = "output/PID_GTFS_1_11_2021_filtered/"

modified_files = ["routes.txt", "trips.txt", "stop_times.txt", "stops.txt", "route_stops.txt", "shapes.txt"]

routes = gtfs_dir + "routes.txt"
trips = gtfs_dir + "trips.txt"
stop_times = gtfs_dir + "stop_times.txt"
stops = gtfs_dir + "stops.txt"
route_stops = gtfs_dir + "route_stops.txt"
shapes = gtfs_dir + "shapes.txt"

def main():
    route_id = []

    for x in range(300):
        route = "L" + str(x)
        route_id.append(route)

    subway = ["L991", "L992", "L993"]
    ferry = ["L1801", "L1802","L1803","L1804","L1805","L1806","L1807"]

    for x in np.arange(901,917):
        route = "L" + str(x)
        route_id.append(route)

    route_id = route_id + subway + ferry
    

    print("Processing routes.txt...")
    df_routes = pd.read_csv(routes, dtype=str)
    df_routes.update('\'' + df_routes[['route_long_name', 'route_url']].astype(str) + '\'')
    df_routes_filtered = df_routes.loc[df_routes['route_id'].isin(route_id)]

    print("Processing trips.txt...")
    df_trips = pd.read_csv(trips, dtype=str)
    #print(len(df_trips['trip_id'].unique()))
    df_trips.update('\'' + df_trips['trip_headsign'].astype(str) + '\'')
    df_trips_filtered = df_trips.loc[df_trips['route_id'].isin(route_id)]

    trip_id = df_trips_filtered['trip_id'].unique()
    shape_id = df_trips_filtered['shape_id'].unique()

    print("Processing stop_times.txt...")
    df_stop_times = pd.read_csv(stop_times, dtype=str)
    df_stop_times_filtered = df_stop_times.loc[df_stop_times['trip_id'].isin(trip_id)]

    stop_id = df_stop_times_filtered['stop_id'].unique()

    print("Processing stops.txt...")
    df_stops = pd.read_csv(stops, dtype=str)
    df_stops.update('\'' + df_stops[['stop_name', 'zone_id']].astype(str) + '\'')
    df_stops_filtered = df_stops.loc[df_stops['stop_id'].isin(stop_id)]

    print("Processing route_stops.txt...")
    df_route_stops = pd.read_csv(route_stops, dtype=str)
    df_route_stops_filtered = df_route_stops.loc[df_route_stops['stop_id'].isin(stop_id)]

    print("Processing shapes.txt...")
    df_shapes = pd.read_csv(shapes, dtype=str)
    df_shapes_filtered = df_shapes.loc[df_shapes['shape_id'].isin(shape_id)]

    if not os.path.exists(filtered_gtfs_dir):
        os.makedirs(filtered_gtfs_dir)

    print("Saving modified GTFS...")
    df_routes_filtered.to_csv(filtered_gtfs_dir + "routes.txt", index=False)
    df_trips_filtered.to_csv(filtered_gtfs_dir + "trips.txt", index=False)
    df_stop_times_filtered.to_csv(filtered_gtfs_dir + "stop_times.txt", index=False)
    df_stops_filtered.to_csv(filtered_gtfs_dir + "stops.txt", index=False)
    df_route_stops_filtered.to_csv(filtered_gtfs_dir + "route_stops.txt", index=False)
    df_shapes_filtered.to_csv(filtered_gtfs_dir + "shapes.txt", index=False)

    #copy unmodified GTFS files
    for f in os.listdir(gtfs_dir):
        if f not in modified_files:
            copyfile(gtfs_dir + "/" + f, filtered_gtfs_dir + "/" + f)

if __name__ == "__main__":
    main()
