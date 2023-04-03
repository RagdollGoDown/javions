package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;
import ch.epfl.javions.aircraft.IcaoAddress;

public record AirborneVelocityMessage(long timeStampNs, IcaoAddress icaoAddress, double speed, double trackOrHeading) implements Message{
    //TODO trouver sous-type en anglais
    private final static int SOUS_TYPE_START = 48;
    private final static int SOUS_TYPE_SIZE = 3;

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

    public AirborneVelocityMessage{
        if (icaoAddress == null) throw new NullPointerException();
        Preconditions.checkArgument(timeStampNs >= 0 && speed >= 0 && trackOrHeading >= 0);
    }

    public static AirborneVelocityMessage of(RawMessage message){

        long payload = message.payload();

        //il faut peut-être mettre unsigned int
        int sousType = Bits.extractUInt(payload,SOUS_TYPE_START,SOUS_TYPE_SIZE);

        if (sousType > 4 || sousType == 0){return null;}

        double[] speedAndTrackOrHeading = sousType < 3 ?
                groundSpeedAndRotation(payload,sousType):airSpeedAndRotation(payload,sousType);

        if (speedAndTrackOrHeading == null){return null;}

        return new AirborneVelocityMessage(message.timeStampNs(), message.icaoAddress(),
                speedAndTrackOrHeading[0],speedAndTrackOrHeading[1]);
    }

    private static double[] groundSpeedAndRotation(long payload, int sousType){
        int vew = Bits.extractUInt(payload, VEW_START,VEW_VNS_SIZE)-1;
        int vns = Bits.extractUInt(payload, VNS_START,VEW_VNS_SIZE)-1;

        if (vew < 0 || vns < 0) return null;

        int ewCoords = vew * (Bits.testBit(payload,DEW_POSITION) ? -1:1);
        int nsCoords = vns * (Bits.testBit(payload,DNS_POSITION) ? -1:1);

        double track = Math.atan2(ewCoords,nsCoords);
        double speed = Units.convert(Math.hypot(nsCoords,ewCoords),Units.Speed.KNOT,Units.Speed.METER_PER_SECOND);

        //TODO voir s'il y a une meilleur méthode
        if (track < 0) {track = 2*Math.PI + track;}

        if (sousType == 2){
            return new double[]{speed * 4,track};
        }
        else {
            return new double[]{speed,track};
        }
    }

    private static double[] airSpeedAndRotation(long payload, int sousType){
        if (!Bits.testBit(payload, SH_POSITION)){return null;}

        double heading = ((double)  (Bits.extractUInt(payload, CAP_START, CAP_SIZE))) / CONVERSION_BIT_CAP;
        heading = Units.convert(heading, Units.Angle.TURN, Units.Angle.RADIAN);
        if (Bits.extractUInt(payload,AIRSPEED_START,AIRSPEED_SIZE) == 0) return null;
        double airSpeed = Bits.extractUInt(payload,AIRSPEED_START,AIRSPEED_SIZE) - 1;
        airSpeed  = Units.convert(airSpeed, Units.Speed.KNOT, Units.Speed.METER_PER_SECOND);
        if (sousType == 4){
            return new double[]{airSpeed * 4, heading};
        }
        else {
            return new double[]{airSpeed,heading};
        }
    }
}
