package geocodr.github

import dispatch._
import Defaults._
import argonaut._
import Argonaut._
import geocodr.github.search.Users.{ UserSearch, UserSearchEncodeJson, UserSearchDecodeJson}

object repository {
  //implicit def RepositoryInfoEncodeJson =
  //  jencode3L((ri: RepositoryInfo) => (ri.name, ri.language, ri.watchersCount))("name", "language", "watchersCount")

  //case class RepositoryInfo(name: String, language: String, watchersCount: Long)

  object Repository {
    def intersect(xs: List[Repository], ys: List[Repository]): Set[Repository] =
      xs.toSet.intersect(ys.toSet)
  }

  case class Repository (
    id: Long,
    name: String,
    fullName: String,
    description: Option[String],
    priv: Option[Boolean],
    htmlUrl: String,
    fork: Boolean,
    url: String,
    homepage: Option[String],
    watchersCount: Long,
    language: Option[String]
  )

  implicit def RepositoryCodecJson =
    casecodec11(Repository.apply, Repository.unapply)("id", "name", "full_name",
                                                      "description", "private", "html_url",
                                                      "fork", "url", "homepage", "watchers_count",
                                                      "language")

}
