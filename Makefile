SRC_DIR = ./src
MAIN_CLASS_NAME = src.Main

compile:
	javac $(SRC_DIR)/*.java

jar:
	javac $(SRC_DIR)/*.java
	jar -cvfe icvCircleDetector.jar $(MAIN_CLASS_NAME) $(SRC_DIR)/*.class

run:
	java $(MAIN_CLASS_NAME)

clean:
	rm -rf *.jar
	rm -rf $(SRC_DIR)/*.class
