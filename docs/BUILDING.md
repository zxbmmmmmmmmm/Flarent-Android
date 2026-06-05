# Building Flarent Android

This guide explains how to customize and package Flarent Android for a Flarum forum.

## Requirements

- Android Studio with the Android SDK installed.
- JDK 11 or a newer compatible JDK.
- A Flarum forum reachable from the Android device or emulator.
- Firebase project configuration if you keep Firebase Analytics enabled.

You can build from Android Studio or from the command line with the Gradle wrapper included in this repository.

## 1. Configure The Target Forum

Edit:

```text
app/src/main/res/values/forum_config.xml
```

Update these values:

```xml
<string name="app_name">Flarent</string>
<string name="flarum_base_url" translatable="false">https://discuss.flarum.org/</string>
<string name="check_update_url" translatable="false">https://discuss.flarum.org/</string>
<string name="user_agreement_url" translatable="false">https://discuss.flarum.org/</string>
<string name="privacy_policy_url" translatable="false">https://discuss.flarum.org/</string>
```

Notes:

- `app_name` is displayed in the launcher and app UI.
- `flarum_base_url` must end with `/`.
- `check_update_url`, `user_agreement_url`, and `privacy_policy_url` are used by the About page.

## 2. Change Package Name And Version

Edit:

```text
app/build.gradle.kts
```

For a normal branded release, change `applicationId`, `versionCode`, and `versionName`:

```kotlin
android {
    namespace = "com.bettafish.flarent"

    defaultConfig {
        applicationId = "com.example.yourforum"
        versionCode = 1
        versionName = "1.0"
    }
}
```

Guidelines:

- `applicationId` is the package name used by Android, Firebase, Google Play, and installed devices.
- `versionCode` must be increased for every published upgrade.
- `versionName` is the human-readable app version shown to users.
- Keeping `namespace = "com.bettafish.flarent"` is acceptable if you only need a different published app package.

If you want a full source namespace rename, also change `namespace`, the Kotlin package declarations, and the source folder path under:

```text
app/src/main/java/com/bettafish/flarent/
```

Android Studio can do this with refactoring tools. After a full rename, rebuild the project to regenerate Compose Destinations and `R` references.

## 3. Replace Launcher Icons

1. Open Resource Manager in Android Studio

2. Click "Add resources to the module" -> "Image Asset"
<img width="530" height="466" alt="image" src="https://github.com/user-attachments/assets/5c9bed9b-5b94-49dd-97a6-0b9161c08d48" />

3. Upload your custom icons
<img width="1020" height="679" alt="image" src="https://github.com/user-attachments/assets/6d92e1a1-d783-4526-9e69-714d9e15ab15" />

   > Keep the resource names as `ic_launcher` and `ic_launcher_round`, or update `AndroidManifest.xml` if you choose different names.

## 4. Add Google Services Configuration

The project applies the Google Services Gradle plugin and includes Firebase Analytics. Add the Firebase config file here:

```text
app/google-services.json
```

Important:

- In Firebase, the Android app package must match the `applicationId` in `app/build.gradle.kts`.
- Do not commit `google-services.json` unless your project policy allows it. This repository's `.gitignore` excludes it by default.

If you do not want Firebase Analytics, remove or disable:

```kotlin
id("com.google.gms.google-services")
implementation(platform("com.google.firebase:firebase-bom:34.9.0"))
implementation("com.google.firebase:firebase-analytics")
```

These entries are in the root `build.gradle.kts` and `app/build.gradle.kts`.

## 5. Build App Packages

Open `Build > Generate Signed Bundle/APK` in Android Studio, then you can follow the instructions on screen to sign and package your own forum application.

<img width="598" height="410" alt="image" src="https://github.com/user-attachments/assets/4eeb0315-511a-4bbf-be5c-28c8c0472bd6" />


