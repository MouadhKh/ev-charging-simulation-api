package com.mouadhkh.evcharging_simulation_api.repository;

import com.mouadhkh.evcharging_simulation_api.entity.SimulationResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SimulationResultRepository extends JpaRepository<SimulationResult, Long> {
    List<SimulationResult> findAllBySimulationInputId(Long simulationInputId);
}
