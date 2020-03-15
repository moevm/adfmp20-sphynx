package com.moevm.geoquest

import android.app.AlertDialog
import android.content.DialogInterface
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


class QuestsFragment : Fragment() {

    private lateinit var mListView: ListView
    private lateinit var mQuestsArray: Array<String>
    private lateinit var mQuestsList: ArrayAdapter<String>

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (view == null) {
            Log.d("CHECKER", "Empty view in fragment")
        }

        mListView = view!!.findViewById(R.id.quests_list)
        mQuestsArray = Array(10) { "$it" }

        val btnToLogin = view?.findViewById<Button>(R.id.to_login)
        btnToLogin?.setOnClickListener {
            startActivity(Intent(context, LoginActivity::class.java))
        }

        val btnGiveUpQuest = view?.findViewById<TextView>(R.id.give_up_quest)
        btnGiveUpQuest?.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(context)
            dialogBuilder.setMessage(getString(R.string.sure_give_up_quest_title))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.dialog_yes)) { dialog, id ->
                    Log.d("CHECKER", "Give up quest")
                    view?.findViewById<ConstraintLayout>(R.id.current_quest_container)?.visibility =
                        View.GONE
                    view?.findViewById<TextView>(R.id.current_quest_name)?.text = ""
                }
                .setNegativeButton(getString(R.string.dialog_no)) { dialog, id ->
                    dialog.cancel()
                    Log.d("CHECKER", "Don't give up quest")
                }
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
//                    view?.findViewById<ConstraintLayout>(R.id.current_quest_container)?.visibility = View.VISIBLE
//                    view?.findViewById<TextView>(R.id.current_quest_name)?.text = item_id.toString()
//                    TODO(Save visibility of selected quest)
                    val bnb = activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)
                    bnb?.selectedItemId = R.id.bottom_navigation_map
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
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_quests, container, false)
    }
}



