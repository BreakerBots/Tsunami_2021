# Tsunami 2021
A repository for storing all the code for the Tsunami.

This readme explains how to set up and program Tsunami, 
along with a brief walk through this repository.

## Setting up Locally
1) Download the [WPILib Suite](https://docs.wpilib.org/en/stable/docs/zero-to-robot/step-2/wpilib-setup.html)
2) Download [Github Desktop](https://desktop.github.com/)
3) Open Github Desktop and clone this repo (BreakerBots/Tsunami_2021)
4) Download [IntelliJ IDEA Community](https://www.jetbrains.com/idea/download/#section=windows)
5) Open IntelliJ and choose `File > Open` then navigate to the folder where
you downloaded this repo (should be /Documents/Github/Tsunami_2021) and press `OK`. 
I recommend clicking on the settings icon in the top left and selecting
`Flatten Packages`.

## Deploying Code
1) Open up a command prompt in the repo
    - Go back to Github desktop select `Repository > Open in Command Prompt`
2) Type `gradlew deploy` and press `enter`

## Simulating Autonomous
1) Open up a command prompt in the repo
2) Type `gradlew simulateJava` and press `enter`
3) Navigate to `localhost:5804` in a browser
4) Select the `Plotter` tab
5) Go to the `Robot Simulation` GUI window that launched when you ran `simulateJava`
6) In the `Robot State` section (top left) click on `Autonomous`
7) You can watch the robot drive the select autonomous path realtime on your web browser
8) To finish close the command line that launched when you ran `simulateJava` and the GUI should
close with it.

Note: If you want to change the selected auto path navigate to `frc.team5104.RobotSim`
   in IntelliJ and change the path inside the line: `AutoManager.setTargetPath(new ExamplePath());`

## Organization
The robot code is separated into multiple different sections or "packages" located inside
`src/main/java`. This explains what is located inside each package.
- `frc.team5104`
    - `Constants.java`: all constant values for the robot, put in one file for ease of access
    - `Controls.java`: all of the controls for the robot
    - `Ports.java`: all of the ports (CanIDs, RoboRIO ports, etc) for the robot
    - `Robot.java`: the start of the code, edit the attached Subsystems, selected TeleopControllers,
    selected Autonomous Path, and what Subsystems to debug.
    - `RobotSim.java`: the start of the code for simulation, otherwise the same as `Robot.java`
    - `Superstructure.java`: the master statemachine for the robot
- `frc.team5104.auto`: contains all necessary non-robot specific files for autonomous to function
- `frc.team5104.auto.actions`: contains all actions that can be run in autonomous
- `frc.team5104.auto.paths`: contains all paths that can be run in autonomous
- `frc.team5104.subsystems`: contains all subsystems on the robot
- `frc.team5104.teleop`: contains all the teleop controllers for the robot
- `frc.team5104.util`: contains all utility files for the robot
- `frc.team5104.util.managers`: contains all managers (SubsystemManager, TeleopControllerManager) for the robot
and corresponding files
- `frc.team5104.util.setup`: contains backend files for the robot code
    - `Main.java`: the actual start of the code. Simply starts `RobotController.java`
    - `RobotController.java`: extends `RobotBase.java` and handles all calls to `Robot.java`
    or `RobotSim.java` and updated values inside `RobotState.java`
    - `RobotState.java`: stores different values about the robots current state (enabled, mode, etc.)