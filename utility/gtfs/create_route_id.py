import numpy as np

'''
Creates a list of routes.
https://pid.cz/jizdni-rady-podle-linek/
'''


route_id = []

for x in range(300):
    route = "L" + str(x)
    route_id.append(route)

subway = ["L991", "L992", "L993"]

for x in np.arange(901,917):
    route = "L" + str(x)
    route_id.append(route)

route_id = route_id + subway

with open('route_ids.txt', 'w') as f:
    for rid in route_id:
        f.write("%s\n" % rid)
