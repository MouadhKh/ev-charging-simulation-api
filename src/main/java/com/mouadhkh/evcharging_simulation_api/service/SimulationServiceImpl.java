package com.mouadhkh.evcharging_simulation_api.service;

import com.mouadhkh.evcharging_simulation_api.dto.SimulationInputDTO;
import com.mouadhkh.evcharging_simulation_api.dto.SimulationResultDTO;
import com.mouadhkh.evcharging_simulation_api.entity.SimulationInput;
import com.mouadhkh.evcharging_simulation_api.entity.SimulationResult;
import com.mouadhkh.evcharging_simulation_api.repository.SimulationInputRepository;
import com.mouadhkh.evcharging_simulation_api.repository.SimulationResultRepository;
import com.mouadhkh.simulator.ChargingStationSimulator;
import com.mouadhkh.simulator.model.ChargePointMetric;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SimulationServiceImpl implements SimulationService {

    private final SimulationInputRepository simulationInputRepository;
    private final SimulationResultRepository simulationResultRepository;

    public SimulationServiceImpl(SimulationInputRepository simulationInputRepository,
                                 SimulationResultRepository simulationResultRepository) {
        this.simulationInputRepository = simulationInputRepository;
        this.simulationResultRepository = simulationResultRepository;
    }

    public Page<SimulationInputDTO> getAllSimulationInputs(Pageable pageable) {
        Page<SimulationInput> inputPage = simulationInputRepository.findAll(pageable);
        return inputPage.map(this::convertToDTO);
    }

    @Override
    public SimulationInputDTO getSimulationInputById(Long id) {
        return simulationInputRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new EntityNotFoundException("SimulationInput with id: " + id + " not found"));
    }

    @Transactional
    @Override
    public SimulationInputDTO createSimulationInput(SimulationInput simulationInput) {
        validateSimulationInput(simulationInput);
        SimulationInput savedInput = simulationInputRepository.save(simulationInput);
        return convertToDTO(savedInput);
    }

    @Transactional
    @Override
    public SimulationInputDTO updateSimulationInput(Long id, SimulationInput simulationInput) {
        return simulationInputRepository.findById(id)
                .map(existingInput -> {
                    validateSimulationInput(simulationInput);
                    existingInput.setNumberOfChargePoints(simulationInput.getNumberOfChargePoints());
                    existingInput.setArrivalProbabilityMultiplier(simulationInput.getArrivalProbabilityMultiplier());
                    existingInput.setEvConsumption(simulationInput.getEvConsumption());
                    existingInput.setChargingPowerPerChargePoint(simulationInput.getChargingPowerPerChargePoint());
                    SimulationInput updatedInput = simulationInputRepository.save(existingInput);
                    return convertToDTO(updatedInput);
                })
                .orElseThrow(() -> new EntityNotFoundException("SimulationInput with id: " + id + " not found"));
    }

    @Override
    public void deleteSimulationInput(Long id) {
        if (!simulationInputRepository.existsById(id)) {
            throw new EntityNotFoundException("SimulationInput with id: " + id + " not found");
        }
        simulationInputRepository.deleteById(id);
    }

    @Transactional
    @Override
    public SimulationResultDTO runMockSimulation(SimulationInput simulationInput) {
        validateSimulationInput(simulationInput);
        ChargingStationSimulator simulator = new ChargingStationSimulator(
                simulationInput.getNumberOfChargePoints(),
                simulationInput.getArrivalProbabilityMultiplier()
        );

        com.mouadhkh.simulator.simulation.SimulationResult simulationResult = simulator.run();
        List<Double> utilizationRates = new ArrayList<>();
        List<Double> averagePowers = new ArrayList<>();
        List<ChargePointMetric> chargingPointMetrics = simulationResult.getChargingPointsMetrics();
        for (ChargePointMetric metric : chargingPointMetrics) {
            utilizationRates.add(metric.getUtilizationRate());
            averagePowers.add(metric.getAveragePower());
        }
        SimulationResult result = SimulationResult.builder()
                .totalEnergyCharged(simulationResult.getTotalEnergyConsumed())
                .actualMaxPowerDemand(simulationResult.getActualMaxPowerDemand())
                .theoreticalMaxPowerDemand(simulationResult.getTheoreticalMaxPowerDemand())
                .concurrencyFactor(simulationResult.getConcurrencyFactor())
                .utilizationRates(utilizationRates)
                .averagePowers(averagePowers)
                .exemplaryDay(simulationResult.getExemplaryDay())
                .eventsPerYear(simulationResult.getYearlyChargingEvents())
                .eventsPerMonth(simulationResult.getMonthlyChargingEvents())
                .eventsPerWeek(simulationResult.getWeeklyChargingEvents())
                .eventsPerDay(simulationResult.getDailyChargingEvents())
                .simulationInput(simulationInput)
                .build();

        simulationInput.addSimulationResult(result);
        simulationInputRepository.save(simulationInput);
        SimulationResult savedResult = simulationResultRepository.save(result);
        return convertToDTO(savedResult);
    }

    @Transactional
    @Override
    public SimulationResultDTO runNewMockSimulationByInputId(Long id) {
        SimulationInput simulationInput = simulationInputRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("SimulationInput with id:" + id + " not found"));
        return runMockSimulation(simulationInput);
    }

    @Override
    public List<SimulationResultDTO> getSimulationResultByInputId(Long id) {
        simulationInputRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("SimulationInput with id: " + id + " not found"));

        List<SimulationResult> results = simulationResultRepository.findAllBySimulationInputId(id);

        return results.stream().map(this::convertToDTO).toList();
    }

    private void validateSimulationInput(SimulationInput simulationInput) {
        if (simulationInput.getNumberOfChargePoints() <= 0) {
            throw new IllegalArgumentException("Number of charge points must be positive");
        }
        if (simulationInput.getArrivalProbabilityMultiplier() != null &&
                (simulationInput.getArrivalProbabilityMultiplier() < 0.2
                        || simulationInput.getArrivalProbabilityMultiplier() > 2)) {
            throw new IllegalArgumentException("Arrival probability multiplier must be between 20% and 200%");
        }
        if (simulationInput.getEvConsumption() != null && simulationInput.getEvConsumption() <= 0) {
            throw new IllegalArgumentException("EV energy consumption must be positive");
        }
        if (simulationInput.getChargingPowerPerChargePoint() <= 0) {
            throw new IllegalArgumentException("Charging power per point must be positive");
        }
    }

    private SimulationInputDTO convertToDTO(SimulationInput simulationInput) {
        return new SimulationInputDTO(
                simulationInput.getId(),
                simulationInput.getNumberOfChargePoints(),
                simulationInput.getArrivalProbabilityMultiplier(),
                simulationInput.getEvConsumption(),
                simulationInput.getChargingPowerPerChargePoint()
        );
    }

    public Page<SimulationResultDTO> getAllSimulationResults(Pageable pageable) {
        Page<SimulationResult> resultPage = simulationResultRepository.findAll(pageable);
        return resultPage.map(this::convertToDTO);
    }
    public SimulationResultDTO getSimulationResultById(Long id) {
        SimulationResult result = simulationResultRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("SimulationResult not found with id: " + id));
        return convertToDTO(result);
    }


    private SimulationResultDTO convertToDTO(SimulationResult simulationResult) {
        return SimulationResultDTO.builder()
                .id(simulationResult.getId())
                .totalEnergyCharged(simulationResult.getTotalEnergyCharged())
                .actualMaxPowerDemand(simulationResult.getActualMaxPowerDemand())
                .theoreticalMaxPowerDemand(simulationResult.getTheoreticalMaxPowerDemand())
                .concurrencyFactor(simulationResult.getConcurrencyFactor())
                .averagePowers(simulationResult.getAveragePowers())
                .utilizationRates(simulationResult.getUtilizationRates())
                .exemplaryDay(simulationResult.getExemplaryDay())
                .eventsPerYear(simulationResult.getEventsPerYear())
                .eventsPerMonth(simulationResult.getEventsPerMonth())
                .eventsPerWeek(simulationResult.getEventsPerWeek())
                .eventsPerDay(simulationResult.getEventsPerDay())
                .build();
    }
}