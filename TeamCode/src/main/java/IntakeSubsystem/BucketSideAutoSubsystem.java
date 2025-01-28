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

    public void startTransfer() {
        transferState = 0;
        transferTimer.reset();
        transferInProgress = true;
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
        NintakeArm.setPosition(Constants.NIntakeArmTransfer);
        NintakeWrist.setPosition(Constants.NIntakeWristPickUpBefore);
        NintakeWristPivot.setPosition(Constants.NIntakeWristPivotVertical);
    }


    public void downWhileScore() {
        OuttakeArm.setPosition(Constants.OuttakeArmDownWhileBucket);
        OuttakeWrist.setPosition(Constants.OuttakeWristDownWhileBucket);
    }

    public void wristDown() {
        NintakeWrist.setPosition(Constants.NIntakeWristPickUp);
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

    public void retractSpecimenInit() {
        NintakeArm.setPosition(Constants.NIntakeArmExtendedBack);
        NintakeWrist.setPosition(Constants.NIntakeWristPickUp);
        NintakeWristPivot.setPosition(Constants.NIntakeWristPivotTransfer);
    }

    public void openIntakeClaw() {
        NintakeClaw.setPosition(Constants.NIntakeClawOpen);
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

    public void viperDown() {
        viperMotor.setTargetPosition(Constants.VIPER_GROUND);
        viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        viperMotor.setPower(1);
    }

    public void setHighBucket() {
        OuttakeArm.setPosition(Constants.OuttakeArmBucket);
        OuttakeWrist.setPosition(Constants.OuttakeWristBucket);
        OuttakeWristPivot.setPosition(Constants.OuttakeWristPivotHighBar);
        viperMotor.setTargetPosition(Constants.VIPER_HIGHBASKET);
        viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        viperMotor.setPower(1);
    }

    public void setHighBucketViper() {
        viperMotor.setTargetPosition(Constants.VIPER_HIGHBASKET);
        viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        viperMotor.setPower(1);
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
        if (transferInProgress) {
            switch(transferState) {
                case 0:
                    NintakeWrist.setPosition(Constants.NIntakeWristTransfer);
                    OuttakeClaw.setPosition(Constants.OuttakeClawOpen);
                    if(transferTimer.milliseconds() > 500) {
                        transferState = 1;
                        transferTimer.reset();
                    }
                    break;

                case 1:
                    NintakeClaw.setPosition(Constants.NIntakeClawCloseFull);
                    NintakeWristPivot.setPosition(Constants.NIntakeWristPivotTransfer);
                    NintakeArm.setPosition(Constants.NIntakeArmTransfer);
                    OuttakeArm.setPosition(Constants.OuttakeArmNewTransfer);
                    OuttakeWrist.setPosition(Constants.OuttakeWristTransfer);
                    OuttakeWristPivot.setPosition(Constants.OuttakeWristPivotHighBar);
                    if(transferTimer.milliseconds() > 1000) {
                        transferState = 2;
                        transferTimer.reset();
                    }
                    break;

                case 2:
                    OuttakeClaw.setPosition(Constants.OuttakeClawClose);
                    if(transferTimer.milliseconds() > 1000) {
                        transferState = 3;
                        transferTimer.reset();
                    }
                    break;

                case 3:
                    NintakeClaw.setPosition(Constants.NIntakeClawOpen);
                    if(transferTimer.milliseconds() > 1000) {
                        transferState = 4;
                        transferTimer.reset();
                    }
                    break;

                case 4:
                    NintakeArm.setPosition(Constants.NIntakeArmExtendedBack);
                    if(transferTimer.milliseconds() > 1000) {
                        transferState = 5;
                        transferTimer.reset();
                    }
                    break;

                case 5:
                    OuttakeArm.setPosition(Constants.OuttakeArmBucket);
                    OuttakeWrist.setPosition(Constants.OuttakeWristBucket);
                    OuttakeWristPivot.setPosition(Constants.OuttakeWristPivotHighBar);
                    NintakeWristPivot.setPosition(Constants.NIntakeWristPivotHorizontal);
                    NintakeWrist.setPosition(Constants.NIntakeWristPickUpBefore);
                    transferInProgress = false;
                    break;
            }
        }

        telemetry.addData("Sequence in Progress", sequenceInProgress);
        telemetry.addData("Sequence State", sequenceState);
        telemetry.addData("Timer", sequenceTimer.milliseconds());
        telemetry.addData("Viper Position", viperMotor.getCurrentPosition());
        telemetry.addData("Viper Target", viperMotor.getTargetPosition());
        telemetry.addData("Intake Arm", NintakeArm.getPosition());
        telemetry.addData("Intake Wrist", NintakeWrist.getPosition());
        telemetry.addData("Intake Pivot", NintakeWristPivot.getPosition());
        telemetry.addData("Intake Claw", NintakeClaw.getPosition());
        telemetry.addData("Outtake Arm", OuttakeArm.getPosition());
        telemetry.addData("Outtake Wrist", OuttakeWrist.getPosition());
        telemetry.addData("Outtake Pivot", OuttakeWristPivot.getPosition());
        telemetry.addData("Outtake Claw", OuttakeClaw.getPosition());
        telemetry.addData("Transfer State", transferState);
        telemetry.addData("Transfer Timer", transferTimer.milliseconds());
        telemetry.addData("Transfer In Progress", transferInProgress);
        telemetry.update();
    }
}