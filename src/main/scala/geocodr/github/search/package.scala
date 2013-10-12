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
  case class CompoundQuery[A <: SearchQuery](queries: A*) {
    def query = queries.map(_.query).mkString(" ")
    def +(o: A): CompoundQuery[A] = CompoundQuery(queries :+ o: _*)
  }

  trait UserSearchQuery extends SearchQuery
  trait RepositorySearchQuery extends SearchQuery
  
  /* Common Queries Here */
  case class SearchIn(text: String, fields: List[String]) extends RepositorySearchQuery {
    def query = s"$text in:${fields.mkString(",")}"
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
