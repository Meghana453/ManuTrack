package com.manutrack.module.production.repository;

import com.manutrack.module.production.entity.Machine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MachineRepository extends JpaRepository<Machine, Long> {
    List<Machine> findByPlantId(Long plantId);
    List<Machine> findByStatus(Machine.Status status);
}
