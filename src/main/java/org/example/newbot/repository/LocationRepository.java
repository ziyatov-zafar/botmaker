package org.example.newbot.repository;

import org.example.newbot.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Long> {
    List<Location> findAllByUserIdAndActiveIsTrueOrderByIdAsc(Long userId);

    Location findByAddressAndUserId(String address,Long userId);
}
