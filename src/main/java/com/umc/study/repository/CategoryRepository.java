package com.umc.study.repository;

import com.umc.study.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

// POST /books에서 categoryId 존재 여부를 findById로 확인할 때 사용합니다.
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
