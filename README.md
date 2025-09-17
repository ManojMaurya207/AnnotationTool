# AnnotationTool

A simple Android annotation tool allowing users to draw, add shapes, arrows, freehand strokes, and text over images, with undo/redo support, tool/color/shape selection, and annotation persistence. Also supports exporting (flattening) the image with all annotations.

---

## 🧱 Features

- **Freehand drawing**  
  Draw smooth freehand paths over images with adjustable stroke width and color.

- **Shapes & Arrows**  
  Draw rectangles, circles, or arrows via drag gestures.

- **Text Annotations**  
  Add text annotations by tapping on the canvas when in Text mode.

- **Eraser Tool**  
  Erase with a clear brush (blend-mode or eraser strokes).

- **Undo / Redo**  
  Basic undo and redo for actions (adding drawings, text, etc.).

- **Persistent Storage**  
  Save annotations local to a project; load them back when reopening the project.

- **Export / Flatten Image** *(planned/add-on)*  
  Export the base image + annotations as a single flattened image (PNG/JPEG).

---

## ⚙ Architecture & Key Components

| Component | Responsibility |
|-----------|----------------|
| `DrawingState` / `DrawingAction` | Centralised state machine / reducer model: all user actions go through `onAction(...)`, and state includes current annotations, selected tool/color/shape etc. |
| `AnnotationViewModel` | Manages stacks (undo/redo), persistence (via DAO), loading/saving, and responds to `DrawingAction`. |
| `DrawingCanvas` composable | Renders the base image + stored annotations + previews; handles gesture input for drawing. Stateless with respect to app logic. |
| `ToolBar` composable | UI controls for selecting tool, color, shape, stroke width etc. Dispatches `DrawingAction`s. |
| Persistence | Uses Room/DAO (or a local database) to store `AnnotationEntity` per project. |

---

## 🚀 Getting Started

1. **Prerequisites**

   - Android Studio (latest stable, with Kotlin support)
   - Minimum SDK version as defined in `build.gradle`
   - Required permissions (read/write storage if exporting/saving to gallery)

2. **Setup**

   ```bash
   git clone https://github.com/ManojMaurya207/AnnotationTool.git
   cd AnnotationTool

