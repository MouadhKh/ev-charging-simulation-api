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
public class SimulationResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double totalEnergyCharged;
    private double actualMaxPowerDemand;
    private double theoreticalMaxPowerDemand;
    private double concurrencyFactor;

    @ManyToOne
    @JoinColumn(name = "simulation_input_id")
    private SimulationInput simulationInput;


    @ElementCollection
    @CollectionTable(name = "utilization_rates", joinColumns = @JoinColumn(name = "simulation_result_id"))
    @Column(name = "utilization_rate")
    @Builder.Default
    private List<Double> utilizationRates = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "average_powers", joinColumns = @JoinColumn(name = "simulation_result_id"))
    @Column(name = "average_power")
    @Builder.Default
    private List<Double> averagePowers = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "exemplary_day", joinColumns = @JoinColumn(name = "simulation_result_id"))
    @Column(name = "power_demand")
    @Builder.Default
    private List<Double> exemplaryDay = new ArrayList<>();

    private int eventsPerYear;

    @ElementCollection
    @CollectionTable(name = "events_per_month", joinColumns = @JoinColumn(name = "simulation_result_id"))
    @Column(name = "events_per_month")
    @Builder.Default
    private List<Integer> eventsPerMonth = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "events_per_week", joinColumns = @JoinColumn(name = "simulation_result_id"))
    @Column(name = "events_per_week")
    @Builder.Default
    private List<Integer> eventsPerWeek = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "events_per_day", joinColumns = @JoinColumn(name = "simulation_result_id"))
    @Column(name = "events_per_day")
    @Builder.Default
    private List<Integer> eventsPerDay = new ArrayList<>();


}