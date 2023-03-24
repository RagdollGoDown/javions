package ch.epfl.javions.adsb;

import ch.epfl.javions.demodulation.AdsbDemodulator;


import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class AirbornePositionMessageTest {
    private record RawMessageData(long timeStampNs, String bytes) {}
    private static final List<RawMessageData> EXPECTED_RAW_MESSAGE_DATA = List.of(
            new RawMessageData(75898000L, "8D49529958B302E6E15FA352306B"),
            new RawMessageData(116538700L, "8D4241A9601B32DA4367C4C3965E"),
            new RawMessageData(138560100L, "8D4D222860B985F7F53FAB33CE76"),
            new RawMessageData(208135700L, "8D4D029F594B52EFDB7E94ACEAC8"),
            new RawMessageData(233069800L, "8D3C648158AF92F723BC275EC692"),
            new RawMessageData(10, "8D39203559B225F07550ADBE328F"),
            new RawMessageData(10, "8DAE02C85864A5F5DD4975A1A3F5"));

    private static final String[] ExpectedAircraftPositionMessages = {
            "AirbornePositionMessage[timeStampNs=75898000, icaoAddress=IcaoAddress[string=495299], altitude=10546.08, parity=0, x=0.6867904663085938, y=0.7254638671875]",
    "AirbornePositionMessage[timeStampNs=116538700, icaoAddress=IcaoAddress[string=4241A9], altitude=1303.02, parity=0, x=0.702667236328125, y=0.7131423950195312]",
    "AirbornePositionMessage[timeStampNs=138560100, icaoAddress=IcaoAddress[string=4D2228], altitude=10972.800000000001, parity=1, x=0.6243515014648438, y=0.4921417236328125]",
    "AirbornePositionMessage[timeStampNs=208135700, icaoAddress=IcaoAddress[string=4D029F], altitude=4244.34, parity=0, x=0.747222900390625, y=0.7342300415039062]",
    "AirbornePositionMessage[timeStampNs=233069800, icaoAddress=IcaoAddress[string=3C6481], altitude=10370.82, parity=0, x=0.8674850463867188, y=0.7413406372070312]",
    "AirbornePositionMessage[timeStampNs=349256100, icaoAddress=IcaoAddress[string=4B1A00], altitude=2796.54, parity=1, x=0.6786422729492188, y=0.5558624267578125]",
    "AirbornePositionMessage[timeStampNs=587873700, icaoAddress=IcaoAddress[string=4D2228], altitude=10972.800000000001, parity=0, x=0.64007568359375, y=0.6192169189453125]",
    "AirbornePositionMessage[timeStampNs=600759300, icaoAddress=IcaoAddress[string=39D300], altitude=11582.400000000001, parity=0, x=0.624267578125, y=0.6164398193359375]",
    "AirbornePositionMessage[timeStampNs=612521000, icaoAddress=IcaoAddress[string=4BCDE9], altitude=10972.800000000001, parity=1, x=0.7257156372070312, y=0.6263427734375]",
    "AirbornePositionMessage[timeStampNs=633078600, icaoAddress=IcaoAddress[string=3C6481], altitude=10370.82, parity=0, x=0.8675537109375, y=0.741455078125]",
    "AirbornePositionMessage[timeStampNs=645590600, icaoAddress=IcaoAddress[string=4D029F], altitude=4244.34, parity=0, x=0.7471542358398438, y=0.734161376953125]",
    "AirbornePositionMessage[timeStampNs=715212600, icaoAddress=IcaoAddress[string=4D0221], altitude=838.2, parity=0, x=0.6855926513671875, y=0.696197509765625]",
    "AirbornePositionMessage[timeStampNs=797810600, icaoAddress=IcaoAddress[string=4CA2BF], altitude=5791.200000000001, parity=0, x=0.5660781860351562, y=0.7490234375]",
    "AirbornePositionMessage[timeStampNs=812236600, icaoAddress=IcaoAddress[string=4951CE], altitude=6705.6, parity=0, x=0.8513717651367188, y=0.7908096313476562]",
    "AirbornePositionMessage[timeStampNs=857674200, icaoAddress=IcaoAddress[string=39CEAA], altitude=10363.2, parity=1, x=0.48673248291015625, y=0.5936203002929688]",
    "AirbornePositionMessage[timeStampNs=1008567300, icaoAddress=IcaoAddress[string=495299], altitude=10546.08, parity=0, x=0.6865463256835938, y=0.725311279296875]",
    "AirbornePositionMessage[timeStampNs=1032331100, icaoAddress=IcaoAddress[string=4B17E5], altitude=1905.0, parity=0, x=0.6668930053710938, y=0.66949462890625]",
    "AirbornePositionMessage[timeStampNs=1127252300, icaoAddress=IcaoAddress[string=4D2228], altitude=10972.800000000001, parity=1, x=0.6245880126953125, y=0.49233245849609375]",
    "AirbornePositionMessage[timeStampNs=1138542200, icaoAddress=IcaoAddress[string=4241A9], altitude=1310.64, parity=1, x=0.68560791015625, y=0.5846710205078125]",
    "AirbornePositionMessage[timeStampNs=1184750000, icaoAddress=IcaoAddress[string=4D029F], altitude=4236.72, parity=1, x=0.728851318359375, y=0.6051712036132812]",
    "AirbornePositionMessage[timeStampNs=1214082000, icaoAddress=IcaoAddress[string=394C13], altitude=10668.0, parity=1, x=0.44959259033203125, y=0.47266387939453125]",
    "AirbornePositionMessage[timeStampNs=1223069500, icaoAddress=IcaoAddress[string=3C6481], altitude=10370.82, parity=0, x=0.8676605224609375, y=0.7416305541992188]",
    "AirbornePositionMessage[timeStampNs=1244978500, icaoAddress=IcaoAddress[string=4D0221], altitude=822.96, parity=1, x=0.6689300537109375, y=0.5679931640625]",
    "AirbornePositionMessage[timeStampNs=1399281900, icaoAddress=IcaoAddress[string=4B1A00], altitude=2788.92, parity=0, x=0.6954269409179688, y=0.6837844848632812]",
    "AirbornePositionMessage[timeStampNs=1437501700, icaoAddress=IcaoAddress[string=39CEAA], altitude=10363.2, parity=1, x=0.48670196533203125, y=0.5938186645507812]",
    "AirbornePositionMessage[timeStampNs=1515918300, icaoAddress=IcaoAddress[string=39D300], altitude=11582.400000000001, parity=1, x=0.6088943481445312, y=0.48973846435546875]",
    "AirbornePositionMessage[timeStampNs=1580887900, icaoAddress=IcaoAddress[string=4D2228], altitude=10972.800000000001, parity=0, x=0.6403121948242188, y=0.6194076538085938]",
    "AirbornePositionMessage[timeStampNs=1581106900, icaoAddress=IcaoAddress[string=4BCDE9], altitude=10972.800000000001, parity=0, x=0.7435684204101562, y=0.7557525634765625]",
    "AirbornePositionMessage[timeStampNs=1645019700, icaoAddress=IcaoAddress[string=4D029F], altitude=4236.72, parity=0, x=0.7469863891601562, y=0.7339935302734375]",
    "AirbornePositionMessage[timeStampNs=1656866000, icaoAddress=IcaoAddress[string=440237], altitude=11277.6, parity=1, x=0.533203125, y=0.5020675659179688]",
    "AirbornePositionMessage[timeStampNs=1688150900, icaoAddress=IcaoAddress[string=3C6481], altitude=10370.82, parity=0, x=0.8677291870117188, y=0.7417449951171875]",
    "AirbornePositionMessage[timeStampNs=1714855000, icaoAddress=IcaoAddress[string=4CA2BF], altitude=5791.200000000001, parity=0, x=0.5660247802734375, y=0.748748779296875]",
    "AirbornePositionMessage[timeStampNs=1762693600, icaoAddress=IcaoAddress[string=4951CE], altitude=6705.6, parity=1, x=0.8307952880859375, y=0.6611328125]",
    "AirbornePositionMessage[timeStampNs=1835102700, icaoAddress=IcaoAddress[string=4D0221], altitude=822.96, parity=0, x=0.6857147216796875, y=0.6963272094726562]",
    "AirbornePositionMessage[timeStampNs=1896411000, icaoAddress=IcaoAddress[string=4B17E5], altitude=1897.38, parity=0, x=0.6667861938476562, y=0.6695785522460938]",
    "AirbornePositionMessage[timeStampNs=1955871700, icaoAddress=IcaoAddress[string=A4F239], altitude=10363.2, parity=1, x=0.5801239013671875, y=0.350616455078125]",
    "AirbornePositionMessage[timeStampNs=2011173700, icaoAddress=IcaoAddress[string=495299], altitude=10546.08, parity=1, x=0.669525146484375, y=0.5963821411132812]",
    "AirbornePositionMessage[timeStampNs=2075921200, icaoAddress=IcaoAddress[string=4D029F], altitude=4236.72, parity=0, x=0.746917724609375, y=0.7339248657226562]",
    "AirbornePositionMessage[timeStampNs=2076864400, icaoAddress=IcaoAddress[string=4241A9], altitude=1325.88, parity=0, x=0.70281982421875, y=0.7133026123046875]",
    "AirbornePositionMessage[timeStampNs=2118341200, icaoAddress=IcaoAddress[string=4D2228], altitude=10972.800000000001, parity=1, x=0.6248245239257812, y=0.49251556396484375]",
    "AirbornePositionMessage[timeStampNs=2186271000, icaoAddress=IcaoAddress[string=4B2964], altitude=4160.52, parity=1, x=0.7754287719726562, y=0.6389999389648438]",
    "AirbornePositionMessage[timeStampNs=2283115100, icaoAddress=IcaoAddress[string=3C6481], altitude=10370.82, parity=0, x=0.8678359985351562, y=0.7419204711914062]",
    "AirbornePositionMessage[timeStampNs=2289331500, icaoAddress=IcaoAddress[string=4B1A00], altitude=2788.92, parity=0, x=0.6952972412109375, y=0.6836700439453125]",
    "AirbornePositionMessage[timeStampNs=2345132300, icaoAddress=IcaoAddress[string=4D0221], altitude=822.96, parity=0, x=0.685760498046875, y=0.6963653564453125]",
    "AirbornePositionMessage[timeStampNs=2367505900, icaoAddress=IcaoAddress[string=39CEAA], altitude=10363.2, parity=0, x=0.49881744384765625, y=0.7228240966796875]",
    "AirbornePositionMessage[timeStampNs=2471212800, icaoAddress=IcaoAddress[string=495299], altitude=10546.08, parity=0, x=0.6861114501953125, y=0.72503662109375]",
    "AirbornePositionMessage[timeStampNs=2567937000, icaoAddress=IcaoAddress[string=4D2228], altitude=10972.800000000001, parity=0, x=0.6405563354492188, y=0.619598388671875]",
    "AirbornePositionMessage[timeStampNs=2580798000, icaoAddress=IcaoAddress[string=39D300], altitude=11582.400000000001, parity=0, x=0.6239242553710938, y=0.6169815063476562]",
    "AirbornePositionMessage[timeStampNs=2584585300, icaoAddress=IcaoAddress[string=4BCDE9], altitude=10972.800000000001, parity=1, x=0.7251510620117188, y=0.6266403198242188]",
    "AirbornePositionMessage[timeStampNs=2662471400, icaoAddress=IcaoAddress[string=4D029F], altitude=4236.72, parity=0, x=0.7468185424804688, y=0.7338180541992188]",
    "AirbornePositionMessage[timeStampNs=2729261700, icaoAddress=IcaoAddress[string=4B1A00], altitude=2788.92, parity=1, x=0.6782608032226562, y=0.5555496215820312]",
    "AirbornePositionMessage[timeStampNs=2808517000, icaoAddress=IcaoAddress[string=4B17E5], altitude=1897.38, parity=1, x=0.650421142578125, y=0.5418548583984375]",
    "AirbornePositionMessage[timeStampNs=2813102200, icaoAddress=IcaoAddress[string=3C6481], altitude=10370.82, parity=1, x=0.846771240234375, y=0.6130523681640625]",
    "AirbornePositionMessage[timeStampNs=2906194400, icaoAddress=IcaoAddress[string=495299], altitude=10546.08, parity=0, x=0.6859970092773438, y=0.7249679565429688]",
    "AirbornePositionMessage[timeStampNs=2911496600, icaoAddress=IcaoAddress[string=4D0221], altitude=822.96, parity=1, x=0.669097900390625, y=0.5681533813476562]",
    "AirbornePositionMessage[timeStampNs=2912510900, icaoAddress=IcaoAddress[string=39CEAA], altitude=10363.2, parity=1, x=0.4866180419921875, y=0.5942764282226562]",
    "AirbornePositionMessage[timeStampNs=2935911000, icaoAddress=IcaoAddress[string=A4F239], altitude=10363.2, parity=1, x=0.5797882080078125, y=0.35076141357421875]",
    "AirbornePositionMessage[timeStampNs=3150286900, icaoAddress=IcaoAddress[string=4D2228], altitude=10972.800000000001, parity=1, x=0.6250686645507812, y=0.492706298828125]",
    "AirbornePositionMessage[timeStampNs=3179279100, icaoAddress=IcaoAddress[string=4B1A00], altitude=2781.3, parity=1, x=0.6781997680664062, y=0.5554962158203125]",
    "AirbornePositionMessage[timeStampNs=3322516000, icaoAddress=IcaoAddress[string=39CEAA], altitude=10363.2, parity=0, x=0.49875640869140625, y=0.7231597900390625]",
    "AirbornePositionMessage[timeStampNs=3333067700, icaoAddress=IcaoAddress[string=3C6481], altitude=10370.82, parity=0, x=0.8680267333984375, y=0.7422332763671875]",
    "AirbornePositionMessage[timeStampNs=3358467600, icaoAddress=IcaoAddress[string=4B17E1], altitude=4945.38, parity=0, x=0.6279525756835938, y=0.7650375366210938]",
    "AirbornePositionMessage[timeStampNs=3428806700, icaoAddress=IcaoAddress[string=495299], altitude=10553.7, parity=0, x=0.6858596801757812, y=0.724884033203125]",
    "AirbornePositionMessage[timeStampNs=3495274700, icaoAddress=IcaoAddress[string=4D0221], altitude=815.34, parity=0, x=0.685882568359375, y=0.6964950561523438]",
    "AirbornePositionMessage[timeStampNs=3663721900, icaoAddress=IcaoAddress[string=440237], altitude=11277.6, parity=1, x=0.5326690673828125, y=0.5016860961914062]",
    "AirbornePositionMessage[timeStampNs=3682884300, icaoAddress=IcaoAddress[string=4D2228], altitude=10972.800000000001, parity=0, x=0.6408233642578125, y=0.6198043823242188]",
    "AirbornePositionMessage[timeStampNs=3708360900, icaoAddress=IcaoAddress[string=4B2964], altitude=4175.76, parity=1, x=0.7755661010742188, y=0.6391143798828125]",
    "AirbornePositionMessage[timeStampNs=3719244300, icaoAddress=IcaoAddress[string=4B1A00], altitude=2781.3, parity=1, x=0.6781005859375, y=0.5554122924804688]",
    "AirbornePositionMessage[timeStampNs=3732622800, icaoAddress=IcaoAddress[string=4951CE], altitude=6705.6, parity=0, x=0.8519668579101562, y=0.7913360595703125]",
    "AirbornePositionMessage[timeStampNs=3756122600, icaoAddress=IcaoAddress[string=4CA2BF], altitude=5798.820000000001, parity=0, x=0.5659255981445312, y=0.7481460571289062]",
    "AirbornePositionMessage[timeStampNs=3768086500, icaoAddress=IcaoAddress[string=3C6481], altitude=10370.82, parity=0, x=0.8680953979492188, y=0.7423477172851562]",
    "AirbornePositionMessage[timeStampNs=3843907900, icaoAddress=IcaoAddress[string=495299], altitude=10553.7, parity=0, x=0.6857452392578125, y=0.7248077392578125]",
    "AirbornePositionMessage[timeStampNs=3885921800, icaoAddress=IcaoAddress[string=A4F239], altitude=10363.2, parity=0, x=0.5936050415039062, y=0.475494384765625]",
    "AirbornePositionMessage[timeStampNs=4011575200, icaoAddress=IcaoAddress[string=4D0221], altitude=815.34, parity=1, x=0.669219970703125, y=0.5682754516601562]",
    "AirbornePositionMessage[timeStampNs=4220520200, icaoAddress=IcaoAddress[string=01024C], altitude=10980.42, parity=1, x=0.8081130981445312, y=0.555755615234375]",
    "AirbornePositionMessage[timeStampNs=4222114100, icaoAddress=IcaoAddress[string=4D2228], altitude=10972.800000000001, parity=1, x=0.6253280639648438, y=0.49291229248046875]",
    "AirbornePositionMessage[timeStampNs=4222824200, icaoAddress=IcaoAddress[string=4B2964], altitude=4175.76, parity=0, x=0.7950057983398438, y=0.7686233520507812]",
    "AirbornePositionMessage[timeStampNs=4227579400, icaoAddress=IcaoAddress[string=39CEAA], altitude=10363.2, parity=1, x=0.486541748046875, y=0.5947341918945312]",
    "AirbornePositionMessage[timeStampNs=4229259700, icaoAddress=IcaoAddress[string=4B1A00], altitude=2781.3, parity=0, x=0.694976806640625, y=0.6834030151367188]",
    "AirbornePositionMessage[timeStampNs=4368912000, icaoAddress=IcaoAddress[string=495299], altitude=10553.7, parity=1, x=0.6688690185546875, y=0.5959701538085938]",
    "AirbornePositionMessage[timeStampNs=4421279500, icaoAddress=IcaoAddress[string=4BCDE9], altitude=10972.800000000001, parity=1, x=0.7246170043945312, y=0.626922607421875]",
    "AirbornePositionMessage[timeStampNs=4424072900, icaoAddress=IcaoAddress[string=3C6545], altitude=11590.02, parity=1, x=0.858551025390625, y=0.6821670532226562]",
    "AirbornePositionMessage[timeStampNs=4475329800, icaoAddress=IcaoAddress[string=4D0221], altitude=807.72, parity=1, x=0.6692581176757812, y=0.5683212280273438]",
    "AirbornePositionMessage[timeStampNs=4581047700, icaoAddress=IcaoAddress[string=4D029F], altitude=4229.1, parity=1, x=0.7282867431640625, y=0.6046066284179688]",
    "AirbornePositionMessage[timeStampNs=4608801200, icaoAddress=IcaoAddress[string=4B17E5], altitude=1882.14, parity=1, x=0.6502151489257812, y=0.5420761108398438]",
    "AirbornePositionMessage[timeStampNs=4669266500, icaoAddress=IcaoAddress[string=4B1A00], altitude=2773.6800000000003, parity=0, x=0.6949081420898438, y=0.683349609375]",
    "AirbornePositionMessage[timeStampNs=4672806400, icaoAddress=IcaoAddress[string=4951CE], altitude=6705.6, parity=0, x=0.8521652221679688, y=0.79150390625]",
    "AirbornePositionMessage[timeStampNs=4673064100, icaoAddress=IcaoAddress[string=3C6481], altitude=10370.82, parity=1, x=0.847076416015625, y=0.6135635375976562]",
    "AirbornePositionMessage[timeStampNs=4722539900, icaoAddress=IcaoAddress[string=39CEAA], altitude=10363.2, parity=0, x=0.4986724853515625, y=0.7236251831054688]",
    "AirbornePositionMessage[timeStampNs=4730733600, icaoAddress=IcaoAddress[string=4B2964], altitude=4175.76, parity=1, x=0.7756576538085938, y=0.6391830444335938]",
    "AirbornePositionMessage[timeStampNs=4768552300, icaoAddress=IcaoAddress[string=4D2228], altitude=10972.800000000001, parity=1, x=0.625457763671875, y=0.493011474609375]",
    "AirbornePositionMessage[timeStampNs=4825922400, icaoAddress=IcaoAddress[string=A4F239], altitude=10363.2, parity=0, x=0.5932998657226562, y=0.4756317138671875]",
    "AirbornePositionMessage[timeStampNs=5021779700, icaoAddress=IcaoAddress[string=4D029F], altitude=4229.1, parity=0, x=0.7464065551757812, y=0.7334136962890625]",
    "AirbornePositionMessage[timeStampNs=5123076500, icaoAddress=IcaoAddress[string=3C6481], altitude=10370.82, parity=1, x=0.8471450805664062, y=0.613677978515625]",
    "AirbornePositionMessage[timeStampNs=5219108600, icaoAddress=IcaoAddress[string=4D2228], altitude=10972.800000000001, parity=1, x=0.6255645751953125, y=0.49309539794921875]",
    "AirbornePositionMessage[timeStampNs=5250520700, icaoAddress=IcaoAddress[string=01024C], altitude=10980.42, parity=0, x=0.8280258178710938, y=0.68402099609375]",
    "AirbornePositionMessage[timeStampNs=5259942500, icaoAddress=IcaoAddress[string=4B2964], altitude=4183.38, parity=0, x=0.7950973510742188, y=0.7686996459960938]",
    "AirbornePositionMessage[timeStampNs=5277535800, icaoAddress=IcaoAddress[string=39CEAA], altitude=10363.2, parity=1, x=0.48648834228515625, y=0.595062255859375]",
    "AirbornePositionMessage[timeStampNs=5374073500, icaoAddress=IcaoAddress[string=3C6545], altitude=11590.02, parity=0, x=0.880706787109375, y=0.8126373291015625]",
    "AirbornePositionMessage[timeStampNs=5418420800, icaoAddress=IcaoAddress[string=4B17E1], altitude=4914.900000000001, parity=1, x=0.6129989624023438, y=0.6351394653320312]",
    "AirbornePositionMessage[timeStampNs=5444719600, icaoAddress=IcaoAddress[string=4BB279], altitude=6111.240000000001, parity=0, x=0.7886962890625, y=0.6512298583984375]",
    "AirbornePositionMessage[timeStampNs=5472936100, icaoAddress=IcaoAddress[string=4B17E5], altitude=1882.14, parity=0, x=0.6663818359375, y=0.6700210571289062]",
    "AirbornePositionMessage[timeStampNs=5619249800, icaoAddress=IcaoAddress[string=4B1A00], altitude=2773.6800000000003, parity=0, x=0.6947479248046875, y=0.6832199096679688]",
    "AirbornePositionMessage[timeStampNs=5684907400, icaoAddress=IcaoAddress[string=4CA2BF], altitude=5798.820000000001, parity=0, x=0.5658340454101562, y=0.7475662231445312]",
    "AirbornePositionMessage[timeStampNs=5688356200, icaoAddress=IcaoAddress[string=3C6481], altitude=10370.82, parity=1, x=0.8472442626953125, y=0.6138458251953125]",
    "AirbornePositionMessage[timeStampNs=5715422700, icaoAddress=IcaoAddress[string=4B2964], altitude=4183.38, parity=1, x=0.7757492065429688, y=0.6392593383789062]",
    "AirbornePositionMessage[timeStampNs=5736587400, icaoAddress=IcaoAddress[string=495299], altitude=10553.7, parity=0, x=0.6851959228515625, y=0.7244644165039062]",
    "AirbornePositionMessage[timeStampNs=5742208300, icaoAddress=IcaoAddress[string=4D2228], altitude=10972.800000000001, parity=0, x=0.641326904296875, y=0.6202011108398438]",
    "AirbornePositionMessage[timeStampNs=5765929900, icaoAddress=IcaoAddress[string=A4F239], altitude=10363.2, parity=1, x=0.578857421875, y=0.3511810302734375]",
    "AirbornePositionMessage[timeStampNs=5998271000, icaoAddress=IcaoAddress[string=4D029F], altitude=4229.1, parity=0, x=0.7462387084960938, y=0.733245849609375]",
    "AirbornePositionMessage[timeStampNs=6025450100, icaoAddress=IcaoAddress[string=4D0221], altitude=800.1, parity=1, x=0.6694183349609375, y=0.5684738159179688]",
    "AirbornePositionMessage[timeStampNs=6049248700, icaoAddress=IcaoAddress[string=4B1A00], altitude=2766.06, parity=0, x=0.6946792602539062, y=0.68316650390625]",
    "AirbornePositionMessage[timeStampNs=6103072700, icaoAddress=IcaoAddress[string=3C6481], altitude=10363.2, parity=1, x=0.8473281860351562, y=0.6139907836914062]",
    "AirbornePositionMessage[timeStampNs=6242739900, icaoAddress=IcaoAddress[string=4CA2BF], altitude=5798.820000000001, parity=0, x=0.5658187866210938, y=0.747406005859375]",
    "AirbornePositionMessage[timeStampNs=6252823600, icaoAddress=IcaoAddress[string=4B2964], altitude=4191.0, parity=0, x=0.7951812744140625, y=0.7687759399414062]",
    "AirbornePositionMessage[timeStampNs=6310512200, icaoAddress=IcaoAddress[string=01024C], altitude=10980.42, parity=1, x=0.8075408935546875, y=0.55615234375]",
    "AirbornePositionMessage[timeStampNs=6316314200, icaoAddress=IcaoAddress[string=4BCDE9], altitude=10972.800000000001, parity=1, x=0.7240676879882812, y=0.6272125244140625]",
    "AirbornePositionMessage[timeStampNs=6408458600, icaoAddress=IcaoAddress[string=4B17E1], altitude=4907.280000000001, parity=1, x=0.6131744384765625, y=0.6349105834960938]",
    "AirbornePositionMessage[timeStampNs=6432548700, icaoAddress=IcaoAddress[string=39CEAA], altitude=10363.2, parity=1, x=0.486419677734375, y=0.5954513549804688]",
    "AirbornePositionMessage[timeStampNs=6503498300, icaoAddress=IcaoAddress[string=4D0221], altitude=792.48, parity=1, x=0.669464111328125, y=0.5685195922851562]",
    "AirbornePositionMessage[timeStampNs=6540584200, icaoAddress=IcaoAddress[string=4D029F], altitude=4221.4800000000005, parity=1, x=0.7279586791992188, y=0.6042709350585938]",
    "AirbornePositionMessage[timeStampNs=6586640700, icaoAddress=IcaoAddress[string=4B1A1E], altitude=9044.94, parity=0, x=0.7992095947265625, y=0.574066162109375]",
    "AirbornePositionMessage[timeStampNs=6649117100, icaoAddress=IcaoAddress[string=4B17E5], altitude=1874.52, parity=0, x=0.666259765625, y=0.6701812744140625]",
    "AirbornePositionMessage[timeStampNs=6653130500, icaoAddress=IcaoAddress[string=3C6481], altitude=10370.82, parity=0, x=0.8685989379882812, y=0.7431869506835938]",
    "AirbornePositionMessage[timeStampNs=6711750100, icaoAddress=IcaoAddress[string=440237], altitude=11277.6, parity=0, x=0.5451507568359375, y=0.6282424926757812]",
    "AirbornePositionMessage[timeStampNs=6733320500, icaoAddress=IcaoAddress[string=4CA2BF], altitude=5798.820000000001, parity=0, x=0.5657958984375, y=0.7472610473632812]",
    "AirbornePositionMessage[timeStampNs=6739433700, icaoAddress=IcaoAddress[string=4B2964], altitude=4191.0, parity=1, x=0.7758331298828125, y=0.6393280029296875]",
    "AirbornePositionMessage[timeStampNs=6857664800, icaoAddress=IcaoAddress[string=39CEAA], altitude=10363.2, parity=1, x=0.48639678955078125, y=0.5955810546875]",
    "AirbornePositionMessage[timeStampNs=6881606700, icaoAddress=IcaoAddress[string=495299], altitude=10561.32, parity=1, x=0.6682052612304688, y=0.595550537109375]",
    "AirbornePositionMessage[timeStampNs=7013032200, icaoAddress=IcaoAddress[string=4D2228], altitude=10972.800000000001, parity=0, x=0.6416397094726562, y=0.6204452514648438]",
    "AirbornePositionMessage[timeStampNs=7019252900, icaoAddress=IcaoAddress[string=4B1A00], altitude=2766.06, parity=0, x=0.69451904296875, y=0.6830368041992188]",
    "AirbornePositionMessage[timeStampNs=7059959800, icaoAddress=IcaoAddress[string=4D029F], altitude=4221.4800000000005, parity=0, x=0.7460708618164062, y=0.7330703735351562]",
    "AirbornePositionMessage[timeStampNs=7060665700, icaoAddress=IcaoAddress[string=4D0221], altitude=792.48, parity=0, x=0.6862640380859375, y=0.6968612670898438]",
    "AirbornePositionMessage[timeStampNs=7208021100, icaoAddress=IcaoAddress[string=4B2964], altitude=4191.0, parity=0, x=0.7952728271484375, y=0.7688446044921875]",
    "AirbornePositionMessage[timeStampNs=7228828000, icaoAddress=IcaoAddress[string=4BCDE9], altitude=10972.800000000001, parity=1, x=0.7238082885742188, y=0.627349853515625]",
    "AirbornePositionMessage[timeStampNs=7440752300, icaoAddress=IcaoAddress[string=4D2228], altitude=10972.800000000001, parity=1, x=0.6260910034179688, y=0.4935150146484375]",
    "AirbornePositionMessage[timeStampNs=7463177400, icaoAddress=IcaoAddress[string=4D0221], altitude=784.86, parity=0, x=0.6863021850585938, y=0.6968994140625]",
    };


    private static List<RawMessage> getListRawMessage(List<RawMessageData> rawMessages){
        List<RawMessage> result = new ArrayList<>();
        for (var message : EXPECTED_RAW_MESSAGE_DATA) {
            var messageBytes = HexFormat.of().parseHex(message.bytes());
            var rawMessage = RawMessage.of(message.timeStampNs(), messageBytes);
            result.add(rawMessage);
        }
        return result;
    }
    private static final List<RawMessage>  LIST_RAW_MESSAGE = getListRawMessage(EXPECTED_RAW_MESSAGE_DATA);
    @Test
    void x_trivial() {
        double[] expected = {0.6867904663085938, 0.702667236328125, 0.6243515014648438, 0.747222900390625, 0.8674850463867188};
        for (int i = 0; i < expected.length; i++) {
            AirbornePositionMessage airbornePositionMessage = AirbornePositionMessage.of(LIST_RAW_MESSAGE.get(i));
            assertEquals(expected[i], airbornePositionMessage.x());
        }
    }
    @Test
    void y_trivial() {
        double[] expected = {0.7254638671875, 0.7131423950195312, 0.4921417236328125, 0.7342300415039062, 0.7413406372070312};
        for (int i = 0; i < expected.length; i++) {
            AirbornePositionMessage airbornePositionMessage = AirbornePositionMessage.of(LIST_RAW_MESSAGE.get(i));
            assertEquals(expected[i], airbornePositionMessage.y());
        }
    }
    @Test
    void altitude_trivial() {
        double[] expected = {10546.08, 1303.02, 10972.800000000001, 4244.34, 10370.82};
        for (int i = 0; i < expected.length; i++) {
            AirbornePositionMessage airbornePositionMessage = AirbornePositionMessage.of(LIST_RAW_MESSAGE.get(i));
            assertEquals(expected[i], airbornePositionMessage.altitude());
        }
    }
    @Test
    void altitudeQ(){
        double[] expected = {3474.72 , 7315.20};
        for (int i = 0; i < expected.length; i++) {
            AirbornePositionMessage airbornePositionMessage = AirbornePositionMessage.of(LIST_RAW_MESSAGE.get(i+5));
            assertEquals(expected[i], airbornePositionMessage.altitude(), 0.01);
        }
    }

    @Test
    void parity_trivial() {
        double[] expected = {0, 0, 1, 0, 0};
        for (int i = 0; i < expected.length; i++) {
            AirbornePositionMessage airbornePositionMessage = AirbornePositionMessage.of(LIST_RAW_MESSAGE.get(i));
            assertEquals(expected[i], airbornePositionMessage.parity());
        }
    }

    @Test
    void icao_trivial() {
        String[] expectedX = {"495299", "4241A9", "4D2228", "4D029F", "3C6481"};
        for (int i = 0; i < expectedX.length; i++) {
            AirbornePositionMessage airbornePositionMessage = AirbornePositionMessage.of(LIST_RAW_MESSAGE.get(i));
            assertEquals(expectedX[i], airbornePositionMessage.icaoAddress().string());
        }
    }

    @Test
    void altitudeErrorTimeStamp(){
        assertDoesNotThrow(() ->  AirbornePositionMessage.of(RawMessage.of(0, HexFormat.of().parseHex("8D49529958B302E6E15FA352306B"))));
        assertThrows(IllegalArgumentException.class, () -> AirbornePositionMessage.of(RawMessage.of(-1, HexFormat.of().parseHex("8D49529958B302E6E15FA352306B"))));
    }

    @Test
    void altitudeInvalidAltitude(){
        int[] values = {0b111111101010,0b111111111111, 0b111111111011};
        for (int v:
             values) {
            int res = AirbornePositionMessage.getValueWeirdAlgoAltitude(v);
            assertEquals(-2000, res);
        }
    }

    @Test
    void of() throws IOException{
        String f = "resources/samples_20230304_1442.bin";

        try (InputStream s = new FileInputStream(f)) {
            AdsbDemodulator d = new AdsbDemodulator(s);
            RawMessage m;
            AirbornePositionMessage a;
            int i = 0;

            while ((m = d.nextMessage()) != null){

                if ((a = AirbornePositionMessage.of(m)) != null){
                    //alors le test ne marche pas mais jsp si c'est à cause de comment ils mettent les textes pour le test
                    assertEquals(a.toString(),ExpectedAircraftPositionMessages[i]);
                    i++;
                }
            }
        }
    }
}