package com.moevm.geoquest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.moevm.geoquest.models.LeaderModel

fun timeToHoursMinutes(time: Int): String {
    Log.d("questProgress", "time: %.1f ч.".format(time.toFloat()/60))
    return if (time < 60)
        "$time мин."
    else {
        "%.1f ч.".format(time.toFloat()/60)
    }
}

class RecordsActivity : AppCompatActivity() {

    companion object {
        const val recordsToView = 10
    }

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
        val questId = intent.extras?.get("questId").toString()
        Log.d("username", currentUser?.displayName.toString())
        db.collection("Quests")
            .document(questId)
            .collection("Records")
            .orderBy("Time")
            .get()
            .addOnSuccessListener { result ->
                Log.d("QuestResults", "size: ${result.documents.size}")
                findViewById<TextView>(R.id.subtitle)
                    ?.text = getString(R.string.records_subtitle, result.documents.size)
                val docs = result.documents

                val userPosition = result.documents.indexOfFirst {
                    it.id == currentUser?.uid
                }
                if (userPosition >= recordsToView) {
                    val userData = result.documents[userPosition].data
                    val time = userData?.getValue("Time").toString().toInt()
                    findViewById<ConstraintLayout>(R.id.userResult)?.visibility = View.VISIBLE
                    findViewById<TextView>(R.id.place)?.text = (userPosition+1).toString()
                    findViewById<TextView>(R.id.name)?.text = "Вы"
                    findViewById<TextView>(R.id.time)?.text = timeToHoursMinutes(time)
                }

                val leaders = docs.take(recordsToView)
                result.documents.forEach{
                    val time = it.data?.getValue("Time").toString().toInt()
                    val usernameDb = if ("Username" in it.data!!.keys)
                        it.data?.getValue("Username").toString()
                    else ""
                    val userName = if (currentUser!!.uid == it.id) "Вы" else usernameDb
                    mLeaders.add(
                        LeaderModel(
                            userName,
                            mLeaders.size+1,
                            timeToHoursMinutes(time)
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
