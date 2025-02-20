//package TeleOp;
//
//import com.qualcomm.robotcore.R;
//import com.qualcomm.robotcore.hardware.AnalogInputController;
//import com.qualcomm.robotcore.hardware.HardwareDevice;
//import com.qualcomm.robotcore.hardware.configuration.annotations.AnalogSensorType;
//import com.qualcomm.robotcore.hardware.configuration.annotations.DeviceProperties;
//import org.firstinspires.ftc.robotcore.internal.system.AppUtil;
//
///**
// * Control a single analog device.
// */
//@AnalogSensorType
//@DeviceProperties(name = "@string/configTypeAnalogInput", xmlTag = "AnalogInputMine", builtIn = true)
//public class AnalogInputMine implements HardwareDevice {
//    private AnalogInputController controller = null;
//    private int channel = -1;
//
//    /**
//     * Constructor used by the hardware mapping system.
//     * In a real configuration, the SDK will supply a valid controller and channel.
//     */
//    public AnalogInputMine(AnalogInputController controller, int channel) {
//        this.controller = controller;
//        this.channel = channel;
//    }
//
//    /**
//     * Default no-argument constructor required for hardwareMap instantiation.
//     * If not properly initialized, methods will return default values.
//     */
//    public AnalogInputMine() {
//        // No initialization here.
//    }
//
//    @Override
//    public Manufacturer getManufacturer() {
//        return controller != null ? controller.getManufacturer() : Manufacturer.Unknown;
//    }
//
//    /**
//     * Returns the current voltage of this input (in volts).
//     */
//    public double getVoltage() {
//        return controller != null ? controller.getAnalogInputVoltage(channel) : 0.0;
//    }
//
//    /**
//     * Returns the maximum voltage this input can read.
//     */
//    public double getMaxVoltage() {
//        return controller != null ? controller.getMaxAnalogInputVoltage() : 5.0;
//    }
//
//    @Override
//    public String getDeviceName() {
//        return AppUtil.getDefContext().getString(R.string.configTypeAnalogInput);
//    }
//
//    @Override
//    public String getConnectionInfo() {
//        return controller != null ? (controller.getConnectionInfo() + "; analog port " + channel) : "Not Connected";
//    }
//
//    @Override
//    public int getVersion() {
//        return 1;
//    }
//
//    @Override
//    public void resetDeviceConfigurationForOpMode() {
//        // No action needed.
//    }
//
//    @Override
//    public void close() {
//        // No resources to release.
//    }
//}
