package com.moevm.geoquest

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment

class QuestsFragment : Fragment() {
    internal lateinit var callback: OnHeadlineSelectedListener
    private lateinit var mListView: ListView
    private lateinit var mQuestsArray: Array<Int>
    private lateinit var mQuestsList: ArrayAdapter<Int>
    private var isCurrentQuestExist: Boolean = false
    private var currentQuestId: Int = -1

    fun setOnQuestSelectedListener(callback: OnHeadlineSelectedListener) {
        this.callback = callback
    }

    interface OnHeadlineSelectedListener {
        fun onQuestSelected(position: Long)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_quests, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("bottomNavView", "saved ist: $savedInstanceState")
        super.onViewCreated(view, savedInstanceState)

        if(savedInstanceState != null){
//            val isQuestSelected_ = savedInstanceState.getBoolean("isQuestSelected")
            if(savedInstanceState.getBoolean("isQuestSelected")) {
                val mCurrentQuestId = savedInstanceState.getInt("currentQuestId", currentQuestId)
                view.findViewById<ConstraintLayout>(R.id.current_quest_container)?.visibility = View.VISIBLE
                view.findViewById<TextView>(R.id.current_quest_name)?.text = mCurrentQuestId.toString()
            }

        }

        mListView = view.findViewById(R.id.quests_list)
        mQuestsArray = Array(10){it+1}

        val btnToLogin = view.findViewById<Button>(R.id.to_login)
        btnToLogin?.setOnClickListener {
            startActivity(Intent(context, LoginActivity::class.java))
        }

        val btnGiveUpQuest = view.findViewById<TextView>(R.id.give_up_quest)
        btnGiveUpQuest?.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(context)
            dialogBuilder.setMessage(getString(R.string.sure_give_up_quest_title))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.dialog_yes)) { dialog, id ->
                    view.findViewById<ConstraintLayout>(R.id.current_quest_container)?.visibility = View.GONE
                    view.findViewById<TextView>(R.id.current_quest_name)?.text = ""
                    currentQuestId = -1
                    isCurrentQuestExist = false
                }
                .setNegativeButton(getString(R.string.dialog_no)) { dialog, id -> dialog.cancel() }
            val alert = dialogBuilder.create()
            alert.setTitle(getString(R.string.sure_start_quest_title))
            alert.show()
        }

        mQuestsList = ArrayAdapter(context!!, android.R.layout.simple_list_item_1, mQuestsArray)
        mListView.adapter = mQuestsList

        mListView.setOnItemClickListener { parent, view_, position, item_id ->
            val dialogBuilder = AlertDialog.Builder(context)
            dialogBuilder.setMessage("questName")
                .setCancelable(false)
                .setPositiveButton(getString(R.string.dialog_yes)) { dialog, id ->
                    Log.d("Sending_data","Quest selected questList: $item_id")
                    currentQuestId = item_id.toInt()
                    view.findViewById<ConstraintLayout>(R.id.current_quest_container)?.visibility = View.VISIBLE
                    isCurrentQuestExist = true
                    view.findViewById<TextView>(R.id.current_quest_name)?.text = item_id.toString()
                    callback.onQuestSelected(item_id)
                }
                .setNegativeButton(getString(R.string.dialog_no)) { dialog, id -> dialog.cancel()
                }
            val alert = dialogBuilder.create()
            alert.setTitle(getString(R.string.sure_start_quest_title))
            alert.show()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("isQuestSelected", isCurrentQuestExist)
        outState.putInt("currentQuestId", currentQuestId)
    }


}



