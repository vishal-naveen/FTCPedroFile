//package WasteOrOld.Subsystem.Commands;
//
//import com.arcrobotics.ftclib.command.CommandBase;
//import com.qualcomm.robotcore.util.ElapsedTime;
//
//import WasteOrOld.Subsystem.OuttakeSubsystemTeleOp;
//import WasteOrOld.Subsystem.ViperSubsystem;
//import Positions.positions_motor;
//
//public class ViperGroundPositionCommand extends CommandBase {
//    private final ViperSubsystem viper;
//    private final OuttakeSubsystemTeleOp outtake;
//    private final ElapsedTime timer = new ElapsedTime();
//    private int state = 0;
//
//    public ViperGroundPositionCommand(ViperSubsystem viper, OuttakeSubsystemTeleOp outtake) {
//        this.viper = viper;
//        this.outtake = outtake;
//        addRequirements(viper, outtake);
//    }
//
//    @Override
//    public void initialize() {
//        state = 0;
//        timer.reset();
//    }
//
//    @Override
//    public void execute() {
//        switch (state) {
//            case 0:
//                outtake.setArmPosition(positions_motor.OuttakeArmNewTransferWAIT);
//                outtake.setWristPosition(positions_motor.OuttakeWristPickUpSpecimen);
//                outtake.setWristPivotPosition(positions_motor.OuttakeWristPivotHighBar);
//                if (timer.milliseconds() > 750) {
//                    state = 1;
//                }
//                break;
//
//            case 1:
//                viper.setTargetPosition(positions_motor.VIPER_GROUND);
//                break;
//        }
//    }
//
//    @Override
//    public boolean isFinished() {
//        return state == 1;
//    }
//}