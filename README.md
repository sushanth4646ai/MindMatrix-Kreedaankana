# Kreeda Ankana

A dual-role sports arena booking and management Android application built with Kotlin and Jetpack Compose.

## Screenshots

| Customer Home | Ground Listing | Booking Flow |
|---------------|----------------|--------------|
| ![Home](screenshots/customer_home.jpeg) | ![Grounds](screenshots/auth.jpeg) | ![Booking](screenshots/booking.jpeg) |

| QR Entry Pass | Owner Dashboard | Slot Management |
|---------------|-----------------|-----------------|
| ![QR](screenshots/qr_pass.jpeg) | ![Dashboard](screenshots/owner_dashboard.jpeg) | ![Slots](screenshots/owner_slots.jpeg) |

| Analytics |
|-----------|
| ![Analytics](screenshots/owner_analytics.jpeg) |

## Overview

Kreeda Ankana connects players with sports ground owners through a unified platform. Customers can discover venues, book slots, manage teams, and generate QR entry passes. Ground owners can manage properties, configure availability, approve bookings, run tournaments, and track revenue — all with offline-first reliability and real-time Firebase sync.

## Tech Stack

| Layer | Technology |
|-------|------------|
| **Language** | Kotlin 1.9.20 |
| **UI** | Jetpack Compose + Material 3 |
| **Architecture** | MVVM + Repository Pattern + Clean Architecture |
| **Local Database** | Room 2.6.1 (18 entities, 15 DAOs) |
| **Remote Database** | Firebase Firestore |
| **Authentication** | Firebase Authentication |
| **Navigation** | Jetpack Navigation Compose |
| **Async** | Kotlin Coroutines + Flow |
| **QR Generation** | ZXing 3.5.2 |
| **QR Scanning** | CameraX + ML Kit Barcode Scanning |
| **PDF** | Android PdfDocument API |
| **Charts** | MPAndroidChart 3.1.0 |
| **Image Loading** | Coil 2.5.0 |
| **Animations** | Lottie Compose 6.3.0 |
| **Notifications** | Firebase Cloud Messaging |
| **Blockchain** | Hyperledger Fabric via Retrofit |
| **Background Sync** | WorkManager |
| **DI** | ServiceLocator + ViewModelFactory |

## Features

### Customer

**Authentication & Onboarding**
- Role-based signup/login (Customer / Ground Owner)
- Firebase Email/Password authentication
- Onboarding flow for first-time users
- Session management with SharedPreferences

**Home & Discovery**
- "Village Hub" dashboard with search bar
- Live Broadcasting carousel with real-time match scores
- Sport filter chips (Cricket, Football, Badminton, Volleyball, Tennis)
- Popular grounds list from Room DB synced with Firestore
- My Passes count and Rank display

**Ground Listing & Details**
- Browse grounds filtered by sport type
- Ground detail screen with images, amenities, pricing, and rules
- Image gallery support
- Favorite/bookmark grounds

**5-Step Booking Engine**
1. **Sport** — Select from Football, Cricket, Basketball, Tennis, Badminton, Volleyball
2. **Date** — Horizontal scroll of 14 available days
3. **Time Slot** — 2-column grid (6 AM – 10 PM, 2-hour intervals) with availability indicators
4. **Team** — Select existing team or create new one
5. **Payment** — Summary with confirm button and processing dialog
- Progress stepper (SPORT → DATE → TIME → TEAM → PAY)
- Double-booking prevention via Room DAO queries
- Persistent booking via Room BookingEntity

**Team Management**
- Create teams with name, sport selector, captain name/phone
- Dynamic member list with add/remove
- Sport-specific player count validation:
  - Tennis / Badminton: min 1
  - Football / Cricket: min 11
  - Basketball: min 5
  - Volleyball: min 6
- Persistent team storage (TeamEntity + TeamDao)

**QR Entry Pass**
- Animated QR card with NeonGreen pulsing glow
- Self-contained Base64 JSON QR (no server lookup)
- Booking detail card (team, captain, venue, date, time, amount)
- Team members list
- Share via WhatsApp, Email, SMS
- Download Entry Pass PDF and Invoice PDF with SHA-256 hashing
- FileProvider for Android 7+ compatibility

**QR Scanner (Check-In)**
- CameraX preview + ML Kit barcode scanning
- Decodes Base64 JSON back to BookingEntity
- Check-in flow with status validation
- Owner-side QR scanner for venue check-in

**Booking History & Passes**
- Past bookings list with status
- Digital passes gallery
- Re-booking from history

**Community & Challenges**
- Challenge board for team-to-team challenges
- Firestore real-time updates
- Accept/decline challenges

**Live Matches**
- Watch live match scores
- Versus screen for match comparisons
- Real-time score updates via Firestore

**Notifications**
- In-app notification screen
- Firebase Cloud Messaging
- Booking confirmation, reminders, promotional alerts

**Profile**
- User profile with stats tracking
- Edit profile screen
- My stats dashboard

### Ground Owner

**Dashboard**
- Revenue overview (Today's Revenue in Rs)
- Today's Bookings count
- Active Grounds count
- Quick Actions grid (2×4): Grounds, Slots, Bookings, Tournaments, Live Scores, Analytics, Payments, QR Scan
- Recent Activity feed (last 3 bookings)
- Animated stats cards with scale-on-press effects

**Ground Management**
- List of owned grounds with status indicators
- Ground creation/edit form:
  - Name, location, description
  - Sport type selector
  - Amenities chips (Parking, Washroom, Floodlights, Equipment Rental, Cafe, Changing Room)
  - Pricing fields (per hour, per session, membership)
  - Rules text area
  - Image URL input
- OwnerGroundEntity with syncStatus tracking
- GroundSyncRepository bridges owner→customer by copying Firestore grounds into Room GroundEntity

**Slot Management**
- Date picker to select a day
- 8-slot grid (6 AM – 10 PM, 2-hour intervals)
- Block/unblock individual slots
- Real-time Firestore sync on changes
- Color-coded status (available/blocked/booked)

**Booking Requests**
- List of pending booking requests
- Customer details, ground, slot, date
- Approve / Reject actions
- Status tracking (pending/approved/rejected)

**Tournament Management**
- Create tournaments with name, date range, sport type
- List of existing tournaments
- Knockout bracket generation
- Team registration management

**Live Scoring**
- Start/pause matches
- Real-time score entry
- Declare winner
- Team vs team display
- Firestore real-time sync for customer-side live viewing

**Analytics**
- Revenue bar charts (MPAndroidChart)
- Monthly/weekly revenue breakdowns
- Booking trends
- Ground-wise statistics
- OwnerAnalyticsViewModel with loadAnalytics()

**Payments**
- Payment request listing
- Approve/reject payments
- UPI ID management (add/view OwnerUpiEntity)
- PaymentEntity with status tracking
- Revenue tracking per payment

**QR Scanner**
- CameraX + ML Kit
- Venue check-in validation
- Owner-specific verification flow

## Architecture

### MVVM + Repository Pattern

```
UI Layer (Compose Screens)
    ↓ observes
ViewModels (StateFlow + MutableStateFlow)
    ↓ calls
Repositories (Auth, Booking, Owner, GroundSync, ...)
    ↓ reads/writes
Room DB (Local Source of Truth) ←→ Firestore (Cloud Sync)
```

### Clean Architecture (Owner Module)

The owner module follows Clean Architecture with:

- **Separate entities**: OwnerGroundEntity, OwnerVerificationEntity, OwnerProfileEntity
- **Separate DAOs**: OwnerGroundDao, TournamentDao, MatchDao, SlotBlockDao, RevenueDao, PaymentDao, VerificationDao
- **OwnerRepository**: All owner CRUD with Firebase sync
- **9 dedicated ViewModels**: OwnerDashboard, OwnerGround, OwnerSlot, OwnerBookingRequest, OwnerTournament, OwnerLiveScore, OwnerAnalytics, OwnerPayment, OwnerQRScanner

### Offline-First Sync

1. All data writes to Room DB first (local source of truth)
2. SyncQueueEntity tracks pending operations
3. SyncWorker (WorkManager) runs background sync to Firestore
4. GroundSyncRepository syncs owner-created grounds to customer GroundEntity for marketplace visibility
5. Conflict resolution: latest-timestamp-wins strategy
6. Room version 4 with fallbackToDestructiveMigration()

### QR System

QR codes are self-contained Base64 JSON — no server lookup required.
- `BookingEntity.toQRString()` serializes booking data → Base64 → QR bitmap
- Scanner decodes QR → Base64 → deserializes → BookingEntity → check-in

### Database (18 Room Entities)

**Customer**: GroundEntity, GroundImageEntity, BookingEntity, TeamEntity, ChallengeEntity, InvoiceEntity, NotificationEntity, UserEntity, FavoriteEntity, WaitlistEntity, AnnouncementEntity

**Owner**: OwnerGroundEntity, SlotBlockEntity, TournamentEntity, MatchEntity, PaymentEntity, RevenueEntity, OwnerUpiEntity, OwnerProfileEntity, OwnerVerificationEntity, VerificationLogEntity

**Sync**: SyncQueueEntity

### Navigation

- Single-activity architecture (MainActivity)
- AppNavigation composable with NavHost
- 30+ routes (auth, customer tabs, customer screens, owner screens)
- Bottom navigation (Home, Book, Passes, Versus, Live, Profile)
- Role-based routing (owner login → OwnerDashboard)

## Project Structure

```
app/src/main/java/com/kreedaankana/
├── KreedaApplication.kt              # App class, DB + repos init
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt            # Room DB (v4, 18 entities)
│   │   ├── dao/                       # 15 DAO interfaces
│   │   ├── entity/                    # 21 Entity classes
│   │   └── util/Converters.kt        # Room type converters
│   ├── model/                         # UI model classes
│   ├── repository/                    # 8 Repository classes
│   └── blockchain/                    # Hyperledger Fabric API
├── viewmodel/                         # 12 ViewModel classes
── ui/
│   ├── MainActivity.kt               # Single activity entry
│   ├── MainScreen.kt                 # Bottom nav host
│   ├── navigation/                   # 30+ route definitions
│   ├── theme/                        # Dark theme palette
│   ├── components/                   # Reusable composables
│   ├── splash/
│   ├── auth/                         # Login, Signup, Onboarding
│   ├── customer/                     # 15 customer screens
│   └── owner/                        # 10 owner screens
├── util/                             # Utilities (QR, PDF, Security)
├── service/                          # FCM service
├── worker/                           # Background sync worker
└── di/                               # Dependency container
```

## Firebase Setup

1. Create a Firebase project at https://console.firebase.google.com/
2. Add Android app with package name `com.kreedaankana`
3. Download `google-services.json` and place it in `app/` directory
4. Enable Authentication (Email/Password)
5. Create Firestore Database (start in test mode)
6. Enable Cloud Messaging for push notifications

## Build & Run

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Android SDK API 34

### Build
```bash
# Via Android Studio
# File → Open → Select project → Wait for Gradle sync
# Build → Build Bundle(s)/APK(s) → Build APK(s)

# Via Command Line
./gradlew assembleDebug
```

APK location: `app/build/outputs/apk/debug/app-debug.apk`

### Install
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

> **Note**: Replace `app/google-services.json` with your own Firebase config before building.

## License

This project is for educational and demonstration purposes.

Built with Kotlin and Jetpack Compose.
