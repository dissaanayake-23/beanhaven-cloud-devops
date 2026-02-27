// js/menu.js - SIMPLIFIED VERSION THAT WILL WORK

// Global variables
let cart = [];
let coffeeItems = [];

// Load coffee menu when page loads
document.addEventListener('DOMContentLoaded', function() {
    console.log('DOM loaded, initializing menu...');
    loadCoffeeMenu();
    setupEventListeners();
});

// Load coffee items
async function loadCoffeeMenu() {
    console.log('Loading coffee menu...');
    
    try {
        // Try to load from API
        const response = await fetch('http://localhost:8080/api/coffees');
        
        if (response.ok) {
            coffeeItems = await response.json();
            console.log('API loaded items:', coffeeItems);
            displayCoffeeItems(coffeeItems);
            
            // Hide loading message
            document.getElementById('menuMessage').style.display = 'none';
        } else {
            throw new Error('API returned ' + response.status);
        }
        
    } catch (error) {
        console.log('API failed, using sample data:', error);
        
        // Show error message
        document.getElementById('menuMessage').innerHTML = `
            <i class="fas fa-exclamation-triangle me-2"></i>
            Could not load menu. Showing sample items instead.
        `;
        document.getElementById('menuMessage').className = 'alert alert-warning';
        
        // Show sample data immediately
        showSampleCoffeeItems();
    }
}

// Display coffee items
function displayCoffeeItems(items) {
    console.log('Displaying items:', items);
    const container = document.getElementById('coffeeItemsContainer');
    container.innerHTML = '';
    
    if (!items || items.length === 0) {
        console.log('No items to display');
        return;
    }
    
    items.forEach(coffee => {
        const coffeeCard = createCoffeeCard(coffee);
        container.appendChild(coffeeCard);
    });
}

// Create coffee card HTML - SIMPLIFIED VERSION
function createCoffeeCard(coffee) {
    const col = document.createElement('div');
    col.className = 'col-md-6 mb-4';
    col.setAttribute('data-category', coffee.category || 'Other');
    
    // Format price to LKR
    const price = coffee.price ? coffee.price.toFixed(2) : '0.00';
    
    col.innerHTML = `
        <div class="coffee-card">
            <div class="coffee-img">
                <i class="fas fa-mug-hot"></i>
            </div>
            <div class="coffee-body">
                <div class="d-flex justify-content-between align-items-start mb-2">
                    <h5 class="coffee-title">${coffee.name || 'Coffee'}</h5>
                    <div class="coffee-price">LKR ${price}</div>
                </div>
                
                <p class="coffee-description">${coffee.description || 'A delicious coffee crafted with care.'}</p>
                
                <div class="coffee-details">
                    ${coffee.category ? `<span><i class="fas fa-tag"></i>${coffee.category}</span>` : ''}
                    ${coffee.roastType ? `<span><i class="fas fa-fire"></i>${coffee.roastType} Roast</span>` : ''}
                    ${coffee.origin ? `<span><i class="fas fa-globe"></i>${coffee.origin}</span>` : ''}
                </div>
                
                <div class="d-flex justify-content-between align-items-center">
                    <div class="quantity-control">
                        <button class="quantity-btn" onclick="decreaseQuantity(${coffee.id})">-</button>
                        <input type="number" class="quantity-input" id="qty-${coffee.id}" value="0" min="0" max="10" readonly>
                        <button class="quantity-btn" onclick="increaseQuantity(${coffee.id})">+</button>
                    </div>
                    <button class="btn btn-outline" onclick="addToCart(${coffee.id})">
                        <i class="fas fa-plus me-1"></i>Add to Cart
                    </button>
                </div>
            </div>
        </div>
    `;
    
    return col;
}

// Setup event listeners
function setupEventListeners() {
    // Place order button
    document.getElementById('placeOrderBtn').addEventListener('click', placeOrder);
    
    // Clear order button
    document.getElementById('clearOrderBtn').addEventListener('click', clearOrder);
    
    // Input validation
    document.getElementById('customerName').addEventListener('input', validateOrder);
    document.getElementById('customerPhone').addEventListener('input', validateOrder);
}

// Sample data - SHOWS UP IMMEDIATELY
function showSampleCoffeeItems() {
    console.log('Showing sample coffee items');
    
    const sampleItems = [
        {
            id: 1,
            name: 'Espresso',
            description: 'Strong and bold, our classic espresso shot.',
            price: 450.00,
            category: 'Espresso',
            roastType: 'Dark',
            origin: 'Italy',
            available: true
        },
        {
            id: 2,
            name: 'Double Espresso',
            description: 'Twice the strength for true coffee lovers.',
            price: 650.00,
            category: 'Espresso',
            roastType: 'Dark',
            origin: 'Italy',
            available: true
        },
        {
            id: 3,
            name: 'Cappuccino',
            description: 'Perfect blend of espresso, steamed milk, and foam.',
            price: 750.00,
            category: 'Cappuccino',
            roastType: 'Medium',
            origin: 'Italy',
            available: true
        },
        {
            id: 4,
            name: 'Vanilla Cappuccino',
            description: 'Cappuccino with a touch of vanilla syrup.',
            price: 850.00,
            category: 'Cappuccino',
            roastType: 'Medium',
            origin: 'Italy',
            available: true
        },
        {
            id: 5,
            name: 'Caramel Latte',
            description: 'Smooth latte with sweet caramel flavor.',
            price: 900.00,
            category: 'Latte',
            roastType: 'Medium',
            origin: 'USA',
            available: true
        },
        {
            id: 6,
            name: 'Americano',
            description: 'Espresso diluted with hot water for a milder taste.',
            price: 600.00,
            category: 'Americano',
            roastType: 'Medium',
            origin: 'USA',
            available: true
        },
        {
            id: 7,
            name: 'Mocha',
            description: 'Chocolate lovers delight with espresso and cocoa.',
            price: 950.00,
            category: 'Specialty',
            roastType: 'Medium',
            origin: 'Yemen',
            available: true
        },
        {
            id: 8,
            name: 'Caramel Macchiato',
            description: 'Vanilla-flavored milk marked with espresso and caramel.',
            price: 1000.00,
            category: 'Specialty',
            roastType: 'Light',
            origin: 'USA',
            available: true
        },
        {
            id: 9,
            name: 'Iced Coffee',
            description: 'Chilled coffee with milk and sugar, served over ice.',
            price: 800.00,
            category: 'Specialty',
            roastType: 'Light',
            origin: 'USA',
            available: true
        },
        {
            id: 10,
            name: 'Filter Coffee',
            description: 'Traditional Sri Lankan style filter coffee.',
            price: 550.00,
            category: 'Filter Coffee',
            roastType: 'Dark',
            origin: 'Sri Lanka',
            available: true
        }
    ];
    
    coffeeItems = sampleItems;
    displayCoffeeItems(sampleItems);
    
    // Make sure container is visible
    document.getElementById('coffeeItemsContainer').style.display = 'flex';
}

// Add item to cart
function addToCart(coffeeId) {
    console.log('Adding to cart:', coffeeId);
    const coffee = coffeeItems.find(item => item.id === coffeeId);
    if (!coffee) {
        console.error('Coffee not found:', coffeeId);
        return;
    }
    
    const quantityInput = document.getElementById(`qty-${coffeeId}`);
    let quantity = parseInt(quantityInput.value) + 1;
    if (quantity > 10) quantity = 10;
    quantityInput.value = quantity;
    
    // Update or add to cart
    const existingItem = cart.find(item => item.id === coffeeId);
    if (existingItem) {
        existingItem.quantity = quantity;
    } else {
        cart.push({
            id: coffee.id,
            name: coffee.name,
            price: coffee.price,
            quantity: quantity
        });
    }
    
    updateOrderSummary();
    validateOrder();
    showOrderMessage(`Added ${coffee.name} to cart`, 'success');
}

// Increase quantity
function increaseQuantity(coffeeId) {
    const quantityInput = document.getElementById(`qty-${coffeeId}`);
    let quantity = parseInt(quantityInput.value) + 1;
    if (quantity > 10) quantity = 10;
    quantityInput.value = quantity;
    
    const existingItem = cart.find(item => item.id === coffeeId);
    if (existingItem) {
        existingItem.quantity = quantity;
        updateOrderSummary();
        validateOrder();
    }
}

// Decrease quantity
function decreaseQuantity(coffeeId) {
    const quantityInput = document.getElementById(`qty-${coffeeId}`);
    let quantity = parseInt(quantityInput.value) - 1;
    if (quantity < 0) quantity = 0;
    quantityInput.value = quantity;
    
    const existingItem = cart.find(item => item.id === coffeeId);
    if (existingItem) {
        if (quantity === 0) {
            // Remove from cart
            cart = cart.filter(item => item.id !== coffeeId);
            showOrderMessage('Item removed from cart', 'info');
        } else {
            existingItem.quantity = quantity;
        }
        updateOrderSummary();
        validateOrder();
    }
}

// Update order summary
function updateOrderSummary() {
    const container = document.getElementById('orderItemsContainer');
    const subtotalElement = document.getElementById('subtotal');
    const serviceChargeElement = document.getElementById('serviceCharge');
    const totalElement = document.getElementById('totalAmount');
    
    if (cart.length === 0) {
        container.innerHTML = '<p class="text-muted text-center">Your cart is empty</p>';
        subtotalElement.textContent = 'LKR 0.00';
        serviceChargeElement.textContent = 'LKR 0.00';
        totalElement.textContent = 'LKR 0.00';
        return;
    }
    
    // Calculate totals
    let subtotal = 0;
    let html = '';
    
    cart.forEach(item => {
        const itemTotal = item.price * item.quantity;
        subtotal += itemTotal;
        
        html += `
            <div class="order-item">
                <div>
                    <strong>${item.name}</strong><br>
                    <small>LKR ${item.price.toFixed(2)} × ${item.quantity}</small>
                </div>
                <div>
                    <strong>LKR ${itemTotal.toFixed(2)}</strong>
                </div>
            </div>
        `;
    });
    
    container.innerHTML = html;
    
    // Calculate charges (minimum order LKR 500)
    const serviceCharge = subtotal * 0.05;
    const total = subtotal + serviceCharge;
    
    // Update display
    subtotalElement.textContent = `LKR ${subtotal.toFixed(2)}`;
    serviceChargeElement.textContent = `LKR ${serviceCharge.toFixed(2)}`;
    totalElement.textContent = `LKR ${total.toFixed(2)}`;
}

// Validate order before submission
function validateOrder() {
    const name = document.getElementById('customerName').value.trim();
    const phone = document.getElementById('customerPhone').value.trim();
    const placeOrderBtn = document.getElementById('placeOrderBtn');
    
    // Basic phone validation (10 digits)
    const isPhoneValid = /^[0-9]{10}$/.test(phone);
    
    // Calculate subtotal
    const subtotal = cart.reduce((sum, item) => sum + (item.price * item.quantity), 0);
    
    // Enable button if valid
    const isValid = name.length > 0 && isPhoneValid && cart.length > 0 && subtotal >= 500;
    
    placeOrderBtn.disabled = !isValid;
    
    return isValid;
}

// Place order - SIMPLIFIED
async function placeOrder() {
    console.log('Placing order...');
    
    if (!validateOrder()) {
        alert('Please fill all details correctly and meet minimum order of LKR 500');
        return;
    }
    
    const name = document.getElementById('customerName').value.trim();
    const phone = document.getElementById('customerPhone').value.trim();
    const orderType = document.getElementById('orderType').value;
    
    // Calculate total
    let subtotal = cart.reduce((sum, item) => sum + (item.price * item.quantity), 0);
    let total = subtotal * 1.05;
    
    // Show loading
    const placeOrderBtn = document.getElementById('placeOrderBtn');
    const originalText = placeOrderBtn.innerHTML;
    placeOrderBtn.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i>Processing...';
    placeOrderBtn.disabled = true;
    
    // Simulate API call with timeout
    setTimeout(() => {
        // Reset button
        placeOrderBtn.innerHTML = originalText;
        placeOrderBtn.disabled = false;
        
        // Show success message
        alert(`🎉 Thank you for your order!\n\nCustomer: ${name}\nPhone: ${phone}\nOrder Type: ${orderType}\nTotal: LKR ${total.toFixed(2)}\n\n⏰ We'll prepare your order shortly.`);
        
        // Clear cart
        clearOrder();
        
    }, 1500);
}

// Clear order
function clearOrder() {
    if (cart.length === 0) {
        showOrderMessage('Cart is already empty', 'info');
        return;
    }
    
    if (!confirm('Are you sure you want to clear your cart?')) return;
    
    cart = [];
    
    // Reset quantity inputs
    document.querySelectorAll('.quantity-input').forEach(input => {
        input.value = '0';
    });
    
    updateOrderSummary();
    validateOrder();
    showOrderMessage('Cart cleared successfully', 'info');
}

// Show order message
function showOrderMessage(text, type) {
    const messageDiv = document.getElementById('orderMessage');
    messageDiv.innerHTML = `<i class="fas fa-${type === 'success' ? 'check-circle' : 'exclamation-circle'} me-2"></i>${text}`;
    messageDiv.className = `alert alert-${type}`;
    messageDiv.style.display = 'block';
    
    setTimeout(() => {
        messageDiv.style.display = 'none';
    }, 3000);
}

// Debug: Force show sample data
console.log('Menu.js loaded successfully!');