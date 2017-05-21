// This is the code of Kaggle competition: Expedia Hotel Recommendations
// It is a implementation of a Bayesian model.
// https://www.kaggle.com/c/expedia-hotel-recommendations
//
// The code runs in Scala REPL.

System.currentTimeMillis()
var c = 1
var line: String = null
do {
  line = br.readLine()
  allTrainingData.add(line)
} while (line != null)
// while ((line = br.readLine()) != null) {
//   allTrainingData.add(line)
//   println(c)
//   c = c+1;
// }
System.currentTimeMillis()


 // =======

import sys.process._
import java.io._
import java.util._

val br = new BufferedReader(new FileReader("train.csv"))
br.readLine()
val allTrainingData: List[String] = new LinkedList[String]();

println("<start>"+System.currentTimeMillis()+"</start>");
var c: Int = 1;
var line: String = null;
do {
  line = br.readLine();
  allTrainingData.add(line);
  println(c)
  c = c + 1
} while (line != null);
println("<end>"+System.currentTimeMillis()+"</end>");

// ====== random sample 1.5% ======


import sys.process._
import java.io._
import java.util._

val br = new BufferedReader(new FileReader("train.csv"))
val title = br.readLine()

val outTrain = new PrintWriter(new BufferedWriter(new FileWriter("train135.csv", true)));
val outTest = new PrintWriter(new BufferedWriter(new FileWriter("test015.csv", true)));

outTrain.println(title)
outTest.println(title)

var line: String = null

println("<start>"+System.currentTimeMillis()+"</start>");
var c: Int = 1;
do {
  line = br.readLine();
  var flip = Math.random()
  if (flip < 0.015) { // choosen
    if (flip < 0.0135) { // train
      outTrain.println(line)
    } else { // test
      outTest.println(line)
    }
    println(c)
    c = c + 1;
  }
} while (line != null);
println("<end>"+System.currentTimeMillis()+"</end>");

// ====  read destination.csv ====
import java.util._
import java.io._
import sys.process._

val destnations = new HashMap[Int, Array[Double]]()

var brDest = new BufferedReader(new FileReader("destinations.csv"))
var title2 = brDest.readLine()
// val title = brDest.readLine()
// var line = null
var line = brDest.readLine()
var c = 1
while (line != null) {
  val info = line.split(",")
  val dests = info.slice(1, info.length).map(_.toDouble)
  val lable = info(0).toInt
  destnations.put(lable, dests)
  println(c)
  c = c + 1
  line = brDest.readLine()
}
brDest.close


// System.gc()  use it to suggest gabarge collection
// scala -J-Xmx1g  make memory to 1G

// read training data
// === a full program ===
import java.util._
import java.io._
import sys.process._

case class FeatureVector(
  lable: Int,
  regularFeature: Array[Option[Double]],
  boolFeature: Array[Option[Boolean]]
)

trait Distribution[T <: AnyVal] {
  def likelihood(value: T): Double
}

case class NormalDistribution(mean: Double, std: Double) extends Distribution[Double] {
  override def likelihood(value: Double): Double = {
    value
  }
}

case class BernoulliDistribution(p: Double)  extends Distribution[Boolean] {
  override def likelihood(value: Boolean): Double = {
    if (value) p else 1-p
  }
}

val destnations = new HashMap[Int, Array[Double]]()

var brDest = new BufferedReader(new FileReader("destinations.csv"))
var brTrain = new BufferedReader(new FileReader("train135.csv"))

var destTitles = brDest.readLine().split(",")
var dataTitles = brTrain.readLine().split(",")

var line: String = null
var c = 1
while ((line = brDest.readLine) != null) {
  val info = line.split(",")
  val dests = info.slice(1, info.length).map(_.toDouble)
  val lable = info(0).toInt
  destnations.put(lable, dests)
  println(c)
  c = c + 1
}
brDest.close

var distributionMap = new HashMap[String, Distribution]() // feature title map to its distribution
var line: String = null
while ((line = brTrain.readLine) != null) {
  val info = line.split(",")
}

// === convert for chi square ===

import java.util._
import java.io._
import sys.process._

var br = new BufferedReader(new FileReader("train135.csv"))
var out = new PrintWriter(new BufferedWriter(new FileWriter("train_4_chi.txt", true)));

var title = br.readLine
val titleInfo = title.split(",")
var line: String = null
var c = 1
while ((line = br.readLine) != null) {
  val info = line.split(",")
  val lable = info(info.length-1)
  val feats = info
    .slice(0, info.length-1)
    .zip(titleInfo.slice(0, info.length-1))
    .map(tp => tp._2+"^"+tp._1+":1")
    .mkString(" ")
  val res = new StringBuilder()
  res.append(lable+" ");
  res.append(feats);
  out.println(res)
  println(c)
  c = c + 1
}