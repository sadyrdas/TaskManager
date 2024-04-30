package cz.cvut.fel.tasktest.data.events

import cz.cvut.fel.tasktest.data.Section
import cz.cvut.fel.tasktest.data.Task

sealed interface SectionEvent {
    object SaveSection: SectionEvent

    data class SetSectionName(val name: String) : SectionEvent
    data class SetSectionDeskId(val deskId: Long) : SectionEvent
    data class SetTaskToSection(val task: Task) : SectionEvent
    data class DeleteTaskFromSection(val task: Task) : SectionEvent
    data class DeleteSection(val section: Section) : SectionEvent
}