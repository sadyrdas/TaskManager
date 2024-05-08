package cz.cvut.fel.tasktest.data.events

sealed interface UserEvent {
    object SaveUser: UserEvent
    data class SetUsername(val userName: String): UserEvent
    data class SetBackground(val background: String): UserEvent
    data class ImageSelected(val imagePath: String) : UserEvent
}