package ie.setu.placemark.models

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
}
