package Testers;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;


@TeleOp(name="Testers.encoderTestMotor")
public class encoderTestMotor extends OpMode {

    private DcMotor viperMotor = null;

    @Override
    public void init() {
        viperMotor = hardwareMap.get(DcMotor.class,"viper1motor");
        viperMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        viperMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        viperMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    @Override
    public void loop() {
        // Basic positions
        if(gamepad2.a) {
            viperMotor.setTargetPosition(0);
            viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            viperMotor.setPower(1);
        }
        if(gamepad2.b) {
            viperMotor.setTargetPosition(250);
            viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            viperMotor.setPower(1);
        }
        if(gamepad2.x) {
            viperMotor.setTargetPosition(500);
            viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            viperMotor.setPower(1);
        }
        if(gamepad2.y) {
            viperMotor.setTargetPosition(750);
            viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            viperMotor.setPower(1);
        }

        // D-pad positions
        if(gamepad2.dpad_up) {
            viperMotor.setTargetPosition(1000);
            viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            viperMotor.setPower(1);
        }
        if(gamepad2.dpad_right) {
            viperMotor.setTargetPosition(1250);
            viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            viperMotor.setPower(1);
        }
        if(gamepad2.dpad_down) {
            viperMotor.setTargetPosition(1500);
            viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            viperMotor.setPower(1);
        }
        if(gamepad2.dpad_left) {
            viperMotor.setTargetPosition(1750);
            viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            viperMotor.setPower(1);
        }

        // Bumper and trigger positions
        if(gamepad2.left_bumper) {
            viperMotor.setTargetPosition(2000);
            viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            viperMotor.setPower(1);
        }
        if(gamepad2.right_bumper) {
            viperMotor.setTargetPosition(2250);
            viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            viperMotor.setPower(1);
        }
        if(gamepad2.left_trigger > 0.5) {
            viperMotor.setTargetPosition(2500);
            viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            viperMotor.setPower(1);
        }
        if(gamepad2.right_trigger > 0.5) {
            viperMotor.setTargetPosition(2750);
            viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            viperMotor.setPower(1);
        }

        // Stick button positions
        if(gamepad2.left_stick_button) {
            viperMotor.setTargetPosition(3000);
            viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            viperMotor.setPower(1);
        }
        if(gamepad2.right_stick_button) {
            viperMotor.setTargetPosition(3250);
            viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            viperMotor.setPower(1);
        }

        // Start/Back/Guide positions
        if(gamepad2.start) {
            viperMotor.setTargetPosition(3500);
            viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            viperMotor.setPower(1);
        }
        if(gamepad2.back) {
            viperMotor.setTargetPosition(3750);
            viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            viperMotor.setPower(1);
        }
        if(gamepad2.guide) {
            viperMotor.setTargetPosition(4000);
            viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            viperMotor.setPower(1);
        }

        viperMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        telemetry.addData("Viper Current Pos", viperMotor.getCurrentPosition());
        telemetry.addData("Viper Target Pos", viperMotor.getTargetPosition());
        telemetry.addData("Viper Power", viperMotor.getPower());
        telemetry.update();
    }
}
