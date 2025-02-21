//package WasteOrOld.Subsystem.Commands;
//
//import com.arcrobotics.ftclib.command.CommandBase;
//
//import WasteOrOld.Subsystem.IntakeSubsystem;
//import WasteOrOld.Subsystem.OuttakeSubsystemTeleOp;
//import Positions.positions_motor;
//
//public class PickupCommand extends CommandBase {
//    private final IntakeSubsystem intake;
//    private final OuttakeSubsystemTeleOp outtake;
//
//    public PickupCommand(IntakeSubsystem intake, OuttakeSubsystemTeleOp outtake) {
//        this.intake = intake;
//        this.outtake = outtake;
//        addRequirements(intake, outtake);
//    }
//
//    @Override
//    public void initialize() {
//        outtake.setArmPosition(positions_motor.OuttakeArmPickUpSpecimen);
//        outtake.setWristPosition(positions_motor.OuttakeWristPickUpSpecimen);
//        outtake.setWristPivotPosition(positions_motor.OuttakeWristPivotSpecimenPickUp);
//        intake.setWristPosition(positions_motor.NIntakeWristPickUp);
//    }
//
//    @Override
//    public boolean isFinished() {
//        return true;
//    }
//}