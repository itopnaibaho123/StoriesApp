package com.dicoding.picodiploma.loginwithanimation.utils

import com.dicoding.picodiploma.loginwithanimation.api.ListStoryItem

object DataDummy {

    fun generateDummyResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                id = "$i",
                name = "Name $i",
                description = "Description $i",
                photoUrl = "https://media.valorant-api.com/agents/dade69b4-4f5a-8528-247b-219e5a1facd6/displayicon.png",
                createdAt = "2022-02-22T22:22:22Z",
                lon= -6.1,
                lat=  106.8

            )
            items.add(story)
        }
        return items
    }
    fun generateNullDummyData() : List<ListStoryItem> {
        return ArrayList<ListStoryItem>()
    }
}