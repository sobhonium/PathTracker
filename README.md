# 🚶‍♂️ PathTracker - GPS Walking Path Recorder

PathTracker is a comprehensive Android application that allows users to record walking paths with GPS tracking, capture photos, add comments, rate experiences, and export/share their adventures as KML files.

## ✨ Features

### 🗺️ GPS Path Recording
- Real-time GPS tracking with high accuracy
- Background location service with notification
- Path visualization and statistics (distance, time, speed)
- Start/stop recording functionality

### 📸 Photo Integration
- Camera integration for capturing photos during walks
- GPS coordinates automatically attached to photos
- Photo gallery for each path
- Caption support for photos

### 💬 Comments & Rating System
- Add text comments at specific locations
- Rate your walking experience (1-5 stars)
- Location-based comments with GPS coordinates
- Rich text descriptions

### 📤 Export & Sharing
- Export paths as KML files compatible with Google Earth
- Share KML files via email, messaging, or cloud storage
- Professional KML formatting with styles and descriptions
- Photo locations and comments included in exports

### 🏠 Path Management
- View all recorded paths in a list
- Path details with statistics
- Delete unwanted paths
- Search and filter capabilities

## 📱 Screenshots

The app includes:
- Main dashboard with path list
- Recording interface with real-time stats
- Camera integration
- Comment and rating interface
- Export and sharing options

## 🛠️ Technical Features

### Architecture
- **Language**: Kotlin
- **Architecture**: MVVM with LiveData
- **Database**: Room (SQLite)
- **Camera**: CameraX
- **Location**: Google Play Services Location API
- **UI**: Material Design Components

### Database Schema
- **Paths**: Main path records with metadata
- **PathPoints**: GPS coordinates with timestamps
- **Photos**: Image references with location data
- **Comments**: Text notes with optional locations

### Permissions Required
- `ACCESS_FINE_LOCATION` - GPS tracking
- `CAMERA` - Photo capture
- `WRITE_EXTERNAL_STORAGE` - KML file export
- `INTERNET` - Optional for enhanced location services

## 🏗️ Building the App

### Prerequisites
1. **Android Studio** (Arctic Fox or newer)
2. **Android SDK** (API level 24+)
3. **Java 8** or higher
4. **Gradle 7.5** or newer

### Setup Instructions

1. **Clone the project**:
   ```bash
   cd /path/to/PathTracker
   ```

2. **Set up Android SDK**:
   ```bash
   export ANDROID_HOME=/path/to/android/sdk
   export PATH=$PATH:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools
   ```

3. **Build the APK**:
   ```bash
   ./build_apk.sh
   ```

   Or manually:
   ```bash
   ./gradlew clean
   ./gradlew assembleDebug
   ```

4. **Find your APK**:
   ```
   app/build/outputs/apk/debug/app-debug.apk
   ```

### Alternative Build Methods

#### Using Android Studio
1. Open the project in Android Studio
2. Wait for Gradle sync to complete
3. Click **Build** → **Build Bundle(s) / APK(s)** → **Build APK(s)**
4. Find the APK in `app/build/outputs/apk/debug/`

#### Command Line (with Gradle installed)
```bash
gradle clean
gradle assembleDebug
```

## 📲 Installation

### Installing on Android Device

1. **Enable Unknown Sources**:
   - Go to **Settings** → **Security** → **Unknown Sources**
   - Enable installation from unknown sources

2. **Transfer APK**:
   - Copy `app-debug.apk` to your Android device
   - Or use ADB: `adb install app-debug.apk`

3. **Install**:
   - Tap the APK file on your device
   - Follow installation prompts
   - Grant required permissions when prompted

### Required Android Version
- **Minimum**: Android 7.0 (API level 24)
- **Target**: Android 14 (API level 34)
- **Recommended**: Android 8.0+ for best performance

## 🚀 Usage Instructions

### Recording Your First Path

1. **Launch PathTracker**
2. **Tap the '+' button** to start recording
3. **Enter a path name** (optional)
4. **Begin walking** - GPS tracking starts automatically
5. **Take photos** using the camera button
6. **Add comments** about interesting locations
7. **Rate your experience** using the star rating
8. **Stop recording** when finished

### Exporting & Sharing

1. **Stop your recording**
2. **Tap 'Export KML'** during the stop dialog
3. **Choose sharing method** (email, drive, messaging)
4. **Send to friends** or import into Google Earth

### Viewing Your Paths

1. **Main screen** shows all recorded paths
2. **Tap any path** to view details
3. **See photos, comments, and statistics**
4. **Export individual paths** as needed

## 🔧 Configuration

### GPS Settings
- Update interval: 5 seconds (configurable)
- Minimum accuracy: High precision
- Background tracking: Enabled with notification

### Storage
- Photos stored in app's private directory
- Database stored locally (no cloud sync)
- KML exports saved to Downloads folder

## 🐛 Troubleshooting

### Common Issues

**GPS not working**:
- Ensure location permissions granted
- Check GPS is enabled in system settings
- Try recording outdoors with clear sky view

**Camera not opening**:
- Grant camera permission in app settings
- Restart app after granting permissions
- Check available storage space

**Export failing**:
- Ensure storage permission granted
- Check available storage space
- Try exporting smaller paths first

**App crashes**:
- Clear app data in system settings
- Reinstall the app
- Check Android version compatibility

## 📄 License

This project is open source and available under the MIT License.

## 🤝 Contributing

Contributions are welcome! Please feel free to submit issues, feature requests, or pull requests.

## 📞 Support

For support, please create an issue in the project repository or contact the development team.

---

**Happy Walking! 🚶‍♂️🗺️**

*PathTracker - Your personal GPS walking companion*