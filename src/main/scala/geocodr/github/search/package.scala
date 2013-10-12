package geocodr.github

import com.github.nscala_time.time.Imports._

import dispatch._
import Defaults._

package object search {
  
  val root = url("https://api.github.com")
  
  sealed trait SearchQuery { self =>
    def query: String
  }

  implicit def queryToCompoundQuery[A <: SearchQuery](x: A): CompoundQuery[A] = CompoundQuery(x)

  /* A Sequence of Queries */
  case class CompoundQuery[+A <: SearchQuery](queries: A*) {
    def query = queries.map(_.query).mkString(" ")
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
  case class SearchIn(fields: List[String]) extends UserSearchQuery with RepositoriesSearchQuery {
    def query = s"in:${fields.mkString(",")}"
  }

  case class Language(language: String) extends UserSearchQuery with RepositoriesSearchQuery {
    def query = s"language:${language.map(_.toLower).mkString("")}"
  }

  case class Location(location: String) extends UserSearchQuery with RepositoriesSearchQuery {
    def query = s"location:$location"
  }

  trait Constrainable[A]

  sealed class Constraint[A: Constrainable] {
    def filter: String
  }

  implicit object DateTimeConstr extends Constrainable[DateTime]
  implicit object IntConstr extends Constrainable[Int]


  case class Eq[A](value: A) extends Constrainable[A] {
    def filter = value.toString
  }

  case class LessThan[A](value: A) extends Constrainable[A] {
    def filter = s"<$value"
  }

  case class LessThanEq[A](value: A) extends Constrainable[A] {
    def filter = s"<=$value"
  }

  case class GreaterThan[A](value: A) extends Constrainable[A] {
    def filter = s">$value"
  }

  case class GreaterThanEq[A](value: A) extends Constrainable[A] {
    def filter = s">=$value"
  }

  case class Range[A](start: A, end: A) extends Constrainable[A] {
    def filter = s"$start..$end"
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
