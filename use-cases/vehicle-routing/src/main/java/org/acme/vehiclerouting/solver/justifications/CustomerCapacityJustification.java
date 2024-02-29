package org.acme.vehiclerouting.solver.justifications;

import ai.timefold.solver.core.api.score.stream.ConstraintJustification;


public record CustomerCapacityJustification(String visitId, int demand, long capacity, String description)
        implements ConstraintJustification {
 
    public CustomerCapacityJustification(String visitId, int demand, long capacity) {
        this(visitId, demand, capacity,  "Visit '%s'  demand of %d exceeds the vehicle capacity of %d."
                .formatted(visitId, demand, capacity));
    }

    
}
