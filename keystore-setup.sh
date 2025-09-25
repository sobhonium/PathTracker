#!/bin/bash

# PathTracker Keystore Setup Script
# This script helps create a keystore for signing Android APKs

echo "🔐 PathTracker Keystore Setup"
echo "============================="

KEYSTORE_FILE="pathtracker-keystore.jks"
ALIAS="pathtracker"

echo "📱 This script will create a keystore for signing your Android APK."
echo ""

# Check if keystore already exists
if [ -f "$KEYSTORE_FILE" ]; then
    echo "⚠️  Keystore file already exists: $KEYSTORE_FILE"
    read -p "Do you want to create a new one? (y/N): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        echo "❌ Cancelled."
        exit 1
    fi
    rm -f "$KEYSTORE_FILE"
fi

echo ""
echo "📝 Please provide the following information for your keystore:"
echo ""

# Get keystore password
while true; do
    read -s -p "🔒 Enter keystore password (minimum 6 characters): " KEYSTORE_PASSWORD
    echo
    if [ ${#KEYSTORE_PASSWORD} -ge 6 ]; then
        read -s -p "🔒 Confirm keystore password: " KEYSTORE_PASSWORD_CONFIRM
        echo
        if [ "$KEYSTORE_PASSWORD" = "$KEYSTORE_PASSWORD_CONFIRM" ]; then
            break
        else
            echo "❌ Passwords do not match. Please try again."
        fi
    else
        echo "❌ Password must be at least 6 characters. Please try again."
    fi
done

# Get key password
while true; do
    read -s -p "🗝️  Enter key password (minimum 6 characters): " KEY_PASSWORD
    echo
    if [ ${#KEY_PASSWORD} -ge 6 ]; then
        read -s -p "🗝️  Confirm key password: " KEY_PASSWORD_CONFIRM
        echo
        if [ "$KEY_PASSWORD" = "$KEY_PASSWORD_CONFIRM" ]; then
            break
        else
            echo "❌ Passwords do not match. Please try again."
        fi
    else
        echo "❌ Password must be at least 6 characters. Please try again."
    fi
done

# Get certificate information
read -p "👤 Enter your first and last name: " CN
read -p "🏢 Enter your organization unit (e.g., IT Department): " OU
read -p "🏛️  Enter your organization (e.g., My Company): " O
read -p "🌍 Enter your city or locality: " L
read -p "📍 Enter your state or province: " ST
read -p "🚩 Enter your country code (2 letters, e.g., US): " C

echo ""
echo "🏗️  Creating keystore..."

# Create keystore
keytool -genkeypair \
    -keystore "$KEYSTORE_FILE" \
    -alias "$ALIAS" \
    -keyalg RSA \
    -keysize 2048 \
    -validity 10000 \
    -storepass "$KEYSTORE_PASSWORD" \
    -keypass "$KEY_PASSWORD" \
    -dname "CN=$CN, OU=$OU, O=$O, L=$L, ST=$ST, C=$C"

if [ $? -eq 0 ]; then
    echo "✅ Keystore created successfully: $KEYSTORE_FILE"
    echo ""
    echo "🔐 GitHub Secrets Setup:"
    echo "========================"
    echo ""
    echo "Add these secrets to your GitHub repository:"
    echo "(Go to: Settings → Secrets and variables → Actions → New repository secret)"
    echo ""

    echo "🔑 SIGNING_KEY:"
    echo "$(base64 -w 0 "$KEYSTORE_FILE")"
    echo ""

    echo "🏷️  ALIAS:"
    echo "$ALIAS"
    echo ""

    echo "🔒 KEY_STORE_PASSWORD:"
    echo "$KEYSTORE_PASSWORD"
    echo ""

    echo "🗝️  KEY_PASSWORD:"
    echo "$KEY_PASSWORD"
    echo ""

    echo "📋 Instructions:"
    echo "1. Copy each value above"
    echo "2. Go to your GitHub repository"
    echo "3. Navigate to Settings → Secrets and variables → Actions"
    echo "4. Click 'New repository secret'"
    echo "5. Add each secret with the exact name and value shown above"
    echo ""
    echo "⚠️  IMPORTANT: Keep this keystore file safe! You'll need it for future app updates."
    echo "⚠️  Do NOT commit the keystore file to your repository!"

else
    echo "❌ Failed to create keystore."
    exit 1
fi