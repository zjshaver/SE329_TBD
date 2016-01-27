cmake_minimum_required(VERSION 2.8)
set(OpenCV_DIR "/usr/local/opencv-3.0.0/release")
project( FaceDetect )
find_package( OpenCV REQUIRED )
add_executable( FaceDetect.exe FaceDetect.cpp )
target_link_libraries( FaceDetect.exe ${OpenCV_LIBS} )


