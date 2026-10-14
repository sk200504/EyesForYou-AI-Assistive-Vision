# EyesForYou — AI-Powered Assistive Vision

An Android-based assistive vision application designed to help visually impaired users understand their surroundings through AI-powered image analysis and spoken feedback.

## Overview

EyesForYou combines camera-based image acquisition, Gemini vision capabilities, contextual scene descriptions, and Android Text-to-Speech to make visual information more accessible.

The project explores how multimodal AI can turn visual input into natural-language information that users can listen to instead of relying entirely on visual output.

## Key Features

- **AI-Powered Scene Understanding:** Uses the Gemini API to analyze images and generate natural-language scene descriptions.
- **Camera Integration:** Captures visual input through the Android camera interface.
- **Object and Spatial Information:** Presents object-related information and position representations returned by the vision-analysis pipeline.
- **Spoken Feedback:** Converts supported descriptions into speech using Android Text-to-Speech.
- **Multiple Vision Modes:** Provides an interface for assistance, depth-related information, and text-reading workflows.
- **Interactive Android UI:** Built with Jetpack Compose, including scene information cards, visual overlays, and speech controls.
- **Fallback Handling:** Includes a local fallback analyzer for predefined example scenes when live AI analysis is unavailable.

> Note: The local fallback uses predefined example data; it should not be described as real-time object detection.

## Technology Stack

- **Language:** Kotlin
- **Platform:** Android
- **UI:** Jetpack Compose
- **Camera:** CameraX
- **AI / Vision:** Google Gemini API
- **Networking and Data:** HTTP requests and JSON response parsing
- **Speech:** Android Text-to-Speech
- **Build System:** Gradle with Kotlin DSL
- **Development Environment:** Android Studio

## System Architecture

```text
Android Camera
      |
      v
Camera Capture Interface
      |
      v
Vision Repository
      |
      +--------------------+
      |                    |
      v                    v
Gemini Vision API    Local Fallback Analyzer
      |                    |
      +---------+----------+
                |
                v
       Vision Result Models
                |
                v
       ViewModel and UI
                |
       +--------+--------+
       |                 |
       v                 v
 Scene Information   Text-to-Speech
 and Visual Overlay  Spoken Feedback
```

The architecture separates camera input, vision analysis, data models, interface management, and audio feedback to make the application easier to maintain and extend.

## Project Structure

```text
EyesForYou/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/
│   │   │   │   ├── data/
│   │   │   │   ├── model/
│   │   │   │   ├── service/
│   │   │   │   ├── ui/
│   │   │   │   └── MainActivity.kt
│   │   │   ├── res/
│   │   │   └── AndroidManifest.xml
│   │   ├── androidTest/
│   │   └── test/
│   └── build.gradle.kts
├── gradle/
├── .env.example
├── .gitignore
├── build.gradle.kts
├── gradle.properties
├── settings.gradle.kts
└── README.md
```

## Getting Started

### Prerequisites

- Android Studio
- A compatible Android device or emulator
- A valid Gemini API key for live AI analysis
- Internet connectivity for Gemini API requests

### Installation

1. Clone the repository:

   ```bash
   git clone https://github.com/sk200504/EyesForYou-AI-Assistive-Vision.git
   ```

2. Open Android Studio.

3. Select **Open** and choose the cloned `EyesForYou` project directory.

4. Allow Gradle to sync and download the required dependencies.

5. Configure the Gemini API key using the configuration method supported by the project. Refer to `.env.example` for the expected variable name and setup guidance.

6. Run the application on a compatible Android device or emulator.

> Never commit a real API key. An API key embedded in a distributed Android application can be extracted; production deployments should use a secure backend to protect credentials.


## Future Improvements

- Integrate and validate dedicated on-device object detection.
- Improve spatial reasoning and object-position estimation.
- Develop priority-based narration to reduce repetitive speech.
- Evaluate response latency, AI usage, and speech relevance.
- Improve privacy, reliability, and accessibility.

## Applications

Potential applications include assistive technology, accessible mobile computing, multimodal AI interfaces, and AI-assisted scene understanding.

## Repository

**GitHub:** https://github.com/sk200504/EyesForYou-AI-Assistive-Vision

