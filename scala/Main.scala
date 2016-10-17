import scala.io.Source
import java.text.SimpleDateFormat
import scala.math.{pow, sqrt, abs}

object Main {

  type User = String
  type Floor = Int
  type TimeStamp = Long
  type Distance = Double
  type Result = (Distance, Record, Record)

  class Record(val x: Double, val y: Double, val floor: Floor, val timeStamp: TimeStamp, val user: User) {
    override def toString: String =
      s"Record($x, $y, $floor, $timeStamp, $user)"

    def distance(other: Record) : Distance = {
      sqrt(pow(other.x - x, 2) + pow(other.y - y, 2))
    }
  }


  def bruteForce(userA : List[Record], userB : List[Record], timeFrame: Long, withinDistance: Distance) : Result = {
    var minDistance : Distance = Long.MaxValue
    var distance : Distance = 0
    var timeDelta : Long = 0
    var result : Result = (minDistance, null, null)
    for (r1 <- userA) {
      for (r2 <- userB) {
        timeDelta = abs(r1.timeStamp - r2.timeStamp)
        distance = r1.distance(r2)
        if (timeDelta < timeFrame && distance < minDistance) {
          minDistance = distance
          result = (minDistance, r1, r2)
          if (result._1 < withinDistance) {
            return result
          }
        }
      }
    }
    result
  }

  def stripClosest(userA : List[Record], userB : List[Record], d : Result, timeFrame: Long, withinDistance: Distance) : Result = {
    if (d._1 < withinDistance) return d
    var minDistance : Distance = Long.MaxValue
    var result : Result = (minDistance, null, null)
    var distance : Distance = 0
    var timeDelta : Long = 0
    val userAYsorted = userA.sortBy(_.y)
    val userBYsorted = userB.sortBy(_.y)
    for (r1 <- userAYsorted) {
      for (r2 <- userBYsorted) {
        if (abs(r1.y - r2.y) < minDistance) return result
        timeDelta = abs(r1.timeStamp - r2.timeStamp)
        distance = r1.distance(r2)
        if (timeDelta < timeFrame && distance < minDistance) {
          minDistance = distance
          result = (minDistance, r1, r2)
          if (result._1 < withinDistance) {
            return result
          }
        }
      }
    }
    result
  }

  def findClosest(userA : List[Record], userB : List[Record], timeFrame: Long, withinDistance: Distance) : Result = {

    val sizeA = userA.length
    val sizeB = userB.length
    if (sizeA <= 3 || sizeB <= 3) {
      bruteForce(userA, userB, timeFrame, withinDistance)
    } else {

      // find middle records
      val middleA : Int = sizeA / 2
      val middleB : Int = sizeB / 2
      val middleARecord : Record = userA(middleA)
      val middleBRecord : Record = userB(middleB)
      //    return (0, new Record(0, 0, 0, 0, ""), new Record(0, 0, 0, 0, ""))

      val dl : Result = findClosest(userA.slice(0, middleA), userB.slice(0, middleB), timeFrame, withinDistance)
      val dr : Result = findClosest(userA.slice(middleA, sizeA), userB.slice(middleB, sizeB), timeFrame, withinDistance)

      val d : Result = if (dl._1 < dr._1) dl else dr

      // build strip array
      val stripA : List[Record] = userA.foldLeft(List[Record]()) ((acc, record) => {
        if (abs(record.x - middleARecord.x) < d._1 ) record :: acc else acc
      })

      val stripB : List[Record] = userB.foldLeft(List[Record]()) ((acc, record) => {
        if (abs(record.x - middleBRecord.x) < d._1 ) record :: acc else acc
      })

      val stripped : Result = stripClosest(stripA, stripB, d, timeFrame, withinDistance)

      if (d._1 < stripped._1) d else stripped
    }

  }

  def main(args: Array[String]): Unit = {

    // TODO arguments --user-a --user-b --file | all mandatory and user a != user b

    if (args.length < 3) {
      println("program should have three positional arguments:")
      println("scala Main user_1 user_2 file.csv")
      return
    }
    val userA = args(0)
    val userB = args(1)

    if (userA == userB) {
      println("the two users should be different")
      println("scala Main user_1 user_2 file.csv")
    }

    val filename = args(2)
    val dateParse = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX")

    try {

      val init : Map[User, Map[Floor, List[Record]]] = Map(
        userA -> Map(1 -> List[Record](), 2 -> List[Record](), 3 -> List[Record]()),
        userB -> Map(1 -> List[Record](), 2 -> List[Record](), 3 -> List[Record]()))

      // Reading the dataset and pre processing it
      val data = Source.fromFile(filename).getLines().foldLeft (init)  {
        (acc, line) => {
          val splitted = line split ","
          if (splitted(0) == "timestamp") acc
          else {
            val user = splitted(4) // user
            if (user == userA || user == userB) {
              val timeStamp = dateParse.parse(splitted(0)).getTime   // timestamp
              val x = splitted(1).toDouble // x
              val y = splitted(2).toDouble // y
              val floor = splitted(3).toInt // floor
              val record = new Record(x, y, floor, timeStamp, user)
              acc.updated(user, acc(user) updated(floor, record :: acc(user)(floor)))
            } else {
              acc
            }
          }
        }
      }

      val toSort : List[(User, Floor)]= for (u <- List(userA, userB); f <- 1 to 3) yield (u, f)

      val sortedData = toSort.foldLeft(data) ((acc, value) => {
        acc.updated(value._1, acc(value._1) updated(value._2, acc(value._1)(value._2).sortBy(_.x) ))
      })

//      val date = new java.util.Date()

      val result = (1 to 3).foldLeft( (Double.MaxValue, new Record(0, 0, 0, 0, ""), new Record(0, 0, 0, 0, "")) ) ((acc, floor) => {
        if (acc._1 < 10) acc else findClosest(sortedData(userA)(1), sortedData(userB)(1), 300*1000, 10)
      })

      println(result)
      println(if (result._1 < 10) "These two people met" else "These two people didn't met")
//      println("process duration :", new java.util.Date().getTime - date.getTime)

    } catch {
      case ex: Exception => println("An exception has occurred when opening the file.", ex)
    }
  }
}