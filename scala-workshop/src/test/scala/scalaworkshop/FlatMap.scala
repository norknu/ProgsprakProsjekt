package scalaworkshop

import org.scalatest.{FlatSpec, Matchers}

class FlatMap extends FlatSpec with Matchers {

  val quote =
    """Try not.
      |Do... or do not.
      |There is no try.""".stripMargin.lines.toList

  def words(line: String): List[String] = line.split(" ").toList


  // Exercise: Implement the methods using map, filter & flatMap

  def words(lines: List[String]): List[String] = ???

  def wordLengths(lines: List[String]): List[Int] = ???

  // Exercise: Implement map and filter using flatMap

  def map[T](list: List[String], f: String => T): List[T] = ???

  def filter(list: List[Int], f: Int => Boolean): List[Int] = ???


  // Tests

  "words" should "return an empty list of words for an empty list of lines" in {
    words(Nil) should be (Nil)
  }

  it should "return an empty list of words for an empty line" in {
    words(List("")) should be (Nil)
  }

  it should "return a list of all the words in the lines" in {
    words(quote) should be (List("Try", "not.", "Do...", "or", "do", "not.", "There", "is", "no", "try."))
  }


  "wordLengths" should "return an empty list for empty list of lines" in {
    wordLengths(Nil) should be (Nil)
  }

  it should "return an empty list of lengths for an empty line" in {
    wordLengths(List("")) should be (Nil)
  }

  it should "return a list of the length of all the words" in {
    wordLengths(quote) should be (List(3, 4, 5, 2, 2, 4, 5, 2, 2, 4))
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
