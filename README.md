# RomajiJP 🎵

![Logo Placeholder](https://via.placeholder.com/150)

**RomajiJP** is a modern Android application designed for music lovers and language learners alike. It simplifies the experience of finding songs and viewing lyrics, with a specialized engine to transliterate Japanese lyrics into high-quality Romanized (Romaji) text.

## 🌟 Key Features

*   **Global Song Search:** Integrated with the **iTunes Search API** to help you find tracks from any artist or album worldwide.
*   **Smart Lyrics Fetching:** Automatically retrieves plain-text lyrics via the **Lrclib API**.
*   **Intelligent Romanization:** Detects Japanese characters and converts them to Romaji using **Kuromoji-ipadic** (morphological analysis) and **ICU4J**. It correctly handles complex linguistic nuances like particle pronunciation (は → wa, へ → e, を → o).
*   **Personal Library:** Save your favorite tracks and their lyrics locally using **Room Database** for offline access.
*   **Search History:** Persistent history of your recent searches for quick navigation.
*   **Material 3 Design:** A sleek, modern interface featuring a responsive 2-column grid and full support for edge-to-edge displays.

## 🛠 Tech Stack

### Architecture & Core
*   **Language:** Kotlin
*   **Pattern:** MVVM (Model-View-ViewModel) + Repository Pattern
*   **Asynchronous Work:** Kotlin Coroutines & Flow for reactive data streams.

### Networking & Data
*   **Retrofit:** REST API communication.
*   **GSON:** JSON parsing and serialization.
*   **Room:** Local SQLite persistence for the library and cache.
*   **Glide:** Efficient image loading and caching for album artwork.

### UI & UX
*   **Material Design 3:** Latest Android design components.
*   **DataBinding:** Seamless binding of UI components to data sources.
*   **ConstraintLayout:** Responsive and flat view hierarchies.

### Linguistics
*   **Kuromoji:** Japanese morphological analysis.
*   **ICU4J:** Unicode transliteration and internationalization support.

## 🚀 Getting Started

### Prerequisites
*   Android Studio Ladybug (or newer)
*   JDK 17
*   Android SDK 24+

### Installation
1.  **Clone the repository:**
    ```bash
    git clone https://github.com/yourusername/RomajiJP.git
    ```
2.  **Open in Android Studio:**
    Launch Android Studio and select **Open**, then navigate to the cloned directory.
3.  **Build & Run:**
    Wait for the Gradle sync to complete and click the **Run** button (Shift + F10) to deploy the app to your emulator or physical device.

## 🏗 Architecture Overview

The app follows the **Clean Architecture** principles within the MVVM pattern:
1.  **UI Layer:** Activities (MainActivity, LyricsDisplay, LibraryActivity) observe StateFlows from ViewModels.
2.  **ViewModel Layer:** Manages UI state and interacts with the Repository.
3.  **Repository Layer:** The single source of truth that coordinates data between the **Remote API** (iTunes/Lrclib) and the **Local Database** (Room).
4.  **Linguistic Engine:** A utility layer that processes raw Japanese text into Romanized output using morphological tokens.

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
