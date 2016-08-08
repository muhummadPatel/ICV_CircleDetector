SRC_DIR = ./src
MAIN_CLASS_NAME = src.Main

compile:
	javac $(SRC_DIR)/*.java

run:
	java $(MAIN_CLASS_NAME)

clean:
	rm -rf $(SRC_DIR)/*.class
