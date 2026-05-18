# Build Instructions for Kreeda-Ankana

## Prerequisites

### Required Software
- **Android Studio**: Hedgehog (2023.1.1) or later
  - Download: https://developer.android.com/studio
- **JDK 17**: Required for Kotlin 1.9+
- **Android SDK**: API Level 34 (Android 14)
- **Gradle**: 8.2 (included with project)

### System Requirements
- **RAM**: 8GB minimum, 16GB recommended
- **Disk Space**: 10GB free
- **OS**: Windows 10+, macOS 10.14+, or Linux

---

## Step-by-Step Build Process

### 1. Install Android Studio

#### Windows
1. Download Android Studio installer
2. Run installer
3. Follow setup wizard
4. Install Android SDK and tools

#### macOS
1. Download .dmg file
2. Drag to Applications
3. Run Android Studio
4. Complete first-time setup

#### Linux
```bash
# Extract archive
tar -xzf android-studio-*.tar.gz

# Move to /opt
sudo mv android-studio /opt/

# Run
/opt/android-studio/bin/studio.sh
```

---

### 2. Configure JDK 17

#### Check Current JDK
```bash
java -version
# Should show: openjdk version "17.x.x"
```

#### Install JDK 17 if needed

**Windows/macOS**:
- Download from: https://adoptium.net/
- Install and restart Android Studio

**Linux**:
```bash
sudo apt update
sudo apt install openjdk-17-jdk
```

#### Set JDK in Android Studio
1. File → Project Structure
2. SDK Location → JDK location
3. Select JDK 17
4. Click Apply

---

### 3. Clone/Open Project

#### Option A: Open Existing Project
1. Android Studio → Open
2. Navigate to `Kreeda-Ankana` folder
3. Click OK
4. Wait for Gradle sync

#### Option B: Import from Version Control
```bash
# If using Git
git clone <repository-url>
cd Kreeda-Ankana
# Then open in Android Studio
```

---

### 4. Configure Firebase

**CRITICAL**: Must complete before building!

1. **Get google-services.json**
   - Follow `FIREBASE_SETUP.md` instructions
   - Download from Firebase Console

2. **Replace Placeholder**
   ```
   Kreeda-Ankana/app/google-services.json
   ```

3. **Verify Package Name**
   - Open `google-services.json`
   - Confirm: `"package_name": "com.kreedaankana"`

---

### 5. Gradle Sync

1. **Automatic Sync**
   - Android Studio should auto-sync on project open
   - Wait for progress bar to complete

2. **Manual Sync** (if needed)
   - File → Sync Project with Gradle Files
   - Or click "Sync Now" banner

3. **Resolve Issues**
   ```bash
   # If sync fails, try:
   ./gradlew clean
   
   # Invalidate caches
   # File → Invalidate Caches → Invalidate and Restart
   ```

---

### 6. Build APK

#### Method 1: Android Studio GUI

**Debug APK** (for testing):
1. Build → Build Bundle(s) / APK(s) → Build APK(s)
2. Wait for build completion
3. Click "locate" in notification
4. APK location: `app/build/outputs/apk/debug/app-debug.apk`

**Release APK** (for distribution):
1. Build → Generate Signed Bundle / APK
2. Select APK
3. Create/Select keystore
4. Enter passwords
5. Select release variant
6. Click Finish

#### Method 2: Command Line

**Debug Build**:
```bash
# Windows
gradlew.bat assembleDebug

# macOS/Linux
./gradlew assembleDebug
```

**Release Build**:
```bash
# Unsigned
./gradlew assembleRelease

# Signed (requires keystore)
./gradlew assembleRelease -Pandroid.injected.signing.store.file=/path/to/keystore \
  -Pandroid.injected.signing.store.password=PASSWORD \
  -Pandroid.injected.signing.key.alias=ALIAS \
  -Pandroid.injected.signing.key.password=PASSWORD
```

---

### 7. Run on Device/Emulator

#### Setup Android Emulator
1. Tools → Device Manager
2. Create Device
3. Select hardware: Pixel 6
4. Select system image: API 34 (Android 14)
5. Finish and launch

#### Connect Physical Device
1. Enable Developer Options:
   - Settings → About Phone → Tap "Build number" 7 times
2. Enable USB Debugging:
   - Settings → Developer Options → USB Debugging
3. Connect via USB
4. Accept debugging prompt

#### Run App
1. Select target device from dropdown
2. Click Run (green play button)
3. App installs and launches

---

### 8. Install APK Manually

#### Via ADB
```bash
# Install debug APK
adb install app/build/outputs/apk/debug/app-debug.apk

# Install release APK
adb install app/build/outputs/apk/release/app-release.apk

# Reinstall (if already installed)
adb install -r app-debug.apk
```

#### Via File Manager (on device)
1. Copy APK to device
2. Open file manager
3. Tap APK file
4. Allow "Install from unknown sources"
5. Install

---

## Build Variants

### Debug
- **Purpose**: Development and testing
- **Features**: 
  - Debuggable
  - No minification
  - Faster build time
- **Command**: `./gradlew assembleDebug`

### Release
- **Purpose**: Production distribution
- **Features**:
  - Not debuggable
  - Code minification (ProGuard)
  - Optimized
  - Requires signing
- **Command**: `./gradlew assembleRelease`

---

## Signing Configuration

### Create Keystore
```bash
keytool -genkey -v -keystore release.keystore \
  -alias kreeda-ankana \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000
```

### Add to gradle.properties
```properties
# In gradle.properties (project root)
RELEASE_STORE_FILE=../release.keystore
RELEASE_STORE_PASSWORD=your_store_password
RELEASE_KEY_ALIAS=kreeda-ankana
RELEASE_KEY_PASSWORD=your_key_password
```

### Update build.gradle.kts
```kotlin
android {
    signingConfigs {
        create("release") {
            storeFile = file(project.findProperty("RELEASE_STORE_FILE") as String)
            storePassword = project.findProperty("RELEASE_STORE_PASSWORD") as String
            keyAlias = project.findProperty("RELEASE_KEY_ALIAS") as String
            keyPassword = project.findProperty("RELEASE_KEY_PASSWORD") as String
        }
    }
    
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
        }
    }
}
```

---

## Troubleshooting

### "Failed to resolve dependencies"
```bash
# Clear Gradle cache
./gradlew clean --refresh-dependencies

# Delete .gradle folders
rm -rf ~/.gradle/caches/
rm -rf .gradle/
```

### "Unsupported class file major version"
- Ensure JDK 17 is selected
- File → Project Structure → SDK Location

### "google-services.json missing"
- Download from Firebase Console
- Place in `app/` directory
- Sync Gradle again

### Build fails with "Out of memory"
```properties
# Add to gradle.properties
org.gradle.jvmargs=-Xmx4096m -XX:MaxMetaspaceSize=1024m
```

### Emulator won't start
- Tools → SDK Manager
- SDK Tools → Intel x86 Emulator Accelerator (HAXM)
- Install and configure

---

## Verification Checklist

After successful build:

- [ ] APK created in `app/build/outputs/apk/`
- [ ] APK size reasonable (~15-25 MB for debug)
- [ ] App installs on device/emulator
- [ ] Firebase authentication works
- [ ] Database operations succeed
- [ ] No crashes on launch
- [ ] Camera permission works (for QR scanner)
- [ ] All screens navigate correctly

---

## Build Performance Tips

### Speed Up Gradle Builds
```properties
# gradle.properties
org.gradle.parallel=true
org.gradle.caching=true
org.gradle.daemon=true
org.gradle.configureondemand=true
```

### Reduce APK Size
- Enable minification in release build
- Use APK Analyzer: Build → Analyze APK
- Remove unused resources
- Use WebP instead of PNG

---

## Distribution

### Google Play Store
1. Generate signed release APK
2. Create developer account
3. Upload to Play Console
4. Complete store listing
5. Submit for review

### Direct Distribution
1. Build signed release APK
2. Host on website or file sharing
3. Users must enable "Unknown sources"

---

## Next Steps

1. ✅ Build successful
2. Test all features
3. Fix bugs
4. Optimize performance
5. Add app signing for Play Store
6. Submit for distribution

---

## Support

For build issues:
1. Check Logcat output
2. Review Gradle console
3. Search error message
4. Check Android Studio Event Log

**Common Resources**:
- Android Developer Docs: https://developer.android.com
- Stack Overflow: https://stackoverflow.com/questions/tagged/android
- Gradle Docs: https://docs.gradle.org
