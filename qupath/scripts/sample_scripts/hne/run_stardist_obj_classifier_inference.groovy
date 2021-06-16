import qupath.tensorflow.stardist.StarDist2D
import qupath.lib.io.GsonTools
import static qupath.lib.gui.scripting.QPEx.*


setImageType('BRIGHTFIELD_H_E');
setColorDeconvolutionStains('{"Name" : "H&E default", "Stain 1" : "Hematoxylin", "Values 1" : "0.60968 0.65246 0.4501 ", "Stain 2" : "Eosin", "Values 2" : "0.21306 0.87722 0.43022 ", "Background" : " 243 243 243 "}');


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


def pathModel = '/models/he_heavy_augment'

// StarDist model parameters
def cell_expansion_factor = 3.0
def cellConstrainScale = 1.5

def stardist = StarDist2D.builder(pathModel)
    .threshold(0.5)              // Probability (detection) threshold
    .normalizePercentiles(1, 99) // Percentile normalization
    .pixelSize(0.5)              // Resolution for detection
    .cellExpansion(cell_expansion_factor)          // Approximate cells based upon nucleus expansion
    .cellConstrainScale(cellConstrainScale)     // Constrain cell expansion using nucleus size
    .measureShape()              // Add shape measurements
    .measureIntensity()          // Add cell measurements (in all compartments)
    .includeProbability(true)    // Add probability as a measurement (enables later filtering)
    .build()


def imageData = getCurrentImageData()
def server = imageData.getServer()
def filename = GeneralTools.getNameWithoutExtension(imageData.getServer().getMetadata().getName())
def image_id = filename.replace(".svs", "").toString()    


// select rectangle object created
selectObjects {
   //Some criteria here
   return it == annotationROI
}
print 'Selected ' + getSelectedObjects().size()
def pathObjects = getSelectedObjects()

// stardist segmentations
stardist.detectObjects(imageData, pathObjects)
def celldetections = getDetectionObjects()
selectDetections();

// obj classifier
runObjectClassifier("v5_ANN_StardistSegs_3.0cell_expansion_factor_1.5cellConstrainScale_classifier_lymphocyte_other.json")

// to save as x,y coordinates in TSV format
saveDetectionMeasurements('/detections/' + image_id + '_object_detection_results.tsv')

// to save each cell as a polygon with many coordinates (much slower to process)
// boolean detection_pretty_print = true
// def detections = getDetectionObjects()
// def detection_gson = GsonTools.getInstance(detection_pretty_print)
// new File('/detections/' + image_id  + '_object_detection_results.geojson').withWriter('UTF-8') {
//     detection_gson.toJson(detections, it)
// }



