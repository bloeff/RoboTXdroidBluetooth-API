@echo off
if not "%ROOT_BATCH%"/ == ""/ goto start
if "%ROOT_BATCH%"/ == ""/ ..\..\Bin\_start go %0

:start
set BIN_PATH=..\..\Bin
set BIN_GCC_PATH=%BIN_PATH%\GNU\GNU_ARM\bin
set TOOLS_PATH=%BIN_PATH%\GNU\Tools
set PATH=%BIN_GCC_PATH%;%TOOLS_PATH%;%PATH%

%TOOLS_PATH%\make -f ..\..\Common\Makefile all
