package com.example.subduaintermediate

import com.example.subduaintermediate.data.response.ListStoryItem

object DataDummy {
    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0 until 100) {
            val story = ListStoryItem(
                i.toString(),
                "createdAt$i",
                "name$i",
                "description$i",
                i * 0.1,
                "id$i",
                i * 0.1
            )
            items.add(story)
        }
        return items
    }
}
