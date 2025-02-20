// OuttakeSubsystem.java
package TeleOp.Organized.Subsystems;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import Positions.positions_motor;

public class OuttakeSubsystem {
    private final Servo arm;
    private final Servo wrist;
    private final Servo wristPivot;
    private final Servo claw;

    private boolean isHorizontal = false;
    private boolean lastRightBumper = false;
    private int pickupState = 0;
    private ElapsedTime pickupTimer = new ElapsedTime();
    private boolean pickupInProgress = false;
    private boolean lastB = false;

    public OuttakeSubsystem(HardwareMap hardwareMap) {
        arm = hardwareMap.get(Servo.class, "OuttakeArm");
        wrist = hardwareMap.get(Servo.class, "OuttakeWrist");
        wristPivot = hardwareMap.get(Servo.class, "OuttakeWristPivot");
        claw = hardwareMap.get(Servo.class, "OuttakeClaw");
    }

    public void handleControls(Gamepad gamepad) {
        // Wrist pivot control
        if (gamepad.right_bumper && !lastRightBumper) {
            isHorizontal = !isHorizontal;
            wristPivot.setPosition(isHorizontal ?
                    positions_motor.OuttakeWristPivotHighBar :
                    positions_motor.OuttakeWristPivotVertical);
        }
        lastRightBumper = gamepad.right_bumper;

        // Pickup sequence
        if (gamepad.b && !lastB && !pickupInProgress) {
            pickupState = 0;
            pickupTimer.reset();
            pickupInProgress = true;
        }
        lastB = gamepad.b;

        if (pickupInProgress) {
            switch (pickupState) {
                case 0:
                    arm.setPosition(positions_motor.OuttakeArmNewHighBar);
                    if (pickupTimer.milliseconds() > 250) {
                        pickupState = 1;
                        pickupTimer.reset();
                    }
                    break;
                case 1:
                    wristPivot.setPosition(positions_motor.OuttakeWristPivotHighBar);
                    wrist.setPosition(positions_motor.OuttakeWristNewHighBar);
                    if (pickupTimer.milliseconds() > 10) {
                        pickupInProgress = false;
                    }
                    break;
            }
        }

        // Preset positions
        if (gamepad.a) {
            arm.setPosition(positions_motor.OuttakeArmPickUpSpecimen);
            wrist.setPosition(positions_motor.OuttakeWristPickUpSpecimen);
            wristPivot.setPosition(positions_motor.OuttakeWristPivotSpecimenPickUp);
        }
        if (gamepad.x) {
            arm.setPosition(positions_motor.OuttakeArmNewHighBarFLICK);
            wrist.setPosition(positions_motor.OuttakeWristNewHighBarFLICK);
        }
        if (gamepad.dpad_down) {
            arm.setPosition(positions_motor.OuttakeArmNewTransferWAIT);
            wrist.setPosition(positions_motor.OuttakeWristPickUpSpecimen);
            wristPivot.setPosition(positions_motor.OuttakeWristPivotSpecimenPickUp);
        }
        if (gamepad.y) {
            arm.setPosition(positions_motor.OuttakeArmNewTransferWAIT);
            wrist.setPosition(positions_motor.OuttakeWristPickUpSpecimen);
            wristPivot.setPosition(positions_motor.OuttakeWristPivotHighBar);
        }
        if (gamepad.back) {
            arm.setPosition(positions_motor.OuttakeArmBucket);
            wrist.setPosition(positions_motor.OuttakeWristBucket);
            wristPivot.setPosition(positions_motor.OuttakeWristPivotHighBar);
        }

        // Claw control
        if (gamepad.left_trigger > 0.25) {
            claw.setPosition(positions_motor.OuttakeClawOpen);
        }
        if (gamepad.right_trigger > 0.25) {
            claw.setPosition(positions_motor.OuttakeClawClose);
        }
    }

    public Servo getArm() { return arm; }
    public Servo getWrist() { return wrist; }
    public Servo getWristPivot() { return wristPivot; }
    public Servo getClaw() { return claw; }
}