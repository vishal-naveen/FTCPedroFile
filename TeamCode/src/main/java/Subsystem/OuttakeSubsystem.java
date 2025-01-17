package Subsystem;

import com.arcrobotics.ftclib.command.SubsystemBase;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import com.arcrobotics.ftclib.command.SequentialCommandGroup;
import com.arcrobotics.ftclib.command.WaitCommand;

import Positions.Constants;
import Positions.positions_motor;

public class OuttakeSubsystem extends SubsystemBase {
    private final Servo OuttakeArm, OuttakeWrist, OuttakeWristPivot, OuttakeClaw;
//    private final DcMotorEx viperMotor;
    private Telemetry telemetry;

    public enum OuttakeState {
        PICKUP(Constants.OuttakeArmPickUpSpecimen,
               Constants.OuttakeWristPickUpSpecimen,
               Constants.OuttakeWristPivotSpecimenPickUp,
               Constants.VIPER_GROUND),
        SCORE(Constants.OuttakeArmNewHighBar,
              Constants.OuttakeWristNewHighBar,
              Constants.OuttakeWristPivotHighBar,
              Constants.VIPER_HIGHBAR),

        PRELOAD(Constants.OuttakeArmPedroAuto,         // 0.6
                Constants.OuttakeWristPedroAuto,        // 0
                Constants.OuttakeWristPivotPedro,    // 0
                Constants.VIPER_GROUND),

        FLICK(Constants.OuttakeArmNewHighBarFLICK,     // 0.15
                Constants.OuttakeWristNewHighBarFLICK,    // 0.8
                Constants.OuttakeWristPivotHighBar,       // 0
                Constants.VIPER_HIGHBAR);


        private final double armPos, wristPos, wristPivotPos, viperPos;

        OuttakeState(double armPos, double wristPos, double wristPivotPos, int viperPos) {
            this.armPos = armPos;
            this.wristPos = wristPos;
            this.wristPivotPos = wristPivotPos;
            this.viperPos = viperPos;
        }
    }

    public enum ClawState {
        OPEN(Constants.OuttakeClawOpen),
        CLOSED(Constants.OuttakeClawClose);

        private final double position;
        ClawState(double position) {
            this.position = position;
        }
    }

    private OuttakeState currentState = OuttakeState.SCORE;
    private ClawState clawState = ClawState.CLOSED;



    public OuttakeSubsystem(HardwareMap hardwareMap, Telemetry telemetry) {
        this.telemetry = telemetry;

        OuttakeArm = hardwareMap.get(Servo.class, "OuttakeArm");
        OuttakeWrist = hardwareMap.get(Servo.class, "OuttakeWrist");
        OuttakeWristPivot = hardwareMap.get(Servo.class, "OuttakeWristPivot");
        OuttakeClaw = hardwareMap.get(Servo.class, "OuttakeClaw");

//        viperMotor = hardwareMap.get(DcMotorEx.class, "viper1motor");
//        viperMotor.setDirection(DcMotorSimple.Direction.FORWARD);
//        viperMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//        viperMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    public void setToState(OuttakeState state) {
        currentState = state;
        OuttakeArm.setPosition(state.armPos);
        OuttakeWrist.setPosition(state.wristPos);
        OuttakeWristPivot.setPosition(state.wristPivotPos);

//        viperMotor.setTargetPosition((int)state.viperPos);
//        viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//        viperMotor.setPower(1);
    }

    public void prepareScoreViper() {
        // Only lift the viper to high bar position
//        viperMotor.setTargetPosition((int)OuttakeState.SCORE.viperPos);
//        viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//        viperMotor.setPower(1);
    }

    public void completeScoringPosition() {
        // Set arm and wrist positions after viper is lifted
        OuttakeArm.setPosition(OuttakeState.SCORE.armPos);
        OuttakeWrist.setPosition(OuttakeState.SCORE.wristPos);
        OuttakeWristPivot.setPosition(OuttakeState.SCORE.wristPivotPos);
    }

    public void preloadPosition() {
        setToState(OuttakeState.PRELOAD);
    }

    public void setClaw(ClawState state) {
        clawState = state;
        OuttakeClaw.setPosition(state.position);
    }

    public void scoreFull() {
        setToState(OuttakeState.SCORE);
    }

    public void pickUpFull() {
        setToState(OuttakeState.PICKUP);
    }

    public void openClaw() {
        setClaw(ClawState.OPEN);
    }

    public void closeClaw() {
        setClaw(ClawState.CLOSED);
    }
    public void flick() {
        setToState(OuttakeState.FLICK);
    }



    @Override
    public void periodic() {
        telemetry.addData("Outtake State", currentState);
        telemetry.addData("Claw State", clawState);
//        telemetry.addData("Viper Position", viperMotor.getCurrentPosition());
        telemetry.update();
    }
}