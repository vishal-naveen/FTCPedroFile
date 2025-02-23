package RedSide5th;


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
import RedSide5th.REDPush3Specimen;
import Subsystem.OuttakeSubsystem;
import Subsystem.PathsPush3;
import RedSide5th.REDPush3Specimen;
import Subsystem.PathsPush3Chain;
import Positions.Commands;
import pedroPathing.constants.FConstants;
import pedroPathing.constants.LConstants;
import static RedSide5th.REDPush3Specimen.*;


@Autonomous(name="RedSideSpecimen", group = "Auto Testing")
public class RedSideSpecimen extends CommandOpMode {
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

        // Initialize paths once and store the chain
        this.chain = REDPathsPush3.initializePaths(follower);

        // Import static path references

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

                        Commands.followPath(follower, preloadBeforePathPRE)
                                .andThen(Commands.sleep(10))
                                .andThen(Commands.closeClawThenScorePreload(outtakeSubsystem))
                                .andThen(Commands.sleep(600))
                                .andThen(Commands.followPath(follower, preloadBeforePath))
                                .andThen(Commands.flick(outtakeSubsystem))
                                .andThen(Commands.followPath(follower, scorePreload).withTimeout(300))
                                .andThen(Commands.openClaw(outtakeSubsystem)),


                        Commands.followPath(follower, blueLineDirect)
                                .andThen(Commands.pickUpPOS(outtakeSubsystem)),
                        Commands.followPath(follower, blueLineUpToPushBlock1),
                        Commands.followPath(follower, pushBlock1ToPushBlock2Up),
                        Commands.followPath(follower, pushBlock2UpToPushBlock2),
                        Commands.followPath(follower, pushBlock2ToPushBlock3Up)
                                .withTimeout(3000),
                        Commands.sleep(10),
                        Commands.followPath(follower, pushBlock3UpToPushBlock3)
                                .withTimeout(3000),
                        Commands.sleep(10),

                        // First scoring sequence

//                        Commands.pickUpSpecimen(outtakeSubsystem)
//                                .andThen(Commands.followPath(follower, pushBlock3ToPickUp)),

//                        Commands.sleep(10)
//                                .andThen(Commands.closeClawThenScore(outtakeSubsystem))
                        Commands.closeClawThenScore(outtakeSubsystem)
                                .andThen(Commands.followPath(follower, pushToScoreBefore1))
//                                .andThen(Commands.sleep(10))
                                .andThen(Commands.flick(outtakeSubsystem)),
                        Commands.followPath(follower, scoreBefore1ToScore1).withTimeout(300)
//                                .raceWith(time>100)
                                .andThen(Commands.openClaw(outtakeSubsystem)),



                        Commands.pickUpPOS(outtakeSubsystem)
                                .andThen(Commands.followPath(follower, score1ToPickUp)),

                        // Second scoring sequence
                        Commands.sleep(10)
                                .andThen(Commands.closeClawThenScore(outtakeSubsystem))
                                .andThen(Commands.followPath(follower, pickUpToScoreBefore2))
//                                .andThen(Commands.sleep(50))
                                .andThen(Commands.flick(outtakeSubsystem))
                                .andThen(Commands.followPath(follower, scoreBefore2ToScore2).withTimeout(300))
                                .andThen(Commands.openClaw(outtakeSubsystem)),

                        Commands.pickUpPOS(outtakeSubsystem)
                                .andThen(Commands.followPath(follower, score2ToPickUp)),

                        // Third scoring sequence
                        Commands.sleep(10)
                                .andThen(Commands.closeClawThenScore(outtakeSubsystem))
                                .andThen(Commands.followPath(follower, pickUpToScoreBefore3))
                                .andThen(Commands.flick(outtakeSubsystem))
                                .andThen(Commands.followPath(follower, scoreBefore3ToScore3).withTimeout(300))
                                .andThen(Commands.openClaw(outtakeSubsystem)),

                        Commands.pickUpPOS(outtakeSubsystem)
                                .andThen(Commands.followPath(follower, score3ToPickUp)),

                        Commands.sleep(10)
                                .andThen(Commands.closeClawThenScore(outtakeSubsystem))
                                .andThen(Commands.followPath(follower, pickUpToScoreBefore4))
                                .andThen(Commands.flick(outtakeSubsystem))
                                .andThen(Commands.followPath(follower, scoreBefore4ToScore4).withTimeout(300))
                                .andThen(Commands.openClaw(outtakeSubsystem)),


                        Commands.pickUpPOS(outtakeSubsystem)
                                .andThen(Commands.followPath(follower, score4ToPickUp))
                                .alongWith(CommandsBucket.extendIntake(bucketSubsystem))
                )
        );
    }
}