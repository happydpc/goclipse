## User Guide

### Eclipse basics

If you are new to Eclipse, you can learn some of the basics of the Eclipse IDE with this short intro article: 

[An introduction to Eclipse for Visual Studio users
](http://www.ibm.com/developerworks/opensource/library/os-eclipse-visualstudio/)

Also, to improve Eclipse performance on modern machines, it is recommended you increase the memory available to 
the JVM. You can do so by modifying the _`eclipse.ini`_ file in your Eclipse installation. The two VM parameters 
in _`eclipse.ini`_ to note are _-Xms_ (initial Java heap size) and _-Xmx_ (maximum Java heap size). For a machine
with 4Gb of RAM or more, the following is recommended as minimum values:

```
-vmargs
-Xms256m
-Xmx1024m
```

### GoClipse Prerequisites and Initial Setup

Open Eclipse preferences, go to the Go preference page, and configure the GOROOT, GOPATH settings appropriately. You will need an installation of the Go SDK, as well as [gocode](https://github.com/nsf/gocode) and [Go Oracle](http://golang.org/s/oracle-user-manual). These last two can be configured in the `Go / Tools` preference page.

> Note: for convenience, Goclipse comes installed with its own version of gocode, but this one can be quite out of date. It is recommended to install the latest gocode: `go get -u github.com/nsf/gocode`

### Project setup

##### Project creation:
A new Go project can be created in the Project Explorer view. Open `New / Project...` and then `Go / Go Project`. The Go perspective should open after creation, if it's not open already.

##### Project structure: 
A Goclipse project can work in two ways:
 * The project location is a subfolder of the 'src' folder of some GOPATH entry. The project will then consist of the Go source packages contained there.
 * The project location is not part of any GOPATH entry. In this case the project location will implicitly be added as an entry to the GOPATH, and a Go workspace structure with the `bin`, `pkg`, and `src` directories will be used in the project. Note that the project's implicit GOPATH entry will only apply to the source modules in that project. It will not be visible to other Goclipse projects (unless the entry is explicitly added to the global GOPATH).

 > In the `src` folder you can create Go source files that will be compiled into a library package (and placed into `pkg`), or into an executable (and placed in `bin`). See http://golang.org/doc/code.html for more information on the organization of a Go workspace.

##### Build:
The `go` tool will be used to build the project. The output of this tool will be displayed in a console. Additionally, error markers resulting from the build will be collected and displayed in the the Go editor and Problems view.

Goclipse will specify the `./...` pattern to `go build`, to match all Go packages in the project/GOPATH being built. **(note that .go files directly under src/ do not belong to any Go package and as such will not be built by this command)**. For more information see the command line help: `go help packages`. Additional per-project build options can be specified in the project Properties dialog, in the `Go Project Configuration` page.

Note that if the `Project / Build Automatically` option in the main menu is enabled (the default), a workspace build will be requested whenever any file is saved. Turn this on or off as desired.

### Editor and Navigation

##### Editor newline auto-indentation:
The editor will auto-indent new lines after an Enter is pressed. Pressing Backspace with the cursor after the indent characters in the start of the line will delete the indent and preceding newline, thus joining the rest of the line with the previous line. Pressing Delete before a newline will have an identical effect.
This is unlike most source editors - if instead you want to just remove one level of indent (or delete the preceding Tab), press Shift-Tab. 

##### Code-Completion/Auto-Complete:
Invoked with Ctrl-Space. This functionality is generally called Content Assist in Eclipse. 

Code completion is provided by means of the [gocode tool](http://github.com/nsf/gocode). Goclipse includes gocode already, but you can also use your own gocode instance, by configuring its location in the `Go / Gocode` preference page. This is recommended (using the latest gocode version). 

> If there is a problem with this operation, and you need a diagnostics log, the output of gocode can be seen in the `Oracle/gocode log` console page in the Eclipse Console view.

##### Open Definition:
The Open Definition functionality is invoked by pressing F3 in the source editor. Open Definition is also available in the editor context menu and by means of editor *hyper-linking* (hold Ctrl and click on a reference with the mouse cursor). When pressing F3, the [Go Oracle](http://golang.org/s/oracle-user-manual) tool will be used to resolve the definition. When Ctrl-click is used, you will be given a choice between Oracle and the Goclipse built-in resolver (Oracle is more accurate and complete).

> If there is a problem with this operation, and you need a diagnostics log, the output of oracle can be seen in the `Oracle/gocode log` console page in the Eclipse Console view.

### Launch and Debug:
To run a Go project that builds to an executable, you will need to create a launch configuration. Locate the main menu, open 'Run' / 'Run Configurations...'. Then double click 'Go Application" to create a new launch, and configure it accordingly. You can run these launches from the 'Run Configurations...', or for quicker access, from the Launch button in the Eclipse toolbar.

Alternatively, to automatically create and run a launch configuration, you can select a Go project in the workspace explorer, open the context menu, and do 'Run As...' / 'Go Application'. (or 'Debug As...' for debugging instead). If a matching configuration exists already, that one will be run.

Whenever a launch is requested, a build will be performed beforehand. This behavior can be configured under general Eclipse settings, or in the launch configuration.

##### Debugging

> _**Note:** The Go toolchain does not properly support debugging in Windows. While setting breakpoints and step/continue seems to work, inspecting variables doesn't work - garbage values are displayed._

You can debug a Go program by running a launch in debug mode. You will need a GDB debugger. To configure debug options (in particular, the path to the debugger to use), open the launch under 'Run' / 'Debug Configurations...', and then navigate to the 'Debugger' tab in the desired launch configuration:

<div align="center">
<a href="screenshots/UserGuide_DebuggerLaunchConfiguration.png?raw=true"><img src="screenshots/UserGuide_DebuggerLaunchConfiguration.png" /><a/> 
</div>

GDB debugger integration is achieved by using the CDT plugins. To configure global debugger options, go the 'C/C++'/'Debug'/'GDB' preference page.

**Note that for debugging to work**, the program must be compiled with debug symbols information, and those debug symbols must be on a format that GDB understands (DWARF3). Otherwise you will get GDB error messages such "(no debugging symbols found)" or "file format not recognized". 

With Go, debugging information is already added by default when compiling, but certain inline optimizations might confuse the debugger. To disable the, use them `-gcflags "-N -l"` flags. For more info see: http://golang.org/doc/gdb.
