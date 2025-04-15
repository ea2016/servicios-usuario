package com.easj.repository;

import com.easj.model.SolicitudItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolicitudItemRepository extends JpaRepository<SolicitudItem, Long> {
    List<SolicitudItem> findBySolicitudId(Long solicitudId);
}
