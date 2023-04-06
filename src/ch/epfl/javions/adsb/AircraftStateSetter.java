package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;

/**
 *Represents the modifiable state of an aircraft
 *
 * @author André Cadet (359392)
 * @author Emile Schüpbach Cadet (3347505)
 */
public interface AircraftStateSetter {
     /**
      * Set the TimeStamp of the last message
      * @param timeStampNs time stamps in Nanoseconds
      */
     void setLastMessageTimeStampNs(long timeStampNs);

     /**
      * Set the category
      * @param category the category
      */
     void setCategory(int category);

     /**
      * Set the CallSign
      * @param callSign the CallSign
      */
     void setCallSign(CallSign callSign);

     /**
      * Set the position
      * @param position the position
      */
     void setPosition(GeoPos position);

     /**
      * Set the altitude
      * @param altitude the altitude
      */
     void setAltitude(double altitude);

     /**
      * Set the velocity
      * @param velocity the velocity
      */
     void setVelocity(double velocity);

     /**
      * Changes the direction of the aircraft
      * @param trackOrHeading the value of the TrackOrHeading
      */
     void setTrackOrHeading(double trackOrHeading);
}
