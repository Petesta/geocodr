package geocodr.github

import com.github.nscala_time.time.Imports._

import dispatch._
import Defaults._

package object search {
  
  val root = url("https://api.github.com")
  
  val b64 = new sun.misc.BASE64Encoder().encode {
    s"${System.getenv("GITUSER")}:${System.getenv("GITPASS")}".getBytes
  }

  val globalHeaders = 
    List("Accept" -> "application/vnd.github.preview", "Authorization" -> ("Basic " + b64))

  sealed trait SearchQuery { self =>
    def query: String
  }

  implicit def queryToCompoundQuery[A <: SearchQuery](x: A): CompoundQuery[A] = CompoundQuery(x)

  /* A Sequence of Queries */
  case class CompoundQuery[+A <: SearchQuery](queries: A*) {
    def query = queries.map(q => q.query).mkString(" ")

    def +[B >: A <: SearchQuery](o: B): CompoundQuery[B] = CompoundQuery[B]((queries :+ o): _*)
  }

  trait LocationSearchQuery extends SearchQuery
  trait RepositoriesSearchQuery extends SearchQuery
  trait UserSearchQuery extends SearchQuery
  
  /* Support text as a SearchQuery */
  case class QueryText(name: String) extends SearchQuery with UserSearchQuery with RepositoriesSearchQuery {
    def query = name
  }

  implicit def stringToText(s: String) = QueryText(s)
  
  /* Common Queries Here */
  case class SearchIn(fields: List[String]) extends SearchQuery with UserSearchQuery with RepositoriesSearchQuery {
    def query = s"in:${fields.mkString(",")}"
  }

  case class Language(language: String) extends SearchQuery with UserSearchQuery with RepositoriesSearchQuery {
    def query = s"language:${language}"
  }

  case class Location(location: String) extends SearchQuery with UserSearchQuery with RepositoriesSearchQuery {
    def query = s"location:${
      location.map {
        case ' ' => '-'
        case c   => c.toLower
        }.replace(",", "").mkString("")
      }"
  }

  trait Constrainable[A]

  implicit object IntConstr extends Constrainable[Int]

  sealed abstract class Constraint[A: Constrainable] {
    def filter: String
  }

  case class Eq[A: Constrainable](value: A) extends Constraint[A] {
    def filter = value.toString
  }

  case class LessThan[A: Constrainable](value: A) extends Constraint[A] {
    def filter = s"<$value"
  }

  case class LessThanEq[A: Constrainable](value: A) extends Constraint[A] {
    def filter = s"<=$value"
  }

  case class GreaterThan[A: Constrainable](value: A) extends Constraint[A] {
    def filter = s">$value"
  }

  case class GreaterThanEq[A: Constrainable](value: A) extends Constraint[A] {
    def filter = s">=$value"
  }

  case class Range[A: Constrainable](start: A, end: A) extends Constraint[A] {
    def filter = s"$start..$end"
  }

  abstract class ComparableOps[A: Constrainable, B] { self =>
    val constructor: Constraint[A] => B
    def ==(x: A) = constructor(Eq(x))
    def <(x: A)  = constructor(LessThan(x))
    def <=(x: A) = constructor(LessThanEq(x))
    def >(x: A)  = constructor(GreaterThan(x))
    def >=(x: A) = constructor(GreaterThanEq(x))
    def range(pair: (A, A)) = constructor(Range(pair._1, pair._2))
  }
  
  /* Order */
  sealed trait Order {
    def order: String
  }

  case object Asc extends Order {
    def order = "asc"
  }

  case object Desc extends Order {
    def order = "desc"
  }
}
