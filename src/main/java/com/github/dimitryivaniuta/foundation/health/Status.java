package com.github.dimitryivaniuta.foundation.health;

/**
 * Enumeration of possible health states for liveness and readiness checks.
 */
public enum Status {

    /**
     * The application or component is operating normally.
     */
    UP,

    /**
     * The application or component is not operating normally.
     */
    DOWN,

    /**
     * The application or component's status is unknown or not checked.
     */
    UNKNOWN
}
