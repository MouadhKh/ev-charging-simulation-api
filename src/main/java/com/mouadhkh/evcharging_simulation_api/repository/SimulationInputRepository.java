package com.mouadhkh.evcharging_simulation_api.repository;

import com.mouadhkh.evcharging_simulation_api.entity.SimulationInput;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SimulationInputRepository extends JpaRepository<SimulationInput, Long> {
}
