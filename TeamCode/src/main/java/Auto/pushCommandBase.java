package Auto;

import static Subsystem.Push3Specimen.preloadToBlueLineUp;
import static Subsystem.Push3Specimen.blueLineUpToPushBlock1;
import static Subsystem.Push3Specimen.pushBlock1ToPushBlock2;
import static Subsystem.Push3Specimen.pushBlock2ToPushBlock3;
import static Subsystem.Push3Specimen.preloadBackPath;
import static Subsystem.Push3Specimen.scorePreload;
import static Subsystem.Push3Specimen.pickUpToScore1;
import static Subsystem.Push3Specimen.scoreToPickUp1;
import static Subsystem.Push3Specimen.pickUpToScore2;
import static Subsystem.Push3Specimen.scoreToPickUp2;
import static Subsystem.Push3Specimen.pickUpToScore3;
import static Subsystem.Push3Specimen.scoreToPickUp3;
import static Subsystem.Push3Specimen.blockPushToScorePath;
import static Subsystem.Push3Specimen.pushScoreToPickUpPath;
import static Subsystem.Push3Specimen.park;

import com.arcrobotics.ftclib.command.CommandOpMode;
import com.arcrobotics.ftclib.command.RunCommand;
import com.arcrobotics.ftclib.command.SequentialCommandGroup;
import com.arcrobotics.ftclib.command.WaitUntilCommand;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.PathChain;
import com.pedropathing.util.Constants;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.pedropathing.follower.Follower;

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

@Autonomous(name="pushCommandBase", group = "Auto Testing")
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

        // Schedule the autonomous routine
        schedule(
                new RunCommand(follower::update),
                new SequentialCommandGroup(
                        new WaitUntilCommand(this::opModeIsActive),
                        // Preload scoring sequence
                        Commands.scoreSpecimen(outtakeSubsystem)
                                .andThen(Commands.sleep(500))
                                .andThen(Commands.followPath(follower, scorePreload))
                                .andThen(Commands.sleep(1000))
                                .andThen(Commands.openClaw(outtakeSubsystem))
                                .andThen(Commands.followPath(follower, preloadBackPath)),
                        Commands.openClawThenPickUp(outtakeSubsystem)
                                .andThen(Commands.followPath(follower, preloadToBlueLineUp))
                                .andThen(Commands.followPath(follower, blueLineUpToPushBlock1))
                                .andThen(Commands.followPath(follower, pushBlock1ToPushBlock2))
                                .andThen(Commands.followPath(follower, pushBlock2ToPushBlock3)),

                        Commands.sleep(300)
                                .andThen(Commands.closeClawThenScore(outtakeSubsystem))
                                .andThen(Commands.sleep(250))
                                .andThen(Commands.followPath(follower, blockPushToScorePath)),
                        Commands.followPath(follower, pushScoreToPickUpPath)
                                .alongWith(Commands.openClawThenPickUp(outtakeSubsystem)),
                        // First pickup and score cycle
                        Commands.sleep(300)
                                .andThen(Commands.closeClawThenScore(outtakeSubsystem))
                                .andThen(Commands.sleep(250))
                                .andThen(Commands.followPath(follower, pickUpToScore1)),
                        Commands.followPath(follower, scoreToPickUp1)
                                .alongWith(Commands.openClawThenPickUp(outtakeSubsystem)),
                        // Second scoring cycle
                        Commands.sleep(300)
                                .andThen(Commands.closeClawThenScore(outtakeSubsystem))
                                .andThen(Commands.sleep(250))
                                .andThen(Commands.followPath(follower, pickUpToScore2)),
                        Commands.followPath(follower, scoreToPickUp2)
                                .alongWith(Commands.openClawThenPickUp(outtakeSubsystem)),
                        // Third scoring cycle
                        Commands.sleep(300)
                                .andThen(Commands.closeClawThenScore(outtakeSubsystem))
                                .andThen(Commands.sleep(250))
                                .andThen(Commands.followPath(follower, pickUpToScore3)),
                        Commands.followPath(follower, scoreToPickUp3)
                                .alongWith(Commands.openClawThenPickUp(outtakeSubsystem)),
                        // Park
                        Commands.followPath(follower, park)
                                .alongWith(Commands.openClawThenPickUp(outtakeSubsystem))
                )
        );
    }
}