package geocodr.github

import dispatch._
import Defaults._
import argonaut._
import Argonaut._
import geocodr.github.search.Users.{ UserSearch, UserSearchDecodeJSON, UserSearchDecodeJSON}

object repository {
  case class Repository(
    id: Long,
    owner: UserSearch,
    name: String,
    fullName: String,
    description: String,
    priv: Boolean, // private
    fork: Boolean,
    url: String,
    /* "html_url": "https://github.com/octocat/Hello-World",
    "clone_url": "https://github.com/octocat/Hello-World.git",
    "git_url": "git://github.com/octocat/Hello-World.git",
    "ssh_url": "git@github.com:octocat/Hello-World.git",
    "svn_url": "https://svn.github.com/octocat/Hello-World",
    "mirror_url": "git://git.example.com/octocat/Hello-World",*/
    homepage: String,
    language: Option[String],
    forks: Long,
    forksCount: Long,
    watchers: Long,
    size: Long,
    masterBranch: String,
    openIssues: Long,
    openIssuesCount: Long,
    pushedAt: String,
    createdAt: String,
    updatedAt: String
  )
}

