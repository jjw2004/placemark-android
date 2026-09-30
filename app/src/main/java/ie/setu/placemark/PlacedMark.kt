package ie.setu.placemark

/**
 * Data class representing a single Placemark item.
 * Kotlin automatically generates toString(), equals(), hashCode(), and copy().
 */
data class PlacedMark(
    var id: Long = 0L,
    val title: String = "",
    val desc: String = "",
    val x: Double = 0.0,
    val y: Double = 0.0
)
