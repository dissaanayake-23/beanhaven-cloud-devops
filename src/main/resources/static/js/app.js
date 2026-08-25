// API Base URL
// Frontend and backend are running from the same Spring Boot application.
const API_BASE_URL = '/api/bookings';


// Show message
function showMessage(text, type) {

    const messageDiv = document.getElementById('message');

    if (!messageDiv) {
        console.log(text);
        return;
    }

    messageDiv.textContent = text;
    messageDiv.className = `alert alert-${type}`;
    messageDiv.style.display = 'block';

    // Scroll to message
    messageDiv.scrollIntoView({
        behavior: 'smooth',
        block: 'center'
    });

    // Hide automatically
    const hideTime = type === 'success' ? 5000 : 10000;

    setTimeout(() => {
        messageDiv.style.display = 'none';
    }, hideTime);
}


// Format date
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


// Wait until HTML page is loaded
window.addEventListener('DOMContentLoaded', function () {

    const bookingForm = document.getElementById('bookingForm');

    const dateInput = document.getElementById('bookingDate');

    // Set minimum booking date
    if (dateInput) {

        const today = new Date().toISOString().split('T')[0];

        dateInput.min = today;

        // Set tomorrow as default date
        if (!dateInput.value) {

            const tomorrow = new Date();

            tomorrow.setDate(tomorrow.getDate() + 1);

            dateInput.value =
                tomorrow.toISOString().split('T')[0];
        }
    }


    // If booking form doesn't exist, stop
    if (!bookingForm) {
        return;
    }


    // Booking form submit
    bookingForm.addEventListener('submit', async function (e) {

        e.preventDefault();


        // Get form data
        const bookingData = {

            fullName:
                document.getElementById('fullName').value.trim(),

            email:
                document.getElementById('email').value.trim(),

            phoneNumber:
                document.getElementById('phoneNumber').value.trim(),

            bookingDate:
            document.getElementById('bookingDate').value,

            preferredTime:
            document.getElementById('preferredTime').value,

            numberOfGuests:
                parseInt(
                    document.getElementById('numberOfGuests').value
                ),

            specialRequests:
                document.getElementById('specialRequests').value.trim()
        };


        // Validate name
        if (!bookingData.fullName) {

            showMessage(
                'Please enter your full name',
                'danger'
            );

            document.getElementById('fullName').focus();

            return;
        }


        // Validate email
        if (!bookingData.email) {

            showMessage(
                'Please enter your email address',
                'danger'
            );

            document.getElementById('email').focus();

            return;
        }


        if (!validateEmail(bookingData.email)) {

            showMessage(
                'Please enter a valid email address',
                'danger'
            );

            document.getElementById('email').focus();

            return;
        }


        // Validate phone
        if (!bookingData.phoneNumber) {

            showMessage(
                'Please enter your phone number',
                'danger'
            );

            document.getElementById('phoneNumber').focus();

            return;
        }


        if (!validatePhoneNumber(bookingData.phoneNumber)) {

            showMessage(
                'Phone number must be 10 digits (example: 0768368000)',
                'danger'
            );

            document.getElementById('phoneNumber').focus();

            return;
        }


        // Validate booking date
        if (!bookingData.bookingDate) {

            showMessage(
                'Please select a booking date',
                'danger'
            );

            document.getElementById('bookingDate').focus();

            return;
        }


        // Validate preferred time
        if (!bookingData.preferredTime) {

            showMessage(
                'Please select a preferred time',
                'danger'
            );

            document.getElementById('preferredTime').focus();

            return;
        }


        // Validate guests
        if (
            !bookingData.numberOfGuests ||
            bookingData.numberOfGuests < 1 ||
            bookingData.numberOfGuests > 10
        ) {

            showMessage(
                'Please select number of guests (1-10)',
                'danger'
            );

            document.getElementById('numberOfGuests').focus();

            return;
        }


        // Button/loading elements
        const submitBtn =
            document.querySelector(
                '#bookingForm button[type="submit"]'
            );

        const submitText =
            document.getElementById('submitText');

        const loadingSpinner =
            document.getElementById('loadingSpinner');


        // Loading state
        if (submitText) {
            submitText.textContent = 'Processing...';
        }

        if (loadingSpinner) {
            loadingSpinner.style.display = 'inline-block';
        }

        if (submitBtn) {
            submitBtn.disabled = true;
        }


        try {

            console.log(
                'Sending booking data:',
                bookingData
            );


            // Send data to Spring Boot
            const response = await fetch(API_BASE_URL, {

                method: 'POST',

                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                },

                body: JSON.stringify(bookingData)
            });


            console.log(
                'Response status:',
                response.status
            );


            // Successful booking
            if (response.ok) {

                const result = await response.json();

                console.log(
                    'Booking successful:',
                    result
                );


                showMessage(
                    `✅ Booking successful! Your reference: ${
                        result.bookingReference || result.id
                    }`,
                    'success'
                );


                // Clear fields
                document.getElementById('fullName').value = '';

                document.getElementById('email').value = '';

                document.getElementById('phoneNumber').value = '';

                document.getElementById('numberOfGuests').value = '';

                document.getElementById('preferredTime').value = '';

                document.getElementById('specialRequests').value = '';


                // Set tomorrow again
                const tomorrow = new Date();

                tomorrow.setDate(
                    tomorrow.getDate() + 1
                );

                document.getElementById('bookingDate').value =
                    tomorrow.toISOString().split('T')[0];

            } else {

                let errorMessage = 'Booking failed';

                try {

                    const errorData =
                        await response.json();

                    errorMessage =
                        errorData.message ||
                        errorData.error ||
                        JSON.stringify(errorData);

                } catch (error) {

                    const text =
                        await response.text();

                    if (text) {
                        errorMessage = text;
                    }
                }


                console.error(
                    'Booking failed:',
                    errorMessage
                );


                showMessage(
                    `❌ ${errorMessage}`,
                    'danger'
                );
            }

        } catch (error) {

            console.error(
                'Network error:',
                error
            );


            showMessage(
                `❌ Network error: ${error.message}`,
                'danger'
            );

        } finally {

            // Reset button
            if (submitText) {
                submitText.textContent =
                    'Book Table Now';
            }

            if (loadingSpinner) {
                loadingSpinner.style.display =
                    'none';
            }

            if (submitBtn) {
                submitBtn.disabled = false;
            }
        }
    });
});