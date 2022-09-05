package org.firstinspires.ftc.teamcode.OLD_CODE.OLDTELEOPS;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Hardware.HardwareMaster;
import org.firstinspires.ftc.teamcode.constants.RobotConstants;
import org.openftc.revextensions2.ExpansionHubEx;
import org.openftc.revextensions2.ExpansionHubMotor;


//@TeleOp(name = "ONE PLAYER RED SPECIFIC", group = "Teleop")
@Config
public class Teleop_ONE_PLAYER_RED_ALLIANCE_SPECIFIC extends OpMode {

    /*================================ROBOT INIT================================*/

                                /** Toggle Booleans **/
    private             boolean             duck                            = false;
    private             boolean             duckDOUBLECLICK                 = false;
    private             boolean             reversedDrive                   = false;
    private             boolean             reversedDriveDOUBLECLICK        = false;
    private             boolean             clawOpen                        = true;
    private             boolean             clawOpenDOUBLECLICK             = true;
    private             boolean             dpadUp                          = false;
    private             boolean             dpadUpDOUBLECLICK               = false;
    private             boolean             dpadDownDOUBLECLICK             = false;
    private             boolean             dpadDown                        = false;
    private             boolean             extension                       = false;
    private             boolean             extensionDOUBLECLICK            = false;
    private             boolean             initiateSpikeDetector           = false;
    private             boolean             timerBoolean                    = true;
    private             boolean             leftBumper1                      = false;
    private             boolean             leftBumper1DOUBLECLICK           = false;
    private             boolean             rightBumper1                     = false;
    private             boolean             rightBumper1DOUBLECLICK          = false;
    private             boolean             leftBumper2                      = false;
    private             boolean             leftBumper2DOUBLECLICK           = false;
    private             boolean             rightBumper2                     = false;
    private             boolean             rightBumper2DOUBLECLICK          = false;
    private             boolean             rightClawAdjustment             = false;
    private             boolean             rightClawAdjustmentDOUBLECLICK  = false;
    private             boolean             intakeOn                        = false;
    private             boolean             intakeOnDOUBLECLICK             = false;
    private             boolean             capstone                        = false;
    private             boolean             capstoneDOUBLECLICK             = false;

                                /** Logic Booleans **/
    private             boolean             liftDown                        = false;
    private             boolean             colorDetectionStart             = false;
    private             boolean             highLevel                       = true;

                                   /** Numbers **/
    private             double              startTimer                      = 0;
    private             double              currentTimer                    = 0;
    private             double              intakeThreshold                 = 1.0;

    private             int                 liftCounter                     = 0;
    private             int                 extendCounter                   = 0;

                              /** Essential Variables **/
    private             FtcDashboard        dashboard                       = FtcDashboard.getInstance();
    private             Telemetry           dashboardTelemetry              = dashboard.getTelemetry();
    private             ElapsedTime         elapsedTime                     = new ElapsedTime();
    private             ElapsedTime         limitTest;
    public              HardwareMaster      robot                           = HardwareMaster.getInstance();
    private             ElapsedTime         runTime                         = new ElapsedTime();
    private             ExpansionHubMotor intakeMotor;
    private             ExpansionHubEx expansionHub;

    private             boolean             freightDetermination            = false;
    private             boolean             ball                            = false;






    @Override
    public void init() {
        robot.init(hardwareMap); // Initialize Hardware Config
        clawOpen = true; // Open the claw
        elapsedTime.reset(); // Reset Timer
        //resetEncoders(); // Reset Encoders

        robot.rightCatEar.setPosition(RobotConstants.rightCatUp); // Cat Ears Up
        robot.leftCatEar.setPosition(RobotConstants.leftCatUp); // Cat Ears Up
        expansionHub = hardwareMap.get(ExpansionHubEx.class, "Expansion Hub 2"); // Initialize EHub
        intakeMotor = (ExpansionHubMotor) hardwareMap.dcMotor.get("intake"); // Initialize Intake
        robot.rR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.rF.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.lF.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.lR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.rackAndPinionMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

    }

    @Override
    public void start() {
        runTime.reset();
    }

    @Override
    public void loop() {
        currentTimer = System.currentTimeMillis();
        standardDriving();
        toggles();

        //DBT();

        dashboardTelemetry.addData("Intake Voltage", intakeMotor.getCurrentDraw(ExpansionHubEx.CurrentDrawUnits.AMPS));
        dashboardTelemetry.addData("Color Sensor", robot.colorSensor.red());
        dashboardTelemetry.addData("clawOpen", clawOpen);
        dashboardTelemetry.addData("liftDown", liftDown);
        dashboardTelemetry.addData("intakeOn", intakeOn);
        dashboardTelemetry.addData("dpadDown", dpadDown);
        dashboardTelemetry.addData("dpadUp", dpadUp);
        dashboardTelemetry.addData("Average RGB Value", robot.colorSensor.green());
        dashboardTelemetry.addData("Ball", ball);

        dashboardTelemetry.addData("Lift Motor Encoder", robot.liftMotor.getCurrentPosition());
        dashboardTelemetry.addData("Rack Motor Encoder", robot.rackAndPinionMotor.getCurrentPosition());
        dashboardTelemetry.update();

        /** DPad Up Control **/
        if(dpadUp){
            //dpadDown = false;
            liftCounter = 0;
            extendCounter = 0;
            intakeOn = false;
            capstone = false;
            if(ball){
                moveLift(RobotConstants.liftLevel2, RobotConstants.liftSpeed, 4); // move lift to level 3

            }
            else{
                moveLift(RobotConstants.liftLevel3, RobotConstants.liftSpeed, 4); // move lift to level 3
            }
            if(robot.liftMotor.getCurrentPosition() < 750){ // close the claw just in case to not break it
                clawOpen = false;
            }
            else if(robot.liftMotor.getCurrentPosition() > 750) {
                moveExtension2(RobotConstants.rackRed + 125, RobotConstants.rackSpeed, 3); // move extension out once the lift is high enough
            }
        }

        /** DPad Down Control **/
        if(dpadDown){
            dpadUp = false;
            extendCounter = 0;
            moveExtension2(0,RobotConstants.rackSpeed, 3); // Bring the rack back in the robot
            if(Math.abs(robot.rackAndPinionMotor.getCurrentPosition()) < 550) {
                robot.liftMotor.setPower(0); // bring the lift down
            }
            clawOpen = false; // close the claw
            if(liftDown){ //once the lift is down (magnet sensor)....
                intakeOn = true; // turn intake on
                clawOpen = true; // open the claw
                timerBoolean = true;
                dpadDown = false;
            }
        }

        if (!dpadDown && !dpadUp){
            robot.rackAndPinionMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            if (gamepad2.left_trigger > 0.2){
                robot.rackAndPinionMotor.setPower(-0.25);
            }
            else if(gamepad2.right_trigger > 0.2){
                robot.rackAndPinionMotor.setPower(0.25);
            }
            else{
                robot.rackAndPinionMotor.setPower(0);
            }
        }








        if(robot.magnet.getState()){
            liftDown = false;   //lift up
        }
        else{
            liftDown = true;
        }

        /*if(duck){
            robot.duckMotor.setPower(-0.7);
            intakeOn = false;
        }
        else{
            robot.duckMotor.setPower(0);
        }

         */

        robot.duckMotor.setPower(gamepad1.left_trigger * -1);



        if(clawOpen){
            robot.clawServo.setPosition(RobotConstants.clawOpen);
        }
        else{
            robot.clawServo.setPosition(RobotConstants.clawClose);
        }
        if(intakeOn){
            robot.intakeMotor.setPower(RobotConstants.intakeSpeed);
            robot.LED.setState(false);
            clawOpen = true;
        }
        else{
            robot.intakeMotor.setPower(-0.3);
            robot.LED.setState(true);
        }





        if(gamepad2.a){
            timerBoolean = true;
        }

        if(leftBumper1){
            robot.leftCatEar.setPosition(RobotConstants.leftCatDown);
        }else{
            robot.leftCatEar.setPosition(RobotConstants.leftCatUp);
        }
        if(rightBumper1){
            robot.rightCatEar.setPosition(RobotConstants.rightCatDown);
        } else{
            robot.rightCatEar.setPosition(RobotConstants.rightCatUp);
        }



        automaticClaw();
        freightDetermination();
    }

    private void automaticClaw(){
        if(intakeOn && clawOpen){
            if(robot.colorSensor.red() > 13){
                clawOpen = false;
                intakeOn = false;
                freightDetermination = true;
            }
        }
    }

    private void freightDetermination(){
        if(freightDetermination){
            int greenValue = robot.colorSensor.green();
            if(greenValue > 120){
                ball = true;
            }
            else{
                ball = false;
            }
        }
    }

    /*private void automaticClaw(double threshold){
        double intakeVoltage = intakeMotor.getCurrentDraw(ExpansionHubEx.CurrentDrawUnits.AMPS);
        dashboardTelemetry.addData("IntakeMotor.getVelocity() > RobotConstants.intakeVelocity - 100", intakeMotor.getVelocity() > RobotConstants.intakeVelocity - 100);
        dashboardTelemetry.addData("intakeVoltage < intakeThreshold", intakeVoltage < intakeThreshold);
        dashboardTelemetry.addData("Intake Velocity", intakeMotor.getVelocity());


        //if(intakeMotor.getVelocity() > RobotConstants.intakeVelocity - 100) { //if intake is almost at full velocity

            if(intakeVoltage < intakeThreshold) {
                initiateSpikeDetector = true;
                if(timerBoolean){
                    startTimer = System.currentTimeMillis();
                    clawOpen = true;
                    timerBoolean = false;
                }
            }
        //}





        if(initiateSpikeDetector && !gamepad2.a){
            if(intakeVoltage > threshold && currentTimer > startTimer + 1000){
                colorDetectionStart = true;
            }

            if(colorDetectionStart){
                if(robot.colorSensor.red() > 25){
                    if(currentTimer > startTimer + 1000) {
                        clawOpen = false;
                        //moveLift(100, 0.5, 2);
                    }
                    if(currentTimer > startTimer + 1400) {
                        intakeOn = false;

                    }
                    colorDetectionStart = false;
                }
            }


        }

    }

     */



    /**
     * Toggles between true and false for a mode using a
     * button or a combo of buttons and prevents double clicking (where it just
     * constantly switches from true to false when holding
     * the button down.)
     *
     * @param button      Gamepad Button
     * @param mode        Boolean variable for mode which will be toggled
     * @param doubleClick DoubleClick boolean specific for this mode
     * @return Element 1 is what the toggled "mode" became and Element 2 is what the doubleClick boolean became.
     */
    public boolean[] toggle(boolean button, boolean mode, boolean doubleClick) {
        if (button && !doubleClick && !mode) mode = true;
        else if (button && !doubleClick && mode) mode = false;
        if (button) doubleClick = true;
        else doubleClick = false;

        boolean[] output = {mode, doubleClick};
        return output;
    }
    private void standardDriving() {

        /*-------------------Drive Controls-------------------*/

        double drive = 0;
        double angle = 0;

        double turn = 0;

            turn = gamepad1.right_stick_x;
            angle = gamepad1.left_stick_x;
            drive = -gamepad1.left_stick_y;



        /*------------------Driver 1 Controls (Wheels)-------------------*/

        //Slow Motor Power based on Right Trigger
        double scaleFactor = (1 / (1 + 3 * gamepad1.right_trigger));

        //Set motor power based on gamepad input
        robot.setDrivePower(scaleFactor * (drive + turn - angle ), scaleFactor * (drive + turn + angle), scaleFactor * (drive - turn + angle), scaleFactor * (drive - turn - angle));

    }
    private void resetEncoders() {
        robot.rR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.rF.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.lF.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.lR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.rackAndPinionMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

    }
    private void moveLift(int tick, double speed, double timeLimit) {
        // Move encoders towards target position until the position is reached, or the time limit expires
        if (robot.liftMotor != null) {
            robot.liftMotor.setTargetPosition(tick);

            if(liftCounter == 0){
                limitTest = new ElapsedTime();
            }

            robot.liftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);


            try {
                robot.liftMotor.setPower(speed);
            } catch (Exception p_exception) {
                robot.liftMotor.setPower(speed);
            }

            if (limitTest.time()  > timeLimit) {
                robot.liftMotor.setTargetPosition((robot.liftMotor.getCurrentPosition()));

            }
            liftCounter++;
        }
    }
    private void moveExtension(int tickTarget, double speed, double timeLimit) { // Move encoders towards target position until the position is reached, or the time limit expires
        if (robot.rackAndPinionMotor != null) {
            robot.rackAndPinionMotor.setTargetPosition(tickTarget);
            int currentTick = robot.rackAndPinionMotor.getCurrentPosition();

            robot.rackAndPinionMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            double tickRatio =  1 - Math.pow(Math.abs(currentTick / tickTarget), 1.25);

            robot.rackAndPinionMotor.setPower(speed * tickRatio);

            ElapsedTime limitTest = new ElapsedTime();
            while ((robot.rackAndPinionMotor.isBusy() && limitTest.time() < timeLimit)) {
            }
            if (limitTest.time() > timeLimit) {
                robot.rackAndPinionMotor.setTargetPosition((currentTick));

            }
            extendCounter++;
            //robot.liftMotor.setPower(0);

        }
    }
    private void moveExtension2(int tick, double speed, double timeLimit) { // Move encoders towards target position until the position is reached, or the time limit expires
        if (robot.rackAndPinionMotor != null) {



            if(extendCounter == 0){
                limitTest = new ElapsedTime();
                robot.rackAndPinionMotor.setTargetPosition(tick);
                robot.rackAndPinionMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            }



            try {
                robot.rackAndPinionMotor.setPower(speed);
            } catch (Exception p_exception) {
                robot.rackAndPinionMotor.setPower(speed);
            }


            if (limitTest.time() > timeLimit) {
                robot.rackAndPinionMotor.setTargetPosition((robot.rackAndPinionMotor.getCurrentPosition()));

            }
            //robot.liftMotor.setPower(0);
            extendCounter++;

        }
    }

    public void DBT(){
        dashboardTelemetry.addData("Intake Voltage", intakeMotor.getCurrentDraw(ExpansionHubEx.CurrentDrawUnits.AMPS));
        dashboardTelemetry.addData("Intake On", intakeOn);
        dashboardTelemetry.addData("clawOpen", clawOpen);

        dashboardTelemetry.addData("Lift Down", liftDown);
        dashboardTelemetry.update();
    }

    public void toggles(){
        reversedDrive = toggle(gamepad1.b , reversedDrive, reversedDriveDOUBLECLICK)[0];
        reversedDriveDOUBLECLICK = toggle(gamepad1.b, reversedDrive, reversedDriveDOUBLECLICK)[1];

        dpadDown = toggle(gamepad1.dpad_down, dpadDown, dpadDownDOUBLECLICK)[0];
        dpadDownDOUBLECLICK = toggle(gamepad1.dpad_down, dpadDown, dpadDownDOUBLECLICK)[1];

        dpadUp = toggle(gamepad1.dpad_up, dpadUp, dpadUpDOUBLECLICK)[0];
        dpadUpDOUBLECLICK = toggle(gamepad1.dpad_up, dpadUp, dpadUpDOUBLECLICK)[1];

        intakeOn = toggle(gamepad1.a, intakeOn, intakeOnDOUBLECLICK)[0];
        intakeOnDOUBLECLICK = toggle(gamepad1.a, intakeOn, intakeOnDOUBLECLICK)[1];

        duck = toggle(gamepad2.y,duck, duckDOUBLECLICK)[0];
        duckDOUBLECLICK = toggle(gamepad2.y, duck, duckDOUBLECLICK)[1];

        extension = toggle(gamepad2.x, extension, extensionDOUBLECLICK)[0];
        extensionDOUBLECLICK = toggle(gamepad2.x, extension, extensionDOUBLECLICK)[1];

        clawOpen = toggle(gamepad1.b, clawOpen, clawOpenDOUBLECLICK)[0];
        clawOpenDOUBLECLICK = toggle(gamepad1.b, clawOpen, clawOpenDOUBLECLICK) [1];

        rightBumper1 = toggle(gamepad1.right_bumper, rightBumper1, rightBumper1DOUBLECLICK)[0];
        rightBumper1DOUBLECLICK = toggle(gamepad1.right_bumper, rightBumper1, rightBumper1DOUBLECLICK)[1];

        leftBumper1 = toggle(gamepad1.left_bumper, leftBumper1, leftBumper1DOUBLECLICK)[0];
        leftBumper1DOUBLECLICK = toggle(gamepad1.left_bumper, leftBumper1, leftBumper1DOUBLECLICK)[1];

        rightBumper2 = toggle(gamepad2.right_bumper, rightBumper2, rightBumper2DOUBLECLICK)[0];
        rightBumper2DOUBLECLICK = toggle(gamepad2.right_bumper, rightBumper2, rightBumper2DOUBLECLICK)[1];

        leftBumper2 = toggle(gamepad2.left_bumper, leftBumper2, leftBumper2DOUBLECLICK)[0];
        leftBumper2DOUBLECLICK = toggle(gamepad2.left_bumper, leftBumper2, leftBumper2DOUBLECLICK)[1];

        rightClawAdjustment = toggle(gamepad2.right_bumper, rightClawAdjustment, rightClawAdjustmentDOUBLECLICK)[0];
        rightClawAdjustmentDOUBLECLICK = toggle(gamepad2.right_bumper, rightClawAdjustment, rightClawAdjustmentDOUBLECLICK)[1];

        capstone = toggle(gamepad2.x, capstone, capstoneDOUBLECLICK)[0];
        capstoneDOUBLECLICK = toggle(gamepad2.x, capstone, capstoneDOUBLECLICK) [1];
    }


}