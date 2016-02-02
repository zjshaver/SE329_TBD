/*
 * Copyright (c) 2011. Philipp Wagner <bytefish[at]gmx[dot]de>.
 * Released to public domain under terms of the BSD Simplified license.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *   * Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *   * Neither the name of the organization nor the names of its contributors
 *     may be used to endorse or promote products derived from this software
 *     without specific prior written permission.
 *
 *   See <http://www.opensource.org/licenses/bsd-license>
 */
#include "opencv2/objdetect.hpp"
#include "opencv2/videoio.hpp"
#include "opencv2/imgproc.hpp"
#include "opencv2/core.hpp"
#include "opencv2/face.hpp"
#include "opencv2/highgui.hpp"
#include <iostream>
#include <stdio.h>
#include <fstream>
#include <sstream>
using namespace cv;
using namespace cv::face;
using namespace std;
static void read_csv(const string& filename, vector<Mat>& images, vector<int>& labels, vector<string>& names, char separator = ';') {
    std::ifstream file(filename.c_str(), ifstream::in);
    if (!file) {
        string error_message = "No valid input file was given, please check the given filename.";
        CV_Error(Error::StsBadArg, error_message);
    }
    string line, path, classlabel, name;
    while (getline(file, line)) {
        stringstream liness(line);
        getline(liness, path, separator);
        getline(liness, classlabel, separator);
	getline(liness, name);
        if(!path.empty() && !classlabel.empty()) {
            images.push_back(imread(path, 0));
            labels.push_back(atoi(classlabel.c_str()));
            names.push_back(name);
        }
    }
}

/** Structs */
typedef struct {
	Mat pic;
	int x;
	int y;
	int w;
	int h;
} FaceLocation;

/** Function Headers */
std::vector<FaceLocation> detectAndDisplay( Mat frame );

/** Global variables */
String face_cascade_name = "../facedetect/lbpcascade_frontalface.xml";
CascadeClassifier face_cascade;
String window_name = "Capture - Face detection";


int main(int argc, const char *argv[]) {
    // Check for valid command line arguments, print usage
    // if no arguments were given.
    if (argc != 2) {
        cout << "usage: " << argv[0] << " <csv.ext>" << endl;
        exit(1);
    }
    // Get the path to your CSV.
    string fn_csv = string(argv[1]);
    // These vectors hold the images and corresponding labels.
    vector<Mat> images;
    vector<int> labels;
    vector<string> names;
    // Read in the data. This can fail if no valid
    // input filename is given.
    try {
        read_csv(fn_csv, images, labels, names);
    } catch (cv::Exception& e) {
        cerr << "Error opening file \"" << fn_csv << "\". Reason: " << e.msg << endl;
        // nothing more we can do
        exit(1);
    }
    // Quit if there are not enough images for this demo.
    if(images.size() <= 1) {
        string error_message = "This demo needs at least 2 images to work. Please add more images to your data set!";
        CV_Error(Error::StsError, error_message);
    }

    Ptr<LBPHFaceRecognizer> model = createLBPHFaceRecognizer();
    model->train(images, labels);

    // Insert FaceDetect Code and input images
    VideoCapture capture;
    Mat frame;

    //-- 1. Load the cascade
    if( !face_cascade.load( face_cascade_name ) ){ printf("--(!)Error loading face cascade\n"); return -1; };

    //-- 2. Read the video stream
    capture.open( -1 );
    if ( ! capture.isOpened() ) { printf("--(!)Error opening video capture\n"); return -1; }

    while ( capture.read(frame) )
    {
        if( frame.empty() )
        {
            printf(" --(!) No captured frame -- Break!");
            break;
        }

        //-- 3. Apply the classifier to the frame
        std::vector<FaceLocation> testSample = detectAndDisplay( frame );

	for(int i = 0; i < testSample.size(); i++){
		// The following line predicts the label of a given
		// test image:
		int predictedLabel = model->predict(testSample[i].pic);

		int index = 0;
		for(int j = 0; j < labels.size(); j++){
			if(labels[j] == predictedLabel){
				index = j;
				break;
			}
		}

		rectangle( frame, Point(testSample[i].x,testSample[i].y), Point((testSample[i].x+testSample[i].w),(testSample[i].y+testSample[i].h)), Scalar( 255, 0, 0 ), 2, 8, 0 );
		putText( frame , names[index], Point(testSample[i].x,testSample[i].y), CV_FONT_HERSHEY_SIMPLEX, 1.0, Scalar::all(255), 2);
	}

	imshow(window_name, frame);

        //-- bail out if escape was pressed
        int c = waitKey(10);
        if( (char)c == 27 ) { break; break; }
    }


    return 0;
}

/**
 * @function detectAndDisplay
 */
std::vector<FaceLocation> detectAndDisplay( Mat frame )
{
    std::vector<FaceLocation> fls;
    FaceLocation fl;
    std::vector<Rect> faces;
    Mat frame_gray;
    Mat testCrop;

    cvtColor( frame, frame_gray, COLOR_BGR2GRAY );
    equalizeHist( frame_gray, frame_gray );

    //-- Detect faces
    face_cascade.detectMultiScale( frame_gray, faces, 1.1, 2, 0, Size(80, 80) );

    for( size_t i = 0; i < faces.size(); i++ )
    {
        Mat faceROI = frame_gray( faces[i] );

        fl.x = faces[i].x;
        fl.y = faces[i].y;
        fl.w = faces[i].width;
        fl.h = faces[i].height;
        Rect ROI(fl.x,fl.y,fl.w,fl.h);
        testCrop = frame_gray(ROI);

        //-- Show what you got
        if(!testCrop.empty()) {
          //imshow( window_name, testCrop );
          fl.pic = testCrop;
        } else {
          //imshow( window_name, frame );
          fl.pic = frame_gray;
        }
	fls.push_back(fl);
    }



    return fls;
}
