# Hyperledger Fabric Blockchain Integration

## Overview

This integration adds blockchain-based verification to the Kreeda Ankana booking system using Hyperledger Fabric. Each booking is registered on the Fabric network, creating an immutable record that can be verified at venue check-in.

## Architecture

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│   Android App   │────▶│   REST API      │────▶│  Hyperledger   │
│  (Kreeda APK)   │     │ (Node.js/Express)│     │     Fabric     │
└─────────────────┘     └─────────────────┘     └─────────────────┘
                              │
                              ▼
                       ┌─────────────────┐
                       │   Mock Mode     │
                       │ (Local Testing) │
                       └─────────────────┘
```

## Components

### Android App (`/app/src/main/java/com/kreedaankana/data/blockchain/`)

- **FabricBlockchainApi.kt** - Retrofit API interface for blockchain calls
- **BlockchainService.kt** - Singleton service managing blockchain communication

### Backend API (`/blockchain-api/`)

- **server.js** - Express.js REST API server
- **fabric-service.js** - Hyperledger Fabric chaincode interaction
- **connection.json.example** - Fabric network connection profile
- **.env.example** - Environment configuration

## Setup & Deployment

### 1. Run Backend API

```bash
cd blockchain-api
npm install
cp .env.example .env
# Edit .env with your Fabric network details
npm start
```

### 2. Configure Android App

Update `BlockchainService.kt` with your API base URL:

```kotlin
BlockchainService.initialize("http://YOUR_SERVER_IP:3000/")
```

### 3. Run in Mock Mode (Development)

The API automatically runs in mock mode if no Fabric connection profile is found:

```javascript
// fabric-service.js will return mock responses
// No Fabric network required for local testing
```

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/network/status` | Check Fabric network connectivity |
| POST | `/api/booking/register` | Register new booking on blockchain |
| GET | `/api/booking/verify/:bookingId` | Verify booking authenticity |
| GET | `/api/booking/:bookingId` | Get booking details from blockchain |
| POST | `/api/booking/checkin/:bookingId` | Record check-in on blockchain |

## Data Flow

### Booking Registration

1. User completes payment in app
2. App calls `/api/booking/register`
3. API submits transaction to Fabric network
4. Fabric chaincode stores booking in world state
5. API returns transaction ID (txId)
6. QR code includes txId for verification

### QR Verification

1. Venue staff scans customer QR code
2. App extracts booking data + txId from QR
3. If txId present, app calls `/api/booking/verify/:id`
4. API queries Fabric network for booking state
5. App displays verification result (BLOCKCHAIN VERIFIED)

## Security Features

- **Immutability** - Booking records cannot be altered once committed
- **Transparency** - All verification events are logged on-chain
- **Offline fallback** - App works without network (validates locally, marks as "offline mode")
- **Tamper detection** - QR contains hash that changes if data is modified

## Chaincode (Smart Contract)

Expected chaincode functions:

```javascript
// RegisterBooking(bookingJson) → txId
// VerifyBooking(bookingId) → { valid, bookingDetails }
// CheckInBooking(bookingId) → { success, timestamp }
// GetBookingDetails(bookingId) → bookingDetails
```

## Production Checklist

- [ ] Deploy Fabric network (Kubernetes/HLF operator)
- [ ] Install and instantiate chaincode
- [ ] Configure TLS certificates
- [ ] Set up CouchDB for world state
- [ ] Generate wallets for app identity
- [ ] Update API base URL in Android app
- [ ] Enable TLS in API configuration

## Mock Mode

For development without Fabric network:
- API returns mock transactions with random txIds
- QR verification uses local fallback
- App displays "Offline Mode" status

## Troubleshooting

**Connection refused**: Check server IP/port in `BlockchainService.kt`

**TLS errors**: Ensure TLS cert paths are correct in `connection.json`

**Mock mode always active**: Ensure `connection.json` is not found (or remove it for testing)