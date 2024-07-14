package com.mouadhkh.evcharging_simulation_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimulationInputDTO {
    private Long id;
    private int numberOfChargePoints;
    private double arrivalProbabilityMultiplier;
    private double evConsumption;
    private double chargingPowerPerPoint;

    // Simulations resulted from this input are omitted and can be accessed through dedicated endpoint
}
