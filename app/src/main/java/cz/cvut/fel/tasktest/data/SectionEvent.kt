package cz.cvut.fel.tasktest.data

sealed interface SectionEvent {
    object SaveSection: SectionEvent

    data class SetSectionName(val name: String) : SectionEvent
    data class SetSectionDeskId(val deskId: Long) : SectionEvent
    data class SetTaskToSection(val task: Task) : SectionEvent
    data class DeleteTaskFromSection(val task: Task) : SectionEvent
    data class DeleteSection(val section: Section) : SectionEvent
}