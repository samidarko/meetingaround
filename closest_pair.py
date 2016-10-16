# import time
# from datetime import datetime, timedelta
# import dateutil.parser
import pandas as pd
from math import pow, sqrt
df = pd.read_csv('reduced.csv')


def get_coord(dp):
    return dp.x, dp.y


def calc_coord_dist(p1, p2):
    x1, y1 = get_coord(p1)
    x2, y2 = get_coord(p2)
    return sqrt(pow(x2 - x1, 2) + pow(y2 - y1, 2))


def brute_force(dps):
    min_distance = float('inf')
    result = (float('inf'), (), ())
    for index_a, dp_a in dps.iterrows():
        for index_b, dp_b in dps.iterrows():
            if index_a == index_b:
                continue
            distance = calc_coord_dist(dp_a, dp_b)
            if distance < min_distance:
                min_distance = distance
                result = (min_distance, get_coord(dp_a), get_coord(dp_b))

    return result


def strip_closest(strip, d):
    min_distance = d[0]
    result = (float('inf'), (), ())
    strip.sort(key=lambda item: item.y)
    for i, v in enumerate(strip):
        for j in range(i+1, len(strip)):
            if (strip[j].y - strip[i].y) < min_distance:
                break
            distance = calc_coord_dist(strip[i], strip[j])
            if distance < min_distance:
                min_distance = distance
                result = (min_distance, get_coord(strip[i]), get_coord(strip[j]))

    return result


def find_closest(dps):
    size = len(dps)
    if size <= 3:
        return brute_force(dps)

    # find middle point
    middle = size // 2
    middle_dp = dps.iloc[middle]

    dl = find_closest(dps[0:middle])
    dr = find_closest(dps[middle:])

    # find smaller distance
    # d = min(dl, dr)
    d = dl if dl[0] < dr[0] else dr

    # build strip array
    strip = []
    for _, dp in dps.iterrows():
        if abs(dp.x - middle_dp.x) < d[0]:
            strip.append(dp)

    stripped = strip_closest(strip, d)
    return d if d[0] < stripped[0] else stripped
    # return min(d, stripped)


def solution_dc(sub):
    x_sorted = sub.copy()
    x_sorted = x_sorted.sort_values(by='x')
    return find_closest(x_sorted)

a = '600dfbe2'
b = '5e7b40e1'
# subset = df[(((df.uid == a) | (df.uid == b)) & df.floor == 1)].copy()
subset = df[(df.uid == b) & (df.floor == 1)].copy()

print(solution_dc(subset))
