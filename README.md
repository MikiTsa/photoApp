# 📸 SlikniSi

SlikniSi is an Android application designed to encourage users to
explore their surroundings through photography. The app highlights
popular landmarks on an interactive map, allows users to capture photos
of these locations, and rewards them with points that contribute to
their profile level.

> *This application is developed as part of the subject PORA.*

------------------------------------------------------------------------

## ✨ Purpose of the Application

SlikniSi is intended for both tourists and locals who want to:

-   Discover scenic and iconic spots within a city\
-   Photograph marked landmarks\
-   Collect points by visiting and capturing locations\
-   Track progress and level up their profile\
-   Build a personal gallery of meaningful captured moments

The app combines exploration, geolocation features, and gamification
into a modern and visually engaging experience.

------------------------------------------------------------------------

## 👨‍💻 Author

**Mihail Trajkoski**

------------------------------------------------------------------------

## 🗺️ Key Features (Planned)

-   Interactive map with marked landmarks (OpenStreetMap)\
-   Point system and user level progression\
-   Capture and store photos for visited locations\
-   GPS location validation to confirm proximity\
-   Push notifications (e.g., daily challenges, new landmarks)\
-   Multi-language support (e.g., English, Slovenian)\
-   Local storage of user data and preferences\
-   Display of visited locations and achievements

------------------------------------------------------------------------

## 🛠️ Technologies Used

### **Android & Kotlin Technologies**

-   Android Studio\
-   Kotlin\
-   ConstraintLayout\
-   ViewBinding\
-   SharedPreferences\
-   Application class for global state\
-   Kotlinx.serialization or Gson for `.json` data storage\
-   RecyclerView for displaying lists\
-   Kotlin-faker\
-   UUID generation for unique object/user identification\
-   registerForActivityResult for passing data between activities

### **Higher-Level Functionalities**

-   OpenStreetMap SDK\
-   Push Notifications (Firebase Cloud Messaging or
    NotificationManager)\
-   Camera & MediaStore for capturing photos\
-   Haptic feedback\
-   Optional QR code functionality

------------------------------------------------------------------------

## 🎨 Planned UI & Design Features

-   Custom app theme and color palette\
-   Use of vector graphics (.svg)\
-   Custom font for titles or UI elements\
-   Multi-language string resources (`strings.xml`)\
-   Well-structured dimension resources (`dimens.xml`)

------------------------------------------------------------------------

## 📷 Screenshots

### Home Page
<img src="screenshots/ss_HomePage.jpeg" width="250" alt="Home Page"/>

*Nearby landmarks sorted by distance*

### Achievements
<img src="screenshots/ss_AchievementPage.jpeg" width="250" alt="Achievements"/>

*Track progress and unlock achievements*

### Map
<img src="screenshots/ss_MapPage.jpeg" width="250" alt="Map"/>

*Map integration (coming soon)*

### All Landmarks
<img src="screenshots/ss_LandmarksList.jpeg" width="250" alt="Landmarks List"/>

*Complete list of landmarks with details*

### Add Landmark
<img src="screenshots/ss_AddLandmark.jpeg" width="250" alt="Add Landmark"/>

*Form for adding new landmarks*

### Edit Landmark
<img src="screenshots/ss_EditLandmark.jpeg" width="250" alt="Edit Landmark"/>

*Edit existing landmark details*

### Delete Landmark
<img src="screenshots/ss_DeleteLandmark.jpeg" width="250" alt="Delete Landmark"/>

*Confirmation before deletion*

### Profile
<img src="screenshots/ss_ProfilePage.jpeg" width="250" alt="Profile"/>

*User statistics and progress*
