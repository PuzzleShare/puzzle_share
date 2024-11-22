package com.puzzle.backend.friend.domain

import com.puzzle.backend.oauth.domain.Users
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "friends")
class Friend(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    @ManyToOne
    @JoinColumn(name = "user_id")
    var user: Users,
    @ManyToOne
    @JoinColumn(name = "friend_id")
    var friend: Users,
)
