package Testers;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import Positions.positions_motor;
import Subsystem.OuttakeWristSubsystem;

@TeleOp(name="intakeDoubkeArmTester.ServoTester")
public class intakeDoubkeArmTester extends OpMode {

    private Servo lefttestServo = null;
    private Servo righttestServo = null;
    private Servo armServo = null;
    private DcMotor viperMotor = null;
    private Servo OuttakeArm = null;
    private Servo OuttakeArmRight = null;
    private Servo OuttakeWrist = null;
    private Servo OuttakeWristPivot = null;

    @Override
    public void init() {
        lefttestServo = hardwareMap.get(Servo.class, "IntakeArmLeft");
        righttestServo = hardwareMap.get(Servo.class, "IntakeArmRight");

//        OuttakeWrist = hardwareMap.get(Servo.class, "OuttakeWrist");
//        OuttakeArmRight = hardwareMap.get(Servo.class, "OuttakeArmRight");
//        OuttakeWristPivot = hardwareMap.get(Servo.class, "OuttakeWristPivot");
//
//        viperMotor = hardwareMap.get(DcMotor.class, "viper1motor");
//        viperMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//        viperMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

//        OuttakeArm.setPosition(positions_motor.OuttakeArmPickUpSpecimen);

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
            lefttestServo.setPosition(0.9);
            righttestServo.setPosition(0.45);
        }
        if (gamepad2.dpad_left) {
            lefttestServo.setPosition(0.4);
            righttestServo.setPosition(0.95);
        }

        telemetry.addData("Left Test Servo Position:", lefttestServo.getPosition());
        telemetry.addData("Right Test Servo Position:", righttestServo.getPosition());

//        telemetry.addData("OuttakeArm Position:", OuttakeArm.getPosition());
//        telemetry.addData("OuttakeArmRight Position:", OuttakeArmRight.getPosition());
//        telemetry.addData("OuttakeWrist Position:", OuttakeWrist.getPosition());
//        telemetry.addData("OuttakeWristPivot Position:", OuttakeWristPivot.getPosition());
        telemetry.update();
    }
}