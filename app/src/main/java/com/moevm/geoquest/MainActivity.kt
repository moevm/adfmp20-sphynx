package com.moevm.geoquest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity(), QuestsFragment.OnQuestActionListener {

    lateinit var auth: FirebaseAuth
    lateinit var bottomView: BottomNavigationView
    private lateinit var stateHelper: FragmentStateHelper
    private val fragments = mutableMapOf<Int, Fragment>()

    private val navigationSelectionListener =
        BottomNavigationView.OnNavigationItemSelectedListener {
            val newFragment = fragments[it.itemId]!!
            stateHelper.restoreState(newFragment, it.itemId)

            supportFragmentManager.beginTransaction().apply {
                show(newFragment)
                hide(fragments[bottomView.selectedItemId]!!)
                commit()
            }

            true
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null)
            startActivity(Intent(this, LoginActivity::class.java))
        bottomView = findViewById(R.id.bottom_navigation)
        stateHelper = FragmentStateHelper(supportFragmentManager)

        fragments[R.id.bottom_navigation_quests] = QuestsFragment()
        fragments[R.id.bottom_navigation_map] = MapFragment()
        fragments[R.id.bottom_navigation_profile] = ProfileFragment()

        fragments.forEach {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, it.value).hide(it.value).commit()
        }

        supportFragmentManager.beginTransaction().show(fragments[R.id.bottom_navigation_quests]!!)
            .commit()

        Log.d("Sending_data", "fragments: ${supportFragmentManager.fragments}")

        bottomView.setOnNavigationItemSelectedListener(navigationSelectionListener)
    }

    override fun onQuestSelected(position: Int) {
        Log.d("Sending_data", "Quest selected MainActivity: $position")
        var data = fragments[R.id.bottom_navigation_map]?.arguments
        if (data == null)
            data = Bundle()
        data.putInt("questId", position)
        data.putBoolean("needCameraChange", true)
        fragments[R.id.bottom_navigation_map]?.arguments = data

        bottomView.selectedItemId = R.id.bottom_navigation_map
    }

    override fun onQuestGiveUp() {
        var data = fragments[R.id.bottom_navigation_map]?.arguments
        if (data == null)
            data = Bundle()
        data.putInt("questId", -1)
    }

    override fun onAttachFragment(fragment: Fragment) {
        Log.d("Sending_data", "Quest Fragment attached")
        if (fragment is QuestsFragment) {
            fragment.setOnQuestActionListener(this)
        }
    }

}