package geocodr.github.search

import dispatch._
import Defaults._

object User {
  case class Size(sc: SizeConstraint) extends UserSearchQuery {
    def query = ???
  }

  case class Forks(sc: SizeConstraint) extends UserSearchQuery {
    def query = ???
  }

  //case class Fork()
  case class Created() extends UserSearchQuery {
    def query = ???
  }

  case class Pushed() extends UserSearchQuery {
    def query = ???
  }

  case class User() extends UserSearchQuery {
    def query = ???
  }

  case class Language(name: String) extends UserSearchQuery {
    override def query = s"language:${name.map(_.toLower).mkString("")}"
  }

  object Followers {
    def ==(x: Int) = Followers(Eq(x))
    def <(x: Int) = Followers(LessThan(x))
    def <=(x: Int) = Followers(LessThanEq(x))
    def >(x: Int)  = Followers(GreaterThan(x))
    def >=(x: Int) = Followers(GreaterThanEq(x))
    def range(pair: (Int, Int)) = Followers(Range(pair._1, pair._2))
  }

  case class Followers(sc: SizeConstraint) extends UserSearchQuery {
    override def query = s"stars:${sc.filter}"
  }

  /* SearchSort */
  sealed trait SearchSort {
    def sort: String
  }

  case object SortByFollowers extends SearchSort {
    def sort = "followers"
  }

  case object SortByRepositories extends SearchSort {
    def sort = "repositories"
  }

  case object SortByJoined extends SearchSort {
    def sort = "joined"
  }  

  def search(sq: CompoundQuery[UserSearchQuery], s: SearchSort, o: Order) = {
    val url = root / "search" / "repositories"
    val params = Map("q" -> sq.query, "sort" -> s.sort, "order" -> o.order) 
    Http(url <:< Seq("Accept" -> "application/vnd.github.preview") <<? params OK as.String)
  }
}
