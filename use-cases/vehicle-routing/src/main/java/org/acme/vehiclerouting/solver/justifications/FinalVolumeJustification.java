package org.acme.vehiclerouting.solver.justifications;

import ai.timefold.solver.core.api.score.stream.ConstraintJustification;

public record  FinalVolumeJustification(String visitId, int demand, int capacity, String description) implements ConstraintJustification  {

    public  FinalVolumeJustification(String visitId, int demand, int capacity)     {
        this(visitId, demand , capacity,    "Visit '%s' %d > %d."
        .formatted(visitId, demand, capacity));

    }

}
