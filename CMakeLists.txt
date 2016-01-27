cmake_minimum_required(VERSION 2.8)

IF(${CMAKE_SYSTEM_NAME} MATCHES "Linux")
    # Linux specific code
    set(OpenCV_DIR "/usr/local/opencv-3.0.0/release")
ENDIF(${CMAKE_SYSTEM_NAME} MATCHES "Linux")

IF(${CMAKE_SYSTEM_NAME} MATCHES "Windows")
    # Linux specific code
    set(OpenCV_DIR ${})
ENDIF(${CMAKE_SYSTEM_NAME} MATCHES "Windows")

project( FaceDetect )
find_package( OpenCV REQUIRED )
add_executable( FaceDetect.exe FaceDetect.cpp )
target_link_libraries( FaceDetect.exe ${OpenCV_LIBS} )


