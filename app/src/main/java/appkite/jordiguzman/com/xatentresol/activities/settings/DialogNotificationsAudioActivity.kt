package appkite.jordiguzman.com.xatentresol.activities.settings

import android.content.Context
import android.database.Cursor
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ArrayAdapter
import appkite.jordiguzman.com.xatentresol.R
import appkite.jordiguzman.com.xatentresol.activities.settings.DialogNotificationsAudioActivity.Companion.list
import kotlinx.android.synthetic.main.custom_dialog_notifications_audio.*




class DialogNotificationsAudioActivity : AppCompatActivity() {

    companion object {
        val list: MutableList<Sound> = mutableListOf()

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.custom_dialog_notifications_audio)
        populateListView(sounds(MediaStore.Audio.Media.IS_NOTIFICATION))

    }

    private fun populateListView(list: MutableList<Sound>) {
        val titles = mutableListOf<String>()
        for (music in list) {
            titles.add(music.title)
        }
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_checked, titles)
        lv_notifications_audio.adapter = adapter

        lv_notifications_audio.setOnItemClickListener { _, _, position, _ ->
            val sparseBooleanArray = lv_notifications_audio.checkedItemPositions
            if (sparseBooleanArray.get(position)){
                Log.d("check", sparseBooleanArray.get(position).toString())
            }
            val ringtoneMgr = RingtoneManager(this)
            ringtoneMgr.setType(RingtoneManager.TYPE_NOTIFICATION)
            val alarmsCursor = ringtoneMgr.cursor
            val alarmsCount = alarmsCursor.count
            if (alarmsCount == 0 && !alarmsCursor.moveToFirst()) {
                return@setOnItemClickListener
            }
            val alarms = arrayOfNulls<Uri>(alarmsCount)
            while (!alarmsCursor.isAfterLast && alarmsCursor.moveToNext()) {
                val currentPosition = alarmsCursor.position
                alarms[currentPosition] = ringtoneMgr.getRingtoneUri(currentPosition)
            }
            alarmsCursor.close()
            try {
                val notification = alarms[position]
                val r = RingtoneManager.getRingtone(applicationContext, notification)
                r.play()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

fun Context.sounds(type: String): MutableList<Sound> {
    val uri: Uri = MediaStore.Audio.Media.INTERNAL_CONTENT_URI
    val selection = "$type != 0"
    val sortOrder = MediaStore.Audio.Media.TITLE + " ASC"
    val cursor: Cursor = this.contentResolver.query(
            uri, // Uri
            null,
            selection,
            null,
            sortOrder
    )
    if (cursor.moveToFirst()) {
        val id: Int = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
        val title: Int = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
        do {
            val audioId: Long = cursor.getLong(id)
            val audioTitle: String = cursor.getString(title)
            list.add(Sound(audioId, audioTitle))
        } while (cursor.moveToNext())
    }
    cursor.close()
    return list
}
data class Sound(val id: Long, val title: String)
