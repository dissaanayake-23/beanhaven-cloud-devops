// =============================================
//          API CONFIGURATION
// =============================================
const API_BASE_URL = '/api';
const BOOKINGS_API = `${API_BASE_URL}/bookings`;
const COFFEE_API = `${API_BASE_URL}/coffees`;


// =============================================
//          AUTHENTICATION HELPERS
// =============================================
function getToken() {
    return localStorage.getItem('token');
}

function isAuthenticated() {
    const token = getToken();

    if (!token) {
        return false;
    }

    const loginTime = localStorage.getItem('loginTime');

    // If login time was not stored, allow the existing token
    if (!loginTime) {
        return true;
    }

    const hoursSinceLogin =
        (Date.now() - Number(loginTime)) / (1000 * 60 * 60);

    // Login expires after 8 hours
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


// =============================================
//          AUTHORIZATION HEADER
// =============================================
function getAuthHeaders() {
    const token = getToken();

    return {
        'Content-Type': 'application/json',
        ...(token
            ? { 'Authorization': `Bearer ${token}` }
            : {})
    };
}


// =============================================
//          UI HELPERS
// =============================================
function showMessage(containerId, text, type = 'info') {
    const container = document.getElementById(containerId);

    if (!container) {
        return;
    }

    container.innerHTML = `
        <div class="alert alert-${type} alert-dismissible fade show" role="alert">
            ${text}
            <button
                type="button"
                class="btn-close"
                data-bs-dismiss="alert"
                aria-label="Close">
            </button>
        </div>
    `;
}

function switchTab(tabId) {
    const tabElement = document.querySelector(
        `[data-bs-target="#${tabId}"], [href="#${tabId}"]`
    );

    if (tabElement && typeof bootstrap !== 'undefined') {
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
            fetch(BOOKINGS_API, {
                headers: getAuthHeaders()
            }),

            fetch(COFFEE_API, {
                headers: getAuthHeaders()
            })
        ]);

        console.log('Bookings response:', bookingsRes.status);
        console.log('Coffees response:', coffeesRes.status);

        if (!bookingsRes.ok) {
            throw new Error(
                `Bookings API error: ${bookingsRes.status}`
            );
        }

        if (!coffeesRes.ok) {
            throw new Error(
                `Coffee API error: ${coffeesRes.status}`
            );
        }

        const bookings = await bookingsRes.json();
        const coffees = await coffeesRes.json();

        console.log('Bookings:', bookings);
        console.log('Coffees:', coffees);

        // Total bookings
        const totalBookings =
            document.getElementById('totalBookings');

        if (totalBookings) {
            totalBookings.textContent = bookings.length;
            totalBookings.setAttribute(
                'data-count',
                bookings.length
            );
        }

        // Total coffees
        const totalCoffee =
            document.getElementById('totalCoffee');

        if (totalCoffee) {
            totalCoffee.textContent = coffees.length;
            totalCoffee.setAttribute(
                'data-count',
                coffees.length
            );
        }

        // Pending bookings
        const pending = bookings.filter(
            booking => booking.status === 'PENDING'
        ).length;

        // Confirmed bookings
        const confirmed = bookings.filter(
            booking => booking.status === 'CONFIRMED'
        ).length;

        const pendingElement =
            document.getElementById('pendingBookings');

        if (pendingElement) {
            pendingElement.textContent = pending;
        }

        const confirmedElement =
            document.getElementById('confirmedBookings');

        if (confirmedElement) {
            confirmedElement.textContent = confirmed;
        }

        loadRecentActivity(bookings);

    } catch (err) {

        console.error(
            'Dashboard load error:',
            err
        );

        showMessage(
            'dashboardMessage',
            'Could not load dashboard data. Please try again.',
            'danger'
        );
    }
}


// =============================================
//          RECENT ACTIVITY
// =============================================
function loadRecentActivity(bookings) {

    const container =
        document.getElementById('recentActivity');

    if (!container) {
        return;
    }

    const recent =
        bookings.slice(-5).reverse();

    if (recent.length === 0) {

        container.innerHTML =
            '<p class="text-muted text-center py-4">No recent bookings</p>';

        return;
    }

    let html = '';

    recent.forEach(booking => {

        const statusClass = {
            PENDING: 'bg-warning',
            CONFIRMED: 'bg-success',
            CANCELLED: 'bg-danger'
        }[booking.status] || 'bg-secondary';

        html += `
            <div class="list-group-item list-group-item-action">

                <div class="d-flex justify-content-between align-items-start">

                    <div>

                        <h6 class="mb-1">
                            ${booking.fullName || 'Guest'}
                        </h6>

                        <p class="mb-1 small">
                            ${booking.numberOfGuests || 0}
                            guest(s) •
                            ${booking.preferredTime || 'N/A'}
                        </p>

                    </div>

                    <small class="text-muted">
                        ${
            booking.bookingDate
                ? new Date(
                    booking.bookingDate
                ).toLocaleDateString()
                : 'N/A'
        }
                    </small>

                </div>

                <small>
                    Status:
                    <span class="badge ${statusClass}">
                        ${booking.status || 'UNKNOWN'}
                    </span>
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

    if (!isAuthenticated()) {
        redirectToLogin();
        return;
    }

    try {

        const res = await fetch(
            BOOKINGS_API,
            {
                headers: getAuthHeaders()
            }
        );

        console.log(
            'Bookings status:',
            res.status
        );

        if (!res.ok) {

            throw new Error(
                `Failed to load bookings (${res.status})`
            );
        }

        const bookings =
            await res.json();

        console.log(
            'Bookings loaded:',
            bookings
        );

        displayBookings(bookings);

    } catch (err) {

        console.error(
            'Booking loading error:',
            err
        );

        showMessage(
            'bookingsMessage',
            'Error loading bookings',
            'danger'
        );
    }
}


// =============================================
//          DISPLAY BOOKINGS
// =============================================
function displayBookings(bookings) {

    const tbody =
        document.querySelector(
            '#bookingsTable tbody'
        );

    if (!tbody) {
        return;
    }

    tbody.innerHTML = '';

    if (!Array.isArray(bookings) ||
        bookings.length === 0) {

        tbody.innerHTML = `
            <tr>
                <td colspan="9"
                    class="text-center py-4">
                    No bookings found
                </td>
            </tr>
        `;

        return;
    }

    // Newest bookings first
    bookings.sort(
        (a, b) =>
            new Date(b.bookingDate) -
            new Date(a.bookingDate)
    );

    bookings.forEach(booking => {

        const row =
            document.createElement('tr');

        const status =
            booking.status || 'PENDING';

        const statusClass =
            `status-${status.toLowerCase()}`;

        let actions = `
            <button
                class="btn btn-sm btn-info me-1"
                onclick="viewBookingDetails(${booking.id})"
                title="View">

                <i class="fas fa-eye"></i>

            </button>
        `;

        if (status === 'PENDING') {

            actions += `
                <button
                    class="btn btn-sm btn-success me-1"
                    onclick="updateBookingStatus(
                        ${booking.id},
                        'CONFIRMED'
                    )">

                    <i class="fas fa-check"></i>
                    Confirm

                </button>
            `;
        }

        if (status !== 'CANCELLED') {

            actions += `
                <button
                    class="btn btn-sm btn-danger"
                    onclick="updateBookingStatus(
                        ${booking.id},
                        'CANCELLED'
                    )">

                    <i class="fas fa-times"></i>
                    Cancel

                </button>
            `;
        }

        row.innerHTML = `

            <td>${booking.id}</td>

            <td>
                ${booking.fullName || '-'}
            </td>

            <td>
                ${booking.email || '-'}
            </td>

            <td>
                ${booking.bookingDate || '-'}
            </td>

            <td>
                ${booking.preferredTime || '-'}
            </td>

            <td>
                ${booking.numberOfGuests || '-'}
            </td>

            <td>
                <span class="badge ${statusClass}">
                    ${status}
                </span>
            </td>

            <td>
                ${booking.bookingReference || 'N/A'}
            </td>

            <td>
                ${actions}
            </td>
        `;

        tbody.appendChild(row);
    });
}


// =============================================
//          UPDATE BOOKING STATUS
// =============================================
async function updateBookingStatus(
    id,
    status
) {

    if (!confirm(
        `Set status to ${status}?`
    )) {
        return;
    }

    try {

        const res = await fetch(
            `${BOOKINGS_API}/${id}/status?status=${status}`,
            {
                method: 'PATCH',
                headers: getAuthHeaders()
            }
        );

        if (!res.ok) {

            throw new Error(
                `Update failed (${res.status})`
            );
        }

        showMessage(
            'bookingsMessage',
            `Booking #${id} → ${status}`,
            'success'
        );

        setTimeout(
            loadBookings,
            800
        );

    } catch (err) {

        console.error(err);

        showMessage(
            'bookingsMessage',
            err.message,
            'danger'
        );
    }
}


// =============================================
//          VIEW BOOKING
// =============================================
function viewBookingDetails(id) {

    alert(
        `View details for booking #${id}`
    );
}


// =============================================
//          COFFEE MENU MANAGEMENT
// =============================================
async function loadCoffeeMenu() {

    if (!isAuthenticated()) {
        redirectToLogin();
        return;
    }

    try {

        const res = await fetch(
            COFFEE_API,
            {
                headers: getAuthHeaders()
            }
        );

        console.log(
            'Coffee API status:',
            res.status
        );

        if (!res.ok) {

            throw new Error(
                `Failed to load coffees (${res.status})`
            );
        }

        const items =
            await res.json();

        console.log(
            'Coffee items:',
            items
        );

        displayCoffeeItems(items);

    } catch (err) {

        console.error(
            'Coffee loading error:',
            err
        );

        showMessage(
            'coffeeMessage',
            'Error loading menu',
            'danger'
        );
    }
}


// =============================================
//          DISPLAY COFFEES
// =============================================
function displayCoffeeItems(items) {

    const tbody =
        document.querySelector(
            '#coffeeTable tbody'
        );

    if (!tbody) {
        return;
    }

    tbody.innerHTML = '';

    if (!Array.isArray(items) ||
        items.length === 0) {

        tbody.innerHTML = `
            <tr>
                <td colspan="7"
                    class="text-center py-4">
                    No coffee items
                </td>
            </tr>
        `;

        return;
    }

    items.forEach(item => {

        const row =
            document.createElement('tr');

        const availBadge =
            item.available
                ? '<span class="badge bg-success">Available</span>'
                : '<span class="badge bg-danger">Unavailable</span>';

        row.innerHTML = `

            <td>${item.id}</td>

            <td>
                <strong>
                    ${item.name || '-'}
                </strong>
            </td>

            <td>
                ${item.description || '-'}
            </td>

            <td>
                LKR ${Number(
            item.price || 0
        ).toFixed(2)}
            </td>

            <td>
                ${item.category || '-'}
            </td>

            <td>
                ${availBadge}
            </td>

            <td>

                <button
                    class="btn btn-sm btn-warning me-1"
                    onclick="editCoffee(${item.id})">

                    <i class="fas fa-edit"></i>

                </button>

                <button
                    class="btn btn-sm btn-danger"
                    onclick="deleteCoffee(${item.id})">

                    <i class="fas fa-trash"></i>

                </button>

            </td>
        `;

        tbody.appendChild(row);
    });
}


// =============================================
//          ADD COFFEE
// =============================================
document
    .getElementById('addCoffeeForm')
    ?.addEventListener(
        'submit',
        async function (e) {

            e.preventDefault();

            if (!isAuthenticated()) {
                redirectToLogin();
                return;
            }

            const formData = {

                name:
                    document
                        .getElementById('coffeeName')
                        ?.value
                        .trim(),

                price:
                    Number(
                        document
                            .getElementById('coffeePrice')
                            ?.value
                    ),

                description:
                    document
                        .getElementById('coffeeDescription')
                        ?.value
                        .trim() || null,

                category:
                    document
                        .getElementById('coffeeCategory')
                        ?.value || null,

                roastType:
                    document
                        .getElementById('coffeeRoast')
                        ?.value || null,

                origin:
                    document
                        .getElementById('coffeeOrigin')
                        ?.value
                        .trim() || null,

                available:
                    document
                        .getElementById('coffeeAvailable')
                        ?.checked ?? true
            };

            if (
                !formData.name ||
                isNaN(formData.price) ||
                formData.price <= 0
            ) {

                showMessage(
                    'coffeeMessage',
                    'Name and valid price are required',
                    'danger'
                );

                return;
            }

            try {

                const res =
                    await fetch(
                        COFFEE_API,
                        {
                            method: 'POST',

                            headers:
                                getAuthHeaders(),

                            body:
                                JSON.stringify(
                                    formData
                                )
                        }
                    );

                if (!res.ok) {

                    const errText =
                        await res.text();

                    throw new Error(
                        errText ||
                        `Failed to add coffee (${res.status})`
                    );
                }

                const saved =
                    await res.json();

                showMessage(
                    'coffeeMessage',
                    `Added: ${saved.name}`,
                    'success'
                );

                this.reset();

                setTimeout(
                    loadCoffeeMenu,
                    1000
                );

            } catch (err) {

                console.error(err);

                showMessage(
                    'coffeeMessage',
                    err.message,
                    'danger'
                );
            }
        }
    );


// =============================================
//          EDIT COFFEE
// =============================================
function editCoffee(id) {

    alert(
        `Edit coffee #${id}`
    );
}


// =============================================
//          DELETE COFFEE
// =============================================
async function deleteCoffee(id) {

    if (!confirm(
        'Delete this coffee item permanently?'
    )) {
        return;
    }

    try {

        const res =
            await fetch(
                `${COFFEE_API}/${id}`,
                {
                    method: 'DELETE',
                    headers: getAuthHeaders()
                }
            );

        if (!res.ok) {

            throw new Error(
                `Delete failed (${res.status})`
            );
        }

        showMessage(
            'coffeeMessage',
            'Coffee deleted',
            'success'
        );

        setTimeout(
            loadCoffeeMenu,
            800
        );

    } catch (err) {

        console.error(err);

        showMessage(
            'coffeeMessage',
            err.message,
            'danger'
        );
    }
}


// =============================================
//          INITIALIZATION
// =============================================
document.addEventListener(
    'DOMContentLoaded',
    () => {

        console.log(
            'Admin JS loaded successfully'
        );

        console.log(
            'Bookings API:',
            BOOKINGS_API
        );

        console.log(
            'Coffee API:',
            COFFEE_API
        );

        // Check authentication
        if (!isAuthenticated()) {

            console.log(
                'Admin not authenticated'
            );

            redirectToLogin();

            return;
        }

        console.log(
            'Admin authenticated'
        );

        // Logout button
        document
            .getElementById('logoutBtn')
            ?.addEventListener(
                'click',
                e => {

                    e.preventDefault();

                    if (
                        confirm('Log out now?')
                    ) {
                        logout();
                    }
                }
            );

        // Bootstrap tabs
        document
            .querySelectorAll(
                '.nav-link[data-bs-toggle="tab"], .nav-link[href^="#"]'
            )
            .forEach(tab => {

                tab.addEventListener(
                    'shown.bs.tab',
                    e => {

                        const tabId =
                            e.target
                                .getAttribute(
                                    'data-bs-target'
                                )
                                ?.replace('#', '') ||

                            e.target
                                .getAttribute(
                                    'href'
                                )
                                ?.replace('#', '');

                        if (!tabId) {
                            return;
                        }

                        document
                            .getElementById(
                                'pageTitle'
                            )
                            ?.setAttribute(
                                'data-active-tab',
                                tabId
                            );

                        switch (tabId) {

                            case 'dashboard':
                                loadDashboard();
                                break;

                            case 'bookings':
                                loadBookings();
                                break;

                            case 'coffee':
                                loadCoffeeMenu();
                                break;
                        }
                    }
                );
            });

        // Load dashboard
        loadDashboard();
    }
);