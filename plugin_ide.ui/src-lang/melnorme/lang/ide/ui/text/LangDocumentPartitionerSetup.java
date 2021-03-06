/*******************************************************************************
 * Copyright (c) 2008, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package melnorme.lang.ide.ui.text;

import melnorme.lang.ide.ui.TextSettings_Actual;
import melnorme.utilbox.misc.ArrayUtil;

import org.eclipse.core.filebuffers.IDocumentSetupParticipant;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;

public class LangDocumentPartitionerSetup implements IDocumentSetupParticipant {
	
	public static final String[] LEGAL_CONTENT_TYPES = 
			ArrayUtil.remove(TextSettings_Actual.PARTITION_TYPES, IDocument.DEFAULT_CONTENT_TYPE);
	
	protected static LangDocumentPartitionerSetup instance = new LangDocumentPartitionerSetup();
	
	public static LangDocumentPartitionerSetup getInstance() {
		return instance;
	}
	
	public LangDocumentPartitionerSetup() {
	}
	
	@Override
	public void setup(IDocument document) {
		setupDocumentPartitioner(document, TextSettings_Actual.PARTITIONING_ID);
	}
	
	public IPartitionTokenScanner createPartitionScanner() {
		return TextSettings_Actual.createPartitionScanner();
	}
	
	public IDocumentPartitioner createDocumentPartitioner() {
		IPartitionTokenScanner scanner = createPartitionScanner();
		return new FastPartitioner(scanner, LEGAL_CONTENT_TYPES);
	}
	
	protected void setupDocumentPartitioner(IDocument document, String partitioning) {
		IDocumentPartitioner partitioner = createDocumentPartitioner();
		if (partitioner != null) {
			partitioner.connect(document);
			if (document instanceof IDocumentExtension3) {
				IDocumentExtension3 extension3 = (IDocumentExtension3) document;
				extension3.setDocumentPartitioner(partitioning, partitioner);
			} else {
				document.setDocumentPartitioner(partitioner);
			}
		}
	}
	
}