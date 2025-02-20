package TeleOp;

import static Positions.Commands.sleep;

import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.util.Constants;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.LinkedList;
import java.util.Queue;

import Positions.RobotPose;
import Positions.positions_motor;
import pedroPathing.constants.FConstants;
import pedroPathing.constants.LConstants;

@TeleOp(name="ultraTesting")
public class ultraTesting extends OpMode {

    public static int rollingAverageSize = 3;

    private AnalogInput sensor;
    private DistanceUnit unit = DistanceUnit.INCH;

    private final Queue<Double> sensorData = new LinkedList<>();
    private double sensorAverage;

    public void periodic() {
        sensorData.add(sensor.getVoltage());
        if (sensorData.size() > rollingAverageSize) {
            sensorData.remove();
        }

        // noinspection OptionalGetWithoutIsPresent
        sensorAverage = sensorData.stream()
                .reduce((total, el) -> total + el / sensorData.size()).get();
    }
    public void setDistanceUnit(DistanceUnit distanceUnit) {
        unit = distanceUnit;
    }

    /**
     * @return In whatever unit you set it to
     */
    public double getDistance() {
        return unit.fromCm(sensorAverage * 500 / 3.3);
    }

    @Override
    public void init() {
        // Initialize Pedro Pathing
        sensor = hardwareMap.get(AnalogInput.class, "basketSensorLeft");

    }

    @Override
    public void start() {

    }

    @Override
    public void loop() {


    }
}