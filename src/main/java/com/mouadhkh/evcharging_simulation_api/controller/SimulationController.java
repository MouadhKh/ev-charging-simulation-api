package com.mouadhkh.evcharging_simulation_api.controller;

import com.mouadhkh.evcharging_simulation_api.dto.SimulationInputDTO;
import com.mouadhkh.evcharging_simulation_api.dto.SimulationResultDTO;
import com.mouadhkh.evcharging_simulation_api.entity.SimulationInput;
import com.mouadhkh.evcharging_simulation_api.service.SimulationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "EV Charging Simulation", description = "An API that simulates how EV chargers are used based on probabilities and input parameters")
@RestController
@RequestMapping("/api")
public class SimulationController {

    private final SimulationService simulationService;

    public SimulationController(SimulationService simulationService) {
        this.simulationService = simulationService;
    }

    @Operation(summary = "Get all simulation inputs", description = "Returns a paginated list of all simulation inputs")
    @ApiResponse(responseCode = "200", description = "Successful operation")
    @GetMapping("/simulation-inputs")
    public ResponseEntity<Page<SimulationInputDTO>> getAllSimulationInputs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<SimulationInputDTO> inputs = simulationService.getAllSimulationInputs(pageable);
        return ResponseEntity.ok(inputs);
    }

    @Operation(summary = "Get simulation input by Id", description = "Returns a simulation input by its ID")
    @ApiResponse(responseCode = "200", description = "Successful operation")
    @ApiResponse(responseCode = "404", description = "Simulation input not found")
    @GetMapping("/simulation-inputs/{id}")
    public ResponseEntity<?> getSimulationInputById(@PathVariable Long id) {
        try {
            SimulationInputDTO inputDTO = simulationService.getSimulationInputById(id);
            return ResponseEntity.ok(inputDTO);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    @Operation(summary = "Create simulation input", description = "Creates a new simulation input")
    @ApiResponse(responseCode = "201", description = "Simulation input created")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    @PostMapping("/simulation-inputs")
    public ResponseEntity<?> createSimulationInput(@RequestBody SimulationInput simulationInput) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(simulationService.createSimulationInput(simulationInput));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Update simulation input", description = "Updates an existing simulation input")
    @ApiResponse(responseCode = "200", description = "Simulation input updated successfully")
    @ApiResponse(responseCode = "404", description = "Simulation input not found")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    @PutMapping("/simulation-inputs/{id}")
    public ResponseEntity<?> updateSimulationInput(@PathVariable Long id, @RequestBody SimulationInput simulationInput) {
        try {
            SimulationInputDTO updatedInput = simulationService.updateSimulationInput(id, simulationInput);
            return ResponseEntity.ok(updatedInput);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Delete simulation input", description = "Deletes a simulation input")
    @ApiResponse(responseCode = "204", description = "Simulation input deleted successfully")
    @ApiResponse(responseCode = "404", description = "Simulation input not found")
    @DeleteMapping("/simulation-inputs/{id}")
    public ResponseEntity<Void> deleteSimulationInput(@PathVariable Long id) {
        try {
            simulationService.deleteSimulationInput(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Run mock simulation", description = "Runs and persists a mock simulation with provided input(also persisted)")
    @ApiResponse(responseCode = "200", description = "Simulation run successfully")
    @PostMapping("/run-simulation")
    public ResponseEntity<SimulationResultDTO> runMockSimulation(@RequestBody SimulationInput simulationInput) {
        return ResponseEntity.ok(simulationService.runMockSimulation(simulationInput));
    }


    @Operation(summary = "Run mock simulation by input Id", description = "Runs a NEW mock simulation using an existing input")
    @ApiResponse(responseCode = "200", description = "Simulation run successfully")
    @ApiResponse(responseCode = "404", description = "Simulation input not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @PostMapping("/run-simulation/{id}")
    public ResponseEntity<?> runMockSimulation(@PathVariable Long id) {
        try {
            SimulationResultDTO result = simulationService.runNewMockSimulationByInputId(id);
            return ResponseEntity.ok(result);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Simulation input with id " + id + " not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while running the simulation: " + e.getMessage());
        }
    }

    @Operation(summary = "Get simulation results by input Id", description = "Retrieves simulation results for a specific input")
    @ApiResponse(responseCode = "200", description = "Results retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Simulation input not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")

    @GetMapping("/simulation-results-by-input/{inputId}")
    public ResponseEntity<?> getSimulationResultByInputId(@PathVariable Long inputId) {
        try {
            List<SimulationResultDTO> results = simulationService.getSimulationResultByInputId(inputId);
            return ResponseEntity.ok(results);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while fetching the simulation result: " + e.getMessage());
        }
    }

    @Operation(summary = "Get all simulation results", description = "Retrieves a paginated list of all simulation results")
    @ApiResponse(responseCode = "200", description = "Results retrieved successfully")
    @GetMapping("/simulation-results")
    public ResponseEntity<Page<SimulationResultDTO>> getAllSimulationResults(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<SimulationResultDTO> results = simulationService.getAllSimulationResults(PageRequest.of(page, size));
        return ResponseEntity.ok(results);
    }

    @Operation(summary = "Get simulation result by Id", description = "Retrieves a specific simulation result")
    @ApiResponse(responseCode = "200", description = "Result retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Simulation result not found")
    @GetMapping("/simulation-results/{id}")
    public ResponseEntity<?> getSimulationResultById(@PathVariable Long id) {
        try {
            SimulationResultDTO result = simulationService.getSimulationResultById(id);
            return ResponseEntity.ok(result);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Simulation result with id " + id + " not found");
        }
    }
}
