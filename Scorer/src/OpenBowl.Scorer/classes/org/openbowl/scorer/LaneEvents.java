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
public class LaneEvents extends Event {

    public static final EventType<LaneEvents> DEFAULT = new EventType<>(Event.ANY, "Default");
    public static final EventType<LaneEvents> SLOW_BALL = new EventType<>("Slow_Ball");
    public static final EventType<LaneEvents> BOWL_EVENT = new EventType<>("Bowling_Event");
    
    public LaneEvents(EventType<LaneEvents> event){
        super(event);
    }

    public LaneEvents(Object source, EventTarget target) {
        super(source, target, DEFAULT);
    }

    @Override
    public LaneEvents copyFor(Object newSource, EventTarget newTarget) {
        return (LaneEvents) super.copyFor(newSource, newTarget);
    }

    @Override
    public EventType<? extends LaneEvents> getEventType() {
        return (EventType<? extends LaneEvents>) super.getEventType();
    }
}
