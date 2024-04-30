package cz.cvut.fel.tasktest.data.events

sealed interface UserEvent {
    object SaveUser: UserEvent
    data class SetUsername(val userName: String): UserEvent
}