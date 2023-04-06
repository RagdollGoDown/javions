package ch.epfl.javions.adsb;

import ch.epfl.javions.aircraft.IcaoAddress;


/**
 * the interface for a message after being parsed
 *
 * @author André Cadet (359392)
 * @author Emile Schüpbach (3347505)
 */
public interface Message {
    long timeStampNs();
    IcaoAddress icaoAddress();
}
