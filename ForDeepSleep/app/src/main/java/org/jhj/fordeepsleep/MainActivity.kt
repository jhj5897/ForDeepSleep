package org.jhj.fordeepsleep

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.media.AudioManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.room.Room
import org.jhj.fordeepsleep.databinding.ActivityMainBinding
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private val ALARM_REQUEST_CODE: Int = 101

    private lateinit var binding: ActivityMainBinding
    private lateinit var db: AppDatabase
    private lateinit var timePicker: TimePicker
    private var selectedItemIndex = mutableListOf<Int>()

    private lateinit var rt: Ringtone
    private lateinit var uri: Uri

    private var doubleBackToExitPressedOn = false
    private var btnRingtonePlayClicked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = binding.toolbar
        timePicker = binding.timePicker

        db = AppDatabase.getInstance(this)

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

        //타임피커 값 변경되면 선택 시간 초기화
        timePicker.setOnTimeChangedListener { timePicker, i, j ->
            binding.textViewPeriod.text = ""
            selectedItemIndex.clear()
        }

        //알람음 초기화
        uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        rt = RingtoneManager.getRingtone(this, uri)
        binding.textViewAlarm.text = rt.getTitle(this)

        //Seekbar 초기화
        val seekbarVolume = binding.textViewVolume
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        val seekbar = binding.seekBarVolume.apply {
            max = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM)
            progress = audioManager.getStreamVolume(AudioManager.STREAM_ALARM)
            seekbarVolume.text = audioManager.getStreamVolume(AudioManager.STREAM_ALARM).toString()
        }

        seekbar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, b: Boolean) {
                seekbarVolume.text = progress.toString()
                audioManager.setStreamVolume(AudioManager.STREAM_ALARM, progress, 0)
            }

            override fun onStartTrackingTouch(s: SeekBar?) {
            }

            override fun onStopTrackingTouch(s: SeekBar?) {
            }
        })

        //알람음 재생 버튼 클릭 리스너
        binding.btnRingtonePlay.setOnClickListener {
            if (btnRingtonePlayClicked) {
                (it as ImageButton).setImageResource(R.drawable.ic_play_circle)
                rt.stop()
                btnRingtonePlayClicked = false
            } else {
                (it as ImageButton).setImageResource(R.drawable.ic_pause_circle)
                rt.play()
                btnRingtonePlayClicked = true
                Log.d(
                    "alarm",
                    "now volume : " + audioManager.getStreamVolume(AudioManager.STREAM_ALARM)
                )
            }
        }

        //취소 버튼 = 나가기
        binding.btnCancel.setOnClickListener(
            { finish() }
        )
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

    fun GetTimeFromTP(): Calendar {
        val timePicker = binding.timePicker

        val now: Calendar = Calendar.getInstance()
        now.time = Date(System.currentTimeMillis())
        val calendar: Calendar = now.clone() as Calendar

        //현재 시간에서 타임피커의 시간, 분으로 교체
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            calendar.set(Calendar.HOUR_OF_DAY, timePicker.hour)
            calendar.set(Calendar.MINUTE, timePicker.minute)
        } else {
            calendar.set(Calendar.HOUR_OF_DAY, timePicker.currentHour)
            calendar.set(Calendar.MINUTE, timePicker.currentMinute)
        }

        //교체한 시간이 now보다 이전이면 하루를 추가
        if (calendar.before(now)) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        //알람 울리는 시간에 대비해 sec, millisec = 0
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        return calendar
    }

    fun OnRightNowButtonClicked(view: View) {
        val timeNow = Calendar.getInstance()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePicker.hour = timeNow.get(Calendar.HOUR_OF_DAY)
            timePicker.minute = timeNow.get(Calendar.MINUTE)

        } else {
            timePicker.currentHour = timeNow.get(Calendar.HOUR_OF_DAY)
            timePicker.currentMinute = timeNow.get(Calendar.MINUTE)
        }
    }

    fun OnAdd10MinButtonClicked(view: View) {
        addMinOnTP(10)
    }

    fun OnAdd30MinButtonClicked(view: View) {
        addMinOnTP(30)
    }

    fun OnAdd1HourButtonClicked(view: View) {
        addMinOnTP(60)
    }

    fun addMinOnTP(addValue: Int) {
        var min: Int

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            min = timePicker.minute + addValue
            timePicker.minute = (min % 60)
            timePicker.hour += (min / 60)

        } else {
            min = timePicker.currentMinute + addValue
            timePicker.currentMinute = (min % 60)
            timePicker.currentHour += (min / 60)
        }
    }

    fun OnCheckboxDialogClick(view: View) {
        selectedItemIndex.clear()

        val orglTime = GetTimeFromTP()
        val periodStringArray = listOf<String>("1시간 30분", "3시간", "4시간 30분", "6시간", "7시간 30분", "9시간")


        var items = Array<String>(6) { i ->
            val min = 90 * (i + 1)

            var orglTimeClone = orglTime.clone() as Calendar
            orglTimeClone.add(Calendar.HOUR_OF_DAY, min / 60)
            orglTimeClone.add(Calendar.MINUTE, min % 60)

            var str = SimpleDateFormat("hh:mm a").format(orglTimeClone.time)
                .toString() + " (%s)".format(periodStringArray[i])

            str
        }

        var builder = AlertDialog.Builder(this)
        builder.setTitle("숙면 시간")
        builder.setMultiChoiceItems(items, null) { dialogInterface, i, b ->
            if (b) {
                selectedItemIndex.add(i)
            } else if (selectedItemIndex.contains(i)) {
                selectedItemIndex.remove(i)
            }
        }

        var listener = DialogInterface.OnClickListener { _, which ->
            var str = StringBuilder()
            selectedItemIndex.sort()
            for (i in selectedItemIndex) {
                str.append(items.get(i) + "\n")
            }
            binding.textViewPeriod.text = str.toString()
        }


        builder.setPositiveButton("확인", listener)
        builder.setNegativeButton("취소", null)
        builder.show()
    }

    fun OnAlarmRingtoneClick(view: View) {

        var intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
            this.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "알람음을 선택하세요.")
            this.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, true)
            this.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true)
            this.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM)
        }
        startActivityForResult(intent, ALARM_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ALARM_REQUEST_CODE && resultCode == RESULT_OK) {
            uri = data?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)!!

            rt = RingtoneManager.getRingtone(this, uri)
            binding.textViewAlarm.text = rt?.getTitle(this)

        }
    }


    fun updateToolbar() {
        val emptyItem = binding.toolbar.menu.findItem(R.id.action_empty_alarm)
        val existItem = binding.toolbar.menu.findItem(R.id.action_exist_alarm)

        if (db.alarmDao().getAllCount() > 0) {
            emptyItem.setVisible(false)
            existItem.setVisible(true)
        } else {
            emptyItem.setVisible(true)
            existItem.setVisible(false)
        }
    }


    fun OnSaveButtonClicked(view: View) {

        if (selectedItemIndex.all { b -> false }) {
            Toast.makeText(this, "알람 시간을 설정해주세요.", Toast.LENGTH_SHORT).show()
            return
        } else {
            val orglTime = GetTimeFromTP()

            for (i in selectedItemIndex) {
                val min = 90 * (i + 1)

                var orglTimeClone = orglTime.clone() as Calendar
                orglTimeClone.add(Calendar.HOUR_OF_DAY, min / 60)
                orglTimeClone.add(Calendar.MINUTE, min % 60)


                db.alarmDao().insertAll(
                    Alarm(
                        null,
                        orglTimeClone,
                        uri,
                        binding.textViewVolume.text.toString().toInt(),
                        binding.swtichVibration.isChecked
                    )
                )
            }

            //저장 완료 후 화면 초기화
            Toast.makeText(this, "알람이 저장되었습니다.", Toast.LENGTH_SHORT).show()
            OnRightNowButtonClicked(view)
            selectedItemIndex.clear()
            binding.textViewPeriod.text = ""

            invalidateOptionsMenu()
        }

    }


    //뒤로가기 버튼
    override fun onBackPressed() {
        if (doubleBackToExitPressedOn) {
            super.onBackPressed()
            return
        } else {
            doubleBackToExitPressedOn = true
            Toast.makeText(this, "한 번 더 뒤로가기를 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()

            Handler().postDelayed(Runnable { doubleBackToExitPressedOn = false }, 2000)
        }
    }
}