package com.example.coffeeshopbackend.controller;

import com.example.coffeeshopbackend.entity.TableBooking;
import com.example.coffeeshopbackend.service.TableBookingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin("*")
public class TableBookingController {

    @Autowired
    private TableBookingService tableBookingService;

    // Get all bookings
    @GetMapping
    public ResponseEntity<List<TableBooking>> getAllBookings() {
        List<TableBooking> bookings = tableBookingService.getAllBookings();
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }

    // Get booking by ID
    @GetMapping("/{id}")
    public ResponseEntity<TableBooking> getBookingById(@PathVariable Long id) {
        TableBooking booking = tableBookingService.getBookingById(id);
        return new ResponseEntity<>(booking, HttpStatus.OK);
    }

    // Create new booking (with validation)
    @PostMapping
    public ResponseEntity<TableBooking> createBooking(@Valid @RequestBody TableBooking booking) {
        TableBooking createdBooking = tableBookingService.createBooking(booking);
        return new ResponseEntity<>(createdBooking, HttpStatus.CREATED);
    }

    // Update booking
    @PutMapping("/{id}")
    public ResponseEntity<TableBooking> updateBooking(@PathVariable Long id,
                                                      @RequestBody TableBooking bookingDetails) {
        TableBooking updatedBooking = tableBookingService.updateBooking(id, bookingDetails);
        return new ResponseEntity<>(updatedBooking, HttpStatus.OK);
    }

    // Delete booking
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        tableBookingService.deleteBooking(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Get bookings by email
    @GetMapping("/email/{email}")
    public ResponseEntity<List<TableBooking>> getBookingsByEmail(@PathVariable String email) {
        List<TableBooking> bookings = tableBookingService.getBookingsByEmail(email);
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }

    // Get bookings by date
    @GetMapping("/date/{date}")
    public ResponseEntity<List<TableBooking>> getBookingsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<TableBooking> bookings = tableBookingService.getBookingsByDate(date);
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }

    // Get bookings by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<TableBooking>> getBookingsByStatus(@PathVariable String status) {
        try {
            TableBooking.BookingStatus bookingStatus = TableBooking.BookingStatus.valueOf(status.toUpperCase());
            List<TableBooking> bookings = tableBookingService.getBookingsByStatus(bookingStatus);
            return new ResponseEntity<>(bookings, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // Update booking status
    @PatchMapping("/{id}/status")
    public ResponseEntity<TableBooking> updateBookingStatus(@PathVariable Long id,
                                                            @RequestParam String status) {
        try {
            TableBooking.BookingStatus bookingStatus = TableBooking.BookingStatus.valueOf(status.toUpperCase());
            TableBooking updatedBooking = tableBookingService.updateBookingStatus(id, bookingStatus);
            return new ResponseEntity<>(updatedBooking, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // Get available time slots for a date
    @GetMapping("/available-times/{date}")
    public ResponseEntity<List<LocalTime>> getAvailableTimeSlots(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<LocalTime> availableTimes = tableBookingService.getAvailableTimeSlots(date);
        return new ResponseEntity<>(availableTimes, HttpStatus.OK);
    }

    // Check if specific time slot is available
    @GetMapping("/check-availability")
    public ResponseEntity<Boolean> checkAvailability(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time) {
        try {
            Integer existingBookings = tableBookingService.getBookingsByDate(date)
                    .stream()
                    .filter(b -> b.getPreferredTime().equals(time)
                            && b.getStatus() != TableBooking.BookingStatus.CANCELLED)
                    .toList()
                    .size();

            return new ResponseEntity<>(existingBookings < 5, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}