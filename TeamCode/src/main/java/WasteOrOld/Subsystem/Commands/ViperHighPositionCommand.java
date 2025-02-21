//package WasteOrOld.Subsystem.Commands;
//
//import com.arcrobotics.ftclib.command.CommandBase;
//
//import WasteOrOld.Subsystem.OuttakeSubsystemTeleOp;
//import WasteOrOld.Subsystem.ViperSubsystem;
//import Positions.positions_motor;
//
//public class ViperHighPositionCommand extends CommandBase {
//    private final ViperSubsystem viper;
//    private final OuttakeSubsystemTeleOp outtake;
//
//    public ViperHighPositionCommand(ViperSubsystem viper, OuttakeSubsystemTeleOp outtake) {
//        this.viper = viper;
//        this.outtake = outtake;
//        addRequirements(viper, outtake);
//    }
//
//    @Override
//    public void initialize() {
//        outtake.setArmPosition(positions_motor.OuttakeArmBucket);
//        outtake.setWristPosition(positions_motor.OuttakeWristBucket);
//        outtake.setWristPivotPosition(positions_motor.OuttakeWristPivotHighBar);
//        viper.setTargetPosition(positions_motor.VIPER_HIGHBASKET);
//    }
//
//    @Override
//    public boolean isFinished() {
//        return true;
//    }
//}