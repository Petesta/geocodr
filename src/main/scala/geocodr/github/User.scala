package geocodr.github

import dispatch._
import Defaults._
import argonaut._
import Argonaut._

object user {
  case class User (
    login: String,
    id: Long,
    avatar_url: String,
    gravatar_id: String,
    url: String,
    name: String,
    company: String,
    blog: String,
    location: String,
    email: String,
    hireable: Boolean,
    bio: String,
    public_repos: Long,
    public_gists: Long,
    followers: Long,
    following: Long,
    html_url: String,
    created_at: String,
    account_type: String
  )

  implicit def UserCodecJson =
    casecodec19(User.apply, User.unapply)("login", "id", "avatar_url", "gravatar_id",
                                          "url", "name", "company", "blog", "location",
                                          "email", "hireable", "bio", "public_repos",
                                          "public_gists", "followers", "following",
                                          "html_url", "created_at", "account_type")
}
