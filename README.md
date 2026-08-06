# TAAM-App-G3

## Description

TAAM Artifact Management System is an Android application for managing and viewing artifacts from
the TAAM collection. The application supports two types of logged-in accounts: regular users and
admin users.

## User roles

**A regular user has access to the following features:**  
- Log in using their email and password
- View the artifact home page
- Search and browse artifacts
- Open an expanded artifact view for full artifact details
- View artifact likes and comments
- Like and unlike artifacts
- Add comments to artifacts
- Save artifacts to their own saved collection
- View saved artifacts
- Log out

**Along with these features, an admin user has access to the following additional features:**  
- Add new artifacts
- Edit existing artifacts
- Delete artifacts
- Delete comments on artifacts

## Technologies used

- **Languages:** Java, XML
- **Database:** Firebase Realtime Database
- **Cloud image storage:** Supabase Storage
- **Version control:** GitHub / Git
- **Scrum management:** Jira
- **IDE:** Android Studio

## Database structure

**The application uses the following Firebase Realtime Database structure:**  
[TAAM-App-G3 database structure](https://docs.google.com/document/d/1aUBHzFQW1A2_IHyt9NG_178kWJljhPmoQcTUjEI65go/edit?usp=sharing)


## Requirements

**To run the application, you should have the following installed on your device:**
- JDK
- Git
- Android Studio
- Android SDK
- Android emulator (or having a physical Android device)

**Note:** Internet connection is required to access databases and keep the application functioning 
normally

## Setup Instructions

1. Clone the repository using https://github.com/shersxn/TAAM-App-G3.git
2. Open the application in Android Studio
3. Wait for the Gradle project sync and build to finish in Android Studio
4. Select an Android device or emulator (Pixel 10 emulator recommended) for running the application
5. Click **Run** in Android Studio to start the application

## Troubleshooting
If you run into an "SDK location not found" error when opening the project, please make sure to sync the project with Gradle files first, and then try opening the project again

## Contributors

- Sherry Sun (Scrum master)
- Chengbin Chen
- Luoyuan Gu
- Rakhim Kharov
- Sadad Mahmood
- Patrick Ng
- Shakir Sufi
