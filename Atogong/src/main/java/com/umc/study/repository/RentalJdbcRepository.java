package com.umc.study.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
@RequiredArgsConstructor
public class RentalJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public void save(Map<String, Object> body) {
        // rental_id는 AUTO_INCREMENT, returned_at은 아직 반납 전이므로 NULL
        String sql = "INSERT INTO rental (user_id, book_id, rented_at, due_at, returned_at) "
                + "VALUES (?, ?, NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY), NULL)";

        jdbcTemplate.update(sql, body.get("userId"), body.get("bookId"));
    }

    public int updateReturnedAt(Long rentalId) {
        String sql = "UPDATE rental SET returned_at = NOW() WHERE rental_id = ?";
        // update()는 영향받은 행 수를 반환 → 0이면 해당 rentalId가 없는 것
        return jdbcTemplate.update(sql, rentalId);
    }
}