package com.moevm.geoquest

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.moevm.geoquest.models.QuestModel
import com.moevm.geoquest.models.QuestStatus
import com.squareup.picasso.Picasso


class QuestsFragment(private val userId: String?) : FragmentUpdateUI() {
    private val db = Firebase.firestore
    private lateinit var callback: QuestsActionListener
    private lateinit var mListView: ListView
    private var mIsQuestSelected: Boolean = false
    private var mQuestsArray: ArrayList<QuestModel> = arrayListOf()
    private lateinit var mQuestsArrayAdapter: ArrayAdapter<QuestModel>
    private var currentQuest: QuestModel? = null

    interface QuestsActionListener {
        fun onQuestSelected(position: Int)
        fun onQuestGiveUp()
    }
    fun setOnQuestsActionListener(callback: QuestsActionListener) {
        this.callback = callback
    }

    private val questSelectListener = AdapterView.OnItemClickListener { _, _, position, _ ->
        if (view?.findViewById<LinearLayout>(R.id.current_quest_container)
                ?.visibility == View.VISIBLE
        ) {
            Toast.makeText(
                context, getString(R.string.current_quest_already_selected),
                Toast.LENGTH_LONG
            ).show()
            return@OnItemClickListener
        }
        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setMessage(mQuestsArray[position].name)
            .setCancelable(false)
            .setPositiveButton(getString(R.string.dialog_yes)) { _, _ ->
                questApproved(position)
            }
            .setNegativeButton(getString(R.string.dialog_no)) { dialog, _ ->
                dialog.cancel()
            }
        val alert = dialogBuilder.create()
        alert.setTitle(getString(R.string.sure_start_quest_title))
        alert.show()
    }

    private val giveUpQuestListener = View.OnClickListener {
        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setMessage(getString(R.string.sure_give_up_quest_title))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.dialog_yes)) { dialog, id ->
                questGiveUp()
            }
            .setNegativeButton(getString(R.string.dialog_no)) { dialog, id ->
                dialog.cancel()
            }
        val alert = dialogBuilder.create()
        alert.setTitle(getString(R.string.sure_start_quest_title))
        alert.show()
    }

    override fun updateUI(){
        updateCurrentQuestVisible()
        fillQuestArray()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_quests, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateCurrentQuestVisible()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (view == null) {
            return
        }
        mListView = view!!.findViewById(R.id.quests_list)

        val btnGiveUpQuest = view?.findViewById<TextView>(R.id.give_up_quest)
        btnGiveUpQuest?.setOnClickListener(giveUpQuestListener)

        fillQuestArray()

        mListView.onItemClickListener = questSelectListener

        val infoButton = view!!.findViewById<ImageButton>(R.id.info_button)

        infoButton.setOnClickListener {
            startActivity(Intent(context, InfoActivity::class.java))
        }
    }

    private fun updateCurrentQuestVisible() {
        if (view == null || userId == null)
            return
        db.collection("Users")
            .document(userId)
            .collection("Quests")
            .whereEqualTo("status", QuestStatus.InProgress)
            .get()
            .addOnSuccessListener { current_user_quest ->
                val currentQuest = current_user_quest.documents
                when (currentQuest.size) {
                    0 -> {
                        view?.findViewById<LinearLayout>(R.id.current_quest_container)
                            ?.visibility = View.GONE
                    }
                    1 -> {
                        db.collection("Quests").document(currentQuest[0].id).get()
                            .addOnSuccessListener {
                                val questData = it.data
                                view?.findViewById<LinearLayout>(R.id.current_quest_container)
                                    ?.visibility = View.VISIBLE
                                val imageView = view?.findViewById<ImageView>(R.id.image_view)
                                if (imageView != null) {
                                    val link = questData?.getValue("Image") as String
                                    Picasso.get()
                                        .load(link)
                                        .into(imageView)
                                }

                                view?.findViewById<TextView>(R.id.name)
                                    ?.text = questData?.getValue("Name") as String
                                view?.findViewById<TextView>(R.id.location)
                                    ?.text = questData.getValue("Location") as String
                            }
                            .addOnFailureListener {
                                // TODO: Sorry fail to load
                            }
                    }
                    else -> {
                        Log.d("currentQuest", "count of currentQuest > 1")
                    }
                }
            }
    }

    private fun fillQuestArray() {
        if (userId == null) {
            mQuestsArray = arrayListOf()
            return
        }
        Log.d("currentQuest", "user id: $userId")
        db.collection("Quests").get()
            .addOnSuccessListener { quests_list ->
                db.collection("Users")
                    .document(userId)
                    .collection("Quests")
                    .whereIn("status", listOf(QuestStatus.Completed, QuestStatus.InProgress))
                    .get()
                    .addOnSuccessListener { user_quests ->
                        val doesNotViewObjectsIds = user_quests.documents.map { it.id }
                        val toView = quests_list.filter { it.id !in doesNotViewObjectsIds }
                        view?.findViewById<TextView>(R.id.available_quests_count)?.text =
                            getString(R.string.available_quest_count_fill, toView.size)
                        val currentId = user_quests.documents.find {
                            it.data?.getValue("status") == QuestStatus.InProgress.toString()
                        }
                        if (currentId != null) {
                            val currentQuestObj =
                                quests_list.documents.find { it.id.toInt() == currentId.id.toInt() }!!
                            currentQuest = QuestModel(
                                currentQuestObj.id.toInt(),
                                currentQuestObj.data?.getValue("Name").toString(),
                                currentQuestObj.data?.getValue("Location").toString(),
                                currentQuestObj.data?.getValue("Image").toString()
                            )
                            Log.d("currentQuest", "$currentQuest")
                        }
                        val arr = Array(toView.size) {
                            val toViewObj = toView[it]
                            val toViewObjData = toViewObj.data
                            QuestModel(
                                toViewObj.id.toInt(),
                                toViewObjData.getValue("Name").toString(),
                                toViewObjData.getValue("Location").toString(),
                                toViewObjData.getValue("Image").toString()
                            )
                        }

                        mQuestsArray = ArrayList(arr.asList())
                        if (context == null)
                            return@addOnSuccessListener

                        view?.findViewById<ProgressBar>(R.id.progress_bar)?.visibility = View.GONE
                        mQuestsArrayAdapter = QuestsArrayAdapter(
                            context!!,
                            R.layout.quest_list_item,
                            mQuestsArray
                        )
                        mListView.adapter = mQuestsArrayAdapter
                    }
                    .addOnFailureListener { ex ->
                        // TODO: Sorry fail to load
                        mQuestsArray = arrayListOf()
                    }
            }
            .addOnFailureListener { ex ->
                // TODO: Sorry fail to load
                mQuestsArray = arrayListOf()
            }
    }

    private fun questApproved(position: Int) {
        currentQuest = mQuestsArray[position]
        if (currentQuest == null)
            return
        mIsQuestSelected = true
        val questId = currentQuest!!.id
        if (userId != null) {
            db.collection("Users")
                .document(userId)
                .collection("Quests")
                .document(currentQuest!!.id.toString())
                .set(mapOf("status" to QuestStatus.InProgress))
                .addOnSuccessListener {
                    updateCurrentQuestVisible()
                    mQuestsArrayAdapter.remove(currentQuest)
                    callback.onQuestSelected(questId)
                }
                .addOnFailureListener {
                    currentQuest = null
                    mIsQuestSelected = false
                    //TODO: No internet connection
                }
        }

    }

    private fun questGiveUp() {
        if (userId != null && currentQuest != null) {
            db.collection("Users")
                .document(userId)
                .collection("Quests")
                .document(currentQuest?.id.toString())
                .set(mapOf("status" to QuestStatus.Nothing))
                .addOnSuccessListener {
                    mIsQuestSelected = false
                    updateCurrentQuestVisible()
                    mQuestsArrayAdapter.add(currentQuest)
                    view?.findViewById<TextView>(R.id.available_quests_count)?.text =
                        getString(R.string.available_quest_count_fill, mQuestsArray.size)
                    currentQuest = null
                    callback.onQuestGiveUp()

                }
                .addOnFailureListener { Log.d("Sending_data", "failure remove current") }
        }
    }

}



