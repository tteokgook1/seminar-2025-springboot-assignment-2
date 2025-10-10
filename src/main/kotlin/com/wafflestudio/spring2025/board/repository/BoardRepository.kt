package com.wafflestudio.spring2025.board.repository

import com.wafflestudio.spring2025.board.model.Board
import org.springframework.data.repository.ListCrudRepository

interface BoardRepository : ListCrudRepository<Board, Long> {
    fun existsByName(name: String): Boolean
}
