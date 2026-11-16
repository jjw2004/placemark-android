package ie.setu.placemark.adapters

import ie.setu.placemark.models.PlacedMarkData

interface OnMarkListener {

    fun onMarkClick(mark: PlacedMarkData)

    fun onMarkLongClick(mark: PlacedMarkData): Boolean
}
