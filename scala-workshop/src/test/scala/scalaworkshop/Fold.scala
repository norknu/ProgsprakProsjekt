package scalaworkshop

import org.scalatest.{FlatSpec, Matchers}

class Fold extends FlatSpec with Matchers {

  // Exercise: Implement the following methods using foldLeft / foldRight

  def product(list: List[Int]): Int = 
    list.foldLeft(1)(_*_) //ganer sammen det første elementet du ser/finner med det neste

  def mkString(list: List[String]): String = ???

  def map[T](list: List[String], f: String => T): List[T] = ???

  def filter(list: List[Int], f: Int => Boolean): List[Int] = ???


  // Tests

  "product" should "return 0 for empty lists" in {
    product(Nil) should be (0)
  }

  it should "multiply the given numbers" in {
    product(List(2, 3)) should be (6)
  }


  "mkString" should "return the empty string for empty list" in {
    mkString(Nil) should be ("")
  }

  it should "return just the string for a singleton list" in {
    mkString(List("foo")) should be ("foo")
  }

  it should "intersperse commas between elements" in {
    mkString(List("foo", "bar")) should be ("foo, bar")
  }


  "map" should "not do anything to empty list" in {
    map(Nil, _ => true) should be (Nil)
  }

  it should "transform elements" in {
    map(List("This", "is", "a", "test"), _.length) should be (List(4, 2, 1, 4))
  }


  "filter" should "not do anything to empty list" in {
    filter(Nil, _ => true) should be (Nil)
  }

  it should "return empty list for predicate that is always false" in {
    filter(List(1, 2, 3), _ => false) should be (Nil)
  }

  it should "return all elements for predicate that is always true" in {
    filter(List(1, 2, 3), _ => true) should be (List(1, 2, 3))
  }

  it should "filter out elements for which predicate is false" in {
    filter(List(1, 2, 3), _ > 1) should be (List(2, 3))
  }

}
