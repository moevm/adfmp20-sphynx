package com.moevm.geoquest

import android.app.AlertDialog
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


class QuestsFragment : Fragment() {
    private val db = Firebase.firestore
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private lateinit var callback: OnQuestActionListener
    private lateinit var mListView: ListView
    private var mIsQuestSelected: Boolean = false
    private var mSelectedQuestId: Int = -1
    private var mQuestsArray: ArrayList<QuestModel> = arrayListOf()
    private lateinit var mQuestsArrayAdapter: ArrayAdapter<QuestModel>
    private var currentQuest: QuestModel? = null

    interface OnQuestActionListener {
        fun onQuestSelected(position: Int)
        fun onQuestGiveUp()
    }

    fun setOnQuestActionListener(callback: OnQuestActionListener) {
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
        dialogBuilder.setMessage("questName")
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_quests, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("Sending_data", "saved inst: $savedInstanceState")
        if (savedInstanceState != null) {
            mIsQuestSelected = savedInstanceState.getBoolean("isQuestSelected")
            mSelectedQuestId = savedInstanceState.getInt("currentQuestId")
        }
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
                            toView.size.toString()
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
        Log.d("Sending_data", "Quest selected questList: $position")
        Log.d("Sending_data", "id: $position; model: $currentQuest")
        mIsQuestSelected = true
        mSelectedQuestId = currentQuest!!.id
        if (userId != null) {
            db.collection("Users")
                .document(userId)
                .collection("Quests")
                .document(currentQuest!!.id.toString())
                .set(mapOf("status" to QuestStatus.InProgress))
                .addOnSuccessListener {
                    Log.d("Sending_data", "success add")
                    updateCurrentQuestVisible()
                    mQuestsArrayAdapter.remove(currentQuest)
                    callback.onQuestSelected(mSelectedQuestId)
                }
                .addOnFailureListener {
                    currentQuest = null
                    Log.d("Sending_data", "failure add")
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
                    Log.d("Sending_data", "success remove current")
                    updateCurrentQuestVisible()
                    mQuestsArrayAdapter.add(currentQuest)
                    currentQuest = null
                    callback.onQuestGiveUp()

                }
                .addOnFailureListener { Log.d("Sending_data", "failure remove current") }
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("isQuestSelected", mIsQuestSelected)
        outState.putInt("currentQuestId", mSelectedQuestId)
    }
}



