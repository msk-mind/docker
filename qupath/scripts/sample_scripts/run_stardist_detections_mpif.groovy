//java -jar qupath-0.2.3.jar script --image /path_to_image/image.svs /path_to_script/run_mpif_analysis.groovy

import qupath.lib.gui.scripting.QPEx.*

import qupath.lib.objects.PathObjects
import qupath.lib.roi.ROIs
import qupath.lib.regions.ImagePlane

import qupath.tensorflow.stardist.StarDist2D
import qupath.lib.io.GsonTools
import static qupath.lib.gui.scripting.QPEx.*
import qupath.lib.regions.RegionRequest
import qupath.lib.gui.scripting.QPEx


setImageType('FLUORESCENCE');


// Get main data structures
def imageData = getCurrentImageData()
def server = imageData.getServer()

// Set channel names
setChannelNames(
    'CD68',
    'TOX',
    'PD1',
    'PDL1',
    'CD8',
    'panCK',
    'DAPI'
);

// // Create a new Rectangle ROI and add it to the object hierarchy
int x = 0
int y = 0
int width = server.getWidth()
int height = server.getHeight()
int z = 0
int t = 0

def plane = ImagePlane.getPlane(z, t)
def roi = ROIs.createRectangleROI(x, y, width, height, plane)
def annotationROI = PathObjects.createAnnotationObject(roi)
addObject(annotationROI)
selectObjects {
   return it == annotationROI
}
print 'Selected ' + getSelectedObjects().size()


def pathModel = '/models/dsb2018_heavy_augment'
def cell_expansion_factor = 3.0
def threshold  = 0.5
def cellConstrainScale = 2.0

// cell = (nuclearSize * cellConstrainScale) + cellExpansion
def stardist = StarDist2D.builder(pathModel)
        .threshold(threshold)              // Probability (detection) threshold
        .normalizePercentiles(1, 99) // Percentile normalization
        .channels('DAPI')
        .pixelSize(0.5)              // Resolution for detection
        .cellExpansion(cell_expansion_factor)          // Approximate cells based upon nucleus expansion
        .cellConstrainScale(cellConstrainScale)     // Constrain cell expansion using nucleus size
        .measureShape()              // Add shape measurements
        .measureIntensity()          // Add cell measurements (in all compartments)
        .includeProbability(true)    // Add probability as a measurement (enables later filtering)
        .build()

def pathObjects = getSelectedObjects()
stardist.detectObjects(imageData, pathObjects)

toRemove = getAnnotationObjects().findAll {
    return it != annotationROI
}
print(toRemove)
removeObjects(toRemove, true)


def output_prefix =  '/mpif_detections/stardist_object_detections' + '_threshold' + threshold + '_cell_expansion_factor' + cell_expansion_factor + '_cellConstrainScale' + cellConstrainScale 
def output_measurements_filepath = output_prefix + '.tsv'
def output_detections_filepath = output_prefix + '.geojson'


// Calculate the distance of each object to other object classes
saveDetectionMeasurements(output_measurements_filepath)

boolean prettyPrint=true
def gson = GsonTools.getInstance(prettyPrint)
// def output_detections_filepath = '/data/qupath_detections_hne/' + filename + 'cell_expansion' + cell_expansion_factor + '_qupath_cell_detections.geojson'
// def output_detections_filepath = 'mpif_detections/stardist_object_detection_results_ZEROthreshold.geojson'
def celldetections = getDetectionObjects()
// print(celldetections)

// print(celldetections)
new File(output_detections_filepath).withWriter('UTF-8'){
    gson.toJson(celldetections, it)
}
print('Done')


