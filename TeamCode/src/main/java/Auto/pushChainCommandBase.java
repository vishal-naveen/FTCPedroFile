package Auto;

import static Subsystem.pushChain3.preloadBackPath;
import static Subsystem.pushChain3.scorePreload;
import static Subsystem.pushChain3.lineFirstUp;
import static Subsystem.pushChain3.blockPushToScorePath;
import static Subsystem.pushChain3.pushScoreToPickUpPath;
import static Subsystem.pushChain3.pickUpToScore1;
import static Subsystem.pushChain3.scoreToPickUp1;
import static Subsystem.pushChain3.pickUpToScore2;
import static Subsystem.pushChain3.scoreToPickUp2;
import static Subsystem.pushChain3.pickUpToScore3;
import static Subsystem.pushChain3.scoreToPickUp3;
import static Subsystem.pushChain3.park;

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
import Positions.Commands;
import Subsystem.PathsPush3;
import Subsystem.PathsPush3Chain;
import pedroPathing.constants.FConstants;
import pedroPathing.constants.LConstants;

//@Autonomous(name="pushChainCommandBase", group = "Auto Testing")
public class pushChainCommandBase extends CommandOpMode {
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

        PathsPush3Chain.initializePaths(follower);
        this.chain = PathsPush3Chain.pushChain3;

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
                        // Push block sequence
                        Commands.openClawThenPickUp(outtakeSubsystem)
                                .andThen(Commands.followPath(follower, lineFirstUp)),
                        // First scoring cycle
                        Commands.sleep(300)
                                .andThen(Commands.closeClawThenScore(outtakeSubsystem))
                                .andThen(Commands.sleep(250))
                                .andThen(Commands.followPath(follower, blockPushToScorePath)),
                        Commands.followPath(follower, pushScoreToPickUpPath)
                                .alongWith(Commands.openClawThenPickUp(outtakeSubsystem)),
                        // Second scoring cycle
                        Commands.sleep(300)
                                .andThen(Commands.closeClawThenScore(outtakeSubsystem))
                                .andThen(Commands.sleep(250))
                                .andThen(Commands.followPath(follower, pickUpToScore1)),
                        Commands.followPath(follower, scoreToPickUp1)
                                .alongWith(Commands.openClawThenPickUp(outtakeSubsystem)),
                        // Third scoring cycle
                        Commands.sleep(300)
                                .andThen(Commands.closeClawThenScore(outtakeSubsystem))
                                .andThen(Commands.sleep(250))
                                .andThen(Commands.followPath(follower, pickUpToScore2)),
                        Commands.followPath(follower, scoreToPickUp2)
                                .alongWith(Commands.openClawThenPickUp(outtakeSubsystem)),
                        // Final scoring cycle
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