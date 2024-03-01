package org.acme.vehiclerouting.solver;

import ai.timefold.solver.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.ConstraintProvider;

import org.acme.vehiclerouting.domain.Visit;
import org.acme.vehiclerouting.domain.Vehicle;
import org.acme.vehiclerouting.solver.justifications.CustomerCapacityJustification;
import org.acme.vehiclerouting.solver.justifications.MinimizeTravelTimeJustification;
import org.acme.vehiclerouting.solver.justifications.ServiceFinishedAfterMaxEndTimeJustification;
import org.acme.vehiclerouting.solver.justifications.VehicleCapacityJustification;

public class VehicleRoutingConstraintProvider implements ConstraintProvider {

        public static final String VEHICLE_CAPACITY = "vehicleCapacity";
        public static final String SERVICE_FINISHED_AFTER_MAX_END_TIME = "serviceFinishedAfterMaxEndTime";
        public static final String MINIMIZE_TRAVEL_TIME = "minimizeTravelTime";

        @Override
        public Constraint[] defineConstraints(ConstraintFactory factory) {
                return new Constraint[] {
                                vehicleCapacity(factory),
                                serviceFinishedAfterMaxEndTime(factory),
                                minimizeTravelTime(factory),
                                customerCapacity(factory),
                               visitOrder(factory)
                };
        }

        // ************************************************************************
        // Hard constraints
        // ************************************************************************

      

        protected Constraint vehicleCapacity(ConstraintFactory factory) {
                return factory.forEach(Vehicle.class)
                                .filter(vehicle -> vehicle.getTotalDemand() > vehicle.getCapacity())
                                .penalizeLong(HardSoftLongScore.ONE_HARD,
                                                vehicle -> vehicle.getTotalDemand() - vehicle.getCapacity())
                                .justifyWith((vehicle, score) -> new VehicleCapacityJustification(vehicle.getId(),
                                                vehicle.getTotalDemand(),
                                                vehicle.getCapacity()))
                                .asConstraint(VEHICLE_CAPACITY);
        }

        protected Constraint serviceFinishedAfterMaxEndTime(ConstraintFactory factory) {
                return factory.forEach(Visit.class)
                                .filter(Visit::isServiceFinishedAfterMaxEndTime)
                                .penalizeLong(HardSoftLongScore.ONE_HARD,
                                                Visit::getServiceFinishedDelayInMinutes)
                                .justifyWith((visit, score) -> new ServiceFinishedAfterMaxEndTimeJustification(
                                                visit.getId(),
                                                visit.getServiceFinishedDelayInMinutes()))
                                .asConstraint(SERVICE_FINISHED_AFTER_MAX_END_TIME);
        }
/*
 * check that the arrivalTime of a visit is before the arrivalTime of the next visit in the customer's visit list
 */
        private Constraint visitOrder(ConstraintFactory factory) {
                return factory.forEachUniquePair(Visit.class)
                .filter((v1, v2) -> v1.getCustomer().equals(v2.getCustomer())&& v1.getId().compareTo(v2.getId()) < 0
                                && v1.getArrivalTime() != null && v2.getArrivalTime() != null
                                && v1.getArrivalTime().isAfter(v2.getArrivalTime()))
                                .penalizeLong(HardSoftLongScore.ONE_HARD, (v1, v2) -> 1)
                                .asConstraint("visitOrder");
        }
        /*
         * check that visit demand does not exceed the visit's customer capacity
         */
        private Constraint customerCapacity(ConstraintFactory factory) {
                return factory.forEach(Visit.class)
                                .filter(visit -> visit.getDemand() > visit.getCustomer().getCapacity())
                                .penalizeLong(HardSoftLongScore.ONE_HARD,
                                                visit -> visit.getDemand() - visit.getCustomer().getCapacity())
                                .justifyWith( (visit, score) -> new CustomerCapacityJustification(visit.getId(),
                                                visit.getDemand(),
                                                visit.getCustomer().getCapacity()))
                                .asConstraint("customerCapacity");
        }

        // ************************************************************************
        // Soft constraints
        // ************************************************************************

        protected Constraint minimizeTravelTime(ConstraintFactory factory) {
                return factory.forEach(Vehicle.class)
                                .penalizeLong(HardSoftLongScore.ONE_SOFT,
                                                Vehicle::getTotalDrivingTimeSeconds)
                                .justifyWith((vehicle, score) -> new MinimizeTravelTimeJustification(vehicle.getId(),
                                                vehicle.getTotalDrivingTimeSeconds()))
                                .asConstraint(MINIMIZE_TRAVEL_TIME);
        }
}
