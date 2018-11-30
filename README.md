Note: All docker command might require sudo
1. Startup docker (on a separate terminal)

dockerd

2. Package the code

mvn package

3. build the docker image

docker build -t ikea-shopping-list-demo .

verify it was created recently:

docker image ls

3. Run the image (Standalone)

docker run -d -p 5000:8080 ikea-shopping-list-demo

(-d executes in background so wait a while for server to startup)
Runs docker in background and portforward 5000 to 8080

4. Open browser 
navigate to: localhost:5000

5. Two account exists 
Username: admin
Password: admin

Username: client
Password: client

6. Some API description at:

localhost:5000/swagger-ui.html

7. Peak into database:

localhost:5000/h2-console/
