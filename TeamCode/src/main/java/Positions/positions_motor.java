package Positions;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

public class positions_motor {
    // Viper slide positions
    public static final int VIPER_GROUND = 25;
    public static final int VIPER_HIGHBAR = 300;

    public static final double STATE_VIPER_HIGHBAR = 410;

    public static final double STATE_OUTTAKEARMLEFT_HIGHBAR = 0.2;
 public static final double STATE_OUTTAKEARMLEFT_FLICK = 0;
    public static final double STATE_OUTTAKEARMLEFT_PICKUP = 1;
    public static final double STATE_OUTTAKEARMRIGHT_HIGHBAR = 0.8;
 public static final double STATE_OUTTAKEARMRIGHT_FLICK= 1;
    public static final double STATE_OUTTAKEARMRIGHT_PICKUP = 0;

 public static final double STATE_OUTTAKEARMLEFT_TRANSFER_WAIT = 0.6;
 public static final double STATE_OUTTAKEARMRIGHT_TRANSFER_WAIT = 0.4;

 public static final double STATE_OUTTAKEARMLEFT_TRANSFER = 0.8425;
 public static final double STATE_OUTTAKEARMRIGHT_TRANSFER = 0.1575;


 public static final double STATE_OUTTAKEWRIST_HIGHBAR = 0.3;

    public static final double STATE_OUTTAKEWRIST_PICKUP = 0.495;

 public static final double STATE_OUTTAKEWRIST_TRANSFER = 0.4167;

 public static final double STATE_OUTTAKEWRISTPIVOT_PICKUP = 0.7144;
 public static final double STATE_OUTTAKEWRISTPIVOT_HIGHBAR = 0.055;

 public static final double STATE_OUTTAKECLAW_OPEN = 0.52;
 public static final double STATE_OUTTAKECLAW_CLOSE = 0.15;


 public static final double STATE_INTAKELEFTARM_CLOSE = 0.9;
 public static final double STATE_INTAKELEFTARM_EXTEND_FULL = 0.4;

 public static final double STATE_INTAKERIGHTARM_CLOSE = 0.45;
 public static final double STATE_INTAKERIGHTARM_EXTEND_FULL = 0.95;


   //2650
    public static final int VIPER_HIGHBASKET = 2600;

    // Servo positions
    public static final double NIntakeArmExtendedFull = 0.1;
    public static final double NIntakeArmExtendedThird = 0.4;

    public static final double NIntakeArmTransfer = 0.295;

    public static final double NintakeArmWall = 0.45;

    public static final double NIntakeArmSpecimenPickUp = 0.3;
    public static final double NIntakeArmExtendedBack = 0.8;

    public static final double NIntakeWristPickUp = 0.9;

    public static final double NIntakeWristPickUpBefore = 0.6;
    public static final double NIntakeWristTransfer = 0.165;
    public static final double NIntakeWristPivotHorizontal = 0.205;

    public static final double NIntakeWristPivotVertical = 0.555;

    public static final double NIntakeWristPivotTransfer = 0.9;
    public static final double NIntakeClawOpen = 0.8;

    public static final double NIntakeClawOpenTransfer = 0.75;
    public static final double NIntakeClawClose = 0.53;

    public static final double NIntakeClawCloseFull = 0.475;


    //OUTTAKEEE


    public static final double OuttakeWristTransfer = 0.66;



    public static final double OuttakeArmTransfer = 0.5125;

    public static final double OuttakeArmPedroAuto = 0.8;
    public static final double OuttakeArmBucket = 0.35;

    public static final double OuttakeArmDownWhileBucket = 0.1;
    public static final double OuttakeArmHighBar = 0.15;

    public static final double OuttakeArmHighBarFlick = 0.18;

    public static final double OuttakeArmPickUpSpecimen = 0.985;

    public static final double OuttakeArmNewTransfer = 0.9475;

    public static final double OuttakeArmNewTransferWAIT = 0.675;

    public static final double OuttakeArmNewHighBar = 0.125;

    public static final double OuttakeArmNewHighBarFLICK = 0.04;




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
    public static final double OuttakeClawClose = 0.6;
}