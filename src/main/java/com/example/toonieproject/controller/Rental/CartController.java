package com.example.toonieproject.controller.Rental;

import com.example.toonieproject.dto.Rental.Cart.CartRequest;
import com.example.toonieproject.dto.Rental.Cart.CartResponse;
import com.example.toonieproject.service.Rental.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PreAuthorize("hasRole('OWNER')")
    @PostMapping("/add")
    public ResponseEntity<String> addBooksToCart(@RequestBody CartRequest request) throws AccessDeniedException {
        cartService.addCartItems(request);


        return ResponseEntity.ok("Books added to cart successfully.");
    }

    @PreAuthorize("hasRole('OWNER')")
    @PostMapping("/remove")
    public ResponseEntity<String> removeBooksFromCart(@RequestBody CartRequest request) throws AccessDeniedException {
        cartService.removeCartItems(request);


        return ResponseEntity.ok("Books removed from cart successfully.");
    }

    @PreAuthorize("hasRole('OWNER')")
    @GetMapping("/rentable/{userId}")
    public ResponseEntity<List<CartResponse>> getRentableBooks(@PathVariable Long userId) throws AccessDeniedException {
        return ResponseEntity.ok(cartService.getCartBooksByUserAndRentable(userId, true));
    }

    @PreAuthorize("hasRole('OWNER')")
    @GetMapping("/unrentable/{userId}")
    public ResponseEntity<List<CartResponse>> getUnrentableBooks(@PathVariable Long userId) throws AccessDeniedException {
        return ResponseEntity.ok(cartService.getCartBooksByUserAndRentable(userId, false));
    }


}
