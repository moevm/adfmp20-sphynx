package com.moevm.geoquest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ListView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.moevm.geoquest.models.LeaderModel

class RecordsActivity : AppCompatActivity() {

    lateinit var mLeaders: Array<LeaderModel>
    private val db = Firebase.firestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_records)

        val backButton = findViewById<ImageButton>(R.id.back_button)

        backButton.setOnClickListener {
            finish()
        }

//        TODO loading progress bar


        db.collectionGroup("Quests").get()
            .addOnSuccessListener { result ->
                Log.d("QuestResults", "size: ${result.documents.size}")
                result.documents.forEach{
                    Log.d("QuestResults", "${it.data}")
                }
            }
            .addOnFailureListener{
                Log.d("QuestResults", "Fail")
            }

        mLeaders = Array(30) { LeaderModel(it, "Новэльны", it + 1, 34232123) }


        val leadersList = findViewById<ListView>(R.id.leaders_list)
        leadersList.adapter = LeadersArrayAdapter(this, R.layout.leader_list_item, mLeaders)
    }
}
