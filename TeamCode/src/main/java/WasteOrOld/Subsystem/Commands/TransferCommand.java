//package WasteOrOld.Subsystem.Commands;
//
//import com.arcrobotics.ftclib.command.CommandBase;
//import com.qualcomm.robotcore.util.ElapsedTime;
//
//import WasteOrOld.Subsystem.IntakeSubsystem;
//import WasteOrOld.Subsystem.OuttakeSubsystemTeleOp;
//import WasteOrOld.Subsystem.ViperSubsystem;
//import Positions.positions_motor;
//
//
//public class TransferCommand extends CommandBase {
//    private final IntakeSubsystem intake;
//    private final OuttakeSubsystemTeleOp outtake;
//    private final ViperSubsystem viper;
//    private final ElapsedTime timer = new ElapsedTime();
//    private int state = 0;
//
//    public TransferCommand(IntakeSubsystem intake, OuttakeSubsystemTeleOp outtake, ViperSubsystem viper) {
//        this.intake = intake;
//        this.outtake = outtake;
//        this.viper = viper;
//        addRequirements(intake, outtake, viper);
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
//                intake.setWristPosition(positions_motor.NIntakeWristTransfer);
//                outtake.setClawPosition(positions_motor.OuttakeClawOpen);
//                intake.setArmPosition(positions_motor.NIntakeArmTransfer);
//                if (timer.milliseconds() > 500) {
//                    state = 1;
//                    timer.reset();
//                }
//                break;
//
//            case 1:
//                intake.setClawPosition(positions_motor.NIntakeClawCloseFull);
//                intake.setWristPivotPosition(positions_motor.NIntakeWristPivotTransfer);
//                outtake.setArmPosition(positions_motor.OuttakeArmNewTransfer);
//                outtake.setWristPosition(positions_motor.OuttakeWristTransfer);
//                outtake.setWristPivotPosition(positions_motor.OuttakeWristPivotHighBar);
//                if (timer.milliseconds() > 200) {
//                    state = 2;
//                    timer.reset();
//                }
//                break;
//
//            case 2:
//                outtake.setClawPosition(positions_motor.OuttakeClawClose);
//                if (timer.milliseconds() > 400) {
//                    state = 3;
//                    timer.reset();
//                }
//                break;
//
//            case 3:
//                intake.setClawPosition(positions_motor.NIntakeClawOpen);
//                if (timer.milliseconds() > 100) {
//                    state = 4;
//                    timer.reset();
//                }
//                break;
//
//            case 4:
//                outtake.setArmPosition(positions_motor.OuttakeArmBucket);
//                outtake.setWristPosition(positions_motor.OuttakeWristBucket);
//                outtake.setWristPivotPosition(positions_motor.OuttakeWristPivotHighBar);
//                intake.setWristPivotPosition(positions_motor.NIntakeWristPivotHorizontal);
//                intake.setWristPosition(positions_motor.NIntakeWristPickUp);
//                viper.setTargetPosition(positions_motor.VIPER_HIGHBASKET);
//                break;
//        }
//    }
//
//    @Override
//    public boolean isFinished() {
//        return state == 4 && timer.milliseconds() > 100;
//    }
//}