SRC_DIR = ./src
BUILD_DIR = ./bin
COMPILER_FLAGS = -d $(BUILD_DIR)
MAIN_CLASS_NAME = Main

compile:
	javac $(COMPILER_FLAGS) $(SRC_DIR)/*.java

run:
	java -cp $(BUILD_DIR) $(MAIN_CLASS_NAME)

clean:
	rm -rf $(BUILD_DIR)/*.class
