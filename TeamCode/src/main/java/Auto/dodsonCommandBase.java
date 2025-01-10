package Auto;

import static Subsystem.FiveSpecimenPaths.blueLineUpToPushBlock1;
import static Subsystem.FiveSpecimenPaths.lineFirstUpToBlueLineUp;
import static Subsystem.FiveSpecimenPaths.scorePreload;
import static Subsystem.FiveSpecimenPaths.lineFirstUp;
import static Subsystem.FiveSpecimenPaths.pickUpToScore1;
import static Subsystem.FiveSpecimenPaths.scoreToPickUp;
import static Subsystem.FiveSpecimenPaths.pickUpToScore2;
import static Subsystem.FiveSpecimenPaths.scoreToBlock2Top;
import static Subsystem.FiveSpecimenPaths.block2TopToPickUp;
import static Subsystem.FiveSpecimenPaths.pickUpToScore3;
import static Subsystem.FiveSpecimenPaths.scoreToBlock3Top;
import static Subsystem.FiveSpecimenPaths.block3TopToPickUp;
import static Subsystem.FiveSpecimenPaths.pickUpToScore4;
import static Subsystem.FiveSpecimenPaths.park;

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
import Subsystem.Paths;
import Positions.Commands;
import pedroPathing.constants.FConstants;
import pedroPathing.constants.LConstants;

@Autonomous(name="dodsonCommandBase", group = "Auto Testing")
public class dodsonCommandBase extends CommandOpMode {
    public PathChain chain;
    public Follower follower;
    private OuttakeSubsystem outtakeSubsystem;

    @Override
    public void initialize() {
        // Initialize hardware and subsystems
        this.outtakeSubsystem = new OuttakeSubsystem(hardwareMap, telemetry);




        Constants.setConstants(FConstants.class, LConstants.class);
        this.follower = new Follower(hardwareMap);
        this.follower.setStartingPose(new Pose(9.4, 62.8, Math.toRadians(0)));
        this.chain = Paths.fiveSpecimanAuto;
        // Initialize paths
        Paths.initializePaths(follower);

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
                                .alongWith(Commands.followPath(follower, scorePreload)),
                        Commands.openClaw(outtakeSubsystem),
                        Commands.sleep(3000).andThen
                                (Commands.openClawThenPickUp(outtakeSubsystem)),
                        Commands.followPath(follower, lineFirstUpToBlueLineUp),
                        Commands.followPath(follower, blueLineUpToPushBlock1),

                        // First pickup and score cycle
                        Commands.sleep(300).andThen(Commands.closeClawThenScore(outtakeSubsystem)).andThen(Commands.sleep(250))
                                .andThen(Commands.followPath(follower, pickUpToScore1)),
                        Commands.followPath(follower, scoreToPickUp).
                                alongWith(Commands.openClawThenPickUp(outtakeSubsystem)),

                        // Second pickup and score cycle
                        Commands.sleep(300).andThen(Commands.closeClawThenScore(outtakeSubsystem)).andThen(Commands.sleep(250))
                                .andThen(Commands.followPath(follower, pickUpToScore1)),
                        Commands.followPath(follower, scoreToBlock2Top).
                                alongWith(Commands.openClawThenPickUp(outtakeSubsystem)),
                        Commands.followPath(follower, block2TopToPickUp),

                        // Third pickup and score cycle
                        Commands.sleep(300).andThen(Commands.closeClawThenScore(outtakeSubsystem)).andThen(Commands.sleep(250))
                                .andThen(Commands.followPath(follower, pickUpToScore3)),
                        Commands.followPath(follower, scoreToBlock3Top).
                                alongWith(Commands.openClawThenPickUp(outtakeSubsystem)),
                        Commands.followPath(follower, block3TopToPickUp),

                        // Fourth pickup and score cycle
                        Commands.sleep(300).andThen(Commands.closeClawThenScore(outtakeSubsystem)).andThen(Commands.sleep(250))
                                .andThen(Commands.followPath(follower, pickUpToScore4)),
                        Commands.followPath(follower, park).
                                alongWith(Commands.openClawThenPickUp(outtakeSubsystem))
                )
        );
    }
}