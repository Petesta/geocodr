package geocodr.github

import dispatch._
import Defaults._
import argonaut._
import Argonaut._

object user {
  case class User (
    login: String,
    id: Long,
    avatarUrl: String,
    gravatarId: String,
    url: String,
    name: String,
    company: String,
    blog: String,
    location: String,
    email: String,
    hireable: Boolean,
    bio: String,
    publicRepos: Long,
    publicGists: Long,
    followers: Long,
    following: Long,
    htmlUrl: String,
    createdAt: String,
    accountType: String
  ) {
    case UserInfo(name: String, avatarUrl: String, location: String, close: List[User])
  }

  implicit def UserCodecJson =
    casecodec19(User.apply, User.unapply)("login", "id", "avatar_url", "gravatar_id",
                                          "url", "name", "company", "blog", "location",
                                          "email", "hireable", "bio", "public_repos",
                                          "public_gists", "followers", "following",
                                          "html_url", "created_at", "type")
}
