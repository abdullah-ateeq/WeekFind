# WeekFind 📅

A modern, fast, and fully offline Android utility app for instant calendar calculations, day-of-week lookups, and year progress tracking. Built natively with **Jetpack Compose** and **Material 3**.

---

## 🌟 Key Features

* **Instant Weekday Lookup**: Find the day of the week for any past, present, or future calendar date.
* **Quick Navigation & Shortcuts**:
  * One-tap jump to **Today**, **Yesterday**, and **Tomorrow**.
  * Step forwards or backwards by **Day**, **Week**, or **Month**.
  * Material 3 Date Picker for quick date selection.
  * Direct ISO date entry support (`YYYY-MM-DD`).
* **Rich Calendar Analytics**:
  * **Day of Year** (e.g., Day 297 of 365)
  * **Week of Year** (Locale-aware & ISO 8601 standard)
  * **Days Elapsed & Days Remaining** in the year
  * **Year Progress** visual bar and percentage
  * **Leap Year** detection
* **Interactive Views**:
  * **Month Calendar Grid**: Expandable month view highlighting the selected date and current week.
  * **Week View**: Horizontal bar for quick context within the week.
* **History & Quick Restore**:
  * Save lookup results to history for easy access.
  * One-tap restore back to the main view.
* **Share & Copy**: Quickly export date information to clipboard or share with other apps.
* **Fully Customizable**:
  * **Theme Options**: Light, Dark, or System Theme with **Material You Dynamic Color** support.
  * **Date Formatting**: Choose between `24 October 2026`, `Oct 24, 2026`, `24/10/2026`, or `2026-10-24`.
  * **Week Numbering**: Switch between **Locale-based** and **ISO 8601** rules.
  * Haptic feedback and transition animations toggle.
* **100% Private & Offline**: Works completely offline. No internet permission required, no analytics, no ads, and zero data leaves your device.

---

## 🛠️ Tech Stack & Architecture

* **Language**: [Kotlin](https://kotlinlang.org/)
* **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with [Material Design 3](https://m3.material.io/)
* **Navigation**: [Jetpack Navigation Compose](https://developer.android.com/jetpack/compose/navigation)
* **Architecture**: MVVM (Model-View-ViewModel) + Repository Pattern with Kotlin `StateFlow`
* **Date & Time API**: Modern Java 8+ `java.time` API (`LocalDate`, `WeekFields`, `DayOfWeek`)
* **Local Storage**: Lightweight `PreferencesStorage` abstraction over `SharedPreferences`
* **Build System**: Gradle with Kotlin DSL (`build.gradle.kts`)
* **Target SDK**: Android 16 / SDK 37 (Minimum SDK 26)

---

## 📱 Screenshots & Previews

| Home Screen | History Screen | Settings Screen | About Screen |
|:---:|:---:|:---:|:---:|
| Calendar calculations, month view, and date controls | Saved lookup entries with restore option | Deep personalization and date formatting | Application details and definitions |

---

## 🧪 Testing

The app includes comprehensive unit tests verifying calendar calculations, leap year edge cases, date navigation, and formatting.

To run tests via Gradle CLI:
```bash
./gradlew test
```

---

## 🚀 Building & Running

### Prerequisites
* **Android Studio** (Ladybug | 2024.2.1 or newer recommended)
* **JDK 11** or higher
* **Android SDK API 37** (or API 26+ device / emulator)

### Steps
1. Clone the repository:
   ```bash
   git clone https://github.com/abdullah-ateeq/WeekFind.git
   cd WeekFind
   ```
2. Open the project in Android Studio.
3. Sync Gradle and build the project:
   ```bash
   ./gradlew assembleDebug
   ```
4. Run on your device or emulator.

---

## 📄 License

```text
Copyright 2026 WeekFind

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
either express or implied.
See the License for the specific language governing permissions and limitations
under the License.
```
