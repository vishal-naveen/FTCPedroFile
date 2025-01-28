package specimenPLUS;

import static specimenPLUS.specimenPLUS_PATHS.blueLineDirect;
import static specimenPLUS.specimenPLUS_PATHS.blueLineUpToPushBlock1;
import static specimenPLUS.specimenPLUS_PATHS.pickUpToScoreBefore2;
import static specimenPLUS.specimenPLUS_PATHS.pickUpToScoreBefore3;
import static specimenPLUS.specimenPLUS_PATHS.pickUpToScoreBefore4;
import static specimenPLUS.specimenPLUS_PATHS.pickUpToScoreBefore5;
import static specimenPLUS.specimenPLUS_PATHS.preloadBeforePath;
import static specimenPLUS.specimenPLUS_PATHS.pushBlock1ToPushBlock2;
import static specimenPLUS.specimenPLUS_PATHS.pushBlock2ToPushBlock3;
import static specimenPLUS.specimenPLUS_PATHS.pushBlock3ToPickUp;
import static specimenPLUS.specimenPLUS_PATHS.pushToScoreBefore1;
import static specimenPLUS.specimenPLUS_PATHS.score1ToPickUp;
import static specimenPLUS.specimenPLUS_PATHS.score2ToPickUp;
import static specimenPLUS.specimenPLUS_PATHS.score3ToPickUp;
import static specimenPLUS.specimenPLUS_PATHS.score4ToPickUp;
import static specimenPLUS.specimenPLUS_PATHS.score5ToPickUp;
import static specimenPLUS.specimenPLUS_PATHS.scoreBefore1ToScore1;
import static specimenPLUS.specimenPLUS_PATHS.scoreBefore2ToScore2;
import static specimenPLUS.specimenPLUS_PATHS.scoreBefore3ToScore3;
import static specimenPLUS.specimenPLUS_PATHS.scoreBefore4ToScore4;
import static specimenPLUS.specimenPLUS_PATHS.scoreBefore5ToScore5;
import static specimenPLUS.specimenPLUS_PATHS.scorePreload;

import com.arcrobotics.ftclib.command.CommandOpMode;
import com.arcrobotics.ftclib.command.RunCommand;
import com.arcrobotics.ftclib.command.SequentialCommandGroup;
import com.arcrobotics.ftclib.command.WaitUntilCommand;
import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.PathChain;
import com.pedropathing.util.Constants;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import IntakeSubsystem.BucketSideAutoSubsystem;
import IntakeSubsystem.CommandsBucket;
import Positions.Commands;
import Subsystem.OuttakeSubsystem;
import Subsystem.PathsPush3;
import pedroPathing.constants.FConstants;
import pedroPathing.constants.LConstants;

@Autonomous(name="specimenPLUSMAIN", group = "Auto Testing")
public class specimenPLUS_MAIN extends CommandOpMode {
    public PathChain chain;
    public Follower follower;
    private OuttakeSubsystem outtakeSubsystem;

    private BucketSideAutoSubsystem bucketSubsystem;



    @Override
    public void initialize() {
        this.outtakeSubsystem = new OuttakeSubsystem(hardwareMap, telemetry);
        this.bucketSubsystem = new BucketSideAutoSubsystem(hardwareMap, telemetry);

        Constants.setConstants(FConstants.class, LConstants.class);
        this.follower = new Follower(hardwareMap);
        this.follower.setStartingPose(new Pose(9.4, 81.1, Math.toRadians(0)));

        specimenpluscha.initializePaths(follower);
        this.chain = specimenpluscha.specimenPlus;

        if(opModeInInit()) {
            outtakeSubsystem.closeClaw();
            outtakeSubsystem.preloadPosition();
//            bucketSubsystem.armBack();
        }

        schedule(
                new RunCommand(follower::update),
                new SequentialCommandGroup(
                        new WaitUntilCommand(this::opModeIsActive),

                        Commands.followPath(follower, blueLineDirect),
                        Commands.followPath(follower, blueLineUpToPushBlock1)
                                .andThen(Commands.closeClawThenScore(outtakeSubsystem)),
                        Commands.followPath(follower, pushBlock1ToPushBlock2)
                                .andThen(Commands.pickUpSpecimen(outtakeSubsystem)),
                        Commands.followPath(follower, pushBlock2ToPushBlock3),

                        Commands.pickUpSpecimen(outtakeSubsystem)
                                .andThen(Commands.followPath(follower, pushBlock3ToPickUp)),

                        Commands.sleep(10)
                                .andThen(Commands.closeClawThenScore(outtakeSubsystem))
                                .andThen(Commands.followPath(follower, pushToScoreBefore1))
                                .andThen(Commands.sleep(10))
                                .andThen(Commands.flick(outtakeSubsystem)),
                        Commands.followPath(follower, scoreBefore1ToScore1).withTimeout(2000)
//                                .raceWith(time>100)
                                .andThen(Commands.openClaw(outtakeSubsystem)),



                        Commands.pickUpSpecimen(outtakeSubsystem)
                                        .andThen(Commands.followPath(follower, score1ToPickUp)),

                        // Second scoring sequence
                        Commands.sleep(10)
                                .andThen(Commands.closeClawThenScore(outtakeSubsystem))
                                .andThen(Commands.followPath(follower, pickUpToScoreBefore2))
                                .andThen(Commands.sleep(50))
                                .andThen(Commands.flick(outtakeSubsystem))
                                .andThen(Commands.followPath(follower, scoreBefore2ToScore2).withTimeout(2000))
                                .andThen(Commands.openClaw(outtakeSubsystem)),

                        Commands.pickUpSpecimen(outtakeSubsystem)
                                .andThen(Commands.followPath(follower, score2ToPickUp)),

                        // Third scoring sequence
                        Commands.sleep(10)
                                .andThen(Commands.closeClawThenScore(outtakeSubsystem))
                                .andThen(Commands.followPath(follower, pickUpToScoreBefore3))
                                .andThen(Commands.flick(outtakeSubsystem))
                                .andThen(Commands.followPath(follower, scoreBefore3ToScore3).withTimeout(2000))
                                .andThen(Commands.openClaw(outtakeSubsystem)),

                        Commands.pickUpSpecimen(outtakeSubsystem)
                                .andThen(Commands.followPath(follower, score3ToPickUp)),

                        Commands.sleep(10)
                                .andThen(Commands.closeClawThenScore(outtakeSubsystem))
                                .andThen(Commands.followPath(follower, pickUpToScoreBefore4))
                                .andThen(Commands.flick(outtakeSubsystem))
                                .andThen(Commands.followPath(follower, scoreBefore4ToScore4).withTimeout(2000))
                                .andThen(Commands.openClaw(outtakeSubsystem)),

                        Commands.pickUpSpecimen(outtakeSubsystem)
                                .andThen(Commands.followPath(follower, score4ToPickUp)),

                        Commands.sleep(10)
                                .andThen(Commands.closeClawThenScore(outtakeSubsystem))
                                .andThen(Commands.followPath(follower, pickUpToScoreBefore5))
                                .andThen(Commands.flick(outtakeSubsystem))
                                .andThen(Commands.followPath(follower, scoreBefore5ToScore5).withTimeout(2000))
                                .andThen(Commands.openClaw(outtakeSubsystem)),

                        Commands.pickUpSpecimen(outtakeSubsystem)
                                .andThen(Commands.followPath(follower, score5ToPickUp))
//                                .alongWith(CommandsBucket.extendIntakeFull(bucketSubsystem, BucketSideAutoSubsystem.HoverOrientation.HORIZONTAL))
                )
        );
    }
}