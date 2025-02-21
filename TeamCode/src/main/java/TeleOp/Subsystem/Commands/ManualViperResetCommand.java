package TeleOp.Subsystem.Commands;

import com.arcrobotics.ftclib.command.CommandBase;
import com.qualcomm.robotcore.util.ElapsedTime;

import TeleOp.Subsystem.DriveSubsystem;
import TeleOp.Subsystem.IntakeSubsystem;
import TeleOp.Subsystem.OuttakeSubsystem;
import TeleOp.Subsystem.ViperSubsystem;

public class ManualViperResetCommand extends CommandBase {
    private final ViperSubsystem viper;
    private final ElapsedTime timer = new ElapsedTime();
    private int lastPosition;
    private static final int STALL_POSITION_THRESHOLD = 5;
    private static final double STALL_TIME_THRESHOLD = 250;

    public ManualViperResetCommand(ViperSubsystem viper) {
        this.viper = viper;
        addRequirements(viper);
    }

    @Override
    public void initialize() {
        viper.setPower(-0.5);
        lastPosition = viper.getCurrentPosition();
        timer.reset();
    }

    @Override
    public void execute() {
        if (timer.milliseconds() > STALL_TIME_THRESHOLD) {
            int currentPos = viper.getCurrentPosition();
            if (Math.abs(currentPos - lastPosition) < STALL_POSITION_THRESHOLD) {
                viper.setPower(0);
                viper.resetEncoder();
                cancel();
            }
            lastPosition = currentPos;
            timer.reset();
        }
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public void end(boolean interrupted) {
        if (!interrupted) {
            viper.setPower(0);
            viper.resetEncoder();
        }
    }
}