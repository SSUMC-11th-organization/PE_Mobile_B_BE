package com.umc.springstudy.service;

import com.umc.springstudy.repository.RentalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RentalService {
    private final RentalRepository rentalRepository;

    public void createRental(Map<String, Object> body){
        rentalRepository.save(body);
    }
}
