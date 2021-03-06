#
# Makefile rules and useful functions for wide use
#

#
# Directories for standard stuff
#
include $(PROJECT_ROOT)/make/Makefile.inc	 

JAVA_DEV_ROOT = $(SRC_DIR)

CLASS_DIR     = $(PROJECT_ROOT)/classes
JAVADOC_DIR   = $(DOCS_DIR)/javadoc
JAR_DIR       = $(PROJECT_ROOT)/jars
JAVA_SRC_DIR  = $(JAVA_DEV_ROOT)
DESTINATION   = $(PROJECT_ROOT)/classes

#
# Built up tool information
#
ifdef JAVA_HOME
JAVAC    = $(JAVA_HOME)/bin/javac
JAR      = $(JAVA_HOME)/bin/jar
JAVADOC  = $(JAVA_HOME)/bin/javadoc
else 
JAVAC    = javac
JAR      = jar
JAVADOC  = javadoc
endif

EMPTY         =
SPACE         = $(EMPTY) $(EMPTY)

ifdef JARS
LOCAL_JARTMP  = $(patsubst %,$(JAR_DIR)/%,$(JARS))
LOCAL_JARLIST = $(subst $(SPACE),:,$(LOCAL_JARTMP))
endif

ifdef JARS_3RDPARTY
OTHER_JARTMP  = $(patsubst %,$(JAR_DIR)/%,$(JARS_3RDPARTY))
OTHER_JARLIST = $(subst $(SPACE),:,$(OTHER_JARTMP))
endif

SOURCEPATH    = $(JAVA_SRC_DIR)

CLASSPATH     = $(CLASS_DIR)

ifdef LOCAL_JARLIST	
CLASSPATH +=:$(LOCAL_JARLIST)
endif

ifdef OTHER_JARLIST
CLASSPATH +=:$(OTHER_JARLIST)
endif

#
# Option listing for the various commands
#
JAVAC_OPTIONS = -d $(DESTINATION) -classpath $(CLASSPATH) \
		        -sourcepath $(SOURCEPATH)		
JAVAH_OPTIONS = -d $(INCLUDE_DIR) -classpath $(CLASSPATH)

ifdef MANIFEST
JAR_OPTIONS = -cvmf
JAR_MANIFEST = $(JAVA_SRC_DIR)/$(MANIFEST)
else
JAR_OPTIONS = -cvf
endif

JAVADOC_OPTIONS  = \
	 -d $(JAVADOC_DIR) \
	 -sourcepath $(JAVA_SRC_DIR) \
	 -classpath $(CLASSPATH) \
	 -author \
	 -use \
	 -version \
	 -windowtitle $(WINDOWTITLE) \
	 -doctitle $(DOCTITLE) \
	 -header $(HEADER) \
	 -bottom $(BOTTOM)

#
# Build rules.
#
PACKAGE_LOC     = $(subst .,/,$(PACKAGE))
PACKAGE_DIR     = $(DESTINATION)/$(PACKAGE_LOC)
JAVA_FILES      = $(filter  %.java,$(SOURCE))
NONJAVA_FILES   = $(patsubst %.java,,$(SOURCE))
CLASS_FILES     = $(JAVA_FILES:%.java=$(PACKAGE_DIR)/%.class)
OTHER_FILES     = $(NONJAVA_FILES:%=$(PACKAGE_DIR)/%)
JNI_CLASS_FILES = $(JNI_SOURCE:%.java=$(PACKAGE_DIR)/%.class)
JNI_HEADERS     = $(JNI_SOURCE:%.java=%.h)
JAR_CONTENT_CMD = $(patsubst %,-C $(CLASS_DIR) %,$(JAR_CONTENT))

# Make a list of all packages involved
ifdef PACKAGE
PACKAGE_LIST    = $(subst .,/,$(PACKAGE))
else
PACKAGE_LIST    = $(subst .,/,$(PACKAGES)) $(subst .,/,$(NODOC_PACKAGES))
endif

PLIST_CLEAN     = $(patsubst %,$(JAVA_SRC_DIR)/%/.clean,$(PACKAGE_LIST))
PLIST_BUILD     = $(patsubst %,$(JAVA_SRC_DIR)/%/.build,$(PACKAGE_LIST))

#
# General build rules
#

# Rule 0. Applied when make is called without targets.
all: $(CLASS_FILES) $(OTHER_FILES)

# Rule 1. Build JNI .h files. Invokes rule 5.
jni : $(JNI_CLASS_FILES) $(JNI_HEADERS)

# Rule 2. Change ".build" tag to "Makefile", thus call the package makefile 
# which in turn recalls this makefile with target all (rule 10).
%.build :
	 $(MAKE) -k -f $(subst .build,Makefile,$@) all

# Rule 3. Call rule 2 for every package
buildall : $(PLIST_BUILD)               
	 $(PRINT) Done build.

#
# Specific dependency build rules
#

# Rule 4. Building a .class file from a .java file
$(PACKAGE_DIR)/%.class : $(JAVA_SRC_DIR)/$(PACKAGE_LOC)/%.java
	$(PRINT) Compiling $*.java
	@ $(JAVAC) $(JAVAC_OPTIONS) $< 

# Rule 5. Building a .class file from a .java file. Invokes rule 1.
%.class : $(JAVA_SRC_DIR)/$(PACKAGE_LOC)/%.java
	 $(MAKE) -k $(PACKAGE_DIR)/$@

# Rule 6. Building a JNI .h stub file from a .class file
$(JAVA_SRC_DIR)/$(PACKAGE_LOC)/%.h : $(PACKAGE_DIR)/%.class
	 $(PRINT) Creating header for $*
	 @ $(JAVAH) $(JAVAH_OPTIONS) $(PACKAGE).$*

# Rule 7. Building a JNI .h stub file from a class file. Invokes rule 3.
%.h : %.class
	 $(MAKE) -k $(JAVA_SRC_DIR)/$(PACKAGE_LOC)/$@

# Rule 8. Default behaviour within a package: Simply copy the object from src
# to classes. Note that the location of this rule is important. It must be after
# the package specifics.
$(PACKAGE_DIR)/% : $(SRC_DIR)/$(PACKAGE_LOC)/%
	$(MAKEDIR)  $(PACKAGE_DIR)
	$< $@
	$(CHMOD) u+rw $@

# 
# Cleanups
#

# Rule 8. Remove all produced files (except javadoc)
cleanall :
	 $(DELETE) $(PACKAGE_DIR)/*.class $(OTHER_FILES) $(JNI_HEADERS)


# Rule 9. Change ".clean" tag to "Makefile", thus call the package makefile 
# which in turn recalls this makefile with target cleanall (rule 8).
%.clean :
	 $(MAKE) -k -f $(subst .clean,Makefile,$@) cleanall


# Rule 10: Call rule 9 for every package directory
clean : $(PLIST_CLEAN)
	 $(PRINT) Done clean.

#
# JAR file related stuff
#

# Rule 10. Build a jar file. $* strips the last phony .JAR extension.
%.JAR :
	 $(DELETE) $(JAR_DIR)/$*
	 $(JAR) $(JAR_OPTIONS) $(JAR_MANIFEST) $(JAR_DIR)/$* $(JAR_CONTENT_CMD)
#       $(JAR) -i $(JAR_DIR)/$@


# Rule 11. Create given jar file by invoking its Makefile which triggers
# rule 10
%.jar :
	 $(MAKE) -k -f $(patsubst %,$(MAKE_DIR)/jar/Makefile.$*,$@) $@.JAR


# Rule 12. Create all jar files by invoking rule 11
jar : $(JARS)
	 $(PRINT) Done jars.


# Rule 13. Build javadoc for all listed packages
javadoc :
	$(MAKEDIR) $(JAVADOC_DIR)
	$(RMDIR) $(JAVADOC_DIR)/*
	$(PRINT) $(PACKAGES) > $(JAVA_DEV_ROOT)/packages.tmp
	$(JAVADOC) $(JAVADOC_OPTIONS) @$(JAVA_DEV_ROOT)/packages.tmp
	$(DELETE) $(JAVA_DEV_ROOT)/packages.tmp
	$(PRINT) Done JavaDoc.

# Rule 14. A combination of steps used for automatic building
complete : clean buildall jar javadoc	 

