package com.example.coffeeshopbackend.controller;

import com.example.coffeeshopbackend.entity.Order;
import com.example.coffeeshopbackend.repository.OrderRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;


    // =========================================================
    // GET ALL ORDERS
    // =========================================================

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {

        try {

            List<Order> orders =
                    orderRepository.findAll();

            return new ResponseEntity<>(
                    orders,
                    HttpStatus.OK
            );

        } catch (Exception e) {

            e.printStackTrace();

            return new ResponseEntity<>(
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }


    // =========================================================
    // GET ORDER BY ID
    // =========================================================

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(
            @PathVariable Long id
    ) {

        try {

            Order order =
                    orderRepository
                            .findById(id)
                            .orElse(null);


            if (order == null) {

                return new ResponseEntity<>(
                        HttpStatus.NOT_FOUND
                );
            }


            return new ResponseEntity<>(
                    order,
                    HttpStatus.OK
            );


        } catch (Exception e) {

            e.printStackTrace();

            return new ResponseEntity<>(
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }


    // =========================================================
    // CREATE NEW ORDER
    // =========================================================

    @PostMapping
    public ResponseEntity<Order> createOrder(
            @RequestBody Order order
    ) {

        try {

            // Set current order date
            if (order.getOrderDate() == null) {

                order.setOrderDate(
                        LocalDateTime.now()
                );
            }


            // Default status
            if (order.getStatus() == null) {

                order.setStatus(
                        Order.OrderStatus.PENDING
                );
            }


            // Default order type if missing
            if (order.getOrderType() == null) {

                order.setOrderType(
                        Order.OrderType.DELIVERY
                );
            }


            // For takeaway, ensure a valid address value exists
            if (
                    order.getOrderType()
                            == Order.OrderType.TAKEAWAY
            ) {

                order.setDeliveryAddress(
                        "Takeaway"
                );
            }


            Order savedOrder =
                    orderRepository.save(order);


            return new ResponseEntity<>(
                    savedOrder,
                    HttpStatus.CREATED
            );


        } catch (Exception e) {

            e.printStackTrace();

            return new ResponseEntity<>(
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }


    // =========================================================
    // UPDATE FULL ORDER
    // =========================================================

    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(
            @PathVariable Long id,
            @RequestBody Order orderDetails
    ) {

        try {

            Order order =
                    orderRepository
                            .findById(id)
                            .orElse(null);


            if (order == null) {

                return new ResponseEntity<>(
                        HttpStatus.NOT_FOUND
                );
            }


            if (orderDetails.getCustomerName() != null) {

                order.setCustomerName(
                        orderDetails.getCustomerName()
                );
            }


            if (orderDetails.getCustomerEmail() != null) {

                order.setCustomerEmail(
                        orderDetails.getCustomerEmail()
                );
            }


            if (orderDetails.getCustomerPhone() != null) {

                order.setCustomerPhone(
                        orderDetails.getCustomerPhone()
                );
            }


            if (orderDetails.getTotalPrice() != null) {

                order.setTotalPrice(
                        orderDetails.getTotalPrice()
                );
            }


            if (orderDetails.getStatus() != null) {

                order.setStatus(
                        orderDetails.getStatus()
                );
            }


            if (orderDetails.getOrderType() != null) {

                order.setOrderType(
                        orderDetails.getOrderType()
                );
            }


            if (orderDetails.getDeliveryAddress() != null) {

                order.setDeliveryAddress(
                        orderDetails.getDeliveryAddress()
                );
            }


            if (orderDetails.getSpecialInstructions() != null) {

                order.setSpecialInstructions(
                        orderDetails.getSpecialInstructions()
                );
            }


            Order updatedOrder =
                    orderRepository.save(order);


            return new ResponseEntity<>(
                    updatedOrder,
                    HttpStatus.OK
            );


        } catch (Exception e) {

            e.printStackTrace();

            return new ResponseEntity<>(
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }


    // =========================================================
    // UPDATE ORDER STATUS
    // Example:
    // PATCH /api/orders/5/status?status=PREPARING
    // =========================================================

    @PatchMapping("/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam Order.OrderStatus status
    ) {

        try {

            Order order =
                    orderRepository
                            .findById(id)
                            .orElse(null);


            if (order == null) {

                return new ResponseEntity<>(
                        HttpStatus.NOT_FOUND
                );
            }


            order.setStatus(
                    status
            );


            Order updatedOrder =
                    orderRepository.save(order);


            return new ResponseEntity<>(
                    updatedOrder,
                    HttpStatus.OK
            );


        } catch (IllegalArgumentException e) {

            e.printStackTrace();

            return new ResponseEntity<>(
                    null,
                    HttpStatus.BAD_REQUEST
            );


        } catch (Exception e) {

            e.printStackTrace();

            return new ResponseEntity<>(
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
// =========================================================
// GET ORDERS BY CUSTOMER PHONE
// Used for customer order tracking
// =========================================================

    @GetMapping("/phone/{phone}")
    public ResponseEntity<List<Order>> getOrdersByPhone(
            @PathVariable String phone
    ) {

        try {

            List<Order> orders =
                    orderRepository.findByCustomerPhone(phone);

            return new ResponseEntity<>(
                    orders,
                    HttpStatus.OK
            );

        } catch (Exception e) {

            e.printStackTrace();

            return new ResponseEntity<>(
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    // =========================================================
    // DELETE ORDER
    // =========================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteOrder(
            @PathVariable Long id
    ) {

        try {

            if (
                    !orderRepository.existsById(id)
            ) {

                return new ResponseEntity<>(
                        HttpStatus.NOT_FOUND
                );
            }


            orderRepository.deleteById(id);


            return new ResponseEntity<>(
                    HttpStatus.NO_CONTENT
            );


        } catch (Exception e) {

            e.printStackTrace();

            return new ResponseEntity<>(
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}