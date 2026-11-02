package com.feonzmeko.daytag.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DayEventDaoTest {
    private lateinit var database: DayTagDatabase
    private lateinit var dao: DayEventDao

    @Before
    fun createDatabase() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, DayTagDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.dayEventDao()
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun savesUpdatesAndDeletesEvent() = runBlocking {
        val id = dao.upsert(
            DayEvent(title = "旅行", targetEpochDay = 123L, accentKey = AccentKeys.OCHRE),
        )
        val inserted = dao.findById(id)!!
        assertEquals("旅行", inserted.title)

        dao.upsert(inserted.copy(title = "看海"))
        val updated = dao.findById(id)!!
        assertEquals("看海", updated.title)
        assertEquals(1, dao.observeAll().first().size)

        dao.delete(updated)
        assertNull(dao.findById(id))
    }
}
