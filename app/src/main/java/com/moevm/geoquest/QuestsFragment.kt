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
import com.moevm.geoquest.models.QuestModel
import com.moevm.geoquest.models.QuestStatus
import com.squareup.picasso.Picasso


class QuestsFragment : Fragment() {
    private lateinit var callback: OnQuestActionListener
//    private lateinit var callbackQuestGiveUp: OnQuestActionListener
    private lateinit var mListView: ListView
    private lateinit var mQuestsArray: Array<QuestModel>
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

//    fun setOnQuestGiveUpListener(callback: OnQuestActionListener) {
//        this.callbackQuestGiveUp = callback
//    }

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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (view == null) {
            Log.d("CHECKER", "Empty view in fragment")
        }

        mListView = view!!.findViewById(R.id.quests_list)
        mQuestsArray = Array(30) {
            QuestModel(
                it+1,
                "СФИНКСЫ${it+1}",
                QuestStatus.completed,
                "Петрога",
                "https://scontent-waw1-1.cdninstagram.com/v/t51.2885-15/sh0.08/e35/s640x640/80039569_849419158844763_2465991202160182540_n.jpg?_nc_ht=scontent-waw1-1.cdninstagram.com&_nc_cat=103&_nc_ohc=G1av5QdM8a4AX_09XR3&oh=7f40849b9575c5f4ff193659c8b02768&oe=5ED888FE"
            )
        }

        val btnGiveUpQuest = view?.findViewById<TextView>(R.id.give_up_quest)
        btnGiveUpQuest?.setOnClickListener( giveUpQuestListener )

        mQuestsList = QuestsArrayAdapter(
            context!!,
            R.layout.quest_list_item,
            mQuestsArray
        )
        mListView.adapter = mQuestsList

        mListView.onItemClickListener = questSelectListener

        val infoButton = view!!.findViewById<ImageButton>(R.id.info_button)

        infoButton.setOnClickListener {
            startActivity(Intent(context, InfoActivity::class.java))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_quests, container, false)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("Sending_data" , "saved inst: $savedInstanceState")
        if(savedInstanceState != null){
            mIsQuestSelected = savedInstanceState.getBoolean("isQuestSelected")
            mSelectedQuestId = savedInstanceState.getInt("currentQuestId")
        }
        updateCurrentQuestVisible(view)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("isQuestSelected", mIsQuestSelected)
        outState.putInt("currentQuestId", mSelectedQuestId)
    }
}



