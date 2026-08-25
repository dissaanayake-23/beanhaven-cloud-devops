package com.example.coffeeshopbackend.controller;

import com.example.coffeeshopbackend.entity.Coffee;
import com.example.coffeeshopbackend.service.CoffeeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/coffees")
@CrossOrigin(origins = "*")
public class CoffeeController {

    @Autowired
    private CoffeeService coffeeService;


    // =========================================================
    // IMAGE UPLOAD FOLDER
    // =========================================================

    private static final String UPLOAD_DIR =
            "./src/main/resources/static/uploads";

    // =========================================================
    // GET ALL COFFEES
    // =========================================================

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllCoffees() {

        try {

            List<Coffee> coffees =
                    coffeeService.getAllCoffees();

            return ResponseEntity.ok(coffees);

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            Map.of(
                                    "error",
                                    "Failed to fetch coffees: "
                                            + e.getMessage()
                            )
                    );
        }
    }


    // =========================================================
    // GET COFFEE BY ID
    // =========================================================

    @GetMapping(
            value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> getCoffeeById(
            @PathVariable Long id
    ) {

        try {

            Coffee coffee =
                    coffeeService.getCoffeeById(id);

            if (coffee == null) {

                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(
                                Map.of(
                                        "error",
                                        "Coffee not found with id: " + id
                                )
                        );
            }

            return ResponseEntity.ok(coffee);

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            Map.of(
                                    "error",
                                    e.getMessage()
                            )
                    );
        }
    }


    // =========================================================
    // UPLOAD COFFEE IMAGE
    // =========================================================

    @PostMapping(
            value = "/upload-image",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> uploadCoffeeImage(
            @RequestParam("image") MultipartFile image
    ) {

        try {

            // Check empty file
            if (image == null || image.isEmpty()) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                Map.of(
                                        "error",
                                        "Please select an image."
                                )
                        );
            }


            // Limit file size - 5 MB
            long maxFileSize =
                    5 * 1024 * 1024;

            if (image.getSize() > maxFileSize) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                Map.of(
                                        "error",
                                        "Image size must be less than 5 MB."
                                )
                        );
            }


            // Check file type
            String contentType =
                    image.getContentType();

            if (
                    contentType == null ||
                            !(
                                    contentType.equals("image/jpeg") ||
                                            contentType.equals("image/png") ||
                                            contentType.equals("image/webp")
                            )
            ) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                Map.of(
                                        "error",
                                        "Only JPG, PNG and WEBP images are allowed."
                                )
                        );
            }


            // Get original file name
            String originalFilename =
                    image.getOriginalFilename();

            if (originalFilename == null) {
                originalFilename = "coffee-image";
            }


            // Get file extension
            String extension = "";

            int dotIndex =
                    originalFilename.lastIndexOf(".");

            if (dotIndex >= 0) {

                extension =
                        originalFilename
                                .substring(dotIndex)
                                .toLowerCase();
            }


            // Generate unique file name
            String newFileName =
                    UUID.randomUUID()
                            .toString()
                            + extension;


            // Create upload directory
            Path uploadPath =
                    Paths.get(UPLOAD_DIR)
                            .toAbsolutePath()
                            .normalize();

            if (!Files.exists(uploadPath)) {

                Files.createDirectories(uploadPath);
            }


            // Save image
            Path filePath =
                    uploadPath.resolve(newFileName);

            Files.copy(
                    image.getInputStream(),
                    filePath,
                    StandardCopyOption.REPLACE_EXISTING
            );


            // URL stored in database
            String imageUrl =
                    "/uploads/" + newFileName;


            // Return response
            Map<String, Object> response =
                    new HashMap<>();

            response.put(
                    "success",
                    true
            );

            response.put(
                    "message",
                    "Image uploaded successfully."
            );

            response.put(
                    "imageUrl",
                    imageUrl
            );

            response.put(
                    "fileName",
                    newFileName
            );


            return ResponseEntity.ok(response);


        } catch (IOException e) {

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            Map.of(
                                    "error",
                                    "Failed to save image: "
                                            + e.getMessage()
                            )
                    );


        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            Map.of(
                                    "error",
                                    "Image upload failed: "
                                            + e.getMessage()
                            )
                    );
        }
    }


    // =========================================================
    // CREATE COFFEE
    // =========================================================

    @PostMapping(
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> createCoffee(
            @RequestBody Coffee coffee
    ) {

        try {

            // Validate name
            if (
                    coffee.getName() == null ||
                            coffee.getName()
                                    .trim()
                                    .isEmpty()
            ) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                Map.of(
                                        "error",
                                        "Coffee name is required"
                                )
                        );
            }


            // Validate price
            if (
                    coffee.getPrice() == null ||
                            coffee.getPrice() <= 0
            ) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                Map.of(
                                        "error",
                                        "Valid price is required (greater than 0)"
                                )
                        );
            }


            // Default available value
            if (coffee.getAvailable() == null) {

                coffee.setAvailable(true);
            }


            // Save coffee
            Coffee savedCoffee =
                    coffeeService.createCoffee(coffee);


            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(savedCoffee);


        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            Map.of(
                                    "error",
                                    "Failed to create coffee: "
                                            + e.getMessage()
                            )
                    );
        }
    }


    // =========================================================
    // UPDATE COFFEE
    // =========================================================

    @PutMapping(
            value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> updateCoffee(
            @PathVariable Long id,
            @RequestBody Coffee coffeeDetails
    ) {

        try {

            Coffee existingCoffee =
                    coffeeService.getCoffeeById(id);


            if (existingCoffee == null) {

                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(
                                Map.of(
                                        "error",
                                        "Coffee not found with id: " + id
                                )
                        );
            }


            Coffee updatedCoffee =
                    coffeeService.updateCoffee(
                            id,
                            coffeeDetails
                    );


            return ResponseEntity.ok(updatedCoffee);


        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            Map.of(
                                    "error",
                                    "Failed to update coffee: "
                                            + e.getMessage()
                            )
                    );
        }
    }


    // =========================================================
    // DELETE COFFEE
    // =========================================================

    @DeleteMapping(
            value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> deleteCoffee(
            @PathVariable Long id
    ) {

        try {

            Coffee existingCoffee =
                    coffeeService.getCoffeeById(id);


            if (existingCoffee == null) {

                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(
                                Map.of(
                                        "error",
                                        "Coffee not found with id: " + id
                                )
                        );
            }


            boolean deleted =
                    coffeeService.deleteCoffee(id);


            if (deleted) {

                return ResponseEntity.ok(
                        Map.of(
                                "message",
                                "Coffee deleted successfully",

                                "deletedId",
                                id,

                                "deletedName",
                                existingCoffee.getName()
                        )
                );

            } else {

                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(
                                Map.of(
                                        "error",
                                        "Failed to delete coffee"
                                )
                        );
            }


        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            Map.of(
                                    "error",
                                    "Failed to delete coffee: "
                                            + e.getMessage()
                            )
                    );
        }
    }


    // =========================================================
    // TEST ENDPOINT
    // =========================================================

    @GetMapping(
            value = "/test",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> test() {

        Map<String, Object> response =
                new HashMap<>();

        response.put(
                "status",
                "OK"
        );

        response.put(
                "message",
                "Coffee API is working!"
        );

        response.put(
                "timestamp",
                System.currentTimeMillis()
        );

        response.put(
                "service",
                "Bean Haven Coffee Shop API"
        );


        return ResponseEntity.ok(response);
    }


    // =========================================================
    // HEALTH CHECK
    // =========================================================

    @GetMapping(
            value = "/health",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> health() {

        Map<String, Object> response =
                new HashMap<>();

        response.put(
                "status",
                "UP"
        );

        response.put(
                "service",
                "Coffee Shop Backend"
        );

        response.put(
                "timestamp",
                System.currentTimeMillis()
        );


        try {

            List<Coffee> coffees =
                    coffeeService.getAllCoffees();

            response.put(
                    "coffeeCount",
                    coffees.size()
            );

            response.put(
                    "database",
                    "Connected"
            );


        } catch (Exception e) {

            response.put(
                    "database",
                    "Error: " + e.getMessage()
            );
        }


        return ResponseEntity.ok(response);
    }


    // =========================================================
    // GET COFFEES BY CATEGORY
    // =========================================================

    @GetMapping(
            value = "/category/{category}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> getCoffeesByCategory(
            @PathVariable String category
    ) {

        try {

            List<Coffee> coffees =
                    coffeeService.getCoffeesByCategory(category);


            return ResponseEntity.ok(coffees);


        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            Map.of(
                                    "error",
                                    e.getMessage()
                            )
                    );
        }
    }


    // =========================================================
    // GET AVAILABLE COFFEES
    // =========================================================

    @GetMapping(
            value = "/available",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> getAvailableCoffees() {

        try {

            List<Coffee> coffees =
                    coffeeService.getAvailableCoffees();


            return ResponseEntity.ok(coffees);


        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            Map.of(
                                    "error",
                                    e.getMessage()
                            )
                    );
        }
    }


    // =========================================================
    // SEARCH COFFEES
    // =========================================================

    @GetMapping(
            value = "/search",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> searchCoffees(
            @RequestParam String keyword
    ) {

        try {

            List<Coffee> coffees =
                    coffeeService.searchCoffees(keyword);


            return ResponseEntity.ok(coffees);


        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            Map.of(
                                    "error",
                                    e.getMessage()
                            )
                    );
        }
    }
}