package org.epnoi.model;

import org.epnoi.model.commons.StringUtils;

import java.util.Comparator;

public class Term implements Resource, Comparator<Term> {
	private String uri;
	private AnnotatedWord<TermMetadata> annotatedTerm;

	// -----------------------------------------------------------------------------

	public Term() {
		this.annotatedTerm = new AnnotatedWord<TermMetadata>(new TermMetadata());
	}

	// -----------------------------------------------------------------------------

	public static String buildURI(String term, String domain) {
		String uri = "http://" + domain + "/"
				+ StringUtils.replace(term, "[^a-zA-Z0-9]", "_");
		return uri;

	}

	// -----------------------------------------------------------------------------

	public String getUri() {
		return uri;
	}

	// -----------------------------------------------------------------------------

	public void setUri(String uri) {
		this.uri = uri;
	}

	// -----------------------------------------------------------------------------

	public AnnotatedWord<TermMetadata> getAnnotatedTerm() {
		return annotatedTerm;
	}

	// -----------------------------------------------------------------------------

	public void setAnnotatedTerm(AnnotatedWord<TermMetadata> annotatedTerm) {
		this.annotatedTerm = annotatedTerm;
	}

	@Override
	public int compare(Term t1, Term t2){
		if(t1.getAnnotatedTerm().getAnnotation().getTermhood()<t2.getAnnotatedTerm().getAnnotation().getTermhood())
			return -1;
		else if(t1.getAnnotatedTerm().getAnnotation().getTermhood()>t2.getAnnotatedTerm().getAnnotation().getTermhood())
			return 1;
		else
			return 0;
	}

	// -----------------------------------------------------------------------------

	@Override
	public String toString() {
		return "Term [URI=" + uri + ", annotatedTerm=" + annotatedTerm + "]";
	}

	// -----------------------------------------------------------------------------

}
