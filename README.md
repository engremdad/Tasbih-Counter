# তাসবিহ কাউন্টার (Tasbih Counter)

A modern, Bangla-language digital tasbih (dhikr counter) for Android, built with Jetpack Compose and Material 3.

## ✨ Features

- **একাধিক জিকির প্রিসেট** — SubhanAllah, Alhamdulillah, Allahu Akbar and more, plus add your own custom dhikr.
- **দৈনিক লক্ষ্য ট্র্যাকিং** — Set a target per dhikr and watch a circular progress ring fill up.
- **হ্যাপটিক ফিডব্যাক** — A subtle bead-like vibration on every count, with a stronger pulse when a goal is reached.
- **রিসেট ও ইতিহাস** — Reset a session anytime while a daily totals log is preserved.
- **কণ্ঠে গণনা (Voice recognition)** — Say "SubhanAllah" and the counter auto-increments.
- **আসমাউল হুসনা** — All 99 Names of Allah with meanings, a progress counter and text-to-speech playback.
- **কাস্টম থিম** — Five Islamic palettes (Emerald, Midnight, Calligraphy Gold, Islamic Plate, Rose) with light/dark/system modes and optional Material You dynamic color.
- **ধারাবাহিকতা ও অর্জন (Streaks & badges)** — Daily streak tracking and unlockable achievement badges.
- **স্মার্ট রিমাইন্ডার** — Gentle morning/evening adhkar notifications.
- **দোয়া সংগ্রহ** — A curated collection of authentic daily supplications.
- **সেটিংস প্যানেল** — Full control over haptics, sound, screen, voice, theme and reminders.

## 🏗️ Architecture

- **UI:** Jetpack Compose + Material 3, single-Activity with bottom navigation (Navigation Compose).
- **State:** MVVM — `TasbihViewModel` exposes a single `StateFlow<TasbihState>`.
- **Persistence:** Preferences DataStore (JSON via kotlinx.serialization).
- **Background:** WorkManager for scheduled reminder notifications.
- **Language:** UI and translations fully in Bangla; Bangla numerals throughout.

```
app/src/main/java/com/islamic/tasbihcounter/
├── data/            # models, repository (DataStore), seed data (dhikr, 99 names, duas)
├── ui/
│   ├── screens/     # Counter, AsmaulHusna, Dua, History, Settings
│   ├── components/  # ProgressRing
│   ├── theme/       # palettes & Material theme
│   ├── Strings.kt   # centralized Bangla strings
│   └── TasbihViewModel.kt
├── util/            # haptics, voice recognition, reminders, date/streak, Bangla numerals
└── MainActivity.kt  # nav host
```

## 🔧 Build

```bash
./gradlew :app:assembleDebug      # build debug APK
./gradlew :app:testDebugUnitTest  # run unit tests
```

Requires Android SDK Platform 37 (compileSdk 37), minSdk 29.

## 📋 Permissions

- `VIBRATE` — haptic feedback
- `RECORD_AUDIO` — voice-recognition counting (optional, requested on demand)
- `POST_NOTIFICATIONS` — reminder notifications (optional, requested on demand)
