package com.mouadhkh.evcharging_simulation_api.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimulationInput {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int numberOfChargePoints;

    @Builder.Default
    private Double arrivalProbabilityMultiplier = 1.0;

    @Builder.Default
    private Double evConsumption = 18.0;

    @Builder.Default
    private Double chargingPowerPerChargePoint = 11.0;

    @OneToMany(mappedBy = "simulationInput")
    private List<SimulationResult> simulationResults = new ArrayList<>();


    public void addSimulationResult(SimulationResult result) {
        simulationResults.add(result);
        result.setSimulationInput(this);
    }
    }