package com.umc.study.service;

import com.umc.study.repository.RentalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class RentalService {

    private final RentalRepository rentalRepository;

    public void createRental(Map<String, Object> body) {
        rentalRepository.save(body);
    }

    public String returnBook(Long rentalId) {
        int updated = rentalRepository.updateReturnedAt(rentalId);
        if (updated == 0) {
            return "해당 대여 기록을 찾을 수 없습니다. rentalId=" + rentalId;
        }
        return "도서 반납이 완료되었습니다!";
    }
}