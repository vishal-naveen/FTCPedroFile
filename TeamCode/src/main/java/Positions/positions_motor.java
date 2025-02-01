package Positions;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

public class positions_motor {
    // Viper slide positions
    public static final int VIPER_GROUND = 25;
    public static final int VIPER_INT = 915;
    public static final int VIPER_GROUND_AUTO = 1600;

    public static final int VIPER_HIGHBAR = 300;

    public static final int VIPER_AUTO_END = 1500;
    public static final int VIPER_HIGHBAR2 = 1200;
    public static final int VIPER_LOWBASKET = 900;

   //2650
    public static final int VIPER_HIGHBASKET = 2600;

    // Servo positions
    public static final double ARM_UP = 0.6;
    public static final double ARM_MIDDLE = 0.35;
    public static final double ARM_DOWN = 0.15;

    public static final double CLAW_UP = 0.75;
    public static final double CLAW_DOWN = 0.91;

    public static final double CLAW_SPECIMEN_PICKUP = 0.8;

    public static final double WRIST_SPECIMEN = 0.83;


    public static final double CLAW_AFTER_SPECIMEN = 0.85;

    public static final double CLAW_SPECIMEN = 0.4;


    public static final double CLAW_CLOSED = 1;
    public static final double CLAW_OPEN = 0.4;

    public static final double NIntakeArmExtendedFull = 0.7;
    public static final double NIntakeArmExtendedThird = 0.4;
    public static final double NIntakeArmExtendedQuater = 0.2;

    public static final double NIntakeArmTransfer = 0.295;

    public static final double NIntakeArmSpecimenPickUp = 0.3;
    public static final double NIntakeArmExtendedBack = 0.1;

    public static final double NIntakeWristPickUp = 0.9;

    public static final double NIntakeWristPickUpBefore = 0.6;
    public static final double NIntakeWristTransfer = 0.25;
    public static final double NIntakeWristPivotHorizontal = 0.235;

    public static final double NIntakeWristPivotVertical = 0.585;

    public static final double NIntakeWristPivotTransfer = 1;
    public static final double NIntakeClawOpen = 0.8;

    public static final double NIntakeClawOpenTransfer = 0.75;
    public static final double NIntakeClawClose = 0.53;

    public static final double NIntakeClawCloseFull = 0.475;


    //OUTTAKEEE


    public static final double OuttakeWristTransfer = 0.66;



    public static final double OuttakeWristTransferAuto = 0.27;

    public static final double OuttakeArmTransfer = 0.525;

    public static final double OuttakeArmTransferAuto = 0.7;

    public static final double OuttakeArmTransferWait = 0.55;

    public static final double OuttakeArmPedroAuto = 0.8;
    public static final double OuttakeArmBucket = 0.35;

    public static final double OuttakeArmDownWhileBucket = 0.1;
    public static final double OuttakeArmHighBar = 0.15;

    public static final double OuttakeArmAutoLoading = 0.8;
    public static final double OuttakeArmHighBarFlick = 0.18;

    public static final double OuttakeArmPickUpSpecimen = 0.985;

    public static final double OuttakeArmNewTransfer = 0.93;

    public static final double OuttakeArmNewTransferWAIT = 0.675;
    public static final double OuttakeArmPickUpGround = 0.2;

    public static final double OuttakeArmNewHighBar = 0.125;

    public static final double OuttakeArmNewHighBarFLICK = 0.04;



    public static final double OuttakeArmBucketTEMP = 0.6;



    //New

    public static final double OuttakeArmRightTransfer = 0.75;

    public static final double OuttakeArmRightTransferAuto = 0.7;

    public static final double OuttakeArmRightTransferWait = 0.55;

    public static final double OuttakeArmRightPedroAuto = 0.8;
    public static final double OuttakeArmRightBucket = 0.3;
    public static final double OuttakeArmRightHighBar = 0.15;

    public static final double OuttakeArmRightAutoLoading = 0.8;
    public static final double OuttakeArmRightHighBarFlick = 0.18;

    public static final double OuttakeArmRightPickUpSpecimen = 0;
    public static final double OuttakeArmRightPickUpGround = 0.2;

    public static final double OuttakeArmRightNewHighBar = 1;

    public static final double OuttakeArmRightNewHighBarFLICK = 1;



    public static final double OuttakeWristHighBar = 0.51;

    public static final double OuttakeWristNewHighBar = 0.5;

    public static final double OuttakeWristNewHighBarFLICK = 0.6625;


    //0.9
    public static final double OuttakeWristBucket = 0.825;

    public static final double OuttakeWristDownWhileBucket = 0.55;

    public static final double OuttakeWristPedroAuto = 0.1;
    //0.75 is straight
    //0.6 is sticking up

    //0.05
    public static final double OuttakeWristPickUpSpecimen = 0.6985;


    public static final double OuttakeWristHighBarMore = 0.6;



    public static final double OuttakeWristPivotVertical = 0.3;

    public static final double OuttakeWristPivotPedro = 0.35;

    public static final double OuttakeWristPivotHorizontal = 0.735;

    public static final double OuttakeWristPivotSpecimenPickUp = 0.685;

    public static final double OuttakeWristPivotHighBar = 0;

    public static final double OuttakeClawOpen = 0.1;
    public static final double OuttakeClawClose = 0.55;

    public static final int OUTAKE_NEW_VIPER_GROUND = 1500;
    public static final int OUTAKE_NEW_VIPER_HIGHBAR = 1200;
    public static final int OUTAKE_NEW_VIPER_HIGHBASKET = 3700;



    // Viper height control methods
    public static void setViperHighBasket(DcMotor viperMotor, Servo armServo, Servo clawServo) {
        viperMotor.setTargetPosition(VIPER_HIGHBASKET);
        viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        viperMotor.setPower(1);
        armServo.setPosition(ARM_UP);
        clawServo.setPosition(CLAW_UP);
    }

    public static void setViperHighBasketArm(Servo armServo, Servo clawServo) {
        armServo.setPosition(ARM_UP);
        clawServo.setPosition(CLAW_UP);
    }

    public static void autoViperHigh(DcMotor viperMotor) {
        viperMotor.setTargetPosition(VIPER_HIGHBASKET);
        viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        viperMotor.setPower(1);
    }

    public static void setViperLowBasket(DcMotor viperMotor) {
//        armServo.setPosition(ARM_UP);
        viperMotor.setTargetPosition(VIPER_LOWBASKET);
        viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        viperMotor.setPower(0.9);
    }

    public static void setViperHighBar(DcMotor viperMotor, Servo armServo, Servo clawServo) {
        viperMotor.setTargetPosition(VIPER_HIGHBAR);
        viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        viperMotor.setPower(1);
        clawServo.setPosition(CLAW_SPECIMEN);
        armServo.setPosition(ARM_MIDDLE);
    }

    public static void setViperAutoEnd(DcMotor viperMotor, Servo armServo, Servo clawServo) {
        viperMotor.setTargetPosition(VIPER_AUTO_END);
        viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        viperMotor.setPower(1);
        clawServo.setPosition(CLAW_SPECIMEN);
        armServo.setPosition(ARM_MIDDLE);
    }

    public static void setViperHighBarJustLift(DcMotor viperMotor) {
        viperMotor.setTargetPosition(VIPER_HIGHBAR);
        viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        viperMotor.setPower(1);
    }

    public static void setViperHighBarJustArm(Servo armServo, Servo clawServo) {
        clawServo.setPosition(CLAW_SPECIMEN);
        armServo.setPosition(ARM_MIDDLE);
    }

    public static void setViperHighBar2(DcMotor viperMotor, Servo armServo, Servo clawServo) {
        viperMotor.setTargetPosition(VIPER_HIGHBAR2);
        viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        viperMotor.setPower(0.3);
        clawServo.setPosition(CLAW_UP);
        armServo.setPosition(ARM_MIDDLE);
    }

    public static void setViperGround(DcMotor viperMotor) {
        viperMotor.setTargetPosition(VIPER_GROUND);
        viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        viperMotor.setPower(1);
    }

    public static void setViperSpecimenPick(DcMotor viperMotor, Servo armServo, Servo clawServo) {
        viperMotor.setTargetPosition(VIPER_GROUND);
        viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        viperMotor.setPower(1);
        armServo.setPosition(ARM_MIDDLE);
        clawServo.setPosition(CLAW_UP);
    }

    public static void setViperAutoInt(DcMotor viperMotor) {
        viperMotor.setTargetPosition(VIPER_INT);
        viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        viperMotor.setPower(1);
    }

    public static void setViperGroundAuto(DcMotor viperMotor) {
        viperMotor.setTargetPosition(VIPER_GROUND_AUTO);
        viperMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        viperMotor.setPower(0.3);
    }

    // Servo control methods
    public static void setGroundPickUp(Servo armServo, Servo clawServo) {
        armServo.setPosition(ARM_DOWN);
        clawServo.setPosition(CLAW_DOWN);
//        clawActServo.setPosition(CLAW_OPEN);
    }

    public static void setGroundPickUpAuto(Servo armServo, Servo clawServo, Servo clawActServo) {
        armServo.setPosition(ARM_DOWN);
        clawServo.setPosition(CLAW_DOWN);
        clawActServo.setPosition(CLAW_OPEN);
    }

    public static void setClawOpen(Servo clawActServo) {
        clawActServo.setPosition(CLAW_OPEN);
    }

    public static void setArmUp(Servo armServo, Servo clawServo) {
        armServo.setPosition(ARM_UP);
        clawServo.setPosition(CLAW_UP);
    }

    public static void setClawClosed(Servo clawActServo) {
        clawActServo.setPosition(CLAW_CLOSED);
    }

    public static void setClawDown(Servo clawServo) {
        clawServo.setPosition(CLAW_DOWN);
    }

    public static void setClawSpecimenPickup(Servo clawServo) {
        clawServo.setPosition(CLAW_SPECIMEN_PICKUP);
    }

    public static void setClawUp(Servo clawServo) {
        clawServo.setPosition(CLAW_UP);
    }

    public static void setMiddle(Servo armServo, Servo clawServo) {
        armServo.setPosition(ARM_MIDDLE);
        clawServo.setPosition(WRIST_SPECIMEN);
    }
}