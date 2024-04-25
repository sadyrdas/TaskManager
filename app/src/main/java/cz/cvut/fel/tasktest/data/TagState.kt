package cz.cvut.fel.tasktest.data

data class TagState(
    val name: String = "",
    val background: String = "#FFFFFF",
    val tags: List<Tag> = emptyList()
)