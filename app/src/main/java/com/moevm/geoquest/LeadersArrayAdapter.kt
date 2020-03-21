package com.moevm.geoquest

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.moevm.geoquest.models.LeaderModel

class LeadersArrayAdapter(context: Context, resource: Int, private val leaders: Array<LeaderModel>) :
    ArrayAdapter<LeaderModel>(context, resource, leaders){


    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater: LayoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val leader = leaders[position]
        val customView: View = inflater.inflate(R.layout.leader_list_item, parent, false)
        val placeView = customView.findViewById<TextView>(R.id.place)

        placeView.text = leader.place.toString()
        val nameView = customView.findViewById<TextView>(R.id.name)
        nameView.text = leader.name
        val timeView = customView.findViewById<TextView>(R.id.time)
        timeView.text = leader.time.toString()
        return customView
    }

}