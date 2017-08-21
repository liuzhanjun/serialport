#设置cmake的版本
cmake_minimum_required(VERSION 3.6.0-rc2)
project(serialport)
set(M_JNI_DIR ${serialport_SOURCE_DIR})
include_directories(${M_JNI_DIR}/include)
add_library(serial_port SHARED
            ${M_JNI_DIR}/SerialPort.c)
target_include_directories(serial_port PRIVATE ${M_JNI_DIR}/include)
find_library( # Sets the name of the path variable.
              log-lib
              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log)
target_link_libraries( serial_port
              ${log-lib})