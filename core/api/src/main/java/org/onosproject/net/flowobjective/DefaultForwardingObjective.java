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

import org.onosproject.core.ApplicationId;
import org.onosproject.net.flow.TrafficSelector;
import org.onosproject.net.flow.TrafficTreatment;

import java.util.Objects;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Default implementation of a forwarding objective.
 */
public final class DefaultForwardingObjective implements ForwardingObjective {

    private final TrafficSelector selector;
    private final Flag flag;
    private final boolean permanent;
    private final int timeout;
    private final ApplicationId appId;
    private final int priority;
    private final int nextId;
    private final TrafficTreatment treatment;
    private final Operation op;

    private final int id;

    private DefaultForwardingObjective(TrafficSelector selector,
                                      Flag flag, boolean permanent,
                                      int timeout, ApplicationId appId,
                                      int priority, int nextId,
                                      TrafficTreatment treatment, Operation op) {
        this.selector = selector;
        this.flag = flag;
        this.permanent = permanent;
        this.timeout = timeout;
        this.appId = appId;
        this.priority = priority;
        this.nextId = nextId;
        this.treatment = treatment;
        this.op = op;

        this.id = Objects.hash(selector, flag, permanent,
                               timeout, appId, priority, nextId,
                               treatment, op);
    }


    @Override
    public TrafficSelector selector() {
        return selector;
    }

    @Override
    public Integer nextId() {
        return nextId;
    }

    @Override
    public TrafficTreatment treatment() {
        return treatment;
    }


    @Override
    public Flag flag() {
        return flag;
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public int priority() {
        return priority;
    }

    @Override
    public ApplicationId appId() {
        return appId;
    }

    @Override
    public int timeout() {
        return timeout;
    }

    @Override
    public boolean permanent() {
        return permanent;
    }

    @Override
    public Operation op() {
        return op;
    }

    /**
     * Returns a new builder.
     *
     * @return new builder
     */
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder implements ForwardingObjective.Builder {

        private TrafficSelector selector;
        private Flag flag;
        private boolean permanent = DEFAULT_PERMANENT;
        private int timeout = DEFAULT_TIMEOUT;
        private int priority = DEFAULT_PRIORITY;
        private ApplicationId appId;
        private Integer nextId;
        private TrafficTreatment treatment;

        @Override
        public Builder withSelector(TrafficSelector selector) {
            this.selector = selector;
            return this;
        }

        @Override
        public Builder nextStep(int nextId) {
            this.nextId = nextId;
            return this;
        }

        @Override
        public Builder withTreatment(TrafficTreatment treatment) {
            this.treatment = treatment;
            return this;
        }

        @Override
        public Builder withFlag(Flag flag) {
            this.flag = flag;
            return this;
        }

        @Override
        public Builder makeTemporary(int timeout) {
            this.timeout = timeout;
            this.permanent = false;
            return this;
        }

        @Override
        public Builder makePermanent() {
            this.permanent = true;
            return this;
        }

        @Override
        public Builder fromApp(ApplicationId appId) {
            this.appId = appId;
            return this;
        }

        @Override
        public Builder withPriority(int priority) {
            this.priority = priority;
            return this;
        }

        @Override
        public ForwardingObjective add() {
            checkNotNull(selector, "Must have a selector");
            checkNotNull(flag, "A flag must be set");
            checkArgument(nextId != null && treatment != null, "Must supply at " +
                    "least a treatment and/or a nextId");
            checkNotNull(appId, "Must supply an application id");
            return new DefaultForwardingObjective(selector, flag, permanent,
                                                  timeout, appId, priority,
                                                  nextId, treatment, Operation.ADD);
        }

        @Override
        public ForwardingObjective remove() {
            checkNotNull(selector, "Must have a selector");
            checkNotNull(flag, "A flag must be set");
            checkArgument(nextId != null && treatment != null, "Must supply at " +
                    "least a treatment and/or a nextId");
            checkNotNull(appId, "Must supply an application id");
            return new DefaultForwardingObjective(selector, flag, permanent,
                                                   timeout, appId, priority,
                                                   nextId, treatment, Operation.REMOVE);
        }
    }
}
