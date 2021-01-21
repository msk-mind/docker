import qupath.tensorflow.stardist.StarDist2D

// Specify the model directory (you will need to change this!)
def pathModel = '/models/he_heavy_augment'

def stardist = StarDist2D.builder(pathModel)
      .threshold(0.5)              // Prediction threshold
      .normalizePercentiles(1, 99) // Percentile normalization
      .pixelSize(0.5)              // Resolution for detection
      .build()

// Run detection for the selected objects
def imageData = getCurrentImageData()

def server = getCurrentImageData().getServer()
// get dimensions of slide
minX = 0
minY = 0
maxX = server.getWidth()
maxY = server.getHeight()
// create rectangle roi (over entire area of image) for detections to be run over
def plane = ImagePlane.getPlane(0, 0)
def roi = ROIs.createRectangleROI(minX, minY, maxX-minX, maxY-minY, plane)
def annotationROI = PathObjects.createAnnotationObject(roi)
addObject(annotationROI)
selectAnnotations();

def pathObjects = getSelectedObjects()
if (pathObjects.isEmpty()) {
    Dialogs.showErrorMessage("StarDist", "Please select a parent object!")
    return
}
stardist.detectObjects(imageData, pathObjects)
println 'Done!'
