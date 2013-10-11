package github

import dispatch._
import Defaults._

object Repository {
  val root = url("https://api.github.com")
  
  sealed trait SearchQuery {
    def query: String = ???
    def +(o: SearchQuery) = CompoundQuery(this, o)
  }

  case class SearchIn(fields: List[String]) extends SearchQuery

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


  case class Size(sc: SizeConstraint) extends SearchQuery
  case class Forks(sc: SizeConstraint) extends SearchQuery
  //case class Fork()
  case class Created() extends SearchQuery
  case class Pushed() extends SearchQuery
  case class User() extends SearchQuery

  case class Language(name: String) extends SearchQuery {
    override def query = s"language:${name.map(_.toLower).mkString("")}"
  }

  object Stars {
    def ==(x: Int) = Stars(Eq(x))
    def <(x: Int) = Stars(LessThan(x))
    def <=(x: Int) = Stars(LessThanEq(x))
    def >(x: Int)  = Stars(GreaterThan(x))
    def >=(x: Int) = Stars(GreaterThanEq(x))
    def range(pair: (Int, Int)) = Stars(Range(pair._1, pair._2))
  }

  case class Stars(sc: SizeConstraint) extends SearchQuery {
    override def query = s"stars:${sc.filter}"
  }

  case class CompoundQuery(queries: SearchQuery*) extends SearchQuery {
    override def query = queries.map(_.query).mkString(" ")
    override def +(o: SearchQuery) = CompoundQuery(queries :+ o: _*)
  }

  /* SearchSort */
  sealed trait SearchSort {
    def sort: String
  }

  case object SortByStars extends SearchSort {
    def sort = "stars"
  }

  case object SortByForks extends SearchSort {
    def sort = "forks"
  }

  case object SortByUpdated extends SearchSort {
    def sort = "updated"
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

  def search(sq: SearchQuery, s: SearchSort, o: Order) = {
    val url = root / "search" / "repositories"
    val params = Map("q" -> sq.query, "sort" -> s.sort, "order" -> o.order) 
    Http(url <:< Seq("Accept" -> "application/vnd.github.preview") <<? params OK as.String)
  }
}
