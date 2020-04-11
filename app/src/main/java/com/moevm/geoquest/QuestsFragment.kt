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
    private var mQuestsArray: Array<QuestModel> = arrayOf()
    private var mSelectedQuestId: Int = -1
    private lateinit var mQuestsList: ArrayAdapter<QuestModel>
    private var mIsQuestSelected: Boolean = false

    interface OnQuestActionListener {
        fun onQuestSelected(position: Long)
        fun onQuestGiveUp()
    }

    fun setOnQuestActionListener(callback: OnQuestActionListener) {
        this.callback = callback
    }

    private val questSelectListener = AdapterView.OnItemClickListener { parent, view_, position, item_id ->
        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setMessage("questName")
            .setCancelable(false)
            .setPositiveButton(getString(R.string.dialog_yes)) { dialog, id ->
                Log.d("Sending_data","Quest selected questList: $item_id, $position, $view_")
                mIsQuestSelected = true
                mSelectedQuestId = item_id.toInt()
                updateCurrentQuestVisible(view!!)
                callback.onQuestSelected(item_id)
            }
            .setNegativeButton(getString(R.string.dialog_no)) { dialog, id ->
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
                mIsQuestSelected = false
                updateCurrentQuestVisible(view!!)
                callback.onQuestGiveUp()
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
        Log.d("Sending_data" , "saved inst: $savedInstanceState")
        if(savedInstanceState != null){
            mIsQuestSelected = savedInstanceState.getBoolean("isQuestSelected")
            mSelectedQuestId = savedInstanceState.getInt("currentQuestId")
        }
        updateCurrentQuestVisible(view)
    }

    private fun getQuestArray() {
        if(userId == null) {
            Log.d("questList", "user is null, fail to create quest list")
            mQuestsArray = arrayOf()
            return
        }

        Log.d("questList", "user id: $userId")
        val questList = db.collection("Quests").get()
            .addOnSuccessListener { quests_list ->
                Log.d("questList", "quest lst: $quests_list")
                // TODO : should be userId(variable exist)
                db.collection("Users")
                    .document("QM15vQJQDNQkAdhUVyn6KV1YVQp2")
                    .collection("Quests")
                    .whereIn("status", listOf(0,1))
                    .get()
                    .addOnSuccessListener { user_quests ->
                        Log.d("questList", "success get status")
                        val inProgressQuestId = user_quests.documents.find {
                            it.data?.getValue("status") == 1
                        }?.id

                        val doesNotViewObjectsIds = user_quests.documents.map { it.id }
                        val toView = quests_list.filter { it.id !in doesNotViewObjectsIds }

                        mQuestsArray = Array(toView.size){
                            val toViewObj = toView[it]
                            val toViewObjData = toViewObj.data
                            QuestModel(
                                toViewObj.id.toInt(),
                                toViewObjData.getValue("Name").toString(),
                                "SaintP",
                                toViewObjData.getValue("Image").toString()
                            )
                        }
                        mQuestsList = QuestsArrayAdapter(
                            context!!,
                            R.layout.quest_list_item,
                            mQuestsArray
                        )
                        mListView.adapter = mQuestsList
                        Log.d("questList", "to ret: ${mQuestsArray.contentDeepToString()}")
                    }
                    .addOnFailureListener { ex ->
                        Log.d("questList", "Fail get user quests info: $ex")
                        // TODO: Sorry fail to load
                        mQuestsArray = arrayOf()
                    }
            }
            .addOnFailureListener{ ex ->
                Log.d("questList", "Fail get quests list with ex: $ex")
                // TODO: Sorry fail to load
                mQuestsArray = arrayOf()
            }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (view == null) {
            Log.d("CHECKER", "Empty view in fragment")
            return
        }
        mListView = view!!.findViewById(R.id.quests_list)

        val btnGiveUpQuest = view?.findViewById<TextView>(R.id.give_up_quest)
        btnGiveUpQuest?.setOnClickListener( giveUpQuestListener )

        getQuestArray()

        mListView.onItemClickListener = questSelectListener

        val infoButton = view!!.findViewById<ImageButton>(R.id.info_button)

        infoButton.setOnClickListener {
            startActivity(Intent(context, InfoActivity::class.java))
        }
    }

    private fun updateCurrentQuestVisible(view: View){
        if(mIsQuestSelected && mSelectedQuestId >= 0) {
            view.findViewById<LinearLayout>(R.id.current_quest_container).visibility = View.VISIBLE
            val imageView = view.findViewById<ImageView>(R.id.image_view)
            val selectedQuestModel = mQuestsArray[mSelectedQuestId]
            Picasso.get()
                .load(selectedQuestModel.imageUrl)
                .into(imageView)

            val nameView = view.findViewById<TextView>(R.id.name)
            nameView?.text = selectedQuestModel.name
            val locationView = view.findViewById<TextView>(R.id.location)
            locationView?.text = selectedQuestModel.location
        }
        else{
            view.findViewById<LinearLayout>(R.id.current_quest_container).visibility = View.GONE
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("isQuestSelected", mIsQuestSelected)
        outState.putInt("currentQuestId", mSelectedQuestId)
    }
}



