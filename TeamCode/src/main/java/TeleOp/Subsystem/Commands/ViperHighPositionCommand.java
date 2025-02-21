package TeleOp.commands;

import com.arcrobotics.ftclib.command.CommandBase;
import TeleOp.Subsystem.DriveSubsystem;
import TeleOp.Subsystem.IntakeSubsystem;
import TeleOp.Subsystem.OuttakeSubsystem;
import TeleOp.Subsystem.ViperSubsystem;
import Positions.positions_motor;

public class ViperHighPositionCommand extends CommandBase {
    private final ViperSubsystem viper;
    private final OuttakeSubsystem outtake;

    public ViperHighPositionCommand(ViperSubsystem viper, OuttakeSubsystem outtake) {
        this.viper = viper;
        this.outtake = outtake;
        addRequirements(viper, outtake);
    }

    @Override
    public void initialize() {
        outtake.setArmPosition(positions_motor.OuttakeArmBucket);
        outtake.setWristPosition(positions_motor.OuttakeWristBucket);
        outtake.setWristPivotPosition(positions_motor.OuttakeWristPivotHighBar);
        viper.setTargetPosition(positions_motor.VIPER_HIGHBASKET);
    }

    @Override
    public boolean isFinished() {
        return true;
    }
}