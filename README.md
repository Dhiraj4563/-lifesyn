# LifeSync 📱

> **"One App. Your Entire Life. Perfectly Organized."**

LifeSync is an all-in-one native Android personal life management application designed to organize professional deliverables, personal tasks, meetings, family commitments, upcoming trips, bill reminders, notes, and daily habit routines into a single intuitive dashboard.

---

## 🔒 100% Local Storage & Privacy First

- **Zero Cloud Databases**: All personal data is stored strictly on your device using **Room Database (SQLite)**.
- **Complete Privacy**: No telemetry, no external accounts, no cloud sync, and zero internet AI processing of private notes or finances.
- **Offline Always**: Fully functional without any internet connection.
- **Manual Backup & Restore**: Export your complete database to verified JSON files and restore anytime with built-in schema validation and merge protection.
- **No Automatic Deletion**: Completed tasks, paid payments, historical events, notes, and habits are **never** automatically cleared.

---

## ✨ Features & Modules

- **🏠 Home Dashboard**: Time-aware greeting, profile metrics, 6 interactive stat cards (Work Tasks, Meetings, Personal, Payments Due, Trips, Events), and Today's Schedule timeline.
- **📅 Native Calendar**: Month picker, dynamic day strip, 4 view modes (**Day, Week, Month, Agenda**), overlapping event conflict detection, and event scheduling.
- **✅ Task Management**: Category filtering (Work, Personal, Family, Finance, Travel, Shopping), priority badges (Urgent, High, Medium, Low), starred tasks, and completed task history.
- **💳 Payment Reminders**: Tracks recurring bills (Electricity, Rent, Credit Cards, Mobile, EMI, Subscriptions) with status indicators, due dates, reminder alerts, and 1-tap paid toggles.
- **✈️ Travel Planner**: Destination management, budget tracking, status badges (Planning, Confirmed, Booked, Completed), and packing checklists.
- **👥 Family & Friends**: Important contacts, relationship tagging, birthday and anniversary countdowns, and call reminders.
- **📝 Notes & Daily Scratchpad**: Categorized personal notes, pinned quick-access ideas, and full-text search.
- **🎯 Goals & Habit Tracker**: Daily routines, milestone percentages, streak counters (🔥), and 1-tap progress increments.
- **📊 Productivity Analytics**: 100% locally calculated metrics for task completion, work vs. personal distribution, and monthly financial commitments.
- **⚡ Smart Quick Add (+)**: Bottom sheet with a **local rule-based Natural Language Parser** (e.g., *"Pay rent 15000 on November 5"* or *"Meeting tomorrow at 10am"*) with confirmation preview before saving.
- **🔔 Local Notifications & Alarms**: Exact scheduling via `AlarmManager` and `NotificationManager`, with auto-rescheduling on device reboot via `BootReceiver`.
- **🔍 Global Offline Search**: Fast search across all modules with category filters.

---

## 🛠️ Tech Stack & Architecture

- **Language**: Kotlin 2.2+
- **UI Framework**: Jetpack Compose (Material Design 3)
- **Local Persistence**: Android Room Database 2.7 with SQLite & Kotlin Symbol Processing (KSP)
- **Architecture**: Model-View-ViewModel (MVVM) with Repository Pattern
- **Reactive Streams**: Kotlin Coroutines & `StateFlow` / `Flow`
- **Navigation**: Jetpack Navigation Compose
- **Scheduling**: `AlarmManager` (Exact Alarms) & `BroadcastReceiver`
- **Theme**: Material 3 Dark Navy theme (`#0B1220`) with Purple Gradients and Light Mode support

---

## 🚀 Building & Running

### Requirements
- Android Studio Ladybug (or newer)
- Android SDK 36 (minSdk 24)
- JDK 11 or JDK 17

### Steps
1. Clone the repository:
   ```bash
   git clone https://github.com/YOUR_USERNAME/lifesync-android.git
   ```
2. Open the project in **Android Studio**.
3. Let Gradle sync project dependencies.
4. Run on an Android device or emulator with API 24+.

Or build via command line:
```bash
./gradlew assembleDebug
```
The APK will be generated at `app/build/outputs/apk/debug/app-debug.apk`.

---

## 📄 License
Distributed under the MIT License.
