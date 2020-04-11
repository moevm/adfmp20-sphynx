package com.moevm.geoquest


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.moevm.geoquest.models.QuestModel
import com.squareup.picasso.Picasso


class QuestsArrayAdapter(context: Context, resource: Int, private val quests: ArrayList<QuestModel>) :
    ArrayAdapter<QuestModel>(context, resource, quests) {

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater: LayoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val quest = quests[position]
        val customView: View = inflater.inflate(R.layout.quest_list_item, parent, false)
        val imageView = customView.findViewById<ImageView>(R.id.image_view)

        Picasso.get()
            .load(quest.imageUrl)
            .into(imageView)

        val nameView = customView.findViewById<TextView>(R.id.name)
        nameView.text = quest.name
        val locationView = customView.findViewById<TextView>(R.id.location)
        locationView.text = quest.location
        return customView
    }
}