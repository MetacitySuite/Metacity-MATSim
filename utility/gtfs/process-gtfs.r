#library(gtfstools)
library(gtfs2gps)


#' Filters GTFS routes by ID, outputs a filtered GTFS with specified routes only.
 

data_path <- "/home/metakocour/Scripts"
gtfs_path <- file.path(data_path, "PID_GTFS-1-nov-2021.zip")
gtfs <- read_gtfs(gtfs_path)

route_id_list_file <- file("route_ids.txt",open="r")
route_ids <- readLines(route_id_list_file)

smaller_gtfs <- filter_by_route_id(gtfs, route_ids)

write_gtfs(smaller_gtfs, "PID_GTFS_filtered.zip")
