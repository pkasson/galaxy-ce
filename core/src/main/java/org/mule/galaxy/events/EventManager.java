package org.mule.galaxy.events;

public interface EventManager {

    void addListener(GalaxyEventListener listener);

    void removeListener(GalaxyEventListener listener);

    void fireEvent(GalaxyEvent event);
}
