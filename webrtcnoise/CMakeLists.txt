


cmake_minimum_required(VERSION 2.8)

project(webrtc)

#notice the "recycling" of CMAKE_C_FLAGS
#this is necessary to pick up android flags
set( CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -Wall -pedantic" )

include_directories(${PROJECT_SOURCE_DIR}/include)

add_definitions(-DFIXED_POINT -DUSE_KISS_FFT -DHAVE_CONFIG_H)

add_library(webrtc SHARED

                src/main/cpp/native-lib.cpp

                src/main/cpp/audio_ns.cpp
                src/main/cpp/fft4g.cpp
                src/main/cpp/noise_suppression.cpp
                src/main/cpp/ns_core.cpp

           )

target_link_libraries(webrtc log m)