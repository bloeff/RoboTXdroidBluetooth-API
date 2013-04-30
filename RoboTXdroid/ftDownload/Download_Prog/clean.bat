@echo off
if not "%ROOT_BATCH%"/ == ""/ goto start
if "%ROOT_BATCH%"/ == ""/ ..\..\Bin\_start go %0

:start
..\..\Bin\GNU\Tools\make -f ..\..\Common\Makefile clean
