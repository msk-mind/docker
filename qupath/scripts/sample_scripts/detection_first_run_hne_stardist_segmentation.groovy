import qupath.tensorflow.stardist.StarDist2D
import qupath.lib.io.GsonTools
import static qupath.lib.gui.scripting.QPEx.*

import qupath.lib.gui.images.servers.RenderedImageServer
import javax.imageio.ImageIO
import java.awt.Color
import java.awt.image.BufferedImage


new qupath.process.gui.ProcessingExtension()
new qupath.opencv.ops.ImageOps()


setImageType('BRIGHTFIELD_H_E');

print getCurrentImageData()
print '\n'

// Set color deconvolution stains
setColorDeconvolutionStains('{"Name" : "H&E default", "Stain 1" : "Hematoxylin", "Values 1" : "0.60968 0.65246 0.4501 ", "Stain 2" : "Eosin", "Values 2" : "0.21306 0.87722 0.43022 ", "Background" : " 243 243 243 "}');

// Run object detection
def pathModel = '/models/he_heavy_augment'
def stardist = StarDist2D.builder(pathModel)
      .threshold(0.5)              // Prediction threshold
      .normalizePercentiles(1, 99) // Percentile normalization
      .pixelSize(0.5)              // Resolution for detection
      .cellExpansion(3.0)          // Approximate cells based upon nucleus expansion
      .cellConstrainScale(1.5)     // Constrain cell expansion using nucleus size
      .measureShape()              // Add shape measurements
      .measureIntensity()          // Add cell measurements (in all compartments)
      .includeProbability(true)    // Add probability as a measurement (enables later filtering)
      // .nThreads(10)
      .build()
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



// selectObjectsByClassification('Tumor','Stroma','Necrosis','Adipocytes')
def pathObjects = getSelectedObjects()
if (pathObjects.isEmpty()) {
    Dialogs.showErrorMessage("StarDist", "Please select a parent object!")
    return
}
stardist.detectObjects(imageData, pathObjects)
print("finished detections")
runObjectClassifier("/models/ANN_StardistSeg3.0CellExp1.0CellConstraint_AllFeatures_LymphClassifier.json")
print("finished lymphocyte classification")

// tissue vs glass classification
createAnnotationsFromPixelClassifier("/models/TISSUE-GLASS_CLASSIFIER_UpdatedThresholder-HighRes.json", 0.0, 0.0, "SPLIT", "INCLUDE_IGNORED");
print("finished tissue/glass classification")
saveAnnotationMeasurements('/detections/tissue_annotation_results.tsv')
print("wrote tissue/glass classification to file")

writeTissueMask('Tissue');
writeTissueMask('Glass');


// select tissue annotation 
def tissueAnnotations = getAnnotationObjects().findAll {it.getPathClass() == getPathClass("Tissue")}
//This line does the selecting, and you should be able to swap in any list of objects to select
getCurrentHierarchy().getSelectionModel().setSelectedObjects(tissueAnnotations  , null)

// tissue type classification
createAnnotationsFromPixelClassifier("/models/simplified_classifier_26.json", 0.0, 0.0, "INCLUDE_IGNORED");
fireHierarchyUpdate()
resolveHierarchy()

// remove cells in areas that were unclassified by the tissue-type classsifier (still labeled as "tissue" instead of more specific classes)
toRemove = getDetectionObjects().findAll {
	return it.parent.getPathClass() == getPathClass("Tissue")
}
print(toRemove.size() + " removed post-tissue-type-clf tissue-parent cells")
removeObjects(toRemove, true)


print("finished tissue-type classification")
saveAnnotationMeasurements('/detections/region_annotation_results.tsv')
print("finished writing tissue-type classification tsv ")
selectAnnotations();
def region_annotations = getAnnotationObjects()
boolean region_pretty_print = true
def region_gson = GsonTools.getInstance(region_pretty_print)
new File('/detections/region_annotation_results.geojson').withWriter('UTF-8') {
    region_gson.toJson(region_annotations, it)
}
print("finished writing tissue-type classification geojson ")


// Save tissue masks as PNGs
writeTissueMask('Adipocytes');
writeTissueMask('Necrosis');
writeTissueMask('Stroma');
writeTissueMask('Tumor');

// Save tissue image as PNG
writeTissueImage('tissue');

// write cell object tsv (with tissue-type labels)
print("started writing cell object tsv")
saveDetectionMeasurements('/detections/object_detection_results.tsv')
print("finished writing cell object tsv")

// write cell object geojsson (with tissue-type labels)
print("started writing cell object geojson")
boolean detection_pretty_print = true
def detections = getDetectionObjects()
def detection_gson = GsonTools.getInstance(detection_pretty_print)
new File('/detections/object_detection_results.geojson').withWriter('UTF-8') {
    detection_gson.toJson(detections, it)
}
print("finished writing cell object geojson")

// Define function to write tissue image — here set downsample to 20 //
public void writeTissueImage(def tissue) {
  // def viewer = getCurrentViewer()
  def imageData = QPEx.getCurrentImageData()
  double downsample = 20
  def server = new RenderedImageServer.Builder(imageData)
      .downsamples(downsample)
      // .layers(new HierarchyOverlay(viewer.getImageRegionStore(), viewer.getOverlayOptions(), imageData))
      .build()
  // Save image
  def fileOutput = "/detections/" + tissue + "_image.png"
  writeImage(server, fileOutput)
}

// Define function to write tissue masks — here set downsample to 20 //
// Modified from QuPath P. Bankhead on QuPath user board //
// Haven't included any args to function. Modify function for now if needed//
public void writeTissueMask(def tissue) {
  
  // Extract ROI
  def shape_ann = getObjects { p -> p.getPathClass() == getPathClass(tissue) }
  def shapes = shape_ann.collect({RoiTools.getShape(it.getROI())})
  // print(shapes)
  double downsample = 20

  def server = getCurrentImageData().getServer()
  int w = (server.getWidth() / downsample) as int
  int h = (server.getHeight() / downsample) as int
  // print(w)
  // print(h)

  // Define the mask
  def imgMask = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY)

  def g2d = imgMask.createGraphics()
  g2d.scale(1.0/downsample, 1.0/downsample)
  g2d.setColor(Color.WHITE)
  for (shape in shapes)
    g2d.fill(shape)
  g2d.dispose()
  // Save mask
  File fileOutput = new File("/detections/" + tissue + "_mask.png")
  ImageIO.write(imgMask, 'PNG', fileOutput)

}

