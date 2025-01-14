package IntakeSubsystem;

import static BucketAuto.BucketSidePaths.scorePreload;
import static BucketAuto.BucketSidePaths.pickUp1Path;
import static BucketAuto.BucketSidePaths.score1Path;
import static BucketAuto.BucketSidePaths.pickUp2Path;
import static BucketAuto.BucketSidePaths.score2Path;
import static BucketAuto.BucketSidePaths.pickUp3Path;
import static BucketAuto.BucketSidePaths.score3Path;
import static BucketAuto.BucketSidePaths.parkPath;

import com.arcrobotics.ftclib.command.CommandOpMode;
import com.arcrobotics.ftclib.command.RunCommand;
import com.arcrobotics.ftclib.command.SequentialCommandGroup;
import com.arcrobotics.ftclib.command.WaitUntilCommand;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.PathChain;
import com.pedropathing.util.Constants;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.pedropathing.follower.Follower;

import BucketAuto.BucketSidePaths;
import Positions.RobotPose;
import pedroPathing.constants.FConstants;
import pedroPathing.constants.LConstants;
import IntakeSubsystem.BucketSideAutoSubsystem.HoverOrientation;

@Autonomous(name="BucketSideCommandAuto", group = "Auto Testing")
public class BucketSideCommandAuto extends CommandOpMode {
    public PathChain chain;
    public Follower follower;
    private BucketSideAutoSubsystem bucketSubsystem;

    @Override
    public void initialize() {
        // Initialize hardware and subsystems
        this.bucketSubsystem = new BucketSideAutoSubsystem(hardwareMap, telemetry);

        // Initialize follower and paths
        Constants.setConstants(FConstants.class, LConstants.class);
        this.follower = new Follower(hardwareMap);
        this.follower.setStartingPose(new Pose(9, 111, Math.toRadians(180)));
        BucketSidePaths bucketPaths = new BucketSidePaths(follower);
        this.chain = bucketPaths.paths();

        // Initialize robot state during init
        if(opModeInInit()) {
            bucketSubsystem.setOuttakeToTransferPosition();
            bucketSubsystem.openIntakeClaw();
            bucketSubsystem.startIntakeOnly();
            bucketSubsystem.closeOuttakeClaw();
        }

        // Schedule the autonomous routine
        schedule(
                new RunCommand(follower::update),
                new SequentialCommandGroup(
                        new WaitUntilCommand(this::opModeIsActive),

                        // Preload scoring sequence
                        CommandsBucket.setOuttakeToHighBucket(bucketSubsystem)
                                .andThen(CommandsBucket.sleep(1000))
                                .andThen(CommandsBucket.followPath(follower, scorePreload))
                                .andThen(CommandsBucket.sleep(500))
                                .andThen(CommandsBucket.openOuttakeClaw(bucketSubsystem))
                                .andThen(CommandsBucket.sleep(2000))
                                .andThen(CommandsBucket.setOuttakeToTransferPosition(bucketSubsystem)),

                        // First pickup and score cycle
                        CommandsBucket.followPath(follower, pickUp1Path)
                                .alongWith(CommandsBucket.extendIntakeFull(bucketSubsystem, HoverOrientation.HORIZONTAL)),
                        CommandsBucket.startIntakePickupSequence(bucketSubsystem)
                                .andThen(CommandsBucket.sleep(500))
                                .andThen(CommandsBucket.startFullOuttakeTransferSequence(bucketSubsystem))
                                .andThen(CommandsBucket.followPath(follower, score1Path))
                                .andThen(CommandsBucket.sleep(1000))
                                .andThen(CommandsBucket.openOuttakeClaw(bucketSubsystem))
                                .andThen(CommandsBucket.sleep(500))
                                .andThen(CommandsBucket.setOuttakeToTransferPosition(bucketSubsystem)),

                        // Second pickup and score cycle
                        CommandsBucket.followPath(follower, pickUp2Path)
                                .alongWith(CommandsBucket.extendIntakeFull(bucketSubsystem, HoverOrientation.HORIZONTAL)),
                        CommandsBucket.startIntakePickupSequence(bucketSubsystem)
                                .andThen(CommandsBucket.sleep(500))
                                .andThen(CommandsBucket.startFullOuttakeTransferSequence(bucketSubsystem))
                                .andThen(CommandsBucket.followPath(follower, score2Path))
                                .andThen(CommandsBucket.sleep(1000))
                                .andThen(CommandsBucket.openOuttakeClaw(bucketSubsystem))
                                .andThen(CommandsBucket.sleep(500))
                                .andThen(CommandsBucket.setOuttakeToTransferPosition(bucketSubsystem)),

                        // Third pickup and score cycle
                        CommandsBucket.followPath(follower, pickUp3Path)
                                .alongWith(CommandsBucket.extendIntakeFull(bucketSubsystem, HoverOrientation.SLANT_FORWARD)),
                        CommandsBucket.startIntakePickupSequence(bucketSubsystem)
                                .andThen(CommandsBucket.sleep(500))
                                .andThen(CommandsBucket.startFullOuttakeTransferSequence(bucketSubsystem))
                                .andThen(CommandsBucket.followPath(follower, score3Path))
                                .andThen(CommandsBucket.sleep(1000))
                                .andThen(CommandsBucket.openOuttakeClaw(bucketSubsystem))
                                .andThen(CommandsBucket.sleep(500))
                                .andThen(CommandsBucket.setOuttakeToTransferPosition(bucketSubsystem)),

                        // Park
                        CommandsBucket.followPath(follower, parkPath)
                                .alongWith(CommandsBucket.outtakePark(bucketSubsystem)),
                        new RunCommand(() -> RobotPose.stopPose = follower.getPose())
                )
        );
    }

}