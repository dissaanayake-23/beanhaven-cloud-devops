const API_BASE_URL = 'http://localhost:8080/api/bookings';

// Load bookings when page loads
document.addEventListener('DOMContentLoaded', loadBookings);

async function loadBookings() {
    try {
        showMessage('Loading bookings...', 'info');
        
        const response = await fetch(API_BASE_URL);
        
        if (!response.ok) {
            throw new Error(`Failed to load bookings: ${response.status}`);
        }
        
        const bookings = await response.json();
        displayBookings(bookings);
        
        if (bookings.length === 0) {
            showMessage('No bookings found.', 'info');
        } else {
            document.getElementById('message').style.display = 'none';
        }
    } catch (error) {
        showMessage(`Error loading bookings: ${error.message}`, 'danger');
        console.error('Error:', error);
    }
}

function displayBookings(bookings) {
    const tbody = document.querySelector('#bookingsTable tbody');
    tbody.innerHTML = '';
    
    if (bookings.length === 0) {
        tbody.innerHTML = '<tr><td colspan="9" class="text-center py-4">No bookings found</td></tr>';
        return;
    }
    
    // Sort by date (newest first)
    bookings.sort((a, b) => new Date(b.bookingDate) - new Date(a.bookingDate));
    
    bookings.forEach(booking => {
        const row = document.createElement('tr');
        
        // Format status badge
        const statusClass = `status-${booking.status.toLowerCase()}`;
        const statusHTML = `<span class="status-badge ${statusClass}">${booking.status}</span>`;
        
        // Action buttons
        const actions = `
            <div class="btn-group">
                <button class="btn btn-sm btn-outline-info action-btn" onclick="viewBookingDetails(${booking.id})" title="View">
                    <i class="fas fa-eye"></i>
                </button>
                ${booking.status === 'PENDING' ? `
                    <button class="btn btn-sm btn-outline-success action-btn" onclick="updateBookingStatus(${booking.id}, 'CONFIRMED')" title="Confirm">
                        <i class="fas fa-check"></i>
                    </button>
                ` : ''}
                ${booking.status !== 'CANCELLED' ? `
                    <button class="btn btn-sm btn-outline-danger action-btn" onclick="updateBookingStatus(${booking.id}, 'CANCELLED')" title="Cancel">
                        <i class="fas fa-times"></i>
                    </button>
                ` : ''}
            </div>
        `;
        
        row.innerHTML = `
            <td>${booking.id}</td>
            <td>${booking.fullName}</td>
            <td>${booking.email}</td>
            <td>${booking.bookingDate}</td>
            <td>${booking.preferredTime}</td>
            <td>${booking.numberOfGuests}</td>
            <td>${statusHTML}</td>
            <td><code>${booking.bookingReference || 'N/A'}</code></td>
            <td>${actions}</td>
        `;
        
        tbody.appendChild(row);
    });
    
    // Initialize DataTable
    if ($.fn.DataTable) {
        $('#bookingsTable').DataTable({
            order: [[0, 'desc']],
            pageLength: 10,
            responsive: true
        });
    }
}

async function updateBookingStatus(bookingId, newStatus) {
    if (!confirm(`Are you sure you want to change status to ${newStatus}?`)) {
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE_URL}/${bookingId}/status?status=${newStatus}`, {
            method: 'PATCH',
            headers: {
                'Accept': 'application/json'
            }
        });
        
        if (response.ok) {
            showMessage(`✅ Booking status updated to ${newStatus}`, 'success');
            setTimeout(() => loadBookings(), 1000); // Reload after 1 second
        } else {
            throw new Error('Failed to update status');
        }
    } catch (error) {
        showMessage(`❌ Error: ${error.message}`, 'danger');
    }
}

function viewBookingDetails(bookingId) {
    // In a real app, you would fetch and show detailed view
    alert(`Booking ID: ${bookingId}\n\nFeature: Detailed view would show here.\n\nIn a complete system, this would open a modal with all booking details.`);
}

function showMessage(text, type) {
    const messageDiv = document.getElementById('message');
    messageDiv.textContent = text;
    messageDiv.className = `alert alert-${type}`;
    messageDiv.style.display = 'block';
}