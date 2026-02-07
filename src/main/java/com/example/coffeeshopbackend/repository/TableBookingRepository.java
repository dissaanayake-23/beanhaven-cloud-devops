package com.example.coffeeshopbackend.repository;

import com.example.coffeeshopbackend.entity.TableBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface TableBookingRepository extends JpaRepository<TableBooking, Long> {

    // Find bookings by email
    List<TableBooking> findByEmail(String email);

    // Find bookings by date
    List<TableBooking> findByBookingDate(LocalDate bookingDate);

    // Find bookings by status
    List<TableBooking> findByStatus(TableBooking.BookingStatus status);

    // Find bookings between dates
    List<TableBooking> findByBookingDateBetween(LocalDate startDate, LocalDate endDate);

    // Find bookings by date and time range
    @Query("SELECT b FROM TableBooking b WHERE b.bookingDate = :date AND b.preferredTime BETWEEN :startTime AND :endTime")
    List<TableBooking> findByDateAndTimeRange(
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime);

    // Check if booking exists for specific time slot
    @Query("SELECT COUNT(b) FROM TableBooking b WHERE b.bookingDate = :date AND b.preferredTime = :time AND b.status != 'CANCELLED'")
    Integer countBookingsByDateTime(
            @Param("date") LocalDate date,
            @Param("time") LocalTime time);
}