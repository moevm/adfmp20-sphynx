package com.moevm.geoquest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), QuestsFragment.OnHeadlineSelectedListener {

    lateinit var questFragement: QuestsFragment
    lateinit var mapFragement: MapFragment
    lateinit var profileFragement: ProfileFragment
    lateinit var bottomView: BottomNavigationView

    companion object {
        private const val STATE_SAVE_STATE = "save_state"
        private const val STATE_KEEP_FRAGS = "keep_frags"
        private const val STATE_HELPER = "helper"
    }

    private lateinit var stateHelper: FragmentStateHelper

    private val fragments = mutableMapOf<Int, Fragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        questFragement = QuestsFragment()
        mapFragement = MapFragment()
        profileFragement = ProfileFragment()

        bottomView = findViewById(R.id.bottom_navigation)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, questFragement).commit()

        Log.d("Sending_data", "fragments: ${supportFragmentManager.fragments}")

        bottomView.setOnNavigationItemSelectedListener {
            var selectedFragment: Fragment? = null
            when (it.itemId) {

                R.id.bottom_navigation_quests -> {
                    selectedFragment = questFragement
                }
                R.id.bottom_navigation_map -> {
                    selectedFragment = mapFragement
                }
                R.id.bottom_navigation_profile -> {
                    selectedFragment = profileFragement
                }
            }
            return@setOnNavigationItemSelectedListener if (selectedFragment != null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment).commit()
                true
            } else
                false
        }
    }

    override fun onQuestSelected(position: Long) {
        Log.d("Sending_data", "Quest selected MainActivity: $position")
        var data = mapFragement.arguments
        if (data == null)
            data = Bundle()
        data.putLong("questId", position)

        mapFragement.arguments = data

        bottomView.selectedItemId = R.id.bottom_navigation_map
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.fragment_container, mapFragement).commit()
    }

    override fun onAttachFragment(fragment: Fragment) {
        Log.d("Sending_data", "Quest Fragment attached")

        if (fragment is QuestsFragment) {
            fragment.setOnQuestSelectedListener(this)
        }
    }

}