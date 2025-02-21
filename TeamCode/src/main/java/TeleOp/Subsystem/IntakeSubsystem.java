package TeleOp.Subsystem;

import com.arcrobotics.ftclib.command.SubsystemBase;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class IntakeSubsystem extends SubsystemBase {
    private final Servo arm;
    private final Servo wrist;
    private final Servo wristPivot;
    private final Servo claw;

    public IntakeSubsystem(HardwareMap hardwareMap) {
        arm = hardwareMap.get(Servo.class, "NintakeArm");
        wrist = hardwareMap.get(Servo.class, "NintakeWrist");
        wristPivot = hardwareMap.get(Servo.class, "NintakeWristPivot");
        claw = hardwareMap.get(Servo.class, "NintakeClaw");
    }

    public void setArmPosition(double position) {
        arm.setPosition(position);
    }

    public void setWristPosition(double position) {
        wrist.setPosition(position);
    }

    public void setWristPivotPosition(double position) {
        wristPivot.setPosition(position);
    }

    public void setClawPosition(double position) {
        claw.setPosition(position);
    }

    public double getArmPosition() {
        return arm.getPosition();
    }

    public double getWristPosition() {
        return wrist.getPosition();
    }

    public double getWristPivotPosition() {
        return wristPivot.getPosition();
    }

    public double getClawPosition() {
        return claw.getPosition();
    }
}