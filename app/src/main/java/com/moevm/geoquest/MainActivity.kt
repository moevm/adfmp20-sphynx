package com.moevm.geoquest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), QuestsFragment.OnHeadlineSelectedListener {

//    lateinit var questFragement: QuestsFragment
//    lateinit var mapFragement: MapFragment
//    lateinit var profileFragement: ProfileFragment
    lateinit var bottomView: BottomNavigationView

    private lateinit var stateHelper: FragmentStateHelper

    private val fragments = mutableMapOf<Int, Fragment>()

    private fun saveCurrentFragmentState() {
        fragments[bottomView.selectedItemId]?.let { oldFragment->
            stateHelper.saveState(oldFragment, bottomView.selectedItemId)
        }
    }

    private val navigationSelectionListener = BottomNavigationView.OnNavigationItemSelectedListener {
        val newFragment = fragments[it.itemId] ?: QuestsFragment()
        Log.d("bottomNavView", "fragment: $newFragment")
        fragments[it.itemId] = newFragment

        saveCurrentFragmentState()
        stateHelper.restoreState(newFragment, it.itemId)


        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, newFragment)
            .commitNowAllowingStateLoss()


        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomView = findViewById(R.id.bottom_navigation)
        stateHelper = FragmentStateHelper(supportFragmentManager)

        fragments[R.id.bottom_navigation_quests] = QuestsFragment()
        fragments[R.id.bottom_navigation_map] = MapFragment()
        fragments[R.id.bottom_navigation_profile] = ProfileFragment()

        if (fragments[R.id.bottom_navigation_quests] != null)
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragments[R.id.bottom_navigation_quests] as Fragment).commit()


        Log.d("Sending_data", "fragments: ${supportFragmentManager.fragments}")

        bottomView.setOnNavigationItemSelectedListener(navigationSelectionListener)
    }

    override fun onQuestSelected(position: Long) {
        Log.d("Sending_data", "Quest selected MainActivity: $position")
        var data = fragments[R.id.bottom_navigation_map]?.arguments
        if (data == null)
            data = Bundle()
        data.putLong("questId", position)

        fragments[R.id.bottom_navigation_map]?.arguments = data

        bottomView.selectedItemId = R.id.bottom_navigation_map
    }

    override fun onAttachFragment(fragment: Fragment) {
        Log.d("Sending_data", "Quest Fragment attached")
        if (fragment is QuestsFragment) {
            fragment.setOnQuestSelectedListener(this)
        }
    }

}