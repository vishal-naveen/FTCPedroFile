// IntakeSubsystem.java
package TeleOp.Organized.Subsystems;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import Positions.positions_motor;

public class IntakeSubsystem {
    private final Servo arm;
    private final Servo wrist;
    private final Servo wristPivot;
    private final Servo claw;

    private boolean isWristHorizontal = false;
    private boolean lastLeftBumper = false;
    private boolean lastDpadLeft = false;
    private boolean lastDpadRight = false;

    public IntakeSubsystem(HardwareMap hardwareMap) {
        arm = hardwareMap.get(Servo.class, "NintakeArm");
        wrist = hardwareMap.get(Servo.class, "NintakeWrist");
        wristPivot = hardwareMap.get(Servo.class, "NintakeWristPivot");
        claw = hardwareMap.get(Servo.class, "NintakeClaw");
    }

    public void handleControls(Gamepad gamepad1, Gamepad gamepad2) {
        // Wrist pivot control
        if (gamepad2.left_bumper && !lastLeftBumper) {
            isWristHorizontal = !isWristHorizontal;
            wristPivot.setPosition(isWristHorizontal ?
                    positions_motor.NIntakeWristPivotHorizontal :
                    positions_motor.NIntakeWristPivotVertical);
        }
        lastLeftBumper = gamepad2.left_bumper;

        // Claw control
        if (gamepad2.dpad_left && !lastDpadLeft) {
            claw.setPosition(positions_motor.NIntakeClawOpen);
        }
        if (gamepad2.dpad_right && !lastDpadRight) {
            claw.setPosition(positions_motor.NIntakeClawClose);
        }
        lastDpadLeft = gamepad2.dpad_left;
        lastDpadRight = gamepad2.dpad_right;

        // Arm positions
        if (gamepad1.dpad_up) {
            arm.setPosition(positions_motor.NIntakeArmSpecimenPickUp);
            wristPivot.setPosition(positions_motor.NIntakeWristPivotTransfer);
        }
        if (gamepad1.dpad_down) {
            arm.setPosition(positions_motor.NIntakeArmExtendedBack);
        }
        if (gamepad2.left_stick_y > 0.5) {
            arm.setPosition(positions_motor.NIntakeArmTransfer);
        }
        if (gamepad2.left_stick_y < -0.5) {
            arm.setPosition(positions_motor.NIntakeArmExtendedFull);
            wrist.setPosition(positions_motor.NIntakeWristPickUpBefore);
        }

        // Wrist positions
        if (gamepad2.right_stick_y > 0.5) {
            wrist.setPosition(positions_motor.NIntakeWristPickUp);
        }
        if (gamepad2.right_stick_y < -0.5) {
            wrist.setPosition(positions_motor.NIntakeWristTransfer);
        }
        if (gamepad2.dpad_up) {
            wrist.setPosition(positions_motor.NIntakeWristPickUpBefore);
        }
    }

    public Servo getArm() { return arm; }
    public Servo getWrist() { return wrist; }
    public Servo getWristPivot() { return wristPivot; }
    public Servo getClaw() { return claw; }
}