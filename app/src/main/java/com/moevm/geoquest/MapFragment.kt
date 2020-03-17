package com.moevm.geoquest

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolygonOptions

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin


class MapFragment : Fragment(), OnMapReadyCallback {
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var gmap: GoogleMap
    private var questId: Long? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val v: Fragment = childFragmentManager.findFragmentById(R.id.mapFragment) ?: return
        mapFragment = v as SupportMapFragment
        mapFragment.getMapAsync(this)
        questId = arguments?.getLong("questId");
        Log.d("Sending_data", "args: $questId")
    }

    override fun onMapReady(googleMap: GoogleMap) {
        gmap = googleMap
        val rectOptions = PolygonOptions()
            .strokeColor(Color.RED)
            .fillColor(0x7f0000ff)

        val saintP = LatLng(59.93861111111111, 30.31388888888889)

        if (questId != null) {
            val step = questId ?: 1

                for (i in 0..(360 / step)) {
                    rectOptions.add(
                        saintP + LatLng(
                            0.005 * cos(i * step * PI / 180),
                            0.01 * sin(i * step * PI / 180)
                        )
                    )
                }

            googleMap.addPolygon(rectOptions)

            googleMap.addMarker(MarkerOptions().position(saintP).title("Marker in SaintP"))
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(saintP, 12.0f))
        }
    }
}

private operator fun LatLng.plus(latLng: LatLng): LatLng? {
    return LatLng(this.latitude+latLng.latitude, this.longitude + latLng.longitude)
}

