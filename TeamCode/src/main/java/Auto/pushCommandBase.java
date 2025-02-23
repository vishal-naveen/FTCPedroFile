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

@Autonomous(name="pushCommandBase15", group = "Auto Testing")
public class pushCommandBase extends CommandOpMode {
    public PathChain chain;
    public Follower follower;
    private OuttakeSubsystem outtakeSubsystem;
    private BucketSideAutoSubsystem bucketSubsystem;

    private double slowPower = 0.2;

    @Override
    public void initialize() {
        this.follower = new Follower(hardwareMap);
        this.outtakeSubsystem = new OuttakeSubsystem(hardwareMap, telemetry, this.follower);
        this.bucketSubsystem = new BucketSideAutoSubsystem(hardwareMap, telemetry);

        Constants.setConstants(FConstants.class, LConstants.class);
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
                        Commands.closeClawThenScorePreload(outtakeSubsystem)
                                .andThen(Commands.followPath(follower, preloadBeforePath))
                                .andThen(Commands.flick(outtakeSubsystem))
                                .andThen(Commands.followPath(follower, scorePreload).withTimeout(300))
                                .andThen(Commands.openClaw(outtakeSubsystem)),

                        Commands.followPath(follower, blueLineDirect)
                                .andThen(Commands.pickUpPOS(outtakeSubsystem)),
                        Commands.followPath(follower, blueLineUpToPushBlock1),
                        Commands.followPath(follower, pushBlock1ToPushBlock2Up),
                        Commands.followPath(follower, pushBlock2UpToPushBlock2),
                        Commands.followPath(follower, pushBlock2ToPushBlock3Up),
                        Commands.followPath(follower, pushBlock3UpToPushBlock3)
                                .withTimeout(2000),
//                        Commands.setMaxPower(outtakeSubsystem, slowPower),
                        Commands.followPath(follower, pushBlock3ToFinal)
                                .withTimeout(100),
//                        Commands.setMaxPower(outtakeSubsystem, 1),

                        Commands.closeClawThenScoreCorner(outtakeSubsystem)
                                .andThen(Commands.followPath(follower, pushToScoreBefore1))
                                .andThen(Commands.flick(outtakeSubsystem)),
                        Commands.followPath(follower, scoreBefore1ToScore1).withTimeout(300)
                                .andThen(Commands.openClaw(outtakeSubsystem)),

                        Commands.pickUpPOS(outtakeSubsystem)
                                .andThen(Commands.followPath(follower, score1ToPickUpBefore).withTimeout(1500))
//                                .andThen(Commands.setMaxPower(outtakeSubsystem, slowPower))
                                .andThen(Commands.followPath(follower, score1ToPickUp).withTimeout(100)),
//                                .andThen(Commands.setMaxPower(outtakeSubsystem, 1)),

                        Commands.closeClawThenScore(outtakeSubsystem)
                                .andThen(Commands.followPath(follower, pickUpToScoreBefore2))
                                .andThen(Commands.flick(outtakeSubsystem))
                                .andThen(Commands.followPath(follower, scoreBefore2ToScore2).withTimeout(300))
                                .andThen(Commands.openClaw(outtakeSubsystem)),

                        Commands.pickUpPOS(outtakeSubsystem)
                                .andThen(Commands.followPath(follower, score2ToPickUpBefore).withTimeout(1500))
//                                .andThen(Commands.setMaxPower(outtakeSubsystem, slowPower))
                                .andThen(Commands.followPath(follower, score2ToPickUp).withTimeout(100)),
//                                .andThen(Commands.setMaxPower(outtakeSubsystem, 1)),

                        Commands.closeClawThenScore(outtakeSubsystem)
                                .andThen(Commands.followPath(follower, pickUpToScoreBefore3))
                                .andThen(Commands.flick(outtakeSubsystem))
                                .andThen(Commands.followPath(follower, scoreBefore3ToScore3).withTimeout(300))
                                .andThen(Commands.openClaw(outtakeSubsystem)),

                        Commands.pickUpPOS(outtakeSubsystem)
                                .andThen(Commands.followPath(follower, score3ToPickUpBefore).withTimeout(1500))
//                                .andThen(Commands.setMaxPower(outtakeSubsystem, slowPower))
                                .andThen(Commands.followPath(follower, score3ToPickUp).withTimeout(100)),
//                                .andThen(Commands.setMaxPower(outtakeSubsystem, 1)),

                        Commands.closeClawThenScore(outtakeSubsystem)
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