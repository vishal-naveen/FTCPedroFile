package specimenPLUS;

import static specimenPLUS.specimenPLUS_PATHS.*;


import com.arcrobotics.ftclib.command.CommandOpMode;
import com.arcrobotics.ftclib.command.RunCommand;
import com.arcrobotics.ftclib.command.SequentialCommandGroup;
import com.arcrobotics.ftclib.command.WaitUntilCommand;
import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.PathChain;
import com.pedropathing.util.Constants;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import java.net.ConnectException;

import IntakeSubsystem.BucketSideAutoSubsystem;
import IntakeSubsystem.CommandsBucket;
import Positions.Commands;
import Subsystem.OuttakeSubsystem;
import Subsystem.PathsPush3;
import Subsystem.Push3Specimen;
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
        this.follower.setStartingPose(new Pose(8.4, 86.9, Math.toRadians(0)));

        specimenpluscha.initializePaths(follower);
        this.chain = specimenpluscha.specimenPlus;

        if(opModeInInit()) {
            outtakeSubsystem.closeClaw();
            outtakeSubsystem.preloadPosition();
            bucketSubsystem.retractSpecimenInit();
        }

        schedule(
                new RunCommand(follower::update),
                new SequentialCommandGroup(
                        new WaitUntilCommand(this::opModeIsActive),

                        Commands.followPath(follower, blueLineDirect)
                                .andThen(Commands.scoreSpecimen(outtakeSubsystem)),
                        Commands.followPath(follower, blueLineUpToPushBlock1),
                        Commands.followPath(follower, pushBlock1ToPushBlock2Up)
                                .andThen(Commands.pickUpPOS(outtakeSubsystem))
                                .andThen(Commands.openClaw(outtakeSubsystem)),
                        Commands.followPath(follower, pushBlock2UpToPushBlock2),
                        Commands.sleep(5),
                        Commands.followPath(follower, pushBlock2ToPushBlock3Up)
                                .withTimeout(3000),
                        Commands.sleep(5),
                        Commands.followPath(follower, pushBlock3UpToPushBlock3)
                                .withTimeout(3000),

                        Commands.closeClawThenScore(outtakeSubsystem)
                                .andThen(Commands.followPath(follower, pushToScoreBefore1))
                                .andThen(Commands.flick(outtakeSubsystem)),
                        Commands.followPath(follower, scoreBefore1ToScore1).withTimeout(300)
                                .andThen(Commands.openClaw(outtakeSubsystem)),


                        Commands.pickUpPOS(outtakeSubsystem)
                                .andThen(Commands.followPath(follower, score1ToPickUp)),

                        // Second scoring sequence
                        Commands.closeClawThenScore(outtakeSubsystem)
                                .andThen(Commands.followPath(follower, pickUpToScoreBefore2))
                                .andThen(Commands.flick(outtakeSubsystem))
                                .andThen(Commands.followPath(follower, scoreBefore2ToScore2).withTimeout(300))
                                .andThen(Commands.openClaw(outtakeSubsystem)),

                        Commands.pickUpPOS(outtakeSubsystem)
                                .andThen(Commands.followPath(follower, score2ToPickUp)),

                        // Third scoring sequence
                        Commands.closeClawThenScore(outtakeSubsystem)
                                .andThen(Commands.followPath(follower, pickUpToScoreBefore3))
                                .andThen(Commands.flick(outtakeSubsystem))
                                .andThen(Commands.followPath(follower, scoreBefore3ToScore3).withTimeout(300))
                                .andThen(Commands.openClaw(outtakeSubsystem)),

                        Commands.pickUpPOS(outtakeSubsystem)
                                .andThen(Commands.followPath(follower, score3ToPickUp)),

                        Commands.closeClawThenScore(outtakeSubsystem)
                                .andThen(Commands.followPath(follower, pickUpToScoreBefore4))
                                .andThen(Commands.flick(outtakeSubsystem))
                                .andThen(Commands.followPath(follower, scoreBefore4ToScore4).withTimeout(300))
                                .andThen(Commands.openClaw(outtakeSubsystem)),

                        Commands.pickUpPOS(outtakeSubsystem)
                                .andThen(Commands.followPath(follower, score4ToPickUp)),

                        Commands.closeClawThenScore(outtakeSubsystem)
                                .andThen(Commands.followPath(follower, pickUpToScoreBefore5))
                                .andThen(Commands.flick(outtakeSubsystem))
                                .andThen(Commands.followPath(follower, scoreBefore5ToScore5).withTimeout(300))
                                .andThen(Commands.openClaw(outtakeSubsystem)),

                        Commands.pickUpPOS(outtakeSubsystem)
                                .andThen(Commands.followPath(follower, score5ToPickUp))
                )
        );
    }
}