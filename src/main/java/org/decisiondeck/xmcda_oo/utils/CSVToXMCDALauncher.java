package org.decisiondeck.xmcda_oo.utils;

import java.io.File;
import java.io.IOException;

import org.decisiondeck.jmcda.exc.InvalidInputException;
import org.decisiondeck.jmcda.exc.InvalidInvocationException;
import org.decisiondeck.jmcda.persist.text.CsvImporterEvaluations;
import org.decisiondeck.jmcda.persist.xmcda2.X2Concept;
import org.decisiondeck.jmcda.persist.xmcda2.aggregates.XMCDASortingProblemWriter;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XMCDADoc;
import org.decisiondeck.jmcda.persist.xmcda2.generated.XMCDADoc.XMCDA;
import org.decisiondeck.jmcda.structure.sorting.problem.data.IProblemData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class CSVToXMCDALauncher {
	private static final Logger s_logger = LoggerFactory.getLogger(CSVToXMCDALauncher.class);

	public static void main(String[] args) {
		final CSVCommandLineParser parser = new CSVCommandLineParser();
		try {
			parser.parse(args);
			final File inputFile = parser.getInputFile();
			final CsvImporterEvaluations importer = new CsvImporterEvaluations(
					Files.asCharSource(inputFile, Charsets.UTF_8));
			IProblemData data;
			data = importer.read();
			final File outputFile = parser.getOutputFile();

			final XMCDASortingProblemWriter writer = new XMCDASortingProblemWriter(Files.asByteSink(outputFile));
			if (!parser.getOverwrite()) {
				final boolean created = outputFile.createNewFile();
				if (!created) {
					if (outputFile.exists()) {
						throw new InvalidInvocationException("Output " + outputFile + " already exists.");
					}
					throw new InvalidInvocationException("Output " + outputFile + " couldn't be created.");
				}
			}
			final XMCDADoc doc = XMCDADoc.Factory.newInstance();
			final XMCDA xmcda = doc.addNewXMCDA();
			writer.append(data.getAlternatives(), null, xmcda);
			writer.append(data.getCriteria(), null, null, xmcda);
			writer.append(data.getAlternativesEvaluations(), X2Concept.REAL, data.getAlternatives(), data.getCriteria(),
					xmcda);
			writer.write(doc, null);
		} catch (InvalidInputException exc) {
			s_logger.error("Fatal error, terminating.", exc);
			System.err.println(exc.getLocalizedMessage());
		} catch (IOException exc) {
			s_logger.error("Fatal error, terminating.", exc);
			System.err.println(exc.getLocalizedMessage());
		} catch (InvalidInvocationException exc) {
			s_logger.error("Fatal error, terminating.", exc);
			System.err.println(exc.getLocalizedMessage());
			System.out.println(parser.getSyntaxHelp());
		}
	}

}
