/*
 * Copyright 2015 Open Networking Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.onosproject.net.flowobjective;

import org.onosproject.net.flow.TrafficTreatment;

import java.util.Collection;

/**
 * Represents a nexthop which will be translated by a driver
 * into the appropriate group or actions needed to implement
 * the function.
 */
public interface NextObjective extends Objective {

    /**
     * Represents the type of next phase to build.
     */
    enum Type {
        /**
         * A hashed packet processing.
         */
        HASHED,

        /**
         * Broadcast packet process.
         */
        BROADCAST,

        /**
         * Failover handling.
         */
        FAILOVER,

        /**
         * Simple processing. Could be a group or a treatment.
         */
        SIMPLE
    }

    /**
     * The collection of treatments that need to be applied to a set of traffic.
     *
     * @return a collection of traffic treatments
     */
    Collection<TrafficTreatment> next();

    /**
     * The type of operation that will be applied to the traffic using the collection
     * of treatments.
     *
     * @return a type
     */
    Type type();

    /**
     * A next step builder.
     */
    public interface Builder extends Objective.Builder {

        /**
         * Specifies the id for this next objective.
         *
         * @param nextId an integer
         * @return a next objective builder
         */
        public Builder withId(int nextId);

        /**
         * Sets the type of next step.
         *
         * @param type a type
         * @return a next step builder
         */
        public Builder withType(Type type);

        /**
         * Adds a treatment to this next step.
         *
         * @param treatment a traffic treatment
         * @return a next step builder
         */
        public Builder addTreatment(TrafficTreatment treatment);

        /**
         * Builds a next step.
         *
         * @return a next step
         */
        public NextObjective build();

    }

}
