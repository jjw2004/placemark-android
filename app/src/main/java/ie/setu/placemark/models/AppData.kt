package ie.setu.placemark.models

import android.content.Context
import java.io.File

object AppData {

    lateinit var placedMarkList: PlacedMarkList
        private set

    fun initialize(context: Context) {
        // MainActivity is recreated on rotation, so only load the file once
        if (::placedMarkList.isInitialized) return

        val file = File(
            context.filesDir,
            "place_list.json"
        )

        placedMarkList = PlacedMarkList(file)
    }
}
