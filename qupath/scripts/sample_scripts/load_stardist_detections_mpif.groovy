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
def name = getProjectEntry()?.getImageName()
if (name == null) {
    println "No image name found! Be sure to run this script with a project and image open."
    return    
}
    
// Resist adding (potentially duplicate) points unless the user explicitly requests this
def existingPoints = getAnnotationObjects().findAll {it.getROI()?.isPoint()}
if (!ignoreExisting && !existingPoints.isEmpty()) {
    println "Point annotations are already present! Please delete these first, or set ignoreExisting = true at the start of this script."
    return
}





def imageData = getCurrentImageData()
def filename = GeneralTools.getNameWithoutExtension(imageData.getServer().getMetadata().getName()) 

def cell_expansion_factor = "3.0"
def threshold  = "0.5"
def cellConstrainScale = "2.0"


def output_prefix =  '/mpif_detections/stardist_object_detections' + '_threshold' + threshold + '_cell_expansion_factor' + cell_expansion_factor + '_cellConstrainScale' + cellConstrainScale 
def output_measurements_filepath = output_prefix + '.tsv'
def output_detections_filepath = output_prefix + '.geojson'
def filepath = '/Users/pateld6/work/docker/qupath/' + output_detections_filepath


def path = buildFilePath(filepath)
def file = new File(path)
if (!file.exists()) {
    println "{$file} does not exist! Please ensure the points are available for import."
    return
}

def text = file.text


def type = new com.google.gson.reflect.TypeToken<List<qupath.lib.objects.PathObject>>() {}.getType()
def points = GsonTools.getInstance().fromJson(file.text, type)
hierarchy.insertPathObjects(points)
fireHierarchyUpdate()