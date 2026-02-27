const express = require('express');
const cors = require('cors');
const bodyParser = require('body-parser');
const mysql = require('mysql2');

const app = express();
const PORT = 8080;

// Middleware
app.use(cors());
app.use(bodyParser.json());

// Database connection
const db = mysql.createConnection({
    host: 'localhost',
    user: 'root',      // Change to your MySQL username
    password: 'root',      // Change to your MySQL password
    database: 'coffeeshop'
});

db.connect((err) => {
    if (err) {
        console.error('Database connection failed:', err);
    } else {
        console.log('✅ Connected to MySQL database');
    }
});

// ========== COFFEE ENDPOINTS ==========

// GET ALL COFFEES
app.get('/api/coffees', (req, res) => {
    const query = 'SELECT * FROM coffees ORDER BY id DESC';
    
    db.query(query, (err, results) => {
        if (err) {
            console.error('Database error:', err);
            return res.status(500).json({ error: 'Database error' });
        }
        res.json(results);
    });
});

// GET SINGLE COFFEE
app.get('/api/coffees/:id', (req, res) => {
    const coffeeId = req.params.id;
    
    const query = 'SELECT * FROM coffees WHERE id = ?';
    db.query(query, [coffeeId], (err, results) => {
        if (err) {
            console.error('Database error:', err);
            return res.status(500).json({ error: 'Database error' });
        }
        if (results.length === 0) {
            return res.status(404).json({ error: 'Coffee not found' });
        }
        res.json(results[0]);
    });
});

// ADD NEW COFFEE
app.post('/api/coffees', (req, res) => {
    const coffee = req.body;
    
    console.log('Adding new coffee:', coffee);
    
    const query = `
        INSERT INTO coffees 
        (name, description, price, category, roast_type, origin, available) 
        VALUES (?, ?, ?, ?, ?, ?, ?)
    `;
    
    db.query(query, [
        coffee.name,
        coffee.description || '',
        coffee.price,
        coffee.category,
        coffee.roast_type || '',
        coffee.origin || '',
        coffee.available !== undefined ? coffee.available : true
    ], (err, result) => {
        if (err) {
            console.error('Database error:', err);
            return res.status(500).json({ error: 'Failed to add coffee: ' + err.message });
        }
        
        res.json({
            success: true,
            message: 'Coffee added successfully!',
            id: result.insertId
        });
    });
});

// UPDATE COFFEE
app.put('/api/coffees/:id', (req, res) => {
    const coffeeId = req.params.id;
    const coffee = req.body;
    
    const query = `
        UPDATE coffees SET 
            name = ?, 
            description = ?, 
            price = ?, 
            category = ?, 
            roast_type = ?, 
            origin = ?, 
            available = ?
        WHERE id = ?
    `;
    
    db.query(query, [
        coffee.name,
        coffee.description || '',
        coffee.price,
        coffee.category,
        coffee.roast_type || '',
        coffee.origin || '',
        coffee.available,
        coffeeId
    ], (err, result) => {
        if (err) {
            console.error('Database error:', err);
            return res.status(500).json({ error: 'Failed to update coffee' });
        }
        
        if (result.affectedRows === 0) {
            return res.status(404).json({ error: 'Coffee not found' });
        }
        
        res.json({ 
            success: true, 
            message: 'Coffee updated successfully!' 
        });
    });
});

// UPDATE COFFEE AVAILABILITY
app.put('/api/coffees/:id/availability', (req, res) => {
    const coffeeId = req.params.id;
    const { available } = req.body;
    
    const query = 'UPDATE coffees SET available = ? WHERE id = ?';
    db.query(query, [available, coffeeId], (err, result) => {
        if (err) {
            console.error('Database error:', err);
            return res.status(500).json({ error: 'Failed to update availability' });
        }
        
        res.json({ 
            success: true, 
            message: `Coffee marked as ${available ? 'available' : 'unavailable'}`
        });
    });
});

// DELETE COFFEE
app.delete('/api/coffees/:id', (req, res) => {
    const coffeeId = req.params.id;
    
    const query = 'DELETE FROM coffees WHERE id = ?';
    db.query(query, [coffeeId], (err, result) => {
        if (err) {
            console.error('Database error:', err);
            return res.status(500).json({ error: 'Failed to delete coffee' });
        }
        
        if (result.affectedRows === 0) {
            return res.status(404).json({ error: 'Coffee not found' });
        }
        
        res.json({ 
            success: true, 
            message: 'Coffee deleted successfully!' 
        });
    });
});

// ========== ORDER ENDPOINTS ==========

// GET ALL ORDERS
app.get('/api/orders', (req, res) => {
    const query = 'SELECT * FROM orders ORDER BY order_date DESC';
    
    db.query(query, (err, results) => {
        if (err) {
            console.error('Database error:', err);
            return res.status(500).json({ error: 'Database error' });
        }
        res.json(results);
    });
});

// GET ORDER BY ID
app.get('/api/orders/:id', (req, res) => {
    const orderId = req.params.id;
    
    const query = 'SELECT * FROM orders WHERE id = ?';
    db.query(query, [orderId], (err, results) => {
        if (err) {
            console.error('Database error:', err);
            return res.status(500).json({ error: 'Database error' });
        }
        if (results.length === 0) {
            return res.status(404).json({ error: 'Order not found' });
        }
        res.json(results[0]);
    });
});

// UPDATE ORDER STATUS
app.put('/api/orders/:id/status', (req, res) => {
    const orderId = req.params.id;
    const { status } = req.body;
    
    const validStatuses = ['PENDING', 'PROCESSING', 'COMPLETED', 'CANCELLED'];
    if (!validStatuses.includes(status)) {
        return res.status(400).json({ error: 'Invalid status' });
    }
    
    const query = 'UPDATE orders SET status = ? WHERE id = ?';
    db.query(query, [status, orderId], (err, result) => {
        if (err) {
            console.error('Database error:', err);
            return res.status(500).json({ error: 'Failed to update order status' });
        }
        
        res.json({ 
            success: true, 
            message: 'Order status updated successfully!' 
        });
    });
});

// ========== DASHBOARD STATS ==========
app.get('/api/dashboard/stats', (req, res) => {
    const queries = [
        'SELECT COUNT(*) as totalOrders FROM orders',
        'SELECT COUNT(*) as pendingOrders FROM orders WHERE status = "PENDING"',
        'SELECT COUNT(*) as completedOrders FROM orders WHERE status = "COMPLETED"',
        'SELECT COUNT(*) as totalCoffees FROM coffees'
    ];

    Promise.all(
        queries.map(query => 
            new Promise((resolve, reject) => {
                db.query(query, (err, results) => {
                    if (err) reject(err);
                    else resolve(results[0]);
                });
            })
        )
    ).then(results => {
        const stats = {
            totalOrders: results[0].totalOrders,
            pendingOrders: results[1].pendingOrders,
            completedOrders: results[2].completedOrders,
            totalCoffees: results[3].totalCoffees
        };
        res.json(stats);
    }).catch(err => {
        console.error('Error fetching stats:', err);
        res.status(500).json({ error: 'Failed to fetch stats' });
    });
});

// ========== ADMIN LOGIN ==========
app.post('/api/admin/login', (req, res) => {
    const { username, password } = req.body;
    
    // Simple hardcoded admin check
    if (username === 'admin' && password === 'admin123') {
        res.json({
            success: true,
            message: 'Login successful',
            user: {
                username: 'admin',
                name: 'Administrator'
            }
        });
    } else {
        res.status(401).json({
            success: false,
            message: 'Invalid credentials'
        });
    }
});

// ========== SERVER START ==========
app.listen(PORT, () => {
    console.log(`🚀 Server running on http://localhost:${PORT}`);
});