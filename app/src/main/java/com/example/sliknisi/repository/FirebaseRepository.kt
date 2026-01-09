package com.example.sliknisi.repository

import android.util.Log
import com.example.lib.Landmark
import com.example.lib.Photo
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class FirebaseRepository {

    private val db: FirebaseFirestore = Firebase.firestore
    private val landmarksCollection = db.collection("landmarks")
    private val photosCollection = db.collection("photos")

    companion object {
        private const val TAG = "FirebaseRepository"
    }

    suspend fun saveLandmark(landmark: Landmark) {
        try {
            landmarksCollection.document(landmark.id).set(landmark).await()
            Log.d(TAG, "Saved landmark: ${landmark.name}")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving landmark", e)
        }
    }

    suspend fun getAllLandmarks(): List<Landmark> {
        return try {
            val snapshot = landmarksCollection.get().await()
            snapshot.toObjects(Landmark::class.java).also {
                Log.d(TAG, "Loaded ${it.size} landmarks from Firestore")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading landmarks", e)
            emptyList()
        }
    }

    suspend fun updateLandmark(landmark: Landmark) {
        try {
            landmarksCollection.document(landmark.id).set(landmark).await()
            Log.d(TAG, "Updated landmark: ${landmark.id}")
        } catch (e: Exception) {
            Log.e(TAG, "Error updating landmark", e)
        }
    }

    suspend fun deleteLandmark(id: String) {
        try {
            landmarksCollection.document(id).delete().await()
            Log.d(TAG, "Deleted landmark: $id")
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting landmark", e)
        }
    }

    suspend fun savePhoto(photo: Photo) {
        try {
            photosCollection.document(photo.id).set(photo).await()
            Log.d(TAG, "Saved photo: ${photo.id}")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving photo", e)
        }
    }

    suspend fun getAllPhotos(): List<Photo> {
        return try {
            val snapshot = photosCollection.get().await()
            snapshot.toObjects(Photo::class.java).also {
                Log.d(TAG, "Loaded ${it.size} photos from Firestore")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading photos", e)
            emptyList()
        }
    }

    suspend fun deletePhoto(id: String) {
        try {
            photosCollection.document(id).delete().await()
            Log.d(TAG, "Deleted photo: $id")
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting photo", e)
        }
    }

    suspend fun seedInitialData(landmarks: List<Landmark>) {
        try {
            val existingCount = landmarksCollection.get().await().size()
            if (existingCount == 0) {
                landmarks.forEach { landmark ->
                    landmarksCollection.document(landmark.id).set(landmark).await()
                }
                Log.d(TAG, "Seeded ${landmarks.size} initial landmarks")
            } else {
                Log.d(TAG, "Data already exists, skipping seed")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error seeding data", e)
        }
    }
}