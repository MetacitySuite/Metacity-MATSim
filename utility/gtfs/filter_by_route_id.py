import pandas as pd
import numpy as np
import re
import os

gtfs_dir = "data/PID_GTFS_1_11_2021/"
filtered_gtfs_dir = "output/PID_GTFS_1_11_2021_filtered/"

routes = gtfs_dir + "routes.txt"
trips = gtfs_dir + "trips.txt"
stop_times = gtfs_dir + "stop_times.txt"
stops = gtfs_dir + "stops.txt"
route_stops = gtfs_dir + "route_stops.txt"
shapes = gtfs_dir + "shapes.txt"

class GTFS():
    def __init__(self, template_config_file):
        self.df_routes = pd.read_csv(routes)
        self.df_trips = pd.read_csv(trips)
        self.df_route_stops = pd.read_csv(route_stops)
        self.df_stops = pd.read_csv(stops)
        self.df_stop_times = pd.read_csv(stop_times)
        self.df_shapes = pd.read_csv(shapes)
    def save(self):
        if not os.path.exists(filtered_gtfs_dir):
            os.makedirs(filtered_gtfs_dir)
        self.df_routes.to_csv(filtered_gtfs_dir + "routes.txt", index=False)




def main():
    route_id = []

    for x in range(300):
        route = "L" + str(x)
        route_id.append(route)

    subway = ["L991", "L992", "L993"]

    for x in np.arange(901,917):
        route = "L" + str(x)
        route_id.append(route)

    route_id = route_id + subway


    df_routes = pd.read_csv(routes, dtype=str)
    df_routes.update('\'' + df_routes[['route_long_name', 'route_url']].astype(str) + '\'')
    df_routes_filtered = df_routes.loc[df_routes['route_id'].isin(route_id)]

    df_trips = pd.read_csv(trips, dtype=str)
    print(len(df_trips['trip_id'].unique()))
    df_trips.update('\'' + df_trips['trip_headsign'].astype(str) + '\'')
    df_trips_filtered = df_trips.loc[df_trips['route_id'].isin(route_id)]

    trip_id = df_trips_filtered['trip_id'].unique()
    shape_id = df_trips_filtered['shape_id'].unique()
    print(len(trip_id))

    df_stop_times = pd.read_csv(stop_times, dtype=str)
    df_stop_times_filtered = df_stop_times.loc[df_stop_times['trip_id'].isin(trip_id)]

    #stop_id = df_stop_times_filtered['stop_id'].unique()

    df_stop_times_filtered.to_csv(filtered_gtfs_dir + "stop_times.txt", index=False)
    #print(df_stop_times.dtypes)



    #df_routes_x.to_csv(filtered_gtfs_dir + "routes.txt", index=False)


if __name__ == "__main__":
    main()