package com.example.sliknisi

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.lib.DataGenerator
import com.example.lib.Landmark
import com.example.lib.Photo
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.IOException
import java.util.UUID

const val MY_SP_FILE_NAME = "sliknisi_shared.data"
const val MY_FILE_NAME = "landmarks_data.json"
const val PHOTOS_FILE_NAME = "photos_data.json"

class MyApplication : Application() {

    val landmarksList = ArrayList<Landmark>()
    val photosList = ArrayList<Photo>()

    private lateinit var sharedPref: SharedPreferences
    private lateinit var gson: Gson
    private lateinit var file: File
    private lateinit var photosFile: File

    override fun onCreate() {
        super.onCreate()

        Log.d(TAG, "MyApplication onCreate() - Initializing app")

        gson = Gson()
        file = File(filesDir, MY_FILE_NAME)
        photosFile = File(filesDir, PHOTOS_FILE_NAME)
        
        Log.d(TAG, "JSON file path: ${file.absolutePath}")
        Log.d(TAG, "Photos file path: ${photosFile.absolutePath}")

        initSharedPreferences()

        if (!containsID()) {
            val newUUID = UUID.randomUUID().toString().replace("-", "")
            saveID(newUUID)
            Log.d(TAG, "First launch - Generated new UUID: $newUUID")
        } else {
            Log.d(TAG, "App UUID already exists: ${getID()}")
        }

        loadFromFile()
        loadPhotosFromFile()

        if (landmarksList.isEmpty()) {
            Log.d(TAG, "Generating initial landmarks...")

            val initialLandmarks = DataGenerator.generateRealMariborLandmarks()
            
            landmarksList.addAll(initialLandmarks)
            saveToFile()
            Log.d(TAG, "Generated and saved ${landmarksList.size} real Maribor landmarks")
        }
    }

    fun saveToFile() {
        try {
            val jsonString = gson.toJson(landmarksList)
            FileUtils.writeStringToFile(file, jsonString, "UTF-8")
            Log.d(TAG, "Successfully saved ${landmarksList.size} landmarks to file")
        } catch (e: IOException) {
            Log.e(TAG, "Error saving to file: ${file.absolutePath}", e)
        }
    }

    fun loadFromFile() {
        try {
            if (file.exists()) {
                val jsonString = FileUtils.readFileToString(file, "UTF-8")
                Log.d(TAG, "Reading from file...")

                val type = object : TypeToken<ArrayList<Landmark>>() {}.type
                val loadedList: ArrayList<Landmark> = gson.fromJson(jsonString, type)

                landmarksList.clear()
                landmarksList.addAll(loadedList)

                Log.d(TAG, "Successfully loaded ${landmarksList.size} landmarks from file")
            } else {
                Log.d(TAG, "No existing data file - starting with empty list")
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error loading from file", e)
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing JSON", e)
        }
    }

    private fun savePhotosToFile() {
        try {
            val jsonString = gson.toJson(photosList)
            FileUtils.writeStringToFile(photosFile, jsonString, "UTF-8")
            Log.d(TAG, "Successfully saved ${photosList.size} photos to file")
        } catch (e: IOException) {
            Log.e(TAG, "Error saving photos to file: ${photosFile.absolutePath}", e)
        }
    }

    private fun loadPhotosFromFile() {
        try {
            if (photosFile.exists()) {
                val jsonString = FileUtils.readFileToString(photosFile, "UTF-8")
                Log.d(TAG, "Reading photos from file...")

                val type = object : TypeToken<ArrayList<Photo>>() {}.type
                val loadedList: ArrayList<Photo> = gson.fromJson(jsonString, type) ?: ArrayList()

                photosList.clear()
                photosList.addAll(loadedList)

                Log.d(TAG, "Successfully loaded ${photosList.size} photos from file")
            } else {
                Log.d(TAG, "No existing photos file - starting with empty list")
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error loading photos from file", e)
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing photos JSON", e)
        }
    }

    fun addLandmark(landmark: Landmark) {
        landmarksList.add(landmark)
        saveToFile()
        Log.d(TAG, "Added landmark: ${landmark.name} with ID: ${landmark.id}")
    }

    fun deleteLandmark(id: String) {
        val removed = landmarksList.removeIf { it.id == id }
        if (removed) {
            saveToFile()
            Log.d(TAG, "Deleted landmark with ID: $id")
        } else {
            Log.w(TAG, "Landmark with ID $id not found")
        }
    }

    fun updateLandmark(updatedLandmark: Landmark) {
        val index = landmarksList.indexOfFirst { it.id == updatedLandmark.id }
        if (index != -1) {
            landmarksList[index] = updatedLandmark
            saveToFile()
            Log.d(TAG, "Updated landmark with ID: ${updatedLandmark.id}")
        } else {
            Log.w(TAG, "Landmark with ID ${updatedLandmark.id} not found for update")
        }
    }

    fun findLandmarkById(id: String): Landmark? {
        return landmarksList.find { it.id == id }
    }

    fun addPhoto(photo: Photo) {
        photosList.add(photo)
        savePhotosToFile()
        Log.d(TAG, "Added photo with ID: ${photo.id} for landmark: ${photo.landmarkId}")
    }

    fun deletePhoto(id: String) {
        val removed = photosList.removeIf { it.id == id }
        if (removed) {
            savePhotosToFile()
            Log.d(TAG, "Deleted photo with ID: $id")
        } else {
            Log.w(TAG, "Photo with ID $id not found")
        }
    }

    fun getPhotosForLandmark(landmarkId: String): List<Photo> {
        return photosList.filter { it.landmarkId == landmarkId }
    }

    private fun initSharedPreferences() {
        sharedPref = getSharedPreferences(MY_SP_FILE_NAME, Context.MODE_PRIVATE)
        Log.d(TAG, "SharedPreferences initialized")
    }

    private fun saveID(id: String) {
        with(sharedPref.edit()) {
            putString("ID", id)
            apply()
        }
        Log.d(TAG, "Saved UUID to SharedPreferences: $id")
    }

    private fun containsID(): Boolean {
        return sharedPref.contains("ID")
    }

    fun getID(): String {
        return sharedPref.getString("ID", "No ID") ?: "No ID"
    }

    companion object {
        private const val TAG = "MyApplication"
    }
}
