package org.acme.vehiclerouting.solver;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import ai.timefold.solver.core.api.domain.variable.VariableListener;
import ai.timefold.solver.core.api.score.director.ScoreDirector;

import org.acme.vehiclerouting.domain.Visit;
import org.acme.vehiclerouting.domain.VehicleRoutePlan;

// TODO: update this to take the demand change into account
public class ArrivalTimeUpdatingVariableListener implements VariableListener<VehicleRoutePlan, Visit> {

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
        LocalDateTime departureTime =
                previousVisit == null ? visit.getVehicle().getDepartureTime() : previousVisit.getDepartureTime();

        Visit nextVisit = visit;
        LocalDateTime arrivalTime = calculateArrivalTime(nextVisit, departureTime);
        while (nextVisit != null && !Objects.equals(nextVisit.getArrivalTime(), arrivalTime)) {
            scoreDirector.beforeVariableChanged(nextVisit, ARRIVAL_TIME_FIELD);
            nextVisit.setArrivalTime(arrivalTime);
            scoreDirector.afterVariableChanged(nextVisit, ARRIVAL_TIME_FIELD);
        //    updateDemand(scoreDirector, nextVisit);
        //    updateDemand(scoreDirector, nextVisit.getNextDelivery());
            departureTime = nextVisit.getDepartureTime();
            nextVisit = nextVisit.getNextVisit();
            arrivalTime = calculateArrivalTime(nextVisit, departureTime);
        }
    }

    private void updateDemand(ScoreDirector<VehicleRoutePlan> scoreDirector, Visit nextVisit) {
        if (nextVisit == null) {
            return;
        }
       // Visit previousDelivery = nextVisit.getPreviousDelivery();
      //  Integer demand = calculateDemand(previousDelivery, nextVisit);
        //if (!Objects.equals(customer.getDemand(), demand)) {
        scoreDirector.beforeVariableChanged(nextVisit, "demand");
        nextVisit.setDemand(0);
        scoreDirector.afterVariableChanged(nextVisit, "demand");
    }

    private Integer calculateDemand(Visit previousDelivery, Visit visit) {
          Integer demand = null;
        if (visit.getArrivalTime() != null) {
            if (previousDelivery != null) {
                if (previousDelivery.getArrivalTime() != null) {
                   // demand = calculateDemand(previousDelivery.getArrivalTime(), visit.getArrivalTime(), visit.getLocation().getRate());
                }
            } else { // demand for first pickup, after changes, this should never happen
                demand = 0;
            }
        }

        return demand;
    }

    private Integer calculateDemand(LocalDateTime startTime, LocalDateTime endtime, float rate) {
        //get the difference in days between the two dates
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
