package org.acme.vehiclerouting.solver.justifications;

import ai.timefold.solver.core.api.score.stream.ConstraintJustification;


public record VisitOrderJustification(String firstVisitId, String secondVisitId, String description)
        implements ConstraintJustification {
 
    public VisitOrderJustification(String firstVisitId, String secondVisitId) {
        this(firstVisitId, secondVisitId,   "Visit '%s' must be visited before visit '%s'."
                .formatted(firstVisitId, secondVisitId));
    }

    
}
