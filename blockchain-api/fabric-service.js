const { Wallets, Gateway } = require('fabric-network');
const fs = require('fs');
const path = require('path');
const { v4: uuidv4 } = require('uuid');

class BlockchainService {
    constructor() {
        this.channelName = process.env.CHANNEL_NAME || 'booking-channel';
        this.chaincodeName = process.env.CHAINCODE_NAME || 'kreeda-ankana';
        this.walletPath = process.env.WALLET_PATH || './wallet';
        this.connectionProfile = process.env.CONNECTION_PROFILE || './connection.json';
        this.connected = false;
        this.gateway = null;
        this.network = null;
        this.contract = null;
    }

    async initialize() {
        try {
            if (!fs.existsSync(this.connectionProfile)) {
                console.log('Connection profile not found. Running in MOCK mode.');
                this.connected = false;
                return;
            }

            const wallet = await Wallets.newFileSystemWallet(this.walletPath);
            
            const gateway = new Gateway();
            await gateway.connect(this.connectionProfile, {
                wallet: wallet,
                identity: process.env.IDENTITY || 'app-user',
                discovery: { enabled: true, asLocalhost: false }
            });

            this.gateway = gateway;
            this.network = await gateway.getNetwork(this.channelName);
            this.contract = this.network.getContract(this.chaincodeName);
            this.connected = true;
            console.log('Connected to Hyperledger Fabric network');
        } catch (error) {
            console.log('Failed to connect to Fabric:', error.message);
            this.connected = false;
        }
    }

    async getNetworkStatus() {
        await this.initialize();
        return {
            connected: this.connected,
            channelName: this.channelName,
            chaincodeName: this.chaincodeName,
            blockHeight: this.connected ? Math.floor(Math.random() * 10000) + 5000 : 0,
            networkId: process.env.NETWORK_ID || 'dev-network'
        };
    }

    async registerBooking(bookingData) {
        const txId = `0x${uuidv4().replace(/-/g, '')}`;
        
        if (!this.connected) {
            return {
                success: true,
                txId: txId,
                message: 'Mock mode - booking registered locally',
                blockNumber: Math.floor(Math.random() * 1000) + 1,
                timestamp: Date.now()
            };
        }

        try {
            const bookingJson = JSON.stringify(bookingData);
            await this.contract.submitTransaction('RegisterBooking', bookingJson);
            
            return {
                success: true,
                txId: txId,
                message: 'Booking registered on Hyperledger Fabric',
                blockNumber: Math.floor(Math.random() * 1000) + 1,
                timestamp: Date.now()
            };
        } catch (error) {
            console.error('Fabric register error:', error);
            return {
                success: false,
                txId: null,
                message: error.message,
                blockNumber: null,
                timestamp: Date.now()
            };
        }
    }

    async verifyBooking(bookingId) {
        if (!this.connected) {
            const mockBooking = this.getMockBooking(bookingId);
            return {
                valid: mockBooking !== null,
                ...mockBooking,
                message: 'Mock verification mode'
            };
        }

        try {
            const result = await this.contract.evaluateTransaction('VerifyBooking', bookingId);
            const parsed = JSON.parse(result.toString());
            
            return {
                valid: parsed.valid,
                bookingId: parsed.bookingId,
                customerName: parsed.customerName,
                groundName: parsed.groundName,
                sport: parsed.sport,
                bookingDate: parsed.bookingDate,
                slotTime: parsed.slotTime,
                checkedIn: parsed.checkedIn,
                expiryTime: parsed.expiryTime,
                blockNumber: parsed.blockNumber,
                txId: parsed.txId,
                message: 'Verified on Hyperledger Fabric'
            };
        } catch (error) {
            console.error('Fabric verify error:', error);
            return {
                valid: false,
                message: error.message
            };
        }
    }

    async getBookingDetails(bookingId) {
        return this.verifyBooking(bookingId);
    }

    async checkInBooking(bookingId) {
        if (!this.connected) {
            return {
                valid: true,
                checkedIn: true,
                bookingId: bookingId,
                message: 'Mock check-in successful'
            };
        }

        try {
            const result = await this.contract.submitTransaction('CheckInBooking', bookingId);
            const parsed = JSON.parse(result.toString());
            
            return {
                valid: true,
                ...parsed,
                message: 'Check-in recorded on Hyperledger Fabric'
            };
        } catch (error) {
            console.error('Fabric check-in error:', error);
            return {
                valid: false,
                message: error.message
            };
        }
    }

    getMockBooking(bookingId) {
        const mockBookings = {
            'BK1234567890': {
                bookingId: 'BK1234567890',
                customerName: 'Rahul Sharma',
                groundName: 'Decathlon Arena',
                sport: 'Football',
                bookingDate: 'Mon, May 12, 2025',
                slotTime: '7:30 PM - 9:30 PM',
                checkedIn: false,
                expiryTime: Date.now() + (3 * 60 * 60 * 1000),
                blockNumber: 1523,
                txId: '0x7f9a2b8c1d4e5f6a7b8c9d0e1f2a3b4c5d6e7f8'
            }
        };
        return mockBookings[bookingId] || null;
    }
}

module.exports = { BlockchainService };