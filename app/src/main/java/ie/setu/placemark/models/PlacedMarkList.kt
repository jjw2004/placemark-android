package ie.setu.placemark.models

import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import timber.log.Timber
import java.io.File
import java.util.concurrent.atomic.AtomicLong

class PlacedMarkList(private val file: File) {

    private val placemarks = ArrayList<PlacedMarkData>()
    private val lastId = AtomicLong(0L)

    private val gson = GsonBuilder()
        .setPrettyPrinting()
        .create()

    init {
        load()
    }

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
        save()
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
            save()
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
            save()
            Timber.i("Deleted mark id=$id")
        } else {
            Timber.w("Delete failed, no mark with id=$id")
        }
        return removed
    }

    private fun load() {
        if (!file.exists()) return

        val type = object : TypeToken<ArrayList<PlacedMarkData>>() {}.type

        // A damaged file shouldn't stop the app from starting
        val loaded: ArrayList<PlacedMarkData> = try {
            gson.fromJson(file.readText(), type) ?: arrayListOf()
        } catch (e: JsonParseException) {
            Timber.e(e, "Could not read ${file.name}, starting with an empty list")
            arrayListOf()
        }

        placemarks.clear()
        placemarks.addAll(loaded)

        lastId.set(placemarks.maxOfOrNull { it.id } ?: 0L)

        Timber.i("Loaded ${placemarks.size} marks from ${file.name}")
    }

    private fun save() {
        file.parentFile?.mkdirs()
        file.writeText(gson.toJson(placemarks))
    }
}
