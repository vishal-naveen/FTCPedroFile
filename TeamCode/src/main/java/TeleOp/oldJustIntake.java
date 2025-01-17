package TeleOp;

import static Positions.Commands.sleep;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import Positions.positions_motor;

@TeleOp(name="TeleOp.oldJustIntake")
public class oldJustIntake extends OpMode {

    private DcMotor frontLeft = null;
    private DcMotor frontRight = null;
    private DcMotor backLeft = null;
    private DcMotor backRight = null;

    private DcMotor viperMotor = null;

    private Servo NintakeArm = null;
    private Servo NintakeWrist = null;
    private Servo NintakeWristPivot = null;
    private Servo NintakeClaw = null;

    double power = 0.9;

    boolean isOutakeHorizontal = false;
    boolean lastRightBumper = false;

    boolean isWristHorizontal = false;
    boolean lastLeftBumper = false;

    private Servo OuttakeArm = null;
    private Servo OuttakeWrist = null;
    private Servo OuttakeWristPivot = null;
    private Servo OuttakeClaw = null;

    private ElapsedTime flickTimer = new ElapsedTime();
    private boolean isFlicking = false;
    private int flickState = 0;


    @Override
    public void init() {
        frontLeft = hardwareMap.get(DcMotor.class, "FL");
        frontRight = hardwareMap.get(DcMotor.class, "FR");
        backLeft = hardwareMap.get(DcMotor.class, "BL");
        backRight = hardwareMap.get(DcMotor.class, "BR");




        frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);

        viperMotor = hardwareMap.get(DcMotor.class, "viper1motor");
        viperMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        viperMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        viperMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        NintakeArm = hardwareMap.get(Servo.class, "NintakeArm");
        NintakeWrist = hardwareMap.get(Servo.class, "NintakeWrist");
        NintakeWristPivot = hardwareMap.get(Servo.class, "NintakeWristPivot");
        NintakeClaw = hardwareMap.get(Servo.class, "NintakeClaw");

        OuttakeArm = hardwareMap.get(Servo.class, "OuttakeArm");
        OuttakeWrist = hardwareMap.get(Servo.class, "OuttakeWrist");
        OuttakeWristPivot = hardwareMap.get(Servo.class, "OuttakeWristPivot");
        OuttakeClaw = hardwareMap.get(Servo.class, "OuttakeClaw");

        flickTimer = new ElapsedTime();



    }

    @Override
    public void loop() {
        // Intake Controls
        if (gamepad2.left_bumper && !lastLeftBumper) {
            isWristHorizontal = !isWristHorizontal;
            NintakeWristPivot.setPosition(isWristHorizontal ?
                    positions_motor.NIntakeWristPivotHorizontal :
                    positions_motor.NIntakeWristPivotVertical);
        }
        lastLeftBumper = gamepad2.left_bumper;

        if (gamepad2.right_bumper && !lastRightBumper) {
            isOutakeHorizontal = !isOutakeHorizontal;
            OuttakeWristPivot.setPosition(isOutakeHorizontal ?
                    positions_motor.OuttakeWristPivotHighBar:
                    positions_motor.OuttakeWristPivotVertical);
        }
        lastRightBumper = gamepad2.right_bumper;

        if(gamepad2.dpad_left)
        {
            NintakeClaw.setPosition(positions_motor.NIntakeClawClose);
        }

        if(gamepad2.dpad_right)
        {
            NintakeClaw.setPosition(positions_motor.NIntakeClawOpen);
        }

        if(gamepad2.left_stick_y > 0.25){
            NintakeArm.setPosition(positions_motor.NIntakeArmExtendedBack);
            NintakeWrist.setPosition(positions_motor.NIntakeWristPickUpBefore);
        }
        if(gamepad2.left_stick_y < -0.25){
            NintakeArm.setPosition(positions_motor.NIntakeArmExtendedFull);
            NintakeWrist.setPosition(positions_motor.NIntakeWristPickUpBefore);
        }
        if(gamepad2.b){
//            viperMotor.setTargetPosition(positions_motor.VIPER_HIGHBAR);
//            viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//            viperMotor.setPower(1);
            OuttakeArm.setPosition(positions_motor.OuttakeArmNewHighBar);
            OuttakeWrist.setPosition(positions_motor.OuttakeWristNewHighBar);
            OuttakeWristPivot.setPosition(positions_motor.OuttakeWristPivotHighBar);
        }
//        if(gamepad2.left_stick_x > 0.25){
//            OuttakeArm.setPosition(Positions.positions_motor.OuttakeArmTransfer);
//            OuttakeWrist.setPosition(Positions.positions_motor.OuttakeWristTransfer);
//            OuttakeWristPivot.setPosition(Positions.positions_motor.OuttakeWristPivotHorizontal);
//        }

        if(gamepad2.a){
//            viperMotor.setTargetPosition(positions_motor.VIPER_GROUND);
//            viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//            viperMotor.setPower(1);
            OuttakeArm.setPosition(positions_motor.OuttakeArmPickUpSpecimen);
            OuttakeWrist.setPosition(positions_motor.OuttakeWristPickUpSpecimen);
            OuttakeWristPivot.setPosition(positions_motor.OuttakeWristPivotSpecimenPickUp);
        }

        if(gamepad2.x){
            OuttakeWrist.setPosition(positions_motor.OuttakeWristNewHighBarFLICK);
            OuttakeArm.setPosition(positions_motor.OuttakeArmNewHighBarFLICK);
        }









        if(gamepad1.b){
            viperMotor.setTargetPosition(positions_motor.VIPER_HIGHBAR);
            viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            viperMotor.setPower(1);
            OuttakeArm.setPosition(positions_motor.OuttakeArmHighBar);
            OuttakeWrist.setPosition(positions_motor.OuttakeWristHighBar);
            OuttakeWristPivot.setPosition(positions_motor.OuttakeWristPivotHighBar);
        }

        if(gamepad1.a){
            viperMotor.setTargetPosition(positions_motor.VIPER_GROUND);
            viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            viperMotor.setPower(1);
            OuttakeArm.setPosition(positions_motor.OuttakeArmPickUpSpecimen);
            OuttakeWrist.setPosition(positions_motor.OuttakeWristPickUpSpecimen);
            OuttakeWristPivot.setPosition(positions_motor.OuttakeWristPivotSpecimenPickUp);
        }

        if(gamepad1.x && !isFlicking) {
            isFlicking = true;
            flickState = 0;
            flickTimer.reset();
        }

        if(isFlicking) {
            switch(flickState) {
                case 0:
                    OuttakeWrist.setPosition(positions_motor.OuttakeWristNewHighBarFLICK);
                    OuttakeArm.setPosition(positions_motor.OuttakeArmNewHighBarFLICK);
                    if(flickTimer.milliseconds() >= 1000) {
                        flickState = 1;
                        flickTimer.reset();
                    }
                    break;
                case 1:
                    viperMotor.setTargetPosition(100);
                    viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    viperMotor.setPower(1);
                    if(flickTimer.milliseconds() >= 1000) {
                        flickState = 2;
                        flickTimer.reset();
                    }
                    break;
                case 2:
                    OuttakeClaw.setPosition(positions_motor.OuttakeClawOpen);
                    isFlicking = false;
                    break;
            }
        }

        if(gamepad2.left_trigger>0.25)
        {
            OuttakeClaw.setPosition(positions_motor.OuttakeClawOpen);
        }
        if(gamepad2.right_trigger>0.25)
        {
            OuttakeClaw.setPosition(positions_motor.OuttakeClawClose);
        }



        if(gamepad2.right_stick_y > 0.25){
            NintakeWrist.setPosition(positions_motor.NIntakeWristPickUp);
        }
        if(gamepad2.right_stick_y < -0.25){
            NintakeWrist.setPosition(positions_motor.NIntakeWristTransfer);
        }

        if(gamepad2.back){
            NintakeWrist.setPosition(positions_motor.NIntakeWristPickUpBefore);
        }

        // Drivetrain Controls
        if(gamepad1.left_bumper) {
            power = 0.7;
        }
        if(gamepad1.dpad_up) {
            power = 1;
        }
        if(gamepad1.dpad_down) {
            power = 0.6;
        }

        double y = -gamepad1.left_stick_y;
        double x = gamepad1.left_stick_x * 1.1;
        double rx = gamepad1.right_stick_x;

        double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
        double frontLeftPower = power * ((y + x + rx) / denominator);
        double backLeftPower = power * ((y - x + rx) / denominator);
        double frontRightPower = power * ((y - x - rx) / denominator);
        double backRightPower = power * ((y + x - rx) / denominator);

        frontLeft.setPower(frontLeftPower);
        backLeft.setPower(backLeftPower);
        frontRight.setPower(frontRightPower);
        backRight.setPower(backRightPower);

        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        telemetry.addData("FL Power", frontLeftPower);
        telemetry.addData("FR Power", frontRightPower);
        telemetry.addData("BL Power", backLeftPower);
        telemetry.addData("BR Power", backRightPower);

        telemetry.addData("NintakeArm", NintakeArm.getPosition());
        telemetry.addData("NintakeWrist", NintakeWrist.getPosition());
        telemetry.addData("NintakeWristPivot", NintakeWristPivot.getPosition());
        telemetry.addData("NintakeClaw", NintakeClaw.getPosition());
        telemetry.addData("OuttakeArm", OuttakeArm.getPosition());
        telemetry.addData("OuttakeWrist", OuttakeWrist.getPosition());
        telemetry.addData("OuttakeWristPivot", OuttakeWristPivot.getPosition());
        telemetry.addData("OuttakeClaw", OuttakeClaw.getPosition());
        telemetry.update();
    }
}