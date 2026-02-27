// =============================================
//          API CONFIGURATION
// =============================================
const API_BASE_URL    = 'http://localhost:8080';
const BOOKINGS_API    = `${API_BASE_URL}/api/bookings`;
const COFFEE_API      = `${API_BASE_URL}/api/coffees`;

// =============================================
//          AUTHENTICATION HELPERS
// =============================================
function getToken() {
    return localStorage.getItem('token');
}

function isAuthenticated() {
    const token = getToken();
    if (!token) return false;

    const loginTime = localStorage.getItem('loginTime');
    if (!loginTime) return true; // no timeout enforced if missing

    const hoursSinceLogin = (Date.now() - Number(loginTime)) / (1000 * 60 * 60);
    if (hoursSinceLogin >= 8) {
        localStorage.removeItem('token');
        localStorage.removeItem('adminName');
        localStorage.removeItem('loginTime');
        return false;
    }

    return true;
}

function redirectToLogin() {
    window.location.href = 'admin-login.html';
}

function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('adminName');
    localStorage.removeItem('loginTime');
    redirectToLogin();
}

// Add Authorization header helper
function getAuthHeaders() {
    const token = getToken();
    return {
        'Content-Type': 'application/json',
        ...(token ? { 'Authorization': `Bearer ${token}` } : {})
    };
}

// =============================================
//          UI HELPERS
// =============================================
function showMessage(containerId, text, type = 'info') {
    const container = document.getElementById(containerId);
    if (!container) return;

    container.innerHTML = `
        <div class="alert alert-${type} alert-dismissible fade show" role="alert">
            ${text}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    `;
}

function switchTab(tabId) {
    const tabElement = document.querySelector(`[data-bs-target="#${tabId}"], [href="#${tabId}"]`);
    if (tabElement) {
        const bsTab = new bootstrap.Tab(tabElement);
        bsTab.show();
    }
}

// =============================================
//          DASHBOARD DATA LOADING
// =============================================
async function loadDashboard() {
    if (!isAuthenticated()) {
        redirectToLogin();
        return;
    }

    try {
        const [bookingsRes, coffeesRes] = await Promise.all([
            fetch(BOOKINGS_API, { headers: getAuthHeaders() }),
            fetch(COFFEE_API,   { headers: getAuthHeaders() })
        ]);

        if (!bookingsRes.ok || !coffeesRes.ok) {
            throw new Error('Failed to load dashboard data');
        }

        const bookings = await bookingsRes.json();
        const coffees  = await coffeesRes.json();

        // Update counters
        document.getElementById('totalBookings')?.setAttribute('data-count', bookings.length) ||
        (document.getElementById('totalBookings').textContent = bookings.length);

        document.getElementById('totalCoffee')?.setAttribute('data-count', coffees.length) ||
        (document.getElementById('totalCoffee').textContent = coffees.length);

        const pending   = bookings.filter(b => b.status === 'PENDING').length;
        const confirmed = bookings.filter(b => b.status === 'CONFIRMED').length;

        document.getElementById('pendingBookings').textContent   = pending;
        document.getElementById('confirmedBookings').textContent = confirmed;

        loadRecentActivity(bookings);

    } catch (err) {
        console.error('Dashboard load error:', err);
        showMessage('dashboardMessage', 'Could not load dashboard data. Please try again.', 'danger');
    }
}

function loadRecentActivity(bookings) {
    const container = document.getElementById('recentActivity');
    if (!container) return;

    const recent = bookings.slice(-5).reverse();

    if (recent.length === 0) {
        container.innerHTML = '<p class="text-muted text-center py-4">No recent bookings</p>';
        return;
    }

    let html = '';
    recent.forEach(b => {
        const statusClass = {
            PENDING:   'bg-warning',
            CONFIRMED: 'bg-success',
            CANCELLED: 'bg-danger'
        }[b.status] || 'bg-secondary';

        html += `
            <div class="list-group-item list-group-item-action">
                <div class="d-flex justify-content-between align-items-start">
                    <div>
                        <h6 class="mb-1">${b.fullName || 'Guest'}</h6>
                        <p class="mb-1 small">${b.numberOfGuests} guest(s) • ${b.preferredTime || 'N/A'}</p>
                    </div>
                    <small class="text-muted">${new Date(b.bookingDate).toLocaleDateString()}</small>
                </div>
                <small>
                    Status: <span class="badge ${statusClass}">${b.status}</span>
                </small>
            </div>
        `;
    });

    container.innerHTML = html;
}

// =============================================
//          BOOKINGS MANAGEMENT
// =============================================
async function loadBookings() {
    if (!isAuthenticated()) return;

    try {
        const res = await fetch(BOOKINGS_API, { headers: getAuthHeaders() });
        if (!res.ok) throw new Error('Failed to load bookings');

        const bookings = await res.json();
        displayBookings(bookings);
    } catch (err) {
        console.error(err);
        showMessage('bookingsMessage', 'Error loading bookings', 'danger');
    }
}

function displayBookings(bookings) {
    const tbody = document.querySelector('#bookingsTable tbody');
    if (!tbody) return;

    tbody.innerHTML = '';

    if (bookings.length === 0) {
        tbody.innerHTML = '<tr><td colspan="9" class="text-center py-4">No bookings found</td></tr>';
        return;
    }

    // Newest first
    bookings.sort((a, b) => new Date(b.bookingDate) - new Date(a.bookingDate));

    bookings.forEach(b => {
        const row = document.createElement('tr');
        const statusClass = `status-${b.status.toLowerCase()}`;

        let actions = `
            <button class="btn btn-sm btn-info me-1" onclick="viewBookingDetails(${b.id})" title="View">
                <i class="fas fa-eye"></i>
            </button>
        `;

        if (b.status === 'PENDING') {
            actions += `
                <button class="btn btn-sm btn-success me-1" onclick="updateBookingStatus(${b.id}, 'CONFIRMED')">
                    <i class="fas fa-check"></i> Confirm
                </button>
            `;
        }
        if (b.status !== 'CANCELLED') {
            actions += `
                <button class="btn btn-sm btn-danger" onclick="updateBookingStatus(${b.id}, 'CANCELLED')">
                    <i class="fas fa-times"></i> Cancel
                </button>
            `;
        }

        row.innerHTML = `
            <td>${b.id}</td>
            <td>${b.fullName}</td>
            <td>${b.email || '-'}</td>
            <td>${b.bookingDate}</td>
            <td>${b.preferredTime || '-'}</td>
            <td>${b.numberOfGuests}</td>
            <td><span class="badge ${statusClass}">${b.status}</span></td>
            <td>${b.bookingReference || 'N/A'}</td>
            <td>${actions}</td>
        `;

        tbody.appendChild(row);
    });
}

async function updateBookingStatus(id, status) {
    if (!confirm(`Set status to ${status}?`)) return;

    try {
        const res = await fetch(`${BOOKINGS_API}/${id}/status?status=${status}`, {
            method: 'PATCH',
            headers: getAuthHeaders()
        });

        if (!res.ok) throw new Error('Update failed');

        showMessage('bookingsMessage', `Booking #${id} → ${status}`, 'success');
        setTimeout(loadBookings, 800);
    } catch (err) {
        showMessage('bookingsMessage', err.message, 'danger');
    }
}

function viewBookingDetails(id) {
    alert(`View details for booking #${id}\n(This would open a modal in a full implementation)`);
}

// =============================================
//          COFFEE MENU MANAGEMENT
// =============================================
async function loadCoffeeMenu() {
    if (!isAuthenticated()) return;

    try {
        const res = await fetch(COFFEE_API, { headers: getAuthHeaders() });
        if (!res.ok) throw new Error('Failed to load coffees');

        const items = await res.json();
        displayCoffeeItems(items);
    } catch (err) {
        showMessage('coffeeMessage', 'Error loading menu', 'danger');
    }
}

function displayCoffeeItems(items) {
    const tbody = document.querySelector('#coffeeTable tbody');
    if (!tbody) return;

    tbody.innerHTML = '';

    if (items.length === 0) {
        tbody.innerHTML = '<tr><td colspan="7" class="text-center py-4">No coffee items</td></tr>';
        return;
    }

    items.forEach(item => {
        const row = document.createElement('tr');
        const availBadge = item.available
            ? '<span class="badge bg-success">Available</span>'
            : '<span class="badge bg-danger">Unavailable</span>';

        row.innerHTML = `
            <td>${item.id}</td>
            <td><strong>${item.name}</strong></td>
            <td>${item.description || '-'}</td>
            <td>LKR ${Number(item.price).toFixed(2)}</td>
            <td>${item.category || '-'}</td>
            <td>${availBadge}</td>
            <td>
                <button class="btn btn-sm btn-warning me-1" onclick="editCoffee(${item.id})">
                    <i class="fas fa-edit"></i>
                </button>
                <button class="btn btn-sm btn-danger" onclick="deleteCoffee(${item.id})">
                    <i class="fas fa-trash"></i>
                </button>
            </td>
        `;
        tbody.appendChild(row);
    });
}

document.getElementById('addCoffeeForm')?.addEventListener('submit', async function(e) {
    e.preventDefault();

    if (!isAuthenticated()) return;

    const formData = {
        name:        document.getElementById('coffeeName')?.value.trim(),
        price:       Number(document.getElementById('coffeePrice')?.value),
        description: document.getElementById('coffeeDescription')?.value.trim() || null,
        category:    document.getElementById('coffeeCategory')?.value,
        roastType:   document.getElementById('coffeeRoast')?.value || null,
        origin:      document.getElementById('coffeeOrigin')?.value.trim() || null,
        available:   document.getElementById('coffeeAvailable')?.checked ?? true
    };

    if (!formData.name || isNaN(formData.price) || formData.price <= 0) {
        showMessage('coffeeMessage', 'Name and valid price are required', 'danger');
        return;
    }

    try {
        const res = await fetch(COFFEE_API, {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify(formData)
        });

        if (!res.ok) {
            const errText = await res.text();
            throw new Error(errText || 'Failed to add coffee');
        }

        const saved = await res.json();
        showMessage('coffeeMessage', `Added: ${saved.name}`, 'success');

        this.reset();
        setTimeout(loadCoffeeMenu, 1000);
    } catch (err) {
        showMessage('coffeeMessage', err.message, 'danger');
    }
});

function editCoffee(id) {
    alert(`Edit coffee #${id} (edit modal would appear here)`);
}

async function deleteCoffee(id) {
    if (!confirm('Delete this coffee item permanently?')) return;

    try {
        const res = await fetch(`${COFFEE_API}/${id}`, {
            method: 'DELETE',
            headers: getAuthHeaders()
        });

        if (!res.ok) throw new Error('Delete failed');

        showMessage('coffeeMessage', 'Coffee deleted', 'success');
        setTimeout(loadCoffeeMenu, 800);
    } catch (err) {
        showMessage('coffeeMessage', err.message, 'danger');
    }
}

// =============================================
//          INITIALIZATION
// =============================================
document.addEventListener('DOMContentLoaded', () => {
    if (!isAuthenticated()) {
        redirectToLogin();
        return;
    }

    // Logout button
    document.getElementById('logoutBtn')?.addEventListener('click', e => {
        e.preventDefault();
        if (confirm('Log out now?')) logout();
    });

    // Tab change → load relevant data
    document.querySelectorAll('.nav-link[data-bs-toggle="tab"], .nav-link[href^="#"]').forEach(tab => {
        tab.addEventListener('shown.bs.tab', e => {
            const tabId = e.target.getAttribute('data-bs-target')?.replace('#','') ||
                e.target.getAttribute('href')?.replace('#','');

            if (!tabId) return;

            document.getElementById('pageTitle')?.setAttribute('data-active-tab', tabId);

            switch (tabId) {
                case 'dashboard': loadDashboard();    break;
                case 'bookings':  loadBookings();     break;
                case 'coffee':    loadCoffeeMenu();   break;
                // case 'reports':   loadReports();   break;
            }
        });
    });

    // Load default view
    loadDashboard();
});