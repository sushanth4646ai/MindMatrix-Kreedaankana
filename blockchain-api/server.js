require('dotenv').config();
const express = require('express');
const cors = require('cors');
const bodyParser = require('body-parser');
const { BlockchainService } = require('./fabric-service');

const app = express();
const PORT = process.env.PORT || 3000;

app.use(cors());
app.use(bodyParser.json());

const blockchain = new BlockchainService();

app.get('/api/network/status', async (req, res) => {
    try {
        const status = await blockchain.getNetworkStatus();
        res.json(status);
    } catch (error) {
        res.status(500).json({ 
            connected: false, 
            message: error.message 
        });
    }
});

app.post('/api/booking/register', async (req, res) => {
    try {
        const {
            bookingId, customerName, customerPhone, teamName,
            groundName, groundAddress, sport, bookingDate,
            slotTime, amount, players, timestamp
        } = req.body;

        if (!bookingId || !customerName || !groundName) {
            return res.status(400).json({
                success: false,
                message: 'Missing required fields'
            });
        }

        const result = await blockchain.registerBooking({
            bookingId,
            customerName,
            customerPhone,
            teamName,
            groundName,
            groundAddress,
            sport,
            bookingDate,
            slotTime,
            amount,
            players,
            timestamp
        });

        res.json(result);
    } catch (error) {
        console.error('Register booking error:', error);
        res.status(500).json({
            success: false,
            message: error.message
        });
    }
});

app.get('/api/booking/verify/:bookingId', async (req, res) => {
    try {
        const { bookingId } = req.params;
        const result = await blockchain.verifyBooking(bookingId);
        res.json(result);
    } catch (error) {
        console.error('Verify booking error:', error);
        res.status(500).json({
            valid: false,
            message: error.message
        });
    }
});

app.get('/api/booking/:bookingId', async (req, res) => {
    try {
        const { bookingId } = req.params;
        const result = await blockchain.getBookingDetails(bookingId);
        res.json(result);
    } catch (error) {
        console.error('Get booking error:', error);
        res.status(500).json({
            message: error.message
        });
    }
});

app.post('/api/booking/checkin/:bookingId', async (req, res) => {
    try {
        const { bookingId } = req.params;
        const result = await blockchain.checkInBooking(bookingId);
        res.json(result);
    } catch (error) {
        console.error('Check-in error:', error);
        res.status(500).json({
            valid: false,
            message: error.message
        });
    }
});

app.listen(PORT, () => {
    console.log(`Kreeda Ankana Fabric API running on port ${PORT}`);
    console.log(`Network status endpoint: http://localhost:${PORT}/api/network/status`);
});