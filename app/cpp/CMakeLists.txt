cmake_minimum_required(VERSION 3.10.2)
project("native-lib")

# Add OpenCV dependency
find_package(OpenCV REQUIRED)

add_library( # Name of the library
        native-lib
        SHARED
        NativeProcessor.cpp )

# Include directories
include_directories(${OpenCV_INCLUDE_DIRS})

# Link OpenCV libraries
target_link_libraries(native-lib ${OpenCV_LIBS} log jnigraphics)
