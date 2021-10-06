//java -jar qupath-0.2.3.jar script --image /path_to_image/image.svs /path_to_script/run_hne_stardist_segmentation.groovy
import qupath.lib.scripting.QP
import qupath.lib.gui.scripting.QPEx
import qupath.imagej.tools.IJTools
import qupath.lib.gui.images.servers.RenderedImageServer
import qupath.lib.regions.RegionRequest
import javax.imageio.ImageIO
import java.awt.Color
import java.awt.image.BufferedImage
import qupath.tensorflow.stardist.StarDist2D
new qupath.process.gui.ProcessingExtension()
new qupath.opencv.ops.ImageOps()
setImageType('BRIGHTFIELD_H_E');
print QP.getCurrentImageData()
print '\n'

// Set color deconvolution stains
setColorDeconvolutionStains('{"Name" : "H&E default", "Stain 1" : "Hematoxylin", "Values 1" : "0.60968 0.65246 0.4501 ", "Stain 2" : "Eosin", "Values 2" : "0.21306 0.87722 0.43022 ", "Background" : " 243 243 243 "}');

// Run tissue detection
createAnnotationsFromPixelClassifier("/models/simple_model/TISSUE-GLASS_CLASSIFIER_UpdatedThresholder-HighRes.json", 50000.0, 0.0, "SPLIT", "INCLUDE_IGNORED");
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
createAnnotationsFromPixelClassifier("/models/simple_model/simplified_classifier_26.json", 0.0, 0.0, "INCLUDE_IGNORED");
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
// writeTissueMask('Arteries');
writeTissueMask('Necrosis');
writeTissueMask('Stroma');
writeTissueMask('Tumor');
// writeTissueMask('Veins');
// writeTissueMask('Vasc'); // this is 'other' tissues
// Save tissue image as PNG
writeTissueImage('tissue');
// // Run tile processing
// runPlugin('qupath.lib.algorithms.TilerPlugin', '{"tileSizeMicrons": 100.0,  "trimToROI": true,  "makeAnnotations": false,  "removeParentAnnotation": false}');
// // Select tissue to run object detection
// selectObjectsByClassification('Tumor','Stroma','Necrosis','Adipocytes');
// selectObjects {
//    return it.isAnnotation() && (it.getPathClass() == getPathClass('Tumor') || it.getPathClass() == getPathClass('Stroma') || it.getPathClass() == getPathClass('Necrosis') || it.getPathClass() == getPathClass('Adipocytes'))
// }
// Run object detection
def pathModel = "/models/simple_model/simple_model.pb"
def stardist = StarDist2D.builder(pathModel)
      .threshold(0.5)              // Prediction threshold
      .normalizePercentiles(1, 99) // Percentile normalization
      .pixelSize(0.5)              // Resolution for detection
      .build()
def imageData = getCurrentImageData()
def pathObjects = selectObjectsByClassification('Tumor','Stroma','Necrosis','Adipocytes');
if (pathObjects.isEmpty()) {
    Dialogs.showErrorMessage("StarDist", "Please select a parent object!")
    return
}
stardist.detectObjects(imageData, pathObjects)