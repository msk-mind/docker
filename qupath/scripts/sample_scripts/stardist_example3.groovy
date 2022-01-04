import qupath.lib.scripting.QP
import qupath.lib.gui.scripting.QPEx
import qupath.imagej.tools.IJTools
import qupath.lib.gui.images.servers.RenderedImageServer
import qupath.lib.regions.RegionRequest
import javax.imageio.ImageIO
import java.awt.Color
import java.awt.image.BufferedImage
import qupath.ext.stardist.StarDist2D
import qupath.lib.io.GsonTools
new qupath.process.gui.ProcessingExtension()
new qupath.opencv.ops.ImageOps()
setImageType('BRIGHTFIELD_H_E');
print QP.getCurrentImageData()
print '\n'

// Set color deconvolution stains
setColorDeconvolutionStains('{"Name" : "H&E default", "Stain 1" : "Hematoxylin", "Values 1" : "0.60968 0.65246 0.4501 ", "Stain 2" : "Eosin", "Values 2" : "0.21306 0.87722 0.43022 ", "Background" : " 243 243 243 "}');

// Run tissue detection
createAnnotationsFromPixelClassifier("models/TISSUE-GLASS_CLASSIFIER_UpdatedThresholder-HighRes.json", 50000.0, 0.0, "SPLIT", "INCLUDE_IGNORED");
saveAnnotationMeasurements('/data/tissue_annotation_results.tsv')
selectAnnotations();

// Save tissue objects
def tissue_annotations = getAnnotationObjects()
new File('/data/tissue_annotation_stream').withObjectOutputStream {
    it.writeObject(tissue_annotations)
}

// Save tissue image as PNG
writeTissueMask('Tissue');
writeTissueMask('Glass');

// Select tissue to run pixel classifier
selectObjectsByClassification("Tissue");

// Run pixel classification
createAnnotationsFromPixelClassifier("/models/simplified_classifier_26.json", 0.0, 0.0, "INCLUDE_IGNORED");
saveAnnotationMeasurements('/data/region_annotation_results.tsv')
selectAnnotations();

// Save region objects
def region_annotations = getAnnotationObjects()
new File('/data/region_annotation_stream').withObjectOutputStream {
    it.writeObject(region_annotations)
}

// Save regions as GeoJSON
// def gson = GsonTools.getInstance(true)
// gson.toJson(region_annotations, new FileWriter('region_annotation_results.geojson'));
boolean prettyPrint = true
def gson = GsonTools.getInstance(prettyPrint)
new File('/data/region_annotation_results.geojson').withWriter('UTF-8') {
    gson.toJson(region_annotations, it)
}

// Save tissue masks as PNGs
writeTissueMask('Adipocytes');
writeTissueMask('Necrosis');
writeTissueMask('Stroma');
writeTissueMask('Tumor');
writeTissueImage('tissue');
// // Run tile processing
// runPlugin('qupath.lib.algorithms.TilerPlugin', '{"tileSizeMicrons": 100.0,  "trimToROI": true,  "makeAnnotations": false,  "removeParentAnnotation": false}');
// // Select tissue to run object detection
// selectObjectsByClassification('Tumor','Stroma','Necrosis','Adipocytes');
// selectObjects {
//    return it.isAnnotation() && (it.getPathClass() == getPathClass('Tumor') || it.getPathClass() == getPathClass('Stroma') || it.getPathClass() == getPathClass('Necrosis') || it.getPathClass() == getPathClass('Adipocytes'))
// }

// Run object detection
// model donwloaded from https://github.com/qupath/models/raw/main/stardist/he_heavy_augment.pb
def pathModel = "/models/he_heavy_augment.pb"
def stardist = StarDist2D.builder(pathModel)
      .threshold(0.5)              // Prediction threshold
      .normalizePercentiles(1, 99) // Percentile normalization
      .pixelSize(0.5)              // Resolution for detection
      .build()
def imageData = getCurrentImageData()
//def pathObjects = selectObjectsByClassification('Tumor','Stroma','Necrosis','Adipocytes');
def pathObjects = getSelectedObjects()
if (!pathObjects || pathObjects.isEmpty()) {
    Dialogs.showErrorMessage("StarDist", "Please select a parent object!")
    return
}
println 'Run stardist detection'
stardist.detectObjects(imageData, pathObjects)

// write cell object geojsson (with tissue-type labels)
println "Write cell object geojson"
boolean detection_pretty_print = true
def detections = getDetectionObjects()
def detection_gson = GsonTools.getInstance(detection_pretty_print)
new File('/detections/stardist_object_detection_results.geojson').withWriter('UTF-8') {
    detection_gson.toJson(detections, it)
}
println "Finished writing cell object geojson"

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
  def fileOutput = "/data/" + tissue + "_image.png"
  writeImage(server, fileOutput)
}

// Define function to write tissue masks — here set downsample to 20 //
// Modified from QuPath P. Bankhead on QuPath user board //
// Haven't included any args to function. Modify function for now if needed//
public void writeTissueMask(def tissue) {
  
  // Extract ROI
  def shape_ann = getObjects { p -> p.getPathClass() == getPathClass(tissue) }
  def shapes = shape_ann.collect({RoiTools.getShape(it.getROI())})
  print(shapes)
  double downsample = 20

  def server = getCurrentImageData().getServer()
  int w = (server.getWidth() / downsample) as int
  int h = (server.getHeight() / downsample) as int
  print(w)
  print(h)

  // Define the mask
  def imgMask = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY)

  def g2d = imgMask.createGraphics()
  g2d.scale(1.0/downsample, 1.0/downsample)
  g2d.setColor(Color.WHITE)
  for (shape in shapes)
    g2d.fill(shape)
  g2d.dispose()
  // Save mask
  File fileOutput = new File("/data/" + tissue + "_mask.png")
  ImageIO.write(imgMask, 'PNG', fileOutput)

}
