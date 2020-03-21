package com.moevm.geoquest

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.moevm.geoquest.models.QuestModel
import com.moevm.geoquest.models.QuestStatus


class ProfileFragment : Fragment() {

    private lateinit var mCompletedQuests: Array<QuestModel>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val exitButton = view!!.findViewById<ImageButton>(R.id.exit_button)

        mCompletedQuests  = Array(30) {
            QuestModel(
                4,
                "СФИНКСЫ",
                QuestStatus.completed,
                "Петрога",
                "https://scontent-waw1-1.cdninstagram.com/v/t51.2885-15/sh0.08/e35/s640x640/80039569_849419158844763_2465991202160182540_n.jpg?_nc_ht=scontent-waw1-1.cdninstagram.com&_nc_cat=103&_nc_ohc=G1av5QdM8a4AX_09XR3&oh=7f40849b9575c5f4ff193659c8b02768&oe=5ED888FE"
            )
        }

        val listView = view!!.findViewById<ListView>(R.id.quests_list)

        listView.adapter = QuestsArrayAdapter(
            context!!,
            R.layout.quest_list_item,
            mCompletedQuests
        )


        exitButton.setOnClickListener {
            startActivity(Intent(context, LoginActivity::class.java))
        }
    }
}

