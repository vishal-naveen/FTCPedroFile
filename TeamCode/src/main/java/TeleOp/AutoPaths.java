package TeleOp;

import com.arcrobotics.ftclib.command.CommandBase;
import com.arcrobotics.ftclib.command.SequentialCommandGroup;
import com.arcrobotics.ftclib.command.WaitCommand;
import com.arcrobotics.ftclib.command.InstantCommand;
import com.arcrobotics.ftclib.command.Command;
import com.arcrobotics.ftclib.command.SubsystemBase;
import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.Path;
import com.pedropathing.pathgen.BezierCurve;
import com.pedropathing.pathgen.Point;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.Telemetry;

public class AutoPaths extends SubsystemBase {
    private final Follower follower;
    private final AnalogInput ultra;
    private final Telemetry telemetry;
    private Path scorePath;
    private Path pickPath;
    private boolean autoSequenceActive = false;
    private int currentCycle = 0;
    private int maxCycles = 3;

    public AutoPaths(HardwareMap hardwareMap, Follower follower, Telemetry telemetry) {
        this.follower = follower;
        this.ultra = hardwareMap.get(AnalogInput.class, "ultra");
        this.telemetry = telemetry;
        initializePaths();
    }

    private void initializePaths() {
        Pose startPose = new Pose(30, 0, Math.toRadians(0));
        Pose scorePose = new Pose(0, 0, Math.toRadians(0));

        scorePath = new Path(new BezierCurve(new Point(startPose), new Point(scorePose)));
        scorePath.setConstantHeadingInterpolation(startPose.getHeading());
        pickPath = new Path(new BezierCurve(new Point(scorePose), new Point(startPose)));
        pickPath.setConstantHeadingInterpolation(startPose.getHeading());
    }

    public double getDistanceInches() {
        return (100 * (ultra.getVoltage() / 3.3)) / 2.54;
    }

    public double getRawVoltage() {
        return ultra.getVoltage();
    }

    public boolean isActive() {
        return autoSequenceActive;
    }

    public void setActive(boolean active) {
        this.autoSequenceActive = active;
    }

    public int getCurrentCycle() {
        return currentCycle;
    }

    public void resetCycle() {
        currentCycle = 0;
    }

    public void incrementCycle() {
        currentCycle++;
    }

    @Override
    public void periodic() {
        telemetry.addData("Auto Active", isActive());
        telemetry.addData("Current Cycle", getCurrentCycle());
        telemetry.addData("Distance", "%.2f", getDistanceInches());
    }

    public static class FollowPathCommand extends CommandBase {
        private final Follower follower;
        private final Path path;
        private final AutoPaths subsystem;
        private final ElapsedTime timer = new ElapsedTime();
        private Double timeout = null;

        public FollowPathCommand(AutoPaths subsystem, Path path) {
            this.subsystem = subsystem;
            this.follower = subsystem.follower;
            this.path = path;
            addRequirements(subsystem);
        }

        public FollowPathCommand withTimeout(double timeoutMs) {
            this.timeout = timeoutMs;
            return this;
        }

        @Override
        public void initialize() {
            follower.followPath(path);
            timer.reset();
        }

        @Override
        public void execute() {
            follower.update();
        }

        @Override
        public boolean isFinished() {
            if (timeout != null && timer.milliseconds() >= timeout) {
                return true;
            }
            return !follower.isBusy() || subsystem.getDistanceInches() <= 10;
        }

        @Override
        public void end(boolean interrupted) {
            if (interrupted) {
                follower.breakFollowing();
            }
        }
    }

    public class StartAutoCommand extends InstantCommand {
        public StartAutoCommand() {
            super(() -> {
                setActive(true);
                resetCycle();
                follower.startTeleopDrive();
            }, AutoPaths.this);
        }
    }

    public class StopAutoCommand extends InstantCommand {
        public StopAutoCommand() {
            super(() -> {
                setActive(false);
                resetCycle();
                follower.breakFollowing();
                follower.startTeleopDrive();
            }, AutoPaths.this);
        }
    }

    public Command getScoreCycleCommand() {
        return new SequentialCommandGroup(
                new FollowPathCommand(this, scorePath).withTimeout(3000),
                new WaitCommand(100),
                new FollowPathCommand(this, pickPath).withTimeout(3000),
                new InstantCommand(this::incrementCycle),
                new InstantCommand(() -> {
                    if (getCurrentCycle() >= maxCycles) {
                        new StopAutoCommand().schedule();
                    }
                })
        );
    }

    public Command getManualDriveCommand(double x, double y, double rotate, double power) {
        return new InstantCommand(() -> {
            if (!isActive()) {
                follower.setTeleOpMovementVectors(
                        y * power,
                        x * power,
                        rotate * power,
                        true
                );
            }
        });
    }

    public Command getResetHeadingCommand() {
        return new InstantCommand(() -> {
            follower.setCurrentPoseWithOffset(new Pose(
                    follower.getPose().getX(),
                    follower.getPose().getY(),
                    Math.toRadians(0)
            ));
        });
    }
}