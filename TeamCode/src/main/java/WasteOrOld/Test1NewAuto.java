package WasteOrOld;

import com.arcrobotics.ftclib.command.CommandOpMode;
import com.arcrobotics.ftclib.command.RunCommand;
import com.arcrobotics.ftclib.command.SequentialCommandGroup;
import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.Path;
import com.pedropathing.pathgen.PathChain;
import com.pedropathing.util.Constants;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import Positions.Commands;
import Subsystem.OuttakeWristSubsystem;
import pedroPathing.constants.FConstants;
import pedroPathing.constants.LConstants;

@Autonomous(name = "WasteOrOld.Test1NewAuto", group = "Examples")
public class Test1NewAuto extends CommandOpMode {
    private Follower follower;
    private Timer pathTimer, opmodeTimer, outtakeTimer;
    private OuttakeWristSubsystem outtakeWrist;

    private Path scorePreload, pickUpToScore, scoreToPickUp;
    private PathChain lineFirstUp;

    @Override
    public void initialize() {
        pathTimer = new Timer();
        opmodeTimer = new Timer();

        outtakeWrist = new OuttakeWristSubsystem(hardwareMap);

        Constants.setConstants(FConstants.class, LConstants.class);
        follower = new Follower(hardwareMap);
        follower.setStartingPose(new Pose(0.5, 73, Math.toRadians(0)));



        scorePreload = Test1Paths.getScorePreload(follower);
        lineFirstUp = Test1Paths.getLineFirstUp(follower);
        pickUpToScore = Test1Paths.getPickUpToScore(follower);
        scoreToPickUp = Test1Paths.getScoreToPickUp(follower);

        schedule(
                new RunCommand(follower::update),
                new SequentialCommandGroup(
                        Commands.followPath(follower, scorePreload).alongWith(
                                outtakeWrist.score()
                        ),
                        Commands.followPath(follower, lineFirstUp).alongWith(
                                outtakeWrist.pickup()
                        ),
                        Commands.followPath(follower, scoreToPickUp),
                        Commands.followPath(follower, pickUpToScore).alongWith(
                                outtakeWrist.score()
                        )
                )
        );
    }
}