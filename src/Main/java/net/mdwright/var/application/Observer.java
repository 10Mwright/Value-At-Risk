package net.mdwright.var.application;

/**
 * Observer interface for Observers. Code taken from Dave Cohen via CS2800 Moodle Page
 *
 * @author Dave Cohen
 */
@FunctionalInterface
public interface Observer {

  void tell();
}

