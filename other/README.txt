 **************************************************************

 ########  ########    ###    ########     ##     ## ######## 
 ##     ## ##         ## ##   ##     ##    ###   ### ##       
 ##     ## ##        ##   ##  ##     ##    #### #### ##       
 ########  ######   ##     ## ##     ##    ## ### ## ######   
 ##   ##   ##       ######### ##     ##    ##     ## ##       
 ##    ##  ##       ##     ## ##     ##    ##     ## ##       
 ##     ## ######## ##     ## ########     ##     ## ######## 

**************************************************************
David Jones [dsj1n15@soton.ac.uk]
Digital Doily - COMP1206 Coursework
**************************************************************

COMPILING AND RUNNING CODE:
--------------------------------------------------------------

If in the directory with the source files, run:
    javac Main.java
[This will ensure all files are compiled]

The program can then be ran through the Main class:
    java Main

There are no arguments.

NOTE:
--------------------------------------------------------------
* Point mapping is achieved using a relative coordinate system.
  Specifically when a point is recorded, it is converted into a
  percentage of the radius and a percentage of the sector arc 
  length. This point is converted back to absolute coordinates 
  for display. 
  This has some performance impact compared to recording an 
  absolute coordinate immediately but means Doily functionality 
  is more correct as points move in relation to the sector if 
  sector size changes (i.e. sector count changes). It also allows 
  for easy scaling capabilities based off the window size and hence
  doily radius. Pen sizes also scale relative to the radius so that
  the doily acts like a resizable vector image. 

* When points are recorded linear interpolation can be performed to
  capture other points that lie between a new point and the last point.
  This is done to improve smoothness when reducing sector count for 
  an existing drawing (high sector count means small sectors so less
  data is recorded per sector). Interpolation is performed until a 
  minimum scaled separation is reached.

* By default all points are redrawn directly onto the panel. This
  offers the best graphical output (especially on high resolution
  displays with scaling enabled), best resize performance and is very 
  usable in terms of drawing performance until complexity is high.
  Another drawing method can be toggled using a checkbox so that
  a buffered image is drawn instead. This buffered image remembers
  existing points and means only new points must be drawn. This mode
  means performance is fast for even complex drawings but quality
  suffers significantly and it does not scale quickly.
  Both methods are fully functional, however, the default mode is 
  most tested.

* As per the points above, the GUI is completely scalable and hence
  window size can be changed without breaking the current Doily.

* When drawing directly onto the panel, drawings should be made with 
  anti-aliasing turned off due to the significant performance impact. 
  Toggle after to redraw with anti-aliasing. If performance is greatly
  suffering, use draw image.

* Preview shows the current pen size (scaled) and colour.

* Bind to Circle allows for drawing outside of the designated
  doily circle and is togglable per line drawn as opposed to clipping
  the displayed doily.

* Selection in the gallery can use modifiers (Shift/Ctrl). If
  one is held then multiple gallery images can be selected or
  deselected. 

* There is export functionality in the gallery, this is limited as
  it is not a requirement but it exports a 5000x5000 scaled version 
  of the Doily Panel; this can take a while for complex drawings!
 


