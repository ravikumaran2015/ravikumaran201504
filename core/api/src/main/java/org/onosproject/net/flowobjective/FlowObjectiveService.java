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

import org.onosproject.net.DeviceId;

import java.util.Collection;
import java.util.concurrent.Future;

/**
 * Service for programming data plane flow rules in manner independent of
 * specific device table pipeline configuration.
 */
public interface FlowObjectiveService {

    /**
     * Installs the filtering rules onto the specified device.
     *
     * @param deviceId            device identifier
     * @param filteringObjectives the collection of filters
     * @return a future indicating the success of the operation
     */
    Future<Boolean> filter(DeviceId deviceId, Collection<FilteringObjective> filteringObjectives);

    /**
     * Installs the forwarding rules onto the specified device.
     *
     * @param deviceId             device identifier
     * @param forwardingObjectives the collection of forwarding objectives
     * @return a future indicating the success of the operation
     */
    Future<Boolean> forward(DeviceId deviceId, Collection<ForwardingObjective> forwardingObjectives);

    /**
     * Installs the next hop elements into the specified device.
     *
     * @param deviceId       device identifier
     * @param nextObjectives the collection of next objectives
     * @return a future indicating the success of the operation
     */
    Future<Boolean> next(DeviceId deviceId, Collection<NextObjective> nextObjectives);

}
