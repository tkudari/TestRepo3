package com.dashwire.asset.event;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Maintains a singleton instance for obtaining the bus. Ideally this would be replaced with a more efficient means
 * such as through injection directly into interested classes.
 *
 * This was "borrowed" from the DashConfig 2.0 code.
 *
 */
public final class BusProvider {
  private static final Bus BUS = new Bus(ThreadEnforcer.ANY);

  public static Bus getInstance() {
    return BUS;
  }

  private BusProvider() {
    // No instances.
  }
}
