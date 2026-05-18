# Firebase Configuration Guide for Kreeda-Ankana

## Complete Step-by-Step Firebase Setup

### Part 1: Firebase Console Setup

#### 1. Create Firebase Project
1. Visit [Firebase Console](https://console.firebase.google.com/)
2. Click **"Add project"**
3. Project name: `Kreeda-Ankana`
4. Disable Google Analytics (optional for development)
5. Click **"Create project"**

#### 2. Register Android App
1. In project overview, click Android icon
2. **Package name**: `com.kreedaankana` (MUST match exactly)
3. **App nickname**: Kreeda-Ankana
4. **Debug signing certificate SHA-1** (optional for now)
5. Click **"Register app"**

#### 3. Download Configuration File
1. Download `google-services.json`
2. **IMPORTANT**: Replace the placeholder file at:
   ```
   Kreeda-Ankana/app/google-services.json
   ```

---

### Part 2: Enable Firebase Services

#### A. Firebase Authentication

1. **Navigate to Authentication**
   - Firebase Console → Build → Authentication
   - Click **"Get started"**

2. **Enable Email/Password**
   - Go to **"Sign-in method"** tab
   - Click **"Email/Password"**
   - Toggle **"Enable"**
   - Save

3. **Email Verification Settings**
   - Go to **Templates** tab
   - Edit **"Email address verification"** template
   - Customize subject/body (optional)

---

#### B. Cloud Firestore

1. **Create Database**
   - Firebase Console → Build → Firestore Database
   - Click **"Create database"**

2. **Select Mode**
   - Start in **Test mode** (for development)
   - **Production mode** (for release - requires proper rules)

3. **Choose Location**
   - Select region closest to your users
   - Click **"Enable"**

4. **Set Security Rules** (Important!)
   - Go to **"Rules"** tab
   - Replace with:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    
    // User profiles - users can only access their own
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
    
    // Bookings - authenticated users can read/write
    match /bookings/{bookingId} {
      allow read: if request.auth != null;
      allow create: if request.auth != null && request.resource.data.userId == request.auth.uid;
      allow update, delete: if request.auth != null && resource.data.userId == request.auth.uid;
    }
    
    // Challenges - all authenticated users can read, create own
    match /challenges/{challengeId} {
      allow read: if request.auth != null;
      allow create: if request.auth != null && request.resource.data.userId == request.auth.uid;
      allow update: if request.auth != null;
    }
    
    // Matches
    match /matches/{matchId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null;
    }
    
    // Live matches - real-time updates
    match /matches_live/{matchId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null;
    }
    
    // Tournaments
    match /tournaments/{tournamentId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null;
    }
  }
}
```

5. Click **"Publish"**

---

#### C. Firebase Storage

1. **Create Storage Bucket**
   - Firebase Console → Build → Storage
   - Click **"Get started"**

2. **Select Security Rules**
   - Start in **Test mode** (for development)
   - Click **"Next"**

3. **Choose Location**
   - Same as Firestore location
   - Click **"Done"**

4. **Set Storage Rules** (Important!)
   - Go to **"Rules"** tab
   - Replace with:

```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    
    // Profile images - users can only upload their own
    match /profile_images/{userId}/{fileName} {
      allow read: if request.auth != null;
      allow write: if request.auth != null 
                   && request.auth.uid == userId
                   && request.resource.size < 5 * 1024 * 1024  // 5MB limit
                   && request.resource.contentType.matches('image/.*');
    }
  }
}
```

5. Click **"Publish"**

---

#### D. Firebase Cloud Messaging (FCM)

1. **Enable FCM**
   - Firebase Console → Build → Cloud Messaging
   - No additional setup needed
   - API is automatically enabled

2. **Server Key** (for future use)
   - Go to **Cloud Messaging** tab
   - Find **"Server key"** under **Project credentials**
   - Save for backend integration (if needed)

---

### Part 3: Android App Integration

#### 1. Verify google-services.json
```bash
# File location
Kreeda-Ankana/app/google-services.json

# Verify package name inside file matches:
"package_name": "com.kreedaankana"
```

#### 2. Build and Run
```bash
./gradlew clean build
./gradlew assembleDebug
```

#### 3. Get SHA-1 Certificate (for production)
```bash
# Debug certificate
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android

# Release certificate (after creating keystore)
keytool -list -v -keystore release.keystore -alias your-alias
```

Then add to Firebase Console → Project Settings → Your apps → SHA certificate fingerprints

---

### Part 4: Testing Firebase Integration

#### Test Authentication
1. Run app
2. Sign up with email
3. Check Firebase Console → Authentication → Users
4. Verify email sent
5. Login after verification

#### Test Firestore
1. Create a booking
2. Check Firebase Console → Firestore Database
3. Verify `bookings` collection created
4. Verify data structure

#### Test Storage
1. Upload profile image
2. Check Firebase Console → Storage
3. Verify file under `profile_images/{userId}/`

#### Test Real-time Updates
1. Open app on two devices/emulators
2. Post a challenge on device 1
3. Verify it appears on device 2 in real-time

---

### Part 5: Production Checklist

- [ ] Replace test mode rules with production rules
- [ ] Add SHA-1/SHA-256 fingerprints for release build
- [ ] Enable App Check for security
- [ ] Set up Firebase Analytics (optional)
- [ ] Configure FCM for notifications
- [ ] Set up Firebase Crashlytics (recommended)
- [ ] Review and tighten security rules
- [ ] Set up Firebase Performance Monitoring (optional)

---

### Troubleshooting

#### "Default FirebaseApp is not initialized"
- Ensure `google-services.json` is in `app/` directory
- Verify package name matches exactly
- Clean and rebuild project

#### "API not enabled"
- Go to Google Cloud Console
- Enable required APIs: Firestore, Storage, FCM

#### Authentication not working
- Verify Email/Password is enabled in Firebase Console
- Check internet connection
- Review Logcat for detailed errors

#### Firestore permission denied
- Review security rules
- Ensure user is authenticated before accessing data
- Check userId matches auth.uid

---

### Support Resources

- [Firebase Android Documentation](https://firebase.google.com/docs/android/setup)
- [Firestore Security Rules Guide](https://firebase.google.com/docs/firestore/security/get-started)
- [Firebase Authentication Guide](https://firebase.google.com/docs/auth/android/start)

---

## Quick Reference

### Firebase Console URLs
- **Project Console**: `https://console.firebase.google.com/project/YOUR_PROJECT_ID`
- **Authentication**: `https://console.firebase.google.com/project/YOUR_PROJECT_ID/authentication/users`
- **Firestore**: `https://console.firebase.google.com/project/YOUR_PROJECT_ID/firestore`
- **Storage**: `https://console.firebase.google.com/project/YOUR_PROJECT_ID/storage`

Replace `YOUR_PROJECT_ID` with your actual Firebase project ID.
