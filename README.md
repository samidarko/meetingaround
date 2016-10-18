# Meeting around

For any given pair of uid s, determine when and where they could have met each other as
they moved through the building. Please state your assumptions about what would constitute
a “meeting.” Note that the coordinates can be assumed to be 1 unit = 1 meter.

## Prerequisites

 * Python 3 and Pandas installed (see [Notebook](https://github.com/samidarko/meetingaround/blob/master/exploration.ipynb) for more details)
 * [Scala](http://scala-lang.org/download/install.html)
 
## What is a meeting
I will assume that a meeting could happened if the two people where:
 * Are on the same floor
 * In a distance less than 10 units
 * In a time frame of more or less 5 minutes

## Content

In this repository you will find my work in two parts

 * A data exploration with Python and iPython [Notebook](https://github.com/samidarko/meetingaround/blob/master/exploration.ipynb)
 * A [Scala application](https://github.com/samidarko/meetingaround/blob/master/scala/Main.scala) that solves the problem in less than 1 second
 
## The files
 * [Notebook](https://github.com/samidarko/meetingaround/blob/master/exploration.ipynb)
 * [Python script v1](https://github.com/samidarko/meetingaround/blob/master/closest_pair.py)
 * [Python script v2](https://github.com/samidarko/meetingaround/blob/master/closest_pair_2.py)
 * [Python script v3](https://github.com/samidarko/meetingaround/blob/master/closest_pair_3.py)
 * [A bash script to compile the Scala file](https://github.com/samidarko/meetingaround/blob/master/scala/compile.sh)
 * [Scala program](https://github.com/samidarko/meetingaround/blob/master/scala/Main.scala)

## How to use

### The Python scripts
(In bash terminal)
```bash
python closest_pair.py
# or
python closest_pair_2.py
# or
python closest_pair_3.py
```

### The Scala program
(In bash terminal)

#### Compile
```bash
cd scala
./compile.sh
```
#### Compile
In the same directory than compilation

```bash
scala Main 600dfbe2 5e7b40e1 reduced.csv
```

Please note:
 * ***Scala*** as the interpreter
 * ***Main*** as the compiled program
 * ***600dfbe2*** as the first user id
 * ***5e7b40e1*** as the second user id
 * ***reduced.csv*** as the extracted dataset
 
#### Results interpretation

A result is tuple of 3 values is returned containing:
 * position 0: distance between user 1 and user 2
 * position 1: user 1 "Record"
 * position 2: user 2 "Record"
 
A "Record" contains:
 * position 0: x value
 * position 1: y value
 * position 2: floor value
 * position 3: timestamp (converted to unix timestamp)
 * position 4: user id

When the two users didn't met in the within time frame of 5 minutes (300 seconds) 
the result will look like this:

```bash
(9.223372036854776E18,null,null)
These two people didn't met
```

If they met during the time frame but within a distance exceeding the radius of 10 units:

```bash
(33,Record(1, 7, 1, 1405811095482, 1c8e8e4c),Record(75, 64, 1, 1405811119459, eaa576ea)
These two people didn't met
```

If they met during the time frame but within a distance not exceeding the radius of 10 units:

```bash
(1,Record(1, 7, 1, 1405811095482, 1c8e8e4c),Record(75, 64, 1, 1405811119459, eaa576ea)
These two people met
```

## Todo
 * Add some command line arguments to the scala program
 * Add distance and time frame for the meeting as arguments
 * Add some unit tests
 * Use parallelism to make it faster
 
## Remarks

It is an interesting problem and I know my solution can be improved. 

I used an algorithm of O(n (Logn)^2) time complexity but there is also
another one faster of O(n log n).

There was another approach I wanted to test but didn't because of time reason which is 
named [Locality-sensitive hashing](https://en.wikipedia.org/wiki/Locality-sensitive_hashing)