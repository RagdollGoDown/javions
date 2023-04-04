package ch.epfl.javions.adsb;

import ch.epfl.javions.aircraft.IcaoAddress;


/**
 * the interface for a message after being parsed
 */
public interface Message {
    long timeStampNs();
    IcaoAddress icaoAddress();
}
