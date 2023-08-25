#Parallel Computing make file
#Noah Gonsenhauser
#11/04/2023

#this is setup for the java compiler
JAVAC=/usr/bin/javac
JAVA=/usr/bin/java
JAVADOC=/usr/bin/javadoc
JFLAGS= -g

.SUFFIXES: .java .class
SRCDIR=src/*/
BINDIR=bin
DOCDIR=doc

#these are the arguments for the program to run when running "make run"
ARGS=200 20 20 200

#compiling all java programs into their class files
$(BINDIR)/%.class:$(SRCDIR)/%.java
	$(JAVAC) $ -d $(BINDIR)/ -cp $(SRCDIR)*.java $(JFLAGS) $<

CLASSES=*.class \

CLASS_FILES=$(CLASSES:%.class=$(BINDIR)/%.class)

default: $(CLASS_FILES)

run: $(CLASS_FILES)
	$(JAVA) -cp bin clubSimulation.ClubSimulation $(ARGS)

clean:
	rm -r bin/*