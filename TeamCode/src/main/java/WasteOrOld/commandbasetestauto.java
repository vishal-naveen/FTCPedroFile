//package Auto;
//
//import static Subsystem.FiveSpecimenPaths.scorePreload;
//import static Subsystem.FiveSpecimenPaths.lineFirstUp;
//import static Subsystem.FiveSpecimenPaths.firstPickUp;
//import static Subsystem.FiveSpecimenPaths.pickUpToScore;
//import static Subsystem.FiveSpecimenPaths.scoreToPickUp;
//
//import com.arcrobotics.ftclib.command.CommandOpMode;
//import com.arcrobotics.ftclib.command.RunCommand;
//import com.arcrobotics.ftclib.command.SequentialCommandGroup;
//import com.arcrobotics.ftclib.command.WaitUntilCommand;
//import com.pedropathing.localization.Pose;
//import com.pedropathing.pathgen.PathChain;
//import com.pedropathing.util.Constants;
//import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
//
//import com.pedropathing.follower.Follower;
//
//import Subsystem.OuttakeSubsystem;
//import Subsystem.Paths;
//import Positions.Commands;
//import pedroPathing.constants.FConstants;
//import pedroPathing.constants.LConstants;
//
//@Autonomous(name="commandbasetestauto", group = "WIP")
//public class commandbasetestauto extends CommandOpMode {
//    public PathChain chain;
//    public Follower follower;
//
//    private OuttakeSubsystem outtakesubsystem;
//
//    @Override
//    public void initialize() {
//        this.outtakesubsystem = new OuttakeSubsystem(hardwareMap, telemetry);
//
//        Constants.setConstants(FConstants.class, LConstants.class);
//        this.follower = new Follower(hardwareMap);
//        this.follower.setStartingPose(new Pose(0.5, 73.0, 0.0));
//
//        // Initialize paths
//        Paths.initializePaths(follower);
//
//        schedule(
//                new RunCommand(follower::update),
//                new SequentialCommandGroup(
//                        new WaitUntilCommand(this::opModeIsActive),
//                        // Score preload
//                        Commands.followPath(follower, scorePreload).alongWith(
//                                Commands.scoreSpecimen(outtakesubsystem)
//                        ),
//                        Commands.sleep(150).andThen(
//                                Commands.openClaw(outtakesubsystem)
//                        ),
//                        // Line up and push blocks
//                        Commands.followPath(follower, lineFirstUp).alongWith(
//                                Commands.pickUpSpecimen(outtakesubsystem)
//                        ),
//                        // First pickup
//                        Commands.followPath(follower, firstPickUp),
//                        Commands.closeClaw(outtakesubsystem),
//                        Commands.sleep(300),
//                        Commands.scoreSpecimen(outtakesubsystem),
//                        // Score and pickup cycle
//                        Commands.followPath(follower, pickUpToScore),
//                        Commands.openClaw(outtakesubsystem),
//                        Commands.followPath(follower, scoreToPickUp).alongWith(
//                                Commands.pickUpSpecimen(outtakesubsystem)
//                        )
//                        // Continue pattern for remaining cycles...
//                )
//        );
//    }
//}