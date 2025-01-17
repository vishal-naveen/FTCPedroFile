package Auto;

import static Subsystem.Push3Specimen.*;  // Updated to import all paths

import com.arcrobotics.ftclib.command.CommandOpMode;
import com.arcrobotics.ftclib.command.RunCommand;
import com.arcrobotics.ftclib.command.SequentialCommandGroup;
import com.arcrobotics.ftclib.command.WaitUntilCommand;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.PathChain;
import com.pedropathing.util.Constants;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.pedropathing.follower.Follower;

import Subsystem.OuttakeSubsystem;
import Subsystem.PathsPush3;
import Subsystem.PathsPush3Chain;
import Positions.Commands;
import pedroPathing.constants.FConstants;
import pedroPathing.constants.LConstants;

@Autonomous(name="pushCommandBase12", group = "Auto Testing")
public class pushCommandBase extends CommandOpMode {
    public PathChain chain;
    public Follower follower;
    private OuttakeSubsystem outtakeSubsystem;

    @Override
    public void initialize() {
        // Initialize hardware and subsystems
        this.outtakeSubsystem = new OuttakeSubsystem(hardwareMap, telemetry);

        Constants.setConstants(FConstants.class, LConstants.class);
        this.follower = new Follower(hardwareMap);
        this.follower.setStartingPose(new Pose(10, 62.8, Math.toRadians(0)));

        // Initialize paths
        PathsPush3.initializePaths(follower);
        this.chain = PathsPush3.Push3Specimen;

        if(opModeInInit()) {
            outtakeSubsystem.closeClaw();
            outtakeSubsystem.preloadPosition();
        }

        schedule(
                new RunCommand(follower::update),
                new SequentialCommandGroup(
                        new WaitUntilCommand(this::opModeIsActive),
                        // Preload scoring sequence
                        Commands.scoreSpecimen(outtakeSubsystem)
                                .andThen(Commands.sleep(500))
                                .andThen(Commands.followPath(follower, preloadBeforePath))
                                .andThen(Commands.flick(outtakeSubsystem))
                                .andThen(Commands.followPath(follower, scorePreload))
                                .andThen(Commands.openClaw(outtakeSubsystem)),

                        Commands.followPath(follower, preloadBackPath)
                                .andThen(Commands.openClawThenPickUp(outtakeSubsystem)),

                        Commands.followPath(follower, preloadToBlueLineUp),
                        Commands.followPath(follower, blueLineUpToPushBlock1),
                        Commands.followPath(follower, pushBlock1ToPushBlock2),

                        // First scoring sequence
                        Commands.sleep(750)
                                .andThen(Commands.closeClawThenScore(outtakeSubsystem))
                                .andThen(Commands.followPath(follower, pushToScoreBefore1))
                                .andThen(Commands.flick(outtakeSubsystem))
                                .andThen(Commands.followPath(follower, scoreBefore1ToScore1))
                                .andThen(Commands.openClaw(outtakeSubsystem)),

                        Commands.followPath(follower, score1ToPickUp)
                                .alongWith(Commands.openClawThenPickUp(outtakeSubsystem)),

                        // Second scoring sequence
                        Commands.sleep(750)
                                .andThen(Commands.closeClawThenScore(outtakeSubsystem))
                                .andThen(Commands.followPath(follower, pickUpToScoreBefore2))
                                .andThen(Commands.flick(outtakeSubsystem))
                                .andThen(Commands.followPath(follower, scoreBefore2ToScore2))
                                .andThen(Commands.openClaw(outtakeSubsystem)),

                        Commands.followPath(follower, score2ToPickUp)
                                .alongWith(Commands.openClawThenPickUp(outtakeSubsystem)),

                        // Third scoring sequence
                        Commands.sleep(750)
                                .andThen(Commands.closeClawThenScore(outtakeSubsystem))
                                .andThen(Commands.followPath(follower, pickUpToScoreBefore3))
                                .andThen(Commands.flick(outtakeSubsystem))
                                .andThen(Commands.followPath(follower, scoreBefore3ToScore3))
                                .andThen(Commands.openClaw(outtakeSubsystem)),

                        Commands.followPath(follower, score3ToPickUp)
                                .andThen(Commands.openClawThenPickUp(outtakeSubsystem)),

                        // Park
                        Commands.followPath(follower, park)
                                .andThen(Commands.openClawThenPickUp(outtakeSubsystem))
                )
        );
    }
}