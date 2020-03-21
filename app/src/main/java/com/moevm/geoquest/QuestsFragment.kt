package com.moevm.geoquest

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.moevm.geoquest.models.QuestModel
import com.moevm.geoquest.models.QuestStatus
import com.squareup.picasso.Picasso


class QuestsFragment : Fragment() {
    internal lateinit var callback: OnHeadlineSelectedListener
    private lateinit var mListView: ListView
    private lateinit var mQuestsArray: Array<QuestModel>
    private lateinit var mSelectedQuest: QuestModel
    private lateinit var mQuestsList: ArrayAdapter<QuestModel>

    mListView = view!!.findViewById(R.id.quests_list)
    mQuestsArray = Array(30) {
        QuestModel(
            4,
            "СФИНКСЫ",
            QuestStatus.completed,
            "Петрога",
            "https://scontent-waw1-1.cdninstagram.com/v/t51.2885-15/sh0.08/e35/s640x640/80039569_849419158844763_2465991202160182540_n.jpg?_nc_ht=scontent-waw1-1.cdninstagram.com&_nc_cat=103&_nc_ohc=G1av5QdM8a4AX_09XR3&oh=7f40849b9575c5f4ff193659c8b02768&oe=5ED888FE"
        )
    }

    interface OnHeadlineSelectedListener {
        fun onQuestSelected(position: Long)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (view == null) {
            Log.d("CHECKER", "Empty view in fragment")
        }

        mListView = view!!.findViewById(R.id.quests_list)
        mQuestsArray = Array(30) {
            QuestModel(
                4,
                "СФИНКСЫ",
                QuestStatus.completed,
                "Петрога",
                "https://scontent-waw1-1.cdninstagram.com/v/t51.2885-15/sh0.08/e35/s640x640/80039569_849419158844763_2465991202160182540_n.jpg?_nc_ht=scontent-waw1-1.cdninstagram.com&_nc_cat=103&_nc_ohc=G1av5QdM8a4AX_09XR3&oh=7f40849b9575c5f4ff193659c8b02768&oe=5ED888FE"
            )
        }


        val btnToLogin = view?.findViewById<Button>(R.id.to_login)
        btnToLogin?.setOnClickListener {
            startActivity(Intent(context, LoginActivity::class.java))
        }

        mSelectedQuest = mQuestsArray.first()

        val btnGiveUpQuest = view?.findViewById<TextView>(R.id.give_up_quest)
        btnGiveUpQuest?.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(context)
            dialogBuilder.setMessage(getString(R.string.sure_give_up_quest_title))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.dialog_yes)) { dialog, id ->
                    view?.findViewById<ConstraintLayout>(R.id.current_quest_container)?.visibility = View.GONE
                    view?.findViewById<TextView>(R.id.current_quest_name)?.text = ""
                    currentQuestId = -1
                    isCurrentQuestExist = false
                }
                .setNegativeButton(getString(R.string.dialog_no)) { dialog, id ->
                    dialog.cancel()
                    Log.d("CHECKER", "Don't give up quest")
                }
            val alert = dialogBuilder.create()
            alert.setTitle(getString(R.string.sure_start_quest_title))
            alert.show()
        }


        mQuestsList = QuestsArrayAdapter(
            context!!,
            R.layout.quest_list_item,
            mQuestsArray
        )
        mListView.adapter = mQuestsList

        mListView.setOnItemClickListener { parent, view_, position, item_id ->
            val dialogBuilder = AlertDialog.Builder(context)
            dialogBuilder.setMessage("questName")
                .setCancelable(false)
                .setPositiveButton(getString(R.string.dialog_yes)) { dialog, id ->
                    Log.d("Sending_data","Quest selected questList: $item_id")
                    currentQuestId = item_id.toInt()
                    view?.findViewById<ConstraintLayout>(R.id.current_quest_container)?.visibility = View.VISIBLE
                    isCurrentQuestExist = true
                    view?.findViewById<TextView>(R.id.current_quest_name)?.text = item_id.toString()
                    callback.onQuestSelected(item_id)
                }
                .setNegativeButton(getString(R.string.dialog_no)) { dialog, id ->
                    dialog.cancel()
                }
            val alert = dialogBuilder.create()
            alert.setTitle(getString(R.string.sure_start_quest_title))
            alert.show()
        }

        val infoButton = view!!.findViewById<ImageButton>(R.id.info_button)

        infoButton.setOnClickListener {
            startActivity(Intent(context, InfoActivity::class.java))
        }

        val imageView = view!!.findViewById<ImageView>(R.id.image_view)

        Picasso.get()
            .load(mSelectedQuest.imageUrl)
            .into(imageView)

        val nameView = view!!.findViewById<TextView>(R.id.name)
        nameView.text = mSelectedQuest.name
        val locationView = view!!.findViewById<TextView>(R.id.location)
        locationView.text = mSelectedQuest.location
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_quests, container, false)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("isQuestSelected", isCurrentQuestExist)
        outState.putInt("currentQuestId", currentQuestId)
    }
}



