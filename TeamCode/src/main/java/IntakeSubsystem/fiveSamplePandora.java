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
import Positions.Commands;
import Subsystem.OuttakeSubsystem;
import pedroPathing.constants.FConstants;
import pedroPathing.constants.LConstants;

@Autonomous(name="fiveSamplePandora", group = "Auto Testing")
public class fiveSamplePandora extends CommandOpMode {
    public PathChain chain;
    private Follower follower;
    private BucketSideAutoSubsystem bucketSubsystem;

    private OuttakeSubsystem outtakeSubsystem;  // Declared as class field


    @Override
    public void initialize() {
        bucketSubsystem = new BucketSideAutoSubsystem(hardwareMap, telemetry);
        this.outtakeSubsystem = new OuttakeSubsystem(hardwareMap, telemetry, this.follower);

        Constants.setConstants(FConstants.class, LConstants.class);
        follower = new Follower(hardwareMap);
        follower.setStartingPose(new Pose(9, 111, Math.toRadians(180)));

        BucketPathInitializer.initializePaths(follower);
        chain = BucketPathInitializer.BucketSidePaths;

        if(opModeInInit()) {
//            bucketSubsystem.preloadOuttake();
            bucketSubsystem.openIntakeClaw();
            bucketSubsystem.bucketAuto();
            bucketSubsystem.closeOuttakeClaw();
            outtakeSubsystem.preloadPosition();
        }

        schedule(
                new RunCommand(follower::update),
                new SequentialCommandGroup(
                        new WaitUntilCommand(this::opModeIsActive),

                        // Scoring preload
                        CommandsBucket.setHighBucket(bucketSubsystem),
                        CommandsBucket.followPath(follower, waitPre),
                        CommandsBucket.sleep(10),
                        CommandsBucket.followPath(follower, scorePreload),
                        CommandsBucket.sleep(100),
                        CommandsBucket.openOuttakeClaw(bucketSubsystem),
                        CommandsBucket.sleep(150),
                        CommandsBucket.extendIntake(bucketSubsystem),

                        // First cycle - Pickup pixel 1
                        CommandsBucket.setViperDown(bucketSubsystem),
                        CommandsBucket.setWaitPOS(bucketSubsystem),
//                        CommandsBucket.sleep(250),
                        CommandsBucket.followPath(follower, pandoraPick),
                        CommandsBucket.sleep(100),
                        CommandsBucket.pickupAndTransferHigher(bucketSubsystem),
                        CommandsBucket.followPath(follower, pandoraScore),
                        CommandsBucket.sleep(150),
                        CommandsBucket.openOuttakeClaw(bucketSubsystem),
                        CommandsBucket.sleep(150),
                        CommandsBucket.extendIntake(bucketSubsystem),
                        CommandsBucket.setWaitPOS(bucketSubsystem),
                        CommandsBucket.setViperDown(bucketSubsystem),
                        CommandsBucket.sleep(100),
                        CommandsBucket.followPath(follower, pickUp1Path),
                        CommandsBucket.sleep(300),
                        CommandsBucket.pickupAndTransferHigher(bucketSubsystem),
                        CommandsBucket.sleep(150),
                        CommandsBucket.followPath(follower, score1Path),
                        CommandsBucket.sleep(250),
                        CommandsBucket.openOuttakeClaw(bucketSubsystem),
                        CommandsBucket.sleep(150),
                        CommandsBucket.extendIntake(bucketSubsystem),


                        // Second cycle - Pickup pixel 2
                        CommandsBucket.setWaitPOS(bucketSubsystem),
                        CommandsBucket.setViperDown(bucketSubsystem),
                        CommandsBucket.sleep(100),
                        CommandsBucket.followPath(follower, pickUp2Path),
                        CommandsBucket.sleep(100),
                        CommandsBucket.pickupAndTransferHigher(bucketSubsystem),
                        CommandsBucket.sleep(150),
                        CommandsBucket.followPath(follower, score2Path),
                        CommandsBucket.sleep(150),
                        CommandsBucket.openOuttakeClaw(bucketSubsystem),
                        CommandsBucket.sleep(150),
                        CommandsBucket.extendIntakeCross(bucketSubsystem),


                        // Third cycle - Pickup pixel 3
                        CommandsBucket.setWaitPOS(bucketSubsystem),
                        CommandsBucket.setViperDown(bucketSubsystem),
                        CommandsBucket.followPath(follower, pickUpPath3PRE),
                        CommandsBucket.sleep(50),
                        CommandsBucket.followPath(follower, pickUp3Path),
                        CommandsBucket.sleep(100),
                        CommandsBucket.justPickUPCross(bucketSubsystem),
                        CommandsBucket.sleep(150),
                        CommandsBucket.followPath(follower, pickUpPath3Grab),
                        CommandsBucket.sleep(50),
                        CommandsBucket.pivotHorizontal(bucketSubsystem),
                        CommandsBucket.sleep(100),
                        CommandsBucket.wristUp(bucketSubsystem),
                        CommandsBucket.sleep(200),
                        CommandsBucket.justTransferCross(bucketSubsystem),
                        CommandsBucket.followPath(follower, score3Path),
                        CommandsBucket.sleep(150),
                        CommandsBucket.openOuttakeClaw(bucketSubsystem),
                        CommandsBucket.sleep(150),
                        CommandsBucket.setWaitPOS(bucketSubsystem),
                        CommandsBucket.setViperDown(bucketSubsystem),
                        CommandsBucket.followPath(follower, parkPath),
                        CommandsBucket.sleep(500),
                        Commands.scoreSpecimen(outtakeSubsystem),
                        CommandsBucket.sleep(1500)
                )
        );
    }
}