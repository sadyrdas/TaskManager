package cz.cvut.fel.tasktest.data

sealed interface UserEvent {
    object SaveUser: UserEvent
    data class SetUsername(val userName: String): UserEvent
}