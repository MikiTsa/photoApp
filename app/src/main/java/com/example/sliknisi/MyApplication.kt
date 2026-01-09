package com.example.sliknisi

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.lib.DataGenerator
import com.example.lib.Landmark
import com.example.lib.Photo
import com.example.sliknisi.repository.FirebaseRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

const val MY_SP_FILE_NAME = "sliknisi_shared.data"

class MyApplication : Application() {

    val landmarksList = ArrayList<Landmark>()
    val photosList = ArrayList<Photo>()

    private lateinit var firebaseRepo: FirebaseRepository
    private lateinit var sharedPref: SharedPreferences
    private val applicationScope = CoroutineScope(Dispatchers.Main)

    var onDataLoadedCallback: (() -> Unit)? = null
    var isDataLoaded = false

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "MyApplication onCreate() - Initializing app")

        firebaseRepo = FirebaseRepository()
        initSharedPreferences()

        if (!containsID()) {
            val newUUID = UUID.randomUUID().toString().replace("-", "")
            saveID(newUUID)
            Log.d(TAG, "First launch - Generated new UUID: $newUUID")
        } else {
            Log.d(TAG, "App UUID already exists: ${getID()}")
        }

        applicationScope.launch {
            loadFromFirebase()
            isDataLoaded = true
            onDataLoadedCallback?.invoke()
        }
    }

    private suspend fun loadFromFirebase() {
        try {
            val loadedLandmarks = firebaseRepo.getAllLandmarks()
            landmarksList.clear()
            landmarksList.addAll(loadedLandmarks)

            Log.d(TAG, "Loaded ${landmarksList.size} landmarks from Firebase")

            if (landmarksList.isEmpty()) {
                Log.d(TAG, "No data in Firebase, generating initial landmarks...")
                val initialLandmarks = DataGenerator.generateRealMariborLandmarks()
                firebaseRepo.seedInitialData(initialLandmarks)
                landmarksList.addAll(initialLandmarks)
                Log.d(TAG, "Seeded ${landmarksList.size} landmarks to Firebase")
            }

            val loadedPhotos = firebaseRepo.getAllPhotos()
            photosList.clear()
            photosList.addAll(loadedPhotos)
            Log.d(TAG, "Loaded ${photosList.size} photos from Firebase")

        } catch (e: Exception) {
            Log.e(TAG, "Error loading from Firebase", e)
        }
    }

    fun addLandmark(landmark: Landmark) {
        landmarksList.add(landmark)
        applicationScope.launch {
            firebaseRepo.saveLandmark(landmark)
        }
        Log.d(TAG, "Added landmark: ${landmark.name}")
    }

    fun deleteLandmark(id: String) {
        val removed = landmarksList.removeIf { it.id == id }
        if (removed) {
            applicationScope.launch {
                firebaseRepo.deleteLandmark(id)
            }
            Log.d(TAG, "Deleted landmark with ID: $id")
        }
    }

    fun updateLandmark(updatedLandmark: Landmark) {
        val index = landmarksList.indexOfFirst { it.id == updatedLandmark.id }
        if (index != -1) {
            landmarksList[index] = updatedLandmark
            applicationScope.launch {
                firebaseRepo.updateLandmark(updatedLandmark)
            }
            Log.d(TAG, "Updated landmark: ${updatedLandmark.id}")
        }
    }

    fun findLandmarkById(id: String): Landmark? {
        return landmarksList.find { it.id == id }
    }

    fun addPhoto(photo: Photo) {
        photosList.add(photo)
        applicationScope.launch {
            firebaseRepo.savePhoto(photo)
        }
        Log.d(TAG, "Added photo: ${photo.id}")
    }

    private fun initSharedPreferences() {
        sharedPref = getSharedPreferences(MY_SP_FILE_NAME, Context.MODE_PRIVATE)
    }

    private fun saveID(id: String) {
        sharedPref.edit().putString("ID", id).apply()
        Log.d(TAG, "Saved UUID: $id")
    }

    private fun containsID(): Boolean = sharedPref.contains("ID")

    fun getID(): String = sharedPref.getString("ID", "No ID") ?: "No ID"

    companion object {
        private const val TAG = "MyApplication"
    }
}