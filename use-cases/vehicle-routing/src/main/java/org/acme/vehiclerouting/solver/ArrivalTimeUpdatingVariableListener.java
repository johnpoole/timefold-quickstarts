package org.acme.vehiclerouting.solver;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;

import ai.timefold.solver.core.api.domain.variable.VariableListener;
import ai.timefold.solver.core.api.score.director.ScoreDirector;

import org.acme.vehiclerouting.domain.Visit;
import org.acme.vehiclerouting.domain.VehicleRoutePlan;

// TODO: update this to take the demand change into account
public class ArrivalTimeUpdatingVariableListener implements VariableListener<VehicleRoutePlan, Visit> {

    private static final String DEMAND_FIELD = "demand";
    private static final String ARRIVAL_TIME_FIELD = "arrivalTime";

    @Override
    public void beforeVariableChanged(ScoreDirector<VehicleRoutePlan> scoreDirector, Visit visit) {

    }

    @Override
    public void afterVariableChanged(ScoreDirector<VehicleRoutePlan> scoreDirector, Visit visit) {
        if (visit.getVehicle() == null) {
            if (visit.getArrivalTime() != null) {
                scoreDirector.beforeVariableChanged(visit, ARRIVAL_TIME_FIELD);
                visit.setArrivalTime(null);
                scoreDirector.afterVariableChanged(visit, ARRIVAL_TIME_FIELD);
            }
            return;
        }

        Visit previousVisit = visit.getPreviousVisit();
        LocalDateTime departureTime = previousVisit == null ? visit.getVehicle().getDepartureTime()
                : previousVisit.getDepartureTime();

        Visit nextVisit = visit;
        LocalDateTime arrivalTime = calculateArrivalTime(nextVisit, departureTime);
        while (nextVisit != null && !Objects.equals(nextVisit.getArrivalTime(), arrivalTime)) {
            safeSetArrivalTime(scoreDirector, nextVisit, arrivalTime);
            updateDemand(scoreDirector, nextVisit);
            updateDemand(scoreDirector, nextVisit.getNextDelivery()); //not the same as the next visit, so both need to be updated
            departureTime = nextVisit.getDepartureTime();
            nextVisit = nextVisit.getNextVisit();
            arrivalTime = calculateArrivalTime(nextVisit, departureTime);
        }
    }

    private void safeSetArrivalTime(ScoreDirector<VehicleRoutePlan> scoreDirector, Visit nextVisit, LocalDateTime arrivalTime) {
        scoreDirector.beforeVariableChanged(nextVisit, ARRIVAL_TIME_FIELD);
        nextVisit.setArrivalTime(arrivalTime);
        scoreDirector.afterVariableChanged(nextVisit, ARRIVAL_TIME_FIELD);
    }

    private void updateDemand(ScoreDirector<VehicleRoutePlan> scoreDirector, Visit visit) {
        if (visit == null || visit.getArrivalTime() == null) {
            return;
        }
        Visit previousDelivery = visit.getPreviousDelivery();
        Integer demand = calculateDemand(previousDelivery, visit).orElseGet(
                () -> {
                    if (visit.getArrivalTime() == null) {
                        return 0;
                    }
                    int d =  calculateDemand(visit.getCustomer().getSensorReading().getDate().atStartOfDay(),
                            visit.getArrivalTime(),
                            visit.getCustomer().getRate()) + visit.getCustomer().getSensorReading().getValue();
                    return d;
                });
        safeSetDemand(scoreDirector, visit, demand);
    }

    private void safeSetDemand(ScoreDirector<VehicleRoutePlan> scoreDirector, Visit visit, Integer demand) {
        if (!Objects.equals(visit.getDemand(), demand)) {
            scoreDirector.beforeVariableChanged(visit, DEMAND_FIELD);
            visit.setDemand(demand);
            scoreDirector.afterVariableChanged(visit, DEMAND_FIELD);
        }
    }

    private Optional<Integer> calculateDemand(Visit previousDelivery, Visit visit) {
        if (visit.getArrivalTime() != null && previousDelivery != null && previousDelivery.getArrivalTime() != null) {
            return Optional.of(calculateDemand(previousDelivery.getArrivalTime(), visit.getArrivalTime(),
                    visit.getCustomer().getRate()));
        }
        return Optional.empty();
    }

    private Integer calculateDemand(LocalDateTime startTime, LocalDateTime endtime, float rate) {
        // get the difference in days between the two dates
        int days = (int) ChronoUnit.DAYS.between(startTime, endtime);
        return (int) (days * rate);
    }

    protected Integer calculateDemand(long startTime, long endTime, double rate) {
        return (int) ((endTime - startTime) * rate);
    }

    @Override
    public void beforeEntityAdded(ScoreDirector<VehicleRoutePlan> scoreDirector, Visit visit) {

    }

    @Override
    public void afterEntityAdded(ScoreDirector<VehicleRoutePlan> scoreDirector, Visit visit) {

    }

    @Override
    public void beforeEntityRemoved(ScoreDirector<VehicleRoutePlan> scoreDirector, Visit visit) {

    }

    @Override
    public void afterEntityRemoved(ScoreDirector<VehicleRoutePlan> scoreDirector, Visit visit) {

    }

    private LocalDateTime calculateArrivalTime(Visit visit, LocalDateTime previousDepartureTime) {
        if (visit == null || previousDepartureTime == null) {
            return null;
        }
        return previousDepartureTime.plusSeconds(visit.getDrivingTimeSecondsFromPreviousStandstill());
    }
}
