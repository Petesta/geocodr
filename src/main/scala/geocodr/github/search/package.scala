package geocodr.github

import dispatch._
import Defaults._

package object search {
  
  val root = url("https://api.github.com")
  
  sealed trait SearchQuery[A <: SearchQuery[A]] {
    def query: String
    def +(o: SearchQuery[A]): CompoundQuery[SearchQuery[A]] = CompoundQuery(this, o)
  }

  /* A Sequence of Queries */
  case class CompoundQuery[A <: SearchQuery[A]](queries: A*) extends SearchQuery[A] {
    override def query = queries.map(_.query).mkString(" ")
    override def +(o: A) = CompoundQuery(queries :+ o: _*)
  }

  trait UserSearchQuery extends SearchQuery[UserSearchQuery]
  trait RepositorySearchQuery extends SearchQuery[RepositorySearchQuery]
  
  /* Common Queries Here */
  case class SearchIn(text: String, fields: List[String]) extends UserSearchQuery with RepositorySearchQuery {
    override def query = s"$text in:${fields.mkString(",")}"
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
