package ie.setu.placemark.models

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class PlacedMarkListTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private lateinit var file: File

    private fun newStore(): PlacedMarkList {
        if (!::file.isInitialized) {
            file = File(tempFolder.root, "place_list.json")
        }
        return PlacedMarkList(file)
    }

    private fun valid() = PlacedMarkData(title = "Dublin", x = 53.3498, y = -6.2603)

    @Test
    fun startsEmptyWhenNoFile() {
        assertTrue(newStore().findAll().isEmpty())
        assertFalse(file.exists())
    }

    @Test
    fun updateCopiesImageUri() {
        val store = newStore()
        val mark = valid()
        store.create(mark)

        val changed = mark.copy(title = "Home", imageUri = "content://image/1")
        assertTrue(store.update(changed))

        val stored = store.findOne(mark.id)!!
        assertEquals("Home", stored.title)
        assertEquals("content://image/1", stored.imageUri)
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsInvalidCreate() {
        newStore().create(valid().copy(x = 91.0))
    }

    @Test
    fun createdMarksSurviveRestart() {
        val store = newStore()
        store.create(valid())
        store.create(PlacedMarkData(title = "Cork", desc = "South", x = 51.8985, y = -8.4756, imageUri = "content://image/2"))

        val reloaded = newStore().findAll()

        assertEquals(2, reloaded.size)
        assertEquals("Cork", reloaded[1].title)
        assertEquals("South", reloaded[1].desc)
        assertEquals(51.8985, reloaded[1].x, 0.0)
        assertEquals("content://image/2", reloaded[1].imageUri)
    }

    @Test
    fun updateAndDeleteSurviveRestart() {
        val store = newStore()
        val first = valid()
        val second = valid().copy(title = "Galway")
        store.create(first)
        store.create(second)

        store.update(first.copy(title = "Home"))
        store.delete(second.id)

        val reloaded = newStore()

        assertEquals(1, reloaded.findAll().size)
        assertEquals("Home", reloaded.findOne(first.id)!!.title)
        assertNull(reloaded.findOne(second.id))
    }

    @Test
    fun idsContinueAfterRestart() {
        val store = newStore()
        store.create(valid())
        store.create(valid())

        val reloaded = newStore()
        val mark = valid()
        reloaded.create(mark)

        assertEquals(3L, mark.id)
    }

    @Test
    fun damagedFileStartsEmpty() {
        file = File(tempFolder.root, "place_list.json")
        file.writeText("{ not valid json")

        assertTrue(newStore().findAll().isEmpty())
    }
}
