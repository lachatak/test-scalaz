package scalaz.stuff

import scalaz._
import Scalaz._

import Tags._


/**
 * Also see folding.sc for more stuff on tagged types.
 */
object taggedtypes {
  sealed trait KiloGram
  type KG = Double @@ KiloGram
  def KiloGram(a: Double): KG = Tag[Double, KiloGram](a)
                                                  //> KiloGram: (a: Double)scalaz.stuff.taggedtypes.KG
  
  val mass = KiloGram(20.0)                       //> mass  : scalaz.stuff.taggedtypes.KG = 20.0
  
  Tag.unwrap(mass) * 2                            //> res0: Double = 40.0
  2 * Tag.unwrap(mass)                            //> res1: Double = 40.0
  
  
  sealed trait JoulePerKiloGram
  def JoulePerKiloGram[A](a: A): A @@ JoulePerKiloGram = Tag[A, JoulePerKiloGram](a)
                                                  //> JoulePerKiloGram: [A](a: A)scalaz.@@[A,scalaz.stuff.taggedtypes.JoulePerKilo
                                                  //| Gram]
  
  def energyR(m: Double @@ KiloGram): Double @@ JoulePerKiloGram =
    JoulePerKiloGram(299792458.0 * 299792458.0 * Tag.unwrap(m))
                                                  //> energyR: (m: scalaz.@@[Double,scalaz.stuff.taggedtypes.KiloGram])scalaz.@@[D
                                                  //| ouble,scalaz.stuff.taggedtypes.JoulePerKiloGram]
  
  energyR(mass)                                   //> res2: scalaz.@@[Double,scalaz.stuff.taggedtypes.JoulePerKiloGram] = 1.797510
                                                  //| 35747363533E18
  //energyR(10.0) -> produces errror "type mismatch; found : Double(10.0) required:
  //                                  scalaz.@@[Double,scalaz.stuff.taggedtypes.KiloGram]"
  
  
  
  Tags.First('a'.some) |+| Tags.First('b'.some)   //> res3: scalaz.@@[Option[Char],scalaz.Tags.First] = Some(a)
  Tags.First(none: Option[Char]) |+| Tags.First('b'.some)
                                                  //> res4: scalaz.@@[Option[Char],scalaz.Tags.First] = Some(b)
  Tags.First('a'.some) |+| Tags.First(none: Option[Char])
                                                  //> res5: scalaz.@@[Option[Char],scalaz.Tags.First] = Some(a)
  Tags.Last('a'.some) |+| Tags.Last('b'.some)     //> res6: scalaz.@@[Option[Char],scalaz.Tags.Last] = Some(b)
  
  
  Multiplication(2)                               //> res7: scalaz.@@[Int,scalaz.Tags.Multiplication] = 2
  Multiplication(2) |+| Multiplication(10)        //> res8: scalaz.@@[Int,scalaz.Tags.Multiplication] = 20
  
  Tags.Multiplication(10) |+| Monoid[Int @@ Tags.Multiplication].zero
                                                  //> res9: scalaz.@@[Int,scalaz.Tags.Multiplication] = 10
  Tags.Disjunction(true) |+| Tags.Disjunction(false)
                                                  //> res10: scalaz.@@[Boolean,scalaz.Tags.Disjunction] = true
  
  Tags.Multiplication(BigDecimal(-1)) |+| Tags.Multiplication(5)
                                                  //> res11: scalaz.@@[scala.math.BigDecimal,scalaz.Tags.Multiplication] = -5
  
  
  // Tags are not only useful for selecting typeclass instances
  // lets create our own tag, named Sorted which indicates a List that has been sorted
  sealed trait Sorted
  val Sorted = Tag.of[Sorted]                     //> Sorted  : scalaz.Tag.TagOf[scalaz.stuff.taggedtypes.Sorted] = scalaz.Tag$Ta
                                                  //| gOf@65694399

  // a sort function which will sort then add the Tag
  def sortList[A: scala.math.Ordering](as: List[A]): List[A] @@ Sorted =
    Sorted(as.sorted)                             //> sortList: [A](as: List[A])(implicit evidence$1: scala.math.Ordering[A])scal
                                                  //| az.@@[List[A],scalaz.stuff.taggedtypes.Sorted]

  // now we can define a function which takes lists which are tagged as being sorted
  def minOption[A](a: List[A] @@ Sorted): Option[A] = Sorted.unwrap(a).headOption
                                                  //> minOption: [A](a: scalaz.@@[List[A],scalaz.stuff.taggedtypes.Sorted])Option
                                                  //| [A]

  // why is this implicit conversion needed - works fine without it???
  implicit val ord = implicitly[Order[Option[Int]]].toScalaOrdering
                                                  //> ord  : scala.math.Ordering[Option[Int]] = scalaz.Order$$anon$1@d6e32d7
  minOption(sortList(List(3,2,1,5,3)))            //> res12: Option[Int] = Some(1)
  assert(minOption(sortList(List(3,2,1,5,3))) === Some(1))
}