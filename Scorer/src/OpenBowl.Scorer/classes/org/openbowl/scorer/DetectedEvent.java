/*
 * Copyright (C) 2020 Open Bowl <http://www.openbowlscoring.org/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openbowl.scorer;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
    public class DetectedEvent extends Event {
        //https://stackoverflow.com/questions/27416758/how-to-emit-and-handle-custom-events
        public static final EventType<DetectedEvent> DETECTION = new EventType<>(Event.ANY, "Detection");

        public DetectedEvent() {
            super(DETECTION);
        }

        public DetectedEvent(Object source, EventTarget target) {
            super(source, target, DETECTION);
        }

        @Override
        public DetectedEvent copyFor(Object newSource, EventTarget newTarget) {
            return (DetectedEvent) super.copyFor(newSource, newTarget);
        }

        @Override
        public EventType<? extends DetectedEvent> getEventType() {
            return (EventType<? extends DetectedEvent>) super.getEventType();
        }

    }
