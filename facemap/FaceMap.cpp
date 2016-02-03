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
// #include <curl/curl.h>
#include <iostream>
#include <stdio.h>
#include <fstream>
#include <sstream>
using namespace cv;
using namespace cv::face;
using namespace std;
static void read_csv(const string& filename, vector<Mat>& images, vector<int>& labels, vector<string>& names, vector<string>& ids, vector<int>& attendance, char separator = ',') {
    std::ifstream file(filename.c_str(), ifstream::in);
    if (!file) {
        string error_message = "No valid input file was given, please check the given filename.";
        CV_Error(Error::StsBadArg, error_message);
    }
    string line, path, name, userid, attend;
    int i = 1;
    while (getline(file, line)) {
        stringstream liness(line);
        getline(liness, userid, separator);
        getline(liness, name, separator);
	getline(liness, attend, separator);
	if(!userid.empty() && !name.empty()){
		while(getline(liness, path, separator)){
      Mat m = imread(path, 0);
      if ( m.empty() )
      {
           cerr << path << " could not be read!" << endl;
           return;
      }
      images.push_back(m);
      labels.push_back(i);
      names.push_back(name);
      ids.push_back(userid);
      attendance.push_back(atoi(attend.c_str()));
		}
	}
	i++;
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
// size_t writeCallback(char* buf, size_t size, size_t nmemb, void* up);
void checkAttendance(vector<Mat>& images, vector<int>& labels, vector<string>& names, vector<string>& ids, vector<int>& attendance, vector<int>& indexInFrame);

/** Global variables */
String face_cascade_name = "../facedetect/lbpcascade_frontalface.xml";
CascadeClassifier face_cascade;
String window_name = "Capture - Face detection";
// string data; //will hold the url's contents


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
  vector<string>ids;
  vector<int> attendance;
  // Read in the data. This can fail if no valid
  // input filename is given.
  try {
    read_csv(fn_csv, images, labels, names, ids, attendance);
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
  /*Size size(1075,1500);
  for(int i=0; i < images.size(); i++) {
    resize(images[i],images[i],size);
    cout << "Resizing image " << i << " to " << images[i].size() << endl;
  }*/
  //Ptr<FaceRecognizer> model = createFisherFaceRecognizer();
  Ptr<LBPHFaceRecognizer> model = createLBPHFaceRecognizer(1,8,8,8,123.0);
  //Ptr<LBPHFaceRecognizer> model = createLBPHFaceRecognizer(2,16,16,16,123.0);
  //Ptr<FaceRecognizer> model = createEigenFaceRecognizer();
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
    vector<int> indexInFrame; //TODO this may or may not work

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

      indexInFrame.push_back(index);

      rectangle( frame, Point(testSample[i].x,testSample[i].y), Point((testSample[i].x+testSample[i].w),(testSample[i].y+testSample[i].h)), Scalar( 255, 0, 0 ), 2, 8, 0 );
      putText( frame , names[index], Point(testSample[i].x,testSample[i].y), CV_FONT_HERSHEY_SIMPLEX, 1.0, Scalar::all(255), 2);
    }

    imshow(window_name, frame);
    Mat testimg = imread("../pics/zach1.jpg",0);
    imshow("blank", testimg);

    //-- bail out if escape was pressed
    int c = waitKey(10);
    if( (char)c == 27 ) {
	 break; break;
    }
    if( (char)c == 32 ) {
	checkAttendance(images, labels, names, ids, attendance, indexInFrame);
}
  }


  return 0;
}

void checkAttendance(vector<Mat>& images, vector<int>& labels, vector<string>& names, vector<string>& ids, vector<int>& attendance, vector<int>& indexInFrame){
  printf("Spacebar pressed\n");

  for(int j = 0; j < indexInFrame.size(); j++) {
    printf("%d\n", indexInFrame[j]);
  }

  int index = 0;
  string str_attend;
  stringstream convert;
  for(int i = 0; i < indexInFrame.size(); i++) {
    index = indexInFrame[i];
    attendance[index] = (attendance[index]+1);
    convert.str("");
    convert << (attendance[index]);
    str_attend = convert.str();
    FILE *fp;
    char file_type[40];
    // system(("curl.exe -b cookie.txt -d test="+line+"  http://example.com").c_str());
    //curl -X PATCH -d '{"timesAttended": "3"}' 'https://torrid-heat-4382.firebaseio.com/subjects/-K9YVGntuVU6LGLANABC.json'
    fp = popen(("curl -X PATCH -d '{ \"timesAttended\": \""+str_attend+"\" }' 'https://torrid-heat-4382.firebaseio.com/subjects/"+ids[index]+".json'").c_str(), "r");
    if (fp == NULL) {
        printf("Failed to run command\n" );
        exit;
    }

    while (fgets(file_type, sizeof(file_type), fp) != NULL) {
        printf("%s", file_type);
    }

    pclose(fp);
  }
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
    //Size size(1075,1500);
    //resize(fl.pic,fl.pic,size);
    fls.push_back(fl);
  }
  return fls;
}
