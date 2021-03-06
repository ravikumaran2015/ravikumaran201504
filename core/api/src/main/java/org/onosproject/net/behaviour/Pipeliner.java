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
package org.onosproject.net.behaviour;

import org.onosproject.net.DeviceId;
import org.onosproject.net.driver.HandlerBehaviour;
import org.onosproject.net.flowobjective.FilteringObjective;
import org.onosproject.net.flowobjective.ForwardingObjective;
import org.onosproject.net.flowobjective.NextObjective;

import java.util.Collection;
import java.util.concurrent.Future;

/**
 * Behaviour for handling various pipelines.
 */
public interface Pipeliner extends HandlerBehaviour {

    /**
     * Initializes the driver with context required for its operation.
     *
     * @param deviceId the deviceId
     * @param context  processing context
     */
    void init(DeviceId deviceId, PipelinerContext context);

    /**
     * Installs the filtering rules onto the device.
     *
     * @param filterObjectives the collection of filters
     * @return a future indicating the success of the operation
     */
    Future<Boolean> filter(Collection<FilteringObjective> filterObjectives);

    /**
     * Installs the forwarding rules onto the device.
     *
     * @param forwardObjectives the collection of forwarding objectives
     * @return a future indicating the success of the operation
     */
    Future<Boolean> forward(Collection<ForwardingObjective> forwardObjectives);

    /**
     * Installs the next hop elements into the device.
     *
     * @param nextObjectives the collection of next objectives
     * @return a future indicating the success of the operation
     */
    Future<Boolean> next(Collection<NextObjective> nextObjectives);
}
