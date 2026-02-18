package com.bettafish.flarent.models

import com.github.jasminb.jsonapi.annotations.Id
import com.github.jasminb.jsonapi.annotations.Relationship
import com.github.jasminb.jsonapi.annotations.Type

@Type("post_reactions")
class PostReactions {
    @Id
    lateinit var id: String

    var userId: String? = null
    var postId: String? = null
    var reactionId: String? = null

    @Relationship("user")
    var user: User? = null

    @Relationship("reaction")
    var reaction: Reaction? = null
}