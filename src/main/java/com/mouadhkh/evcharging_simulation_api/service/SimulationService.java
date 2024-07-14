package com.mouadhkh.evcharging_simulation_api.service;

import com.mouadhkh.evcharging_simulation_api.dto.SimulationInputDTO;
import com.mouadhkh.evcharging_simulation_api.dto.SimulationResultDTO;
import com.mouadhkh.evcharging_simulation_api.entity.SimulationInput;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SimulationService {
    Page<SimulationInputDTO> getAllSimulationInputs(Pageable pageable);

    SimulationInputDTO getSimulationInputById(Long id);

    SimulationInputDTO createSimulationInput(SimulationInput simulationInput);

    SimulationInputDTO updateSimulationInput(Long id, SimulationInput simulationInput);

    void deleteSimulationInput(Long id);

    SimulationResultDTO runMockSimulation(SimulationInput simulationInput);

    List<SimulationResultDTO> getSimulationResultByInputId(Long id);

    SimulationResultDTO runNewMockSimulationByInputId(Long id);

    Page<SimulationResultDTO> getAllSimulationResults(Pageable pageable);

    SimulationResultDTO getSimulationResultById(Long id);
}
