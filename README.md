<div align="center">

# ✨ Lumina Notes

**Capture your brilliance.**

A gorgeous, modern notes app for Android — built with Jetpack Compose — featuring categories, checklists, freehand sketch notes, cloud sync, and buttery-smooth transitions.

[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-BOM%202024.09-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Material 3](https://img.shields.io/badge/Material%20You-3-757575?style=for-the-badge&logo=materialdesign&logoColor=white)](https://m3.material.io)
[![Min SDK](https://img.shields.io/badge/minSdk-24-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com)
[![License](https://img.shields.io/badge/License-MIT-blue.svg?style=for-the-badge)](LICENSE)

</div>

---

## 📖 Overview

**Lumina Notes** is a beautifully designed, offline-first note-taking app that combines a polished Material 3 interface with real productivity features — categorized notes, interactive checklists, freehand drawings, color tagging, pinning, archiving, and optional cloud account sync. It's built entirely with modern Android tooling: 100% Kotlin, 100% Jetpack Compose, no XML layouts.

## 🖼️ Screenshots

<div align="center">
<table>
  <tr>
    <td align="center"><em>Onboarding</em></td>
    <td align="center"><em>Notes List</em></td>
    <td align="center"><em>Edit Note</em></td>
    <td align="center"><em>Draw Note</em></td>
  </tr>
  <tr>
    <td><img src="docs/screenshots/onboarding.png" width="200"/></td>
    <td><img src="docs/screenshots/notes_list.png" width="200"/></td>
    <td><img src="docs/screenshots/edit_note.png" width="200"/></td>
    <td><img src="docs/screenshots/draw_note.png" width="200"/></td>
  </tr>
</table>

<sub>📸 Add your own screenshots to <code>docs/screenshots/</code> — placeholders shown above.</sub>
</div>

---

## 🚀 Features

- 📝 **Rich Note Editing** — Create and edit notes with titles, content, custom colors, and categories
- ✅ **Interactive Checklists** — Turn any note into a task list with checkable items
- 🎨 **Draw Notes** — Freehand sketch canvas with undo, stroke color, and brush controls
- 🏷️ **Categories & Tags** — Organize notes with default and custom category chips
- 📌 **Pin & Archive** — Keep important notes at the top, tuck the rest away
- 🔍 **Instant Search** — Live filtering across titles, content, and categories
- 🌗 **Light & Dark Theme** — Full Material You dynamic theming support
- 🔐 **Secure Auth** — Email/password and Google Sign-In, powered by Firebase Authentication
- ☁️ **Cloud Sync** — Sync notes across devices tied to your account
- 🎬 **Onboarding Flow** — A smooth, animated first-run experience
- 💾 **Offline-First Storage** — All notes persisted locally with Room, so the app works without a connection

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| **Language** | [Kotlin](https://kotlinlang.org) |
| **UI Toolkit** | [Jetpack Compose](https://developer.android.com/jetpack/compose) + [Material 3](https://m3.material.io) |
| **Architecture** | MVVM (`ViewModel` + `StateFlow`) |
| **Local Database** | [Room](https://developer.android.com/training/data-storage/room) |
| **Networking** | [Retrofit](https://square.github.io/retrofit/) + [OkHttp](https://square.github.io/okhttp/) + [Moshi](https://github.com/square/moshi) |
| **Auth & Backend** | [Firebase Authentication](https://firebase.google.com/docs/auth) (Email + Google Sign-In), [Firebase AI](https://firebase.google.com/docs/ai-logic) |
| **Image Loading** | [Coil](https://coil-kt.github.io/coil/) |
| **Async** | Kotlin Coroutines & Flow |
| **Navigation** | [Navigation Compose](https://developer.android.com/jetpack/compose/navigation) |
| **Testing** | JUnit, Espresso, Robolectric, [Roborazzi](https://github.com/takahirom/roborazzi) (screenshot testing) |
| **Build System** | Gradle (Kotlin DSL) with Version Catalogs |

---

## 🏗️ Architecture

Lumina Notes follows an **MVVM** pattern with a single `NoteViewModel` as the source of truth for UI state, backed by a Room-powered repository layer.

```
com.example
├── data/
│   ├── NoteEntity.kt        # Room entity for a note
│   ├── NoteDao.kt           # Data access object (CRUD + Flow queries)
│   ├── NoteDatabase.kt      # Room database definition
│   └── NoteRepository.kt    # Single source of truth for note data
├── model/
│   └── ChecklistItem.kt     # Checklist item model (Moshi-serializable)
└── ui/
    ├── LuminaApp.kt         # Navigation graph / app entry composable
    ├── LuminaLogo.kt        # Brand logo composable
    ├── OnboardingScreen.kt  # First-run animated intro
    ├── LoginScreen.kt       # Email + Google authentication
    ├── NotesListScreen.kt   # Grid/list view, search & category filters
    ├── EditNoteScreen.kt    # Note editor with checklist support
    ├── DrawNoteScreen.kt    # Freehand canvas note editor
    ├── ProfileScreen.kt     # Account & settings
    ├── NoteViewModel.kt     # App-wide state & business logic
    └── theme/                # Color, typography, and Material 3 theme
```

---

## ⚡ Getting Started

### Prerequisites

- [Android Studio](https://developer.android.com/studio) (latest stable)
- JDK 11+
- An Android device or emulator running **API 24 (Android 7.0)** or higher
- A [Firebase project](https://console.firebase.google.com/) with Authentication (Email/Password + Google) enabled

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/mustafizur-web/lumina-notes.git
   cd lumina-notes
   ```

2. **Open in Android Studio**
   Select **Open**, then choose the project's root directory and let Gradle sync.

3. **Configure environment variables**
   Copy the example env file and fill in your keys:
   ```bash
   cp .env.example .env
   ```
   Then set your Gemini API key inside `.env`:
   ```
   GEMINI_API_KEY=your_key_here
   ```

4. **Add Firebase config**
   Download `google-services.json` from your Firebase project console and place it in the `app/` directory.

5. **Set your signing config** (for local development)
   In `app/build.gradle.kts`, the debug build uses the local `debugConfig`. For release builds, provide your keystore via the `KEYSTORE_PATH`, `STORE_PASSWORD`, and `KEY_PASSWORD` environment variables.

6. **Run the app**
   Select a device or emulator and hit **Run** ▶️ in Android Studio.

---

## 🔑 Environment Variables

| Variable | Description |
|---|---|
| `GEMINI_API_KEY` | API key used for AI-powered features via Firebase AI |
| `KEYSTORE_PATH` | (Release only) Path to your upload keystore |
| `STORE_PASSWORD` | (Release only) Keystore password |
| `KEY_PASSWORD` | (Release only) Key alias password |

---

## 🧪 Testing

Run unit tests:
```bash
./gradlew test
```

Run instrumented tests on a connected device/emulator:
```bash
./gradlew connectedAndroidTest
```

Run screenshot tests (Roborazzi):
```bash
./gradlew verifyRoborazziDebug
```

---

## 🗺️ Roadmap

- [ ] Rich text formatting (bold, italics, lists)
- [ ] Widget support for home screen quick-notes
- [ ] Note reminders & notifications
- [ ] Export notes to PDF / Markdown
- [ ] Collaborative / shared notes

---

## 🤝 Contributing

Contributions are welcome! To get started:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

Please make sure your code follows the existing style and that tests pass before submitting.

---

## 📄 License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.

---

<div align="center">

Made with 💜 by **[MustafiZ](https://github.com/mustafizur-web)**

If you like this project, consider giving it a ⭐!

</div>
