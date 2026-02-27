// API Base URL - IMPORTANT: This connects to your Spring Boot backend
const API_BASE_URL = 'http://localhost:8080/api/bookings';

// Show message function
function showMessage(text, type) {
    const messageDiv = document.getElementById('message');
    messageDiv.textContent = text;
    messageDiv.className = `alert alert-${type}`;
    messageDiv.style.display = 'block';
    
    // Scroll to message
    messageDiv.scrollIntoView({ behavior: 'smooth', block: 'center' });
    
    // Auto hide after 5 seconds for success, 10 seconds for errors
    const hideTime = type === 'success' ? 5000 : 10000;
    setTimeout(() => {
        messageDiv.style.display = 'none';
    }, hideTime);
}

// Format date for display
function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', { 
        weekday: 'short', 
        year: 'numeric', 
        month: 'short', 
        day: 'numeric' 
    });
}

// Validate phone number
function validatePhoneNumber(phone) {
    const phoneRegex = /^[0-9]{10}$/;
    return phoneRegex.test(phone);
}

// Validate email
function validateEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}

// Handle form submission
document.getElementById('bookingForm').addEventListener('submit', async function(e) {
    e.preventDefault();
    
    // Get form values
    const bookingData = {
        fullName: document.getElementById('fullName').value.trim(),
        email: document.getElementById('email').value.trim(),
        phoneNumber: document.getElementById('phoneNumber').value.trim(),
        bookingDate: document.getElementById('bookingDate').value,
        preferredTime: document.getElementById('preferredTime').value,
        numberOfGuests: parseInt(document.getElementById('numberOfGuests').value),
        specialRequests: document.getElementById('specialRequests').value.trim()
    };

    // Validate all fields
    if (!bookingData.fullName) {
        showMessage('Please enter your full name', 'danger');
        document.getElementById('fullName').focus();
        return;
    }
    
    if (!bookingData.email) {
        showMessage('Please enter your email address', 'danger');
        document.getElementById('email').focus();
        return;
    }
    
    if (!validateEmail(bookingData.email)) {
        showMessage('Please enter a valid email address', 'danger');
        document.getElementById('email').focus();
        return;
    }
    
    if (!bookingData.phoneNumber) {
        showMessage('Please enter your phone number', 'danger');
        document.getElementById('phoneNumber').focus();
        return;
    }
    
    if (!validatePhoneNumber(bookingData.phoneNumber)) {
        showMessage('Phone number must be 10 digits (ex: 0768368000)', 'danger');
        document.getElementById('phoneNumber').focus();
        return;
    }
    
    if (!bookingData.bookingDate) {
        showMessage('Please select a booking date', 'danger');
        document.getElementById('bookingDate').focus();
        return;
    }
    
    if (!bookingData.preferredTime) {
        showMessage('Please select a preferred time', 'danger');
        document.getElementById('preferredTime').focus();
        return;
    }
    
    if (!bookingData.numberOfGuests || bookingData.numberOfGuests < 1 || bookingData.numberOfGuests > 10) {
        showMessage('Please select number of guests (1-10)', 'danger');
        document.getElementById('numberOfGuests').focus();
        return;
    }

    // Show loading state
    const submitBtn = document.querySelector('#bookingForm button[type="submit"]');
    const submitText = document.getElementById('submitText');
    const loadingSpinner = document.getElementById('loadingSpinner');
    
    submitText.textContent = 'Processing...';
    loadingSpinner.style.display = 'inline-block';
    submitBtn.disabled = true;

    try {
        console.log('Sending booking data:', bookingData);
        
        // Send POST request to backend
        const response = await fetch(API_BASE_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify(bookingData)
        });

        console.log('Response status:', response.status);
        
        if (response.ok) {
            const result = await response.json();
            console.log('Booking successful:', result);
            
            showMessage(`✅ Booking successful! Your reference: ${result.bookingReference || result.id}. You can book another table below.`, 'success');
            
            // Clear form fields but keep tomorrow's date
            document.getElementById('fullName').value = '';
            document.getElementById('email').value = '';
            document.getElementById('phoneNumber').value = '';
            document.getElementById('numberOfGuests').value = '';
            document.getElementById('preferredTime').value = '';
            document.getElementById('specialRequests').value = '';
            
            // Keep tomorrow's date as default for next booking
            const tomorrow = new Date();
            tomorrow.setDate(tomorrow.getDate() + 1);
            document.getElementById('bookingDate').value = tomorrow.toISOString().split('T')[0];
            
        } else {
            // Try to get error message
            let errorMessage = 'Booking failed';
            try {
                const errorData = await response.json();
                errorMessage = errorData.message || errorData.error || JSON.stringify(errorData);
            } catch (e) {
                errorMessage = await response.text();
            }
            
            console.error('Booking failed:', errorMessage);
            showMessage(`❌ ${errorMessage}`, 'danger');
        }
    } catch (error) {
        console.error('Network error:', error);
        showMessage(`❌ Network error: ${error.message}. Make sure backend is running at ${API_BASE_URL}`, 'danger');
    } finally {
        // Reset button
        submitText.textContent = 'Book Table Now';
        loadingSpinner.style.display = 'none';
        submitBtn.disabled = false;
    }
});

// Set minimum date to today on page load
window.addEventListener('DOMContentLoaded', function() {
    const today = new Date().toISOString().split('T')[0];
    const dateInput = document.getElementById('bookingDate');
    
    if (dateInput) {
        dateInput.min = today;
        
        // Set default date to tomorrow if not already set
        if (!dateInput.value) {
            const tomorrow = new Date();
            tomorrow.setDate(tomorrow.getDate() + 1);
            dateInput.value = tomorrow.toISOString().split('T')[0];
        }
    }
});