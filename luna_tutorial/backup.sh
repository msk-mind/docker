CONTAINER=$USER-luna-tutorial

# backup notebook
echo backing up notebooks
if [ -f notebooks/dataset-prep.ipynb ] 
then
	mv notebooks/dataset-prep.ipynb notebooks/dataset-prep.ipynb.bk
fi
if [ -f notebooks/dsa-tools.ipynb ] 
then
	mv notebooks/dsa-tools.ipynb notebooks/dsa-tools.ipynb.bk
fi        
if [ -f notebooks/end-to-end-pipeline.ipynb ] 
then
	mv notebooks/end-to-end-pipeline.ipynb notebooks/end-to-end-pipeline.ipynb.bk
fi        
if [ -f notebooks/inference-and-visualization.ipynb ] 
then 
	mv notebooks/inference-and-visualization.ipynb notebooks/inference-and-visualization.ipynb.bk
fi 
if [ -f notebooks/model-training.ipynb ] 
then
	mv notebooks/model-training.ipynb notebooks/model-training.ipynb.bk
fi        
if [ -f notebooks/tiling.ipynb ] 
then
	mv notebooks/tiling.ipynb notebooks/tiling.ipynb.bk
fi

# copy notebooks from container
echo copying notebooks from container
docker cp $CONTAINER:/home/laluna/notebooks/dataset-prep.ipynb notebooks/dataset-prep.ipynb
docker cp $CONTAINER:/home/laluna/notebooks/dsa-tools.ipynb notebooks/dsa-tools.ipynb
docker cp $CONTAINER:/home/laluna/notebooks/end-to-end-pipeline.ipynb notebooks/end-to-end-pipeline.ipynb
docker cp $CONTAINER:/home/laluna/notebooks/inference-and-visualization.ipynb notebooks/inference-and-visualization.ipynb
docker cp $CONTAINER:/home/laluna/notebooks/model-training.ipynb notebooks/model-training.ipynb
docker cp $CONTAINER:/home/laluna/notebooks/tiling.ipynb notebooks/tiling.ipynb
        
# verify successful backup
echo verifying backups
if [ ! -f notebooks/dataset-prep.ipynb ] 
then
	echo ERROR notebooks/dataset-prep.ipynb did not get backed up!
fi
if [ ! -f notebooks/dsa-tools.ipynb ] 
then 
	echo ERROR notebooks/dsa-tools.ipynb did not get backed up!
fi
if [ ! -f notebooks/end-to-end-pipeline.ipynb ] 
then
	echo ERROR notebooks/end-to-end-pipeline.ipynb did not get backed up!
fi
if [ ! -f notebooks/inference-and-visualization.ipynb ] 
then 
	echo ERROR notebooks/inference-and-visualization.ipynb did not get backed up!
fi
if [ ! -f notebooks/model-training.ipynb ] 
then 
	echo ERROR notebooks/model-training.ipynb did not get backed up!
fi
if [ ! -f notebooks/tiling.ipynb ] 
then 
	echo ERROR notebooks/tiling.ipynb did not get backed up!
fi
