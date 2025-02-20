package IntakeSubsystem;

import com.arcrobotics.ftclib.command.SubsystemBase;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import Positions.Constants;
import Positions.positions_motor;

public class BucketSideAutoSubsystem extends SubsystemBase {
    private final Servo NintakeArm, NintakeWrist, NintakeWristPivot, NintakeClaw;
    private final Servo OuttakeArm, OuttakeWrist, OuttakeWristPivot, OuttakeClaw;
    private final DcMotorEx viperMotor;
    private final Telemetry telemetry;

    private final ElapsedTime sequenceTimer = new ElapsedTime();
    private boolean sequenceInProgress = false;
    private int sequenceState = 0;

    private boolean transferInProgress = false;
    private int transferState = 0;
    private ElapsedTime transferTimer = new ElapsedTime();
    private boolean isArmExtended = false;
    private boolean isPivotHorizontal = false;

    private static final double VIPER_HOLDING_POWER = 0.1;
    private static final int POSITION_TOLERANCE = 20;
    private boolean isViperAtTarget = false;

    private int viperDownState = 0;
    private ElapsedTime viperDownTimer = new ElapsedTime();
    private boolean viperDownInProgress = false;



    public void startTransfer() {
        transferState = 0;
        transferTimer.reset();
        transferInProgress = true;
        updateArmExtensionState();
        updatePivotState();
    }




    private void updateViperMotorState() {
        if (viperMotor.getMode() == DcMotor.RunMode.RUN_TO_POSITION) {
            int currentPos = viperMotor.getCurrentPosition();
            int targetPos = viperMotor.getTargetPosition();

            if (Math.abs(currentPos - targetPos) <= POSITION_TOLERANCE) {
                isViperAtTarget = true;
                viperMotor.setPower(VIPER_HOLDING_POWER);
            } else {
                isViperAtTarget = false;
                viperMotor.setPower(1);
            }
        }
    }

    private void updateArmExtensionState() {
        // Update based on arm position
        double currentArmPos = NintakeArm.getPosition();
        isArmExtended = Math.abs(currentArmPos - Constants.NIntakeArmExtendedFull) < 0.05;
    }



    private void updatePivotState() {
        double pivotPosition = NintakeWristPivot.getPosition();
        isPivotHorizontal = Math.abs(pivotPosition - Constants.NIntakeWristPivotHorizontal) < 0.05;
    }

    public BucketSideAutoSubsystem(HardwareMap hardwareMap, Telemetry telemetry) {
        this.telemetry = telemetry;

        NintakeArm = hardwareMap.get(Servo.class, "NintakeArm");
        NintakeWrist = hardwareMap.get(Servo.class, "NintakeWrist");
        NintakeWristPivot = hardwareMap.get(Servo.class, "NintakeWristPivot");
        NintakeClaw = hardwareMap.get(Servo.class, "NintakeClaw");

        OuttakeArm = hardwareMap.get(Servo.class, "OuttakeArm");
        OuttakeWrist = hardwareMap.get(Servo.class, "OuttakeWrist");
        OuttakeWristPivot = hardwareMap.get(Servo.class, "OuttakeWristPivot");
        OuttakeClaw = hardwareMap.get(Servo.class, "OuttakeClaw");

        viperMotor = hardwareMap.get(DcMotorEx.class, "viper1motor");
        viperMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        viperMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        viperMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    public void extendIntake() {
        NintakeArm.setPosition(Constants.NIntakeArmExtendedFull);
        NintakeWrist.setPosition(Constants.NIntakeWristPickUpBefore);
    }

    public void extendIntakeCross() {
        NintakeArm.setPosition(Constants.NIntakeArmExtendedThird);
        NintakeWrist.setPosition(Constants.NIntakeWristPickUp);
        NintakeWristPivot.setPosition(Constants.NIntakeWristPivotVertical);
    }


    public void downWhileScore() {
        OuttakeArm.setPosition(Constants.OuttakeArmDownWhileBucket);
        OuttakeWrist.setPosition(Constants.OuttakeWristDownWhileBucket);
    }

    public void wristDown() {
        NintakeWrist.setPosition(Constants.NIntakeWristPickUp);
    }

    public void wristUp() {
        NintakeWrist.setPosition(Constants.NIntakeWristTransfer);
    }


    public void retractIntakeTransfer() {
        NintakeArm.setPosition(Constants.NIntakeArmTransfer);
        NintakeWrist.setPosition(Constants.NIntakeWristTransfer);
        NintakeWristPivot.setPosition(Constants.NIntakeWristPivotHorizontal);
    }

    public void IntakePivotHorizontal() {
        NintakeWristPivot.setPosition(Constants.NIntakeWristPivotHorizontal);
    }



    public void retractIntakeFull() {
        NintakeArm.setPosition(Constants.NIntakeArmExtendedBack);
        NintakeWrist.setPosition(Constants.NIntakeWristPickUp);
        NintakeWristPivot.setPosition(Constants.NIntakeWristPivotHorizontal);
    }

    public void bucketAuto() {
        NintakeArm.setPosition(Constants.NIntakeArmExtendedBack);
        NintakeWrist.setPosition(Constants.NIntakeWristPickUp);
    }

    public void retractSpecimenInit() {
        NintakeArm.setPosition(Constants.NIntakeArmExtendedBack);
//        NintakeWrist.setPosition(Constants.NIntakeWristPickUp);
        NintakeWristPivot.setPosition(Constants.NIntakeWristPivotTransfer);
    }

    public void openIntakeClaw() {
        NintakeClaw.setPosition(Constants.NIntakeClawOpen);
    }

    public void armWall() {
        NintakeArm.setPosition(Constants.NintakeArmWall);
    }


    public void closeIntakeClaw() {
        NintakeClaw.setPosition(Constants.NIntakeClawClose);
    }

    public void openOuttakeClaw() {
        OuttakeClaw.setPosition(Constants.OuttakeClawOpen);
    }

    public void closeOuttakeClaw() {
        OuttakeClaw.setPosition(Constants.OuttakeClawClose);
    }

    public void waitPOS() {
        OuttakeArm.setPosition(positions_motor.OuttakeArmNewTransferWAIT);
        OuttakeWrist.setPosition(positions_motor.OuttakeWristPickUpSpecimen);
        OuttakeWristPivot.setPosition(positions_motor.OuttakeWristPivotHighBar);
    }

    public void viperDown() {
        viperDownState = 0;
        viperDownTimer.reset();
        viperDownInProgress = true;
    }

    public void setHighBucket() {
        OuttakeArm.setPosition(Constants.OuttakeArmBucket);
        OuttakeWrist.setPosition(Constants.OuttakeWristBucket);
        OuttakeWristPivot.setPosition(Constants.OuttakeWristPivotHighBar);
        viperMotor.setTargetPosition(Constants.VIPER_HIGHBASKET);
        viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        viperMotor.setPower(1);  // updateViperMotorState will manage the actual power
    }

    public void setHighBucketViper() {
        viperMotor.setTargetPosition(Constants.VIPER_HIGHBASKET);
        viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        viperMotor.setPower(1);  // updateViperMotorState will manage the actual power
    }
    public void setPickupPOS() {
        OuttakeArm.setPosition(Constants.OuttakeArmNewTransfer);
        OuttakeWrist.setPosition(Constants.OuttakeWristTransfer);
        OuttakeWristPivot.setPosition(Constants.OuttakeWristPivotHighBar);
    }

    public void tempPOS() {
        OuttakeArm.setPosition(Constants.OuttakeArmNewTransferWAIT);
    }

    public void preloadOuttake()
    {
        OuttakeArm.setPosition(Constants.OuttakeArmPedroAuto);
        OuttakeWrist.setPosition(Constants.OuttakeWristPedroAuto);
        OuttakeWristPivot.setPosition(Constants.OuttakeWristPivotPedro);
    }

    public void parkOuttake() {
        viperMotor.setTargetPosition(0);
        viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        viperMotor.setPower(1);
        OuttakeArm.setPosition(Constants.OuttakeArmBucket);
    }

    @Override
    public void periodic() {

        updateViperMotorState();
        if(transferInProgress) {
            switch(transferState) {
                case 0:
                    updateArmExtensionState(); // Update arm state before starting transfer
                    NintakeWrist.setPosition(positions_motor.NIntakeWristTransfer);
                    OuttakeClaw.setPosition(positions_motor.OuttakeClawOpen);
                    NintakeArm.setPosition(positions_motor.NIntakeArmTransfer);

                    if(transferTimer.milliseconds() > 500) {
                        transferState = 1;
                        transferTimer.reset();
                    }
                    break;

                case 1:
                    if(isArmExtended) {
                        if(transferTimer.milliseconds() <= 450) {
                            // Do nothing and wait until 300ms has passed
                        } else {
                            // Execute movements after 300ms delay
                            NintakeClaw.setPosition(positions_motor.NIntakeClawCloseFull);
                            NintakeWristPivot.setPosition(positions_motor.NIntakeWristPivotTransfer);
                            NintakeArm.setPosition(positions_motor.NIntakeArmTransfer);
                            OuttakeArm.setPosition(positions_motor.OuttakeArmNewTransfer);
                            OuttakeWrist.setPosition(positions_motor.OuttakeWristTransfer);
                            OuttakeWristPivot.setPosition(positions_motor.OuttakeWristPivotHighBar);
                            if(transferTimer.milliseconds() > 200) {
                                transferState = 2;
                                transferTimer.reset();
                            }
                        }
                    } else {
                        // Execute movements immediately if not extended
                        NintakeClaw.setPosition(positions_motor.NIntakeClawCloseFull);
                        NintakeWristPivot.setPosition(positions_motor.NIntakeWristPivotTransfer);
                        OuttakeArm.setPosition(positions_motor.OuttakeArmNewTransfer);
                        OuttakeWrist.setPosition(positions_motor.OuttakeWristTransfer);
                        OuttakeWristPivot.setPosition(positions_motor.OuttakeWristPivotHighBar);
                        if(transferTimer.milliseconds() > 200) {
                            transferState = 2;
                            transferTimer.reset();
                        }
                    }
                    break;

                case 2:
                    OuttakeClaw.setPosition(positions_motor.OuttakeClawClose);
                    if(transferTimer.milliseconds() > 400) {
                        transferState = 3;
                        transferTimer.reset();
                    }
                    break;

                case 3:
                    NintakeClaw.setPosition(positions_motor.NIntakeClawOpen);
                    if(transferTimer.milliseconds() > 100) {
                        transferState = 4;
                        transferTimer.reset();
                    }
                    break;

                case 4:
                    OuttakeArm.setPosition(positions_motor.OuttakeArmBucket);
                    OuttakeWrist.setPosition(positions_motor.OuttakeWristBucket);
                    OuttakeWristPivot.setPosition(positions_motor.OuttakeWristPivotHighBar);
                    NintakeWristPivot.setPosition(positions_motor.NIntakeWristPivotHorizontal);
                    NintakeWrist.setPosition(positions_motor.NIntakeWristPickUp);
                    viperMotor.setTargetPosition(positions_motor.VIPER_HIGHBASKET);
                    viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    viperMotor.setPower(1);
                    transferInProgress = false;
                    break;
            }
        }

        if(viperDownInProgress) {
            switch(viperDownState) {
                case 0:
                    // First move the arms to a safe position
                    OuttakeArm.setPosition(Constants.OuttakeArmNewTransferWAIT);
                    OuttakeWrist.setPosition(Constants.OuttakeWristPickUpSpecimen);
                    OuttakeWristPivot.setPosition(Constants.OuttakeWristPivotHighBar);

                    // Wait for arms to reach position before moving viper
                    if(viperDownTimer.milliseconds() > 750) {
                        viperDownState = 1;
                        viperDownTimer.reset();
                    }
                    break;

                case 1:
                    // Move viper to ground position
                    viperMotor.setTargetPosition(Constants.VIPER_GROUND);
                    viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    viperMotor.setPower(1);
                    viperDownInProgress = false;
                    break;
            }
        }



        telemetry.addData("Viper Power", viperMotor.getPower());
        telemetry.update();
    }


}