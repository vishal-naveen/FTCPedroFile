package Testers;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import Positions.positions_motor;

@TeleOp(name="Testers.ServoTester")
public class ServoTester extends OpMode {

    private  Servo testServo = null;
    private  Servo armServo = null;

    private DcMotor viperMotor = null;


    @Override
    public void init() {
        testServo = hardwareMap.get(Servo.class, "testServo");

        viperMotor = hardwareMap.get(DcMotor.class, "viper1motor");
        viperMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        viperMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);


        viperMotor.setTargetPosition(positions_motor.VIPER_HIGHBAR);
        viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        viperMotor.setPower(1);
    }


    @Override
    public void loop() {



        if (gamepad2.dpad_down) {
            testServo.setPosition(0);
        }
        if (gamepad2.dpad_left) {
            testServo.setPosition(0.1);
        }
        if (gamepad2.dpad_up) {
            testServo.setPosition(0.2);
        }
        if (gamepad2.dpad_right) {
            testServo.setPosition(0.3);
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
        telemetry.update();
    }
}
