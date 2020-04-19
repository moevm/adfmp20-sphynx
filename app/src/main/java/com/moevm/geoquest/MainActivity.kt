package com.moevm.geoquest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

open class FragmentUpdateUI: Fragment(){
    open fun updateUI(){}
}

class MainActivity : AppCompatActivity(), QuestsFragment.QuestsActionListener {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    lateinit var bottomView: BottomNavigationView
    private val fragments = mutableMapOf<Int, FragmentUpdateUI>()

    private val navigationSelectionListener =
        BottomNavigationView.OnNavigationItemSelectedListener {
            val newFragment = fragments[it.itemId]!!
            if(it.itemId != bottomView.selectedItemId) {
                supportFragmentManager.beginTransaction().apply {
                    show(newFragment)
                    hide(fragments[bottomView.selectedItemId]!!)
                    commit()
                }
                newFragment.updateUI()
            }
            true
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            return
        }
        fragments[R.id.bottom_navigation_quests] = QuestsFragment(auth.currentUser?.uid)
        fragments[R.id.bottom_navigation_map] =
            MapFragment(auth.currentUser?.uid, auth.currentUser?.displayName ?: "")
        fragments[R.id.bottom_navigation_profile] = ProfileFragment(auth.currentUser?.uid)

        fragments.forEach {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, it.value).hide(it.value).commit()
        }

        supportFragmentManager.beginTransaction().show(fragments[R.id.bottom_navigation_quests]!!)
            .commit()
        bottomView = findViewById(R.id.bottom_navigation)
        bottomView.setOnNavigationItemSelectedListener(navigationSelectionListener)
    }

    override fun onQuestSelected(questId: Int) {
        Log.d("Sending_data", "onQuestSelected")
        (fragments[R.id.bottom_navigation_map] as MapFragment).startQuest(questId)
        bottomView.selectedItemId = R.id.bottom_navigation_map
    }

    override fun onQuestGiveUp() {
        (fragments[R.id.bottom_navigation_map] as MapFragment).questGiveUp()
    }

    override fun onAttachFragment(fragment: Fragment) {
        if (fragment is QuestsFragment) {
            fragment.setOnQuestsActionListener(this)
        }
    }
}