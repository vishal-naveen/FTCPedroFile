package Testers;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import Positions.positions_motor;

@TeleOp(name="transfertesting")
public class transfertesting extends OpMode {

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

    boolean lastRightBumperl = false;
    boolean lastLeftBumperl = false;

    private Servo OuttakeArm = null;
    private Servo OuttakeWrist = null;
    private Servo OuttakeWristPivot = null;
    private Servo OuttakeClaw = null;

    double outtakeArmPosition = 0.4; // Starting position, adjust as needed

    private int transferState = 0;
    private ElapsedTime transferTimer = new ElapsedTime();
    private boolean transferInProgress = false;

    private int lengthWait = 500;


    @Override
    public void init() {
        frontLeft = hardwareMap.get(DcMotor.class, "FL");
        frontRight = hardwareMap.get(DcMotor.class, "FR");
        backLeft = hardwareMap.get(DcMotor.class, "BL");
        backRight = hardwareMap.get(DcMotor.class, "BR");

        viperMotor = hardwareMap.get(DcMotor.class, "viper1motor");

        frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);

        viperMotor.setDirection(DcMotorSimple.Direction.FORWARD);

        NintakeArm = hardwareMap.get(Servo.class, "NintakeArm");
        NintakeWrist = hardwareMap.get(Servo.class, "NintakeWrist");
        NintakeWristPivot = hardwareMap.get(Servo.class, "NintakeWristPivot");
        NintakeClaw = hardwareMap.get(Servo.class, "NintakeClaw");

        OuttakeArm = hardwareMap.get(Servo.class, "OuttakeArm");
        OuttakeWrist = hardwareMap.get(Servo.class, "OuttakeWrist");
        OuttakeWristPivot = hardwareMap.get(Servo.class, "OuttakeWristPivot");
        OuttakeClaw = hardwareMap.get(Servo.class, "OuttakeClaw");

        viperMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        viperMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    @Override
    public void loop() {

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
                    positions_motor.OuttakeWristPivotHorizontal :
                    positions_motor.OuttakeWristPivotVertical);
        }
        lastRightBumper = gamepad2.right_bumper;
        // Intake Controls
        if(gamepad2.dpad_left)
        {
            NintakeClaw.setPosition(positions_motor.NIntakeClawClose);
        }

        if(gamepad2.dpad_right)
        {
            NintakeClaw.setPosition(positions_motor.NIntakeClawOpen);
        }

        if(gamepad1.dpad_left)
        {
            NintakeClaw.setPosition(positions_motor.NIntakeClawClose);
        }

        if(gamepad1.dpad_right)
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
            viperMotor.setTargetPosition(positions_motor.VIPER_GROUND);
            viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            viperMotor.setPower(1);
            OuttakeArm.setPosition(positions_motor.OuttakeArmHighBar);
            OuttakeWrist.setPosition(positions_motor.OuttakeWristHighBar);
            OuttakeWristPivot.setPosition(positions_motor.OuttakeWristPivotHighBar);
        }
//        if(gamepad2.left_stick_x > 0.25){
//            OuttakeArm.setPosition(positions_motor.OuttakeArmTransfer);
//            OuttakeWrist.setPosition(positions_motor.OuttakeWristTransfer);
//            OuttakeWristPivot.setPosition(positions_motor.OuttakeWristPivotHorizontal);
//        }


        if(gamepad2.x){
            OuttakeWrist.setPosition(positions_motor.OuttakeWristHighBarMore);
            OuttakeArm.setPosition(positions_motor.OuttakeArmHighBarFlick);
        }

        if(gamepad2.a){
            viperMotor.setTargetPosition(positions_motor.VIPER_GROUND);
            viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            viperMotor.setPower(1);
            OuttakeArm.setPosition(positions_motor.OuttakeArmPickUpSpecimen);
            OuttakeWrist.setPosition(positions_motor.OuttakeWristPickUpSpecimen);
            OuttakeWristPivot.setPosition(positions_motor.OuttakeWristPivotSpecimenPickUp);
        }

        if(gamepad2.y){
            viperMotor.setTargetPosition(positions_motor.VIPER_HIGHBASKET);
            viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            viperMotor.setPower(1);
            OuttakeArm.setPosition(positions_motor.OuttakeArmBucket);
            OuttakeWrist.setPosition(positions_motor.OuttakeWristBucket);
        }



        if(gamepad2.left_trigger>0.25)
        {
            OuttakeClaw.setPosition(positions_motor.OuttakeClawOpen);
        }
        if(gamepad2.right_trigger>0.25)
        {
            OuttakeClaw.setPosition(positions_motor.OuttakeClawClose);
        }

//        if(gamepad2.start)
//        {
//            NintakeArm.setPosition(positions_motor.NIntakeArmExtendedBack);
//            NintakeWrist.setPosition(positions_motor.NIntakeWristTransfer);
//            NintakeWristPivot.setPosition(positions_motor.NIntakeWristPivotVertical);
//            OuttakeClaw.setPosition(positions_motor.OuttakeClawOpen);
//            OuttakeWrist.setPosition(positions_motor.OuttakeWristTransfer);
//            OuttakeWristPivot.setPosition(positions_motor.OuttakeWristPivotHighBar);
//            wait(500);
//            OuttakeArm.setPosition(positions_motor.OuttakeArmTransfer);
//            wait(500);
//            OuttakeClaw.setPosition(positions_motor.OuttakeClawClose);
//            wait(500);
//            NintakeClaw.setPosition(positions_motor.NIntakeClawOpen);
//            wait(500);
//            OuttakeArm.setPosition(positions_motor.OuttakeArmHighBar);
//        }

        if(gamepad1.y)
        {
            viperMotor.setTargetPosition(positions_motor.VIPER_HIGHBASKET);
            viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            viperMotor.setPower(1);
            OuttakeArm.setPosition(positions_motor.OuttakeArmBucket);
            OuttakeWrist.setPosition(positions_motor.OuttakeWristBucket);
        }


        if(gamepad2.start && !transferInProgress) {
            // Check if OuttakeArm is in highbar or flick position
            boolean isOuttakeInHighPosition = Math.abs(OuttakeArm.getPosition() - positions_motor.OuttakeArmHighBar) < 0.05 ||
                    Math.abs(OuttakeArm.getPosition() - positions_motor.OuttakeArmHighBarFlick) < 0.05;

            // Check if NintakeWrist is not in transfer position
            boolean isNintakeNotInTransfer = Math.abs(NintakeWrist.getPosition() - positions_motor.NIntakeWristTransfer) > 0.05;

            // Set wait time based on conditions
            lengthWait = 500; // base time
            if(isOuttakeInHighPosition) lengthWait += 500;  // Add time for high position
            if(isNintakeNotInTransfer) lengthWait += 500;   // Add time for wrist movement

            transferState = 0;
            transferTimer.reset();
            transferInProgress = true;
        }

        if(gamepad1.start && !transferInProgress) {
            // Check if OuttakeArm is in highbar or flick position
            boolean isOuttakeInHighPosition = Math.abs(OuttakeArm.getPosition() - positions_motor.OuttakeArmHighBar) < 0.05 ||
                    Math.abs(OuttakeArm.getPosition() - positions_motor.OuttakeArmHighBarFlick) < 0.05;

            // Check if NintakeWrist is not in transfer position
            boolean isNintakeNotInTransfer = Math.abs(NintakeWrist.getPosition() - positions_motor.NIntakeWristTransfer) > 0.05;

            // Set wait time based on conditions
            lengthWait = 500; // base time
            if(isOuttakeInHighPosition) lengthWait += 500;  // Add time for high position
            if(isNintakeNotInTransfer) lengthWait += 500;   // Add time for wrist movement

            transferState = 0;
            transferTimer.reset();
            transferInProgress = true;
        }

        if(transferInProgress) {
            switch(transferState) {
                case 0:
                    NintakeArm.setPosition(positions_motor.NIntakeArmExtendedBack);
                    NintakeWrist.setPosition(positions_motor.NIntakeWristTransfer);
                    NintakeWristPivot.setPosition(positions_motor.NIntakeWristPivotVertical);
                    OuttakeClaw.setPosition(positions_motor.OuttakeClawOpen);
                    OuttakeWrist.setPosition(positions_motor.OuttakeWristTransfer);
                    OuttakeWristPivot.setPosition(positions_motor.OuttakeWristPivotHighBar);
                    if(transferTimer.milliseconds() > lengthWait) {
                        transferState = 1;
                        transferTimer.reset();
                    }
                    break;

                case 1:
                    OuttakeArm.setPosition(positions_motor.OuttakeArmTransfer);
                    if(transferTimer.milliseconds() > lengthWait) {
                        transferState = 2;
                        transferTimer.reset();
                    }
                    break;

                case 2:
                    OuttakeClaw.setPosition(positions_motor.OuttakeClawClose);
                    if(transferTimer.milliseconds() > lengthWait) {
                        transferState = 3;
                        transferTimer.reset();
                    }
                    break;

                case 3:
                    NintakeClaw.setPosition(positions_motor.NIntakeClawOpenTransfer);
                    if(transferTimer.milliseconds() > lengthWait) {
                        transferState = 4;
                        transferTimer.reset();
                    }
                    break;

                case 4:
                    OuttakeArm.setPosition(positions_motor.OuttakeArmHighBar);
                    transferInProgress = false;
                    break;
            }
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



        if(gamepad1.left_bumper) {
            OuttakeWrist.setPosition(0.3);
        }

        if(gamepad1.right_bumper) {
            OuttakeWrist.setPosition(0.25);
        }

// Add this to your telemetry
        telemetry.addData("OuttakeArm Position Value", outtakeArmPosition);



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