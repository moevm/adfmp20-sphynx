package com.moevm.geoquest

import android.Manifest
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.places.Place
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

    companion object{
        private const val KEY_CAMERA_POSITION = "camera_position"

        private const val DEFAULT_ZOOM = 12.0f
        private val DEFAULT_LOCATION = LatLng(59.93861111111111, 30.31388888888889)
        private const val REQUEST_PERMISSION_COARSE = 1
    }

    private var mLocationPermissionGranted = false
    private var mLastKnownLocation: Place? = null
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient

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
        getLocationPermission()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.d("CurrentLocation", "onMapReady")
        gmap = googleMap
        gmap.isMyLocationEnabled = true
        val rectOptions = PolygonOptions()
            .strokeColor(Color.RED)
            .fillColor(0x7f0000ff)

        val saintP = DEFAULT_LOCATION
        Log.d("Sending_data", "quest id: $questId")
        if (questId != null) {
            val newStepId = 10
            val step = 360/newStepId

                for (i in 0..newStepId) {
                    val shift = LatLng(
                        0.005 * cos(i * step * PI / 180),
                        0.01 * sin(i * step * PI / 180))
                    Log.d("Sending_data", "steps: $i, shift: $shift")
                    rectOptions.add( saintP + shift )
                }

            googleMap.addPolygon(rectOptions)
            googleMap.addMarker(MarkerOptions().position(saintP).title("Marker in SaintP"))
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(saintP, 12.0f))
        }
    }

    private fun drawMyLocation(){
        Log.d("CurrentLocation", "Draw my location called")
        val v: Fragment = childFragmentManager.findFragmentById(R.id.mapFragment) ?: return
        mapFragment = v as SupportMapFragment
        mapFragment.getMapAsync(this)
        Log.d("CurrentLocation", "get map async called")
        questId = arguments?.getLong("questId");
    }


    private fun getLocationPermission(){
        //TODO check permissions in fragment
        if (activity == null)
            return
        val thisActivity = activity as Activity
        val permissionAccessCoarseLocationApproved = ActivityCompat
            .checkSelfPermission(thisActivity, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
        val permissionAccessFineLocationApproved = ActivityCompat
            .checkSelfPermission(thisActivity, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
        if (permissionAccessCoarseLocationApproved && permissionAccessFineLocationApproved) {
//            ACCESS_BACKGROUND_LOCATION
            drawMyLocation()
        } else {
            // App doesn't have access to the device's location at all. Make full request
            // for permission.
            this.requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                REQUEST_PERMISSION_COARSE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d("LocationPermissions", "on request result")
        when(requestCode){
            REQUEST_PERMISSION_COARSE -> {
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    Log.d("LocationPermissions", "permissions granted")
                    mLocationPermissionGranted = true
                } else {
                    Log.e("LocationPermissions", "access coarse & background location wasn't granted")
                }
                return
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(KEY_CAMERA_POSITION, gmap.cameraPosition)
        super.onSaveInstanceState(outState)
    }

}

private operator fun LatLng.plus(latLng: LatLng): LatLng? {
    return LatLng(this.latitude+latLng.latitude, this.longitude + latLng.longitude)
}

