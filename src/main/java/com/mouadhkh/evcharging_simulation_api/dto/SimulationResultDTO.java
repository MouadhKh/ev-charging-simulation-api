package com.mouadhkh.evcharging_simulation_api.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class SimulationResultDTO {
    private Long id;
    private double totalEnergyCharged;
    private double actualMaxPowerDemand;
    private double theoreticalMaxPowerDemand;
    private double concurrencyFactor;
    private List<Double> exemplaryDay;
    private List<Double> utilizationRates;
    private List<Double> averagePowers;
    private int eventsPerYear;
    // can transform it to Map month->eventsCount/day->eventsCount but we keep it light in the backend.
    // Do it in the frontend
    private List<Integer> eventsPerMonth;
    private List<Integer> eventsPerWeek;
    private List<Integer> eventsPerDay;
}
