package TeleOp.Subsystem.Commands;

import com.arcrobotics.ftclib.command.CommandBase;
import TeleOp.Subsystem.DriveSubsystem;
import TeleOp.Subsystem.IntakeSubsystem;
import TeleOp.Subsystem.OuttakeSubsystem;
import Positions.positions_motor;

public class PickupCommand extends CommandBase {
    private final IntakeSubsystem intake;
    private final OuttakeSubsystem outtake;

    public PickupCommand(IntakeSubsystem intake, OuttakeSubsystem outtake) {
        this.intake = intake;
        this.outtake = outtake;
        addRequirements(intake, outtake);
    }

    @Override
    public void initialize() {
        outtake.setArmPosition(positions_motor.OuttakeArmPickUpSpecimen);
        outtake.setWristPosition(positions_motor.OuttakeWristPickUpSpecimen);
        outtake.setWristPivotPosition(positions_motor.OuttakeWristPivotSpecimenPickUp);
        intake.setWristPosition(positions_motor.NIntakeWristPickUp);
    }

    @Override
    public boolean isFinished() {
        return true;
    }
}