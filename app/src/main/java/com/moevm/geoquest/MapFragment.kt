package com.moevm.geoquest

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.moevm.geoquest.models.AttractionModel
import com.moevm.geoquest.models.AttractionStatus
import com.moevm.geoquest.models.QuestStatus
import java.util.*


class MapFragment(private val userId: String?, private val userName: String) : FragmentUpdateUI(), OnMapReadyCallback {

    companion object {
        private const val DEFAULT_ZOOM = 12.0f
        private const val REQUEST_PERMISSION_COARSE = 1
    }

    private val db = Firebase.firestore

    private lateinit var mapFragment: SupportMapFragment
    private lateinit var gmap: GoogleMap

    private var mLocationPermissionGranted = false
    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    private var questArea: List<LatLng>? = null
    private var drawableQuestArea: Polygon? = null
    private var foundedQuestsPoints: MutableList<Marker>? = null

    private var timer: Timer = Timer()
    private var timerValue: Int = -1

    private var mQuestId: Int = -1
    private var questAttractionsCount: Int = 0
    private var questFoundedAttractionsCount: Int = 0
    private var questProgress: QuestProgress = QuestProgress()


    fun startQuest(questId: Int){
        Log.d("Sending_data", "start quest")
        if(foundedQuestsPoints != null){
            foundedQuestsPoints?.forEach{
                it.remove()
            }
        }
        foundedQuestsPoints = mutableListOf()
        mQuestId = questId
        fillQuestInfo(questId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    private fun checkCurrentQuestAlreadySelected(){
        db.collection("Users")
            .document(userId!!)
            .collection("Quests")
            .whereEqualTo("status", QuestStatus.InProgress)
            .get()
            .addOnSuccessListener { current_user_quest ->
                val currentQuest = current_user_quest.documents
                if (currentQuest.size == 1) {
                    db.collection("Quests").document(currentQuest[0].id).get()
                        .addOnSuccessListener {
                            val questId = it?.id?.toIntOrNull()
                            if(questId != null)
                            startQuest(questId)
                        }
                        .addOnFailureListener {
                            // TODO: Sorry fail to load
                        }
                }
            }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.d("Sending_data", "recreate map")
        super.onActivityCreated(savedInstanceState)
        checkCurrentQuestAlreadySelected()
        view?.findViewById<ImageButton>(R.id.area_button)
            ?.setOnClickListener(onShowQuestAreaListener)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
        mLocationPermissionGranted = checkLocationPermission()
        if (mLocationPermissionGranted) {
            getLastLocation()
            initMapAsync()
        } else {
            requestLocationPermission()
        }
    }

    private val onShowQuestAreaListener = View.OnClickListener {
        drawableQuestArea = if (questArea != null && drawableQuestArea == null) {
            gmap.addPolygon(getQuestArea())
        } else {
            drawableQuestArea?.remove()
            null
        }
    }

    private fun fillQuestArea(areaReference: DocumentReference) {
        areaReference.get()
            .addOnSuccessListener { result ->
                val points = result.data?.getValue("Points") as ArrayList<GeoPoint>
                questArea = List(points.size) {
                    LatLng(points[it].latitude, points[it].longitude)
                }
            }
    }

    private fun fillQuestAttractions(attractionList: ArrayList<DocumentReference>) {
        val questAttractions = mutableListOf<AttractionModel>()
        attractionList.forEach { attraction ->
            attraction.get()
                .addOnSuccessListener { attractionInfo ->
                    val attrName = attractionInfo.data?.getValue("Name") as String
                    val latlng = attractionInfo.data?.getValue("Coordinates") as GeoPoint
                    val trigger = attractionInfo.data?.getValue("Trigger-zone").toString().toFloat()
                    questAttractions.add(
                        AttractionModel(
                            attrName,
                            LatLng(latlng.latitude, latlng.longitude),
                            trigger
                        )
                    )
                    if (questAttractions.size == attractionList.size) {
                        questFoundedAttractionsCount = 0
                        questAttractionsCount = questAttractions.size
                        Log.d("Sending_data","questAttractionsCount: $questAttractionsCount")
                        updateCardsInfoProgress("Loading", "#FFFFFFFF", true)
                        questProgress.setupQuest(questAttractions)
                    }
                }
                .addOnFailureListener {
                    Log.d("quest_action", "Fail get info about attraction: $attraction")
                }
        }

    }

    private fun fillQuestInfo(questId: Int) {
        db.collection("Quests")
            .document(questId.toString())
            .get()
            .addOnSuccessListener { questInfo ->
                startTimer()
                val attractions = questInfo.data?.get("Attractions")
                        as ArrayList<DocumentReference>
                fillQuestAttractions(attractions)
                val areas = questInfo.data?.get("Areas")
                        as ArrayList<DocumentReference>
                if (areas.size != 1) {
                    Log.d("quest_action", "more than 1 quest area reference")
                }
                fillQuestArea(areas[0])
            }
            .addOnFailureListener {
                // TODO sorry fail to load
                Log.d("quest_action", "fail to load quest info")
            }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkLocationPermission()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener { _ ->
                    requestNewLocationData()
                }
            } else {
                Toast.makeText(context, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            permissionInfoAndRequest()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 5000
        mLocationRequest.fastestInterval = 1000

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private fun startTimer() {
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                timerValue += 1
                mHandler.obtainMessage(1).sendToTarget()
            }
        }, 0, 60_000)
    }

    var mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message?) {
            view?.findViewById<TextView>(R.id.time_statistics)?.text = timeToHoursMinutes(timerValue)
        }
    }

    private fun questCompleted() {
        if (mQuestId >= 0) {
            db.collection("Users")
                .document(userId!!)
                .collection("Quests")
                .document(mQuestId.toString())
                .set( mapOf("status" to QuestStatus.Completed) )
                .addOnSuccessListener {
                    drawableQuestArea?.remove()
                    drawableQuestArea = null
                    questArea = null

                    Log.d("questProgress", "Completed. user: $userId, quest: $mQuestId")
                }
                .addOnFailureListener {
                    Log.d("questProgress", "Fail to save complete status. user: $userId, quest: $mQuestId")
                    //TODO: No internet connection
                }
            db.collection("Quests")
                .document(mQuestId.toString())
                .collection("Records")
                .document(userId)
                .set( mapOf(
                    "Time" to timerValue,
                    "Username" to userName
                ) )
                .addOnSuccessListener {
                    mQuestId = -1
                    Log.d("questProgress", "Completed. Saved results to DB: success")
                }
                .addOnFailureListener{
                    Log.d("questProgress", "Completed. Saved results to DB: fail")
                }
            db.collection("Users")
                .document(userId)
                .get()
                .addOnSuccessListener {
                    val keys = it.data?.keys
                    if (keys != null && "Statistic" in keys) {
                        val userStat = it.data?.getValue("Statistic") as Map<String, *>
                        val pointsFounded = userStat.getValue("Points").toString().toInt()
                        val questsCompleted = userStat.getValue("Quests").toString().toInt()
                        val questsTime = userStat.getValue("Time").toString().toInt()
                        val travelledDistance = userStat.getValue("Distance").toString().toDouble()
                        db.collection("Users")
                            .document(userId)
                            .set(
                                mapOf(
                                    "Statistic" to mapOf(
                                        "Points" to pointsFounded + questProgress.getQuestAttractionStartCount(),
                                        "Quests" to questsCompleted + 1,
                                        "Time" to questsTime + timerValue,
                                        "Distance" to travelledDistance + questProgress.getTravelledDistance()
                                    )
                                )
                            )
                            .addOnSuccessListener {
                                timer.cancel()
                                timer = Timer()
                                timerValue = -1
                                Log.d("quest_action", "update statistic successful")
                            }
                            .addOnFailureListener {
                                Log.d("quest_action", "update statistic failure")
                            }
                    } else {
                        db.collection("Users")
                            .document(userId)
                            .set(
                                mapOf(
                                    "Statistic" to mapOf(
                                        "Points" to questProgress.getQuestAttractionStartCount(),
                                        "Quests" to 1,
                                        "Time" to timerValue,
                                        "Distance" to questProgress.getTravelledDistance()
                                    )

                                )
                            )
                            .addOnSuccessListener {
                                Log.d("quest_action", "update statistic successful")
                            }
                            .addOnFailureListener {
                                Log.d("quest_action", "update statistic failure")
                            }
                    }
                }
                .addOnFailureListener {
                    Log.d("quest_action", "user stat fail")
                    //TODO: No internet connection
                }
        }
        Log.d("TimerTag", "timer cancel")
        view?.findViewById<ImageButton>(R.id.area_button)?.visibility = View.GONE
        view?.findViewById<TextView>(R.id.points_statistics)
            ?.text = getString(R.string.quest_progress_points, questAttractionsCount, questAttractionsCount)
    }

    private fun updateCardsInfoProgress(distanceCardText:String,
                                        distanceCardColor: String,
                                        needFoundedCountUpdate: Boolean=false) {
        view?.findViewById<ImageButton>(R.id.area_button)?.visibility = View.VISIBLE
        view?.findViewById<CardView>(R.id.statistics)?.visibility = View.VISIBLE
        view?.findViewById<CardView>(R.id.distance_card_view)?.visibility = View.VISIBLE
        view?.findViewById<CardView>(R.id.distance_card_view)
            ?.setCardBackgroundColor(Color.parseColor(distanceCardColor))
        view?.findViewById<TextView>(R.id.status)?.text = distanceCardText
        if(needFoundedCountUpdate)
        {
            questFoundedAttractionsCount++

            view?.findViewById<TextView>(R.id.points_statistics)
                ?.text = getString(R.string.quest_progress_points,
            questFoundedAttractionsCount/4, questAttractionsCount)
        }
    }

    private fun addLastFoundedQuestPoint(){
        val lastFounded = questProgress.getLastFounded()
        if (lastFounded != null) {
            foundedQuestsPoints?.add(
                gmap.addMarker(
                    MarkerOptions()
                        .position(lastFounded.coordinates)
                        .title(lastFounded.name)
                )
            )
        }
    }

    private fun updateCardInfo(result: AttractionStatus) {
        //TODO quest completed: congratulation, prob. statistic
        when (result) {
            AttractionStatus.Success -> {
                addLastFoundedQuestPoint()
                updateCardsInfoProgress("Точка найдена", "#E100FF19", true)
            }
            AttractionStatus.Colder -> {
                updateCardsInfoProgress("Холоднее", "#D457E1FF")
            }
            AttractionStatus.Warmer -> {
                updateCardsInfoProgress("Теплее", "#E1FFA800")
            }
            AttractionStatus.QuestCompleted -> {
                view?.findViewById<CardView>(R.id.distance_card_view)?.visibility = View.GONE
                Toast.makeText(context, "Поздравляем! Вы прошли квест. " +
                            "Затраченное время: " + timeToHoursMinutes(timerValue) +
                            "Найдено точек: $questAttractionsCount", Toast.LENGTH_LONG).show()
                addLastFoundedQuestPoint()
                questCompleted()
            }
            AttractionStatus.Nothing -> {
                view?.findViewById<CardView>(R.id.distance_card_view)?.visibility = View.GONE
                view?.findViewById<CardView>(R.id.statistics)?.visibility = View.GONE
            }
        }

    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            super.onLocationResult(locationResult)
            if (locationResult == null)
                return
            val mLastLocation: Location = locationResult.lastLocation
            val result =
                questProgress.checkDistanceToObject(mLastLocation)
            updateCardInfo(result)
        }
    }

    private fun getQuestArea(): PolygonOptions {
        //TODO get quest area from db
        Log.d("quest_action", "questArea(onClick): $questArea")
        return PolygonOptions()
            .strokeColor(Color.RED)
            .fillColor(ContextCompat.getColor(context!!, R.color.questArea))
            .addAll(questArea)
            .strokeWidth(4.0f)
    }

    fun questGiveUp(){
        Log.d("Sending_data", "MapFrag quest give up")
        timer.cancel()
        timer = Timer()
        timerValue = -1
        questProgress = QuestProgress()
        drawableQuestArea?.remove()
        drawableQuestArea = null
        questArea = null
        if(foundedQuestsPoints != null){
            foundedQuestsPoints?.forEach {
                it.remove()
            }
            foundedQuestsPoints = null
        }
        view?.findViewById<ImageButton>(R.id.area_button)?.visibility = View.GONE
        view?.findViewById<CardView>(R.id.distance_card_view)?.visibility = View.GONE
        view?.findViewById<CardView>(R.id.statistics)?.visibility = View.GONE
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.d("currentLocation", "onMapReady")
        gmap = googleMap.apply {
            uiSettings.isMyLocationButtonEnabled = false
            uiSettings.isCompassEnabled = false
        }

        if (mLocationPermissionGranted) {
            gmap.isMyLocationEnabled = true
            Log.d("currentLocation", "arguments: $arguments")
        }
    }

    private fun initMapAsync() {
        val v: Fragment = childFragmentManager.findFragmentById(R.id.mapFragment) ?: return
        mapFragment = v as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun checkLocationPermission(): Boolean {
        return ActivityCompat
            .checkSelfPermission(activity!!, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED && ActivityCompat
            .checkSelfPermission(activity!!, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        this.requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ),
            REQUEST_PERMISSION_COARSE
        )
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d("LocationPermissions", "on request result")
        when (requestCode) {
            REQUEST_PERMISSION_COARSE -> {
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    Log.d("LocationPermissions", "permissions granted")
                    mLocationPermissionGranted = true
                }
                if (!this::gmap.isInitialized) {
                    getLastLocation()
                    initMapAsync()
                }
            }
        }
    }

    private fun permissionInfoAndRequest() {
        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder
            .setMessage(getString(R.string.request_location_permissions))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.dialog_yes)) { _, _ ->
                requestLocationPermission()
            }
            .setNegativeButton(getString(R.string.dialog_no)) { dialog, _ ->
                dialog.cancel()
            }
        val alert = dialogBuilder.create()
        alert.setTitle(getString(R.string.request_location_permissions_title))
        alert.show()
    }

}

operator fun LatLng.plus(latLng: LatLng): LatLng? {
    return LatLng(this.latitude + latLng.latitude, this.longitude + latLng.longitude)
}


