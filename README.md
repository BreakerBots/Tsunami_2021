# Tsunami 2021
A repository for storing all the code for the Tsunami.

This readme explains how to set up and program Tsunami, 
along with a brief walk through this repository.

## Setting up Code Environment
1) Download the [WPILib Suite](https://docs.wpilib.org/en/stable/docs/zero-to-robot/step-2/wpilib-setup.html)
2) Download [Github Desktop](https://desktop.github.com/)
3) Open Github Desktop and clone this repo (BreakerBots/Tsunami_2021)
4) Download [IntelliJ IDEA Community](https://www.jetbrains.com/idea/download/#section=windows)
5) Set your `JAVA_HOME` enviornment variable to `C:\Users\Public\wpilib\2021\jdk` and then add `%JAVA_HOME%\bin` to your path.

## Running the Robot
1) Install the [FRC Game Tools](https://docs.wpilib.org/en/stable/docs/zero-to-robot/step-2/frc-game-tools.html)
2) Install the [Phoenix Tuner](https://github.com/CrossTheRoadElec/Phoenix-Releases/releases/tag/v5.19.4.1)
3) Install the [FRC Radio Config Tool](https://firstfrc.blob.core.windows.net/frc2020/Radio/FRC_Radio_Configuration_20_0_0.zip)
4) [Image the RoboRIO](https://docs.wpilib.org/en/stable/docs/zero-to-robot/step-3/imaging-your-roborio.html)
5) [Program the Radio](https://docs.wpilib.org/en/stable/docs/zero-to-robot/step-3/radio-programming.html)
6) [Update CAN Devices](https://docs.ctre-phoenix.com/en/stable/ch08_BringUpCAN.html)
7) Follow the `Setting up Code Environment` and `Deploying Code` tutorials.

## Deploying Code
1) In the top right of IntelliJ select the `deploy` configuration (the dropdown next the green build hammer) and then press `run` (the green arrow)

## Simulating Autonomous
1) In the top right of IntelliJ select the `simulate` configuration (the dropdown next the green build hammer) and then press `run` (the green arrow)
2) See `BreakerBots/BreakerBoard` on Github for the working with simulation tutorial.
3) Close the command line that launched after running code in IntelliJ before rerunning.

Note: If you want to change the selected auto path navigate to `RobotSim`
   in IntelliJ and change the path inside the line: `AutoManager.setTargetPath(new ExamplePath());`

## Building
Building is a helpful way to check for deeper issues than IntelliJ can.
1) Simply press the green build hammer in the top right of IntelliJ (next to run configurations)
2) Any errors should pop up in the bottom of your screen under the `Build` tab.

## BreakerBoard Setup
See `BreakerBots/BreakerBoard` on Github

## Testing
Testing is a way of catching issues that we predefine to our code. Testing is set up to run before the `build`, `simulate`, and `deploy` run configurations along with every Github commit.
1) In the top right of IntelliJ select the `test` configuration (the dropdown next the green build hammer) and then press `run` (the green arrow)
2) Any errors should pop up in the bottom of your screen under the `Run` tab.

## Characterizing
TODO after BreakerBoard V4 Release

## Code Structure/Flow
TODO
[Terrible (slightly outdated) Youtube Explanation For Now](https://www.youtube.com/watch?v=JPygx1CUo80)

## Writing Subsystems
TODO
[Terrible (slightly outdated) Youtube Explanation For Now](https://www.youtube.com/watch?v=6aGEaAtm4WE)

## Package Names
TODO
