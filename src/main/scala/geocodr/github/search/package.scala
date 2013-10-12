package geocodr.github

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
  trait RepositorySearchQuery extends SearchQuery
  trait UserSearchQuery extends SearchQuery
  
  /* Support text as a SearchQuery */
  case class QueryText(name: String) extends SearchQuery with UserSearchQuery with RepositorySearchQuery {
    def query = name
  }

  implicit def stringToText(s: String) = QueryText(s)
  
  /* Common Queries Here */
  case class SearchIn(fields: List[String]) extends UserSearchQuery with RepositorySearchQuery {
    def query = s"in:${fields.mkString(",")}"
  }

  case class Language(language: String) extends UserSearchQuery with RepositorySearchQuery {
    def query = s"language:${language.map(_.toLower).mkString("")}"
  }

  case class Location(location: String) extends UserSearchQuery with RepositorySearchQuery {
    def query = s"location:$location"
  }

  /* Sizing Constraints */
  sealed trait SizeConstraint {
    def filter: String
  }

  case class Eq(value: Int) extends SizeConstraint {
    def filter = value.toString
  }

  case class LessThan(bound: Int) extends SizeConstraint {
    def filter = s"<$bound"
  }

  case class LessThanEq(bound: Int) extends SizeConstraint {
    def filter = s"<=$bound"
  }

  case class GreaterThan(bound: Int) extends SizeConstraint {
    def filter = s">$bound"
  }

  case class GreaterThanEq(bound: Int) extends SizeConstraint {
    def filter = s">=$bound"
  }

  case class Range(start: Int, end: Int) extends SizeConstraint {
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
