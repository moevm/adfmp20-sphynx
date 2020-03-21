package com.moevm.geoquest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ListView
import com.moevm.geoquest.models.LeaderModel

class RecordsActivity : AppCompatActivity() {

    lateinit var mLeaders : Array<LeaderModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_records)

        val backButton = findViewById<ImageButton>(R.id.back_button)

        backButton.setOnClickListener {
            finish()
        }
        mLeaders = Array(30) {LeaderModel(it, "Новэльны", it + 1, 34232123)}

        val leadersList = findViewById<ListView>(R.id.leaders_list)
        leadersList.adapter = LeadersArrayAdapter(this, R.layout.leader_list_item, mLeaders)
    }
}
