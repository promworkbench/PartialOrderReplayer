package org.processmining.exception;

import org.processmining.partialorder.ptrace.model.PTrace;

public class ExceptionXFactory {

	public static Exception newUnsupportedImplementation(Class<? extends PTrace> pTraceClass) {
		return new UnsupportedImplementationException(pTraceClass.toString());
	}

}
