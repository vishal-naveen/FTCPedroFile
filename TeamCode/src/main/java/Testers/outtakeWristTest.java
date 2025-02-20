package Testers;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import Positions.positions_motor;
import Subsystem.OuttakeWristSubsystem;

@TeleOp(name="outtakeWristTest")
public class outtakeWristTest extends OpMode {

    private Servo testServo = null;
    private Servo armServo = null;
    private DcMotor viperMotor = null;
    private Servo OuttakeArm = null;

    private Servo OuttakeArmLeft = null;
    private Servo OuttakeArmRight = null;
    private Servo OuttakeWrist = null;
    private Servo OuttakeClaw = null;
    private Servo OuttakeWristPivot = null;
    private double INCREMENT = 0.05;   // Default increment value

    // For touchpad edge detection to cycle the increment value
    private boolean previousTouchpad = false;

    @Override
    public void init() {
        testServo = hardwareMap.get(Servo.class, "OuttakeWrist");
        OuttakeClaw = hardwareMap.get(Servo.class, "OuttakeClaw");
        OuttakeWrist = hardwareMap.get(Servo.class, "OuttakeWrist");
        OuttakeArmLeft = hardwareMap.get(Servo.class, "leftArmServo");
        OuttakeArmRight = hardwareMap.get(Servo.class, "rightArmServo");
        OuttakeWristPivot = hardwareMap.get(Servo.class, "OuttakeWristPivot");
//
        viperMotor = hardwareMap.get(DcMotor.class, "viper1motor");
        viperMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        viperMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

//        OuttakeArm.setPosition(positions_motor.OuttakeArmPickUpSpecimen);
        OuttakeArmLeft.setPosition(0);
        OuttakeArmRight.setPosition(1);
        OuttakeWrist.setPosition(0);
        OuttakeWristPivot.setPosition(0);

    }

    @Override
    public void loop() {

        viperMotor.setTargetPosition(200);
        viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        viperMotor.setPower(0.5);
        // Gamepad 1 controls
//        if(gamepad1.b) {
//            OuttakeArm.setPosition(positions_motor.OuttakeArmNewHighBar);
//            OuttakeArmRight.setPosition(positions_motor.OuttakeArmNewHighBar);
//            OuttakeWrist.setPosition(positions_motor.OuttakeWristNewHighBar);
//            OuttakeWristPivot.setPosition(positions_motor.OuttakeWristPivotHighBar);
//        }
//
//        if(gamepad1.a) {
//            OuttakeArm.setPosition(positions_motor.OuttakeArmPickUpSpecimen);
//            OuttakeArmRight.setPosition(positions_motor.OuttakeArmPickUpSpecimen);
//            OuttakeWrist.setPosition(positions_motor.OuttakeWristPickUpSpecimen);
//            OuttakeWristPivot.setPosition(positions_motor.OuttakeWristPivotSpecimenPickUp);
//        }
//
//        if(gamepad1.x) {
//            OuttakeWrist.setPosition(positions_motor.OuttakeWristNewHighBarFLICK);
//            OuttakeArm.setPosition(positions_motor.OuttakeArmNewHighBarFLICK);
//            OuttakeArmRight.setPosition(positions_motor.OuttakeArmNewHighBarFLICK);
//        }


        if (gamepad2.touchpad && !previousTouchpad) {
            if (INCREMENT == 0.05) {
                INCREMENT = 0.1;
            } else if (INCREMENT == 0.1) {
                INCREMENT = 0.0025;
            } else {
                INCREMENT = 0.05;
            }
        }
        previousTouchpad = gamepad2.touchpad;

        if (gamepad2.left_bumper) {
            double newPos = testServo.getPosition() + INCREMENT;
            testServo.setPosition(newPos);
            OuttakeArmLeft.setPosition(0);
            OuttakeArmRight.setPosition(1);
        }
        // Right bumper: Decrease servo position by INCREMENT
        if (gamepad2.right_bumper) {
            double newPos = testServo.getPosition() - INCREMENT;
            testServo.setPosition(newPos);
            OuttakeArmLeft.setPosition(0);
            OuttakeArmRight.setPosition(1);
        }

        // Gamepad 2 controls
        if (gamepad2.dpad_down) {
            testServo.setPosition(0);
        }
        if (gamepad2.dpad_left) {
            testServo.setPosition(0.1);
        }
        if (gamepad2.dpad_up) {
            testServo.setPosition(0.15);
        }
        if (gamepad2.dpad_right) {
            testServo.setPosition(0.2);
        }
        if(gamepad2.a) {
            testServo.setPosition(0.4);
        }
        if(gamepad2.b) {
            testServo.setPosition(0.5);
        }
        if(gamepad2.x) {
            testServo.setPosition(0.6);
        }
        if(gamepad2.y) {
            testServo.setPosition(0.7);
        }
        if(gamepad2.left_trigger>0.25) {
            OuttakeClaw.setPosition(0);
        }
        if(gamepad2.right_trigger>0.25) {
            OuttakeClaw.setPosition(0.7);
        }
        if(gamepad2.start) {
            testServo.setPosition(1);
        }

        telemetry.addData("Test Servo Position:", testServo.getPosition());
        telemetry.addData("OuttakeArmLeft Position:", OuttakeArmLeft.getPosition());
        telemetry.addData("OuttakeArmRight Position:", OuttakeArmRight.getPosition());
        telemetry.update();
    }
}