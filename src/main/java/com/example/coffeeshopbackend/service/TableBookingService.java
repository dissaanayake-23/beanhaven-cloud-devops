package com.example.coffeeshopbackend.service;

import com.example.coffeeshopbackend.entity.TableBooking;
import com.example.coffeeshopbackend.repository.TableBookingRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class TableBookingService {

    @Autowired
    private TableBookingRepository tableBookingRepository;

    @Autowired
    private EmailService emailService;


    // =========================================================
    // GET ALL BOOKINGS
    // =========================================================

    public List<TableBooking> getAllBookings() {

        return tableBookingRepository.findAll();
    }


    // =========================================================
    // GET BOOKING BY ID
    // =========================================================

    public TableBooking getBookingById(Long id) {

        Optional<TableBooking> booking =
                tableBookingRepository.findById(id);

        return booking.orElseThrow(
                () -> new RuntimeException(
                        "Booking not found with id: " + id
                )
        );
    }


    // =========================================================
    // CREATE NEW BOOKING
    // =========================================================

    public TableBooking createBooking(TableBooking booking) {

        // Check current bookings for selected date + time
        Integer existingBookings =
                tableBookingRepository.countBookingsByDateTime(
                        booking.getBookingDate(),
                        booking.getPreferredTime()
                );


        // Maximum 5 bookings for one time slot
        if (existingBookings >= 5) {

            throw new RuntimeException(
                    "This time slot is fully booked. Please choose another time."
            );
        }


        // Generate booking reference
        String bookingReference =
                generateBookingReference(
                        booking.getBookingDate()
                );


        booking.setBookingReference(
                bookingReference
        );


        // Default status
        if (booking.getStatus() == null) {

            booking.setStatus(
                    TableBooking.BookingStatus.PENDING
            );
        }


        // Save booking first
        TableBooking savedBooking =
                tableBookingRepository.save(
                        booking
                );


        // Send reservation received email
        sendPendingReservationEmail(
                savedBooking
        );


        return savedBooking;
    }


    // =========================================================
    // SEND PENDING RESERVATION EMAIL
    // =========================================================

    private void sendPendingReservationEmail(
            TableBooking booking
    ) {

        try {

            if (
                    booking.getEmail() == null ||
                            booking.getEmail().isBlank()
            ) {

                return;
            }


            String subject =
                    "Reservation Received - "
                            + booking.getBookingReference();


            String message =
                    "Dear "
                            + booking.getFullName()
                            + ",\n\n"
                            + "We have received your reservation request.\n\n"

                            + "Booking Reference: "
                            + booking.getBookingReference()
                            + "\n"

                            + "Date: "
                            + booking.getBookingDate()
                            + "\n"

                            + "Time: "
                            + booking.getPreferredTime()
                            + "\n"

                            + "Guests: "
                            + booking.getNumberOfGuests()
                            + "\n"

                            + "Status: PENDING\n\n"

                            + "Your reservation is waiting for admin confirmation.\n"
                            + "You will receive another email once your reservation is confirmed.\n\n"

                            + "Thank you.";


            emailService.sendEmail(
                    booking.getEmail(),
                    subject,
                    message
            );


            System.out.println(
                    "Pending reservation email sent to: "
                            + booking.getEmail()
            );


        } catch (Exception e) {

            System.out.println(
                    "Pending email could not be sent: "
                            + e.getMessage()
            );
        }
    }


    // =========================================================
    // GENERATE BOOKING REFERENCE
    // Example: BH-20260825-4821
    // =========================================================

    private String generateBookingReference(
            LocalDate bookingDate
    ) {

        String datePart =
                bookingDate.format(
                        DateTimeFormatter.BASIC_ISO_DATE
                );


        int randomNumber =
                ThreadLocalRandom.current()
                        .nextInt(
                                1000,
                                10000
                        );


        return "BH-"
                + datePart
                + "-"
                + randomNumber;
    }


    // =========================================================
    // UPDATE BOOKING
    // =========================================================

    public TableBooking updateBooking(
            Long id,
            TableBooking bookingDetails
    ) {

        TableBooking booking =
                getBookingById(id);


        if (bookingDetails.getFullName() != null) {

            booking.setFullName(
                    bookingDetails.getFullName()
            );
        }


        if (bookingDetails.getEmail() != null) {

            booking.setEmail(
                    bookingDetails.getEmail()
            );
        }


        if (bookingDetails.getPhoneNumber() != null) {

            booking.setPhoneNumber(
                    bookingDetails.getPhoneNumber()
            );
        }


        if (bookingDetails.getBookingDate() != null) {

            booking.setBookingDate(
                    bookingDetails.getBookingDate()
            );
        }


        if (bookingDetails.getPreferredTime() != null) {

            booking.setPreferredTime(
                    bookingDetails.getPreferredTime()
            );
        }


        if (bookingDetails.getNumberOfGuests() != null) {

            booking.setNumberOfGuests(
                    bookingDetails.getNumberOfGuests()
            );
        }


        if (bookingDetails.getSpecialRequests() != null) {

            booking.setSpecialRequests(
                    bookingDetails.getSpecialRequests()
            );
        }


        if (bookingDetails.getStatus() != null) {

            booking.setStatus(
                    bookingDetails.getStatus()
            );
        }


        return tableBookingRepository.save(
                booking
        );
    }


    // =========================================================
    // DELETE BOOKING
    // =========================================================

    public void deleteBooking(Long id) {

        TableBooking booking =
                getBookingById(id);


        tableBookingRepository.delete(
                booking
        );
    }


    // =========================================================
    // GET BOOKINGS BY EMAIL
    // =========================================================

    public List<TableBooking> getBookingsByEmail(
            String email
    ) {

        return tableBookingRepository.findByEmail(
                email
        );
    }


    // =========================================================
    // GET BOOKINGS BY DATE
    // =========================================================

    public List<TableBooking> getBookingsByDate(
            LocalDate date
    ) {

        return tableBookingRepository
                .findByBookingDate(
                        date
                );
    }


    // =========================================================
    // GET BOOKINGS BY STATUS
    // =========================================================

    public List<TableBooking> getBookingsByStatus(
            TableBooking.BookingStatus status
    ) {

        return tableBookingRepository.findByStatus(
                status
        );
    }


    // =========================================================
    // UPDATE BOOKING STATUS
    // =========================================================

    public TableBooking updateBookingStatus(
            Long id,
            TableBooking.BookingStatus status
    ) {

        TableBooking booking =
                getBookingById(id);


        booking.setStatus(
                status
        );


        TableBooking updatedBooking =
                tableBookingRepository.save(
                        booking
                );


        // Send email after admin changes status
        if (
                status ==
                        TableBooking.BookingStatus.CONFIRMED
        ) {

            sendConfirmationEmail(
                    updatedBooking
            );

        } else if (
                status ==
                        TableBooking.BookingStatus.CANCELLED
        ) {

            sendCancellationEmail(
                    updatedBooking
            );
        }


        return updatedBooking;
    }


    // =========================================================
    // SEND CONFIRMATION EMAIL
    // =========================================================

    private void sendConfirmationEmail(
            TableBooking booking
    ) {

        try {

            if (
                    booking.getEmail() == null ||
                            booking.getEmail().isBlank()
            ) {

                return;
            }


            String subject =
                    "Reservation Confirmed - "
                            + booking.getBookingReference();


            String message =
                    "Dear "
                            + booking.getFullName()
                            + ",\n\n"

                            + "Good news! Your reservation has been confirmed.\n\n"

                            + "Booking Reference: "
                            + booking.getBookingReference()
                            + "\n"

                            + "Date: "
                            + booking.getBookingDate()
                            + "\n"

                            + "Time: "
                            + booking.getPreferredTime()
                            + "\n"

                            + "Guests: "
                            + booking.getNumberOfGuests()
                            + "\n"

                            + "Status: CONFIRMED\n\n"

                            + "We look forward to seeing you.\n\n"

                            + "Thank you.";


            emailService.sendEmail(
                    booking.getEmail(),
                    subject,
                    message
            );


            System.out.println(
                    "Confirmation email sent to: "
                            + booking.getEmail()
            );


        } catch (Exception e) {

            System.out.println(
                    "Confirmation email could not be sent: "
                            + e.getMessage()
            );
        }
    }


    // =========================================================
    // SEND CANCELLATION EMAIL
    // =========================================================

    private void sendCancellationEmail(
            TableBooking booking
    ) {

        try {

            if (
                    booking.getEmail() == null ||
                            booking.getEmail().isBlank()
            ) {

                return;
            }


            String subject =
                    "Reservation Cancelled - "
                            + booking.getBookingReference();


            String message =
                    "Dear "
                            + booking.getFullName()
                            + ",\n\n"

                            + "Your reservation has been cancelled.\n\n"

                            + "Booking Reference: "
                            + booking.getBookingReference()
                            + "\n"

                            + "Date: "
                            + booking.getBookingDate()
                            + "\n"

                            + "Time: "
                            + booking.getPreferredTime()
                            + "\n"

                            + "Guests: "
                            + booking.getNumberOfGuests()
                            + "\n"

                            + "Status: CANCELLED\n\n"

                            + "If needed, you can make a new reservation.\n\n"

                            + "Thank you.";


            emailService.sendEmail(
                    booking.getEmail(),
                    subject,
                    message
            );


            System.out.println(
                    "Cancellation email sent to: "
                            + booking.getEmail()
            );


        } catch (Exception e) {

            System.out.println(
                    "Cancellation email could not be sent: "
                            + e.getMessage()
            );
        }
    }


    // =========================================================
    // GET AVAILABLE TIME SLOTS
    // =========================================================

    public List<LocalTime> getAvailableTimeSlots(
            LocalDate date
    ) {

        List<LocalTime> allTimeSlots =
                List.of(

                        LocalTime.of(9, 0),

                        LocalTime.of(11, 0),

                        LocalTime.of(13, 0),

                        LocalTime.of(15, 0),

                        LocalTime.of(17, 0),

                        LocalTime.of(19, 0)
                );


        return allTimeSlots
                .stream()
                .filter(
                        time -> {

                            Integer bookings =
                                    tableBookingRepository
                                            .countBookingsByDateTime(
                                                    date,
                                                    time
                                            );


                            return bookings < 5;
                        }
                )
                .toList();
    }
}