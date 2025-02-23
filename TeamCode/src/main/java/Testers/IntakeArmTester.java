package Testers;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import Positions.positions_motor;
import Subsystem.OuttakeWristSubsystem;

@TeleOp(name="IntakeArmTester")
public class IntakeArmTester extends OpMode {

    private Servo testServo = null;

    @Override
    public void init() {
        testServo = hardwareMap.get(Servo.class, "NintakeArm");

    }

    @Override
    public void loop() {
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
        if(gamepad2.left_bumper) {
            testServo.setPosition(0.8);
        }
        if(gamepad2.right_bumper) {
            testServo.setPosition(0.9);
        }
        if(gamepad2.start) {
            testServo.setPosition(1);
        }

        telemetry.addData("Test Servo Position:", testServo.getPosition());
//        telemetry.addData("OuttakeArm Position:", OuttakeArm.getPosition());
//        telemetry.addData("OuttakeArmRight Position:", OuttakeArmRight.getPosition());
//        telemetry.addData("OuttakeWrist Position:", OuttakeWrist.getPosition());
//        telemetry.addData("OuttakeWristPivot Position:", OuttakeWristPivot.getPosition());
        telemetry.update();
    }
}