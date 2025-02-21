package TeleOp.Subsystem;

import com.arcrobotics.ftclib.command.SubsystemBase;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class ViperSubsystem extends SubsystemBase {
    private final DcMotor viperMotor;
    private static final double HOLDING_POWER = 0.1;
    private static final int POSITION_TOLERANCE = 20;
    private boolean isStallCheckActive = false;
    private int lastPosition = 0;

    public ViperSubsystem(HardwareMap hardwareMap) {
        viperMotor = hardwareMap.get(DcMotor.class, "viper1motor");
        viperMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        viperMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        viperMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    public void setTargetPosition(int position) {
        viperMotor.setTargetPosition(position);
        viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        viperMotor.setPower(1.0);
    }

    public void setPower(double power) {
        viperMotor.setPower(power);
    }

    public int getCurrentPosition() {
        return viperMotor.getCurrentPosition();
    }

    public int getTargetPosition() {
        return viperMotor.getTargetPosition();
    }

    public void resetEncoder() {
        viperMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    @Override
    public void periodic() {
        if (viperMotor.getMode() == DcMotor.RunMode.RUN_TO_POSITION) {
            int currentPos = getCurrentPosition();
            int targetPos = getTargetPosition();

            if (Math.abs(currentPos - targetPos) <= POSITION_TOLERANCE) {
                viperMotor.setPower(HOLDING_POWER);
            }
        }
    }
}