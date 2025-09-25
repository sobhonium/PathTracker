#!/bin/bash

# PathTracker Development Setup Script
# This script helps set up the Android development environment

echo "🚶 PathTracker Development Setup"
echo "=================================="

# Detect OS
OS=""
if [[ "$OSTYPE" == "linux-gnu"* ]]; then
    OS="Linux"
elif [[ "$OSTYPE" == "darwin"* ]]; then
    OS="macOS"
elif [[ "$OSTYPE" == "msys" ]] || [[ "$OSTYPE" == "win32" ]]; then
    OS="Windows"
else
    echo "❌ Unsupported operating system: $OSTYPE"
    exit 1
fi

echo "📱 Detected OS: $OS"

# Function to install Java (required for Android development)
install_java() {
    echo "☕ Installing Java..."

    if [[ "$OS" == "Linux" ]]; then
        if command -v apt-get &> /dev/null; then
            sudo apt-get update
            sudo apt-get install -y openjdk-11-jdk
        elif command -v yum &> /dev/null; then
            sudo yum install -y java-11-openjdk-devel
        else
            echo "❌ Unsupported Linux distribution. Please install Java 11 manually."
            return 1
        fi
    elif [[ "$OS" == "macOS" ]]; then
        if command -v brew &> /dev/null; then
            brew install openjdk@11
        else
            echo "❌ Homebrew not found. Please install Java 11 manually or install Homebrew first."
            return 1
        fi
    fi
}

# Function to download and set up Android SDK
setup_android_sdk() {
    echo "📱 Setting up Android SDK..."

    ANDROID_HOME="$HOME/Android/Sdk"

    if [ ! -d "$ANDROID_HOME" ]; then
        echo "📥 Downloading Android SDK Command Line Tools..."

        mkdir -p "$ANDROID_HOME"
        cd "$ANDROID_HOME"

        if [[ "$OS" == "Linux" ]]; then
            wget -q https://dl.google.com/android/repository/commandlinetools-linux-9477386_latest.zip
            unzip -q commandlinetools-linux-9477386_latest.zip
        elif [[ "$OS" == "macOS" ]]; then
            curl -O https://dl.google.com/android/repository/commandlinetools-mac-9477386_latest.zip
            unzip -q commandlinetools-mac-9477386_latest.zip
        fi

        mkdir -p cmdline-tools/latest
        mv cmdline-tools/* cmdline-tools/latest/ 2>/dev/null || true

        echo "🔧 Installing Android SDK components..."

        export ANDROID_HOME="$ANDROID_HOME"
        export PATH="$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools"

        yes | sdkmanager --licenses
        sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"

        echo "✅ Android SDK installed at $ANDROID_HOME"
    else
        echo "✅ Android SDK already exists at $ANDROID_HOME"
    fi

    # Add to shell profile
    SHELL_PROFILE=""
    if [[ "$SHELL" == *"zsh"* ]]; then
        SHELL_PROFILE="$HOME/.zshrc"
    elif [[ "$SHELL" == *"bash"* ]]; then
        SHELL_PROFILE="$HOME/.bashrc"
    fi

    if [ ! -z "$SHELL_PROFILE" ]; then
        if ! grep -q "ANDROID_HOME" "$SHELL_PROFILE"; then
            echo "" >> "$SHELL_PROFILE"
            echo "# Android SDK" >> "$SHELL_PROFILE"
            echo "export ANDROID_HOME=\"$ANDROID_HOME\"" >> "$SHELL_PROFILE"
            echo "export PATH=\"\$PATH:\$ANDROID_HOME/cmdline-tools/latest/bin:\$ANDROID_HOME/platform-tools\"" >> "$SHELL_PROFILE"
            echo "📝 Added ANDROID_HOME to $SHELL_PROFILE"
        fi
    fi
}

# Function to install Gradle
install_gradle() {
    echo "🏗️  Installing Gradle..."

    if [[ "$OS" == "Linux" ]] && command -v apt-get &> /dev/null; then
        sudo apt-get install -y gradle
    elif [[ "$OS" == "macOS" ]] && command -v brew &> /dev/null; then
        brew install gradle
    else
        echo "📥 Downloading Gradle..."

        GRADLE_VERSION="7.5"
        GRADLE_HOME="$HOME/gradle-$GRADLE_VERSION"

        if [ ! -d "$GRADLE_HOME" ]; then
            cd "$HOME"
            wget -q "https://services.gradle.org/distributions/gradle-$GRADLE_VERSION-bin.zip"
            unzip -q "gradle-$GRADLE_VERSION-bin.zip"
            rm "gradle-$GRADLE_VERSION-bin.zip"

            # Add to PATH
            if [ ! -z "$SHELL_PROFILE" ]; then
                if ! grep -q "GRADLE_HOME" "$SHELL_PROFILE"; then
                    echo "" >> "$SHELL_PROFILE"
                    echo "# Gradle" >> "$SHELL_PROFILE"
                    echo "export GRADLE_HOME=\"$GRADLE_HOME\"" >> "$SHELL_PROFILE"
                    echo "export PATH=\"\$PATH:\$GRADLE_HOME/bin\"" >> "$SHELL_PROFILE"
                fi
            fi
        fi

        echo "✅ Gradle installed at $GRADLE_HOME"
    fi
}

# Main installation process
main() {
    echo "🚀 Starting development environment setup..."

    # Check if Java is installed
    if ! command -v java &> /dev/null; then
        echo "☕ Java not found. Installing Java..."
        install_java
    else
        echo "✅ Java is already installed"
    fi

    # Set up Android SDK
    if [ -z "$ANDROID_HOME" ] || [ ! -d "$ANDROID_HOME" ]; then
        setup_android_sdk
    else
        echo "✅ Android SDK already configured"
    fi

    # Install Gradle if not present
    if ! command -v gradle &> /dev/null; then
        install_gradle
    else
        echo "✅ Gradle is already installed"
    fi

    echo ""
    echo "🎉 Setup complete!"
    echo ""
    echo "📋 Next steps:"
    echo "1. Restart your terminal or run: source ~/.bashrc (or ~/.zshrc)"
    echo "2. Navigate to the PathTracker directory"
    echo "3. Run: ./build_apk.sh"
    echo ""
    echo "🔧 Manual verification:"
    echo "- Check Java: java -version"
    echo "- Check Android SDK: echo \$ANDROID_HOME"
    echo "- Check Gradle: gradle -version"
}

# Run main function
main