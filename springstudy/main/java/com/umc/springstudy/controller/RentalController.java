package com.umc.springstudy.controller;

import com.umc.springstudy.service.RentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rentals")
@RequiredArgsConstructor
public class RentalController {
    private final RentalService rentalService;

    @PostMapping
    public String createRental(@RequestBody Map<String, Object> body){
        rentalService.createRental(body);
        return "대출이 완료되었습니다!";
    }
}
