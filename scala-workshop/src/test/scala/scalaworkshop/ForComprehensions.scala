package scalaworkshop

import org.scalatest.{FlatSpec, Matchers}

class ForComprehensions extends FlatSpec with Matchers {

  val quote =
    """Try not.
      |Do... or do not.
      |There is no try.""".stripMargin.lines.toList

  def words(line: String): List[String] = line.split(" ").toList


  // Exercise: Implement the methods using for comprehensions

  def words(lines: List[String]): List[String] = ???

  def wordLengths(lines: List[String]): List[Int] = ???


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


  "wordLengths" should "return an empty list for empty lines" in {
    wordLengths(Nil) should be (Nil)
  }

  it should "return an empty list of lengths for an empty line" in {
    wordLengths(List("")) should be (Nil)
  }

  it should "return a list of the length of all the words" in {
    wordLengths(quote) should be (List(3, 4, 5, 2, 2, 4, 5, 2, 2, 4))
  }

}
