@echo off

frc-characterization simple-motor logger "%~dp0.characterize"
frc-characterization simple-motor analyzer "%~dp0.characterize"
echo If errors occur make sure you have python and frc-characterization installed ^(see readme^)