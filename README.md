# 🚀 PDF Render: A Modern Android Journey 🎨

<p align="center">
  <img src="https://img.shields.io/badge/Kotlin-2.0-purple.svg" alt="Kotlin">
  <img src="https://img.shields.io/badge/Jetpack_Compose-Latest-green.svg" alt="Compose">
  <img src="https://img.shields.io/badge/Architecture-MVVM-blue.svg" alt="MVVM">
  <img src="https://img.shields.io/badge/UI/UX-Neon_Glassmorphism-pink.svg" alt="UI">
  <img src="https://img.shields.io/badge/Platform-Android_15-orange.svg" alt="Android 15">
</p>

---

## 🌟 Project Brief
**PDF Render** is a high-performance, visually stunning Android application developed as a personal education project. The primary goal of this project is to master **Modern Android Development (MAD)** practices, explore the latest **Android 15 APIs**, and push the boundaries of **Jetpack Compose** animations and UI/UX design.

This isn't just a PDF reader; it's an exploration of how high-performance document rendering can coexist with a vibrant, neon-modern aesthetic (Neon-Glassmorphism).

---

## 🏆 MAD Score (Modern Android Development)
| Category | Score | Details |
| :--- | :--- | :--- |
| **Language** | 🚀 100% | Pure Kotlin with Coroutines and Flows. |
| **UI** | ✨ 100% | 100% Jetpack Compose with complex animations. |
| **Architecture** | 🏛️ 90% | Strict MVVM with Repository pattern. |
| **Jetpack** | 📦 95% | Lifecycle, ViewModel, Compose-BOM, Material 3. |
| **Android 15** | 📱 100% | Integrated latest `PdfRenderer.Page.searchText` API. |

---


## 📸 Visual Showcase

<p align="center">
  <img src="https://github.com/user-attachments/assets/4a1704a4-7a35-4650-9682-b59351519657" width="24%">
  <img src="https://github.com/user-attachments/assets/50443579-c6d6-4de1-88f2-b372172eb4a2" width="24%">
  <img src="https://github.com/user-attachments/assets/729e086f-040c-43bd-b169-9afbaa46d918" width="24%">
</p>

---

## 🛠️ Technical Architecture

### 🏛️ MVVM Pattern
The application follows a clean separation of concerns to ensure scalability and testability.

```mermaid
graph TD
    subgraph View_Layer
        A[PdfScreen.kt] --> B[Components.kt]
    end
    
    subgraph ViewModel_Layer
        C[PdfViewModel.kt]
    end
    
    subgraph Data_Layer
        D[PdfRepository.kt]
        E[PdfRenderer Framework]
    end

    A <--> C
    C <--> D
    D <--> E
```

### 🔄 Data Flow (PDF Rendering & Search)
```mermaid
sequenceDiagram
    participant User
    participant UI as Compose UI
    participant VM as PdfViewModel
    participant REPO as PdfRepository
    participant OS as Android Framework

    User->>UI: Select PDF File
    UI->>VM: loadPdf(uri)
    VM->>REPO: openDocument(uri)
    REPO->>OS: ParcelFileDescriptor
    OS-->>REPO: PdfRenderer Instance
    REPO-->>VM: Page Count & Metadata
    VM-->>UI: Update Success State
    
    Note over User,OS: Searching for Text
    
    User->>UI: Input Search Query
    UI->>VM: onSearchQueryChanged(query)
    VM->>REPO: searchText(query)
    REPO->>OS: PdfRenderer.Page.searchText()
    OS-->>REPO: List of MatchBounds
    REPO-->>VM: SearchResults
    VM-->>UI: Update Highlights (Canvas Overlay)
```

---

## ✨ Features & Learning Objectives

### 1. High-Performance Rendering
- **Learning Objective**: Memory-efficient bitmap handling.
- **Implementation**: Utilizes a `LazyColumn` for virtualization and scales bitmaps (2x) for Retina-level clarity without crashing the heap.

### 2. Android 15 Search API 🔍
- **Learning Objective**: Working with cutting-edge system APIs.
- **Implementation**: Integrates `android.graphics.pdf.PdfRenderer.Page.searchText` to find and extract text coordinates in real-time.

### 3. Neon-Glassmorphism UI 🎨
- **Learning Objective**: Advanced Compose Graphics & Animations.
- **Implementation**: 
    - **Animated Gradients**: Custom background shaders that flow between Deep Space and Indigo.
    - **Glassmorphism**: Top bars with real-time blur and translucency.
    - **Pulse Animations**: Infinite transitions using `animateColor` and `animateFloat`.

---

## 📦 Dependencies
- `androidx.compose.ui`: Modern UI Toolkit
- `androidx.lifecycle.viewmodel-compose`: Architecture support
- `androidx.graphics.pdf`: Framework PDF handling
- `kotlinx.coroutines`: Asynchronous processing
- `androidx.compose.material.icons.extended`: Vibrant icon sets

---

## 👨‍💻 About the Developer
This project is part of my professional development portfolio. I am dedicated to mastering the Android ecosystem and staying ahead of the curve with the latest Google technologies.

---
<p align="center">
  <b>Built with Kotlin</b>
</p>


---
