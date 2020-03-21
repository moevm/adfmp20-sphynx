package com.moevm.geoquest

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.places.Place
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin


class MapFragment : Fragment(), OnMapReadyCallback {

    companion object{
        private const val KEY_CAMERA_POSITION = "camera_position"
        private val petrog = listOf(LatLng(59.96342065914428,30.26378779395241),LatLng(59.96352002665255,30.26376094486086),LatLng(59.96370396893354,30.26409621067465),LatLng(59.96375297548509,30.264285308777826),LatLng(59.963796273487,30.264304758180913),LatLng(59.96421263555884,30.266913541970666),LatLng(59.964338976073385,30.268186616736834),LatLng(59.96442221791231,30.26832072759629),LatLng(59.96446383821585,30.269111980615715),LatLng(59.9643154850936,30.269164284057013),LatLng(59.964303905428984,30.269441556859373),LatLng(59.9643245465163,30.269665185549897),LatLng(59.96441415878821,30.269935417014544),LatLng(59.9644463798995,30.272671270209734),LatLng(59.964537672877775,30.273679780799334),LatLng(59.96468266709094,30.274645376044695),LatLng(59.96501024427312,30.276608753043597),LatLng(59.96528412159931,30.277853287883218),LatLng(59.96559020904447,30.278926181632464),LatLng(59.96620775296271,30.28026728613991),LatLng(59.96648997455404,30.280924387050153),LatLng(59.96676115041205,30.281560065895874),LatLng(59.96692627134889,30.28159895945017),LatLng(59.96714374749906,30.281814880443097),LatLng(59.96751425785421,30.28215552098799),LatLng(59.96768138849514,30.2822829254628),LatLng(59.96784851907503,30.282335228991986),LatLng(59.968977394368466,30.282771493041952),LatLng(59.97004589729766,30.283136273467978),LatLng(59.97077610608221,30.283694157289),LatLng(59.971125100986995,30.284134042513763),LatLng(59.97143114123688,30.284681225860556),LatLng(59.97177207496007,30.28539468630756),LatLng(59.971958647310096,30.285826522467147),LatLng(59.97213447935343,30.286515856826743),LatLng(59.973127717663594,30.29133310421749),LatLng(59.97329019227076,30.291828489484935),LatLng(59.97358010344112,30.292600965681224),LatLng(59.974938357159616,30.296044922056346),LatLng(59.97569268166375,30.29869006768516),LatLng(59.97648393726972,30.30127923631901),LatLng(59.97719253619132,30.303725410940594),LatLng(59.97757903830233,30.305919457914776),LatLng(59.977769607337265,30.306544401643542),LatLng(59.97788099383258,30.30685687890481),LatLng(59.97799036625148,30.307066763909464),LatLng(59.978061155962976,30.307321911604596),LatLng(59.978201726657865,30.308252979757732),LatLng(59.97836276658662,30.308853792013082),LatLng(59.97840302426795,30.30902009153599),LatLng(59.97849092856581,30.309222595444954),LatLng(59.9785493090374,30.30956993945339),LatLng(59.978647263724774,30.31230311537022),LatLng(59.9787304657657,30.31551640177006),LatLng(59.97873583767347,30.316739489920142),LatLng(59.978676787053566,30.31758706712956),LatLng(59.97856674710178,30.31832467779059),LatLng(59.97838692050419,30.31904082441563),LatLng(59.9778581759099,30.32050531053776),LatLng(59.977428732461945,30.32151382112736),LatLng(59.975887887487964,30.32452964792733),LatLng(59.9746960972853,30.326439380745935),LatLng(59.97260230792339,30.32882118235116),LatLng(59.96766258798787,30.332772478386445),LatLng(59.96507849452711,30.334679217623666),LatLng(59.95878696373534,30.33537849098588),LatLng(59.958010285040984,30.33542509812443),LatLng(59.95774558090203,30.335463329011006),LatLng(59.956816338368164,30.335924668961567),LatLng(59.9556668346709,30.336954637223286),LatLng(59.954361508761814,30.33817489137377),LatLng(59.95412419047417,30.337914815962083),LatLng(59.95134371454494,30.325883880957527),LatLng(59.95113178062372,30.32471008625653),LatLng(59.95255004826491,30.323358252913025),LatLng(59.95288244145387,30.321170564604476),LatLng(59.952938848005395,30.31882471159939),LatLng(59.952885127482325,30.31733340338711),LatLng(59.95277903628443,30.315867492418615),LatLng(59.95253862801648,30.31450888457551),LatLng(59.951762348622616,30.311923235085153),LatLng(59.95114109191515,30.310676749608437),LatLng(59.95022434330853,30.309344493272867),LatLng(59.94962838030525,30.30840074363155),LatLng(59.94894027345159,30.307446887918335),LatLng(59.94892146901006,30.307226946779114),LatLng(59.94743741984157,30.304677698995526),LatLng(59.94741046229998,30.304441479231937),LatLng(59.947339271130325,30.304409292381393),LatLng(59.9473433004317,30.303765562560184),LatLng(59.947458818760886,30.3009487925049),LatLng(59.94780189439301,30.2938217933039),LatLng(59.94984352613802,30.286207405543706),LatLng(59.95208111754353,30.289206115222356),LatLng(59.95275246056096,30.289006191706175),LatLng(59.95364738744599,30.28705500989945),LatLng(59.953601725983134,30.28685116201432),LatLng(59.953792429322434,30.28636299997361),LatLng(59.953883751659745,30.28628253370316),LatLng(59.95546305109776,30.280966395435645),LatLng(59.95653126368728,30.27915929710315),LatLng(59.95981243283747,30.27580690776725))
        private const val DEFAULT_ZOOM = 12.0f
        private val DEFAULT_LOCATION = LatLng(59.93861111111111, 30.31388888888889)
        private const val REQUEST_PERMISSION_COARSE = 1
    }

    private var mLocationPermissionGranted = false
    private var mLastKnownLocation: Place? = null
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private var questArea: Polygon? = null
    private var easterEggPolyline: Polyline? = null

    private lateinit var mapFragment: SupportMapFragment
    private lateinit var gmap: GoogleMap
    private var questId: Long? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    private val onShowQuestAreaListener = View.OnClickListener {
        if (questArea == null){
            questArea = gmap.addPolygon(getQuestArea())
//            easterEggPolyline = gmap.addPolyline(getEasterEggPolyline())
        } else {
            questArea!!.remove()
//            easterEggPolyline!!.remove()
            questArea = null
//            easterEggPolyline = null
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        view?.findViewById<ImageButton>(R.id.area_button)?.setOnClickListener(onShowQuestAreaListener)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getLocationPermission()
    }

//    private fun getEasterEggPolyline(): PolylineOptions{
//        return PolylineOptions()
//            .width(1.0f)
//            .color(0xffff0000.toInt())
//            .addAll(listOf(
//                LatLng(59.972102, 30.322887),
//                LatLng(59.972225, 30.322915),
//                LatLng(59.972134, 30.323065),
//                LatLng(59.972239, 30.322974),
//                LatLng(59.972261, 30.323062),
//                LatLng(59.972239, 30.322974),
//                LatLng(59.9721865,30.3230195),
//                LatLng(59.9722085,30.3229315)
//            ))
//    }

    private fun getQuestArea(): PolygonOptions {
        //TODO get quest area from db
        return PolygonOptions()
            .strokeColor(Color.RED)
            .fillColor(ContextCompat.getColor(context!!, R.color.questArea))
            .addAll(petrog)
            .strokeWidth(4.0f)
     }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.d("CurrentLocation", "onMapReady")
        gmap = googleMap
        gmap.isMyLocationEnabled = true
        if (questId != null) {
            val saintP = DEFAULT_LOCATION
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
        questId = arguments?.getLong("questId")
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

