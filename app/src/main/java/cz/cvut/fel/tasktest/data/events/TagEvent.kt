package cz.cvut.fel.tasktest.data.events

import cz.cvut.fel.tasktest.data.Tag

interface TagEvent {
    object SaveTag: TagEvent
    data class SetTagName(val name: String) : TagEvent
    data class SetTagBackground(val background: String) : TagEvent
    data class DeleteTag(val tag: Tag) : TagEvent
}