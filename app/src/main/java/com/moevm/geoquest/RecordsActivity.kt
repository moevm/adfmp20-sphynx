package com.moevm.geoquest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.moevm.geoquest.models.LeaderModel

class RecordsActivity : AppCompatActivity() {

    private val mLeaders: MutableList<LeaderModel> = mutableListOf()
    private val db = Firebase.firestore
    private val currentUser = FirebaseAuth.getInstance().currentUser


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_records)

        val backButton = findViewById<ImageButton>(R.id.back_button)

        backButton.setOnClickListener {
            finish()
        }

//        TODO loading progress bar
//        TODO set cloud function on update completed count and get that value

        db.collection("Quests")
            .document("0")
            .collection("Records")
            .orderBy("Time")
            .get()
            .addOnSuccessListener { result ->
                Log.d("QuestResults", "size: ${result.documents.size}")
                findViewById<TextView>(R.id.subtitle)
                    ?.text = getString(R.string.records_subtitle, result.documents.size)
                val docs = result.documents
                val leaders = docs.take(10)
                Log.d("QuestResults","current user display name: ${currentUser?.displayName}")
//                val userPosition = docs.indexOfFirst {
//                    it.id == currentUser!!.uid
//                }
                result.documents.forEach{
                    Log.d("QuestResults", "${it.id}: ${it.data}")
                    val time = it.data?.getValue("Time").toString().toInt()
                    val userName = if (currentUser!!.uid == it.id) "Вы" else "Не вы"
                    mLeaders.add(
                        LeaderModel(
                            userName,
                            mLeaders.size+1,
                            "$time мин."
                        )
                    )
                    if(leaders.size == mLeaders.size){
                        val leadersList = findViewById<ListView>(R.id.leaders_list)
                        leadersList.adapter = LeadersArrayAdapter(this, R.layout.leader_list_item, mLeaders)
                    }
                }
            }
            .addOnFailureListener{
                Log.d("QuestResults", "Fail")
            }
    }
}
