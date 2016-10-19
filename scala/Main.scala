/**
  * Created by vincentdupont on 18/10/16.
  */

import scala.io.Source
import java.text.SimpleDateFormat
import java.util.Date
import scala.annotation.tailrec
import scala.math.{pow, sqrt}
import scala.math.Ordering.Implicits._


object Main {

  type X = Double
  type Y = Double
  type Floor = Int
  type Hours = Int
  type Chunk = Int
  type Key = (Floor, Hours, Chunk)
  type Value = (Int, X, Y)
  type UMap = Map[Key, Value]


  def makeKey(d: Date, f: Floor): Key = {
    val minutes = d.getMinutes
    val hours = if (d.getDate == 19) 0 else 24
    (f, d.getHours + hours, minutes / 10)
  }

  def updateMap(m: UMap, key: Key, x: X, y: Y) : UMap = {
    def calcMean(item : (Int, X, Y), x: X, y: Y) : Value = item match {
      case (i, xx, yy) => (i+1, (xx*i+x) / (i+1), (yy*i + y) / (i+1))
    }
    if (m.contains(key))
      m + (key -> calcMean(m(key), x, y))
    else
      m + (key -> (1, x, y))
  }

  def distance(a : Value, b : Value) : Double = a match {
    case (_, x1, y1) => b match {
      case (_, x2, y2) => sqrt(pow(x1 - x2, 2) + pow(y1 - y2, 2))
    }
  }

  @tailrec
  def process(userAmap: UMap, userBmap: UMap, userAkeys : List[Key], userBkeys : List[Key]) : Key = userAkeys match {
    case aKey :: xs =>
      userBkeys match {
        case bKey :: ys if aKey < bKey =>
          process(userAmap, userBmap, xs, userBkeys)
        case bKey :: ys if aKey > bKey =>
          process(userAmap, userBmap, userAkeys, ys)
        case bKey :: ys if aKey == bKey =>
          if (distance(userAmap(aKey) , userBmap(bKey)) < 10) bKey else process(userAmap, userBmap, xs, ys)
        case _ => (0, 0, 0)
      }
    case _ => (0, 0, 0)
  }

  def output(userA : String, userB : String,  key: Key) : Unit = {
    if (key._1 == 0) println(s"$userA and $userB didn't actually met")
    else {
      val day = if (key._2 > 24) "20" else "19"
      val hour = if (key._2 > 24) key._2 - 24 else key._2
      val chunk = key._3
      println(s"$userA and $userB met at the ${key._1} floor on the ${day}th between $hour:${chunk*10} and $hour:${chunk*10+9}")
    }
  }


  def main(args: Array[String]): Unit = {
    val userA = args(0)
    val userB = args(1)
    val filename = args(2)

    // create chunk of time, let's say 6 by hours by people and by floor

    val dateParse = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX")

    def preProcess(acc: (UMap, UMap), line: String): (UMap, UMap) = {
      val splitted = line split ","
      if (splitted(0) == "timestamp") acc
      else {
        val user = splitted(4) // user
        if (user == userA || user == userB) {
          val timeStamp = dateParse.parse(splitted(0)) // timestamp
          val x = splitted(1).toDouble // x
          val y = splitted(2).toDouble // y
          val floor = splitted(3).toInt // floor
          val key = makeKey(timeStamp, floor)
          if (user == userA) {
            (updateMap(acc._1, key, x, y), acc._2)
          } else {
            (acc._1, updateMap(acc._2, key, x, y))
          }
        } else acc
      }
    }

    val (userAmap, userBmap) = Source.fromFile(filename).getLines().foldLeft((Map[Key, Value](), Map[Key, Value]()))(preProcess)
    output(userA, userB, process(userAmap, userBmap, userAmap.keys.toList.sorted, userBmap.keys.toList.sorted))
  }
}