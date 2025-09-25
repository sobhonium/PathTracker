# 🚀 GitHub Actions Build Guide for PathTracker

This guide will show you how to build your PathTracker APK automatically using GitHub Actions - no local Android development tools needed!

## 🎯 Overview

GitHub Actions will:
- ✅ Build your APK automatically when you push code
- ✅ Sign the APK for secure installation
- ✅ Upload the APK for easy download
- ✅ Create releases with changelog

## 📋 Step-by-Step Setup

### 1. 📤 Upload Your Project to GitHub

1. **Create a new repository** on GitHub:
   - Go to [github.com/new](https://github.com/new)
   - Repository name: `PathTracker`
   - Make it public or private (your choice)
   - Don't initialize with README (we already have one)

2. **Upload your project**:
   ```bash
   cd /home/sbn/Documents/kml/PathTracker
   git init
   git add .
   git commit -m "Initial PathTracker Android app"
   git remote add origin https://github.com/YOUR_USERNAME/PathTracker.git
   git branch -M main
   git push -u origin main
   ```

### 2. 🔐 Create Signing Keys (Required for APK)

**Option A: Use the script (if Java is installed)**:
```bash
./keystore-setup.sh
```

**Option B: Manual keystore creation**:
```bash
# Install Java first if not available
keytool -genkeypair \
  -keystore pathtracker-keystore.jks \
  -alias pathtracker \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -storepass YOUR_PASSWORD \
  -keypass YOUR_KEY_PASSWORD \
  -dname "CN=Your Name, OU=OrgUnit, O=Organization, L=City, ST=State, C=US"

# Convert to base64 for GitHub
base64 -w 0 pathtracker-keystore.jks
```

### 3. 🔑 Add GitHub Secrets

1. Go to your GitHub repository
2. Click **Settings** → **Secrets and variables** → **Actions**
3. Click **New repository secret** and add these 4 secrets:

| Secret Name | Description | Example |
|-------------|-------------|---------|
| `SIGNING_KEY` | Base64 encoded keystore file | `MIIKXgIBAzCCChoGCSq...` |
| `ALIAS` | Keystore alias | `pathtracker` |
| `KEY_STORE_PASSWORD` | Keystore password | `yourpassword123` |
| `KEY_PASSWORD` | Key password | `yourkeypass123` |

**⚠️ IMPORTANT**: Keep these passwords secure and don't share them!

### 4. 🏗️ Trigger Your First Build

**Option A: Push code (automatic)**:
```bash
git add .
git commit -m "Add GitHub Actions workflow"
git push
```

**Option B: Manual trigger**:
1. Go to your repository on GitHub
2. Click **Actions** tab
3. Select **🚶 Build PathTracker APK**
4. Click **Run workflow**

### 5. 📱 Download Your APK

1. **After the build completes**:
   - Go to **Actions** tab in your repository
   - Click on the latest workflow run
   - Scroll down to **Artifacts**
   - Download **PathTracker-APK**

2. **Extract and install**:
   - Unzip the downloaded file
   - Transfer the `.apk` file to your Android device
   - Install it (enable "Unknown sources" first)

## 🎉 Creating Releases

### Automatic Release Creation

1. **Create a new release**:
   ```bash
   git tag v1.0.0
   git push origin v1.0.0
   ```

2. **Or create via GitHub web**:
   - Go to your repository
   - Click **Releases** → **Create a new release**
   - Tag: `v1.0.0`
   - Title: `PathTracker v1.0.0`
   - Click **Publish release**

3. **Download the APK**:
   - The release will automatically include the signed APK
   - Users can download it directly from the release page

### Manual Release Trigger

1. Go to **Actions** → **🚀 Release PathTracker APK**
2. Click **Run workflow**
3. Enter version tag (e.g., `v1.0.0`)
4. Click **Run workflow**

## 🛠️ Customizing Your Build

### Modify Build Settings

Edit `.github/workflows/build-apk.yml`:

```yaml
# Change Android SDK version
BUILD_TOOLS_VERSION: "34.0.0"

# Add build variants
run: ./gradlew assembleDebug assembleRelease

# Add testing
run: ./gradlew test
```

### Add Build Notifications

Add Slack/Discord notifications:

```yaml
- name: 📢 Notify Build Success
  uses: 8398a7/action-slack@v3
  with:
    status: success
    webhook_url: ${{ secrets.SLACK_WEBHOOK }}
```

## 🔍 Troubleshooting

### Build Fails

**Common issues and fixes**:

1. **Gradle build fails**:
   ```bash
   # Add to workflow before build
   - name: Make gradlew executable
     run: chmod +x gradlew
   ```

2. **SDK license not accepted**:
   ```bash
   # Already included in workflow
   - name: Accept licenses
     run: yes | $ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager --licenses
   ```

3. **Signing fails**:
   - Check that all 4 secrets are added correctly
   - Verify the base64 keystore is complete (no line breaks)
   - Make sure passwords match your keystore

### APK Install Issues

1. **"App not installed"**:
   - Enable "Unknown sources" in Android settings
   - Check Android version compatibility (7.0+)

2. **"Parse error"**:
   - APK might be corrupted during download
   - Re-download from GitHub

3. **Permissions not working**:
   - Grant location and camera permissions manually
   - Check Android settings → Apps → PathTracker → Permissions

## 📈 Advanced Features

### Auto-increment Version

Add to `app/build.gradle`:

```gradle
def getVersionCode = { ->
    try {
        return Integer.parseInt('git rev-list HEAD --count'.execute().text.trim())
    } catch (Exception e) {
        return 1
    }
}

android {
    defaultConfig {
        versionCode getVersionCode()
        versionName "1.0.${getVersionCode()}"
    }
}
```

### Multiple Build Types

```yaml
strategy:
  matrix:
    variant: [debug, release]

steps:
  - run: ./gradlew assemble${{ matrix.variant }}
```

### Automated Testing

```yaml
- name: 🧪 Run Tests
  run: ./gradlew test

- name: 📊 Upload Test Reports
  uses: actions/upload-artifact@v3
  with:
    name: test-reports
    path: app/build/reports/tests/
```

## 🎯 Summary

With GitHub Actions, you can:

✅ **Build APKs automatically** - No local Android Studio needed
✅ **Sign APKs securely** - Using encrypted GitHub secrets
✅ **Create releases easily** - One click to publish
✅ **Share with others** - Direct download links
✅ **Track build history** - See all your builds

**Your PathTracker app is now ready for professional deployment! 🚀**

---

## 🤔 Need Help?

- Check the **Actions** tab for build logs
- Review **Issues** in your repository
- Consult [GitHub Actions documentation](https://docs.github.com/en/actions)

**Happy building! 📱✨**