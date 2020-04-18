package com.moevm.geoquest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.moevm.geoquest.models.QuestModel
import com.moevm.geoquest.models.QuestStatus

class ProfileFragment(private val userId: String?) : FragmentUpdateUI() {
    private val db = Firebase.firestore
    private lateinit var mListView: ListView
    private lateinit var mCompletedQuests: ArrayList<QuestModel>
    private lateinit var mQuestsArrayAdapter: ArrayAdapter<QuestModel>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun updateUI(){
        fillCompletedQuest()
        updateStatistics()
    }

    private fun updateStatistics(){
        if(userId != null)
            db.collection("Users")
                .document(userId)
                .get()
                .addOnSuccessListener {
                    val mapKeys = it.data?.keys
                    var points = 0L
                    var quests = 0L
                    var time = 0L
                    var distance = 0.0

                    if (mapKeys?.contains("Statistic") == true) {
                        Log.d("QuestResults", "Statistic exist")
                        val data = it.data?.getValue("Statistic") as Map<String, *>
                        points = data.getValue("Points").toString().toLong()
                        quests = data.getValue("Quests").toString().toLong()
                        time = data.getValue("Time").toString().toLong()
                        distance = data.getValue("Distance").toString().toDouble() / 1000
                    }
                    view?.findViewById<TextView>(R.id.walked_total)
                        ?.text = getString(R.string.profile_walked_total, distance)
                    view?.findViewById<TextView>(R.id.quests_total)
                        ?.text = getString(R.string.profile_quests_total, quests)
                    view?.findViewById<TextView>(R.id.spent_time)
                        ?.text = getString(R.string.profile_spent_time, timeToHoursMinutes(time.toInt()))
                    view?.findViewById<TextView>(R.id.points_discovered)
                        ?.text = getString(R.string.profile_points_discovered, points)
                }
                .addOnFailureListener{
                    // TODO sorry fail to load
                }

    }

    private fun fillCompletedQuest(){
        if(userId == null) {
            mCompletedQuests = arrayListOf()
            return
        }
        db.collection("Quests").get()
            .addOnSuccessListener { quests_list ->
                db.collection("Users")
                    .document(userId)
                    .collection("Quests")
                    .whereEqualTo("status", QuestStatus.Completed)
                    .get()
                    .addOnSuccessListener { completed_quests ->
                        val toViewObjectsIds = completed_quests.documents.map { it.id }
                        val toView = quests_list.filter { it.id in toViewObjectsIds }
                        val arr = Array(toView.size){
                            val toViewObj = toView[it]
                            val toViewObjData = toViewObj.data
                            QuestModel(
                                toViewObj.id.toInt(),
                                toViewObjData.getValue("Name").toString(),
                                toViewObjData.getValue("Location").toString(),
                                toViewObjData.getValue("Image").toString()
                            )
                        }
                        mCompletedQuests = ArrayList(arr.asList())
                        if (context == null)
                            return@addOnSuccessListener
                        mQuestsArrayAdapter = QuestsArrayAdapter(
                            context!!,
                            R.layout.quest_list_item,
                            mCompletedQuests
                        )
                        mListView.adapter = mQuestsArrayAdapter
                    }
                    .addOnFailureListener { ex ->
                        // TODO: Sorry fail to load
                    }
            }
            .addOnFailureListener{ ex ->
                // TODO: Sorry fail to load
            }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val exitButton = view!!.findViewById<ImageButton>(R.id.exit_button)
        if(view == null){
            Log.d("viewProblem", "view is null")
            return
        }

        mListView = view!!.findViewById(R.id.quests_list)
        fillCompletedQuest()

        mListView.setOnItemClickListener { parent, view, position, id ->
            val questRecords = Intent(context, RecordsActivity::class.java)
            questRecords.putExtra("questId", mCompletedQuests[position].id)
            startActivity(questRecords)
        }

        exitButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(context, LoginActivity::class.java))
        }
    }
}

