import qupath.ext.stardist.StarDist2D
import qupath.lib.io.GsonTools
import static qupath.lib.gui.scripting.QPEx.*
import org.slf4j.LoggerFactory
setImageType('BRIGHTFIELD_H_E');
setColorDeconvolutionStains('{"Name" : "H&E default", "Stain 1" : "Hematoxylin", "Values 1" : "0.60968 0.65246 0.4501 ", "Stain 2" : "Eosin", "Values 2" : "0.21306 0.87722 0.43022 ", "Background" : " 243 243 243 "}');

def logger = LoggerFactory.getLogger("stardist_cell_lymphocyte");

def imageData = getCurrentImageData()
def server = imageData.getServer()

// get dimensions of slide
minX = 0
minY = 0
maxX = server.getWidth()
maxY = server.getHeight()

logger.info('maxX' + maxX)
logger.info('maxY' + maxY)

// create rectangle roi (over entire area of image) for detections to be run over
def plane = ImagePlane.getPlane(0, 0)
def roi = ROIs.createRectangleROI(minX, minY, maxX-minX, maxY-minY, plane)
def annotationROI = PathObjects.createAnnotationObject(roi)
addObject(annotationROI)
selectAnnotations();
def pathModel = '/models/stardist/he_heavy_augment.pb'
def cell_expansion_factor = 3.0
def cellConstrainScale = 1.0
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
// select rectangle object created
selectObjects {
   //Some criteria here
   return it == annotationROI
}
def pathObjects = getSelectedObjects()
logger.info('Selected ' + pathObjects.size())
// stardist segmentations
stardist.detectObjects(imageData, pathObjects)
def celldetections = getDetectionObjects()
logger.info('Detected' + celldetections.size())
selectDetections();
// obj classifier
runObjectClassifier("/classifier-models/ANN_StardistSeg3.0CellExp1.0CellConstraint_AllFeatures_LymphClassifier.json")
saveDetectionMeasurements('/output_dir/cell_detections.tsv')

def detection_objects = ['type': 'FeatureCollection', 'features': celldetections]
def detection_geojson = GsonTools.getInstance(true)
new File('/output_dir/cell_detections.geojson').withWriter('UTF-8') {
    detection_geojson.toJson(detection_objects, it)
}
