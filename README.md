##Intro to Computer Vision - _Hough Transform Circle Detector_
_A simple Hough feature detector that can detect circles of arbitrary size in input images._

**Author** : Muhummad Patel  
**Student#** : PTLMUH006  
**Date** : 17-Aug-2016

Should you require any further clarification regarding this submission, please feel free to contact me at muhummad.patel@gmail.com. Thank you. :\)

###Approach:
This project was divided into 3 issues that needed to be solved, viz.: detect the edges in an image, detect the circles in a binary edge image, and a GUI to allow the user to perform these actions easily. This project is written in Java and makes use of the Swing framework to build the GUI and load the images. All other functionality, including image convolutions, has been hand coded. 

The project uses the Sobel filter to detect the edges in the input images. It uses a threshold value (set in icvConfig.java) to binarise the resulting image. The result is an edge image consisting of black edge pixels, and white background pixels. To detect circles, we use the hough transform which looks for circles between a min and max radius (as defined in icvConfig.java). The detected circles are then plotted over the original input image and displayed for the user.

###Usage:
I have provided a pre-built executable .jar file and a makefile to simplify the build and run process. The commands supported by the makefile are explained below:

* `make compile` or `make` - compiles the source code
* `make run` - runs the program/launches the GUI
* `make jar` - compiles the source and builds an executable .jar file
* `make clean` - deletes all .class and .jar files

A typical usage scenario is as follows: 

* navigate to the project directory in the command-line/terminal
* type `make` to compile the source code
* type `make run` to run the program
* you will now be presented with a GUI. Press the 'Open File' button on the left hand side of the GUI
* in the filechooser, choose which image file you want to open and click the 'Open' button
* the tabbed panels on the right hand side of the GUI will now each show the original image, the edge image, and the detected circles image respectively
* if you wish to save the detected circles image, go to the 'Detected Circles' tab and click the 'Save Image' button at the bottom. The circles image will now be saved as circlesImage.png.

**Note:** You can also save the Hough transform accumulator buffers as images by setting the HOUGH_SAVE_BUFFER option in icvConfig.java to 'true'. You will then need to recompile for the change to take effect.

###Challenges/Issues:
The most difficult part of the project was finding the local maxima in the accumulator of the Hough transform. To solve this issue, I compared the current pixel to the 'cube' of values around it to see if it was greater than all of them (a local maxima). I used the 'cube' of values around a pixel because just comparing to the surrounding 8 led to too many false matches. This reduced the number of false matches somewhat. To further reduce false matches, I also introduced a threshold of votes below which a value wouldn't even be considered a candidate local maxima. This threshold is applied relative to the radius of the circle under consideration (otherwise it would penalise smaller radius circles for not having as many edge points to vote for them). This relative threshold further reduced the number of false matches.

The problem of space efficiency also came up. The Hough transform, if implemented naively, requires a 3D accumulator buffer of size imgWidth X imgHeight X ((maxRadius - minRadius)/radiusStep). This can be very large and hugely space inefficient. I managed to reduce the third dimension to a constant value of 3. My implementation uses a sliding window of 3 frames in the accumulation buffer. This data-structure is far more space-efficient than the naive implementation. In order to not lose track of the circles/local-maxima that we've already found when the window slides forward, I store them in a list of Circle objects. Once I have gone through all the radii, I sort the list of detected circles in order of confidence (number of votes). In this way, I can find circles of all radii in the given range without using the full naive 3D buffer.

I managed to partially solve the problem of truncated circles. My program can find circles that are slightly truncated (their centre must be well inside the picture). Circles whose centers are off-image are not detected by my program. In terms of accuracy, my program does not detect obviously non-circle objects like irregular polygons and lines, however; it will detect regular polygons (e.g. regular hexagons, etc.) as circles (although these are admittedly very circle-like). I was unable to eliminate the detection of regular polygons.

The parameters for the Sobel filter and the Hough transform are all adjustable in the icvConfig.java file. The provided defaults have been set to values that I found to work well for the provided test images. See the comments in icvConfig.java for a description of the purpose of each parameter. **NOTE:** the HOUGH_SAVE_BUFFER option allows you to save the all the frames of the hough transform buffer as image files for later inspection. This really helps visualise the behaviour of the hough transform.

