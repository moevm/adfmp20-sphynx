package com.moevm.geoquest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

//    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, QuestsFragment()).commit()

        bottomView.setOnNavigationItemSelectedListener {
            var selectedFragment: Fragment? = null
            when (it.itemId) {

                R.id.bottom_navigation_quests -> {
                    selectedFragment = QuestsFragment()
                    Toast.makeText(this, "quests", Toast.LENGTH_SHORT).show()
                }
                R.id.bottom_navigation_map -> {
                    selectedFragment = MapFragment()
                    Toast.makeText(this, "map", Toast.LENGTH_SHORT).show()
                }
                R.id.bottom_navigation_profile -> {
                    selectedFragment = ProfileFragment()
                    Toast.makeText(this, "profile", Toast.LENGTH_SHORT).show()
                }
            }
            return@setOnNavigationItemSelectedListener if(selectedFragment != null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment).commit()
                 true
            }
            else
                false
        }

    }


//    override fun onMapReady(googleMap: GoogleMap) {
//        mMap = googleMap
//
//        // Add a marker in Sydney and move the camera
//        val saintP = LatLng(59.9, 31.3)
//        mMap.addMarker(MarkerOptions().position(saintP).title("Marker in SaintP"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(saintP))
//    }
}