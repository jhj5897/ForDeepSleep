package org.jhj.fordeepsleep

import android.content.*
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.jhj.fordeepsleep.databinding.ActivityMainBinding
import org.jhj.fordeepsleep.room.Alarm
import org.jhj.fordeepsleep.room.AppDatabase
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private val RINGTONE_REQUEST_CODE: Int = 101
    private val URI = "URI"
    private val VOLUME = "VOLUME"
    private val VIBRATION = "VIBRATION"

    private lateinit var binding: ActivityMainBinding
    private lateinit var db: AppDatabase

    private lateinit var audioManager: AudioManager
    private var MAX_VOLUME: Float = 0.0f
    private lateinit var rt: Ringtone
    private var uri: Uri? = null
    private var selectedItemIndex = BooleanArray(6)
    private var doubleBackToExitPressedOn = false

    private var ringtoneResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                try {
                    val tmpUri: Uri =
                        result.data?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
                            ?: throw NullPointerException()
                    uri = tmpUri
                    setRingtoneUri(uri!!)

                    binding.textViewAlarm.text = rt.getTitle(this)
                } catch (e: NullPointerException) {
                }
            }
        }

    private val onPlayButtonListener = View.OnClickListener {
        if (rt.isPlaying) {
            (it as ImageButton).setImageResource(R.drawable.ic_play_circle)
            rt.stop()
        } else {
            (it as ImageButton).setImageResource(R.drawable.ic_pause_circle)
            rt.play()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        volumeControlStream = AudioManager.STREAM_ALARM

        val toolbar = binding.toolbar

        db = AppDatabase.getInstance(applicationContext)
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        MAX_VOLUME = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM).toFloat()

        TimePickerFunction.getInstance(binding.timePicker)
        AlarmFunction.init(applicationContext)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_exist_alarm -> {
                    val intent = Intent(this, AlarmListActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.action_empty_alarm -> {
                    Toast.makeText(this, "현재 예약된 알람이 없습니다.", Toast.LENGTH_SHORT).show()
                    true
                }

                else -> false
            }
        }

        initOptions()

        //타임피커 값 변경되면 선택 시간 초기화
        binding.timePicker.setOnTimeChangedListener { _, _, _ -> clearPeriodTextAndList() }

        //알람음 재생 버튼 클릭 리스너
        binding.btnRingtonePlay.setOnClickListener(onPlayButtonListener)

        //취소 버튼 = 나가기
        binding.btnCancel.setOnClickListener { finish() }

        val shown = PreferenceManager.getBoolean(this, "dialogShown")
        Log.d("TAG", "$shown")
        if (!shown) {
            AlertDialog.Builder(this)
                .setMessage(R.string.first_use_dialog)
                .setPositiveButton(R.string.dialog_accept, null)
                .show()

            PreferenceManager.setBoolean(this, "dialogShown", true)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.alarm_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        updateToolbar()
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onResume() {
        invalidateOptionsMenu()
        super.onResume()
    }

    private fun initOptions() {
        //알람음 초기화
        val prefUri = PreferenceManager.getString(applicationContext, URI)
        val prefVolume = PreferenceManager.getInt(applicationContext, VOLUME)

        uri = if (prefUri == "") {
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        } else {
            Uri.parse(prefUri)
        }
        setRingtoneUri(uri!!)

        binding.textViewAlarm.text = rt.getTitle(this)

        //Seekbar 초기화
        val seekbar = binding.seekBarVolume.apply {
            max = MAX_VOLUME.toInt()

            progress = if (prefVolume == -1) {
                audioManager.getStreamVolume(AudioManager.STREAM_ALARM)
            } else {
                PreferenceManager.getInt(applicationContext, VOLUME)
            }
            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, progress, 0)
            binding.textViewVolume.text = progress.toString()
        }

        seekbar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, b: Boolean) {
                binding.textViewVolume.text = progress.toString()
                audioManager.setStreamVolume(AudioManager.STREAM_ALARM, progress, 0)
            }

            override fun onStartTrackingTouch(s: SeekBar?) {
            }

            override fun onStopTrackingTouch(s: SeekBar?) {
            }
        })

        //진동 초기화
        binding.switchVibration.isChecked = PreferenceManager.getBoolean(this, VIBRATION)
    }

    private fun saveOptions(uri: Any, volume: Int, vibration: Boolean) {
        PreferenceManager.setString(applicationContext, URI, uri.toString())
        PreferenceManager.setInt(applicationContext, VOLUME, volume)
        PreferenceManager.setBoolean(applicationContext, VIBRATION, vibration)
    }

    private fun getNow(): Calendar {
        val now = Calendar.getInstance().apply {
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return now
    }

    private fun clearPeriodTextAndList() {
        Arrays.fill(selectedItemIndex, false)
        binding.textViewPeriod.text = getString(R.string.sample_option)
    }

    private fun updateToolbar() {
        val emptyItem = binding.toolbar.menu.findItem(R.id.action_empty_alarm)
        val existItem = binding.toolbar.menu.findItem(R.id.action_exist_alarm)

        if (db.alarmDao().getAllCount() > 0) {
            emptyItem.isVisible = false
            existItem.isVisible = true
        } else {
            emptyItem.isVisible = true
            existItem.isVisible = false
        }
    }

    fun onRightNowButtonClicked(view: View) {
        TimePickerFunction.setTP(getNow())
    }

    fun onAdd10MinButtonClicked(view: View) {
        TimePickerFunction.addTP(10)
    }

    fun onAdd30MinButtonClicked(view: View) {
        TimePickerFunction.addTP(30)
    }

    fun onAdd1HourButtonClicked(view: View) {
        TimePickerFunction.addTP(60)
    }


    fun onCheckboxDialogClick(view: View) {
        clearPeriodTextAndList()

        val orgTime = TimePickerFunction.getTP()
        val periodStringArray = listOf("1시간 30분", "3시간", "4시간 30분", "6시간", "7시간 30분", "9시간")

        val items = Array(6) { i ->
            val tmpTime = TimePickerFunction.getCycleTime(orgTime, i + 1) //cycle은 양수이므로 1 더함

            val str = SimpleDateFormat("a hh:mm", Locale.getDefault()).format(tmpTime.timeInMillis)
                .toString() + " (%s)".format(periodStringArray[i])

            str
        }

        val builder = AlertDialog.Builder(this)
        builder.setTitle("시간 선택")
        builder.setMultiChoiceItems(items, null) { _, ind, _ ->
            selectedItemIndex[ind] = !selectedItemIndex[ind]
        }

        val listener = DialogInterface.OnClickListener { _, _ ->
            val sb = StringBuilder()
            for (i in 0 until selectedItemIndex.size) {
                if (selectedItemIndex[i]) {
                    sb.append(items[i] + "\n")
                }
            }
            binding.textViewPeriod.text = sb.trim().toString()
        }


        builder.setPositiveButton("확인", listener)
        builder.setNegativeButton("취소", null)
        builder.show()
    }

    fun onAlarmRingtoneClick(view: View) {

        val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
            this.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "알람음을 선택하세요.")
            this.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, true)
            this.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false)
            this.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM)
        }
        ringtoneResultLauncher.launch(intent)
    }

    fun setRingtoneUri(uri: Uri) {
        rt = RingtoneManager.getRingtone(this, uri)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            rt.audioAttributes =
                AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_ALARM).build()
        } else {
            rt.streamType = AudioManager.STREAM_ALARM
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RINGTONE_REQUEST_CODE && resultCode == RESULT_OK) {
            uri = data?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
            setRingtoneUri(uri!!)

            binding.textViewAlarm.text = rt.getTitle(this)

        }
    }

    fun onSaveButtonClicked(view: View) {
        if (rt.isPlaying)
            binding.btnRingtonePlay.performClick()

        if (selectedItemIndex.all { b -> b == false }) {
            Toast.makeText(this, "알람 시간을 설정해주세요.", Toast.LENGTH_SHORT).show()
            return
        } else {
            val orgTime = TimePickerFunction.getTP()

            for (i in 0 until selectedItemIndex.size) {
                if (!selectedItemIndex[i]) continue

                val tmpTime = TimePickerFunction.getCycleTime(orgTime, i + 1)

                if (isTimeBefore(tmpTime)) {
                    tmpTime.add(Calendar.DAY_OF_MONTH, 1)
                }

                val alarm = Alarm(
                    null,
                    tmpTime.timeInMillis,
                    uri.toString(),
                    audioManager.getStreamVolume(AudioManager.STREAM_ALARM) / MAX_VOLUME,
                    binding.switchVibration.isChecked
                )

                db.alarmDao().insertAll(alarm)
                AlarmFunction.setAlarmIntent(db.alarmDao().getLastAlarm())
            }

            //저장 완료 후 화면 초기화
            Toast.makeText(this, "알람이 저장되었습니다.", Toast.LENGTH_SHORT).show()
            saveOptions(
                uri!!,
                binding.textViewVolume.text.toString().toInt(),
                binding.switchVibration.isChecked
            )
            onRightNowButtonClicked(view)
            clearPeriodTextAndList()

            invalidateOptionsMenu()
        }

    }

    fun isTimeBefore(calendar: Calendar): Boolean = !calendar.after(getNow())

    //뒤로가기 버튼
    override fun onBackPressed() {
        if (doubleBackToExitPressedOn) {
            super.onBackPressed()
            return
        } else {
            doubleBackToExitPressedOn = true
            Toast.makeText(this, "한 번 더 뒤로가기를 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()

            Handler(Looper.getMainLooper()).postDelayed({ doubleBackToExitPressedOn = false }, 2000)
        }
    }
}