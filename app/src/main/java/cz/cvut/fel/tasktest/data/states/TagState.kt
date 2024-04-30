package cz.cvut.fel.tasktest.data.states

import cz.cvut.fel.tasktest.data.Tag

data class TagState(
    val name: String = "",
    val background: String = "#FFFFFF",
    val tags: List<Tag> = emptyList()
)