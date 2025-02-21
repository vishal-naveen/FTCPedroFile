//package WasteOrOld.Subsystem;
//
//import com.arcrobotics.ftclib.command.SubsystemBase;
//import com.qualcomm.robotcore.hardware.HardwareMap;
//import com.qualcomm.robotcore.hardware.Servo;
//
//import Positions.Constants;
//import Positions.positions_motor;
//
//public class OuttakeSubsystemTeleOp extends SubsystemBase {
//    private final Servo arm;
//    private final Servo wrist;
//    private final Servo wristPivot;
//    private final Servo claw;
//
//    public OuttakeSubsystemTeleOp(HardwareMap hardwareMap) {
//        arm = hardwareMap.get(Servo.class, "OuttakeArm");
//        wrist = hardwareMap.get(Servo.class, "OuttakeWrist");
//        wristPivot = hardwareMap.get(Servo.class, "OuttakeWristPivot");
//        claw = hardwareMap.get(Servo.class, "OuttakeClaw");
//    }
//
//    public enum OuttakeState {
//        PICKUP(Constants.OuttakeArmPickUpSpecimen,
//                Constants.OuttakeWristPickUpSpecimen,
//                Constants.OuttakeWristPivotSpecimenPickUp),
//        SCORE(Constants.OuttakeArmNewHighBar,
//                Constants.OuttakeWristNewHighBar,
//                Constants.OuttakeWristPivotHighBar);
//
//        private final double armPos, wristPos, wristPivotPos;
//
//        OuttakeState(double armPos, double wristPos, double wristPivotPos) {
//            this.armPos = armPos;
//            this.wristPos = wristPos;
//            this.wristPivotPos = wristPivotPos;
//        }
//    }
//
//    private OuttakeState currentState = OuttakeState.SCORE;
//
//    public void setToState(OuttakeState state) {
//        currentState = state;
//        arm.setPosition(state.armPos);
//        wrist.setPosition(state.wristPos);
//        wristPivot.setPosition(state.wristPivotPos);
//    }
//
//    public void pickUpPOS() {
//        setToState(OuttakeState.PICKUP);
//    }
//
//    public void scoreFull() {
//        arm.setPosition(positions_motor.OuttakeArmNewHighBar);
//        wrist.setPosition(positions_motor.OuttakeWristNewHighBar);
//        wristPivot.setPosition(positions_motor.OuttakeWristPivotHighBar);
//    }
//
//    public void pickUpFull() {
//        arm.setPosition(positions_motor.OuttakeArmPickUpSpecimen);
//        wrist.setPosition(positions_motor.OuttakeWristPickUpSpecimen);
//        wristPivot.setPosition(positions_motor.OuttakeWristPivotSpecimenPickUp);
//    }
//
//    public void setArmPosition(double position) {
//        arm.setPosition(position);
//    }
//
//    public void setWristPosition(double position) {
//        wrist.setPosition(position);
//    }
//
//    public void setWristPivotPosition(double position) {
//        wristPivot.setPosition(position);
//    }
//
//    public void setClawPosition(double position) {
//        claw.setPosition(position);
//    }
//
//    public double getArmPosition() {
//        return arm.getPosition();
//    }
//
//    public double getWristPosition() {
//        return wrist.getPosition();
//    }
//
//    public double getWristPivotPosition() {
//        return wristPivot.getPosition();
//    }
//
//    public double getClawPosition() {
//        return claw.getPosition();
//    }
//}