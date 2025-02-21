// fieldOrganized.java (full updated version)
package TeleOp.Organized;

import static Positions.Commands.sleep;

import com.arcrobotics.ftclib.command.CommandOpMode;
import com.arcrobotics.ftclib.command.CommandScheduler;
import com.arcrobotics.ftclib.command.RunCommand;
import com.arcrobotics.ftclib.command.SequentialCommandGroup;
import com.arcrobotics.ftclib.command.WaitUntilCommand;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import TeleOp.Organized.Subsystems.DriveSubsystem;
import TeleOp.Organized.Subsystems.IntakeSubsystem;
import TeleOp.Organized.Subsystems.OuttakeSubsystem;
import TeleOp.Organized.Subsystems.ViperSubsystem;
import TeleOp.Organized.Subsystems.TelemetryManager;
import Positions.positions_motor;

//@TeleOp(name="fieldOrganized")
public class fieldOrganized extends CommandOpMode {
    private DriveSubsystem drive;
    private ViperSubsystem viper;
    private IntakeSubsystem intake;
    private OuttakeSubsystem outtake;
    private TelemetryManager telemetryManager;
    private AnalogInput ultra;

    private boolean autoSequenceActive = false;
    private int autoState = 0;
    private int currentCycle = 0;
    private int maxCycles = 3;
    private SequentialCommandGroup autoSequence;
    private boolean cancelAutoRequested = false;  // New flag for safe cancellation

    private int transferState = 0;
    private ElapsedTime transferTimer = new ElapsedTime();
    private boolean transferInProgress = false;

    private boolean isArmExtended = false;

    @Override
    public void initialize() {
        ultra = hardwareMap.get(AnalogInput.class, "ultra");

        drive = new DriveSubsystem(this);
        viper = new ViperSubsystem(hardwareMap.get(DcMotor.class, "viper1motor"));
        intake = new IntakeSubsystem(hardwareMap);
        outtake = new OuttakeSubsystem(hardwareMap);
        telemetryManager = new TelemetryManager(telemetry, drive, viper, ultra, this);

        drive.initialize();
        viper.initialize();
        intake.initialize();
        outtake.initialize();
        telemetryManager.initialize();
    }

    @Override
    public void run() {
        drive.updateDrive(gamepad1);
        viper.handleControls(gamepad1, gamepad2, outtake);
        intake.handleControls(gamepad1, gamepad2);
        outtake.handleControls(gamepad2, viper);
        handleAutoSequence();
        handleTransfer();

        // Handle cancellation after other operations
        if (cancelAutoRequested) {
            performCancelAutoSequence();
            cancelAutoRequested = false;
        }

        telemetryManager.update();
        CommandScheduler.getInstance().run();
    }

    private void handleAutoSequence() {
        if (autoState == 0 && gamepad1.b && !autoSequenceActive) {
            autoSequenceActive = true;
            drive.initializePaths();
            autoState = 1;

            autoSequence = new SequentialCommandGroup(
                    new RunCommand(() -> drive.getFollower().followPath(drive.getScorePath()))
                            .raceWith(new WaitUntilCommand(() -> telemetryManager.getDistInches() <= 10)
                                    .andThen(new RunCommand(() -> drive.getFollower().followPath(drive.getPickPath())))),
                    new RunCommand(() -> {
                        transferState = 0;
                        transferTimer.reset();
                        transferInProgress = true;
                    }),
                    new RunCommand(() -> drive.getFollower().followPath(drive.getPickPath()))
                            .andThen(new RunCommand(() -> {
                                currentCycle++;
                                if (currentCycle >= maxCycles) {
                                    cancelAutoRequested = true;  // Request cancellation
                                } else {
                                    autoState = 1;
                                    // Schedule safely in next iteration
                                    schedule(new RunCommand(() -> schedule(autoSequence)));
                                }
                            }))
            );
            schedule(autoSequence);
        }

        if (autoSequenceActive && drive.hasJoystickInput(gamepad1)) {
            cancelAutoRequested = true;  // Request cancellation instead of immediate cancel
        }
    }

    private void performCancelAutoSequence() {
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
                updateArmExtensionState();
                intake.getWrist().setPosition(positions_motor.NIntakeWristTransfer);
                outtake.getClaw().setPosition(positions_motor.OuttakeClawOpen);
                intake.getArm().setPosition(positions_motor.NIntakeArmTransfer);
                if (transferTimer.milliseconds() > 500) {
                    transferState = 1;
                    transferTimer.reset();
                }
                break;
            case 1:
                if (isArmExtended) {
                    if (transferTimer.milliseconds() <= 450) {
                        // Wait
                    } else {
                        intake.getClaw().setPosition(positions_motor.NIntakeClawCloseFull);
                        intake.getWristPivot().setPosition(positions_motor.NIntakeWristPivotTransfer);
                        intake.getArm().setPosition(positions_motor.NIntakeArmTransfer);
                        outtake.getArm().setPosition(positions_motor.OuttakeArmNewTransfer);
                        outtake.getWrist().setPosition(positions_motor.OuttakeWristTransfer);
                        outtake.getWristPivot().setPosition(positions_motor.OuttakeWristPivotHighBar);
                        if (transferTimer.milliseconds() > 200) {
                            transferState = 2;
                            transferTimer.reset();
                        }
                    }
                } else {
                    intake.getClaw().setPosition(positions_motor.NIntakeClawCloseFull);
                    intake.getWristPivot().setPosition(positions_motor.NIntakeWristPivotTransfer);
                    outtake.getArm().setPosition(positions_motor.OuttakeArmNewTransfer);
                    outtake.getWrist().setPosition(positions_motor.OuttakeWristTransfer);
                    outtake.getWristPivot().setPosition(positions_motor.OuttakeWristPivotHighBar);
                    if (transferTimer.milliseconds() > 200) {
                        transferState = 2;
                        transferTimer.reset();
                    }
                }
                break;
            case 2:
                outtake.getClaw().setPosition(positions_motor.OuttakeClawClose);
                if (transferTimer.milliseconds() > 400) {
                    transferState = 3;
                    transferTimer.reset();
                }
                break;
            case 3:
                intake.getClaw().setPosition(positions_motor.NIntakeClawOpen);
                if (transferTimer.milliseconds() > 100) {
                    transferState = 4;
                    transferTimer.reset();
                }
                break;
            case 4:
                outtake.getArm().setPosition(positions_motor.OuttakeArmBucket);
                outtake.getWrist().setPosition(positions_motor.OuttakeWristBucket);
                outtake.getWristPivot().setPosition(positions_motor.OuttakeWristPivotHighBar);
                intake.getWristPivot().setPosition(positions_motor.NIntakeWristPivotHorizontal);
                intake.getWrist().setPosition(positions_motor.NIntakeWristPickUp);
                viper.getMotor().setTargetPosition(positions_motor.VIPER_HIGHBASKET);
                viper.getMotor().setMode(DcMotor.RunMode.RUN_TO_POSITION);
                viper.getMotor().setPower(1);
                transferInProgress = false;
                break;
        }
    }

    private void updateArmExtensionState() {
        double armPosition = intake.getArm().getPosition();
        isArmExtended = (armPosition == positions_motor.NIntakeArmExtendedFull ||
                armPosition == positions_motor.NIntakeArmExtendedBack);
    }

    public int getAutoState() {
        return autoState;
    }

    public int getCurrentCycle() {
        return currentCycle;
    }
}