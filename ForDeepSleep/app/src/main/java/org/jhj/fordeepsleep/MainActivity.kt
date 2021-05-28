package org.jhj.fordeepsleep

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.jhj.fordeepsleep.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var toolbar = binding.toolbar

        toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_exist_alarm -> {
                    var intent = Intent(this, AlarmListActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }


}