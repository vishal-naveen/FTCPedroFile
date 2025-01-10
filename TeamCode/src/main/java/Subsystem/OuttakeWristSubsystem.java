package Subsystem;

import com.arcrobotics.ftclib.command.SubsystemBase;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.arcrobotics.ftclib.command.Command;
import com.arcrobotics.ftclib.command.InstantCommand;

public class OuttakeWristSubsystem extends SubsystemBase {
    public enum WristState {
        PICKUP(0.2),
        TRANSFER(0.0),
        SCORE(0.5);

        public final double position;
        WristState(double position) {
            this.position = position;
        }
    }

    private final Servo wrist;
    private WristState state;

    public OuttakeWristSubsystem(HardwareMap hardwareMap) {
        wrist = hardwareMap.get(Servo.class, "OuttakeWristPivot");
        state = WristState.TRANSFER;
    }

    public Command pickup() {
        return new InstantCommand(() -> {
            wrist.setPosition(WristState.PICKUP.position);
            state = WristState.PICKUP;
        }, this);
    }

    public Command transfer() {
        return new InstantCommand(() -> {
            wrist.setPosition(WristState.TRANSFER.position);
            state = WristState.TRANSFER;
        }, this);
    }

    public Command score() {
        return new InstantCommand(() -> {
            wrist.setPosition(WristState.SCORE.position);
            state = WristState.SCORE;
        }, this);
    }

    public void init() {
        transfer();
    }

    public WristState getState() {
        return state;
    }
}