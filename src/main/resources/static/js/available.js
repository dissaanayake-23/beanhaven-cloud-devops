// API Base URL
const API_BASE_URL = 'http://localhost:8080/api/bookings';

// All possible time slots
const ALL_TIME_SLOTS = [
    { time: '09:00', label: '09:00 AM', period: 'Morning' },
    { time: '11:00', label: '11:00 AM', period: 'Late Morning' },
    { time: '13:00', label: '01:00 PM', period: 'Afternoon' },
    { time: '15:00', label: '03:00 PM', period: 'Late Afternoon' },
    { time: '17:00', label: '05:00 PM', period: 'Evening' },
    { time: '19:00', label: '07:00 PM', period: 'Night' }
];

// Show message function
function showMessage(text, type) {
    const messageDiv = document.getElementById('message');
    messageDiv.textContent = text;
    messageDiv.className = `alert alert-${type}`;
    messageDiv.style.display = 'block';
    
    setTimeout(() => {
        messageDiv.style.display = 'none';
    }, 5000);
}

// Format date for display
function formatDateForDisplay(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', { 
        weekday: 'long', 
        year: 'numeric', 
        month: 'long', 
        day: 'numeric' 
    });
}

// Check availability
async function checkAvailability() {
    const dateInput = document.getElementById('checkDate');
    const selectedDate = dateInput.value;
    
    if (!selectedDate) {
        showMessage('Please select a date', 'warning');
        return;
    }
    
    try {
        showMessage('Checking availability...', 'info');
        
        // Get available time slots from API
        const response = await fetch(`${API_BASE_URL}/available-times/${selectedDate}`);
        
        if (!response.ok) {
            throw new Error('Failed to check availability');
        }
        
        const availableTimes = await response.json();
        displayTimeSlots(selectedDate, availableTimes);
        
    } catch (error) {
        console.error('Error:', error);
        showMessage(`Error: ${error.message}`, 'danger');
        
        // Fallback: Show all slots as available if API fails
        displayTimeSlots(selectedDate, ALL_TIME_SLOTS.map(slot => slot.time));
    }
}

// Display time slots
function displayTimeSlots(date, availableTimes) {
    const container = document.getElementById('timeSlotsContainer');
    const dateDisplay = document.getElementById('dateDisplay');
    
    // Format and display the selected date
    const formattedDate = formatDateForDisplay(date);
    dateDisplay.textContent = `Available slots for ${formattedDate}`;
    
    // Clear container
    container.innerHTML = '';
    
    if (!availableTimes || availableTimes.length === 0) {
        container.innerHTML = `
            <div class="no-slots">
                <i class="fas fa-times-circle fa-3x mb-3 text-danger"></i>
                <h4>No available slots</h4>
                <p>All time slots are booked for ${formattedDate}</p>
                <p class="text-muted">Try selecting a different date</p>
            </div>
        `;
        return;
    }
    
    // Group slots by period
    const slotsByPeriod = {};
    ALL_TIME_SLOTS.forEach(slot => {
        const isAvailable = availableTimes.includes(slot.time);
        if (!slotsByPeriod[slot.period]) {
            slotsByPeriod[slot.period] = [];
        }
        slotsByPeriod[slot.period].push({ ...slot, available: isAvailable });
    });
    
    // Display slots grouped by period
    Object.entries(slotsByPeriod).forEach(([period, slots]) => {
        const periodSection = document.createElement('div');
        periodSection.className = 'mb-4';
        
        let periodHTML = `
            <h5 class="mb-3">
                <i class="fas fa-sun text-warning me-2"></i>${period}
            </h5>
            <div class="row">
        `;
        
        slots.forEach(slot => {
            const isAvailable = slot.available;
            const statusText = isAvailable ? 'Available' : 'Booked';
            const statusClass = isAvailable ? 'time-available' : 'time-booked';
            const statusIcon = isAvailable ? 'fa-check-circle text-success' : 'fa-times-circle text-danger';
            
            periodHTML += `
                <div class="col-md-4 col-sm-6 mb-3">
                    <div class="time-slot ${statusClass}">
                        <div class="d-flex justify-content-between align-items-start mb-2">
                            <div>
                                <h5 class="mb-1">${slot.label}</h5>
                                <p class="mb-0">
                                    <i class="fas ${statusIcon} availability-icon"></i>
                                    ${statusText}
                                </p>
                            </div>
                        </div>
                        <div class="d-grid">
                            ${isAvailable ? 
                                `<a href="booking.html?date=${date}&time=${slot.time}" class="btn btn-book">
                                    <i class="fas fa-calendar-plus me-2"></i>Book Now
                                </a>` : 
                                `<button class="btn btn-disabled" disabled>
                                    <i class="fas fa-ban me-2"></i>Unavailable
                                </button>`
                            }
                        </div>
                    </div>
                </div>
            `;
        });
        
        periodHTML += '</div>';
        periodSection.innerHTML = periodHTML;
        container.appendChild(periodSection);
    });
    
    // Show summary
    const availableCount = availableTimes.length;
    const totalCount = ALL_TIME_SLOTS.length;
    
    const summary = document.createElement('div');
    summary.className = 'alert alert-info mt-4';
    summary.innerHTML = `
        <i class="fas fa-info-circle me-2"></i>
        <strong>${availableCount} out of ${totalCount} slots available</strong> 
        for ${formattedDate}
        ${availableCount > 0 ? 
            ' - Click "Book Now" to reserve your table' : 
            ' - Please try another date'
        }
    `;
    container.appendChild(summary);
    
    // Clear any previous messages
    document.getElementById('message').style.display = 'none';
}

// Check for date and time parameters in URL
function checkUrlParameters() {
    const urlParams = new URLSearchParams(window.location.search);
    const dateParam = urlParams.get('date');
    const timeParam = urlParams.get('time');
    
    if (dateParam) {
        document.getElementById('checkDate').value = dateParam;
        
        // Auto-check availability after a short delay
        setTimeout(() => {
            checkAvailability();
            
            // Scroll to specific time slot if time is provided
            if (timeParam) {
                setTimeout(() => {
                    const timeSlot = document.querySelector(`[data-time="${timeParam}"]`);
                    if (timeSlot) {
                        timeSlot.scrollIntoView({ behavior: 'smooth', block: 'center' });
                        timeSlot.classList.add('highlight');
                    }
                }, 500);
            }
        }, 300);
    }
}

// Initialize when page loads
document.addEventListener('DOMContentLoaded', function() {
    // Set default date to today
    const today = new Date().toISOString().split('T')[0];
    const dateInput = document.getElementById('checkDate');
    
    if (dateInput && !dateInput.value) {
        dateInput.value = today;
    }
    
    // Check URL parameters
    checkUrlParameters();
    
    // Set up date input change event
    if (dateInput) {
        dateInput.addEventListener('change', checkAvailability);
    }
    
    // Auto-check availability after page loads
    setTimeout(checkAvailability, 800);
});