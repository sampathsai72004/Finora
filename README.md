# 🐟 Finora — Smart Offline Fish Catch Analyzer

**Finora** is an AI-powered mobile application that instantly identifies fish species, counts them, evaluates freshness, and estimates total weight — **completely offline**.  
It brings **digital proof, fair pricing, and traceability** right to the docks and fishing boats.

---

## 📱 What Finora Does

- 🎥 **Instant Detection** – Point the camera at a pile of fish; Finora detects each fish in real-time.  
- 🐠 **Species Identification** – Labels each detected fish from a known species list using on-device AI.  
- 💧 **Freshness Scoring** – Analyzes visual cues (eye clarity, gill color, skin gloss) to rate freshness.  
- ⚖️ **Weight Estimation** – Uses scale markers or ARCore Depth API to estimate size and weight.  
- 📍 **Proof & Traceability** – Automatically saves results with **GPS location**, **timestamp**, and **photo evidence**.  
- 🔒 **Offline-First Design** – Works fully **offline**, syncing later when the network is available.

---

## 🧠 How It Works (Under the Hood)

| Function | Technology | Description |
|-----------|-------------|--------------|
| **Detection** | `YOLOv8-Tiny` / `MobileNet-SSD` | Real-time fish detection using optimized object detection. |
| **Species Classification** | `MobileNetV3` / `EfficientNet-Lite` | Classifies species from cropped detections. |
| **Freshness Estimation** | Custom CNN model | Rates fish freshness based on visual quality cues. |
| **Weight/Size Estimation** | `ARCore Depth API` + scale marker | Estimates length, volume, and weight using species-specific length–weight curves. |
| **Data Storage** | `SQLite (local)` + Cloud Sync | Offline-first design; data synced later for traceability. |

---

## 🚀 Key Features

- ⚡ Real-time on-device inference (no internet required)  
- 🧮 Species count, total weight, and freshness scoring  
- 🗺️ Auto-save with GPS, timestamp, and camera metadata  
- 📂 Offline data logging and cloud sync for compliance/export  
- 🔋 Optimized for mid-range Android devices (quantized INT8 models)

---

## 🛠️ Tech Stack

- **Mobile Platform:** Android (Kotlin / Java)
- **AI Frameworks:** TensorFlow Lite / NCNN
- **Models:**
  - Detection → YOLOv8-Tiny
  - Classification → EfficientNet-Lite / MobileNetV3
  - Freshness → Custom CNN
- **Database:** SQLite (Offline-first)
- **Cloud Sync:** Firebase / REST API
- **AR & Depth:** ARCore Depth API (optional)

---

## 🌍 Why It’s Useful

### 🎣 For Fishermen
- Get instant, transparent pricing at the dock.  
- Digital proof of species and quantity for fair trade.  

### 🧾 For Regulators
- Verify catches on the spot with geo-tagged and timestamped data.  
- Prevent illegal or misreported fishing activities.  

### 🌐 For Exporters
- Reliable traceability for export compliance.  
- Builds trust with verified freshness and source data.

---

## 🧩 System Flow


---

## 📸 Usage Flow

1. **Open Finora App**
2. **Point the camera** at the fish pile
3. **Capture 3–5 angles** (with a known scale object like a ruler)
4. **View instant results** – species list, count, freshness, and weight
5. **Save record** – automatically stores with GPS and timestamp
6. **Sync** later to cloud (optional)

---

## 🧪 Rollout Plan

1. **Phase 1:** Common target species (e.g., tuna, mackerel, sardine, pomfret)  
2. **Phase 2:** Expand to shrimp, crab, and other seafood  
3. **Phase 3:** Integrate freshness grade certification for export standards

---

## 📂 Repository Structure

Finora/
├── app/ # Android app source (Java/Kotlin)
├── models/ # YOLOv8, MobileNetV3, Freshness CNN models
├── data/ # Sample fish images and metadata
├── docs/ # Research papers, dataset references
├── assets/ # Icons, splash screens, etc.
├── README.md # You are here
└── LICENSE


---

## 🧠 Model Optimization

- Models are **quantized (INT8)** for faster inference.  
- Uses **TensorFlow Lite Delegate** or **NCNN Vulkan backend** for speed.  
- Average inference time: **<100ms per fish** on mid-range phones.  

---

## 🔄 Sync and Traceability

- Offline records are stored in **SQLite**.  
- On network availability, data is synced securely to **Firebase** or **custom REST API**.  
- Supports integration with **market pricing systems** and **fisheries regulation databases**.

---

> 🌊 *Finora — Empowering fishermen with AI-driven traceability, transparency, and trust.*

