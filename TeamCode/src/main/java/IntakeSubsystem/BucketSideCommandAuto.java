package IntakeSubsystem;

import static BucketAuto.BucketSidePaths.*;
import com.arcrobotics.ftclib.command.CommandOpMode;
import com.arcrobotics.ftclib.command.RunCommand;
import com.arcrobotics.ftclib.command.SequentialCommandGroup;
import com.arcrobotics.ftclib.command.WaitUntilCommand;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.PathChain;
import com.pedropathing.util.Constants;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.pedropathing.follower.Follower;

import BucketAuto.BucketPathInitializer;
import BucketAuto.BucketSidePaths;
import pedroPathing.constants.FConstants;
import pedroPathing.constants.LConstants;

@Autonomous(name="BucketSideCommandAuto", group = "Auto Testing")
public class BucketSideCommandAuto extends CommandOpMode {
    public PathChain chain;
    private Follower follower;
    private BucketSideAutoSubsystem bucketSubsystem;

    @Override
    public void initialize() {
        bucketSubsystem = new BucketSideAutoSubsystem(hardwareMap, telemetry);

        Constants.setConstants(FConstants.class, LConstants.class);
        follower = new Follower(hardwareMap);
        follower.setStartingPose(new Pose(9, 111, Math.toRadians(180)));

        BucketPathInitializer.initializePaths(follower);
        chain = BucketPathInitializer.BucketSidePaths;

        if(opModeInInit()) {
            bucketSubsystem.preloadOuttake();
            bucketSubsystem.openIntakeClaw();
            bucketSubsystem.retractIntakeFull();
            bucketSubsystem.closeOuttakeClaw();
        }

        schedule(
                new RunCommand(follower::update),
                new SequentialCommandGroup(
                        new WaitUntilCommand(this::opModeIsActive),

                        // Scoring preload
                        CommandsBucket.setHighBucket(bucketSubsystem),
                        CommandsBucket.sleep(1000),
                        CommandsBucket.followPath(follower, scorePreload),
                        CommandsBucket.sleep(250),
                        CommandsBucket.openOuttakeClaw(bucketSubsystem),
                        CommandsBucket.sleep(500),
                        CommandsBucket.extendIntake(bucketSubsystem),

                        // First cycle - Pickup pixel 1
                        CommandsBucket.followPath(follower, pickUp1Path),
                        CommandsBucket.setViperDown(bucketSubsystem),
                        CommandsBucket.sleep(250),
                        CommandsBucket.pickupAndTransfer(bucketSubsystem)
                                .andThen(CommandsBucket.sleep(250))
                                .andThen(CommandsBucket.setHighBucket(bucketSubsystem)),
                        CommandsBucket.sleep(1500),
                        // Score pixel 1
                        CommandsBucket.followPath(follower, score1Path),
                        CommandsBucket.sleep(250),
                        CommandsBucket.openOuttakeClaw(bucketSubsystem),
                        CommandsBucket.sleep(500),
                        CommandsBucket.extendIntake(bucketSubsystem),


                        // Second cycle - Pickup pixel 2
                        CommandsBucket.followPath(follower, pickUp2Path),
                        CommandsBucket.setViperDown(bucketSubsystem),
                        CommandsBucket.sleep(250),
                        CommandsBucket.pickupAndTransfer(bucketSubsystem)
                                .andThen(CommandsBucket.sleep(250))
                                .andThen(CommandsBucket.setHighBucket(bucketSubsystem)),
                        CommandsBucket.sleep(1500),
                        // Score pixel 2
                        CommandsBucket.followPath(follower, score2Path),
                        CommandsBucket.sleep(250),
                        CommandsBucket.openOuttakeClaw(bucketSubsystem),
                        CommandsBucket.sleep(500),
                        CommandsBucket.extendIntakeCross(bucketSubsystem),


                        // Third cycle - Pickup pixel 3
                        CommandsBucket.followPath(follower, pickUp3Path),
                        CommandsBucket.extendIntakeCross(bucketSubsystem),
                        CommandsBucket.setViperDown(bucketSubsystem),
                        CommandsBucket.sleep(250),
                        CommandsBucket.pickupAndTransferCross(bucketSubsystem)
                                .andThen(CommandsBucket.sleep(250))
                                .andThen(CommandsBucket.setHighBucket(bucketSubsystem)),
                        CommandsBucket.sleep(500),
                        // Score pixel 3
                        CommandsBucket.followPath(follower, score3Path),
                        CommandsBucket.sleep(250),
                        CommandsBucket.openOuttakeClaw(bucketSubsystem),
                        CommandsBucket.sleep(500),
                        CommandsBucket.pickUpPOS(bucketSubsystem),
                        CommandsBucket.setViperDown(bucketSubsystem),
                        CommandsBucket.sleep(1500)
                )
        );
    }
}