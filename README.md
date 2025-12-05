# 🍕 PizzaMania - Android Delivery App

![Platform](https://img.shields.io/badge/Platform-Android-green) ![Language](https://img.shields.io/badge/Language-Kotlin-purple) ![Backend](https://img.shields.io/badge/Backend-Firebase-orange)

**PizzaMania** is a native Android application designed for a seamless pizza ordering experience. Built entirely using **Kotlin**, it leverages **Firebase** for real-time data handling and the **Google Maps SDK** for location-based services, ensuring a smooth and efficient delivery tracking system.

## 🚀 Key Features

* **📍 Live Location Tracking:** Integrated **Google Maps API** to track delivery location and set delivery addresses.
* **🔥 Firebase Integration:**
    * **Authentication:** Secure login/signup via Firebase Auth.
    * **Real-time Database:** Instant order updates and menu management using Cloud Firestore.
* **🛒 Cart & Order Management:** Easy-to-use cart functionality with real-time price calculation.
* **📱 Modern UI:** Clean and responsive user interface designed with Android XML/Jetpack Compose.
* **🔔 Push Notifications:** Order status updates via Firebase Cloud Messaging (FCM).

## 🛠️ Tech Stack

| Component | Technology |
| :--- | :--- |
| **Language** | Kotlin (100%) |
| **Architecture** | MVVM (Model-View-ViewModel) |
| **Backend** | Firebase (Firestore, Auth, Storage) |
| **Maps** | Google Maps SDK for Android |
| **Build System** | Gradle (Kotlin DSL) |

## 📂 Project Structure

pizzamaniaapp/
├── app/
│   ├── src/main/java/com/pizzamania  # Kotlin Source Code
│   │   ├── activities/               # UI Screens
│   │   ├── adapters/                 # RecyclerView Adapters
│   │   ├── models/                   # Data Classes
│   │   └── utils/                    # Helper Functions
│   └── src/main/res/                 # Layouts, Drawables, Values
├── build.gradle.kts                  # App-level build config
└── settings.gradle.kts               # Project settings

⚙️ Setup & Installation
To run this project locally, follow these steps:

1. Clone the Repository
Bash

git clone [https://github.com/WAH-ISHAN/pizzamaniaapp.git](https://github.com/WAH-ISHAN/pizzamaniaapp.git)
2. Firebase Setup
Go to the Firebase Console.

Create a new project.

Add an Android app with the package name com.example.pizzamania (check your AndroidManifest.xml for the exact package name).

Download the google-services.json file.

Paste the file into the app/ directory of the project.

3. Google Maps API Setup
Go to the Google Cloud Console.

Enable the Maps SDK for Android.

Generate an API Key.

Add the key to your AndroidManifest.xml or local.properties:

XML

<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="YOUR_API_KEY_HERE" />
4. Build and Run
Open the project in Android Studio.

Sync Gradle files.

Run on an Emulator or Physical Device.
