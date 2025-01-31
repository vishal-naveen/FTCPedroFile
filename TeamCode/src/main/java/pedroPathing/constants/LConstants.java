package pedroPathing.constants;

import com.pedropathing.localization.*;
import com.pedropathing.localization.constants.*;

public class LConstants {
    static {
        ThreeWheelConstants.forwardTicksToInches = 0.003;
        ThreeWheelConstants.strafeTicksToInches = 0.003;
        ThreeWheelConstants.turnTicksToInches = 0.003;
        ThreeWheelConstants.leftY = 6.15625;
        ThreeWheelConstants.rightY = -6.15625;
        ThreeWheelConstants.strafeX = -5.40625;
        ThreeWheelConstants.leftEncoder_HardwareMapName = "FL";
        ThreeWheelConstants.rightEncoder_HardwareMapName = "BR";
        ThreeWheelConstants.strafeEncoder_HardwareMapName = "BL";
        ThreeWheelConstants.leftEncoderDirection = Encoder.REVERSE;
        ThreeWheelConstants.rightEncoderDirection = Encoder.FORWARD;
        ThreeWheelConstants.strafeEncoderDirection = Encoder.FORWARD;
    }
}




