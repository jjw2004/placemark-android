package ie.setu.placemark

import java.util.concurrent.atomic.AtomicLong

class PlacedMarkList {

    private val placemarks = ArrayList<PlacedMark>()
    private val lastId = AtomicLong(0L)

    fun findAll(): List<PlacedMark> {
        return placemarks.toList()   // return a copy so callers can't modify the store directly
    }

    fun findOne(id: Long): PlacedMark? {
        return placemarks.find { p -> p.id == id }
    }

    fun create(placemark: PlacedMark) {
        placemark.id = lastId.incrementAndGet()
        placemarks.add(placemark)
    }

    fun update(placemark: PlacedMark): Boolean {
        val foundIndex = placemarks.indexOfFirst { p -> p.id == placemark.id }
        return if (foundIndex != -1) {
            placemarks[foundIndex] = placemarks[foundIndex].copy(
                title = placemark.title,
                desc = placemark.desc,
                x = placemark.x,
                y = placemark.y
            )
            true
        } else {
            false
        }
    }

    fun delete(id: Long): Boolean {
        return placemarks.removeIf { p -> p.id == id }
    }
}
