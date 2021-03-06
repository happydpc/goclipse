/*******************************************************************************
 * Copyright (c) 2014, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package com.googlecode.goclipse.tooling.env;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import melnorme.utilbox.core.CommonException;
import melnorme.utilbox.misc.MiscUtil;
import melnorme.utilbox.misc.PathUtil.InvalidPathExceptionX;

import com.googlecode.goclipse.tooling.GoPackageName;

/**
 * Immutable description of a Go environment, under which Go operations and semantic analysis can be run.
 * (similar to a build path)
 */
public class GoEnvironment {
	
	public static final String ENV_BIN_FOLDER = "bin";
	public static final String ENV_PKG_FOLDER = "pkg";
	
	protected final GoRoot goRoot;
	protected final GoArch goArch;
	protected final GoOs goOs;
	protected final GoPath goPath;
	
	public GoEnvironment(GoRoot goRoot, GoArch goArch, GoOs goOs, GoPath goPath) {
		this.goRoot = assertNotNull(goRoot);
		this.goArch = goArch;
		this.goOs = goOs;
		this.goPath = assertNotNull(goPath);
	}
	
	public GoEnvironment(GoRoot goRoot, GoArch goArch, GoOs goOs, String goPath) {
		this(goRoot, goArch, goOs, new GoPath(goPath));
	}
	
	public GoRoot getGoRoot() {
		return goRoot;
	}
	
	public Path getGoRoot_Path() throws CommonException {
		return goRoot.asPath();
	}
	
	public GoArch getGoArch() throws CommonException{
		validateGoArch();
		return goArch;
	}
	public void validateGoArch() throws CommonException {
		if(goArch == null || goArch.asString().isEmpty()) 
			throw new CommonException("GOARCH is undefined");
	}
	
	public GoOs getGoOs() throws CommonException {
		validateGoOs();
		return goOs;
	}
	public void validateGoOs() throws CommonException {
		if(goOs == null || goOs.asString().isEmpty()) 
			throw new CommonException("GOOS is undefined");
	}
	
	public GoPath getGoPath() {
		return goPath;
	}
	
	public List<String> getGoPathEntries() {
		return goPath.getGoPathEntries();
	}
	
	public String getGoPathString() {
		return goPath.getGoPathWorkspaceString();
	}
	
	public GoPackageName findGoPackageForSourceModule(Path goModulePath) throws CommonException {
		GoPackageName goPackage = goRoot.findGoPackageForSourceModule(goModulePath);
		if(goPackage != null) {
			return goPackage;
		}
		
		return goPath.findGoPackageForSourceFile(goModulePath);
	}
	
	public ProcessBuilder createProcessBuilder(List<String> commandLine, Path workingDir) {
		return createProcessBuilder(commandLine, workingDir.toFile());
	}
	
	public ProcessBuilder createProcessBuilder(List<String> commandLine, File workingDir) {
		ProcessBuilder pb = createProcessBuilder(commandLine);
		if(workingDir != null) {
			pb.directory(workingDir);
		}
		return pb;
	}
	
	public ProcessBuilder createProcessBuilder(List<String> commandLine) {
		ProcessBuilder pb = melnorme.lang.utils.ProcessUtils.createProcessBuilder(commandLine, null);
		
		Map<String, String> env = pb.environment();
		
		putMapEntry(env, GoEnvironmentConstants.GOROOT, goRoot.asString());
		putMapEntry(env, GoEnvironmentConstants.GOPATH, getGoPathString());
		
		if(goArch != null) {
			putMapEntry(env, GoEnvironmentConstants.GOARCH, goArch.asString());
		}
		if(goOs != null) {
			putMapEntry(env, GoEnvironmentConstants.GOOS, goOs.asString());
		}
		
		return pb;
	}
	
	protected void putMapEntry(Map<String, String> env, String key, String value) {
		if(value != null) {
			env.put(key, value);
		}
	}
	
	/* ----------------- helpers ----------------- */
	
	protected static Path createPath(String pathString) throws CommonException {
		try {
			return MiscUtil.createPath(pathString);
		} catch (InvalidPathExceptionX e) {
			throw new CommonException("Invalid path: " + e.getCause().getMessage(), null);
		}
	}
	
	/* -----------------  ----------------- */
	
	protected String getGoOS_GoArch_segment() throws CommonException {
		return getGoOs().asString() + "_" + getGoArch().asString();
	}
	
	public Path getGoRootToolsDir() throws CommonException {
		return goRoot.asPath().resolve("pkg/tool/").resolve(createPath(getGoOS_GoArch_segment()));
	}
	
	protected static GoPackageName getGoPackageForSourceFile(Path sourceFilePath, Path sourceRoot) {
		sourceFilePath = sourceFilePath.normalize();
		if(!sourceFilePath.startsWith(sourceRoot)) {
			return null;
		}
		sourceFilePath = sourceRoot.relativize(sourceFilePath);
		return GoPackageName.fromPath(sourceFilePath.getParent()); // Discard file name
	}
	
	public boolean isValid() {
		if (isNullOrEmpty(goRoot.asString())) {
			return false;
		}
		return true;
	}
	
	public static boolean isNullOrEmpty(String string) {
		return string == null || string.isEmpty();
	}
	
}