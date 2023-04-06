package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;
import ch.epfl.javions.aircraft.IcaoAddress;

/**
 * Records the timeStamp, the Icao Address, the speed of the plane and either the track or the heading
 * @param timeStampNs the time at which the message was received in nanoseconds
 * @param icaoAddress the Icao Address of the plane being recorded
 * @param speed the speed of the plane in question in meters per second
 * @param trackOrHeading the direction of the plane in radians
 *
 * @author André Cadet (359392)
 * @author Emile Schüpbach(3347505)
 */
public record AirborneVelocityMessage(long timeStampNs, IcaoAddress icaoAddress, double speed, double trackOrHeading) implements Message{
    private final static int SUB_TYPE_START = 48;
    private final static int SUB_TYPE_SIZE = 3;

    private final static int DEW_POSITION = 42;
    private final static int VEW_START = 32;
    private final static int DNS_POSITION = 31;
    private final static int VNS_START = 21;
    private final static  int VEW_VNS_SIZE = 10;

    private final static int SH_POSITION = 42;
    private final static int CAP_START = 32;
    private final static int CAP_SIZE = 10;
    private final static int CONVERSION_BIT_CAP = 1024;
    private final static int AIRSPEED_START = 21;
    private final static int AIRSPEED_SIZE = 10;

    /**
     * Constructor of AirborneVelocityMessage
     * @param timeStampNs time the message was sent
     * @param icaoAddress IcaoAddress of the plane
     * @param speed the speed of said plane
     * @param trackOrHeading the direction the plane is facing
     * @throws IllegalArgumentException if either timeStamp,speed ot trackOrHeading are negative
     * @throws NullPointerException if IcaoAddress is null
     */
    public AirborneVelocityMessage{
        if (icaoAddress == null) throw new NullPointerException();
        Preconditions.checkArgument(timeStampNs >= 0 && speed >= 0 && trackOrHeading >= 0);
    }

    /**
     * Build an instance of AirborneVelocityMessage
     * Calculates the speed of the plane based on the sous-type
     * @param message contains the payload that contains the speed and the subtype
     *                as well as the rest that we need to create the instance
     * @return the created AirborneVelocityMessage
     *         null if the subType isn't valid
     *         null if the trackOrHeading is null
     */
    public static AirborneVelocityMessage of(RawMessage message){

        long payload = message.payload();

        int subType = Bits.extractUInt(payload, SUB_TYPE_START, SUB_TYPE_SIZE);

        if (subType > 4 || subType == 0){return null;}

        double[] speedAndTrackOrHeading = subType < 3 ?
                groundSpeedAndRotation(payload,subType):airSpeedAndRotation(payload,subType);

        if (speedAndTrackOrHeading == null){return null;}

        return new AirborneVelocityMessage(message.timeStampNs(), message.icaoAddress(),
                speedAndTrackOrHeading[0],speedAndTrackOrHeading[1]);
    }

    /**
     * Calculates the speed and track
     * @param payload the long containing the vector form of the speed
     * @param subType either 1 for normal speed or 2 for subsonic speed
     * @return an array with in first position the speed and in second the track
     *         null if the vector components are invalid
     */
    private static double[] groundSpeedAndRotation(long payload, int subType){
        int vew = Bits.extractUInt(payload, VEW_START,VEW_VNS_SIZE)-1;
        int vns = Bits.extractUInt(payload, VNS_START,VEW_VNS_SIZE)-1;

        if (vew < 0 || vns < 0) return null;

        int ewCoords = vew * (Bits.testBit(payload,DEW_POSITION) ? -1:1);
        int nsCoords = vns * (Bits.testBit(payload,DNS_POSITION) ? -1:1);

        double track = Math.atan2(ewCoords,nsCoords);
        double speed = Units.convert(Math.hypot(nsCoords,ewCoords),Units.Speed.KNOT,Units.Speed.METER_PER_SECOND);

        if (track < 0) {track = 2*Math.PI + track;}

        if (subType == 2){
            return new double[]{speed * 4,track};
        }
        else {
            return new double[]{speed,track};
        }
    }

    /**
     * Calculates the speed and heading of the plain
     * @param payload the long containing both elements
     * @param subType either 3 for normal speed or 4 for subsonic speed
     * @return an array with in first position the speed and in second the heading
     *         null if the payload is invalid for the subType, checks the bit SH
     *         null if the speed is equal to -1
     */
    private static double[] airSpeedAndRotation(long payload, int subType){
        if (!Bits.testBit(payload, SH_POSITION)){return null;}

        double heading = ((double)  (Bits.extractUInt(payload, CAP_START, CAP_SIZE))) / CONVERSION_BIT_CAP;
        heading = Units.convert(heading, Units.Angle.TURN, Units.Angle.RADIAN);

        if (Bits.extractUInt(payload,AIRSPEED_START,AIRSPEED_SIZE) == 0) return null;

        double airSpeed = Bits.extractUInt(payload,AIRSPEED_START,AIRSPEED_SIZE) - 1;
        airSpeed  = Units.convert(airSpeed, Units.Speed.KNOT, Units.Speed.METER_PER_SECOND);

        if (subType == 4){
            return new double[]{airSpeed * 4, heading};
        }
        else {
            return new double[]{airSpeed,heading};
        }
    }
}
