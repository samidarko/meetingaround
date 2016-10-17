# Meeting around

For any given pair of uid s, determine when and where they could have met each other as
they moved through the building. Please state your assumptions about what would constitute
a “meeting.” Note that the coordinates can be assumed to be 1 unit = 1 meter.

## Prerequisites

 * Python 3 and Pandas installed (see [Notebook](https://github.com/samidarko/meetingaround/blob/master/exploration.ipynb) for more details)
 * [Scala](http://scala-lang.org/download/install.html)

## Content

In this repository you will find my work in two parts

 * A data exploration with Python and iPython Notebook
 * A Scala application that solves the problem in less than 1 second
 
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



## Todo
 * Add some command line arguments to the scala program
 * Add distance and timeframe for the meeting as arguments
 * Add some unit tests
 * Use parallelism to make it faster
 
## Remarks

It is an interesting problem and I know my solution can be improved. 
The algorithm is usually accurate but it sometimes returns:

(9.223372036854776E18,null,null)

For example with:

```bash
scala Main ea03ada6 eaa576ea reduced.csv
```

But I'm reaching my deadline.
