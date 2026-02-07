package com.example.coffeeshopbackend.service;

import com.example.coffeeshopbackend.entity.TableBooking;
import com.example.coffeeshopbackend.repository.TableBookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class TableBookingService {

    @Autowired
    private TableBookingRepository tableBookingRepository;

    // Get all bookings
    public List<TableBooking> getAllBookings() {
        return tableBookingRepository.findAll();
    }

    // Get booking by ID
    public TableBooking getBookingById(Long id) {
        Optional<TableBooking> booking = tableBookingRepository.findById(id);
        return booking.orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));
    }

    // Create new booking
    public TableBooking createBooking(TableBooking booking) {
        // Check for existing bookings at same time
        Integer existingBookings = tableBookingRepository.countBookingsByDateTime(
                booking.getBookingDate(),
                booking.getPreferredTime()
        );

        // Simple capacity check (adjust based on your restaurant capacity)
        if (existingBookings >= 5) { // Assuming max 5 bookings per time slot
            throw new RuntimeException("This time slot is fully booked. Please choose another time.");
        }

        return tableBookingRepository.save(booking);
    }

    // Update booking
    public TableBooking updateBooking(Long id, TableBooking bookingDetails) {
        TableBooking booking = getBookingById(id);

        if (bookingDetails.getFullName() != null) {
            booking.setFullName(bookingDetails.getFullName());
        }
        if (bookingDetails.getEmail() != null) {
            booking.setEmail(bookingDetails.getEmail());
        }
        if (bookingDetails.getPhoneNumber() != null) {
            booking.setPhoneNumber(bookingDetails.getPhoneNumber());
        }
        if (bookingDetails.getBookingDate() != null) {
            booking.setBookingDate(bookingDetails.getBookingDate());
        }
        if (bookingDetails.getPreferredTime() != null) {
            booking.setPreferredTime(bookingDetails.getPreferredTime());
        }
        if (bookingDetails.getNumberOfGuests() != null) {
            booking.setNumberOfGuests(bookingDetails.getNumberOfGuests());
        }
        if (bookingDetails.getSpecialRequests() != null) {
            booking.setSpecialRequests(bookingDetails.getSpecialRequests());
        }
        if (bookingDetails.getStatus() != null) {
            booking.setStatus(bookingDetails.getStatus());
        }

        return tableBookingRepository.save(booking);
    }

    // Delete booking
    public void deleteBooking(Long id) {
        TableBooking booking = getBookingById(id);
        tableBookingRepository.delete(booking);
    }

    // Get bookings by email
    public List<TableBooking> getBookingsByEmail(String email) {
        return tableBookingRepository.findByEmail(email);
    }

    // Get bookings by date
    public List<TableBooking> getBookingsByDate(LocalDate date) {
        return tableBookingRepository.findByBookingDate(date);
    }

    // Get bookings by status
    public List<TableBooking> getBookingsByStatus(TableBooking.BookingStatus status) {
        return tableBookingRepository.findByStatus(status);
    }

    // Update booking status
    public TableBooking updateBookingStatus(Long id, TableBooking.BookingStatus status) {
        TableBooking booking = getBookingById(id);
        booking.setStatus(status);
        return tableBookingRepository.save(booking);
    }

    // Check available time slots for a date
    public List<LocalTime> getAvailableTimeSlots(LocalDate date) {
        List<LocalTime> allTimeSlots = List.of(
                LocalTime.of(9, 0),  // 9:00 AM
                LocalTime.of(11, 0), // 11:00 AM
                LocalTime.of(13, 0), // 1:00 PM
                LocalTime.of(15, 0), // 3:00 PM
                LocalTime.of(17, 0), // 5:00 PM
                LocalTime.of(19, 0)  // 7:00 PM
        );

        // Filter out fully booked time slots
        return allTimeSlots.stream()
                .filter(time -> {
                    Integer bookings = tableBookingRepository.countBookingsByDateTime(date, time);
                    return bookings < 5; // Capacity check
                })
                .toList();
    }
}