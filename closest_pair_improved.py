import time
# from datetime import datetime, timedelta
import dateutil.parser
import pandas as pd
from math import pow, sqrt
df = pd.read_csv('reduced.csv')


def get_coord(dp):
    return dp.x, dp.y


def calc_coord_dist(p1, p2):
    x1, y1 = get_coord(p1)
    x2, y2 = get_coord(p2)
    return sqrt(pow(x2 - x1, 2) + pow(y2 - y1, 2))


def from_iso_to_unix_ts(iso):
    return int(time.mktime(dateutil.parser.parse(iso).timetuple()))


def brute_force_2(sub_a, sub_b, time_frame):
    min_distance = float('inf')
    result = (float('inf'), (), ())
    for index_a, dp_a in sub_a.iterrows():
        for index_b, dp_b in sub_b.iterrows():
            time_delta = abs(dp_a.timestamp - dp_b.timestamp)
            distance = calc_coord_dist(dp_a, dp_b)
            if time_delta < time_frame and distance < min_distance:
                min_distance = distance
                result = (min_distance, get_coord(dp_a), get_coord(dp_b))

    return result


def strip_closest_2(strip_a, strip_b, d, time_frame):
    min_distance = d[0]
    result = (float('inf'), (), ())
    strip_a.sort(key=lambda item: item.y)
    strip_b.sort(key=lambda item: item.y)
    for dp_a in strip_a:
        for dp_b in strip_b:
            if (dp_a.y - dp_b.y) < min_distance:
                break
            time_delta = abs(dp_a.timestamp - dp_b.timestamp)
            distance = calc_coord_dist(dp_a, dp_b)
            if time_delta < time_frame and distance < min_distance:
                min_distance = distance
                result = (min_distance, get_coord(dp_a), get_coord(dp_b))

    return result


def find_closest_2(sub_a, sub_b, time_frame):
    size_a = len(sub_a)
    size_b = len(sub_b)
    if size_a <= 3 or size_b <= 3:
        return brute_force_2(sub_a, sub_b, time_frame)

    # find middle point
    middle_a = size_a // 2
    middle_b = size_b // 2
    middle_dp_a = sub_a.iloc[middle_a]
    middle_dp_b = sub_b.iloc[middle_b]

    dl = find_closest_2(sub_a[0:middle_a], sub_b[0:middle_b], time_frame)
    dr = find_closest_2(sub_a[middle_a:], sub_b[middle_b:], time_frame)

    # find smaller distance
    d = dl if dl[0] < dr[0] else dr

    # build strip array
    strip_a = []
    for _, dp in sub_a.iterrows():
        if abs(dp.x - middle_dp_a.x) < d[0]:
            strip_a.append(dp)
    strip_b = []
    for _, dp in sub_b.iterrows():
        if abs(dp.x - middle_dp_b.x) < d[0]:
            strip_b.append(dp)

    stripped = strip_closest_2(strip_a, strip_b, d, time_frame)
    return d if d[0] < stripped[0] else stripped


def solution_dc_2(sub, user_a, user_b):
    x_sorted = sub.copy()
    x_sorted.timestamp = x_sorted.timestamp.apply(lambda x: from_iso_to_unix_ts(x))
    x_sorted = x_sorted.sort_values(by=['x', 'timestamp'])
    x_sorted_a = x_sorted[x_sorted.uid == user_a]
    x_sorted_b = x_sorted[x_sorted.uid == user_b]
    return find_closest_2(x_sorted_a, x_sorted_b, 300)

a = '600dfbe2'
b = '5e7b40e1'
subset = df[(((df.uid == a) | (df.uid == b)) & df.floor == 1)].copy()
# subset = df[(df.uid == b) & (df.floor == 1)].copy()

print(solution_dc_2(subset, a, b))
