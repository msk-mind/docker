import qupath.tensorflow.stardist.StarDist2D
import qupath.lib.io.GsonTools
import static qupath.lib.gui.scripting.QPEx.*

// Name of the subdirectory containing the TMA grid
def subDirectory = "Manual points"

// If true, don't check for existing points
boolean ignoreExisting = false

// Check we have an image open from a project
def hierarchy = getCurrentHierarchy()
if (hierarchy == null) {
    println "No image is available!"
    return
}


setImageType('BRIGHTFIELD_H_E');
setColorDeconvolutionStains('{"Name" : "H&E default", "Stain 1" : "Hematoxylin", "Values 1" : "0.60968 0.65246 0.4501 ", "Stain 2" : "Eosin", "Values 2" : "0.21306 0.87722 0.43022 ", "Background" : " 243 243 243 "}');


def imageData = getCurrentImageData()
def filename = GeneralTools.getNameWithoutExtension(imageData.getServer().getMetadata().getName())
def image_id = filename.replace(".svs", "").toString()    


// make sure to change URL, details can be found on confluence on how to configure the API's url accordingly.
// API FORMAT  http://{SERVER}:{PORT}/mind/api/v1/getPathologyAnnotation/{PROJECT}/{image_id}/point/{labelset_name}

// here's an example url without server and port
def url = "http://{SERVER}:{PORT}/mind/api/v1/getPathologyAnnotation/OV_16-158/" + image_id + "/point/lymphocyte_detection_labelset"
print(url)

def get = new URL(url).openConnection();
def getRC = get.getResponseCode();

if(getRC.equals(200)) {

    def text = get.getInputStream().getText();


    def type = new com.google.gson.reflect.TypeToken<List<qupath.lib.objects.PathObject>>() {}.getType()
    def points = GsonTools.getInstance().fromJson(text, type)
    hierarchy.insertPathObjects(points)
    println(hierarchy.getAnnotationObjects().size() + " point annotations added to" + filename)

}

fireHierarchyUpdate()

// Draw bounding boxes around expert-labeled point annotations



getAnnotationObjects()
points = getAnnotationObjects()

minX = -1
minY = -1
maxX = -1
maxY = -1

points.each {
    x = it.getROI().getGeometry().getX()
    y = it.getROI().getGeometry().getY()
    
    // inititalize to first point
    if (minX == -1) {
        minX = x
        maxX = x
        
        minY = y
        maxY = y
    }
    else {
    minX = Math.min(minX, x)
    minY = Math.min(minY, y)
    
    maxX = Math.max(maxX, x)
    maxY = Math.max(maxY, y)
    
    }
   
}

// make bounding box a bit bigger to not cut off corner cells 
minX-=5
minY-=5

maxX+=5
maxY+=5

print("final coords for roi")
print(minX)
print(minY)

print(maxX)
print(maxY)

def plane = ImagePlane.getPlane(0, 0)
def roi = ROIs.createRectangleROI(minX, minY, maxX-minX, maxY-minY, plane)
def annotationROI = PathObjects.createAnnotationObject(roi)
addObject(annotationROI)



// PART 3: Run StarDist cell detections over bounding box area

// Specify the model directory 
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



def server = imageData.getServer()


// select bounding box 
selectObjects {
   return it == annotationROI
}
print 'Selected ' + getSelectedObjects().size()

def pathObjects = getSelectedObjects()
if (pathObjects.isEmpty()) {
    Dialogs.showErrorMessage("StarDist", "Please select a parent object!")
    return
}

// run detections
stardist.detectObjects(imageData, pathObjects)
boolean prettyPrint=true
def gson = GsonTools.getInstance(prettyPrint)
def training_detections_filepath = '/detections/training_segmentations/' + filename + 'cell_expansion' + cell_expansion_factor + '_cellConstrainScale' + cellConstrainScale +    '_stardist_cell_detections.geojson'
def celldetections = getDetectionObjects()

// write detections to file
new File(training_detections_filepath).withWriter('UTF-8'){
    gson.toJson(celldetections, it)
}


println 'Done!'
