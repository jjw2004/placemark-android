package ie.setu.placemark.models

import timber.log.Timber
import java.util.concurrent.atomic.AtomicLong

class PlacedMarkList {

    private val placemarks = ArrayList<PlacedMarkData>()
    private val lastId = AtomicLong(0L)

    fun findAll(): List<PlacedMarkData> {
        return placemarks.toList()   // return a copy so callers can't modify the store directly
    }

    fun findOne(id: Long): PlacedMarkData? {
        return placemarks.find { p -> p.id == id }
    }

    fun create(placemark: PlacedMarkData) {
        placemark.validate()
        placemark.id = lastId.incrementAndGet()
        placemarks.add(placemark)
        Timber.i("Created mark id=${placemark.id}, title=${placemark.title}")
    }

    fun update(placemark: PlacedMarkData): Boolean {
        placemark.validate()

        val foundPlacemark = findOne(placemark.id)
        return if (foundPlacemark != null) {
            foundPlacemark.title = placemark.title
            foundPlacemark.desc = placemark.desc
            foundPlacemark.x = placemark.x
            foundPlacemark.y = placemark.y
            foundPlacemark.imageUri = placemark.imageUri
            Timber.i("Updated mark id=${placemark.id}, title=${placemark.title}")
            true
        } else {
            Timber.w("Update failed, no mark with id=${placemark.id}")
            false
        }
    }

    fun delete(id: Long): Boolean {
        val removed = placemarks.removeIf { p -> p.id == id }
        if (removed) {
            Timber.i("Deleted mark id=$id")
        } else {
            Timber.w("Delete failed, no mark with id=$id")
        }
        return removed
    }
}
