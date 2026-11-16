package ie.setu.placemark.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ie.setu.placemark.R
import ie.setu.placemark.adapters.OnMarkListener
import ie.setu.placemark.adapters.PlacedMarkAdapter
import ie.setu.placemark.models.AppData
import ie.setu.placemark.models.PlacedMarkData
import timber.log.Timber

class MarkListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PlacedMarkAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mark_list)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView =
            findViewById(R.id.marksRecyclerView)

        adapter = PlacedMarkAdapter(
            marks = AppData.placedMarkList.findAll(),

            listener = object : OnMarkListener {

                override fun onMarkClick(mark: PlacedMarkData) {
                    editMark(mark)
                }

                override fun onMarkLongClick(mark: PlacedMarkData): Boolean {
                    confirmDelete(mark)
                    return true
                }
            }
        )

        recyclerView.layoutManager =
            LinearLayoutManager(this)

        recyclerView.adapter = adapter

        val returnButton =
            findViewById<Button>(R.id.returnButton)

        // The menu is still underneath this screen, so just close this one
        returnButton.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()

        if (::adapter.isInitialized) {
            val marks = AppData.placedMarkList.findAll()
            Timber.d("Showing ${marks.size} marks")
            adapter.updateMarks(marks)
        }
    }

    private fun editMark(mark: PlacedMarkData) {

        Timber.d("Editing mark id=${mark.id}, title=${mark.title}")

        val intent =
            Intent(this, AddEditActivity::class.java)

        intent.putExtra("id", mark.id)

        startActivity(intent)
    }

    private fun confirmDelete(mark: PlacedMarkData) {

        AlertDialog.Builder(this)
            .setTitle("Delete this mark?")
            .setMessage("\"${mark.title}\" will be removed.")
            .setPositiveButton("Delete") { _, _ ->
                deleteMark(mark)
            }
            .setNegativeButton("Cancel") { _, _ ->
                Timber.d("Delete cancelled for mark id=${mark.id}")
            }
            .show()
    }

    private fun deleteMark(mark: PlacedMarkData) {

        AppData.placedMarkList.delete(mark.id)

        adapter.updateMarks(
            AppData.placedMarkList.findAll()
        )
    }
}
