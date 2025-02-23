package Auto;

import static Subsystem.Push3Specimen.*;

import com.arcrobotics.ftclib.command.CommandOpMode;
import com.arcrobotics.ftclib.command.RunCommand;
import com.arcrobotics.ftclib.command.SequentialCommandGroup;
import com.arcrobotics.ftclib.command.WaitUntilCommand;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.PathChain;
import com.pedropathing.util.Constants;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.pedropathing.follower.Follower;

import IntakeSubsystem.BucketSideAutoSubsystem;
import IntakeSubsystem.CommandsBucket;
import Subsystem.OuttakeSubsystem;
import Subsystem.PathsPush3;
import Subsystem.PathsPush3Chain;
import Positions.Commands;
import pedroPathing.constants.FConstants;
import pedroPathing.constants.LConstants;

@Autonomous(name="directScoringTestPushCommandBase", group = "Auto Testing")
public class directScoringTestPushCommandBase extends CommandOpMode {
    public PathChain chain;
    public Follower follower;
    private OuttakeSubsystem outtakeSubsystem;

    private BucketSideAutoSubsystem bucketSubsystem;



    @Override
    public void initialize() {
        this.outtakeSubsystem = new OuttakeSubsystem(hardwareMap, telemetry, this.follower);
        this.bucketSubsystem = new BucketSideAutoSubsystem(hardwareMap, telemetry);

        Constants.setConstants(FConstants.class, LConstants.class);
        this.follower = new Follower(hardwareMap);
        this.follower.setStartingPose(new Pose(8.4, 62.8, Math.toRadians(0)));

        PathsPush3.initializePaths(follower);
        this.chain = PathsPush3.Push3Specimen;

        if(opModeInInit()) {
            outtakeSubsystem.closeClaw();
            outtakeSubsystem.preloadPosition();
            bucketSubsystem.retractSpecimenInit();
        }

        schedule(
                new RunCommand(follower::update),
                new SequentialCommandGroup(
                        new WaitUntilCommand(this::opModeIsActive),
                        // Preload scoring sequence
//                        Commands.closeClawThenScorePreload(outtakeSubsystem)
//                                .andThen(Commands.followPath(follower, preloadBeforePathPRE))
//                                .andThen(Commands.flick(outtakeSubsystem))
//                                .andThen(Commands.followPath(follower, scorePreload).withTimeout(300))
//                                .andThen(Commands.openClaw(outtakeSubsystem)),
                        Commands.closeClawThenScorePreload(outtakeSubsystem)
                                .andThen(Commands.followPath(follower, scorePreload).withTimeout(2500))
                                .andThen(Commands.flick(outtakeSubsystem))
                                .andThen(Commands.openClaw(outtakeSubsystem)),


                        Commands.followPath(follower, blueLineDirect)
                                .andThen(Commands.pickUpPOS(outtakeSubsystem)),
                        Commands.followPath(follower, blueLineUpToPushBlock1),
//                        CommandsBucket.armWallLength(bucketSubsystem),
                        Commands.followPath(follower, pushBlock1ToPushBlock2Up),
                        Commands.followPath(follower, pushBlock2UpToPushBlock2),
                        Commands.followPath(follower, pushBlock2ToPushBlock3Up),
                        Commands.followPath(follower, pushBlock3UpToPushBlock3),
                        Commands.followPath(follower, pushBlock3ToFinal)
                                .withTimeout(200),

                        // First scoring sequence

//                        Commands.pickUpSpecimen(outtakeSubsystem)
//                                .andThen(Commands.followPath(follower, pushBlock3ToPickUp)),

//                        Commands.sleep(10)
//                                .andThen(Commands.closeClawThenScore(outtakeSubsystem))
                        Commands.closeClawThenScore(outtakeSubsystem)
                                .andThen(Commands.followPath(follower, scoreBefore1ToScore1).withTimeout(2500))
                                .andThen(Commands.flick(outtakeSubsystem))
                                .andThen(Commands.openClaw(outtakeSubsystem)),



                        Commands.pickUpPOS(outtakeSubsystem)
                                .andThen(Commands.followPath(follower, score1ToPickUpBefore).withTimeout(1500))
//                                .andThen(Commands.setMaxPower(outtakeSubsystem,0.2))
                                .andThen(Commands.followPath(follower, score1ToPickUp).withTimeout(100)),
//                                .andThen(Commands.setMaxPower(outtakeSubsystem,1)),


                        Commands.closeClawThenScore(outtakeSubsystem)
                                .andThen(Commands.followPath(follower, scoreBefore2ToScore2).withTimeout(2500))
                                .andThen(Commands.flick(outtakeSubsystem))
                                .andThen(Commands.openClaw(outtakeSubsystem)),

                        Commands.pickUpPOS(outtakeSubsystem)
                                .andThen(Commands.followPath(follower, score2ToPickUpBefore).withTimeout(1500))
//                                .andThen(Commands.setMaxPower(outtakeSubsystem,0.2))
                                .andThen(Commands.followPath(follower, score2ToPickUp).withTimeout(100)),
//                                .andThen(Commands.setMaxPower(outtakeSubsystem,1)),

                        // Third scoring sequence
                        Commands.closeClawThenScore(outtakeSubsystem)
                                .andThen(Commands.followPath(follower, scoreBefore3ToScore3).withTimeout(2500))
                                .andThen(Commands.flick(outtakeSubsystem))
                                .andThen(Commands.openClaw(outtakeSubsystem)),

                        Commands.pickUpPOS(outtakeSubsystem)
                                .andThen(Commands.followPath(follower, score3ToPickUpBefore).withTimeout(1500))
//                                .andThen(Commands.setMaxPower(outtakeSubsystem,0.2))
                                .andThen(Commands.followPath(follower, score3ToPickUp).withTimeout(100)),
//                                .andThen(Commands.setMaxPower(outtakeSubsystem,1)),

                        Commands.closeClawThenScore(outtakeSubsystem)
                                .andThen(Commands.followPath(follower, scoreBefore4ToScore4).withTimeout(2500))
                                .andThen(Commands.flick(outtakeSubsystem))
                                .andThen(Commands.openClaw(outtakeSubsystem)),


                        Commands.pickUpPOS(outtakeSubsystem)
                                .andThen(Commands.followPath(follower, score4ToPickUp))
                                .alongWith(CommandsBucket.extendIntake(bucketSubsystem))
                )
        );
    }
}