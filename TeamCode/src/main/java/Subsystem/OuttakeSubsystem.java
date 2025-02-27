package Subsystem;

import com.arcrobotics.ftclib.command.SubsystemBase;
import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import com.qualcomm.robotcore.util.ElapsedTime;
import Positions.positions_motor;

public class OuttakeSubsystem extends SubsystemBase {
    private final Servo OuttakeArmLeft, OuttakeArmRight, OuttakeWrist, OuttakeWristPivot, OuttakeClaw;
    private final Telemetry telemetry; // Can be null
    private final Follower follower;
    private int pickupState = 0;
    private boolean pickupInProgress = false;
    private ElapsedTime pickupTimer = new ElapsedTime();
    private int backState = 0;
    private boolean backProgress = false;
    private ElapsedTime backTimer = new ElapsedTime();
    private int preloadPickupState = 0;
    private boolean preloadPickupInProgress = false;
    private ElapsedTime preloadPickupTimer = new ElapsedTime();
    private int pickUpFullState = 0;
    private boolean pickUpFullInProgress = false;
    private ElapsedTime pickUpFullTimer = new ElapsedTime();
    private int directState = 0;
    private boolean directInProgress = false;
    private ElapsedTime directFullTimer = new ElapsedTime();
    private DistanceSensor sensor;

    public OuttakeSubsystem(HardwareMap hardwareMap, Telemetry telemetry, Follower follower) {
        this.telemetry = telemetry; // Telemetry might be null
        this.follower = follower;
        OuttakeArmLeft = hardwareMap.get(Servo.class, "OuttakeArmLeft");
        OuttakeArmRight = hardwareMap.get(Servo.class, "OuttakeArmRight");
        OuttakeWrist = hardwareMap.get(Servo.class, "OuttakeWrist");
        OuttakeWristPivot = hardwareMap.get(Servo.class, "OuttakeWristPivot");
        OuttakeClaw = hardwareMap.get(Servo.class, "OuttakeClaw");
        sensor = hardwareMap.get(DistanceSensor.class, "sensor");
    }

    public void preloadPickUpFull() {
        OuttakeArmLeft.setPosition(positions_motor.STATE_OUTTAKEARMLEFT_HIGHBAR);
        OuttakeArmRight.setPosition(positions_motor.STATE_OUTTAKEARMRIGHT_HIGHBAR);
        OuttakeWrist.setPosition(positions_motor.STATE_OUTTAKEWRIST_HIGHBAR);
        OuttakeWristPivot.setPosition(positions_motor.STATE_OUTTAKEWRISTPIVOT_HIGHBAR);
    }

    public void pickUpFull() {
        pickUpFullState = 0;
        pickUpFullTimer.reset();
        pickUpFullInProgress = true;
    }

    public void pickUpFullPreload() {
        pickupState = 0;
        pickupTimer.reset();
        pickupInProgress = true;
    }

    public void directPlacement() {
        directState = 0;
        directFullTimer.reset();
        directInProgress = true;
    }

    public void pickUpPOS() {
        backState = 0;
        backTimer.reset();
        backProgress = true;
    }

    public enum OuttakeState {
        PICKUP(positions_motor.STATE_OUTTAKEARMLEFT_PICKUP,
                positions_motor.STATE_OUTTAKEARMRIGHT_PICKUP,
                positions_motor.STATE_OUTTAKEWRIST_PICKUP,
                positions_motor.STATE_OUTTAKEWRISTPIVOT_PICKUP,
                positions_motor.VIPER_GROUND),

        PRELOAD(positions_motor.STATE_OUTTAKEARMLEFT_PICKUP,
                positions_motor.STATE_OUTTAKEARMRIGHT_PICKUP,
                positions_motor.STATE_OUTTAKEWRIST_PICKUP,
                positions_motor.STATE_OUTTAKEWRISTPIVOT_PICKUP,
                positions_motor.VIPER_GROUND),
        SCORE(positions_motor.STATE_OUTTAKEARMLEFT_HIGHBAR,
                positions_motor.STATE_OUTTAKEARMRIGHT_HIGHBAR,
                positions_motor.STATE_OUTTAKEWRIST_HIGHBAR,
                positions_motor.STATE_OUTTAKEWRISTPIVOT_HIGHBAR,
                positions_motor.STATE_VIPER_HIGHBAR);

        private final double armLeftPos, armRightPos, wristPos, wristPivotPos, viperPos;

        OuttakeState(double armLeftPos, double armRightPos, double wristPos,
                     double wristPivotPos, double viperPos) {
            this.armLeftPos = armLeftPos;
            this.armRightPos = armRightPos;
            this.wristPos = wristPos;
            this.wristPivotPos = wristPivotPos;
            this.viperPos = viperPos;
        }
    }

    public enum ClawState {
        OPEN(positions_motor.STATE_OUTTAKECLAW_OPEN),
        CLOSED(positions_motor.STATE_OUTTAKECLAW_CLOSE);

        private final double position;
        ClawState(double position) {
            this.position = position;
        }
    }

    private OuttakeState currentState = OuttakeState.SCORE;
    private ClawState clawState = ClawState.CLOSED;

    public void setToState(OuttakeState state) {
        currentState = state;
        OuttakeArmLeft.setPosition(state.armLeftPos);
        OuttakeArmRight.setPosition(state.armRightPos);
        OuttakeWrist.setPosition(state.wristPos);
        OuttakeWristPivot.setPosition(state.wristPivotPos);
    }

    public void prepareScoreViper() {
    }

    public void completeScoringPosition() {
        OuttakeArmLeft.setPosition(positions_motor.STATE_OUTTAKEARMLEFT_HIGHBAR);
        OuttakeArmRight.setPosition(positions_motor.STATE_OUTTAKEARMRIGHT_HIGHBAR);
        OuttakeWrist.setPosition(positions_motor.STATE_OUTTAKEWRIST_HIGHBAR);
        OuttakeWristPivot.setPosition(positions_motor.STATE_OUTTAKEWRISTPIVOT_HIGHBAR);
    }

    public boolean distance() {
        double a = sensor.getDistance(DistanceUnit.CM);
        boolean b = false;
        if(a <= 1.5 && a >= 0.5) {
            b = true;
        }
        return b;
    }

    public void preloadPosition() {
        OuttakeArmLeft.setPosition(positions_motor.SPECIMEN_OUTTAKEARMLEFT_PRELOAD);
        OuttakeArmRight.setPosition(positions_motor.SPECIMEN_OUTTAKEARMRIGHT_PRELOAD);
        OuttakeWrist.setPosition(positions_motor.SPECIMEN_OUTTAKEWRIST_PRELOAD);
        OuttakeWristPivot.setPosition(positions_motor.SPECIMEN_OUTTAKEWRISTPIVOT_PRELOAD);
    }

    public void setMaxSpeed(double speed) {
        if (follower != null) {
            follower.setMaxPower(speed);
        }
    }

    public void setClaw(ClawState state) {
        clawState = state;
        OuttakeClaw.setPosition(state.position);
    }

    public void scoreFull() {
        setToState(OuttakeState.SCORE);
    }

    public void openClaw() {
        setClaw(ClawState.OPEN);
    }

    public void closeClaw() {
        setClaw(ClawState.CLOSED);
    }

    public void flick() {
        OuttakeArmLeft.setPosition(positions_motor.STATE_OUTTAKEARMLEFT_FLICK);
        OuttakeArmRight.setPosition(positions_motor.STATE_OUTTAKEARMRIGHT_FLICK);
        OuttakeWrist.setPosition(positions_motor.STATE_OUTTAKEWRIST_FLICK);
        OuttakeWristPivot.setPosition(positions_motor.STATE_OUTTAKEWRISTPIVOT_HIGHBAR);
    }

    @Override
    public void periodic() {
        if(pickupInProgress) {
            switch(pickupState) {
                case 0:
                    OuttakeArmLeft.setPosition(positions_motor.STATE_OUTTAKEARMLEFT_HIGHBAR);
                    OuttakeArmRight.setPosition(positions_motor.STATE_OUTTAKEARMRIGHT_HIGHBAR);
                    OuttakeWrist.setPosition(positions_motor.STATE_OUTTAKEWRIST_HIGHBAR);
                    if(pickupTimer.milliseconds() > 100) {
                        pickupState = 1;
                        pickupTimer.reset();
                    }
                    break;

                case 1:
                    OuttakeWristPivot.setPosition(positions_motor.STATE_OUTTAKEWRISTPIVOT_HIGHBAR);
                    if(pickupTimer.milliseconds() > 50) {
                        pickupInProgress = false;
                    }
                    break;
            }
        }

        if(backProgress) {
            switch(backState) {
                case 0:
                    if(backTimer.milliseconds() > 500) {
                        OuttakeArmLeft.setPosition(positions_motor.STATE_OUTTAKEARMLEFT_PICKUP);
                        OuttakeArmRight.setPosition(positions_motor.STATE_OUTTAKEARMRIGHT_PICKUP);
                        OuttakeWrist.setPosition(positions_motor.STATE_OUTTAKEWRIST_PICKUP);
                        if(backTimer.milliseconds() > 1200) {
                            backState = 1;
                            backTimer.reset();
                        }
                    }
                    break;

                case 1:
                    OuttakeWristPivot.setPosition(positions_motor.STATE_OUTTAKEWRISTPIVOT_PICKUP);
                    if(backTimer.milliseconds() > 50) {
                        backProgress = false;
                    }
                    break;
            }
        }

        if(preloadPickupInProgress) {
            switch(preloadPickupState) {
                case 0:
                    OuttakeArmLeft.setPosition(positions_motor.STATE_OUTTAKEARMLEFT_HIGHBAR);
                    OuttakeArmRight.setPosition(positions_motor.STATE_OUTTAKEARMRIGHT_HIGHBAR);
                    OuttakeWrist.setPosition(positions_motor.STATE_OUTTAKEWRIST_HIGHBAR);
                    if(preloadPickupTimer.milliseconds() > 300) {
                        preloadPickupState = 1;
                        preloadPickupTimer.reset();
                    }
                    break;

                case 1:
                    OuttakeWristPivot.setPosition(positions_motor.STATE_OUTTAKEWRISTPIVOT_HIGHBAR);
                    if(preloadPickupTimer.milliseconds() > 75) {
                        preloadPickupInProgress = false;
                    }
                    break;
            }
        }

        if(pickUpFullInProgress) {
            switch(pickUpFullState) {
                case 0:
                    OuttakeArmLeft.setPosition(positions_motor.STATE_OUTTAKEARMLEFT_HIGHBAR);
                    OuttakeArmRight.setPosition(positions_motor.STATE_OUTTAKEARMRIGHT_HIGHBAR);
                    OuttakeWrist.setPosition(positions_motor.STATE_OUTTAKEWRIST_HIGHBAR);
                    if(pickUpFullTimer.milliseconds() > 5) {
                        pickUpFullState = 1;
                        pickUpFullTimer.reset();
                    }
                    break;

                case 1:
                    OuttakeWristPivot.setPosition(positions_motor.STATE_OUTTAKEWRISTPIVOT_HIGHBAR);
                    if(pickUpFullTimer.milliseconds() > 50) {
                        pickUpFullInProgress = false;
                    }
                    break;
            }
        }

        if(directInProgress) {
            switch(directState) {
                case 0:
                    OuttakeArmLeft.setPosition(positions_motor.STATE_OUTTAKEARMLEFT_HIGHBAR);
                    OuttakeArmRight.setPosition(positions_motor.STATE_OUTTAKEARMRIGHT_HIGHBAR);
                    OuttakeWrist.setPosition(positions_motor.STATE_OUTTAKEWRIST_HIGHBAR);
                    if(directFullTimer.milliseconds() > 500) {
                        directState = 1;
                        directFullTimer.reset();
                    }
                    break;

                case 1:
                    OuttakeWristPivot.setPosition(positions_motor.STATE_OUTTAKEWRISTPIVOT_HIGHBAR);
                    if(directFullTimer.milliseconds() > 1800) {
                        directState = 2;
                        directFullTimer.reset();
                    }
                    break;

                case 2:
                    OuttakeArmLeft.setPosition(positions_motor.STATE_OUTTAKEARMLEFT_FLICK);
                    OuttakeArmRight.setPosition(positions_motor.STATE_OUTTAKEARMRIGHT_FLICK);
                    if(directFullTimer.milliseconds() > 50) {
                        directState = 3;
                        directFullTimer.reset();
                        directInProgress = false;
                    }
                    break;
            }
        }

        // Null-safe telemetry calls
        if (telemetry != null) {
//            telemetry.addData("Outtake State", currentState);
//            telemetry.addData("Claw State", clawState);
//            telemetry.addData("Pickup State", pickupState);
//            telemetry.addData("Pickup Timer", pickupTimer.milliseconds());
//            telemetry.addData("Pickup In Progress", pickupInProgress);
//            telemetry.addData("PickUpFull State", pickUpFullState);
//            telemetry.addData("PickUpFull Timer", pickUpFullTimer.milliseconds());
//            telemetry.addData("PickUpFull In Progress", pickUpFullInProgress);
//            telemetry.update();
        }
    }
}