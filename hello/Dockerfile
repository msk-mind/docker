FROM python:3-alpine
WORKDIR /opt/
EXPOSE 5020 
RUN pip install flask 
COPY hello_server.py .
RUN ls .
CMD ["python3", "./hello_server.py"]
