package appkite.jordiguzman.com.xatentresol.activities.settings

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ArrayAdapter
import appkite.jordiguzman.com.xatentresol.R
import kotlinx.android.synthetic.main.custom_dialog_notifications_audio.*

class DialogNotificationsAudioActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.custom_dialog_notifications_audio)

        populateListView(sounds(MediaStore.Audio.Media.IS_NOTIFICATION))

    }

    private fun populateListView(list: MutableList<Sound>) {
        // Get specific sound titles list
        val titles = mutableListOf<String>()
        for (music in list) {
            titles.add(music.title)
        }

        // Display sound list on list view
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, titles)
        lv_notifications_audio.adapter = adapter
        lv_notifications_audio.setOnItemClickListener { parent, view, position, id ->
            Log.d("List", titles[position])
        }
    }


}

fun Context.sounds(type: String): MutableList<Sound> {
    // Initialize an empty mutable list of sounds
    val list: MutableList<Sound> = mutableListOf()

    // Get the internal storage media store audio uri
    //val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    val uri: Uri = MediaStore.Audio.Media.INTERNAL_CONTENT_URI

    // Non-zero if the audio file type match
    val selection = "$type != 0"

    // Sort the audio
    val sortOrder = MediaStore.Audio.Media.TITLE + " ASC"
    //val sortOrder = MediaStore.Audio.Media.TITLE + " DESC"

    // Query the storage for specific type audio files
    val cursor: Cursor = this.contentResolver.query(
            uri, // Uri
            null, // Projection
            selection, // Selection
            null, // Selection arguments
            sortOrder // Sort order
    )

    // If query result is not empty
    if (cursor.moveToFirst()) {
        val id: Int = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
        val title: Int = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)

        // Now loop through the audio files
        do {
            val audioId: Long = cursor.getLong(id)
            val audioTitle: String = cursor.getString(title)

            // Add the current audio/sound to the list
            list.add(Sound(audioId, audioTitle))
        } while (cursor.moveToNext())
    }

    cursor.close()

    // Finally, return the audio files list
    return list
}

data class Sound(val id: Long, val title: String)
