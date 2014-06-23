package ch.tkuhn.bel2nanopub;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openbel.bel.model.BELAnnotation;
import org.openbel.bel.model.BELDocument;
import org.openbel.bel.model.BELParseErrorException;
import org.openbel.bel.model.BELStatement;
import org.openbel.bel.model.BELStatementGroup;
import org.openbel.framework.common.bel.parser.BELParseResults;
import org.openbel.framework.common.bel.parser.BELParser;
import org.openbel.framework.common.model.Parameter;
import org.openbel.framework.common.model.Statement;
import org.openbel.framework.common.model.Term;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

public class BelStats {

	@com.beust.jcommander.Parameter(description = "input-bel-files", required = true)
	private List<File> inputFiles = new ArrayList<File>();

	@com.beust.jcommander.Parameter(names = "-m", description = "Maximum number of terms to be shown")
	private int maxTerms = 1000;

	public static void main(String[] args) {
		BelStats obj = new BelStats();
		JCommander jc = new JCommander(obj);
		try {
			jc.parse(args);
		} catch (ParameterException ex) {
			jc.usage();
			System.exit(1);
		}
		obj.run();
		System.out.println("STATEMENT COUNT: " + obj.getStatementCount());
		System.out.println("RELATIONS:");
		obj.printStats(obj.getRelations());
		System.out.println("FUNCTIONS:");
		obj.printStats(obj.getFunctions());
		System.out.println("ANNOTATIONS:");
		obj.printStats(obj.getAnnotations());
		System.out.println("PARAMETERS:");
		obj.printStats(obj.getParams());
	}

	public void printStats(final Map<String,Integer> map) {
		List<String> list = new ArrayList<String>(map.keySet());
		Collections.sort(list, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return map.get(o2) - map.get(o1);
			}
		});
		int c = 0;
		for (String k : list) {
			c++;
			if (c > maxTerms) {
				System.out.println("  (and more ...)");
				break;
			}
			System.out.println("  " + map.get(k) + "  " + k);
		}
	}

	private BELDocument belDoc;

	private int statementCount;
	private Map<String,Integer> relations = new HashMap<String,Integer>();
	private Map<String,Integer> functions = new HashMap<String,Integer>();
	private Map<String,Integer> annotations = new HashMap<String,Integer>();
	private Map<String,Integer> params = new HashMap<String,Integer>();


	public BelStats(File... belDocs) {
		for (File f : belDocs) {
			inputFiles.add(f);
		}
	}

	private BelStats() {
	}

	public void run() {
		for (File f : inputFiles) {
			run(f);
		}
	}

	private void run(File belFile) {
		BELParseResults belParse = BELParser.parse(Utils.readFile(belFile));

		if (!belParse.getSyntaxErrors().isEmpty()) {
			if (!belParse.getSyntaxErrors().isEmpty()) {
				System.err.println("ERRORS:");
				for (BELParseErrorException ex : belParse.getSyntaxErrors()) {
					System.err.println(ex.getMessage());
				}
			}
			return;
		}
		belDoc = belParse.getDocument();

		for (BELStatementGroup g : belDoc.getBelStatementGroups()) {
			for (BELStatement bst : g.getStatements()) {
				processBelStatement(bst);
			}
		}
	}

	private void processBelStatement(BELStatement belStatement) {
		statementCount++;
		Statement st = BELParser.parseStatement(belStatement.getStatementSyntax());
		processBelStatement(st);
		for (BELAnnotation ann : belStatement.getAnnotations()) {
			processAnnotation(ann);
		}
	}

	private void processBelStatement(Statement statement) {
		if (statement.getRelationshipType() == null) {
			processBelTerm(statement.getSubject());
		} else {
			processBelTerm(statement.getSubject());
			String relN = statement.getRelationshipType().getDisplayValue();
			increase(relations, relN);
			processBelObject(statement.getObject());
		}
	}

	private void processBelObject(org.openbel.framework.common.model.Statement.Object obj) {
		if (obj.getTerm() != null) {
			processBelTerm(obj.getTerm());
		} else {
			processBelStatement(obj.getStatement());
		}
	}

	private void processBelTerm(Term term) {
		String funcAbbrev = Utils.getFunctionAbbrev(term);
		increase(functions, funcAbbrev);
		for (Parameter p : term.getParameters()) {
			String s = "";
			if (p.getNamespace() != null && p.getNamespace().getPrefix() != null) {
				s += p.getNamespace().getPrefix() + ":";
			}
			if (p.getValue() != null) {
				s += p.getValue();
			}
			increase(params, s);
		}
	}

	private void processAnnotation(BELAnnotation ann) {
		String annN = ann.getAnnotationDefinition().getName();
		increase(annotations, annN);
	}

	public int getStatementCount() {
		return statementCount;
	}

	public Map<String,Integer> getRelations() {
		return relations;
	}

	public Map<String,Integer> getFunctions() {
		return functions;
	}

	public Map<String,Integer> getAnnotations() {
		return annotations;
	}

	public Map<String,Integer> getParams() {
		return params;
	}

	private static void increase(Map<String,Integer> map, String key) {
		if (map.containsKey(key)) {
			map.put(key, map.get(key) + 1);
		} else {
			map.put(key, 1);
		}
	}

}
