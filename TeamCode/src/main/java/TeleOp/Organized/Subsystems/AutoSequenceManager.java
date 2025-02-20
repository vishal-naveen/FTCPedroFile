package TeleOp.Organized.Subsystems;

import com.arcrobotics.ftclib.command.CommandOpMode;
import com.arcrobotics.ftclib.command.CommandScheduler;
import com.arcrobotics.ftclib.command.RunCommand;
import com.arcrobotics.ftclib.command.SequentialCommandGroup;
import com.arcrobotics.ftclib.command.WaitUntilCommand;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.BezierCurve;
import com.pedropathing.pathgen.Path;
import com.pedropathing.pathgen.Point;
import com.qualcomm.robotcore.hardware.AnalogInput; // Added for sensor
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;

import TeleOp.Organized.Subsystems.IntakeSubsystem;
import TeleOp.Organized.Subsystems.DriveSubsystem;
import TeleOp.Organized.Subsystems.OuttakeSubsystem;
import TeleOp.Organized.Subsystems.ViperSubsystem;
import TeleOp.Organized.Subsystems.AutoSequenceManager;
import TeleOp.Organized.Subsystems.TelemetryManager;
import Positions.positions_motor;

public class AutoSequenceManager {
    private final DriveSubsystem drive;
    private final IntakeSubsystem intake;
    private final OuttakeSubsystem outtake;
    private final ViperSubsystem viper;
    private final AnalogInput ultra; // Added sensor field

    private Path scorePath;
    private Path pickPath;
    private SequentialCommandGroup autoSequence;
    private boolean autoSequenceActive = false;
    private int autoState = 0;
    private int currentCycle = 0;
    private final int maxCycles = 3;
    private int transferState = 0;
    private ElapsedTime transferTimer = new ElapsedTime();
    private boolean transferInProgress = false;

    public AutoSequenceManager(DriveSubsystem drive, IntakeSubsystem intake,
                               OuttakeSubsystem outtake, ViperSubsystem viper,
                               AnalogInput ultra) { // Updated constructor
        this.drive = drive;
        this.intake = intake;
        this.outtake = outtake;
        this.viper = viper;
        this.ultra = ultra; // Initialize sensor
    }

    public void handleAutoSequence(Gamepad gamepad, CommandOpMode opMode) {
        // Calculate distInches using the sensor
        double distInches = (100 * (ultra.getVoltage() / 3.3)) / 2.54; // Integrated sensor calculation
        boolean isDistanceLessThan25 = distInches <= 25;
        boolean isDistanceLessThan10 = distInches <= 10;

        if (autoState == 0 && (gamepad.b || isDistanceLessThan25) && !autoSequenceActive) {
            autoSequenceActive = true;
            initializePaths();
            autoState = 1;

            autoSequence = new SequentialCommandGroup(
                    new RunCommand(() -> drive.getFollower().followPath(scorePath))
                            .raceWith(new WaitUntilCommand(() -> isDistanceLessThan10)
                                    .andThen(new RunCommand(() -> drive.getFollower().followPath(pickPath)))),
                    new RunCommand(() -> {
                        transferState = 0;
                        transferTimer.reset();
                        transferInProgress = true;
                    }),
                    new RunCommand(() -> drive.getFollower().followPath(pickPath))
                            .andThen(new RunCommand(() -> {
                                currentCycle++;
                                if (currentCycle >= maxCycles) {
                                    cancelAutoSequence();
                                } else {
                                    autoState = 1;
                                    opMode.schedule(autoSequence);
                                }
                            }))
            );
            opMode.schedule(autoSequence);
        }

        if (autoSequenceActive && drive.hasJoystickInput(gamepad)) {
            cancelAutoSequence();
        }

        handleTransfer();
    }

    private void initializePaths() {
        Pose startPose = new Pose(30, 0, Math.toRadians(0));
        Pose scorePose = new Pose(0, 0, Math.toRadians(0));

        drive.getFollower().setPose(startPose);

        scorePath = new Path(new BezierCurve(new Point(startPose), new Point(scorePose)));
        scorePath.setConstantHeadingInterpolation(startPose.getHeading());
        pickPath = new Path(new BezierCurve(new Point(scorePose), new Point(startPose)));
        pickPath.setConstantHeadingInterpolation(startPose.getHeading());
    }

    private void cancelAutoSequence() {
        autoState = 0;
        autoSequenceActive = false;
        currentCycle = 0;
        CommandScheduler.getInstance().cancelAll();
        drive.getFollower().breakFollowing();
        drive.getFollower().startTeleopDrive();
    }

    private void handleTransfer() {
        if (!transferInProgress) return;

        viper.cancelGroundTimer();
        switch (transferState) {
            case 0:
                intake.getWrist().setPosition(positions_motor.NIntakeWristTransfer);
                outtake.getClaw().setPosition(positions_motor.OuttakeClawOpen);
                intake.getArm().setPosition(positions_motor.NIntakeArmTransfer);
                if (transferTimer.milliseconds() > 500) {
                    transferState = 1;
                    transferTimer.reset();
                }
                break;
            // Add other transfer states here (cases 1-4)
            // Removed for brevity, but should be implemented similarly
        }
    }
}