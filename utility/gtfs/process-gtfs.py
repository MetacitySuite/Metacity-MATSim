import pandas as pd
import re

'''
Edits the filtered GTFS data, correcting time formats.

'''

GTFS_stop_times = "./PID_GTFS-1-nov-2021/stop_times.txt"
filtered_GTFS_stop_times = "./PID_GTFS_filtered/stop_times.txt"

new_filtered_GTFS_stop_times = "/home/metakocour/Scripts/edited_gtfs_files/stop_times.txt"

#df_filtered_routes = pd.read_csv('./PID_GTFS_filtered/routes.txt')
#df_routes = pd.read_csv('./PID_GTFS-1-nov-2021/routes.txt')
#df_routes = df_routes.drop(['agency_id', 'route_type'], axis = 1)
#df_new_filtered_routes = df_filtered_routes.merge(df_routes, how='left', on='route_id')
#
#df_new_filtered_routes['route_long_name'] = df_new_filtered_routes['route_long_name'].apply(lambda x: "'" + str(x) + "'")
#df_new_filtered_routes['route_url'] = df_new_filtered_routes['route_url'].apply(lambda x: "'" + str(x) + "'")
#
#df_new_filtered_routes.to_csv("/home/metakocour/Scripts/edited_gtfs_files/routes.txt", index=False)


#process the stop_time files
df_filtered_stop_times = pd.read_csv(filtered_GTFS_stop_times)
df_stop_times = pd.read_csv(GTFS_stop_times)

df_stop_times = df_stop_times.drop(['stop_sequence', 'stop_headsign', 'pickup_type', 'drop_off_type', 'shape_dist_traveled', 'trip_operation_type', 'bikes_allowed' ], axis = 1)
df_new_filtered_stop_times = pd.merge(df_filtered_stop_times, df_stop_times,  how='left', left_on=['trip_id','stop_id'], right_on = ['trip_id','stop_id'])

df_new_filtered_stop_times['arrival_time_x'] = df_new_filtered_stop_times['arrival_time_x'].fillna(df_new_filtered_stop_times['arrival_time_y'])
df_new_filtered_stop_times['departure_time_x'] = df_new_filtered_stop_times['departure_time_x'].fillna(df_new_filtered_stop_times['departure_time_y'])

df_new_filtered_stop_times = df_new_filtered_stop_times.drop(['arrival_time_y', 'departure_time_y'], axis = 1)
df_new_filtered_stop_times.columns = ['trip_id', 'arrival_time', 'departure_time', 'stop_id', 'stop_sequence']

#edit time overflow
df_new_filtered_stop_times['arrival_time'] = df_new_filtered_stop_times['arrival_time'].apply(lambda x: re.sub("^00", "24", str(x)))
df_new_filtered_stop_times['departure_time'] = df_new_filtered_stop_times['departure_time'].apply(lambda x: re.sub("^00", "24", str(x)))
df_new_filtered_stop_times['arrival_time'] = df_new_filtered_stop_times['arrival_time'].apply(lambda x: re.sub("^0", "", str(x)))
df_new_filtered_stop_times['departure_time'] = df_new_filtered_stop_times['departure_time'].apply(lambda x: re.sub("^0", "", str(x)))

#save to new stop_times.txt file
df_new_filtered_stop_times.to_csv(new_filtered_GTFS_stop_times, index=False)

#df_stop_times = pd.read_csv('./PID_GTFS-1-nov-2021/stop_times.txt', encoding = 'ISO-8859-1')
#df_stop_times = df_stop_times.drop(['arrival_time','departure_time','stop_id','stop_sequence'], axis = 1)
#df_new_filtered_stop_times = df_filtered_stop_times.merge(df_stop_times, how='left', on='trip_id')
#df_new_filtered_stop_times.to_csv("/home/metakocour/Scripts/edited_gtfs_files/stop_times.txt", index=False)
