package ie.setu.placemark.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ie.setu.placemark.R
import ie.setu.placemark.models.AppData
import ie.setu.placemark.models.PlacedMarkData
import timber.log.Timber

class AddEditActivity : AppCompatActivity() {

    private lateinit var titleInput: EditText
    private lateinit var descriptionInput: EditText
    private lateinit var xInput: EditText
    private lateinit var yInput: EditText
    private lateinit var imagePreview: ImageView

    private var selectedImageUri: Uri? = null
    private var editingId: Long? = null

    // Must be registered before the Activity starts, so it lives outside onCreate()
    private val imagePicker =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            if (uri != null) {
                // Keep access to the document URI for later use, when supported.
                try {
                    contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (_: SecurityException) {
                    // Some providers may not grant persistable access.
                }

                Timber.d("Image selected")
                selectedImageUri = uri
                imagePreview.setImageURI(uri)
            } else {
                Timber.d("Image picker cancelled")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_edit)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        titleInput =
            findViewById(R.id.titleInput)

        descriptionInput =
            findViewById(R.id.descriptionInput)

        xInput =
            findViewById(R.id.xInput)

        yInput =
            findViewById(R.id.yInput)

        imagePreview =
            findViewById(R.id.imageViewAdd)

        imagePreview.setImageResource(android.R.drawable.ic_menu_gallery)

        findViewById<Button>(R.id.imagePickButton).setOnClickListener {
            imagePicker.launch(arrayOf("image/*"))
        }

        val saveButton =
            findViewById<Button>(R.id.saveButton)

        val cancelButton =
            findViewById<Button>(R.id.cancelButton)

        editingId =
            intent.getLongExtra("id", -1L)
                .takeIf { it != -1L }

        if (editingId != null) {

            findViewById<TextView>(R.id.formTitle)
                .text = "Edit Mark"

            loadExistingMark(editingId!!)
        } else {
            Timber.d("Adding a new mark")
        }

        saveButton.setOnClickListener {
            saveMark()
        }

        cancelButton.setOnClickListener {
            Timber.d("Add/edit cancelled")
            finish()
        }
    }

    private fun loadExistingMark(id: Long) {

        val mark =
            AppData.placedMarkList.findOne(id)

        if (mark == null) {

            Timber.w("Mark not found for editing, id=$id")

            Toast.makeText(
                this,
                "Mark not found",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        Timber.d("Loaded mark id=${mark.id} for editing")

        titleInput.setText(mark.title)
        descriptionInput.setText(mark.desc)
        xInput.setText(mark.x.toString())
        yInput.setText(mark.y.toString())

        selectedImageUri = mark.imageUri?.let { Uri.parse(it) }

        if (selectedImageUri != null) {
            imagePreview.setImageURI(selectedImageUri)
        } else {
            imagePreview.setImageResource(android.R.drawable.ic_menu_gallery)
        }
    }

    private fun saveMark() {

        val x =
            xInput.text.toString().toDoubleOrNull()

        if (x == null) {
            xInput.error = "Enter a valid number"
            return
        }

        val y =
            yInput.text.toString().toDoubleOrNull()

        if (y == null) {
            yInput.error = "Enter a valid number"
            return
        }

        val mark = PlacedMarkData(
            id = editingId ?: 0L,
            title = titleInput.text.toString().trim(),
            desc = descriptionInput.text.toString().trim(),
            x = x,
            y = y,
            imageUri = selectedImageUri?.toString()
        )

        try {
            mark.validate()

            if (editingId == null) {
                AppData.placedMarkList.create(mark)
                Toast.makeText(this, "Mark created", Toast.LENGTH_SHORT).show()
            } else {
                AppData.placedMarkList.update(mark)
                Toast.makeText(this, "Mark updated", Toast.LENGTH_SHORT).show()
            }

            finish()

        } catch (e: IllegalArgumentException) {
            Timber.w("Validation failed: ${e.message}")

            Toast.makeText(
                this,
                e.message ?: "Please check the entered values.",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
