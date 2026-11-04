package ie.setu.placemark.models

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PlacedMarkDataTest {

    private fun valid() = PlacedMarkData(title = "Dublin", x = 53.3498, y = -6.2603)

    @Test
    fun validMarkIsAccepted() {
        valid().validate()
    }

    @Test(expected = IllegalArgumentException::class)
    fun blankNameIsRejected() {
        valid().copy(title = "   ").validate()
    }

    @Test(expected = IllegalArgumentException::class)
    fun latitudeOutOfRangeIsRejected() {
        valid().copy(x = 91.0).validate()
    }

    @Test(expected = IllegalArgumentException::class)
    fun longitudeOutOfRangeIsRejected() {
        valid().copy(y = 181.0).validate()
    }

    @Test(expected = IllegalArgumentException::class)
    fun nanIsRejected() {
        valid().copy(x = Double.NaN).validate()
    }

    @Test
    fun updateCopiesImageUri() {
        val store = PlacedMarkList()
        val mark = valid()
        store.create(mark)

        val changed = mark.copy(title = "Home", imageUri = "content://image/1")
        assertTrue(store.update(changed))

        val stored = store.findOne(mark.id)!!
        assertEquals("Home", stored.title)
        assertEquals("content://image/1", stored.imageUri)
    }

    @Test(expected = IllegalArgumentException::class)
    fun storeRejectsInvalidCreate() {
        PlacedMarkList().create(valid().copy(x = 91.0))
    }
}
